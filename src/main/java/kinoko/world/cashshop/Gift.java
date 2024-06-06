package kinoko.world.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class Gift implements Encodable {
    private final long giftSn;
    private final int itemId;
    private final int commodityId;
    private final String sender;
    private final String message;

    public Gift(long giftSn, int itemId, int commodityId, String sender, String message) {
        this.giftSn = giftSn;
        this.itemId = itemId;
        this.commodityId = commodityId;
        this.sender = sender;
        this.message = message;
    }

    public long getGiftSn() {
        return giftSn;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_GiftList struct (98)
        outPacket.encodeLong(getGiftSn()); // liSN
        outPacket.encodeInt(getItemId()); // nItemID
        outPacket.encodeString(sender, 13); // sBuyCharacterName
        outPacket.encodeString(message, 73); // sText
    }
}
