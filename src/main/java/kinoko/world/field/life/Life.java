package kinoko.world.field.life;

import kinoko.util.Rect;
import kinoko.world.field.FieldObjectImpl;

public abstract class Life extends FieldObjectImpl {
    private int foothold;
    private int moveAction;

    public int getFoothold() {
        return foothold;
    }

    public void setFoothold(int foothold) {
        this.foothold = foothold;
    }

    public int getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(int moveAction) {
        this.moveAction = moveAction;
    }

    public boolean isLeft() {
        return (getMoveAction() & 1) != 0;
    }

    public void setLeft(boolean left) {
        if (left) {
            setMoveAction(getMoveAction() | 1);
        } else {
            setMoveAction(getMoveAction() & ~1);
        }
    }

    public Rect getRelativeRect(Rect rect) {
        if (!isLeft()) {
            // Flip horizontally along x = 0
            rect = rect.flipX();
        }
        return rect.translate(getX(), getY());
    }
}
