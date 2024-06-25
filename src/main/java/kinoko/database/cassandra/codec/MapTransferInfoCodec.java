package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.MapTransferInfoUDT;
import kinoko.world.user.info.MapTransferInfo;

import java.util.List;

public final class MapTransferInfoCodec extends MappingCodec<UdtValue, MapTransferInfo> {
    public MapTransferInfoCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<MapTransferInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected MapTransferInfo innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final MapTransferInfo mapTransferInfo = new MapTransferInfo();
        final List<Integer> mapTransfer = value.getList(MapTransferInfoUDT.MAP_TRANSFER, Integer.class);
        if (mapTransfer != null) {
            mapTransferInfo.getMapTransfer().addAll(mapTransfer);
        }
        final List<Integer> mapTransferEx = value.getList(MapTransferInfoUDT.MAP_TRANSFER_EX, Integer.class);
        if (mapTransferEx != null) {
            mapTransferInfo.getMapTransferEx().addAll(mapTransferEx);
        }
        return mapTransferInfo;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable MapTransferInfo mapTransferInfo) {
        if (mapTransferInfo == null) {
            return null;
        }
        return getCqlType().newValue()
                .setList(MapTransferInfoUDT.MAP_TRANSFER, mapTransferInfo.getMapTransfer(), Integer.class)
                .setList(MapTransferInfoUDT.MAP_TRANSFER_EX, mapTransferInfo.getMapTransferEx(), Integer.class);
    }
}
