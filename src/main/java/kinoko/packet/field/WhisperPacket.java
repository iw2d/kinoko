package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.social.whisper.WhisperFlag;

public final class WhisperPacket {
    // CField::OnWhisper -----------------------------------------------------------------------------------------------

    public static OutPacket whisperBlocked(String targetCharacterName) {
        final OutPacket outPacket = WhisperPacket.of(WhisperFlag.WhisperBlocked);
        outPacket.encodeString(targetCharacterName);
        outPacket.encodeByte(false); // bool1 ? "Unable to find '%s'" : "'%s' have currently disabled whispers."
        return outPacket;
    }

    public static OutPacket whisperResult(String targetCharacterName, boolean success) {
        final OutPacket outPacket = WhisperPacket.of(WhisperFlag.WhisperResult);
        outPacket.encodeString(targetCharacterName); // sReceiver
        outPacket.encodeByte(success); // success
        return outPacket;
    }

    public static OutPacket whisperReceive(int sourceChannelId, String sourceCharacterName, String message) {
        final OutPacket outPacket = WhisperPacket.of(WhisperFlag.WhisperReceive);
        outPacket.encodeString(sourceCharacterName);
        outPacket.encodeByte(sourceChannelId); // nChannelID
        outPacket.encodeByte(false); // bFromAdmin
        outPacket.encodeString(message);
        return outPacket;
    }

    public static OutPacket whisperReceiveManager(String sourceCharacterName, String message) {
        final OutPacket outPacket = WhisperPacket.of(WhisperFlag.WhisperReceiveManager);
        // CField::BlowWeather (item id : 5120025)
        outPacket.encodeString(sourceCharacterName);
        outPacket.encodeByte(0); // unused
        outPacket.encodeString(message);
        return outPacket;
    }

    public static OutPacket locationResultNone(String targetName) {
        return locationResult(LocationResultType.None, targetName, false, 0);
    }

    public static OutPacket locationResultSameChannel(String targetName, boolean isFriend, int fieldId) {
        return locationResult(LocationResultType.GameSvr, targetName, isFriend, fieldId);
    }

    public static OutPacket locationResultCashShop(String targetName, boolean isFriend) {
        return locationResult(LocationResultType.ShopSvr, targetName, isFriend, 0);
    }

    public static OutPacket locationResultOtherChannel(String targetName, boolean isFriend, int channelId) {
        return locationResult(LocationResultType.OtherChannel, targetName, isFriend, channelId);
    }

    public static OutPacket locationResultAdmin(String targetName) {
        return locationResult(LocationResultType.Admin, targetName, false, 0);
    }

    private static OutPacket locationResult(LocationResultType resultType, String targetName, boolean isFriend, int locationId) {
        final OutPacket outPacket = WhisperPacket.of(isFriend ? WhisperFlag.LocationResult_F : WhisperFlag.LocationResult);
        outPacket.encodeString(targetName); // sFind
        outPacket.encodeByte(resultType.getValue());
        outPacket.encodeInt(locationId); // fieldId | channelId
        if (isFriend && resultType == LocationResultType.GameSvr) {
            // s_bChase -> CField::SendTransferFieldRequest
            outPacket.encodeInt(0); // nTargetPosition_X
            outPacket.encodeInt(0); // nTargetPosition_Y
        }
        return outPacket;
    }

    private static OutPacket of(WhisperFlag flag) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Whisper);
        outPacket.encodeByte(flag.getValue());
        return outPacket;
    }
}
