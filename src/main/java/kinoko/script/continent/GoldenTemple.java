package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;

import java.util.Map;
import java.util.Optional;

public class GoldenTemple extends ScriptHandler {
    final static int GOLDEN_TICKET_PRICE = -2000000;
    final static int PREMIUM_TICKET_PRICE = -50000000;
    final static int GOLDEN_TICKET_ID = 4001431;
    final static int PREMIUM_TICKET_ID = 4001432;

    @Script("outGoldenTemple")
    public static void outGoldenTemple(ScriptManager sm) {
        // Mr. YOO - Golden Temple PR Manager
        final int answer = sm.askMenu("Welcome to Golden Temple! I can issue you a Golden Ticket.", Map.of(
                0, "Golden Ticket for 2,000,000 mesos (one-time use)",
                1, "Premium Golden Ticket for 50,000,000 mesos"
        ));

        if (answer == 0) {
            handleTransaction(sm, GOLDEN_TICKET_PRICE, GOLDEN_TICKET_ID);
        } else if (answer == 1) {
            handleTransaction(sm, PREMIUM_TICKET_PRICE, PREMIUM_TICKET_ID);
        }
    }

    private static void handleTransaction(ScriptManager sm, int ticketPrice, int ticketId) {
        if (!sm.canAddMoney(ticketPrice)) {
            sm.sayOk("You do not have enough mesos.");
            return;
        }
        if (!sm.canAddItem(ticketId, 1)) {
            sm.sayOk("Please make room in your inventory.");
            return;
        }
        sm.addMoney(ticketPrice);
        sm.addItem(ticketId, 1);
        sm.sayOk("Thank you for your purchase!");
    }

    @Script("MD_monkey")
    public static void MD_monkey(ScriptManager sm) {
        // Dao - Monkey Temple Guide
        final int answer = sm.askMenu("Hello. Welcome to the Monkey Temple Entrance. Where would you like to go? #rYou need a Golden Temple Ticket to enter.", Map.of(
                0, "Monkey Temple 1 - Wild Monkey (250 HP/52 EXP)",
                1, "Monkey Temple 2 - Mama Monkey (350 HP/70 EXP)",
                2, "Monkey Temple 3 - White Baby Monkey (650 HP/120 EXP)",
                3, "Monkey Temple 4 - White Mama Monkey (1040 HP/200 EXP)"
        ));

        if (!sm.hasItem(GOLDEN_TICKET_ID) && !sm.hasItem(PREMIUM_TICKET_ID)) {
            sm.sayOk("You need a Golden Temple ticket in order to get in.");
            return;
        }

        Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(sm.getFieldId() + 100 + (answer * 100));
        if (tryField.isPresent()) {
            handleWarp(sm, tryField.get());
        } else {
            sm.sayOk("The destination is unavailable.");
        }
    }

    @Script("MD_goblin")
    public static void MD_goblin(ScriptManager sm) {
        final int answer = sm.askMenu("Hello. Welcome to the Goblin Temple Entrance. Where would you like to go? #rYou need a Golden Temple Ticket to enter. All these monsters drop Sunbursts, required to get into the boss Ravana.#k", Map.of(
                0, "Goblin Temple 1 - Blue Goblin (2200 HP/170 EXP)",
                1, "Goblin Temple 2 - Red Goblin (4150 HP/336 EXP)",
                2, "Goblin Temple 3 - Stone Goblin (9300 HP/501 EXP)"
        ));

        if (!sm.hasItem(GOLDEN_TICKET_ID) && !sm.hasItem(PREMIUM_TICKET_ID)) {
            sm.sayOk("You need a Golden Temple ticket in order to get in.");
            return;
        }

        Optional<Field> tryField = sm.getField().getFieldStorage().getFieldById(sm.getFieldId() + 500 + (answer * 100));
        if (tryField.isPresent()) {
            handleWarp(sm, tryField.get());
        } else {
            sm.sayOk("The destination is unavailable.");
        }
    }

    private static void handleWarp(ScriptManager sm, Field warpField) {
        if (warpField.getUserPool().getCount() > 0) {
            sm.sayOk("There is already someone in the map.");
            return;
        }

        if (sm.hasItem(GOLDEN_TICKET_ID) && !sm.hasItem(PREMIUM_TICKET_ID)) {
            sm.removeItem(GOLDEN_TICKET_ID);
        }
        sm.warp(warpField.getFieldId());
    }
}