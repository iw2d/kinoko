package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.FuncKeyManUDT;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.user.funckey.FuncKeyManager;
import kinoko.world.user.funckey.FuncKeyMapped;
import kinoko.world.user.funckey.FuncKeyType;

import java.util.HashMap;
import java.util.Map;

public final class FuncKeyManCodec extends MappingCodec<UdtValue, FuncKeyManager> {
    public static final String DELIMITER = ",";

    public FuncKeyManCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<FuncKeyManager> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected FuncKeyManager innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        // Load func key map
        final FuncKeyManager fkm = new FuncKeyManager();
        final Map<Integer, FuncKeyMapped> funcKeyMap = new HashMap<>();
        final Map<Integer, String> funcKeyStringMap = value.getMap(FuncKeyManUDT.FUNC_KEY_MAP, Integer.class, String.class);
        if (funcKeyStringMap != null) {
            for (var entry : funcKeyStringMap.entrySet()) {
                if (entry.getKey() >= GameConstants.FUNC_KEY_MAP_SIZE) {
                    continue;
                }
                final String[] split = entry.getValue().split(DELIMITER);
                if (split.length != 2 || !Util.isInteger(split[0]) || !Util.isInteger(split[1])) {
                    continue;
                }
                final FuncKeyType funcKeyType = FuncKeyType.getByValue(Integer.parseInt(split[0]));
                if (funcKeyType == null || funcKeyType == FuncKeyType.NONE) {
                    continue;
                }
                final int funcKeyId = Integer.parseInt(split[1]);
                funcKeyMap.put(entry.getKey(), new FuncKeyMapped(funcKeyType, funcKeyId));
            }
        }
        fkm.updateFuncKeyMap(funcKeyMap);
        // Load quickslot key map
        final int[] quickslotKeyMap = new int[GameConstants.QUICKSLOT_KEY_SIZE];
        final Map<Integer, Integer> quickslotKeyIntegerMap = value.getMap(FuncKeyManUDT.QUICKSLOT_KEY_MAP, Integer.class, Integer.class);
        if (quickslotKeyIntegerMap != null) {
            for (var entry : quickslotKeyIntegerMap.entrySet()) {
                if (entry.getKey() < quickslotKeyMap.length) {
                    quickslotKeyMap[entry.getKey()] = entry.getValue();
                }
            }
        }
        fkm.setQuickslotKeyMap(quickslotKeyMap);
        // Load pet consume items
        fkm.setPetConsumeItem(value.getInt(FuncKeyManUDT.PET_CONSUME_ITEM));
        fkm.setPetConsumeMpItem(value.getInt(FuncKeyManUDT.PET_CONSUME_MP_ITEM));
        return fkm;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable FuncKeyManager fkm) {
        if (fkm == null) {
            return null;
        }
        // Prepare func key map
        final Map<Integer, String> funcKeyStringMap = new HashMap<>();
        for (var entry : fkm.getFuncKeyMap().entrySet()) {
            final int index = entry.getKey();
            final FuncKeyMapped funcKeyMapped = entry.getValue();
            final String string = String.format("%d%s%d",
                    funcKeyMapped.getType().getValue(),
                    DELIMITER,
                    funcKeyMapped.getId()
            );
            funcKeyStringMap.put(index, string);
        }
        // Prepare quickslot key map
        final int[] quickslotKeyMap = fkm.getQuickslotKeyMap();
        final Map<Integer, Integer> quickslotKeyIntegerMap = new HashMap<>();
        for (int i = 0; i < quickslotKeyMap.length; i++) {
            quickslotKeyIntegerMap.put(i, quickslotKeyMap[i]);
        }
        return getCqlType().newValue()
                .setMap(FuncKeyManUDT.FUNC_KEY_MAP, funcKeyStringMap, Integer.class, String.class)
                .setMap(FuncKeyManUDT.QUICKSLOT_KEY_MAP, quickslotKeyIntegerMap, Integer.class, Integer.class)
                .setInt(FuncKeyManUDT.PET_CONSUME_ITEM, fkm.getPetConsumeItem())
                .setInt(FuncKeyManUDT.PET_CONSUME_MP_ITEM, fkm.getPetConsumeMpItem());
    }
}
