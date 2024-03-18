package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.skill.Attack;
import kinoko.world.skill.AttackInfo;
import kinoko.world.skill.HitInfo;
import kinoko.world.user.User;

public final class SummonedPacket {
    // CSummonedPool::OnPacket -----------------------------------------------------------------------------------------

    public static OutPacket summonedEnterField(User user, Summoned summoned) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SUMMONED_ENTER_FIELD);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        // CSummonedPool::OnCreated
        outPacket.encodeInt(summoned.getId()); // dwSummonedID
        outPacket.encodeInt(summoned.getSkillId()); // nSkillID
        outPacket.encodeByte(user.getLevel()); // nCharLevel
        outPacket.encodeByte(summoned.getSkillLevel()); // nSLV
        summoned.encode(outPacket);
        return outPacket;
    }

    public static OutPacket summonedLeaveField(User user, Summoned summoned) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SUMMONED_LEAVE_FIELD);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        // CSummonedPool::OnRemoved
        outPacket.encodeInt(summoned.getId()); // dwSummonedID
        outPacket.encodeByte(summoned.getLeaveType().getValue());
        return outPacket;
    }

    public static OutPacket move(User user, Summoned summoned, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SUMMONED_MOVE);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket attack(User user, Summoned summoned, Attack attack) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SUMMONED_ATTACK);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        // CSummoned::OnAttack
        outPacket.encodeByte(user.getLevel()); // nCharLevel
        outPacket.encodeByte(attack.actionAndDir);
        outPacket.encodeByte(attack.getMobCount()); // nMobCount
        for (AttackInfo ai : attack.getAttackInfo()) {
            outPacket.encodeInt(ai.mobId); // ATTACKINFO->dwMobID
            outPacket.encodeByte(ai.hitAction); // ATTACKINFO->nHitAction
            outPacket.encodeInt(ai.damage[0]); // ATTACKINFO->aDamage[0]
        }
        outPacket.encodeByte(0); // ignored
        return outPacket;
    }

    public static OutPacket skill(User user, Summoned summoned) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SUMMONED_SKILL);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        // CSummoned::OnSkill
        outPacket.encodeByte(0); // actionAndDir
        return outPacket;
    }

    public static OutPacket hit(User user, Summoned summoned, HitInfo hitInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SUMMONED_HIT);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(summoned.getId());
        // CSummoned::OnHit
        outPacket.encodeByte(hitInfo.attackIndex.getValue()); // nAttackIdx
        outPacket.encodeInt(hitInfo.damage); // nDamage
        if (hitInfo.attackIndex.getValue() > -2) {
            outPacket.encodeInt(hitInfo.mobId); // dwMobTemplateID
            outPacket.encodeByte(hitInfo.dir); // bLeft
        }
        return outPacket;
    }
}
