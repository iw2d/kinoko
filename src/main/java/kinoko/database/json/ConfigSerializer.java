package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.world.GameConstants;
import kinoko.world.user.data.ConfigManager;
import kinoko.world.user.data.FuncKeyMapped;
import kinoko.world.user.data.FuncKeyType;
import kinoko.world.user.data.SingleMacro;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;

import static kinoko.database.schema.ConfigSchema.*;

public final class ConfigSerializer implements JsonSerializer<ConfigManager> {
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @Override
    public JSONObject serialize(ConfigManager cm) {
        if (cm == null) {
            throw new NullPointerException();
        }
        // Prepare macro sys data
        final List<SingleMacro> macroSysData = cm.getMacroSysData();
        final ByteBuffer msdBuffer = ByteBuffer.allocate(1 + macroSysData.size() * 26);
        msdBuffer.order(BYTE_ORDER);
        msdBuffer.put((byte) macroSysData.size());
        for (SingleMacro macro : macroSysData) {
            final byte[] nameArray = new byte[13];
            final byte[] nameBytes = macro.getName().getBytes();
            System.arraycopy(nameBytes, 0, nameArray, 0, Math.min(nameArray.length, nameBytes.length));
            msdBuffer.put(nameArray);
            msdBuffer.put((byte) (macro.isMute() ? 1 : 0));
            for (int skillId : macro.getSkills()) {
                msdBuffer.putInt(skillId);
            }
        }
        // Prepare func key map
        final FuncKeyMapped[] funcKeyMap = cm.getFuncKeyMap();
        final ByteBuffer fkmBuffer = ByteBuffer.allocate(GameConstants.FUNC_KEY_MAP_SIZE * 5);
        fkmBuffer.order(BYTE_ORDER);
        for (FuncKeyMapped mapped : funcKeyMap) {
            fkmBuffer.put(mapped.getType().getValue());
            fkmBuffer.putInt(mapped.getId());
        }
        // Prepare quickslot key map
        final int[] quickslotKeyMap = cm.getQuickslotKeyMap();
        final ByteBuffer qkmBuffer = ByteBuffer.allocate(GameConstants.QUICKSLOT_KEY_MAP_SIZE * 4);
        qkmBuffer.order(BYTE_ORDER);
        for (int key : quickslotKeyMap) {
            qkmBuffer.putInt(key);
        }

        final JSONObject object = new JSONObject();
        object.put(MACRO_SYS_DATA, Base64.getEncoder().encodeToString(msdBuffer.array()));
        object.put(FUNC_KEY_MAP, Base64.getEncoder().encodeToString(fkmBuffer.array()));
        object.put(QUICKSLOT_KEY_MAP, Base64.getEncoder().encodeToString(qkmBuffer.array()));
        object.put(PET_CONSUME_ITEM, cm.getPetConsumeItem());
        object.put(PET_CONSUME_MP_ITEM, cm.getPetConsumeMpItem());
        object.put(PET_EXCEPTION_LIST, new JSONArray(cm.getPetExceptionList()));
        return object;
    }

    @Override
    public ConfigManager deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        // Load macro sys data
        final byte[] msdBytes = Base64.getDecoder().decode(object.getString(MACRO_SYS_DATA));
        final ByteBuffer msdBuffer = ByteBuffer.wrap(msdBytes).order(BYTE_ORDER);
        final int msdSize = msdBuffer.get();
        final List<SingleMacro> macroSysData = new java.util.ArrayList<>();
        for (int i = 0; i < msdSize; i++) {
            final byte[] nameArray = new byte[13];
            msdBuffer.get(nameArray);
            final String name = new String(nameArray).trim();
            final boolean mute = msdBuffer.get() != 0;
            final int[] skills = new int[GameConstants.MACRO_SKILL_COUNT];
            for (int j = 0; j < skills.length; j++) {
                skills[j] = msdBuffer.getInt();
            }
            macroSysData.add(new SingleMacro(name, mute, skills));
        }
        // Load func key map
        final byte[] fkmBytes = Base64.getDecoder().decode(object.getString(FUNC_KEY_MAP));
        final ByteBuffer fkmBuffer = ByteBuffer.wrap(fkmBytes).order(BYTE_ORDER);
        final FuncKeyMapped[] funcKeyMap = new FuncKeyMapped[GameConstants.FUNC_KEY_MAP_SIZE];
        for (int i = 0; i < funcKeyMap.length; i++) {
            final FuncKeyType type = FuncKeyType.getByValue(fkmBuffer.get());
            final int id = fkmBuffer.getInt();
            funcKeyMap[i] = type != null ? FuncKeyMapped.of(type, id) : FuncKeyMapped.none();
        }
        // Load quickslot key map
        final byte[] qkmBytes = Base64.getDecoder().decode(object.getString(QUICKSLOT_KEY_MAP));
        final ByteBuffer qkmBuffer = ByteBuffer.wrap(qkmBytes).order(BYTE_ORDER);
        final int[] quickslotKeyMap = new int[GameConstants.QUICKSLOT_KEY_MAP_SIZE];
        for (int i = 0; i < quickslotKeyMap.length; i++) {
            quickslotKeyMap[i] = qkmBuffer.getInt();
        }
        // Initialize config manager
        final ConfigManager cm = new ConfigManager(funcKeyMap, quickslotKeyMap);
        cm.updateMacroSysData(macroSysData);
        cm.setPetConsumeItem(object.getIntValue(PET_CONSUME_ITEM));
        cm.setPetConsumeMpItem(object.getIntValue(PET_CONSUME_MP_ITEM));
        cm.setPetExceptionList(object.getJSONArray(PET_EXCEPTION_LIST).toList(Integer.class));
        return cm;
    }
}
