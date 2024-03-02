package kinoko.server.dialog.trunk;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Trunk;
import kinoko.world.user.DBChar;

public final class TrunkResult implements Encodable {
    private final TrunkResultType type;
    private Trunk trunk;
    private int templateId;
    private String message = "Due to an error, the trade did not happen."; // default message for SERVER_MSG

    private TrunkResult(TrunkResultType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        if (type == TrunkResultType.OPEN_TRUNK_DLG) {
            outPacket.encodeInt(templateId); // dwNpcTemplateID
        }
        switch (type) {
            case GET_SUCCESS, PUT_SUCCESS, SORT_ITEM, OPEN_TRUNK_DLG -> {
                trunk.encode(outPacket);
            }
            case MONEY_SUCCESS -> {
                trunk.encodeItems(DBChar.MONEY, outPacket);
            }
            case SERVER_MSG -> {
                outPacket.encodeByte(true);
                outPacket.encodeString(message);
            }
        }
    }

    public static TrunkResult of(TrunkResultType type) {
        return new TrunkResult(type);
    }

    public static TrunkResult open(Trunk trunk, int templateId) {
        final TrunkResult result = new TrunkResult(TrunkResultType.OPEN_TRUNK_DLG);
        result.trunk = trunk;
        result.templateId = templateId;
        return result;
    }

    public static TrunkResult getSuccess(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.GET_SUCCESS);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult putSuccess(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.PUT_SUCCESS);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult sortItem(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.SORT_ITEM);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult moneySuccess(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.MONEY_SUCCESS);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult message(String message) {
        final TrunkResult result = new TrunkResult(TrunkResultType.SERVER_MSG);
        result.message = message;
        return result;
    }
}
