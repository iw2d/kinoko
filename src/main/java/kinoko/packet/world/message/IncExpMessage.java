package kinoko.packet.world.message;

import kinoko.server.packet.OutPacket;

public final class IncExpMessage extends Message {
    private int exp;
    private int partyBonus;
    private boolean white;
    private boolean quest;

    private IncExpMessage() {
        super(MessageType.INC_EXP);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case INC_EXP -> {
                outPacket.encodeByte(white); // white
                outPacket.encodeInt(exp); // exp
                outPacket.encodeByte(quest); // bOnQuest
                outPacket.encodeInt(0); // bonus event exp
                outPacket.encodeByte(0); // nMobEventBonusPercentage
                outPacket.encodeByte(0); // ignored
                outPacket.encodeInt(0); // nWeddingBonusEXP
                // outPacket.encodeByte(0); // nPlayTimeHour (if nMobEventBonusPercentage > 0)
                if (quest) {
                    outPacket.encodeByte(0); // nSpiritWeekEventEXP
                    // outPacket.encodeByte(0); // nQuestBonusRemainCount (if nSpiritWeekEventEXP != 0)
                }
                outPacket.encodeByte(0); // nPartyBonusEventRate
                outPacket.encodeInt(partyBonus); // nPartyBonusExp
                outPacket.encodeInt(0); // nItemBonusEXP
                outPacket.encodeInt(0); // nPremiumIPEXP
                outPacket.encodeInt(0); // nRainbowWeekEventEXP
                outPacket.encodeInt(0); // nPartyEXPRingEXP
                outPacket.encodeInt(0); // nCakePieEventBonus
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported message type");
            }
        }
    }

    public static IncExpMessage quest(int exp) {
        final IncExpMessage message = new IncExpMessage();
        message.white = true;
        message.exp = exp;
        message.quest = true;
        return message;
    }

    public static IncExpMessage mob(boolean white, int exp, int partyBonus) {
        final IncExpMessage message = new IncExpMessage();
        message.white = white;
        message.exp = exp;
        message.partyBonus = partyBonus;
        return message;
    }
}
