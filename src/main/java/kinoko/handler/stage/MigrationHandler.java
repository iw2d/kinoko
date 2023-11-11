package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.stage.StagePacket;
import kinoko.server.Client;
import kinoko.server.MigrationRequest;
import kinoko.server.Server;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.Account;
import kinoko.world.user.CalcDamage;
import kinoko.world.user.CharacterData;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class MigrationHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.MIGRATE_IN)
    public static void handleMigrateIn(Client c, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        c.setMachineId(machineId);

        // Check Migration Request
        final Optional<MigrationRequest> migrationResult = Server.handleMigration(c, characterId);
        if (migrationResult.isEmpty()) {
            log.error("[MigrationHandler] Migration failed for character ID : {}", characterId);
            c.close();
            return;
        }
        final MigrationRequest migrationRequest = migrationResult.get();

        // Check Account
        final int accountId = migrationRequest.accountId();
        final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountById(accountId);
        if (accountResult.isEmpty()) {
            log.error("[MigrationHandler] Could not retrieve account with ID : {}", accountId);
            c.close();
            return;
        }
        final Account account = accountResult.get();
        account.setWorldId(migrationRequest.selectedChannel().getWorldId());
        account.setChannelId(migrationRequest.selectedChannel().getChannelId());
        c.setAccount(account);

        // TODO: load AvatarDatas and check character belongs to account (also for initializing blessing)

        // Check Character
        final Optional<CharacterData> characterResult = DatabaseManager.characterAccessor().getCharacterById(characterId);
        if (characterResult.isEmpty()) {
            log.error("[MigrationHandler] Could not retrieve character with ID : {}", characterId);
            c.close();
            return;
        }
        final CharacterData characterData = characterResult.get();
        if (characterData.getAccountId() != accountId) {
            log.error("[MigrationHandler] attempted to log into character with ID {} that belongs to account with ID {} using account with ID {}", characterId, characterData.getAccountId(), accountId);
            c.close();
            return;
        }
        final User user = new User(c, characterData, CalcDamage.using(ThreadLocalRandom.current()));
        c.setUser(user);

        // TODO: add User to channel
        c.write(StagePacket.setField(user, account.getChannelId(), true, false));

        // TODO: keymap, quickslot, macros

        // TODO: add to Field

        // TODO: update friends, family, guild, party
    }
}
