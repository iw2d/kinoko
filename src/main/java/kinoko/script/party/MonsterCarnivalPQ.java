package kinoko.script.party;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;

import java.util.List;
import java.util.Map;

public final class MonsterCarnivalPQ extends ScriptHandler {
    // Monster Carnival 1 maps
    public static final int CPQ1_EXIT = 980000000;
    public static final int CPQ1_LOBBY = 980000010;
    public static final int CPQ1_WAITING_1 = 980000100;
    public static final int CPQ1_WAITING_2 = 980000101;
    public static final int CPQ1_FIELD = 980000102;
    public static final int CPQ1_REVIVE = 980000103;
    public static final int CPQ1_WINNER = 980000104;
    public static final int CPQ1_LOSER = 980000105;

    // Monster Carnival 2 maps
    public static final int CPQ2_EXIT = 980030000;
    public static final int CPQ2_LOBBY = 980030010;
    public static final int CPQ2_WAITING_1 = 980031000;
    public static final int CPQ2_WAITING_2 = 980001001;
    public static final int CPQ2_FIELD = 980031100;
    public static final int CPQ2_REVIVE = 980001002;
    public static final int CPQ2_WINNER = 980031300;
    public static final int CPQ2_LOSER = 980031400;

    @Script("monsterCarnival1_enter")
    public static void monsterCarnival1_enter(ScriptManager sm) {
        // Spiegelmann (2042000)
        //   Kerning City : Spiegelmann's Office (980000000)
        if (sm.getFieldId() == CPQ1_EXIT) {
            final int answer = sm.askMenu("#e<Monster Carnival 1>#n\r\nWelcome to Monster Carnival! Two parties compete by summoning monsters and defeating them to earn Carnival Points (CP)!", Map.of(
                    0, "I want to participate in Monster Carnival",
                    1, "I want to hear the details",
                    2, "I want to leave"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("Only the party leader can register for Monster Carnival.");
                    return;
                }
                if (!sm.checkParty(2, 30)) {
                    sm.sayOk("You need 2-6 party members at level 30+ to participate in Monster Carnival 1.");
                    return;
                }
                // TODO: Implement carnival party registration
                sm.sayOk("Monster Carnival registration system is being prepared. Please check back later!");
            } else if (answer == 1) {
                sm.sayOk("#e<Monster Carnival 1>#n\r\nMonster Carnival is a competitive party quest where two parties battle by summoning monsters against each other!\r\n\r\n#e - Level:#n 30-50\r\n#e - Time Limit:#n 10 min.\r\n#e - Players:#n 2-6 per party (2 parties compete)\r\n#e - Objective:#n Earn Carnival Points (CP) by defeating monsters and use CP to summon monsters for the opposing team!\r\n#e - Reward:#n Carnival Coins (exchange for items)");
            } else if (answer == 2) {
                sm.warp(103000000); // Kerning City
            }
        }
    }

    @Script("monsterCarnival2_enter")
    public static void monsterCarnival2_enter(ScriptManager sm) {
        // Spiegelmann (2042001)
        //   Ludibrium : Spiegelmann's Office (980030000)
        if (sm.getFieldId() == CPQ2_EXIT) {
            final int answer = sm.askMenu("#e<Monster Carnival 2>#n\r\nWelcome to Monster Carnival 2! A more challenging version of the carnival for higher level adventurers!", Map.of(
                    0, "I want to participate in Monster Carnival 2",
                    1, "I want to hear the details",
                    2, "I want to leave"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("Only the party leader can register for Monster Carnival.");
                    return;
                }
                if (!sm.checkParty(2, 51)) {
                    sm.sayOk("You need 2-6 party members at level 51+ to participate in Monster Carnival 2.");
                    return;
                }
                // TODO: Implement carnival party registration
                sm.sayOk("Monster Carnival 2 registration system is being prepared. Please check back later!");
            } else if (answer == 1) {
                sm.sayOk("#e<Monster Carnival 2>#n\r\nMonster Carnival 2 is the advanced version of the carnival with stronger monsters and better rewards!\r\n\r\n#e - Level:#n 51-70\r\n#e - Time Limit:#n 10 min.\r\n#e - Players:#n 2-6 per party (2 parties compete)\r\n#e - Objective:#n Earn Carnival Points (CP) by defeating monsters and use CP to summon monsters for the opposing team!\r\n#e - Reward:#n Carnival Coins, Maple Coins");
            } else if (answer == 2) {
                sm.warp(220000000); // Ludibrium
            }
        }
    }

    @Script("monsterCarnival_exit")
    public static void monsterCarnival_exit(ScriptManager sm) {
        // Exit NPC for Monster Carnival
        final int mapId = sm.getFieldId();

        if (mapId == CPQ1_EXIT || mapId == CPQ2_EXIT) {
            sm.sayOk("Talk to Spiegelmann to participate in Monster Carnival!");
            return;
        }

        if (sm.askYesNo("Do you want to leave Monster Carnival? You will forfeit all progress.")) {
            if (mapId >= 980000000 && mapId < 980030000) {
                sm.warp(CPQ1_EXIT, "sp");
            } else {
                sm.warp(CPQ2_EXIT, "sp");
            }
        }
    }

    // NOTE: Monster Carnival requires a full carnival party system implementation including:
    // - Carnival Party registration and matching
    // - CP (Carnival Point) tracking per player and team
    // - Monster summoning UI and mechanics
    // - Team-based scoring and winner determination
    // - Protector and Guardian summoning
    // - This is a placeholder structure until the full carnival system is implemented
}
