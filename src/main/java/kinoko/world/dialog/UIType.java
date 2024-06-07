package kinoko.world.dialog;

public enum UIType {
    // UI
    ITEM(0),
    EQUIP(1),
    STAT(2),
    SKILL(3),
    MINIMAP(4),
    KEYCONFIG(5),
    QUESTINFO(6),
    USERLIST(7),
    MESSENGER(8),
    MONSTERBOOK(9),
    USERINFO(10),
    SHORTCUT(11),
    MENU(12),
    QUESTALARM(13),
    PARTYHP(14),
    QUESTTIMER(15),
    QUESTTIMERACTION(16),
    MONSTERCARNIVAL(17),
    ITEMSEARCH(18),
    ENERGYBAR(19),
    GUILDBOARD(20),
    PARTYSEARCH(21),
    ITEMMAKE(22),
    CONSULT(23),
    CLASSCOMPETITION(24),
    RANKING(25),
    FAMILY(26),
    FAMILYCHART(27),
    OPERATORBOARD(28),
    OPERATORBOARDSTATE(29),
    MEDALQUESTINFO(30),
    WEBEVENT(31),
    SKILLEX(32),
    REPAIRDURABILITY(33),
    CHATWND(34),
    BATTLERECORD(35),
    GUILDMAKEMARK(36),
    GUILDMAKE(37),
    GUILDRANK(38),
    GUILDBBS(39),
    ACCOUNTMOREINFO(40),
    FINDFRIEND(41),
    DRAGONBOX(42),
    WNDNO(43),
    UNRELEASE(44);

    private final int value;

    UIType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
