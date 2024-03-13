package kinoko.server.node;

public final class UserProxy {
    private final int channelId;
    private final int accountId;
    private final int characterId;
    private final String characterName;
    private int level;
    private int job;

    public UserProxy(int channelId, int accountId, int characterId, String characterName, int level, int job) {
        this.channelId = channelId;
        this.accountId = accountId;
        this.characterId = characterId;
        this.characterName = characterName;
        this.level = level;
        this.job = job;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }
}
