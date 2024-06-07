package kinoko.world.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.user.UserPacket;
import kinoko.server.event.EventScheduler;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class MiniGameRoom extends MiniRoom {
    private final String title;
    private final String password;
    private final int gameSpec;
    private final User owner;
    private final Set<User> leaveEngage = new HashSet<>();
    private User guest;
    private boolean open = true;
    private boolean ready = false;
    private int nextTurn = 0;

    public MiniGameRoom(String title, String password, int gameSpec, User owner) {
        this.title = title;
        this.password = password;
        this.gameSpec = gameSpec;
        this.owner = owner;
    }

    public final String getTitle() {
        return title;
    }

    public final boolean isPrivate() {
        return password != null;
    }

    public final int getGameSpec() {
        return gameSpec;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isOwner(User user) {
        return owner.getCharacterId() == user.getCharacterId();
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public final boolean isOpen() {
        return open;
    }

    public final void setOpen(boolean open) {
        this.open = open;
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

    protected abstract boolean isScorePenalty();

    @Override
    public final boolean checkPassword(String password) {
        return Objects.equals(this.password, password);
    }

    @Override
    public final int getMaxUsers() {
        return 2;
    }

    @Override
    public final boolean addUser(User user) {
        if (getGuest() != null) {
            return false;
        }
        setGuest(user);
        getOwner().write(MiniRoomPacket.MiniGame.enter(1, user, getType()));
        updateBalloon();
        return true;
    }

    @Override
    public final Map<Integer, User> getUsers() {
        if (getGuest() == null) {
            return Map.of(0, getOwner());
        } else {
            return Map.of(0, getOwner(), 1, getGuest());
        }
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
        final User other = isOwner(user) ? getGuest() : getOwner();
        if (other == null) {
            log.error("Received mini game room action {} without a guest in the room", mrp);
            return;
        }
        switch (mrp) {
            case MGRP_TieRequest -> {
                other.write(MiniRoomPacket.MiniGame.tieRequest());
            }
            case MGRP_TieResult -> {
                if (inPacket.decodeBoolean()) {
                    try (var lockedOther = other.acquire()) {
                        gameResult(GameResultType.DRAW, other, user);
                    }
                } else {
                    other.write(MiniRoomPacket.MiniGame.tieResult());
                }
            }
            case MGRP_GiveUpRequest -> {
                setNextTurn(getPosition(user));
                broadcastPacket(MiniRoomPacket.gameMessage(GameMessageType.UserGiveUp, user.getCharacterName()));
                try (var lockedOther = other.acquire()) {
                    gameResult(GameResultType.GIVEUP, other, user);
                }
            }
            case MGRP_LeaveEngage -> {
                broadcastPacket(MiniRoomPacket.gameMessage(GameMessageType.UserLeaveEngage, user.getCharacterName()));
                leaveEngage.add(user);
            }
            case MGRP_LeaveEngageCancel -> {
                broadcastPacket(MiniRoomPacket.gameMessage(GameMessageType.UserLeaveEngageCancel, user.getCharacterName()));
                leaveEngage.remove(user);
            }
            case MGRP_Ready, MGRP_CancelReady -> {
                if (isOwner(user)) {
                    log.error("Tried to ready as owner of the omok game room");
                    return;
                }
                setReady(mrp == MiniRoomProtocol.MGRP_Ready);
                broadcastPacket(MiniRoomPacket.MiniGame.ready(isReady()));
            }
            case MGRP_Ban -> {
                if (!isOwner(user)) {
                    log.error("Tried to ban user as guest of the omok game room");
                    return;
                }
                if (!isOpen()) {
                    log.error("Tried to ban user during game");
                }
                try (var lockedOther = other.acquire()) {
                    broadcastPacket(MiniRoomPacket.leave(getPosition(other), LeaveType.Kicked));
                    other.setDialog(null);
                    setGuest(null);
                    updateBalloon();
                }
            }
            case MGRP_TimeOver -> {
                setNextTurn(getPosition(other));
                broadcastPacket(MiniRoomPacket.MiniGame.timeOver(getNextTurn()));
            }
            default -> {
                log.error("Unhandled mini game room action {}", mrp);
            }
        }
    }

    @Override
    public final void close() {
        super.close();
        getOwner().getField().broadcastPacket(UserPacket.userRemoveMiniGameRoomBalloon(getOwner()));
    }


    // UTILITY METHODS -------------------------------------------------------------------------------------------------

    public final void updateBalloon() {
        getOwner().getField().broadcastPacket(UserPacket.userMiniGameRoomBalloon(getOwner(), this));
    }

    /**
     * This should only be called after acquiring the {@link kinoko.util.Lockable<User>} object.
     *
     * @see User#isLocked()
     */
    public final void leaveUnsafe(User user) {
        assert user.isLocked();
        final User other = isOwner(user) ? getGuest() : getOwner();
        // Conclude game
        if (other != null && !isOpen()) {
            EventScheduler.submit(() -> {
                try (var lockedOther = other.acquire()) {
                    gameResult(GameResultType.GIVEUP, other, user);
                }
            });
        }
        // Close game if owner
        if (isOwner(user)) {
            if (other != null) {
                other.write(MiniRoomPacket.leave(getPosition(other), LeaveType.HostOut)); // The room is closed.
                other.setDialog(null);
            }
            user.write(MiniRoomPacket.leave(getPosition(user), LeaveType.UserRequest)); // You have left the room.
            user.setDialog(null);
            close();
        } else {
            broadcastPacket(MiniRoomPacket.leave(getPosition(user), LeaveType.UserRequest)); // You have left the room. | [%s] have left.
            user.setDialog(null);
            setGuest(null);
            updateBalloon();
        }
        leaveEngage.remove(user);
    }

    protected final void gameResult(GameResultType resultType, User winner, User loser) {
        assert winner.isLocked();
        assert loser.isLocked();
        // Process score
        final boolean isDraw = resultType == GameResultType.DRAW;
        final boolean scorePenalty = resultType == GameResultType.GIVEUP && isScorePenalty();
        winner.getCharacterData().getMiniGameRecord().processResult(getType(), loser.getCharacterData().getMiniGameRecord(), isDraw, scorePenalty);
        // Update clients
        broadcastPacket(MiniRoomPacket.MiniGame.gameResult(resultType, this, getPosition(winner)));
        setReady(false);
        setOpen(true);
        updateBalloon();
        // Handle leave engage
        if (leaveEngage.contains(getOwner())) {
            leaveUnsafe(getOwner());
        } else if (leaveEngage.contains(getGuest())) {
            leaveUnsafe(getGuest());
        }
        leaveEngage.clear();
    }
}
