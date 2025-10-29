package kinoko.world.item;

import kinoko.provider.StringProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.packet.OutPacket;

public final class PetData {
    private String petName;
    private byte level;
    private byte fullness;
    private short tameness; // closeness in GMS
    private short petSkill;
    private short petAttribute;
    private int remainLife;

    public PetData() {
    }

    public PetData(PetData petData) {
        this.petName = petData.petName;
        this.level = petData.level;
        this.fullness = petData.fullness;
        this.tameness = petData.tameness;
        this.petSkill = petData.petSkill;
        this.petAttribute = petData.petAttribute;
        this.remainLife = petData.remainLife;
    }

    public PetData(
            String petName,
            byte level,
            byte fullness,
            short tameness,
            short petSkill,
            short petAttribute,
            int remainLife
    ) {
        this.petName = petName;
        this.level = level;
        this.fullness = fullness;
        this.tameness = tameness;
        this.petSkill = petSkill;
        this.petAttribute = petAttribute;
        this.remainLife = remainLife;
    }


    public void encode(OutPacket outPacket, Item item) {
        outPacket.encodeString(getPetName(), 13); // sPetName
        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeShort(getTameness()); // nTameness
        outPacket.encodeByte(getFullness()); // nRepleteness
        outPacket.encodeFT(item.getDateExpire()); // dateDead
        outPacket.encodeShort(getPetAttribute()); // nPetAttribute
        outPacket.encodeShort(getPetSkill()); // usPetSkill
        outPacket.encodeInt(getRemainLife()); // nRemainLife
        outPacket.encodeShort(item.getAttribute()); // nAttribute
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getFullness() {
        return fullness;
    }

    public void setFullness(byte fullness) {
        this.fullness = fullness;
    }

    public short getTameness() {
        return tameness;
    }

    public void setTameness(short tameness) {
        this.tameness = tameness;
    }

    public short getPetSkill() {
        return petSkill;
    }

    public void setPetSkill(short petSkill) {
        this.petSkill = petSkill;
    }

    public short getPetAttribute() {
        return petAttribute;
    }

    public void setPetAttribute(short petAttribute) {
        this.petAttribute = petAttribute;
    }

    public int getRemainLife() {
        return remainLife;
    }

    public void setRemainLife(int remainLife) {
        this.remainLife = remainLife;
    }

    public static PetData from(ItemInfo itemInfo) {
        final PetData petData = new PetData();
        petData.setPetName(StringProvider.getItemName(itemInfo.getItemId()));
        petData.setLevel((byte) 1);
        petData.setFullness((byte) 100);
        return petData;
    }
}
