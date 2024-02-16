package kinoko.world.user;

import kinoko.server.packet.OutPacket;

import java.util.Set;

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

    public void encode(int characterId, String characterName, OutPacket outPacket) {
        outPacket.encodeInt(characterId); // dwCharacterID
        outPacket.encodeString(characterName, 13); // sCharacterName
        outPacket.encodeByte(gender); // nGender
        outPacket.encodeByte(skin); // nSkin
        outPacket.encodeInt(face); // nFace
        outPacket.encodeInt(hair); // nHair

        // aliPetLockerSN
        for (int i = 0; i < 3; i++) {
            outPacket.encodeLong(0);
        }

        outPacket.encodeByte(level); // nLevel
        outPacket.encodeShort(job); // nJob
        outPacket.encodeShort(baseStr); // nSTR
        outPacket.encodeShort(baseDex); // nDEX
        outPacket.encodeShort(baseInt); // nINT
        outPacket.encodeShort(baseLuk); // nLUK
        outPacket.encodeInt(hp); // nHP
        outPacket.encodeInt(maxHp); // nMHP
        outPacket.encodeInt(mp); // nMP
        outPacket.encodeInt(maxMp); // nMMP
        outPacket.encodeShort(ap); // nAP
        sp.encode(job, outPacket);

        outPacket.encodeInt(exp); // nEXP
        outPacket.encodeShort(pop); // nPOP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(posMap); // dwPosMap
        outPacket.encodeByte(portal); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(subJob); // nSubJob
    }

    public void encodeChangeStat(Set<StatFlag> flags, int money, OutPacket outPacket) {
        // GW_CharacterStat::DecodeChangeStat
        outPacket.encodeInt(StatFlag.from(flags));
        if (flags.contains(StatFlag.SKIN)) {
            outPacket.encodeByte(skin);
        }
        if (flags.contains(StatFlag.FACE)) {
            outPacket.encodeInt(face);
        }
        if (flags.contains(StatFlag.HAIR)) {
            outPacket.encodeInt(hair);
        }
        if (flags.contains(StatFlag.PET_1)) {
            outPacket.encodeLong(0);
        }
        if (flags.contains(StatFlag.PET_2)) {
            outPacket.encodeLong(0);
        }
        if (flags.contains(StatFlag.PET_3)) {
            outPacket.encodeLong(0);
        }
        if (flags.contains(StatFlag.LEVEL)) {
            outPacket.encodeByte(level);
        }
        if (flags.contains(StatFlag.JOB)) {
            outPacket.encodeShort(job);
        }
        if (flags.contains(StatFlag.STR)) {
            outPacket.encodeShort(baseStr);
        }
        if (flags.contains(StatFlag.DEX)) {
            outPacket.encodeShort(baseDex);
        }
        if (flags.contains(StatFlag.INT)) {
            outPacket.encodeShort(baseInt);
        }
        if (flags.contains(StatFlag.LUK)) {
            outPacket.encodeShort(baseLuk);
        }
        if (flags.contains(StatFlag.HP)) {
            outPacket.encodeInt(hp);
        }
        if (flags.contains(StatFlag.MAX_HP)) {
            outPacket.encodeInt(maxHp);
        }
        if (flags.contains(StatFlag.MP)) {
            outPacket.encodeInt(mp);
        }
        if (flags.contains(StatFlag.MAX_MP)) {
            outPacket.encodeInt(maxMp);
        }
        if (flags.contains(StatFlag.AP)) {
            outPacket.encodeShort(ap);
        }
        if (flags.contains(StatFlag.SP)) {
            sp.encode(job, outPacket);
        }
        if (flags.contains(StatFlag.EXP)) {
            outPacket.encodeInt(exp);
        }
        if (flags.contains(StatFlag.POP)) {
            outPacket.encodeShort(pop);
        }
        if (flags.contains(StatFlag.MONEY)) {
            outPacket.encodeInt(money);
        }
        if (flags.contains(StatFlag.TEMP_EXP)) {
            outPacket.encodeInt(0);
        }
    }
}
