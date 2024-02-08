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
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.cassandra.CassandraAccountAccessor;
import kinoko.database.cassandra.CassandraCharacterAccessor;
import kinoko.database.cassandra.CassandraMigrationAccessor;
import kinoko.database.cassandra.codec.*;
import kinoko.database.cassandra.table.AccountTable;
import kinoko.database.cassandra.table.CharacterTable;
import kinoko.database.cassandra.table.IdTable;
import kinoko.database.cassandra.table.MigrationTable;
import kinoko.database.cassandra.type.*;
import kinoko.world.item.EquipData;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;
import kinoko.world.item.PetData;
import kinoko.world.quest.QuestRecord;
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
    private static MigrationAccessor migrationAccessor;

    public static AccountAccessor accountAccessor() {
        return accountAccessor;
    }

    public static CharacterAccessor characterAccessor() {
        return characterAccessor;
    }

    public static MigrationAccessor migrationAccessor() {
        return migrationAccessor;
    }

    public static void createKeyspace(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createKeyspace(keyspace)
                        .ifNotExists()
                        .withSimpleStrategy(1)
                        .build()
        );
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
        createKeyspace(cqlSession, DATABASE_KEYSPACE);

        // Create UDTs
        EquipDataUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        PetDataUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        ItemUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        InventoryUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        QuestRecordUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        CharacterStatUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);

        // Create Tables
        IdTable.createTable(cqlSession, DATABASE_KEYSPACE);
        AccountTable.createTable(cqlSession, DATABASE_KEYSPACE);
        CharacterTable.createTable(cqlSession, DATABASE_KEYSPACE);
        MigrationTable.createTable(cqlSession, DATABASE_KEYSPACE);

        // Register Codecs
        registerCodec(cqlSession, EquipDataUDT.getTypeName(), (ic) -> new EquipDataCodec(ic, GenericType.of(EquipData.class)));
        registerCodec(cqlSession, PetDataUDT.getTypeName(), (ic) -> new PetDataCodec(ic, GenericType.of(PetData.class)));
        registerCodec(cqlSession, ItemUDT.getTypeName(), (ic) -> new ItemCodec(ic, GenericType.of(Item.class)));
        registerCodec(cqlSession, InventoryUDT.getTypeName(), (ic) -> new InventoryCodec(ic, GenericType.of(Inventory.class)));
        registerCodec(cqlSession, QuestRecordUDT.getTypeName(), (ic) -> new QuestRecordCodec(ic, GenericType.of(QuestRecord.class)));
        registerCodec(cqlSession, CharacterStatUDT.getTypeName(), (ic) -> new CharacterStatCodec(ic, GenericType.of(CharacterStat.class)));

        // Create Accessors
        accountAccessor = new CassandraAccountAccessor(cqlSession, DATABASE_KEYSPACE);
        characterAccessor = new CassandraCharacterAccessor(cqlSession, DATABASE_KEYSPACE);
        migrationAccessor = new CassandraMigrationAccessor(cqlSession, DATABASE_KEYSPACE);
    }

    public static void shutdown() {
        cqlSession.close();
    }
}
