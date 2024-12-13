package kinoko.server.maker;


import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.InPacket;
import kinoko.util.Tuple;
import kinoko.world.item.*;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

public class ItemMakerHandler {

    public static void ItemMaker(final InPacket inPacket, final User user) {
        final int makerType = inPacket.decodeInt();

        switch (makerType) {
            case 1: // Gem Creation
                handleGemCreation(inPacket, user);
                break;
            case 3: // Creating Crystals
                handleCrystalCreation(inPacket, user);
                break;
            case 4: // Disassembling Equipment
                handleEquipmentDisassembly(inPacket, user);
                break;
            default:
                user.write(UserRemote.effect(user, Effect.ItemMakerResult(false))); // Invalid type
                break;
        }
    }

    private static void handleGemCreation(final InPacket inPacket, final User user) {
        final int itemToCreate = inPacket.decodeInt();

        if (ItemConstants.isGem(itemToCreate)) {
            createGem(inPacket, user, itemToCreate);
        } else if (ItemConstants.isOtherGem(itemToCreate)) {
            createOtherGem(inPacket, user, itemToCreate);
        } else {
            createCustomItem(inPacket, user, itemToCreate);
        }
    }

    private static void createGem(final InPacket inPacket, final User user, final int gemId) {
        final ItemMakerFactory.GemCreateEntry gemInfo = ItemMakerFactory.getInstance().getGemInfo(gemId);
        if (gemInfo == null || !hasRequiredSkill(user, gemInfo.getReqSkillLevel()) || !hasSufficientMoney(user, gemInfo.getCost())) {
            return;
        }

        final int randomGem = getRandomGem(gemInfo.getRandomRewards());
        if (isInventoryFull(user, randomGem) || !user.getInventoryManager().hasItems(user, gemInfo.getReqRecipes())) {
            System.out.println("User does not have required items. " + gemInfo.getReqRecipes());
            return;
        }

        //Remove items
        for (final Tuple<Integer, Integer> requiredItems : gemInfo.getReqRecipes()) {
            final Optional<List<InventoryOperation>> removeResult = user.getInventoryManager().removeItem(requiredItems.getLeft(), requiredItems.getRight());
            if (removeResult.isEmpty()) {
                throw new IllegalStateException("Failed to remove an item from inventory with ID: " + requiredItems.getLeft());
            }

            user.write(WvsContext.inventoryOperation(removeResult.get(), false));
        }

        //Remove money
        if(!user.getInventoryManager().addMoney(-gemInfo.getCost())) {
            return; // Mesos not removed
        }
        user.write(WvsContext.statChanged(Stat.MONEY, user.getInventoryManager().getMoney(), true));

        //Gain item to create
        user.getInventoryManager().addItemToInventory(user, randomGem, (byte) 1);
        triggerMakerSuccess(user);
    }

    private static void createOtherGem(final InPacket inPacket, final User user, final int itemToCreate) {
        final ItemMakerFactory.GemCreateEntry gemInfo = ItemMakerFactory.getInstance().getGemInfo(itemToCreate);
        if (gemInfo == null || !hasRequiredSkill(user, gemInfo.getReqSkillLevel()) || !hasSufficientMoney(user, gemInfo.getCost())) {
            return;
        }

        if (isInventoryFull(user, itemToCreate) || !user.getInventoryManager().hasItems(user, gemInfo.getReqRecipes())) {
            return;
        }

        //Remove items
        for (final Tuple<Integer, Integer> requiredItems : gemInfo.getReqRecipes()) {
            final Optional<List<InventoryOperation>> removeResult = user.getInventoryManager().removeItem(requiredItems.getLeft(), requiredItems.getRight());
            if (removeResult.isEmpty()) {
                throw new IllegalStateException("Failed to remove an item from inventory with ID: " + requiredItems.getLeft());
            }

            user.write(WvsContext.inventoryOperation(removeResult.get(), false));
        }

        //Remove money
        if(!user.getInventoryManager().addMoney(-gemInfo.getCost())) {
            return; // Mesos not removed
        }
        user.write(WvsContext.statChanged(Stat.MONEY, user.getInventoryManager().getMoney(), true));

        //Gain item to create
        user.getInventoryManager().addItemToInventory(user, itemToCreate, (byte) 1);
        triggerMakerSuccess(user);
    }

