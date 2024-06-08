package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.social.messenger.MessengerProtocol;
import kinoko.world.social.messenger.MessengerUser;
import kinoko.world.user.AvatarLook;
import kinoko.world.user.User;

import java.util.Map;

public final class MessengerPacket {
    // CUIMessenger::OnPacket ------------------------------------------------------------------------------------------

    public static OutPacket enter(int userIndex, MessengerUser messengerUser, boolean isNew) {
        // CUIMessenger::OnEnter
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Enter);
        outPacket.encodeByte(userIndex); // nIdx
        messengerUser.encode(outPacket);
        outPacket.encodeByte(isNew); // bNew
        return outPacket;
    }

    public static OutPacket selfEnterResult(int userIndex) {
        // CUIMessenger::OnSelfEnterResult
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_SelfEnterResult);
        outPacket.encodeByte(userIndex); // nIdx
        return outPacket;
    }

    public static OutPacket leave(int userIndex) {
        // CUIMessenger::OnLeave
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Leave);
        outPacket.encodeByte(userIndex); // nIdx
        return outPacket;
    }

    public static OutPacket invite(User user, int messengerId) {
        // CUIMessenger::OnInvite
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Invite);
        outPacket.encodeString(user.getCharacterName()); // sInviter
        outPacket.encodeByte(user.getChannelId()); // nChannelID
        outPacket.encodeInt(messengerId); // dwSN
        outPacket.encodeByte(false); // admin?
        return outPacket;
    }

    public static OutPacket inviteResult(String characterName, boolean success) {
        // CUIMessenger::OnInviteResult
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_InviteResult);
        outPacket.encodeString(characterName);
        outPacket.encodeByte(success);
        return outPacket;
    }

    public static OutPacket blocked(String characterName, boolean blocked) {
        // CUIMessenger::OnBlocked
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Blocked);
        outPacket.encodeString(characterName); // sBlockedUser
        outPacket.encodeByte(blocked); // '%s' is currently not accepting chat. | '%s' denied the request.
        return outPacket;
    }

    public static OutPacket chat(String text) {
        // CUIMessenger::OnChat
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Chat);
        outPacket.encodeString(text);
        return outPacket;
    }

    public static OutPacket avatar(int userIndex, AvatarLook avatarLook) {
        // CUIMessenger::OnAvatar
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Avatar);
        outPacket.encodeByte(userIndex); // nIdx
        avatarLook.encode(outPacket); // AvatarLook::AvatarLook
        return outPacket;
    }

    public static OutPacket migrated(Map<Integer, MessengerUser> users) {
        // CUIMessenger::OnMigrated - doesn't work?
        final OutPacket outPacket = MessengerPacket.of(MessengerProtocol.MSMP_Migrated);
        for (var entry : users.entrySet()) {
            outPacket.encodeByte(entry.getKey()); // user index
            entry.getValue().encode(outPacket);
        }
        outPacket.encodeByte(-1);
        return outPacket;
    }

    private static OutPacket of(MessengerProtocol messengerProtocol) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Messenger);
        outPacket.encodeByte(messengerProtocol.getValue());
        return outPacket;
    }
}
