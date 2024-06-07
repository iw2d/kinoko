package kinoko.packet.stage;

public enum LoginResultType {
    // LoginResCode
    Success(0),
    TempBlocked(1),
    Blocked(2),
    Abandoned(3),
    IncorrectPassword(4),
    NotRegistered(5),
    DBFail(6),
    AlreadyConnected(7),
    NotConnectableWorld(8),
    Unknown(9),
    Timeout(10),
    NotAdult(11),
    AuthFail(12),
    ImpossibleIP(13),
    NotAuthorizedNexonID(14),
    NoNexonID(15),
    NotAuthorized(16),
    InvalidRegionInfo(17),
    InvalidBirthDate(18),
    PassportSuspended(19),
    IncorrectSSN2(20),
    IncorrectSPW(20),
    WebAuthNeeded(21),
    DeleteCharacterFailedOnGuildMaster(22),
    SamePasswordAndSPW(22),
    NotagreedEULA(23),
    SamePincodeAndSPW(23),
    DeleteCharacterFailedEngaged(24),
    RegisterLimitedIP(25),
    RequestedCharacterTransfer(26),
    CashUserCannotUserSimpleClient(27),
    DeleteCharacterFailedOnFamily(29),
    InvalidCharacterName(30),
    IncorrectSSN(31),
    SSNConfirmFailed(32),
    SSNNotConfirmed(33),
    WorldTooBusy(34),
    OTPReissuing(35),
    OTPInfoNotExist(36),
    ProcFail(-1);

    private final int value;

    LoginResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
