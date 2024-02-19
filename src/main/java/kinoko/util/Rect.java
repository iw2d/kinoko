package kinoko.util;

public final class Rect {
    private final int left, top, right, bottom;

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getCenterX() {
        return (left + right) / 2;
    }

    public int getCenterY() {
        return (top + bottom) / 2;
    }

    public boolean isInsideRect(int x, int y) {
        return x >= left && y >= top && x <= right && y <= bottom;
    }

    public Rect union(Rect rect) {
        return new Rect(
                Math.min(getLeft(), rect.getLeft()),
                Math.min(getTop(), rect.getTop()),
                Math.max(getRight(), rect.getRight()),
                Math.max(getBottom(), rect.getBottom())
        );
    }
}
