package kinoko.world.user;

import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.map.Foothold;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.life.Life;
import kinoko.world.item.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class Pet extends Life implements Encodable {
    private final User owner;
    private final Item item;
    private Instant nextFullnessUpdate;
    private Instant nextRemainLifeUpdate;

    public Pet(User owner, Item item) {
        this.owner = owner;
        this.item = item;
        this.nextFullnessUpdate = Instant.now().plus(36000, ChronoUnit.MILLIS);
        this.nextRemainLifeUpdate = item.getPetData().getRemainLife() > 0 ? Instant.now().plus(60, ChronoUnit.SECONDS) : Instant.MAX;
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

    public int getPetWear() {
        final Item petWearItem = owner.getInventoryManager().getEquipped().getItem(BodyPart.getByPetIndex(BodyPart.PETWEAR, getPetIndex()).getValue() + BodyPart.CASH_BASE.getValue());
        return petWearItem != null ? petWearItem.getItemId() : 0;
    }

    public boolean getNameTag() {
        return owner.getInventoryManager().getEquipped().getItem(BodyPart.getByPetIndex(BodyPart.PETRING_LABEL, getPetIndex()).getValue() + BodyPart.CASH_BASE.getValue()) != null;
    }

    public boolean getChatBalloon() {
        return owner.getInventoryManager().getEquipped().getItem(BodyPart.getByPetIndex(BodyPart.PETRING_QUOTE, getPetIndex()).getValue() + BodyPart.CASH_BASE.getValue()) != null;
    }

    public void setPosition(Field field, int x, int y) {
        setField(field);
        setX(x);
        setY(y);
        setFoothold(field.getFootholdBelow(x, y).map(Foothold::getSn).orElse(0));
    }

    public boolean updateFullness(Instant now) {
        if (now.isBefore(nextFullnessUpdate)) {
            return false;
        }
        // Schedule next update
        final int hungry = ItemProvider.getItemInfo(item.getItemId())
                .map((ii) -> ii.getInfo(ItemInfoType.hungry))
                .orElse(0);
        final int variance = Math.clamp(36 - 6L * hungry, 0, 36);
        nextFullnessUpdate = now.plus(Util.getRandom(0, variance) + 60, ChronoUnit.SECONDS);
        // Resolve pet item
        final Optional<Tuple<Integer, Item>> itemEntryResult = owner.getInventoryManager().getItemBySn(InventoryType.CASH, getItemSn());
        if (itemEntryResult.isEmpty()) {
            return true;
        }
        final int position = itemEntryResult.get().getLeft();
        final Item petItem = itemEntryResult.get().getRight();
        // Update pet item
        final PetData petData = petItem.getPetData();
        petData.setFullness((byte) Math.max(petData.getFullness() - 1, 0));
        final boolean remove = petData.getFullness() == 0;
        if (remove) {
            petData.setFullness((byte) 5);
            petData.setTameness((short) Math.max(petData.getTameness() - 1, 0));
        }
        owner.write(WvsContext.inventoryOperation(InventoryOperation.newItem(InventoryType.CASH, position, petItem), false));
        return remove;
    }

    public boolean updateRemainLife(Instant now) {
        if (now.isBefore(nextRemainLifeUpdate)) {
            return false;
        }
        // Schedule next update
        nextRemainLifeUpdate = now.plus(60, ChronoUnit.SECONDS);
        // Resolve pet item
        final Optional<Tuple<Integer, Item>> itemEntryResult = owner.getInventoryManager().getItemBySn(InventoryType.CASH, getItemSn());
        if (itemEntryResult.isEmpty()) {
            return true;
        }
        final int position = itemEntryResult.get().getLeft();
        final Item petItem = itemEntryResult.get().getRight();
        // Update pet item
        final PetData petData = petItem.getPetData();
        petData.setRemainLife(Math.max(petData.getRemainLife() - 60, 0));
        final boolean remove = petData.getRemainLife() == 0;
        owner.write(WvsContext.inventoryOperation(InventoryOperation.newItem(InventoryType.CASH, position, petItem), false));
        return remove;
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
        outPacket.encodeByte(getNameTag()); // bNameTag
        outPacket.encodeByte(getChatBalloon()); // bChatBalloon
    }

    public static Pet from(User user, Item item) {
        assert item.getItemType() == ItemType.PET;
        return new Pet(user, item);
    }
}
