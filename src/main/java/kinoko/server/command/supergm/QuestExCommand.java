package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * Gets or sets a quest record (QR) value for a specified quest ID.
 * SuperGM-level command.
 */
public final class QuestExCommand {

    @Command({ "questex", "qr" })
    @Arguments("quest ID")
    public static void questex(User user, String[] args) {
        try {
            int questId = Integer.parseInt(args[1]);
            String newValue = args.length > 2 ? args[2] : null;

            if (newValue == null) {
                Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(questId);
                String value = questRecordResult.map(QuestRecord::getValue).orElse("");
                user.systemMessage("Get QR value for quest ID %d : %s", questId, value);
            } else {
                QuestRecord qr = user.getQuestManager().setQuestInfoEx(questId, newValue);
                user.write(MessagePacket.questRecord(qr));
                user.validateStat();
                user.systemMessage("Set QR value for quest ID %d : %s", questId, newValue);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !questex <quest ID> [value]");
        }
    }
}
