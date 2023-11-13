package kinoko.packet.world;

import kinoko.packet.world.message.Message;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterStat;

public final class WvsContext {
    public static OutPacket statChanged(StatFlag flag, CharacterData characterData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.STAT_CHANGED);
        outPacket.encodeByte(true); // reset bExclRequestSent

        // GW_CharacterStat::DecodeChangeStat
        final CharacterStat characterStat = characterData.getCharacterStat();
        outPacket.encodeInt(flag.getValue());
        if (flag.hasFlag(StatFlag.SKIN)) {
            outPacket.encodeByte(characterStat.getSkin());
        }
        if (flag.hasFlag(StatFlag.FACE)) {
            outPacket.encodeInt(characterStat.getFace());
        }
        if (flag.hasFlag(StatFlag.HAIR)) {
            outPacket.encodeInt(characterStat.getHair());
        }
        if (flag.hasFlag(StatFlag.PET_1)) {
            outPacket.encodeLong(0);
        }
        if (flag.hasFlag(StatFlag.PET_2)) {
            outPacket.encodeLong(0);
        }
        if (flag.hasFlag(StatFlag.PET_3)) {
            outPacket.encodeLong(0);
        }
        if (flag.hasFlag(StatFlag.LEVEL)) {
            outPacket.encodeByte(characterStat.getLevel());
        }
        if (flag.hasFlag(StatFlag.JOB)) {
            outPacket.encodeShort(characterStat.getJob());
        }
        if (flag.hasFlag(StatFlag.STR)) {
            outPacket.encodeShort(characterStat.getBaseStr());
        }
        if (flag.hasFlag(StatFlag.DEX)) {
            outPacket.encodeShort(characterStat.getBaseDex());
        }
        if (flag.hasFlag(StatFlag.INT)) {
            outPacket.encodeShort(characterStat.getBaseInt());
        }
        if (flag.hasFlag(StatFlag.LUK)) {
            outPacket.encodeShort(characterStat.getBaseLuk());
        }
        if (flag.hasFlag(StatFlag.HP)) {
            outPacket.encodeInt(characterStat.getHp());
        }
        if (flag.hasFlag(StatFlag.MAX_HP)) {
            outPacket.encodeInt(characterStat.getMaxHp());
        }
        if (flag.hasFlag(StatFlag.MP)) {
            outPacket.encodeInt(characterStat.getMp());
        }
        if (flag.hasFlag(StatFlag.MAX_MP)) {
            outPacket.encodeInt(characterStat.getMaxMp());
        }
        if (flag.hasFlag(StatFlag.AP)) {
            outPacket.encodeShort(characterStat.getAp());
        }
        if (flag.hasFlag(StatFlag.SP)) {
            characterStat.getSp().encode(characterStat.getJob(), outPacket);
        }
        if (flag.hasFlag(StatFlag.EXP)) {
            outPacket.encodeInt(characterStat.getExp());
        }
        if (flag.hasFlag(StatFlag.POP)) {
            outPacket.encodeShort(characterStat.getPop());
        }
        if (flag.hasFlag(StatFlag.MONEY)) {
            outPacket.encodeInt(characterData.getCharacterInventory().getMoney());
        }
        if (flag.hasFlag(StatFlag.TEMP_EXP)) {
            outPacket.encodeInt(0);
        }

        outPacket.encodeByte(false); // bool -> byte (CUserLocal::SetSecondaryStatChangedPoint)
        outPacket.encodeByte(false); // bool -> int, int (CBattleRecordMan::SetBattleRecoveryInfo)
        return outPacket;
    }

    public static OutPacket message(Message message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MESSAGE);
        message.encode(outPacket);
        return outPacket;
    }
}
