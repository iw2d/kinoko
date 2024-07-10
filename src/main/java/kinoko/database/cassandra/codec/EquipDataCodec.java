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
        final EquipData equipData = new EquipData();
        equipData.setIncStr(value.getShort(EquipDataUDT.INC_STR));
        equipData.setIncDex(value.getShort(EquipDataUDT.INC_DEX));
        equipData.setIncInt(value.getShort(EquipDataUDT.INC_INT));
        equipData.setIncLuk(value.getShort(EquipDataUDT.INC_LUK));
        equipData.setIncMaxHp(value.getShort(EquipDataUDT.INC_MAX_HP));
        equipData.setIncMaxMp(value.getShort(EquipDataUDT.INC_MAX_MP));
        equipData.setIncPad(value.getShort(EquipDataUDT.INC_PAD));
        equipData.setIncMad(value.getShort(EquipDataUDT.INC_MAD));
        equipData.setIncPdd(value.getShort(EquipDataUDT.INC_PDD));
        equipData.setIncMdd(value.getShort(EquipDataUDT.INC_MDD));
        equipData.setIncAcc(value.getShort(EquipDataUDT.INC_ACC));
        equipData.setIncEva(value.getShort(EquipDataUDT.INC_EVA));
        equipData.setIncCraft(value.getShort(EquipDataUDT.INC_CRAFT));
        equipData.setIncSpeed(value.getShort(EquipDataUDT.INC_SPEED));
        equipData.setIncJump(value.getShort(EquipDataUDT.INC_JUMP));
        equipData.setRuc(value.getByte(EquipDataUDT.RUC));
        equipData.setCuc(value.getByte(EquipDataUDT.CUC));
        equipData.setIuc(value.getInt(EquipDataUDT.IUC));
        equipData.setChuc(value.getByte(EquipDataUDT.CHUC));
        equipData.setGrade(value.getByte(EquipDataUDT.GRADE));
        equipData.setOption1(value.getShort(EquipDataUDT.OPTION_1));
        equipData.setOption2(value.getShort(EquipDataUDT.OPTION_2));
        equipData.setOption3(value.getShort(EquipDataUDT.OPTION_3));
        equipData.setSocket1(value.getShort(EquipDataUDT.SOCKET_1));
        equipData.setSocket2(value.getShort(EquipDataUDT.SOCKET_2));
        equipData.setLevelUpType(value.getByte(EquipDataUDT.LEVEL_UP_TYPE));
        equipData.setLevel(value.getByte(EquipDataUDT.LEVEL));
        equipData.setExp(value.getInt(EquipDataUDT.EXP));
        equipData.setDurability(value.getInt(EquipDataUDT.DURABILITY));
        return equipData;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable EquipData equipData) {
        if (equipData == null) {
            return null;
        }
        return getCqlType().newValue()
                .setShort(EquipDataUDT.INC_STR, equipData.getIncStr())
                .setShort(EquipDataUDT.INC_DEX, equipData.getIncDex())
                .setShort(EquipDataUDT.INC_INT, equipData.getIncInt())
                .setShort(EquipDataUDT.INC_LUK, equipData.getIncLuk())
                .setShort(EquipDataUDT.INC_MAX_HP, equipData.getIncMaxHp())
                .setShort(EquipDataUDT.INC_MAX_MP, equipData.getIncMaxMp())
                .setShort(EquipDataUDT.INC_PAD, equipData.getIncPad())
                .setShort(EquipDataUDT.INC_MAD, equipData.getIncMad())
                .setShort(EquipDataUDT.INC_PDD, equipData.getIncPdd())
                .setShort(EquipDataUDT.INC_MDD, equipData.getIncMdd())
                .setShort(EquipDataUDT.INC_ACC, equipData.getIncAcc())
                .setShort(EquipDataUDT.INC_EVA, equipData.getIncEva())
                .setShort(EquipDataUDT.INC_CRAFT, equipData.getIncCraft())
                .setShort(EquipDataUDT.INC_SPEED, equipData.getIncSpeed())
                .setShort(EquipDataUDT.INC_JUMP, equipData.getIncJump())
                .setByte(EquipDataUDT.RUC, equipData.getRuc())
                .setByte(EquipDataUDT.CUC, equipData.getCuc())
                .setInt(EquipDataUDT.IUC, equipData.getIuc())
                .setByte(EquipDataUDT.CHUC, equipData.getChuc())
                .setByte(EquipDataUDT.GRADE, equipData.getGrade())
                .setShort(EquipDataUDT.OPTION_1, equipData.getOption1())
                .setShort(EquipDataUDT.OPTION_2, equipData.getOption2())
                .setShort(EquipDataUDT.OPTION_3, equipData.getOption3())
                .setShort(EquipDataUDT.SOCKET_1, equipData.getSocket1())
                .setShort(EquipDataUDT.SOCKET_2, equipData.getSocket2())
                .setByte(EquipDataUDT.LEVEL_UP_TYPE, equipData.getLevelUpType())
                .setByte(EquipDataUDT.LEVEL, equipData.getLevel())
                .setInt(EquipDataUDT.EXP, equipData.getExp())
                .setInt(EquipDataUDT.DURABILITY, equipData.getDurability());
    }
}
