package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Inventory;

public final class AvatarData implements Encodable {
    private final int characterId;
    private final String characterName;
    private final CharacterStat characterStat;
    private final Inventory equipped;

    public AvatarData(int characterId, String characterName, CharacterStat characterStat, Inventory equipped) {
        this.characterId = characterId;
        this.characterName = characterName;
        this.characterStat = characterStat;
        this.equipped = equipped;
    }

    @Override
    public void encode(OutPacket outPacket) {
        characterStat.encode(characterId, characterName, outPacket);
        AvatarLook.from(characterStat, equipped).encode(outPacket);
    }

    public static AvatarData from(int characterId, String characterName, CharacterStat characterStat, Inventory equipped) {
        return new AvatarData(characterId, characterName, characterStat, equipped);
    }

    public static AvatarData from(CharacterData cd) {
        return from(cd.getCharacterId(), cd.getCharacterName(), cd.getCharacterStat(), cd.getCharacterInventory().getEquipped());
    }
}
