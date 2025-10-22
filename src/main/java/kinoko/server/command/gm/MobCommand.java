package kinoko.server.command.gm;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.MobProvider;
import kinoko.provider.map.Foothold;
import kinoko.provider.mob.MobTemplate;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * GM command to spawn mobs at the user's location.
 */
public final class MobCommand {

    @Command({ "mob", "spawn" })
    @Arguments("mob template ID")
    public static void mob(User user, String[] args) {
        try {
            final int templateId = Integer.parseInt(args[1]);
            final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
            if (mobTemplateResult.isEmpty()) {
                user.write(MessagePacket.system("Could not resolve mob template ID: %d", templateId));
                return;
            }

            final int count = args.length > 2 ? Integer.parseInt(args[2]) : 1;
            final Field field = user.getField();
            final Optional<Foothold> footholdResult = field.getFootholdBelow(user.getX(), user.getY());

            for (int i = 0; i < count; i++) {
                final Mob mob = new Mob(
                        mobTemplateResult.get(),
                        null,
                        user.getX(),
                        user.getY(),
                        footholdResult.map(Foothold::getSn).orElse(user.getFoothold())
                );
                field.getMobPool().addMob(mob);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !mob <mob template ID> [count]"));
        }
    }
}
