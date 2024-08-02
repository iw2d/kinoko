package kinoko.server.guild;

import kinoko.server.packet.OutPacket;
import kinoko.server.party.Party;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Guild instance managed by CentralServerNode. RemoteUser instances managed by this object should always be pointing to
 * the instance stored in UserStorage.
 */
public final class Guild implements Encodable, Lockable<Party> {
    private final Lock lock = new ReentrantLock();
    private final int guildId;
    private final String guildName;
    private List<String> gradeNames;
    private Map<Integer, GuildMember> guildMembers;
    private int maxMemberNum;

    private int allianceId;
    private String allianceName;

    private short markBg;
    private byte markBgColor;
    private short mark;
    private byte markColor;

    private String notice;
    private int points;
    private byte level;


    public Guild(int guildId, String guildName) {
        this.guildId = guildId;
        this.guildName = guildName;
    }

    public int getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public List<String> getGradeNames() {
        return gradeNames;
    }

    public void setGradeNames(List<String> gradeNames) {
        this.gradeNames = gradeNames;
    }

    public Map<Integer, GuildMember> getGuildMembers() {
        return guildMembers;
    }

    public void setGuildMembers(Map<Integer, GuildMember> guildMembers) {
        this.guildMembers = guildMembers;
    }

    public int getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(int maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
    }

    public int getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(int allianceId) {
        this.allianceId = allianceId;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public void setAllianceName(String allianceName) {
        this.allianceName = allianceName;
    }

    public short getMarkBg() {
        return markBg;
    }

    public void setMarkBg(short markBg) {
        this.markBg = markBg;
    }

    public byte getMarkBgColor() {
        return markBgColor;
    }

    public void setMarkBgColor(byte markBgColor) {
        this.markBgColor = markBgColor;
    }

    public short getMark() {
        return mark;
    }

    public void setMark(short mark) {
        this.mark = mark;
    }

    public byte getMarkColor() {
        return markColor;
    }

    public void setMarkColor(byte markColor) {
        this.markColor = markColor;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GUILDDATA::Decode
        outPacket.encodeInt(guildId); // nGuildID
        outPacket.encodeString(guildName); // sGuildName
        for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
            outPacket.encodeString(gradeNames.get(i)); // asGradeName
        }

        final List<GuildMember> members = guildMembers.values().stream().toList();
        outPacket.encodeByte(members.size());
        for (GuildMember member : members) {
            outPacket.encodeInt(member.getCharacterId()); // adwCharacterID
        }
        for (GuildMember member : members) {
            member.encode(outPacket); // aMemberData (37)
        }

        outPacket.encodeInt(maxMemberNum); // nMaxMemberNum
        outPacket.encodeShort(markBg); // nMarkBg
        outPacket.encodeByte(markBgColor); // nMarkBgColor
        outPacket.encodeShort(mark); // nMark
        outPacket.encodeByte(markColor); // nMarkColor

        outPacket.encodeString(notice); // sNotice
        outPacket.encodeInt(points); // nPoint
        outPacket.encodeInt(allianceId); // nAllianceID
        outPacket.encodeByte(level); // nLevel

        // mSkillRecord - no guild skills in v95
        outPacket.encodeShort(0); // short * int (nSkillID)
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
