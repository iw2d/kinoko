package kinoko.world.user;

import kinoko.provider.map.Foothold;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.field.Field;
import kinoko.world.field.life.Life;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;

public final class Pet extends Life implements Encodable {
    private final User owner;
    private final Item item;

    public Pet(User owner, Item item) {
        this.owner = owner;
        this.item = item;
    }

    public User getOwner() {
        return owner;
    }

    public Item getItem() {
        return item;
    }

    public long getItemSn() {
        return item.getItemSn();
    }

    public int getTemplateId() {
        return item.getItemId();
    }

    public String getName() {
        return item.getPetData().getPetName();
    }

    public int getLevel() {
        return item.getPetData().getLevel();
    }

    public int getTameness() {
        return item.getPetData().getTameness();
    }

    public int getFullness() {
        return item.getPetData().getFullness();
    }

    public int getPetSkill() {
        return item.getPetData().getPetSkill();
    }

    public int getPetIndex() {
        return owner.getPetIndex(getItemSn()).orElseThrow();
    }

    public void setPosition(Field field, int x, int y) {
        setField(field);
        setX(x);
        setY(y);
        setFoothold(field.getFootholdBelow(x, y).map(Foothold::getSn).orElse(0));
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CPet::Init
        outPacket.encodeInt(getTemplateId()); // dwTemplateID
        outPacket.encodeString(getName()); // sName
        outPacket.encodeLong(getItemSn()); // liPetLockerSN
        outPacket.encodeShort(getX()); // ptPosPrev.x
        outPacket.encodeShort(getY()); // ptPosPrev.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // Foothold
        outPacket.encodeByte(false); // bNameTag
        outPacket.encodeByte(false); // bChatBalloon
    }

    public static Pet from(User user, Item item) {
        assert item.getItemType() == ItemType.PET;
        return new Pet(user, item);
    }
}
