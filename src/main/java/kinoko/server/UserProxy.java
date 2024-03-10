package kinoko.server;

import kinoko.world.user.User;

public final class UserProxy {
    private final ChannelServer connectedServer;
    private final int accountId;
    private final int characterId;
    private final String characterName;
    private int level;
    private int job;

    public UserProxy(ChannelServer connectedServer, int accountId, int characterId, String characterName, int level, int job) {
        this.connectedServer = connectedServer;
        this.accountId = accountId;
        this.characterId = characterId;
        this.characterName = characterName;
        this.level = level;
        this.job = job;
    }

    public ChannelServer getConnectedServer() {
        return connectedServer;
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

    public static UserProxy from(ChannelServer connectedServer, User user) {
        return new UserProxy(
                connectedServer,
                user.getAccountId(),
                user.getCharacterId(),
                user.getCharacterName(),
                user.getLevel(),
                user.getJob()
        );
    }
}
