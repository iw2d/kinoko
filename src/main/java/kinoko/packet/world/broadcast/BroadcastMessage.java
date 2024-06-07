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
        outPacket.encodeByte(type.getValue());
        if (type == BroadcastMessageType.SLIDE) {
            outPacket.encodeByte(bool1);
        }

        outPacket.encodeString(string1); // message

        switch (type) {
            case SPEAKERWORLD, SKULLSPEAKER -> {
                outPacket.encodeByte(int1); // nChannelID
                outPacket.encodeByte(bool1); // bWhisperIcon
            }
            case ITEMSPEAKER -> {
                outPacket.encodeByte(int1); // nChannelID
                outPacket.encodeByte(bool1); // bWhisperIcon
                outPacket.encodeByte(item != null);
                if (item != null) {
                    item.encode(outPacket); // GW_ItemSlotBase::Decode
                }
            }
            case SPEAKERBRIDGE -> {
                outPacket.encodeByte(int1); // nChannelID
            }
            case ARTSPEAKERWORLD -> {
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
            case BLOWWEATHER -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case GACHAPONANNOUNCE -> {
                outPacket.encodeInt(int1); // nChannelID
                outPacket.encodeString(string2); // strFieldName
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
            case GACHAPONANNOUNCE_OPEN, GACHAPONANNOUNCE_COPY -> {
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
