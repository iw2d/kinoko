package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.data.MapTransferInfo;
import kinoko.world.user.data.MapTransferResultType;

public final class MapTransferPacket {
    // CWvsContext::OnMapTransferResult --------------------------------------------------------------------------------

    public static OutPacket deleteList(MapTransferInfo mapTransferInfo, boolean canTransferContinent) {
        return MapTransferPacket.update(MapTransferResultType.DeleteList, mapTransferInfo, canTransferContinent);
    }

    public static OutPacket registerList(MapTransferInfo mapTransferInfo, boolean canTransferContinent) {
        return MapTransferPacket.update(MapTransferResultType.RegisterList, mapTransferInfo, canTransferContinent);
    }

    public static OutPacket unknown() {
        return MapTransferPacket.of(MapTransferResultType.Unknown);
    }

    public static OutPacket notAllowed() {
        return MapTransferPacket.of(MapTransferResultType.NotAllowed);
    }

    public static OutPacket targetNotExist() {
        return MapTransferPacket.of(MapTransferResultType.TargetNotExist);
    }

    public static OutPacket registerFail() {
        return MapTransferPacket.of(MapTransferResultType.RegisterFail);
    }

    private static OutPacket update(MapTransferResultType resultType, MapTransferInfo mapTransferInfo, boolean canTransferContinent) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MapTransferResult);
        outPacket.encodeByte(resultType.getValue());
        outPacket.encodeByte(canTransferContinent);
        if (canTransferContinent) {
            mapTransferInfo.encodeMapTransferEx(outPacket);
        } else {
            mapTransferInfo.encodeMapTransfer(outPacket);
        }
        return outPacket;
    }

    private static OutPacket of(MapTransferResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MapTransferResult);
        outPacket.encodeByte(resultType.getValue());
        outPacket.encodeByte(0); // ignored unless result type is DeleteList or RegisterList
        return outPacket;
    }
}
