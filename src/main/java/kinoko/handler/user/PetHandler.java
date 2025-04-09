package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.PetInteraction;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.life.MovePath;
import kinoko.world.item.*;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PetHandler {
    private static final Logger log = LogManager.getLogger(PetHandler.class);

    @Handler(InHeader.UserDestroyPetItemRequest)
    public static void handleUserDestroyPetItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final long petSn = inPacket.decodeLong(); // liCashItemSN
        final InventoryManager im = user.getInventoryManager();
        final Optional<Tuple<Integer, Item>> itemEntryResult = im.getItemBySn(InventoryType.CASH, petSn);
        if (itemEntryResult.isEmpty()) {
            log.error("Tried to destroy pet item with sn {} not in inventory", petSn);
            user.dispose();
            return;
        }
        final int position = itemEntryResult.get().getLeft();
        final Item petItem = itemEntryResult.get().getRight();
        final Optional<InventoryOperation> removeResult = im.removeItem(position, petItem);
        if (removeResult.isEmpty()) {
            throw new IllegalStateException("Could not remove pet item in inventory");
        }
        user.write(WvsContext.inventoryOperation(removeResult.get(), true));
    }

    @Handler(InHeader.UserActivatePetRequest)
    public static void handleUserActivatePetRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final boolean bossPet = inPacket.decodeBoolean(); // bBossPet
        // Resolve pet item in inventory
        final InventoryManager im = user.getInventoryManager();
        final Item item = im.getCashInventory().getItem(position);
        if (item == null || item.getItemType() != ItemType.PET) {
            log.error("Received UserActivatePetRequest with incorrect position {}", position);
            user.dispose();
            return;
        }
        // Check if pet already activated
        final long petSn = item.getItemSn();
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            final boolean hasFollowTheLead = user.getSkillLevel(SkillConstants.getNoviceSkillAsRace(Beginner.FOLLOW_THE_LEAD, user.getJob())) > 0;
            if (hasFollowTheLead && !bossPet) {
                // Check if max number of pets active
                if (user.getPets().size() >= GameConstants.PET_COUNT_MAX) {
                    log.error("Tried to activate pet while having max number of pets active");
                    user.dispose();
                    return;
                }
                // Create and add pet
                final Pet pet = Pet.from(user, item);
                if (!user.addPet(pet, false)) {
                    log.error("Could not add pet");
                    user.dispose();
                    return;
                }
                // Update client
                user.getField().broadcastPacket(PetPacket.petActivated(user, pet));
                user.write(PetPacket.petLoadExceptionList(user, pet.getPetIndex(), pet.getItemSn(), user.getConfigManager().getPetExceptionList()));
            } else {
                // Create and set pet
                final Pet pet = Pet.from(user, item);
                pet.setPosition(user.getField(), user.getX(), user.getY());
                user.setPet(pet, 0, false);
                // Update client
                user.getField().broadcastPacket(PetPacket.petActivated(user, pet));
                user.write(PetPacket.petLoadExceptionList(user, pet.getPetIndex(), pet.getItemSn(), user.getConfigManager().getPetExceptionList()));
            }
        } else {
            // Deactivate pet and update client
            final int petIndex = petIndexResult.get();
            if (!user.removePet(petIndex)) {
                log.error("Could not remove pet at index : {}", petIndex);
                user.dispose();
                return;
            }
            user.getField().broadcastPacket(PetPacket.petDeactivated(user, petIndex, 0));
        }
    }

    @Handler(InHeader.PetMove)
    public static void handlePetMove(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong(); // liPetLockerSN
        final MovePath movePath = MovePath.decode(inPacket);
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            log.error("Received PetMove for invalid pet sn : {}", petSn);
            return;
        }
        final int petIndex = petIndexResult.get();
        final Pet pet = user.getPet(petIndex);
        if (pet == null) {
            log.error("Received PetMove for invalid pet index : {}", petIndex);
            return;
        }
        movePath.applyTo(pet);
        user.getField().broadcastPacket(PetPacket.petMove(user, petIndex, movePath), user);
    }

    @Handler(InHeader.PetAction)
    public static void handlePetAction(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong(); // liPetLockerSN
        inPacket.decodeInt(); // update_time
        final int type = inPacket.decodeByte(); // nType
        final int action = inPacket.decodeByte(); // nAction
        final String chat = inPacket.decodeString(); // sChat
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            log.error("Received PetAction for invalid pet sn : {}", petSn);
            return;
        }
        final int petIndex = petIndexResult.get();
        final Pet pet = user.getPet(petIndex);
        if (pet == null) {
            log.error("Received PetAction for invalid pet index : {}", petIndex);
            return;
        }
        user.getField().broadcastPacket(PetPacket.petAction(user, petIndex, type, action, chat, pet.getChatBalloon()), user);
    }

    @Handler(InHeader.PetInteractionRequest)
    public static void handlePetInteractionRequest(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong(); // liPetLockerSN
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            log.error("Received PetInteractionRequest for invalid pet sn : {}", petSn);
            return;
        }
        inPacket.decodeByte(); // bCommandWithName
        final int action = inPacket.decodeByte();

        // Resolve pet interaction
        final int petIndex = petIndexResult.get();
        final Pet pet = user.getPet(petIndex);
        if (pet == null) {
            log.error("Received PetInteractionRequest for invalid pet index : {}", petIndex);
            return;
        }
        final Optional<PetInteraction> interactionResult = ItemProvider.getPetInteraction(pet.getTemplateId(), action);
        if (interactionResult.isEmpty()) {
            log.error("Could not resolve pet interaction for template {}, action {}", pet.getTemplateId(), action);
            return;
        }
        final PetInteraction interaction = interactionResult.get();

        // Check interaction and success
        if (pet.getLevel() < interaction.getLevelMin() || pet.getLevel() > interaction.getLevelMax()) {
            log.error("Tried to perform pet action {} which is not available for pet {} at level {}", action, pet.getTemplateId(), pet.getLevel());
            return;
        }
        final boolean success = Util.succeedProp(interaction.getProp());
        if (success) {
            // Resolve pet item
            final InventoryManager im = user.getInventoryManager();
            final Optional<Tuple<Integer, Item>> itemEntryResult = im.getItemBySn(InventoryType.CASH, petSn);
            if (itemEntryResult.isEmpty()) {
                throw new IllegalStateException("Could not resolve pet item");
            }
            final int position = itemEntryResult.get().getLeft();
            final Item item = itemEntryResult.get().getRight();

            // Increase tameness (closeness)
            final PetData petData = item.getPetData();
            final int newTameness = Math.min(petData.getTameness() + interaction.getIncTameness(), GameConstants.PET_TAMENESS_MAX);
            petData.setTameness((short) newTameness);

            // Level up
            boolean levelUp = false;
            while (petData.getLevel() < GameConstants.PET_LEVEL_MAX &&
                    newTameness > GameConstants.getNextLevelPetCloseness(petData.getLevel())) {
                petData.setLevel((byte) (petData.getLevel() + 1));
                levelUp = true;
            }

            // Update pet item
            final Optional<InventoryOperation> updateResult = im.updateItem(position, item);
            if (updateResult.isEmpty()) {
                throw new IllegalStateException("Could not update pet item");
            }

            // Update client
            user.write(WvsContext.inventoryOperation(updateResult.get(), false));
            if (levelUp) {
                user.write(UserLocal.effect(Effect.petLevelUp(petIndex)));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.petLevelUp(petIndex)), user);
            }
        }

        // Broadcast pet action
        user.getField().broadcastPacket(PetPacket.petActionInteract(user, petIndex, action, success, pet.getChatBalloon()));
    }

    @Handler(InHeader.PetDropPickUpRequest)
    public static void handlePetDropPickUpRequest(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong(); // liPetLockerSN
        final Optional<Integer> petIndexResult = user.getPetIndex(petSn);
        if (petIndexResult.isEmpty()) {
            log.error("Received PetDropPickUpRequest for invalid pet sn : {}", petSn);
            return;
        }
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
            return;
        }
        inPacket.decodeInt(); // update_time
        inPacket.decodeShort(); // pt->x
        inPacket.decodeShort(); // pt->y
        final int objectId = inPacket.decodeInt(); // dwID
        inPacket.decodeInt(); // dwCliCrc
        inPacket.decodeByte(); // bPickupOthers
        inPacket.decodeByte(); // bSweepForDrop
        inPacket.decodeByte(); // bLongRange

        // Find drop in field
        final Field field = user.getField();
        final Optional<Drop> dropResult = field.getDropPool().getById(objectId);
        if (dropResult.isEmpty()) {
            return;
        }

        // Pick up drop
        field.getDropPool().pickUpDrop(user, dropResult.get(), DropLeaveType.PICKED_UP_BY_PET, petIndexResult.get());
    }

    @Handler(InHeader.PetUpdateExceptionListRequest)
    public static void PetUpdateExceptionListRequest(User user, InPacket inPacket) {
        inPacket.decodeLong(); // liPetLockerSN
        final List<Integer> exceptionList = new ArrayList<>();
        final int count = inPacket.decodeByte();
        for (int i = 0; i < count; i++) {
            exceptionList.add(inPacket.decodeInt());
        }
        // Overwrite exception list for all pets
        user.getConfigManager().setPetExceptionList(exceptionList);
        for (Pet pet : user.getPets()) {
            user.write(PetPacket.petLoadExceptionList(user, pet.getPetIndex(), pet.getItemSn(), exceptionList));
        }
    }
}
