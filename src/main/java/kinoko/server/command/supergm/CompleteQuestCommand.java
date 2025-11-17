package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;

/**
 * Force-completes a quest for the user.
 * SuperGM-level command.
 */
public final class CompleteQuestCommand {

    @Command("completequest")
    @Arguments("quest ID")
    public static void completeQuest(User user, String[] args) {
        try {
            int questId = Integer.parseInt(args[1]);
            QuestRecord qr = user.getQuestManager().forceCompleteQuest(questId);
            user.write(MessagePacket.questRecord(qr));
            user.validateStat();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !completequest <quest ID>");
        }
    }
}
