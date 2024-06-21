package kinoko.world.field.summoned;

import kinoko.provider.map.Foothold;
import kinoko.provider.skill.SkillInfo;
import kinoko.util.Lockable;
import kinoko.util.Rect;
import kinoko.world.field.Field;
import kinoko.world.field.life.Life;
import kinoko.world.user.AvatarLook;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Summoned extends Life implements Lockable<Summoned> {
    private final Lock lock = new ReentrantLock();
    private final int skillId;
    private final int skillLevel;
    private final SummonedMoveAbility moveAbility;
    private final SummonedAssistType assistType;
    private final AvatarLook avatarLook;
    private final Instant expireTime;

    private SummonedEnterType enterType = SummonedEnterType.CREATE_SUMMONED;
    private SummonedLeaveType leaveType = SummonedLeaveType.LEAVE_FIELD;
    private Rect rect;
    private int hp = 1;
    private int teslaCoilState = 0;

    public Summoned(int skillId, int skillLevel, SummonedMoveAbility moveAbility, SummonedAssistType assistType, AvatarLook avatarLook, Instant expireTime) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.moveAbility = moveAbility;
        this.assistType = assistType;
        this.avatarLook = avatarLook;
        this.expireTime = expireTime;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public SummonedMoveAbility getMoveAbility() {
        return moveAbility;
    }

    public SummonedAssistType getAssistType() {
        return assistType;
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public SummonedEnterType getEnterType() {
        return enterType;
    }

    public void setEnterType(SummonedEnterType enterType) {
        this.enterType = enterType;
    }

    public SummonedLeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(SummonedLeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getTeslaCoilState() {
        return teslaCoilState;
    }

    public void setTeslaCoilState(int teslaCoilState) {
        this.teslaCoilState = teslaCoilState;
    }

    public void setPosition(Field field, int x, int y, boolean left) {
        setField(field);
        setX(x);
        setY(y);
        setLeft(left);
        setFoothold(field.getFootholdBelow(x, y).map(Foothold::getFootholdId).orElse(0));
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    public static Summoned from(SkillInfo si, int slv, SummonedMoveAbility moveAbility, SummonedAssistType assistType) {
        return Summoned.from(si.getSkillId(), slv, moveAbility, assistType, Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS));
    }

    public static Summoned from(int skillId, int slv, SummonedMoveAbility moveAbility, SummonedAssistType assistType, Instant expireTime) {
        return new Summoned(
                skillId,
                slv,
                moveAbility,
                assistType,
                null,
                expireTime
        );
    }
}
