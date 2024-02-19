package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Inventory;

public final class AvatarData implements Encodable {
    private final CharacterStat characterStat;
    private final Inventory equipped;

    public AvatarData(CharacterStat characterStat, Inventory equipped) {
        this.characterStat = characterStat;
        this.equipped = equipped;
    }

    @Override
    public void encode(OutPacket outPacket) {
        characterStat.encode(outPacket);
        AvatarLook.from(characterStat, equipped).encode(outPacket);
    }

    public int getCharacterId() {
        return characterStat.getId();
    }

    public String getCharacterName() {
        return characterStat.getName();
    }

    public static AvatarData from(CharacterStat characterStat, Inventory equipped) {
        return new AvatarData(characterStat, equipped);
    }

    public static AvatarData from(CharacterData cd) {
        return from(cd.getCharacterStat(), cd.getInventoryManager().getEquipped());
    }
}
