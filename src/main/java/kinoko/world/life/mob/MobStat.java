package kinoko.world.life.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Tuple;

import java.util.*;

public final class MobStat {
    private final Map<MobTemporaryStat, MobStatOption> stats = new EnumMap<>(MobTemporaryStat.class);
    private final Map<Tuple<Integer, Integer>, BurnedInfo> burnedInfos = new HashMap<>(); // characterId, skillId -> BurnedInfo
    private final Set<Tuple<Integer, Integer>> resetBurnedInfos = new HashSet<>(); // characterId, skillId
    private final BitFlag<MobTemporaryStat> setStatFlag = new BitFlag<>(MobTemporaryStat.FLAG_SIZE);
    private final BitFlag<MobTemporaryStat> resetStatFlag = new BitFlag<>(MobTemporaryStat.FLAG_SIZE);

    public void clear() {
        stats.clear();
        burnedInfos.clear();
        resetBurnedInfos.clear();
        setStatFlag.clear();
        resetStatFlag.clear();
    }

    public void encode(OutPacket outPacket, boolean complete) {
        final BitFlag<MobTemporaryStat> statFlag = complete ? BitFlag.from(stats.keySet(), MobTemporaryStat.FLAG_SIZE) : setStatFlag;

        // CMob::SetTemporaryStat
        statFlag.encode(outPacket); // uFlag

        // MobStat::DecodeTemporary
        for (MobTemporaryStat mts : MobTemporaryStat.ENCODE_ORDER) {
            if (statFlag.hasFlag(mts)) {
                stats.get(mts).encode(outPacket);
            }
        }
        if (statFlag.hasFlag(MobTemporaryStat.Burned)) {
            outPacket.encodeInt(burnedInfos.size()); // uCount;
            for (BurnedInfo bi : burnedInfos.values()) {
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

    public void encodeReset(OutPacket outPacket) {
        resetStatFlag.encode(outPacket); // uFlagReset

        // MobStat::Reset
        if (resetStatFlag.hasFlag(MobTemporaryStat.Burned)) {
            outPacket.encodeInt(resetBurnedInfos.size());
            for (var entry : resetBurnedInfos) {
                outPacket.encodeInt(entry.getLeft()); // dwCharacterID
                outPacket.encodeInt(entry.getRight()); // nSkillID
            }
        }
    }
}
