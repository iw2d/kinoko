package kinoko.world.field.summoned;

import kinoko.packet.field.SummonedPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.field.UserObject;
import kinoko.world.field.life.Life;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.user.AvatarLook;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Summoned extends Life implements UserObject, Encodable, Lockable<Summoned> {
    private final Lock lock = new ReentrantLock();
    private final int ownerId;
    private final int ownerLevel;
    private final int skillId;
    private final int skillLevel;
    private final SummonedMoveAbility moveAbility;
    private final SummonedAssistType assistType;
    private final SummonedEnterType enterType;
    private final AvatarLook avatarLook;

    private int hp = 1;

    public Summoned(int ownerId, int ownerLevel, int skillId, int skillLevel, SummonedMoveAbility moveAbility, SummonedAssistType assistType, SummonedEnterType enterType, AvatarLook avatarLook) {
        this.ownerId = ownerId;
        this.ownerLevel = ownerLevel;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.moveAbility = moveAbility;
        this.assistType = assistType;
        this.enterType = enterType;
        this.avatarLook = avatarLook;
    }

    @Override
    public int getOwnerId() {
        return ownerId;
    }

    public int getOwnerLevel() {
        return ownerLevel;
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

    public SummonedEnterType getEnterType() {
        return enterType;
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
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
    public OutPacket enterFieldPacket() {
        return SummonedPacket.summonedEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return SummonedPacket.summonedLeaveField(this);
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
