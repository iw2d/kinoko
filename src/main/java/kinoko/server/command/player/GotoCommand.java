package kinoko.server.command.player;

import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.HashMap;

public final class GotoCommand {

    public static final HashMap<String, Integer> GOTO_TOWNS = new HashMap<String, Integer>() {{
        put("amherst", 1000000);
        put("amoria", 680000000);
        put("aqua", 230000000);
        put("ariant", 260000000);
        put("cbd", 540000000);
        put("china", 702050000);
        put("ellin", 300000000);
        put("ellinia", 101000000);
        put("elnath", 211000000);
        put("ereve", 130000000);
        put("florina", 110000000);
        put("happy", 209000000);
        put("henesys", 100000000);
        put("herb", 251000000);
        put("kampung", 551000000);
        put("kerning", 103000000);
        put("korea", 222000000);
        put("kft", 222000000);
        put("krex", 541020700);
        put("krexel", 541020700);
        put("leafre", 240000000);
        put("lith", 104000000);
        put("ludi", 220000000);
        put("magatia", 261000000);
        put("malaysia", 551000000);
        put("mulung", 250000000);
        put("mushking", 106020000);
        put("naut", 120000000);
        put("nautilus", 120000000);
        put("neo", 240070000);
        put("nlc", 600000000);
        put("omega", 221000000);
        put("orbis", 200000000);
        put("perion", 102000000);
        put("quay", 541000000);
        put("rien", 140000000);
        put("showa", 801000000);
        put("shrine", 800000000);
        put("singapore", 540000000);
        put("sleepywood", 105040300);
        put("southperry", 2000000);
        put("square", 103040000);
        put("temple", 270000000);
        put("tot", 270000000);
    }};

    @Command("goto")
    @Arguments("location")
    public static void goTo(User user, String[] args) {
        if (args.length < 2) {
            user.systemMessage("Usage: !goto <location>");
            return;
        }

        String target = args[1].toLowerCase();
        Integer mapId = GOTO_TOWNS.get(target);

        if (mapId == null) {
            user.systemMessage("Unknown location: " + target);
            user.systemMessage(("Available towns: " + String.join(", ", GOTO_TOWNS.keySet())));
            return;
        }

        user.warp(mapId, false, false);
        user.systemMessage(("Warped to " + target + " (" + mapId + ")"));
    }
}