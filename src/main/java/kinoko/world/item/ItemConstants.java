package kinoko.world.item;

import kinoko.util.Util;
import kinoko.world.GameConstants;

public final class ItemConstants {
    // ITEM IDS --------------------------------------------------------------------------------------------------------

    public static final int HORNTAIL_NECKLACE = 1122000;
    public static final int CHAOS_HORNTAIL_NECKLACE = 1122076;

    public static final int ADVANCED_EQUIP_ENHANCEMENT_SCROLL = 2049300;
    public static final int EQUIP_ENHANCEMENT_SCROLL = 2049301;
    public static final int ADVANCED_POTENTIAL_SCROLL = 2049400;
    public static final int POTENTIAL_SCROLL = 2049401;
    public static final int WHITE_SCROLL = 2340000;
    public static final int MIRACLE_CUBE_FRAGMENT = 2430112;
    public static final int MAGNIFYING_GLASS_BASIC = 2460000;
    public static final int MAGNIFYING_GLASS_AVERAGE = 2460001;
    public static final int MAGNIFYING_GLASS_ADVANCED = 2460002;
    public static final int MAGNIFYING_GLASS_PREMIUM = 2460003;

    public static final int OMOK_SET_BASE = 4080000;
    public static final int OMOK_SET_END = 4080011;
    public static final int MATCH_CARDS = 4080100;

    public static final int MIRACLE_CUBE = 5062000;
    public static final int REGULAR_STORE_PERMIT = 5140000;
    public static final int WHEEL_OF_DESTINY = 5510000;


    // ITEM UPGRADE CONSTANTS ------------------------------------------------------------------------------------------

    public static final int EQUIP_ENHANCEMENT_STAT_BASE = 2;
    public static final int EQUIP_ENHANCEMENT_ATT_BASE = 2;
    public static final int EQUIP_ENHANCEMENT_DEF_BASE = 2;

    public static final int POTENTIAL_THIRD_LINE_PROP = 50;
    public static final int POTENTIAL_PRIME_LINE2_PROP = 10;
    public static final int POTENTIAL_PRIME_LINE3_PROP = 1;
    public static final double POTENTIAL_TIER_UP_EPIC = 0.06;
    public static final double POTENTIAL_TIER_UP_UNIQUE = 0.018;


    public static int getGenderFromId(int itemId) {
        if (itemId / 1000000 != 1) {
            return 2;
        }
        switch (itemId / 1000 % 10) {
            case 0 -> {
                return 0;
            }
            case 1 -> {
                return 1;
            }
            default -> {
                return 2;
            }
        }
    }

