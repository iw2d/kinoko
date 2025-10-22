package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * Force-starts a quest for the user.
 * SuperGM-level command.
 */
public final class StartQuestCommand {

    @Command("startquest")
    @Arguments("quest ID")
    public static void startQuest(User user, String[] args) {
        try {
            int questId = Integer.parseInt(args[1]);
            Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
            if (questInfoResult.isEmpty()) {
                user.write(MessagePacket.system("Could not find quest : %d", questId));
                return;
            }

            QuestRecord qr = user.getQuestManager().forceStartQuest(questId);
            user.write(MessagePacket.questRecord(qr));
            user.validateStat();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !startquest <quest ID>"));
        }
    }
}
