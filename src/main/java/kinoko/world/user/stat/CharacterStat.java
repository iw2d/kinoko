package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.world.Encodable;
import kinoko.world.job.JobConstants;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class CharacterStat implements Encodable, Lockable<CharacterStat> {
    private final Lock lock = new ReentrantLock();
    private int id;
    private String name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Optional<Map<Stat, Object>> addExp(int exp) {
        // TODO + level up handling
        return Optional.of(Map.of());
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(id); // dwCharacterID
        outPacket.encodeString(name, 13); // sCharacterName
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
        if (JobConstants.isExtendSpJob(job)) {
            sp.encode(outPacket);
        } else {
            outPacket.encodeShort(sp.getTotal());
        }

        outPacket.encodeInt(exp); // nEXP
        outPacket.encodeShort(pop); // nPOP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(posMap); // dwPosMap
        outPacket.encodeByte(portal); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(subJob); // nSubJob
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
