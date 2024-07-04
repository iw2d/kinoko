package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.ConfigUDT;
import kinoko.world.GameConstants;
import kinoko.world.user.info.ConfigManager;
import kinoko.world.user.info.FuncKeyMapped;
import kinoko.world.user.info.FuncKeyType;
import kinoko.world.user.info.SingleMacro;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public final class ConfigCodec extends MappingCodec<UdtValue, ConfigManager> {
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    public ConfigCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<ConfigManager> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected ConfigManager innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        // Load macro sys data
        final ByteBuffer msdBuffer = value.getByteBuffer(ConfigUDT.MACRO_SYS_DATA);
        assert msdBuffer != null;
        msdBuffer.order(BYTE_ORDER);
        final List<SingleMacro> macroSysData = new ArrayList<>();
        final int msdSize = msdBuffer.get();
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
        final ByteBuffer fkmBuffer = value.getByteBuffer(ConfigUDT.FUNC_KEY_MAP);
        assert fkmBuffer != null;
        fkmBuffer.order(BYTE_ORDER);
        final FuncKeyMapped[] funcKeyMap = new FuncKeyMapped[GameConstants.FUNC_KEY_MAP_SIZE];
        for (int i = 0; i < funcKeyMap.length; i++) {
            final FuncKeyType type = FuncKeyType.getByValue(fkmBuffer.get());
            final int id = fkmBuffer.getInt();
            funcKeyMap[i] = type != null ? FuncKeyMapped.of(type, id) : FuncKeyMapped.none();
        }
        // Load quickslot key map
        final ByteBuffer qkmBuffer = value.getByteBuffer(ConfigUDT.QUICKSLOT_KEY_MAP);
        assert qkmBuffer != null;
        qkmBuffer.order(BYTE_ORDER);
        final int[] quickslotKeyMap = new int[GameConstants.QUICKSLOT_KEY_MAP_SIZE];
        for (int i = 0; i < quickslotKeyMap.length; i++) {
            quickslotKeyMap[i] = qkmBuffer.getInt();
        }
        // Initialize config manager
        final ConfigManager cm = new ConfigManager(funcKeyMap, quickslotKeyMap);
        cm.updateMacroSysData(macroSysData);
        cm.setPetConsumeItem(value.getInt(ConfigUDT.PET_CONSUME_ITEM));
        cm.setPetConsumeMpItem(value.getInt(ConfigUDT.PET_CONSUME_MP_ITEM));
        return cm;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable ConfigManager cm) {
        if (cm == null) {
            return null;
        }
        // Prepare macro sys data
        final List<SingleMacro> macroSysData = cm.getMacroSysData();
        final ByteBuffer msdBuffer = ByteBuffer.allocate(1 + macroSysData.size() * 26);
        msdBuffer.order(BYTE_ORDER);
        msdBuffer.put((byte) macroSysData.size());
        for (SingleMacro macro : macroSysData) {
            final byte[] nameArray = new byte[13];
            final byte[] nameBytes = macro.getName().getBytes();
            System.arraycopy(macro.getName().getBytes(), 0, nameArray, 0, Math.min(nameArray.length, nameBytes.length));
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
        for (FuncKeyMapped funcKeyMapped : funcKeyMap) {
            fkmBuffer.put(funcKeyMapped.getType().getValue());
            fkmBuffer.putInt(funcKeyMapped.getId());
        }
        // Prepare quickslot key map
        final int[] quickslotKeyMap = cm.getQuickslotKeyMap();
        final ByteBuffer qkmBuffer = ByteBuffer.allocate(GameConstants.QUICKSLOT_KEY_MAP_SIZE * 4);
        qkmBuffer.order(BYTE_ORDER);
        for (int key : quickslotKeyMap) {
            qkmBuffer.putInt(key);
        }
        return getCqlType().newValue()
                .setByteBuffer(ConfigUDT.MACRO_SYS_DATA, msdBuffer.position(0))
                .setByteBuffer(ConfigUDT.FUNC_KEY_MAP, fkmBuffer.position(0))
                .setByteBuffer(ConfigUDT.QUICKSLOT_KEY_MAP, qkmBuffer.position(0))
                .setInt(ConfigUDT.PET_CONSUME_ITEM, cm.getPetConsumeItem())
                .setInt(ConfigUDT.PET_CONSUME_MP_ITEM, cm.getPetConsumeMpItem());
    }
}
