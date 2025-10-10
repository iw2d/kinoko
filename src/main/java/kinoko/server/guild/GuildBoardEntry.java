package kinoko.server.guild;

import kinoko.database.DatabaseManager;
import kinoko.server.packet.OutPacket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class GuildBoardEntry {
    private int entryId;
    private final int characterId;
    private String title;
    private String text;
    private Instant date;
    private int emoticon;
    private List<GuildBoardComment> comments;
    private AtomicInteger commentSnCounter;

    public GuildBoardEntry(int entryId, int characterId, String title, String text, Instant date, int emoticon) {
        this.entryId = entryId;
        this.characterId = characterId;
        this.title = title;
        this.text = text;
        this.date = date;
        this.emoticon = emoticon;
        this.comments = new ArrayList<>();
        this.commentSnCounter = new AtomicInteger(1);
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int newEntryId) {
        this.entryId = newEntryId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public int getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(int emoticon) {
        this.emoticon = emoticon;
    }

    public List<GuildBoardComment> getComments() {
        return comments;
    }

    public void setComments(List<GuildBoardComment> comments) {
        this.comments = comments;
    }

    public AtomicInteger getCommentSnCounter() {
        return commentSnCounter;
    }

    public void setCommentSnCounter(AtomicInteger commentSnCounter) {
        this.commentSnCounter = commentSnCounter;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public int getNextCommentSn() {
        if (DatabaseManager.isRelational()) {
            // Let the relational database handle SN generation; return placeholder
            return -1;
        }

        return commentSnCounter.getAndIncrement();
    }

    public Optional<GuildBoardComment> getComment(int commentSn) {
        for (GuildBoardComment comment : comments) {
            if (comment.getCommentSn() == commentSn) {
                return Optional.of(comment);
            }
        }
        return Optional.empty();
    }

    public void addComment(GuildBoardComment comment) {
        comments.add(comment);
    }

    public void removeComment(int commentSn) {
        comments.removeIf((comment) -> comment.getCommentSn() == commentSn);
    }

    public void encodeList(OutPacket outPacket) {
        // CUIGuildBBS::ENTRYLIST
        outPacket.encodeInt(entryId); // nEntryID
        outPacket.encodeInt(characterId); // nCharacterID
        outPacket.encodeString(title); // sTitle
        outPacket.encodeFT(date); // ftDate
        outPacket.encodeInt(emoticon); // nEmoticon
        outPacket.encodeInt(comments.size()); // nComments
    }

    public void encodeCurrent(OutPacket outPacket) {
        // CUIGuildBBS::CURENTRY
        outPacket.encodeInt(entryId); // nCurEntryID
        outPacket.encodeInt(characterId); // nCurCharacterID
        outPacket.encodeFT(date); // ftCurDate
        outPacket.encodeString(title); // sCurTitle
        outPacket.encodeString(text); // sCurText
        outPacket.encodeInt(emoticon); // nEmoticon
        outPacket.encodeInt(comments.size()); // nComments
        for (GuildBoardComment comment : comments) {
            comment.encode(outPacket); // CUIGuildBBS::COMMENT
        }
    }

    /**
     * Checks whether this GuildBoardEntry has a valid entry ID.
     *
     * In relational databases, entry IDs are typically automatically generated.
     * Returns true if the entry has no ID and therefore needs to be inserted
     * into the database to obtain one.
     *
     * @return true if the entry ID is zero or negative, false otherwise
     */
    public boolean hasNoSN() {
        return getEntryId() <= 0;
    }
}
