package kinoko.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.CassandraAccessor;
import kinoko.database.cassandra.CassandraAccountAccessor;
import kinoko.database.cassandra.CassandraCharacterAccessor;
import kinoko.database.cassandra.codec.*;
import kinoko.database.cassandra.model.CharacterStatModel;
import kinoko.database.cassandra.model.EquipInfoModel;
import kinoko.database.cassandra.model.ItemModel;
import kinoko.database.cassandra.model.PetInfoModel;
import kinoko.world.item.EquipInfo;
import kinoko.world.item.Item;
import kinoko.world.item.PetInfo;
import kinoko.world.user.CharacterStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.function.Function;

public final class DatabaseManager {
    public static final InetSocketAddress DATABASE_ADDRESS = new InetSocketAddress("127.0.0.1", 9042);
    public static final String DATABASE_DATACENTER = "datacenter1";
    public static final String DATABASE_KEYSPACE = "kinoko";
    private static final Logger log = LogManager.getLogger(DatabaseManager.class);
    private static CqlSession cqlSession;
    private static AccountAccessor accountAccessor;
    private static CharacterAccessor characterAccessor;

    public static AccountAccessor accountAccessor() {
        return accountAccessor;
    }

    public static CharacterAccessor characterAccessor() {
        return characterAccessor;
    }

    private static UserDefinedType getUserDefinedType(CqlSession session, String typeName) {
        return session.getMetadata()
                .getKeyspace(DATABASE_KEYSPACE)
                .flatMap(ks -> ks.getUserDefinedType(typeName))
                .orElseThrow(() -> new IllegalArgumentException("Missing UDT definition " + typeName));
    }

    private static void registerCodec(CqlSession session, String typeName, Function<TypeCodec<UdtValue>, MappingCodec<UdtValue, ?>> constructor) {
        final CodecRegistry codecRegistry = session.getContext().getCodecRegistry();
        final TypeCodec<UdtValue> innerCodec = codecRegistry.codecFor(getUserDefinedType(session, typeName));
        ((MutableCodecRegistry) codecRegistry).register(constructor.apply(innerCodec));
    }

    public static void initialize() {
        // Create Session
        cqlSession = new CqlSessionBuilder()
                .addContactPoint(DATABASE_ADDRESS)
                .withLocalDatacenter(DATABASE_DATACENTER)
                .build();

        // Create Keyspace
        CassandraAccessor.createKeyspace(cqlSession, DATABASE_KEYSPACE);

        // Create UDTs
        EquipInfoCodec.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        PetInfoCodec.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        ItemCodec.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        InventoryCodec.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        CharacterStatCodec.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);

        // Create Tables
        CassandraAccountAccessor.createTable(cqlSession, DATABASE_KEYSPACE);
        CassandraCharacterAccessor.createTable(cqlSession, DATABASE_KEYSPACE);

        // Register Codecs
        registerCodec(cqlSession, EquipInfoModel.TYPE_NAME, (ic) -> new EquipInfoCodec(ic, GenericType.of(EquipInfo.class)));
        registerCodec(cqlSession, PetInfoModel.TYPE_NAME, (ic) -> new PetInfoCodec(ic, GenericType.of(PetInfo.class)));
        registerCodec(cqlSession, ItemModel.TYPE_NAME, (ic) -> new ItemCodec(ic, GenericType.of(Item.class)));
        registerCodec(cqlSession, CharacterStatModel.TYPE_NAME, (ic) -> new CharacterStatCodec(ic, GenericType.of(CharacterStat.class)));

        // Create Accessors
        accountAccessor = new CassandraAccountAccessor(cqlSession, DATABASE_KEYSPACE);
        characterAccessor = new CassandraCharacterAccessor(cqlSession, DATABASE_KEYSPACE);
    }

    public static void shutdown() {
        cqlSession.close();
    }
}
