package kinoko.server.node;

import kinoko.server.ServerConfig;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.Encodable;
import kinoko.world.user.User;
import kinoko.world.user.stat.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class MigrationInfo implements Encodable {
    private final int channelId;
    private final int accountId;
    private final int characterId;
    private final byte[] machineId;
    private final byte[] clientKey;
    private final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats;
    private final Instant expireTime;

    public MigrationInfo(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey, Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats, Instant expireTime) {
        assert machineId.length == 16 && clientKey.length == 8;
        this.channelId = channelId;
        this.accountId = accountId;
        this.characterId = characterId;
        this.machineId = machineId;
        this.clientKey = clientKey;
        this.temporaryStats = temporaryStats;
        this.expireTime = expireTime;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public byte[] getMachineId() {
        return machineId;
    }

    public byte[] getClientKey() {
        return clientKey;
    }

    public Map<CharacterTemporaryStat, TemporaryStatOption> getTemporaryStats() {
        return temporaryStats;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expireTime);
    }

    public boolean verify(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        return this.channelId == channelId && this.accountId == accountId && this.characterId == characterId &&
                Arrays.equals(this.machineId, machineId) && Arrays.equals(this.clientKey, clientKey);
    }

    @Override
    public String toString() {
        return "MigrationInfo{" +
                "channelId=" + channelId +
                ", accountId=" + accountId +
                ", characterId=" + characterId +
                ", machineId=" + Arrays.toString(machineId) +
                ", clientKey=" + Arrays.toString(clientKey) +
                ", temporaryStats=" + temporaryStats +
                ", expireTime=" + expireTime +
                '}';
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(channelId);
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeArray(machineId);
        outPacket.encodeArray(clientKey);
        encodeTemporaryStats(outPacket, temporaryStats);
        outPacket.encodeLong(expireTime.toEpochMilli());
    }

    public static MigrationInfo decode(InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = inPacket.decodeArray(8);
        final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats = decodeTemporaryStats(inPacket);
        final long expireTime = inPacket.decodeLong();
        return new MigrationInfo(channelId, accountId, characterId, machineId, clientKey, temporaryStats, Instant.ofEpochMilli(expireTime));
    }

    public static MigrationInfo from(User user, int targetChannelId) {
        return new MigrationInfo(
                targetChannelId,
                user.getAccountId(),
                user.getCharacterId(),
                user.getClient().getMachineId(),
                user.getClient().getClientKey(),
                user.getSecondaryStat().getTemporaryStats(),
                Instant.now().plus(ServerConfig.CENTRAL_REQUEST_TTL, ChronoUnit.SECONDS)
        );
    }

    public static MigrationInfo from(int targetChannelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        return new MigrationInfo(
                targetChannelId,
                accountId,
                characterId,
                machineId,
                clientKey,
                Map.of(),
                Instant.now().plus(ServerConfig.CENTRAL_REQUEST_TTL, ChronoUnit.SECONDS)
        );
    }


    // TEMPORARY STATS -------------------------------------------------------------------------------------------------

    private static void encodeTemporaryStats(OutPacket outPacket, Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats) {
        final BitFlag<CharacterTemporaryStat> statFlag = BitFlag.from(temporaryStats.keySet(), CharacterTemporaryStat.FLAG_SIZE);
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.values()) {
            if (!statFlag.hasFlag(cts)) {
                continue;
            }
            // Option type
            final TemporaryStatOptionType type = TemporaryStatOptionType.getByCTS(cts);
            outPacket.encodeByte(type.getValue());
            // Option values
            final TemporaryStatOption option = temporaryStats.get(cts);
            outPacket.encodeInt(option.nOption);
            outPacket.encodeInt(option.rOption);
            outPacket.encodeInt(option.tOption);
            outPacket.encodeLong(option.getExpireTime().toEpochMilli());
            // Extra information
            switch (type) {
                case DICE_INFO -> {
                    option.getDiceInfo().encode(outPacket);
                }
                case TWO_STATE -> {
                    final TwoStateTemporaryStat twoStateOption = (TwoStateTemporaryStat) option;
                    outPacket.encodeByte(twoStateOption.getType().getValue());
                }
                case GUIDED_BULLET -> {
                    final GuidedBullet guidedBullet = (GuidedBullet) option;
                    outPacket.encodeInt(guidedBullet.getMobId());
                }
            }
        }
    }

    private static Map<CharacterTemporaryStat, TemporaryStatOption> decodeTemporaryStats(InPacket inPacket) {
        final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats = new HashMap<>();
        final BitFlag<CharacterTemporaryStat> statFlag = BitFlag.decode(inPacket, CharacterTemporaryStat.FLAG_SIZE);
        for (CharacterTemporaryStat cts : CharacterTemporaryStat.values()) {
            if (!statFlag.hasFlag(cts)) {
                continue;
            }
            final TemporaryStatOptionType type = TemporaryStatOptionType.getByValue(inPacket.decodeByte());
            final int nOption = inPacket.decodeInt();
            final int rOption = inPacket.decodeInt();
            final int tOption = inPacket.decodeInt();
            final Instant expireTime = Instant.ofEpochMilli(inPacket.decodeLong());
            switch (type) {
                case NORMAL -> {
                    temporaryStats.put(cts, new TemporaryStatOption(nOption, rOption, tOption, expireTime));
                }
                case DICE_INFO -> {
                    final DiceInfo diceInfo = DiceInfo.decode(inPacket);
                    temporaryStats.put(cts, new TemporaryStatOption(nOption, rOption, tOption, diceInfo, expireTime));
                }
                case TWO_STATE -> {
                    final TwoStateType twoStateType = TwoStateType.getByValue(inPacket.decodeByte());
                    temporaryStats.put(cts, new TwoStateTemporaryStat(twoStateType, nOption, rOption, tOption, expireTime));
                }
                case GUIDED_BULLET -> {
                    final int mobId = inPacket.decodeInt();
                    temporaryStats.put(cts, new GuidedBullet(nOption, rOption, tOption, mobId)); // NO_EXPIRE
                }
                case null -> {
                    throw new IllegalStateException("Received unknown temporary stat option type while decoding migration info");
                }
            }
        }
        return temporaryStats;
    }

    private enum TemporaryStatOptionType {
        NORMAL,
        DICE_INFO,
        TWO_STATE,
        GUIDED_BULLET;

        public final int getValue() {
            return ordinal();
        }

        public static TemporaryStatOptionType getByValue(int value) {
            for (TemporaryStatOptionType type : values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }

        public static TemporaryStatOptionType getByCTS(CharacterTemporaryStat cts) {
            if (cts == CharacterTemporaryStat.Dice) {
                return DICE_INFO;
            }
            if (cts == CharacterTemporaryStat.GuidedBullet) {
                return GUIDED_BULLET;
            }
            if (CharacterTemporaryStat.TWO_STATE_ORDER.contains(cts)) {
                return TWO_STATE;
            }
            return NORMAL;
        }
    }
}
