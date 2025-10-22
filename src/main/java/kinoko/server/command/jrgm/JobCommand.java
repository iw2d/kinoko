package kinoko.server.command.jrgm;

import kinoko.packet.user.DragonPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.Dragon;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Changes the user's job and initializes job skills.
 * JrGM-level command.
 */
public final class JobCommand {

    @Command("job")
    @Arguments("job ID")
    public static void job(User user, String[] args) {
        try {
            int jobId = Integer.parseInt(args[1]);
            Job job = Job.getById(jobId);
            if (job == null) {
                user.write(MessagePacket.system("Could not change to unknown job : %d", jobId));
                return;
            }

            // Update job
            user.getCharacterStat().setJob(job.getJobId());
            user.write(WvsContext.statChanged(Stat.JOB, job.getJobId(), false));
            user.getField().broadcastPacket(UserRemote.effect(user, Effect.jobChanged()), user);

            // Update skills
            SkillManager sm = user.getSkillManager();
            List<SkillRecord> skillRecords = new ArrayList<>();
            for (int skillRoot : JobConstants.getSkillRootFromJob(jobId)) {
                for (SkillInfo si : SkillProvider.getSkillsForJob(Job.getById(skillRoot))) {
                    if (sm.getSkill(si.getSkillId()).isPresent()) continue;
                    if (si.isInvisible()) continue;

                    SkillRecord sr = new SkillRecord(si.getSkillId());
                    sr.setSkillLevel(0);
                    sr.setMasterLevel(si.getMasterLevel());
                    sm.addSkill(sr);
                    skillRecords.add(sr);
                }
            }

            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecords, true));

            // Additional handling for Dragon and WildHunter jobs
            if (JobConstants.isDragonJob(jobId)) {
                Dragon dragon = new Dragon(user.getJob());
                user.setDragon(dragon);
                user.getField().broadcastPacket(DragonPacket.dragonEnterField(user, dragon));
            } else {
                user.setDragon(null);
            }

            if (JobConstants.isWildHunterJob(jobId)) {
                user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
            }

            user.getConnectedServer().notifyUserUpdate(user);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !job <job ID>"));
        }
    }
}
