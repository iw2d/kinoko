package kinoko.world.field.life;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

import java.util.ArrayList;
import java.util.List;

public final class MovePath implements Encodable {
    private final short x;
    private final short y;
    private final short vx;
    private final short vy;
    private final List<MoveElem> moveElems;

    public MovePath(short x, short y, short vx, short vy, List<MoveElem> moveElems) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.moveElems = moveElems;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public short getVx() {
        return vx;
    }

    public short getVy() {
        return vy;
    }

    public List<MoveElem> getElems() {
        return moveElems;
    }

    public void applyTo(Life life) {
        for (MoveElem elem : getElems()) {
            switch (MoveType.fromAttr(elem.getAttr())) {
                case NORMAL, TELEPORT -> {
                    life.setX(elem.getX());
                    life.setY(elem.getY());
                    life.setFoothold(elem.getFh());
                }
                case JUMP, START_FALL_DOWN, FLYING_BLOCK -> {
                    life.setX(elem.getX());
                    life.setY(elem.getY());
                }
                case STAT_CHANGE -> {
                    continue;
                }
                case ACTION -> {
                    // noop
                }
            }
            life.setMoveAction(elem.getMoveAction());
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(getX());
        outPacket.encodeShort(getY());
        outPacket.encodeShort(getVx());
        outPacket.encodeShort(getVy());
        outPacket.encodeByte(getElems().size());
        for (MoveElem elem : getElems()) {
            outPacket.encodeByte(elem.getAttr());
            switch (MoveType.fromAttr(elem.getAttr())) {
                case NORMAL -> {
                    outPacket.encodeShort(elem.getX()); // x
                    outPacket.encodeShort(elem.getY()); // y
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                    outPacket.encodeShort(elem.getFh()); // fh
                    if (elem.getAttr() == 12) { // FALL_DOWN
                        outPacket.encodeShort(elem.getFhFallStart()); // fhFallStart
                    }
                    outPacket.encodeShort(elem.getXOffset()); // xOffset
                    outPacket.encodeShort(elem.getYOffset()); // yOffset
                }
                case JUMP -> {
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                }
                case TELEPORT -> {
                    outPacket.encodeShort(elem.getX()); // x
                    outPacket.encodeShort(elem.getY()); // y
                    outPacket.encodeShort(elem.getFh()); // fh
                }
                case STAT_CHANGE -> {
                    outPacket.encodeByte(elem.getStat()); // bStat
                    continue; // moveAction and elapse not encoded
                }
                case START_FALL_DOWN -> {
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                    outPacket.encodeShort(elem.getFhFallStart()); // fhFallStart
                }
                case FLYING_BLOCK -> {
                    outPacket.encodeShort(elem.getX()); // x
                    outPacket.encodeShort(elem.getY()); // y
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                }
                case ACTION -> {
                    // noop
                }
            }
            outPacket.encodeByte(elem.getMoveAction()); // bMoveAction
            outPacket.encodeShort(elem.getElapse()); // tElapse
            // if (CClientOptMan::GetOpt(2)) short (usRandCnt), short (usActualRandCnt)
        }
        // Follow Mode: if (bPassive) byte * byte, short, short, short, short
    }

    public static MovePath decode(InPacket inPacket) {
        final short x = inPacket.decodeShort();
        final short y = inPacket.decodeShort();
        final short vx = inPacket.decodeShort();
        final short vy = inPacket.decodeShort();

        final List<MoveElem> moveElems = new ArrayList<>();
        final int count = inPacket.decodeByte();
        for (int i = 0; i < count; i++) {
            final byte attr = inPacket.decodeByte(); // nAttr
            final MoveElem elem = new MoveElem(attr);
            switch (MoveType.fromAttr(attr)) {
                case NORMAL -> {
                    elem.setX(inPacket.decodeShort()); // x
                    elem.setY(inPacket.decodeShort()); // y
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                    elem.setFh(inPacket.decodeShort()); // fh
                    if (attr == 12) {
                        elem.setFhFallStart(inPacket.decodeShort()); // fhFallStart
                    }
                    elem.setXOffset(inPacket.decodeShort()); // xOffset
                    elem.setYOffset(inPacket.decodeShort()); // yOffset
                }
                case JUMP -> {
                    elem.setX(x);
                    elem.setY(y);
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                }
                case TELEPORT -> {
                    elem.setX(inPacket.decodeShort()); // x
                    elem.setY(inPacket.decodeShort()); // y
                    elem.setFh(inPacket.decodeShort()); // fh
                }
                case STAT_CHANGE -> {
                    elem.setStat(inPacket.decodeByte()); // bStat
                    elem.setX(x);
                    elem.setY(y);
                    moveElems.add(elem);
                    continue; // moveAction and elapse not decoded
                }
                case START_FALL_DOWN -> {
                    elem.setX(x);
                    elem.setY(y);
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                    elem.setFhFallStart(inPacket.decodeShort()); // fhFallStart
                }
                case FLYING_BLOCK -> {
                    elem.setX(inPacket.decodeShort()); // x
                    elem.setY(inPacket.decodeShort()); // y
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                }
                case ACTION -> {
                    elem.setX(x);
                    elem.setY(y);
                    elem.setVy(vx);
                    elem.setVy(vy);
                }
            }
            elem.setMoveAction(inPacket.decodeByte()); // bMoveAction
            elem.setElapse(inPacket.decodeShort()); // tElapse
            // if (CClientOptMan::GetOpt(2)) short (usRandCnt), short (usActualRandCnt)
            moveElems.add(elem);
        }
        return new MovePath(x, y, vx, vy, moveElems);
    }

    private enum MoveType {
        NORMAL,
        JUMP,
        TELEPORT,
        STAT_CHANGE,
        START_FALL_DOWN,
        FLYING_BLOCK,
        ACTION;

        private static MoveType fromAttr(byte attr) {
            switch (attr) {
                case 0, 5, 12, 14, 35, 36 -> {
                    return NORMAL;
                }
                case 1, 2, 13, 16, 18, 31, 32, 33, 34 -> {
                    return JUMP;
                }
                case 3, 4, 6, 7, 8, 10 -> {
                    return TELEPORT;
                }
                case 9 -> {
                    return STAT_CHANGE;
                }
                case 11 -> {
                    return START_FALL_DOWN;
                }
                case 17 -> {
                    return FLYING_BLOCK;
                }
                case 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> {
                    return ACTION;
                }
                default -> {
                    throw new IllegalStateException("Unknown attr " + attr);
                }
            }
        }
    }
}
