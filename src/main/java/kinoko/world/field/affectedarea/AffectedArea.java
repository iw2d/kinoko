package kinoko.world.field.affectedarea;

import kinoko.meta.SkillId;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.ElementAttribute;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.ServerConfig;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Rect;
import kinoko.world.field.FieldObject;
import kinoko.world.field.FieldObjectImpl;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.Mob;
import kinoko.world.job.cygnus.BlazeWizard;
import kinoko.world.job.cygnus.NightWalker;
import kinoko.world.job.explorer.Magician;
import kinoko.world.job.legend.Evan;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

public final class AffectedArea extends FieldObjectImpl implements Encodable {
    private static final Logger log = LogManager.getLogger(AffectedArea.class);
    private final AffectedAreaType type;
    private final FieldObject owner;
    private final SkillId skillId;
    private final int skillLevel;
    private final int delay;
    private final int interval;
    private final Rect rect;
    private final ElementAttribute elemAttr;
    private final Instant expireTime;

    public AffectedArea(AffectedAreaType type, FieldObject owner, SkillId skillId, int skillLevel, int delay, int interval, Rect rect, ElementAttribute elemAttr, Instant expireTime) {
        this.type = type;
        this.owner = owner;
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.delay = delay;
        this.interval = interval;
        this.rect = rect;
        this.elemAttr = elemAttr;
        this.expireTime = expireTime;
    }

    public AffectedAreaType getType() {
        return type;
    }

    public FieldObject getOwner() {
        return owner;
    }

    public SkillId getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getDelay() {
        return delay;
    }

    public int getInterval() {
        return interval;
    }

    public Rect getRect() {
        return rect;
    }

    public ElementAttribute getElemAttr() {
        return elemAttr;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void handleUserInside(User user) {
        if (Objects.requireNonNull(skillId) == SkillId.EVAN8_RECOVERY_AURA) {
            final int partyId = ((User) owner).getPartyId();
            if (user.getCharacterId() == owner.getId() || (partyId != 0 && user.getPartyId() == partyId)) {
                final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
                if (skillInfoResult.isEmpty()) {
                    log.error("Failed to resolve skill info for affected area : {}", skillId);
                    return;
                }
                final SkillInfo si = skillInfoResult.get();
                double recoveryRate = si.getValue(SkillStat.x, skillLevel) / 100.0;
                recoveryRate = recoveryRate * (interval * ServerConfig.FIELD_TICK_INTERVAL) / si.getDuration(skillLevel);
                user.addMp((int) (recoveryRate * user.getMaxMp()));
            }
        }
    }

    public void handleMobInside(Mob mob) {
        switch (skillId) {
            case SkillId.FP2_POISON_MIST, SkillId.BW3_FLAME_GEAR, SkillId.NW3_POISON_BOMB -> {
                if (mob.getHp() == 1 || mob.getMobStat().hasBurnedInfo(owner.getId(), skillId.getId())) {
                    return;
                }
                final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
                if (skillInfoResult.isEmpty()) {
                    log.error("Failed to resolve skill info for affected area : {}", skillId);
                    return;
                }
                mob.setBurnedInfo(BurnedInfo.from((User) owner, skillInfoResult.get(), skillLevel, mob), 0);
            }
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getId()); // dwID
        outPacket.encodeInt(type.getValue()); // nType
        outPacket.encodeInt(owner.getId()); // dwOwnerID
        outPacket.encodeSkillId(skillId); // nSkillID
        outPacket.encodeByte(skillLevel); // nSLV
        outPacket.encodeShort(delay); // tStart = get_update_time() + 100 * delay
        outPacket.encodeInt(rect.getLeft()); // rcArea->left
        outPacket.encodeInt(rect.getTop()); // rcArea->top
        outPacket.encodeInt(rect.getRight()); // rcArea->right
        outPacket.encodeInt(rect.getBottom()); // rcArea->bottom
        outPacket.encodeInt(elemAttr.getValue()); // nElemAttr
        outPacket.encodeInt(0); // nPhase
    }

    public static AffectedArea mobSkill(Mob owner, SkillInfo si, int slv, int delay) {
        final Rect rect = owner.getRelativeRect(si.getRect(slv));
        final Instant expireTime = Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS);
        return new AffectedArea(AffectedAreaType.MobSkill, owner, si.getSkillId(), slv, delay, 1, rect, si.getElemAttr(), expireTime);
    }

    public static AffectedArea userSkill(User owner, SkillInfo si, int slv, int delay, int x, int y) {
        return AffectedArea.from(AffectedAreaType.UserSkill, owner, si, slv, delay, 1, x, y);
    }

    public static AffectedArea buff(User owner, int itemId, Rect rect, Instant expireTime) {
        //TODO fix skill id
        return new AffectedArea(AffectedAreaType.Buff, owner, SkillId.NONE, 0, 0, 0, owner.getRelativeRect(rect), ElementAttribute.PHYSICAL, expireTime);
    }

    public static AffectedArea from(AffectedAreaType affectedAreaType, User owner, SkillInfo si, int slv, int delay, int interval, int x, int y) {
        final Rect rect = si.getRect(slv).translate(x, y);
        final Instant expireTime = Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS);
        return new AffectedArea(affectedAreaType, owner, si.getSkillId(), slv, delay, interval, rect, si.getElemAttr(), expireTime);
    }
}
