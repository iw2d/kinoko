package kinoko.provider.reward;

public final class Reward {
    private final int itemId;
    private final int min;
    private final int max;
    private final double prob;
    private final int questId;
    private final int fieldId;

    public Reward(int itemId, int min, int max, double prob, int questId, int fieldId) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.prob = prob;
        this.questId = questId;
        this.fieldId = fieldId;
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

    public int getFieldId() {
        return fieldId;
    }

    public boolean isMoney() {
        return itemId == 0;
    }

    public boolean isQuest() {
        return questId != 0;
    }

    public boolean isFieldRequirement() {
        return fieldId != 0;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ", min=" + min +
                ", max=" + max +
                ", prob=" + prob +
                ", questId=" + questId +
                ", fieldId=" + fieldId +
                '}';
    }

    public static Reward item(int itemId, int min, int max, double prob) {
        return new Reward(itemId, min, max, prob, 0, 0);
    }

    public static Reward item(int itemId, int min, int max, double prob, int questId) {
        return new Reward(itemId, min, max, prob, questId, 0);
    }

    public static Reward item(int itemId, int min, int max, double prob, int questId, int fieldId) {
        return new Reward(itemId, min, max, prob, questId, fieldId);
    }

    public static Reward money(int min, int max, double prob) {
        return new Reward(0, min, max, prob, 0, 0);
    }
}
