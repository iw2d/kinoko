package kinoko.world.field.mob;

import kinoko.packet.field.MobPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.QuestProvider;
import kinoko.provider.RewardProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobSkill;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.reward.Reward;
import kinoko.server.event.EventScheduler;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.ControlledObject;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.life.Life;
import kinoko.world.item.Item;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Mob extends Life implements ControlledObject, Encodable, Lockable<Mob> {
    private final Lock lock = new ReentrantLock();
    private final MobStat mobStat = new MobStat();
    private final AtomicInteger attackCounter = new AtomicInteger(0);
    private final Map<MobSkill, Instant> skillCooltimes = new HashMap<>();
    private final Map<Integer, Integer> damageDone = new HashMap<>();
    private final MobTemplate template;
    private final MobSpawnPoint spawnPoint;
    private final Instant removeAfter;
    private final int startFoothold;

    private MobAppearType appearType = MobAppearType.REGEN;
    private User controller;
    private int hp;
    private int mp;
    private Instant nextRecovery;

    public Mob(MobTemplate template, MobSpawnPoint spawnPoint, int x, int y, int fh) {
        this.template = template;
        this.spawnPoint = spawnPoint;
        this.removeAfter = template.getRemoveAfter() > 0 ? Instant.now().plus(template.getRemoveAfter(), ChronoUnit.SECONDS) : Instant.MAX;
        this.startFoothold = fh;
        // Life initialization
        setX(x);
        setY(y);
        setFoothold(fh);
        setMoveAction(5); // idk
        // Mob initialization
        setHp(template.getMaxHp());
        setMp(template.getMaxMp());
        nextRecovery = Instant.now();
    }

    public int getTemplateId() {
        return this.template.getId();
    }

    public int getLevel() {
        return template.getLevel();
    }

    public int getMaxHp() {
        return template.getMaxHp();
    }

    public int getMaxMp() {
        return template.getMaxMp();
    }

    public int getHpRecovery() {
        return template.getHpRecovery();
    }

    public int getMpRecovery() {
        return template.getMpRecovery();
    }

    public int getFixedDamage() {
        return template.getFixedDamage();
    }

    public boolean isBoss() {
        return template.isBoss();
    }

    public boolean isDamagedByMob() {
        return template.isDamagedByMob();
    }

    public Optional<MobAttack> getAttack(int attackIndex) {
        return template.getAttack(attackIndex);
    }

    public Optional<MobSkill> getSkill(int skillIndex) {
        return template.getSkill(skillIndex);
    }

    public MobStat getMobStat() {
        return mobStat;
    }

    public boolean isSkillAvailable(MobSkill mobSkill) {
        return skillCooltimes.getOrDefault(mobSkill, Instant.MIN).isBefore(Instant.now());
    }

    public void setSkillOnCooltime(MobSkill mobSkill, Instant nextAvailableTime) {
        skillCooltimes.put(mobSkill, nextAvailableTime);
    }

    public int getAndDecrementAttackCounter() {
        return attackCounter.getAndDecrement();
    }

    public void setAttackCounter(int value) {
        attackCounter.set(value);
    }

    public MobAppearType getAppearType() {
        return appearType;
    }

    public void setAppearType(MobAppearType appearType) {
        this.appearType = appearType;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void heal(int hp) {
        setHp(Math.min(getHp() + hp, getMaxHp()));
        getField().broadcastPacket(MobPacket.mobDamaged(this, -hp));
    }

    public void recovery(Instant now) {
        if (getHp() < 0 || now.isBefore(nextRecovery)) {
            return;
        }
        if (getHpRecovery() > 0) {
            final int newHp = getHp() + getHpRecovery();
            setHp(Math.min(newHp, getMaxHp()));
        }
        if (getMpRecovery() > 0) {
            final int newMp = getMp() + getMpRecovery();
            setMp(Math.min(newMp, getMaxMp()));
        }
        nextRecovery = now.plus(GameConstants.MOB_RECOVER_TIME, ChronoUnit.SECONDS);
    }

    public void remove(Instant now) {
        if (template.getRemoveAfter() <= 0 && now.isBefore(removeAfter)) {
            return;
        }
        if (getField().getMobPool().removeMob(this)) {
            spawnRevives();
        }
        if (spawnPoint != null) {
            spawnPoint.setNextMobRespawn();
        }
    }

    public void setTemporaryStat(MobTemporaryStat mts, MobStatOption option) {
        setTemporaryStat(Map.of(mts, option));
    }

    public void setTemporaryStat(Map<MobTemporaryStat, MobStatOption> setStats) {
        for (var entry : setStats.entrySet()) {
            getMobStat().getTemporaryStats().put(entry.getKey(), entry.getValue());
        }
        getField().broadcastPacket(MobPacket.mobStatSet(this, setStats, Set.of()));
    }

    public void setBurnedInfo(BurnedInfo burnedInfo) {
        final MobStatOption option = MobStatOption.of(0, burnedInfo.getSkillId(), 0);
        getMobStat().getTemporaryStats().put(MobTemporaryStat.Burned, option);
        getMobStat().addBurnedInfo(burnedInfo);
        getField().broadcastPacket(MobPacket.mobStatSet(this, Map.of(MobTemporaryStat.Burned, option), Set.of(burnedInfo)));
        // TODO: damage task
    }

    public void damage(User attacker, int totalDamage) {
        // Process damage
        final int actualDamage = Math.min(getHp(), totalDamage);
        setHp(getHp() - actualDamage);
        damageDone.put(attacker.getCharacterId(), damageDone.getOrDefault(attacker.getCharacterId(), 0) + actualDamage);
        // Show mob hp indicator
        final double percentage = (double) getHp() / getMaxHp();
        attacker.write(MobPacket.mobHpIndicator(this, (int) (percentage * 100)));
        // Handle death
        if (getHp() <= 0) {
            if (getField().getMobPool().removeMob(this)) {
                distributeExp();
                dropRewards(attacker);
                spawnRevives();
            }
            if (spawnPoint != null) {
                spawnPoint.setNextMobRespawn();
            }
        }
    }

    private void distributeExp() {
        final int totalExp = template.getExp();
        final int topAttackerId = damageDone.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
        for (var entry : damageDone.entrySet()) {
            final int attackerDamage = entry.getValue();
            final Optional<User> attackerResult = getField().getUserPool().getById(entry.getKey());
            if (attackerResult.isEmpty()) {
                continue;
            }
            // Schedule event as user lock is required
            EventScheduler.addEvent(() -> {
                try (var locked = attackerResult.get().acquire()) {
                    // Distribute exp
                    final User attacker = locked.get();
                    final int splitExp = (int) (((double) attackerDamage / getMaxHp()) * totalExp);
                    attacker.addExp(splitExp);
                    attacker.write(MessagePacket.incExp(splitExp, 0, attacker.getCharacterId() == topAttackerId, false));
                    // Process mob kill for quest
                    for (QuestRecord qr : attacker.getQuestManager().getStartedQuests()) {
                        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(qr.getQuestId());
                        if (questInfoResult.isEmpty()) {
                            continue;
                        }
                        final Optional<QuestRecord> questProgressResult = questInfoResult.get().progressQuest(qr, getTemplateId());
                        if (questProgressResult.isEmpty()) {
                            continue;
                        }
                        attacker.write(MessagePacket.questRecord(questProgressResult.get()));
                        attacker.validateStat();
                    }
                }
            }, 0);
        }
    }

    private void dropRewards(User lastAttacker) {
        // Sort damageDone by highest damage, assign owner to most damage attacker present in the field
        User owner = lastAttacker;
        final var iter = damageDone.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .iterator();
        while (iter.hasNext()) {
            final Optional<User> userResult = getField().getUserPool().getById(iter.next().getKey());
            if (userResult.isPresent()) {
                owner = userResult.get();
                break;
            }
        }
        // Create drops from possible rewards
        final Set<Drop> drops = new HashSet<>();
        final Set<Reward> possibleRewards = Stream.concat(RewardProvider.getMobRewards(this).stream(), Stream.of(RewardProvider.getMobMoneyReward(this)))
                .collect(Collectors.toUnmodifiableSet());
        for (Reward reward : possibleRewards) {
            // Quest drops
            if (reward.isQuest()) {
                if (!owner.getQuestManager().hasQuestStarted(reward.getQuestId())) {
                    continue;
                }
            }
            // Drop probability
            if (Util.getRandom().nextDouble() > reward.getProb()) {
                continue;
            }
            // Create drop
            if (reward.isMoney()) {
                final int money = Util.getRandom(reward.getMin(), reward.getMax());
                if (money <= 0) {
                    continue;
                }
                drops.add(Drop.money(DropOwnType.USEROWN, this, money, owner.getCharacterId()));
            } else {
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
                if (itemInfoResult.isEmpty()) {
                    continue;
                }
                final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
                final Item item = itemInfoResult.get().createItem(owner.getNextItemSn(), quantity);
                drops.add(Drop.item(DropOwnType.USEROWN, this, item, owner.getCharacterId(), reward.getQuestId()));
            }
        }
        // Add drops to field
        getField().getDropPool().addDrops(drops, DropEnterType.CREATE, getX(), getY() - GameConstants.DROP_HEIGHT);
    }


    private void spawnRevives() {
        if (template.getRevives().isEmpty()) {
            return;
        }
        EventScheduler.addEvent(() -> {
            for (int reviveId : template.getRevives()) {
                final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(reviveId);
                if (mobTemplateResult.isEmpty()) {
                    // Should not happen
                    continue;
                }
                final Mob reviveMob = new Mob(
                        mobTemplateResult.get(),
                        null,
                        getX(),
                        getY(),
                        getFoothold()
                );
                reviveMob.setAppearType(MobAppearType.REVIVED);
                getField().getMobPool().addMob(reviveMob);
                reviveMob.setAppearType(MobAppearType.NORMAL);
            }
        }, template.getReviveDelay());
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public boolean isLeft() {
        if (template.isNoFlip()) {
            return true;
        }
        return super.isLeft();
    }

    @Override
    public User getController() {
        return controller;
    }

    @Override
    public void setController(User controller) {
        this.controller = controller;
    }

    @Override
    public OutPacket changeControllerPacket(boolean forController) {
        return MobPacket.mobChangeController(this, forController);
    }

    @Override
    public String toString() {
        return String.format("Mob { %d, oid : %d, hp : %d, mp : %d, boss : %b }", getTemplateId(), getId(), getHp(), getMp(), isBoss());
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CMob::Init
        outPacket.encodeShort(getX()); // ptPosPrev.x
        outPacket.encodeShort(getY()); // ptPosPrev.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // pvcMobActiveObj (current foothold)
        outPacket.encodeShort(startFoothold); // Foothold (start foothold)
        outPacket.encodeByte(appearType.getValue()); // nAppearType
        if (appearType == MobAppearType.REVIVED || appearType.getValue() >= 0) {
            outPacket.encodeInt(0); // dwOption
        }
        outPacket.encodeByte(0); // nTeamForMCarnival
        outPacket.encodeInt(0); // nEffectItemID
        outPacket.encodeInt(0); // nPhase
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
