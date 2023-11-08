package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.Encodable;
import lombok.Data;

@Data
public class Item implements Encodable {
    private final ItemType itemType;
    private long itemSn;
    private int itemId;
    private boolean cash;
    private short quantity;
    private short attribute;
    private String title;
    private EquipInfo equipInfo;
    private PetInfo petInfo;

    public Item(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getItemType().getValue()); // nType

        // GW_ItemSlotBase::RawDecode
        outPacket.encodeInt(getItemId()); // nItemID
        outPacket.encodeByte(isCash());
        if (isCash()) {
            outPacket.encodeLong(getItemSn()); // liCashItemSN
        }
        outPacket.encodeFT(FileTime.MAX_TIME); // dateExpire

        switch (getItemType()) {
            case EQUIP -> {
                // GW_ItemSlotEquip::RawDecode
                equipInfo.encode(this, outPacket);
            }
            case PET -> {
                // GW_ItemSlotPet::RawDecode
                petInfo.encode(this, outPacket);
            }
            default -> {
                // GW_ItemSlotBundle::RawDecode
                outPacket.encodeShort(getQuantity()); // nNumber
                outPacket.encodeString(getTitle()); // sTitle
                outPacket.encodeShort(getAttribute()); // nAttribute

                final int itemPrefix = getItemId() / 10000;
                if (itemPrefix == 207 || itemPrefix == 233) {
                    outPacket.encodeLong(getItemSn());
                }
            }
        }
    }
}
