package kinoko.world.field.life;

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
}
