package kinoko.world.field.mob;

import kinoko.packet.field.MobPacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.QuestProvider;
import kinoko.provider.RewardProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.mob.DamagedAttribute;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobSkill;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.reward.Reward;
import kinoko.provider.skill.ElementAttribute;
import kinoko.provider.skill.SkillStat;
import kinoko.script.event.MoonBunny;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
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
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;

public final class Mob extends Life implements ControlledObject, Encodable, Lockable<Mob> {
    private final Lock lock = new ReentrantLock();
    private final MobStat mobStat = new MobStat();
    private final AtomicInteger attackCounter = new AtomicInteger(0);
    private final Map<MobSkill, Instant> skillCooltimes = new HashMap<>();
    private final Map<Integer, Integer> damageDone = new HashMap<>();
    private final MobTemplate template;
    private final MobSpawnPoint spawnPoint;
    private final int startFoothold;

    private MobAppearType appearType = MobAppearType.REGEN;
    private MobLeaveType leaveType = MobLeaveType.ETC;
    private User controller;
    private int hp;
    private int mp;
    private int itemDropCount;
    private boolean slowUsed;
    private int swallowCharacterId;
    private Reward stolenReward;
    private Instant nextRecovery;
    private Instant removeAfter;
    private Instant nextDropItem;

    public Mob(MobTemplate template, MobSpawnPoint spawnPoint, int x, int y, int fh) {
        this.template = template;
        this.spawnPoint = spawnPoint;
        this.startFoothold = fh;
        // Life initialization
        setX(x);
        setY(y);
        setFoothold(fh);
        setMoveAction(MobActionType.REGEN.getValue() << 1);
        // Mob initialization
        this.hp = template.getMaxHp();
        this.mp = template.getMaxMp();
        this.nextRecovery = Instant.now().plus(GameConstants.MOB_RECOVER_TIME, ChronoUnit.SECONDS);
        this.removeAfter = template.getRemoveAfter() > 0 ? Instant.now().plus(template.getRemoveAfter(), ChronoUnit.SECONDS) : Instant.MAX;
        this.nextDropItem = template.getDropItemPeriod() > 0 ? Instant.now().plus(template.getDropItemPeriod(), ChronoUnit.SECONDS) : Instant.MAX;
    }

    public MobTemplate getTemplate() {
        return template;
    }

