package kinoko.world.user;

import kinoko.packet.user.PetPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;
import kinoko.world.life.Life;

public final class Pet extends Life implements Encodable {
    private final User owner;
    private final Item item;

    public Pet(User owner, Item item) {
        this.owner = owner;
        this.item = item;
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

    @Override
    public OutPacket enterFieldPacket() {
        return PetPacket.petActivated(owner, this, 0);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return PetPacket.petDeactivated(owner, 0, 0);
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CPet::Init
        outPacket.encodeInt(getTemplateId()); // dwTemplateID
        outPacket.encodeString(getName()); // sName
        outPacket.encodeLong(item.getItemSn()); // liPetLockerSN
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
