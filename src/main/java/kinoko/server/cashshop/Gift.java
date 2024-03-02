package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Item;

public final class Gift implements Encodable {
    private final Item item;
    private final String sender;
    private final String message;

    public Gift(Item item, String sender, String message) {
        this.item = item;
        this.sender = sender;
        this.message = message;
    }

    public Item getItem() {
        return item;
    }

    public long getItemSn() {
        return item.getItemSn();
    }

    public int getItemId() {
        return item.getItemId();
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
        outPacket.encodeLong(getItemSn()); // liSN
        outPacket.encodeInt(getItemId()); // nItemID
        outPacket.encodeString(sender, 13); // sBuyCharacterName
        outPacket.encodeString(message, 73); // sText
    }
}
