package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

public record LifeInfo(
        LifeType type,
        int id,
        int x,
        int y,
        int rx0,
        int rx1,
        int cy,
        int fh,
        boolean f,
        boolean hide,
        int mobTime
) {
    public static LifeInfo from(LifeType lifeType, WzListProperty lifeProp) {
        return new LifeInfo(
                lifeType,
                Integer.parseInt(lifeProp.get("id")),
                lifeProp.get("x"),
                lifeProp.get("y"),
                lifeProp.get("rx0"),
                lifeProp.get("rx1"),
                lifeProp.get("cy"),
                lifeProp.get("fh"),
                lifeProp.getOrDefault("f", 0) != 0,
                lifeProp.getOrDefault("hide", 0) != 0,
                lifeProp.getOrDefault("mobTime", 0)
        );
    }
}
