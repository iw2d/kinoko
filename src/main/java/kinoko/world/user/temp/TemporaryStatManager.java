package kinoko.world.user.temp;

import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;

import java.util.EnumMap;
import java.util.Map;

public final class TemporaryStatManager {
    private final Map<CharacterTemporaryStat, Option> stats = new EnumMap<>(CharacterTemporaryStat.class);
    // TODO: scheduler and lock
    private final BitFlag<CharacterTemporaryStat> setStatFlag = new BitFlag<>(CharacterTemporaryStat.FLAG_SIZE);
    private final BitFlag<CharacterTemporaryStat> resetStatFlag = new BitFlag<>(CharacterTemporaryStat.FLAG_SIZE);

    public void encodeForLocal(OutPacket outPacket, boolean complete) {
        final BitFlag<CharacterTemporaryStat> statFlag = complete ? BitFlag.from(stats.keySet(), CharacterTemporaryStat.FLAG_SIZE) : setStatFlag;
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.LOCAL_ENCODE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                stats.get(cts).encode(outPacket);
            }
        }

        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseAtt, new Option()).nOption);
        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseState, new Option()).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.SWALLOW_BUFF) {
            if (statFlag.hasFlag(cts)) {
                outPacket.encodeByte(stats.get(cts).tOption); // tSwallowBuffTime
                break;
            }
        }

        if (statFlag.hasFlag(CharacterTemporaryStat.Dice)) {
            // aDiceInfo
            for (int i = 0; i < 22; i++) {
                outPacket.encodeInt(0);
            }
        }

        if (statFlag.hasFlag(CharacterTemporaryStat.BlessingArmor)) {
            outPacket.encodeInt(stats.get(CharacterTemporaryStat.BlessingArmor).nOption); // nBlessingArmorIncPAD
        }

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                stats.get(cts).encode(outPacket);
            }
        }
    }

    public void encodeForRemote(OutPacket outPacket, boolean complete) {
        final BitFlag<CharacterTemporaryStat> statFlag = complete ? BitFlag.from(stats.keySet(), CharacterTemporaryStat.FLAG_SIZE) : setStatFlag;
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.REMOTE_ENCODE_ORDER) {
            switch (cts) {
                case Speed, ComboCounter, Cyclone -> {
                    outPacket.encodeByte(stats.get(cts).nOption);
                }
                case Morph, Ghost -> {
                    outPacket.encodeShort(stats.get(cts).nOption);
                }
                case SpiritJavelin, RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, MagicShield -> {
                    outPacket.encodeInt(stats.get(cts).nOption);
                }
                case WeaponCharge, Stun, Darkness, Seal, Weakness, ShadowPartner, Attract, BanMap, DojangShield,
                        ReverseInput, RepeatEffect, StopPortion, StopMotion, Fear, Frozen, SuddenDeath, FinalCut,
                        Mechanic, DarkAura, BlueAura, YellowAura -> {
                    outPacket.encodeInt(stats.get(cts).rOption);
                }
                case Poison -> {
                    outPacket.encodeShort(stats.get(cts).nOption); // overwritten with 1
                    outPacket.encodeInt(stats.get(cts).rOption);
                }
            }
        }

        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseAtt, new Option()).nOption);
        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseState, new Option()).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                stats.get(cts).encode(outPacket);
            }
        }
    }
}
