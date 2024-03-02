package kinoko.packet.world.broadcast;

public enum BroadcastMessageType {
    // BM
    ALL(0),
    NOTICE(0),
    CLONE(1),
    ALERT(1),
    MAP(2),
    SPEAKER_CHANNEL(2),
    SPEAKER_WORLD(3),
    SLIDE(4),
    EVENT(5),
    NOTICE_WITHOUT_PREFIX(6),
    UTIL_DLG_EX(7),
    ITEM_SPEAKER(8),
    SPEAKER_BRIDGE(9),
    ART_SPEAKER_WORLD(10),
    BLOW_WEATHER(11),
    GACHAPON_ANNOUNCE(12),
    GACHAPON_ANNOUNCE_OPEN(13),
    GACHAPON_ANNOUNCE_COPY(14),
    ULIST_CLIP(15),
    FREE_MARKET_CLIP(16),
    DESTROY_SHOP(17),
    CASHSHOP_AD(18),
    HEART_SPEAKER(19),
    SKULL_SPEAKER(20);

    private final int value;

    BroadcastMessageType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
