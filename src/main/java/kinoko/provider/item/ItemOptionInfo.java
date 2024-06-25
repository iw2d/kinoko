package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.item.BodyPart;
import kinoko.world.item.ItemGrade;

import java.util.Map;
import java.util.Optional;

public final class ItemOptionInfo {
    private final int itemOptionId;
    private final int reqLevel;
    private final ItemOptionType optionType;
    private final Map<Integer, ItemOptionLevelData> levelData;

    public ItemOptionInfo(int itemOptionId, int reqLevel, ItemOptionType optionType, Map<Integer, ItemOptionLevelData> levelData) {
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

    public ItemOptionType getOptionType() {
        return optionType;
    }

    public Optional<ItemOptionLevelData> getLevelData(int optionLevel) {
        return Optional.ofNullable(levelData.get(optionLevel));
    }

    public boolean isMatchingGrade(ItemGrade itemGrade) {
        switch (itemGrade) {
            case NORMAL -> {
                return itemOptionId < 10000;
            }
            case RARE -> {
                return itemOptionId >= 10000 && itemOptionId < 20000;
            }
            case EPIC -> {
                return itemOptionId >= 20000 && itemOptionId < 30000;
            }
            case UNIQUE -> {
                return itemOptionId >= 30000 && itemOptionId < 40000;
            }
        }
        return false;
    }

    public boolean isMatchingLevel(int itemReqLevel) {
        return reqLevel <= itemReqLevel;
    }

    public boolean isMatchingType(BodyPart bodyPart) {
        switch (optionType) {
            case ANY_EQUIP -> {
                return true;
            }
            case WEAPON -> {
                return bodyPart == BodyPart.WEAPON || bodyPart == BodyPart.SHIELD;
            }
            case EXCEPT_WEAPON -> {
                return bodyPart != BodyPart.WEAPON && bodyPart != BodyPart.SHIELD;
            }
            case ANY_ARMOR -> {
                return bodyPart.isArmor();
            }
            case ACCESSORY -> {
                return bodyPart.isAccessory();
            }
            case CAP -> {
                return bodyPart == BodyPart.CAP;
            }
            case COAT -> {
                return bodyPart == BodyPart.CLOTHES;
            }
            case PANTS -> {
                return bodyPart == BodyPart.PANTS;
            }
            case GLOVE -> {
                return bodyPart == BodyPart.GLOVES;
            }
            case SHOES -> {
                return bodyPart == BodyPart.SHOES;
            }
        }
        return false;
    }

    public static ItemOptionInfo from(int itemOptionId, WzListProperty itemOptionProp) throws ProviderError {
        int reqLevel = 0;
        ItemOptionType optionType = ItemOptionType.ANY_EQUIP;
        if (itemOptionProp.get("info") instanceof WzListProperty infoProp) {
            reqLevel = WzProvider.getInteger(infoProp.get("reqLevel"), 0);
            optionType = ItemOptionType.getByValue(WzProvider.getInteger(infoProp.get("optionType")));
            assert optionType != null;
        }
        if (!(itemOptionProp.get("level") instanceof WzListProperty levelList)) {
            throw new ProviderError("Failed to resolve item option level list");
        }
        return new ItemOptionInfo(itemOptionId, reqLevel, optionType, ItemOptionLevelData.resolveLevelData(levelList));
    }
}
