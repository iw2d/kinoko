package kinoko.server.maker;

import kinoko.provider.EtcProvider;
import kinoko.util.Tuple;
import kinoko.world.item.ItemConstants;

import java.util.*;

public class ItemMakerFactory {

    private static ItemMakerFactory instance;
    protected final static Map<Integer, ItemMakerCreateEntry> createCache =
            Collections.synchronizedMap(new HashMap<>());
    protected final static Map<Integer, GemCreateEntry> gemCache =
            Collections.synchronizedMap(new HashMap<>());

    public static synchronized ItemMakerFactory getInstance() {
        if (instance == null) {
            instance = new ItemMakerFactory();
        }
        return instance;
    }

    protected ItemMakerFactory() {
        initialize();
    }

    public void initialize() {
        createCache.clear();
        gemCache.clear();

        Map<Integer, MakerInfo> makerInfoMap = EtcProvider.getItemMakeInfo();

        if (makerInfoMap == null || makerInfoMap.isEmpty()) {
            System.err.println("ItemMakeInfo data is unavailable. Ensure loadItemMake() was called.");
            return;
        }

        for (var entry : makerInfoMap.entrySet()) {
            int itemId = entry.getKey();
            MakerInfo makerInfo = entry.getValue();

            if (ItemConstants.isGem(makerInfo.getItemId()) || ItemConstants.isOtherGem(makerInfo.getItemId())) { // Gems or special creations
                GemCreateEntry gemEntry = new GemCreateEntry(
                        makerInfo.getMeso(),
                        makerInfo.getReqLevel(),
                        makerInfo.getReqSkillLevel(),
                        makerInfo.getItemNum()
                );

                for (MakerInfo.Reward reward : makerInfo.getRewards()) {
                    gemEntry.addRandomReward(reward.getItemId(), reward.getProb());
                }

                for (MakerInfo.Recipe recipe : makerInfo.getRecipes()) {
                    gemEntry.addReqRecipe(recipe.getItemId(), recipe.getCount());
                }

                gemCache.put(itemId, gemEntry);
                //System.out.println("Added GemCreateEntry for item ID: " + itemId);
            } else { // Standard item creations
                ItemMakerCreateEntry createEntry = new ItemMakerCreateEntry(
                        makerInfo.getMeso(),
                        makerInfo.getReqLevel(),
                        makerInfo.getReqSkillLevel(),
                        makerInfo.getItemNum(),
                        (byte) makerInfo.getTuc(),
                        makerInfo.getCatalyst()
                );

                for (MakerInfo.Recipe recipe : makerInfo.getRecipes()) {
                    createEntry.addReqItem(recipe.getItemId(), recipe.getCount());
                }

                createCache.put(itemId, createEntry);
                //System.out.println("Added ItemMakerCreateEntry for item ID: " + itemId);
            }
        }

        System.out.println("Item Make Initialization complete. createCache size: " + createCache.size()
                + ", gemCache size: " + gemCache.size());
    }

    public GemCreateEntry getGemInfo(int itemId) {
        return gemCache.get(itemId);
    }

    public ItemMakerCreateEntry getCreateInfo(int itemId) {
        return createCache.get(itemId);
    }

    public static class GemCreateEntry {

        private final int reqLevel;
        private final int reqMakerLevel;
        private final int cost;
        private final int quantity;
        private final List<Tuple<Integer, Integer>> randomRewards = new ArrayList<>();
        private final List<Tuple<Integer, Integer>> reqRecipes = new ArrayList<>();

        public GemCreateEntry(int cost, int reqLevel, int reqMakerLevel, int quantity) {
            this.cost = cost;
            this.reqLevel = reqLevel;
            this.reqMakerLevel = reqMakerLevel;
            this.quantity = quantity;
        }

        public int getRewardAmount() {
            return quantity;
        }

        public List<Tuple<Integer, Integer>> getRandomRewards() {
            return randomRewards;
        }

        public List<Tuple<Integer, Integer>> getReqRecipes() {
            return reqRecipes;
        }

        public int getReqLevel() {
            return reqLevel;
        }

        public int getReqSkillLevel() {
            return reqMakerLevel;
        }

        public int getCost() {
            return cost;
        }

        protected void addRandomReward(int itemId, int probability) {
            randomRewards.add(new Tuple<>(itemId, probability));
        }

        protected void addReqRecipe(int itemId, int count) {
            reqRecipes.add(new Tuple<>(itemId, count));
        }
    }

    public static class ItemMakerCreateEntry {

        private final int reqLevel;
        private final int cost;
        private final int quantity;
        private final int stimulator;
        private final byte tuc;
        private final int reqMakerLevel;
        private final List<Tuple<Integer, Integer>> reqItems = new ArrayList<>();
        private final List<Integer> reqEquips = new ArrayList<>();

        public ItemMakerCreateEntry(int cost, int reqLevel, int reqMakerLevel, int quantity, byte tuc, int stimulator) {
            this.cost = cost;
            this.reqLevel = reqLevel;
            this.reqMakerLevel = reqMakerLevel;
            this.quantity = quantity;
            this.tuc = tuc;
            this.stimulator = stimulator;
        }

        public byte getTUC() {
            return tuc;
        }

        public int getRewardAmount() {
            return quantity;
        }

        public List<Tuple<Integer, Integer>> getReqItems() {
            return reqItems;
        }

        public List<Integer> getReqEquips() {
            return reqEquips;
        }

        public int getReqLevel() {
            return reqLevel;
        }

        public int getReqSkillLevel() {
            return reqMakerLevel;
        }

        public int getCost() {
            return cost;
        }

        public int getStimulator() {
            return stimulator;
        }

        protected void addReqItem(int itemId, int amount) {
            reqItems.add(new Tuple<>(itemId, amount));
        }
    }
}
