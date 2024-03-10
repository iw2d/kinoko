package kinoko.server;

public final class UserProxy {
    private final ChannelServer connectedServer;
    private final int characterId;
    private final String characterName;
    private int level;
    private int job;

    public UserProxy(ChannelServer connectedServer, int characterId, String characterName, int level, int job) {
        this.connectedServer = connectedServer;
        this.characterId = characterId;
        this.characterName = characterName;
        this.level = level;
        this.job = job;
    }

    public ChannelServer getConnectedServer() {
        return connectedServer;
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

    public static String normalizeName(String name) {
        return name.toLowerCase();
    }
}
