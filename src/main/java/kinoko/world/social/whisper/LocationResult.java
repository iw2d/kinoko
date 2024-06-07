package kinoko.world.social.whisper;

import kinoko.server.packet.OutPacket;

public final class LocationResult extends WhisperResult {
    private final LocationResultType type;
    private final String targetName;
    private int int1;
    private int positionX;
    private int positionY;

    LocationResult(WhisperFlag flag, LocationResultType type, String targetName) {
        super(flag);
        assert flag == WhisperFlag.LocationResult || flag == WhisperFlag.LocationResult_F;
        this.type = type;
        this.targetName = targetName;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(flag.getValue());
        outPacket.encodeString(targetName); // sFind
        outPacket.encodeByte(type.getValue());
        outPacket.encodeInt(int1); // fieldId | channelId
        if (flag == WhisperFlag.LocationResult && type == LocationResultType.GameSvr) {
            // s_bChase -> CField::SendTransferFieldRequest
            outPacket.encodeInt(positionX); // nTargetPosition_X
            outPacket.encodeInt(positionY); // nTargetPosition_Y
        }
    }

    public static LocationResult sameChannel(String targetName, boolean isFriend, int fieldId) {
        final LocationResult result = LocationResult.of(targetName, isFriend, LocationResultType.GameSvr);
        result.int1 = fieldId;
        return result;
    }

    public static LocationResult otherChannel(String targetName, boolean isFriend, int channelId) {
        final LocationResult result = LocationResult.of(targetName, isFriend, LocationResultType.OtherChannel);
        result.int1 = channelId;
        return result;
    }

    public static LocationResult cashshop(String targetName, boolean isFriend) {
        return LocationResult.of(targetName, isFriend, LocationResultType.ShopSvr);
    }

    public static LocationResult admin(String targetName) {
        return new LocationResult(WhisperFlag.LocationResult, LocationResultType.Admin, targetName);
    }

    public static LocationResult none(String targetName) {
        return new LocationResult(WhisperFlag.LocationResult, LocationResultType.None, targetName);
    }

    private static LocationResult of(String targetName, boolean isFriend, LocationResultType type) {
        return new LocationResult(isFriend ? WhisperFlag.LocationResult_F : WhisperFlag.LocationResult, type, targetName);
    }
}
