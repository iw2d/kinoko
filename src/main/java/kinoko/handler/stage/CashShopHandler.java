package kinoko.handler.stage;

import kinoko.handler.Handler;
import kinoko.packet.stage.CashShopPacket;
import kinoko.server.cashshop.CashItemRequestType;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.Account;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CashShopHandler {
    private static final Logger log = LogManager.getLogger(CashShopHandler.class);

    @Handler(InHeader.CASHSHOP_QUERY_CASH_REQUEST)
    public static void handleQueryCashRequest(User user, InPacket inPacket) {
        try (var locked = user.getAccount().acquire()) {
            final Account account = user.getAccount();
            user.write(CashShopPacket.queryCashResult(
                    account.getNxCredit(),
                    account.getNxPrepaid(),
                    account.getMaplePoint()
            ));
        }
    }

    @Handler(InHeader.CASHSHOP_CASH_ITEM_REQUEST)
    public static void handleCashItemRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final CashItemRequestType requestType = CashItemRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown cash item request type : {}", type);
            return;
        }
        switch (requestType) {
            case BUY -> {
                // CCashShop::OnBuy
                // CCashShop::SendBuyAvatarPacket
                final boolean isMaplePoint = inPacket.decodeBoolean();
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN
                // These two encodes are swapped for OnBuy and SendBuyAvatarPacket - ignore
                inPacket.decodeByte(); // bRequestBuyOneADay
                inPacket.decodeInt(); // nEventSN
            }
            case GIFT -> {
                // CCashShop::SendGiftsPacket
                // CCashShop::GiftWishItem
                final String secondaryPassword = inPacket.decodeString();
                final int commodityId = inPacket.decodeInt(); // nCommSN
                // This byte is only encoded in CCashShop::SendGiftsPacket, and not CCashShop::GiftWishItem
                // Should be able to differentiate them by checking if 0
                if (inPacket.peekByte() == 0) {
                    inPacket.decodeByte(); // bRequestBuyOneADay
                }
                final String name = inPacket.decodeString();
                final String text = inPacket.decodeString();
            }
            case SET_WISH -> {
                // CCashShop::OnSetWish
                // CCashShop::OnRemoveWish
                final int[] wishlist = new int[10];
                for (int i = 0; i < 10; i++) {
                    wishlist[i] = inPacket.decodeInt(); // nCommSN
                }
            }
            case INC_SLOT_COUNT, INC_TRUNK_COUNT -> {
                // CCashShop::OnBuySlotInc
                // CCashShop::OnIncTrunkCount
                final boolean isMaplePoint = inPacket.decodeBoolean();
                final int paymentType = inPacket.decodeInt(); // dwOption
                final boolean isAdd4Slots = inPacket.decodeInt() == 0;
                if (isAdd4Slots) {
                } else {
                    final int commodityId = inPacket.decodeInt(); // nCommSN
                }
            }
            case INC_CHAR_SLOT_COUNT -> {
                // CCashShop::OnIncCharacterSlotCount
                final boolean isMaplePoint = inPacket.decodeBoolean();
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN
            }
            case ENABLE_EQUIP_SLOT_EXT -> {
                // CCashShop::OnEnableEquipSlotExt
                final boolean isMaplePoint = inPacket.decodeBoolean(); // nx credit or maple point onlyz
                final int commodityId = inPacket.decodeInt(); // nCommSN
            }
        }
    }
}
