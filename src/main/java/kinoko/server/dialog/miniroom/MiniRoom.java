package kinoko.server.dialog.miniroom;

import kinoko.server.dialog.Dialog;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.FieldObjectImpl;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class MiniRoom extends FieldObjectImpl implements Dialog {
    protected static final Logger log = LogManager.getLogger(MiniRoom.class);
    private final String title;
    private final String password;
    private final int gameSpec;
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<User, MiniRoomLeaveType> leaveRequests = new HashMap<>();
    private boolean gameOn = false;
    private boolean ready = false;
    private int nextTurn = 0;

    public MiniRoom(String title, String password, int gameSpec) {
        this.title = title;
        this.password = password;
        this.gameSpec = gameSpec;
    }

    public abstract MiniRoomType getType();

    public abstract int getMaxUsers();

    public abstract void handlePacket(User user, MiniRoomProtocol mrp, InPacket inPacket);

    public abstract void leave(User user);

    public abstract void updateBalloon();

    public final String getTitle() {
        return title;
    }

    public final String getPassword() {
        return password;
    }

    public final int getGameSpec() {
        return gameSpec;
    }

    public final void addUser(int userIndex, User user) {
        users.put(userIndex, user);
    }

    public final void removeUser(int userIndex) {
        users.remove(userIndex);
    }

    public final Map<Integer, User> getUsers() {
        return users;
    }

    public final User getUser(int userIndex) {
        return users.getOrDefault(userIndex, null);
    }

    public final int getUserIndex(User user) {
        for (var entry : users.entrySet()) {
            if (entry.getValue().getCharacterId() == user.getCharacterId()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public final Map<User, MiniRoomLeaveType> getLeaveRequests() {
        return leaveRequests;
    }

    public final void setLeaveRequest(User user, MiniRoomLeaveType leaveType) {
        leaveRequests.put(user, leaveType);
    }

    public final boolean isGameOn() {
        return gameOn;
    }

    public final void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

    public final boolean isReady() {
        return ready;
    }

    public final void setReady(boolean ready) {
        this.ready = ready;
    }

    public final int getNextTurn() {
        return nextTurn;
    }

    public final void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public final boolean isOwner(User user) {
        return getUserIndex(user) == 0;
    }

    public final boolean checkPassword(String password) {
        return Objects.equals(this.password, password);
    }

    public final boolean isPrivate() {
        return password != null && !password.isEmpty();
    }

    public final void broadcastPacket(OutPacket outPacket) {
        for (var entry : users.entrySet()) {
            entry.getValue().write(outPacket);
        }
    }
}
