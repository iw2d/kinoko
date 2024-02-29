package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public class GiftList implements Encodable {
    private final long itemSn;
    private final int itemId;
    private final String name;
    private final String text;

    public GiftList(long itemSn, int itemId, String name, String text) {
        this.itemSn = itemSn;
        this.itemId = itemId;
        this.name = name;
        this.text = text;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_GiftList struct (98)
        outPacket.encodeLong(itemSn); // liSN
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeString(name, 13); // sBuyCharacterName
        outPacket.encodeString(text, 73); // sText
    }
}
