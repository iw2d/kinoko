package kinoko.server.script;

import kinoko.packet.field.FieldEffectPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.provider.MobProvider;
import kinoko.provider.map.Foothold;
import kinoko.provider.mob.MobTemplate;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;

import java.util.Optional;

public final class ReactorScriptManager extends ScriptManager {
    private final Reactor reactor;

    public ReactorScriptManager(User user, Reactor reactor) {
        super(user);
        this.reactor = reactor;
    }

    @Override
    public void disposeManager() {
        ScriptDispatcher.removeScriptManager(ScriptType.REACTOR, user);
    }

    public Reactor getReactor() {
        return reactor;
    }

    public Field getField() {
        return reactor.getField();
    }

    public void dropRewards() {
        reactor.dropRewards(user);
    }

    public void changeBgm(String uol) {
        getField().broadcastPacket(FieldEffectPacket.changeBgm(uol));
    }

    public void messageAll(String message) {
        getField().broadcastPacket(MessagePacket.system(message));
    }

    public void spawnMob(int templateId, int appearType, int x, int y) {
        final MobAppearType mobAppearType = MobAppearType.getByValue(appearType);
        if (mobAppearType == null) {
            log.error("Unknown mob appear type received for spawnMob : {}", appearType);
        }
        spawnMob(templateId, mobAppearType != null ? mobAppearType : MobAppearType.REGEN, x, y);
    }

    private void spawnMob(int templateId, MobAppearType appearType, int x, int y) {
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
        if (mobTemplateResult.isEmpty()) {
            log.error("Could not resolve mob template ID : {}", templateId);
            return;
        }
        final Optional<Foothold> footholdResult = getField().getFootholdBelow(x, y - GameConstants.REACTOR_SPAWN_HEIGHT);
        final Mob mob = new Mob(
                mobTemplateResult.get(),
                null,
                x,
                y,
                footholdResult.map(Foothold::getFootholdId).orElse(0)
        );
        mob.setAppearType(appearType);
        getField().getMobPool().addMob(mob);
    }
}
