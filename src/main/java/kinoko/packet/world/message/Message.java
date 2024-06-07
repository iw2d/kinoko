package kinoko.packet.world.message;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.quest.QuestRecord;

public class Message implements Encodable {
    protected final MessageType type;
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
            case QuestRecord -> {
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
            case CashItemExpire, IncPOP, IncMoney, IncGP, GiveBuff -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case IncSP -> {
                outPacket.encodeShort(int1); // nJob
                outPacket.encodeByte(int2); // nSP
            }
            case GeneralItemExpire, ItemProtectExpire, SkillExpire -> {
                outPacket.encodeByte(1); // count
                outPacket.encodeInt(int1); // nItemID / nSkillID
            }
            case System -> {
                outPacket.encodeString(string1); // sChat
            }
            case QuestRecordEx -> {
                outPacket.encodeShort(int1); // usQuestID
                outPacket.encodeString(string1); // rawStr
            }
            case ItemExpireReplace -> {
                outPacket.encodeByte(1); // count
                outPacket.encodeString(string1); // sChat
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported message type");
            }
        }
    }

    public static Message questRecord(QuestRecord questRecord) {
        final Message message = new Message(MessageType.QuestRecord);
        message.questRecord = questRecord;
        return message;
    }

    public static Message questRecordEx(int questId, String value) {
        final Message message = new Message(MessageType.QuestRecordEx);
        message.int1 = questId;
        message.string1 = value;
        return message;
    }

    public static Message incPop(int pop) {
        final Message message = new Message(MessageType.IncPOP);
        message.int1 = pop;
        return message;
    }

    public static Message incMoney(int money) {
        final Message message = new Message(MessageType.IncMoney);
        message.int1 = money;
        return message;
    }

    public static Message incSp(int job, int sp) {
        final Message message = new Message(MessageType.IncSP);
        message.int1 = job;
        message.int2 = sp;
        return message;
    }

    public static Message skillExpired(int skillId) {
        final Message message = new Message(MessageType.SkillExpire);
        message.int1 = skillId;
        return message;
    }

    public static Message system(String text) {
        final Message message = new Message(MessageType.System);
        message.string1 = text;
        return message;
    }

    public static Message system(String format, Object... args) {
        final Message message = new Message(MessageType.System);
        message.string1 = String.format(format, args);
        return message;
    }
}
