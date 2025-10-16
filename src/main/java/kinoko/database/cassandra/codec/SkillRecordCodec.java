package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.SkillRecordUDT;
import kinoko.meta.SkillId;
import kinoko.world.skill.SkillRecord;

public final class SkillRecordCodec extends MappingCodec<UdtValue, SkillRecord> {
    public SkillRecordCodec(TypeCodec<UdtValue> innerCodec, GenericType<SkillRecord> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected SkillRecord innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final SkillId skillId = SkillId.fromValue(value.getInt(SkillRecordUDT.SKILL_ID));
        final SkillRecord sr = new SkillRecord(skillId);
        sr.setSkillLevel(value.getInt(SkillRecordUDT.SKILL_LEVEL));
        sr.setMasterLevel(value.getInt(SkillRecordUDT.MASTER_LEVEL));
        return sr;
    }

    @Override
    protected UdtValue outerToInner(SkillRecord sr) {
        if (sr == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(SkillRecordUDT.SKILL_ID, sr.getSkillId().getId())
                .setInt(SkillRecordUDT.SKILL_LEVEL, sr.getSkillLevel())
                .setInt(SkillRecordUDT.MASTER_LEVEL, sr.getMasterLevel());
    }
}
