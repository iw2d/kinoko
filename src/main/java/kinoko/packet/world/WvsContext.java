package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterStat;
import kinoko.world.user.StatFlag;

import java.util.Set;

public final class WvsContext {
    public static OutPacket statChanged(Set<StatFlag> flags, CharacterData characterData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.STAT_CHANGED);
        outPacket.encodeByte(true); // reset bExclRequestSent

        final CharacterStat characterStat = characterData.getCharacterStat();
        characterStat.encodeChangeStat(flags, characterData.getCharacterInventory().getMoney(), outPacket);

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
