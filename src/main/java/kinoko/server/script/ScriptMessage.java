package kinoko.server.script;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.List;
import java.util.Set;

public final class ScriptMessage implements Encodable {
    private final int speakerId;
    private final ScriptMessageType messageType;
    private final Set<ScriptMessageParam> messageParams;

    // SAY
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

    public boolean isPrevPossible() {
        return messageType == ScriptMessageType.SAY && hasPrev;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(0); // nSpeakerTypeID, unused?
        outPacket.encodeInt(speakerId); // nSpeakerTemplateID
        outPacket.encodeByte(messageType.getValue()); // nMsgType
        outPacket.encodeByte(ScriptMessageParam.from(messageParams)); // bParam
        switch (messageType) {
            case SAY -> {
                if (messageParams.contains(ScriptMessageParam.OVERRIDE_SPEAKER_ID)) {
                    outPacket.encodeInt(speakerId); // nSpeakerTemplateID
                }
                outPacket.encodeString(text); // sText
                outPacket.encodeByte(hasPrev); // bPrev
                outPacket.encodeByte(hasNext); // bNext
            }
            case SAYIMAGE -> {
                outPacket.encodeByte(images.size());
                for (String image : images) {
                    outPacket.encodeString(image); // sPath
                }
            }
            case ASKYESNO, ASKACCEPT, ASKMENU -> {
                outPacket.encodeString(text); // sText
            }
            case ASKTEXT -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeString(textDefault); // sStrDefault
                outPacket.encodeShort(textLengthMin); // nLenMin
                outPacket.encodeShort(textLengthMax); // nLenMax
            }
            case ASKNUMBER -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeInt(numberDefault); // nDef
                outPacket.encodeInt(numberMin); // nMin
                outPacket.encodeInt(numberMax); // nMax
            }
            case ASKAVATAR, ASKMEMBERSHOPAVATAR -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeByte(options.size());
                for (Integer option : options) {
                    outPacket.encodeInt(option);
                }
            }
            case ASKBOXTEXT -> {
                outPacket.encodeString(text); // sText
                outPacket.encodeString(textDefault); // sStrDefault
                outPacket.encodeShort(textBoxColumns); // nCol
                outPacket.encodeShort(textBoxLines); // nLine
            }
            case ASKSLIDEMENU -> {
                outPacket.encodeInt(0); // slide menu dialog type
                // CSlideMenuDlgEX::SetSlideMenuDlg
                outPacket.encodeInt(0); // unused
                outPacket.encodeString(text); // #<DimensionalPortalType.getValue()>#<DimensionalPortalType.getDescription()>
            }
            case ASKQUIZ, ASKSPEEDQUIZ, ASKPET, ASKPETALL -> {
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
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.SAYIMAGE, messageParams);
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
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKTEXT, messageParams);
        message.text = text;
        message.textDefault = textDefault;
        message.textLengthMin = textLengthMin;
        message.textLengthMax = textLengthMax;
        return message;
    }

    public static ScriptMessage askNumber(int speakerId, Set<ScriptMessageParam> messageParams, String text, int numberDefault, int numberMin, int numberMax) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKNUMBER, messageParams);
        message.text = text;
        message.numberDefault = numberDefault;
        message.numberMin = numberMin;
        message.numberMax = numberMax;
        return message;
    }

    public static ScriptMessage askAvatar(int speakerId, Set<ScriptMessageParam> messageParams, String text, List<Integer> options) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKAVATAR, messageParams);
        message.text = text;
        message.options = options;
        return message;
    }

    public static ScriptMessage askBoxText(int speakerId, Set<ScriptMessageParam> messageParams, String text, String textDefault, int textBoxColumns, int textBoxLines) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKBOXTEXT, messageParams);
        message.text = text;
        message.textDefault = textDefault;
        message.textBoxColumns = textBoxColumns;
        message.textBoxLines = textBoxLines;
        return message;
    }
}
