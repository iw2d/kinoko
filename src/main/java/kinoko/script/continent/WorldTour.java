package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.event.Airport;
import kinoko.server.event.EventState;
import kinoko.server.event.EventType;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class WorldTour extends ScriptHandler {
    public static final int TICKET_TO_SINGAPORE = 4031731;
    public static final int TICKET_TO_KERNING_CITY = 4031732;

    @Script("world_trip")
    public static void world_trip(ScriptManager sm) {
        // Spinel : World Tour Guide (9000020)
        //   Henesys : Henesys (100000000)
        //   Ellinia : Ellinia (101000000)
        //   Perion : Perion (102000000)
        //   Kerning City : Kerning City (103000000)
        //   Lith Harbor : Lith Harbor (104000000)
        //   Orbis : Orbis (200000000)
        //   Ludibrium : Ludibrium (220000000)
        //   Leafre : Leafre (240000000)
        //   Mu Lung : Mu Lung (250000000)
        //   The Burning Road : Ariant (260000000)
        //   Singapore : Boat Quay Town (541000000)
        //   Amoria : Amoria (680000000)
        //   Zipangu : Mushroom Shrine (800000000)
        //   Hidden Street : Travel's End (950000000)
        //   Hidden Street : Spinel's Forest (950000100)
        final List<Integer> towns = List.of(
                100000000, // Henesys : Henesys
                101000000, // Ellinia : Ellinia
                102000000, // Perion : Perion
                103000000, // Kerning City : Kerning City
                104000000, // Lith Harbor : Lith Harbor
                200000000, // Orbis : Orbis
                220000000, // Ludibrium : Ludibrium
                240000000, // Leafre : Leafre
                250000000, // Mu Lung : Mu Lung
                260000000, // The Burning Road : Ariant
                541000000, // Singapore : Boat Quay Town
                680000000 // Amoria : Amoria
                // 800000000, 950000000, 950000100
        );
        if (towns.contains(sm.getFieldId())) {
            sm.sayNext("If you're tired of the monotony of daily life, it might be time for change, and the #bMaple Travel Agency#k is here to give your life a new sense of adventure! For a low, low free, we can offer you a #bWorld Tour#k that will make your dreariest days sparkle!");
            final int answer = sm.askMenu("We're currently servicing multiple destinations for your traveling pleasure, though our list of service areas is always expanding. Just pick your destination below, and I'll be there to serve you as your travel guide.", Map.of(
                    0, "Mushroom Shrine of Japan (3,000 mesos)",
                    1, "Malaysian Metropolis (300,000 mesos)"
            ));
            if (answer == 0) {
                sm.sayNext("Would you like to travel to #bMushrom Shrine of Japan#k? If you desire to feel the essence of Japan, there's nothing like visiting the Shrine, a Japanese cultural melting pot. Mushroom Shrine is a mythical place that serves the incomparable Mushroom God from ancient times.");
                sm.sayNext("Check out the female shaman serving the Mushroom God, and I strongly recommend trying Takoyaki, Yakisoba, and other delocious food sold in the streets of Japan. Now, let's head over to #bMushroom Shrine#k, a mythical place if there ever was one.");
                if (sm.addMoney(-3000)) {
                    sm.warp(800000000, "st00"); // Zipangu : Mushroom Shrine
                    sm.setQRValue(QuestRecordType.WorldTour, String.valueOf(sm.getFieldId()));
                } else {
                    sm.sayOk("I'm afraid you don't have enough meso.");
                }
            } else if (answer == 1) {
                if (sm.askYesNo("You can return to Victoria Island through the Changi Airport in CBD. Would you like to go now? It will cost 300,000 mesos.")) {
                    if (sm.addMoney(-300000)) {
                        sm.warp(550000000); // Malaysia : Trend Zone Metropolis
                    } else {
                        sm.sayOk("I'm afraid you don't have enough meso.");
                    }
                } else {
                    sm.sayOk("OK. if you ever change your mind, please let me know.");
                }
            }
        } else if (sm.getFieldId() == 800000000) {
            // Zipangu : Mushroom Shrine
            int returnMapId = 100000000;  // Henesys : Henesys
            final String returnMap = sm.getQRValue(QuestRecordType.WorldTour);
            if (Util.isInteger(returnMap) && towns.contains(Integer.parseInt(returnMap))) {
                returnMapId = Integer.parseInt(returnMap);
            }
            final int answer = sm.askMenu("How's the traveling? Are you enjoying it?", Map.of(
                    0, String.format("Yes, I'm done with travelling. Can I go back to #e#m%d##n?", returnMapId),
                    1, "No, I'd like to continue exploring this place."
            ));
            if (answer == 0) {
                sm.sayNext("Alright. I'll take you back to where you were before the visit to Japan. If you ever feel like traveling again down the road, please let me know!");
                sm.warp(returnMapId);
                sm.setQRValue(QuestRecordType.WorldTour, "");
            } else {
                sm.sayOk("OK. if you ever change your mind, please let me know.");
            }
        }
    }


    // ZIPANGU SCRIPTS -------------------------------------------------------------------------------------------------

    @Script("in_bath")
    public static void in_bath(ScriptManager sm) {
        // Hikari (9120003)
        //   Zipangu : Showa Town (801000000)
        if (!sm.askYesNo("Would you like to enter the bathhouse? That'll be 300 mesos for you.")) {
            return;
        }
        if (sm.addMoney(-300)) {
            sm.warp(801000100 + (100 * sm.getGender()), "out00");
        } else {
            sm.sayOk("Please check and see if you have 300 mesos to enter this place");
        }
    }

    @Script("goNinja")
    public static void goNinja(ScriptManager sm) {
        // Palanquin (9110107)
        //   Zipangu : Mushroom Shrine (800000000)
        //   Zipangu : Outside Ninja Castle (800040000)
        if (sm.getFieldId() == 800000000) {
            // Zipangu : Mushroom Shrine
            sm.sayNext("We are... the palanquin... bearers! Need to... get to... Ninja Castle? Talk to us! Talk to us!");
            if (sm.askYesNo("Huh, what? You want to go to Ninja Castle?")) {
                sm.sayNext("Got it! We are... the palanquin.... bearers! We'll get you there faster than you can blink. And since we're in such a jolly mood, you don't even have to pay us!");
                sm.warp(800040000); // Zipangu : Outside Ninja Castle
            }
        } else if (sm.getFieldId() == 800040000) {
            // Zipangu : Outside Ninja Castle
            sm.sayNext("We are... the palanquin... bearers! Need to... get to... Ninja Castle? Talk to us! Talk to us!");
            if (sm.askYesNo("Huh, what? You want to go to Mushroom Shrine?")) {
                sm.sayNext("Got it! We are... the palanquin.... bearers! We'll get you there faster than you can blink. And since we're in such a jolly mood, you don't even have to pay us!");
                sm.warp(800000000); // Zipangu : Mushroom Shrine
            }
        }
    }


    // SINGAPORE SCRIPTS -----------------------------------------------------------------------------------------------

    @Script("sellticket_sg")
    public static void sellticket_sg(ScriptManager sm) {
        // Irene : Ticketing Usher (9270041)
        //   Kerning City : Kerning City (103000000)
        final int answer = sm.askMenu("Hello there~ I am Irene from Singapore Airport. I was transferred to Kerning City to celebrate the new opening of our service! How can I help you?", Map.of(
                0, "I would like to buy a plane ticket to Singapore",
                1, "Let me go in to the departure point"
        ));
        if (answer == 0) {
            if (sm.askYesNo("The ticket will cost you 5,000 mesos. Will you purchase the ticket?")) {
                if (sm.canAddItem(TICKET_TO_SINGAPORE, 1)) {
                    if (sm.addMoney(-5000)) {
                        sm.addItem(TICKET_TO_SINGAPORE, 1);
                        sm.sayOk("Thank you for choosing Wizet Airline! Enjoy your flight!");
                    } else {
                        sm.sayOk("You don't have enough mesos.");
                    }
                } else {
                    sm.sayOk("Please check if your inventory is full or not.");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("Would you like to go in now? You will lose your ticket once you go in~ Thank you for choosing Wizet Airline.")) {
                if (sm.hasItem(TICKET_TO_SINGAPORE)) {
                    if (sm.getEventState(EventType.CM_AIRPORT) == EventState.AIRPORT_BOARDING) {
                        sm.removeItem(TICKET_TO_SINGAPORE, 1);
                        sm.warp(Airport.KERNING_AIRPORT, "sp"); // Victoria Island : Kerning Airport
                    } else {
                        sm.sayOk("Sorry, the plane has already taken off. Please wait a few minutes.");
                    }
                } else {
                    sm.sayOk(String.format("You need a #b#t%d##k to get on the plane!", TICKET_TO_SINGAPORE));
                }
            }
        }
    }

    @Script("sellticket_cbd")
    public static void sellticket_cbd(ScriptManager sm) {
        // Shalon : Ticketing Usher (9270038)
        //   Singapore : Changi Airport (540010000)
        final int answer = sm.askMenu("Hello there~ I am Shalon from Singapore Airport. How can I help you?", Map.of(
                0, "I would like to buy a plane ticket to Kerning City",
                1, "Let me go in to the departure point"
        ));
        if (answer == 0) {
            if (sm.askYesNo("The ticket will cost you 5,000 mesos. Will you purchase the ticket?")) {
                if (sm.canAddItem(TICKET_TO_KERNING_CITY, 1)) {
                    if (sm.addMoney(-5000)) {
                        sm.addItem(TICKET_TO_KERNING_CITY, 1);
                        sm.sayOk("Thank you for choosing Wizet Airline! Enjoy your flight!");
                    } else {
                        sm.sayOk("You don't have enough mesos.");
                    }
                } else {
                    sm.sayOk("Please check if your inventory is full or not.");
                }
            }
        } else if (answer == 1) {
            if (sm.askYesNo("Would you like to go in now? You will lose your ticket once you go in~ Thank you for choosing Wizet Airline.")) {
                if (sm.hasItem(TICKET_TO_KERNING_CITY)) {
                    if (sm.getEventState(EventType.CM_AIRPORT) == EventState.AIRPORT_BOARDING) {
                        sm.removeItem(TICKET_TO_KERNING_CITY, 1);
                        sm.warp(Airport.BEFORE_DEPARTURE_TO_KERNING_CITY, "sp"); // Singapore : Before Departure (To Kerning City)
                    } else {
                        sm.sayOk("Sorry, the plane has already taken off. Please wait a few minutes.");
                    }
                } else {
                    sm.sayOk(String.format("You need a #b#t%d##k to get on the plane!", TICKET_TO_KERNING_CITY));
                }
            }
        }
    }

    @Script("goback_kerning")
    public static void goback_kerning(ScriptManager sm) {
        // Xinga : Pilot (9270017)
        //   Victoria Island : Kerning Airport (540010100)
        if (sm.getFieldId() == 540010100) {
            // Victoria Island : Kerning Airport
            if (sm.askYesNo("The plane will be taking off soon, will you leave now? You will have to buy the plane ticket again to come in here.")) {
                sm.sayNext("The ticket is not refundable, hope to see you again!");
                sm.warp(103000000); // Kerning City : Kerning City
            }
        }
    }

    @Script("goback_cbd")
    public static void goback_cbd(ScriptManager sm) {
        // Kerny : Pilot (9270018)
        //   Singapore : Before Departure (To Kerning City) (540010001)
        //   Singapore : On the way to Kerning City (540010002)
        //   Victoria Island : On the way to CBD (540010101)
        if (sm.getFieldId() == 540010001) {
            // Singapore : Before Departure (To Kerning City)
            if (sm.askYesNo("The plane will be taking off soon, will you leave now? You will have to buy the plane ticket again to come in here.")) {
                sm.sayNext("The ticket is not refundable, hope to see you again!");
                sm.warp(540010000); //  Singapore : Changi Airport
            }
        }
    }

    @Script("Malay_Warp2")
    public static void Malay_Warp2(ScriptManager sm) {
        // Audrey : Malaysia Tour Guide (9201135)
        //   Singapore : CBD (540000000)
        //   Malaysia : Trend Zone Metropolis (550000000)
        //   Malaysia : Kampung Village (551000000)
        final Consumer<List<Tuple<Integer, Integer>>> handleWarp = (locations) -> {
            final Map<Integer, String> options = createOptions(locations, (tuple) -> String.format("#m%d# (%,d mesos)", tuple.getLeft(), tuple.getRight()));
            final int answer = sm.askMenu("Where would you like to travel?", options);
            if (answer >= 0 && answer < locations.size()) {
                final int mapId = locations.get(answer).getLeft();
                final int price = locations.get(answer).getRight();
                if (sm.askYesNo(String.format("Would you like to travel to #b#m%d##? To head over to #m%d#, it'll cost you #b%,d mesos#k=. Would you like to go right now?", mapId, mapId, price))) {
                    if (sm.addMoney(-price)) {
                        sm.warp(mapId);
                    } else {
                        sm.sayNext("You do not seem to have enough mesos.");
                    }
                } else {
                    sm.sayNext("You know where to come if you need a ride!");
                }
            }
        };
        if (sm.getFieldId() == 540000000) {
            // Singapore : CBD
            handleWarp.accept(List.of(
                    Tuple.of(550000000, 1000), // Malaysia : Trend Zone Metropolis
                    Tuple.of(551000000, 10000) // Malaysia : Kampung Village
            ));
        } else if (sm.getFieldId() == 550000000) {
            // Malaysia : Trend Zone Metropolis
            handleWarp.accept(List.of(
                    Tuple.of(540000000, 1000), // Singapore : CBD
                    Tuple.of(551000000, 10000) // Malaysia : Kampung Village
            ));
        } else if (sm.getFieldId() == 551000000) {
            // Malaysia : Kampung Village
            handleWarp.accept(List.of(
                    Tuple.of(540000000, 10000), // Singapore : CBD
                    Tuple.of(550000000, 10000) // Malaysia : Trend Zone Metropolis
            ));
        }
    }

    @Script("treeboss00")
    public static void treeboss00(ScriptManager sm) {
        // Singapore : Ruins of Krexel I (541020700)
        //   boss00 (81, -1751)
        final Predicate<User> entryRequirement = (user) -> {
            if (user.getLevel() < 70) {
                return false;
            }
            // Soul Lantern
            if (!user.getInventoryManager().hasItem(4000385, 1)) {
                return false;
            }
            // Savior of Ulu City
            return user.getQuestManager().hasQuestStarted(4530) || user.getQuestManager().hasQuestCompleted(4530);
        };
        if (sm.getUser().hasParty()) {
            if (!sm.getUser().isPartyBoss()) {
                sm.message("You are not the leader of the party.");
                return;
            }
            if (!sm.checkParty(1, entryRequirement)) {
                sm.message("One or more members of your party do not meet the requirements to enter.");
                return;
            }
        } else if (!entryRequirement.test(sm.getUser())) {
            sm.message("You do not meet the requirements to enter.");
            return;
        }
        sm.partyWarpInstance(541020800, "sp", 541020700, 60 * 30);
    }

    @Script("treeboss01")
    public static void treeboss01(ScriptManager sm) {
        // Commando Jim (9270045)
        //   Singapore : Ruins of Krexel II (541020800)
        if (sm.askYesNo("Are you sure you want to leave The Ruin of Krexel II? I can take you to the safer place...")) {
            sm.warp(541020700);
        }
    }

    @Script("treeBossSG")
    public static void treeBossSG(ScriptManager sm) {
        // treeBossSG (5411001)
        //   Singapore : Ruins of Krexel II (541020800)
        sm.soundEffect("Bgm09/TimeAttack");
        sm.spawnMob(9420520, MobAppearType.NORMAL, sm.getSource().getX(), sm.getSource().getY(), true);
        sm.broadcastMessage("As you wish, here comes Krexel.");
    }
}
