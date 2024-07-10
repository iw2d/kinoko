package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.RingDataUDT;
import kinoko.world.item.RingData;

public final class RingDataCodec extends MappingCodec<UdtValue, RingData> {
    public RingDataCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<RingData> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected RingData innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final RingData ringData = new RingData();
        ringData.setPairCharacterId(value.getInt(RingDataUDT.PAIR_CHARACTER_ID));
        ringData.setPairCharacterName(value.getString(RingDataUDT.PAIR_CHARACTER_NAME));
        ringData.setPairItemSn(value.getLong(RingDataUDT.PAIR_ITEM_SN));
        return ringData;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable RingData ringData) {
        if (ringData == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(RingDataUDT.PAIR_CHARACTER_ID, ringData.getPairCharacterId())
                .setString(RingDataUDT.PAIR_CHARACTER_NAME, ringData.getPairCharacterName())
                .setLong(RingDataUDT.PAIR_ITEM_SN, ringData.getPairItemSn());
    }
}
