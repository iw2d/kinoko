package kinoko.world.dialog.shop;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.ItemConstants;

public final class ShopItem implements Encodable {
    private final int itemId;
    private final int price;
    private final int quantity;
    private final int maxPerSlot;

    private final int tokenItemId;
    private final int tokenPrice;
    private final double unitPrice;

    public ShopItem(int itemId, int price, int quantity, int maxPerSlot, int tokenItemId, int tokenPrice, double unitPrice) {
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.maxPerSlot = maxPerSlot;
        this.tokenItemId = tokenItemId;
        this.tokenPrice = tokenPrice;
        this.unitPrice = unitPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxPerSlot() {
        return maxPerSlot;
    }

    public int getTokenItemId() {
        return tokenItemId;
    }

    public int getTokenPrice() {
        return tokenPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeInt(price); // nPrice
        outPacket.encodeByte(0); // nDiscountRate
        outPacket.encodeInt(tokenItemId); // nTokenItemID
        outPacket.encodeInt(tokenPrice); // nTokenPrice
        outPacket.encodeInt(0); // nItemPeriod
        outPacket.encodeInt(0); // nLevelLimited
        if (ItemConstants.isRechargeableItem(itemId)) {
            outPacket.encodeDouble(unitPrice); // dUnitPrice
        } else {
            outPacket.encodeShort(quantity); // nQuantity
        }
        outPacket.encodeShort(maxPerSlot); // nMaxPerSlot
    }

    public static ShopItem from(int itemId, int price, int quantity, int maxPerSlot) {
        return new ShopItem(itemId, price, quantity, maxPerSlot, 0, 0, 0);
    }

    public static ShopItem rechargeable(int itemId, int maxPerSlot, double unitPrice) {
        return new ShopItem(itemId, 0, 0, maxPerSlot, 0, 0, unitPrice);
    }
}
