package kinoko.world.item;

import kinoko.provider.WzProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;

import java.util.Map;

public final class EquipData {
    private short incStr;
    private short incDex;
    private short incInt;
    private short incLuk;
    private short incMaxHp;
    private short incMaxMp;
    private short incPad;
    private short incMad;
    private short incPdd;
    private short incMdd;
    private short incAcc;
    private short incEva;
    private short incCraft;
    private short incSpeed;
    private short incJump;

    private byte ruc; // Remaining Upgrade Count
    private byte cuc; // Current Upgrade Count
    private int iuc; // Increase Upgrade Count
    private byte chuc; // Current Hyper Upgrade Count

    private byte grade;
    private short option1;
    private short option2;
    private short option3;
    private short socket1;
    private short socket2;

    private byte levelUpType;
    private byte level;
    private int exp;
    private int durability = -1;

    public EquipData() {
    }

    public EquipData(EquipData equipData) {
        this.incStr = equipData.incStr;
        this.incDex = equipData.incDex;
        this.incInt = equipData.incInt;
        this.incLuk = equipData.incLuk;
        this.incMaxHp = equipData.incMaxHp;
        this.incMaxMp = equipData.incMaxMp;
        this.incPad = equipData.incPad;
        this.incMad = equipData.incMad;
        this.incPdd = equipData.incPdd;
        this.incMdd = equipData.incMdd;
        this.incAcc = equipData.incAcc;
        this.incEva = equipData.incEva;
        this.incCraft = equipData.incCraft;
        this.incSpeed = equipData.incSpeed;
        this.incJump = equipData.incJump;

        this.ruc = equipData.ruc;
        this.cuc = equipData.cuc;
        this.iuc = equipData.iuc;
        this.chuc = equipData.chuc;

        this.grade = equipData.grade;
        this.option1 = equipData.option1;
        this.option2 = equipData.option2;
        this.option3 = equipData.option3;
        this.socket1 = equipData.socket1;
        this.socket2 = equipData.socket2;

        this.levelUpType = equipData.levelUpType;
        this.level = equipData.level;
        this.exp = equipData.exp;
        this.durability = equipData.durability;
    }

    public void encode(OutPacket outPacket, Item item) {
        outPacket.encodeByte(getRuc()); // nRUC
        outPacket.encodeByte(getCuc()); // nCUC

        outPacket.encodeShort(getIncStr()); // iSTR
        outPacket.encodeShort(getIncDex()); // iDEX
        outPacket.encodeShort(getIncInt()); // iINT
        outPacket.encodeShort(getIncLuk()); // iLUK
        outPacket.encodeShort(getIncMaxHp()); // iMaxHP
        outPacket.encodeShort(getIncMaxMp()); // iMaxMP
        outPacket.encodeShort(getIncPad()); // iPAD
        outPacket.encodeShort(getIncMad()); // iMAD
        outPacket.encodeShort(getIncPdd()); // iPDD
        outPacket.encodeShort(getIncMdd()); // iMDD
        outPacket.encodeShort(getIncAcc()); // iACC
        outPacket.encodeShort(getIncEva()); // iEva
        outPacket.encodeShort(getIncCraft()); // iCraft
        outPacket.encodeShort(getIncSpeed()); // iSpeed
        outPacket.encodeShort(getIncJump()); // iJump

        outPacket.encodeString(item.getTitle()); // sTitle

        outPacket.encodeShort(item.getAttribute()); // nAttribute
        outPacket.encodeByte(getLevelUpType()); // nLevelUpType
        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeInt(getExp()); // nEXP
        outPacket.encodeInt(getDurability()); // nDurability

        outPacket.encodeInt(getIuc()); // nIUC
        outPacket.encodeByte(getGrade()); // nGrade
        outPacket.encodeByte(getChuc()); // nCHUC

        outPacket.encodeShort(getOption1()); // nOption1
        outPacket.encodeShort(getOption2()); // nOption2
        outPacket.encodeShort(getOption3()); // nOption3
        outPacket.encodeShort(getSocket1()); // nSocket1
        outPacket.encodeShort(getSocket2()); // nSocket2

        if (!item.isCash()) {
            outPacket.encodeLong(item.getItemSn()); // liSN
        }

        outPacket.encodeFT(FileTime.ZERO_TIME); // ftEquipped
        outPacket.encodeInt(0); // nPrevBonusExpRate
    }

