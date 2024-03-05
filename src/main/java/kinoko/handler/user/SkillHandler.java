package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.job.JobConstants;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class SkillHandler {
    private static final Logger log = LogManager.getLogger(SkillHandler.class);

    @Handler(InHeader.USER_SKILL_UP_REQUEST)
    public static void handleUserSkillUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int skillId = inPacket.decodeInt(); // nSkillID
        try (var locked = user.acquire()) {
            final SkillManager sm = user.getSkillManager();
            final Optional<SkillRecord> skillRecordResult = sm.getSkill(skillId);
            if (skillRecordResult.isEmpty()) {
                log.error("Tried to add a skill {} not owned by user", skillId);
                user.dispose();
                return;
            }
            final SkillRecord skillRecord = skillRecordResult.get();
            if (skillRecord.getSkillLevel() >= skillRecord.getMasterLevel()) {
                log.error("Tried to add a skill {} at master level {}/{}", skillId, skillRecord.getSkillLevel(), skillRecord.getMasterLevel());
                user.dispose();
                return;
            }

            final int skillRoot = SkillConstants.getSkillRoot(skillId);
            if (JobConstants.isBeginnerJob(skillRoot)) {
                // Check if valid beginner skill
                if (!SkillConstants.isBeginnerSpAddableSkill(skillId)) {
                    log.error("Tried to add an invalid beginner skill {}", skillId);
                    user.dispose();
                    return;
                }
                // Compute sp spent on beginner skills
                final int spentSp = sm.getSkillRecords().stream()
                        .filter((sr) -> SkillConstants.isBeginnerSpAddableSkill(sr.getSkillId()))
                        .mapToInt(SkillRecord::getSkillLevel)
                        .sum();
                // Beginner sp is calculated by level
                final int totalSp;
                if (JobConstants.isResistanceJob(skillRoot)) {
                    totalSp = Math.min(user.getLevel(), 10) - 1; // max total sp = 9
                } else {
                    totalSp = Math.min(user.getLevel(), 7) - 1; // max total sp = 6
                }
                // Check if sp can be added
                if (spentSp >= totalSp) {
                    log.error("Tried to add skill {} without having the required amount of sp", skillId);
                    user.dispose();
                    return;
                }
            } else if (JobConstants.isExtendSpJob(skillRoot)) {
                // TODO
            } else {
                // TODO
            }
            // Add skill point and update client
            skillRecord.setSkillLevel(skillRecord.getSkillLevel() + 1);
            user.write(WvsContext.changeSkillRecordResult(skillRecord));
        }
    }

    @Handler(InHeader.USER_SKILL_USE_REQUEST)
    public static void handleUserSkillUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int skillId = inPacket.decodeInt(); // nSkillID
        final int slv = inPacket.decodeByte(); // nSLV
        try (var locked = user.acquire()) {
            SkillProcessor.processSkill(user, skillId, slv, inPacket);
        }
    }

    @Handler(InHeader.USER_SKILL_CANCEL_REQUEST)
    public static void handleUserSkillCancelRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        user.getField().broadcastPacket(UserRemote.skillCancel(user, skillId), user);
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
}
