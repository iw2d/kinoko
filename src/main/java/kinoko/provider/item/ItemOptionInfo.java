package kinoko.provider.item;

import java.util.Map;
import java.util.Optional;

public final class ItemOptionInfo {
    private final int itemOptionId;
    private final int reqLevel;
    private final int optionType;
    private final Map<Integer, ItemOptionLevelData> levelData;

    public ItemOptionInfo(int itemOptionId, int reqLevel, int optionType, Map<Integer, ItemOptionLevelData> levelData) {
        this.itemOptionId = itemOptionId;
        this.reqLevel = reqLevel;
        this.optionType = optionType;
        this.levelData = levelData;
    }

    public int getItemOptionId() {
        return itemOptionId;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public int getOptionType() {
        return optionType;
    }

    public Optional<ItemOptionLevelData> getLevelData(int optionLevel) {
        return Optional.ofNullable(levelData.get(optionLevel));
    }
}
