package kinoko.world.user.info;

import kinoko.server.packet.OutPacket;
import kinoko.world.dialog.miniroom.MiniRoomType;

public final class MiniGameRecord {
    private int omokGameWins = 0;
    private int omokGameTies = 0;
    private int omokGameLosses = 0;
    private double omokGameScore = 2000.0;

    private int memoryGameWins = 0;
    private int memoryGameTies = 0;
    private int memoryGameLosses = 0;
    private double memoryGameScore = 2000.0;

    public int getOmokGameWins() {
        return omokGameWins;
    }

    public void setOmokGameWins(int omokGameWins) {
        this.omokGameWins = omokGameWins;
    }

    public int getOmokGameTies() {
        return omokGameTies;
    }

    public void setOmokGameTies(int omokGameTies) {
        this.omokGameTies = omokGameTies;
    }

    public int getOmokGameLosses() {
        return omokGameLosses;
    }

    public void setOmokGameLosses(int omokGameLosses) {
        this.omokGameLosses = omokGameLosses;
    }

    public double getOmokGameScore() {
        return omokGameScore;
    }

    public void setOmokGameScore(double omokGameScore) {
        this.omokGameScore = omokGameScore;
    }

    public int getMemoryGameWins() {
        return memoryGameWins;
    }

    public void setMemoryGameWins(int memoryGameWins) {
        this.memoryGameWins = memoryGameWins;
    }

    public int getMemoryGameTies() {
        return memoryGameTies;
    }

    public void setMemoryGameTies(int memoryGameTies) {
        this.memoryGameTies = memoryGameTies;
    }

    public int getMemoryGameLosses() {
        return memoryGameLosses;
    }

    public void setMemoryGameLosses(int memoryGameLosses) {
        this.memoryGameLosses = memoryGameLosses;
    }

    public double getMemoryGameScore() {
        return memoryGameScore;
    }

    public void setMemoryGameScore(double memoryGameScore) {
        this.memoryGameScore = memoryGameScore;
    }

    public void encode(MiniRoomType miniGameType, OutPacket outPacket) {
        outPacket.encodeInt(miniGameType.getValue());
        switch (miniGameType) {
            case OmokRoom -> {
                outPacket.encodeInt(omokGameWins);
                outPacket.encodeInt(omokGameTies);
                outPacket.encodeInt(omokGameLosses);
                outPacket.encodeInt(Math.round(omokGameScore));
            }
            case MemoryGameRoom -> {
                outPacket.encodeInt(memoryGameWins);
                outPacket.encodeInt(memoryGameTies);
                outPacket.encodeInt(memoryGameLosses);
                outPacket.encodeInt(Math.round(memoryGameScore));
            }
            default -> {
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
            }
        }
    }

    public void processResult(MiniRoomType miniRoomType, MiniGameRecord other, boolean isDraw, boolean scorePenalty) {
        final double multiplier = scorePenalty ? 0.1 : 1.0;
        switch (miniRoomType) {
            case OmokRoom -> {
                // Update stats
                if (isDraw) {
                    this.omokGameTies++;
                    other.omokGameTies++;
                } else {
                    this.omokGameWins++;
                    other.omokGameLosses++;
                }
                // Update ratings
                final int k1 = this.omokGameScore > 3000 ? 20 : (this.getOmokGameTotal() > 50 ? 50 : 30);
                final int k2 = other.omokGameScore > 3000 ? 20 : (other.getOmokGameTotal() > 50 ? 30 : 20);
                final double r1 = computeScoreGain(this.omokGameScore, other.omokGameScore, isDraw ? 0.5 : 1.0, k1);
                final double r2 = computeScoreGain(other.omokGameScore, this.omokGameScore, isDraw ? 0.5 : 0.0, k2);
                this.omokGameScore += (r1 * multiplier);
                other.omokGameScore += (r2 * multiplier);
            }
            case MemoryGameRoom -> {
                // Update stats
                if (isDraw) {
                    this.memoryGameTies++;
                    other.memoryGameTies++;
                } else {
                    this.memoryGameWins++;
                    other.memoryGameLosses++;
                }
                // Update ratings
                final int k1 = this.memoryGameScore > 3000 ? 20 : (this.getMemoryGameTotal() > 50 ? 50 : 30);
                final int k2 = other.memoryGameScore > 3000 ? 20 : (other.getMemoryGameTotal() > 50 ? 30 : 20);
                final double r1 = computeScoreGain(this.memoryGameScore, other.memoryGameScore, isDraw ? 0.5 : 1.0, k1);
                final double r2 = computeScoreGain(other.memoryGameScore, this.memoryGameScore, isDraw ? 0.5 : 0.0, k2);
                this.memoryGameScore += (r1 * multiplier);
                other.memoryGameScore += (r2 * multiplier);
            }
        }
    }

    private int getOmokGameTotal() {
        return omokGameWins + omokGameTies + omokGameLosses;
    }

    private int getMemoryGameTotal() {
        return memoryGameWins + memoryGameTies + memoryGameLosses;
    }

    private static double computeScoreGain(double r1, double r2, double score, int k) {
        // Elo rating system, score = 1.0 (win) | 0.5 (tie) | 0.0 (loss)
        final double expectedScore = 1 / (1 + Math.pow(10, (r2 - r1) / 400));
        return k * (score - expectedScore);
    }
}
