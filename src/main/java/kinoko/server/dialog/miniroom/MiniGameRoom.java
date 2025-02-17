package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.packet.user.UserPacket;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;
import kinoko.world.user.data.MiniGameRecord;

import java.util.HashSet;
import java.util.Set;

public abstract class MiniGameRoom extends MiniRoom {
    private final Set<User> leaveBooked = new HashSet<>();

    public MiniGameRoom(String title, String password, int gameSpec) {
        super(title, password, gameSpec);
    }

    public abstract boolean isScorePenalty();

    public final User getOther(User user) {
        return getUserIndex(user) == 0 ? getUser(1) : getUser(0);
    }

    @Override
    public int getMaxUsers() {
        return 2;
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
        final User other = getOther(user);
        if (other == null) {
            log.error("Received mini room action {} without another player in the mini game room", mrp);
            return;
        }
        switch (mrp) {
            case MGRP_TieRequest -> {
                if (isGameOn()) {
                    other.write(MiniRoomPacket.MiniGame.tieRequest());
                }
            }
            case MGRP_TieResult -> {
                if (isGameOn()) {
                    if (inPacket.decodeBoolean()) {
                        gameSet(MiniGameResultType.DRAW, user, other);
                    } else {
                        other.write(MiniRoomPacket.MiniGame.tieResult());
                    }
                }
            }
            case MGRP_GiveUpRequest -> {
                if (isGameOn()) {
                    setNextTurn(getUserIndex(user));
                    broadcastPacket(MiniRoomPacket.gameMessage(MiniGameMessageType.UserGiveUp, user.getCharacterName()));
                    gameSet(MiniGameResultType.GIVEUP, other, user);
                }
            }
            case MGRP_LeaveEngage -> {
                if (isGameOn()) {
                    leaveBooked.add(user);
                    broadcastPacket(MiniRoomPacket.gameMessage(MiniGameMessageType.UserLeaveEngage, user.getCharacterName()));
                }
            }
            case MGRP_LeaveEngageCancel -> {
                if (isGameOn()) {
                    leaveBooked.remove(user);
                    broadcastPacket(MiniRoomPacket.gameMessage(MiniGameMessageType.UserLeaveEngageCancel, user.getCharacterName()));
                }
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
                if (isGameOn()) {
                    log.error("Tried to ban user during game");
                    return;
                }
                setLeaveRequest(other, MiniRoomLeaveType.Kicked);
            }
            case MGRP_TimeOver -> {
                setNextTurn(getUserIndex(other));
                broadcastPacket(MiniRoomPacket.MiniGame.timeOver(getNextTurn()));
            }
            default -> {
                log.error("Unhandled mini game room action {}", mrp);
            }
        }
    }

    @Override
    public void leaveUnsafe(User user, MiniRoomLeaveType leaveType) {
        assert user.isLocked();
        final User other = getOther(user);
        if (other != null && isGameOn()) {
            try (var lockedOther = other.acquire()) {
                MiniGameRecord.processResult(getType(), other.getMiniGameRecord(), user.getMiniGameRecord(), false, isScorePenalty());
            }
            broadcastPacket(MiniRoomPacket.MiniGame.gameResult(MiniGameResultType.GIVEUP, this, getUserIndex(other)));
            setGameOn(false);
            setReady(false);
        }
        setLeaveRequest(user, MiniRoomLeaveType.UserRequest);
        leaveBooked.clear();
    }

    @Override
    public void updateBalloon() {
        getField().broadcastPacket(UserPacket.userMiniRoomBalloon(getUser(0), this));
    }

    protected final void gameSet(MiniGameResultType resultType, User winner, User loser) {
        final boolean isDraw = resultType == MiniGameResultType.DRAW;
        final boolean isScorePenalty = resultType == MiniGameResultType.GIVEUP && isScorePenalty();
        ServerExecutor.submit(winner, () -> {
            try (var lockedRoom = this.acquire()) {
                try (var lockedWinner = winner.acquire()) {
                    try (var lockedLoser = loser.acquire()) {
                        MiniGameRecord.processResult(getType(), winner.getMiniGameRecord(), loser.getMiniGameRecord(), isDraw, isScorePenalty);
                    }
                }
                broadcastPacket(MiniRoomPacket.MiniGame.gameResult(resultType, this, getUserIndex(winner)));
                setGameOn(false);
                setReady(false);
                for (User leaver : leaveBooked) {
                    setLeaveRequest(leaver, MiniRoomLeaveType.UserRequest);
                }
                leaveBooked.clear();
            }
        });
    }
}
