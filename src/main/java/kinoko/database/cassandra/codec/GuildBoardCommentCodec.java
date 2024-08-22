package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.GuildBoardCommentUDT;
import kinoko.server.guild.GuildBoardComment;

import java.time.Instant;

public final class GuildBoardCommentCodec extends MappingCodec<UdtValue, GuildBoardComment> {
    public GuildBoardCommentCodec(TypeCodec<UdtValue> innerCodec, GenericType<GuildBoardComment> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected GuildBoardComment innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final int commentSn = value.getInt(GuildBoardCommentUDT.COMMENT_SN);
        final int characterId = value.getInt(GuildBoardCommentUDT.CHARACTER_ID);
        final String text = value.getString(GuildBoardCommentUDT.TEXT);
        final Instant date = value.getInstant(GuildBoardCommentUDT.DATE);
        return new GuildBoardComment(
                commentSn,
                characterId,
                text,
                date
        );
    }

    @Override
    protected UdtValue outerToInner(GuildBoardComment comment) {
        if (comment == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(GuildBoardCommentUDT.COMMENT_SN, comment.getCommentSn())
                .setInt(GuildBoardCommentUDT.CHARACTER_ID, comment.getCharacterId())
                .setString(GuildBoardCommentUDT.TEXT, comment.getText())
                .setInstant(GuildBoardCommentUDT.DATE, comment.getDate());
    }
}
