package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Inventory;
import lombok.Data;

@Data
public final class AvatarData implements Encodable {
    private final CharacterStat characterStat;
    private final Inventory equipped;

    @Override
    public void encode(OutPacket outPacket) {
        getCharacterStat().encode(outPacket);
        AvatarLook.from(getCharacterStat(), getEquipped()).encode(outPacket);
    }
}
