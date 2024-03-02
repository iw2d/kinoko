package kinoko.server.memo;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.List;

public final class MemoResult implements Encodable {
    private final MemoResultType type;

    private List<Memo> memos;
    private int warningType;

    public MemoResult(MemoResultType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case LOAD -> {
                outPacket.encodeByte(memos.size());
                for (Memo memo : memos) {
                    memo.encode(outPacket);
                }
            }
            case SEND_WARNING -> {
                outPacket.encodeByte(warningType);
            }
        }
    }

    public static MemoResult load(List<Memo> memos) {
        final MemoResult result = new MemoResult(MemoResultType.SEND_WARNING);
        result.memos = memos; // lReceivedMemo
        return result;
    }

    public static MemoResult sendSucceed() {
        return new MemoResult(MemoResultType.SEND_SUCCEED);
    }

    public static MemoResult sendWarningOnline() {
        final MemoResult result = new MemoResult(MemoResultType.SEND_WARNING);
        result.warningType = 0; // The other character is online now.\r\nPlease use the whisper function%2C
        return result;
    }

    public static MemoResult sendWarningName() {
        final MemoResult result = new MemoResult(MemoResultType.SEND_WARNING);
        result.warningType = 1; // Please check the name of the receiving character.
        return result;
    }

    public static MemoResult sendWarningFull() {
        final MemoResult result = new MemoResult(MemoResultType.SEND_WARNING);
        result.warningType = 2; // The receiver's inbox is full.\r\nPlease try again.
        return result;
    }
}
