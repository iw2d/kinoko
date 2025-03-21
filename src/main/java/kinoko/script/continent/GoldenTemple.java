package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;

import java.util.Map;
import java.util.Optional;

public final class GoldenTemple extends ScriptHandler {
    public static final int GOLDEN_TICKET_ID = 4001431;
    public static final int PREMIUM_TICKET_ID = 4001432; // TODO : probably some QR value to track cooltime
    public static final int TIME_LIMIT = 1800; // Map/Map/Map%d/%d/info/timeLimit

    @Script("outGoldenTemple")
    public static void outGoldenTemple(ScriptManager sm) {
        // Mr. Yoo : Golden Temple PR Manager (9000078)
        //   Golden Temple : Golden Temple (809060000)
        //   Golden Temple : Golden Temple (950100000)

        // TODO
    }

    @Script("MD_monkey")
    public static void MD_monkey(ScriptManager sm) {
        // Dao : Monkey Temple Guide (9000080)
        //   Golden Temple : Golden Temple (809060000)
        //   Golden Temple : Golden Temple (950100000)
        final int answer = sm.askMenu("Are you here because you heard about the Monkey Temple inside the Golden Temple?", Map.of(
                0,  "I want to enter the Monkey Temple.",
                1, "Please tell me more about the Monkey Temple."
        ));
        if (answer == 0) {
            final int dungeon = sm.askMenu("Which temple do you wish to enter? And you know that must enter alone, right?", Map.of(
                    0, "Monkey Temple 1 (Lv. 15 Wild Monkey",
                    1, "Monkey Temple 2 (Lv. 21 Mama Monkey",
                    2, "Monkey Temple 3 (Lv. 20 White Baby Monkey",
                    3, "Monkey Temple 4 (Lv. 34 White Mama Monkey)"
            ));
            if (!sm.hasItem(PREMIUM_TICKET_ID) && !sm.removeItem(GOLDEN_TICKET_ID, 1)) {
                sm.sayOk("I'm sorry but you can't enter the Monkey Temple without a ticket. Let me explain the Monkey Temple to you again so you can understand how to obtain a ticket.");
                return;
            }
            sm.warpInstance(950100100 + (dungeon * 100), "out00",  950010000, TIME_LIMIT);
        } else if (answer == 1) {
            sm.sayOk("This is a forest where the monkeys outside of the Golden Temple live. \r\n\r\n1. Benefits of the Monkey Temple \r\n#b- Yields more EXP than other monsters of the same level \r\n- Drops various scrolls#k \r\n\r\n2. How to obtain the Golden Ticket required to enter \r\n- Mr. Yoo's quest can be completed once per day \r\n- Freely enter once per hour if you possess a Premium Golden Ticket.");
        }
    }

    @Script("MD_goblin")
    public static void MD_goblin(ScriptManager sm) {
        // Chan : Goblin Cave Guard (9000075)
        //   Golden Temple : Golden Temple (809060000)
        //   Golden Temple : Golden Temple (950100000)
        final int answer = sm.askMenu("What do you want? Please step aside.", Map.of(
                0,  "I want to enter the Goblin Cave.",
                1, "Please tell me more about the Goblin Cave."
        ));
        if (answer == 0) {
            final int dungeon = sm.askMenu("You need a Golden Ticket to enter. You can only enter when you're alone, too. Where do you want to go?", Map.of(
                    0, "Goblin Temple 1 (Lv. 43 Blue Goblin)",
                    1, "Goblin Temple 2 (Lv. 54 Red Goblin)",
                    2, "Goblin Temple 3 (Lv. 66 Stone Goblin)"
            ));
            if (!sm.hasItem(PREMIUM_TICKET_ID) && !sm.removeItem(GOLDEN_TICKET_ID, 1)) {
                sm.sayOk("I'm sorry but you can't enter the Goblin Cave without a ticket. Let me explain the Goblin Cave to you again so you can understand how to obtain a ticket.");
                return;
            }
            sm.warpInstance(950100500 + (dungeon * 100), "out00",  950010000, TIME_LIMIT);
        } else if (answer == 1) {
            sm.sayOk("This is a Cave where the Goblins outside of the Golden Temple live. \r\n\r\n1. Benefits of the Goblin Cave \r\n#b- Yields more EXP than other monsters of the same level \r\n- Drops Sunburst#k \r\n\r\n2. How to obtain the Golden Ticket required to enter \r\n- Mr. Yoo's quest can be completed once per day \r\n- Freely enter once per hour if you possess a Premium Golden Ticket.");
        }
    }

    @Script("goMonkey")
    public static void goMonkey(ScriptManager sm) {
        // Golden Temple : Golden Temple (809060000)
        //   in00 (1328, 531)
        // Golden Temple : Golden Temple (950100000)
        //   in00 (-827, 532)
        MD_monkey(sm);
    }

    @Script("goGoblin")
    public static void goGoblin(ScriptManager sm) {
        // Golden Temple : Golden Temple (809060000)
        //   in01 (-532, 531)
        // Golden Temple : Golden Temple (950100000)
        //   in01 (977, 532)
        MD_goblin(sm);
    }
}
