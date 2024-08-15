package kinoko.provider.map;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.Objects;

public final class Foothold {
    private final int layerId;
    private final int groupId;
    private final int sn;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final int drag;
    private final int force;
    private final int forbidFallDown;
    private final int cantThrough;
    private final int prev;
    private final int next;

    public Foothold(int layerId, int groupId, int sn, int x1, int y1, int x2, int y2, int drag, int force, int forbidFallDown, int cantThrough, int prev, int next) {
        this.layerId = layerId;
        this.groupId = groupId;
        this.sn = sn;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.drag = drag;
        this.force = force;
        this.forbidFallDown = forbidFallDown;
        this.cantThrough = cantThrough;
        this.prev = prev;
        this.next = next;
    }

    public int getLayerId() {
        return layerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getSn() {
        return sn;
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

    public int getDrag() {
        return drag;
    }

    public int getForce() {
        return force;
    }

    public int getForbidFallDown() {
        return forbidFallDown;
    }

    public int getCantThrough() {
        return cantThrough;
    }

    public int getPrev() {
        return prev;
    }

    public int getNext() {
        return next;
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
        return Objects.hash(layerId, groupId, sn);
    }

    @Override
    public String toString() {
        return "Foothold{" +
                "layerId=" + layerId +
                ", groupId=" + groupId +
                ", sn=" + sn +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", drag=" + drag +
                ", force=" + force +
                ", forbidFallDown=" + forbidFallDown +
                ", cantThrough=" + cantThrough +
                ", prev=" + prev +
                ", next=" + next +
                '}';
    }

    public static Foothold from(int layerId, int groupId, int sn, WzListProperty footholdProp) {
        return new Foothold(
                layerId,
                groupId,
                sn,
                WzProvider.getInteger(footholdProp.get("x1")),
                WzProvider.getInteger(footholdProp.get("y1")),
                WzProvider.getInteger(footholdProp.get("x2")),
                WzProvider.getInteger(footholdProp.get("y2")),
                WzProvider.getInteger(footholdProp.get("drag"), 0),
                WzProvider.getInteger(footholdProp.get("force"), 0),
                WzProvider.getInteger(footholdProp.get("forbidFallDown"), 0),
                WzProvider.getInteger(footholdProp.get("cantThrough"), 0),
                WzProvider.getInteger(footholdProp.get("prev")),
                WzProvider.getInteger(footholdProp.get("next"))
        );
    }
}
