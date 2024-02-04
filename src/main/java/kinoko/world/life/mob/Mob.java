package kinoko.world.life.mob;

import kinoko.packet.life.MobPacket;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.mob.MobSkillInfo;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.ControlledObject;
import kinoko.world.life.Life;
import kinoko.world.user.User;

import java.util.List;

public final class Mob extends Life implements ControlledObject {
    private final MobStatManager mobStatManager = new MobStatManager();
    private final LifeInfo lifeInfo;
    private final MobInfo mobInfo;
    private final AppearType appearType;
    private User controller;

    private int hp;
    private int mp;

    public Mob(LifeInfo lifeInfo, MobInfo mobInfo) {
        this.lifeInfo = lifeInfo;
        this.mobInfo = mobInfo;
        this.appearType = AppearType.NORMAL;

        // Initialization
        setX(lifeInfo.getX());
        setY(lifeInfo.getY());
        setFh(lifeInfo.getFh());
        setMoveAction(5); // idk

        setHp(mobInfo.maxHP());
        setMp(mobInfo.maxMP());
    }

    public MobStatManager getMobStatManager() {
        return mobStatManager;
    }

    public int getTemplateId() {
        return this.mobInfo.templateId();
    }

    public List<MobSkillInfo> getSkills() {
        return mobInfo.skills();
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

    public void encodeInit(OutPacket outPacket) {
        // CMob::Init
        outPacket.encodeShort(getX()); // ptPosPrev.x
        outPacket.encodeShort(getY()); // ptPosPrev.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFh()); // pvcMobActiveObj (current foothold)
        outPacket.encodeShort(lifeInfo.getFh()); // Foothold (start foothold)
        outPacket.encodeByte(appearType.getValue()); // nAppearType
        if (appearType == AppearType.REVIVED || appearType.getValue() >= 0) {
            outPacket.encodeInt(0); // dwOption
        }
        outPacket.encodeByte(0); // nTeamForMCarnival
        outPacket.encodeInt(0); // nEffectItemID
        outPacket.encodeInt(0); // nPhase
    }
}
