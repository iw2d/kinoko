package kinoko.server.command.admin;

import kinoko.packet.user.UserLocal;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.job.legend.Aran;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

/**
 * Sets the Combo Ability Buff temporary stat for the user.
 * Admin-level command.
 */
public final class ComboCommand {

    @Command("combo")
    @Arguments("value")
    public static void combo(User user, String[] args) {
        try {
            int combo = Integer.parseInt(args[1]);
            user.setTemporaryStat(CharacterTemporaryStat.ComboAbilityBuff,
                    TemporaryStatOption.of(combo, Aran.COMBO_ABILITY, 0));
            user.write(UserLocal.incCombo(combo));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(kinoko.packet.world.MessagePacket.system("Usage: !combo <value>"));
        }
    }
}
