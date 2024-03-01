package kinoko.world.field;

import kinoko.provider.map.Foothold;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.map.LifeType;
import kinoko.util.Util;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.mob.MobAppearType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class MobPool extends FieldObjectPool<Mob> {
    private final Map<Mob, Instant> graveyard = new HashMap<>(); // mob -> removed time

    public MobPool(Field field) {
        super(field);
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return objects.isEmpty() && graveyard.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public void addMob(Mob mob) {
        lock.lock();
        try {
            addMobUnsafe(mob);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeMob(Mob mob) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(mob)) {
                return false;
            }
            field.broadcastPacket(mob.leaveFieldPacket());
            if (mob.isRespawn()) {
                graveyard.put(mob, Instant.now());
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void respawnMobs() {
        lock.lock();
        try {
            final var iter = graveyard.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<Mob, Instant> entry = iter.next();
                try (var lockedMob = entry.getKey().acquire()) {
                    final Mob mob = lockedMob.get();
                    // Check respawn timer and remove from graveyard
                    if (Instant.now().isBefore(entry.getValue().plus(mob.getMobTime(), ChronoUnit.SECONDS))) {
                        continue;
                    }
                    iter.remove();
                    // Randomize spawn point
                    final Set<LifeInfo> possibleSpawnPoints = field.getMapInfo().getLifeInfos().stream()
                            .filter((li) -> li.getLifeType() == LifeType.MOB && li.getTemplateId() == mob.getTemplateId())
                            .collect(Collectors.toUnmodifiableSet());
                    final Optional<LifeInfo> spResult = Util.getRandomFromCollection(possibleSpawnPoints);
                    if (spResult.isEmpty()) {
                        throw new IllegalStateException(String.format("Failed to assign spawn point for mob ID : %d in field ID : %d", mob.getTemplateId(), field.getFieldId()));
                    }
                    final Optional<Foothold> footholdResult = field.getFootholdById(spResult.get().getFh());
                    if (footholdResult.isEmpty()) {
                        throw new IllegalStateException(String.format("Failed to assign spawn point for mob ID : %d in field ID : %d, foothold ID : %d", mob.getTemplateId(), field.getFieldId(), spResult.get().getFh()));
                    }
                    // Randomize position on foothold
                    final Foothold fh = footholdResult.get();
                    final int newX = Util.getRandom(fh.getX1(), fh.getX2());
                    final int newY = fh.getYFromX(newX);
                    // Reset and spawn mob, set appear type to REGEN while respawning
                    mob.reset(newX, newY, fh.getFootholdId());
                    mob.setAppearType(MobAppearType.REGEN);
                    addMobUnsafe(mob);
                    mob.setAppearType(MobAppearType.NORMAL);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void addMobUnsafe(Mob mob) {
        mob.setField(field);
        mob.setId(field.getNewObjectId());
        addObjectUnsafe(mob);
        field.broadcastPacket(mob.enterFieldPacket());
        field.getUserPool().assignController(mob);
    }
}
