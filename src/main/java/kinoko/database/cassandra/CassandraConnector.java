package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.servererrors.AlreadyExistsException;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.*;
import kinoko.database.cassandra.codec.*;
import kinoko.database.cassandra.table.*;
import kinoko.database.cassandra.type.*;
import kinoko.server.ServerConstants;
import kinoko.server.cashshop.CashItemInfo;
import kinoko.server.guild.GuildBoardComment;
import kinoko.server.guild.GuildBoardEntry;
import kinoko.server.guild.GuildMember;
import kinoko.world.item.*;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.data.MapTransferInfo;
import kinoko.world.user.data.MiniGameRecord;
import kinoko.world.user.data.WildHunterInfo;
import kinoko.world.user.stat.CharacterStat;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public final class CassandraConnector implements DatabaseConnector {
    public static final InetSocketAddress DATABASE_ADDRESS = new InetSocketAddress(ServerConstants.DATABASE_HOST, ServerConstants.DATABASE_PORT);
    public static final String DATABASE_DATACENTER = "datacenter1";
    public static final String DATABASE_KEYSPACE = "kinoko";
    public static final String PROFILE_ONE = "profile_one";
    private CqlSession cqlSession;
    private IdAccessor idAccessor;
    private AccountAccessor accountAccessor;
    private CharacterAccessor characterAccessor;
    private FriendAccessor friendAccessor;
    private GuildAccessor guildAccessor;
    private AllianceAccessor allianceAccessor;
    private GiftAccessor giftAccessor;
    private MemoAccessor memoAccessor;

    public boolean createKeyspace(CqlSession session, String keyspace) {
        try {
            session.execute(
                    SchemaBuilder.createKeyspace(keyspace)
                            .withSimpleStrategy(1)
                            .build()
            );
            return true;
        } catch (AlreadyExistsException e) {
            return false;
        }
    }

    private UserDefinedType getUserDefinedType(CqlSession session, String typeName) {
        return session.getMetadata()
                .getKeyspace(DATABASE_KEYSPACE)
                .flatMap(ks -> ks.getUserDefinedType(typeName))
                .orElseThrow(() -> new IllegalArgumentException("Missing UDT definition " + typeName));
    }

    private void registerCodec(CqlSession session, String typeName, Function<TypeCodec<UdtValue>, MappingCodec<UdtValue, ?>> constructor) {
        final CodecRegistry codecRegistry = session.getContext().getCodecRegistry();
        final TypeCodec<UdtValue> innerCodec = codecRegistry.codecFor(getUserDefinedType(session, typeName));
        ((MutableCodecRegistry) codecRegistry).register(constructor.apply(innerCodec));
    }

    // OVERRIDES -------------------------------------------------------------------------------------------------------
    @Override
    public IdAccessor getIdAccessor() {
        return idAccessor;
    }

    @Override
    public AccountAccessor getAccountAccessor() {
        return accountAccessor;
    }

    @Override
    public CharacterAccessor getCharacterAccessor() {
        return characterAccessor;
    }

    @Override
    public FriendAccessor getFriendAccessor() {
        return friendAccessor;
    }

    @Override
    public GuildAccessor getGuildAccessor() {
        return guildAccessor;
    }

    @Override
    public AllianceAccessor getAllianceAccessor() {
        return allianceAccessor;
    }

    @Override
    public GiftAccessor getGiftAccessor() {
        return giftAccessor;
    }

    @Override
    public MemoAccessor getMemoAccessor() {
        return memoAccessor;
    }

    @Override
    public void initialize() {
// Create Config
        final DriverConfigLoader configLoader = DriverConfigLoader.programmaticBuilder()
                // Default profile
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.of(5, ChronoUnit.SECONDS))
                .withString(DefaultDriverOption.REQUEST_CONSISTENCY, "ALL")
                .withString(DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, "SERIAL")
                // Fast profile
                .startProfile(PROFILE_ONE)
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.of(2, ChronoUnit.SECONDS))
                .withString(DefaultDriverOption.REQUEST_CONSISTENCY, "ONE")
                .withString(DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, "SERIAL")
                .endProfile()
                .build();
        // Create Session
        cqlSession = new CqlSessionBuilder()
                .addContactPoint(DATABASE_ADDRESS)
                .withLocalDatacenter(DATABASE_DATACENTER)
                .withConfigLoader(configLoader)
                .build();

        // Create Keyspace
        if (createKeyspace(cqlSession, DATABASE_KEYSPACE)) {
            // Create UDTs
            EquipDataUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
            PetDataUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
            RingDataUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
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
            GuildMemberUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
            GuildBoardCommentUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);
            GuildBoardEntryUDT.createUserDefinedType(cqlSession, DATABASE_KEYSPACE);

            // Create Tables
            IdTable.createTable(cqlSession, DATABASE_KEYSPACE);
            AccountTable.createTable(cqlSession, DATABASE_KEYSPACE);
            CharacterTable.createTable(cqlSession, DATABASE_KEYSPACE);
            FriendTable.createTable(cqlSession, DATABASE_KEYSPACE);
            GuildTable.createTable(cqlSession, DATABASE_KEYSPACE);
            GiftTable.createTable(cqlSession, DATABASE_KEYSPACE);
            MemoTable.createTable(cqlSession, DATABASE_KEYSPACE);
        }

        // Register Codecs
        registerCodec(cqlSession, EquipDataUDT.getTypeName(), (ic) -> new EquipDataCodec(ic, GenericType.of(EquipData.class)));
        registerCodec(cqlSession, PetDataUDT.getTypeName(), (ic) -> new PetDataCodec(ic, GenericType.of(PetData.class)));
        registerCodec(cqlSession, RingDataUDT.getTypeName(), (ic) -> new RingDataCodec(ic, GenericType.of(RingData.class)));
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
        registerCodec(cqlSession, GuildMemberUDT.getTypeName(), (ic) -> new GuildMemberCodec(ic, GenericType.of(GuildMember.class)));
        registerCodec(cqlSession, GuildBoardCommentUDT.getTypeName(), (ic) -> new GuildBoardCommentCodec(ic, GenericType.of(GuildBoardComment.class)));
        registerCodec(cqlSession, GuildBoardEntryUDT.getTypeName(), (ic) -> new GuildBoardEntryCodec(ic, GenericType.of(GuildBoardEntry.class)));

        // Create Accessors
        idAccessor = new CassandraIdAccessor(cqlSession, DATABASE_KEYSPACE);
        accountAccessor = new CassandraAccountAccessor(cqlSession, DATABASE_KEYSPACE);
        characterAccessor = new CassandraCharacterAccessor(cqlSession, DATABASE_KEYSPACE);
        friendAccessor = new CassandraFriendAccessor(cqlSession, DATABASE_KEYSPACE);
        guildAccessor = new CassandraGuildAccessor(cqlSession, DATABASE_KEYSPACE);
        giftAccessor = new CassandraGiftAccessor(cqlSession, DATABASE_KEYSPACE);
        memoAccessor = new CassandraMemoAccessor(cqlSession, DATABASE_KEYSPACE);
    }

    @Override
    public void shutdown() {
        if (cqlSession != null) {
            cqlSession.close();
        }
    }
}
