package kinoko.world.quest;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class QuestResult implements Encodable {
    private final QuestResultType questResultType;
    private int questId;
    private int nextQuestId;
    private int templateId;
    private int time;

    public QuestResult(QuestResultType questResultType) {
        this.questResultType = questResultType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(questResultType.getValue());
        switch (questResultType) {
            case START_QUEST_TIMER -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case END_QUEST_TIMER -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
            }
            case START_TIME_KEEP_QUEST_TIMER -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case END_TIME_KEEP_QUEST_TIMER, FAILED_INVENTORY, FAILED_TIME_OVER, RESET_QUEST_TIMER -> {
                outPacket.encodeShort(questId);
            }
            case SUCCESS -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(templateId);
                outPacket.encodeShort(nextQuestId);
            }
            case FAILED_MESO, FAILED_PET, FAILED_EQUIPPED, FAILED_ONLY_ITEM -> {
            }
        }
    }

    public static QuestResult of(QuestResultType type) {
        return new QuestResult(type);
    }

    public static QuestResult success(int questId, int templateId, int nextQuestId) {
        final QuestResult questResult = new QuestResult(QuestResultType.SUCCESS);
        questResult.questId = questId;
        questResult.templateId = templateId;
        questResult.nextQuestId = nextQuestId;
        return questResult;
    }

    public static QuestResult failedInventory(int questId) {
        final QuestResult questResult = new QuestResult(QuestResultType.FAILED_INVENTORY);
        questResult.questId = questId;
        return questResult;
    }
}
