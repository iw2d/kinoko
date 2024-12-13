package kinoko.world.user.effect;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Item;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.skill.Skill;
import kinoko.world.user.User;

public class Effect implements Encodable {
    protected final EffectType type;
    private boolean bool1;
    private int int1;
    private int int2;
    private String string1;

    Effect(EffectType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case LevelUp, PlayPortalSE, JobChanged, QuestComplete, MonsterBookCardGet, ItemLevelUp,
                    ItemMaker, ExpItemConsumed, Buff, SoulStoneUse, RepeatEffectRemove, EvolRing -> {
                // no encodes
            }
            case Quest -> {
                outPacket.encodeByte(bool1); // byte > 0 ? byte * (item gain message) : screen message
                if (bool1) {
                    outPacket.encodeInt(int1); // nItemID
                    outPacket.encodeInt(int2); // quantity
                } else {
                    outPacket.encodeString(string1); // sStrMsg
                    outPacket.encodeInt(int1); // nEffect
                }
            }
            case Pet -> {
                outPacket.encodeByte(int1); // nType
                outPacket.encodeByte(int2); // pet index
            }
            case ProtectOnDieItemUse -> {
                outPacket.encodeByte(bool1); // is safety charm
                if (bool1) {
                    outPacket.encodeByte(int1); // times left
                    outPacket.encodeByte(int2); // days left
                } else {
                    outPacket.encodeInt(int1); // nItemID
                }
            }
            case IncDecHPEffect -> {
                outPacket.encodeByte(int1); // nDelta
            }
            case BuffItemEffect -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case SquibEffect -> {
                outPacket.encodeString(string1); // sEffect
            }
            case LotteryUse -> {
                outPacket.encodeInt(int1); // nItemId
                outPacket.encodeByte(bool1); // bool
                if (bool1) {
                    outPacket.encodeString(string1); // sEffect
                }
            }
            case ReservedEffect -> {
                outPacket.encodeString(string1); // sEffect
            }
            case ConsumeEffect -> {
                outPacket.encodeInt(int1); // nItemID (Item/Cash/0528.img/%d/effect)
            }
            case UpgradeTombItemUse -> {
                outPacket.encodeByte(int1); // number of wheels of destiny left
            }
            case BattlefieldItemUse -> {
                outPacket.encodeString(string1); // sEffect
            }
            case AvatarOriented -> {
                outPacket.encodeString(string1); // sEffect
                outPacket.encodeInt(0); // ignored
            }
            case IncubatorUse -> {
                outPacket.encodeInt(int1); // nItemId
                outPacket.encodeString(string1); // sEffect
            }
            case PlaySoundWithMuteBGM -> {
                outPacket.encodeString(string1); // sName
            }
            case IncDecHPEffect_EX -> {
                outPacket.encodeInt(int1); // nDelta
            }
            case DeliveryQuestItemUse -> {
                outPacket.encodeInt(int1); // nItemId
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported effect type");
            }
        }
    }

    public static Effect levelUp() {
        return new Effect(EffectType.LevelUp);
    }

    public static Effect playPortalSE() {
        return new Effect(EffectType.PlayPortalSE);
    }

    public static Effect jobChanged() {
        return new Effect(EffectType.JobChanged);
    }

    public static Effect soulStoneUse() {
        return new Effect(EffectType.SoulStoneUse);
    }

    public static Effect upgradeTombItemUse(int remain) {
        final Effect effect = new Effect(EffectType.UpgradeTombItemUse);
        effect.int1 = remain;
        return effect;
    }

    public static Effect avatarOriented(String effectPath) {
        final Effect effect = new Effect(EffectType.AvatarOriented);
        effect.string1 = effectPath; // sEffect
        return effect;
    }

    public static Effect incDecHpEffect(int delta) {
        final Effect effect = new Effect(EffectType.IncDecHPEffect_EX);
        effect.int1 = delta; // nDelta
        return effect;
    }

    public static Effect buffItemEffect(int itemId) {
        final Effect effect = new Effect(EffectType.BuffItemEffect);
        effect.int1 = itemId;
        return effect;
    }

    public static Effect squibEffect(String effectPath) {
        final Effect effect = new Effect(EffectType.SquibEffect);
        effect.string1 = effectPath; // sEffect
        return effect;
    }

    public static Effect reservedEffect(String effectPath) {
        final Effect effect = new Effect(EffectType.ReservedEffect);
        effect.string1 = effectPath; // sEffect
        return effect;
    }

    public static Effect consumeEffect(int itemId) {
        final Effect effect = new Effect(EffectType.ConsumeEffect);
        effect.int1 = itemId; // nItemID
        return effect;
    }

    public static Effect gainItem(Item item) {
        return gainItem(item.getItemId(), item.getQuantity());
    }

    public static Effect gainItem(int itemId, int quantity) {
        final Effect effect = new Effect(EffectType.Quest);
        effect.bool1 = true; // item gain message
        effect.int1 = itemId;
        effect.int2 = quantity;
        return effect;
    }

    public static Effect questComplete() {
        return new Effect(EffectType.QuestComplete);
    }

    public static SkillEffect skillUse(int skillId, int skillLevel, int charLevel) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillUse);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        effect.charLevel = charLevel;
        return effect;
    }

    public static SkillEffect skillUse(Skill skill, int charLevel) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillUse);
        effect.skillId = skill.skillId;
        effect.skillLevel = skill.slv;
        switch (skill.skillId) {
            case Thief.CHAINS_OF_HELL -> {
                effect.left = skill.left; // bLeft
                if (skill.targetIds != null && skill.targetIds.length > 0) {
                    effect.info = skill.targetIds[0]; // dwMobID
                }
            }
            case Citizen.CALL_OF_THE_HUNTER -> {
                effect.left = skill.left; // bLeft
                effect.positionX = skill.positionX; // ptOffset.x
                effect.positionY = skill.positionY; // ptOffset.x
            }
        }
        effect.charLevel = charLevel;
        return effect;
    }

    public static SkillEffect skillUseEnable(int skillId, int skillLevel, int charLevel, boolean enable) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillUse);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        effect.charLevel = charLevel;
        effect.enable = enable;
        return effect;
    }

    public static SkillEffect skillUseInfo(int skillId, int skillLevel, int charLevel, int info) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillUse);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        effect.charLevel = charLevel;
        effect.info = info;
        return effect;
    }

    public static SkillEffect skillAffected(int skillId, int skillLevel) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillAffected);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        return effect;
    }

    public static SkillEffect skillAffectedSelect(int select, int skillId, int skillLevel) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillAffected_Select);
        effect.info = select;
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        return effect;
    }

    public static SkillEffect skillSpecial(int skillId, int skillLevel, int positionX, int positionY) {
        final SkillEffect effect = new SkillEffect(EffectType.SkillAffected_Select);
        effect.skillId = skillId;
        effect.skillLevel = skillLevel;
        effect.positionX = positionX;
        effect.positionY = positionY;
        return effect;
    }

    public static Effect petLevelUp(int petIndex) {
        final Effect effect = new Effect(EffectType.Pet);
        effect.int1 = PetEffectType.LevelUp.getValue(); // nType
        effect.int2 = petIndex;
        return effect;
    }

    public static Effect ItemMakerResult(boolean is_success) {
        final Effect effect = new Effect(EffectType.Quest);
        effect.int1 = (17);
        effect.bool1 = is_success;
        return effect;
    }

    public static Effect ItemMakerResultTo(User user, boolean is_success) {
        final Effect effect = new Effect(EffectType.Quest); // Could be EffectType.buff not sure
        effect.int1 = user.getCharacterId();
        effect.int2 = (17);
        effect.bool1 = is_success;
        return effect;
    }

}
