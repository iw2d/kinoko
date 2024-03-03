package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.user.effect.PetEffectType;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemSpecType;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public final class ItemHandler {
    private static final Logger log = LogManager.getLogger(ItemHandler.class);

    @Handler(InHeader.USER_STAT_CHANGE_ITEM_USE_REQUEST)
    public static void handleUserStatChangeItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo ii = itemInfoResult.get();

        try (var locked = user.acquire()) {
            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Apply stat change
            for (var entry : ii.getItemSpecs().entrySet()) {
                switch (entry.getKey()) {
                    case hp -> {
                        user.addHp(ii.getSpec(ItemSpecType.hp));
                    }
                    case mp -> {
                        user.addMp(ii.getSpec(ItemSpecType.mp));
                    }
                    default -> {
                        log.error("Unhandled item spec type : {}", entry.getKey().name());
                    }
                }
            }
        }
    }

    @Handler(InHeader.USER_PET_FOOD_ITEM_USE_REQUEST)
    public static void handleUserPetFoodItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Check item
        if (!ItemConstants.isPetFoodItem(itemId)) {
            log.error("Received USER_PET_FOOD_ITEM_USE_REQUEST with a non-pet food item {}", itemId);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo ii = itemInfoResult.get();
        final int incFullness = ii.getSpec(ItemSpecType.inc);

        try (var locked = user.acquire()) {
            // Select target pet
            Pet target = null;
            for (Pet pet : user.getPets()) {
                if (pet == null) {
                    continue;
                }
                if (target == null || target.getFullness() > pet.getFullness()) {
                    target = pet;
                }
            }
            if (target == null) {
                log.error("Could not select target pet for using pet food item : {}", itemId);
                user.dispose();
                return;
            }
            final Optional<Integer> petIndexResult = user.getPetIndex(target.getItemSn());
            if (petIndexResult.isEmpty()) {
                throw new IllegalStateException("Could not resolve pet index");
            }
            final long petSn = target.getItemSn();
            final int petIndex = petIndexResult.get();

            // Resolve pet item
            final InventoryManager im = user.getInventoryManager();
            final Optional<Map.Entry<Integer, Item>> itemEntry = im.getInventoryByType(InventoryType.CASH).getItems().entrySet().stream()
                    .filter((entry) -> entry.getValue().getItemSn() == petSn)
                    .findFirst();
            if (itemEntry.isEmpty()) {
                throw new IllegalStateException("Could not resolve pet item");
            }
            final int petPosition = itemEntry.get().getKey();
            final Item petItem = itemEntry.get().getValue();

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Increase fullness
            final PetData petData = petItem.getPetData();
            final int fullness = petData.getFullness();
            final boolean success = fullness < GameConstants.PET_FULLNESS_MAX;
            petData.setFullness((byte) Math.min(fullness + incFullness, GameConstants.PET_FULLNESS_MAX));

            boolean levelUp = false;
            if (fullness <= GameConstants.PET_FULLNESS_FOR_TAMENESS) {
                // Increase tameness (closeness)
                final int newTameness = Math.min(petData.getTameness() + 1, GameConstants.PET_TAMENESS_MAX);
                petData.setTameness((short) newTameness);

                // Level up
                while (petData.getLevel() < GameConstants.PET_LEVEL_MAX &&
                        newTameness > GameConstants.getNextLevelPetCloseness(petData.getLevel())) {
                    petData.setLevel((byte) (petData.getLevel() + 1));
                    levelUp = true;
                }
            } else if (fullness == GameConstants.PET_FULLNESS_MAX) {
                // Decrease tameness (closeness)
                final int newTameness = Math.max(petData.getTameness() - 1, 0);
                petData.setTameness((short) newTameness);
            }

            // Update pet item
            final Optional<InventoryOperation> updateResult = user.getInventoryManager().updateItem(petPosition, petItem);
            if (updateResult.isEmpty()) {
                throw new IllegalStateException("Could not update pet item");
            }

            // Update client
            user.write(WvsContext.inventoryOperation(updateResult.get(), false));
            if (levelUp) {
                user.write(UserLocal.effect(Effect.pet(PetEffectType.LEVEL_UP, petIndex)));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.pet(PetEffectType.LEVEL_UP, petIndex)), user);
            }

            // Broadcast pet action
            user.getField().broadcastPacket(PetPacket.actionFeed(user, petIndex, success, false));
        }
    }

    private static Optional<InventoryOperation> consumeItem(Locked<User> locked, int position, int itemId) {
        final User user = locked.get();
        final InventoryType inventoryType = InventoryType.getByItemId(itemId);
        if (inventoryType != InventoryType.CONSUME) {
            log.error("Tried to use a non-consume item : {}", itemId);
            return Optional.empty();
        }
        final InventoryManager im = user.getInventoryManager();
        final Item item = im.getInventoryByType(inventoryType).getItem(position);
        if (item == null || item.getItemId() != itemId) {
            log.error("Tried to use an item in position {} as item ID : {}", position, itemId);
            return Optional.empty();
        }
        final Optional<InventoryOperation> removeResult = im.removeItem(position, item, 1);
        if (removeResult.isEmpty()) {
            throw new IllegalStateException("Could not remove item from inventory");
        }
        return removeResult;
    }
}
