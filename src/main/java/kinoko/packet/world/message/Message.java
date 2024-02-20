package kinoko.packet.world.message;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.quest.QuestRecord;

public class Message implements Encodable {
    protected final MessageType type;
    private boolean bool1;
    private boolean bool2;
    private int int1;
    private int int2;
    private String string1;
    private QuestRecord questRecord;

    protected Message(MessageType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case QUEST_RECORD -> {
                outPacket.encodeShort(questRecord.getQuestId()); // nQuestID
                outPacket.encodeByte(questRecord.getState().getValue());
                switch (questRecord.getState()) {
                    case NONE -> {
                        outPacket.encodeByte(true); // delete quest
                    }
                    case PERFORM -> {
                        outPacket.encodeString(questRecord.getValue()); // sQRValue
                    }
                    case COMPLETE -> {
                        outPacket.encodeFT(questRecord.getCompletedTime()); // ftEnd
                    }
                }
            }
            case CASH_ITEM_EXPIRE, INC_POP, INC_MONEY, INC_GP, GIVE_BUFF -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case INC_EXP -> {
                outPacket.encodeByte(bool1); // white
                outPacket.encodeInt(int1); // exp
                outPacket.encodeByte(bool2); // bOnQuest
                outPacket.encodeInt(0); // bonus event exp
                outPacket.encodeByte(0); // nMobEventBonusPercentage
                outPacket.encodeByte(0); // ignored
                outPacket.encodeInt(0); // nWeddingBonusEXP
                // outPacket.encodeByte(0); // nPlayTimeHour (if nMobEventBonusPercentage > 0)
                outPacket.encodeByte(0); // if (bOnQuest) nQuestBonusRemainCount else nSpiritWeekEventEXP
                outPacket.encodeByte(0); // nPartyBonusEventRate
                outPacket.encodeInt(0); // nPartyBonusExp
                outPacket.encodeInt(0); // nItemBonusEXP
                outPacket.encodeInt(0); // nPremiumIPEXP
                outPacket.encodeInt(0); // nRainbowWeekEventEXP
                outPacket.encodeInt(0); // nPartyEXPRingEXP
                outPacket.encodeInt(0); // nCakePieEventBonus
            }
            case INC_SP -> {
                outPacket.encodeShort(int1); // nJob
                outPacket.encodeByte(int2); // nSP
            }
            case GENERAL_ITEM_EXPIRE, ITEM_PROTECT_EXPIRE, SKILL_EXPIRE -> {
                outPacket.encodeByte(1); // count
                outPacket.encodeInt(int1); // nItemID / nSkillID
            }
            case SYSTEM -> {
                outPacket.encodeString(string1); // sChat
            }
            case QUEST_RECORD_EX -> {
                outPacket.encodeShort(int1); // usQuestID
                outPacket.encodeString(string1); // rawStr
            }
            case ITEM_EXPIRE_REPLACE -> {
                outPacket.encodeByte(1); // count
                outPacket.encodeString(string1); // sChat
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported message type");
            }
        }
    }

    public static Message questRecord(QuestRecord questRecord) {
        final Message message = new Message(MessageType.QUEST_RECORD);
        message.questRecord = questRecord;
        return message;
    }

    public static Message questRecordEx(int questId, String value) {
        final Message message = new Message(MessageType.QUEST_RECORD_EX);
        message.int1 = questId;
        message.string1 = value;
        return message;
    }

    public static Message incPop(int pop) {
        final Message message = new Message(MessageType.INC_POP);
        message.int1 = pop;
        return message;
    }

    public static Message incMoney(int money) {
        final Message message = new Message(MessageType.INC_MONEY);
        message.int1 = money;
        return message;
    }

    public static Message incExp(int exp, boolean white, boolean quest) {
        final Message message = new Message(MessageType.INC_EXP);
        message.int1 = exp;
        message.bool1 = white;
        message.bool2 = quest;
        return message;
    }

    public static Message incSp(int job, int sp) {
        final Message message = new Message(MessageType.INC_SP);
        message.int1 = job;
        message.int2 = sp;
        return message;
    }

    public static Message system(String text) {
        final Message message = new Message(MessageType.SYSTEM);
        message.string1 = text;
        return message;
    }

    public static Message system(String format, Object... args) {
        final Message message = new Message(MessageType.SYSTEM);
        message.string1 = String.format(format, args);
        return message;
    }
}
