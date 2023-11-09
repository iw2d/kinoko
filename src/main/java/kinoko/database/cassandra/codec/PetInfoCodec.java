package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.PetInfoUDT;
import kinoko.world.item.PetInfo;

public final class PetInfoCodec extends MappingCodec<UdtValue, PetInfo> {
    public PetInfoCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<PetInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected PetInfo innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final PetInfo info = new PetInfo();
        info.setPetName(value.getString(PetInfoUDT.PET_NAME));
        info.setLevel(value.getByte(PetInfoUDT.LEVEL));
        info.setFullness(value.getByte(PetInfoUDT.FULLNESS));
        info.setTameness(value.getShort(PetInfoUDT.TAMENESS));
        info.setPetSkill(value.getShort(PetInfoUDT.PET_SKILL));
        info.setPetAttribute(value.getShort(PetInfoUDT.PET_ATTRIBUTE));
        info.setRemainLife(value.getInt(PetInfoUDT.REMAIN_LIFE));
        return info;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable PetInfo info) {
        if (info == null) {
            return null;
        }
        return getCqlType().newValue()
                .setString(PetInfoUDT.PET_NAME, info.getPetName())
                .setByte(PetInfoUDT.LEVEL, info.getLevel())
                .setByte(PetInfoUDT.FULLNESS, info.getFullness())
                .setShort(PetInfoUDT.TAMENESS, info.getTameness())
                .setShort(PetInfoUDT.PET_SKILL, info.getPetSkill())
                .setShort(PetInfoUDT.PET_ATTRIBUTE, info.getPetAttribute())
                .setInt(PetInfoUDT.REMAIN_LIFE, info.getRemainLife());
    }
}
