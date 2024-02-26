package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

public final class ReactorInfo {
    private final int templateId;
    private final String name;
    private final int x;
    private final int y;
    private final boolean flip;
    private final int reactorTime;

    public ReactorInfo(int templateId, String name, int x, int y, boolean flip, int reactorTime) {
        this.templateId = templateId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.flip = flip;
        this.reactorTime = reactorTime;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getName() {
        return name;
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
                "id=" + templateId + ", " +
                "name=" + name + ", " +
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
