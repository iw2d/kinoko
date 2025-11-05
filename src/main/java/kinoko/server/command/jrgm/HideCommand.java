package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

public final class HideCommand {
    /**
     * Activates GM Hide mode (makes you invisible to players).
     * Usage: !hide
     */
    @Command("hide")
    public static void hide(User user, String[] args) {
        final int GM_HIDE_SKILL_ID = 9101004;

        // Apply the GM Hide buff using DarkSight
        // nOption = 1 (value), rOption = skillId, tOption = 0 (permanent until unhide)
        final TemporaryStatOption option = TemporaryStatOption.of(1, GM_HIDE_SKILL_ID, 0);
        user.setTemporaryStat(CharacterTemporaryStat.DarkSight, option);
        user.systemMessage("You are now invisible to players.");
    }
}
