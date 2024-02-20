package kinoko.world.field.life.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Lockable;
import kinoko.util.Tuple;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MobStatManager implements Lockable<MobStatManager> {
    private final Lock lock = new ReentrantLock();
    private final Map<MobStat, MobStatOption> stats = new EnumMap<>(MobStat.class);
    private final Map<Tuple<Integer, Integer>, BurnedInfo> burnedInfos = new HashMap<>();
    private final BitFlag<MobStat> setStatFlag = new BitFlag<>(MobStat.FLAG_SIZE);
    private final BitFlag<MobStat> resetStatFlag = new BitFlag<>(MobStat.FLAG_SIZE);

    public void encode(OutPacket outPacket, boolean complete) {
        final BitFlag<MobStat> statFlag = complete ? BitFlag.from(stats.keySet(), MobStat.FLAG_SIZE) : setStatFlag;

        // CMob::SetTemporaryStat
        statFlag.encode(outPacket); // uFlag

        // MobStat::DecodeTemporary
        for (MobStat ms : MobStat.ENCODE_ORDER) {
            if (statFlag.hasFlag(ms)) {
                stats.get(ms).encode(outPacket);
            }
        }
        if (statFlag.hasFlag(MobStat.Burned)) {
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
        if (statFlag.hasFlag(MobStat.PCounter)) {
            outPacket.encodeInt(0); // wPCounter
            outPacket.encodeInt(100); // nCounterProb
        }
        if (statFlag.hasFlag(MobStat.PCounter)) {
            outPacket.encodeInt(0); // wMCounter
            outPacket.encodeInt(100); // nCounterProb
        }
        if (statFlag.hasFlag(MobStat.Disable)) {
            outPacket.encodeByte(true); // bInvincible
            outPacket.encodeByte(false); // bDisable
        }
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
