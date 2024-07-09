package kinoko.server.dialog.shop;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.ShopProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.skill.SkillStat;
import kinoko.server.dialog.Dialog;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Locked;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.job.cygnus.NightWalker;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class ShopDialog implements Dialog {
    private static final Logger log = LogManager.getLogger(ShopDialog.class);
    private final NpcTemplate npcTemplate;
    private final List<ShopItem> items;

    public ShopDialog(NpcTemplate npcTemplate, List<ShopItem> items) {
        this.npcTemplate = npcTemplate;
        this.items = items;
    }

    public void handlePacket(Locked<User> locked, InPacket inPacket) {
        final User user = locked.get();
        final int type = inPacket.decodeByte();
        final ShopRequestType requestType = ShopRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown shop request type {}", type);
            return;
        }
        switch (requestType) {
            case Buy -> {
                final int index = inPacket.decodeShort(); // nBuySelected
                final int itemId = inPacket.decodeInt(); // nItemID
                final int count = inPacket.decodeShort(); // nCount
                final int price = inPacket.decodeInt(); // DiscountPrice
                // Check buy request matches with shop data
                if (index >= items.size()) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                final ShopItem shopItem = items.get(index);
                if (shopItem.getItemId() != itemId || shopItem.getMaxPerSlot() < count || shopItem.getPrice() != price) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                // Check if user has enough money
                final long totalPrice = ((long) count) * price;
                if (totalPrice > GameConstants.MONEY_MAX) {
                    user.write(FieldPacket.shopResult(ShopResultType.BuyNoMoney)); // You do not have enough mesos.
                    return;
                }
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddMoney((int) -totalPrice)) {
                    user.write(FieldPacket.shopResult(ShopResultType.BuyNoMoney)); // You do not have enough mesos.
                    return;
                }
                // Check if user can add item to inventory
                if (!im.canAddItem(itemId, count)) {
                    user.write(FieldPacket.shopResult(ShopResultType.BuyUnknown)); // Please check if your inventory is full or not.
                    return;
                }
                // Create item
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                if (itemInfoResult.isEmpty()) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
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
                user.write(FieldPacket.shopResult(ShopResultType.BuySuccess));
            }
            case Sell -> {
                final int position = inPacket.decodeShort(); // nPOS
                final int itemId = inPacket.decodeInt(); // nItemID
                final int count = inPacket.decodeShort(); // nCount
                // Check if sell request possible
                final InventoryManager im = user.getInventoryManager();
                final Inventory inventory = im.getInventoryByItemId(itemId);
                final Item sellItem = inventory.getItem(position);
                if (sellItem.getItemId() != itemId || sellItem.getQuantity() < count) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                // Resolve sell price
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                if (itemInfoResult.isEmpty()) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                final int price = itemInfoResult.get().getPrice();
                final long totalPrice = ((long) price) * count;
                // Check if user can add money
                if (!im.canAddMoney((int) totalPrice)) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg, "You cannot hold any more mesos."));
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
                user.write(FieldPacket.shopResult(ShopResultType.SellSuccess));
            }
            case Recharge -> {
                final int position = inPacket.decodeShort(); // nPos
                final InventoryManager im = user.getInventoryManager();
                final Item item = im.getConsumeInventory().getItem(position);
                // Check if item is rechargeable
                if (!ItemConstants.isRechargeableItem(item.getItemId())) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                final Optional<ShopItem> shopItemResult = items.stream()
                        .filter((shopitem) -> shopitem.getItemId() == item.getItemId() && shopitem.getUnitPrice() > 0)
                        .findFirst();
                if (shopItemResult.isEmpty()) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                final ShopItem shopitem = shopItemResult.get();
                // Resolve item slot max
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
                if (itemInfoResult.isEmpty()) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                final int slotMax = itemInfoResult.get().getSlotMax() + getIncSlotMax(user, item.getItemId());
                if (item.getQuantity() >= slotMax) {
                    user.write(FieldPacket.shopResult(ShopResultType.ServerMsg)); // Due to an error, the trade did not happen.
                    return;
                }
                // Compute price and check if user has enough money
                final int delta = slotMax - item.getQuantity();
                final long totalPrice = (long) (delta * shopitem.getUnitPrice());
                if (totalPrice > GameConstants.MONEY_MAX) {
                    user.write(FieldPacket.shopResult(ShopResultType.RechargeNoMoney)); // You do not have enough mesos.
                    return;
                }
                if (!im.canAddMoney((int) -totalPrice)) {
                    user.write(FieldPacket.shopResult(ShopResultType.RechargeNoMoney)); // You do not have enough mesos.
                    return;
                }
                // Deduct money and recharge item
                if (!im.addMoney((int) -totalPrice)) {
                    throw new IllegalStateException("Could not deduct total price from user");
                }
                item.setQuantity((short) slotMax);
                // Update client
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
                user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, position, item.getQuantity()), true));
                user.write(FieldPacket.shopResult(ShopResultType.RechargeSuccess));
            }
            case Close -> {
                user.setDialog(null);
            }
        }
    }

    public void encode(OutPacket outPacket, User user) {
        // CShopDlg::SetShopDlg
        outPacket.encodeInt(npcTemplate.getId()); // dwNpcTemplateID
        outPacket.encodeShort(items.size()); // nCount
        for (ShopItem item : items) {
            outPacket.encodeInt(item.getItemId()); // nItemID
            outPacket.encodeInt(item.getPrice()); // nPrice
            outPacket.encodeByte(0); // nDiscountRate
            outPacket.encodeInt(item.getTokenItemId()); // nTokenItemID
            outPacket.encodeInt(item.getTokenPrice()); // nTokenPrice
            outPacket.encodeInt(0); // nItemPeriod
            outPacket.encodeInt(0); // nLevelLimited
            if (ItemConstants.isRechargeableItem(item.getItemId())) {
                outPacket.encodeDouble(item.getUnitPrice()); // dUnitPrice
                outPacket.encodeShort(item.getMaxPerSlot() + getIncSlotMax(user, item.getItemId())); // nMaxPerSlot
            } else {
                outPacket.encodeShort(item.getQuantity()); // nQuantity
                outPacket.encodeShort(item.getMaxPerSlot()); // nMaxPerSlot
            }
        }
    }

    public static ShopDialog from(NpcTemplate npcTemplate) {
        final List<ShopItem> items = ShopProvider.getNpcShopItems(npcTemplate.getId());
        return new ShopDialog(npcTemplate, items);
    }

    private static int getIncSlotMax(User user, int itemId) {
        int skillId = 0;
        if (ItemConstants.isJavelinItem(itemId)) {
            if (user.getSkillLevel(Thief.CLAW_MASTERY) > 0) {
                skillId = Thief.CLAW_MASTERY;
            } else if (user.getSkillLevel(NightWalker.CLAW_MASTERY) > 0) {
                skillId = NightWalker.CLAW_MASTERY;
            }
        } else if (ItemConstants.isPelletItem(itemId)) {
            if (user.getSkillLevel(Pirate.GUN_MASTERY) > 0) {
                skillId = Pirate.GUN_MASTERY;
            }
        }
        return user.getSkillStatValue(skillId, SkillStat.y);
    }
}
