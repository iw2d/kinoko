package kinoko.handler.user.item;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.item.*;
import kinoko.provider.map.FieldOption;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.skill.SkillInfo;
import kinoko.script.common.ScriptDispatcher;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ItemHandler {
    protected static final Logger log = LogManager.getLogger(ItemHandler.class);

    @Handler(InHeader.UserStatChangeItemUseRequest)
    public static void handleUserStatChangeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check field limit
            final Field field = locked.get().getField();
            if (field.hasFieldOption(FieldOption.STATCHANGEITEMCONSUMELIMIT) && !field.getMapInfo().getAllowedItems().contains(itemId)) {
                log.error("Tried to use stat change item in a restricted field");
                user.dispose();
                return;
            }

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Apply stat change
            changeStat(locked, itemInfoResult.get());
        }
    }

    @Handler(InHeader.UserStatChangeItemCancelRequest)
    public static void handleUserStatChangeItemCancelRequest(User user, InPacket inPacket) {
        final int itemId = inPacket.decodeInt(); // sign inverted
        try (var locked = user.acquire()) {
            locked.get().resetTemporaryStat(itemId);
        }
    }

    @Handler(InHeader.UserStatChangeByPortableChairRequest)
    public static void handleUserStatChangeByPortableChairRequest(User user, InPacket inPacket) {
        // Client notifying the server that the recovery amount from UserChangeStatRequest has changed
    }

    @Handler(InHeader.UserMobSummonItemUseRequest)
    public static void handleUserMobSummonItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Check item
        if (!ItemConstants.isMobSummonItem(itemId)) {
            log.error("Received UserMobSummonItemUseRequest with an invalid mob summon item {}", itemId);
            user.dispose();
            return;
        }

        // Resolve mob summon info
        final Optional<MobSummonInfo> mobSummonInfoResult = ItemProvider.getMobSummonInfo(itemId);
        if (mobSummonInfoResult.isEmpty() || mobSummonInfoResult.get().getEntries().isEmpty()) {
            log.error("Could not resolve mob summon info for item ID : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check field limit
            final Field field = locked.get().getField();
            if (field.hasFieldOption(FieldOption.SUMMONLIMIT) && !field.getMapInfo().getAllowedItems().contains(itemId)) {
                log.error("Tried to use mob summon item in a restricted field");
                user.dispose();
                return;
            }
            final Optional<Foothold> footholdResult = field.getFootholdBelow(user.getX(), user.getY());

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Summon mobs
            for (Tuple<Integer, Integer> entry : mobSummonInfoResult.get().getEntries()) {
                final int templateId = entry.getLeft();
                final int prop = entry.getRight();
                if (!Util.succeedProp(prop)) {
                    continue;
                }
                final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
                if (mobTemplateResult.isEmpty()) {
                    log.error("Could not resolve mob template ID : {}", templateId);
                    continue;
                }
                final Mob mob = new Mob(
                        mobTemplateResult.get(),
                        null,
                        user.getX(),
                        user.getY(),
                        footholdResult.map(Foothold::getSn).orElse(user.getFoothold())
                );
                field.getMobPool().addMob(mob);
            }
        }
    }

    @Handler(InHeader.UserPetFoodItemUseRequest)
    public static void handleUserPetFoodItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Check item
        if (!ItemConstants.isPetFoodItem(itemId)) {
            log.error("Received UserPetFoodItemUseRequest with an invalid pet food item {}", itemId);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo ii = itemInfoResult.get();
        final int incFullness = ii.getSpec(ItemSpecType.inc);

        try (var locked = user.acquire()) {
            // Select target pet
            Pet target = null;
            for (Pet pet : user.getPets()) {
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
                log.error("Could not resolve pet index for item : {}", target.getItemSn());
                user.dispose();
                return;
            }
            final long petSn = target.getItemSn();
            final int petIndex = petIndexResult.get();

            // Resolve pet item
            final InventoryManager im = user.getInventoryManager();
            final Optional<Map.Entry<Integer, Item>> itemEntry = im.getCashInventory().getItems().entrySet().stream()
                    .filter((entry) -> entry.getValue().getItemSn() == petSn)
                    .findFirst();
            if (itemEntry.isEmpty()) {
                log.error("Could not resolve pet item : {}", target.getItemSn());
                user.dispose();
                return;
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
                user.write(UserLocal.effect(Effect.petLevelUp(petIndex)));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.petLevelUp(petIndex)), user);
            }

            // Broadcast pet action
            user.getField().broadcastPacket(PetPacket.petActionFeed(user, petIndex, success, false));
        }
    }

    @Handler(InHeader.UserScriptItemUseRequest)
    public static void handleUserScriptItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        if (!ItemConstants.isScriptRunItem(itemId)) {
            log.error("Received UserScriptItemUseRequest with an invalid script run item {}", itemId);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo itemInfo = itemInfoResult.get();

        // Check item
        try (var locked = user.acquire()) {
            final InventoryManager im = locked.get().getInventoryManager();
            final Item item = im.getInventoryByItemId(itemId).getItem(position);
            if (item == null || item.getItemId() != itemId) {
                log.error("Tried to use an item in position {} as item ID : {}", position, itemId);
                user.dispose();
                return;
            }
        }

        // Dispatch item script
        final String scriptName = itemInfo.getScript();
        if (scriptName == null || scriptName.isEmpty()) {
            log.error("Could not resolve script for item : {}", itemId);
            user.dispose();
            return;
        }
        final int speakerId = itemInfo.getSpec(ItemSpecType.npc, 9010000); // Maple Administrator
        ScriptDispatcher.startItemScript(user, scriptName, speakerId);
    }

    @Handler(InHeader.UserSkillLearnItemUseRequest)
    public static void handleUserSkillLearnItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt();

        final boolean isMasteryBook = ItemConstants.isMasteryBookItem(itemId);
        if (!ItemConstants.isSkillLearnItem(itemId)) {
            log.error("Received UserSkillLearnItemUseRequest with an invalid skill learn item {}", itemId);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo itemInfo = itemInfoResult.get();
        final int masterLevel = itemInfo.getInfo(ItemInfoType.masterLevel, 0);
        final int reqSkillLevel = itemInfo.getInfo(ItemInfoType.reqSkillLevel, 0);
        final List<Integer> skill = itemInfo.getSkill();
        if (masterLevel <= 0 || skill.isEmpty()) {
            log.error("Invalid skill learn item {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check requirements
            final SkillManager sm = locked.get().getSkillManager();
            final Optional<Integer> skillIdResult = skill.stream()
                    .filter((skillId) -> {
                        if (reqSkillLevel > 0) {
                            final Optional<SkillRecord> skillRecordResult = sm.getSkill(skillId);
                            if (skillRecordResult.isEmpty()) {
                                return false;
                            }
                            final SkillRecord skillRecord = skillRecordResult.get();
                            return skillRecord.getSkillLevel() >= reqSkillLevel && skillRecord.getMasterLevel() < masterLevel;
                        } else {
                            final int skillRoot = SkillConstants.getSkillRoot(skillId);
                            return JobConstants.isCorrectJobForSkillRoot(user.getJob(), skillRoot) && sm.getSkill(skillId).isEmpty();
                        }
                    })
                    .findAny();
            if (skillIdResult.isEmpty()) {
                user.write(WvsContext.skillLearnItemResult(user.getCharacterId(), isMasteryBook, false, false, true));
                return;
            }
            final int skillId = skillIdResult.get();

            // Resolve skill
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("Could not resolve skill info for skill ID : {}", skillId);
                user.write(WvsContext.skillLearnItemResult(user.getCharacterId(), isMasteryBook, false, false, true));
                return;
            }

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), false));

            final boolean success = Util.succeedProp(itemInfo.getInfo(ItemInfoType.success));
            if (success) {
                // Update skill record
                final SkillRecord skillRecord = new SkillRecord(skillId);
                skillRecord.setSkillLevel(user.getSkillLevel(skillId));
                skillRecord.setMasterLevel(itemInfo.getInfo(ItemInfoType.masterLevel));
                sm.addSkill(skillRecord);
                user.write(WvsContext.changeSkillRecordResult(skillRecord, false));
            }
            user.write(WvsContext.skillLearnItemResult(user.getCharacterId(), isMasteryBook, true, success, true));
            user.getField().broadcastPacket(WvsContext.skillLearnItemResult(user.getCharacterId(), isMasteryBook, true, success, false), user);
        }
    }

    @Handler(InHeader.UserPortalScrollUseRequest)
    public static void handleUserPortalScrollUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt();

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check portal scroll can be used
            final Field field = locked.get().getField();
            if (field.hasFieldOption(FieldOption.PORTALSCROLLLIMIT)) {
                user.write(MessagePacket.system("You can't use it here in this map."));
                user.dispose();
                return;
            }
            final int moveTo = itemInfoResult.get().getSpec(ItemSpecType.moveTo);
            if (moveTo != GameConstants.UNDEFINED_FIELD_ID && field.isConnected(moveTo)) {
                user.write(MessagePacket.system("You cannot go to that place."));
                user.dispose();
                return;
            }

            // Resolve target field
            final int destinationFieldId = moveTo == GameConstants.UNDEFINED_FIELD_ID ? field.getReturnMap() : moveTo;
            final Optional<Field> destinationFieldResult = user.getConnectedServer().getFieldById(destinationFieldId);
            if (destinationFieldResult.isEmpty()) {
                log.error("Could not resolve field ID : {}", destinationFieldId);
                user.write(MessagePacket.system("You cannot go to that place."));
                user.dispose();
                return;
            }
            final Field destinationField = destinationFieldResult.get();
            final Optional<PortalInfo> destinationPortalResult = destinationField.getRandomStartPoint();
            if (destinationPortalResult.isEmpty()) {
                log.error("Could not resolve start point portal for field ID : {}", destinationFieldId);
                user.write(MessagePacket.system("You cannot go to that place."));
                user.dispose();
                return;
            }

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                log.error("Failed to consume portal scroll item {} in position {}", itemId, position);
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Move to field
            user.warp(destinationField, destinationPortalResult.get(), false, false);
        }
    }

    @Handler(InHeader.UserLotteryItemUseRequest)
    public static void handleUserLotteryItemUseRequest(User user, InPacket inPacket) {
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt();

        // Resolve reward info
        final Optional<ItemRewardInfo> itemRewardInfoResult = ItemProvider.getItemRewardInfo(itemId);
        if (itemRewardInfoResult.isEmpty()) {
            log.error("Received UserLotteryItemUseRequest with an invalid lottery item {}", itemId);
            user.dispose();
            return;
        }
        final ItemRewardInfo itemRewardInfo = itemRewardInfoResult.get();

        try (var locked = user.acquire()) {
            // Resolve reward
            if (!itemRewardInfo.canAddReward(locked)) {
                user.write(MessagePacket.system("You do not have enough inventory space."));
                user.dispose();
                return;
            }
            final Optional<ItemRewardEntry> rewardResult = Util.getRandomFromCollection(itemRewardInfo.getEntries(), ItemRewardEntry::getProbability);
            if (rewardResult.isEmpty()) {
                log.error("Could not resolve lottery item reward for item {}", itemId);
                return;
            }
            final ItemRewardEntry rewardEntry = rewardResult.get();
            final Optional<ItemInfo> rewardItemInfoResult = ItemProvider.getItemInfo(rewardEntry.getItemId());
            if (rewardItemInfoResult.isEmpty()) {
                log.error("Could not resolve item info for item ID : {}", rewardEntry.getItemId());
                return;
            }

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                log.error("Failed to consume lottery item {} in position {}", itemId, position);
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), false));

            // Add reward item
            final Item rewardItem = rewardItemInfoResult.get().createItem(user.getNextItemSn(), rewardEntry.getCount());
            if (rewardEntry.getPeriod() > 0) {
                rewardItem.setDateExpire(Instant.now().plus(rewardEntry.getPeriod(), ChronoUnit.MINUTES));
            }
            final Optional<List<InventoryOperation>> addResult = user.getInventoryManager().addItem(rewardItem);
            if (addResult.isEmpty()) {
                throw new IllegalStateException("Could not add reward item to inventory");
            }
            user.write(WvsContext.inventoryOperation(addResult.get(), true));
            if (rewardEntry.hasEffect()) {
                user.write(UserLocal.effect(Effect.lotteryUse(itemId, rewardEntry.getEffect())));
            }
        }
    }

    @Handler(InHeader.PetStatChangeItemUseRequest)
    public static void handlePetStatChangeItemUseRequest(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong();
        inPacket.decodeBoolean(); // bBuffSkill
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Resolve pet
        if (user.getPetIndex(petSn).isEmpty()) {
            log.error("Received PetStatChangeItemUseRequest for invalid pet SN : {}", petSn);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check field limit
            final Field field = locked.get().getField();
            if (field.hasFieldOption(FieldOption.STATCHANGEITEMCONSUMELIMIT) && !field.getMapInfo().getAllowedItems().contains(itemId)) {
                log.error("Tried to use stat change item by pet in a restricted field");
                user.dispose();
                return;
            }

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Apply stat change
            changeStat(locked, itemInfoResult.get());
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    protected static Optional<InventoryOperation> consumeItem(Locked<User> locked, int position, int itemId) {
        final User user = locked.get();
        final InventoryType inventoryType = InventoryType.getByItemId(itemId);
        if (inventoryType != InventoryType.CONSUME) {
            log.error("Tried to use an invalid consume item : {}", itemId);
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

    protected static void changeStat(Locked<User> locked, ItemInfo itemInfo) {
        final User user = locked.get();
        user.setConsumeItemEffect(itemInfo);
    }
}
