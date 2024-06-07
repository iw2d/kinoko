package kinoko.packet.script;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ScriptPacket {
    // ScriptMan::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket scriptMessage(ScriptMessage scriptMessage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ScriptMessage);
        scriptMessage.encode(outPacket);
        return outPacket;
    }
}
