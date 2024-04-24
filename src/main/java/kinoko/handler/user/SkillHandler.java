package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public final class SkillHandler {
    private static final Logger log = LogManager.getLogger(SkillHandler.class);

    @Handler(InHeader.USER_SKILL_USE_REQUEST)
    public static void handleUserSkillUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final Skill skill = new Skill();
        skill.skillId = inPacket.decodeInt(); // nSkillID
        skill.slv = inPacket.decodeByte(); // nSLV
        if (skill.skillId == Mechanic.ROCK_N_SHOCK) {
            skill.rockAndShockCount = inPacket.decodeByte();
            if (skill.rockAndShockCount == 2) {
                skill.rockAndShockId1 = inPacket.decodeInt();
                skill.rockAndShockId2 = inPacket.decodeInt();
            }
        }
        if (skill.skillId == Citizen.CALL_OF_THE_HUNTER) {
            skill.randomCapturedMobId = inPacket.decodeInt();
        }
        if (SkillConstants.isEncodePositionSkill(skill.skillId)) {
            skill.positionX = inPacket.decodeShort(); // GetPos()->x
            skill.positionY = inPacket.decodeShort(); // GetPos()->y
        }
        if (skill.skillId == Thief.SHADOW_STARS) {
            skill.spiritJavelinItemId = inPacket.decodeInt(); // nSpiritJavelinItemID
        }
        try (var locked = user.acquire()) {
            SkillProcessor.processSkill(locked, skill, inPacket);
        }
    }

    @Handler(InHeader.USER_SKILL_CANCEL_REQUEST)
    public static void handleUserSkillCancelRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        try (var locked = user.acquire()) {
            final Set<CharacterTemporaryStat> resetStats = locked.get().getSecondaryStat().resetTemporaryStat(skillId);
            if (resetStats.isEmpty()) {
                log.error("Tried to cancel skill {}", skillId);
                return;
            }
            user.validateStat();
            user.write(WvsContext.temporaryStatReset(resetStats));
            user.getField().broadcastPacket(UserRemote.temporaryStatReset(user, resetStats), user);
        }
    }

    @Handler(InHeader.USER_SKILL_PREPARE_REQUEST)
    public static void handleUserSkillPrepareRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final int slv = inPacket.decodeByte(); // nSLV
        final short actionAndDir = inPacket.decodeShort(); // nOneTimeAction & 0x7FFF | (nMoveAction << 15)
        final byte attackSpeed = inPacket.decodeByte(); // attack_speed_degree
        if (skillId == WildHunter.JAGUAR_OSHI) {
            inPacket.decodeInt(); // dwSwallowMobID
        }
        user.getField().broadcastPacket(UserRemote.skillPrepare(user, skillId, slv, actionAndDir, attackSpeed), user);
    }

    @Handler(InHeader.USER_CALC_DAMAGE_STAT_SET_REQUEST)
    public static void handleUserCalcDamageStatSetRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            user.updatePassiveSkillData();
            user.validateStat();
        }
    }

    @Handler(InHeader.PASSIVE_SKILL_INFO_UPDATE)
    public static void handlePassiveSkillInfoUpdate(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        try (var locked = user.acquire()) {
            user.updatePassiveSkillData();
            user.validateStat();
        }
    }
}
