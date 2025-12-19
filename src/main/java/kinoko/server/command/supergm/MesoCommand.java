package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.item.InventoryManager;
import kinoko.world.user.User;
import kinoko.packet.world.WvsContext;
import kinoko.world.user.stat.Stat;


public final class MesoCommand {
    /**
     * Sets the specified amount of mesos for the given user.
     *
     * This command is intended for SuperGM (or GM+) use only.
     * The user must provide a single numeric argument representing the meso amount.
     *
     * @param user the target user whose mesos will be set
     * @param args the command arguments, where args[1] should be the amount of mesos
     */
    @Command(value = {"meso", "money", "mesos"})
    @Arguments("amount")
    public static void meso(User user, String[] args) {
        try {
            final int money = Integer.parseInt(args[1]);
            final InventoryManager im = user.getInventoryManager();
            im.setMoney(money);
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !meso <amount>");
        }
    }
}