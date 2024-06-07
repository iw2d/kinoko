package kinoko.world.item;

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
    MECHANIC_END(1200);

    private final int value;

    BodyPart(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static BodyPart getByValue(int value) {
        for (BodyPart bodyPart : values()) {
            if (bodyPart.getValue() == value) {
                return bodyPart;
            }
        }
        return null;
    }
}
