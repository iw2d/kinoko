package kinoko.server.command.manager;

import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.StringProvider;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.GameConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

/**
 * Changes the user's avatar (hair, face, or skin).
 * SuperGM-level command.
 */
public final class AvatarCommand {

    @Command("avatar")
    @Arguments("new look")
    public static void avatar(User user, String[] args) {
        try {
            int look = Integer.parseInt(args[1]);

            if (look >= 0 && look <= GameConstants.SKIN_MAX) {
                user.getCharacterStat().setSkin((byte) look);
                user.write(WvsContext.statChanged(Stat.SKIN, user.getCharacterStat().getSkin(), false));
                user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
            } else if (look >= GameConstants.FACE_MIN && look <= GameConstants.FACE_MAX) {
                if (StringProvider.getItemName(look) == null) {
                    user.systemMessage("Tried to change face with invalid ID : %d", look);
                    return;
                }
                user.getCharacterStat().setFace(look);
                user.write(WvsContext.statChanged(Stat.FACE, user.getCharacterStat().getFace(), false));
                user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
            } else if (look >= GameConstants.HAIR_MIN && look <= GameConstants.HAIR_MAX) {
                if (StringProvider.getItemName(look) == null) {
                    user.systemMessage("Tried to change hair with invalid ID : %d", look);
                    return;
                }
                user.getCharacterStat().setHair(look);
                user.write(WvsContext.statChanged(Stat.HAIR, user.getCharacterStat().getHair(), false));
                user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
            } else {
                user.systemMessage("Tried to change avatar with invalid ID : %d", look);
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !avatar <new look>");
        }
    }
}
