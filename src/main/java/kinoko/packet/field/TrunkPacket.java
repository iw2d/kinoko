package kinoko.packet.field;

import kinoko.server.dialog.trunk.TrunkResultType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.item.Trunk;
import kinoko.world.user.DBChar;

public final class TrunkPacket {
    // CTrunkDlg::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket getSuccess(Trunk trunk) {
        final OutPacket outPacket = TrunkPacket.of(TrunkResultType.GetSuccess);
        trunk.encode(outPacket);
        return outPacket;
    }

    public static OutPacket putSuccess(Trunk trunk) {
        final OutPacket outPacket = TrunkPacket.of(TrunkResultType.PutSuccess);
        trunk.encode(outPacket);
        return outPacket;
    }

    public static OutPacket sortItem(Trunk trunk) {
        final OutPacket outPacket = TrunkPacket.of(TrunkResultType.SortItem);
        trunk.encode(outPacket);
        return outPacket;
    }

    public static OutPacket moneySuccess(Trunk trunk) {
        final OutPacket outPacket = TrunkPacket.of(TrunkResultType.MoneySuccess);
        trunk.encodeItems(DBChar.MONEY, outPacket);
        return outPacket;
    }

    public static OutPacket openTrunkDlg(int templateId, Trunk trunk) {
        final OutPacket outPacket = TrunkPacket.of(TrunkResultType.OpenTrunkDlg);
        outPacket.encodeInt(templateId); // dwNpcTemplateID
        trunk.encode(outPacket);
        return outPacket;
    }

    public static OutPacket serverMsg(String message) {
        final OutPacket outPacket = TrunkPacket.of(TrunkResultType.ServerMsg);
        outPacket.encodeByte(message != null && !message.isEmpty());
        if (message != null && !message.isEmpty()) {
            outPacket.encodeString(message);
        }
        return outPacket;
    }

    public static OutPacket of(TrunkResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TrunkResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
