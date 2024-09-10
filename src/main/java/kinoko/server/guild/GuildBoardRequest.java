package kinoko.server.guild;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class GuildBoardRequest implements Encodable {
    private final GuildBoardProtocol requestType;
    private boolean modify;
    private boolean notice;
    private int entryId;
    private String title;
    private String text;
    private int emoticon;
    private int commentSn;
    private int start;

    public GuildBoardRequest(GuildBoardProtocol requestType) {
        this.requestType = requestType;
    }

    public GuildBoardProtocol getRequestType() {
        return requestType;
    }

    public boolean isModify() {
        return modify;
    }

    public boolean isNotice() {
        return notice;
    }

    public int getEntryId() {
        return entryId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getEmoticon() {
        return emoticon;
    }

    public int getCommentSn() {
        return commentSn;
    }

    public int getStart() {
        return start;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case Register -> {
                outPacket.encodeByte(modify);
                if (modify) {
                    outPacket.encodeInt(entryId);
                }
                outPacket.encodeByte(notice);
                outPacket.encodeString(title);
                outPacket.encodeString(text);
                outPacket.encodeInt(emoticon);
            }
            case Delete, ViewEntryRequest -> {
                outPacket.encodeInt(entryId);
            }
            case LoadListRequest -> {
                outPacket.encodeInt(start);
            }
            case RegisterComment -> {
                outPacket.encodeInt(entryId);
                outPacket.encodeString(text);
            }
            case DeleteComment -> {
                outPacket.encodeInt(entryId);
                outPacket.encodeInt(commentSn);
            }
        }
    }

    public static GuildBoardRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.getByValue(type));
        switch (request.requestType) {
            case Register -> {
                request.modify = inPacket.decodeBoolean();
                if (request.modify) {
                    request.entryId = inPacket.decodeInt();
                }
                request.notice = inPacket.decodeBoolean();
                request.title = inPacket.decodeString();
                request.text = inPacket.decodeString();
                request.emoticon = inPacket.decodeInt();
            }
            case Delete, ViewEntryRequest -> {
                request.entryId = inPacket.decodeInt();
            }
            case LoadListRequest -> {
                request.start = inPacket.decodeInt();
            }
            case RegisterComment -> {
                request.entryId = inPacket.decodeInt();
                request.text = inPacket.decodeString();
            }
            case DeleteComment -> {
                request.entryId = inPacket.decodeInt();
                request.commentSn = inPacket.decodeInt();
            }
        }
        return request;
    }

    public static GuildBoardRequest register(boolean modify, int entryId, boolean notice, String title, String text, int emoticon) {
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.Register);
        request.modify = modify;
        request.entryId = entryId;
        request.notice = notice;
        request.title = title;
        request.text = text;
        request.emoticon = emoticon;
        return request;
    }

    public static GuildBoardRequest delete(int entryId) {
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.Delete);
        request.entryId = entryId;
        return request;
    }

    public static GuildBoardRequest loadList(int start) {
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.LoadListRequest);
        request.start = start;
        return request;
    }

    public static GuildBoardRequest viewEntry(int entryId) {
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.ViewEntryRequest);
        request.entryId = entryId;
        return request;
    }

    public static GuildBoardRequest registerComment(int entryId, String text) {
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.RegisterComment);
        request.entryId = entryId;
        request.text = text;
        return request;
    }

    public static GuildBoardRequest deleteComment(int entryId, int commentSn) {
        final GuildBoardRequest request = new GuildBoardRequest(GuildBoardProtocol.DeleteComment);
        request.entryId = entryId;
        request.commentSn = commentSn;
        return request;
    }
}
