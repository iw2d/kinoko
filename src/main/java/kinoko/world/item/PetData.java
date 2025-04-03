package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.StringProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.server.packet.OutPacket;

import java.time.Duration;
import java.time.Instant;

public final class PetData {
    private String petName;
    private byte level;
    private byte fullness;
    private short tameness; // closeness in GMS
    private short petSkill;
    private short petAttribute;
    private int remainLife;
    private Duration remainHungriness = Duration.ofMillis(36000);
    private Instant lastUpdated = Instant.MIN;

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

    public Duration getRemainHungriness() {
        return remainHungriness;
    }

    public void setRemainHungriness(Duration remainHungriness) {
        this.remainHungriness = remainHungriness;
    }

    public boolean update(Instant now, Item item) {
        boolean shouldUpdate = false;
        Duration tElapsed = Duration.between(lastUpdated, now);
        lastUpdated = now;

        if (remainHungriness.compareTo(tElapsed) < 0) {
            ItemInfo itemInfo = ItemProvider.getItemInfo(item.getItemId()).orElseThrow();
            int hungry = itemInfo.getInfo(ItemInfoType.hungry);
            int bound = Math.max(1, 36 - 6 * hungry);
            int newHungrinessSeconds = (int) (Math.random() * bound) + 60;
            Duration newDuration = Duration.ofSeconds(newHungrinessSeconds);
            setRemainHungriness(newDuration);

            if (getFullness() > 0) {
                byte newFullness = (byte) (getFullness() - 1);
                setFullness(newFullness);
            }

            shouldUpdate = true;
        } else {
            Duration newDuration = getRemainHungriness().minus(tElapsed);
            setRemainHungriness(newDuration);
        }

        if (getFullness() == 0) {
            if (getTameness() > 0) {
                setTameness((byte) (getTameness() - 1));
            }

            setFullness((byte) 5);

            shouldUpdate = true;
        }

        return shouldUpdate;
    }

    public static PetData from(ItemInfo itemInfo) {
        final PetData petData = new PetData();
        petData.setPetName(StringProvider.getItemName(itemInfo.getItemId()));
        petData.setLevel((byte) 1);
        petData.setFullness((byte) 100);
        return petData;
    }
}
