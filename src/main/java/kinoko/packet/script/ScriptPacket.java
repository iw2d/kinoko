package kinoko.packet.script;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptMessage;
import kinoko.world.quest.QuestResult;

public final class ScriptPacket {
    public static OutPacket userQuestResult(QuestResult questResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_QUEST_RESULT);
        questResult.encode(outPacket);
        return outPacket;
    }

    public static OutPacket scriptMessage(ScriptMessage scriptMessage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SCRIPT_MESSAGE);
        scriptMessage.encode(outPacket);
        return outPacket;
    }
}
