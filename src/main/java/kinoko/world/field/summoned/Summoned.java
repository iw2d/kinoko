package kinoko.world.field.summoned;

import kinoko.provider.map.Foothold;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.field.Field;
import kinoko.world.field.life.Life;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.user.AvatarLook;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Summoned extends Life implements Encodable, Lockable<Summoned> {
    private final Lock lock = new ReentrantLock();
    private final int skillId;
    private final int skillLevel;
    private final SummonedMoveAbility moveAbility;
    private final SummonedAssistType assistType;
    private final AvatarLook avatarLook;
    private final Instant expireTime;

    private SummonedEnterType enterType = SummonedEnterType.CREATE_SUMMONED;
    private SummonedLeaveType leaveType = SummonedLeaveType.LEAVE_FIELD;
    private int hp = 1;

    private int rockAndShockCount;
    private Summoned rockAndShock1;
    private Summoned rockAndShock2;


    public Summoned(int skillId, int skillLevel, SummonedMoveAbility moveAbility, SummonedAssistType assistType, AvatarLook avatarLook, Instant expireTime) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.moveAbility = moveAbility;
        this.assistType = assistType;
        this.avatarLook = avatarLook;
        this.expireTime = expireTime;
        // Life initialization
        setId(skillId); // id as skill id to prevent multiple summons
    }

    public Summoned(int skillId, int skillLevel, SummonedMoveAbility moveAbility, SummonedAssistType assistType, Instant expireTime) {
        this(skillId, skillLevel, moveAbility, assistType, null, expireTime);
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

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setPosition(Field field, int x, int y) {
        setField(field);
        setX(x);
        setY(y);
        setFoothold(field.getFootholdBelow(x, y).orElse(Foothold.EMPTY_FOOTHOLD).getFootholdId());
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CSummoned::Init
        outPacket.encodeShort(getX()); // nX
        outPacket.encodeShort(getY()); // nY
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // nCurFoothold
        outPacket.encodeByte(moveAbility.getValue()); // nMoveAbility
        outPacket.encodeByte(assistType.getValue()); // nAssistType
        outPacket.encodeByte(enterType.getValue()); // nEnterType
        outPacket.encodeByte(avatarLook != null); // bAvatarLook
        if (avatarLook != null) {
            avatarLook.encode(outPacket); // AvatarLook::Decode
        }
        if (skillId == Mechanic.ROCK_N_SHOCK) {
            outPacket.encodeByte(rockAndShockCount); // nTeslaCoilState
            if (rockAndShockCount == 2) {
                outPacket.encodeShort(rockAndShock1.getX());
                outPacket.encodeShort(rockAndShock1.getX());
                outPacket.encodeShort(rockAndShock2.getX());
                outPacket.encodeShort(rockAndShock2.getX());
                outPacket.encodeShort(getX());
                outPacket.encodeShort(getY());
            }
        }
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
        return new Summoned(
                si.getSkillId(),
                slv,
                moveAbility,
                assistType,
                Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS)
        );
    }
}
