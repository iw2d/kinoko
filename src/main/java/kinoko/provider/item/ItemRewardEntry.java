package kinoko.provider.item;

public final class ItemRewardEntry {
    private final int itemId;
    private final int count;
    private final int probability;
    private final int period;
    private final String effect;

    public ItemRewardEntry(int itemId, int count, int probability, int period, String effect) {
        this.itemId = itemId;
        this.count = count;
        this.probability = probability;
        this.period = period;
        this.effect = effect;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public int getProbability() {
        return probability;
    }

    public int getPeriod() {
        return period;
    }

    public String getEffect() {
        return effect;
    }

    public boolean hasEffect() {
        return effect != null && !effect.isEmpty();
    }
}
