package kinoko.server.alliance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kinoko.server.guild.Guild;
import static kinoko.server.guild.Guild.EMPTY_MEMBER;
import static kinoko.server.guild.Guild.MEMBER_COMPARATOR;
import kinoko.server.guild.GuildMember;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;

import java.util.*;

/**
 * Alliance instance managed by CentralServerNode.
 */
public final class Alliance implements Encodable, Lockable<Alliance> {

	private final int allianceId;
    private final String allianceName;
    private int allianceLordId;
    private final List<String> gradeNames;
    private int memberMax;
    private String notice;
    
    private final Map<Integer, Integer> guildInvites; // invitee ID -> inviter ID
    private final Map<Integer, Guild> guilds; // guild ID -> guild
    
    private final Lock lock = new ReentrantLock();
    
    public Alliance(int allianceId, String allianceName, int allianceLordId) {
        this.allianceId = allianceId;
        this.allianceName = allianceName;
        this.allianceLordId = allianceLordId;
        this.gradeNames = new ArrayList<>(GameConstants.GUILD_GRADE_NAMES);
        this.guildInvites = new HashMap<>();
        this.guilds = new HashMap<>();
        this.memberMax = GameConstants.UNION_CAPACITY_MIN;
        this.notice = "";
    }
    
    public int getMemberMax() {
        return memberMax;
    }

    public void setMemberMax(int memberMax) {
        this.memberMax = memberMax;
    }
    
    public int getAllianceId() {
        return allianceId;
    }

    public String getAllianceName() {
        return allianceName;
    }
    
    public int getLordId() {
        return allianceLordId;
    }
    
    public void setLordId(int characterId) {
        allianceLordId = characterId;
    }
    
    public List<GuildMember> getAllianceMembers() {
        List<GuildMember> list = new ArrayList<>(100);
        
        for (Guild guild : guilds.values()) {
            list.addAll(guild.getGuildMembersUnsorted());
        }
        
        return list.stream().sorted(MEMBER_COMPARATOR) // sort by rank, then level
                .toList();
    }
    
    public List<GuildMember> getMemberIds() {
        return getAllianceMembers().stream().toList();
    }

    public List<GuildMember> getMemberIds(int exceptId) {
        return getAllianceMembers().stream()
                .filter((member) -> member.getCharacterId() != exceptId)
                .toList();
    }

    public boolean hasMember(int characterId) {
        for (Guild guild : guilds.values()) {
            if (guild.hasMember(characterId)) {
                return true;
            }
        }
        
        return false;
    }

    public GuildMember getMember(int characterId) {
        for (Guild guild : guilds.values()) {
            GuildMember member = guild.getMember(characterId);
            if (member != EMPTY_MEMBER) {
                return member;
            }
        }
        
        return EMPTY_MEMBER;
    }
    
    public boolean hasGuild(int guildId) {
        return guilds.containsKey(guildId);
    }

    public Guild getGuild(int guildId) {
        return guilds.get(guildId);
    }

    public boolean canAddGuild(int guildId) {
        if (guilds.size() >= getMemberMax()) {
            return false;
        }
        return !guilds.containsKey(guildId);
    }
    
    public boolean addGuild(Guild guild) {
    	this.lock();
    	try {
    		if (!canAddGuild(guild.getGuildId())) {
                return false;
            }
            guilds.put(guild.getGuildId(), guild);
            return true;
    	} finally {
    		this.unlock();
    	}
    }

    public void removeGuild(Guild guild) {
    	this.lock();
    	try {
    		guilds.remove(guild.getGuildId());
    	} finally {
    		this.unlock();
    	}
    }

    public String getGradeNames(int rank) {
        return gradeNames.get(rank - 1);
    }
    
    public String setGradeName(int rank, String name) {
        return gradeNames.set(rank - 1, name);
    }
    
    public String getNotice() {
        return notice;
    }
    
    public void setNotice(String str) {
        notice = str;
    }

    public boolean addGuild(int guildId, Guild guild) {
        if (canAddGuild(guildId)) {
            return false;
        }
        guilds.put(guildId, guild);
        return true;
    }

    public boolean removeGuild(int guildId) {
        return guilds.remove((Integer) guildId) != null;
    }

    public List<Integer> getGuilds() {
        return Collections.unmodifiableList(guilds.keySet().stream().toList());
    }

    public void registerInvite(int inviterId, int targetId) {
        guildInvites.put(targetId, inviterId);
    }

    public boolean unregisterInvite(int inviterId, int targetId) {
        return guildInvites.remove(targetId) == inviterId;
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
        for (int guildId : guilds.keySet()) {
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
