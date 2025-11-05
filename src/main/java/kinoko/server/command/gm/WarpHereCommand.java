package kinoko.server.command.gm;

import kinoko.provider.map.PortalInfo;
import kinoko.server.Server;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.user.RemoteUser;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.user.User;

import java.util.Optional;

public final class WarpHereCommand {
    /**
     * Summons another player to your location.
     * Usage: !summon <player name>
     */
    @Command({"warphere", "summon"})
    @Arguments("player name")
    public static void warphere(User user, String[] args) {
        final String targetName = args[1];

        Optional<User> targetUserResult = Server.getCentralServerNode().getUserByCharacterName(targetName);

        if (targetUserResult.isEmpty()) {
            user.systemMessage("Could not find player '%s'.", targetName);
            return;
        }

        final User targetUser = targetUserResult.get();
        targetUser.warpTo(user);
        user.systemMessage("Summoned %s to your location.", targetName);
        targetUser.systemMessage("You have been summoned by %s.", user.getCharacterName());
    }
}
