package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

public final class ReactorInfo {
    private final int reactorId;
    private final String reactorName;
    private final int x;
    private final int y;
    private final boolean flip;
    private final int reactorTime;

    public ReactorInfo(int reactorId, String reactorName, int x, int y, boolean flip, int reactorTime) {
        this.reactorId = reactorId;
        this.reactorName = reactorName;
        this.x = x;
        this.y = y;
        this.flip = flip;
        this.reactorTime = reactorTime;
    }

    public int getReactorId() {
        return reactorId;
    }

    public String getReactorName() {
        return reactorName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFlip() {
        return flip;
    }

    public int getReactorTime() {
        return reactorTime;
    }

    @Override
    public String toString() {
        return "ReactorInfo[" +
                "id=" + reactorId + ", " +
                "name=" + reactorName + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "f=" + flip + ", " +
                "reactorTime=" + reactorTime + ']';
    }

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
