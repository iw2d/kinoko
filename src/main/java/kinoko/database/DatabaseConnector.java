package kinoko.database;

public interface DatabaseConnector {
    IdAccessor getIdAccessor();

    AccountAccessor getAccountAccessor();

    CharacterAccessor getCharacterAccessor();

    FriendAccessor getFriendAccessor();

    GuildAccessor getGuildAccessor();
    
    AllianceAccessor getAllianceAccessor();

    GiftAccessor getGiftAccessor();

    MemoAccessor getMemoAccessor();

    void initialize();

    void shutdown();
}
