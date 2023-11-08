package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import lombok.Data;

@Data
public final class PetInfo {
    private String petName;
    private byte level;
    private byte fullness;
    private short tameness;
    private short petSkill;
    private short petAttribute;
    private int remainLife;

    public void encode(Item item, OutPacket outPacket) {
        outPacket.encodeString(getPetName(), 13); // sPetName
        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeShort(getTameness()); // nTameness
        outPacket.encodeByte(getFullness()); // nRepleteness
        outPacket.encodeFT(FileTime.MAX_TIME); // dateDead
        outPacket.encodeShort(getPetAttribute()); // nPetAttribute
        outPacket.encodeShort(getPetSkill()); // usPetSkill
        outPacket.encodeInt(getRemainLife()); // nRemainLife
        outPacket.encodeShort(item.getAttribute()); // nAttribute
    }
}
