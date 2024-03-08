package kinoko.provider.map;

import kinoko.util.Rect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class FootholdNode {
    private static final int MAX_SIZE = 5;
    private final List<Foothold> data = new ArrayList<>();
    private final FootholdNode parent;
    private FootholdNode left;
    private FootholdNode right;
    private Rect bounds = null;
    private Rect rootBounds = null;

    public FootholdNode() {
        this(null);
    }

    private FootholdNode(FootholdNode parent) {
        this.parent = parent;
    }

    public void searchDown(Consumer<Foothold> check, int x, int y) {
        if (left != null) {
            if (canSearchDown(left.rootBounds, x, y)) {
                left.searchDown(check, x, y);
            }
        }
        if (right != null) {
            if (canSearchDown(right.rootBounds, x, y)) {
                right.searchDown(check, x, y);
            }
        }
        data.forEach(check);
    }

    public void insert(Foothold fh) {
        if (data.isEmpty()) {
            data.add(fh);
            resize(fh);
        } else if (canInsert(fh)) {
            data.add(fh);
            resize(fh);
        } else {
            delegateInsert(fh);
        }
    }

    public Rect getRootBounds() {
        return rootBounds;
    }

    private void resize(Foothold fh) {
        final boolean slopeDown = fh.getY1() < fh.getY2();
        Rect rect = new Rect(
                fh.getX1(),
                slopeDown ? fh.getY1() : fh.getY2(),
                fh.getX2(),
                slopeDown ? fh.getY2() : fh.getY1()
        );
        resize(rect);
    }

    private void resize(Rect rect) {
        if (bounds == null) {
            bounds = rect;
            rootBounds = rect;
        } else {
            bounds = bounds.union(rect);
        }
        resizeRoot(rect);
    }

    private void resizeRoot(Rect rect) {
        rootBounds = rootBounds.union(rect);
        if (parent != null) {
            parent.resizeRoot(rect);
        }
    }

    private boolean canInsert(Foothold fh) {
        if (data.size() < MAX_SIZE) return true;
        if (bounds.isInsideRect(fh.getX1(), fh.getY1()) &&
                bounds.isInsideRect(fh.getX2(), fh.getY2())) {
            final int centerX = bounds.getCenterX();
            final int centerY = bounds.getCenterY();
            Foothold max = data.stream().max(Comparator.comparingDouble(f -> distance(f, centerX, centerY))).orElseThrow();
            if (distance(fh, centerX, centerY) > distance(max, centerX, centerY)) {
                return false;
            }
            if (data.remove(max)) {
                delegateInsert(max);
                return true; // resize called by insert
            }
        }
        return false;
    }

    private void delegateInsert(Foothold fh) {
        int cx = (bounds.getLeft() + bounds.getRight()) / 2;
        int fx = (fh.getX1() + fh.getX2()) / 2;
        if (fx <= cx) {
            if (left == null) left = new FootholdNode(this);
            left.insert(fh);
        } else {
            if (right == null) right = new FootholdNode(this);
            right.insert(fh);
        }
    }

    private static boolean canSearchDown(Rect rect, int x, int y) {
        return x >= rect.getLeft() && x <= rect.getRight() && rect.getBottom() >= y;
    }

    private static double distance(Foothold fh, int x, int y) {
        final int fx = (fh.getX1() + fh.getX2()) / 2;
        final int fy = fh.getYFromX(fx);
        return distance(x, y, fx, fy);
    }

    private static double distance(int x1, int y1, int x2, int y2) {
        final int dx = x1 - x2;
        final int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static final class SearchResult {
        private Foothold value;

        public Foothold get() {
            return value;
        }

        public void set(Foothold value) {
            this.value = value;
        }

        public void setIf(Predicate<Foothold> check, Foothold value) {
            if (this.value == null) {
                this.value = value;
            } else if (check.test(this.value)) {
                this.value = value;
            }
        }
    }
}
