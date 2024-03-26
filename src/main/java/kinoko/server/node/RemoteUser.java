package kinoko.server.node;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.User;

public final class RemoteUser implements Encodable {
    private final int channelId;
    private final int fieldId;
    private final int accountId;
    private final int characterId;
    private final String characterName;
    private final int level;
    private final int job;

    public RemoteUser(int channelId, int fieldId, int accountId, int characterId, String characterName, int level, int job) {
        this.channelId = channelId;
        this.fieldId = fieldId;
        this.accountId = accountId;
        this.characterId = characterId;
        this.characterName = characterName;
        this.level = level;
        this.job = job;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getFieldId() {
        return fieldId;
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

    public int getJob() {
        return job;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(channelId);
        outPacket.encodeInt(fieldId);
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeString(characterName);
        outPacket.encodeInt(level);
        outPacket.encodeInt(job);
    }

    public static RemoteUser decode(InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final int fieldId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final String characterName = inPacket.decodeString();
        final int level = inPacket.decodeInt();
        final int job = inPacket.decodeInt();
        return new RemoteUser(
                channelId,
                fieldId,
                accountId,
                characterId,
                characterName,
                level,
                job
        );
    }

    public static RemoteUser from(User user) {
        return new RemoteUser(
                user.getChannelId(),
                user.getFieldId(),
                user.getAccountId(),
                user.getCharacterId(),
                user.getCharacterName(),
                user.getLevel(),
                user.getJob()
        );
    }
}
