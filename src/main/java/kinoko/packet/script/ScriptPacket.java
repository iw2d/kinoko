package kinoko.packet.script;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ScriptPacket {
    public static OutPacket questResult(QuestResult questResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.QUEST_RESULT);
        questResult.encode(outPacket);
        return outPacket;
    }
}
