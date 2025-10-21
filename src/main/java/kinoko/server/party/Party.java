package kinoko.server.party;

import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteTownPortal;
import kinoko.server.user.RemoteUser;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;
import kinoko.world.user.PartyInfo;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Party instance managed by CentralServerNode. RemoteUser instances managed by this object should always be pointing to
 * the instance stored in UserStorage.
 */
public final class Party implements Encodable, Lockable<Party> {
    private static final RemoteUser EMPTY_MEMBER = new RemoteUser(0, 0, "", 0, 0, GameConstants.CHANNEL_OFFLINE, GameConstants.UNDEFINED_FIELD_ID, 0, 0, 0, 0, RemoteTownPortal.EMPTY);
    private final Lock lock = new ReentrantLock();
    private final int partyId;
    private final List<RemoteUser> partyMembers;
    private final Map<Integer, Integer> partyInvites; // invitee ID -> inviter ID
    private int partyBossId;

    public Party(int partyId, RemoteUser remoteUser) {
        this.partyId = partyId;
        this.partyMembers = new ArrayList<>(GameConstants.PARTY_MAX);
        this.partyMembers.add(remoteUser);
        this.partyInvites = new HashMap<>();
        this.partyBossId = remoteUser.getCharacterId();
    }

    public int getPartyId() {
        return partyId;
    }

    public boolean canAddMember(RemoteUser remoteUser) {
        if (partyMembers.size() >= GameConstants.PARTY_MAX) {
            return false;
        }
        for (RemoteUser member : partyMembers) {
            if (member.getCharacterId() == remoteUser.getCharacterId()) {
                return false;
            }
        }
        return true;
    }

    public boolean addMember(RemoteUser remoteUser) {
        if (!canAddMember(remoteUser)) {
            return false;
        }
        partyMembers.add(remoteUser);
        return true;
    }

    public boolean removeMember(RemoteUser remoteUser) {
        return partyMembers.removeIf((member) -> member.getCharacterId() == remoteUser.getCharacterId());
    }

    public void registerInvite(int inviterId, int targetId) {
        partyInvites.put(targetId, inviterId);
    }

    public boolean unregisterInvite(int inviterId, int targetId) {
        return partyInvites.remove(targetId) == inviterId;
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

    public Optional<RemoteUser> getMember(int characterId) {
        return partyMembers.stream()
                .filter((member) -> member.getCharacterId() == characterId)
                .findFirst();
    }

    public int getMemberIndex(RemoteUser remoteUser) {
        for (int i = 0; i < GameConstants.PARTY_MAX; i++) {
            if (i >= partyMembers.size()) {
                break;
            }
            if (partyMembers.get(i).getCharacterId() == remoteUser.getCharacterId()) {
                return i + 1; // used for affectedMemberBitMap
            }
        }
        return 0;
    }

    public boolean hasMember(int characterId) {
        return getMember(characterId).isPresent();
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

    public PartyInfo createInfo(RemoteUser remoteUser) {
        return new PartyInfo(partyId, getMemberIndex(remoteUser), partyBossId == remoteUser.getCharacterId());
    }

    public void forEachMember(Consumer<RemoteUser> consumer) {
        for (RemoteUser member : partyMembers) {
            consumer.accept(member);
        }
    }

    private void forEachMemberForPartyData(Consumer<RemoteUser> consumer) {
        for (int i = 0; i < GameConstants.PARTY_MAX; i++) {
            if (i < partyMembers.size()) {
                consumer.accept(partyMembers.get(i));
            } else {
                consumer.accept(EMPTY_MEMBER);
            }
        }
    }

    @Override
    public String toString() {
        return "Party{" +
                "partyId=" + partyId +
                ", partyMembers=" + partyMembers +
                ", partyBossId=" + partyBossId +
                '}';
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
        forEachMemberForPartyData((member) -> member.getTownPortal().encode(outPacket)); // aTownPortal
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
