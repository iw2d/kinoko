package kinoko.provider.reward;

public final class Reward {
    private final int itemId;
    private final int min;
    private final int max;
    private final double prob;
    private final int questId;

    public Reward(int itemId, int min, int max, double prob, int questId) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.prob = prob;
        this.questId = questId;
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

    public int getQuestId() {
        return questId;
    }

    public boolean isMoney() {
        return itemId == 0;
    }

    public boolean isQuest() {
        return questId != 0;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ", min=" + min +
                ", max=" + max +
                ", prob=" + prob +
                ", questId=" + questId +
                '}';
    }

    public static Reward item(int itemId, int min, int max, double prob) {
        return new Reward(itemId, min, max, prob, 0);
    }

    public static Reward item(int itemId, int min, int max, double prob, int questId) {
        return new Reward(itemId, min, max, prob, questId);
    }

    public static Reward money(int min, int max, double prob) {
        return new Reward(0, min, max, prob, 0);
    }
}
