package kinoko.world.field;

import kinoko.packet.field.MobPacket;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.map.LifeType;
import kinoko.util.BitFlag;
import kinoko.util.Rect;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.*;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public final class MobPool extends FieldObjectPool<Mob> {
    private final List<MobSpawnPoint> mobSpawnPoints;
    private final int mobCapacityMin;
    private final int mobCapacityMax;

    public MobPool(Field field) {
        super(field);
        this.mobSpawnPoints = initializeMobSpawnPoints(field);
        this.mobCapacityMin = initializeMobCapacity(field);
        this.mobCapacityMax = mobCapacityMin * 2;
    }

    public void addMob(Mob mob) {
        mob.setField(field);
        mob.setId(field.getNewObjectId());
        addObject(mob);
        field.broadcastPacket(MobPacket.mobEnterField(mob));
        field.getUserPool().assignController(mob);
    }

    public synchronized boolean removeMob(Mob mob) {
        if (!removeObject(mob)) {
            return false;
        }
        field.broadcastPacket(MobPacket.mobLeaveField(mob));
        return true;
    }

    public void updateMobs(Instant now) {
        for (Mob mob : getObjects()) {
            try (var lockedMob = mob.acquire()) {
                // Handle burn
                final Set<BurnedInfo> resetBurnedInfos = new HashSet<>();
                final var iter = mob.getMobStat().getBurnedInfos().values().iterator();
                while (iter.hasNext()) {
                    final BurnedInfo burnedInfo = iter.next();
                    if (now.isBefore(burnedInfo.getNextUpdate())) {
                        continue;
                    }
                    mob.burn(burnedInfo.getCharacterId(), burnedInfo.getDamage());
                    if (burnedInfo.getDotCount() > 1) {
                        burnedInfo.setDotCount(burnedInfo.getDotCount() - 1);
                        burnedInfo.setLastUpdate(now);
                    } else {
                        iter.remove();
                        resetBurnedInfos.add(burnedInfo);
                    }
                }
                // Expire temporary stat
                final Set<MobTemporaryStat> resetStats = mob.getMobStat().expireTemporaryStat(now);
                if (!resetBurnedInfos.isEmpty() && mob.getMobStat().getBurnedInfos().isEmpty()) {
                    mob.getMobStat().getTemporaryStats().remove(MobTemporaryStat.Burned);
                    resetStats.add(MobTemporaryStat.Burned);
                }
                final BitFlag<MobTemporaryStat> flag = BitFlag.from(resetStats, MobTemporaryStat.FLAG_SIZE);
                if (!flag.isEmpty()) {
                    field.broadcastPacket(MobPacket.mobStatReset(mob, flag, resetBurnedInfos));
                }
                // Try recovering hp/mp
                mob.recovery(now);
                // Try removing mob (removeAfter)
                mob.remove(now);
                // Try dropping item (dropItemPeriod)
                mob.dropItem(now);
            }
        }
    }

    public void respawnMobs(Instant now) {
        // Shuffle spawn points
        final List<MobSpawnPoint> shuffledSpawnPoints = new ArrayList<>(mobSpawnPoints);
        Collections.shuffle(shuffledSpawnPoints);

        final int userCount = field.getUserPool().getCount();
        for (MobSpawnPoint msp : shuffledSpawnPoints) {
            // Check mob capacity
            if (getCount() >= getMobCapacity(userCount)) {
                break;
            }
            // Try spawn mob
            final Optional<Mob> spawnMobResult = msp.trySpawnMob(now);
            if (spawnMobResult.isEmpty()) {
                continue;
            }
            final Mob mob = spawnMobResult.get();
            // Apply modifiers
            for (Consumer<Mob> consumer : field.getMobSpawnModifiers().values()) {
                consumer.accept(mob);
            }
            // Add mob to pool
            addMob(mob);
            mob.setAppearType(MobAppearType.NORMAL);
        }
    }

    private int getMobCapacity(int userCount) {
        if (userCount > mobCapacityMin / 2) {
            if (userCount < mobCapacityMax) {
                return mobCapacityMin + (((mobCapacityMax - mobCapacityMin) * (2 * userCount - mobCapacityMin)) / (3 * mobCapacityMin));
            } else {
                return mobCapacityMax;
            }
        } else {
            return mobCapacityMin;
        }
    }

    private static List<MobSpawnPoint> initializeMobSpawnPoints(Field field) {
        final List<MobSpawnPoint> spawnPoints = new ArrayList<>();
        for (LifeInfo lifeInfo : field.getMapInfo().getLifeInfos()) {
            if (lifeInfo.getLifeType() != LifeType.MOB) {
                continue;
            }
            spawnPoints.add(new MobSpawnPoint(
                    field,
                    lifeInfo.getTemplateId(),
                    lifeInfo.getX(),
                    lifeInfo.getY(),
                    lifeInfo.getFh(),
                    lifeInfo.getMobTime()
            ));
        }
        return Collections.unmodifiableList(spawnPoints);
    }

    private static int initializeMobCapacity(Field field) {
        final Rect rootBounds = field.getMapInfo().getRootBounds();
        final int mobCapacity = (int) ((double) (rootBounds.getHeight() * rootBounds.getWidth()) * field.getMapInfo().getMobRate() * GameConstants.MOB_CAPACITY_CONSTANT);
        return Math.clamp(mobCapacity, 1, GameConstants.MOB_CAPACITY_MAX);
    }
}