    public int getTemplateId() {
        return template.getId();
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

    public Map<ElementAttribute, DamagedAttribute> getDamagedElemAttr() {
        return template.getDamagedElemAttr();
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

    public MobLeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(MobLeaveType leaveType) {
        this.leaveType = leaveType;
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

    public int getExp() {
        if (getMobStat().hasOption(MobTemporaryStat.Showdown)) {
            final double multiplier = (getMobStat().getOption(MobTemporaryStat.Showdown).nOption + 100) / 100.0;
            return (int) (template.getExp() * multiplier);
        }
        return template.getExp();
    }

    public boolean isSlowUsed() {
        return slowUsed;
    }

    public void setSlowUsed(boolean slowUsed) {
        this.slowUsed = slowUsed;
    }

    public int getSwallowCharacterId() {
        return swallowCharacterId;
    }

    public void setSwallowCharacterId(int swallowCharacterId) {
        this.swallowCharacterId = swallowCharacterId;
    }

    public Instant getRemoveAfter() {
        return removeAfter;
    }

    public void setRemoveAfter(Instant removeAfter) {
        this.removeAfter = removeAfter;
    }

    public void resetDropItemPeriod() {
        if (template.getDropItemPeriod() > 0) {
            nextDropItem = Instant.now().plus(template.getDropItemPeriod(), ChronoUnit.SECONDS);
        }
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
        if (template.getRemoveAfter() <= 0 || now.isBefore(removeAfter)) {
            return;
        }
        if (getField().getMobPool().removeMob(this)) {
            spawnRevives();
        }
        if (spawnPoint != null) {
            spawnPoint.setNextMobRespawn();
        }
    }

    public void dropItem(Instant now) {
        if (template.getDropItemPeriod() <= 0 || now.isBefore(nextDropItem)) {
            return;
        }
        final Optional<User> ownerResult = getField().getUserPool().getNearestUser(this);
        if (ownerResult.isEmpty()) {
            return;
        }
        dropRewards(ownerResult.get());
        itemDropCount++;
        nextDropItem = now.plus(template.getDropItemPeriod(), ChronoUnit.SECONDS);
        switch (template.getId()) {
            case MoonBunny.MOON_BUNNY -> {
                getField().broadcastPacket(BroadcastPacket.noticeWithoutPrefix(String.format("The Moon Bunny made rice cake number %d", itemDropCount)));
            }
        }
    }

    public void setBurnedInfo(BurnedInfo burnedInfo) {
        setTemporaryStat(Map.of(), burnedInfo);
    }

    public void setTemporaryStat(MobTemporaryStat mts, MobStatOption option) {
        setTemporaryStat(Map.of(mts, option));
    }

    public void setTemporaryStat(Map<MobTemporaryStat, MobStatOption> setStats) {
        for (var entry : setStats.entrySet()) {
            getMobStat().getTemporaryStats().put(entry.getKey(), entry.getValue());
        }
        final BitFlag<MobTemporaryStat> flag = BitFlag.from(setStats.keySet(), MobTemporaryStat.FLAG_SIZE);
        if (!flag.isEmpty()) {
            getField().broadcastPacket(MobPacket.mobStatSet(this, getMobStat(), flag));
        }
    }

    public void setTemporaryStat(Map<MobTemporaryStat, MobStatOption> setStats, BurnedInfo burnedInfo) {
        setStats = new HashMap<>(setStats);
        setStats.put(MobTemporaryStat.Burned, MobStatOption.of(1, burnedInfo.getSkillId(), 0));
        for (var entry : setStats.entrySet()) {
            getMobStat().getTemporaryStats().put(entry.getKey(), entry.getValue());
        }
        getMobStat().addBurnedInfo(burnedInfo);
        final BitFlag<MobTemporaryStat> flag = BitFlag.from(setStats.keySet(), MobTemporaryStat.FLAG_SIZE);
        if (!flag.isEmpty()) {
            getField().broadcastPacket(MobPacket.mobStatSet(this, getMobStat(), flag));
        }
    }

    public void resetTemporaryStat(int skillId) {
        resetTemporaryStat((mts, option) -> option.rOption == skillId);
    }

    public void resetTemporaryStat(Set<MobTemporaryStat> stats) {
        resetTemporaryStat((mts, option) -> stats.contains(mts));
    }

    public void resetTemporaryStat(BiPredicate<MobTemporaryStat, MobStatOption> predicate) {
        final Set<MobTemporaryStat> resetStats = getMobStat().resetTemporaryStat(predicate);
        final BitFlag<MobTemporaryStat> flag = BitFlag.from(resetStats, MobTemporaryStat.FLAG_SIZE);
        if (!flag.isEmpty()) {
            getField().broadcastPacket(MobPacket.mobStatReset(this, flag, Set.of()));
        }
    }

    public void burn(int attackerId, int burnDamage) {
        // Apply damage
        final int actualDamage = Math.min(getHp() - 1, burnDamage);
        setHp(getHp() - actualDamage);
        damageDone.put(attackerId, damageDone.getOrDefault(attackerId, 0) + actualDamage);
        // Show mob hp indicator
        final double percentage = (double) getHp() / getMaxHp();
        getField().broadcastPacket(MobPacket.mobHpIndicator(this, (int) (percentage * 100)));
    }

    public void damage(User attacker, int totalDamage) {
        // Apply damage
        final int actualDamage = Math.min(getHp(), totalDamage);
        setHp(getHp() - actualDamage);
        damageDone.put(attacker.getCharacterId(), damageDone.getOrDefault(attacker.getCharacterId(), 0) + actualDamage);
        // Show mob hp indicator
        final double percentage = (double) getHp() / getMaxHp();
        attacker.write(MobPacket.mobHpIndicator(this, (int) (percentage * 100)));
        // Handle death
        if (getHp() <= 0) {
            getField().broadcastPacket(changeControllerPacket(false));
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

    public void steal(User attacker) {
        if (stolenReward != null) {
            return;
        }
        final List<Reward> possibleRewards = RewardProvider.getMobRewards(getTemplateId());
        final Optional<Reward> stealResult = Util.getRandomFromCollection(possibleRewards, Reward::getProb);
        if (stealResult.isEmpty()) {
            return;
        }
        final Reward reward = stealResult.get();
        final Optional<Drop> dropResult = createDrop(attacker, reward);
        if (dropResult.isPresent()) {
            getField().getDropPool().addDrop(dropResult.get(), DropEnterType.CREATE, getX(), getY() - GameConstants.DROP_HEIGHT, 0);
            stolenReward = reward;
        }
    }


    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    /**
     * Distribute exp by damage dealt, share exp with party if:
     * <pre>
     * 1) dealt damage,
     * 2) within 5 levels of mob, or
     * 3) within 5 levels of a member satisfying either (1) or (2)
     *
     * Exp for highest damage : (0.6 + 0.4 * level / totalPartyLevel) + partyBonus
     * Exp for other members : (0.4 * level / totalPartyLevel) + partyBonus
     * </pre>
     */
    private void distributeExp() {
        // Calculate exp split based on damage dealt
        final int totalExp = getExp();
        final Map<User, Integer> expSplit = new HashMap<>(); // user -> exp
        final Map<Integer, Set<User>> partyMembers = new HashMap<>(); // party id -> members
        for (var entry : damageDone.entrySet()) {
            final int characterId = entry.getKey();
            final Optional<User> userResult = getField().getUserPool().getById(characterId);
            if (userResult.isEmpty()) {
                continue;
            }
            final User user = userResult.get();
            final int splitExp = (int) ((double) entry.getValue() / getMaxHp() * totalExp);
            expSplit.put(user, splitExp);
            // Gather party members satisfying (1) and (2)
            final int partyId = user.getPartyId();
            if (partyId != 0) {
                partyMembers.computeIfAbsent(partyId, (id) -> new HashSet<>()).add(user);
                getField().getUserPool().forEachPartyMember(user, (member) -> {
                    if (damageDone.containsKey(member.getCharacterId()) || member.getLevel() + 5 >= getLevel()) {
                        partyMembers.get(partyId).add(member);
                    }
                });
            }
        }
        // Gather party members satisfying (3)
        for (Set<User> members : partyMembers.values()) {
            final Set<User> leechers = new HashSet<>();
            for (User member : members) {
                getField().getUserPool().forEachPartyMember(member, (leecher) -> {
                    if (leecher.getLevel() + 5 >= member.getLevel()) {
                        leechers.add(leecher);
                    }
                });
            }
            members.addAll(leechers);
        }
        // Calculate final exp split
        final Map<User, Integer> finalExpSplit = new HashMap<>();
        for (var entry : expSplit.entrySet()) {
            final User user = entry.getKey();
            final int exp = entry.getValue();
            final Set<User> members = partyMembers.get(user.getPartyId());
            if (members != null) {
                final int totalPartyLevel = members.stream().mapToInt(User::getLevel).sum();
                for (User member : members) {
                    finalExpSplit.put(member, finalExpSplit.getOrDefault(member, 0) +
                            (int) (user == member ? 0.6 * exp : 0.0) +
                            (int) (0.4 * exp * member.getLevel() / totalPartyLevel)
                    );
                }
            } else {
                finalExpSplit.put(user, finalExpSplit.getOrDefault(user, 0) + exp);
            }
        }
        final User highestDamageDone = finalExpSplit.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();
        for (var entry : finalExpSplit.entrySet()) {
            final User user = entry.getKey();
            final int exp = entry.getValue();
            final int memberCount = partyMembers.getOrDefault(user.getPartyId(), Set.of()).size();
            final int partyBonus = GameConstants.getPartyBonusExp(exp, memberCount);
            ServerExecutor.submit(getField(), () -> {
                try (var locked = user.acquire()) {
                    // Distribute exp
                    if (locked.get().getField() != getField()) {
                        return;
                    }
                    int finalExp = exp;
                    int finalPartyBonus = partyBonus;
                    if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.HolySymbol)) {
                        final int bonus = GameConstants.getHolySymbolBonus(user.getSecondaryStat().getOption(CharacterTemporaryStat.HolySymbol).nOption, memberCount);
                        final double multiplier = (bonus + 100) / 100.0;
                        finalExp = (int) (finalExp * multiplier);
                        finalPartyBonus = (int) (finalPartyBonus * multiplier);
                    }
                    if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.ExpBuffRate)) {
                        final double multiplier = user.getSecondaryStat().getOption(CharacterTemporaryStat.ExpBuffRate).nOption / 100.0;
                        finalExp = (int) (finalExp * multiplier);
                        finalPartyBonus = (int) (finalPartyBonus * multiplier);
                    }
                    if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Dice)) {
                        final int expR = user.getSecondaryStat().getOption(CharacterTemporaryStat.Dice).getDiceInfo().getInfoArray()[17];
                        if (expR > 0) {
                            final double multiplier = (expR + 100) / 100.0;
                            finalExp = (int) (finalExp * multiplier);
                            finalPartyBonus = (int) (finalPartyBonus * multiplier);
                        }
                    }
                    if (finalExp + finalPartyBonus > 0) {
                        user.addExp(finalExp + finalPartyBonus);
                        user.write(MessagePacket.incExp(finalExp, finalPartyBonus, user == highestDamageDone, false));
                    }
                    // Process mob kill for quest
                    for (QuestRecord qr : user.getQuestManager().getStartedQuests()) {
                        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(qr.getQuestId());
                        if (questInfoResult.isEmpty()) {
                            continue;
                        }
                        final Optional<QuestRecord> questProgressResult = questInfoResult.get().progressQuest(qr, getTemplateId());
                        if (questProgressResult.isEmpty()) {
                            continue;
                        }
                        user.write(MessagePacket.questRecord(questProgressResult.get()));
                        user.validateStat();
                    }
                }
            });
        }
    }

    private void dropRewards(User lastAttacker) {
        // Sort damageDone by highest damage, assign owner to the highest damage attacker present in the field
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
        final List<Drop> drops = new ArrayList<>();
        for (Reward reward : RewardProvider.getMobRewards(getTemplateId())) {
            if (stolenReward == reward) {
                continue;
            }
            final Optional<Drop> dropResult = createDrop(owner, reward);
            dropResult.ifPresent(drops::add);
        }
        // Add drops to field if any
        if (!drops.isEmpty()) {
            getField().getDropPool().addDrops(drops, DropEnterType.CREATE, getX(), getY() - GameConstants.DROP_HEIGHT, 0);
        }
    }

    private Optional<Drop> createDrop(User owner, Reward reward) {
        // Drop probability
        double probability = reward.getProb();
        if (owner.getSecondaryStat().hasOption(CharacterTemporaryStat.ItemUpByItem)) {
            final double multiplier = (owner.getSecondaryStat().getOption(CharacterTemporaryStat.ItemUpByItem).nOption + 100) / 100.0;
            probability = probability * multiplier;
        }
        if (getMobStat().hasOption(MobTemporaryStat.Showdown) && getMobStat().getOption(MobTemporaryStat.Showdown).rOption != WildHunter.STINK_BOMB_SHOT) {
            final double multiplier = (getMobStat().getOption(MobTemporaryStat.Showdown).nOption + 100) / 100.0;
            probability = probability * multiplier;
        }
        if (!Util.succeedDouble(probability)) {
            return Optional.empty();
        }
        // Create drop
        if (reward.isMoney()) {
            int money = Util.getRandom(reward.getMin(), reward.getMax());
            if (money <= 0) {
                return Optional.empty();
            }
            if (owner.getSkillLevel(Thief.MESO_MASTERY) > 0) {
                final double multiplier = (owner.getSkillStatValue(Thief.MESO_MASTERY, SkillStat.mesoR) + 100) / 100.0;
                money = (int) (money * multiplier);
            }
            if (owner.getSecondaryStat().hasOption(CharacterTemporaryStat.MesoUp)) {
                final double multiplier = owner.getSecondaryStat().getOption(CharacterTemporaryStat.MesoUp).nOption / 100.0;
                money = (int) (money * multiplier);
            }
            if (owner.getSecondaryStat().hasOption(CharacterTemporaryStat.MesoUpByItem)) {
                final double multiplier = (owner.getSecondaryStat().getOption(CharacterTemporaryStat.MesoUpByItem).nOption + 100) / 100.0;
                money = (int) (money * multiplier);
            }
            return Optional.of(owner.getPartyId() == 0 ?
                    Drop.money(DropOwnType.USEROWN, this, money, owner.getCharacterId()) :
                    Drop.money(DropOwnType.PARTYOWN, this, money, owner.getPartyId())
            );
        } else {
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
            if (itemInfoResult.isEmpty()) {
                return Optional.empty();
            }
            final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
            final Item item = itemInfoResult.get().createItem(owner.getNextItemSn(), quantity);
            return Optional.of(owner.getPartyId() == 0 ?
                    Drop.item(DropOwnType.USEROWN, this, item, owner.getCharacterId(), reward.getQuestId()) :
                    Drop.item(DropOwnType.PARTYOWN, this, item, owner.getPartyId(), reward.getQuestId())
            );
        }
    }


    private void spawnRevives() {
        if (template.getRevives().isEmpty()) {
            return;
        }
        ServerExecutor.schedule(getField(), () -> {
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
                reviveMob.setLeft(isLeft());
                reviveMob.setAppearType(MobAppearType.REVIVED);
                getField().getMobPool().addMob(reviveMob);
                reviveMob.setAppearType(MobAppearType.NORMAL);
            }
        }, template.getReviveDelay(), TimeUnit.MILLISECONDS);
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
        return String.format("Mob { %d, oid : %d, hp : %d, mp : %d, controller : %s }", getTemplateId(), getId(), getHp(), getMp(), getController() != null ? getController().getCharacterName() : "null");
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
