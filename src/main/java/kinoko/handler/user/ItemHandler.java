package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemSpecType;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class ItemHandler {
    private static final Logger log = LogManager.getLogger(ItemHandler.class);

    @Handler(InHeader.USER_STAT_CHANGE_ITEM_USE_REQUEST)
    public static void handleUserStatChangeItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            user.dispose();
            return;
        }
        final ItemInfo ii = itemInfoResult.get();
        final InventoryType inventoryType = InventoryType.getByItemId(itemId);
        if (inventoryType != InventoryType.CONSUME) {
            user.dispose();
            return;
        }

        // Consume item
        final Inventory inventory = user.getInventoryManager().getInventoryByType(inventoryType);
        final Item item = inventory.getItem(position);
        if (item == null || item.getItemId() != itemId) {
            user.dispose();
            return;
        }
        item.setQuantity((short) (item.getQuantity() - 1));
        if (item.getQuantity() > 0) {
            user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(inventoryType, position, item.getQuantity()), true));
        } else {
            if (!inventory.removeItem(position, item)) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, position), true));
        }

        // Apply stat change
        final CharacterStat cs = user.getCharacterStat();
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        for (var entry : ii.getItemSpecs().entrySet()) {
            switch (entry.getKey()) {
                case hp -> {
                    final int newHp = cs.getHp() + ii.getSpec(ItemSpecType.hp);
                    cs.setHp(Math.min(newHp, cs.getMaxHp()));
                    statMap.put(Stat.HP, cs.getHp());
                }
                case mp -> {
                    final int newMp = cs.getMp() + ii.getSpec(ItemSpecType.mp);
                    cs.setMp(Math.min(newMp, cs.getMaxMp()));
                    statMap.put(Stat.MP, cs.getMp());
                }
                default -> {
                    log.error("Unhandled item spec type : {}", entry.getKey().name());
                }
            }
        }
        user.write(WvsContext.statChanged(statMap));
    }
}
