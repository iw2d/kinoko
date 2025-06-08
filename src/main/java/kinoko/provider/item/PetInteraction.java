package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;

public final class PetInteraction {
    private final int incTameness;
    private final int levelMin;
    private final int levelMax;
    private final int prop;

    public PetInteraction(int incTameness, int levelMin, int levelMax, int prop) {
        this.incTameness = incTameness;
        this.levelMin = levelMin;
        this.levelMax = levelMax;
        this.prop = prop;
    }

    public int getIncTameness() {
        return incTameness;
    }

    public int getLevelMin() {
        return levelMin;
    }

    public int getLevelMax() {
        return levelMax;
    }

    public int getProp() {
        return prop;
    }

    public static PetInteraction from(WzProperty interactProp) throws ProviderError {
        return new PetInteraction(
                WzProvider.getInteger(interactProp.get("inc")),
                WzProvider.getInteger(interactProp.get("l0")),
                WzProvider.getInteger(interactProp.get("l1")),
                WzProvider.getInteger(interactProp.get("prob"))
        );
    }
}
