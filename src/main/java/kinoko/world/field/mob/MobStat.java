package kinoko.world.field.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Tuple;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
        burnedInfos.put(new Tuple<>(burnedInfo.getCharacterId(), burnedInfo.getSkillId()), burnedInfo);
    }

    public boolean hasBurnedInfo(int characterId, int skillId) {
        return burnedInfos.containsKey(new Tuple<>(characterId, skillId));
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

    public Set<MobTemporaryStat> expireMobStat(Instant now) {
        final Set<MobTemporaryStat> resetStats = new HashSet<>();
        final var statIter = temporaryStats.entrySet().iterator();
        while (statIter.hasNext()) {
            final Map.Entry<MobTemporaryStat, MobStatOption> entry = statIter.next();
            final MobTemporaryStat mts = entry.getKey();
            final MobStatOption option = entry.getValue();
            // Check temporary stat expire time and remove mts
            if (now.isBefore(option.getExpireTime())) {
                continue;
            }
            statIter.remove();
            resetStats.add(mts);
        }
        return resetStats;
    }

    public static void encode(OutPacket outPacket, MobStat ms) {
        final Map<MobTemporaryStat, MobStatOption> stats = ms.getTemporaryStats();
        final Set<BurnedInfo> burnedInfos = ms.getBurnedInfos().values().stream().collect(Collectors.toUnmodifiableSet());
        encode(outPacket, stats, burnedInfos);
    }

    public static void encode(OutPacket outPacket, Map<MobTemporaryStat, MobStatOption> setStats, Set<BurnedInfo> setBurnedInfos) {
        // CMob::SetTemporaryStat
        final BitFlag<MobTemporaryStat> statFlag = BitFlag.from(setStats.keySet(), MobTemporaryStat.FLAG_SIZE);
        statFlag.encode(outPacket); // uFlag

        // MobStat::DecodeTemporary
        for (MobTemporaryStat mts : MobTemporaryStat.ENCODE_ORDER) {
            if (statFlag.hasFlag(mts)) {
                setStats.get(mts).encode(outPacket);
            }
        }
        if (statFlag.hasFlag(MobTemporaryStat.Burned)) {
            outPacket.encodeInt(setBurnedInfos.size()); // uCount;
            for (BurnedInfo burnedInfo : setBurnedInfos) {
                burnedInfo.encode(outPacket);
            }
        }
        if (statFlag.hasFlag(MobTemporaryStat.PCounter)) {
            outPacket.encodeInt(0); // wPCounter
            outPacket.encodeInt(100); // nCounterProb
        }
        if (statFlag.hasFlag(MobTemporaryStat.PCounter)) {
            outPacket.encodeInt(0); // wMCounter
            outPacket.encodeInt(100); // nCounterProb
        }
        if (statFlag.hasFlag(MobTemporaryStat.Disable)) {
            outPacket.encodeByte(true); // bInvincible
            outPacket.encodeByte(false); // bDisable
        }
    }

    public static void encodeReset(OutPacket outPacket, Set<MobTemporaryStat> resetStats, Set<BurnedInfo> resetBurnedInfos) {
        final BitFlag<MobTemporaryStat> statFlag = BitFlag.from(resetStats, MobTemporaryStat.FLAG_SIZE);
        statFlag.encode(outPacket); // uFlagReset

        // MobStat::Reset
        if (statFlag.hasFlag(MobTemporaryStat.Burned)) {
            outPacket.encodeInt(resetBurnedInfos.size());
            for (BurnedInfo burnedInfo : resetBurnedInfos) {
                outPacket.encodeInt(burnedInfo.getCharacterId()); // dwCharacterID
                outPacket.encodeInt(burnedInfo.getSkillId()); // nSkillID
            }
        }
    }
}
