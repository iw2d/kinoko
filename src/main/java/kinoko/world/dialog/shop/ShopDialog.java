package kinoko.world.dialog.shop;

import kinoko.packet.world.DialogPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.ShopProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Locked;
import kinoko.world.Encodable;
import kinoko.world.GameConstants;
import kinoko.world.dialog.Dialog;
import kinoko.world.dialog.trunk.TrunkDialog;
import kinoko.world.dialog.trunk.TrunkResult;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class ShopDialog implements Dialog, Encodable {
    private static final Logger log = LogManager.getLogger(TrunkDialog.class);
    private final Npc npc;
    private final List<ShopItem> items;

    public ShopDialog(Npc npc, List<ShopItem> items) {
        this.npc = npc;
        this.items = items;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CShopDlg::SetShopDlg
        outPacket.encodeInt(npc.getTemplateId()); // dwNpcTemplateID
        outPacket.encodeShort(items.size()); // nCount
        for (ShopItem item : items) {
            item.encode(outPacket);
        }
    }

    @Override
    public void onPacket(Locked<User> locked, InPacket inPacket) {
        final User user = locked.get();
        final int type = inPacket.decodeByte();
        final ShopRequestType requestType = ShopRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown shop request type {}", type);
            return;
        }
        switch (requestType) {
            case BUY -> {
                final int index = inPacket.decodeShort(); // nBuySelected
                final int itemId = inPacket.decodeInt(); // nItemID
                final int count = inPacket.decodeShort(); // nCount
                final int price = inPacket.decodeInt(); // DiscountPrice
                // Check buy request matches with shop data
                if (index >= items.size()) {
                    user.write(DialogPacket.shopResult(ShopResultType.SERVER_MSG));
                    return;
                }
                final ShopItem shopItem = items.get(index);
                if (shopItem.getItemId() != itemId || shopItem.getMaxPerSlot() < count || shopItem.getPrice() != price) {
                    user.write(DialogPacket.shopResult(ShopResultType.SERVER_MSG));
                    return;
                }
                // Check if user has enough money
                final long totalPrice = ((long) count) * price;
                if (totalPrice > GameConstants.MAX_MONEY) {
                    user.write(DialogPacket.shopResult(ShopResultType.BUY_NO_MONEY));
                    return;
                }
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddMoney((int) -totalPrice)) {
                    user.write(DialogPacket.shopResult(ShopResultType.BUY_NO_MONEY));
                    return;
                }
                // Check if user can add item to inventory
                if (im.getInventoryByItemId(itemId).getRemaining() == 0) {
                    user.write(DialogPacket.shopResult(ShopResultType.BUY_UNKNOWN));
                    return;
                }
                // Create item
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                if (itemInfoResult.isEmpty()) {
                    user.write(DialogPacket.shopResult(ShopResultType.SERVER_MSG));
                    return;
                }
                final Item boughtItem = itemInfoResult.get().createItem(user.getNextItemSn(), count);
                // Deduct money and add item to inventory
                if (!im.addMoney((int) -totalPrice)) {
                    throw new IllegalStateException("Could not deduct total price from user");
                }
                final Optional<List<InventoryOperation>> addResult = im.addItem(boughtItem);
                if (addResult.isEmpty()) {
                    throw new IllegalStateException("Could not add bought item to inventory");
                }
                // Update client
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
                user.write(WvsContext.inventoryOperation(addResult.get(), true));
                user.write(DialogPacket.shopResult(ShopResultType.BUY_SUCCESS));
            }
            case SELL -> {
                final int position = inPacket.decodeShort(); // nPOS
                final int itemId = inPacket.decodeInt(); // nItemID
                final int count = inPacket.decodeShort(); // nCount
                // Check if sell request possible
                final InventoryManager im = user.getInventoryManager();
                final Inventory inventory = im.getInventoryByItemId(itemId);
                final Item sellItem = inventory.getItem(position);
                if (sellItem.getItemId() != itemId || sellItem.getQuantity() < count) {
                    user.write(DialogPacket.shopResult(ShopResultType.SERVER_MSG));
                    return;
                }
                // Resolve sell price
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                if (itemInfoResult.isEmpty()) {
                    user.write(DialogPacket.shopResult(ShopResultType.SERVER_MSG));
                    return;
                }
                final int price = itemInfoResult.get().getPrice();
                final long totalPrice = ((long) price) * count;
                // Check if user can add money
                if (!im.canAddMoney((int) totalPrice)) {
                    user.write(DialogPacket.trunkResult(TrunkResult.message("You cannot hold any more mesos.")));
                    return;
                }
                // Remove items and add money
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, sellItem, count);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException("Could not remove item from inventory");
                }
                if (!im.addMoney((int) totalPrice)) {
                    throw new IllegalStateException("Could not add money to inventory");
                }
                // Update client
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                user.write(DialogPacket.shopResult(ShopResultType.SELL_SUCCESS));
            }
            case RECHARGE -> {
                inPacket.decodeShort(); // nPos
                // TODO
            }
            case CLOSE -> {
                user.closeDialog();
            }
        }
    }

    public static ShopDialog from(Npc npc) {
        final List<ShopItem> items = ShopProvider.getNpcShopItems(npc);
        return new ShopDialog(npc, items);
    }
}
