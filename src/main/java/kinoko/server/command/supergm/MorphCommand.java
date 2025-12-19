package kinoko.server.command.supergm;

import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.util.BitFlag;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.util.Set;

/**
 * Morphs the user into a specified morph ID.
 * SuperGM-level command.
 */
public final class MorphCommand {

    @Command("morph")
    @Arguments("morph ID")
    public static void morph(User user, String[] args) {
        try {
            int morphId = Integer.parseInt(args[1]);
            if (SkillProvider.getMorphInfoById(morphId).isEmpty()) {
                user.systemMessage("Could not resolve morph info for morph ID : %d", morphId);
                return;
            }

            SecondaryStat ss = user.getSecondaryStat();
            BitFlag<CharacterTemporaryStat> flag = BitFlag.from(Set.of(CharacterTemporaryStat.Morph), CharacterTemporaryStat.FLAG_SIZE);
            ss.getTemporaryStats().put(CharacterTemporaryStat.Morph, TemporaryStatOption.of(morphId, -5300000, 0));
            user.write(WvsContext.temporaryStatSet(ss, flag));
            user.getField().broadcastPacket(UserRemote.temporaryStatSet(user, ss, flag));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !morph <morph ID>");
        }
    }
}
