package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryType;
import kinoko.world.user.stat.CharacterStat;

public final class AvatarData implements Encodable {
    private static final Inventory EMPTY_INVENTORY = new Inventory(0, InventoryType.EQUIPPED);
    private final CharacterStat characterStat;
    private final AvatarLook avatarLook;

    public AvatarData(CharacterStat characterStat, AvatarLook avatarLook) {
        this.characterStat = characterStat;
        this.avatarLook = avatarLook;
    }

    @Override
    public void encode(OutPacket outPacket) {
        characterStat.encode(outPacket);
        avatarLook.encode(outPacket);
    }

    public int getCharacterId() {
        return characterStat.getId();
    }

    public String getCharacterName() {
        return characterStat.getName();
    }

    public int getJob() {
        return characterStat.getJob();
    }

    public int getLevel() {
        return characterStat.getLevel();
    }

    public static AvatarData from(CharacterStat characterStat, Inventory equipped, Inventory cashInventory) {
        return new AvatarData(characterStat, AvatarLook.from(characterStat, equipped, cashInventory));
    }

    public static AvatarData from(CharacterStat characterStat, Inventory equipped) {
        // When you don't care about the pets
        return AvatarData.from(characterStat, equipped, EMPTY_INVENTORY);
    }

    public static AvatarData from(CharacterData cd) {
        return AvatarData.from(cd.getCharacterStat(), cd.getInventoryManager().getEquipped(), cd.getInventoryManager().getCashInventory());
    }
}
