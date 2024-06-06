package kinoko.world.dialog;

public enum UIType {
    ITEM(0),
    EQUIP(1),
    STAT(2),
    SKILL(3),
    MINIMAP(4),
    KEY_CONFIG(5),
    QUEST_INFO(6),
    USER_LIST(7),
    MESSENGER(8),
    MONSTER_BOOK(9),
    USER_INFO(10),
    SHORTCUT(11),
    MENU(12),
    QUEST_ALARM(13),
    PARTY_HP(14),
    QUEST_TIMER(15),
    QUEST_TIMER_ACTION(16),
    MONSTER_CARNIVAL(17),
    ITEM_SEARCH(18),
    ENERGY_BAR(19),
    GUILD_BOARD(20),
    PARTY_SEARCH(21),
    ITEM_MAKE(22),
    CONSULT(23),
    CLASS_COMPETITION(24),
    RANKING(25),
    FAMILY(26),
    FAMILY_CHART(27),
    OPERATOR_BOARD(28),
    OPERATOR_BOARD_STATE(29),
    MEDAL_QUEST_INFO(30),
    WEB_EVENT(31),
    SKILL_EX(32),
    REPAIR_DURABILITY(33),
    CHAT_WND(34),
    BATTLE_RECORD(35),
    GUILD_MAKE_MARK(36),
    GUILD_MAKE(37),
    GUILD_RANK(38),
    GUILD_BBS(39),
    ACCOUNT_MORE_INFO(40),
    FIND_FRIEND(41),
    DRAGON_BOX(42),
    WND_NO(43),
    UNRELEASE(44);

    private final int value;

    UIType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
