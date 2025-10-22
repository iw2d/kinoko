package kinoko.server.command.admin;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.SkillProvider;
import kinoko.provider.mob.MobSkillType;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Optional;

/**
 * Applies a mob skill as a temporary stat to the user.
 * Admin-level command.
 */
public final class MobSkillCommand {

    @Command("mobskill")
    @Arguments({ "skill ID", "skill level" })
    public static void mobskill(User user, String[] args) {
        try {
            int skillId = Integer.parseInt(args[1]);
            int slv = Integer.parseInt(args[2]);

            MobSkillType skillType = MobSkillType.getByValue(skillId);
            if (skillType == null) {
                user.write(MessagePacket.system("Could not resolve mob skill %d", skillId));
                return;
            }

            CharacterTemporaryStat cts = skillType.getCharacterTemporaryStat();
            if (cts == null) {
                user.write(MessagePacket.system("Mob skill %s does not apply a CTS", skillType));
                return;
            }

            Optional<SkillInfo> skillInfoResult = SkillProvider.getMobSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not resolve mob skill info %d", skillId));
                return;
            }

            SkillInfo si = skillInfoResult.get();
            int value = Math.max(si.getValue(SkillStat.x, slv), 1);
            int duration = si.getDuration(slv);

            user.setTemporaryStat(cts, TemporaryStatOption.ofMobSkill(value, skillId, slv, duration));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !mobskill <skill ID> <skill level>"));
        }
    }
}
