package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.ClientPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.TransferChannelType;
import kinoko.packet.field.TransferFieldType;
import kinoko.packet.stage.CashShopPacket;
import kinoko.packet.stage.StagePacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.world.FriendPacket;
import kinoko.packet.world.MemoPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.Gift;
import kinoko.server.header.InHeader;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.Client;
import kinoko.server.packet.InPacket;
import kinoko.server.party.PartyRequest;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.friend.Friend;
import kinoko.world.friend.FriendManager;
import kinoko.world.friend.FriendResultType;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.user.CharacterData;
import kinoko.world.user.*;
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
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

    @Handler(InHeader.MigrateIn)
    public static void handleMigrateIn(Client c, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        inPacket.decodeBoolean(); // CWvsContext->m_nSubGradeCode >> 7
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

        // Send migrate request to central server
        final ChannelServerNode channelServerNode = ((ChannelServerNode) c.getServerNode());
        final CompletableFuture<Optional<MigrationInfo>> migrationFuture = channelServerNode.submitMigrationRequest(accountId, characterId, machineId, clientKey);
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

        // Load account
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

        // Load character data
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

        // Initialize User
        final User user = new User(c, characterData);
        user.setMessengerId(migrationResult.getMessengerId()); // this is required before user connect
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
            initializePet(user, 0, user.getCharacterStat().getPetSn1());
            initializePet(user, 1, user.getCharacterStat().getPetSn2());
            initializePet(user, 2, user.getCharacterStat().getPetSn3());

            // Initialize dragon
            if (JobConstants.isDragonJob(user.getJob())) {
                user.setDragon(new Dragon(user));
            }

            // Initialize user data from MigrationInfo
            user.getSecondaryStat().getTemporaryStats().putAll(migrationResult.getTemporaryStats());
            user.getSkillManager().getSkillSchedules().putAll(migrationResult.getSchedules());
            user.getSummoned().putAll(migrationResult.getSummoned());
            user.setEffectItemId(migrationResult.getEffectItemId());
            user.setAdBoard(migrationResult.getAdBoard());
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.setGender(user.getGender()));
            user.write(WvsContext.resetTownPortal());

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
                log.error("Could not resolve default portal : {} on field ID : {}", 0, targetField.getFieldId());
                targetPortal = targetField.getPortalById(0).orElse(PortalInfo.EMPTY);
            }

            // Set field packet sent here
            user.warp(targetField, targetPortal, true, false);

            // Initialize func keys and quickslot
            final ConfigManager cm = user.getConfigManager();
            user.write(WvsContext.macroSysDataInit(cm.getMacroSysData()));
            user.write(FieldPacket.funcKeyMappedInit(cm.getFuncKeyMap()));
            user.write(FieldPacket.quickslotMappedInit(cm.getQuickslotKeyMap()));
            user.write(FieldPacket.petConsumeItemInit(cm.getPetConsumeItem()));
            user.write(FieldPacket.petConsumeMpItemInit(cm.getPetConsumeMpItem()));

            // Load friends from database and central server
            FriendManager.updateFriendsFromDatabase(user);
            FriendManager.updateFriendsFromCentralServer(user, FriendResultType.LoadFriend_Done, true); // notify

            // Process friend requests
            for (Friend friend : user.getFriendManager().getFriendRequests()) {
                user.write(FriendPacket.invite(friend));
            }

            // Load party from central server
            channelServerNode.submitPartyRequest(user, PartyRequest.loadParty());

            // Load messenger from central server
            channelServerNode.submitMessengerRequest(user, MessengerRequest.migrated());

            // Check memos
            if (DatabaseManager.memoAccessor().hasMemo(user.getCharacterId())) {
                user.write(MemoPacket.receive());
            }
        }
    }

    @Handler(InHeader.UserTransferFieldRequest)
    public static void handleUserTransferFieldRequest(User user, InPacket inPacket) {
        // Returning from CashShop
        if (inPacket.getRemaining() == 0) {
            try (var locked = user.acquire()) {
                try (var lockedAccount = user.getAccount().acquire()) {
                    handleTransferChannel(user, user.getAccount(), user.getAccount().getChannelId());
                    return;
                }
            }
        }

        // Normal transfer field request
        final Field currentField = user.getField();
        final byte fieldKey = inPacket.decodeByte();
        if (currentField.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final int targetFieldId = inPacket.decodeInt(); // dwTargetField
        final String portalName = inPacket.decodeString(); // sPortal
        if (!portalName.isEmpty()) {
            inPacket.decodeShort();
            inPacket.decodeShort();
        }
        inPacket.decodeByte(); // 0
        final boolean premium = inPacket.decodeBoolean(); // bPremium - also set as true on normal revives, probably a client bug in CUIRevive::OnCreate
        final boolean chase = inPacket.decodeBoolean(); // bChase
        if (chase) {
            inPacket.decodeInt();
            inPacket.decodeInt();
        }

        try (var locked = user.acquire()) {
            if (portalName.isEmpty()) {
                if (user.getHp() <= 0) {
                    // Premium revive
                    if (premium) {
                        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.SoulStone)) {
                            // user.resetTemporaryStat(Set.of(CharacterTemporaryStat.SoulStone)); - SecondaryStat cleared on revive
                            user.write(UserLocal.effect(Effect.soulStoneUse())); // You have revived on the current map through the effect of the Spirit Stone.
                            handleRevive(user, true);
                            return;
                        } else if (user.getInventoryManager().hasItem(ItemConstants.WHEEL_OF_DESTINY, 1)) {
                            if (!currentField.isUpgradeTombUsable()) {
                                log.error("Tried to use wheel of destiny in a restricted field");
                                return;
                            }
                            final Optional<List<InventoryOperation>> removeResult = user.getInventoryManager().removeItem(ItemConstants.WHEEL_OF_DESTINY, 1);
                            if (removeResult.isEmpty()) {
                                throw new IllegalStateException("Failed to remove wheel of destiny from inventory");
                            }
                            user.write(WvsContext.inventoryOperation(removeResult.get(), false));
                            final int remain = user.getInventoryManager().getItemCount(ItemConstants.WHEEL_OF_DESTINY);
                            user.write(UserLocal.effect(Effect.upgradeTombItemUse(remain))); // You have used 1 Wheel of Destiny in order to revive at the current map. (%d left)
                            handleRevive(user, true);
                            return;
                        }
                    }
                    // Normal revive
                    handleRevive(user, false);
                    return;
                }
                // Return to field via client request (usually SquibEffect)
                if (targetFieldId != currentField.getReturnMap()) {
                    log.error("Tried to return to field : {} from field : {}", targetFieldId, currentField.getFieldId());
                    user.dispose();
                    return;
                }
                handleTransferField(user, targetFieldId, GameConstants.DEFAULT_PORTAL_NAME, false, false);
                return;
            }
            // Enter portal to next field
            final Optional<PortalInfo> portalResult = currentField.getPortalByName(portalName);
            if (portalResult.isEmpty() || !portalResult.get().hasDestinationField()) {
                log.error("Tried to use portal : {} on field ID : {}", portalName, currentField.getFieldId());
                user.dispose();
                return;
            }
            final PortalInfo portal = portalResult.get();
            handleTransferField(user, portal.getDestinationFieldId(), portal.getDestinationPortalName(), false, false);
        }
    }

    @Handler(InHeader.UserTransferChannelRequest)
    public static void handleUserTransferChannelRequest(User user, InPacket inPacket) {
        final byte channelId = inPacket.decodeByte();
        inPacket.decodeInt(); // update_time

        try (var locked = user.acquire()) {
            try (var lockedAccount = user.getAccount().acquire()) {
                handleTransferChannel(user, user.getAccount(), channelId);
            }
        }
    }

    @Handler(InHeader.UserMigrateToCashShopRequest)
    public static void handleUserMigrateToCashShopRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time

        try (var locked = user.acquire()) {
            // Remove user from field
            user.getField().removeUser(user);

            // Update friends
            user.getConnectedServer().submitUserPacketBroadcast(
                    user.getFriendManager().getBroadcastTargets(),
                    FriendPacket.notify(user.getCharacterId(), user.getChannelId(), true)
            );

            // Load gifts
            final List<Gift> gifts = DatabaseManager.giftAccessor().getGiftsByCharacterId(user.getCharacterId());

            try (var lockedAccount = user.getAccount().acquire()) {
                final Account account = lockedAccount.get();

                // Load cash shop
                user.write(StagePacket.setCashShop(user));
                user.write(CashShopPacket.loadGiftDone(gifts));
                user.write(CashShopPacket.loadLockerDone(account));
                user.write(CashShopPacket.loadWishDone(account.getWishlist()));
                user.write(CashShopPacket.queryCashResult(account));
            }
        }
    }

    private static void handleTransferChannel(User user, Account account, int targetChannelId) {
        // Submit transfer request
        final MigrationInfo migrationInfo = MigrationInfo.from(user, targetChannelId);
        final CompletableFuture<Optional<TransferInfo>> transferFuture = user.getConnectedServer().submitTransferRequest(migrationInfo);
        try {
            final Optional<TransferInfo> transferFutureResult = transferFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
            if (transferFutureResult.isEmpty()) {
                log.error("Failed to retrieve transfer result for character ID : {}", user.getCharacterId());
                user.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
                return;
            }
            final TransferInfo transferResult = transferFutureResult.get();

            // Logout user and save
            user.logout(false);
            user.setInTransfer(true);
            DatabaseManager.accountAccessor().saveAccount(account);
            DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());

            // Send migrate command
            user.write(ClientPacket.migrateCommand(transferResult.getChannelHost(), transferResult.getChannelPort()));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Exception caught while waiting for transfer result", e);
            user.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
            e.printStackTrace();
        }
    }

    private static void handleTransferField(User user, int fieldId, String portalName, boolean isRevive, boolean isLeaveInstance) {
        // Resolve Field from ChannelFieldStorage | InstanceFieldStorage
        final Field targetField;
        if (isLeaveInstance) {
            final Optional<Field> targetFieldResult = user.getConnectedServer().getFieldById(fieldId);
            if (targetFieldResult.isEmpty()) {
                log.error("Could not resolve field ID : {}", fieldId);
                user.write(FieldPacket.transferFieldReqIgnored(TransferFieldType.NOT_CONNECTED_AREA)); // You cannot go to that place.
                return;
            }
            targetField = targetFieldResult.get();
        } else {
            // Try resolving Field from InstanceFieldStorage
            final Optional<Field> instanceFieldResult = user.getField().getFieldStorage().getFieldById(fieldId);
            if (instanceFieldResult.isPresent()) {
                targetField = instanceFieldResult.get();
            } else {
                // Default to ChannelFieldStorage - leaving instance
                final Optional<Field> targetFieldResult = user.getConnectedServer().getFieldById(fieldId);
                if (targetFieldResult.isEmpty()) {
                    log.error("Could not resolve field ID : {}", fieldId);
                    user.write(FieldPacket.transferFieldReqIgnored(TransferFieldType.NOT_CONNECTED_AREA)); // You cannot go to that place.
                    return;
                }
                targetField = targetFieldResult.get();
            }
        }
        // Resolve Portal
        Optional<PortalInfo> targetPortalResult = targetField.getPortalByName(portalName);
        if (targetPortalResult.isEmpty()) {
            log.warn("Tried to warp to portal : {} on field ID : {}, defaulting to 0", portalName, targetField.getFieldId());
            targetPortalResult = targetField.getPortalById(0);
            if (targetPortalResult.isEmpty()) {
                log.error("Could not resolve default portal : {} on field ID : {}", 0, targetField.getFieldId());
                user.write(FieldPacket.transferFieldReqIgnored(TransferFieldType.NOT_CONNECTED_AREA)); // You cannot go to that place.
                user.dispose();
                return;
            }
        }
        user.warp(targetField, targetPortalResult.get(), false, isRevive);
    }

    private static void handleRevive(User user, boolean premium) {
        user.getSecondaryStat().clear();
        user.getSummoned().clear();
        user.updatePassiveSkillData();
        user.validateStat();
        user.setHp(50);
        if (premium) {
            user.setHp(user.getMaxHp());
            user.setMp(user.getMaxMp());
            handleTransferField(user, user.getField().getFieldId(), GameConstants.DEFAULT_PORTAL_NAME, true, false);
        } else {
            user.setHp(50);
            handleTransferField(user, user.getField().getReturnMap(), GameConstants.DEFAULT_PORTAL_NAME, true, true);
        }
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
