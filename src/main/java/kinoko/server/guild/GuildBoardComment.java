package kinoko.server.guild;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;

public final class GuildBoardComment implements Encodable {
    private final int commentSn;
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
}
