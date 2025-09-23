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

/**
 * Alliance instance managed by CentralServerNode.
 */
public final class Alliance implements Encodable, Lockable<Alliance> {
    
    private final int allianceId;
    private final String allianceName;
    private final List<String> gradeNames;
    private int memberMax;
    
    private final Map<Integer, Integer> guildInvites; // invitee ID -> inviter ID
    private final Map<Integer, Guild> guilds; // guild ID -> guild
    
    private final Lock lock = new ReentrantLock();
    
    public Alliance(int allianceId, String allianceName) {
        this.allianceId = allianceId;
        this.allianceName = allianceName;
        this.gradeNames = new ArrayList<>(GameConstants.GUILD_GRADE_NAMES);
        this.guildInvites = new HashMap<>();
        this.guilds = new HashMap<>();
        this.memberMax = GameConstants.GUILD_CAPACITY_MIN;
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
    
    public List<GuildMember> getAllianceMembers() {
        List<GuildMember> list = new ArrayList<>(100);
        
        for (Guild guild : guilds.values()) {
            list.addAll(guild.getGuildMembersUnsorted());
        }
        
        return list.stream().sorted(MEMBER_COMPARATOR) // sort by rank, then level
                .toList();
    }
    
    public List<Integer> getMemberIds() {
        return getAllianceMembers().keySet().stream().toList();
    }

    public List<Integer> getMemberIds(int exceptId) {
        return getAllianceMembers().keySet().stream()
                .filter((id) -> id != exceptId)
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
        if (!canAddGuild(guild.getGuildId())) {
            return false;
        }
        guilds.put(guild.getGuildId(), guild);
        return true;
    }

    public void removeGuild(Guild guild) {
        guilds.remove(guild.getGuildId());
    }

    public String getGradeNames(int rank) {
        return gradeNames.get(rank - 1);
    }
    
    @Override
    public void encode(OutPacket outPacket) {
        // TODO
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
