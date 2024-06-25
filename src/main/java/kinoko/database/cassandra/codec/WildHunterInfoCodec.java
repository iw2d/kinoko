package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.WildHunterInfoUDT;
import kinoko.world.user.info.WildHunterInfo;

import java.util.List;

public final class WildHunterInfoCodec extends MappingCodec<UdtValue, WildHunterInfo> {
    public WildHunterInfoCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<WildHunterInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected WildHunterInfo innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final WildHunterInfo wildHunterInfo = new WildHunterInfo();
        final List<Integer> capturedMobs = value.getList(WildHunterInfoUDT.CAPTURED_MOBS, Integer.class);
        if (capturedMobs != null) {
            wildHunterInfo.getCapturedMobs().addAll(capturedMobs);
        }
        wildHunterInfo.setRidingType(value.getInt(WildHunterInfoUDT.RIDING_TYPE));
        return wildHunterInfo;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable WildHunterInfo wildHunterInfo) {
        if (wildHunterInfo == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(WildHunterInfoUDT.RIDING_TYPE, wildHunterInfo.getRidingType())
                .setList(WildHunterInfoUDT.CAPTURED_MOBS, wildHunterInfo.getCapturedMobs(), Integer.class);
    }
}
