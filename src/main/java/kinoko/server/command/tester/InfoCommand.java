package kinoko.server.command.tester;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.map.PortalInfo;
import kinoko.server.command.Command;
import kinoko.util.Rect;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import kinoko.world.user.stat.CalcDamage;

/**
 * Tester command to display detailed user info.
 */
public final class InfoCommand {

    @Command("info")
    public static void info(User user, String[] args) {
        final Field field = user.getField();
        final var stats = user.getBasicStat();
        final var charStats = user.getCharacterStat();

        // Basic stats
        user.write(MessagePacket.system("HP : %d / %d, MP : %d / %d", user.getHp(), user.getMaxHp(), user.getMp(), user.getMaxMp()));
        user.write(MessagePacket.system("STR : %d, DEX : %d, INT : %d, LUK : %d", stats.getStr(), stats.getDex(), stats.getInt(), stats.getLuk()));
        user.write(MessagePacket.system("AP : %d", charStats.getAp()));
        user.write(MessagePacket.system("SP : %s", charStats.getSp().getMap()));
        user.write(MessagePacket.system("Damage : %d ~ %d", (int) CalcDamage.calcDamageMin(user), (int) CalcDamage.calcDamageMax(user)));
        user.write(MessagePacket.system("Field ID : %d (%s)", field.getFieldId(), field.getFieldType()));

        // Foothold below
        final String footholdBelow = field.getFootholdBelow(user.getX(), user.getY())
                .map(fh -> String.valueOf(fh.getSn()))
                .orElse("unk");
        user.write(MessagePacket.system("  x : %d, y : %d, fh : %d (%s)", user.getX(), user.getY(), user.getFoothold(), footholdBelow));

        // Nearest portal
        final PortalInfo nearestPortal = field.getMapInfo().getPortalInfos().stream()
                .min((a, b) -> Double.compare(Util.distance(user.getX(), user.getY(), a.getX(), a.getY()),
                                              Util.distance(user.getX(), user.getY(), b.getX(), b.getY())))
                .orElse(null);

        if (nearestPortal != null && Util.distance(user.getX(), user.getY(), nearestPortal.getX(), nearestPortal.getY()) < 200) {
            user.write(MessagePacket.system("Portal name : %s (%d)", nearestPortal.getPortalName(), nearestPortal.getPortalId()));
            user.write(MessagePacket.system("  x : %d, y : %d, script : %s", nearestPortal.getX(), nearestPortal.getY(), nearestPortal.getScript()));
        }

        // Detection rectangle for nearby objects
        final Rect detectRect = Rect.of(-400, -400, 400, 400);

        // Nearest mob
        user.getNearestObject(field.getMobPool().getInsideRect(user.getRelativeRect(detectRect)))
                .ifPresent(mob -> {
                    user.write(MessagePacket.system(mob.toString()));
                    user.write(MessagePacket.system("  Controller : %s", mob.getController().getCharacterName()));
                });

        // Nearest NPC
        user.getNearestObject(field.getNpcPool().getInsideRect(user.getRelativeRect(detectRect)))
                .ifPresent(npc -> user.write(MessagePacket.system(npc.toString())));

        // Nearest Reactor
        user.getNearestObject(field.getReactorPool().getInsideRect(user.getRelativeRect(detectRect)))
                .ifPresent(reactor -> user.write(MessagePacket.system(reactor.toString())));
    }
}
