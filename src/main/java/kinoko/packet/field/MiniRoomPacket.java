package kinoko.packet.field;

import kinoko.server.dialog.miniroom.*;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

public final class MiniRoomPacket {
    // CMiniRoomBaseDlg::OnPacketBase ----------------------------------------------------------------------------------

    public static OutPacket inviteStatic(MiniRoomType miniRoomType, String inviterName, int miniRoomId) {
        // CMiniRoomBaseDlg::OnInviteStatic
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_Invite.getValue());
        outPacket.encodeByte(miniRoomType.getValue());
        outPacket.encodeString(inviterName); // sInviter
        outPacket.encodeInt(miniRoomId); // dwSN
        return outPacket;
    }

    public static OutPacket inviteResult(InviteType inviteType, String targetName) {
        // CMiniRoomBaseDlg::OnInviteResultStatic
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_InviteResult.getValue());
        outPacket.encodeByte(inviteType.getValue());
        if (inviteType != InviteType.NO_CHARACTER) {
            outPacket.encodeString(targetName); // sTargetName
        }
        return outPacket;
    }

    public static OutPacket enterBase(int position, User user) {
        // CMiniRoomBaseDlg::OnEnterBase
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_Enter.getValue());
        outPacket.encodeByte(position);
        user.getCharacterData().getAvatarLook().encode(outPacket); // CMiniRoomBaseDlg::DecodeAvatar
        outPacket.encodeString(user.getCharacterName()); // asUserID
        outPacket.encodeShort(user.getJob()); // anJobCode
        return outPacket;
    }

    public static OutPacket enterResult(MiniRoom miniRoom, User me) {
        // CMiniRoomBaseDlg::OnEnterResultStatic
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_EnterResult.getValue());
        outPacket.encodeByte(miniRoom.getType().getValue()); // nMiniRoomType
        // CMiniRoomBaseDlg::OnEnterResultBase
        outPacket.encodeByte(miniRoom.getMaxUsers()); // nMaxUsers
        outPacket.encodeByte(miniRoom.getPosition(me)); // nMyPosition
        miniRoom.getUsers().forEach((i, user) -> {
            outPacket.encodeByte(i); // position
            user.getCharacterData().getAvatarLook().encode(outPacket); // CMiniRoomBaseDlg::DecodeAvatar
            outPacket.encodeString(user.getCharacterName()); // asUserID
            outPacket.encodeShort(user.getJob()); // anJobCode
        });
        outPacket.encodeByte(-1);
        return outPacket;
    }

    public static OutPacket enterResult(EnterResultType resultType) {
        // CMiniRoomBaseDlg::OnEnterResultStatic
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_EnterResult.getValue());
        outPacket.encodeByte(0); // nMiniRoomType
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }

    public static OutPacket chat(int position, String text) {
        // CMiniRoomBaseDlg::OnChat
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_Chat.getValue());
        outPacket.encodeByte(MiniRoomProtocol.MRP_UserChat.getValue());
        outPacket.encodeByte(position);
        outPacket.encodeString(text); // sText
        return outPacket;
    }

    public static OutPacket chat(int position, String characterName, String message) {
        return chat(position, String.format("%s : %s", characterName, message));
    }

    public static OutPacket gameMessage(GameMessageType messageType, String characterName) {
        // CMiniRoomBaseDlg::MakeGameMessage
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_Chat.getValue());
        outPacket.encodeByte(MiniRoomProtocol.MRP_GameMessage.getValue());
        outPacket.encodeByte(messageType.getValue()); // nMessageCode
        outPacket.encodeString(characterName); // sCharacterName
        return outPacket;
    }

    public static OutPacket leave(int position, LeaveType leaveType) {
        // CMiniRoomBaseDlg::OnEnterBase
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(MiniRoomProtocol.MRP_Leave.getValue());
        outPacket.encodeByte(position);
        // *::OnLeave
        outPacket.encodeByte(leaveType.getValue());
        return outPacket;
    }
}
