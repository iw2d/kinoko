package kinoko.server.script;

import java.util.ArrayList;
import java.util.List;

public final class ScriptMemory {
    private final List<ScriptMessage> messageMemory = new ArrayList<>();
    private int messageIndex = -1;

    public void recordMessage(ScriptMessage scriptMessage) {
        assert (messageIndex == messageMemory.size() - 1); // assert current position
        messageMemory.add(scriptMessage);
        messageIndex++;
    }

    public boolean isInMemory() {
        return messageIndex >= 0 && messageIndex < messageMemory.size() - 1;
    }

    public boolean isPrevPossible() {
        return messageIndex >= 1 && messageMemory.size() > messageIndex &&
                messageMemory.get(messageIndex).isPrevPossible();
    }

    public ScriptMessage prevMessage() {
        messageIndex--;
        return messageMemory.get(messageIndex);
    }

    public ScriptMessage nextMessage() {
        messageIndex++;
        return messageMemory.get(messageIndex);
    }
}
