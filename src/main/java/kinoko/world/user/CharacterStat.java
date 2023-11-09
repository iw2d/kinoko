package kinoko.world.user;

import kinoko.server.packet.OutPacket;

public final class CharacterStat {
    private byte gender;
    private byte skin;
    private int face;
    private int hair;
    private byte level;
    private short job;
    private short subJob;
    private short baseStr;
    private short baseDex;
    private short baseInt;
    private short baseLuk;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private short ap;
    private ExtendSP sp;
    private int exp;
    private short pop;
    private int posMap;
    private byte portal;

    public void encode(int characterId, String characterName, OutPacket outPacket) {
        outPacket.encodeInt(characterId); // dwCharacterID
        outPacket.encodeString(characterName, 13); // sCharacterName
        outPacket.encodeByte(getGender()); // nGender
        outPacket.encodeByte(getSkin()); // nSkin
        outPacket.encodeInt(getFace()); // nFace
        outPacket.encodeInt(getHair()); // nHair

        // aliPetLockerSN
        for (int i = 0; i < 3; i++) {
            outPacket.encodeLong(0);
        }

        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeShort(getJob()); // nJob
        outPacket.encodeShort(getBaseStr()); // nSTR
        outPacket.encodeShort(getBaseDex()); // nDEX
        outPacket.encodeShort(getBaseInt()); // nINT
        outPacket.encodeShort(getBaseLuk()); // nLUK
        outPacket.encodeInt(getHp()); // nHP
        outPacket.encodeInt(getMaxHp()); // nMHP
        outPacket.encodeInt(getMp()); // nMP
        outPacket.encodeInt(getMaxMp()); // nMMP
        outPacket.encodeShort(getAp()); // nAP
        getSp().encode(getJob(), outPacket);

        outPacket.encodeInt(getExp()); // nEXP
        outPacket.encodeShort(getPop()); // nPOP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(getPosMap()); // dwPosMap
        outPacket.encodeByte(getPortal()); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(getSubJob()); // nSubJob
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public byte getSkin() {
        return skin;
    }

    public void setSkin(byte skin) {
        this.skin = skin;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getHair() {
        return hair;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public short getJob() {
        return job;
    }

    public void setJob(short job) {
        this.job = job;
    }

    public short getSubJob() {
        return subJob;
    }

    public void setSubJob(short subJob) {
        this.subJob = subJob;
    }

    public short getBaseStr() {
        return baseStr;
    }

    public void setBaseStr(short baseStr) {
        this.baseStr = baseStr;
    }

    public short getBaseDex() {
        return baseDex;
    }

    public void setBaseDex(short baseDex) {
        this.baseDex = baseDex;
    }

    public short getBaseInt() {
        return baseInt;
    }

    public void setBaseInt(short baseInt) {
        this.baseInt = baseInt;
    }

    public short getBaseLuk() {
        return baseLuk;
    }

    public void setBaseLuk(short baseLuk) {
        this.baseLuk = baseLuk;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
    }

    public short getAp() {
        return ap;
    }

    public void setAp(short ap) {
        this.ap = ap;
    }

    public ExtendSP getSp() {
        return sp;
    }

    public void setSp(ExtendSP sp) {
        this.sp = sp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public short getPop() {
        return pop;
    }

    public void setPop(short pop) {
        this.pop = pop;
    }

    public int getPosMap() {
        return posMap;
    }

    public void setPosMap(int posMap) {
        this.posMap = posMap;
    }

    public byte getPortal() {
        return portal;
    }

    public void setPortal(byte portal) {
        this.portal = portal;
    }
}
