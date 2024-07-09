package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.MiniGameRecordUDT;
import kinoko.world.user.data.MiniGameRecord;

public final class MiniGameRecordCodec extends MappingCodec<UdtValue, MiniGameRecord> {
    public MiniGameRecordCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<MiniGameRecord> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected MiniGameRecord innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final MiniGameRecord miniGameRecord = new MiniGameRecord();
        miniGameRecord.setOmokGameWins(value.getInt(MiniGameRecordUDT.OMOK_WINS));
        miniGameRecord.setOmokGameTies(value.getInt(MiniGameRecordUDT.OMOK_TIES));
        miniGameRecord.setOmokGameLosses(value.getInt(MiniGameRecordUDT.OMOK_LOSSES));
        miniGameRecord.setOmokGameScore(value.getDouble(MiniGameRecordUDT.OMOK_SCORE));
        miniGameRecord.setMemoryGameWins(value.getInt(MiniGameRecordUDT.MEMORY_WINS));
        miniGameRecord.setMemoryGameTies(value.getInt(MiniGameRecordUDT.MEMORY_TIES));
        miniGameRecord.setMemoryGameLosses(value.getInt(MiniGameRecordUDT.MEMORY_LOSSES));
        miniGameRecord.setMemoryGameScore(value.getDouble(MiniGameRecordUDT.MEMORY_SCORE));
        return miniGameRecord;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable MiniGameRecord miniGameRecord) {
        if (miniGameRecord == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(MiniGameRecordUDT.OMOK_WINS, miniGameRecord.getOmokGameWins())
                .setInt(MiniGameRecordUDT.OMOK_TIES, miniGameRecord.getOmokGameTies())
                .setInt(MiniGameRecordUDT.OMOK_LOSSES, miniGameRecord.getOmokGameLosses())
                .setDouble(MiniGameRecordUDT.OMOK_SCORE, miniGameRecord.getOmokGameScore())
                .setInt(MiniGameRecordUDT.MEMORY_WINS, miniGameRecord.getMemoryGameWins())
                .setInt(MiniGameRecordUDT.MEMORY_TIES, miniGameRecord.getMemoryGameTies())
                .setInt(MiniGameRecordUDT.MEMORY_LOSSES, miniGameRecord.getMemoryGameLosses())
                .setDouble(MiniGameRecordUDT.MEMORY_SCORE, miniGameRecord.getMemoryGameScore());
    }
}
