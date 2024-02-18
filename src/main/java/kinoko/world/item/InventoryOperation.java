package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class InventoryOperation implements Encodable {
    private final InventoryOperationType operationType;
    private final InventoryType inventoryType;
    private final int position;

    private Item item;
    private int newPosition;
    private int newQuantity;
    private int newExp;

    private InventoryOperation(InventoryOperationType operationType, InventoryType inventoryType, int position) {
        this.operationType = operationType;
        this.inventoryType = inventoryType;
        this.position = position;
    }

    public InventoryOperationType getOperationType() {
        return operationType;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public Item getItem() {
        return item;
    }

    public int getPosition() {
        return position;
    }

    public int getNewPosition() {
        return newPosition;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public int getNewExp() {
        return newExp;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(operationType.getValue());
        outPacket.encodeByte(inventoryType.getValue());
        outPacket.encodeShort(position);
        switch (operationType) {
            case NEW_ITEM -> {
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
            case ITEM_NUMBER -> {
                outPacket.encodeShort(newQuantity); // nNumber
            }
            case POSITION -> {
                outPacket.encodeShort(newPosition);
            }
            case DEL_ITEM -> {
            }
            case EXP -> {
                outPacket.encodeInt(item.getEquipData().getExp()); // pEquip.p->SetEXP
            }
        }
        outPacket.encodeByte(inventoryType == InventoryType.EQUIP &&
                (position < 0 || newPosition < 0)); // bool -> CWvsContext::CheckEquipOnAutoStartQuest
    }

    public static InventoryOperation newItem(InventoryType inventoryType, int position, Item item) {
        final InventoryOperation op = new InventoryOperation(InventoryOperationType.NEW_ITEM, inventoryType, position);
        op.item = item;
        return op;
    }

    public static InventoryOperation itemNumber(InventoryType inventoryType, int position, int newQuantity) {
        final InventoryOperation op = new InventoryOperation(InventoryOperationType.ITEM_NUMBER, inventoryType, position);
        op.newQuantity = newQuantity;
        return op;
    }

    public static InventoryOperation position(InventoryType inventoryType, int position, int newPosition) {
        final InventoryOperation op = new InventoryOperation(InventoryOperationType.POSITION, inventoryType, position);
        op.newPosition = newPosition;
        return op;
    }

    public static InventoryOperation delItem(InventoryType inventoryType, int position) {
        return new InventoryOperation(InventoryOperationType.DEL_ITEM, inventoryType, position);
    }

    public static InventoryOperation exp(InventoryType inventoryType, int position, int newExp) {
        final InventoryOperation op = new InventoryOperation(InventoryOperationType.EXP, inventoryType, position);
        op.newExp = newExp;
        return op;
    }
}
