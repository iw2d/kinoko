package kinoko.server.guild;

import kinoko.database.DatabaseManager;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;
import kinoko.util.Encodable;
import kinoko.util.Lockable;
import kinoko.world.GameConstants;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Guild instance managed by CentralServerNode.
 */
public final class Guild implements Encodable, Lockable<Guild> {
    public static final GuildMember EMPTY_MEMBER = new GuildMember(0, "", 0, 0, false, GuildRank.NONE, GuildRank.NONE);
    public static final Comparator<GuildMember> MEMBER_COMPARATOR = Comparator.comparing(GuildMember::getGuildRank)
            .thenComparing(Comparator.comparing(GuildMember::getLevel).reversed());

    private final Lock lock = new ReentrantLock();
    private int guildId;
    private final String guildName;
    private final List<String> gradeNames;
    private final Map<Integer, GuildMember> guildMembers; // character ID -> guild member
    private final Map<Integer, Integer> guildInvites; // invitee ID -> inviter ID
    private final List<GuildBoardEntry> boardEntries;
    private GuildBoardEntry boardNoticeEntry;
    private AtomicInteger boardEntryCounter;
    private int memberMax;

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
        this.gradeNames = new ArrayList<>(GameConstants.GUILD_GRADE_NAMES);
        this.guildMembers = new HashMap<>();
        this.guildInvites = new HashMap<>();
        this.boardEntries = new ArrayList<>();
        this.boardEntryCounter = new AtomicInteger(1);
        this.memberMax = GameConstants.GUILD_CAPACITY_MIN;
        this.level = 1;
    }

    public int getGuildId() {
        return guildId;
    }

    public void setGuildId(int newGuildId) {
        this.guildId = newGuildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public List<String> getGradeNames() {
        return gradeNames;
    }

    public void setGradeNames(List<String> gradeNames) {
        for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
            this.gradeNames.set(i, gradeNames.get(i));
        }
    }

    public List<GuildMember> getGuildMembers() {
        return guildMembers.values().stream().sorted(MEMBER_COMPARATOR) // sort by rank, then level
                .toList();
    }

    public List<GuildBoardEntry> getBoardEntries() {
        return boardEntries;
    }

    public GuildBoardEntry getBoardNoticeEntry() {
        return boardNoticeEntry;
    }

    public void setBoardNoticeEntry(GuildBoardEntry boardNoticeEntry) {
        this.boardNoticeEntry = boardNoticeEntry;
    }

    public AtomicInteger getBoardEntryCounter() {
        return boardEntryCounter;
    }

    public void setBoardEntryCounter(AtomicInteger boardEntryCounter) {
        this.boardEntryCounter = boardEntryCounter;
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


    // BBS METHODS -----------------------------------------------------------------------------------------------------

    public Optional<GuildBoardEntry> getBoardEntry(int entryId) {
        if (entryId == 0 || (boardNoticeEntry != null && boardNoticeEntry.getEntryId() == entryId)) {
            return Optional.ofNullable(boardNoticeEntry);
        }
        for (GuildBoardEntry entry : boardEntries) {
            if (entry.getEntryId() == entryId) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public void addBoardEntry(GuildBoardEntry entry) {
        boardEntries.add(entry);
    }

    public void removeBoardEntry(int entryId) {
        if (entryId == 0 || (boardNoticeEntry != null && boardNoticeEntry.getEntryId() == entryId)) {
            boardNoticeEntry = null;
            return;
        }
        boardEntries.removeIf((entry) -> entry.getEntryId() == entryId);
    }

    public List<GuildBoardEntry> getBoardEntryList(int page) {
        final int start = page * 10;
        final int end = Math.min(boardEntries.size(), start + 10);
        if (start > end) {
            return List.of();
        }
        return boardEntries.subList(start, end);
    }

    public int getNextBoardEntryId() {
        //  Board Entries SN are composite keys with Guild IDs, so we don't need to return -1 for Relational DBs.
        return boardEntryCounter.getAndIncrement();
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void registerInvite(int inviterId, int targetId) {
        guildInvites.put(targetId, inviterId);
    }

    public boolean unregisterInvite(int inviterId, int targetId) {
        return guildInvites.remove(targetId) == inviterId;
    }

    public List<Integer> getMemberIds() {
        return guildMembers.keySet().stream().toList();
    }

    public List<Integer> getMemberIds(int exceptId) {
        return guildMembers.keySet().stream()
                .filter((id) -> id != exceptId)
                .toList();
    }

    public boolean hasMember(int characterId) {
        return guildMembers.containsKey(characterId);
    }

    public GuildMember getMember(int characterId) {
        final GuildMember member = guildMembers.get(characterId);
        return member != null ? member : EMPTY_MEMBER;
    }

    public boolean canAddMember(int characterId) {
        if (guildMembers.size() >= getMemberMax()) {
            return false;
        }
        return !guildMembers.containsKey(characterId);
    }

    public boolean addMember(GuildMember guildMember) {
        if (!canAddMember(guildMember.getCharacterId())) {
            return false;
        }
        guildMembers.put(guildMember.getCharacterId(), guildMember);
        return true;
    }

    public void removeMember(GuildMember member) {
        guildMembers.remove(member.getCharacterId());
    }

    public void updateMember(RemoteUser remoteUser) {
        final GuildMember member = guildMembers.get(remoteUser.getCharacterId());
        if (member != null) {
            member.setJob(remoteUser.getJob());
            member.setLevel(remoteUser.getLevel());
            member.setOnline(remoteUser.getChannelId() != GameConstants.CHANNEL_OFFLINE);
        }
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GUILDDATA::Decode
        outPacket.encodeInt(guildId); // nGuildID
        outPacket.encodeString(guildName); // sGuildName
        for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
            outPacket.encodeString(gradeNames.get(i)); // asGradeName
        }

        final List<GuildMember> members = getGuildMembers();
        outPacket.encodeByte(members.size());
        for (GuildMember member : members) {
            outPacket.encodeInt(member.getCharacterId()); // adwCharacterID
        }
        for (GuildMember member : members) {
            member.encode(outPacket); // aMemberData (37)
        }

        outPacket.encodeInt(memberMax); // nMaxMemberNum
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
