package kinoko.world.dialog.miniroom;

import java.util.*;

public final class MemoryGame {
    private final Map<Integer, Integer> score = new HashMap<>();
    private final List<Integer> shuffle;

    public MemoryGame(int gameSpec) {
        this.shuffle = shuffleCards(gameSpec);
    }

    public int getTotalScore() {
        return score.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isScorePenalty() {
        return getTotalScore() < (shuffle.size() / 10);
    }

    public List<Integer> getShuffle() {
        return shuffle;
    }

    public TurnUpResult turnUpCard(int firstCard, int secondCard, int userIndex) {
        if (Objects.equals(shuffle.get(firstCard), shuffle.get(secondCard))) {
            score.put(userIndex, score.getOrDefault(userIndex, 0) + 2);
            if (getTotalScore() >= shuffle.size()) {
                final int half = shuffle.size() / 2;
                if (score.get(userIndex) > half) {
                    return TurnUpResult.WIN;
                } else if (score.get(userIndex) == half) {
                    return TurnUpResult.DRAW;
                } else {
                    return TurnUpResult.LOSE;
                }
            } else {
                return TurnUpResult.MATCH;
            }
        }
        return TurnUpResult.NO_MATCH;
    }

    private static List<Integer> shuffleCards(int gameSpec) {
        final int size;
        if (gameSpec == 0) {
            size = 4 * 3;
        } else if (gameSpec == 1) {
            size = 5 * 4;
        } else {
            size = 6 * 5;
        }
        final List<Integer> shuffle = new ArrayList<>();
        for (int i = 0; i < size / 2; i++) {
            shuffle.add(i);
            shuffle.add(i);
        }
        Collections.shuffle(shuffle);
        return shuffle;
    }

    public enum TurnUpResult {
        NO_MATCH,
        MATCH,
        WIN,
        DRAW,
        LOSE
    }
}
