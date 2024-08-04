package kinoko.server.guild;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class GuildRequest implements Encodable {
    private final GuildRequestType requestType;
    private int guildId;
    private String guildName;

    public GuildRequest(GuildRequestType requestType) {
        this.requestType = requestType;
    }

    public GuildRequestType getRequestType() {
        return requestType;
    }

    public int getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case LoadGuild, RemoveGuild -> {
                outPacket.encodeInt(guildId);
            }
            case CreateNewGuild -> {
                outPacket.encodeInt(guildId);
                outPacket.encodeString(guildName);
            }
        }
    }

    public static GuildRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final GuildRequest request = new GuildRequest(GuildRequestType.getByValue(type));
        switch (request.requestType) {
            case LoadGuild, RemoveGuild -> {
                request.guildId = inPacket.decodeInt();
            }
            case CreateNewGuild -> {
                request.guildId = inPacket.decodeInt();
                request.guildName = inPacket.decodeString();
            }
        }
        return request;
    }

    public static GuildRequest loadGuild(int guildId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.LoadGuild);
        request.guildId = guildId;
        return request;
    }

    public static GuildRequest removeGuild(int guildId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.RemoveGuild);
        request.guildId = guildId;
        return request;
    }

    public static GuildRequest createNewGuild(int guildId, String guildName) {
        final GuildRequest request = new GuildRequest(GuildRequestType.CreateNewGuild);
        request.guildId = guildId;
        request.guildName = guildName;
        return request;
    }
}
