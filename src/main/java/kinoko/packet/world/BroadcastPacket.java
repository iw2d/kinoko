package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.item.Item;

import java.util.List;

public final class BroadcastPacket {
    // CWvsContext::OnBroadcastMsg -------------------------------------------------------------------------------------

    public static OutPacket alert(String message) {
        return BroadcastPacket.of(BroadcastType.ALERT, message);
    }

    public static OutPacket slide(String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.BroadcastMsg);
        outPacket.encodeByte(BroadcastType.SLIDE.getValue());
        outPacket.encodeByte(message != null && !message.isEmpty());
        if (message != null && !message.isEmpty()) {
            outPacket.encodeString(message); // pDlg
        }
        return outPacket;
    }

    public static OutPacket speakerWorld(String message, int channelId, boolean whisperIcon) {
        final OutPacket outPacket = BroadcastPacket.of(BroadcastType.SPEAKERWORLD, message);
        outPacket.encodeByte(channelId); // nChannelID
        outPacket.encodeByte(whisperIcon); // bWhisperIcon
        return outPacket;
    }

    public static OutPacket itemSpeaker(String message, Item item, int channelId, boolean whisperIcon) {
        final OutPacket outPacket = BroadcastPacket.of(BroadcastType.ITEMSPEAKER, message);
        outPacket.encodeByte(channelId); // nChannelID
        outPacket.encodeByte(whisperIcon); // bWhisperIcon
        outPacket.encodeByte(item != null);
        if (item != null) {
            item.encode(outPacket); // GW_ItemSlotBase::Decode
        }
        return outPacket;
    }

    public static OutPacket artSpeakerWorld(List<String> messages, int channelId, boolean whisperIcon) {
        final OutPacket outPacket = BroadcastPacket.of(BroadcastType.ARTSPEAKERWORLD, messages.get(0));
        final int lines = messages.size();
        outPacket.encodeByte(lines);
        if (lines > 1) {
            outPacket.encodeString(messages.get(1));
        }
        if (lines > 2) {
            outPacket.encodeString(messages.get(2));
        }
        outPacket.encodeByte(channelId); // nChannelID
        outPacket.encodeByte(whisperIcon); // bWhisperIcon
        return outPacket;
    }

    public static OutPacket blowWeather(String message, int itemId) {
        final OutPacket outPacket = BroadcastPacket.of(BroadcastType.BLOWWEATHER, message);
        outPacket.encodeInt(itemId); // nItemID
        return outPacket;
    }

    public static OutPacket gachaponAnnounce(String message, Item item, int channelId, String fieldName) {
        final OutPacket outPacket = BroadcastPacket.of(BroadcastType.GACHAPONANNOUNCE, message);
        outPacket.encodeByte(channelId); // nChannelID
        outPacket.encodeString(fieldName); // strFieldName
        item.encode(outPacket); // GW_ItemSlotBase::Decode
        return outPacket;
    }

    private static OutPacket of(BroadcastType broadcastType, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.BroadcastMsg);
        outPacket.encodeByte(broadcastType.getValue());
        outPacket.encodeString(message); // pDlg
        return outPacket;
    }
}
