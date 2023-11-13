package kinoko.packet.world.message;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.quest.QuestRecord;

public final class Message implements Encodable {
    private final MessageType type;
    private int int1;
    private int int2;
    private String string1;
    private QuestRecord questRecord;
    private DropPickUpMessageInfo dropPickUpMessageInfo;
    private IncExpMessageInfo incExpMessageInfo;

    public Message(MessageType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case DROP_PICK_UP -> {
                dropPickUpMessageInfo.encode(outPacket);
            }
            case QUEST_RECORD -> {
                outPacket.encodeShort(questRecord.getQuestId()); // nQuestID
                outPacket.encodeByte(questRecord.getQuestStatus().getValue());
                switch (questRecord.getQuestStatus()) {
                    case STARTED -> {
                        outPacket.encodeString(questRecord.getQuestInfo());
                    }
                    case COMPLETED -> {
                        outPacket.encodeFT(questRecord.getCompletedTime());
                    }
                    default -> {
                        outPacket.encodeByte(true); // delete quest
                    }
                }
            }
            case CASH_ITEM_EXPIRE, INC_POP, INC_MONEY, INC_GP, GIVE_BUFF -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case INC_EXP -> {
                incExpMessageInfo.encode(outPacket);
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
        }
    }
}
