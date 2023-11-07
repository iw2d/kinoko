package kinoko.world.item;

import kinoko.world.user.BodyPart;

public final class ItemConstants {

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

    public static boolean isCorrectBodyPart(int itemId, BodyPart bodyPart, int gender) {
        final int genderFromId = getGenderFromId(itemId);
        if (gender != 2 && genderFromId != 2 && genderFromId != gender) {
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
                final int prefix = itemId / 100000;
                if (prefix != 13 && prefix != 14 && prefix != 16 && prefix != 17) {
                    return false;
                }
                return bodyPart == BodyPart.WEAPON;
            }
        }
    }
}