    public static boolean isEquip(int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isConsume(int itemId) {
        return itemId / 1000000 == 2;
    }

    public static boolean isInstall(int itemId) {
        return itemId / 1000000 == 3;
    }

    public static boolean isEtc(int itemId) {
        return itemId / 1000000 == 4;
    }

    public static boolean isWeapon(int itemId) {
        final int weaponPrefix = itemId / 100000;
        return weaponPrefix == 13 || weaponPrefix == 14 || weaponPrefix == 16 || weaponPrefix == 17;
    }

    public static boolean isPet(int itemId) {
        return itemId / 10000 == 500;
    }

    public static boolean isPetEquipItem(int itemId) {
        return itemId / 100000 == 18;
    }

    public static boolean isPetFoodItem(int itemId) {
        return itemId / 10000 == 212;
    }

    public static boolean isMobSummonItem(int itemId) {
        return itemId / 10000 == 210;
    }

    public static boolean isPortalScrollItem(int itemId) {
        return itemId / 10000 == 203;
    }

    public static boolean isRechargeableItem(int itemId) {
        return itemId / 10000 == 207 || itemId / 10000 == 233;
    }

    public static boolean isJavelinItem(int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isPelletItem(int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isCorrectBulletItem(int weaponItemId, int itemId) {
        final WeaponType wt = WeaponType.getByItemId(weaponItemId);
        if (wt == WeaponType.BOW || weaponItemId == 1472063) {
            return itemId / 1000 == 2060;
        }
        switch (wt) {
            case CROSSBOW -> {
                return itemId / 1000 == 2061;
            }
            case THROWINGGLOVE -> {
                return itemId / 10000 == 207;
            }
            case GUN -> {
                return itemId / 10000 == 233;
            }
        }
        return false;
    }

    public static boolean isScriptRunItem(int itemId) {
        return itemId / 10000 == 243 || itemId == 3994225;
    }

    public static boolean isSkillLearnItem(int itemId) {
        if (itemId / 10000 == 228) {
            return true;
        }
        return isMasteryBookItem(itemId);
    }

    public static boolean isMasteryBookItem(int itemId) {
        return itemId / 10000 == 229 || itemId / 10000 == 562;
    }

    public static boolean isRecoverSlotItem(int itemId) {
        return itemId / 100 == 20490; // clean slate scrolls
    }

    public static boolean isBlackUpgradeItem(int itemId) {
        return itemId / 100 == 20491; // chaos scrolls
    }

    public static boolean isAccUpgradeItem(int itemId) {
        return itemId / 100 == 20492; // scrolls for accessory
    }

    public static boolean isHyperUpgradeItem(int itemId) {
        return itemId / 100 == 20493; // equip enhancement scrolls
    }

    public static boolean isItemOptionUpgradeItem(int itemId) {
        return itemId / 100 == 20494; // potential scrolls
    }

    public static boolean isReleaseItem(int itemId) {
        return itemId / 10000 == 246; // magnifying glass
    }

    public static boolean isNewUpgradeItem(int itemId) {
        return itemId / 1000 == 2046;
    }

    public static boolean isDurabilityUpgradeItem(int itemId) {
        return itemId / 1000 == 2047;
    }

    public static boolean isCorrectUpgradeEquip(int upgradeItemId, int equipItemId) {
        if (upgradeItemId / 10000 == 249 || upgradeItemId / 10000 == 247) {
            return true;
        }
        if (upgradeItemId / 10000 != 204 || !isEquip(equipItemId)) {
            return false;
        }
        if (isRecoverSlotItem(upgradeItemId) || isBlackUpgradeItem(upgradeItemId) && !isPetEquipItem(equipItemId) ||
                isHyperUpgradeItem(upgradeItemId) || isItemOptionUpgradeItem(upgradeItemId)) {
            return true;
        }
        final int upgradeItemType = (upgradeItemId - 2040000) / 100;
        final int equipItemType = equipItemId / 10000 % 100;
        if (isAccUpgradeItem(upgradeItemId)) {
            return equipItemType >= 11 && equipItemType <= 13; // ring, pendant, belt
        }
        if (isNewUpgradeItem(upgradeItemId) || isDurabilityUpgradeItem(upgradeItemId)) {
            switch (upgradeItemType) {
                case 0 -> {
                    return (equipItemType - 30) <= 9; // scroll for one-handed weapon
                }
                case 1 -> {
                    return (equipItemType - 40) <= 9; // scroll for two-handed weapon
                }
                case 2 -> {
                    return equipItemType == 0 || equipItemType > 3 && equipItemType <= 10; // scroll for armor
                }
                case 3 -> {
                    switch (equipItemType) {
                        case 1, 2, 3, 11, 12, 13, 14 -> {
                            // scroll for accessory
                            return true;
                        }
                        default -> {
                            return false;
                        }
                    }
                }
                default -> {
                    return true;
                }
            }
        }
        return upgradeItemType == equipItemType;
    }

    public static boolean isUpgradeScrollNoConsumeWhiteScroll(int itemId) {
        // scroll for spikes on shoes, scroll for cape for cold protection, clean slate scrolls
        return itemId == 2040727 || itemId == 2041058 || isRecoverSlotItem(itemId);
    }

    public static int getHyperUpgradeSuccessProp(int itemId, int chuc) {
        if (itemId == ADVANCED_EQUIP_ENHANCEMENT_SCROLL) {
            return switch (chuc) {
                case 0 -> 100;
                case 1 -> 90;
                case 2 -> 80;
                case 3 -> 70;
                case 4 -> 60;
                case 5 -> 50;
                case 6 -> 40;
                case 7 -> 30;
                case 8 -> 20;
                default -> 10;
            };
        } else if (itemId == EQUIP_ENHANCEMENT_SCROLL) {
            return switch (chuc) {
                case 0 -> 80;
                case 1 -> 70;
                case 2 -> 60;
                case 3 -> 50;
                case 4 -> 40;
                case 5 -> 30;
                case 6 -> 20;
                default -> 10;
            };
        }
        return 0;
    }

    public static int getItemOptionUpgradeSuccessProp(int itemId) {
        if (itemId == ADVANCED_POTENTIAL_SCROLL) {
            return 90;
        } else if (itemId == POTENTIAL_SCROLL) {
            return 70;
        }
        return 0;
    }

    public static int getReleaseItemLevelLimit(int itemId) {
        if (itemId == MAGNIFYING_GLASS_BASIC) {
            return 30;
        } else if (itemId == MAGNIFYING_GLASS_AVERAGE) {
            return 70;
        } else if (itemId == MAGNIFYING_GLASS_ADVANCED) {
            return 120;
        } else if (itemId == MAGNIFYING_GLASS_PREMIUM) {
            return GameConstants.LEVEL_MAX;
        }
        return -1;
    }

    public static int getVariation(int v, ItemVariationOption option) {
        if (v <= 0 || option == ItemVariationOption.NONE) {
            return v;
        }
        final int a = Math.min(
                v / (option == ItemVariationOption.GACHAPON ? 5 : 10) + 1,
                option == ItemVariationOption.GACHAPON ? 7 : 5
        );
        final int b = 1 << (a + 2);
        int c = Util.getRandom(b);
        int d = -2;
        int i = option == ItemVariationOption.GACHAPON ? (a + 2) : 7;
        while (i-- > 0) {
            d += c & 1;
            c >>= 1;
        }
        return Math.max(v + (Util.succeedProp(50) ? d : -d), 0);
    }

    public static boolean isPortableChairItem(int itemId) {
        return itemId / 10000 == 301;
    }

    public static boolean isCashEffectItem(int itemId) {
        return itemId / 10000 == 501;
    }

    public static boolean isNonCashEffectItem(int itemId) {
        return itemId / 10000 == 429;
    }

    public static boolean isCoupleEquipItem(int itemId) {
        return itemId / 100 == 11120 && itemId != 1112000;
    }

    public static boolean isFriendshipEquipItem(int itemId) {
        return itemId / 100 == 11128 && itemId % 10 <= 2;
    }

    public static boolean isMatchedItemIdGender(int itemId, int gender) {
        final int genderFromId = getGenderFromId(itemId);
        return gender == 2 || genderFromId == 2 || gender == genderFromId;
    }

    public static boolean isCorrectBodyPart(int itemId, BodyPart bodyPart, int gender) {
        if (!isMatchedItemIdGender(itemId, gender)) {
            return false;
        }
        return BodyPart.getByItemId(itemId).contains(bodyPart);
    }

    public static BodyPart getExclusiveEquipItemBodyPart(Inventory equipped, int itemId, boolean isCash) {
        // CItemInfo::GetExclusiveEquipItemBodyPart
        final BodyPart result = getExclusiveWeaponShieldBodyPart(equipped, itemId, isCash);
        if (result != null) {
            return result;
        }
        return getExclusiveClothesBodyPart(equipped, itemId, isCash);
    }

    private static BodyPart getExclusiveWeaponShieldBodyPart(Inventory equipped, int itemId, boolean isCash) {
        // CItemInfo::GetExclusiveWeaponShieldBodyPart
        final int offset = (isCash ? BodyPart.CASH_BASE.getValue() : 0);
        final Item weapon = equipped.getItem(BodyPart.WEAPON.getValue() + offset);
        final Item shield = equipped.getItem(BodyPart.SHIELD.getValue() + offset);
        if (itemId / 100000 == 14 && shield != null) {
            return BodyPart.SHIELD;
        }
        if (itemId / 10000 == 109 && weapon != null && weapon.getItemId() / 100000 == 14) {
            return BodyPart.WEAPON;
        }
        return null;
    }

    private static BodyPart getExclusiveClothesBodyPart(Inventory equipped, int itemId, boolean isCash) {
        // CItemInfo::GetExclusiveClothesBodyPart
        final int offset = (isCash ? BodyPart.CASH_BASE.getValue() : 0);
        final Item clothes = equipped.getItem(BodyPart.CLOTHES.getValue() + offset);
        final Item pants = equipped.getItem(BodyPart.PANTS.getValue() + offset);
        if (itemId / 10000 == 105 && pants != null) {
            return BodyPart.PANTS;
        }
        if (itemId / 10000 == 106 && clothes != null && clothes.getItemId() / 10000 == 105) {
            return BodyPart.CLOTHES;
        }
        return null;
    }
}
