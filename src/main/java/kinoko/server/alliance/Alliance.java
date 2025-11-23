package kinoko.server.alliance;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Alliance instance managed by CentralServerNode.
 */
public final class Alliance implements Encodable, Lockable<Alliance> {
    private final Lock lock = new ReentrantLock();
    private final int allianceId;
    private final String allianceName;
    private final List<String> gradeNames;
    private final List<Integer> guilds;
    private final Map<Integer, Integer> guildInvites; // invitee ID -> inviter ID
    private int memberMax;
    private String notice;

    public Alliance(int allianceId, String allianceName) {
        this.allianceId = allianceId;
        this.allianceName = allianceName;
        this.gradeNames = GameConstants.GUILD_GRADE_NAMES;
        this.guilds = new ArrayList<>();
        this.guildInvites = new HashMap<>();
        this.memberMax = GameConstants.UNION_CAPACITY_MIN;
    }

    public int getAllianceId() {
        return allianceId;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public boolean canAddGuild(int guildId) {
        if (guilds.size() >= getMemberMax()) {
            return false;
        }
        return !guilds.contains(guildId);
    }

    public boolean addGuild(int guildId) {
        if (canAddGuild(guildId)) {
            return false;
        }
        guilds.add(guildId);
        return true;
    }

    public boolean removeGuild(int guildId) {
        return guilds.remove((Integer) guildId);
    }

    public List<Integer> getGuilds() {
        return Collections.unmodifiableList(guilds);
    }

    public void registerInvite(int inviterId, int targetId) {
        guildInvites.put(targetId, inviterId);
    }

    public boolean unregisterInvite(int inviterId, int targetId) {
        return guildInvites.remove(targetId) == inviterId;
    }

    public int getMemberMax() {
        return memberMax;
    }

    public void setMemberMax(int memberMax) {
        this.memberMax = memberMax;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // ALLIANCEDATA::Decode
        outPacket.encodeInt(allianceId); // nAllianceID
        outPacket.encodeString(allianceName); // sAllianceName
        for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
            outPacket.encodeString(gradeNames.get(i)); // asGradeName
        }
        outPacket.encodeByte(guilds.size()); // adwGuildID
        for (int guildId : guilds) {
            outPacket.encodeInt(guildId);
        }
        outPacket.encodeInt(memberMax); // nMaxMemberNum
        outPacket.encodeString(notice);
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