    public short getIncStr() {
        return incStr;
    }

    public void setIncStr(short incStr) {
        this.incStr = incStr;
    }

    public short getIncDex() {
        return incDex;
    }

    public void setIncDex(short incDex) {
        this.incDex = incDex;
    }

    public short getIncInt() {
        return incInt;
    }

    public void setIncInt(short incInt) {
        this.incInt = incInt;
    }

    public short getIncLuk() {
        return incLuk;
    }

    public void setIncLuk(short incLuk) {
        this.incLuk = incLuk;
    }

    public short getIncMaxHp() {
        return incMaxHp;
    }

    public void setIncMaxHp(short incMaxHp) {
        this.incMaxHp = incMaxHp;
    }

    public short getIncMaxMp() {
        return incMaxMp;
    }

    public void setIncMaxMp(short incMaxMp) {
        this.incMaxMp = incMaxMp;
    }

    public short getIncPad() {
        return incPad;
    }

    public void setIncPad(short incPad) {
        this.incPad = incPad;
    }

    public short getIncMad() {
        return incMad;
    }

    public void setIncMad(short incMad) {
        this.incMad = incMad;
    }

    public short getIncPdd() {
        return incPdd;
    }

    public void setIncPdd(short incPdd) {
        this.incPdd = incPdd;
    }

    public short getIncMdd() {
        return incMdd;
    }

    public void setIncMdd(short incMdd) {
        this.incMdd = incMdd;
    }

    public short getIncAcc() {
        return incAcc;
    }

    public void setIncAcc(short incAcc) {
        this.incAcc = incAcc;
    }

    public short getIncEva() {
        return incEva;
    }

    public void setIncEva(short incEva) {
        this.incEva = incEva;
    }

    public short getIncCraft() {
        return incCraft;
    }

    public void setIncCraft(short incCraft) {
        this.incCraft = incCraft;
    }

    public short getIncSpeed() {
        return incSpeed;
    }

    public void setIncSpeed(short incSpeed) {
        this.incSpeed = incSpeed;
    }

    public short getIncJump() {
        return incJump;
    }

    public void setIncJump(short incJump) {
        this.incJump = incJump;
    }

    public byte getRuc() {
        return ruc;
    }

    public void setRuc(byte ruc) {
        this.ruc = ruc;
    }

    public byte getCuc() {
        return cuc;
    }

    public void setCuc(byte cuc) {
        this.cuc = cuc;
    }

    public int getIuc() {
        return iuc;
    }

    public void setIuc(int iuc) {
        this.iuc = iuc;
    }

    public byte getChuc() {
        return chuc;
    }

    public void setChuc(byte chuc) {
        this.chuc = chuc;
    }

    public byte getGrade() {
        return grade;
    }

    public void setGrade(byte grade) {
        this.grade = grade;
    }

    public short getOption1() {
        return option1;
    }

    public void setOption1(short option1) {
        this.option1 = option1;
    }

    public short getOption2() {
        return option2;
    }

    public void setOption2(short option2) {
        this.option2 = option2;
    }

    public short getOption3() {
        return option3;
    }

    public void setOption3(short option3) {
        this.option3 = option3;
    }

    public short getSocket1() {
        return socket1;
    }

    public void setSocket1(short socket1) {
        this.socket1 = socket1;
    }

    public short getSocket2() {
        return socket2;
    }

    public void setSocket2(short socket2) {
        this.socket2 = socket2;
    }

    public byte getLevelUpType() {
        return levelUpType;
    }

