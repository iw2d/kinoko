package kinoko.world.character;

import kinoko.server.OutPacket;
import kinoko.world.Encodable;

public final class AvatarLook implements Encodable {
    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(0); // nGender
        outPacket.encodeByte(0); // nSkin
        outPacket.encodeInt(20000); // nFace

        // anHairEquip
        outPacket.encodeByte(0);
        outPacket.encodeInt(30000); // nHair
        outPacket.encodeByte(-1);

        // anUnseenEquip
        outPacket.encodeByte(-1);

        outPacket.encodeInt(0); // nWeaponStickerID
        // anPetID
        for (int i = 0; i < 3; i++) {
            outPacket.encodeInt(0);
        }
    }
}
