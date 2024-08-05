package kinoko.server.guild;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class GuildRanking implements Encodable {
    private final String guildName;
    private final int points;
    private final short mark;
    private final byte markColor;
    private final short markBg;
    private final byte markBgColor;

    public GuildRanking(String guildName, int points, short mark, byte markColor, short markBg, byte markBgColor) {
        this.guildName = guildName;
        this.points = points;
        this.mark = mark;
        this.markColor = markColor;
        this.markBg = markBg;
        this.markBgColor = markBgColor;
    }

    public String getGuildName() {
        return guildName;
    }

    public int getPoints() {
        return points;
    }

    public short getMark() {
        return mark;
    }

    public byte getMarkColor() {
        return markColor;
    }

    public short getMarkBg() {
        return markBg;
    }

    public byte getMarkBgColor() {
        return markBgColor;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(guildName);
        outPacket.encodeInt(points);
        outPacket.encodeInt(mark);
        outPacket.encodeInt(markColor);
        outPacket.encodeInt(markBg);
        outPacket.encodeInt(markBgColor);
    }
}
