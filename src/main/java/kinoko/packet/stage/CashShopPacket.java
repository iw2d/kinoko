package kinoko.packet.stage;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.cashshop.CashItemResult;
import kinoko.world.user.Account;

public final class CashShopPacket {
    // CCashShop::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket queryCashResult(Account account) {
        return queryCashResult(
                account.getNxCredit(),
                account.getMaplePoint(),
                account.getNxPrepaid()
        );
    }

    public static OutPacket queryCashResult(int nxCredit, int maplePoint, int nxPrepaid) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopQueryCashResult);
        outPacket.encodeInt(nxCredit); // nNexonCash
        outPacket.encodeInt(maplePoint); // nMaplePoint
        outPacket.encodeInt(nxPrepaid); // nPrepaidNXCash
        return outPacket;
    }

    public static OutPacket cashItemResult(CashItemResult cashItemResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        cashItemResult.encode(outPacket);
        return outPacket;
    }
}
