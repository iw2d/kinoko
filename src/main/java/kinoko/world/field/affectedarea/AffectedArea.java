package kinoko.world.field.affectedarea;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Locked;
import kinoko.util.Rect;
import kinoko.world.field.FieldObject;
import kinoko.world.field.FieldObjectImpl;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.Mob;
import kinoko.world.job.explorer.Magician;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class AffectedArea extends FieldObjectImpl implements Encodable {
    private static final Logger log = LogManager.getLogger(AffectedArea.class);
    private final AffectedAreaType type;
    private final FieldObject owner;
    private final int skillId;
    private final int skillLevel;
    private final int delay;
    private final Rect rect;
    private final Instant expireTime;

    public AffectedArea(AffectedAreaType type, FieldObject owner, int skillId, int skillLevel, int delay, Rect rect, Instant expireTime) {
        this.type = type;
        this.owner = owner;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.delay = delay;
        this.rect = rect;
        this.expireTime = expireTime;
    }

    public AffectedAreaType getType() {
        return type;
    }

    public FieldObject getOwner() {
        return owner;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getDelay() {
        return delay;
    }

    public Rect getRect() {
        return rect;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void handleMobInside(Locked<Mob> lockedMob) {
        final Mob mob = lockedMob.get();
        if (mob.getHp() <= 0 || !rect.isInsideRect(mob.getX(), mob.getY())) {
            return;
        }
        switch (skillId) {
            case Magician.POISON_MIST -> {
                if (mob.getHp() == 1 || mob.getMobStat().hasBurnedInfo(owner.getId(), skillId)) {
                    return;
                }
                final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
                if (skillInfoResult.isEmpty()) {
                    log.error("Failed to resolve skill info for affected area : {}", skillId);
                    return;
                }
                mob.setBurnedInfo(BurnedInfo.from((User) owner, skillInfoResult.get(), skillLevel, mob));
            }
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getId()); // dwID
        outPacket.encodeInt(type.getValue()); // nType
        outPacket.encodeInt(owner.getId()); // dwOwnerID
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeByte(skillLevel); // nSLV
        outPacket.encodeShort(delay); // tStart = get_update_time() + 100 * delay
        outPacket.encodeInt(rect.getLeft()); // rcArea->left
        outPacket.encodeInt(rect.getTop()); // rcArea->top
        outPacket.encodeInt(rect.getRight()); // rcArea->right
        outPacket.encodeInt(rect.getBottom()); // rcArea->bottom
        outPacket.encodeInt(0); // nElemAttr
        outPacket.encodeInt(0); // nPhase
    }

    public static AffectedArea userSkill(User owner, SkillInfo si, int slv, int delay, int x, int y) {
        return AffectedArea.from(AffectedAreaType.UserSkill, owner, si, slv, delay, x, y);
    }

    public static AffectedArea from(AffectedAreaType affectedAreaType, User owner, SkillInfo si, int slv, int delay, int x, int y) {
        final Rect rect = si.getRect().translate(x, y);
        final Instant expireTime = Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS);
        return new AffectedArea(affectedAreaType, owner, si.getSkillId(), slv, delay, rect, expireTime);
    }
}
