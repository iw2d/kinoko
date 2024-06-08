package kinoko.packet.world;

public enum MessageType {
    // MS
    DropPickUp(0),
    QuestRecord(1),
    CashItemExpire(2),
    IncEXP(3),
    IncSP(4),
    IncPOP(5),
    IncMoney(6),
    IncGP(7),
    GiveBuff(8),
    GeneralItemExpire(9),
    System(10),
    QuestRecordEx(11),
    ItemProtectExpire(12),
    ItemExpireReplace(13),
    SkillExpire(14);

    private final byte value;

    MessageType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
