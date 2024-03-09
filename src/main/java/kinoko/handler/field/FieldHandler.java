package kinoko.handler.field;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.ClientPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.TransferChannelType;
import kinoko.packet.field.TransferFieldType;
import kinoko.packet.stage.CashShopPacket;
import kinoko.packet.stage.StagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.broadcast.BroadcastMessage;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ChannelServer;
import kinoko.server.Server;
import kinoko.server.cashshop.*;
import kinoko.server.client.MigrationRequest;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.Account;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(InHeader.USER_TRANSFER_FIELD_REQUEST)
    public static void handleUserTransferFieldRequest(User user, InPacket inPacket) {
        // Returning from CashShop
        if (inPacket.getRemaining() == 0) {
            migrateToChannelServer(user, user.getConnectedServer());
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
                if (targetField != user.getField().getReturnMap()) {
                    log.error("Tried to return to field : {} from field : {}", targetField, user.getField().getFieldId());
                    user.dispose();
                    return;
                }
                if (isRevive) {
                    // Handle revive
                    // TODO remove CTS, etc
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

        final Account account = user.getAccount();
        final Optional<ChannelServer> channelResult = Server.getChannelServerById(account.getWorldId(), channelId);
        if (channelResult.isEmpty()) {
            user.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
            return;
        }
        migrateToChannelServer(user, channelResult.get());
    }

    @Handler(InHeader.USER_MIGRATE_TO_CASHSHOP_REQUEST)
    public static void handleUserMigrateToCashShopRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time

        // Load gifts
        final List<Gift> gifts = DatabaseManager.giftAccessor().getGiftsByAccountId(user.getAccountId());

        try (var lockedAccount = user.getAccount().acquire()) {
            // Load cash shop
            user.write(StagePacket.setCashShop(user));

            // Add gifts to locker
            final Account account = lockedAccount.get();
            final Locker locker = account.getLocker();
            boolean lockerFull = false;
            final var iter = gifts.iterator();
            while (iter.hasNext()) {
                final Gift gift = iter.next();
                if (locker.getRemaining() < 1) {
                    // Locker is full, gift will stay in DB for next migration to cash shop
                    lockerFull = true;
                    iter.remove();
                    continue;
                }
                // Delete gift from DB and add to locker
                if (!DatabaseManager.giftAccessor().deleteGift(gift)) {
                    log.error("Failed to delete gift with item sn : {}", gift.getItemSn());
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.LOAD_GIFT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    return;
                }
                final CashItemInfo cashItemInfo = CashItemInfo.from(gift.getItem(), user);
                locker.addCashItem(cashItemInfo);
            }

            // Update client
            user.write(CashShopPacket.cashItemResult(CashItemResult.loadGiftDone(gifts)));
            user.write(CashShopPacket.cashItemResult(CashItemResult.loadLockerDone(account)));
            user.write(CashShopPacket.cashItemResult(CashItemResult.loadWishDone(account.getWishlist())));
            user.write(CashShopPacket.queryCashResult(account));

            // Locker full message
            if (lockerFull) {
                user.write(WvsContext.broadcastMsg(BroadcastMessage.alert("Could not receive some gifts due to the locker being full.")));
            }
        }
    }

    @Handler(InHeader.CANCEL_INVITE_PARTY_MATCH)
    public static void handleCancelInvitePartyMatch(User user, InPacket inPacket) {
    }

    private static void migrateToChannelServer(User user, ChannelServer channelServer) {
        // Force character save
        DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());
        DatabaseManager.accountAccessor().saveAccount(user.getAccount());
        // Submit migration request
        final Optional<MigrationRequest> mrResult = Server.submitMigrationRequest(user.getClient(), channelServer, user.getCharacterId());
        if (mrResult.isEmpty()) {
            log.error("Failed to submit migration request for character ID : {}", user.getCharacterId());
            user.write(FieldPacket.transferChannelReqIgnored(TransferChannelType.GAMESVR_DISCONNECTED)); // Cannot move to that Channel
            return;
        }
        // Send migrate command
        user.write(ClientPacket.migrateCommand(channelServer.getAddress(), channelServer.getPort()));
    }
}
