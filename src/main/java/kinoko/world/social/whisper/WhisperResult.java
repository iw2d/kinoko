package kinoko.world.social.whisper;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public class WhisperResult implements Encodable {
    protected final WhisperFlag flag;

    private String characterName;
    private String message;
    private int int1;
    private boolean bool1;

    WhisperResult(WhisperFlag flag) {
        this.flag = flag;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(flag.getValue());
        switch (flag) {
            case WHISPER_BLOCKED -> {
                outPacket.encodeString(characterName);
                outPacket.encodeByte(bool1); // bool1 ? "Unable to find '%s'" : "'%s' have currently disabled whispers."
            }
            case WHISPER_RESULT, WHISPER_RESULT_MANAGER -> {
                outPacket.encodeString(characterName); // sReceiver
                outPacket.encodeByte(bool1); // success
            }
            case WHISPER_RECEIVE -> {
                outPacket.encodeString(characterName);
                outPacket.encodeByte(int1); // nChannelID
                outPacket.encodeByte(bool1); // bFromAdmin
                outPacket.encodeString(message);
            }
            case WHISPER_RECEIVE_MANAGER -> {
                // CField::BlowWeather (item id : 5120025)
                outPacket.encodeString(characterName);
                outPacket.encodeByte(bool1); // unused
                outPacket.encodeString(message);
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported whisper result");
            }
        }
    }

    public static WhisperResult whisperBlocked(String targetCharacterName) {
        final WhisperResult result = new WhisperResult(WhisperFlag.WHISPER_BLOCKED);
        result.characterName = targetCharacterName;
        result.bool1 = false; // '%s' have currently disabled whispers.
        return result;
    }

    public static WhisperResult whisperResult(String targetCharacterName, boolean success) {
        final WhisperResult result = new WhisperResult(WhisperFlag.WHISPER_RESULT);
        result.characterName = targetCharacterName;
        result.bool1 = success;
        return result;
    }

    public static WhisperResult whisperReceive(int sourceChannelId, String sourceCharacterName, String message) {
        final WhisperResult result = new WhisperResult(WhisperFlag.WHISPER_RECEIVE);
        result.characterName = sourceCharacterName;
        result.int1 = sourceChannelId; // nChannelID
        result.bool1 = false; // bFromAdmin
        result.message = message;
        return result;
    }
}
