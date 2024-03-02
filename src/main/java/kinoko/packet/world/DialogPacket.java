package kinoko.packet.world;

import kinoko.server.dialog.shop.ShopDialog;
import kinoko.server.dialog.shop.ShopResultType;
import kinoko.server.dialog.trunk.TrunkResult;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class DialogPacket {
    // CShopDlg::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket openShopDlg(ShopDialog dialog) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OPEN_SHOP_DLG);
        dialog.encode(outPacket);
        return outPacket;
    }

    public static OutPacket shopResult(ShopResultType resultType) {
        return shopResult(resultType, "Due to an error, the trade did not happen."); // Default message for SERVER_MSG
    }

    public static OutPacket shopResult(ShopResultType resultType, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SHOP_RESULT);
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case LIMIT_LEVEL_LESS, LIMIT_LEVEL_MORE -> {
                outPacket.encodeInt(0); // level
            }
            case SERVER_MSG -> {
                outPacket.encodeByte(true);
                outPacket.encodeString(message);
            }
        }
        return outPacket;
    }


    // CTrunkDlg::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket trunkResult(TrunkResult trunkResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRUNK_RESULT);
        trunkResult.encode(outPacket);
        return outPacket;
    }
}
