package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.ClientPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.TransferChannelType;
import kinoko.packet.field.TransferFieldType;
import kinoko.packet.stage.CashShopPacket;
import kinoko.packet.stage.StagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.CashItemResult;
import kinoko.server.cashshop.Gift;
import kinoko.server.header.InHeader;
import kinoko.server.memo.MemoResult;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.Client;
import kinoko.server.node.MigrationInfo;
import kinoko.server.node.TransferInfo;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;
import kinoko.world.user.Account;
import kinoko.world.user.CharacterData;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.funckey.FuncKeyManager;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class MigrationHandler {
    private static final Logger log = LogManager.getLogger(MigrationHandler.class);

    @Handler(InHeader.MIGRATE_IN)
    public static void handleMigrateIn(Client c, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final boolean isUserGm = inPacket.decodeBoolean(); // CWvsContext->m_nSubGradeCode >> 7
        inPacket.decodeByte(); // hardcoded 0
        final byte[] clientKey = inPacket.decodeArray(8);

        c.setMachineId(machineId);
        c.setClientKey(clientKey);

        // Resolve account id
        final Optional<Integer> accountIdResult = DatabaseManager.characterAccessor().getAccountIdByCharacterId(characterId);
        if (accountIdResult.isEmpty()) {
            log.error("Could not resolve account for character ID : {}", characterId);
            c.close();
            return;
        }
        final int accountId = accountIdResult.get();

        // Submit migration request
        final ChannelServerNode channelServerNode = ((ChannelServerNode) c.getServerNode());
        final CompletableFuture<Optional<MigrationInfo>> migrationFuture = channelServerNode.submitMigrationRequest(
                new MigrationInfo(channelServerNode.getChannelId(), accountId, characterId, machineId, clientKey)
        );
        final MigrationInfo migrationResult;
        try {
            final Optional<MigrationInfo> migrationFutureResult = migrationFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
            if (migrationFutureResult.isEmpty()) {
                log.error("Failed to retrieve migration result for character ID : {}", characterId);
                c.close();
                return;
            }
            migrationResult = migrationFutureResult.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Exception caught while waiting for migration result", e);
            e.printStackTrace();
            c.close();
            return;
        }

        // Verify migration request
        if (!migrationResult.verify(channelServerNode.getChannelId(), accountId, characterId, machineId, clientKey)) {
            log.error("Failed to verify migration request for character ID : {}", characterId);
            c.close();
            return;
        }

        // Check account
        final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountById(migrationResult.getAccountId());
        if (accountResult.isEmpty()) {
            log.error("Could not retrieve account with ID : {}", migrationResult.getAccountId());
            c.close();
            return;
        }
        final Account account = accountResult.get();
        if (channelServerNode.isConnected(account)) {
            log.error("Tried to connect to channel server while already connected");
            c.close();
            return;
        }
        account.setChannelId(channelServerNode.getChannelId());
        c.setAccount(account);

        // Check character
        final Optional<CharacterData> characterResult = DatabaseManager.characterAccessor().getCharacterById(characterId);
        if (characterResult.isEmpty()) {
            log.error("Could not retrieve character with ID : {}", characterId);
            c.close();
            return;
        }
        final CharacterData characterData = characterResult.get();
        if (characterData.getAccountId() != migrationResult.getAccountId()) {
            log.error("Mismatching account IDs {}, {}", characterData.getAccountId(), migrationResult.getAccountId());
            c.close();
            return;
        }
        final User user = new User(c, characterData);
        if (channelServerNode.isConnected(user)) {
            log.error("Tried to connect to channel server while already connected");
            c.close();
            return;
        }
        c.setUser(user);
        channelServerNode.addClient(c);
        channelServerNode.notifyUserConnect(user);

        try (var locked = user.acquire()) {
            // Initialize pets
            final CharacterStat cs = locked.get().getCharacterStat();
            initializePet(user, 0, cs.getPetSn1());
            initializePet(user, 1, cs.getPetSn2());
            initializePet(user, 2, cs.getPetSn3());

            // Initialize user stats
            user.getSecondaryStat().getTemporaryStats().putAll(migrationResult.getTemporaryStats());
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.setGender(user.getGender()));

            // Add user to field
            final int fieldId = user.getCharacterStat().getPosMap();
            final byte portalId = user.getCharacterStat().getPortal();
            final Field targetField;
            final Optional<Field> fieldResult = channelServerNode.getFieldById(fieldId);
            if (fieldResult.isPresent()) {
                targetField = fieldResult.get();
            } else {
                log.error("Could not retrieve field ID : {} for character ID : {}, moving to {}", fieldId, user.getCharacterId(), 100000000);
                targetField = channelServerNode.getFieldById(100000000).orElseThrow(() -> new IllegalStateException("Could not resolve Field from ChannelServer"));
            }
            final PortalInfo targetPortal;
            final Optional<PortalInfo> portalResult = targetField.getPortalById(portalId);
            if (portalResult.isPresent()) {
                targetPortal = portalResult.get();
            } else {
                log.error("Tried to warp to portal : {} on field ID : {}", 0, targetField.getFieldId());
                targetPortal = targetField.getPortalById(0).orElseThrow(() -> new IllegalStateException("Could not resolve Portal"));
            }

            // Set field packet sent here
            user.warp(targetField, targetPortal, true, false);


            // Initialize func keys and quickslot
            final FuncKeyManager fkm = user.getFuncKeyManager();
            user.write(FieldPacket.funcKeyMappedInit(fkm.getFuncKeyMap()));
            user.write(FieldPacket.quickSlotMappedInit(fkm.getQuickslotKeyMap()));
            user.write(FieldPacket.petConsumeItemInit(fkm.getPetConsumeItem()));
            user.write(FieldPacket.petConsumeMpItemInit(fkm.getPetConsumeMpItem()));

            // Check memos
            final boolean hasMemo = DatabaseManager.memoAccessor().hasMemo(user.getCharacterId());
            if (hasMemo) {
                user.write(WvsContext.memoResult(MemoResult.receive()));
            }

            // TODO: update friends, family, guild, party
        }
    }

    @Handler(InHeader.USER_TRANSFER_FIELD_REQUEST)
    public static void handleUserTransferFieldRequest(User user, InPacket inPacket) {
        // Returning from CashShop
        if (inPacket.getRemaining() == 0) {
            handleTransfer(user, user.getAccount().getChannelId());
            return;
        }

        // Normal transfer field request
        final byte fieldKey = inPacket.decodeByte();
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final int targetField = inPacket.decodeInt(); // dwTargetField
        final String portalName = inPacket.decodeString(); // sPortal
        if (!portalName.isEmpty()) {
            inPacket.decodeShort();
            inPacket.decodeShort();
        }
        inPacket.decodeByte(); // 0
        inPacket.decodeByte(); // bPremium
        inPacket.decodeByte(); // bChase -> int, int

        try (var locked = user.acquire()) {
            final boolean isRevive = portalName.isEmpty() && user.getHp() == 0;
            final int nextFieldId;
            final String nextPortalName;
            if (portalName.isEmpty()) {
                if (!isRevive && targetField != user.getField().getReturnMap()) {
                    log.error("Tried to return to field : {} from field : {}", targetField, user.getField().getFieldId());
                    user.dispose();
                    return;
                }
                if (isRevive) {
                    // Handle revive
                    user.getSecondaryStat().clear();
                    user.updatePassiveSkillData();
                    user.validateStat();
                    user.setHp(50);
                    user.write(WvsContext.statChanged(Stat.HP, user.getHp(), true));
                }
                nextFieldId = user.getField().getReturnMap();
                nextPortalName = "sp"; // spawn point
            } else {
                // Handle portal name
                final Field currentField = user.getField();
                final Optional<PortalInfo> portalResult = currentField.getPortalByName(portalName);
                if (portalResult.isEmpty() || !portalResult.get().hasDestinationField()) {
                    log.error("Tried to use portal : {} on field ID : {}", portalName, currentField.getFieldId());
                    user.dispose();
                    return;
                }
                final PortalInfo portal = portalResult.get();
                nextFieldId = portal.getDestinationFieldId();
                nextPortalName = portal.getDestinationPortalName();
            }

            // Move User to Field
            final Optional<Field> nextFieldResult = user.getConnectedServer().getFieldById(nextFieldId);
            if (nextFieldResult.isEmpty()) {
                user.write(FieldPacket.transferFieldReqIgnored(TransferFieldType.NOT_CONNECTED_AREA)); // You cannot go to that place
                return;
            }
            final Field nextField = nextFieldResult.get();
            final Optional<PortalInfo> nextPortalResult = nextField.getPortalByName(nextPortalName);
            if (nextPortalResult.isEmpty()) {
                log.error("Tried to warp to portal : {} on field ID : {}", nextPortalName, nextField.getFieldId());
                user.dispose();
                return;
            }
            user.warp(nextField, nextPortalResult.get(), false, isRevive);
        }
    }

    @Handler(InHeader.USER_TRANSFER_CHANNEL_REQUEST)
    public static void handleUserTransferChannelRequest(User user, InPacket inPacket) {
        final byte channelId = inPacket.decodeByte();
        inPacket.decodeInt(); // update_time
        handleTransfer(user, channelId);
    }

    @Handler(InHeader.USER_MIGRATE_TO_CASHSHOP_REQUEST)
    public static void handleUserMigrateToCashShopRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time

        // Load gifts
        final List<Gift> gifts = DatabaseManager.giftAccessor().getGiftsByCharacterId(user.getCharacterId());

        try (var lockedAccount = user.getAccount().acquire()) {
            final Account account = lockedAccount.get();

            // Load cash shop
            user.write(StagePacket.setCashShop(user));
            user.write(CashShopPacket.cashItemResult(CashItemResult.loadGiftDone(gifts)));
            user.write(CashShopPacket.cashItemResult(CashItemResult.loadLockerDone(account)));
            user.write(CashShopPacket.cashItemResult(CashItemResult.loadWishDone(account.getWishlist())));
            user.write(CashShopPacket.queryCashResult(account));
        }
    }

    private static void handleTransfer(User user, int channelId) {
        // Force character save
        DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());
        DatabaseManager.accountAccessor().saveAccount(user.getAccount());

        // Submit transfer request
        final Client c = user.getClient();
        final MigrationInfo migrationInfo = new MigrationInfo(
                channelId,
                user.getAccountId(),
                user.getCharacterId(),
                c.getMachineId(),
                c.getClientKey(),
                user.getSecondaryStat().getTemporaryStats()
        );
        final CompletableFuture<Optional<TransferInfo>> transferFuture = user.getConnectedServer().submitTransferRequest(migrationInfo);
        final TransferInfo transferResult;
        try {
            final Optional<TransferInfo> transferFutureResult = transferFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
            if (transferFutureResult.isEmpty()) {
                log.error("Failed to retrieve transfer result for character ID : {}", user.getCharacterId());
                c.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
                c.close();
                return;
            }
            transferResult = transferFutureResult.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Exception caught while waiting for transfer result", e);
            c.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
            e.printStackTrace();
            return;
        }

        // Send migrate command
        user.write(ClientPacket.migrateCommand(transferResult.getChannelHost(), transferResult.getChannelPort()));
    }

    private static void initializePet(User user, int petIndex, long petSn) {
        if (petSn == 0) {
            return;
        }
        final Inventory cashInventory = user.getInventoryManager().getCashInventory();
        final Optional<Map.Entry<Integer, Item>> itemEntryResult = cashInventory.getItems().entrySet().stream()
                .filter((entry) -> entry.getValue().getItemSn() == petSn)
                .findFirst();
        if (itemEntryResult.isEmpty()) {
            // Pet item not found
            user.setPetSn(petIndex, 0, true);
            return;
        }
        final Item item = itemEntryResult.get().getValue();
        if (item.getItemType() != ItemType.PET || item.getDateExpire().isBefore(Instant.now())) {
            // Invalid pet or expired
            user.setPetSn(petIndex, 0, true);
            return;
        }
        // Create pet and assign to user
        final Pet pet = Pet.from(user, item);
        user.addPet(pet, true);
    }
}
