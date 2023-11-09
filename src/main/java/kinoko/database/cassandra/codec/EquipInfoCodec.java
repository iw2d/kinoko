package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.EquipInfoUDT;
import kinoko.world.item.EquipInfo;

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
        info.setIncStr(value.getShort(EquipInfoUDT.INC_STR));
        info.setIncDex(value.getShort(EquipInfoUDT.INC_DEX));
        info.setIncInt(value.getShort(EquipInfoUDT.INC_INT));
        info.setIncLuk(value.getShort(EquipInfoUDT.INC_LUK));
        info.setIncMaxHp(value.getShort(EquipInfoUDT.INC_MAX_HP));
        info.setIncMaxMp(value.getShort(EquipInfoUDT.INC_MAX_MP));
        info.setIncPad(value.getShort(EquipInfoUDT.INC_PAD));
        info.setIncMad(value.getShort(EquipInfoUDT.INC_MAD));
        info.setIncPdd(value.getShort(EquipInfoUDT.INC_PDD));
        info.setIncMdd(value.getShort(EquipInfoUDT.INC_MDD));
        info.setIncAcc(value.getShort(EquipInfoUDT.INC_ACC));
        info.setIncEva(value.getShort(EquipInfoUDT.INC_EVA));
        info.setIncCraft(value.getShort(EquipInfoUDT.INC_CRAFT));
        info.setIncSpeed(value.getShort(EquipInfoUDT.INC_SPEED));
        info.setIncJump(value.getShort(EquipInfoUDT.INC_JUMP));
        info.setRuc(value.getByte(EquipInfoUDT.RUC));
        info.setCuc(value.getByte(EquipInfoUDT.CUC));
        info.setIuc(value.getInt(EquipInfoUDT.IUC));
        info.setChuc(value.getByte(EquipInfoUDT.CHUC));
        info.setGrade(value.getByte(EquipInfoUDT.GRADE));
        info.setOption1(value.getShort(EquipInfoUDT.OPTION_1));
        info.setOption2(value.getShort(EquipInfoUDT.OPTION_2));
        info.setOption3(value.getShort(EquipInfoUDT.OPTION_3));
        info.setSocket1(value.getShort(EquipInfoUDT.SOCKET_1));
        info.setSocket2(value.getShort(EquipInfoUDT.SOCKET_2));
        info.setLevelUpType(value.getByte(EquipInfoUDT.LEVEL_UP_TYPE));
        info.setLevel(value.getByte(EquipInfoUDT.LEVEL));
        info.setExp(value.getInt(EquipInfoUDT.EXP));
        info.setDurability(value.getInt(EquipInfoUDT.DURABILITY));
        return info;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable EquipInfo info) {
        if (info == null) {
            return null;
        }
        return getCqlType().newValue()
                .setShort(EquipInfoUDT.INC_STR, info.getIncStr())
                .setShort(EquipInfoUDT.INC_DEX, info.getIncDex())
                .setShort(EquipInfoUDT.INC_INT, info.getIncInt())
                .setShort(EquipInfoUDT.INC_LUK, info.getIncLuk())
                .setShort(EquipInfoUDT.INC_MAX_HP, info.getIncMaxHp())
                .setShort(EquipInfoUDT.INC_MAX_MP, info.getIncMaxMp())
                .setShort(EquipInfoUDT.INC_PAD, info.getIncPad())
                .setShort(EquipInfoUDT.INC_MAD, info.getIncMad())
                .setShort(EquipInfoUDT.INC_PDD, info.getIncPdd())
                .setShort(EquipInfoUDT.INC_MDD, info.getIncMdd())
                .setShort(EquipInfoUDT.INC_ACC, info.getIncAcc())
                .setShort(EquipInfoUDT.INC_EVA, info.getIncEva())
                .setShort(EquipInfoUDT.INC_CRAFT, info.getIncCraft())
                .setShort(EquipInfoUDT.INC_SPEED, info.getIncSpeed())
                .setShort(EquipInfoUDT.INC_JUMP, info.getIncJump())
                .setByte(EquipInfoUDT.RUC, info.getRuc())
                .setByte(EquipInfoUDT.CUC, info.getCuc())
                .setInt(EquipInfoUDT.IUC, info.getIuc())
                .setByte(EquipInfoUDT.CHUC, info.getChuc())
                .setByte(EquipInfoUDT.GRADE, info.getGrade())
                .setShort(EquipInfoUDT.OPTION_1, info.getOption1())
                .setShort(EquipInfoUDT.OPTION_2, info.getOption2())
                .setShort(EquipInfoUDT.OPTION_3, info.getOption3())
                .setShort(EquipInfoUDT.SOCKET_1, info.getSocket1())
                .setShort(EquipInfoUDT.SOCKET_2, info.getSocket2())
                .setByte(EquipInfoUDT.LEVEL_UP_TYPE, info.getLevelUpType())
                .setByte(EquipInfoUDT.LEVEL, info.getLevel())
                .setInt(EquipInfoUDT.EXP, info.getExp())
                .setInt(EquipInfoUDT.DURABILITY, info.getDurability());
    }
}
