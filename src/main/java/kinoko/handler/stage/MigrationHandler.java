package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.handler.user.FriendHandler;
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
import kinoko.provider.MapProvider;
import kinoko.provider.map.PortalInfo;
import kinoko.server.cashshop.Gift;
import kinoko.server.field.InstanceFieldStorage;
import kinoko.server.guild.GuildRequest;
import kinoko.server.header.InHeader;
import kinoko.server.memo.Memo;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.Client;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.server.party.PartyRequest;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.user.CharacterData;
import kinoko.world.user.*;
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.CharacterTemporaryStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        channelServerNode.submitMigrationRequest(accountId, characterId, machineId, clientKey, (migrationResult) -> {
            if (migrationResult.isEmpty()) {
                log.error("Failed to retrieve migration result for character ID : {}", characterId);
                c.close();
                return;
            }
            // Load account
            final MigrationInfo migrationInfo = migrationResult.get();
            final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountById(migrationInfo.getAccountId());
            if (accountResult.isEmpty()) {
                log.error("Could not retrieve account with ID : {}", migrationInfo.getAccountId());
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
            if (characterData.getAccountId() != migrationInfo.getAccountId()) {
                log.error("Mismatching account IDs {}, {}", characterData.getAccountId(), migrationInfo.getAccountId());
                c.close();
                return;
            }

            // Initialize User
            final User user = new User(c, characterData);
            user.setMessengerId(migrationInfo.getMessengerId()); // this is required before user connect
            if (channelServerNode.isConnected(user)) {
                log.error("Tried to connect to channel server while already connected");
                c.close();
                return;
            }
            c.setUser(user);
            channelServerNode.addClient(c);
            channelServerNode.notifyUserConnect(user);

            // Initialize pets
            final CharacterStat cs = user.getCharacterStat();
            final long[] pets = new long[]{
                    cs.getPetSn1(), cs.getPetSn2(), cs.getPetSn3()
            };
            cs.setPetSn1(0);
            cs.setPetSn2(0);
            cs.setPetSn3(0);
            // Resolve pets
            final Inventory cashInventory = user.getInventoryManager().getCashInventory();
            for (long petSn : pets) {
                final Optional<Map.Entry<Integer, Item>> itemEntryResult = cashInventory.getItems().entrySet().stream()
                        .filter((entry) -> entry.getValue().getItemSn() == petSn)
                        .findFirst();
                if (itemEntryResult.isEmpty()) {
                    // Item not found
                    continue;
                }
                final Item item = itemEntryResult.get().getValue();
                if (item.getItemType() != ItemType.PET || item.getDateExpire().isBefore(Instant.now())) {
                    // Invalid pet or expired
                    continue;
                }
                // Create pet and assign to user
                final Pet pet = Pet.from(user, item);
                user.addPet(pet, true);
            }

            // Initialize dragon
            if (JobConstants.isDragonJob(user.getJob())) {
                user.setDragon(new Dragon(user.getJob()));
            }

            // Initialize user data from MigrationInfo
            user.getSecondaryStat().getTemporaryStats().putAll(migrationInfo.getTemporaryStats());
            user.getSkillManager().getSkillSchedules().putAll(migrationInfo.getSchedules());
            user.getSummoned().putAll(migrationInfo.getSummoned());
            user.setEffectItemId(migrationInfo.getEffectItemId());
            user.setAdBoard(migrationInfo.getAdBoard());
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.setGender(user.getGender()));
            user.write(WvsContext.resetTownPortal());

            // Resolve user field
            final int fieldId = user.getCharacterStat().getPosMap();
            final byte portalId = user.getCharacterStat().getPortal();
            final Optional<Field> fieldResult = channelServerNode.getFieldById(fieldId);
            final Field targetField = fieldResult.orElseGet(() -> {
                log.error("Could not retrieve field ID : {} for character ID : {}, moving to {}", fieldId, user.getCharacterId(), 100000000);
                return channelServerNode.getFieldById(100000000).orElseThrow(() -> new IllegalStateException("Could not resolve Field from ChannelServer"));
            });
            final Optional<PortalInfo> portalResult = targetField.getPortalById(portalId);
            final PortalInfo targetPortal = portalResult.orElseGet(() -> {
                log.error("Could not resolve portal : {} on field ID : {}", portalId, targetField.getFieldId());
                return targetField.getPortalById(0).orElse(PortalInfo.EMPTY);
            });

            // Add user to field
            ServerExecutor.submit(targetField, () -> {
                // Set field packet sent here
                user.warp(targetField, targetPortal, true, false);

                // Initialize func keys and quickslot
                final ConfigManager cm = user.getConfigManager();
                user.write(WvsContext.macroSysDataInit(cm.getMacroSysData()));
                user.write(FieldPacket.funcKeyMappedInit(cm.getFuncKeyMap()));
                user.write(FieldPacket.quickslotMappedInit(cm.getQuickslotKeyMap()));
                user.write(FieldPacket.petConsumeItemInit(cm.getPetConsumeItem()));
                user.write(FieldPacket.petConsumeMpItemInit(cm.getPetConsumeMpItem()));

                // Load messenger from central server
                if (user.getMessengerId() != 0) {
                    channelServerNode.submitMessengerRequest(user, MessengerRequest.migrated());
                }

                // Load party from central server
                final int partyId = user.getCharacterData().getPartyId();
                if (partyId != 0) {
                    channelServerNode.submitPartyRequest(user, PartyRequest.loadParty(partyId));
                }

                // Load guild from central server
                final int guildId = user.getCharacterData().getGuildId();
                if (guildId != 0) {
                    channelServerNode.submitGuildRequest(user, GuildRequest.loadGuild(guildId));
                }

                // Load memos
                final List<Memo> memos = DatabaseManager.memoAccessor().getMemosByCharacterId(user.getCharacterId());
                if (!memos.isEmpty()) {
                    user.write(MemoPacket.load(memos));
                }

                // Load friends
                FriendHandler.loadFriends(user, (friendMap) -> {
                    user.write(FriendPacket.loadFriendDone(friendMap.values()));
                    final List<Integer> friendIds = friendMap.values().stream()
                            .filter((friend) -> friend.getStatus() == FriendStatus.NORMAL)
                            .map(Friend::getFriendId)
                            .toList();
                    if (!friendIds.isEmpty()) {
                        user.getConnectedServer().submitUserPacketBroadcast(friendIds, FriendPacket.notify(user.getCharacterId(), user.getChannelId(), false));
                    }
                });
            });
        });
    }

    @Handler(InHeader.UserTransferFieldRequest)
    public static void handleUserTransferFieldRequest(User user, InPacket inPacket) {
        // Returning from CashShop
        if (inPacket.getRemaining() == 0) {
            handleTransferChannel(user, user.getAccount(), user.getAccount().getChannelId());
            return;
        }

        // Normal transfer field request
        final byte fieldKey = inPacket.decodeByte();
        if (user.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final int targetFieldId = inPacket.decodeInt(); // dwTargetField
        final String portalName = inPacket.decodeString(); // sPortal
        if (!portalName.isEmpty()) {
            // TODO: validate distance relative to server-side position and portal position
            inPacket.decodeShort(); // GetPos()->x
            inPacket.decodeShort(); // GetPos()->y
        }
        inPacket.decodeByte(); // 0
        final boolean premium = inPacket.decodeBoolean(); // bPremium - also set as true on normal revives, probably a client bug in CUIRevive::OnCreate
        final boolean chase = inPacket.decodeBoolean(); // bChase
        if (chase) {
            inPacket.decodeInt(); // nTargetPosition_X
            inPacket.decodeInt(); // nTargetPosition_Y
        }

        final Field currentField = user.getField();
        if (portalName.isEmpty()) {
            if (user.getHp() <= 0) {
                // Premium revive
                if (premium) {
                    if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.SoulStone)) {
                        // user.resetTemporaryStat(Set.of(CharacterTemporaryStat.SoulStone)); - SecondaryStat cleared on revive
                        user.write(UserLocal.effect(Effect.soulStoneUse())); // You have revived on the current map through the effect of the Spirit Stone.
                        handleRevive(user, currentField, true);
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
                        handleRevive(user, currentField, true);
                        return;
                    }
                }
                // Normal revive
                handleRevive(user, currentField, false);
                return;
            }
            // Transfer field by client request : ReservedEffect, CField::OBSTACLE, /m <map ID> - TODO: disallow /m command for non-GM
            if (!isWhitelistedTransferField(currentField.getFieldId(), targetFieldId)) {
                log.warn("Received non-whitelisted transfer field request from {} to {}", currentField.getFieldId(), targetFieldId);
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

    @Handler(InHeader.UserTransferChannelRequest)
    public static void handleUserTransferChannelRequest(User user, InPacket inPacket) {
        final byte channelId = inPacket.decodeByte();
        inPacket.decodeInt(); // update_time

        handleTransferChannel(user, user.getAccount(), channelId);
    }

    @Handler(InHeader.UserMigrateToCashShopRequest)
    public static void handleUserMigrateToCashShopRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time

        // Remove user from field
        user.getField().removeUser(user);

        // Load gifts
        final List<Gift> gifts = DatabaseManager.giftAccessor().getGiftsByCharacterId(user.getCharacterId());

        // Load cash shop
        final Account account = user.getAccount();
        user.write(StagePacket.setCashShop(user));
        user.write(CashShopPacket.loadGiftDone(gifts));
        user.write(CashShopPacket.loadLockerDone(account));
        user.write(CashShopPacket.loadWishDone(account.getWishlist()));
        user.write(CashShopPacket.queryCashResult(account));
    }

    private static boolean isWhitelistedTransferField(int currentFieldId, int targetFieldId) {
        final int sourceFieldId = MapProvider.getMapLink(currentFieldId).orElse(currentFieldId);
        final int whitelistedFieldId = switch (sourceFieldId) {
            // Witch Tower : Witch Tower Entrance
            case 980040000, 980041000, 980041100, 980042000, 980042100, 980043000, 980043100, 980044000, 980044100 ->
                    980040000;
            // Effect/Direction.img/cygnus/Scene%d - unused
            // Effect/Direction.img/cygnusJobTutorial/Scene%d (cygnusJobTutorial)
            case 913040100 -> 913040101;
            case 913040101 -> 913040102;
            case 913040102 -> 913040103;
            case 913040103 -> 913040104;
            case 913040104 -> 913040105;
            case 913040105 -> 913040106;
            case 913040106 -> 130000000;
            // Effect/Direction1.img/aranDirection/Scene%d -> unused
            // Effect/Direction1.img/aranTutorial/* (aranDirection)
            case 914090010 -> 914090011;
            case 914090011 -> 914090012;
            case 914090012 -> 914090013;
            case 914090013 -> 140090000;
            case 914090100 -> 140000000;
            case 914090200 -> 140000000;
            case 914090201 -> 140030000;
            // Effect/Direction2.img/open/out (TD_MC_Openning)
            case 106020001 -> 106020000;
            // Effect/Direction2.img/gasi/gasi6 (TD_MC_gasi)
            case 106020502 -> 106020501;
            // Effect/Direction3.img/goAdventure/Scene%d (goAdventure)
            case 0 -> 10000;
            // Effect/Direction3.img/%s/Scene%d (goSwordman/goMagician/goArcher/goRogue/goPirate)
            case 1020100, 1020200, 1020300, 1020400, 1020500 -> 1020000;
            // Effect/Direction3.img/goLith/Scene%d (goLith)
            case 2010000 -> 104000000;
            // Effect/Direction4.img/promotion/Scene%d - unused
            // Effect/Direction4.img/meetWithDragon/Scene%d (meetWithDragon)
            case 900090100 -> 900010200;
            // Effect/Direction4.img/PromiseDragon/Scene0 (PromiseDragon)
            case 900090101 -> 100030100;
            // Effect/Direction4.img/crash/Scene%d (crash_Dragon)
            case 900090102 -> 900020200;
            // Effect/Direction4.img/getDragonEgg/Scene%d (getDragonEgg)
            case 900090103 -> 900020110;
            // Effect/Direction4.img/Resistance/TalkInLab (talk2159012)
            case 931000011 -> 931000012;
            // Effect/Direction4.img/Resistance/TalkJ (Resi_tutor70)
            case 931000021 -> 931000030;
            // Effect/DirectionVisitor.img/visitor/Basic -> 502050001
            // Effect/DirectionVisitor.img/visitor/BingCube -> 502050000
            // Effect/DirectionVisitor.img/visitor/BlackHole -> 502050000
            // Effect/DirectionVisitor.img/visitor/Shuttle -> 502050000
            // Effect/DirectionVisitor.img/visitor/Stage1 -> 502050001
            // Effect/DirectionVisitor.img/visitor/Stage2 -> 502050001
            // Effect/DirectionVisitor.img/visitor/Stage3 -> 502050001
            // Effect/DirectionVisitor.img/visitor/Stage4 -> 502050001
            // Effect/DirectionVisitor.img/visitor/TimeTravel -> 502050000
            default -> -1;
        };
        return targetFieldId == whitelistedFieldId;
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

    private static void handleRevive(User user, Field field, boolean premium) {
        user.getSecondaryStat().clear();
        user.getSummoned().clear();
        user.updatePassiveSkillData();
        user.validateStat();
        if (premium) {
            user.setHp(user.getMaxHp());
            user.setMp(user.getMaxMp());
            handleTransferField(user, field.getFieldId(), GameConstants.DEFAULT_PORTAL_NAME, true, false);
        } else {
            final int returnMap = field.getFieldStorage() instanceof InstanceFieldStorage instanceFieldStorage ?
                    instanceFieldStorage.getInstance().getReturnMap() :
                    field.getReturnMap();
            user.setHp(50);
            handleTransferField(user, returnMap, GameConstants.DEFAULT_PORTAL_NAME, true, true);
        }
    }

    private static void handleTransferChannel(User user, Account account, int targetChannelId) {
        // Submit transfer request
        final MigrationInfo migrationInfo = MigrationInfo.from(user, targetChannelId);
        user.getConnectedServer().submitTransferRequest(migrationInfo, (transferResult) -> {
            if (transferResult.isEmpty()) {
                log.error("Failed to retrieve transfer result for character ID : {}", user.getCharacterId());
                user.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
                return;
            }
            // Logout user and save
            user.logout(false);
            user.setInTransfer(true);
            DatabaseManager.accountAccessor().saveAccount(account);
            DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());

            // Send migrate command
            final TransferInfo transferInfo = transferResult.get();
            user.write(ClientPacket.migrateCommand(transferInfo.getChannelHost(), transferInfo.getChannelPort()));
        });
    }
}
