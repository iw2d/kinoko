package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.QuestProvider;
import kinoko.provider.item.PetInteraction;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.life.MovePath;
import kinoko.world.item.*;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PetHandler {
    private static final Logger log = LogManager.getLogger(PetHandler.class);

    @Handler(InHeader.UserDestroyPetItemRequest)
    public static void handleUserDestroyPetItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final long petSn = inPacket.decodeLong(); // liCashItemSN
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            final Optional<Map.Entry<Integer, Item>> itemEntryResult = im.getCashInventory().getItems().entrySet().stream()
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

    @Handler(InHeader.UserActivatePetRequest)
    public static void handleUserActivatePetRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final boolean bossPet = inPacket.decodeBoolean(); // bBossPet
        try (var locked = user.acquire()) {
            // Resolve pet item in inventory
            final InventoryManager im = locked.get().getInventoryManager();
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
                // Check if max number of pets active
                final boolean hasFollowTheLead = user.getSkillManager().getSkillLevel(SkillConstants.getNoviceSkillAsRace(Beginner.FOLLOW_THE_LEAD, user.getJob())) > 0;
                if (user.getPets().size() >= (hasFollowTheLead ? GameConstants.PET_COUNT_MAX : 1)) {
                    log.error("Tried to activate pet while having max number of pets active");
                    user.dispose();
                    return;
                }
                // Create and set pet
                final Pet pet = Pet.from(user, item);
                pet.setPosition(user.getField(), user.getX(), user.getY());
                if (!hasFollowTheLead || bossPet) {
                    user.setPet(pet, 0, false);
                } else {
                    if (!user.addPet(pet, false)) {
                        log.error("Could not add pet");
                        user.dispose();
                        return;
                    }
                }
                // Update client
                user.getField().broadcastPacket(PetPacket.petActivated(user, pet));
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
        user.getField().broadcastPacket(PetPacket.petAction(user, petIndex, type, action, chat), user);
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

        try (var locked = user.acquire()) {
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
                final Optional<Map.Entry<Integer, Item>> itemEntry = im.getCashInventory().getItems().entrySet().stream()
                        .filter((entry) -> entry.getValue().getItemSn() == petSn)
                        .findFirst();
                if (itemEntry.isEmpty()) {
                    throw new IllegalStateException("Could not resolve pet item");
                }
                final int position = itemEntry.get().getKey();
                final Item item = itemEntry.get().getValue();

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
            user.getField().broadcastPacket(PetPacket.petActionInteract(user, petIndex, action, success, false));
        }
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
        final Field field = user.getField();
        if (field.getFieldKey() != fieldKey) {
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
        final Optional<Drop> dropResult = field.getDropPool().getById(objectId);
        if (dropResult.isEmpty()) {
            return;
        }
        final Drop drop = dropResult.get();

        try (var locked = user.acquire()) {
            // Check if drop can be added to inventory
            final InventoryManager im = user.getInventoryManager();
            if (drop.isMoney()) {
                final long newMoney = ((long) im.getMoney()) + drop.getMoney();
                if (newMoney > GameConstants.MONEY_MAX) {
                    user.write(MessagePacket.unavailableForPickUp());
                    return;
                }
            } else {
                // Inventory full
                if (!im.canAddItem(drop.getItem())) {
                    user.write(MessagePacket.cannotGetAnymoreItems());
                    return;
                }
                // Quest item handling
                if (drop.isQuest()) {
                    final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(drop.getQuestId());
                    if (questRecordResult.isEmpty()) {
                        user.write(MessagePacket.unavailableForPickUp());
                        return;
                    }
                    final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(drop.getQuestId());
                    if (questInfoResult.isPresent() && questInfoResult.get().hasRequiredItem(user, drop.getItem().getItemId())) {
                        user.write(MessagePacket.cannotGetAnymoreItems());
                        return;
                    }
                }
            }

            // Try removing drop from field
            if (!field.getDropPool().removeDrop(drop, DropLeaveType.PICKED_UP_BY_PET, user.getCharacterId(), petIndexResult.get(), 0)) {
                return;
            }

            // Add drop to inventory
            if (drop.isMoney()) {
                if (im.addMoney(drop.getMoney())) {
                    user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
                    user.write(MessagePacket.pickUpMoney(drop.getMoney(), false));
                }
            } else {
                final Optional<List<InventoryOperation>> addItemResult = im.addItem(drop.getItem());
                if (addItemResult.isPresent()) {
                    user.write(WvsContext.inventoryOperation(addItemResult.get(), false));
                    user.write(MessagePacket.pickUpItem(drop.getItem()));
                }
            }
        }
    }
}
