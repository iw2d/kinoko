package kinoko.server.script;

import kinoko.packet.script.ScriptMessage;
import kinoko.packet.script.ScriptMessageParam;
import kinoko.packet.script.ScriptMessageType;
import kinoko.packet.script.ScriptPacket;
import kinoko.world.user.User;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * NpcScriptManager handles blocking calls in the script (waiting on user input) and manages the lock on the User, which
 * should be locked during the execution of the non-blocking script code.
 * <pre>
 * Context::eval -> NpcScriptManager::handleAnswer <-> NpcScriptManager::handleAnswer -> NpcScriptManager::dispose
 *      (lock)                  (unlock)         (lock)            (unlock)         (lock)           (unlock)
 * </pre>
 */
public final class NpcScriptManager extends ScriptManager {
    private final Set<ScriptMessageParam> messageParams = EnumSet.noneOf(ScriptMessageParam.class);
    private final ScriptMemory scriptMemory = new ScriptMemory();

    private int speakerId;
    private CompletableFuture<ScriptAnswer> answerFuture;

    public NpcScriptManager(User user, int speakerId) {
        super(user);
        this.speakerId = speakerId;
    }

    @Override
    public void disposeManager() {
        ScriptDispatcher.removeScriptManager(ScriptType.NPC, user);
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        messageParams.add(ScriptMessageParam.OVERRIDE_SPEAKER_ID);
        this.speakerId = speakerId;
    }

    private void toggleParam(ScriptMessageParam messageParam, boolean enabled) {
        if (enabled) {
            messageParams.add(messageParam);
        } else {
            messageParams.remove(messageParam);
        }
    }

    private void sendMessage(ScriptMessage scriptMessage) {
        scriptMemory.recordMessage(scriptMessage);
        user.write(ScriptPacket.scriptMessage(scriptMessage));
    }

    private ScriptAnswer handleAnswer() {
        // Unlock user while waiting on answer
        user.unlock();
        answerFuture = new CompletableFuture<>();
        final ScriptAnswer answer = answerFuture.join();
        user.lock();
        // Handle answer
        if (answer.getAction() == -1 || answer.getAction() == 5) {
            dispose();
        } else if (answer.getAction() == 0 && scriptMemory.isPrevPossible()) {
            // prev message in memory
            user.write(ScriptPacket.scriptMessage(scriptMemory.prevMessage()));
            return handleAnswer();
        } else if (scriptMemory.isInMemory()) {
            // next message in memory
            user.write(ScriptPacket.scriptMessage(scriptMemory.nextMessage()));
            return handleAnswer();
        }
        return answer;
    }


    // CONVERSATION METHODS --------------------------------------------------------------------------------------------

    public void submitAnswer(ScriptAnswer answer) {
        answerFuture.complete(answer);
    }

    public void setNotCancellable(boolean isNotCancellable) {
        toggleParam(ScriptMessageParam.NOT_CANCELLABLE, isNotCancellable);
    }

    public void setPlayerAsSpeaker(boolean isPlayerAsSpeaker) {
        toggleParam(ScriptMessageParam.PLAYER_AS_SPEAKER, isPlayerAsSpeaker);
    }

    public void setFlipSpeaker(boolean isFlipSpeaker) {
        toggleParam(ScriptMessageParam.FLIP_SPEAKER, isFlipSpeaker);
    }

    public void sayOk(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, false, false));
        handleAnswer();
    }

    public void sayPrev(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, true, false));
        handleAnswer();
    }

    public void sayNext(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, false, true));
        handleAnswer();
    }

    public void sayBoth(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, true, true));
        handleAnswer();
    }

    public void sayImage(List<String> images) {
        sendMessage(ScriptMessage.sayImage(speakerId, messageParams, images));
        handleAnswer();
    }

    public boolean askYesNo(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKYESNO, text));
        return handleAnswer().getAction() != 0;
    }

    public boolean askAccept(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKACCEPT, text));
        return handleAnswer().getAction() != 0;
    }

    public int askMenu(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKMENU, text));
        return handleAnswer().getAnswer();
    }

    public int askSlideMenu(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKSLIDEMENU, text));
        return handleAnswer().getAnswer();
    }

    public int askAvatar(String text, List<Integer> options) {
        sendMessage(ScriptMessage.askAvatar(speakerId, messageParams, text, options));
        return handleAnswer().getAnswer();
    }

    public int askNumber(String text, int numberDefault, int numberMin, int numberMax) {
        sendMessage(ScriptMessage.askNumber(speakerId, messageParams, text, numberDefault, numberMin, numberMax));
        return handleAnswer().getAnswer();
    }

    public String askText(String text, String textDefault, int textLengthMin, int textLengthMax) {
        sendMessage(ScriptMessage.askText(speakerId, messageParams, text, textDefault, textLengthMin, textLengthMax));
        return handleAnswer().getTextAnswer();
    }

    public String askBoxText(String text, String textDefault, int textBoxColumns, int textBoxLines) {
        sendMessage(ScriptMessage.askBoxText(speakerId, messageParams, text, textDefault, textBoxColumns, textBoxLines));
        return handleAnswer().getTextAnswer();
    }
}
