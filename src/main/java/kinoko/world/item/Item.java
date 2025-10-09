package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;

public final class Item implements Encodable {
    private final ItemType itemType;
    private long itemSn;
    private int itemId;
    private boolean cash;
    private short quantity;
    private short attribute;
    private Instant dateExpire;
    private String title = "";
    private EquipData equipData;
    private PetData petData;
    private RingData ringData;

    public Item(ItemType itemType) {
        this.itemType = itemType;
    }

    public Item(Item item) {
        this(item.getItemType());
        this.itemSn = item.itemSn;
        this.itemId = item.itemId;
        this.cash = item.cash;
        this.quantity = item.quantity;
        this.attribute = item.attribute;
        this.dateExpire = item.dateExpire;
        this.title = item.title;
        this.equipData = item.equipData != null ? new EquipData(item.equipData) : null;
        this.petData = item.petData != null ? new PetData(item.petData) : null;
        this.ringData = item.ringData != null ? new RingData(item.ringData) : null;
    }

    public Item(int itemId, short quantity) {
        this(ItemType.getByItemId(itemId));
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public Item(
            int itemId,
            short quantity,
            long itemSn,
            boolean cash,
            short attribute,
            String title,
            Instant dateExpire,
            EquipData equipData,
            PetData petData,
            RingData ringData
    ) {
        this(ItemType.getByItemId(itemId));
        this.itemId = itemId;
        this.quantity = quantity;
        this.itemSn = itemSn;
        this.cash = cash;
        this.attribute = attribute;
        this.title = title;
        this.dateExpire = dateExpire;
        this.equipData = equipData;
        this.petData = petData;
        this.ringData = ringData;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getItemType().getValue()); // nType

        // GW_ItemSlotBase::RawDecode
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeByte(cash);
        if (cash) {
            outPacket.encodeLong(itemSn); // liCashItemSN
        }
        outPacket.encodeFT(dateExpire); // dateExpire

        switch (getItemType()) {
            case EQUIP -> {
                // GW_ItemSlotEquip::RawDecode
                equipData.encode(outPacket, this);
            }
            case PET -> {
                // GW_ItemSlotPet::RawDecode
                petData.encode(outPacket, this);
            }
            default -> {
                // GW_ItemSlotBundle::RawDecode
                outPacket.encodeShort(quantity); // nNumber
                outPacket.encodeString(title); // sTitle
                outPacket.encodeShort(attribute); // nAttribute

                if (ItemConstants.isRechargeableItem(itemId)) {
                    outPacket.encodeLong(itemSn);
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Instant dateExpire) {
        this.dateExpire = dateExpire;
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

    public RingData getRingData() {
        return ringData;
    }

    public void setRingData(RingData ringData) {
        this.ringData = ringData;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public boolean hasAttribute(ItemAttribute itemAttribute) {
        return (attribute & itemAttribute.getValue()) != 0;
    }

    public void addAttribute(ItemAttribute itemAttribute) {
        this.attribute |= itemAttribute.getValue();
    }

    public void removeAttribute(ItemAttribute itemAttribute) {
        if (hasAttribute(itemAttribute)) {
            this.attribute ^= itemAttribute.getValue();
        }
    }

    public boolean isPossibleTrading() {
        return hasAttribute(ItemAttribute.getPossibleTradingAttribute(getItemType()));
    }

    public void setPossibleTrading(boolean set) {
        if (set) {
            addAttribute(ItemAttribute.getPossibleTradingAttribute(getItemType()));
        } else {
            removeAttribute(ItemAttribute.getPossibleTradingAttribute(getItemType()));
        }
    }

    /**
     * Checks whether this Item has a valid item serial number (SN).

     * Useful in relational databases where item SNs are automatically generated.
     * Returns true if the item has no SN and therefore needs to be inserted
     * into the database to obtain one.
     *
     * @return true if the item SN is zero or negative, false otherwise
     */
    public boolean hasNoSN() {
        return getItemSn() <= 0;
    }
}
