package kinoko.server.command.tester;

import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Tester command to clear a specific inventory of a user.
 */
public final class ClearInventoryCommand {

    @Command({"clearinventory", "clearinv"})
    @Arguments("inventory type")
    public static void clearInventory(User user, String[] args) {
        try {
            Optional<InventoryType> inventoryTypeResult = Arrays.stream(InventoryType.values())
                    .filter(type -> type.name().equalsIgnoreCase(args[1]))
                    .findFirst();

            if (inventoryTypeResult.isEmpty()) {
                user.systemMessage(
                        "Please specify a valid inventory type: EQUIP | CONSUME | INSTALL | ETC | CASH");
                return;
            }

            InventoryType inventoryType = inventoryTypeResult.get();
            List<InventoryOperation> removeOperations = new ArrayList<>();

            var iter = user.getInventoryManager().getInventoryByType(inventoryType).getItems().entrySet().iterator();
            while (iter.hasNext()) {
                var tuple = iter.next();
                int position = tuple.getKey();
                removeOperations.add(InventoryOperation.delItem(inventoryType, position));
                iter.remove();
            }

            user.write(WvsContext.inventoryOperation(removeOperations, true));
            user.systemMessage("%s inventory cleared!", inventoryType);

        } catch (ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !clearinventory <inventory type>");
        }
    }
}
