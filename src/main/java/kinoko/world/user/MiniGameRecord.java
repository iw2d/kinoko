package kinoko.world.user;

import kinoko.server.dialog.miniroom.GameResultType;
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
            case OMOK_ROOM -> {
                outPacket.encodeInt(omokGameWins);
                outPacket.encodeInt(omokGameTies);
                outPacket.encodeInt(omokGameLosses);
                outPacket.encodeInt(Math.round(omokGameScore));
            }
            case MEMORY_GAME_ROOM -> {
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

    public void processResult(MiniRoomType miniRoomType, GameResultType resultType, MiniGameRecord other) {
        // Elo rating system, not sure of the exact implementation in official - may need to stagger the k factor
        final boolean isDraw = resultType == GameResultType.DRAW;
        switch (miniRoomType) {
            case OMOK_ROOM -> {
                // Update stats
                if (isDraw) {
                    this.omokGameTies++;
                    other.omokGameTies++;
                } else {
                    this.omokGameWins++;
                    other.omokGameLosses++;
                }
                // Update ratings
                final double r1 = computeRating(this.omokGameScore, other.omokGameScore, isDraw ? 0.5 : 1.0, 32);
                final double r2 = computeRating(other.omokGameScore, this.omokGameScore, isDraw ? 0.5 : 0.0, 32);
                this.omokGameScore = r1;
                other.omokGameScore = r2;
            }
            case MEMORY_GAME_ROOM -> {
                // Update stats
                if (isDraw) {
                    this.memoryGameTies++;
                    other.memoryGameTies++;
                } else {
                    this.memoryGameWins++;
                    other.memoryGameLosses++;
                }
                // Update ratings
                final double r1 = computeRating(this.memoryGameScore, other.memoryGameScore, isDraw ? 0.5 : 1.0, 32);
                final double r2 = computeRating(other.memoryGameScore, this.memoryGameScore, isDraw ? 0.5 : 0.0, 32);
                this.memoryGameScore = r1;
                other.memoryGameScore = r2;
            }
        }
    }

    private static double computeRating(double r1, double r2, double score, int k) {
        // Elo rating system, score = 1.0 (win) | 0.5 (tie) | 0.0 (loss)
        final double expectedScore = 1 / (1 + Math.pow(10, (r2 - r1) / 400));
        return (r1 + k * (score - expectedScore));
    }
}
