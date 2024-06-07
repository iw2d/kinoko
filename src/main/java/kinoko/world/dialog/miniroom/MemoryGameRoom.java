package kinoko.world.dialog.miniroom;

import kinoko.packet.field.MiniRoomPacket;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class MemoryGameRoom extends MiniGameRoom {
    private MemoryGame memoryGame;
    private int firstCard;

    public MemoryGameRoom(String title, String password, int gameSpec, User owner) {
        super(title, password, gameSpec, owner);
    }

    @Override
    public MiniRoomType getType() {
        return MiniRoomType.MemoryGameRoom;
    }

    @Override
    public boolean isScorePenalty() {
        return memoryGame != null && memoryGame.isScorePenalty();
    }

    @Override
    public void handlePacket(Locked<User> locked, MiniRoomProtocol mrp, InPacket inPacket) {
        final User user = locked.get();
        final User other = isOwner(user) ? getGuest() : getOwner();
        if (other == null) {
            log.error("Received memory game room action {} without a guest in the room", mrp);
            return;
        }
        switch (mrp) {
            case MGRP_Start -> {
                if (!isOpen() || !isReady() || !isOwner(user)) {
                    log.error("Tried to start memory game without meeting the requirements");
                    return;
                }
                memoryGame = new MemoryGame(getGameSpec());
                setOpen(false);
                updateBalloon();
                broadcastPacket(MiniRoomPacket.MiniGame.memoryGameStart(getNextTurn() == 0 ? 1 : 0, memoryGame.getShuffle()));
            }
            case MGP_TurnUpCard -> {
                final boolean isFirst = inPacket.decodeBoolean();
                final int cardIndex = inPacket.decodeByte();
                if (isFirst) {
                    firstCard = cardIndex;
                    other.write(MiniRoomPacket.MiniGame.turnUpCard(cardIndex));
                } else {
                    final MemoryGame.TurnUpResult result = memoryGame.turnUpCard(firstCard, cardIndex, getPosition(user));
                    broadcastPacket(MiniRoomPacket.MiniGame.turnUpCard(firstCard, cardIndex, getPosition(user), result != MemoryGame.TurnUpResult.NO_MATCH));
                    try (var lockedOther = other.acquire()) {
                        switch (result) {
                            case NO_MATCH -> {
                                setNextTurn(getPosition(other));
                            }
                            case WIN -> {
                                gameResult(GameResultType.NORMAL, user, other);
                            }
                            case DRAW -> {
                                gameResult(GameResultType.DRAW, user, other);
                            }
                            case LOSE -> {
                                gameResult(GameResultType.NORMAL, other, user);
                            }
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
