package kinoko.packet.script;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

import java.util.List;
import java.util.Set;

public final class ScriptMessage implements Encodable {
    private final int speakerId;
    private final ScriptMessageType messageType;
    private final Set<ScriptMessageParam> messageParams;

    // SAY
    private int speakerTemplateId;
    private String text;
    private boolean hasPrev;
    private boolean hasNext;

    // SAY_IMAGE
    private List<String> images;

    // ASK_TEXT
    private String textDefault;
    private int textLengthMin;
    private int textLengthMax;

    // ASK_NUMBER
    private int numberDefault;
    private int numberMin;
    private int numberMax;

    // ASK_AVATAR
    private List<Integer> options;

    // ASK_BOX_TEXT
    private int textBoxColumns;
    private int textBoxLines;

    private ScriptMessage(int speakerId, ScriptMessageType messageType, Set<ScriptMessageParam> messageParams) {
        this.speakerId = speakerId;
        this.messageType = messageType;
        this.messageParams = messageParams;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(0); // nSpeakerTypeID, unused?
        outPacket.encodeInt(speakerId); // nSpeakerTemplateId
        outPacket.encodeByte(messageType.getValue()); // nMsgType
        outPacket.encodeByte(ScriptMessageParam.from(messageParams)); // bParam
        switch (messageType) {
            case SAY -> {
                if (messageParams.contains(ScriptMessageParam.OVERRIDE_SPEAKER_ID)) {
                    outPacket.encodeInt(speakerTemplateId); // nSpeakerTemplateID
                }
                outPacket.encodeString(text); // sText
                outPacket.encodeByte(hasPrev); // bPrev
                outPacket.encodeByte(hasNext); // bNext
            }
            case SAY_IMAGE -> {
                outPacket.encodeByte(images.size());
                for (String image : images) {
                    outPacket.encodeString(image); // sPath
                }
            }
            case ASK_YES_NO, ASK_YES_NO_QUEST, ASK_MENU -> {
                outPacket.encodeString(text); // sText
            }
            case ASK_TEXT -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeString(textDefault); // sStrDefault
                outPacket.encodeShort(textLengthMin); // nLenMin
                outPacket.encodeShort(textLengthMax); // nLenMax
            }
            case ASK_NUMBER -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeInt(numberDefault); // nDef
                outPacket.encodeInt(numberMin); // nMin
                outPacket.encodeInt(numberMax); // nMax
            }
            case ASK_AVATAR, ASK_MEMBER_SHOP_AVATAR -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeByte(options.size());
                for (Integer option : options) {
                    outPacket.encodeInt(option);
                }
            }
            case ASK_BOX_TEXT -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeString(textDefault); // sStrDefault
                outPacket.encodeShort(textBoxColumns); // nCol
                outPacket.encodeShort(textBoxLines); // nLine
            }
            case ASK_SLIDE_MENU -> {
                outPacket.encodeInt(0); // slide menu dialog type
                // CSlideMenuDlgEX::SetSlideMenuDlg
                outPacket.encodeInt(0); // unused
                outPacket.encodeString(text); // #<DimensionalPortalType.getValue()>#<DimensionalPortalType.getDescription()>
            }
            case ASK_QUIZ, ASK_SPEED_QUIZ, ASK_PET, ASK_PET_ALL -> {
                throw new IllegalArgumentException("Unsupported message type : " + messageType.name());
            }
        }
    }

    public static ScriptMessage say(int speakerId, Set<ScriptMessageParam> messageParams, String text, boolean hasPrev, boolean hasNext) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.SAY, messageParams);
        message.text = text;
        message.hasPrev = hasPrev;
        message.hasNext = hasNext;
        return message;
    }

    public static ScriptMessage sayImage(int speakerId, Set<ScriptMessageParam> messageParams, List<String> images) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.SAY_IMAGE, messageParams);
        message.images = images;
        return message;
    }

    public static ScriptMessage ask(int speakerId, Set<ScriptMessageParam> messageParams, ScriptMessageType messageType, String text) {
        // ASK_YES_NO, ASK_YES_NO_QUEST, ASK_MENU, ASK_SLIDE_MENU
        final ScriptMessage message = new ScriptMessage(speakerId, messageType, messageParams);
        message.text = text;
        return message;
    }

    public static ScriptMessage askText(int speakerId, Set<ScriptMessageParam> messageParams, String text, String textDefault, int textLengthMin, int textLengthMax) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASK_TEXT, messageParams);
        message.text = text;
        message.textDefault = textDefault;
        message.textLengthMin = textLengthMin;
        message.textLengthMax = textLengthMax;
        return message;
    }

    public static ScriptMessage askNumber(int speakerId, Set<ScriptMessageParam> messageParams, String text, int numberDefault, int numberMin, int numberMax) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASK_NUMBER, messageParams);
        message.text = text;
        message.numberDefault = numberDefault;
        message.numberMin = numberMin;
        message.numberMax = numberMax;
        return message;
    }

    public static ScriptMessage askAvatar(int speakerId, Set<ScriptMessageParam> messageParams, String text, List<Integer> options) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASK_AVATAR, messageParams);
        message.text = text;
        message.options = options;
        return message;
    }

    public static ScriptMessage askBoxText(int speakerId, Set<ScriptMessageParam> messageParams, String text, String textDefault, int textBoxColumns, int textBoxLines) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASK_BOX_TEXT, messageParams);
        message.text = text;
        message.textDefault = textDefault;
        message.textBoxColumns = textBoxColumns;
        message.textBoxLines = textBoxLines;
        return message;
    }
}
