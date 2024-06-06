package kinoko.world.user;

import kinoko.server.dialog.miniroom.MiniRoomType;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class MiniGameRecord implements Encodable {
    private final MiniRoomType type;
    private int wins;
    private int ties;
    private int losses;
    private int score;

    public MiniGameRecord(MiniRoomType type) {
        this.type = type;
    }

    public MiniRoomType getType() {
        return type;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_MiniGameRecord::Decode (20)
        outPacket.encodeInt(type.getValue());
        outPacket.encodeInt(0); // nWins
        outPacket.encodeInt(0); // nTies
        outPacket.encodeInt(0); // nLosses
        outPacket.encodeInt(2000); // nScore
    }
}
