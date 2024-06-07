package kinoko.packet.world.message;

import kinoko.server.packet.OutPacket;
import kinoko.world.item.Item;

public final class DropPickUpMessage extends Message {
    private final DropPickUpMessageType pickUpMessageType;
    private boolean portionNotFound;
    private int money;
    private int itemId;
    private int quantity;

    private DropPickUpMessage(DropPickUpMessageType pickUpMessageType) {
        super(MessageType.DropPickUp);
        this.pickUpMessageType = pickUpMessageType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case DropPickUp -> {
                outPacket.encodeByte(pickUpMessageType.getValue());
                switch (pickUpMessageType) {
                    case MONEY -> {
                        outPacket.encodeByte(portionNotFound);
                        outPacket.encodeInt(money);
                        outPacket.encodeShort(0); // Internet Cafe Meso Bonus
                    }
                    case ITEM_BUNDLE -> {
                        outPacket.encodeInt(itemId); // nItemID
                        outPacket.encodeInt(quantity);
                    }
                    case ITEM_SINGLE -> {
                        outPacket.encodeInt(itemId);
                    }
                }
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported message type");
            }
        }
    }

    public static DropPickUpMessage cannotAcquireAnyItems() {
        return new DropPickUpMessage(DropPickUpMessageType.CANNOT_ACQUIRE_ANY_ITEMS);
    }

    public static DropPickUpMessage unavailableForPickUp() {
        return new DropPickUpMessage(DropPickUpMessageType.UNAVAILABLE_FOR_PICK_UP);
    }

    public static DropPickUpMessage cannotGetAnymoreItems() {
        return new DropPickUpMessage(DropPickUpMessageType.CANNOT_GET_ANYMORE_ITEMS);
    }

    public static DropPickUpMessage item(Item item) {
        return item(item.getItemId(), item.getQuantity());
    }

    public static DropPickUpMessage item(int itemId, int quantity) {
        final DropPickUpMessage message = new DropPickUpMessage(DropPickUpMessageType.ITEM_BUNDLE);
        message.itemId = itemId;
        message.quantity = quantity;
        return message;
    }

    public static DropPickUpMessage money(int money, boolean portionNotFound) {
        final DropPickUpMessage message = new DropPickUpMessage(DropPickUpMessageType.MONEY);
        message.money = money;
        message.portionNotFound = portionNotFound;
        return message;
    }

    private enum DropPickUpMessageType {
        CANNOT_ACQUIRE_ANY_ITEMS(-3),
        UNAVAILABLE_FOR_PICK_UP(-2),
        CANNOT_GET_ANYMORE_ITEMS(-1),
        ITEM_BUNDLE(0),
        MONEY(1),
        ITEM_SINGLE(2);

        private final byte value;

        DropPickUpMessageType(int value) {
            this.value = (byte) value;
        }

        public final byte getValue() {
            return value;
        }
    }
}
