package kinoko.world.field;

import kinoko.server.dialog.miniroom.MiniRoom;

public final class MiniRoomPool extends FieldObjectPool<MiniRoom> {
    public MiniRoomPool(Field field) {
        super(field);
    }

    public void addMiniRoom(MiniRoom miniRoom) {
        miniRoom.setField(field);
        miniRoom.setId(field.getNewObjectId());
        addObject(miniRoom);
        miniRoom.enterFieldPacket().ifPresent(field::broadcastPacket);
    }

    public void removeMiniRoom(MiniRoom miniRoom) {
        removeObject(miniRoom);
        miniRoom.leaveFieldPacket().ifPresent(field::broadcastPacket);
    }
}
