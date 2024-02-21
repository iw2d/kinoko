package kinoko.world.user;

import kinoko.packet.user.PetPacket;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.Life;
import kinoko.world.item.Item;

public final class Pet extends Life {
    private final User owner;
    private final Item item;

    public Pet(User owner, Item item) {
        this.owner = owner;
        this.item = item;
    }

    @Override
    public OutPacket enterFieldPacket() {
        return PetPacket.petActivated(owner, this, 0);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return PetPacket.petDeactivated(owner, 0, 0);
    }

    public void encodeInit(OutPacket outPacket) {
        // CPet::Init
        outPacket.encodeInt(item.getItemId()); // dwTemplateID
        outPacket.encodeString(owner.getCharacterStat().getName()); // pOwner
        outPacket.encodeLong(item.getItemSn()); // liPetLockerSN
        outPacket.encodeShort(getX()); // ptPosPrev.x
        outPacket.encodeShort(getY()); // ptPosPrev.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // Foothold
        outPacket.encodeByte(false); // bNameTag
        outPacket.encodeByte(false); // bChatBalloon
    }
}
