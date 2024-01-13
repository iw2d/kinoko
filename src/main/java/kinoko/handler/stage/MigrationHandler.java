package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.server.ChannelServer;
import kinoko.server.Server;
import kinoko.server.client.Client;
import kinoko.server.client.MigrationRequest;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.Account;
import kinoko.world.field.Field;
import kinoko.world.user.CalcDamage;
import kinoko.world.user.CharacterData;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class MigrationHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.MIGRATE_IN)
    public static void handleMigrateIn(Client c, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        inPacket.decodeShort(); // unk
        final byte[] clientKey = inPacket.decodeArray(8);

        c.setMachineId(machineId);
        c.setClientKey(clientKey);

        // Check Migration Request
        final Optional<MigrationRequest> migrationResult = Server.fetchMigrationRequest(c, characterId);
        if (migrationResult.isEmpty()) {
            log.error("Migration failed for character ID : {}", characterId);
            c.close();
            return;
        }
        final MigrationRequest mr = migrationResult.get();

        // Check Channel
        if (!(c.getConnectedServer() instanceof final ChannelServer channelServer) ||
                channelServer.getChannelId() != mr.getChannelId()) {
            log.error("Tried to migrate to incorrect channel.");
            c.close();
            return;
        }

        // Check Account
        final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountById(mr.getAccountId());
        if (accountResult.isEmpty()) {
            log.error("Could not retrieve account with ID : {}", mr.getAccountId());
            c.close();
            return;
        }
        final Account account = accountResult.get();
        if (channelServer.getClientStorage().isConnected(account)) {
            log.error("Tried to connect to channel server while already connected");
            c.close();
            return;
        }
        account.setWorldId(channelServer.getWorldId());
        account.setChannelId(channelServer.getChannelId());
        c.setAccount(account);

        // Check Character
        final Optional<CharacterData> characterResult = DatabaseManager.characterAccessor().getCharacterById(characterId);
        if (characterResult.isEmpty()) {
            log.error("Could not retrieve character with ID : {}", characterId);
            c.close();
            return;
        }
        final CharacterData characterData = characterResult.get();
        if (characterData.getAccountId() != mr.getAccountId()) {
            log.error("Mismatching account IDs {}, {}", characterData.getAccountId(), mr.getAccountId());
            c.close();
            return;
        }
        final User user = new User(c, characterData, CalcDamage.getDefault());
        if (channelServer.getClientStorage().isConnected(user)) {
            log.error("Tried to connect to channel server while already connected");
            c.close();
            return;
        }

        // Initialize User
        c.setUser(user);
        channelServer.getClientStorage().addClient(c);

        // Add User to Field
        final int fieldId = user.getCharacterData().getCharacterStat().getPosMap();
        final byte portalId = user.getCharacterData().getCharacterStat().getPortal();
        final Field field;
        final Optional<Field> fieldResult = channelServer.getFieldById(fieldId);
        if (fieldResult.isPresent()) {
            field = fieldResult.get();
        } else {
            log.error("Could not retrieve field ID : {} for character ID : {}, moving to {}", fieldId, user.getCharacterId(), 100000000);
            field = channelServer.getFieldById(100000000).orElseThrow(() -> new IllegalStateException("Could not resolve Field from ChannelServer"));
        }
        user.warp(field, portalId, true, false);


        // TODO: keymap, quickslot, macros

        // TODO: add to Field

        // TODO: update friends, family, guild, party
    }
}
