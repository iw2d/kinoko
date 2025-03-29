package kinoko.world.field;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.user.UserPacket;
import kinoko.server.dialog.miniroom.MiniRoom;
import kinoko.server.dialog.miniroom.MiniRoomLeaveType;
import kinoko.server.dialog.miniroom.MiniRoomType;
import kinoko.util.Rect;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.Map;

public final class MiniRoomPool extends FieldObjectPool<MiniRoom> {
    public MiniRoomPool(Field field) {
        super(field);
    }

    public boolean canAddMiniRoom(MiniRoomType miniRoomType, int x, int y) {
        final Rect rect = getRectByMiniRoomType(miniRoomType).translate(x, y);
        return getInsideRect(rect).stream().noneMatch((miniRoom) -> miniRoom.getType().isBalloon());
    }

    public void addMiniRoom(MiniRoom miniRoom) {
        miniRoom.setField(field);
        miniRoom.setId(field.getNewObjectId());
        addObject(miniRoom);
    }

    public void removeMiniRoom(MiniRoom miniRoom) {
        removeObject(miniRoom);
    }

    private static Rect getRectByMiniRoomType(MiniRoomType miniRoomType) {
        return switch (miniRoomType) {
            case OmokRoom, MemoryGameRoom -> Rect.of(-90, -60, 90, 60);
            case PersonalShop, EntrustedShop -> Rect.of(-120, -80, 80, 120);
            default -> Rect.of(0, 0, 0, 0);
        };
    }

    public void updateMiniRooms(Instant now) {
        final var iter = objects.values().iterator();
        while (iter.hasNext()) {
            final MiniRoom miniRoom = iter.next();
            final Map<User, MiniRoomLeaveType> leaveRequests = miniRoom.getLeaveRequests();
            if (leaveRequests.isEmpty()) {
                continue;
            }
            assert miniRoom.getType() != MiniRoomType.TradingRoom;
            assert miniRoom.getType() != MiniRoomType.PersonalShop;
            final User owner = miniRoom.getUser(0);
            if (leaveRequests.containsKey(owner)) {
                // Close room
                var userIter = miniRoom.getUsers().entrySet().iterator();
                while (userIter.hasNext()) {
                    final var entry = userIter.next();
                    final int userIndex = entry.getKey();
                    final User user = entry.getValue();
                    if (userIndex == 0) {
                        user.write(MiniRoomPacket.leave(userIndex, leaveRequests.get(user)));
                    } else {
                        user.write(MiniRoomPacket.leave(userIndex, MiniRoomLeaveType.HostOut));
                    }
                    user.setDialog(null);
                    userIter.remove();
                }
                iter.remove();
                if (miniRoom.getType().isBalloon()) {
                    field.broadcastPacket(UserPacket.userMiniRoomBalloonRemove(owner));
                }
            } else {
                // Process users leaving
                var leaveIter = leaveRequests.entrySet().iterator();
                while (leaveIter.hasNext()) {
                    final var entry = leaveIter.next();
                    final User user = entry.getKey();
                    final int userIndex = miniRoom.getUserIndex(user);
                    miniRoom.broadcastPacket(MiniRoomPacket.leave(userIndex, entry.getValue()));
                    user.setDialog(null);
                    miniRoom.removeUser(userIndex);
                    leaveIter.remove();
                }
            }
            miniRoom.updateBalloon();
        }
    }
}
