package kinoko.world.field.drop;

import kinoko.util.TimeUtil;
import kinoko.world.GameConstants;
import kinoko.world.field.FieldObject;
import kinoko.world.field.FieldObjectImpl;
import kinoko.world.item.Item;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class Drop extends FieldObjectImpl {
    private final DropOwnType ownType;
    private final FieldObject source;
    private final Item item;
    private final int money;
    private final int ownerId;
    private final int questId;
    private final Instant createTime;

    private Drop(DropOwnType ownType, FieldObject source, Item item, int money, int ownerId, int questId, Instant createTime) {
        this.ownType = ownType;
        this.source = source;
        this.item = item;
        this.money = money;
        this.ownerId = ownerId;
        this.questId = questId;
        this.createTime = createTime;
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

    public int getQuestId() {
        return questId;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public Instant getExpireTime() {
        return getCreateTime().plus(GameConstants.DROP_REMAIN_ON_GROUND_TIME, ChronoUnit.SECONDS);
    }

    public boolean isMoney() {
        return item == null && money > 0;
    }

    public boolean isQuest() {
        return questId != 0;
    }

    public boolean isUserDrop() {
        return source instanceof User;
    }

    public boolean canPickUp(User user) {
        // CDropPool::TryPickUpDrop, CDropPool::TryPickUpDropByPet
        if ((ownType == DropOwnType.USEROWN && ownerId != user.getCharacterId()) ||
                (ownType == DropOwnType.PARTYOWN && ownerId != user.getPartyId())) {
            return createTime.plus(GameConstants.DROP_OWNERSHIP_EXPIRE_TIME, ChronoUnit.SECONDS).isBefore(TimeUtil.getCurrentTime());
        }
        return true;
    }

    public static Drop item(DropOwnType ownType, FieldObject source, Item item, int ownerId) {
        return item(ownType, source, item, ownerId, 0);
    }

    public static Drop item(DropOwnType ownType, FieldObject source, Item item, int ownerId, int questId) {
        return new Drop(ownType, source, item, 0, ownerId, questId, TimeUtil.getCurrentTime());
    }

    public static Drop money(DropOwnType ownType, FieldObject source, int money, int ownerId) {
        return new Drop(ownType, source, null, money, ownerId, 0, TimeUtil.getCurrentTime());
    }
}
