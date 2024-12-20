package kinoko.server.dialog.trunk;

import kinoko.packet.field.TrunkPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.npc.NpcTemplate;
import kinoko.server.dialog.Dialog;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class TrunkDialog implements Dialog {
    private static final Logger log = LogManager.getLogger(TrunkDialog.class);
    private final NpcTemplate npcTemplate;

    public TrunkDialog(NpcTemplate npcTemplate) {
        this.npcTemplate = npcTemplate;
    }

    public int getTrunkPut() {
        return npcTemplate.getTrunkPut();
    }

    public int getTrunkGet() {
        return npcTemplate.getTrunkGet();
    }

    public void handlePacket(Locked<User> locked, InPacket inPacket) {
        final User user = locked.get();
        final int type = inPacket.decodeByte();
        final TrunkRequestType requestType = TrunkRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown trunk request type {}", type);
            return;
        }
        // Lock account to access trunk
        try (var lockedAccount = user.getAccount().acquire()) {
            final Trunk trunk = lockedAccount.get().getTrunk();
            switch (requestType) {
                case GetItem -> {
                    inPacket.decodeByte(); // nItemID / 1000000, can be ignored
                    final int position = inPacket.decodeByte(); // CTrunkDlg::ITEM->nIdx
                    // Check if user has enough money
                    final InventoryManager im = user.getInventoryManager();
                    if (im.getMoney() < getTrunkGet()) {
                        user.write(TrunkPacket.of(TrunkResultType.GetNoMoney));
                        return;
                    }
                    // Check if user can move item from trunk to inventory
                    final Item item = trunk.getItems().get(position);
                    if (item == null) {
                        user.write(TrunkPacket.serverMsg("Due to an error, the trade did not happen."));
                        return;
                    }
                    if (!im.canAddItem(item)) {
                        user.write(TrunkPacket.of(TrunkResultType.GetUnknown));
                        return;
                    }
                    // Deduct money and move item
                    if (!im.addMoney(-getTrunkGet())) {
                        throw new IllegalStateException("Could not deduct trunk get fee");
                    }
                    if (!trunk.getItems().remove(item)) {
                        throw new IllegalStateException("Could not remove item from trunk");
                    }
                    final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
                    if (addItemResult.isEmpty()) {
                        throw new IllegalStateException("Could not add trunk item into inventory");
                    }
                    // Update client
                    user.write(TrunkPacket.getSuccess(trunk));
                    user.write(WvsContext.inventoryOperation(addItemResult.get(), false));
                    user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                }
                case PutItem -> {
                    final int position = inPacket.decodeShort(); // nPOS
                    final int itemId = inPacket.decodeInt(); // nItemID
                    final int quantity = inPacket.decodeShort(); // nCount
                    // Check if user has money and item
                    final InventoryManager im = user.getInventoryManager();
                    if (im.getMoney() < getTrunkPut()) {
                        user.write(TrunkPacket.of(TrunkResultType.PutNoMoney));
                        return;
                    }
                    final InventoryType inventoryType = InventoryType.getByItemId(itemId);
                    final Item item = im.getInventoryByType(inventoryType).getItem(position);
                    if (item == null || item.getItemId() != itemId || item.getQuantity() < quantity) {
                        user.write(TrunkPacket.serverMsg("Due to an error, the trade did not happen."));
                        return;
                    }
                    // Check if trunk has space for item
                    if (trunk.getRemaining() == 0) {
                        user.write(TrunkPacket.of(TrunkResultType.PutNoSpace));
                        return;
                    }
                    // Deduct money and move item
                    if (!im.addMoney(-getTrunkPut())) {
                        throw new IllegalStateException("Could not deduct trunk put fee");
                    }
                    if (item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId()) && item.getQuantity() > quantity) {
                        // Update item count
                        item.setQuantity((short) (item.getQuantity() - quantity));
                        user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(inventoryType, position, item.getQuantity()), false));
                        // Create partial item
                        final Item partialItem = new Item(item);
                        partialItem.setItemSn(user.getNextItemSn());
                        partialItem.setQuantity((short) quantity);
                        partialItem.setPossibleTrading(false);
                        trunk.getItems().add(partialItem);
                    } else {
                        // Move full item
                        final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item);
                        if (removeItemResult.isEmpty()) {
                            throw new IllegalStateException("Could not remove item from inventory");
                        }
                        item.setPossibleTrading(false);
                        trunk.getItems().add(item);
                        user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                    }
                    // Update client
                    user.write(TrunkPacket.putSuccess(trunk));
                    user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                }
                case SortItem -> {
                    // Sort items by item id (ascending), then quantity (descending)
                    trunk.getItems().sort(Comparator.comparing(Item::getItemId).thenComparing(Item::getQuantity, Comparator.reverseOrder()));
                    user.write(TrunkPacket.sortItem(trunk));
                }
                case Money -> {
                    final int money = inPacket.decodeInt(); // nMoney
                    final InventoryManager im = user.getInventoryManager();
                    if (money > 0) {
                        // CTrunkDlg::SendGetMoneyRequest
                        // Check if money can be moved from trunk to inventory
                        if (!trunk.canAddMoney(-money)) {
                            user.write(TrunkPacket.of(TrunkResultType.GetNoMoney));
                            return;
                        }
                        if (!im.canAddMoney(money)) {
                            user.write(TrunkPacket.serverMsg("You cannot hold any more mesos."));
                            return;
                        }
                        // Move money
                        if (!trunk.addMoney(-money)) {
                            throw new IllegalStateException("Could not take money from trunk");
                        }
                        if (!im.addMoney(money)) {
                            throw new IllegalStateException("Could not add money to inventory");
                        }
                        user.write(TrunkPacket.moneySuccess(trunk));
                        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                    } else if (money < 0) {
                        // CTrunkDlg::SendPutMoneyRequest
                        // Check if money can be moved from inventory to trunk
                        if (!im.canAddMoney(money)) {
                            user.write(TrunkPacket.of(TrunkResultType.PutNoMoney));
                            return;
                        }
                        if (!trunk.canAddMoney(-money)) {
                            user.write(TrunkPacket.of(TrunkResultType.PutNoSpace));
                            return;
                        }
                        // Move money
                        if (!im.addMoney(money)) {
                            throw new IllegalStateException("Could not take money from inventory");
                        }
                        if (!trunk.addMoney(-money)) {
                            throw new IllegalStateException("Could not add money to trunk");
                        }
                        user.write(TrunkPacket.moneySuccess(trunk));
                        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                    }
                }
                case CloseDialog -> {
                    user.setDialog(null);
                }
            }
        }
    }

    public static TrunkDialog from(NpcTemplate npcTemplate) {
        return new TrunkDialog(npcTemplate);
    }
}
