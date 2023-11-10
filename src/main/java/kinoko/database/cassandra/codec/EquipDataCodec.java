package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.EquipDataUDT;
import kinoko.world.item.EquipData;

public final class EquipDataCodec extends MappingCodec<UdtValue, EquipData> {
    public EquipDataCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<EquipData> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected EquipData innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final EquipData info = new EquipData();
        info.setIncStr(value.getShort(EquipDataUDT.INC_STR));
        info.setIncDex(value.getShort(EquipDataUDT.INC_DEX));
        info.setIncInt(value.getShort(EquipDataUDT.INC_INT));
        info.setIncLuk(value.getShort(EquipDataUDT.INC_LUK));
        info.setIncMaxHp(value.getShort(EquipDataUDT.INC_MAX_HP));
        info.setIncMaxMp(value.getShort(EquipDataUDT.INC_MAX_MP));
        info.setIncPad(value.getShort(EquipDataUDT.INC_PAD));
        info.setIncMad(value.getShort(EquipDataUDT.INC_MAD));
        info.setIncPdd(value.getShort(EquipDataUDT.INC_PDD));
        info.setIncMdd(value.getShort(EquipDataUDT.INC_MDD));
        info.setIncAcc(value.getShort(EquipDataUDT.INC_ACC));
        info.setIncEva(value.getShort(EquipDataUDT.INC_EVA));
        info.setIncCraft(value.getShort(EquipDataUDT.INC_CRAFT));
        info.setIncSpeed(value.getShort(EquipDataUDT.INC_SPEED));
        info.setIncJump(value.getShort(EquipDataUDT.INC_JUMP));
        info.setRuc(value.getByte(EquipDataUDT.RUC));
        info.setCuc(value.getByte(EquipDataUDT.CUC));
        info.setIuc(value.getInt(EquipDataUDT.IUC));
        info.setChuc(value.getByte(EquipDataUDT.CHUC));
        info.setGrade(value.getByte(EquipDataUDT.GRADE));
        info.setOption1(value.getShort(EquipDataUDT.OPTION_1));
        info.setOption2(value.getShort(EquipDataUDT.OPTION_2));
        info.setOption3(value.getShort(EquipDataUDT.OPTION_3));
        info.setSocket1(value.getShort(EquipDataUDT.SOCKET_1));
        info.setSocket2(value.getShort(EquipDataUDT.SOCKET_2));
        info.setLevelUpType(value.getByte(EquipDataUDT.LEVEL_UP_TYPE));
        info.setLevel(value.getByte(EquipDataUDT.LEVEL));
        info.setExp(value.getInt(EquipDataUDT.EXP));
        info.setDurability(value.getInt(EquipDataUDT.DURABILITY));
        return info;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable EquipData info) {
        if (info == null) {
            return null;
        }
        return getCqlType().newValue()
                .setShort(EquipDataUDT.INC_STR, info.getIncStr())
                .setShort(EquipDataUDT.INC_DEX, info.getIncDex())
                .setShort(EquipDataUDT.INC_INT, info.getIncInt())
                .setShort(EquipDataUDT.INC_LUK, info.getIncLuk())
                .setShort(EquipDataUDT.INC_MAX_HP, info.getIncMaxHp())
                .setShort(EquipDataUDT.INC_MAX_MP, info.getIncMaxMp())
                .setShort(EquipDataUDT.INC_PAD, info.getIncPad())
                .setShort(EquipDataUDT.INC_MAD, info.getIncMad())
                .setShort(EquipDataUDT.INC_PDD, info.getIncPdd())
                .setShort(EquipDataUDT.INC_MDD, info.getIncMdd())
                .setShort(EquipDataUDT.INC_ACC, info.getIncAcc())
                .setShort(EquipDataUDT.INC_EVA, info.getIncEva())
                .setShort(EquipDataUDT.INC_CRAFT, info.getIncCraft())
                .setShort(EquipDataUDT.INC_SPEED, info.getIncSpeed())
                .setShort(EquipDataUDT.INC_JUMP, info.getIncJump())
                .setByte(EquipDataUDT.RUC, info.getRuc())
                .setByte(EquipDataUDT.CUC, info.getCuc())
                .setInt(EquipDataUDT.IUC, info.getIuc())
                .setByte(EquipDataUDT.CHUC, info.getChuc())
                .setByte(EquipDataUDT.GRADE, info.getGrade())
                .setShort(EquipDataUDT.OPTION_1, info.getOption1())
                .setShort(EquipDataUDT.OPTION_2, info.getOption2())
                .setShort(EquipDataUDT.OPTION_3, info.getOption3())
                .setShort(EquipDataUDT.SOCKET_1, info.getSocket1())
                .setShort(EquipDataUDT.SOCKET_2, info.getSocket2())
                .setByte(EquipDataUDT.LEVEL_UP_TYPE, info.getLevelUpType())
                .setByte(EquipDataUDT.LEVEL, info.getLevel())
                .setInt(EquipDataUDT.EXP, info.getExp())
                .setInt(EquipDataUDT.DURABILITY, info.getDurability());
    }
}
