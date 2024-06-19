package kinoko.world.user.effect;

import kinoko.server.packet.OutPacket;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.Citizen;

public final class SkillEffect extends Effect {
    public int skillId;
    public int skillLevel;
    public int charLevel;
    public boolean enable;
    public boolean left;
    public int info;
    public int positionX;
    public int positionY;

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
                        outPacket.encodeByte(info); // 0 : monster successfully captured, 1 : capture failed monster hp too high, 2 : monster cannot be captured
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
                if (skillId == Thief.MONSTER_BOMB) {
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
