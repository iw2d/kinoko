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
import kinoko.world.item.EquipInfo;

import static kinoko.database.cassandra.model.EquipInfoModel.*;

public final class EquipInfoCodec extends MappingCodec<UdtValue, EquipInfo> {
    public EquipInfoCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<EquipInfo> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected EquipInfo innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final EquipInfo info = new EquipInfo();
        info.setIncStr(value.getShort(INC_STR.getName()));
        info.setIncDex(value.getShort(INC_DEX.getName()));
        info.setIncInt(value.getShort(INC_INT.getName()));
        info.setIncLuk(value.getShort(INC_LUK.getName()));
        info.setIncMaxHp(value.getShort(INC_MAX_HP.getName()));
        info.setIncMaxMp(value.getShort(INC_MAX_MP.getName()));
        info.setIncPad(value.getShort(INC_PAD.getName()));
        info.setIncMad(value.getShort(INC_MAD.getName()));
        info.setIncPdd(value.getShort(INC_PDD.getName()));
        info.setIncMdd(value.getShort(INC_MDD.getName()));
        info.setIncAcc(value.getShort(INC_ACC.getName()));
        info.setIncEva(value.getShort(INC_EVA.getName()));
        info.setIncCraft(value.getShort(INC_CRAFT.getName()));
        info.setIncSpeed(value.getShort(INC_SPEED.getName()));
        info.setIncJump(value.getShort(INC_JUMP.getName()));
        info.setRuc(value.getByte(RUC.getName()));
        info.setCuc(value.getByte(CUC.getName()));
        info.setIuc(value.getInt(IUC.getName()));
        info.setChuc(value.getByte(CHUC.getName()));
        info.setGrade(value.getByte(GRADE.getName()));
        info.setOption1(value.getShort(OPTION_1.getName()));
        info.setOption2(value.getShort(OPTION_2.getName()));
        info.setOption3(value.getShort(OPTION_3.getName()));
        info.setSocket1(value.getShort(SOCKET_1.getName()));
        info.setSocket2(value.getShort(SOCKET_2.getName()));
        info.setLevelUpType(value.getByte(LEVEL_UP_TYPE.getName()));
        info.setLevel(value.getByte(LEVEL.getName()));
        info.setExp(value.getInt(EXP.getName()));
        info.setDurability(value.getInt(DURABILITY.getName()));
        return info;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable EquipInfo info) {
        if (info == null) {
            return null;
        }
        return getCqlType().newValue()
                .setShort(INC_STR.getName(), info.getIncStr())
                .setShort(INC_DEX.getName(), info.getIncDex())
                .setShort(INC_INT.getName(), info.getIncInt())
                .setShort(INC_LUK.getName(), info.getIncLuk())
                .setShort(INC_MAX_HP.getName(), info.getIncMaxHp())
                .setShort(INC_MAX_MP.getName(), info.getIncMaxMp())
                .setShort(INC_PAD.getName(), info.getIncPad())
                .setShort(INC_MAD.getName(), info.getIncMad())
                .setShort(INC_PDD.getName(), info.getIncPdd())
                .setShort(INC_MDD.getName(), info.getIncMdd())
                .setShort(INC_ACC.getName(), info.getIncAcc())
                .setShort(INC_EVA.getName(), info.getIncEva())
                .setShort(INC_CRAFT.getName(), info.getIncCraft())
                .setShort(INC_SPEED.getName(), info.getIncSpeed())
                .setShort(INC_JUMP.getName(), info.getIncJump())
                .setByte(RUC.getName(), info.getRuc())
                .setByte(CUC.getName(), info.getCuc())
                .setInt(IUC.getName(), info.getIuc())
                .setByte(CHUC.getName(), info.getChuc())
                .setByte(GRADE.getName(), info.getGrade())
                .setShort(OPTION_1.getName(), info.getOption1())
                .setShort(OPTION_2.getName(), info.getOption2())
                .setShort(OPTION_3.getName(), info.getOption3())
                .setShort(SOCKET_1.getName(), info.getSocket1())
                .setShort(SOCKET_2.getName(), info.getSocket2())
                .setByte(LEVEL_UP_TYPE.getName(), info.getLevelUpType())
                .setByte(LEVEL.getName(), info.getLevel())
                .setInt(EXP.getName(), info.getExp())
                .setInt(DURABILITY.getName(), info.getDurability());
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, TYPE_NAME).ifNotExists()
                        .withField(INC_STR.getName(), DataTypes.SMALLINT)
                        .withField(INC_DEX.getName(), DataTypes.SMALLINT)
                        .withField(INC_INT.getName(), DataTypes.SMALLINT)
                        .withField(INC_LUK.getName(), DataTypes.SMALLINT)
                        .withField(INC_MAX_HP.getName(), DataTypes.SMALLINT)
                        .withField(INC_MAX_MP.getName(), DataTypes.SMALLINT)
                        .withField(INC_PAD.getName(), DataTypes.SMALLINT)
                        .withField(INC_MAD.getName(), DataTypes.SMALLINT)
                        .withField(INC_PDD.getName(), DataTypes.SMALLINT)
                        .withField(INC_MDD.getName(), DataTypes.SMALLINT)
                        .withField(INC_ACC.getName(), DataTypes.SMALLINT)
                        .withField(INC_EVA.getName(), DataTypes.SMALLINT)
                        .withField(INC_CRAFT.getName(), DataTypes.SMALLINT)
                        .withField(INC_SPEED.getName(), DataTypes.SMALLINT)
                        .withField(INC_JUMP.getName(), DataTypes.SMALLINT)
                        .withField(RUC.getName(), DataTypes.TINYINT)
                        .withField(CUC.getName(), DataTypes.TINYINT)
                        .withField(IUC.getName(), DataTypes.INT)
                        .withField(CHUC.getName(), DataTypes.TINYINT)
                        .withField(GRADE.getName(), DataTypes.TINYINT)
                        .withField(OPTION_1.getName(), DataTypes.SMALLINT)
                        .withField(OPTION_2.getName(), DataTypes.SMALLINT)
                        .withField(OPTION_3.getName(), DataTypes.SMALLINT)
                        .withField(SOCKET_1.getName(), DataTypes.SMALLINT)
                        .withField(SOCKET_2.getName(), DataTypes.SMALLINT)
                        .withField(LEVEL_UP_TYPE.getName(), DataTypes.TINYINT)
                        .withField(LEVEL.getName(), DataTypes.TINYINT)
                        .withField(EXP.getName(), DataTypes.INT)
                        .withField(DURABILITY.getName(), DataTypes.INT)
                        .build()
        );
    }
}
