package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.item.EquipData;

import static kinoko.database.schema.EquipDataSchema.*;

public final class EquipDataSerializer implements JsonSerializer<EquipData> {
    public void putIfNotZero(JSONObject object, String key, int value) {
        if (value != 0) {
            object.put(key, value);
        }
    }

    @Override
    public JSONObject serialize(EquipData value) {
        if (value == null) {
            return null;
        }
        final JSONObject object = new JSONObject();
        putIfNotZero(object, INC_STR, value.getIncStr());
        putIfNotZero(object, INC_DEX, value.getIncDex());
        putIfNotZero(object, INC_INT, value.getIncInt());
        putIfNotZero(object, INC_LUK, value.getIncLuk());
        putIfNotZero(object, INC_MAX_HP, value.getIncMaxHp());
        putIfNotZero(object, INC_MAX_MP, value.getIncMaxMp());
        putIfNotZero(object, INC_PAD, value.getIncPad());
        putIfNotZero(object, INC_MAD, value.getIncMad());
        putIfNotZero(object, INC_PDD, value.getIncPdd());
        putIfNotZero(object, INC_MDD, value.getIncMdd());
        putIfNotZero(object, INC_ACC, value.getIncAcc());
        putIfNotZero(object, INC_EVA, value.getIncEva());
        putIfNotZero(object, INC_CRAFT, value.getIncCraft());
        putIfNotZero(object, INC_SPEED, value.getIncSpeed());
        putIfNotZero(object, INC_JUMP, value.getIncJump());

        putIfNotZero(object, RUC, value.getRuc());
        putIfNotZero(object, CUC, value.getCuc());
        putIfNotZero(object, IUC, value.getIuc());
        putIfNotZero(object, CHUC, value.getChuc());

        putIfNotZero(object, GRADE, value.getGrade());
        putIfNotZero(object, OPTION_1, value.getOption1());
        putIfNotZero(object, OPTION_2, value.getOption2());
        putIfNotZero(object, OPTION_3, value.getOption3());
        putIfNotZero(object, SOCKET_1, value.getSocket1());
        putIfNotZero(object, SOCKET_2, value.getSocket2());

        putIfNotZero(object, LEVEL_UP_TYPE, value.getLevelUpType());
        putIfNotZero(object, LEVEL, value.getLevel());
        putIfNotZero(object, EXP, value.getExp());
        putIfNotZero(object, DURABILITY, value.getDurability());
        return object;
    }

    @Override
    public EquipData deserialize(JSONObject object) {
        if (object == null) {
            return null;
        }

        final EquipData data = new EquipData();
        data.setIncStr(object.getShortValue(INC_STR));
        data.setIncDex(object.getShortValue(INC_DEX));
        data.setIncInt(object.getShortValue(INC_INT));
        data.setIncLuk(object.getShortValue(INC_LUK));
        data.setIncMaxHp(object.getShortValue(INC_MAX_HP));
        data.setIncMaxMp(object.getShortValue(INC_MAX_MP));
        data.setIncPad(object.getShortValue(INC_PAD));
        data.setIncMad(object.getShortValue(INC_MAD));
        data.setIncPdd(object.getShortValue(INC_PDD));
        data.setIncMdd(object.getShortValue(INC_MDD));
        data.setIncAcc(object.getShortValue(INC_ACC));
        data.setIncEva(object.getShortValue(INC_EVA));
        data.setIncCraft(object.getShortValue(INC_CRAFT));
        data.setIncSpeed(object.getShortValue(INC_SPEED));
        data.setIncJump(object.getShortValue(INC_JUMP));

        data.setRuc(object.getByteValue(RUC));
        data.setCuc(object.getByteValue(CUC));
        data.setIuc(object.getIntValue(IUC));
        data.setChuc(object.getByteValue(CHUC));

        data.setGrade(object.getByteValue(GRADE));
        data.setOption1(object.getShortValue(OPTION_1));
        data.setOption2(object.getShortValue(OPTION_2));
        data.setOption3(object.getShortValue(OPTION_3));
        data.setSocket1(object.getShortValue(SOCKET_1));
        data.setSocket2(object.getShortValue(SOCKET_2));

        data.setLevelUpType(object.getByteValue(LEVEL_UP_TYPE));
        data.setLevel(object.getByteValue(LEVEL));
        data.setExp(object.getIntValue(EXP));
        data.setDurability(object.getIntValue(DURABILITY));
        return data;
    }
}
