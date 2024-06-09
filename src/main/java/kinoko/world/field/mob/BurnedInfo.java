package kinoko.world.field.mob;

import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class BurnedInfo implements Encodable {
    private final int characterId;
    private final int skillId;
    private final int damage;
    private final int interval;
    private final int dotCount;
    private final Instant expireTime;

    public BurnedInfo(int characterId, int skillId, int damage, int interval, int dotCount, Instant expireTime) {
        this.characterId = characterId;
        this.skillId = skillId;
        this.damage = damage;
        this.interval = interval;
        this.dotCount = dotCount;
        this.expireTime = expireTime;
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

    public Instant getExpireTime() {
        return expireTime;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(characterId); // dwCharacterID
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeInt(damage); // nDamage
        outPacket.encodeInt(interval); // tInterval
        outPacket.encodeInt((int) expireTime.toEpochMilli()); // tEnd
        outPacket.encodeInt(dotCount); // nDotCount
    }

    public static BurnedInfo from(User user, SkillInfo si, int slv) {
        final int interval = si.getValue(SkillStat.dotInterval, slv) * 1000;
        final int duration = si.getValue(SkillStat.dotTime, slv) * 1000;
        final Instant expireTime = Instant.now().plus(duration, ChronoUnit.MILLIS);
        return new BurnedInfo(
                user.getCharacterId(),
                si.getSkillId(),
                1337, // TODO: calculate damage
                interval,
                duration / interval,
                expireTime
        );
    }
}
