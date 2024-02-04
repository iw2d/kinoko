package kinoko.world.life;

public final class MoveElem {
    private final byte attr;
    private short x;
    private short y;
    private short vx;
    private short vy;
    private short fh;
    private short fhFallStart;
    private short xOffset;
    private short yOffset;
    private byte stat;
    private byte moveAction;
    private short elapse;

    public MoveElem(byte attr) {
        this.attr = attr;
    }

    public byte getAttr() {
        return attr;
    }

    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public short getVx() {
        return vx;
    }

    public void setVx(short vx) {
        this.vx = vx;
    }

    public short getVy() {
        return vy;
    }

    public void setVy(short vy) {
        this.vy = vy;
    }

    public short getFh() {
        return fh;
    }

    public void setFh(short fh) {
        this.fh = fh;
    }

    public short getFhFallStart() {
        return fhFallStart;
    }

    public void setFhFallStart(short fhFallStart) {
        this.fhFallStart = fhFallStart;
    }

    public short getXOffset() {
        return xOffset;
    }

    public void setXOffset(short xOffset) {
        this.xOffset = xOffset;
    }

    public short getYOffset() {
        return yOffset;
    }

    public void setYOffset(short yOffset) {
        this.yOffset = yOffset;
    }

    public byte getStat() {
        return stat;
    }

    public void setStat(byte stat) {
        this.stat = stat;
    }

    public byte getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(byte moveAction) {
        this.moveAction = moveAction;
    }

    public short getElapse() {
        return elapse;
    }

    public void setElapse(short elapse) {
        this.elapse = elapse;
    }
}
