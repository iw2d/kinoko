package kinoko.world.dialog.trunk;

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
        if (type == TrunkResultType.OpenTrunkDlg) {
            outPacket.encodeInt(templateId); // dwNpcTemplateID
        }
        switch (type) {
            case GetSuccess, PutSuccess, SortItem, OpenTrunkDlg -> {
                trunk.encode(outPacket);
            }
            case MoneySuccess -> {
                trunk.encodeItems(DBChar.MONEY, outPacket);
            }
            case ServerMsg -> {
                outPacket.encodeByte(true);
                outPacket.encodeString(message);
            }
        }
    }

    public static TrunkResult of(TrunkResultType type) {
        return new TrunkResult(type);
    }

    public static TrunkResult open(Trunk trunk, int templateId) {
        final TrunkResult result = new TrunkResult(TrunkResultType.OpenTrunkDlg);
        result.trunk = trunk;
        result.templateId = templateId;
        return result;
    }

    public static TrunkResult getSuccess(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.GetSuccess);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult putSuccess(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.PutSuccess);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult sortItem(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.SortItem);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult moneySuccess(Trunk trunk) {
        final TrunkResult result = new TrunkResult(TrunkResultType.MoneySuccess);
        result.trunk = trunk;
        return result;
    }

    public static TrunkResult message(String message) {
        final TrunkResult result = new TrunkResult(TrunkResultType.ServerMsg);
        result.message = message;
        return result;
    }
}
