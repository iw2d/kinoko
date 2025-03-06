package kinoko.server.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class MemoryGameRoom extends MiniGameRoom {
    private MemoryGame memoryGame;
    private int firstCard;

    public MemoryGameRoom(String title, String password, int gameSpec) {
        super(title, password, gameSpec);
    }

    @Override
    public boolean isScorePenalty() {
        return memoryGame != null && memoryGame.isScorePenalty();
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.MemoryGameRoom;
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
        final User other = getOther(user);
        if (other == null) {
            log.error("Received mini room action {} without another player in the memory game room", mrp);
            return;
        }
        switch (mrp) {
            case MGRP_Start -> {
                if (isGameOn() || !isReady() || isOwner(user)) {
                    log.error("Tried to start memory game without meeting the requirements");
                    return;
                }
                memoryGame = new MemoryGame(getGameSpec());
                setGameOn(true);
                updateBalloon();
                broadcastPacket(MiniRoomPacket.gameMessage(MiniGameMessageType.GameStart, ""));
                broadcastPacket(MiniRoomPacket.MiniGame.memoryGameStart(getNextTurn() == 0 ? 1 : 0, memoryGame.getShuffle()));
            }
            case MGP_TurnUpCard -> {
                final boolean isFirst = inPacket.decodeBoolean();
                final int cardIndex = inPacket.decodeByte();
                if (isFirst) {
                    firstCard = cardIndex;
                    other.write(MiniRoomPacket.MiniGame.turnUpCard(cardIndex));
                } else {
                    final MemoryGame.TurnUpResult result = memoryGame.turnUpCard(firstCard, cardIndex, getUserIndex(user));
                    broadcastPacket(MiniRoomPacket.MiniGame.turnUpCard(firstCard, cardIndex, getUserIndex(user), result != MemoryGame.TurnUpResult.NO_MATCH));
                    switch (result) {
                        case NO_MATCH -> {
                            setNextTurn(getUserIndex(other));
                        }
                        case WIN -> {
                            gameSet(MiniGameResultType.NORMAL, user, other);
                        }
                        case DRAW -> {
                            gameSet(MiniGameResultType.DRAW, user, other);
                        }
                        case LOSE -> {
                            gameSet(MiniGameResultType.NORMAL, other, user);
                        }
                    }
                }
            }
            default -> {
                super.handlePacket(locked, mrp, inPacket);
            }
        }
    }
}
