package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Item;

public class Gift implements Encodable {
    private final Item item;
    private final String sender;
    private final String message;

    public Gift(Item item, String sender, String message) {
        this.item = item;
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_GiftList struct (98)
        outPacket.encodeLong(item.getItemSn()); // liSN
        outPacket.encodeInt(item.getItemId()); // nItemID
        outPacket.encodeString(sender, 13); // sBuyCharacterName
        outPacket.encodeString(message, 73); // sText
    }
}
