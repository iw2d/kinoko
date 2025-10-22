package kinoko.server.command.admin;

import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.util.BitFlag;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;
import kinoko.world.user.stat.TwoStateTemporaryStat;

import java.util.Set;

/**
 * Sets the user to ride a specific vehicle.
 * Admin-level command.
 */
public final class RideCommand {

    @Command("ride")
    @Arguments("vehicle ID")
    public static void ride(User user, String[] args) {
        try {
            int vehicleId = Integer.parseInt(args[1]);
            if (ItemProvider.getItemInfo(vehicleId).isEmpty()) {
                user.write(MessagePacket.system("Could not resolve item info for vehicle ID : %d", vehicleId));
                return;
            }

            SecondaryStat ss = user.getSecondaryStat();
            BitFlag<CharacterTemporaryStat> flag = BitFlag.from(
                    Set.of(CharacterTemporaryStat.RideVehicle),
                    CharacterTemporaryStat.FLAG_SIZE
            );
            ss.getTemporaryStats().put(CharacterTemporaryStat.RideVehicle,
                    TwoStateTemporaryStat.ofTwoState(CharacterTemporaryStat.RideVehicle, vehicleId, Beginner.MONSTER_RIDER, 0)
            );
            user.write(WvsContext.temporaryStatSet(ss, flag));
            user.getField().broadcastPacket(UserRemote.temporaryStatSet(user, ss, flag));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !ride <vehicle ID>"));
        }
    }
}
