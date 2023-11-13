package kinoko.packet.world.message;

public enum MessageType {
    DROP_PICK_UP(0),
    QUEST_RECORD(1),
    CASH_ITEM_EXPIRE(2),
    INC_EXP(3),
    INC_SP(4),
    INC_POP(5),
    INC_MONEY(6),
    INC_GP(7),
    GIVE_BUFF(8),
    GENERAL_ITEM_EXPIRE(9),
    SYSTEM(10),
    QUEST_RECORD_EX(11),
    ITEM_PROTECT_EXPIRE(12),
    ITEM_EXPIRE_REPLACE(13),
    SKILL_EXPIRE(14);

    private final byte value;

    MessageType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
