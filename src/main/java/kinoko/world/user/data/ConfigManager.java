package kinoko.world.user.data;

import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ConfigManager {
    private final List<SingleMacro> macroSysData = new ArrayList<>();
    private final FuncKeyMapped[] funcKeyMap;
    private final int[] quickslotKeyMap;
    private int petConsumeItem;
    private int petConsumeMpItem;
    private List<Integer> petExceptionList;

    public ConfigManager(FuncKeyMapped[] funcKeyMap, int[] quickslotKeyMap) {
        assert funcKeyMap.length == GameConstants.FUNC_KEY_MAP_SIZE;
        assert quickslotKeyMap.length == GameConstants.QUICKSLOT_KEY_MAP_SIZE;
        this.funcKeyMap = funcKeyMap;
        this.quickslotKeyMap = quickslotKeyMap;
        this.petConsumeItem = 0;
        this.petConsumeMpItem = 0;
        this.petExceptionList = List.of();
    }

    public List<SingleMacro> getMacroSysData() {
        return macroSysData;
    }

    public FuncKeyMapped[] getFuncKeyMap() {
        return funcKeyMap;
    }

    public int[] getQuickslotKeyMap() {
        return quickslotKeyMap;
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

    public List<Integer> getPetExceptionList() {
        return petExceptionList;
    }

    public void setPetExceptionList(List<Integer> petExceptionList) {
        this.petExceptionList = petExceptionList;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void updateMacroSysData(List<SingleMacro> macroSysData) {
        this.macroSysData.clear();
        this.macroSysData.addAll(macroSysData);
    }

    public void updateFuncKeyMap(Map<Integer, FuncKeyMapped> updates) {
        for (var entry : updates.entrySet()) {
            funcKeyMap[entry.getKey()] = entry.getValue();
        }
    }

    public void updateQuickslotKeyMap(int[] quickslotKeyMap) {
        System.arraycopy(quickslotKeyMap, 0, this.quickslotKeyMap, 0, GameConstants.QUICKSLOT_KEY_MAP_SIZE);
    }

    public static ConfigManager defaults() {
        return new ConfigManager(
                Arrays.copyOf(GameConstants.DEFAULT_FUNC_KEY_MAP, GameConstants.FUNC_KEY_MAP_SIZE),
                Arrays.copyOf(GameConstants.DEFAULT_QUICKSLOT_KEY_MAP, GameConstants.QUICKSLOT_KEY_MAP_SIZE)
        );
    }
}
