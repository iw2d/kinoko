package kinoko.world.field;

import kinoko.packet.field.FieldEffectPacket;
import kinoko.packet.field.MobPacket;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.map.LifeType;
import kinoko.script.party.KerningPQ;
import kinoko.server.node.ServerExecutor;
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

    public Optional<Mob> getByTemplateId(int templateId) {
        return getBy((mob) -> mob.getTemplateId() == templateId);
    }

    public void addMob(Mob mob) {
        mob.setField(field);
        mob.setId(field.getNewObjectId());
        addObject(mob);
        field.broadcastPacket(MobPacket.mobEnterField(mob));
        if (mob.getSummonType() != MobAppearType.SUSPENDED.getValue()) {
            mob.setSummonType(MobAppearType.NORMAL.getValue());
        }
        field.getUserPool().assignController(mob);
    }

    public synchronized boolean removeMob(Mob mob, MobLeaveType leaveType) {
        if (!removeObject(mob)) {
            return false;
        }
        // Send MobLeaveField after processing attack
        ServerExecutor.submit(field, () -> {
            field.broadcastPacket(MobPacket.mobLeaveField(mob, leaveType));
        });
        // Special handling for fields
        switch (field.getFieldId()) {
            case KerningPQ.STAGE_4 -> {
                // Hidden Street : First Time Together <4th Stage>
                field.broadcastPacket(FieldEffectPacket.screen("quest/party/clear"));
                field.broadcastPacket(FieldEffectPacket.sound("Party1/Clear"));
                field.blowWeather(5120017, "All of the Curse Eyes have been defeated. The Party Leader should come talk to me.", 20);
            }
            case KerningPQ.STAGE_5 -> {
                // Hidden Street : First Time Together <Last Stage>
                field.broadcastPacket(FieldEffectPacket.screen("quest/party/clear"));
                field.broadcastPacket(FieldEffectPacket.sound("Party1/Clear"));
            }
        }
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
        final int boundWidth = Math.max(rootBounds.getWidth(), 800);
        final int boundHeight = Math.max(rootBounds.getHeight() - 450, 600);
        final int mobCapacity = (int) ((double) (boundWidth * boundHeight) * field.getMapInfo().getMobRate() * GameConstants.MOB_CAPACITY_CONSTANT);
        return Math.clamp(mobCapacity, 1, GameConstants.MOB_CAPACITY_MAX);
    }
}
