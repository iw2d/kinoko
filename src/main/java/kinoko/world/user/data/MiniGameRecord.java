package kinoko.world.user.data;

import kinoko.server.dialog.miniroom.MiniRoomType;
import kinoko.server.packet.OutPacket;

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

    public int getOmokGameTotal() {
        return omokGameWins + omokGameTies + omokGameLosses;
    }

    public int getMemoryGameTotal() {
        return memoryGameWins + memoryGameTies + memoryGameLosses;
    }

    public static void processResult(MiniRoomType miniRoomType, MiniGameRecord winner, MiniGameRecord loser, boolean isDraw, boolean scorePenalty) {
        final double multiplier = scorePenalty ? 0.1 : 1.0;
        switch (miniRoomType) {
            case OmokRoom -> {
                // Update stats
                if (isDraw) {
                    winner.omokGameTies++;
                    loser.omokGameTies++;
                } else {
                    winner.omokGameWins++;
                    loser.omokGameLosses++;
                }
                // Update ratings
                final int k1 = winner.omokGameScore > 3000 ? 20 : (winner.getOmokGameTotal() > 50 ? 50 : 30);
                final int k2 = loser.omokGameScore > 3000 ? 20 : (loser.getOmokGameTotal() > 50 ? 30 : 20);
                final double r1 = computeScoreGain(winner.omokGameScore, loser.omokGameScore, isDraw ? 0.5 : 1.0, k1);
                final double r2 = computeScoreGain(loser.omokGameScore, winner.omokGameScore, isDraw ? 0.5 : 0.0, k2);
                winner.omokGameScore += (r1 * multiplier);
                loser.omokGameScore += (r2 * multiplier);
            }
            case MemoryGameRoom -> {
                // Update stats
                if (isDraw) {
                    winner.memoryGameTies++;
                    loser.memoryGameTies++;
                } else {
                    winner.memoryGameWins++;
                    loser.memoryGameLosses++;
                }
                // Update ratings
                final int k1 = winner.memoryGameScore > 3000 ? 20 : (winner.getMemoryGameTotal() > 50 ? 50 : 30);
                final int k2 = loser.memoryGameScore > 3000 ? 20 : (loser.getMemoryGameTotal() > 50 ? 30 : 20);
                final double r1 = computeScoreGain(winner.memoryGameScore, loser.memoryGameScore, isDraw ? 0.5 : 1.0, k1);
                final double r2 = computeScoreGain(loser.memoryGameScore, winner.memoryGameScore, isDraw ? 0.5 : 0.0, k2);
                winner.memoryGameScore += (r1 * multiplier);
                loser.memoryGameScore += (r2 * multiplier);
            }
        }
    }

    private static double computeScoreGain(double r1, double r2, double score, int k) {
        // Elo rating system, score = 1.0 (win) | 0.5 (tie) | 0.0 (loss)
        final double expectedScore = 1 / (1 + Math.pow(10, (r2 - r1) / 400));
        return k * (score - expectedScore);
    }
}
