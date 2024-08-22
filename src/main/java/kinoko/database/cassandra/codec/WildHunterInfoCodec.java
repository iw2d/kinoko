package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.WildHunterInfoUDT;
import kinoko.world.user.data.WildHunterInfo;

import java.util.List;

public final class WildHunterInfoCodec extends MappingCodec<UdtValue, WildHunterInfo> {
    public WildHunterInfoCodec(TypeCodec<UdtValue> innerCodec, GenericType<WildHunterInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected WildHunterInfo innerToOuter(UdtValue value) {
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

    @Override
    protected UdtValue outerToInner(WildHunterInfo wildHunterInfo) {
        if (wildHunterInfo == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(WildHunterInfoUDT.RIDING_TYPE, wildHunterInfo.getRidingType())
                .setList(WildHunterInfoUDT.CAPTURED_MOBS, wildHunterInfo.getCapturedMobs(), Integer.class);
    }
}
