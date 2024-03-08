package kinoko.world.field.summoned;

import kinoko.packet.field.SummonedPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.field.life.Life;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.user.AvatarLook;
import kinoko.world.user.User;

public final class Summoned extends Life implements Encodable {
    private final User owner;
    private final int skillId;
    private final int skillLevel;
    private final MoveAbility moveAbility;
    private final AssistType assistType;
    private final EnterType enterType;
    private final AvatarLook avatarLook;

    public Summoned(User owner, int skillId, int skillLevel, MoveAbility moveAbility, AssistType assistType, EnterType enterType, AvatarLook avatarLook) {
        this.owner = owner;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.moveAbility = moveAbility;
        this.assistType = assistType;
        this.enterType = enterType;
        this.avatarLook = avatarLook;
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

    public MoveAbility getMoveAbility() {
        return moveAbility;
    }

    public AssistType getAssistType() {
        return assistType;
    }

    public EnterType getEnterType() {
        return enterType;
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
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
}
