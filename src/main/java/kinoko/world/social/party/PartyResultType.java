package kinoko.world.social.party;

public enum PartyResultType {
    // PartyReq
    INVITE_PARTY(4),

    // PartyRes
    LOAD_PARTY_DONE(7),
    CREATE_NEW_PARTY_DONE(8),
    CREATE_NEW_PARTY_ALREADY_JOINED(9),
    CREATE_NEW_PARTY_BEGINNER(10),
    CREATE_NEW_PARTY_UNKNOWN(11),
    WITHDRAW_PARTY_DONE(12),
    WITHDRAW_PARTY_NOT_JOINED(13),
    WITHDRAW_PARTY_UNKNOWN(14),
    JOIN_PARTY_DONE(15),
    JOIN_PARTY_DONE_2(16),
    JOIN_PARTY_ALREADY_JOINED(17),
    JOIN_PARTY_ALREADY_FULL(18),
    JOIN_PARTY_OVER_DESIRED_SIZE(19),
    JOIN_PARTY_UNKNOWN_USER(20),
    JOIN_PARTY_UNKNOWN(21),
    INVITE_PARTY_SENT(22),
    INVITE_PARTY_BLOCKED_USER(23),
    INVITE_PARTY_ALREADY_INVITED(24),
    INVITE_PARTY_ALREADY_INVITED_BY_INVITER(25),
    INVITE_PARTY_REJECTED(26),
    INVITE_PARTY_ACCEPTED(27),
    KICK_PARTY_DONE(28),
    KICK_PARTY_FIELD_LIMIT(29),
    KICK_PARTY_UNKNOWN(30),
    CHANGE_PARTY_BOSS_DONE(31),
    CHANGE_PARTY_BOSS_NOT_SAME_FIELD(32),
    CHANGE_PARTY_BOSS_NO_MEMBER_IN_SAME_FIELD(33),
    CHANGE_PARTY_BOSS_NOT_SAME_CHANNEL(34),
    CHANGE_PARTY_BOSS_UNKNOWN(35),
    ADMIN_CANNOT_CREATE(36),
    ADMIN_CANNOT_INVITE(37),
    USER_MIGRATION(38),
    CHANGE_LEVEL_OR_JOB(39),
    SUCCESS_TO_SELECT_PQ_REWARD(40),
    FAIL_TO_SELECT_PQ_REWARD(41),
    RECEIVE_PQ_REWARD(42),
    FAIL_TO_REQUEST_PQ_REWARD(43),
    CAN_NOT_IN_THIS_FIELD(44),
    SERVER_MSG(45),

    // PartyInfo
    TOWN_PORTAL_CHANGED(46),
    OPEN_GATE(47),

    // AdverNoti
    ADVER_LOAD_DONE(74),
    ADVER_CHANGE(75),
    ADVER_REMOVE(76),
    ADVER_GET_ALL(77),
    ADVER_APPLY(78),
    ADVER_RESULT_APPLY(79),
    ADVER_ADD_FAIL(80);

    private final int value;

    PartyResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static PartyResultType getByValue(int value) {
        for (PartyResultType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
