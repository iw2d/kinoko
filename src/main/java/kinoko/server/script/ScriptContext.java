package kinoko.server.script;

import kinoko.packet.script.ScriptMessage;
import kinoko.packet.script.ScriptMessageParam;
import kinoko.packet.script.ScriptMessageType;
import kinoko.packet.script.ScriptPacket;
import kinoko.world.user.User;
import org.graalvm.polyglot.HostAccess;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class ScriptContext {
    private final User user;
    private final Set<ScriptMessageParam> messageParams;

    private int speakerId;

    public ScriptContext(User user) {
        this.user = user;
        this.messageParams = EnumSet.noneOf(ScriptMessageParam.class);
    }

    private void toggleParam(ScriptMessageParam messageParam, boolean enabled) {
        if (enabled) {
            messageParams.add(messageParam);
        } else {
            messageParams.remove(messageParam);
        }
    }

    @HostAccess.Export
    public void setNotCancellable(boolean isNotCancellable) {
        toggleParam(ScriptMessageParam.NOT_CANCELLABLE, isNotCancellable);
    }

    @HostAccess.Export
    public void setPlayerAsSpeaker(boolean isPlayerAsSpeaker) {
        toggleParam(ScriptMessageParam.PLAYER_AS_SPEAKER, isPlayerAsSpeaker);
    }

    @HostAccess.Export
    public void setSpeakerId(int speakerId) {
        messageParams.add(ScriptMessageParam.OVERRIDE_SPEAKER_ID);
        this.speakerId = speakerId;
    }

    @HostAccess.Export
    public void setFlipSpeaker(boolean isFlipSpeaker) {
        toggleParam(ScriptMessageParam.FLIP_SPEAKER, isFlipSpeaker);
    }

    @HostAccess.Export
    public void sayOk(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, false, false)));
    }

    @HostAccess.Export
    public void sayPrev(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, true, false)));
    }

    @HostAccess.Export
    public void sayNext(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, false, true)));
    }

    @HostAccess.Export
    public void sayNextPrev(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, true, true)));
    }

    @HostAccess.Export
    public void sayImage(List<String> images) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.sayImage(speakerId, messageParams, images)));
    }

    @HostAccess.Export
    public void askYesNo(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_YES_NO, text)));
    }

    @HostAccess.Export
    public void askMenu(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_MENU, text)));
    }

    @HostAccess.Export
    public void askSlideMenu(String text) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_SLIDE_MENU, text)));
    }

    @HostAccess.Export
    public void askAvatar(String text, List<Integer> options) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.askAvatar(speakerId, messageParams, text, options)));
    }

    @HostAccess.Export
    public void askText(String text, String textDefault, int textLengthMin, int textLengthMax) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.askText(speakerId, messageParams, text, textDefault, textLengthMin, textLengthMax)));
    }

    @HostAccess.Export
    public void askNumber(String text, int numberDefault, int numberMin, int numberMax) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.askNumber(speakerId, messageParams, text, numberDefault, numberMin, numberMax)));
    }

    @HostAccess.Export
    public void askBoxText(String text, String textDefault, int textBoxColumns, int textBoxLines) {
        user.write(ScriptPacket.scriptMessage(ScriptMessage.askBoxText(speakerId, messageParams, text, textDefault, textBoxColumns, textBoxLines)));
    }
}
