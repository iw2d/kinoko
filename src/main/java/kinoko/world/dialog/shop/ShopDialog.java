package kinoko.world.dialog.shop;

import kinoko.provider.ShopProvider;
import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.dialog.Dialog;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;

import java.util.List;

public final class ShopDialog implements Dialog, Encodable {
    private final User user;
    private final Npc npc;
    private final List<ShopItem> items;

    public ShopDialog(User user, Npc npc, List<ShopItem> items) {
        this.user = user;
        this.npc = npc;
        this.items = items;
    }

    public Npc getNpc() {
        return npc;
    }

    public int getTemplateId() {
        return npc.getTemplateId();
    }

    public List<ShopItem> getItems() {
        return items;
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
    public User getUser() {
        return user;
    }

    public static ShopDialog from(User user, Npc npc) {
        final List<ShopItem> items = ShopProvider.getNpcShopItems(npc);
        return new ShopDialog(user, npc, items);
    }
}
