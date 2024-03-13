package kinoko.server.node;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.User;

public final class UserProxy implements Encodable {
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

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(channelId);
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeString(characterName);
        outPacket.encodeInt(level);
        outPacket.encodeInt(job);
    }

    public static UserProxy decode(InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final String characterName = inPacket.decodeString();
        final int level = inPacket.decodeInt();
        final int job = inPacket.decodeInt();
        return new UserProxy(
                channelId,
                accountId,
                characterId,
                characterName,
                level,
                job
        );
    }

    public static UserProxy from(User user) {
        return new UserProxy(
                user.getChannelId(),
                user.getAccountId(),
                user.getCharacterId(),
                user.getCharacterName(),
                user.getLevel(),
                user.getJob()
        );
    }
}
