package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class Gift implements Encodable {
    private final long giftSn;
    private final int itemId;
    private final int commodityId;
    private final int senderId;
    private final String senderName;
    private final String senderMessage;
    private final long pairItemSn;

    public Gift(long giftSn, int itemId, int commodityId, int senderId, String senderName, String senderMessage, long pairItemSn) {
        this.giftSn = giftSn;
        this.itemId = itemId;
        this.senderId = senderId;
        this.commodityId = commodityId;
        this.senderName = senderName;
        this.senderMessage = senderMessage;
        this.pairItemSn = pairItemSn;
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

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public long getPairItemSn() {
        return pairItemSn;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_GiftList struct (98)
        outPacket.encodeLong(getGiftSn()); // liSN
        outPacket.encodeInt(getItemId()); // nItemID
        outPacket.encodeString(senderName, 13); // sBuyCharacterName
        outPacket.encodeString(senderMessage, 73); // sText
    }
}
