package kinoko.world.user.config;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;

public final class SingleMacro implements Encodable {
    private final String name;
    private final boolean mute;
    private final int[] skills;

    public SingleMacro(String name, boolean mute, int[] skills) {
        assert skills.length == GameConstants.MACRO_SKILL_COUNT;
        this.name = name;
        this.mute = mute;
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public boolean isMute() {
        return mute;
    }

    public int[] getSkills() {
        return skills;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // SINGLEMACRO::Decode
        outPacket.encodeString(name); // sName
        outPacket.encodeByte(mute); // bMute
        for (int skill : skills) {
            outPacket.encodeInt(skill); // aSkill
        }
    }

    public static SingleMacro decode(InPacket inPacket) {
        final String name = inPacket.decodeString();
        final boolean mute = inPacket.decodeBoolean();
        final int[] skills = new int[GameConstants.MACRO_SKILL_COUNT];
        for (int i = 0; i < skills.length; i++) {
            skills[i] = inPacket.decodeInt();
        }
        return new SingleMacro(name, mute, skills);
    }
}
