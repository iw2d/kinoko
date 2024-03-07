package kinoko.world.life.mob;

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

    public void clear() {
        temporaryStats.clear();
        burnedInfos.clear();
    }

    public Tuple<Set<MobTemporaryStat>, Set<BurnedInfo>> expireMobStat(Instant now) {
        final Set<MobTemporaryStat> resetStats = new HashSet<>();
        final Set<BurnedInfo> resetBurnedInfos = new HashSet<>();
        final var iter = getTemporaryStats().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<MobTemporaryStat, MobStatOption> entry = iter.next();
            final MobTemporaryStat mts = entry.getKey();
            final MobStatOption option = entry.getValue();
            // Check temporary stat expire time and remove mts
            if (now.isBefore(option.getExpireTime())) {
                continue;
            }
            iter.remove();
            resetStats.add(mts);
        }
        return new Tuple<>(resetStats, resetBurnedInfos);
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
            for (BurnedInfo bi : setBurnedInfos) {
                outPacket.encodeInt(bi.characterId); // dwCharacterID
                outPacket.encodeInt(bi.skillId); // nSkillID
                outPacket.encodeInt(bi.damage); // nDamage
                outPacket.encodeInt(bi.interval); // tInterval
                outPacket.encodeInt(bi.end); // tEnd
                outPacket.encodeInt(bi.dotCount); // nDotCount
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
            for (BurnedInfo bi : resetBurnedInfos) {
                outPacket.encodeInt(bi.characterId); // dwCharacterID
                outPacket.encodeInt(bi.skillId); // nSkillID
            }
        }
    }
}
