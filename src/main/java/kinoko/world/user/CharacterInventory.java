package kinoko.world.user;

import kinoko.server.OutPacket;
import kinoko.world.item.Inventory;
import lombok.Data;

@Data
public final class CharacterInventory {
    private Inventory equipInventory;
    private Inventory consumeInventory;
    private Inventory installInventory;
    private Inventory etcInventory;
    private Inventory cashInventory;

    private int money;


    public void encodeSize(OutPacket outPacket) {
        outPacket.encodeByte(getEquipInventory().getSize());
        outPacket.encodeByte(getConsumeInventory().getSize());
        outPacket.encodeByte(getInstallInventory().getSize());
        outPacket.encodeByte(getEtcInventory().getSize());
        outPacket.encodeByte(getCashInventory().getSize());
    }
}
