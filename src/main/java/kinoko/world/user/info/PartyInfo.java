package kinoko.world.user.info;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class PartyInfo implements Encodable {
    public static final PartyInfo EMPTY = new PartyInfo(0, 0, false);
    private final int partyId;
    private final int memberIndex;
    private final boolean boss;

    public PartyInfo(int partyId, int memberIndex, boolean boss) {
        this.partyId = partyId;
        this.memberIndex = memberIndex;
        this.boss = boss;
    }

    public int getPartyId() {
        return partyId;
    }

    public int getMemberIndex() {
        return memberIndex;
    }

    public boolean isBoss() {
        return boss;
    }

    @Override
    public String toString() {
        return "PartyInfo{" +
                "partyId=" + partyId +
                ", memberIndex=" + memberIndex +
                ", boss=" + boss +
                '}';
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(partyId);
        outPacket.encodeByte(memberIndex);
        outPacket.encodeByte(boss);
    }

    public static PartyInfo decode(InPacket inPacket) {
        final int partyId = inPacket.decodeInt();
        final int memberIndex = inPacket.decodeByte();
        final boolean boss = inPacket.decodeBoolean();
        return new PartyInfo(partyId, memberIndex, boss);
    }
}
