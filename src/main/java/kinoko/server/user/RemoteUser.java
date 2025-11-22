package kinoko.server.user;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.User;

public final class RemoteUser implements Encodable {
    private final int accountId;
    private final int characterId;
    private final String characterName;
    private final int level;
    private final int job;
    private int channelId;
    private int fieldId;
    private int messengerId;
    private int partyId;
    private int guildId;
    private int allianceId;
    private RemoteTownPortal townPortal;

    public RemoteUser(int accountId, int characterId, String characterName, int level, int job, int channelId, int fieldId, int messengerId, int partyId, int guildId, int allianceId, RemoteTownPortal townPortal) {
        this.accountId = accountId;
        this.characterId = characterId;
        this.characterName = characterName;
        this.level = level;
        this.job = job;
        this.channelId = channelId;
        this.fieldId = fieldId;
        this.messengerId = messengerId;
        this.partyId = partyId;
        this.guildId = guildId;
        this.allianceId = allianceId;
        this.townPortal = townPortal;
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

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(int messengerId) {
        this.messengerId = messengerId;
    }

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public int getGuildId() {
        return guildId;
    }

    public void setGuildId(int guildId) {
        this.guildId = guildId;
    }

    public int getAllianceId() {
        return guildId;
    }

    public void setAllianceId(int allianceId) {
        this.allianceId = allianceId;
    }

    public RemoteTownPortal getTownPortal() {
        return townPortal != null ? townPortal : RemoteTownPortal.EMPTY;
    }

    public void setTownPortal(RemoteTownPortal townPortal) {
        this.townPortal = townPortal;
    }

    @Override
    public String toString() {
        return "RemoteUser{" +
                "accountId=" + accountId +
                ", characterId=" + characterId +
                ", characterName='" + characterName + '\'' +
                ", level=" + level +
                ", job=" + job +
                ", channelId=" + channelId +
                ", fieldId=" + fieldId +
                ", messengerId=" + messengerId +
                ", partyId=" + partyId +
                ", guildId=" + guildId +
                ", allianceId=" + allianceId +
                ", townPortal=" + townPortal +
                '}';
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeString(characterName);
        outPacket.encodeInt(level);
        outPacket.encodeInt(job);
        outPacket.encodeInt(channelId);
        outPacket.encodeInt(fieldId);
        outPacket.encodeInt(messengerId);
        outPacket.encodeInt(partyId);
        outPacket.encodeInt(guildId);
        outPacket.encodeInt(allianceId);
        outPacket.encodeByte(townPortal != null);
        if (townPortal != null) {
            townPortal.encode(outPacket);
        }
    }

    public static RemoteUser decode(InPacket inPacket) {
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final String characterName = inPacket.decodeString();
        final int level = inPacket.decodeInt();
        final int job = inPacket.decodeInt();
        final int channelId = inPacket.decodeInt();
        final int fieldId = inPacket.decodeInt();
        final int messengerId = inPacket.decodeInt();
        final int partyId = inPacket.decodeInt();
        final int guildId = inPacket.decodeInt();
        final int allianceId = inPacket.decodeInt();
        final RemoteTownPortal townPortal = inPacket.decodeBoolean() ? RemoteTownPortal.decode(inPacket) : null;
        return new RemoteUser(
                accountId,
                characterId,
                characterName,
                level,
                job,
                channelId,
                fieldId,
                messengerId,
                partyId,
                guildId,
                allianceId,
                townPortal
        );
    }

    public static RemoteUser from(User user) {
        return new RemoteUser(
                user.getAccountId(),
                user.getCharacterId(),
                user.getCharacterName(),
                user.getLevel(),
                user.getJob(),
                user.getChannelId(),
                user.getFieldId(),
                user.getMessengerId(),
                user.getCharacterData().getPartyId(),
                user.getCharacterData().getGuildId(),
                user.getCharacterData().getAllianceId(),
                user.getTownPortal() != null ? RemoteTownPortal.from(user.getTownPortal()) : null
        );
    }
}
