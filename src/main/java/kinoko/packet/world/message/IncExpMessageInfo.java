package kinoko.packet.world.message;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class IncExpMessageInfo implements Encodable {
    private boolean quest;
    private boolean white;
    private int exp;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(white);
        outPacket.encodeInt(exp);
        outPacket.encodeByte(quest); // bOnQuest
        outPacket.encodeInt(0); // bonus event exp
        outPacket.encodeByte(0); // nMobEventBonusPercentage
        outPacket.encodeByte(0); // ignored
        outPacket.encodeInt(0); // nWeddingBonusEXP
        // outPacket.encodeByte(0); // nPlayTimeHour (if nMobEventBonusPercentage > 0)

        outPacket.encodeByte(0); // nQuestBonusRemainCount (or spirit week bonus exp)
        outPacket.encodeByte(0); // nPartyBonusEventRate
        outPacket.encodeInt(0); // nPartyBonusExp
        outPacket.encodeInt(0); // nItemBonusEXP
        outPacket.encodeInt(0); // nPremiumIPEXP
        outPacket.encodeInt(0); // nRainbowWeekEventEXP
        outPacket.encodeInt(0); // nPartyEXPRingEXP
        outPacket.encodeInt(0); // nCakePieEventBonus
    }
}
