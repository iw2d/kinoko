package kinoko.world.quest;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class QuestResult implements Encodable {
    private final QuestAction questAction;
    private int questId;
    private int nextQuestId;
    private int templateId;
    private int time;

    public QuestResult(QuestAction questAction) {
        this.questAction = questAction;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(questAction.getValue());
        switch (questAction) {
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
