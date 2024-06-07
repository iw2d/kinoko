package kinoko.packet.world.broadcast;

public enum BroadcastMessageType {
    // BM
    ALL(0),
    NOTICE(0),
    CLONE(1),
    ALERT(1),
    MAP(2),
    SPEAKERCHANNEL(2),
    SPEAKERWORLD(3),
    SLIDE(4),
    EVENT(5),
    NOTICEWITHOUTPREFIX(6),
    UTILDLGEX(7),
    ITEMSPEAKER(8),
    SPEAKERBRIDGE(9),
    ARTSPEAKERWORLD(10),
    BLOWWEATHER(11),
    GACHAPONANNOUNCE(12),
    GACHAPONANNOUNCE_OPEN(13),
    GACHAPONANNOUNCE_COPY(14),
    ULISTCLIP(15),
    FREEMARKETCLIP(16),
    DESTROYSHOP(17),
    CASHSHOPAD(18),
    HEARTSPEAKER(19),
    SKULLSPEAKER(20);

    private final int value;

    BroadcastMessageType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
