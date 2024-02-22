package kinoko.provider.reward;

public final class Reward {
    private final int itemId;
    private final int min;
    private final int max;
    private final double prob;
    private final boolean quest;

    public Reward(int itemId, int min, int max, double prob, boolean quest) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.prob = prob;
        this.quest = quest;
    }

    public int getItemId() {
        return itemId;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public double getProb() {
        return prob;
    }

    public boolean isQuest() {
        return quest;
    }
}
