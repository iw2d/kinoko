package kinoko.world.life.mob;

import kinoko.packet.life.MobPacket;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.mob.MobSkill;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.ControlledObject;
import kinoko.world.life.Life;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Mob extends Life implements ControlledObject {
    private final MobStatManager mobStatManager = new MobStatManager();
    private final Map<MobSkill, Instant> skillCooltimes = new ConcurrentHashMap<>();
    private final AtomicInteger attackCounter = new AtomicInteger(0);
    private final MobInfo mobInfo;
    private final AppearType appearType;

    private User controller;
    private int currentFh;
    private int hp;
    private int mp;

    public Mob(int x, int y, int fh, MobInfo mobInfo, AppearType appearType) {
        this.mobInfo = mobInfo;
        this.appearType = appearType;

        // Life initialization
        setX(x);
        setY(y);
        setFh(fh);
        setMoveAction(5); // idk

        // Mob initialization
        setCurrentFh(fh);
        setHp(mobInfo.getMaxHp());
        setMp(mobInfo.getMaxMp());
    }

    public int getTemplateId() {
        return this.mobInfo.getTemplateId();
    }

    public boolean isBoss() {
        return mobInfo.isBoss();
    }

    public MobStatManager getMobStatManager() {
        return mobStatManager;
    }

    public Optional<MobAttack> getAttack(int attackIndex) {
        if (!mobInfo.getAttacks().containsKey(attackIndex)) {
            return Optional.empty();
        }
        return Optional.of(mobInfo.getAttacks().get(attackIndex));
    }

    public Optional<MobSkill> getSkill(int skillIndex) {
        if (!mobInfo.getSkills().containsKey(skillIndex)) {
            return Optional.empty();
        }
        return Optional.of(mobInfo.getSkills().get(skillIndex));
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
        return String.format("Mob { %d, oid : %d, hp : %d, mp : %d }", getTemplateId(), getObjectId(), getHp(), getMp());
    }

    public void encodeInit(OutPacket outPacket) {
        // CMob::Init
        outPacket.encodeShort(getX()); // ptPosPrev.x
        outPacket.encodeShort(getY()); // ptPosPrev.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getCurrentFh()); // pvcMobActiveObj (current foothold)
        outPacket.encodeShort(getFh()); // Foothold (start foothold)
        outPacket.encodeByte(appearType.getValue()); // nAppearType
        if (appearType == AppearType.REVIVED || appearType.getValue() >= 0) {
            outPacket.encodeInt(0); // dwOption
        }
        outPacket.encodeByte(0); // nTeamForMCarnival
        outPacket.encodeInt(0); // nEffectItemID
        outPacket.encodeInt(0); // nPhase
    }

    public static Mob from(LifeInfo lifeInfo, MobInfo mobInfo) {
        return new Mob(
                lifeInfo.getX(),
                lifeInfo.getY(),
                lifeInfo.getFh(),
                mobInfo,
                AppearType.NORMAL
        );
    }
}
