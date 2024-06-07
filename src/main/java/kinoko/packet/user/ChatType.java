package kinoko.packet.user;

public enum ChatType {
    // CHAT_TYPE
    NORMAL(0),
    WHISPER(1),
    GROUPPARTY(2),
    GROUPFRIEND(3),
    GROUPGUILD(4),
    GROUPALLIANCE(5),
    COUPLE(6),
    GAMEDESC(7),
    TIP(8),
    NOTICE(9),
    NOTICE2(10),
    ADMIN_CHAT(11),
    SYSTEM(12),
    SPEAKERCHANNEL(13),
    SPEAKERWORLD(14),
    SKULLSPEAKER(15),
    ITEMSPEAKER(16),
    ITEMSPEAKER_ITEM(17),
    AVATARMEGAPHONE(18),
    GACHAPONANNOUNCE(19),
    CASHGACHAPONANNOUNCE(20),
    CASHGACHAPON_OPEN_ANNOUNCE(21),
    CASHGACHAPON_COPY_ANNOUNCE(22),
    SPEAKERBRIDGE(23),
    SPEAKERWORLDEX_PREVIEW(24),
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
