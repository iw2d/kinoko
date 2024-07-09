package kinoko.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.cassandra.*;
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
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.data.MapTransferInfo;
import kinoko.world.user.data.MiniGameRecord;
import kinoko.world.user.data.WildHunterInfo;
import kinoko.world.user.stat.CharacterStat;

import java.net.InetSocketAddress;
import java.util.function.Function;

public final class DatabaseManager {
    public static final InetSocketAddress DATABASE_ADDRESS = new InetSocketAddress("127.0.0.1", 9042);
    public static final String DATABASE_DATACENTER = "datacenter1";
    public static final String DATABASE_KEYSPACE = "kinoko";
    private static CqlSession cqlSession;
    private static AccountAccessor accountAccessor;
    private static CharacterAccessor characterAccessor;
    private static FriendAccessor friendAccessor;
    private static GiftAccessor giftAccessor;
    private static MemoAccessor memoAccessor;

    public static AccountAccessor accountAccessor() {
        return accountAccessor;
    }

    public static CharacterAccessor characterAccessor() {
        return characterAccessor;
    }

    public static FriendAccessor friendAccessor() {
        return friendAccessor;
    }

    public static GiftAccessor giftAccessor() {
        return giftAccessor;
    }

    public static MemoAccessor memoAccessor() {
        return memoAccessor;
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
        // Create Config
        final DriverConfigLoader configLoader = DriverConfigLoader.programmaticBuilder()
                .withString(DefaultDriverOption.REQUEST_CONSISTENCY, "ALL")
                .withString(DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, "SERIAL")
                .build();
        // Create Session
        cqlSession = new CqlSessionBuilder()
                .addContactPoint(DATABASE_ADDRESS)
                .withLocalDatacenter(DATABASE_DATACENTER)
                .withConfigLoader(configLoader)
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
        ConfigUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        MiniGameRecordUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        MapTransferInfoUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        WildHunterInfoUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
        CharacterStatUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);

        // Create Tables
        IdTable.createTable(cqlSession, DATABASE_KEYSPACE);
        AccountTable.createTable(cqlSession, DATABASE_KEYSPACE);
        CharacterTable.createTable(cqlSession, DATABASE_KEYSPACE);
        FriendTable.createTable(cqlSession, DATABASE_KEYSPACE);
        GiftTable.createTable(cqlSession, DATABASE_KEYSPACE);
        MemoTable.createTable(cqlSession, DATABASE_KEYSPACE);

        // Register Codecs
        registerCodec(cqlSession, EquipDataUDT.getTypeName(), (ic) -> new EquipDataCodec(ic, GenericType.of(EquipData.class)));
        registerCodec(cqlSession, PetDataUDT.getTypeName(), (ic) -> new PetDataCodec(ic, GenericType.of(PetData.class)));
        registerCodec(cqlSession, ItemUDT.getTypeName(), (ic) -> new ItemCodec(ic, GenericType.of(Item.class)));
        registerCodec(cqlSession, InventoryUDT.getTypeName(), (ic) -> new InventoryCodec(ic, GenericType.of(Inventory.class)));
        registerCodec(cqlSession, CashItemInfoUDT.getTypeName(), (ic) -> new CashItemInfoCodec(ic, GenericType.of(CashItemInfo.class)));
        registerCodec(cqlSession, SkillRecordUDT.getTypeName(), (ic) -> new SkillRecordCodec(ic, GenericType.of(SkillRecord.class)));
        registerCodec(cqlSession, QuestRecordUDT.getTypeName(), (ic) -> new QuestRecordCodec(ic, GenericType.of(QuestRecord.class)));
        registerCodec(cqlSession, ConfigUDT.getTypeName(), (ic) -> new ConfigCodec(ic, GenericType.of(ConfigManager.class)));
        registerCodec(cqlSession, MiniGameRecordUDT.getTypeName(), (ic) -> new MiniGameRecordCodec(ic, GenericType.of(MiniGameRecord.class)));
        registerCodec(cqlSession, MapTransferInfoUDT.getTypeName(), (ic) -> new MapTransferInfoCodec(ic, GenericType.of(MapTransferInfo.class)));
        registerCodec(cqlSession, WildHunterInfoUDT.getTypeName(), (ic) -> new WildHunterInfoCodec(ic, GenericType.of(WildHunterInfo.class)));
        registerCodec(cqlSession, CharacterStatUDT.getTypeName(), (ic) -> new CharacterStatCodec(ic, GenericType.of(CharacterStat.class)));

        // Create Accessors
        accountAccessor = new CassandraAccountAccessor(cqlSession, DATABASE_KEYSPACE);
        characterAccessor = new CassandraCharacterAccessor(cqlSession, DATABASE_KEYSPACE);
        friendAccessor = new CassandraFriendAccessor(cqlSession, DATABASE_KEYSPACE);
        giftAccessor = new CassandraGiftAccessor(cqlSession, DATABASE_KEYSPACE);
        memoAccessor = new CassandraMemoAccessor(cqlSession, DATABASE_KEYSPACE);
    }

    public static void shutdown() {
        cqlSession.close();
    }
}
