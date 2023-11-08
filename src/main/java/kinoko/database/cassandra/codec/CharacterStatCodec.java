package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.world.user.CharacterStat;
import kinoko.world.user.ExtendSP;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;
import static kinoko.database.cassandra.model.CharacterStatModel.*;

public final class CharacterStatCodec extends MappingCodec<UdtValue, CharacterStat> {
    public CharacterStatCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<CharacterStat> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected CharacterStat innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final CharacterStat cs = new CharacterStat();
        cs.setGender(value.getByte(GENDER.getName()));
        cs.setSkin(value.getByte(SKIN.getName()));
        cs.setFace(value.getInt(FACE.getName()));
        cs.setHair(value.getInt(HAIR.getName()));
        cs.setLevel(value.getByte(LEVEL.getName()));
        cs.setJob(value.getShort(JOB.getName()));
        cs.setSubJob(value.getShort(SUB_JOB.getName()));
        cs.setBaseStr(value.getShort(BASE_STR.getName()));
        cs.setBaseDex(value.getShort(BASE_DEX.getName()));
        cs.setBaseInt(value.getShort(BASE_INT.getName()));
        cs.setBaseLuk(value.getShort(BASE_LUK.getName()));
        cs.setHp(value.getInt(HP.getName()));
        cs.setMaxHp(value.getInt(MAX_HP.getName()));
        cs.setMp(value.getInt(MP.getName()));
        cs.setMaxMp(value.getInt(MAX_MP.getName()));
        cs.setAp(value.getShort(AP.getName()));
        cs.setSp(ExtendSP.from(cs, value.getList(SP.getName(), Integer.class)));
        cs.setExp(value.getInt(EXP.getName()));
        cs.setPop(value.getShort(POP.getName()));
        cs.setPosMap(value.getInt(POS_MAP.getName()));
        cs.setPortal(value.getByte(PORTAL.getName()));
        return cs;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable CharacterStat cs) {
        if (cs == null) {
            return null;
        }
        return getCqlType().newValue()
                .setByte(GENDER.getName(), cs.getGender())
                .setByte(SKIN.getName(), cs.getSkin())
                .setInt(FACE.getName(), cs.getFace())
                .setInt(HAIR.getName(), cs.getHair())
                .setByte(LEVEL.getName(), cs.getLevel())
                .setShort(JOB.getName(), cs.getJob())
                .setShort(SUB_JOB.getName(), cs.getSubJob())
                .setShort(BASE_STR.getName(), cs.getBaseStr())
                .setShort(BASE_DEX.getName(), cs.getBaseDex())
                .setShort(BASE_INT.getName(), cs.getBaseInt())
                .setShort(BASE_LUK.getName(), cs.getBaseLuk())
                .setInt(HP.getName(), cs.getHp())
                .setInt(MAX_HP.getName(), cs.getMaxHp())
                .setInt(MP.getName(), cs.getMp())
                .setInt(MAX_MP.getName(), cs.getMaxMp())
                .setShort(AP.getName(), cs.getAp())
                .setList(SP.getName(), cs.getSp().getSpList(), Integer.class)
                .setInt(EXP.getName(), cs.getExp())
                .setShort(POP.getName(), cs.getPop())
                .setInt(POS_MAP.getName(), cs.getPosMap())
                .setByte(PORTAL.getName(), cs.getPortal());
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                createType(keyspace, TYPE_NAME)
                        .ifNotExists()
                        .withField(GENDER.getName(), DataTypes.TINYINT)
                        .withField(SKIN.getName(), DataTypes.TINYINT)
                        .withField(FACE.getName(), DataTypes.INT)
                        .withField(HAIR.getName(), DataTypes.INT)
                        .withField(LEVEL.getName(), DataTypes.TINYINT)
                        .withField(JOB.getName(), DataTypes.SMALLINT)
                        .withField(SUB_JOB.getName(), DataTypes.SMALLINT)
                        .withField(BASE_STR.getName(), DataTypes.SMALLINT)
                        .withField(BASE_DEX.getName(), DataTypes.SMALLINT)
                        .withField(BASE_INT.getName(), DataTypes.SMALLINT)
                        .withField(BASE_LUK.getName(), DataTypes.SMALLINT)
                        .withField(HP.getName(), DataTypes.INT)
                        .withField(MAX_HP.getName(), DataTypes.INT)
                        .withField(MP.getName(), DataTypes.INT)
                        .withField(MAX_MP.getName(), DataTypes.INT)
                        .withField(AP.getName(), DataTypes.SMALLINT)
                        .withField(SP.getName(), DataTypes.frozenListOf(DataTypes.INT))
                        .withField(EXP.getName(), DataTypes.INT)
                        .withField(POP.getName(), DataTypes.SMALLINT)
                        .withField(POS_MAP.getName(), DataTypes.INT)
                        .withField(PORTAL.getName(), DataTypes.TINYINT)
                        .build()
        );
    }
}
