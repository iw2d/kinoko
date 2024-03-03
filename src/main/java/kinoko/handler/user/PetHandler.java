package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.item.*;
import kinoko.world.life.MovePath;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public final class PetHandler {
    private static final Logger log = LogManager.getLogger(PetHandler.class);

    @Handler(InHeader.USER_DESTROY_PET_ITEM_REQUEST)
    public static void handleUserDestroyPetItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final long petSn = inPacket.decodeLong(); // liCashItemSN
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            final Inventory inventory = im.getInventoryByType(InventoryType.CASH);
            final Optional<Map.Entry<Integer, Item>> itemEntryResult = inventory.getItems().entrySet().stream()
                    .filter((entry) -> entry.getValue().getItemSn() == petSn)
                    .findFirst();
            if (itemEntryResult.isEmpty()) {
                log.error("Tried to destroy pet item with sn {} not in inventory", petSn);
                user.dispose();
                return;
            }
            final int position = itemEntryResult.get().getKey();
            final Item petItem = itemEntryResult.get().getValue();
            final Optional<InventoryOperation> removeResult = im.removeItem(position, petItem);
            if (removeResult.isEmpty()) {
                throw new IllegalStateException("Could not remove pet item in inventory");
            }
            user.write(WvsContext.inventoryOperation(removeResult.get(), true));
        }
    }

    @Handler(InHeader.USER_ACTIVATE_PET_REQUEST)
    public static void handleUserActivatePetRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final boolean bossPet = inPacket.decodeBoolean(); // bBossPet
        try (var locked = user.acquire()) {
            // Resolve pet item in inventory
            final InventoryManager im = locked.get().getInventoryManager();
            final Inventory inventory = im.getInventoryByType(InventoryType.CASH);
            final Item item = inventory.getItem(position);
            if (item == null || item.getItemType() != ItemType.PET) {
                log.error("Received USER_ACTIVATE_PET_REQUEST with incorrect position {}", position);
                user.dispose();
                return;
            }
            // Check if pet already activated
            final long petSn = item.getItemSn();
            final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
            if (petIndexResult.isEmpty()) {
                // Resolve pet index
                final CharacterStat cs = user.getCharacterStat();
                final int petIndex;
                if (bossPet || cs.getPetSn1() == 0) {
                    petIndex = 0;
                } else if (cs.getPetSn2() == 0) {
                    petIndex = 1;
                } else if (cs.getPetSn3() == 0) {
                    petIndex = 2;
                } else {
                    log.error("Tried to activate pet while having max number of pets active");
                    user.dispose();
                    return;
                }
                // Create pet and update client
                final Pet pet = Pet.from(user, item);
                pet.setX(user.getX());
                pet.setY(user.getY());
                pet.setFoothold(user.getFoothold());
                user.getPets()[petIndex] = pet;
                user.getField().broadcastPacket(PetPacket.petActivated(user, pet, 0));
                user.setPetIndex(petIndex, petSn);
            } else {
                // Deactivate pet and update client
                final int petIndex = petIndexResult.get();
                user.getPets()[petIndex] = null;
                user.getField().broadcastPacket(PetPacket.petDeactivated(user, petIndex, 0));
                user.setPetIndex(petIndex, 0);
            }
        }
    }

    @Handler(InHeader.PET_MOVE)
    public static void handlePetMove(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong(); // liPetLockerSN
        final MovePath movePath = MovePath.decode(inPacket);
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            log.error("Received PET_MOVE for invalid pet sn : {}", petSn);
            return;
        }
        final int petIndex = petIndexResult.get();
        final Pet pet = user.getPets()[petIndex];
        if (pet == null) {
            log.error("Received PET_MOVE for invalid pet index : {}", petIndex);
            return;
        }
        movePath.applyTo(pet);
        user.getField().broadcastPacket(PetPacket.move(user, petIndex, movePath), user);
    }

    @Handler(InHeader.PET_ACTION)
    public static void handlePetAction(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong(); // liPetLockerSN
        inPacket.decodeInt(); // update_time
        final byte type = inPacket.decodeByte(); // nType
        final byte action = inPacket.decodeByte(); // nAction
        final String chat = inPacket.decodeString(); // sChat
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            log.error("Received PET_ACTION for invalid pet sn : {}", petSn);
            return;
        }
        final int petIndex = petIndexResult.get();
        final Pet pet = user.getPets()[petIndex];
        if (pet == null) {
            log.error("Received PET_ACTION for invalid pet index : {}", petIndex);
            return;
        }
        user.getField().broadcastPacket(PetPacket.action(user, petIndex, type, action, chat), user);
    }
}
