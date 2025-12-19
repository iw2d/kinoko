package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.NpcProvider;
import kinoko.provider.npc.NpcTemplate;
import kinoko.script.common.ScriptDispatcher;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * SuperGM command to start NPC scripts.
 */
public final class NpcCommand {

    @Command("npc")
    @Arguments("npc template ID")
    public static void npc(User user, String[] args) {
        try {
            final int templateId = Integer.parseInt(args[1]);
            final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(templateId);

            if (npcTemplateResult.isEmpty()) {
                user.systemMessage("Could not resolve npc ID: %d", templateId);
                return;
            }

            final String scriptName = npcTemplateResult.get().getScript();
            if (scriptName == null || scriptName.isEmpty()) {
                user.systemMessage("Could not find script for npc ID: %d", templateId);
                return;
            }

            user.systemMessage("Starting script for npc ID: %d, script: %s", templateId, scriptName);
            ScriptDispatcher.startNpcScript(user, user, scriptName, templateId);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !npc <npc template ID>");
        }
    }
}
