package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.CharacterStatUDT;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.ExtendSP;

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
        cs.setGender(value.getByte(CharacterStatUDT.GENDER));
        cs.setSkin(value.getByte(CharacterStatUDT.SKIN));
        cs.setFace(value.getInt(CharacterStatUDT.FACE));
        cs.setHair(value.getInt(CharacterStatUDT.HAIR));
        cs.setLevel(value.getShort(CharacterStatUDT.LEVEL));
        cs.setJob(value.getShort(CharacterStatUDT.JOB));
        cs.setSubJob(value.getShort(CharacterStatUDT.SUB_JOB));
        cs.setBaseStr(value.getShort(CharacterStatUDT.BASE_STR));
        cs.setBaseDex(value.getShort(CharacterStatUDT.BASE_DEX));
        cs.setBaseInt(value.getShort(CharacterStatUDT.BASE_INT));
        cs.setBaseLuk(value.getShort(CharacterStatUDT.BASE_LUK));
        cs.setHp(value.getInt(CharacterStatUDT.HP));
        cs.setMaxHp(value.getInt(CharacterStatUDT.MAX_HP));
        cs.setMp(value.getInt(CharacterStatUDT.MP));
        cs.setMaxMp(value.getInt(CharacterStatUDT.MAX_MP));
        cs.setAp(value.getShort(CharacterStatUDT.AP));
        cs.setSp(ExtendSP.from(value.getList(CharacterStatUDT.SP, Integer.class)));
        cs.setExp(value.getInt(CharacterStatUDT.EXP));
        cs.setPop(value.getShort(CharacterStatUDT.POP));
        cs.setPosMap(value.getInt(CharacterStatUDT.POS_MAP));
        cs.setPortal(value.getByte(CharacterStatUDT.PORTAL));
        return cs;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable CharacterStat cs) {
        if (cs == null) {
            return null;
        }
        return getCqlType().newValue()
                .setByte(CharacterStatUDT.GENDER, cs.getGender())
                .setByte(CharacterStatUDT.SKIN, cs.getSkin())
                .setInt(CharacterStatUDT.FACE, cs.getFace())
                .setInt(CharacterStatUDT.HAIR, cs.getHair())
                .setShort(CharacterStatUDT.LEVEL, cs.getLevel())
                .setShort(CharacterStatUDT.JOB, cs.getJob())
                .setShort(CharacterStatUDT.SUB_JOB, cs.getSubJob())
                .setShort(CharacterStatUDT.BASE_STR, cs.getBaseStr())
                .setShort(CharacterStatUDT.BASE_DEX, cs.getBaseDex())
                .setShort(CharacterStatUDT.BASE_INT, cs.getBaseInt())
                .setShort(CharacterStatUDT.BASE_LUK, cs.getBaseLuk())
                .setInt(CharacterStatUDT.HP, cs.getHp())
                .setInt(CharacterStatUDT.MAX_HP, cs.getMaxHp())
                .setInt(CharacterStatUDT.MP, cs.getMp())
                .setInt(CharacterStatUDT.MAX_MP, cs.getMaxMp())
                .setShort(CharacterStatUDT.AP, cs.getAp())
                .setList(CharacterStatUDT.SP, cs.getSp().getList(), Integer.class)
                .setInt(CharacterStatUDT.EXP, cs.getExp())
                .setShort(CharacterStatUDT.POP, cs.getPop())
                .setInt(CharacterStatUDT.POS_MAP, cs.getPosMap())
                .setByte(CharacterStatUDT.PORTAL, cs.getPortal());
    }
}
