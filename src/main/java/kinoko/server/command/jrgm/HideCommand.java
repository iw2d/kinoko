package kinoko.server.command.jrgm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

public final class HideCommand {
    /**
     * Toggles GM Hide mode (makes you visible/invisible to players).
     * Usage: !hide
     */
    @Command("hide")
    public static void hide(User user, String[] args) {
        user.hide(!user.isHidden(), false);
        user.systemMessage(user.isHidden() ? "You are now invisible to players." : "You are now visible to players.");
    }
}
