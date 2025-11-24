package kinoko.world.user.stat;

import kinoko.server.Server;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.job.JobConstants;
import kinoko.world.user.User;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private long petSn1;
    private long petSn2;
    private long petSn3;
    private AdminLevel adminLevel = AdminLevel.PLAYER;

    public CharacterStat(){

    }

    public CharacterStat(int id, String name, byte gender, byte skin, int face, int hair,
                         short level, short job, short subJob,
                         short baseStr, short baseDex, short baseInt, short baseLuk,
                         int hp, int maxHp, int mp, int maxMp, short ap,
                         int exp, short pop, int posMap, byte portal,
                         long petSn1, long petSn2, long petSn3, AdminLevel adminLevel) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.skin = skin;
        this.face = face;
        this.hair = hair;
        this.level = level;
        this.job = job;
        this.subJob = subJob;
        this.baseStr = baseStr;
        this.baseDex = baseDex;
        this.baseInt = baseInt;
        this.baseLuk = baseLuk;
        this.hp = hp;
        this.maxHp = maxHp;
        this.mp = mp;
        this.maxMp = maxMp;
        this.ap = ap;
        this.exp = exp;
        this.pop = pop;
        this.posMap = posMap;
        this.portal = portal;
        this.petSn1 = petSn1;
        this.petSn2 = petSn2;
        this.petSn3 = petSn3;
        this.sp = ExtendSp.from(new HashMap<>()); // empty on init.

        this.adminLevel = adminLevel == null ? AdminLevel.PLAYER : adminLevel;
    }

    public int getId() {
        return id;
    }

    public int getCharacterId(){  // alias func
        return id;
    }

    public AdminLevel getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(AdminLevel adminLevel) {
        this.adminLevel = adminLevel;
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

        getUser().ifPresent(user -> {
            if (user.getFamilyInfo().hasFamily()) {
                user.getFamilyInfo().updateUser(user);
            }
        });
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

    public long getPetSn1() {
        return petSn1;
    }

    public void setPetSn1(long petSn1) {
        this.petSn1 = petSn1;
    }

    public long getPetSn2() {
        return petSn2;
    }

    public void setPetSn2(long petSn2) {
        this.petSn2 = petSn2;
    }

    public long getPetSn3() {
        return petSn3;
    }

    public void setPetSn3(long petSn3) {
        this.petSn3 = petSn3;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public boolean isValidAp(Stat stat, int delta) {
        switch (stat) {
            case STR -> {
                final int value = getBaseStr() + delta;
                return value >= GameConstants.STAT_MIN && value <= GameConstants.STAT_MAX;
            }
            case DEX -> {
                final int value = getBaseDex() + delta;
                return value >= GameConstants.STAT_MIN && value <= GameConstants.STAT_MAX;
            }
            case INT -> {
                final int value = getBaseInt() + delta;
                return value >= GameConstants.STAT_MIN && value <= GameConstants.STAT_MAX;
            }
            case LUK -> {
                final int value = getBaseLuk() + delta;
                return value >= GameConstants.STAT_MIN && value <= GameConstants.STAT_MAX;
            }
            case MHP -> {
                final int value = getMaxHp() + (delta * StatConstants.getIncHpByAp(getJob()));
                return value >= StatConstants.getMinHp(getLevel(), getJob()) && value <= GameConstants.HP_MAX;
            }
            case MMP -> {
                final int value = getMaxMp() + (delta * StatConstants.getIncMpByAp(getJob()));
                return value >= StatConstants.getMinMp(getLevel(), getJob()) && value <= GameConstants.MP_MAX;
            }
        }
        return false;
    }

    public Map<Stat, Object> addAp(Stat stat, int totalInt) {
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        switch (stat) {
            case STR -> {
                setBaseStr((short) Math.min(getBaseStr() + 1, GameConstants.STAT_MAX));
                statMap.put(Stat.STR, getBaseStr());
            }
            case DEX -> {
                setBaseDex((short) Math.min(getBaseDex() + 1, GameConstants.STAT_MAX));
                statMap.put(Stat.DEX, getBaseDex());
            }
            case INT -> {
                setBaseInt((short) Math.min(getBaseInt() + 1, GameConstants.STAT_MAX));
                statMap.put(Stat.INT, getBaseInt());
            }
            case LUK -> {
                setBaseLuk((short) Math.min(getBaseLuk() + 1, GameConstants.STAT_MAX));
                statMap.put(Stat.LUK, getBaseLuk());
            }
            case MHP -> {
                final int incHp = StatConstants.getIncHpByAp(getJob()) + Util.getRandom(StatConstants.INC_HP_VARIANCE);
                setMaxHp(Math.min(getMaxHp() + incHp, GameConstants.HP_MAX));
                statMap.put(Stat.MHP, getMaxHp());
            }
            case MMP -> {
                final int incMp = StatConstants.getIncMpByAp(getJob()) + Util.getRandom(StatConstants.INC_MP_VARIANCE) + (totalInt / 10);
                setMaxMp(Math.min(getMaxMp() + incMp, GameConstants.MP_MAX));
                statMap.put(Stat.MMP, getMaxMp());
            }
        }
        return statMap;
    }

    public Map<Stat, Object> removeAp(Stat stat) {
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        switch (stat) {
            case STR -> {
                setBaseStr((short) Math.max(getBaseStr() - 1, GameConstants.STAT_MIN));
                statMap.put(Stat.STR, getBaseStr());
            }
            case DEX -> {
                setBaseDex((short) Math.max(getBaseDex() - 1, GameConstants.STAT_MIN));
                statMap.put(Stat.DEX, getBaseDex());
            }
            case INT -> {
                setBaseInt((short) Math.max(getBaseInt() - 1, GameConstants.STAT_MIN));
                statMap.put(Stat.INT, getBaseInt());
            }
            case LUK -> {
                setBaseLuk((short) Math.max(getBaseLuk() - 1, GameConstants.STAT_MIN));
                statMap.put(Stat.LUK, getBaseLuk());
            }
            case MHP -> {
                setMaxHp(Math.max(getMaxHp() - StatConstants.getIncHpByAp(getJob()), StatConstants.getMinHp(getLevel(), getJob())));
                statMap.put(Stat.MHP, getMaxHp());
            }
            case MMP -> {
                setMaxMp(Math.max(getMaxMp() - StatConstants.getIncMpByAp(getJob()), StatConstants.getMinMp(getLevel(), getJob())));
                statMap.put(Stat.MMP, getMaxMp());
            }
        }
        return statMap;
    }

    public Map<Stat, Object> addExp(int delta, int totalInt) {
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        if (getLevel() >= GameConstants.getLevelMax(job)) {
            return statMap;
        }
        long newExp = ((long) getExp()) + delta;
        while (newExp >= GameConstants.getNextLevelExp(getLevel())) {
            newExp -= GameConstants.getNextLevelExp(getLevel());
            statMap.putAll(levelUp(totalInt));
        }
        setExp((int) newExp);
        statMap.put(Stat.EXP, (int) newExp);
        return statMap;
    }

    public Map<Stat, Object> levelUp(int totalInt) {
        final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
        if (getLevel() >= GameConstants.getLevelMax(job)) {
            return statMap;
        }
        // Update level
        setLevel((short) (getLevel() + 1));
        statMap.put(Stat.LEVEL, (byte) getLevel());
        // Update max hp
        final int incHp = StatConstants.getIncHp(getJob()) + Util.getRandom(StatConstants.INC_HP_VARIANCE);
        setMaxHp(Math.min(getMaxHp() + incHp, GameConstants.HP_MAX));
        statMap.put(Stat.MHP, getMaxHp());
        // Update max mp
        final int incMp = StatConstants.getIncMp(getJob()) + Util.getRandom(StatConstants.INC_MP_VARIANCE) + (totalInt / 10);
        setMaxMp(Math.min(getMaxMp() + incMp, GameConstants.MP_MAX));
        statMap.put(Stat.MMP, getMaxMp());
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
            setAp((short) (getAp() + StatConstants.getIncAp(getLevel(), getJob())));
            statMap.put(Stat.AP, getAp());
        }
        // Update sp
        if (!JobConstants.isBeginnerJob(getJob())) {
            if (JobConstants.isExtendSpJob(getJob())) {
                final int jobLevel = JobConstants.getExtendSpJobLevel(getJob(), getLevel());
                getSp().addSp(jobLevel, 3);
                statMap.put(Stat.SP, getSp());
            } else {
                getSp().addNonExtendSp(3);
                statMap.put(Stat.SP, (short) getSp().getNonExtendSp());
            }
        }
        return statMap;
    }

    public long getCumulativeExp() {
        long levelExp = 0;
        for (int level = 1; level < getLevel(); level++) {
            levelExp += GameConstants.getNextLevelExp(level);
        }
        return levelExp + getExp();
    }

    public Optional<User> getUser(){
        return Server.getCentralServerNode().getUserByCharacterId(getCharacterId());
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
        outPacket.encodeLong(petSn1);
        outPacket.encodeLong(petSn2);
        outPacket.encodeLong(petSn3);

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
