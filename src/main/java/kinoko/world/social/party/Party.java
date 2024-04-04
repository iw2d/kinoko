package kinoko.world.social.party;

import kinoko.server.node.RemoteUser;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public final class Party implements Encodable, Lockable<Party> {
    private static final RemoteUser EMPTY_MEMBER = new RemoteUser(0, 0, 0, 0, "", 0, 0, 0);
    private final Lock lock = new ReentrantLock();
    private final int partyId;
    private final List<RemoteUser> partyMembers = new ArrayList<>();
    private int partyBossId;

    public Party(int partyId, RemoteUser remoteUser) {
        this.partyId = partyId;
        this.partyMembers.add(remoteUser);
        this.partyBossId = remoteUser.getCharacterId();
    }

    public int getPartyId() {
        return partyId;
    }

    public boolean addMember(RemoteUser remoteUser) {
        if (partyMembers.size() >= GameConstants.PARTY_MAX) {
            return false;
        }
        for (RemoteUser member : partyMembers) {
            if (member.getCharacterId() == remoteUser.getCharacterId()) {
                return false;
            }
        }
        partyMembers.add(remoteUser);
        return true;
    }

    public boolean removeMember(RemoteUser remoteUser) {
        return partyMembers.removeIf((member) -> member.getCharacterId() == remoteUser.getCharacterId());
    }

    public int getPartyBossId() {
        return partyBossId;
    }

    public boolean setPartyBossId(int currentBossId, int newBossId) {
        if (partyBossId != 0 && partyBossId != currentBossId) {
            return false;
        }
        if (!hasMember(newBossId)) {
            return false;
        }
        this.partyBossId = newBossId;
        return true;
    }

    public boolean hasMember(int characterId) {
        return partyMembers.stream().anyMatch((member) -> member.getCharacterId() == characterId);
    }

    public void updateMember(RemoteUser remoteUser) {
        for (int i = 0; i < GameConstants.PARTY_MAX; i++) {
            if (i >= partyMembers.size()) {
                break;
            }
            if (partyMembers.get(i).getCharacterId() == remoteUser.getCharacterId()) {
                partyMembers.set(i, remoteUser);
            }
        }
    }

    public void forEachMember(Consumer<RemoteUser> consumer) {
        for (RemoteUser member : partyMembers) {
            consumer.accept(member);
        }
    }

    public void forEachMemberForPartyData(Consumer<RemoteUser> consumer) {
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
        forEachMemberForPartyData((member) -> outPacket.encodeInt(member.getCharacterId())); // adwCharacterID
        forEachMemberForPartyData((member) -> outPacket.encodeString(member.getCharacterName(), 13)); // asCharacterName
        forEachMemberForPartyData((member) -> outPacket.encodeInt(member.getJob())); // anJob
        forEachMemberForPartyData((member) -> outPacket.encodeInt(member.getLevel())); // anLevel
        forEachMemberForPartyData((member) -> outPacket.encodeInt(member.getChannelId())); // anChannelID
        outPacket.encodeInt(partyBossId); // dwPartyBossCharacterID
        forEachMemberForPartyData((member) -> outPacket.encodeInt(member.getFieldId())); // adwFieldID
        forEachMemberForPartyData((member) -> TownPortal.EMPTY_PORTAL.encodeForPartyData(outPacket)); // aTownPortal
        forEachMemberForPartyData((member) -> outPacket.encodeInt(0)); // aPQReward
        forEachMemberForPartyData((member) -> outPacket.encodeInt(0)); // aPQRewardType
        outPacket.encodeInt(0); // dwPQRewardMobTemplateID
        outPacket.encodeInt(0); // bPQReward
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
