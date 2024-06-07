package kinoko.provider.map;

public enum FieldType {
    // FIELDTYPE
    DEFAULT(0),
    SNOWBALL(1),
    CONTIMOVE(2),
    TOURNAMENT(3),
    COCONUT(4),
    OXQUIZ(5),
    PERSONALTIMELIMIT(6),
    WAITINGROOM(7),
    GUILDBOSS(8),
    LIMITEDVIEW(9),
    MONSTERCARNIVAL(10),
    MONSTERCARNIVALREVIVE(11),
    ZAKUM(12),
    ARIANTARENA(13),
    DOJANG(14),
    MONSTERCARNIVAL_S2(15),
    MONSTERCARNIVALWAITINGROOM(16),
    COOKIEHOUSE(17),
    BALROG(18),
    BATTLEFIELD(19),
    SPACEGAGA(20),
    WITCHTOWER(21),
    ARANTUTORIAL(22),
    MASSACRE(23),
    MASSACRE_RESULT(24),
    PARTYRAID(25),
    PARTYRAIDBOSS(26),
    PARTYRAIDRESULT(27),
    NODRAGON(28),
    DYNAMICFOOTHOLD(29),
    ESCORT(30),
    ESCORT_RESULT(31),
    HUNTINGADBALLOON(32),
    CHAOSZAKUM(33),
    KILLCOUNT(34),
    CUBE(35),

    WEDDING(60),
    WEDDINGPHOTO(61),

    PROTECTSNOWMAN(1000),
    SHOWABATH(1001),
    TUTORIAL(1002),
    PACHINKO(1003),
    BOATRACE(1004),
    BOATRACE2(1005),
    LANGUAGESCHOOL(1006),
    FISHING(1007),
    FISHINGKING(1008),
    CNWEDDING(1009),
    CNPVPSHOOTINGONEMOB(1010),
    CNPVPSHOOTINGMANYMOB(1011),
    CNPVPJUMP(1012),
    HONTAIL(1013),
    NLCPQ(1014),
    TOKYOBOSS(1015),
    TOKYOBOSSPARTY(1016),
    ENTRUSTEDFISHING(1017),
    PUNCHPUNCH(1018);

    private final int value;

    FieldType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static FieldType getByValue(int value) {
        for (FieldType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
