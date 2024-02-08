package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.Encodable;

import java.util.Optional;

public final class Item implements Encodable {
    private final ItemType itemType;
    private long itemSn;
    private int itemId;
    private boolean cash;
    private short quantity;
    private short attribute;
    private String title = "";
    private EquipData equipData;
    private PetData petData;

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
                equipData.encode(this, outPacket);
            }
            case PET -> {
                // GW_ItemSlotPet::RawDecode
                petData.encode(this, outPacket);
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

    public ItemType getItemType() {
        return itemType;
    }

    public long getItemSn() {
        return itemSn;
    }

    public void setItemSn(long itemSn) {
        this.itemSn = itemSn;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public short getAttribute() {
        return attribute;
    }

    public void setAttribute(short attribute) {
        this.attribute = attribute;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EquipData getEquipData() {
        return equipData;
    }

    public void setEquipData(EquipData equipData) {
        this.equipData = equipData;
    }

    public PetData getPetData() {
        return petData;
    }

    public void setPetData(PetData petData) {
        this.petData = petData;
    }

    public static Optional<Item> createById(long itemSn, int itemId) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final ItemInfo ii = itemInfoResult.get();
        final ItemType type = ItemType.getByItemId(itemId);
        final Item item = new Item(type);
        item.setItemSn(itemSn);
        item.setItemId(itemId);
        item.setCash(ii.isCash());
        item.setQuantity((short) 1);
        if (type == ItemType.EQUIP) {
            item.setEquipData(EquipData.from(ii));
        } else if (type == ItemType.PET) {
            item.setPetData(PetData.from(ii));
        }
        return Optional.of(item);
    }
}
