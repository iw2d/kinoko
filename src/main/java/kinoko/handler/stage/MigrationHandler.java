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
import kinoko.world.ChannelServer;
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
        final Optional<MigrationRequest> migrationResult = Server.fetchMigrationRequest(c, characterId);
        if (migrationResult.isEmpty()) {
            log.error("[MigrationHandler] Migration failed for character ID : {}", characterId);
            c.close();
            return;
        }
        final MigrationRequest mr = migrationResult.get();

        // Check Channel
        if (!(c.getConnectedServer() instanceof final ChannelServer channelServer) ||
                channelServer.getChannelId() != mr.channelId()) {
            log.error("[MigrationHandler] Tried to migrate to incorrect channel.");
            c.close();
            return;
        }

        // Check Account
        final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountById(mr.accountId());
        if (accountResult.isEmpty()) {
            log.error("[MigrationHandler] Could not retrieve account with ID : {}", mr.accountId());
            c.close();
            return;
        }
        final Account account = accountResult.get();
        if (channelServer.getPlayerStorage().isConnected(account)) {
            log.error("[MigrationHandler] Tried to connect to channel server while already connected");
            c.close();
            return;
        }
        account.setWorldId(channelServer.getWorldId());
        account.setChannelId(channelServer.getChannelId());
        c.setAccount(account);

        // Check Character
        final Optional<CharacterData> characterResult = DatabaseManager.characterAccessor().getCharacterById(characterId);
        if (characterResult.isEmpty()) {
            log.error("[MigrationHandler] Could not retrieve character with ID : {}", characterId);
            c.close();
            return;
        }
        final CharacterData characterData = characterResult.get();
        if (characterData.getAccountId() != mr.accountId()) {
            log.error("[MigrationHandler] Mismatching account IDs {}, {}", characterData.getAccountId(), mr.accountId());
            c.close();
            return;
        }
        final User user = new User(characterData, CalcDamage.using(ThreadLocalRandom.current()));
        if (channelServer.getPlayerStorage().isConnected(user)) {
            log.error("[MigrationHandler] Tried to connect to channel server while already connected");
            c.close();
            return;
        }
        c.setUser(user);
        channelServer.getPlayerStorage().addPlayer(c);

        c.write(StagePacket.setField(user, channelServer.getChannelId(), true, false));

        // TODO: keymap, quickslot, macros

        // TODO: add to Field

        // TODO: update friends, family, guild, party
    }
}
