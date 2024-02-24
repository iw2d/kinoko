package kinoko.world.field.life.mob;

import kinoko.packet.field.MobPacket;
import kinoko.provider.ItemProvider;
import kinoko.provider.RewardProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.mob.MobSkill;
import kinoko.provider.reward.Reward;
import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.Encodable;
import kinoko.world.field.ControlledObject;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.life.Life;
import kinoko.world.item.Item;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Mob extends Life implements ControlledObject, Encodable, Lockable<Mob> {
    private final Lock lock = new ReentrantLock();
    private final MobStatManager mobStatManager = new MobStatManager();
    private final AtomicInteger attackCounter = new AtomicInteger(0);
    private final Map<MobSkill, Instant> skillCooltimes = new HashMap<>();
    private final Map<Integer, Integer> damageDone = new HashMap<>();
    private final MobInfo mobInfo;
    private final int mobTime;
    private final boolean respawn;

    private MobAppearType appearType = MobAppearType.NORMAL;
    private User controller;
    private int currentFh;
    private int hp;
    private int mp;

    public Mob(MobInfo mobInfo, int x, int y, int fh, int mobTime, boolean respawn) {
        this.mobInfo = mobInfo;
        this.mobTime = mobTime;
        this.respawn = respawn;
        reset(x, y, fh);
    }

    public int getTemplateId() {
        return this.mobInfo.getTemplateId();
    }

    public int getLevel() {
        return mobInfo.getLevel();
    }

    public int getMaxHp() {
        return mobInfo.getMaxHp();
    }

    public int getMaxMp() {
        return mobInfo.getMaxMp();
    }

    public boolean isBoss() {
        return mobInfo.isBoss();
    }

    public boolean isDamagedByMob() {
        return mobInfo.isDamagedByMob();
    }

    public int getMobTime() {
        return mobTime;
    }

    public boolean isRespawn() {
        return respawn;
    }

    public MobStatManager getMobStatManager() {
        return mobStatManager;
    }

    public Optional<MobAttack> getAttack(int attackIndex) {
        return Optional.ofNullable(mobInfo.getAttacks().get(attackIndex));
    }

    public Optional<MobSkill> getSkill(int skillIndex) {
        return Optional.ofNullable(mobInfo.getSkills().get(skillIndex));
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

    public int getCurrentFh() {
        return currentFh;
    }

    public void setCurrentFh(int currentFh) {
        this.currentFh = currentFh;
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

    public void reset(int x, int y, int fh) {
        // Life initialization
        setX(x);
        setY(y);
        setFoothold(fh);
        setMoveAction(5); // idk
        // Mob initialization
        setCurrentFh(fh);
        setHp(mobInfo.getMaxHp());
        setMp(mobInfo.getMaxMp());
        getMobStatManager().clear();
    }

    public void damage(User user, int totalDamage) {
        final int actualDamage = Math.min(getHp(), totalDamage);
        setHp(getHp() - actualDamage);
        damageDone.put(user.getCharacterId(), damageDone.getOrDefault(user.getCharacterId(), 0) + actualDamage);
    }

    public Set<Drop> getDrops(User user) {
        // TODO: use damageDone to get drop owner
        final User owner = user;
        final Set<Drop> drops = new HashSet<>();
        final Set<Reward> possibleRewards = Stream.concat(RewardProvider.getMobRewards(this).stream(), Stream.of(RewardProvider.getMobMoneyReward(this)))
                .collect(Collectors.toUnmodifiableSet());
        for (Reward reward : possibleRewards) {
            // TODO: apply drop rate
            if (reward.isMoney()) {
                final int money = Util.getRandom(reward.getMin(), reward.getMax());
                if (money <= 0) {
                    continue;
                }
                drops.add(Drop.money(DropOwnType.NO_OWN, this, money, 0));
            } else {
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
                if (itemInfoResult.isEmpty()) {
                    continue;
                }
                final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
                final Item item = itemInfoResult.get().createItem(owner.getNextItemSn(), quantity);
                drops.add(Drop.item(DropOwnType.NO_OWN, this, item, 0));
            }
        }
        return drops;
    }

    public Set<Tuple<Integer, Integer>> getExp(User user) {
        return Set.of(new Tuple<>(user.getCharacterId(), mobInfo.getExp()));
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

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
    public OutPacket enterFieldPacket() {
        return MobPacket.mobEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return MobPacket.mobLeaveField(this);
    }

    @Override
    public String toString() {
        return String.format("Mob { %d, oid : %d, hp : %d, mp : %d }", getTemplateId(), getId(), getHp(), getMp());
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CMob::Init
        outPacket.encodeShort(getX()); // ptPosPrev.x
        outPacket.encodeShort(getY()); // ptPosPrev.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getCurrentFh()); // pvcMobActiveObj (current foothold)
        outPacket.encodeShort(getFoothold()); // Foothold (start foothold)
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

    public static Mob from(MobInfo mobInfo, LifeInfo lifeInfo) {
        return new Mob(
                mobInfo,
                lifeInfo.getX(),
                lifeInfo.getY(),
                lifeInfo.getFh(),
                lifeInfo.getMobTime(),
                true
        );
    }
}
