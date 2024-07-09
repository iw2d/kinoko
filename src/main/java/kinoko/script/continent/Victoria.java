package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.event.EventState;
import kinoko.server.event.EventType;
import kinoko.server.event.Subway;
import kinoko.world.job.JobConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class Victoria extends ScriptHandler {
    public static final int TICKET_TO_CONSTRUCTION_SITE_B1 = 4031036;
    public static final int TICKET_TO_CONSTRUCTION_SITE_B2 = 4031037;
    public static final int TICKET_TO_CONSTRUCTION_SITE_B3 = 4031038;

    public static final int KERNING_SQUARE_SUBWAY_1 = 103020010; // Kerning City -> Kerning Square
    public static final int KERNING_SQUARE_SUBWAY_2 = 103020011; // Kerning Square -> Kerning City

    @Script("victoria_taxi")
    public static void victoria_taxi(ScriptManager sm) {
        // Regular Cab in Victoria (1012000)
        //   Henesys : Henesys (100000000)
        //   Ellinia : Ellinia (101000000)
        //   Perion : Perion (102000000)
        //   Kerning City : Kerning City (103000000)
        //   Lith Harbor : Lith Harbor (104000000)
        //   Nautilus : Nautilus Harbor (120000000)
        final boolean isBeginner = JobConstants.isBeginnerJob(sm.getUser().getJob());
        final int price = isBeginner ? 100 : 1000;
        final List<Integer> towns = Stream.of(
                100000000, // Henesys : Henesys
                101000000, // Ellinia : Ellinia
                102000000, // Perion : Perion
                103000000, // Kerning City : Kerning City
                104000000, // Lith Harbor : Lith Harbor
                120000000 // Nautilus : Nautilus Harbor
        ).filter(mapId -> sm.getFieldId() != mapId).toList();
        final Map<Integer, String> options = createOptions(towns, (mapId) -> String.format("#m%d# (%d Mesos)", mapId, price));
        sm.sayNext("Hello! I'm #p1012000#, and I am here to take you to your destination, quickly and safely. #b#p1012000##k values your satisfaction, so you can always reach your destination at an affordable price. I am here to serve you.");
        final int answer = sm.askMenu("Please select your destination." + (isBeginner ? "\r\nWe have a special 90% discount for beginners." : ""), options);
        if (sm.askYesNo(String.format("You don't have anything else to do here, huh? Do you really want to go to #b#m%d##k? It'll cost you #b%d#k mesos.", towns.get(answer), price))) {
            if (sm.addMoney(-price)) {
                sm.warp(towns.get(answer));
            } else {
                sm.sayOk("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.");
            }
        } else {
            sm.sayOk("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.");
        }
    }

    @Script("subway_ticket")
    public static void subway_ticket(ScriptManager sm) {
        // Jake : Subway Worker (1052006)
        //   Victoria Road : Subway Ticketing Booth (103000100)
        //   Kerning City Subway : Subway Ticketing Booth (103020000)
        final Map<Integer, String> options = new HashMap<>();
        if (sm.getLevel() >= 20) {
            options.put(0, itemName(TICKET_TO_CONSTRUCTION_SITE_B1)); // Shumi's Lost Coin
        }
        if (sm.getLevel() >= 30) {
            options.put(1, itemName(TICKET_TO_CONSTRUCTION_SITE_B2)); // Shumi's Lost Bundle of Money
        }
        if (sm.getLevel() >= 40) {
            options.put(2, itemName(TICKET_TO_CONSTRUCTION_SITE_B3)); // Shumi's Lost Sack of Money
        }
        if (options.isEmpty()) {
            sm.sayNext("You can enter the premise once you have bought the ticket; however it doesn't seem like you can enter here. There are foreign devices underground that may be too much for you to handle, so please train yourself, be prepared, and then come back.");
            return;
        }
        final int answer = sm.askMenu("You must purchase the ticket to enter. Once you have made the purchase, you can enter through #p1052007# on the right. What would you like to buy?", options);
        if (answer == 0) {
            if (sm.askYesNo("Will you purchase the Ticket to #bConstruction site B1#k? It'll cost you 500 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory.")) {
                if (sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1) && sm.addMoney(-500)) {
                    sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1);
                    sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 1 has some precious items available but with so many traps all over the place most come back out early. Wishing you the best of luck.");
                } else {
                    sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.");
                }
            } else {
                sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.");
            }
        } else if (answer == 1) {
            if (sm.askYesNo("Will you purchase the Ticket to #bConstruction site B2#k? It'll cost you 1200 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory.")) {
                if (sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1) && sm.addMoney(-1200)) {
                    sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1);
                    sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 2 has rare, precious items available but with so many traps all over the place most come back out early. Please be safe.");
                } else {
                    sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.");
                }
            } else {
                sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.");
            }
        } else if (answer == 2) {
            if (sm.askYesNo("Will you purchase the Ticket to #bConstruction site B3#k? It'll cost you 2000 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory.")) {
                if (sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1) && sm.addMoney(-2000)) {
                    sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1);
                    sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 3 has very rare, very precious items available but with so many traps all over the place most come back out early. Wishing you the best of luck.");
                } else {
                    sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.");
                }
            } else {
                sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.");
            }
        }
    }

    @Script("subway_in")
    public static void subway_in(ScriptManager sm) {
        // The Ticket Gate (1052007)
        //   Victoria Road : Subway Ticketing Booth (103000100)
        //   Kerning City Subway : Subway Ticketing Booth (103020000)
        final int answer = sm.askMenu("Pick your destination.", Map.of(
                0, bold("Kerning City Subway" + red("Beware of Stirges and Wraiths!")),
                1, "Kerning Square Shopping Center (Get on the Subway)",
                2, "Enter Construction Site",
                3, "New Leaf City"
        ));
        if (answer == 0) {
            // Kerning City Subway : Along the Subway
            sm.warp(103020100, "out00");
        } else if (answer == 1) {
            // Kerning Square : Kerning Square Station
            sm.warpInstance(KERNING_SQUARE_SUBWAY_1, "sp", 103020020, 10);
        } else if (answer == 2) {
            // Enter Construction Site
            if (!sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B1) && !sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B2) && !sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B3)) {
                sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.");
                return;
            }
            final Map<Integer, String> options = new HashMap<>();
            if (sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B1)) {
                options.put(0, "Construction site B1");
            }
            if (sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B2)) {
                options.put(1, "Construction site B2");
            }
            if (sm.hasItem(TICKET_TO_CONSTRUCTION_SITE_B3)) {
                options.put(2, "Construction site B3");
            }
            final int ticketAnswer = sm.askMenu("Here's the ticket reader. You will be brought in immediately. Which ticket would you like to use?", options);
            if (ticketAnswer == 0) {
                if (sm.removeItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1)) {
                    sm.warp(910360000, "sp"); // B1 : Area 1
                }
            } else if (ticketAnswer == 1) {
                if (sm.removeItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1)) {
                    sm.warp(910360100, "sp"); // B2 : Area 1
                }
            } else if (ticketAnswer == 2) {
                if (sm.removeItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1)) {
                    sm.warp(910360200, "sp"); // B3 : Area 1
                }
            }
        } else if (answer == 3) {
            // New Leaf City
            if (!sm.hasItem(Masteria.SUBWAY_TICKET_TO_NLC)) {
                sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.");
                return;
            }
            final EventState eventState = sm.getEventState(EventType.CM_SUBWAY);
            if (eventState == EventState.SUBWAY_BOARDING) {
                if (!sm.askYesNo("It looks like there's plenty of room for this ride. Please have your ticket ready so I can let you in. The ride will be long, but you'll get to your destination just fine. What do you think? Do you want to get on this ride?")) {
                    return;
                }
                if (sm.removeItem(Masteria.SUBWAY_TICKET_TO_NLC, 1)) {
                    sm.warp(Subway.WAITING_ROOM_FROM_KC_TO_NLC, "st00"); // Kerning City Town Street : Waiting Room (From KC to NLC)
                }
            } else if (eventState == EventState.SUBWAY_WAITING) {
                sm.sayNext("This subway is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the usher at the ticketing booth.");
            } else {
                sm.sayNext("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the subway will take off right on time, and we stop receiving tickets 1 minute before that, so please make sure to be here on time.");
            }
        }
    }

    @Script("subway_in2")
    public static void subway_in2(ScriptManager sm) {
        // Victoria Road : Subway Ticketing Booth (103000100)
        //   in00 (200, 187)
        // Kerning City Subway : Subway Ticketing Booth (103020000)
        //   in00 (200, 187)
        subway_in(sm);
    }

    @Script("Depart_inSubway")
    public static void Depart_inSubway(ScriptManager sm) {
        // Kerning City Subway : Kerning Square Subway (103020010)
        // Kerning City Subway : Kerning Square Subway (103020011)
        // Kerning City Subway : Kerning Square Subway (103020012)
        if (sm.getFieldId() == KERNING_SQUARE_SUBWAY_1) {
            sm.scriptProgressMessage("The next stop is at Kerning Square Station. The exit is to your left.");
        } else if (sm.getFieldId() == KERNING_SQUARE_SUBWAY_2) {
            sm.scriptProgressMessage("The next stop is at Kerning Subway Station. The exit is to your left.");
        }
    }

    @Script("Depart_ToKerning")
    public static void Depart_ToKerning(ScriptManager sm) {
        // Kerning Square : Kerning Square Station (103020020)
        //   out00 (465, 26)
        sm.playPortalSE();
        sm.warpInstance(KERNING_SQUARE_SUBWAY_2, "sp", 103020000, 10); // Kerning City Subway : Subway Ticketing Booth
    }

    @Script("enter_VDS")
    public static void enter_VDS(ScriptManager sm) {
        // Sleepywood : Sleepywood (105000000)
        //   east00 (1759, 312)
        sm.playPortalSE();
        sm.warp(105010000, "west00"); // Swamp : Silent Swamp
    }

    @Script("enterAchter")
    public static void enterAchter(ScriptManager sm) {
        // Henesys : Henesys Park (100000200)
        //   in02 (3941, 693)
        sm.playPortalSE();
        sm.warp(100000201, "out02"); // Henesys : Bowman Instructional School
    }

    @Script("enterMagiclibrar")
    public static void enterMagiclibrar(ScriptManager sm) {
        // Ellinia : Ellinia (101000000)
        //   jobin00 (-250, -473)
        sm.playPortalSE();
        sm.warp(101000003, "jobout00"); // Ellinia : Magic Library
    }

    @Script("inERShip")
    public static void inERShip(ScriptManager sm) {
        // Port Road : Victoria Tree Platform (104020100)
        //   in01 (-422, -745)
        sm.playPortalSE();
        sm.warp(104020120, "out00"); // Port Road : Station to Ereve
    }
}
