package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class OmokRoom extends MiniGameRoom {
    private OmokGame omokGame;

    public OmokRoom(String title, String password, int gameSpec) {
        super(title, password, gameSpec);
    }

    @Override
    public boolean isScorePenalty() {
        return omokGame != null && omokGame.isScorePenalty();
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.OmokRoom;
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
        final User other = getOther(user);
        if (other == null) {
            log.error("Received mini room action {} without another player in the omok game room", mrp);
            return;
        }
        switch (mrp) {
            case MGRP_RetreatRequest -> {
                if (isGameOn()) {
                    other.write(MiniRoomPacket.MiniGame.retreatRequest());
                }
            }
            case MGRP_RetreatResult -> {
                if (isGameOn()) {
                    if (inPacket.decodeBoolean()) {
                        final int count = omokGame.retreat();
                        if (count % 2 != 0) {
                            setNextTurn(getNextTurn() == 0 ? 1 : 0);
                        }
                        broadcastPacket(MiniRoomPacket.gameMessage(MiniGameMessageType.UserRetreatSuccess, user.getCharacterName()));
                        broadcastPacket(MiniRoomPacket.MiniGame.retreatResult(true, count, getNextTurn()));
                    } else {
                        other.write(MiniRoomPacket.MiniGame.retreatResult(false, -1, -1));
                    }
                }
            }
            case MGRP_Start -> {
                if (isGameOn() || !isReady() || !isOwner(user)) {
                    log.error("Tried to start omok game without meeting the requirements");
                    return;
                }
                omokGame = new OmokGame();
                setGameOn(true);
                updateBalloon();
                broadcastPacket(MiniRoomPacket.gameMessage(MiniGameMessageType.GameStart, ""));
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
                setNextTurn(getUserIndex(other));
                broadcastPacket(MiniRoomPacket.MiniGame.putStoneChecker(x, y, type));
                if (omokGame.checkWin(x, y, type)) {
                    gameSet(MiniGameResultType.NORMAL, user, other);
                }
            }
            default -> {
                super.handlePacket(locked, mrp, inPacket);
            }
        }
    }
}
