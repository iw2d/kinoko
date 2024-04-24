package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

import java.util.Objects;

public final class Foothold {
    public static final Foothold EMPTY_FOOTHOLD = new Foothold(0, 0, 0, 0, 0, 0, 0);
    private final int layerId;
    private final int groupId;
    private final int footholdId;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    public Foothold(int layerId, int groupId, int footholdId, int x1, int y1, int x2, int y2) {
        this.layerId = layerId;
        this.groupId = groupId;
        this.footholdId = footholdId;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getLayerId() {
        return layerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getFootholdId() {
        return footholdId;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getYFromX(int x) {
        // interpolate between the two foothold ends for the y value below pos.x
        final double f = (double) (x - x1) / (double) (x2 - x1);
        return (int) Math.ceil(y1 + (f * (y2 - y1)));
    }

    public boolean isWall() {
        return x1 == x2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(layerId, groupId, footholdId);
    }

    @Override
    public String toString() {
        return "Foothold[" +
                "layer=" + layerId + ", " +
                "group=" + groupId + ", " +
                "id=" + footholdId + ", " +
                "x1=" + x1 + ", " +
                "y1=" + y1 + ", " +
                "x2=" + x2 + ", " +
                "y2=" + y2 + ']';
    }

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
