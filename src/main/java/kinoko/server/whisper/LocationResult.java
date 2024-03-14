package kinoko.server.whisper;

import kinoko.server.packet.OutPacket;

public final class LocationResult extends WhisperResult {
    private final LocationResultType type;
    private final String targetName;
    private int int1;
    private int positionX;
    private int positionY;

    LocationResult(WhisperFlag flag, LocationResultType type, String targetName) {
        super(flag);
        assert flag == WhisperFlag.LOCATION_RESULT || flag == WhisperFlag.LOCATION_RESULT_F;
        this.type = type;
        this.targetName = targetName;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(flag.getValue());
        outPacket.encodeString(targetName); // sFind
        outPacket.encodeByte(type.getValue());
        outPacket.encodeInt(int1); // fieldId | channelId
        if (flag == WhisperFlag.LOCATION_RESULT && type == LocationResultType.GAMESVR) {
            // s_bChase -> CField::SendTransferFieldRequest
            outPacket.encodeInt(positionX); // nTargetPosition_X
            outPacket.encodeInt(positionY); // nTargetPosition_Y
        }
    }

    public static LocationResult sameChannel(String targetName, boolean isFriend, int fieldId) {
        final LocationResult result = of(targetName, isFriend, LocationResultType.GAMESVR);
        result.int1 = fieldId;
        return result;
    }

    public static LocationResult otherChannel(String targetName, boolean isFriend, int channelId) {
        final LocationResult result = of(targetName, isFriend, LocationResultType.OTHER_CHANNEL);
        result.int1 = channelId;
        return result;
    }

    public static LocationResult cashshop(String targetName, boolean isFriend) {
        return of(targetName, isFriend, LocationResultType.SHOPSVR);
    }

    public static LocationResult admin(String targetName) {
        return new LocationResult(WhisperFlag.LOCATION_RESULT, LocationResultType.ADMIN, targetName);
    }

    public static LocationResult none(String targetName) {
        return new LocationResult(WhisperFlag.LOCATION_RESULT, LocationResultType.NONE, targetName);
    }

    private static LocationResult of(String targetName, boolean isFriend, LocationResultType type) {
        return new LocationResult(isFriend ? WhisperFlag.LOCATION_RESULT_F : WhisperFlag.LOCATION_RESULT, type, targetName);
    }
}
