package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.skill.Attack;
import kinoko.world.skill.AttackInfo;
import kinoko.world.skill.HitInfo;
import kinoko.world.user.User;

import java.util.List;

public final class SummonedPacket {
    // CSummonedPool::OnPacket -----------------------------------------------------------------------------------------

    public static OutPacket summonedEnterField(User user, Summoned summoned) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SummonedEnterField);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        // CSummonedPool::OnCreated
        outPacket.encodeInt(summoned.getId()); // dwSummonedID
        outPacket.encodeInt(summoned.getSkillId()); // nSkillID
        outPacket.encodeByte(user.getLevel()); // nCharLevel
        outPacket.encodeByte(summoned.getSkillLevel()); // nSLV
        // CSummoned::Init
        outPacket.encodeShort(summoned.getX()); // nX
        outPacket.encodeShort(summoned.getY()); // nY
        outPacket.encodeByte(summoned.getMoveAction()); // nMoveAction
        outPacket.encodeShort(summoned.getFoothold()); // nCurFoothold
        outPacket.encodeByte(summoned.getMoveAbility().getValue()); // nMoveAbility
        outPacket.encodeByte(summoned.getAssistType().getValue()); // nAssistType
        outPacket.encodeByte(summoned.getEnterType().getValue()); // nEnterType
        outPacket.encodeByte(summoned.getAvatarLook() != null); // bAvatarLook
        if (summoned.getAvatarLook() != null) {
            summoned.getAvatarLook().encode(outPacket); // AvatarLook::Decode
        }
        if (summoned.getSkillId() == Mechanic.ROCK_N_SHOCK) {
            final List<Summoned> rockAndShockList = user.getSummoned().getOrDefault(Mechanic.ROCK_N_SHOCK, List.of());
            outPacket.encodeByte(rockAndShockList.size() == 3 ? 1 : 0); // nTeslaCoilState == 1
            if (rockAndShockList.size() == 3) {
                for (Summoned rockAndShock : rockAndShockList) {
                    outPacket.encodeShort(rockAndShock.getX());
                    outPacket.encodeShort(rockAndShock.getY());
                }
            }
        }
        return outPacket;
    }

    public static OutPacket summonedLeaveField(User user, Summoned summoned) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SummonedLeaveField);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        // CSummonedPool::OnRemoved
        outPacket.encodeInt(summoned.getId()); // dwSummonedID
        outPacket.encodeByte(summoned.getLeaveType().getValue());
        return outPacket;
    }

    public static OutPacket summonedMove(User user, Summoned summoned, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SummonedMove);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket summonedAttack(User user, Summoned summoned, Attack attack) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SummonedAttack);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        // CSummoned::OnAttack
        outPacket.encodeByte(user.getLevel()); // nCharLevel
        outPacket.encodeByte(attack.actionAndDir);
        outPacket.encodeByte(attack.getAttackInfo().size()); // nMobCount
        for (AttackInfo ai : attack.getAttackInfo()) {
            outPacket.encodeInt(ai.mobId); // ATTACKINFO->dwMobID
            outPacket.encodeByte(ai.hitAction); // ATTACKINFO->nHitAction
            outPacket.encodeInt(ai.damage[0]); // ATTACKINFO->aDamage[0]
        }
        outPacket.encodeByte(0); // ignored
        return outPacket;
    }

    public static OutPacket summonedSkill(User user, Summoned summoned, byte actionAndDir) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SummonedSkill);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        // CSummoned::OnSkill
        outPacket.encodeByte(actionAndDir); // actionAndDir
        return outPacket;
    }

    public static OutPacket summonedHit(User user, Summoned summoned, HitInfo hitInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SummonedHit);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        // CSummoned::OnHit
        outPacket.encodeByte(hitInfo.attackIndex); // nAttackIdx
        outPacket.encodeInt(hitInfo.damage); // nDamage
        if (hitInfo.attackIndex > -2) {
            outPacket.encodeInt(hitInfo.templateId); // dwMobTemplateID
            outPacket.encodeByte(hitInfo.dir); // bLeft
        }
        return outPacket;
    }
}
