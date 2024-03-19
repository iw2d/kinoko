package kinoko.world.social.party;

import kinoko.server.node.UserProxy;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Party implements Encodable {
    private static final UserProxy EMPTY_MEMBER = new UserProxy(0, 0, 0, 0, "", 0, 0);
    private final int partyId;
    private final List<UserProxy> partyMembers = new ArrayList<>();
    private UserProxy partyLeader;

    public Party(int partyId) {
        this.partyId = partyId;
    }

    public int getPartyId() {
        return partyId;
    }

    public void forEachMember(Consumer<UserProxy> consumer) {
        for (int i = 0; i < GameConstants.PARTY_MAX; i++) {
            if (i < partyMembers.size()) {
                consumer.accept(partyMembers.get(i));
            } else {
                consumer.accept(EMPTY_MEMBER);
            }
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        // PARTYDATA::Decode (378)
        forEachMember((member) -> outPacket.encodeInt(member.getCharacterId())); // adwCharacterID
        forEachMember((member) -> outPacket.encodeString(member.getCharacterName(), 13)); // asCharacterName
        forEachMember((member) -> outPacket.encodeInt(member.getJob())); // anJob
        forEachMember((member) -> outPacket.encodeInt(member.getLevel())); // anLevel
        forEachMember((member) -> outPacket.encodeInt(member.getChannelId())); // anChannelID
        outPacket.encodeInt(partyLeader.getCharacterId()); // dwPartyBossCharacterID
        forEachMember((member) -> outPacket.encodeInt(member.getFieldId())); // adwFieldID
        forEachMember((member) -> outPacket.encodeArray(new byte[20])); // aTownPortal // TODO - TownPortal::encodeForPartyData
        forEachMember((member) -> outPacket.encodeInt(0)); // aPQReward
        forEachMember((member) -> outPacket.encodeInt(0)); // aPQRewardType
        outPacket.encodeInt(0); // dwPQRewardMobTemplateID
        outPacket.encodeInt(0); // bPQReward
    }
}
