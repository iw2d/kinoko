package kinoko.world.field.mob;

import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.TimeUtil;
import kinoko.world.GameConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CalcDamage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class BurnedInfo implements Encodable {
    private final int characterId;
    private final int skillId;
    private final int damage;
    private final int interval;
    private int dotCount;
    private Instant lastUpdate;

    public BurnedInfo(int characterId, int skillId, int damage, int interval, int dotCount) {
        this.characterId = characterId;
        this.skillId = skillId;
        this.damage = damage;
        this.interval = interval;
        this.dotCount = dotCount;
        this.lastUpdate = TimeUtil.getCurrentTime();
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getDamage() {
        return damage;
    }

    public int getInterval() {
        return interval;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Instant getNextUpdate() {
        return lastUpdate.plus(interval, ChronoUnit.MILLIS);
    }

    @Override
    public String toString() {
        return "BurnedInfo{" +
                "characterId=" + characterId +
                ", skillId=" + skillId +
                ", damage=" + damage +
                ", interval=" + interval +
                ", dotCount=" + dotCount +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(characterId); // dwCharacterID
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeInt(damage); // nDamage
        outPacket.encodeInt(interval); // tInterval
        outPacket.encodeInt(lastUpdate.plus((long) dotCount * interval, ChronoUnit.MILLIS).toEpochMilli()); // tEnd
        outPacket.encodeInt(dotCount); // nDotCount
    }

    public static BurnedInfo from(User user, SkillInfo si, int slv, Mob mob) {
        return BurnedInfo.from(user, si, slv, si.getValue(SkillStat.dot, slv), mob);
    }

    public static BurnedInfo from(User user, SkillInfo si, int slv, int skillDamage, Mob mob) {
        // Calculate damage - only affected by damage range and element attribute
        double damage = CalcDamage.calcDamageMax(user);
        damage = CalcDamage.getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());
        if (skillDamage > 0) {
            damage = skillDamage / 100.0 * damage;
        }
        // Create burned info
        final int interval = si.getValue(SkillStat.dotInterval, slv) * 1000;
        final int duration = si.getValue(SkillStat.dotTime, slv) * 1000;
        return new BurnedInfo(
                user.getCharacterId(),
                si.getSkillId(),
                (int) Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX),
                interval,
                duration / interval
        );
    }
}
