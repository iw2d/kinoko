package kinoko.world.social.messenger;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.user.AvatarLook;
import kinoko.world.user.User;

public final class MessengerUser implements Encodable {
    private final AvatarLook avatarLook;
    private final String characterName;
    private final int channelId;

    public MessengerUser(AvatarLook avatarLook, String characterName, int channelId) {
        this.avatarLook = avatarLook;
        this.characterName = characterName;
        this.channelId = channelId;
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getChannelId() {
        return channelId;
    }

    @Override
    public void encode(OutPacket outPacket) {
        avatarLook.encode(outPacket);
        outPacket.encodeString(characterName);
        outPacket.encodeByte(channelId);
    }

    public static MessengerUser decode(InPacket inPacket) {
        final AvatarLook avatarLook = AvatarLook.decode(inPacket);
        final String characterName = inPacket.decodeString();
        final int channelId = inPacket.decodeByte();
        return new MessengerUser(avatarLook, characterName, channelId);
    }

    public static MessengerUser from(User user) {
        return new MessengerUser(user.getCharacterData().getAvatarLook(), user.getCharacterName(), user.getChannelId());
    }
}
