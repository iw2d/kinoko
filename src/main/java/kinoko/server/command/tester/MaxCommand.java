package kinoko.server.command.tester;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.MessagePacket;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.command.Command;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.skill.SkillConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MaxCommand {

    /**
     * Fully maxes the character stats, resets and re-adds skills, and restores HP/MP.
     * Tester-level command.
     *
     * @param user the user executing the command
     * @param args command arguments (none expected)
     */
    @Command("max")
    public static void max(User user, String[] args) {
        try {
            // Set stats
            CharacterStat cs = user.getCharacterStat();
            cs.setLevel((short) 200);
            cs.setMaxHp(50000);
            cs.setMaxMp(50000);
            cs.setExp(0);
            user.validateStat();
            user.write(WvsContext.statChanged(Map.of(
                    Stat.LEVEL, (byte) cs.getLevel(),
                    Stat.STR, cs.getBaseStr(),
                    Stat.DEX, cs.getBaseDex(),
                    Stat.INT, cs.getBaseInt(),
                    Stat.LUK, cs.getBaseLuk(),
                    Stat.MHP, cs.getMaxHp(),
                    Stat.MMP, cs.getMaxMp(),
                    Stat.EXP, cs.getExp()
            ), true));

            // Reset skills
            SkillManager sm = user.getSkillManager();
            List<SkillRecord> removedRecords = new ArrayList<>();
            for (SkillRecord skillRecord : sm.getSkillRecords()) {
                if (JobConstants.isBeginnerJob(SkillConstants.getSkillRoot(skillRecord.getSkillId()))) {
                    continue;
                }
                skillRecord.setSkillLevel(0);
                skillRecord.setMasterLevel(0);
                removedRecords.add(skillRecord);
                sm.removeSkill(skillRecord.getSkillId());
            }
            user.write(WvsContext.changeSkillRecordResult(removedRecords, true));

            // Add skills
            List<SkillRecord> skillRecords = new ArrayList<>();
            for (int skillRoot : JobConstants.getSkillRootFromJob(user.getJob())) {
//                if (JobConstants.isBeginnerJob(skillRoot)) {
//                    continue;
//                }
                Job job = Job.getById(skillRoot);
                for (SkillInfo si : SkillProvider.getSkillsForJob(job)) {
                    SkillRecord skillRecord = new SkillRecord(si.getSkillId());
                    skillRecord.setSkillLevel(si.getMaxLevel());
                    skillRecord.setMasterLevel(si.getMaxLevel());
                    sm.addSkill(skillRecord);
                    skillRecords.add(skillRecord);
                }
            }
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecords, true));

            // Heal
            user.setHp(user.getMaxHp());
            user.setMp(user.getMaxMp());

        } catch (Exception e) {
            user.systemMessage("Failed to max your character: %s", e.getMessage());
            e.printStackTrace();
        }
    }
}
