package kinoko.packet.world.message;

public final class DropPickUpMessageInfo {
    private final DropPickUpMessageType type;
    private boolean portionNotFound;
    private int money;
    private int itemId;
    private int itemCount;

    public DropPickUpMessageInfo(DropPickUpMessageType type) {
        this.type = type;
    }

    public DropPickUpMessageType getType() {
        return type;
    }

    public boolean isPortionNotFound() {
        return portionNotFound;
    }

    public void setPortionNotFound(boolean portionNotFound) {
        this.portionNotFound = portionNotFound;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
