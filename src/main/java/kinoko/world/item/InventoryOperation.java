package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class InventoryOperation implements Encodable {
    private final InventoryOperationType operationType;
    private final InventoryType inventoryType;
    private final int inventoryPosition;

    private Item item;
    private int newPosition;

    private InventoryOperation(InventoryOperationType operationType, InventoryType inventoryType, int inventoryPosition) {
        this.operationType = operationType;
        this.inventoryType = inventoryType;
        this.inventoryPosition = inventoryPosition;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(operationType.getValue());
        outPacket.encodeByte(inventoryType.getValue());
        outPacket.encodeShort(inventoryPosition);
        switch (operationType) {
            case NEW_ITEM -> {
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
            case ITEM_NUMBER -> {
                outPacket.encodeShort(item.getQuantity()); // nNumber
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
                (inventoryPosition < 0 || newPosition < 0)); // bool -> CWvsContext::CheckEquipOnAutoStartQuest
    }

    public static InventoryOperation newItem(InventoryType inventoryType, int inventoryPosition, Item item) {
        return withItem(InventoryOperationType.NEW_ITEM, inventoryType, inventoryPosition, item);
    }

    public static InventoryOperation itemNumber(InventoryType inventoryType, int inventoryPosition, Item item) {
        return withItem(InventoryOperationType.ITEM_NUMBER, inventoryType, inventoryPosition, item);
    }

    public static InventoryOperation position(InventoryType inventoryType, int inventoryPosition, int newPosition) {
        final InventoryOperation inventoryOperation = new InventoryOperation(InventoryOperationType.POSITION, inventoryType, inventoryPosition);
        inventoryOperation.newPosition = newPosition;
        return inventoryOperation;
    }

    public static InventoryOperation delItem(InventoryType inventoryType, int inventoryPosition) {
        return new InventoryOperation(InventoryOperationType.DEL_ITEM, inventoryType, inventoryPosition);
    }

    public static InventoryOperation exp(InventoryType inventoryType, int inventoryPosition, Item item) {
        return withItem(InventoryOperationType.EXP, inventoryType, inventoryPosition, item);
    }

    private static InventoryOperation withItem(InventoryOperationType operationType, InventoryType inventoryType, int inventoryPosition, Item item) {
        final InventoryOperation inventoryOperation = new InventoryOperation(operationType, inventoryType, inventoryPosition);
        inventoryOperation.item = item;
        return inventoryOperation;
    }
}
