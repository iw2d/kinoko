package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.PetDataUDT;
import kinoko.world.item.PetData;

public final class PetDataCodec extends MappingCodec<UdtValue, PetData> {
    public PetDataCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<PetData> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected PetData innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final PetData info = new PetData();
        info.setPetName(value.getString(PetDataUDT.PET_NAME));
        info.setLevel(value.getByte(PetDataUDT.LEVEL));
        info.setFullness(value.getByte(PetDataUDT.FULLNESS));
        info.setTameness(value.getShort(PetDataUDT.TAMENESS));
        info.setPetSkill(value.getShort(PetDataUDT.PET_SKILL));
        info.setPetAttribute(value.getShort(PetDataUDT.PET_ATTRIBUTE));
        info.setRemainLife(value.getInt(PetDataUDT.REMAIN_LIFE));
        return info;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable PetData info) {
        if (info == null) {
            return null;
        }
        return getCqlType().newValue()
                .setString(PetDataUDT.PET_NAME, info.getPetName())
                .setByte(PetDataUDT.LEVEL, info.getLevel())
                .setByte(PetDataUDT.FULLNESS, info.getFullness())
                .setShort(PetDataUDT.TAMENESS, info.getTameness())
                .setShort(PetDataUDT.PET_SKILL, info.getPetSkill())
                .setShort(PetDataUDT.PET_ATTRIBUTE, info.getPetAttribute())
                .setInt(PetDataUDT.REMAIN_LIFE, info.getRemainLife());
    }
}
