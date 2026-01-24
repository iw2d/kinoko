package kinoko.packet.stage;

public enum ViewAllCharOpt {
    DETAIL_INFO(0),
    SUMMARY_INFO(1),
    ALREADY_CONNECTED_TO_SERVER(2),
    // The client processing is consistent across values 3, 6, and 7; here, value 3 is chosen.
    ERROR_OCCURRED(3),
    // The client processing is consistent across values 4 and 5; here, value 4 is chosen.
    WAITING_TO_RECEIVE_DATA_OR_EMPTY_DATA(4);
    private final int value;

    ViewAllCharOpt(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
