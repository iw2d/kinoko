package kinoko.world.item;

import java.util.*;

public enum BodyPart {
    // BP
    EQUIPPED_BASE(0),

    HAIR(0),
    CAP(1),
    FACEACC(2),
    EYEACC(3),
    EARACC(4),
    CLOTHES(5), // top / overall
    PANTS(6),
    SHOES(7),
    GLOVES(8),
    CAPE(9),
    SHIELD(10),
    WEAPON(11),

    RING1(12),
    RING2(13),
    PETWEAR(14),
    RING3(15),
    RING4(16),
    PENDANT(17),
    TAMINGMOB(18),
    SADDLE(19),
    MOBEQUIP(20),

    PETRING_LABEL(21),
    PETABIL_ITEM(22),
    PETABIL_MESO(23),
    PETABIL_HPCONSUME(24),
    PETABIL_MPCONSUME(25),
    PETABIL_SWEEPFORDROP(26),
    PETABIL_LONGRANGE(27),
    PETABIL_PICKUPOTHERS(28),
    PETRING_QUOTE(29),

    PETWEAR2(30),
    PETRING_LABEL2(31),
    PETRING_QUOTE2(32),
    PETABIL_ITEM2(33),
    PETABIL_MESO2(34),
    PETABIL_SWEEPFORDROP2(35),
    PETABIL_LONGRANGE2(36),
    PETABIL_PICKUPOTHERS2(37),

    PETWEAR3(38),
    PETRING_LABEL3(39),
    PETRING_QUOTE3(40),
    PETABIL_ITEM3(41),
    PETABIL_MESO3(42),
    PETABIL_SWEEPFORDROP3(43),
    PETABIL_LONGRANGE3(44),
    PETABIL_PICKUPOTHERS3(45),

    PETABIL_IGNOREITEMS1(46),
    PETABIL_IGNOREITEMS2(47),
    PETABIL_IGNOREITEMS3(48),

    MEDAL(49),
    BELT(50),
    SHOULDER(51),
    EXT_PENDANT1(59),
    EQUIPPED_END(60),

    CASH_BASE(100), // -100 when encoding
    CASH_WEAPON(111), // STICKER
    CASH_END(160),

    // DP
    DRAGON_BASE(1000),
    DRAGON_MASK(1000),
    DRAGON_PENDANT(1001),
    DRAGON_WING(1002),
    DRAGON_TAIL(1003),
    DRAGON_END(1100),

    // MP
    MECHANIC_BASE(1100),
    MECHANIC_ENGINE(1100),
    MECHANIC_ARM(1101),
    MECHANIC_LEG(1102),
    MECHANIC_FRAME(1103),
    MECHANIC_TRANSISTOR(1104),
    MECHANIC_END(1200),

    // AP
    ANDROID_BASE(1200),
    ANDROID_CAP(1200),
    ANDROID_CAPE(1201),
    ANDROID_FACEACC(1202),
    ANDROID_CLOTHES(1203),
    ANDROID_PANTS(1204),
    ANDROID_SHOES(1205),
    ANDROID_GLOVES(1206),
    ANDROID_END(1207);

    private static final Map<Integer, BodyPart> bodyParts;
    private static final Set<BodyPart> armorBodyParts = Set.of(CAP, CLOTHES, PANTS, SHOES, GLOVES, CAPE);
    private static final Set<BodyPart> accessoryBodyParts = Set.of(FACEACC, EYEACC, EARACC, RING1, RING2, RING3, RING4, PENDANT, EXT_PENDANT1);
    private static final Map<BodyPart, List<BodyPart>> petBodyParts = Map.of(
            PETWEAR, List.of(PETWEAR, PETWEAR2, PETWEAR3),
            PETRING_LABEL, List.of(PETRING_LABEL, PETRING_LABEL2, PETRING_LABEL3),
            PETRING_QUOTE, List.of(PETRING_QUOTE, PETRING_QUOTE2, PETRING_QUOTE3),
            PETABIL_ITEM, List.of(PETABIL_ITEM, PETABIL_ITEM2, PETABIL_ITEM3),
            PETABIL_MESO, List.of(PETABIL_MESO, PETABIL_MESO2, PETABIL_MESO3),
            PETABIL_SWEEPFORDROP, List.of(PETABIL_SWEEPFORDROP, PETABIL_SWEEPFORDROP2, PETABIL_SWEEPFORDROP3),
            PETABIL_LONGRANGE, List.of(PETABIL_LONGRANGE, PETABIL_LONGRANGE2, PETABIL_LONGRANGE3),
            PETABIL_PICKUPOTHERS, List.of(PETABIL_PICKUPOTHERS, PETABIL_PICKUPOTHERS2, PETABIL_PICKUPOTHERS3),
            PETABIL_IGNOREITEMS1, List.of(PETABIL_IGNOREITEMS1, PETABIL_IGNOREITEMS2, PETABIL_IGNOREITEMS3)
    );

    static {
        final Map<Integer, BodyPart> bodyPartMap = new HashMap<>();
        for (BodyPart bodyPart : values()) {
            switch (bodyPart) {
                case EQUIPPED_BASE:
                case EQUIPPED_END:
                case CASH_BASE:
                case CASH_END:
                case DRAGON_BASE:
                case DRAGON_END:
                case MECHANIC_BASE:
                case MECHANIC_END:
                case ANDROID_BASE:
                case ANDROID_END:
                    continue;
            }
            bodyPartMap.put(bodyPart.getValue(), bodyPart);
        }
        bodyParts = Collections.unmodifiableMap(bodyPartMap);
    }

