package kinoko.server.guild;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;

public final class GuildBoardComment implements Encodable {
    private int commentSn;
    private final int characterId;
    private String text;
    private Instant date;

    public GuildBoardComment(int commentSn, int characterId, String text, Instant date) {
        this.commentSn = commentSn;
        this.characterId = characterId;
        this.text = text;
        this.date = date;
    }

    public int getCommentSn() {
        return commentSn;
    }

    public void setCommentSn(int newCommentSn) {
        this.commentSn = newCommentSn;
    }

    public int getCharacterId() {
        return characterId;
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

    @Override
    public void encode(OutPacket outPacket) {
        // CUIGuildBBS::COMMENT
        outPacket.encodeInt(commentSn); // nSN
        outPacket.encodeInt(characterId); // nCharacterID
        outPacket.encodeFT(date); // ftDate
        outPacket.encodeString(text); // sComment
    }

    /**
     * Checks whether this GuildBoardComment has a valid comment serial number (SN).
     *
     * In relational databases, comment SNs are typically automatically generated.
     * Returns true if the comment has no SN and therefore needs to be inserted
     * into the database to obtain one.
     *
     * @return true if the comment SN is zero or negative, false otherwise
     */
    public boolean hasNoSN() {
        return getCommentSn() <= 0;
    }
}
