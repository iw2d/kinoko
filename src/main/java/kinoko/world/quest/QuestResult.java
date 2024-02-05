package kinoko.world.quest;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

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
            case END_TIME_KEEP_QUEST_TIMER, FAILED_INVENTORY -> {
                outPacket.encodeShort(questId);
            }
            case SUCCESS -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(templateId);
                outPacket.encodeShort(nextQuestId);
            }
        }
    }
}
