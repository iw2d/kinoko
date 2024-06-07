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
            case Start_QuestTimer -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case End_QuestTimer -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
            }
            case Start_TimeKeepQuestTimer -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case End_TimeKeepQuestTimer, Failed_Inventory, Failed_TimeOver, Reset_QuestTimer -> {
                outPacket.encodeShort(questId);
            }
            case Success -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(templateId);
                outPacket.encodeShort(nextQuestId);
            }
            case Failed_Meso, Failed_Pet, Failed_Euipped, Failed_OnlyItem -> {
            }
        }
    }

    public static QuestResult of(QuestResultType type) {
        return new QuestResult(type);
    }

    public static QuestResult success(int questId, int templateId, int nextQuestId) {
        final QuestResult questResult = new QuestResult(QuestResultType.Success);
        questResult.questId = questId;
        questResult.templateId = templateId;
        questResult.nextQuestId = nextQuestId;
        return questResult;
    }

    public static QuestResult failedInventory(int questId) {
        final QuestResult questResult = new QuestResult(QuestResultType.Failed_Inventory);
        questResult.questId = questId;
        return questResult;
    }
}
