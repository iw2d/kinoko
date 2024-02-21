package kinoko.packet.user;

public enum ChatType {
    NORMAL(0),
    WHISPER(1),
    GROUP_PARTY(2),
    GROUP_FRIEND(3),
    GROUP_GUILD(4),
    GROUP_ALLIANCE(5),
    COUPLE(6),
    GAME_DESC(7),
    TIP(8),
    NOTICE(9),
    NOTICE_2(10),
    ADMIN_CHAT(11),
    SYSTEM(12),
    SPEAKER_CHANNEL(13),
    SPEAKER_WORLD(14),
    SKULL_SPEAKER(15),
    ITEM_SPEAKER(16),
    ITEM_SPEAKER_ITEM(17),
    AVATAR_MEGAPHONE(18),
    GACHAPON_ANNOUNCE(19),
    CASH_GACHAPON_ANNOUNCE(20),
    CASH_GACHAPON_OPEN_ANNOUNCE(21),
    CASH_GACHAPON_COPY_ANNOUNCE(22),
    SPEAKER_BRIDGE(23),
    SPEAKER_WORLD_EX_PREVIEW(24),
    MOB(25),
    EXPEDITION(26),
    NO(27);

    private final int value;

    ChatType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
