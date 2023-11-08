package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.world.item.PetInfo;

import static kinoko.database.cassandra.model.PetInfoModel.*;

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
        info.setPetName(value.getString(PET_NAME.getName()));
        info.setLevel(value.getByte(LEVEL.getName()));
        info.setFullness(value.getByte(FULLNESS.getName()));
        info.setTameness(value.getShort(TAMENESS.getName()));
        info.setPetSkill(value.getShort(PET_SKILL.getName()));
        info.setPetAttribute(value.getShort(PET_ATTRIBUTE.getName()));
        info.setRemainLife(value.getInt(REMAIN_LIFE.getName()));
        return info;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable PetInfo info) {
        if (info == null) {
            return null;
        }
        return getCqlType().newValue()
                .setString(PET_NAME.getName(), info.getPetName())
                .setByte(LEVEL.getName(), info.getLevel())
                .setByte(FULLNESS.getName(), info.getFullness())
                .setShort(TAMENESS.getName(), info.getTameness())
                .setShort(PET_SKILL.getName(), info.getPetSkill())
                .setShort(PET_ATTRIBUTE.getName(), info.getPetAttribute())
                .setInt(REMAIN_LIFE.getName(), info.getRemainLife());
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, TYPE_NAME)
                        .ifNotExists()
                        .withField(PET_NAME.getName(), DataTypes.TEXT)
                        .withField(LEVEL.getName(), DataTypes.TINYINT)
                        .withField(FULLNESS.getName(), DataTypes.TINYINT)
                        .withField(TAMENESS.getName(), DataTypes.SMALLINT)
                        .withField(PET_SKILL.getName(), DataTypes.SMALLINT)
                        .withField(PET_ATTRIBUTE.getName(), DataTypes.SMALLINT)
                        .withField(REMAIN_LIFE.getName(), DataTypes.INT)
                        .build()
        );
    }
}
