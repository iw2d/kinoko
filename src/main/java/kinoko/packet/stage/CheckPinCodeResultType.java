package kinoko.packet.stage;

public enum CheckPinCodeResultType {
    Cancel(-1), // This value does not exist on the client side; it is only used on the server side to indicate a cancellation operation.
    Done(0), // In the client, when CheckPinCodeResultType =0 and m_nGameStartMode != 1, a WorldRequest packet will be sent.
    CreateOrUpdate(1),
    CheckInvalid(2),
    CheckTooMuchInvalid(3),
    RequestToEnter(4),
    // no have 5,6 value
    AccountAlreadyLogged(7),
    ;

    private final int value;

    CheckPinCodeResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
