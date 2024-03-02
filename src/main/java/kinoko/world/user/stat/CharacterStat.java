package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.job.JobConstants;

import java.util.EnumMap;
import java.util.Map;

public final class CharacterStat implements Encodable {
    private int id;
    private String name;
    private byte gender;
    private byte skin;
    private int face;
    private int hair;
    private short level; // encoded as unsigned byte
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
    private ExtendSp sp;
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

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
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

    public ExtendSp getSp() {
        return sp;
    }

    public void setSp(ExtendSp sp) {
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


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public Map<Stat, Object> addExp(int delta) {
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        if (getLevel() >= GameConstants.MAX_LEVEL) {
            return statMap;
        }
        long newExp = ((long) getExp()) + delta;
        while (newExp >= GameConstants.getNextLevelExp(getLevel())) {
            newExp -= GameConstants.getNextLevelExp(getLevel());
            statMap.putAll(levelUp());
        }
        setExp((int) newExp);
        statMap.put(Stat.EXP, (int) newExp);
        return statMap;
    }

    public Map<Stat, Object> levelUp() {
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        if (getLevel() >= GameConstants.MAX_LEVEL) {
            return statMap;
        }
        // Update level
        setLevel((short) (getLevel() + 1));
        statMap.put(Stat.LEVEL, (byte) getLevel());
        // Update max hp
        final int incHp = StatConstants.getIncHp(getJob()) + Util.getRandom(StatConstants.INC_HP_VARIANCE);
        setMaxHp(Math.min(getMaxHp() + incHp, GameConstants.MAX_HP));
        statMap.put(Stat.MAX_HP, getMaxHp());
        // Update max mp
        final int incMp = StatConstants.getIncMp(getJob()) + Util.getRandom(StatConstants.INC_MP_VARIANCE);
        setMaxMp(Math.min(getMaxMp() + incMp, GameConstants.MAX_MP));
        statMap.put(Stat.MAX_MP, getMaxMp());
        // Update ap (auto distribution for beginners)
        if (JobConstants.isBeginnerJob(getJob()) && getLevel() <= 11) {
            if (getLevel() <= 6) {
                setBaseStr((short) (getBaseStr() + 5));
                statMap.put(Stat.STR, getBaseStr());
            } else {
                setBaseStr((short) (getBaseStr() + 4));
                statMap.put(Stat.STR, getBaseStr());
                setBaseDex((short) (getBaseDex() + 4));
                statMap.put(Stat.DEX, getBaseDex());
            }
        } else {
            setAp((short) (getAp() + StatConstants.getIncAp(getJob())));
            statMap.put(Stat.AP, getAp());
        }
        // Update sp
        if (JobConstants.isExtendSpJob(getJob())) {
            final int jobLevel = JobConstants.getJobLevel(getJob());
            getSp().addSp(jobLevel, StatConstants.getIncSp(getJob(), getLevel()));
            statMap.put(Stat.SP, getSp());
        } else {
            getSp().addNonExtendSp(StatConstants.getIncSp(getJob(), getLevel()));
            statMap.put(Stat.SP, (short) getSp().getNonExtendSp());
        }
        return statMap;
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
            outPacket.encodeShort((short) sp.getNonExtendSp());
        }

        outPacket.encodeInt(exp); // nEXP
        outPacket.encodeShort(pop); // nPOP
        outPacket.encodeInt(0); // nTempEXP
        outPacket.encodeInt(posMap); // dwPosMap
        outPacket.encodeByte(portal); // nPortal
        outPacket.encodeInt(0); // nPlayTime
        outPacket.encodeShort(subJob); // nSubJob
    }
}
