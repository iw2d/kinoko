package kinoko.world.dialog.miniroom;

public enum GameMessageType {
    // MiniGame
    UserBan(0),
    UserTurn(1),
    UserGiveUp(2),
    UserRetreatSuccess(3),
    UserLeave(4),
    UserLeaveEngage(5),
    UserLeaveEngageCancel(6),
    UserEnter(7),
    UserNotEnoughMoney(8),
    UserMatchCard(9),
    MiniGame_10SecAlert(101),
    GameStart(102),
    TournamentMatchEnd(103);

    private final int value;

    GameMessageType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
