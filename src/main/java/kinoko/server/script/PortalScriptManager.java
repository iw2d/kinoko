package kinoko.server.script;

import kinoko.provider.NpcProvider;
import kinoko.provider.npc.NpcTemplate;
import kinoko.world.user.User;

import java.util.Optional;

public final class PortalScriptManager extends ScriptManager {
    public PortalScriptManager(User user) {
        super(user);
    }

    @Override
    public void disposeManager() {
        ScriptDispatcher.removeScriptManager(ScriptType.PORTAL, user);
    }

    public void openNpc(int templateId) {
        final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(templateId);
        if (npcTemplateResult.isEmpty()) {
            log.error("Could not resolve npc ID : {}", templateId);
            return;
        }
        final String scriptName = npcTemplateResult.get().getScript();
        if (scriptName == null || scriptName.isEmpty()) {
            log.error("Could not find script for npc ID : {}", templateId);
            return;
        }
        ScriptDispatcher.startNpcScript(user, templateId, scriptName);
    }
}
