package kinoko.server.memo;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;

public final class Memo implements Encodable {
    private final MemoType type;
    private final int memoId;
    private final String sender;
    private final String content;
    private final Instant dateSent;

    public Memo(MemoType type, int memoId, String sender, String content, Instant dateSent) {
        this.type = type;
        this.memoId = memoId;
        this.sender = sender;
        this.content = content;
        this.dateSent = dateSent;
    }

    public MemoType getType() {
        return type;
    }

    public int getMemoId() {
        return memoId;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Instant getDateSent() {
        return dateSent;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(memoId); // dwSN
        outPacket.encodeString(sender); // sSender
        outPacket.encodeString(content); // sContent
        outPacket.encodeFT(dateSent); // dateSent
        outPacket.encodeByte(type.getValue()); // nFlag
    }
}