    private static void createCustomItem(final InPacket inPacket, final User user, final int itemToCreate) {
        final boolean useStimulator = inPacket.decodeByte() > 0;
        final int numEnchantItems = inPacket.decodeInt();
        final ItemMakerFactory.ItemMakerCreateEntry createInfo = ItemMakerFactory.getInstance().getCreateInfo(itemToCreate);

        if (createInfo == null || numEnchantItems > createInfo.getTUC() || !hasRequiredSkill(user, createInfo.getReqSkillLevel()) || !hasSufficientMoney(user, createInfo.getCost())) {
            return;
        }

        if (isInventoryFull(user, itemToCreate) || !user.getInventoryManager().hasItems(user, createInfo.getReqItems())) {
            return;
        }
        //Remove items
        for (final Tuple<Integer, Integer> requiredItems : createInfo.getReqItems()) {
            final Optional<List<InventoryOperation>> removeResult = user.getInventoryManager().removeItem(requiredItems.getLeft(), requiredItems.getRight());
            if (removeResult.isEmpty()) {
                throw new IllegalStateException("Failed to remove an item from inventory with ID: " + requiredItems.getLeft());
            }

            user.write(WvsContext.inventoryOperation(removeResult.get(), false));
        }

        //Remove money
        if(!user.getInventoryManager().addMoney(-createInfo.getCost())) {
            return; // Mesos not removed
        }
        user.write(WvsContext.statChanged(Stat.MONEY, user.getInventoryManager().getMoney(), true));

        //Gain a custom item
        ItemInfo toCreateInfo = ItemProvider.getItemInfo(itemToCreate).orElse(null);
        if(toCreateInfo == null) {
            return;
        }

        final Item newItem = toCreateInfo.createItem(user.getNextItemSn());

        applyCustomEnchantments(inPacket, user, newItem.getItemId(), useStimulator, numEnchantItems, createInfo);

        user.getInventoryManager().addItemToInventory(user, newItem.getItemId(), 1);
        triggerMakerSuccess(user);
    }

    private static void handleCrystalCreation(final InPacket inPacket, final User user) {
        final int materialId = inPacket.decodeInt();
        if (!user.getInventoryManager().hasItem(materialId, 100)) {
            return;
        }

        final int crystalId = getCreateCrystal(user);
        user.getInventoryManager().addItemToInventory(user, crystalId, (short) 1);
        user.getInventoryManager().removeItem(materialId, 100);

        triggerMakerSuccess(user);
    }

    private static void handleEquipmentDisassembly(final InPacket inPacket, final User user) {
        final int itemId = inPacket.decodeInt();
        final byte slot = (byte) inPacket.decodeInt();

        final Item itemToDisassemble = user.getInventoryManager().getInventoryByType(InventoryType.EQUIP).getItem(slot);
        if (itemToDisassemble == null || itemToDisassemble.getItemId() != itemId || itemToDisassemble.getQuantity() < 1) {
            return;
        }
        // Disassemble logic to be implemented
        triggerMakerSuccess(user);
    }

    private static boolean hasRequiredSkill(final User user, final int requiredLevel) {
        return user.getSkillLevel(1007) >= requiredLevel;
    }

    private static boolean hasSufficientMoney(final User user, final int cost) {
        return user.getInventoryManager().getMoney() >= cost;
    }

    private static boolean isInventoryFull(final User user, final int itemId) {
        return !user.getInventoryManager().canAddItem(itemId, 1);
    }

    private static void applyCustomEnchantments(final InPacket inPacket, final User user, final int itemId, final boolean useStimulator, final int numEnchantItems, final ItemMakerFactory.ItemMakerCreateEntry createInfo) {
        if (useStimulator && user.getInventoryManager().hasItem(createInfo.getStimulator(), 1)) {
            randomizeStats(user, itemId);
            user.getInventoryManager().removeItem(createInfo.getStimulator(), 1);
        }

        for (int i = 0; i < numEnchantItems; i++) {
            final int enchantItemId = inPacket.decodeInt();
            if (user.getInventoryManager().hasItem(enchantItemId, 1)) {
                final Map<String, Byte> enchantStats = ItemProvider.getItemMakeStats(enchantItemId);
                if (enchantStats != null) {
                    addEnchantStats(enchantStats, itemId, user);
                    user.getInventoryManager().removeItem(enchantItemId, 1);
                }
            }
        }
    }

