package kinoko.packet.world.broadcast;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Item;

public final class BroadcastMessage implements Encodable {
    private final BroadcastMessageType type;
    private String string1;
    private String string2;
    private String string3;
    private int int1;
    private int int2;
    private boolean bool1;

    private Item item;

    public BroadcastMessage(BroadcastMessageType type) {
        this.type = type;
    }


    @Override
    public void encode(OutPacket outPacket) {
        if (type == BroadcastMessageType.SLIDE) {
            outPacket.encodeByte(bool1);
            if (!bool1) {
                return;
            }
        }

        outPacket.encodeString(string1); // message

        switch (type) {
            case SPEAKER_WORLD, SKULL_SPEAKER -> {
                outPacket.encodeByte(int1); // nChannelID
                outPacket.encodeByte(bool1); // bWhisperIcon
            }
            case ITEM_SPEAKER -> {
                outPacket.encodeByte(int1); // nChannelID
                outPacket.encodeByte(bool1); // bWhisperIcon
                outPacket.encodeByte(item != null);
                if (item != null) {
                    item.encode(outPacket); // GW_ItemSlotBase::Decode
                }
            }
            case SPEAKER_BRIDGE -> {
                outPacket.encodeByte(int1); // nChannelID
            }
            case ART_SPEAKER_WORLD -> {
                outPacket.encodeByte(int2); // length
                if (int2 > 1) {
                    outPacket.encodeString(string2);
                }
                if (int2 > 2) {
                    outPacket.encodeString(string3);
                }
                outPacket.encodeByte(int1); // nChannelID
                outPacket.encodeByte(bool1); // bWhisperIcon
            }
            case BLOW_WEATHER -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case GACHAPON_ANNOUNCE -> {
                outPacket.encodeInt(int1); // nChannelID
                outPacket.encodeString(string2); // strFieldName
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
            case GACHAPON_ANNOUNCE_OPEN, GACHAPON_ANNOUNCE_COPY -> {
                outPacket.encodeString(string2); // strCharacterName
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
        }
    }

    public static BroadcastMessage alert(String text) {
        final BroadcastMessage message = new BroadcastMessage(BroadcastMessageType.ALERT);
        message.string1 = text;
        return message;
    }
}
