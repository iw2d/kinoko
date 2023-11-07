package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.Encodable;
import lombok.Data;

@Data
public final class PetItem extends Item {
    private String petName;

    private byte level;
    private byte fullness;
    private short tameness;

    private short petSkill;
    private short petAttribute;

    private int remainLife;

    @Override
    public void encode(OutPacket outPacket) {
        assert getItemType() == 3;
        outPacket.encodeByte(getItemType()); // nType
        encodeBase(outPacket);

        // GW_ItemSlotPet::RawDecode
        outPacket.encodeString(getPetName(), 13); // sPetName
        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeShort(getTameness()); // nTameness
        outPacket.encodeByte(getFullness()); // nRepleteness
        outPacket.encodeFT(FileTime.MAX_TIME); // dateDead
        outPacket.encodeShort(getPetAttribute()); // nPetAttribute
        outPacket.encodeShort(getPetSkill()); // usPetSkill
        outPacket.encodeInt(getRemainLife()); // nRemainLife
        outPacket.encodeShort(getAttribute()); // nAttribute
    }
}
