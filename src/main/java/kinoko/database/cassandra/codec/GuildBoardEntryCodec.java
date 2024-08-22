package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.GuildBoardEntryUDT;
import kinoko.server.guild.GuildBoardComment;
import kinoko.server.guild.GuildBoardEntry;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class GuildBoardEntryCodec extends MappingCodec<UdtValue, GuildBoardEntry> {
    public GuildBoardEntryCodec(TypeCodec<UdtValue> innerCodec, GenericType<GuildBoardEntry> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected GuildBoardEntry innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final int commentSn = value.getInt(GuildBoardEntryUDT.ENTRY_ID);
        final int characterId = value.getInt(GuildBoardEntryUDT.CHARACTER_ID);
        final String title = value.getString(GuildBoardEntryUDT.TITLE);
        final String text = value.getString(GuildBoardEntryUDT.TEXT);
        final Instant date = value.getInstant(GuildBoardEntryUDT.DATE);
        final int emoticon = value.getInt(GuildBoardEntryUDT.EMOTICON);
        final GuildBoardEntry entry = new GuildBoardEntry(
                commentSn,
                characterId,
                title,
                text,
                date,
                emoticon
        );
        final List<GuildBoardComment> comments = value.getList(GuildBoardEntryUDT.COMMENTS, GuildBoardComment.class);
        if (comments != null) {
            entry.getComments().addAll(comments);
        }
        entry.setCommentSnCounter(new AtomicInteger(value.getInt(GuildBoardEntryUDT.COMMENT_SN_COUNTER)));
        return entry;
    }

    @Override
    protected UdtValue outerToInner(GuildBoardEntry entry) {
        if (entry == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(GuildBoardEntryUDT.ENTRY_ID, entry.getEntryId())
                .setInt(GuildBoardEntryUDT.CHARACTER_ID, entry.getCharacterId())
                .setString(GuildBoardEntryUDT.TITLE, entry.getTitle())
                .setString(GuildBoardEntryUDT.TEXT, entry.getText())
                .setInstant(GuildBoardEntryUDT.DATE, entry.getDate())
                .setInt(GuildBoardEntryUDT.ENTRY_ID, entry.getEntryId())
                .setList(GuildBoardEntryUDT.COMMENTS, entry.getComments(), GuildBoardComment.class)
                .setInt(GuildBoardEntryUDT.COMMENT_SN_COUNTER, entry.getCommentSnCounter().get());
    }
}
