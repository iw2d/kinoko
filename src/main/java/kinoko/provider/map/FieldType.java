package kinoko.provider.map;

public enum FieldType {
    DEFAULT(0),
    SNOWBALL(1),
    CONTI_MOVE(2),
    TOURNAMENT(3),
    COCONUT(4),
    OX_QUIZ(5),
    PERSONAL_TIME_LIMIT(6),
    WAITING_ROOM(7),
    GUILD_BOSS(8),
    LIMITED_VIEW(9),
    MONSTER_CARNIVAL(10),
    MONSTER_CARNIVAL_REVIVE(11),
    ZAKUM(12),
    ARIANT_ARENA(13),
    DOJANG(14),
    MONSTER_CARNIVAL_S2(15),
    MONSTER_CARNIVAL_WAITING_ROOM(16),
    COOKIE_HOUSE(17),
    BALROG(18),
    BATTLEFIELD(19),
    SPACE_GAGA(20),
    WITCH_TOWER(21),
    ARAN_TUTORIAL(22),
    MASSACRE(23),
    MASSACRE_RESULT(24),
    PARTY_RAID(25),
    PARTY_RAID_BOSS(26),
    PARTY_RAID_RESULT(27),
    NO_DRAGON(28),
    DYNAMIC_FOOTHOLD(29),
    ESCORT(30),
    ESCORT_RESULT(31),
    HUNTING_AD_BALLOON(32),
    CHAOS_ZAKUM(33),
    KILL_COUNT(34),
    CUBE(35),

    WEDDING(60),
    WEDDING_PHOTO(61),

    PROTECT_SNOWMAN(1000),
    SHOWA_BATH(1001),
    TUTORIAL(1002),
    PACHINKO(1003),
    BOAT_RACE(1004),
    BOAT_RACE_2(1005),
    LANGUAGE_SCHOOL(1006),
    FISHING(1007),
    FISHING_KING(1008),
    CN_WEDDING(1009),
    CN_PVP_SHOOTING_ONE_MOB(1010),
    CN_PVP_SHOOTING_MANY_MOB(1011),
    CN_PVP_JUMP(1012),
    HONTAIL(1013),
    NLCPQ(1014),
    TOKYO_BOSS(1015),
    TOKYO_BOSS_PARTY(1016),
    ENTRUSTED_FISHING(1017),
    PUNCH_PUNCH(1018);

    private final int value;

    FieldType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static FieldType getFromValue(int value) {
        for (FieldType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
