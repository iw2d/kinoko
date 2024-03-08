package kinoko.world.field;

import kinoko.packet.field.MobPacket;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.map.LifeType;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.mob.MobTemporaryStat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class MobPool extends FieldObjectPool<Mob> {
    private final ConcurrentHashMap<Integer, Tuple<Mob, Instant>> graveyard = new ConcurrentHashMap<>(); // mob id -> tuple<mob, next respawn time>

    public MobPool(Field field) {
        super(field);
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty() && graveyard.isEmpty();
    }

    @Override
    public Optional<Mob> getById(int id) {
        if (graveyard.containsKey(id)) {
            return Optional.of(graveyard.get(id).getLeft());
        }
        return Optional.ofNullable(objects.get(id));
    }

    public void addMob(Mob mob) {
        mob.setField(field);
        mob.setId(field.getNewObjectId());
        addObject(mob);
        field.broadcastPacket(mob.enterFieldPacket());
        field.getUserPool().assignController(mob);
    }

    public boolean removeMob(Mob mob) {
        if (!removeObject(mob)) {
            return false;
        }
        field.broadcastPacket(mob.leaveFieldPacket());
        if (mob.isRespawn()) {
            graveyard.put(mob.getId(), new Tuple<>(mob, Instant.now().plus(mob.getMobTime(), ChronoUnit.SECONDS)));
        }
        return true;
    }

    public void updateMobs(Instant now) {
        for (Mob mob : getObjects()) {
            try (var lockedMob = mob.acquire()) {
                // TODO hp/mp regen
                // Expire temporary stat
                final Tuple<Set<MobTemporaryStat>, Set<BurnedInfo>> expireResult = lockedMob.get().getMobStat().expireMobStat(now);
                final Set<MobTemporaryStat> resetStats = expireResult.getLeft();
                final Set<BurnedInfo> resetBurnedInfos = expireResult.getRight();
                if (!resetStats.isEmpty()) {
                    field.broadcastPacket(MobPacket.statReset(mob, resetStats, resetBurnedInfos));
                }
            }
        }
    }

    public void respawnMobs(Instant now) {
        final var iter = graveyard.values().iterator();
        while (iter.hasNext()) {
            final Tuple<Mob, Instant> tuple = iter.next();
            try (var lockedMob = tuple.getLeft().acquire()) {
                final Mob mob = lockedMob.get();
                // Check respawn timer and remove from graveyard
                if (now.isBefore(tuple.getRight())) {
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
                addMob(mob);
                mob.setAppearType(MobAppearType.NORMAL);
            }
        }
    }
}