    public void setLevelUpType(byte levelUpType) {
        this.levelUpType = levelUpType;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void applyScrollStats(Map<ItemInfoType, Object> scrollStats) {
        for (var entry : scrollStats.entrySet()) {
            final int value = WzProvider.getInteger(entry.getValue(), 0);
            switch (entry.getKey()) {
                case incSTR -> {
                    setIncStr((short) Math.clamp(getIncStr() + value, 0, Short.MAX_VALUE));
                }
                case incDEX -> {
                    setIncDex((short) Math.clamp(getIncDex() + value, 0, Short.MAX_VALUE));
                }
                case incINT -> {
                    setIncInt((short) Math.clamp(getIncInt() + value, 0, Short.MAX_VALUE));
                }
                case incLUK -> {
                    setIncLuk((short) Math.clamp(getIncLuk() + value, 0, Short.MAX_VALUE));
                }
                case incMHP, incMaxHP -> {
                    setIncMaxHp((short) Math.clamp(getIncMaxHp() + value, 0, Short.MAX_VALUE));
                }
                case incMMP, incMaxMP -> {
                    setIncMaxMp((short) Math.clamp(getIncMaxMp() + value, 0, Short.MAX_VALUE));
                }
                case incPAD -> {
                    setIncPad((short) Math.clamp(getIncPad() + value, 0, Short.MAX_VALUE));
                }
                case incMAD -> {
                    setIncMad((short) Math.clamp(getIncMad() + value, 0, Short.MAX_VALUE));
                }
                case incPDD -> {
                    setIncPdd((short) Math.clamp(getIncPdd() + value, 0, Short.MAX_VALUE));
                }
                case incMDD -> {
                    setIncMdd((short) Math.clamp(getIncMdd() + value, 0, Short.MAX_VALUE));
                }
                case incACC -> {
                    setIncAcc((short) Math.clamp(getIncAcc() + value, 0, Short.MAX_VALUE));
                }
                case incEVA -> {
                    setIncEva((short) Math.clamp(getIncEva() + value, 0, Short.MAX_VALUE));
                }
                case incCraft -> {
                    setIncCraft((short) Math.clamp(getIncCraft() + value, 0, Short.MAX_VALUE));
                }
                case incSpeed -> {
                    setIncSpeed((short) Math.clamp(getIncSpeed() + value, 0, Short.MAX_VALUE));
                }
                case incJump -> {
                    setIncJump((short) Math.clamp(getIncJump() + value, 0, Short.MAX_VALUE));
                }
            }
        }
    }

    public static EquipData from(ItemInfo itemInfo) {
        final EquipData equipData = new EquipData();
        for (ItemInfoType infoType : itemInfo.getItemInfos().keySet()) {
            switch (infoType) {
                case incSTR -> {
                    equipData.setIncStr((short) itemInfo.getInfo(infoType));
                }
                case incDEX -> {
                    equipData.setIncDex((short) itemInfo.getInfo(infoType));
                }
                case incINT -> {
                    equipData.setIncInt((short) itemInfo.getInfo(infoType));
                }
                case incLUK -> {
                    equipData.setIncLuk((short) itemInfo.getInfo(infoType));
                }
                case incMHP, incMaxHP -> {
                    equipData.setIncMaxHp((short) itemInfo.getInfo(infoType));
                }
                case incMMP, incMaxMP -> {
                    equipData.setIncMaxMp((short) itemInfo.getInfo(infoType));
                }
                case incPAD -> {
                    equipData.setIncPad((short) itemInfo.getInfo(infoType));
                }
                case incMAD -> {
                    equipData.setIncMad((short) itemInfo.getInfo(infoType));
                }
                case incPDD -> {
                    equipData.setIncPdd((short) itemInfo.getInfo(infoType));
                }
                case incMDD -> {
                    equipData.setIncMdd((short) itemInfo.getInfo(infoType));
                }
                case incACC -> {
                    equipData.setIncAcc((short) itemInfo.getInfo(infoType));
                }
                case incEVA -> {
                    equipData.setIncEva((short) itemInfo.getInfo(infoType));
                }
                case incCraft -> {
                    equipData.setIncCraft((short) itemInfo.getInfo(infoType));
                }
                case incSpeed -> {
                    equipData.setIncSpeed((short) itemInfo.getInfo(infoType));
                }
                case incJump -> {
                    equipData.setIncJump((short) itemInfo.getInfo(infoType));
                }
                case tuc -> {
                    equipData.setRuc((byte) itemInfo.getInfo(infoType));
                }
                case durability -> {
                    equipData.setDurability(itemInfo.getInfo(infoType));
                }
            }
        }
        return equipData;
    }
}
