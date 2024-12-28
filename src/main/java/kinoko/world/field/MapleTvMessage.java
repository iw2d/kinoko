package kinoko.world.field;

import kinoko.world.user.AvatarLook;

import java.time.Instant;

public final class MapleTvMessage {
    private final int flag;
    private final int type;
    private final AvatarLook sender;
    private final String senderName;
    private final AvatarLook receiver;
    private final String receiverName;
    private final String s1, s2, s3, s4, s5;
    private final Instant expireTime;


    public MapleTvMessage(int flag, int type, AvatarLook sender, String senderName, AvatarLook receiver, String receiverName, String s1, String s2, String s3, String s4, String s5, Instant expireTime) {
        this.flag = flag;
        this.type = type;
        this.sender = sender;
        this.senderName = senderName;
        this.receiver = receiver;
        this.receiverName = receiverName;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.s5 = s5;
        this.expireTime = expireTime;
    }

    public int getFlag() {
        return flag;
    }

    public int getType() {
        return type;
    }

    public AvatarLook getSender() {
        return sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public AvatarLook getReceiver() {
        return receiver;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }

    public String getS3() {
        return s3;
    }

    public String getS4() {
        return s4;
    }

    public String getS5() {
        return s5;
    }

    public Instant getExpireTime() {
        return expireTime;
    }
}
