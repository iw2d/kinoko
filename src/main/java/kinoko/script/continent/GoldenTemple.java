package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;

import java.util.Map;
import java.util.Optional;

public class GoldenTemple extends ScriptHandler {
    final static int GOLDEN_TICKET_ID = 4001431;
    final static int PREMIUM_TICKET_ID = 4001432;

    @Script("outGoldenTemple")
    public static void outGoldenTemple(ScriptManager sm) {
        // Mr. YOO - Golden Temple PR Manager
    }

    public static void handleDungeonEntry(ScriptManager sm, String dungeonName, String introMessage, String benefitMessage, Map<Integer, String> dungeonOptions, int fieldOffset) {
        if (sm.askMenu(introMessage, Map.of(
                0, "I want to enter the " + dungeonName + ".",
                1, "Please tell me more about the " + dungeonName + "."
        )) == 1) {
            sm.sayOk(benefitMessage);
            sm.dispose();
            return;
        }

        final int chosenDungeon = sm.askMenu("Where do you want to go? You must enter alone.", dungeonOptions);

        if (!sm.hasItem(GOLDEN_TICKET_ID) && !sm.hasItem(PREMIUM_TICKET_ID)) {
            sm.sayOk("I'm sorry but you can't enter the " + dungeonName + " without a ticket. Let me explain again how to obtain a ticket.");
            return;
        }

        Field warpField = findAvailableField(sm, fieldOffset, chosenDungeon);

        if (warpField != null) {
            if (!sm.hasItem(PREMIUM_TICKET_ID)) {
                sm.removeItem(GOLDEN_TICKET_ID, 1);
            }
            sm.warp(warpField.getFieldId());
            sm.dispose();
        } else {
            sm.broadcastMessage("Try again soon.");
            sm.dispose();
        }
    }

    private static Field findAvailableField(ScriptManager sm, int baseOffset, int dungeonIndex) {
        for (int i = 0; i < 50; i++) {
            Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(sm.getFieldId() + baseOffset + (dungeonIndex * 100) + i);
            if (tryField.isPresent()) {
                Field tempWarpField = tryField.get();
                if (tempWarpField.getUserPool().getCount() == 0) {
                    return tempWarpField;
                }
            }
        }
        return null;
    }

    @Script("MD_monkey")
    public static void MD_monkey(ScriptManager sm) {
        handleDungeonEntry(
                sm,
                "Monkey Temple",
                "Are you here because you heard about the Monkey Temple inside the Golden Temple?",
                "This is a forest where the monkeys outside of the Golden Temple live. \r\n\r\n1. Benefits of the Monkey Temple \r\n#b- Yields more EXP than other monsters of the same level \r\n- Drops various scrolls#k \r\n\r\n2. How to obtain the Golden Ticket required to enter \r\n- Mr. Yoo's quest can be completed once per day \r\n- Freely enter once per hour if you possess a Premium Golden Ticket.",
                Map.of(
                        0, "Monkey Temple 1 (Lv. " + (sm.getFieldId() == 950100000 ? 15 : 120) + " Wild Monkey)",
                        1, "Monkey Temple 2 (Lv. " + (sm.getFieldId() == 950100000 ? 21 : 112) + " Mama Monkey)",
                        2, "Monkey Temple 3 (Lv. " + (sm.getFieldId() == 950100000 ? 27 : 104) + " White Baby Monkey)",
                        3, "Monkey Temple 4 (Lv. " + (sm.getFieldId() == 950100000 ? 34 : 96) + " White Mama Monkey)"
                ),
                100
        );
    }

    @Script("MD_goblin")
    public static void MD_goblin(ScriptManager sm) {
        handleDungeonEntry(
                sm,
                "Goblin Cave",
                "What do you want? Please step aside.",
                "This is a Cave where the Goblins outside of the Golden Temple live. \r\n\r\n1. Benefits of the Goblin Cave \r\n#b- Yields more EXP than other monsters of the same level \r\n- Drops Sunburst#k \r\n\r\n2. How to obtain the Golden Ticket required to enter \r\n- Mr. Yoo's quest can be completed once per day \r\n- Freely enter once per hour if you possess a Premium Golden Ticket.",
                Map.of(
                        0, "Goblin Temple 1 (Lv. " + (sm.getFieldId() == 950100000 ? 43 : 86) + " Blue Goblin)",
                        1, "Goblin Temple 2 (Lv. " + (sm.getFieldId() == 950100000 ? 54 : 78) + " Red Goblin)",
                        2, "Goblin Temple 3 (Lv. " + (sm.getFieldId() == 950100000 ? 66 : 70) + " Stone Goblin)"
                ),
                500
        );
    }
}