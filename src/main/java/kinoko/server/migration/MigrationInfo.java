package kinoko.server.migration;

import kinoko.meta.SkillId;
import kinoko.server.ServerConfig;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedEnterType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.AvatarLook;
import kinoko.world.user.User;
import kinoko.world.user.stat.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class MigrationInfo implements Encodable {
    private final int channelId;
    private final int accountId;
    private final int characterId;
    private final byte[] machineId;
    private final byte[] clientKey;

    private final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats;
    private final Map<Integer, Instant> schedules;
    private final Map<Integer, List<Summoned>> summoned;
    private final int messengerId;
    private final int effectItemId;
    private final String adBoard;

    private final Instant expireTime;

    public MigrationInfo(
            int channelId,
            int accountId,
            int characterId,
            byte[] machineId,
            byte[] clientKey,
            Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats,
            Map<Integer, Instant> schedules,
            Map<Integer, List<Summoned>> summoned,
            int messengerId,
            int effectItemId,
            String adBoard,
            Instant expireTime) {
        assert machineId.length == 16 && clientKey.length == 8;
        this.channelId = channelId;
        this.accountId = accountId;
        this.characterId = characterId;
        this.machineId = machineId;
        this.clientKey = clientKey;
        this.temporaryStats = temporaryStats;
        this.schedules = schedules;
        this.summoned = summoned;
        this.messengerId = messengerId;
        this.effectItemId = effectItemId;
        this.adBoard = adBoard;
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

    public Map<Integer, Instant> getSchedules() {
        return schedules;
    }

    public Map<Integer, List<Summoned>> getSummoned() {
        return summoned;
    }

    public int getMessengerId() {
        return messengerId;
    }

    public int getEffectItemId() {
        return effectItemId;
    }

    public String getAdBoard() {
        return adBoard;
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
                ", schedules=" + schedules +
                ", summoned=" + summoned +
                ", messengerId=" + messengerId +
                ", effectItemId=" + effectItemId +
                ", adBoard='" + adBoard + '\'' +
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
        encodeSchedules(outPacket, schedules);
        encodeSummonedMap(outPacket, summoned);
        outPacket.encodeInt(messengerId);
        outPacket.encodeInt(effectItemId);
        outPacket.encodeByte(adBoard != null);
        if (adBoard != null) {
            outPacket.encodeString(adBoard);
        }

        outPacket.encodeLong(expireTime.toEpochMilli());
    }

    public static MigrationInfo decode(InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = inPacket.decodeArray(8);

        final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats = decodeTemporaryStats(inPacket);
        final Map<Integer, Instant> schedules = decodeSchedules(inPacket);
        final Map<Integer, List<Summoned>> summoned = decodeSummonedMap(inPacket);
        final int messengerId = inPacket.decodeInt();
        final int effectItemId = inPacket.decodeInt();
        final String adBoard = inPacket.decodeBoolean() ? inPacket.decodeString() : null;

        final Instant expireTime = Instant.ofEpochMilli(inPacket.decodeLong());
        return new MigrationInfo(channelId, accountId, characterId, machineId, clientKey, temporaryStats, schedules, summoned, messengerId, effectItemId, adBoard, expireTime);
    }

    public static MigrationInfo from(User user, int targetChannelId) {
        // Filter summoned
        final Map<Integer, List<Summoned>> summoned = new HashMap<>();
        for (var entry : user.getSummoned().entrySet()) {
            if (SkillConstants.isSummonMigrateSkill(SkillId.fromValue(entry.getKey()))) {
                summoned.put(entry.getKey(), entry.getValue());
            }
        }
        return new MigrationInfo(
                targetChannelId,
                user.getAccountId(),
                user.getCharacterId(),
                user.getClient().getMachineId(),
                user.getClient().getClientKey(),
                user.getSecondaryStat().getTemporaryStats(),
                user.getSchedules(),
                summoned,
                user.getMessengerId(),
                user.getEffectItemId(),
                user.getAdBoard(),
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
                Map.of(),
                Map.of(),
                0,
                0,
                null,
                Instant.now().plus(ServerConfig.CENTRAL_REQUEST_TTL, ChronoUnit.SECONDS)
        );
    }


    // TEMPORARY STATS -------------------------------------------------------------------------------------------------

    private static void encodeTemporaryStats(OutPacket outPacket, Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats) {
        outPacket.encodeInt(temporaryStats.size());
        for (var entry : temporaryStats.entrySet()) {
            final CharacterTemporaryStat cts = entry.getKey();
            final TemporaryStatOption option = entry.getValue();
            outPacket.encodeInt(cts.getValue());
            outPacket.encodeInt(option.nOption);
            outPacket.encodeInt(option.rOption);
            outPacket.encodeInt(option.tOption);
            if (option.tOption != 0) {
                outPacket.encodeLong(option.getExpireTime().toEpochMilli());
            }
            // Extra information
            final TemporaryStatOptionType type = TemporaryStatOptionType.getByCTS(cts);
            outPacket.encodeByte(type.getValue());
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
        final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats = new EnumMap<>(CharacterTemporaryStat.class);
        final int size = inPacket.decodeInt();
        for (int i = 0; i < size; i++) {
            final CharacterTemporaryStat cts = CharacterTemporaryStat.getByValue(inPacket.decodeInt());
            final int nOption = inPacket.decodeInt();
            final int rOption = inPacket.decodeInt();
            final int tOption = inPacket.decodeInt();
            final Instant expireTime = tOption != 0 ? Instant.ofEpochMilli(inPacket.decodeLong()) : Instant.MAX;
            final TemporaryStatOptionType type = TemporaryStatOptionType.getByValue(inPacket.decodeByte());
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


    // SCHEDULES -------------------------------------------------------------------------------------------------------

    private static void encodeSchedules(OutPacket outPacket, Map<Integer, Instant> schedules) {
        outPacket.encodeInt(schedules.size());
        for (var entry : schedules.entrySet()) {
            outPacket.encodeInt(entry.getKey());
            outPacket.encodeLong(entry.getValue().toEpochMilli());
        }
    }

    private static Map<Integer, Instant> decodeSchedules(InPacket inPacket) {
        final Map<Integer, Instant> schedules = new HashMap<>();
        final int size = inPacket.decodeInt();
        for (int i = 0; i < size; i++) {
            final int skillId = inPacket.decodeInt();
            final Instant nextSchedule = Instant.ofEpochMilli(inPacket.decodeLong());
            schedules.put(skillId, nextSchedule);
        }
        return schedules;
    }


    // SUMMONED --------------------------------------------------------------------------------------------------------

    private static void encodeSummonedMap(OutPacket outPacket, Map<Integer, List<Summoned>> summoned) {
        outPacket.encodeInt(summoned.size());
        for (var entry : summoned.entrySet()) {
            outPacket.encodeInt(entry.getKey());
            outPacket.encodeInt(entry.getValue().size());
            for (Summoned s : entry.getValue()) {
                encodeSummoned(outPacket, s);
            }
        }
    }

    private static void encodeSummoned(OutPacket outPacket, Summoned summoned) {
        outPacket.encodeInt(summoned.getSkillId());
        outPacket.encodeInt(summoned.getSkillLevel());
        outPacket.encodeInt(summoned.getHp());
        outPacket.encodeByte(summoned.getMoveAbility().getValue());
        outPacket.encodeByte(summoned.getAssistType().getValue());
        outPacket.encodeByte(summoned.getAvatarLook() != null);
        if (summoned.getAvatarLook() != null) {
            summoned.getAvatarLook().encode(outPacket);
        }
        outPacket.encodeLong(summoned.getExpireTime().getEpochSecond());
    }

    private static Map<Integer, List<Summoned>> decodeSummonedMap(InPacket inPacket) {
        final Map<Integer, List<Summoned>> summoned = new HashMap<>();
        final int size = inPacket.decodeInt();
        for (int i = 0; i < size; i++) {
            final int skillId = inPacket.decodeInt();
            final int summonedCount = inPacket.decodeInt();
            final List<Summoned> summonedList = new ArrayList<>(summonedCount);
            for (int j = 0; j < summonedCount; j++) {
                summonedList.add(decodeSummoned(inPacket));
            }
            summoned.put(skillId, summonedList);
        }
        return summoned;
    }

    private static Summoned decodeSummoned(InPacket inPacket) {
        final int skillId = inPacket.decodeInt();
        final int skillLevel = inPacket.decodeInt();
        final int summonedHp = inPacket.decodeInt();
        final SummonedMoveAbility moveAbility = SummonedMoveAbility.getByValue(inPacket.decodeByte());
        final SummonedAssistType assistType = SummonedAssistType.getByValue(inPacket.decodeByte());
        final AvatarLook avatarLook = inPacket.decodeBoolean() ? AvatarLook.decode(inPacket) : null;
        final Instant expireTime = Instant.ofEpochSecond(inPacket.decodeLong());
        final Summoned summoned = new Summoned(skillId, skillLevel, moveAbility, assistType, avatarLook, expireTime);
        summoned.setHp(summonedHp);
        summoned.setEnterType(SummonedEnterType.DEFAULT);
        return summoned;
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
