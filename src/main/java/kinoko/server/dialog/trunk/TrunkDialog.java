package kinoko.server.dialog.trunk;

import kinoko.packet.field.FieldPacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.dialog.Dialog;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.field.npc.Npc;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
import kinoko.world.item.Trunk;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class TrunkDialog implements Dialog {
    private static final Logger log = LogManager.getLogger(TrunkDialog.class);
    private final Npc npc;

    public TrunkDialog(Npc npc) {
        this.npc = npc;
    }

    @Override
    public void onPacket(Locked<User> locked, InPacket inPacket) {
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
                case GET_ITEM -> {
                    inPacket.decodeByte(); // nItemID / 1000000, can be ignored
                    final int position = inPacket.decodeByte(); // CTrunkDlg::ITEM->nIdx
                    // Check if user has enough money
                    final InventoryManager im = user.getInventoryManager();
                    if (im.getMoney() < npc.getTrunkGet()) {
                        user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.GET_NO_MONEY)));
                        return;
                    }
                    // Check if user can move item from trunk to inventory
                    final Item item = trunk.getItems().get(position);
                    if (item == null) {
                        user.write(FieldPacket.trunkResult(TrunkResult.message("Due to an error, the trade did not happen.")));
                        return;
                    }
                    if (im.canAddItem(item).isEmpty()) {
                        user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.GET_UNKNOWN)));
                        return;
                    }
                    // Deduct money and move item
                    if (!im.addMoney(-npc.getTrunkGet())) {
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
                    user.write(FieldPacket.trunkResult(TrunkResult.getSuccess(trunk)));
                    user.write(WvsContext.inventoryOperation(addItemResult.get(), false));
                    user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                }
                case PUT_ITEM -> {
                    final int position = inPacket.decodeShort(); // nPOS
                    final int itemId = inPacket.decodeInt(); // nItemID
                    final int quantity = inPacket.decodeShort(); // nCount
                    // Check if user has money and item
                    final InventoryManager im = user.getInventoryManager();
                    if (im.getMoney() < npc.getTrunkPut()) {
                        user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.PUT_NO_MONEY)));
                        return;
                    }
                    final Item item = im.getInventoryByItemId(itemId).getItem(position);
                    if (item == null || item.getItemId() != itemId || item.getQuantity() < quantity) {
                        user.write(FieldPacket.trunkResult(TrunkResult.message("Due to an error, the trade did not happen.")));
                        return;
                    }
                    // Check if trunk has space for item
                    if (trunk.getRemaining() == 0) {
                        user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.PUT_NO_SPACE)));
                        return;
                    }
                    // Deduct money and move item
                    if (!im.addMoney(-npc.getTrunkPut())) {
                        throw new IllegalStateException("Could not deduct trunk put fee");
                    }
                    final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item);
                    if (removeItemResult.isEmpty()) {
                        throw new IllegalStateException("Could not remove item from inventory");
                    }
                    trunk.getItems().add(item);
                    // Update client
                    user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                    user.write(FieldPacket.trunkResult(TrunkResult.putSuccess(trunk)));
                    user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                }
                case SORT_ITEM -> {
                    // Sort items by item id (ascending), then quantity (descending)
                    trunk.getItems().sort(Comparator.comparing(Item::getItemId).thenComparing(Item::getQuantity, Comparator.reverseOrder()));
                    user.write(FieldPacket.trunkResult(TrunkResult.sortItem(trunk)));
                }
                case MONEY -> {
                    final int money = inPacket.decodeInt(); // nMoney
                    final InventoryManager im = user.getInventoryManager();
                    if (money > 0) {
                        // CTrunkDlg::SendGetMoneyRequest
                        // Check if money can be moved from trunk to inventory
                        if (!trunk.canAddMoney(-money)) {
                            user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.GET_NO_MONEY)));
                            return;
                        }
                        if (!im.canAddMoney(money)) {
                            user.write(FieldPacket.trunkResult(TrunkResult.message("You cannot hold any more mesos.")));
                            return;
                        }
                        // Move money
                        if (!trunk.addMoney(-money)) {
                            throw new IllegalStateException("Could not take money from trunk");
                        }
                        if (!im.addMoney(money)) {
                            throw new IllegalStateException("Could not add money to inventory");
                        }
                        user.write(FieldPacket.trunkResult(TrunkResult.moneySuccess(trunk)));
                        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                    } else if (money < 0) {
                        // CTrunkDlg::SendPutMoneyRequest
                        // Check if money can be moved from inventory to trunk
                        if (!im.canAddMoney(money)) {
                            user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.PUT_NO_MONEY)));
                            return;
                        }
                        if (!trunk.canAddMoney(-money)) {
                            user.write(FieldPacket.trunkResult(TrunkResult.of(TrunkResultType.PUT_NO_SPACE)));
                            return;
                        }
                        // Move money
                        if (!im.addMoney(money)) {
                            throw new IllegalStateException("Could not take money from inventory");
                        }
                        if (!trunk.addMoney(-money)) {
                            throw new IllegalStateException("Could not add money to trunk");
                        }
                        user.write(FieldPacket.trunkResult(TrunkResult.moneySuccess(trunk)));
                        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                    }
                }
                case CLOSE_DIALOG -> {
                    user.closeDialog();
                }
            }
        }
    }

    public static TrunkDialog from(Npc npc) {
        return new TrunkDialog(npc);
    }
}