    private static int getRandomGem(final List<Tuple<Integer, Integer>> rewards) {
        final List<Integer> items = new ArrayList<>();
        for (final Tuple<Integer, Integer> reward : rewards) {
            for (int i = 0; i < reward.getRight(); i++) {
                items.add(reward.getLeft());
            }
        }
        return items.get(RandomGenerator.getDefault().nextInt(items.size()));
    }

    private static int getCreateCrystal(final User user) {
        //TODO : Not sure if this is the right level to fetch
        final int level = user.getLevel();
        if (level >= 121) return 4260008;
        if (level >= 111) return 4260007;
        if (level >= 101) return 4260006;
        if (level >= 91) return 4260005;
        if (level >= 81) return 4260004;
        if (level >= 71) return 4260003;
        if (level >= 61) return 4260002;
        if (level >= 51) return 4260001;
        if (level >= 31) return 4260000;
        throw new IllegalArgumentException("Invalid level for creating crystal.");
    }

    private static void triggerMakerSuccess(final User user) {
        user.write(UserRemote.effect(user, Effect.ItemMakerResult(true)));
        user.getField().broadcastPacket(UserRemote.effect(user, Effect.ItemMakerResultTo(user, true)));
    }

    private static void addEnchantStats(final Map<String, Byte> stats, final int itemId, User user) {
        Optional<ItemInfo> info = ItemProvider.getItemInfo(itemId);
        if (info.isEmpty()) {
            return;
        }

        final Item newItem = info.get().createItem(user.getNextItemSn());
        final EquipData equipData = newItem.getEquipData();

        stats.forEach((stat, value) -> {
            if (value != 0) {
                applyStat(equipData, stat, value);
            }
        });
    }

    private static void applyStat(EquipData equipData, String stat, byte value) {
        switch (stat) {
            case "incPAD" -> equipData.setIncPad((short) (equipData.getIncPad() + value));
            case "incMAD" -> equipData.setIncMad((short) (equipData.getIncMad() + value));
            case "incACC" -> equipData.setIncAcc((short) (equipData.getIncAcc() + value));
            case "incEVA" -> equipData.setIncEva((short) (equipData.getIncEva() + value));
            case "incSpeed" -> equipData.setIncSpeed((short) (equipData.getIncSpeed() + value));
            case "incJump" -> equipData.setIncJump((short) (equipData.getIncJump() + value));
            case "incMaxHP" -> equipData.setIncMaxHp((short) (equipData.getIncMaxHp() + value));
            case "incMaxMP" -> equipData.setIncMaxMp((short) (equipData.getIncMaxMp() + value));
            case "incSTR" -> equipData.setIncStr((short) (equipData.getIncStr() + value));
            case "incDEX" -> equipData.setIncDex((short) (equipData.getIncDex() + value));
            case "incINT" -> equipData.setIncInt((short) (equipData.getIncInt() + value));
            case "incLUK" -> equipData.setIncLuk((short) (equipData.getIncLuk() + value));
        }
    }

    private static short getRandStat(final short defaultValue, final int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        // No more than ceil of 10% of stat
        final int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue - lMaxRange) + Math.floor(Math.random() * (lMaxRange * 2 + 1)));
    }

    public static void randomizeStats(final User user, final int itemId) {
        ItemInfo info = ItemProvider.getItemInfo(itemId).orElse(null);

        // Should not happen but for shits
        if (info == null) {
            return;
        }

        EquipData data = EquipData.from(info);

        data.setIncStr(getRandStat(data.getIncStr(), 5));
        data.setIncDex(getRandStat(data.getIncDex(), 5));
        data.setIncInt(getRandStat(data.getIncInt(), 5));
        data.setIncLuk(getRandStat(data.getIncLuk(), 5));
        data.setIncMad(getRandStat(data.getIncMad(), 5));
        data.setIncPad(getRandStat(data.getIncPad(), 5));
        data.setIncAcc(getRandStat(data.getIncAcc(), 5));
        data.setIncEva(getRandStat(data.getIncEva(), 5));
        data.setIncJump(getRandStat(data.getIncJump(), 5));
        data.setIncAcc(getRandStat(data.getIncAcc(), 5));
        data.setIncSpeed(getRandStat(data.getIncSpeed(), 5));
        data.setIncPdd(getRandStat(data.getIncPdd(), 10));
        data.setIncMdd(getRandStat(data.getIncMdd(), 10));
        data.setIncMaxMp(getRandStat(data.getIncMaxHp(), 10));
        data.setIncMaxMp(getRandStat(data.getIncMaxHp(), 10));
    }

}

