package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class OmokGameRoom extends MiniGameRoom {
    private OmokGame omokGame;

    public OmokGameRoom(String title, String password, int gameSpec, User owner) {
        super(title, password, gameSpec, owner);
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.OMOK_ROOM;
    }

    @Override
    public boolean isScorePenalty() {
        return omokGame != null && omokGame.isScorePenalty();
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
        final User other = isOwner(user) ? getGuest() : getOwner();
        if (other == null) {
            log.error("Received omok game room action {} without a guest in the room", mrp);
            return;
        }
        switch (mrp) {
            case MGRP_RetreatRequest -> {
                other.write(MiniRoomPacket.MiniGame.retreatRequest());
            }
            case MGRP_RetreatResult -> {
                if (inPacket.decodeBoolean()) {
                    final int count = omokGame.retreat();
                    if (count % 2 != 0) {
                        setNextTurn(getNextTurn() == 0 ? 1 : 0);
                    }
                    broadcastPacket(MiniRoomPacket.gameMessage(GameMessageType.USER_RETREAT_SUCCESS, user.getCharacterName()));
                    broadcastPacket(MiniRoomPacket.MiniGame.retreatResult(true, count, getNextTurn()));
                } else {
                    other.write(MiniRoomPacket.MiniGame.retreatResult(false, -1, -1));
                }
            }
            case MGRP_Start -> {
                if (!isOpen() || !isReady() || !isOwner(user)) {
                    log.error("Tried to start omok game without meeting the requirements");
                    return;
                }
                omokGame = new OmokGame();
                setOpen(false);
                updateBalloon();
                broadcastPacket(MiniRoomPacket.MiniGame.omokStart(getNextTurn() == 0 ? 1 : 0));
            }
            case ORP_PutStoneChecker -> {
                final int x = inPacket.decodeInt();
                final int y = inPacket.decodeInt();
                final int type = inPacket.decodeByte();
                // Check if move is valid
                if (!omokGame.isValid(x, y)) {
                    user.write(MiniRoomPacket.MiniGame.invalidStonePosition(MiniRoomProtocol.ORP_InvalidStonePosition_Normal));
                    return;
                }
                // Check for three-three, unless it blocks a threat
                if (omokGame.checkThreeThree(x, y, type) && !omokGame.checkWin(x, y, type == 1 ? 2 : 1)) {
                    user.write(MiniRoomPacket.MiniGame.invalidStonePosition(MiniRoomProtocol.ORP_InvalidStonePosition_By33));
                    return;
                }
                // Place stone and check win
                omokGame.putStone(x, y, type);
                setNextTurn(getPosition(other));
                broadcastPacket(MiniRoomPacket.MiniGame.putStoneChecker(x, y, type));
                if (omokGame.checkWin(x, y, type)) {
                    try (var lockedOther = other.acquire()) {
                        gameResult(GameResultType.NORMAL, user, other);
                    }
                }
            }
            default -> {
                super.handlePacket(locked, mrp, inPacket);
            }
        }
    }
}
