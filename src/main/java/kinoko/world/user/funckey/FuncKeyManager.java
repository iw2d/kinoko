package kinoko.world.user.funckey;

import kinoko.world.GameConstants;

import java.util.HashMap;
import java.util.Map;

public final class FuncKeyManager {
    private final Map<Integer, FuncKeyMapped> funcKeyMap = new HashMap<>();
    private int[] quickslotKeyMap;
    private int petConsumeItem;
    private int petConsumeMpItem;

    public Map<Integer, FuncKeyMapped> getFuncKeyMap() {
        return funcKeyMap;
    }

    public int[] getQuickslotKeyMap() {
        if (quickslotKeyMap == null) {
            return GameConstants.DEFAULT_QUICKSLOT_KEY_MAP;
        }
        return quickslotKeyMap;
    }

    public void setQuickslotKeyMap(int[] quickslotKeyMap) {
        this.quickslotKeyMap = quickslotKeyMap;
    }

    public int getPetConsumeItem() {
        return petConsumeItem;
    }

    public void setPetConsumeItem(int petConsumeItem) {
        this.petConsumeItem = petConsumeItem;
    }

    public int getPetConsumeMpItem() {
        return petConsumeMpItem;
    }

    public void setPetConsumeMpItem(int petConsumeMpItem) {
        this.petConsumeMpItem = petConsumeMpItem;
    }

    public void updateFuncKeyMap(Map<Integer, FuncKeyMapped> funcKeyMap) {
        this.funcKeyMap.putAll(funcKeyMap);
    }
}
