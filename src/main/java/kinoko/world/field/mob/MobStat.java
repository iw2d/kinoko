package kinoko.world.field.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Tuple;

import java.time.Instant;
import java.util.*;
import java.util.function.BiPredicate;

public final class MobStat {
    private final Map<MobTemporaryStat, MobStatOption> temporaryStats = new EnumMap<>(MobTemporaryStat.class);
    private final Map<Tuple<Integer, Integer>, BurnedInfo> burnedInfos = new HashMap<>(); // characterId, skillId -> BurnedInfo

    public Map<MobTemporaryStat, MobStatOption> getTemporaryStats() {
        return temporaryStats;
    }

    public Map<Tuple<Integer, Integer>, BurnedInfo> getBurnedInfos() {
        return burnedInfos;
    }

    public void addBurnedInfo(BurnedInfo burnedInfo) {
        burnedInfos.put(Tuple.of(burnedInfo.getCharacterId(), burnedInfo.getSkillId()), burnedInfo);
    }

    public boolean hasBurnedInfo(int characterId, int skillId) {
        return burnedInfos.containsKey(Tuple.of(characterId, skillId));
    }

    public void clear() {
        temporaryStats.clear();
        burnedInfos.clear();
    }

    public MobStatOption getOption(MobTemporaryStat mts) {
        return temporaryStats.getOrDefault(mts, MobStatOption.EMPTY);
    }

    public boolean hasOption(MobTemporaryStat mts) {
        return getOption(mts).nOption > 0;
    }

    public Set<MobTemporaryStat> expireTemporaryStat(Instant now) {
        return resetTemporaryStat((mts, option) -> now.isAfter(option.getExpireTime()));
    }

    public Set<MobTemporaryStat> resetTemporaryStat(BiPredicate<MobTemporaryStat, MobStatOption> predicate) {
        final Set<MobTemporaryStat> resetStats = new HashSet<>();
        final var iter = temporaryStats.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<MobTemporaryStat, MobStatOption> entry = iter.next();
            final MobTemporaryStat mts = entry.getKey();
            final MobStatOption option = entry.getValue();
            if (predicate.test(mts, option)) {
                iter.remove();
                resetStats.add(mts);
            }
        }
        return resetStats;
    }

    public void encodeTemporary(OutPacket outPacket) {
        final BitFlag<MobTemporaryStat> flag = BitFlag.from(temporaryStats.keySet(), MobTemporaryStat.FLAG_SIZE);
        encodeTemporary(flag, outPacket);
    }

    public void encodeTemporary(BitFlag<MobTemporaryStat> flag, OutPacket outPacket) {
        // CMob::SetTemporaryStat
        flag.encode(outPacket); // uFlag

        // MobStat::DecodeTemporary
        for (MobTemporaryStat mts : MobTemporaryStat.ENCODE_ORDER) {
            if (flag.hasFlag(mts)) {
                getOption(mts).encode(outPacket);
            }
        }
        if (flag.hasFlag(MobTemporaryStat.Burned)) {
            outPacket.encodeInt(burnedInfos.size()); // uCount;
            for (BurnedInfo burnedInfo : burnedInfos.values()) {
                burnedInfo.encode(outPacket);
            }
        }
        if (flag.hasFlag(MobTemporaryStat.PCounter)) {
            outPacket.encodeInt(0); // wPCounter
        }
        if (flag.hasFlag(MobTemporaryStat.MCounter)) {
            outPacket.encodeInt(0); // wMCounter
        }
        if (flag.hasFlag(MobTemporaryStat.PCounter) || flag.hasFlag(MobTemporaryStat.MCounter)) {
            outPacket.encodeInt(100); // nCounterProb
        }
        if (flag.hasFlag(MobTemporaryStat.Disable)) {
            outPacket.encodeByte(true); // bInvincible
            outPacket.encodeByte(false); // bDisable
        }
    }
}
