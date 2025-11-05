package kinoko.server.command.gm;

import kinoko.database.DatabaseManager;
import kinoko.packet.stage.LoginPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.Account;
import kinoko.world.user.BanInfo;
import kinoko.world.user.User;
import kinoko.world.user.stat.AdminLevel;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class BanCommand {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Bans a player's account.
     * Usage: !ban <player name> <reason>
     */
    @Command("ban")
    @Arguments({"player name", "reason"})
    public static void ban(User user, String[] args) {
        if (args.length < 3) {
            user.write(MessagePacket.system("Usage: !ban <player name> <reason>"));
            return;
        }

        final String targetName = args[1];
        final String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        // Find the target user on this channel
        final Optional<User> targetUserResult = user.getConnectedServer().getConnectedUsers().stream()
                .filter(u -> u.getCharacterName().equalsIgnoreCase(targetName))
                .findFirst();

        if (targetUserResult.isEmpty()) {
            user.write(MessagePacket.system("Could not find player '%s' on this channel.", targetName));
            return;
        }

        final User targetUser = targetUserResult.get();
        final Account targetAccount = targetUser.getAccount();

        // Prevent self-ban
        if (targetUser.getCharacterId() == user.getCharacterId()) {
            user.write(MessagePacket.system("You cannot ban yourself!"));
            return;
        }

        // Prevent banning a GM with a higher status than you. Banning same-level GMs is allowed.
        if (!user.getAdminLevel().isAtLeast(targetUser.getAdminLevel())) {
            user.write(MessagePacket.system("You cannot ban any GM with a higher status than you."));
            return;
        }

        // Check if already banned
        BanInfo banInfo = targetAccount.getBanInfo();
        if (banInfo.isBanned()) {
            user.write(MessagePacket.system("Player '%s' is already banned.", targetName));
            return;
        }

        // Construct full reason including GM name
        final String fullReason = user.getCharacterName() + " banned " + targetName + " for " + reason;
        // TODO: display this to all users in the server

        banInfo = new BanInfo(reason, null);
        targetAccount.setBanInfo(banInfo);

        // Save the account
        DatabaseManager.accountAccessor().saveAccount(targetAccount);

        // Notify the player and the GM
        targetUser.write(MessagePacket.system("You have been banned by GM %s.", user.getCharacterName()));
        targetUser.write(MessagePacket.system("Reason: %s", reason));
        user.write(MessagePacket.system("Banned player '%s' (Account ID: %d).", targetName, targetAccount.getId()));

        // Disconnect after 5 seconds
        scheduler.schedule(() -> {
            targetUser.logout(true);
            targetUser.getClient().close();
        }, 5, TimeUnit.SECONDS);
    }
}
