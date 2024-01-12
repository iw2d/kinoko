package kinoko.server.script;

import kinoko.packet.script.ScriptMessage;
import kinoko.packet.script.ScriptMessageParam;
import kinoko.packet.script.ScriptMessageType;
import kinoko.packet.script.ScriptPacket;
import kinoko.server.packet.OutPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class ScriptManager {
    private static final Logger log = LogManager.getLogger(ScriptManager.class);

    protected final Set<ScriptMessageParam> messageParams;

    protected int speakerId;
    protected CompletableFuture<ScriptAnswer> answerFuture;

    protected ScriptManager() {
        this.messageParams = EnumSet.noneOf(ScriptMessageParam.class);
    }

    abstract void write(OutPacket outPacket);

    protected final void toggleParam(ScriptMessageParam messageParam, boolean enabled) {
        if (enabled) {
            messageParams.add(messageParam);
        } else {
            messageParams.remove(messageParam);
        }
    }

    protected final ScriptAnswer waitForAnswer() {
        this.answerFuture = new CompletableFuture<>();
        return answerFuture.join(); // TODO: exception handling
    }

    public final void submitAnswer(ScriptAnswer answer) {
        answerFuture.complete(answer);
    }

    public final void setNotCancellable(boolean isNotCancellable) {
        toggleParam(ScriptMessageParam.NOT_CANCELLABLE, isNotCancellable);
    }

    public final void setPlayerAsSpeaker(boolean isPlayerAsSpeaker) {
        toggleParam(ScriptMessageParam.PLAYER_AS_SPEAKER, isPlayerAsSpeaker);
    }

    public final void setSpeakerId(int speakerId) {
        messageParams.add(ScriptMessageParam.OVERRIDE_SPEAKER_ID);
        this.speakerId = speakerId;
    }

    public final void setFlipSpeaker(boolean isFlipSpeaker) {
        toggleParam(ScriptMessageParam.FLIP_SPEAKER, isFlipSpeaker);
    }

    public final int sayOk(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, false, false)));
        return waitForAnswer().getAction();
    }

    public final int sayPrev(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, true, false)));
        return waitForAnswer().getAction();
    }

    public final int sayNext(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, false, true)));
        return waitForAnswer().getAction();
    }

    public final int sayNextPrev(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.say(speakerId, messageParams, text, true, true)));
        return waitForAnswer().getAction();
    }

    public final int sayImage(List<String> images) {
        write(ScriptPacket.scriptMessage(ScriptMessage.sayImage(speakerId, messageParams, images)));
        return waitForAnswer().getAction();
    }

    public final int askMenu(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_MENU, text)));
        return waitForAnswer().getSelection();
    }

    public final int askYesNo(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_YES_NO, text)));
        return waitForAnswer().getAction();
    }

    public final int askYesNoQuest(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_YES_NO_QUEST, text)));
        return waitForAnswer().getAction();
    }

    public final int askSlideMenu(String text) {
        write(ScriptPacket.scriptMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_SLIDE_MENU, text)));
        return waitForAnswer().getSelection();
    }

    public final int askAvatar(String text, List<Integer> options) {
        write(ScriptPacket.scriptMessage(ScriptMessage.askAvatar(speakerId, messageParams, text, options)));
        return waitForAnswer().getSelection();
    }

    public final int askNumber(String text, int numberDefault, int numberMin, int numberMax) {
        write(ScriptPacket.scriptMessage(ScriptMessage.askNumber(speakerId, messageParams, text, numberDefault, numberMin, numberMax)));
        return waitForAnswer().getNumberAnswer();
    }

    public final String askText(String text, String textDefault, int textLengthMin, int textLengthMax) {
        write(ScriptPacket.scriptMessage(ScriptMessage.askText(speakerId, messageParams, text, textDefault, textLengthMin, textLengthMax)));
        return waitForAnswer().getTextAnswer();
    }

    public final String askBoxText(String text, String textDefault, int textBoxColumns, int textBoxLines) {
        write(ScriptPacket.scriptMessage(ScriptMessage.askBoxText(speakerId, messageParams, text, textDefault, textBoxColumns, textBoxLines)));
        return waitForAnswer().getTextAnswer();
    }
}
