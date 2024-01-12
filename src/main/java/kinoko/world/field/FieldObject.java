package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public interface FieldObject {
    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getFh();

    void setFh(int fh);

    int getMoveAction();

    void setMoveAction(int moveAction);

    Field getField();

    void setField(Field field);

    OutPacket enterFieldPacket();

    OutPacket leaveFieldPacket();
}
