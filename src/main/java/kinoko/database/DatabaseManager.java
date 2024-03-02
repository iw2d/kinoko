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
import kinoko.database.cassandra.CassandraGiftAccessor;
import kinoko.database.cassandra.CassandraMigrationAccessor;
import kinoko.database.cassandra.codec.*;
import kinoko.database.cassandra.table.*;
import kinoko.database.cassandra.type.*;
import kinoko.server.cashshop.CashItemInfo;
import kinoko.world.item.EquipData;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;
import kinoko.world.item.PetData;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.funckey.FuncKeyManager;
import kinoko.world.user.stat.CharacterStat;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class DatabaseManager {
    public static final InetSocketAddress DATABASE_ADDRESS = new InetSocketAddress("127.0.0.1", 9042);
    public static final String DATABASE_DATACENTER = "datacenter1";
    public static final String DATABASE_KEYSPACE = "kinoko";
    private static CqlSession cqlSession;
    private static AccountAccessor accountAccessor;
    private static CharacterAccessor characterAccessor;
    private static MigrationAccessor migrationAccessor;
    private static GiftAccessor giftAccessor;

    public static AccountAccessor accountAccessor() {
        return accountAccessor;
    }

    public static CharacterAccessor characterAccessor() {
        return characterAccessor;
    }

    public static MigrationAccessor migrationAccessor() {
        return migrationAccessor;
    }

    public static GiftAccessor giftAccessor() {
        return giftAccessor;
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
        CashItemInfoUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        SkillRecordUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        QuestRecordUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        FuncKeyManUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        CharacterStatUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);

        // Create Tables
        IdTable.createTable(cqlSession, DATABASE_KEYSPACE);
        AccountTable.createTable(cqlSession, DATABASE_KEYSPACE);
        CharacterTable.createTable(cqlSession, DATABASE_KEYSPACE);
        MigrationTable.createTable(cqlSession, DATABASE_KEYSPACE);
        GiftTable.createTable(cqlSession, DATABASE_KEYSPACE);

        // Register Codecs
        registerCodec(cqlSession, EquipDataUDT.getTypeName(), (ic) -> new EquipDataCodec(ic, GenericType.of(EquipData.class)));
        registerCodec(cqlSession, PetDataUDT.getTypeName(), (ic) -> new PetDataCodec(ic, GenericType.of(PetData.class)));
        registerCodec(cqlSession, ItemUDT.getTypeName(), (ic) -> new ItemCodec(ic, GenericType.of(Item.class)));
        registerCodec(cqlSession, InventoryUDT.getTypeName(), (ic) -> new InventoryCodec(ic, GenericType.of(Inventory.class)));
        registerCodec(cqlSession, CashItemInfoUDT.getTypeName(), (ic) -> new CashItemInfoCodec(ic, GenericType.of(CashItemInfo.class)));
        registerCodec(cqlSession, SkillRecordUDT.getTypeName(), (ic) -> new SkillRecordCodec(ic, GenericType.of(SkillRecord.class)));
        registerCodec(cqlSession, QuestRecordUDT.getTypeName(), (ic) -> new QuestRecordCodec(ic, GenericType.of(QuestRecord.class)));
        registerCodec(cqlSession, FuncKeyManUDT.getTypeName(), (ic) -> new FuncKeyManCodec(ic, GenericType.of(FuncKeyManager.class)));
        registerCodec(cqlSession, CharacterStatUDT.getTypeName(), (ic) -> new CharacterStatCodec(ic, GenericType.of(CharacterStat.class)));

        // Create Accessors
        accountAccessor = new CassandraAccountAccessor(cqlSession, DATABASE_KEYSPACE);
        characterAccessor = new CassandraCharacterAccessor(cqlSession, DATABASE_KEYSPACE);
        migrationAccessor = new CassandraMigrationAccessor(cqlSession, DATABASE_KEYSPACE);
        giftAccessor = new CassandraGiftAccessor(cqlSession, DATABASE_KEYSPACE);
    }

    public static CompletableFuture<Void> shutdown() {
        final CompletableFuture<Void> shutdownFuture = new CompletableFuture<>();
        cqlSession.closeAsync().thenAccept(shutdownFuture::complete);
        return shutdownFuture;
    }
}
