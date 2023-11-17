package kinoko.packet.script;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

public final class QuestResult implements Encodable {
    private final QuestResultType resultType;
    private int questId;
    private int nextQuestId;
    private int templateId;
    private int time;

    public QuestResult(QuestResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case ADD_QUEST_TIMER -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case REMOVE_QUEST_TIMER -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
            }
            case ADD_TIME_KEEP_QUEST_TIMER -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case REMOVE_TIME_KEEP_QUEST_TIMER, QUEST_FAILED_ITEM -> {
                outPacket.encodeShort(questId);
            }
            case QUEST_SUCCESS -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(templateId);
                outPacket.encodeShort(nextQuestId);
            }
        }
    }
}
