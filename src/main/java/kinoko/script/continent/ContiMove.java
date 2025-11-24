package kinoko.script.continent;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptError;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.event.*;
import kinoko.util.Util;
import kinoko.world.job.JobConstants;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ContiMove extends ScriptHandler {
    @Script("sell_ticket")
    public static void sell_ticket(ScriptManager sm) {
        // Joel : Station Clerk (1032007)
        //   Port Road : Station to Orbis (104020110)
        // Agatha (2012000)
        //   Orbis : Orbis Station Entrance (200000100)
        // Mel : Selling Ticket to Orbis (2040000)
        //   Ludibrium : Ludibrium Ticketing Place (220000100)
        // Mue : Ticket Usher (2082000)
        //   Leafre : Leafre Station Entrance (240000100)
        // Syras : Ticket Box Desk (2102002)
        //   Ariant : Ariant Station Platform (260000100)
        if (sm.getFieldId() == 104020110) {
            // Port Road : Station to Orbis
            sm.sayNext("Pleased to meet you. I'm Joel, the station attendant. Want to leave Victoria Island and go to another area? At our station, we have an #bairship#k bound for #bOrbis Station#k, on the continent of Ossyria, leaving #bevery 15 minutes on the hour#k.");
            sm.sayBoth("If you are thinking of going to Orbis, please go talk to #bCherry#k on the right.");
            sm.sayBoth("Well, the truth is, we charged for these flights until very recently, but the alchemists of Magatia made a crucial discovery on the fuel that dramatically cuts down the amount of Mana used for the flight, so these flight are now free. Don't worry, we still get paid. Now we just get paid through the government.");
        } else if (sm.getFieldId() == 200000100) {
            // Orbis : Orbis Station Entrance
            final int answer = sm.askMenu("I can guide you to the right ship to reach your destination. Where are you headed?", Map.of(
                    0, "Victoria Island",
                    1, "Ludibrium Castle",
                    2, "Leafre",
                    3, "Mu Lung",
                    4, "Ariant",
                    5, "Ereve",
                    6, "Edelstein"
            ));
            if (answer == 0) {
                sm.sayNext("You're headed to Victoria Island? Oh, it's a beautiful island with a variety of villages. The ship to Victoria Island #bleaves every 15 minutes on the hour#k.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Airship to Victoria. If anyone can show you the way, it's Isa.");
            } else if (answer == 1) {
                sm.sayNext("You're headed to Ludibrium Castle at Ludus Lake? It's such a fun village made of toys. The ship to Ludibrium #bleaves every 10 minutes on the hour#k.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Airship to Ludibrium. If anyone can show you the way, it's Isa.");
            } else if (answer == 2) {
                sm.sayNext("You're headed to Leafre in Minar Forest? I love that quaint little village of Halflingers. The ship to Leafre #bleaves every 10 minutes on the hour#k.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Airship to Leafre. If anyone can show you the way, it's Isa.");
            } else if (answer == 3) {
                sm.sayNext("Are you heading towards Mu Lung in the Mu Lung temple? I'm sorry, but there's no ship that flies from Orbis to Mu Lung. There is another way to get there, though. There's a #bCrane that runs a cab service for 1 that's always available#k, so you'll get there as soon as you wish.");
                sm.sayBoth("Unlike the other ships that fly for free, however, this cab requires a set fee. This personalized flight to Mu Lung will cost you #b1,500 mesos#k, so please have to fee ready before riding the Crane.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Crane to Mu Lung. If anyone can show you the way, it's Isa.");
            } else if (answer == 4) {
                sm.sayNext("You're headed to Ariant in the Nihal Desert? The people living there have a passion as hot as the desert. The ship to Ariant #bleaves every 10 minutes on the hour#k.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the Genie to Ariant. If anyone can show you the way, it's Isa.");
            } else if (answer == 5) {
                sm.sayNext("Are you heading towards Ereve? It's a beautiful island blessed with the presence of the Shinsoo the Holy Beast and Empress Cygnus. #bThe boat is for 1 person and it's always readily available#k so you can travel to Ereve fast.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the ship to Ereve. If anyone can show you the way, it's Isa.");
            } else if (answer == 6) {
                sm.sayNext("Are you going to Edelstein? The brave people who live there constantly fight the influence of dangerous monsters. #b1-person Airship to Edelstein is always on standby#k, so you can use it at any time.");
                sm.sayBoth("Talk to #bIsa the Platform Guide#k on the right if you would like to take the ship to Edelstein. If anyone can show you the way, it's Isa.");
            }
        } else if (sm.getFieldId() == 220000100) {
            // Ludibrium : Ludibrium Ticketing Place
            sm.sayNext("Pleased to meet you. I'm Mel, the station attendant. Are you ready to leave Ludibrium and go to another area? At our station, we have an #bairship#k bound for #bOrbis Station#k, on the continent of Ossyria, leaving #bevery 10 minutes on the hour#k.");
            sm.sayBoth("If you are planning on heading to Orbis, please use the portal on the right and head to the station, then talk to #bTian#k.");
            sm.sayBoth("Sigh... Free flights to everywhere... I don't understand what got the alchemists of Magatia to come up with something like this. This is making our job that much harder, because there are so many more passengers now. Sigh...");
        } else if (sm.getFieldId() == 240000100) {
            // Leafre : Leafre Station Entrance
            sm.sayNext("Pleased to meet you. I'm Mu, the station attendant. Would you like to leave Leafre and go to another area? At our station, we have an #bairship#k bound for #bOrbis Station#k, on the continent of Ossyria, leaving #bevery 10 minutes on the hour#k.");
            sm.sayBoth("If you're going to Orbis, use the portal on the right and head to the station, then talk to #bTommie#k. Ah, don't be surprised when you see him. We keep being mistaken for being twins, but Tommie's actually my third oldest brother.");
            sm.sayBoth("Oh, and this is just between you and me... at the highest point of the station, you'll find a strange old man named Corba. Apparently, he possesses the mystic power to transform people into flying dragons. It has been said that once transformed into a flying dragon, you can fly to the mysterious floating island. Surely adventurers of level 100 and up will be intrigued...");
        } else if (sm.getFieldId() == 260000100) {
            // Ariant : Ariant Station Platform
            sm.sayNext("Hey. I'm the station attendant, Syras. You wanna leave Ariant and go to another area? Here at our station we have a #bgenie#k that's headed to #bOrbis Station#k, on the continent of Ossyria, #bleaving every 10 minutes on the hour#k.");
            sm.sayBoth("If you're going to Orbis, talk to that old man on the right, #bAsesson#k. He has a hard time hearing, so you may want to yell at him to get his attention.");
            sm.sayBoth("Oh, and in case you're not aware of this, somewhere in the desert, a mysterious-looking man called Karcasa sends people to Victoria Island for a fee. I hope you understand that it's against the law to fly these innocent people to other towns without permit!!");
            sm.sayPrev("The Camel Cab, however, is permitted by the king so you can use that. Well, that cab will only take you up to Magatia, but it's still legal.");
        }
    }

    @Script("get_ticket")
    public static void get_ticket(ScriptManager sm) {
        // Cherry : Cabin Crew (1032008)
        //   Port Road : Station to Orbis (104020110)
        // Rini : Cabin Crew (2012001)
        //   Orbis : Station <Victoria Bound> (200000111)
        // Sunny : Cabin Crew (2012013)
        //   Orbis : Station<Ludibrium> (200000121)
        // Ramini : Crewmember (2012021)
        //   Orbis : Cabin <To Leafre> (200000131)
        // Geras : Crew (2012025)
        //   Orbis : Station <To Ariant> (200000151)
        // Tian : Cabin Crew (2041000)
        //   Ludibrium : Station<Orbis> (220000110)
        // Tommie : Crewmember (2082001)
        //   Leafre : Station (240000110)
        // Asesson : Crew (2102000)
        //   Ariant : Ariant Station Platform (260000100)
        final EventType eventType;
        final String moveType;
        final int waitingField;
        switch (sm.getFieldId()) {
            case ContiMoveVictoria.ORBIS_STATION_VICTORIA_BOUND -> {
                // Orbis : Station <Victoria Bound>
                eventType = EventType.CM_VICTORIA;
                moveType = "ship";
                waitingField = ContiMoveVictoria.PRE_DEPARTURE_VICTORIA_BOUND;
            }
            case ContiMoveVictoria.STATION_TO_ORBIS -> {
                // Port Road : Station to Orbis
                eventType = EventType.CM_VICTORIA;
                moveType = "ship";
                waitingField = ContiMoveVictoria.PRE_DEPARTURE_TO_ORBIS;
            }
            case ContiMoveLudibrium.ORBIS_STATION_LUDIBRIUM -> {
                // Orbis : Station <Ludibrium>
                eventType = EventType.CM_LUDIBRIUM;
                moveType = "ship";
                waitingField = ContiMoveLudibrium.BEFORE_THE_DEPARTURE_TO_LUDIBRIUM;
            }
            case ContiMoveLudibrium.LUDIBRIUM_STATION_ORBIS -> {
                // Ludibrium : Station <Orbis>
                eventType = EventType.CM_LUDIBRIUM;
                moveType = "ship";
                waitingField = ContiMoveLudibrium.BEFORE_THE_DEPARTURE_TO_ORBIS;
            }
            case ContiMoveLeafre.ORBIS_STATION_TO_LEAFRE -> {
                // Orbis : Cabin <To Leafre>
                eventType = EventType.CM_LEAFRE;
                moveType = "ship";
                waitingField = ContiMoveLeafre.ORBIS_CABIN_TO_LEAFRE;
            }
            case ContiMoveLeafre.LEAFRE_STATION -> {
                // Leafre : Station
                eventType = EventType.CM_LEAFRE;
                moveType = "ship";
                waitingField = ContiMoveLeafre.BEFORE_TAKEOFF_TO_ORBIS;
            }
            case ContiMoveAriant.ORBIS_STATION_TO_ARIANT -> {
                // Orbis : Station <To Ariant>
                eventType = EventType.CM_ARIANT;
                moveType = "genie";
                waitingField = ContiMoveAriant.BEFORE_TAKEOFF_TO_ARIANT;
            }
            case ContiMoveAriant.ARIANT_STATION_PLATFORM -> {
                // Ariant : Ariant Station Platform
                eventType = EventType.CM_ARIANT;
                moveType = "genie";
                waitingField = ContiMoveAriant.BEFORE_TAKEOFF_TO_ORBIS;
            }
            default -> {
                throw new ScriptError("Tried to board ship from field ID : %d", sm.getFieldId());
            }
        }
        final EventState eventState = sm.getEventState(eventType);
        if (eventState == EventState.CONTIMOVE_BOARDING) {
            if (sm.askYesNo(String.format("This will not be a short flight, so you need to take care of some things, I suggest you do that first before getting on board. Do you still wish to board the %s?", moveType))) {
                sm.warp(waitingField);
            } else {
                sm.sayNext("You must have some business to take care of here, right?");
            }
        } else if (eventState == EventState.CONTIMOVE_WAITING) {
            sm.sayNext(String.format("This %s is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the usher at the ticketing booth.", moveType));
        } else {
            sm.sayNext(String.format("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the %s will take off on time, and we stop receiving tickets 1 minute before that, so please make sure to be here on time.", moveType));
        }
    }

    @Script("goOutWaitingRoom")
    public static void goOutWaitingRoom(ScriptManager sm) {
        // Purin : Crewmember (1032009)
        //   Port Road : To Orbis <Before Starting> (104020111)
        // Erin : Crewmember (2012002)
        //   Orbis : Pre-Departure <Victoria Bound> (200000112)
        // Pelace : Crewmember (2012022)
        //   Orbis : Cabin <To Leafre> (200000132)
        // Egnet : Crew (2012024)
        //   Orbis : Station <To Ariant> (200000152)
        // Rosey : Crewmember (2041001)
        //   Orbis : Before the Departure <Ludibrium> (200000122)
        //   Ludibrium : Before the Departure <Orbis> (220000111)
        // Harry : Crewmember (2082002)
        //   Leafre : Before Takeoff <To Orbis> (240000111)
        // Slyn : Crew (2102001)
        //   Ariant : Before Takeoff <To Orbis> (260000110)
        final int boardingField;
        switch (sm.getFieldId()) {
            case ContiMoveVictoria.PRE_DEPARTURE_VICTORIA_BOUND -> {
                // Orbis : Pre-Departure <Victoria Bound>
                boardingField = ContiMoveVictoria.ORBIS_STATION_VICTORIA_BOUND;
            }
            case ContiMoveVictoria.PRE_DEPARTURE_TO_ORBIS -> {
                // Port Road : To Orbis <Before Starting>
                boardingField = ContiMoveVictoria.STATION_TO_ORBIS;
            }
            case ContiMoveLudibrium.BEFORE_THE_DEPARTURE_TO_LUDIBRIUM -> {
                // Orbis : Before the Departure <Ludibrium>
                boardingField = ContiMoveLudibrium.ORBIS_STATION_LUDIBRIUM;
            }
            case ContiMoveLudibrium.BEFORE_THE_DEPARTURE_TO_ORBIS -> {
                // Ludibrium : Before the Departure <Orbis>
                boardingField = ContiMoveLudibrium.LUDIBRIUM_STATION_ORBIS;
            }
            case ContiMoveLeafre.ORBIS_CABIN_TO_LEAFRE -> {
                // Orbis : Cabin <To Leafre>
                boardingField = ContiMoveLeafre.ORBIS_STATION_TO_LEAFRE;
            }
            case ContiMoveLeafre.BEFORE_TAKEOFF_TO_ORBIS -> {
                // Leafre : Before Takeoff <To Orbis>
                boardingField = ContiMoveLeafre.LEAFRE_STATION;
            }
            case ContiMoveAriant.BEFORE_TAKEOFF_TO_ARIANT -> {
                // Orbis : Station <To Ariant>
                boardingField = ContiMoveAriant.ORBIS_STATION_TO_ARIANT;
            }
            case ContiMoveAriant.BEFORE_TAKEOFF_TO_ORBIS -> {
                // Ariant : Before Takeoff <To Orbis>
                boardingField = ContiMoveAriant.ARIANT_STATION_PLATFORM;
            }
            default -> {
                throw new ScriptError("Tried to leave ship from field ID : %d", sm.getFieldId());
            }
        }
        if (sm.askYesNo("We're just about to take off. Are you sure you want to get off the ship? You may do so, but then you'll have to wait until the next available flight. Do you still wish to get off board?")) {
            sm.warp(boardingField, "sp");
        } else {
            sm.sayOk("You'll get to your destination in a short while. Talk to other passengers and share your stories to them, and you'll be there before you know it.");
        }
    }

    @Script("sBoxItem0")
    public static void sBoxItem0(ScriptManager sm) {
        // sBoxItem0 (9102000)
        //   During the Ride : Cabin <To Orbis> (200090011)
        sm.dropRewards(List.of(
                Reward.money(15, 15, 0.7),
                Reward.item(2000000, 1, 1, 0.1), // Red Potion
                Reward.item(2000001, 1, 1, 0.1), // Orange Potion
                Reward.item(2000002, 1, 1, 0.1), // White Potion
                Reward.item(2000002, 1, 1, 0.1), // Blue Potion
                Reward.item(2010000, 1, 1, 0.1), // Apple
                Reward.item(2010003, 1, 1, 0.1), // Orange
                Reward.item(2010004, 1, 1, 0.1), // Lemon
                Reward.item(4031158, 1, 1, 0.8, 2074) // Maple History Book II
        ));
    }


    // CRANE SCRIPTS ---------------------------------------------------------------------------------------------------

    @Script("crane")
    public static void crane(ScriptManager sm) {
        // Crane : Public Transportation (2090005)
        //   Orbis : Cabin <To Mu Lung> (200000141)
        //   Mu Lung : Mu Lung Temple (250000100)
        //   Herb Town : Herb Town (251000000)
        if (sm.getFieldId() == 200000141) {
            // Orbis : Cabin <To Mu Lung>
            final int answer = sm.askMenu("Hello there. How's the traveling so far? I've been transporting other travelers like you to other regions in no time, and... are you interested? If so, then select the town you'd like to head to.", Map.of(
                    0, "Mu Lung (1500 mesos)"
            ));
            if (answer == 0) {
                if (sm.addMoney(-1500)) {
                    // During the Ride : To Mu Lung -> Mu Lung : Mu Lung Temple
                    sm.warpInstance(200090300, "sp", 250000100, 60);
                } else {
                    sm.sayNext("Are you sure you have enough mesos?");
                }
            }
        } else if (sm.getFieldId() == 250000100) {
            // Mu Lung : Mu Lung Temple
            final int answer = sm.askMenu("Hello there. How's the traveling so far? I understand that walking on two legs is much harder to cover ground compared to someone like me that can navigate the skies. I've been transporting other travelers like you to other regions in no time, and... are you interested? If so, then select the town you'd like to head to.", Map.of(
                    0, "Orbis (1500 mesos)",
                    1, "Herb Town (500 mesos)"
            ));
            if (answer == 0) {
                if (sm.askYesNo("Do you want to fly to #bOrbis#k right now? As long as you don't act silly while in the air, you should reach your destination in no time. It'll only cost you #b1500 mesos#k.")) {
                    if (sm.addMoney(-1500)) {
                        // During the Ride : To Orbis -> Orbis : Cabin <To Mu Lung>
                        sm.warpInstance(200090310, "sp", 200000141, 60);
                    } else {
                        sm.sayNext("Are you sure you have enough mesos?");
                    }
                } else {
                    sm.sayOk("OK. if you ever change your mind, please let me know.");
                }
            } else if (answer == 1) {
                if (sm.askYesNo("Do you want to fly to #bHerb Town#k right now? As long as you don't act silly while in the air, you should reach your destination in no time. It'll only cost you #b500 mesos#k.")) {
                    if (sm.addMoney(-500)) {
                        sm.warp(251000000); // Herb Town : Herb Town
                    } else {
                        sm.sayNext("Are you sure you have enough mesos?");
                    }
                } else {
                    sm.sayOk("OK. if you ever change your mind, please let me know.");
                }
            }
        } else if (sm.getFieldId() == 251000000) {
            // Herb Town : Herb Town
            if (sm.askYesNo("Hello there. How's the traveling so far? I've been transporting other travelers like you to #bMu Lung#k in no time, and... are you interested? It's not as stable as the ship, so you'll have to hold on tight, but i can get there much faster than the ship. I'll take you there as long as you pay #b500 mesos#k.")) {
                if (sm.addMoney(-500)) {
                    sm.warp(250000100); // Mu Lung : Mu Lung Temple
                } else {
                    sm.sayNext("Are you sure you have enough mesos?");
                }
            } else {
                sm.sayOk("OK. if you ever change your mind, please let me know.");
            }
        }
    }

    @Script("crane_MR")
    public static void crane_MR(ScriptManager sm) {
    }

    @Script("crane_SS")
    public static void crane_SS(ScriptManager sm) {
    }


    // EREVE SCRIPTS ---------------------------------------------------------------------------------------------------

    @Script("contimoveOrbEre")
    public static void contimoveOrbEre(ScriptManager sm) {
        // Kiru : Station Guide (1100008)
        //   Orbis : Station (200000161)
        if (sm.askYesNo("This ship will head towards #eEreve#n, an island where you'll find crimson leaves soaking up the sun, the gentle breeze that glides past the stream, and the Empress of Maple Cygnus. If you're interested in joining the Cygnus Knights, then you should definitely pay a visit here. Are you interested in visiting Ereve?\r\n\r\n The Trip will cost you #e1000#n Mesos")) {
            if (sm.addMoney(-1000)) {
                // Empress' Road : To Ereve -> Empress' Road : Sky Ferry
                sm.warpInstance(200090020, "sp", 130000210, 120);
            } else {
                sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
            }
        } else {
            sm.sayNext("If you're not interested, then oh well...");
        }
    }

    @Script("contimoveEreOrb")
    public static void contimoveEreOrb(ScriptManager sm) {
        // Kiru : Station Guide (1100004)
        //   Empress' Road : Sky Ferry  (130000210)
        sm.sayNext("Hmm... The winds are favorable. Are you thinking of leaving #eEreve#n and going somewhere else? This ferry sails to Orbis on the Ossyria Continent.");
        if (sm.askYesNo("Have you taken care of everything you needed to in #eEreve#n? If you happen to be headed towards #b#eOrbis#n#k I can take you there. What do you say? Are you going to go to #eOrbis#n?\r\n\r\nYou'll have to pay a fee of #b1000#k Mesos.")) {
            if (sm.addMoney(-1000)) {
                // Empress' Road : To Orbis -> Orbis : Station
                sm.warpInstance(200090021, "sp", 200000161, 120);
            } else {
                sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
            }
        } else {
            sm.sayNext("If you're not interested, then oh well...");
        }
    }

    @Script("contimoveEliEre")
    public static void contimoveEliEre(ScriptManager sm) {
        // Kiriru : Station Guide (1100007)
        //   Port Road : Station to Ereve (104020120)
        if (sm.askYesNo("Eh... So... Um... Are you trying to leave Victoria to go to a different region? You can take this boat to #eEreve#n. There, you will see bright sunlight shining on the leaves and feel a gentle breeze on your skin. It's where Shinsoo and Empress Cygnus are. Would you like to go to Ereve?\r\n\r\nIt will take about #e2 minutes#n and it will cost you #e1000#n Mesos.")) {
            if (sm.addMoney(-1000)) {
                // Empress' Road : To Ereve -> Empress' Road : Sky Ferry
                sm.warpInstance(200090030, "sp", 130000210, 120);
            } else {
                sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
            }
        } else {
            sm.sayNext("If you're not interested, then oh well...");
        }
    }

    @Script("contimoveEreEli")
    public static void contimoveEreEli(ScriptManager sm) {
        // Kiriru : Station Guide (1100003)
        //   Empress' Road : Sky Ferry  (130000210)
        if (sm.askYesNo("Eh, Hello...again. Do you want to leave Ereve and go somewhere else? If so, you've come to the right place. I operate a ferry that goes from Ereve to Victoria Island, I can take you to #eVictoria Island#n if you want... You'll have to pay a fee of #e1000#n Mesos.")) {
            if (sm.addMoney(-1000)) {
                // Empress' Road : Victoria Bound -> Port Road : Station to Ereve
                sm.warpInstance(200090031, "sp", 104020120, 120);
            } else {
                sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
            }
        } else {
            sm.sayNext("If you're not interested, then oh well...");
        }
    }

    @Script("talkOrv")
    public static void talkOrv(ScriptManager sm) {
    }

    @Script("talkVic")
    public static void talkVic(ScriptManager sm) {
    }

    @Script("move_OrbEre")
    public static void move_OrbEre(ScriptManager sm) {
    }

    @Script("move_EreOrb")
    public static void move_EreOrb(ScriptManager sm) {
    }

    @Script("move_EliEre")
    public static void move_EliEre(ScriptManager sm) {
    }

    @Script("move_EreEli")
    public static void move_EreEli(ScriptManager sm) {
    }


    // RIEN SCRIPTS ----------------------------------------------------------------------------------------------------

    @Script("contimoveRieRit")
    public static void contimoveRieRit(ScriptManager sm) {
        // Puro : To Victoria Island (1200003)
        //   Snow Island : Penguin Port (140020300)
        if (!sm.askYesNo("Are you thinking about leaving Rien and heading back? If you board this ship, I can take you from #bLith Harbor#k to #bRien#k and back. Would you like to go to #bVinctoria Island#k?\r\n\r\nThe trip costs #b1000 Mesos#k")) {
            sm.sayNext("If you're not interested, then oh well...");
            return;
        }
        if (!sm.addMoney(-1000)) {
            sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
            return;
        }
        sm.warpInstance(200090070, "sp", 104000000, 120);
    }

    @Script("contimoveRitRie")
    public static void contimoveRitRie(ScriptManager sm) {
        // Puro : To Rien (1200004)
        //   Lith Harbor : Lith Harbor (104000000)
        if (!sm.askYesNo("Are you thinking about leaving Victoria Island and heading to our town? If you board this ship, I can take you from #bLith Harbor#k to #bRien#k and back. Would you like to go to #bRien#k?\r\n\r\nThe trip costs #b1000 Mesos#k")) {
            sm.sayNext("If you're not interested, then oh well...");
            return;
        }
        if (!sm.addMoney(-1000)) {
            sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
            return;
        }
        sm.warpInstance(200090060, "sp", 140020300, 120);
    }

    @Script("move_RieRit")
    public static void move_RieRit(ScriptManager sm) {
    }

    @Script("move_RitRie")
    public static void move_RitRie(ScriptManager sm) {
    }


    // EDELSTEIN SCRIPTS -----------------------------------------------------------------------------------------------

    @Script("contimoveEdeGo")
    public static void contimoveEdeGo(ScriptManager sm) {
        // Ace : Pilot (2150008)
        //   Edelstein : Edelstein Temporary Airport (310000010)
        final int answer = sm.askMenu("Would you like to leave Edelstein and travel to a different continent? I can take you to Victoria Island and the Orbis area of Ossyria. The cost is 800 Mesos. Where would you like to go?", Map.of(
                0, "Victoria Island",
                1, "Orbis"
        ));
        if (answer == 0) {
            if (sm.addMoney(-800)) {
                // On Voyage : Victoria Island Bound -> Port Road : Station to Edelstein
                sm.warpInstance(200090710, "sp", 104020130, 300);
            } else {
                sm.sayNext("Are you sure you have enough mesos?");
            }
        } else if (answer == 1) {
            if (sm.addMoney(-800)) {
                // On Voyage : Orbis Bound -> Orbis : Station <Edelstein Bound>
                sm.warpInstance(200090610, "sp", 200000170, 180);
            } else {
                sm.sayNext("Are you sure you have enough mesos?");
            }
        }
    }

    @Script("contimoveEliEde")
    public static void contimoveEliEde(ScriptManager sm) {
        // Ace : Pilot (2150010)
        //   Port Road : Station to Edelstein (104020130)
        if (sm.askYesNo("Do you want to go to Edelstein? The fee is 800 Mesos. Hop on if you want to go.")) {
            if (sm.addMoney(-800)) {
                // On Voyage : Edelstein Bound -> Edelstein : Edelstein Temporary Airport
                sm.warpInstance(200090700, "sp", 310000010, 300);
            } else {
                sm.sayNext("Are you sure you have enough mesos?");
            }
        }
    }

    @Script("contimoveOrbEde")
    public static void contimoveOrbEde(ScriptManager sm) {
        // Ace : Pilot (2150009)
        //   Orbis : Station <Edelstein Bound> (200000170)
        if (sm.askYesNo("Do you want to go to Edelstein? The fee is 800 Mesos. Hop on if you want to go.")) {
            if (sm.addMoney(-800)) {
                // On Voyage : Edelstein Bound -> Edelstein : Edelstein Temporary Airport
                sm.warpInstance(200090600, "sp", 310000010, 180);
            } else {
                sm.sayNext("Are you sure you have enough mesos?");
            }
        }
    }

    @Script("move_EdeEli")
    public static void move_EdeEli(ScriptManager sm) {
    }

    @Script("move_EdeOrb")
    public static void move_EdeOrb(ScriptManager sm) {
    }

    @Script("move_EliEde")
    public static void move_EliEde(ScriptManager sm) {
    }

    @Script("move_OrbEde")
    public static void move_OrbEde(ScriptManager sm) {
    }


    // OTHER SCRIPTS ---------------------------------------------------------------------------------------------------

    @Script("elevator")
    public static void elevator(ScriptManager sm) {
        // Ludibrium : Helios Tower <2nd Floor> (222020100)
        //   in00 (-139, 286)
        // Ludibrium : Helios Tower <99th Floor> (222020200)
        //   in00 (-133, 1963)
        if (sm.getFieldId() == 222020100) {
            //Ludibrium : Helios Tower <2nd Floor>
            if (sm.getEventState(EventType.CM_ELEVATOR) == EventState.ELEVATOR_2ND_FLOOR) {
                sm.playPortalSE();
                sm.warp(222020110, "out00"); // Ludibrium : Elevator <To Ludibrium>
            } else {
                sm.message("At the moment, the elevator is not available for this route. Please try again later.");
            }
        } else if (sm.getFieldId() == 222020200) {
            // Ludibrium : Helios Tower <99th Floor>
            if (sm.getEventState(EventType.CM_ELEVATOR) == EventState.ELEVATOR_99TH_FLOOR) {
                sm.playPortalSE();
                sm.warp(222020210, "out00"); // Ludibrium : Elevator <To Korean Folk Town>
            }
        }
    }

    @Script("nihal_taxi")
    public static void nihal_taxi(ScriptManager sm) {
        // Camel Cab (2110005)
        //   The Burning Sands : Outside North Entrance of Ariant (260020000)
        //   Sunset Road : Sahel 1 (260020700)
        if (sm.getFieldId() == 260020000) {
            // The Burning Sands : Outside North Entrance of Ariant
            if (sm.askYesNo("Would you like to take the #b#p2110005##k to #b#m261000000##k, the town of Alchemy? The fare is #b1500 mesos#k.")) {
                if (sm.addMoney(-1500)) {
                    sm.warp(261000000); // Sunset Road : Magatia
                } else {
                    sm.sayNext("I am sorry, but I think you are short on mesos. I am afraid I can't let you ride this if you do not have enough money to do so. Please come back when you have enough money to use this.");
                }
            } else {
                sm.sayNext("Hmmm... too busy to do it right now? If you feel like doing it, though, come back and find me.");
            }
        } else if (sm.getFieldId() == 260020700) {
            // Sunset Road : Sahel 1
            if (sm.askYesNo("Would you like to take the #b#p2110005##k to #b#m260000000##k, the town of Burning Roads? The fare is #b1500 mesos#k.")) {
                if (sm.addMoney(-1500)) {
                    sm.warp(260000000); // The Burning Road : Ariant
                } else {
                    sm.sayNext("I am sorry, but I think you are short on mesos. I am afraid I can't let you ride this if you do not have enough money to do so. Please come back when you have enough money to use this.");
                }
            } else {
                sm.sayNext("Hmmm... too busy to do it right now? If you feel like doing it, though, come back and find me.");
            }
        }
    }

    @Script("karakasa")
    public static void karakasa(ScriptManager sm) {
        // Karcasa (2101013)
        //   The Burning Sands : Tent of the Entertainers (260010600)
        final List<Integer> towns = List.of(
                100000000, // Henesys : Henesys
                101000000, // Ellinia : Ellinia
                102000000, // Perion : Perion
                103000000 // Kerning City : Kerning City
        );
        if (!sm.askAccept("I don't know how you found out about this, but you came to the right place! For those who wandered around Nihal Desert and are getting homesick, I am offering a flight straight to Victoria Island, non-stop. Don't worry about the flying ship - it's only fallen once or twice! Don't you feel claustrophobic being in a long flight on that small ship? What do you think? Are you willing to take the offer on this direct flight?")) {
            sm.sayNext("Aye...are you scared of speed or heights? You can't trust my flying skills? Trust me, I've worked out all the kinks!");
            return;
        }
        if (!sm.askAccept("Please remember two things. One, this line is actually for overseas shipping, so #rI cannot guarantee which town you'll land#k. Two, since I am putting you in this special flight, it'll be a bit expensive. The service charge is #b#e10,000 mesos#n#k. There's a flight that's about to take off. Are you interested?")) {
            sm.sayNext("Aye...are you scared of speed or heights? You can't trust my flying skills? Trust me, I've worked out all the kinks!");
            return;
        }
        sm.sayNext("Okay, ready for takeoff!");
        if (sm.addMoney(-10000)) {
            sm.warp(Util.getRandomFromCollection(towns).orElseThrow());
        } else {
            sm.sayNext("Hey, are you short on cash? I told you you'll need #b10,000 mesos#k to get on this.");
        }
    }

    @Script("enter_earth00")
    public static void enter_earth00(ScriptManager sm) {
        // Nautilus : Navigation Room (120000101)
        //   earth01 (570, -120)
        if (sm.removeItem(4031890, 1)) {
            sm.playPortalSE();
            sm.warp(221000300, "earth00"); // Omega Sector : Command Center
        } else {
            sm.message("You need a warp card to activate this portal.");
        }
    }

    @Script("enter_earth01")
    public static void enter_earth01(ScriptManager sm) {
        // Omega Sector : Command Center (221000300)
        //   earth00 (218, 0)
        if (sm.removeItem(4031890, 1)) {
            sm.playPortalSE();
            sm.warp(120000101, "earth01"); // Nautilus : Navigation Room
        } else {
            sm.message("You need a warp card to activate this portal.");
        }
    }

    @Script("rankRoom")
    public static void rankRoom(ScriptManager sm) {
        // Henesys : Bowman Instructional School (100000201)
        //   rank00 (-323, 181)
        // Ellinia : Magic Library (101000003)
        //   rank00 (-2, 183)
        // Perion : Warriors' Sanctuary (102000003)
        //   rank00 (55, -29)
        // Kerning City : Thieves' Hideout (103000003)
        //   rank00 (291, 181)
        // Nautilus : Navigation Room (120000101)
        //   rank00 (-296, 149)
        // Empress' Road : Ereve (130000000)
        //   west00 (-1644, 86)
        // Empress' Road : Crossroads of Ereve (130000200)
        //   east00 (3303, 87)
        // Snow Island : Dangerous Forest (140010100)
        //   in01 (-2763, -375)
        switch (sm.getFieldId()) {
            case 100000201 -> {
                // Henesys : Bowman Instructional School
                sm.playPortalSE();
                sm.warp(100000204, "out00"); // Henesys : Hall of Bowmen
            }
            case 101000003 -> {
                // Ellinia : Magic Library
                sm.playPortalSE();
                sm.warp(101000004, "out00"); // Ellinia : Hall of Magicians
            }
            case 102000003 -> {
                // Perion : Warriors' Sanctuary
                sm.playPortalSE();
                sm.warp(102000004, "out00"); // Perion : Hall of Warriors
            }
            case 103000003 -> {
                // Kerning City : Thieves' Hideout
                sm.playPortalSE();
                sm.warp(103000008, "out00"); // Kerning City : Hall of Thieves
            }
            case 120000101 -> {
                // Nautilus : Navigation Room
                sm.playPortalSE();
                sm.warp(120000105, "out00"); // Nautilus : Training Room
            }
            case 130000000 -> {
                // Empress' Road : Ereve
                sm.playPortalSE();
                sm.warp(130000100, "east00"); // Empress' Road : Knights Chamber
            }
            case 130000200 -> {
                // Empress' Road : Crossroads of Ereve
                sm.playPortalSE();
                sm.warp(130000100, "west00"); // Empress' Road : Knights Chamber
            }
            case 140010100 -> {
                // Snow Island : Dangerous Forest
                sm.playPortalSE();
                sm.warp(140010110, "out00"); // Snow Island : Palace of the Master
            }
        }
    }

    @Script("ossyria_taxi")
    public static void ossyria_taxi(ScriptManager sm) {
        // Ossyria Taxi (2012001)
        //   Orbis : Orbis (200000000)
        //   El Nath : El Nath (211000000)
        //   Ludibrium : Ludibrium (220000000)
        //   Leafre : Leafre (240000000)
        //   Mu Lung : Mu Lung (250000000)
        //   Herb Town : Herb Town (251000000)
        //   Ariant : Ariant (260000000)
        //   Magatia : Magatia (261000000)
        final boolean isBeginner = JobConstants.isBeginnerJob(sm.getJob());
        final int price = isBeginner ? 100 : 1000;
        final List<Integer> towns = Stream.of(
                200000000, // Orbis : Orbis
                211000000, // El Nath : El Nath
                220000000, // Ludibrium : Ludibrium
                240000000, // Leafre : Leafre
                250000000, // Mu Lung : Mu Lung
                251000000, // Herb Town : Herb Town
                260000000, // Ariant : Ariant
                261000000  // Magatia : Magatia
        ).filter(mapId -> sm.getFieldId() != mapId).toList();
        final Map<Integer, String> options = createOptions(towns, (mapId) -> String.format("#m%d# (%d Mesos)", mapId, price));
        sm.sayNext("Hello, I'm the Ossyria continent taxi driver. I can take you to any major town in Ossyria quickly and safely. Where would you like to go?" + (isBeginner ? "\r\nWe have a special 90% discount for beginners." : ""));
        final int answer = sm.askMenu("Please select your destination.", options);
        if (sm.askYesNo(String.format("You want to go to #b#m%d##k? It'll cost you #b%d#k mesos.", towns.get(answer), price))) {
            if (sm.addMoney(-price)) {
                sm.warp(towns.get(answer));
            } else {
                sm.sayOk("You don't have enough mesos. Sorry, but you can't ride without paying the fare.");
            }
        } else {
            sm.sayOk("There's plenty to explore here too. Come back when you need a ride!");
        }
    }
}
