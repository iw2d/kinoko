package kinoko.server.command.manager;

import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.skill.SkillRecord;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * Adds or sets a skill for a user.
 * Manager-level command.
 */
public final class SkillCommand {

    @Command("skill")
    @Arguments({ "skill ID", "skill level" })
    public static void skill(User user, String[] args) {
        try {
            int skillId = Integer.parseInt(args[1]);
            int slv = Integer.parseInt(args[2]);

            Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find skill : %d", skillId));
                return;
            }

            SkillInfo si = skillInfoResult.get();
            SkillRecord skillRecord = new SkillRecord(si.getSkillId());
            skillRecord.setSkillLevel(Math.min(slv, si.getMaxLevel()));
            skillRecord.setMasterLevel(si.getMaxLevel());

            SkillManager sm = user.getSkillManager();
            sm.addSkill(skillRecord);
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecord, true));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !skill <skill ID> <skill level>"));
        }
    }
}
