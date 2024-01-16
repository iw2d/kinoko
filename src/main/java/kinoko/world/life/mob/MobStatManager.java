package kinoko.world.life.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Option;
import kinoko.util.Tuple;
import kinoko.world.Encodable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class MobStatManager implements Encodable {
    private final Map<MobStat, Option> stats = new EnumMap<>(MobStat.class);
    private final Map<Tuple<Integer, Integer>, BurnedInfo> burnedInfos = new HashMap<>();
    // TODO: scheduler
    private final BitFlag<MobStat> setStatFlag = new BitFlag<>(128);
    private final BitFlag<MobStat> resetStatFlag = new BitFlag<>(128);

    @Override
    public void encode(OutPacket outPacket) {
        // CMob::SetTemporaryStat
        setStatFlag.encode(outPacket); // uFlag

        // MobStat::DecodeTemporary
        for (MobStat ms : MobStat.ENCODE_ORDER) {
            if (setStatFlag.hasFlag(ms)) {
                outPacket.encodeShort(stats.get(ms).nOption);
                outPacket.encodeInt(stats.get(ms).rOption);
                outPacket.encodeShort(stats.get(ms).tOption);
            }
        }
        if (setStatFlag.hasFlag(MobStat.Burned)) {
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
        if (setStatFlag.hasFlag(MobStat.PCounter)) {
            outPacket.encodeInt(0); // wPCounter
            outPacket.encodeInt(100); // nCounterProb
        }
        if (setStatFlag.hasFlag(MobStat.PCounter)) {
            outPacket.encodeInt(0); // wMCounter
            outPacket.encodeInt(100); // nCounterProb
        }
        if (setStatFlag.hasFlag(MobStat.Disable)) {
            outPacket.encodeByte(true); // bInvincible
            outPacket.encodeByte(false); // bDisable
        }
    }
}
