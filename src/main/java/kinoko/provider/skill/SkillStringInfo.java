package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SkillStringInfo {
    private final String name;
    private final String desc;
    private final String h;
    private final Map<Integer, String> hMap;

    public SkillStringInfo(String name, String desc, String h, Map<Integer, String> hMap) {
        this.name = name;
        this.desc = desc;
        this.h = h;
        this.hMap = hMap;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getH() {
        return h;
    }

    public Map<Integer, String> getHMap() {
        return hMap;
    }

    public static SkillStringInfo from(WzListProperty stringProp) throws ProviderError {
        final String name = WzProvider.getString(stringProp.get("name"));
        final String desc = WzProvider.getString(stringProp.get("desc"), "");
        final String h = WzProvider.getString(stringProp.get("h"), "");
        final HashMap<Integer, String> hMap = new HashMap<>();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            final String key = String.format("h%d", i);
            if (stringProp.get(key) == null) {
                break;
            }
            hMap.put(i, WzProvider.getString(stringProp.get(key)));
        }
        return new SkillStringInfo(
                name,
                desc,
                h,
                Collections.unmodifiableMap(hMap)
        );
    }
}
