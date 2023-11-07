package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

public record ReactorInfo(int id, String name, int x, int y, boolean f, int reactorTime) {
    public static ReactorInfo from(WzListProperty reactorProp) {
        return new ReactorInfo(
                Integer.parseInt(reactorProp.get("id")),
                reactorProp.get("name"),
                reactorProp.get("x"),
                reactorProp.get("y"),
                reactorProp.getOrDefault("f", 0) != 0,
                reactorProp.get("reactorTime")
        );
    }
}
