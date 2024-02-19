package kinoko.packet.user.effect;

import kinoko.server.packet.OutPacket;

public final class SkillEffect extends Effect {
    private int skillId;
    private int skillLevel;
    private int charLevel;
    private boolean enable;
    private boolean left;
    private int info;
    private short positionX;
    private short positionY;

    public SkillEffect(EffectType type) {
        super(type);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case SKILL_USE -> {
                outPacket.encodeInt(skillId); // nSkillID
                outPacket.encodeByte(charLevel); // nCharLevel
                outPacket.encodeByte(skillLevel); // nSLV
                switch (skillId) {
                    case 1320006, 22160000 -> {
                        outPacket.encodeByte(enable); // bool -> CUser::LoadDarkForceEffect | CDragon::CreateEffect(1)
                    }
                    case 4341005 -> {
                        outPacket.encodeByte(left); // bLeft
                        outPacket.encodeInt(info); // dwMobID
                    }
                    case 30001062 -> {
                        outPacket.encodeByte(left); // bLeft
                        outPacket.encodeShort(positionX); // ptOffset.x
                        outPacket.encodeShort(positionY); // ptOffset.y
                    }
                    case 30001061 -> {
                        outPacket.encodeByte(info); // 0 : monster successfully captured, 1 : capture failed mosnter hp too high, 2 : monster cannot be captured
                    }
                }
                if (skillId / 10000000 == 9) { // is_unregistered_skill
                    outPacket.encodeByte(left); // bLeft
                }
            }
            case SKILL_AFFECTED, SKILL_SPECIAL_AFFECTED -> {
                outPacket.encodeInt(skillId); // nSkillID
                outPacket.encodeByte(skillLevel); // nSLV
            }
            case SKILL_AFFECTED_SELECT -> {
                outPacket.encodeInt(info); // nSelect
                outPacket.encodeInt(skillId); // nSkillID
                outPacket.encodeByte(skillLevel); // nSLV
            }
            case SKILL_SPECIAL -> {
                outPacket.encodeInt(skillId); // nSkillID
                if (skillId == 4341003) {
                    outPacket.encodeInt(positionX); // nTimeBombX
                    outPacket.encodeInt(positionY); // nTimeBombY
                    outPacket.encodeInt(skillLevel); // nSLV
                    outPacket.encodeInt(0); // ignored
                }
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported effect type");
            }
        }
    }
}
