package kinoko.world.field.drop;

import kinoko.packet.field.DropPacket;
import kinoko.server.packet.OutPacket;
import kinoko.world.GameConstants;
import kinoko.world.field.FieldObject;
import kinoko.world.item.Item;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class Drop extends FieldObject {
    private final DropOwnType ownType;
    private final FieldObject source;
    private final Item item;
    private final int money;
    private final int ownerId;
    private final Instant dateExpire;

    private Drop(DropOwnType ownType, FieldObject source, Item item, int money, int ownerId, Instant dateExpire) {
        this.ownType = ownType;
        this.source = source;
        this.item = item;
        this.money = money;
        this.ownerId = ownerId;
        this.dateExpire = dateExpire;
    }

    public DropOwnType getOwnType() {
        return ownType;
    }

    public FieldObject getSource() {
        return source;
    }

    public Item getItem() {
        return item;
    }

    public int getMoney() {
        return money;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public Instant getDateExpire() {
        return dateExpire;
    }

    public boolean isMoney() {
        return item == null && money > 0;
    }

    public boolean isUserDrop() {
        return source instanceof User;
    }

    @Override
    public OutPacket enterFieldPacket() {
        return DropPacket.dropEnterField(this, DropEnterType.ON_THE_FOOTHOLD);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return DropPacket.dropLeaveField(this, DropLeaveType.TIMEOUT, 0, 0);
    }

    public static Drop item(DropOwnType ownType, FieldObject source, Item item, int ownerId) {
        return new Drop(ownType, source, item, 0, ownerId, Instant.now().plus(GameConstants.DROP_REMOVE_OWNERSHIP_TIME, ChronoUnit.SECONDS));
    }

    public static Drop money(DropOwnType ownType, FieldObject source, int money, int ownerId) {
        return new Drop(ownType, source, null, money, ownerId, Instant.now().plus(GameConstants.DROP_REMOVE_OWNERSHIP_TIME, ChronoUnit.SECONDS));
    }
}
