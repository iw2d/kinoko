package kinoko.packet.world;

import kinoko.packet.world.message.Message;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterStat;

import java.util.Set;

public final class WvsContext {
    public static OutPacket statChanged(Set<StatFlag> flags, CharacterData characterData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.STAT_CHANGED);
        outPacket.encodeByte(true); // reset bExclRequestSent

        // GW_CharacterStat::DecodeChangeStat
        final CharacterStat characterStat = characterData.getCharacterStat();
        outPacket.encodeInt(StatFlag.from(flags));
        if (flags.contains(StatFlag.SKIN)) {
            outPacket.encodeByte(characterStat.getSkin());
        }
        if (flags.contains(StatFlag.FACE)) {
            outPacket.encodeInt(characterStat.getFace());
        }
        if (flags.contains(StatFlag.HAIR)) {
            outPacket.encodeInt(characterStat.getHair());
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
            outPacket.encodeByte(characterStat.getLevel());
        }
        if (flags.contains(StatFlag.JOB)) {
            outPacket.encodeShort(characterStat.getJob());
        }
        if (flags.contains(StatFlag.STR)) {
            outPacket.encodeShort(characterStat.getBaseStr());
        }
        if (flags.contains(StatFlag.DEX)) {
            outPacket.encodeShort(characterStat.getBaseDex());
        }
        if (flags.contains(StatFlag.INT)) {
            outPacket.encodeShort(characterStat.getBaseInt());
        }
        if (flags.contains(StatFlag.LUK)) {
            outPacket.encodeShort(characterStat.getBaseLuk());
        }
        if (flags.contains(StatFlag.HP)) {
            outPacket.encodeInt(characterStat.getHp());
        }
        if (flags.contains(StatFlag.MAX_HP)) {
            outPacket.encodeInt(characterStat.getMaxHp());
        }
        if (flags.contains(StatFlag.MP)) {
            outPacket.encodeInt(characterStat.getMp());
        }
        if (flags.contains(StatFlag.MAX_MP)) {
            outPacket.encodeInt(characterStat.getMaxMp());
        }
        if (flags.contains(StatFlag.AP)) {
            outPacket.encodeShort(characterStat.getAp());
        }
        if (flags.contains(StatFlag.SP)) {
            characterStat.getSp().encode(characterStat.getJob(), outPacket);
        }
        if (flags.contains(StatFlag.EXP)) {
            outPacket.encodeInt(characterStat.getExp());
        }
        if (flags.contains(StatFlag.POP)) {
            outPacket.encodeShort(characterStat.getPop());
        }
        if (flags.contains(StatFlag.MONEY)) {
            outPacket.encodeInt(characterData.getCharacterInventory().getMoney());
        }
        if (flags.contains(StatFlag.TEMP_EXP)) {
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
