package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.ExtendSp;

import java.util.HashMap;
import java.util.Map;

import static kinoko.database.schema.CharacterStatSchema.*;

public final class CharacterStatSerializer implements JsonSerializer<CharacterStat> {
    @Override
    public JSONObject serialize(CharacterStat cs) {
        if (cs == null) {
            return null;
        }
        final JSONObject object = new JSONObject();
        object.put(GENDER, cs.getGender());
        object.put(SKIN, cs.getSkin());
        object.put(FACE, cs.getFace());
        object.put(HAIR, cs.getHair());
        object.put(LEVEL, cs.getLevel());
        object.put(JOB, cs.getJob());
        object.put(SUB_JOB, cs.getSubJob());
        object.put(BASE_STR, cs.getBaseStr());
        object.put(BASE_DEX, cs.getBaseDex());
        object.put(BASE_INT, cs.getBaseInt());
        object.put(BASE_LUK, cs.getBaseLuk());
        object.put(HP, cs.getHp());
        object.put(MAX_HP, cs.getMaxHp());
        object.put(MP, cs.getMp());
        object.put(MAX_MP, cs.getMaxMp());
        object.put(AP, cs.getAp());

        final JSONObject spObject = object.putObject(SP);
        for (var entry : cs.getSp().getMap().entrySet()) {
            spObject.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        object.put(EXP, cs.getExp());
        object.put(POP, cs.getPop());
        object.put(POS_MAP, cs.getPosMap());
        object.put(PORTAL, cs.getPortal());
        object.put(PET_1, cs.getPetSn1());
        object.put(PET_2, cs.getPetSn2());
        object.put(PET_3, cs.getPetSn3());
        return object;
    }

    @Override
    public CharacterStat deserialize(JSONObject object) {
        if (object == null) {
            return null;
        }

        final CharacterStat cs = new CharacterStat();
        cs.setGender(object.getByteValue(GENDER));
        cs.setSkin(object.getByteValue(SKIN));
        cs.setFace(object.getIntValue(FACE));
        cs.setHair(object.getIntValue(HAIR));
        cs.setLevel(object.getShortValue(LEVEL));
        cs.setJob(object.getShortValue(JOB));
        cs.setSubJob(object.getShortValue(SUB_JOB));
        cs.setBaseStr(object.getShortValue(BASE_STR));
        cs.setBaseDex(object.getShortValue(BASE_DEX));
        cs.setBaseInt(object.getShortValue(BASE_INT));
        cs.setBaseLuk(object.getShortValue(BASE_LUK));
        cs.setHp(object.getIntValue(HP));
        cs.setMaxHp(object.getIntValue(MAX_HP));
        cs.setMp(object.getIntValue(MP));
        cs.setMaxMp(object.getIntValue(MAX_MP));
        cs.setAp(object.getShortValue(AP));

        final Map<Integer, Integer> spMap = new HashMap<>();
        final JSONObject spObject = object.getJSONObject(SP);
        for (var key : spObject.keySet()) {
            spMap.put(Integer.parseInt(key), spObject.getIntValue(key));
        }
        cs.setSp(ExtendSp.from(spMap));

        cs.setExp(object.getIntValue(EXP));
        cs.setPop(object.getShortValue(POP));
        cs.setPosMap(object.getIntValue(POS_MAP));
        cs.setPortal(object.getByteValue(PORTAL));
        cs.setPetSn1(object.getLongValue(PET_1));
        cs.setPetSn2(object.getLongValue(PET_2));
        cs.setPetSn3(object.getLongValue(PET_3));

        return cs;
    }
}
