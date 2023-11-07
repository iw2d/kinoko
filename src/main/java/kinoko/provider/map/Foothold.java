package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

public record Foothold(int layer, int group, int id, int x1, int y1, int x2, int y2) {
    public static Foothold from(int layerId, int groupId, int footholdId, WzListProperty footholdProp) {
        return new Foothold(
                layerId,
                groupId,
                footholdId,
                footholdProp.getOrDefault("x1", 0),
                footholdProp.getOrDefault("y1", 0),
                footholdProp.getOrDefault("x2", 0),
                footholdProp.getOrDefault("y2", 0)
        );
    }
}
