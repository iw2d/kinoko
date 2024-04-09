package kinoko.world.field.summoned;

import kinoko.provider.skill.SkillInfo;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.field.life.Life;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.user.AvatarLook;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Summoned extends Life implements Encodable, Lockable<Summoned> {
    private final Lock lock = new ReentrantLock();
    private final User owner;
    private final int skillId;
    private final int skillLevel;
    private final SummonedMoveAbility moveAbility;
    private final SummonedAssistType assistType;
    private final AvatarLook avatarLook;
    private final Instant expireTime;

    private SummonedEnterType enterType = SummonedEnterType.CREATE_SUMMONED;
    private SummonedLeaveType leaveType = SummonedLeaveType.LEAVE_FIELD;
    private int hp = 1;

    public Summoned(User owner, int skillId, int skillLevel, SummonedMoveAbility moveAbility, SummonedAssistType assistType, AvatarLook avatarLook, Instant expireTime) {
        this.owner = owner;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.moveAbility = moveAbility;
        this.assistType = assistType;
        this.avatarLook = avatarLook;
        this.expireTime = expireTime;
        // Life initialization
        setId(skillId); // id as skill id to prevent multiple summons
        setX(owner.getX());
        setY(owner.getY());
        setFoothold(owner.getFoothold());
    }

    public Summoned(User owner, int skillId, int skillLevel, SummonedMoveAbility moveAbility, SummonedAssistType assistType, Instant expireTime) {
        this(owner, skillId, skillLevel, moveAbility, assistType, null, expireTime);
    }

    public User getOwner() {
        return owner;
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
            outPacket.encodeByte(0); // nTeslaCoilState
            if (false) { // TODO : rock n shock
                for (int i = 0; i < 3; i++) {
                    outPacket.encodeShort(0); // x
                    outPacket.encodeShort(0); // y
                }
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

    public static Summoned from(User user, SkillInfo si, int slv, SummonedMoveAbility moveAbility, SummonedAssistType assistType) {
        return new Summoned(
                user,
                si.getSkillId(),
                slv,
                moveAbility,
                assistType,
                Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS)
        );
    }
}
