package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * Clears a quest (sets its state to NONE) for the user.
 * SuperGM-level command.
 */
public final class ClearQuestCommand {

    @Command("clearquest")
    @Arguments("quest ID")
    public static void clearQuest(User user, String[] args) {
        try {
            int questId = Integer.parseInt(args[1]);
            Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(questId);

            if (questRecordResult.isEmpty()) {
                user.systemMessage("Could not find quest record : %d", questId);
                return;
            }

            QuestRecord qr = questRecordResult.get();
            qr.setState(QuestState.NONE);
            user.write(MessagePacket.questRecord(qr));
            user.validateStat();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !clearquest <quest ID>");
        }
    }
}
