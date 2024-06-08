package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.quest.QuestResultType;

public final class QuestPacket {
    // CUserLocal::OnQuestResult ---------------------------------------------------------------------------------------

    public static OutPacket startQuestTimer(int questId, int time) {
        final OutPacket outPacket = QuestPacket.of(QuestResultType.Start_QuestTimer);
        outPacket.encodeShort(1);
        outPacket.encodeShort(questId);
        outPacket.encodeInt(time);
        return outPacket;
    }

    public static OutPacket endQuestTimer(int questId) {
        final OutPacket outPacket = QuestPacket.of(QuestResultType.End_QuestTimer);
        outPacket.encodeShort(1);
        outPacket.encodeShort(questId);
        return outPacket;
    }

    public static OutPacket startTimeKeepQuestTimer(int questId, int time) {
        final OutPacket outPacket = QuestPacket.of(QuestResultType.Start_TimeKeepQuestTimer);
        outPacket.encodeShort(questId);
        outPacket.encodeInt(time);
        return outPacket;
    }

    public static OutPacket endTimeKeepQuestTimer(int questId, int time) {
        final OutPacket outPacket = QuestPacket.of(QuestResultType.End_TimeKeepQuestTimer);
        outPacket.encodeShort(questId);
        return outPacket;
    }

    public static OutPacket success(int questId, int templateId, int nextQuestId) {
        final OutPacket outPacket = QuestPacket.of(QuestResultType.Success);
        outPacket.encodeShort(questId);
        outPacket.encodeInt(templateId);
        outPacket.encodeShort(nextQuestId);
        return outPacket;
    }

    public static OutPacket failedUnknown() {
        return QuestPacket.of(QuestResultType.Failed_Unknown);
    }

    public static OutPacket failedInventory(int questId) {
        final OutPacket outPacket = QuestPacket.of(QuestResultType.Failed_Inventory);
        outPacket.encodeShort(questId);
        return outPacket;
    }

    private static OutPacket of(QuestResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserQuestResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
