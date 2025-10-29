package kinoko.server.command.supergm;

import kinoko.packet.user.UserLocal;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;

import java.util.List;
import java.util.Optional;

/**
 * SuperGM command to give items to a user.
 */
public final class ItemCommand {

    @Command("item")
    @Arguments("item ID")
    public static void item(User user, String[] args) {
        try {
            final int itemId = Integer.parseInt(args[1]);
            final int quantity = args.length > 2 ? Integer.parseInt(args[2]) : 1;

            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not resolve item ID: %d", itemId));
                return;
            }

            final ItemInfo ii = itemInfoResult.get();
            final Item item = ii.createItem(user.getNextItemSn(), Math.min(quantity, ii.getSlotMax()), ItemVariationOption.NORMAL);

            // Add item to inventory
            final InventoryManager im = user.getInventoryManager();
            final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);

            if (addItemResult.isPresent()) {
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(UserLocal.effect(Effect.gainItem(item)));
            } else {
                user.write(MessagePacket.system("Failed to add item ID %d (%d) to inventory", itemId, quantity));
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !item <item ID> [quantity]"));
        }
    }
}
