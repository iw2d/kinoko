package kinoko.world.field.mob;

import kinoko.provider.MobProvider;
import kinoko.provider.mob.MobTemplate;
import kinoko.util.Rect;
import kinoko.world.field.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class MobSpawnPoint {
    private final Field field;
    private final int templateId;
    private final int x;
    private final int y;
    private final int fh;
    private final int mobTime;
    private final Rect checkArea;

    private Instant nextMobRespawn = Instant.now();

    public MobSpawnPoint(Field field, int templateId, int x, int y, int fh, int mobTime) {
        this.field = field;
        this.templateId = templateId;
        this.x = x;
        this.y = y;
        this.fh = fh;
        this.mobTime = mobTime;
        this.checkArea = Rect.of(x - 100, y - 100, x + 100, y + 100);
    }

    public Field getField() {
        return field;
    }

    public int getTemplateId() {
        return templateId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFh() {
        return fh;
    }

    public int getMobTime() {
        return mobTime;
    }

    public void setNextMobRespawn() {
        // Spawns with mobTime > 0 can only spawn after death
        if (mobTime > 0) {
            nextMobRespawn = Instant.now().plus(mobTime, ChronoUnit.SECONDS);
        }
    }

    public Optional<Mob> trySpawnMob(Instant now) {
        // Check respawn time
        if (now.isBefore(nextMobRespawn)) {
            return Optional.empty();
        }
        // Check if there are mobs inside spawn area
        if (!field.getMobPool().getInsideRect(checkArea).isEmpty()) {
            return Optional.empty();
        }
        // Create mob
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(getTemplateId());
        if (mobTemplateResult.isEmpty()) {
            return Optional.empty();
        }
        final Mob mob = new Mob(mobTemplateResult.get(), this, x, y, fh);
        // Spawns with mobTime > 0 can only spawn after death, spawns with mobTime < 0 should not respawn
        if (mobTime != 0) {
            nextMobRespawn = Instant.MAX;
        }
        return Optional.of(mob);
    }
}
