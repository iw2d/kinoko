package kinoko.world.dialog.shop;

import kinoko.provider.ShopProvider;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.dialog.Dialog;
import kinoko.world.dialog.trunk.TrunkDialog;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class ShopDialog implements Dialog, Encodable {
    private static final Logger log = LogManager.getLogger(TrunkDialog.class);
    private final Npc npc;
    private final List<ShopItem> items;

    public ShopDialog(Npc npc, List<ShopItem> items) {
        this.npc = npc;
        this.items = items;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CShopDlg::SetShopDlg
        outPacket.encodeInt(npc.getTemplateId()); // dwNpcTemplateID
        outPacket.encodeShort(items.size()); // nCount
        for (ShopItem item : items) {
            item.encode(outPacket);
        }
    }

    @Override
    public void onPacket(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final ShopRequestType requestType = ShopRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown shop request type {}", type);
            return;
        }
        switch (requestType) {
            case BUY -> {
                inPacket.decodeShort(); // nBuySelected
                inPacket.decodeInt(); // nItemID
                inPacket.decodeShort(); // nCount
                inPacket.decodeInt(); // DiscountPrice
            }
            case SELL -> {
                inPacket.decodeShort(); // nPOS
                inPacket.decodeInt(); // nItemID
                inPacket.decodeShort(); // nCount
            }
            case RECHARGE -> {
                inPacket.decodeShort(); // nPos
            }
            case CLOSE -> {
                user.closeDialog();
            }
        }
    }

    public static ShopDialog from(Npc npc) {
        final List<ShopItem> items = ShopProvider.getNpcShopItems(npc);
        return new ShopDialog(npc, items);
    }
}
