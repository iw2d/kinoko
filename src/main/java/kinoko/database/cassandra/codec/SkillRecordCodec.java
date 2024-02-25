package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.SkillRecordUDT;
import kinoko.world.skill.SkillRecord;

public final class SkillRecordCodec extends MappingCodec<UdtValue, SkillRecord> {
    public SkillRecordCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<SkillRecord> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected SkillRecord innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final int skillId = value.getInt(SkillRecordUDT.SKILL_ID);
        final SkillRecord sr = new SkillRecord(skillId);
        sr.setSkillLevel(value.getInt(SkillRecordUDT.SKILL_LEVEL));
        sr.setMasterLevel(value.getInt(SkillRecordUDT.MASTER_LEVEL));
        return sr;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable SkillRecord sr) {
        if (sr == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(SkillRecordUDT.SKILL_ID, sr.getSkillId())
                .setInt(SkillRecordUDT.SKILL_LEVEL, sr.getSkillLevel())
                .setInt(SkillRecordUDT.MASTER_LEVEL, sr.getMasterLevel());
    }
}
