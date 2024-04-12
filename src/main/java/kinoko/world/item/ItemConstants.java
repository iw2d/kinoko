package kinoko.world.item;

public final class ItemConstants {
    public static final int WHITE_SCROLL = 2340000;

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
        if (isAccUpgradeItem(upgradeItemType)) {
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

    public static boolean isPortableChairItem(int itemId) {
        return itemId / 10000 == 301;
    }

    public static boolean isCashEffectItem(int itemId) {
        return itemId / 10000 == 501;
    }

    public static boolean isNonCashEffectItem(int itemId) {
        return itemId / 10000 == 429;
    }

    public static boolean isMatchedItemIdGender(int itemId, int gender) {
        final int genderFromId = getGenderFromId(itemId);
        return gender == 2 || genderFromId == 2 || gender == genderFromId;
    }

    public static boolean isCorrectBodyPart(int itemId, BodyPart bodyPart, int gender) {
        if (!isMatchedItemIdGender(itemId, gender)) {
            return false;
        }
        switch (itemId / 10000) {
            case 100 -> {
                return bodyPart == BodyPart.CAP;
            }
            case 101 -> {
                return bodyPart == BodyPart.FACE_ACCESSORY;
            }
            case 102 -> {
                return bodyPart == BodyPart.EYE_ACCESSORY;
            }
            case 103 -> {
                return bodyPart == BodyPart.EAR_ACCESSORY;
            }
            case 104, 105 -> {
                return bodyPart == BodyPart.COAT || bodyPart == BodyPart.LONGCOAT;
            }
            case 106 -> {
                return bodyPart == BodyPart.PANTS;
            }
            case 107 -> {
                return bodyPart == BodyPart.SHOES;
            }
            case 108 -> {
                return bodyPart == BodyPart.GLOVE;
            }
            case 109, 119, 134 -> {
                return bodyPart == BodyPart.SHIELD;
            }
            case 110 -> {
                return bodyPart == BodyPart.CAPE;
            }
            case 111 -> {
                return bodyPart == BodyPart.RING_1 ||
                        bodyPart == BodyPart.RING_2 ||
                        bodyPart == BodyPart.RING_3 ||
                        bodyPart == BodyPart.RING_4;
            }
            case 112 -> {
                return bodyPart == BodyPart.PENDANT ||
                        bodyPart == BodyPart.PENDANT_EXT;
            }
            case 113 -> {
                return bodyPart == BodyPart.BELT;
            }
            case 114 -> {
                return bodyPart == BodyPart.MEDAL;
            }
            case 115 -> {
                return bodyPart == BodyPart.SHOULDER;
            }
            case 161 -> {
                return bodyPart == BodyPart.MECHANIC_ENGINE;
            }
            case 162 -> {
                return bodyPart == BodyPart.MECHANIC_ARM;
            }
            case 163 -> {
                return bodyPart == BodyPart.MECHANIC_LEG;
            }
            case 164 -> {
                return bodyPart == BodyPart.MECHANIC_FRAME;
            }
            case 165 -> {
                return bodyPart == BodyPart.MECHANIC_TRANSISTOR;
            }
            case 180 -> {
                if (itemId == 1802100) {
                    return bodyPart == BodyPart.PET_LABEL_RING_1 ||
                            bodyPart == BodyPart.PET_LABEL_RING_2 ||
                            bodyPart == BodyPart.PET_LABEL_RING_3;
                }
                return bodyPart == BodyPart.PET_EQUIP_1 ||
                        bodyPart == BodyPart.PET_EQUIP_2 ||
                        bodyPart == BodyPart.PET_EQUIP_3;
            }
            case 181 -> {
                switch (itemId) {
                    case 1812000 -> {
                        return bodyPart == BodyPart.PET_MESO_MAGNET_1 ||
                                bodyPart == BodyPart.PET_MESO_MAGNET_2 ||
                                bodyPart == BodyPart.PET_MESO_MAGNET_3;
                    }
                    case 1812001 -> {
                        return bodyPart == BodyPart.PET_ITEM_POUCH_1 ||
                                bodyPart == BodyPart.PET_ITEM_POUCH_2 ||
                                bodyPart == BodyPart.PET_ITEM_POUCH_3;
                    }
                    case 1812002 -> {
                        return bodyPart == BodyPart.PET_AUTO_HP_POUCH;
                    }
                    case 1812003 -> {
                        return bodyPart == BodyPart.PET_AUTO_MP_POUCH;
                    }
                    case 1812004 -> {
                        return bodyPart == BodyPart.PET_WING_BOOTS_1 ||
                                bodyPart == BodyPart.PET_WING_BOOTS_2 ||
                                bodyPart == BodyPart.PET_WING_BOOTS_3;
                    }
                    case 1812005 -> {
                        return bodyPart == BodyPart.PET_BINOCULAR_1 ||
                                bodyPart == BodyPart.PET_BINOCULAR_2 ||
                                bodyPart == BodyPart.PET_BINOCULAR_3;
                    }
                    case 1812006 -> {
                        return bodyPart == BodyPart.PET_MAGIC_SCALES_1 ||
                                bodyPart == BodyPart.PET_MAGIC_SCALES_2 ||
                                bodyPart == BodyPart.PET_MAGIC_SCALES_3;
                    }
                    case 1812007 -> {
                        return bodyPart == BodyPart.PET_ITEM_IGNORE_1 ||
                                bodyPart == BodyPart.PET_ITEM_IGNORE_2 ||
                                bodyPart == BodyPart.PET_ITEM_IGNORE_3;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case 182 -> {
                return bodyPart == BodyPart.PET_LABEL_RING_1 ||
                        bodyPart == BodyPart.PET_LABEL_RING_2 ||
                        bodyPart == BodyPart.PET_LABEL_RING_3;
            }
            case 183 -> {
                return bodyPart == BodyPart.PET_QUOTE_RING_1 ||
                        bodyPart == BodyPart.PET_QUOTE_RING_2 ||
                        bodyPart == BodyPart.PET_QUOTE_RING_3;
            }
            case 190 -> {
                return bodyPart == BodyPart.TAMING_MOB;
            }
            case 191 -> {
                return bodyPart == BodyPart.SADDLE;
            }
            case 192 -> {
                return bodyPart == BodyPart.MOB_EQUIP;
            }
            case 194 -> {
                return bodyPart == BodyPart.DRAGON_MASK;
            }
            case 195 -> {
                return bodyPart == BodyPart.DRAGON_PENDANT;
            }
            case 196 -> {
                return bodyPart == BodyPart.DRAGON_WING;
            }
            case 197 -> {
                return bodyPart == BodyPart.DRAGON_TAIL;
            }
            default -> {
                if (!isWeapon(itemId)) {
                    return false;
                }
                return bodyPart == BodyPart.WEAPON;
            }
        }
    }
}
