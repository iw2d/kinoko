package kinoko.packet.user.effect;

import kinoko.server.packet.OutPacket;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.Citizen;

public final class SkillEffect extends Effect {
    private int skillId;
    private int skillLevel;
    private int charLevel;
    private boolean enable;
    private boolean left;
    private int info;
    private short positionX;
    private short positionY;

    SkillEffect(EffectType type) {
        super(type);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case SkillUse -> {
                outPacket.encodeInt(skillId); // nSkillID
                outPacket.encodeByte(charLevel); // nCharLevel
                outPacket.encodeByte(skillLevel); // nSLV
                switch (skillId) {
                    case Warrior.BERSERK, Evan.DRAGON_FURY -> {
                        outPacket.encodeByte(enable); // bool -> CUser::LoadDarkForceEffect | CDragon::CreateEffect(1)
                    }
                    case Thief.CHAINS_OF_HELL -> {
                        outPacket.encodeByte(left); // bLeft
                        outPacket.encodeInt(info); // dwMobID
                    }
                    case Citizen.CALL_OF_THE_HUNTER -> {
                        outPacket.encodeByte(left); // bLeft
                        outPacket.encodeShort(positionX); // ptOffset.x
                        outPacket.encodeShort(positionY); // ptOffset.y
                    }
                    case Citizen.CAPTURE -> {
                        outPacket.encodeByte(info); // 0 : monster successfully captured, 1 : capture failed mosnter hp too high, 2 : monster cannot be captured
                    }
                }
                if (skillId / 10000000 == 9) { // is_unregistered_skill
                    outPacket.encodeByte(left); // bLeft
                }
            }
            case SkillAffected, SkillSpecialAffected -> {
                outPacket.encodeInt(skillId); // nSkillID
                outPacket.encodeByte(skillLevel); // nSLV
            }
            case SkillAffected_Select -> {
                outPacket.encodeInt(info); // nSelect
                outPacket.encodeInt(skillId); // nSkillID
                outPacket.encodeByte(skillLevel); // nSLV
            }
            case SkillSpecial -> {
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

    public static SkillEffect skillUse(int skillId, int skillLevel, int charLevel) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillUse);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        effect.charLevel = charLevel;
        return effect;
    }

    public static SkillEffect skillAffected(int skillId, int skillLevel) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillAffected);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        return effect;
    }
}
