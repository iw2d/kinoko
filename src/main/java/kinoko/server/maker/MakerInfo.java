package kinoko.server.maker;

import java.util.List;

public class MakerInfo {
    private int itemId;                 // Unique ID of the maker item
    private int reqLevel;           // Required level to make the item
    private int reqSkillLevel;      // Required skill level to make the item
    private int itemNum;            // Number of items produced
    private int tuc;                // Slot value
    private int meso;               // Mesos required
    private int catalyst;           // Catalyst item ID (optional)
    private  List<Recipe> recipes;   // List of recipes
    private  List<Reward> rewards;   // List of random rewards

    public MakerInfo(int itemId, int reqLevel, int reqSkillLevel, int itemNum, int tuc, int meso, int catalyst, List<Recipe> recipes, List<Reward> rewards) {
        this.itemId = itemId;
        this.reqLevel = reqLevel;
        this.reqSkillLevel = reqSkillLevel;
        this.itemNum = itemNum;
        this.tuc = tuc;
        this.meso = meso;
        this.catalyst = catalyst;
        this.recipes = recipes;
        this.rewards = rewards;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int id) {
        this.itemId = id;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    public int getReqSkillLevel() {
        return reqSkillLevel;
    }

    public void setReqSkillLevel(int reqSkillLevel) {
        this.reqSkillLevel = reqSkillLevel;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public int getTuc() {
        return tuc;
    }

    public void setTuc(int tuc) {
        this.tuc = tuc;
    }

    public int getMeso() {
        return meso;
    }

    public void setMeso(int meso) {
        this.meso = meso;
    }

    public int getCatalyst() {
        return catalyst;
    }

    public void setCatalyst(int catalyst) {
        this.catalyst = catalyst;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    //  Recipe class
    public static class Recipe {
        private int itemId;  // ID of the item in the recipe
        private int count;   // Number of items required

        public Recipe(int itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    // Reward class
    public static class Reward {
        private int itemId;  // Reward item id
        private int count;   // Item count
        private int prob;    // Probability

        public Reward(int itemId, int count, int prob) {
            this.itemId = itemId;
            this.count = count;
            this.prob = prob;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getProb() {
            return prob;
        }

        public void setProb(int prob) {
            this.prob = prob;
        }
    }
}

