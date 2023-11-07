package kinoko.world.item;

import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.Encodable;
import lombok.Data;

@Data
public final class EquipItem extends Item {
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

    private byte remainingUpgradeCount;
    private byte currentUpgradeCount;
    private int increaseUpgradeCount;
    private byte currentHyperUpgradeCount;

    private byte grade;
    private short option1;
    private short option2;
    private short option3;
    private short socket1;
    private short socket2;

    private byte levelUpType;
    private byte level;
    private int exp;
    private int durability;
    private String title;

    @Override
    public void encode(OutPacket outPacket) {
        assert getItemType() == 1;
        outPacket.encodeByte(getItemType()); // nType
        encodeBase(outPacket);

        // GW_ItemSlotEquip::RawDecode
        outPacket.encodeByte(getRemainingUpgradeCount()); // nRUC
        outPacket.encodeByte(getCurrentUpgradeCount()); // nCUC

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

        outPacket.encodeString(getTitle()); // sTitle

        outPacket.encodeShort(getAttribute()); // nAttribute
        outPacket.encodeByte(getLevelUpType()); // nLevelUpType
        outPacket.encodeByte(getLevel()); // nLevel
        outPacket.encodeInt(getExp()); // nEXP
        outPacket.encodeInt(getDurability()); // nDurability

        outPacket.encodeInt(getIncreaseUpgradeCount()); // nIUC
        outPacket.encodeByte(getGrade()); // nGrade
        outPacket.encodeByte(getCurrentHyperUpgradeCount()); // nCHUC

        outPacket.encodeShort(getOption1()); // nOption1
        outPacket.encodeShort(getOption2()); // nOption2
        outPacket.encodeShort(getOption3()); // nOption3
        outPacket.encodeShort(getSocket1()); // nSocket1
        outPacket.encodeShort(getSocket2()); // nSocket2

        if (!isCash()) {
            outPacket.encodeLong(getItemSn()); // liSN
        }

        outPacket.encodeFT(FileTime.ZERO_TIME); //ftEquipped
        outPacket.encodeInt(0); // nPrevBonusExpRate
    }
}
