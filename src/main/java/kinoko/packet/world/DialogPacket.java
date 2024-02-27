package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.dialog.shop.ShopDialog;
import kinoko.world.dialog.shop.ShopResultType;
import kinoko.world.dialog.trunk.TrunkDialog;
import kinoko.world.dialog.trunk.TrunkResultType;
import kinoko.world.user.DBChar;

public final class DialogPacket {
    // CShopDlg::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket openShopDlg(ShopDialog dialog) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OPEN_SHOP_DLG);
        dialog.encode(outPacket);
        return outPacket;
    }

    public static OutPacket shopResult(ShopResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SHOP_RESULT);
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case LIMIT_LEVEL_LESS, LIMIT_LEVEL_MORE -> {
                outPacket.encodeInt(0); // level
            }
            case SERVER_MSG -> {
                outPacket.encodeByte(true);
                outPacket.encodeString("Due to an error, the trade did not happen.");
            }
        }
        return outPacket;
    }


    // CTrunkDlg::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket trunkResult(TrunkResultType resultType, TrunkDialog dialog) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRUNK_RESULT);
        outPacket.encodeByte(resultType.getValue());
        if (resultType == TrunkResultType.OPEN_TRUNK_DLG) {
            outPacket.encodeInt(dialog.getTemplateId()); // dwNpcTemplateID
        }
        switch (resultType) {
            case GET_SUCCESS, PUT_SUCCESS, SORT_ITEM, OPEN_TRUNK_DLG -> {
                dialog.getTrunk().encode(outPacket);
            }
            case MONEY_SUCCESS -> {
                dialog.getTrunk().encodeItems(DBChar.MONEY, outPacket);
            }
            case SERVER_MSG -> {
                outPacket.encodeByte(true);
                outPacket.encodeString("Due to an error, the trade did not happen.");
            }
        }
        return outPacket;
    }
}
