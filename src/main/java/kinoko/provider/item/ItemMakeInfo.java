package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Triple;
import kinoko.util.Tuple;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.WeaponType;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.maker.MakerConstants;
import kinoko.world.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ItemMakeInfo {
    private final int itemId;
    private final int reqJob;
    private final int reqLevel;
    private final int reqSkillLevel;
    private final int reqItem;
    private final int reqEquip;
    private final int tuc;
    private final int catalyst;
    private final int cost;
    private final List<Tuple<Integer, QuestState>> reqQuest; // quest id, quest state
    private final List<Tuple<Integer, Integer>> recipe; // item id, item count
    private final List<Triple<Integer, Integer, Integer>> randomReward; // item id, item count, probability

    public ItemMakeInfo(int itemId, int reqJob, int reqLevel, int reqSkillLevel, int reqItem, int reqEquip, int tuc, int catalyst, int cost, List<Tuple<Integer, QuestState>> reqQuest, List<Tuple<Integer, Integer>> recipe, List<Triple<Integer, Integer, Integer>> randomReward) {
        this.itemId = itemId;
        this.reqJob = reqJob;
        this.reqLevel = reqLevel;
        this.reqSkillLevel = reqSkillLevel;
        this.reqItem = reqItem;
        this.reqEquip = reqEquip;
        this.tuc = tuc;
        this.catalyst = catalyst;
        this.cost = cost;
        this.reqQuest = reqQuest;
        this.recipe = recipe;
        this.randomReward = randomReward;
    }

    public int getItemId() {
        return itemId;
    }

    public int getReqJob() {
        return reqJob;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public int getReqSkillLevel() {
        return reqSkillLevel;
    }

    public int getReqItem() {
        return reqItem;
    }

    public int getReqEquip() {
        return reqEquip;
    }

    public int getTuc() {
        return tuc;
    }

    public int getCatalyst() {
        return catalyst;
    }

    public int getCost() {
        return cost;
    }

    public List<Tuple<Integer, QuestState>> getReqQuest() {
        return reqQuest;
    }

    public List<Tuple<Integer, Integer>> getRecipe() {
        return recipe;
    }

    public List<Triple<Integer, Integer, Integer>> getRandomReward() {
        return randomReward;
    }

    public boolean canCreateItem(User user, boolean catalyst, List<Integer> gems) {
        final InventoryManager im = user.getInventoryManager();
        final int makerSkillId = SkillConstants.getNoviceSkillAsRace(Beginner.MAKER, user.getJob());
        if (getReqSkillLevel() != 0 && user.getSkillLevel(makerSkillId) < getReqSkillLevel()) {
            return false;
        }
        if (getReqLevel() != 0 && user.getLevel() < getReqLevel()) {
            return false;
        }
        if (getReqItem() != 0 && !user.getInventoryManager().hasItem(getReqItem(), 1)) {
            return false;
        }
        if (getReqEquip() != 0 && !user.getInventoryManager().hasEquipped(getReqEquip())) {
            return false;
        }
        for (var tuple : getReqQuest()) {
            final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(tuple.getLeft());
            if (questRecordResult.isEmpty() || questRecordResult.get().getState() != tuple.getRight()) {
                return false;
            }
        }
        if (!im.canAddMoney(-MakerConstants.getTotalCostToMake(getCost(), catalyst, gems))) {
            return false;
        }
        for (var tuple : getRecipe()) {
            if (!im.hasItem(tuple.getLeft(), tuple.getRight())) {
                return false;
            }
        }
        if (catalyst) {
            if (getCatalyst() == 0) {
                return false;
            }
            if (!im.hasItem(getCatalyst(), 1)) {
                return false;
            }
        }
        if (!gems.isEmpty()) {
            if (gems.size() > getTuc()) {
                return false;
            }
            if (gems.size() != gems.stream().distinct().count()) {
                return false;
            }
            final boolean isWeapon = WeaponType.getByItemId(getItemId()) != WeaponType.NONE;
            for (int gemItemId : gems) {
                if (gemItemId / 10000 != 425) {
                    return false;
                }
                if (gemItemId % 10000 / 100 <= 1 && !isWeapon) {
                    return false;
                }
                if (!im.hasItem(gemItemId, 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canAddReward(User user) {
        final InventoryManager im = user.getInventoryManager();
        if (!im.canAddItem(getItemId(), 1)) {
            return false;
        }
        for (var triple : getRandomReward()) {
            if (!im.canAddItem(triple.getFirst(), triple.getSecond())) {
                return false;
            }
        }
        return true;
    }

    public static ItemMakeInfo from(int itemId, int reqJob, WzListProperty prop) throws ProviderError {
        return new ItemMakeInfo(
                itemId,
                reqJob,
                WzProvider.getInteger(prop.get("reqLevel"), 0),
                WzProvider.getInteger(prop.get("reqSkillLevel"), 0),
                WzProvider.getInteger(prop.get("reqItem"), 0),
                WzProvider.getInteger(prop.get("reqEquip"), 0),
                WzProvider.getInteger(prop.get("tuc"), 0),
                WzProvider.getInteger(prop.get("catalyst"), 0),
                WzProvider.getInteger(prop.get("meso"), 0),
                resolveReqQuest(prop),
                resolveRecipe(prop),
                resolveRandomReward(prop)
        );
    }

    private static List<Tuple<Integer, QuestState>> resolveReqQuest(WzListProperty prop) {
        if (!(prop.get("reqQuest") instanceof WzListProperty questList)) {
            return List.of();
        }
        final List<Tuple<Integer, QuestState>> reqQuest = new ArrayList<>();
        for (var entry : questList.getItems().entrySet()) {
            final int questId = Integer.parseInt(entry.getKey());
            final QuestState questState = QuestState.getByValue(WzProvider.getInteger(entry.getValue()));
            reqQuest.add(Tuple.of(questId, questState));
        }
        return Collections.unmodifiableList(reqQuest);
    }

    private static List<Tuple<Integer, Integer>> resolveRecipe(WzListProperty prop) {
        if (!(prop.get("recipe") instanceof WzListProperty recipeList)) {
            throw new ProviderError("Could not resolve item make recipe");
        }
        final List<Tuple<Integer, Integer>> recipe = new ArrayList<>();
        for (var entry : recipeList.getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzListProperty recipeProp)) {
                throw new ProviderError("Could not resolve item make recipe");
            }
            final int itemId = WzProvider.getInteger(recipeProp.get("item"), 0);
            final int count = WzProvider.getInteger(recipeProp.get("count"), 0);
            if (itemId != 0 && count != 0) {
                recipe.add(Tuple.of(itemId, count));
            }
        }
        return Collections.unmodifiableList(recipe);
    }

    private static List<Triple<Integer, Integer, Integer>> resolveRandomReward(WzListProperty prop) {
        if (!(prop.get("randomReward") instanceof WzListProperty rewardList)) {
            return List.of();
        }
        final List<Triple<Integer, Integer, Integer>> randomReward = new ArrayList<>();
        for (var entry : rewardList.getItems().entrySet()) {
            if (!(entry.getValue() instanceof WzListProperty rewardProp)) {
                throw new ProviderError("Could not resolve item make reward");
            }
            randomReward.add(Triple.of(
                    WzProvider.getInteger(rewardProp.get("item")),
                    WzProvider.getInteger(rewardProp.get("itemNum")),
                    WzProvider.getInteger(rewardProp.get("prob"))
            ));
        }
        return Collections.unmodifiableList(randomReward);
    }
}
