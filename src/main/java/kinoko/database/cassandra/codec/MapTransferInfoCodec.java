package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.MapTransferInfoUDT;
import kinoko.world.user.data.MapTransferInfo;

import java.util.List;

public final class MapTransferInfoCodec extends MappingCodec<UdtValue, MapTransferInfo> {
    public MapTransferInfoCodec(TypeCodec<UdtValue> innerCodec, GenericType<MapTransferInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected MapTransferInfo innerToOuter(UdtValue value) {
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

    @Override
    protected UdtValue outerToInner(MapTransferInfo mapTransferInfo) {
        if (mapTransferInfo == null) {
            return null;
        }
        return getCqlType().newValue()
                .setList(MapTransferInfoUDT.MAP_TRANSFER, mapTransferInfo.getMapTransfer(), Integer.class)
                .setList(MapTransferInfoUDT.MAP_TRANSFER_EX, mapTransferInfo.getMapTransferEx(), Integer.class);
    }
}
