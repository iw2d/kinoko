package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;

public final class UnHideCommand {
    /**
     * Deactivates GM Hide mode (makes you visible again).
     * Usage: !unhide
     */
    @Command("unhide")
    public static void unhide(User user, String[] args) {
        final int GM_HIDE_SKILL_ID = 9101004;

        // Remove the GM Hide buff
        user.resetTemporaryStat(GM_HIDE_SKILL_ID);
        user.systemMessage("You are now visible to players.");
    }
}
