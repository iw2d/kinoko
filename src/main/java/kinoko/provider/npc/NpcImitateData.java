package kinoko.provider.npc;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Util;

public final class NpcImitateData implements Encodable {
    public static final NpcImitateData NPC_9901000 = new NpcImitateData(
            9901000,
            "FangBlade",
            Util.stringToByteArray("00 00 21 4E 00 00 00 30 75 00 00 01 27 4A 0F 00 05 A6 05 10 00 07 28 5C 10 00 08 92 82 10 00 0B 11 DE 13 00 FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 6D 65 6F 77 00 00 00 00 00 00 00 00 00 01 00 09 52 00 00 18 79 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C8 70 00 C8 00 F4 01 D0 07 D0 07 6E 5A 00 00 50 C3 00 00 C5 C2 00 00 50 C3 00 00 05 00 03 00 00 00 00 00 E8 FF 00 00 00 00 83 65 14 06 04 00 00 00 00 00 00")
    );

    private final int npcId;
    private final String name;
    private final byte[] avatarLook;

    public NpcImitateData(int npcId, String name, byte[] avatarLook) {
        this.npcId = npcId;
        this.name = name;
        this.avatarLook = avatarLook;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public byte[] getAvatarLook() {
        return avatarLook;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(npcId); // dwTemplateID
        outPacket.encodeString(name); // sName
        outPacket.encodeArray(avatarLook); // AvatarLook::Decode
    }
}