    private final int value;

    BodyPart(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public final boolean isArmor() {
        return armorBodyParts.contains(this);
    }

    public final boolean isAccessory() {
        return accessoryBodyParts.contains(this);
    }

    public final boolean isDragon() {
        return getValue() >= DRAGON_BASE.getValue() && getValue() < DRAGON_END.getValue();
    }

    public final boolean isMechanic() {
        return getValue() >= MECHANIC_BASE.getValue() && getValue() < MECHANIC_END.getValue();
    }

    public static BodyPart getByValue(int value) {
        return bodyParts.get(value);
    }

    public static BodyPart getByPetIndex(BodyPart bodyPart, int petIndex) {
        if (!petBodyParts.containsKey(bodyPart) || petIndex < 0 || petIndex >= 3) {
            return bodyPart;
        }
        return petBodyParts.get(bodyPart).get(petIndex);
    }

    public static Set<BodyPart> getByItemId(int itemId) {
        // get_bodypart_from_item
        switch (itemId / 10000) {
            case 100 -> {
                return Set.of(CAP);
            }
            case 101 -> {
                return Set.of(FACEACC);
            }
            case 102 -> {
                return Set.of(EYEACC);
            }
            case 103 -> {
                return Set.of(EARACC);
            }
            case 104, 105 -> {
                return Set.of(CLOTHES);
            }
            case 106 -> {
                return Set.of(PANTS);
            }
            case 107 -> {
                return Set.of(SHOES);
            }
            case 108 -> {
                return Set.of(GLOVES);
            }
            case 109, 119, 134 -> {
                return Set.of(SHIELD);
            }
            case 110 -> {
                return Set.of(CAPE);
            }
            case 111 -> {
                return Set.of(RING1, RING2, RING3, RING4);
            }
            case 112 -> {
                return Set.of(PENDANT, EXT_PENDANT1);
            }
            case 113 -> {
                return Set.of(BELT);
            }
            case 114 -> {
                return Set.of(MEDAL);
            }
            case 115 -> {
                return Set.of(SHOULDER);
            }
            case 161 -> {
                return Set.of(MECHANIC_ENGINE);
            }
            case 162 -> {
                return Set.of(MECHANIC_ARM);
            }
            case 163 -> {
                return Set.of(MECHANIC_LEG);
            }
            case 164 -> {
                return Set.of(MECHANIC_FRAME);
            }
            case 165 -> {
                return Set.of(MECHANIC_TRANSISTOR);
            }
            case 180 -> {
                if (itemId == 1802100) {
                    return Set.of(PETRING_LABEL, PETRING_LABEL2, PETRING_LABEL3);
                } else {
                    return Set.of(PETWEAR, PETWEAR2, PETWEAR3);
                }
            }
            case 181 -> {
                switch (itemId) {
                    case 1812000 -> {
                        return Set.of(PETABIL_MESO, PETABIL_MESO2, PETABIL_MESO3);
                    }
                    case 1812001 -> {
                        return Set.of(PETABIL_ITEM, PETABIL_ITEM2, PETABIL_ITEM3);
                    }
                    case 1812002 -> {
                        return Set.of(PETABIL_HPCONSUME);
                    }
                    case 1812003 -> {
                        return Set.of(PETABIL_MPCONSUME);
                    }
                    case 1812004 -> {
                        return Set.of(PETABIL_SWEEPFORDROP, PETABIL_SWEEPFORDROP2, PETABIL_SWEEPFORDROP3);
                    }
                    case 1812005 -> {
                        return Set.of(PETABIL_LONGRANGE, PETABIL_LONGRANGE2, PETABIL_LONGRANGE3);
                    }
                    case 1812006 -> {
                        return Set.of(PETABIL_PICKUPOTHERS, PETABIL_PICKUPOTHERS2, PETABIL_PICKUPOTHERS3);
                    }
                    case 1812007 -> {
                        return Set.of(PETABIL_IGNOREITEMS1, PETABIL_IGNOREITEMS2, PETABIL_IGNOREITEMS3);
                    }
                    default -> {
                        return Set.of(PETRING_LABEL, PETRING_LABEL2, PETRING_LABEL3);
                    }
                }
            }
            case 182 -> {
                return Set.of(PETRING_LABEL, PETRING_LABEL2, PETRING_LABEL3);
            }
            case 183 -> {
                return Set.of(PETRING_QUOTE, PETRING_QUOTE2, PETRING_QUOTE3);
            }
            case 190 -> {
                return Set.of(TAMINGMOB);
            }
            case 191 -> {
                return Set.of(SADDLE);
            }
            case 192 -> {
                return Set.of(MOBEQUIP);
            }
            case 194 -> {
                return Set.of(DRAGON_MASK);
            }
            case 195 -> {
                return Set.of(DRAGON_PENDANT);
            }
            case 196 -> {
                return Set.of(DRAGON_WING);
            }
            case 197 -> {
                return Set.of(DRAGON_TAIL);
            }
            default -> {
                if (!ItemConstants.isWeapon(itemId)) {
                    return Set.of();
                }
                return Set.of(WEAPON);
            }
        }
    }
}
