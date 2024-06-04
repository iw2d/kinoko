package kinoko.server.dialog.miniroom;

import kinoko.server.dialog.Dialog;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.FieldObjectImpl;
import kinoko.world.user.User;

import java.util.Map;
import java.util.Optional;

public abstract class MiniRoom extends FieldObjectImpl implements Dialog {
    public abstract MiniRoomType getType();

    public abstract boolean checkPassword(String password);

    public abstract int getMaxUsers();

    public abstract boolean addUser(User user);

    public abstract Map<Integer, User> getUsers();

    public int getPosition(User user) {
        for (var entry : getUsers().entrySet()) {
            if (entry.getValue().getCharacterId() == user.getCharacterId()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public void broadcastPacket(OutPacket outPacket) {
        for (User user : getUsers().values()) {
            user.write(outPacket);
        }
    }

    public Optional<OutPacket> enterFieldPacket() {
        return Optional.empty();
    }

    public Optional<OutPacket> leaveFieldPacket() {
        return Optional.empty();
    }
}
