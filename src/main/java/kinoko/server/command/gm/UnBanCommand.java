package kinoko.server.command.gm;

import kinoko.database.DatabaseManager;
import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.Account;
import kinoko.world.user.BanInfo;
import kinoko.world.user.CharacterData;
import kinoko.world.user.User;

import java.util.Optional;

public final class UnBanCommand {
    /**
     * Unbans a player's account by username.
     * Usage: !unban <username>
     */
    @Command("unban")
    @Arguments("character username")
    public static void unban(User user, String[] args) {
        if (args.length < 2) {
            user.write(MessagePacket.system("Usage: !unban <character username>"));
            return;
        }

        final String targetUsername = args[1];

        // Look up the account by character username
        final Optional<CharacterData> targetCharacterResult = DatabaseManager.characterAccessor().getCharacterByName(targetUsername);

        if (targetCharacterResult.isEmpty()) {
            user.write(MessagePacket.system("Could not find character with username '%s'.", targetUsername));
            return;
        }

        final CharacterData targetCharacterData = targetCharacterResult.get();
        final Optional<Account> targetAccountResult = DatabaseManager.accountAccessor().getAccountById(targetCharacterData.getAccountId());

        if (targetAccountResult.isEmpty()) {
            user.write(MessagePacket.system("Could not find account with character username '%s'.", targetUsername));
            return;
        }

        final Account targetAccount = targetAccountResult.get();

        BanInfo banInfo = targetAccount.getBanInfo();
        if (!banInfo.isBanned()) {
            user.write(MessagePacket.system("Account '%s' is not banned.", targetUsername));
            return;
        }

        // Lift the ban using BanInfo
        banInfo.liftBan();
        targetAccount.setBanInfo(banInfo);
        DatabaseManager.accountAccessor().saveAccount(targetAccount);

        user.write(MessagePacket.system("Unbanned account '%s' (Account ID: %d).", targetUsername, targetAccount.getId()));
    }
}
