package kinoko.server.script;

import kinoko.packet.script.ScriptMessage;
import kinoko.packet.script.ScriptMessageParam;
import kinoko.packet.script.ScriptMessageType;
import kinoko.packet.script.ScriptPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ScriptManager {
    private static final Logger log = LogManager.getLogger(ScriptManager.class);

    private final List<ScriptMessage> messageMemory = new ArrayList<>();
    private final Set<ScriptMessageParam> messageParams = EnumSet.noneOf(ScriptMessageParam.class);
    private final User user;

    private int memoryIndex = -1;
    private int speakerId;
    private CompletableFuture<ScriptAnswer> answerFuture;

    public ScriptManager(User user) {
        this.user = user;
    }

    private void toggleParam(ScriptMessageParam messageParam, boolean enabled) {
        if (enabled) {
            messageParams.add(messageParam);
        } else {
            messageParams.remove(messageParam);
        }
    }

    private void sendMessage(ScriptMessage scriptMessage) {
        messageMemory.add(scriptMessage);
        memoryIndex++;
        user.write(ScriptPacket.scriptMessage(scriptMessage));
    }

    private ScriptAnswer handleAnswer() {
        this.answerFuture = new CompletableFuture<>();
        final ScriptAnswer answer = answerFuture.join();
        if (answer.getAction() == -1 || answer.getAction() == 5) {
            dispose();
        } else if (answer.getAction() == 0 && memoryIndex >= 1 && messageMemory.size() > memoryIndex &&
                messageMemory.get(memoryIndex).isPrevPossible()) {
            // prev message in memory
            memoryIndex--;
            final ScriptMessage prevMessage = messageMemory.get(memoryIndex);
            user.write(ScriptPacket.scriptMessage(prevMessage));
            return handleAnswer();
        } else if (memoryIndex >= 0 && memoryIndex < messageMemory.size() - 1) {
            // next message in memory
            memoryIndex++;
            final ScriptMessage nextMessage = messageMemory.get(memoryIndex);
            user.write(ScriptPacket.scriptMessage(nextMessage));
            return handleAnswer();
        }
        return answer;
    }

    public void submitAnswer(ScriptAnswer answer) {
        answerFuture.complete(answer);
    }

    // SCRIPTING API METHODS -------------------------------------------------------------------------------------------

    public void setNotCancellable(boolean isNotCancellable) {
        toggleParam(ScriptMessageParam.NOT_CANCELLABLE, isNotCancellable);
    }

    public void setPlayerAsSpeaker(boolean isPlayerAsSpeaker) {
        toggleParam(ScriptMessageParam.PLAYER_AS_SPEAKER, isPlayerAsSpeaker);
    }

    public void setSpeakerId(int speakerId) {
        messageParams.add(ScriptMessageParam.OVERRIDE_SPEAKER_ID);
        this.speakerId = speakerId;
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

    public void askYesNo(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_YES_NO, text));
        handleAnswer();
    }

    public void askYesNoQuest(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_YES_NO_QUEST, text));
        handleAnswer();
    }

    public int askMenu(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_MENU, text));
        return handleAnswer().getAnswer();
    }

    public int askSlideMenu(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASK_SLIDE_MENU, text));
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

    public void dispose() {
        ScriptDispatcher.removeScriptManager(user);
        user.dispose();
    }
}
