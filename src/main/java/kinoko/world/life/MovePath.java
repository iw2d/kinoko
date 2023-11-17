package kinoko.world.life;

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

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(getX());
        outPacket.encodeShort(getY());
        outPacket.encodeShort(getVx());
        outPacket.encodeShort(getVy());
        outPacket.encodeByte(getElems().size());
        for (MoveElem elem : getElems()) {
            outPacket.encodeByte(elem.getAttr());
            switch (elem.getAttr()) {
                case 0, 5, 12, 14, 35, 36 -> {
                    outPacket.encodeShort(elem.getX()); // x
                    outPacket.encodeShort(elem.getY()); // y
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                    outPacket.encodeShort(elem.getFh()); // fh
                    if (elem.getAttr() == 12) {
                        outPacket.encodeShort(elem.getFhFallStart()); // fhFallStart
                    }
                    outPacket.encodeShort(elem.getXOffset()); // xOffset
                    outPacket.encodeShort(elem.getYOffset()); // yOffset
                }
                case 1, 2, 13, 16, 18, 31, 32, 33, 34 -> {
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                }
                case 3, 4, 6, 7, 8, 10 -> {
                    outPacket.encodeShort(elem.getX()); // x
                    outPacket.encodeShort(elem.getY()); // y
                    outPacket.encodeShort(elem.getFh()); // fh
                }
                case 9 -> {
                    outPacket.encodeByte(elem.getStat()); // bStat
                    continue; // moveAction and elapse not encoded
                }
                case 11 -> {
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                    outPacket.encodeShort(elem.getFhFallStart()); // fhFallStart
                }
                case 17 -> {
                    outPacket.encodeShort(elem.getX()); // x
                    outPacket.encodeShort(elem.getY()); // y
                    outPacket.encodeShort(elem.getVx()); // vx
                    outPacket.encodeShort(elem.getVy()); // vy
                }
                case 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> {
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
            switch (attr) {
                case 0, 5, 12, 14, 35, 36 -> {
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
                case 1, 2, 13, 16, 18, 31, 32, 33, 34 -> {
                    elem.setX(x);
                    elem.setY(y);
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                }
                case 3, 4, 6, 7, 8, 10 -> {
                    elem.setX(inPacket.decodeShort()); // x
                    elem.setY(inPacket.decodeShort()); // y
                    elem.setFh(inPacket.decodeShort()); // fh
                }
                case 9 -> {
                    elem.setStat(inPacket.decodeByte()); // bStat
                    elem.setX(x);
                    elem.setY(y);
                    moveElems.add(elem);
                    continue; // moveAction and elapse not decoded
                }
                case 11 -> {
                    elem.setX(x);
                    elem.setY(y);
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                    elem.setFhFallStart(inPacket.decodeShort()); // fhFallStart
                }
                case 17 -> {
                    elem.setX(inPacket.decodeShort()); // x
                    elem.setY(inPacket.decodeShort()); // y
                    elem.setVx(inPacket.decodeShort()); // vx
                    elem.setVy(inPacket.decodeShort()); // vy
                }
                case 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> {
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
}
