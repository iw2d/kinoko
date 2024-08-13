package kinoko.script.common;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.List;
import java.util.Set;

public final class ScriptMessage implements Encodable {
    private final int speakerId;
    private final ScriptMessageType messageType;
    private final byte messageParam;

    // SAY
    private String text;
    private boolean hasPrev;
    private boolean hasNext;

    // SAYIMAGE
    private List<String> images;

    // ASKTEXT
    private String textDefault;
    private int textLengthMin;
    private int textLengthMax;

    // ASKNUMBER
    private int numberDefault;
    private int numberMin;
    private int numberMax;

    // ASKAVATAR
    private List<Integer> options;

    // ASKBOXTEXT
    private int textBoxColumns;
    private int textBoxLines;

    // ASKSLIDEMENU
    private int slideMenuType;

    private ScriptMessage(int speakerId, ScriptMessageType messageType, byte messageParam) {
        this.speakerId = speakerId;
        this.messageType = messageType;
        this.messageParam = messageParam;
    }

    public boolean isPrevPossible() {
        return messageType == ScriptMessageType.SAY && hasPrev;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(0); // nSpeakerTypeID, unused?
        outPacket.encodeInt(speakerId); // nSpeakerTemplateID
        outPacket.encodeByte(messageType.getValue()); // nMsgType
        outPacket.encodeByte(messageParam); // bParam
        switch (messageType) {
            case SAY -> {
                if ((messageParam & ScriptMessageParam.OVERRIDE_SPEAKER_ID.getValue()) != 0) {
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
                outPacket.encodeInt(slideMenuType); // slide menu dialog type
                // CSlideMenuDlgEX::SetSlideMenuDlg
                outPacket.encodeInt(0); // last index
                outPacket.encodeString(text); // #index#description (UI.wz/UIWindow2.img/SlideMenu/%d/BtMain/%d
            }
            case ASKQUIZ, ASKSPEEDQUIZ, ASKPET, ASKPETALL -> {
                throw new IllegalArgumentException("Unsupported message type : " + messageType.name());
            }
        }
    }

    public static ScriptMessage say(int speakerId, byte messageParam, String text, boolean hasPrev, boolean hasNext) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.SAY, messageParam);
        message.text = text;
        message.hasPrev = hasPrev;
        message.hasNext = hasNext;
        return message;
    }

    public static ScriptMessage sayImage(int speakerId, byte messageParam, List<String> images) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.SAYIMAGE, messageParam);
        message.images = images;
        return message;
    }

    public static ScriptMessage ask(int speakerId, byte messageParam, ScriptMessageType messageType, String text) {
        // ASK_YES_NO, ASK_YES_NO_QUEST, ASK_MENU
        final ScriptMessage message = new ScriptMessage(speakerId, messageType, messageParam);
        message.text = text;
        return message;
    }

    public static ScriptMessage askText(int speakerId, byte messageParam, String text, String textDefault, int textLengthMin, int textLengthMax) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKTEXT, messageParam);
        message.text = text;
        message.textDefault = textDefault;
        message.textLengthMin = textLengthMin;
        message.textLengthMax = textLengthMax;
        return message;
    }

    public static ScriptMessage askNumber(int speakerId, byte messageParam, String text, int numberDefault, int numberMin, int numberMax) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKNUMBER, messageParam);
        message.text = text;
        message.numberDefault = numberDefault;
        message.numberMin = numberMin;
        message.numberMax = numberMax;
        return message;
    }

    public static ScriptMessage askAvatar(int speakerId, byte messageParam, String text, List<Integer> options) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKAVATAR, messageParam);
        message.text = text;
        message.options = options;
        return message;
    }

    public static ScriptMessage askBoxText(int speakerId, byte messageParam, String text, String textDefault, int textBoxColumns, int textBoxLines) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKBOXTEXT, messageParam);
        message.text = text;
        message.textDefault = textDefault;
        message.textBoxColumns = textBoxColumns;
        message.textBoxLines = textBoxLines;
        return message;
    }

    public static ScriptMessage askSlideMenu(int speakerId, byte messageParam, int slideMenuType, String text) {
        final ScriptMessage message = new ScriptMessage(speakerId, ScriptMessageType.ASKSLIDEMENU, messageParam);
        message.slideMenuType = slideMenuType;
        message.text = text;
        return message;
    }
}
