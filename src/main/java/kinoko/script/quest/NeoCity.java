package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.List;
import java.util.Map;

public final class NeoCity extends ScriptHandler {
    @Script("TD_NC_title")
    public static void TD_NC_title(ScriptManager sm) {
        // Tera Forest   : Tera Forest Time Gate (240070000)
        // Neo City : <Year 2021> Average Town Entrance (240070100)
        // Neo City : <Year 2099> Midnight Harbor Entrance (240070200)
        // Neo City : <Year 2215> Bombed City Center Retail District (240070300)
        // Neo City : <Year 2216> Ruined City Intersection (240070400)
        // Neo City : <Year 2230> Dangerous Tower Lobby (240070500)
        // Neo City : <Year 2503> Air Battleship Bow (240070600)
        if (sm.getFieldId() == 240070000) {
            // Tera Forest : Tera Forest Time Gate
            sm.screenEffect("temaD/enter/teraForest");
        } else if (sm.getFieldId() == 240070100) {
            // Neo City : <Year 2012> Average Town Entrance
            sm.screenEffect("temaD/enter/neoCity1");
        } else if (sm.getFieldId() == 240070200) {
            // Neo City : <Year 2099> Midnight Harbor Entrance
            sm.screenEffect("temaD/enter/neoCity2");
        } else if (sm.getFieldId() == 240070300) {
            // Neo City : <Year 2215> Bombed City Center Retail District
            sm.screenEffect("temaD/enter/neoCity3");
        } else if (sm.getFieldId() == 240070400) {
            // Neo City : <Year 2216> Ruined City Intersection
            sm.screenEffect("temaD/enter/neoCity4");
        } else if (sm.getFieldId() == 240070500) {
            // Neo City : <Year 2230> Dangerous Tower Lobby
            sm.screenEffect("temaD/enter/neoCity5");
        } else if (sm.getFieldId() == 240070600) {
            // Neo City : <Year 2503> Air Battleship Bow
            sm.screenEffect("temaD/enter/neoCity6");
        }
    }

    @Script("TD_neoCity_enter")
    public static void TD_neoCity_enter(ScriptManager sm) {
        // Time Gate (2083006)
        //   Tera Forest   : Tera Forest Time Gate (240070000)
        final List<Integer> destinations = List.of(
                240070100, // Neo City : <Year 2012> Average Town Entrance
                240070200, // Neo City : <Year 2099> Midnight Harbor Entrance
                240070300, // Neo City : <Year 2215> Bombed City Center Retail District
                240070400, // Neo City : <Year 2216> Ruined City Intersection
                240070500, // Neo City : <Year 2230> Dangerous Tower Lobby
                240070600 // Neo City : <Year 2503> Air Battleship Bow
        );
        final Map<Integer, String> options = createOptions(destinations, ScriptHandler::mapName);
        final int answer = sm.askSlideMenu(1, options);
        if (answer >= 0 && answer < destinations.size()) {
            sm.warp(destinations.get(answer), "left00");
        }
    }

    @Script("TD_chat_enter")
    public static void TD_chat_enter(ScriptManager sm) {
        // Tera Forest   : Tera Forest Time Gate (240070000)
        //   TD_neo (491, 151)
        TD_neoCity_enter(sm);
    }

    @Script("TD_neo_inTree")
    public static void TD_neo_inTree(ScriptManager sm) {
        // Neo City : Tera Forest Time Gate (240070000)
        // Portal to enter Tera Forest time dungeons based on active quest

        // Map ID array for different Tera Forest areas
        final int[] MAP_IDS = {240070010, 240070020, 240070030, 240070040, 240070050, 240070060};
        final int[] QUEST_IDS = {3719, 3724, 3730, 3736, 3742, 3748};

        // Check which quest the player has active and warp to corresponding map
        for (int i = 0; i < QUEST_IDS.length; i++) {
            if (sm.hasQuestStarted(QUEST_IDS[i])) {
                sm.playPortalSE();
                sm.warp(MAP_IDS[i], "sp");
                return;
            }
        }

        sm.message("You need an active Tera Forest quest to enter this area.");
    }

    @Script("TD_Boss_enter")
    public static void TD_Boss_enter(ScriptManager sm) {
        // Neo City : Boss Room Entry
        // Portals from warehouse areas to boss rooms (240070X02 → 240070X03)

        final int[] WAREHOUSE_MAPS = {240070202, 240070302, 240070402, 240070502, 240070602};
        final int currentMapId = sm.getFieldId();

        // Check if player is in one of the warehouse maps
        for (int warehouseMap : WAREHOUSE_MAPS) {
            if (currentMapId == warehouseMap) {
                final int bossMap = warehouseMap + 1; // Boss room is +1 from warehouse
                sm.playPortalSE();
                sm.warp(bossMap, "sp");
                return;
            }
        }

        sm.message("This portal can only be used from Neo City warehouse areas.");
    }

    @Script("TD_neo_Andy")
    public static void TD_neo_Andy(ScriptManager sm) {
        // Andy (NPC 2082004) - Time Traveler at Tera Forest Time Gate (240070000)
        // Main NPC for Neo City time travel quests

        // Simple dialog for players without quests
        sm.sayOk("The answer lies within the passage of time...");
    }

    @Script("npc2082014")
    public static void npc2082014(ScriptManager sm) {
        // Ayasia (NPC 2082014) - Sky Battle Ship Bow (Year 2503 - 240070600)
        // NPC in the final time period
        sm.sayOk("Nothing can be foreseen. This is fate.");
    }

    // ========================================
    // NEO CITY PREREQUISITE QUESTS (3715-3718)
    // ========================================

    @Script("q3715s")
    public static void q3715s(ScriptManager sm) {
        // Quest 3715 - The Suspicious Wanderer START
        // Han the Broker (NPC 2111007) - Magatia
        sm.sayNext("Long time no see. I know what brings you here. People come to me for one thing: #einformation#n. Unfortunately, my memory's gotten a bit fuzzy, so I got nothing. Though #b5000 mesos#k might clear it up.");

        if (!sm.askYesNo("Will you pay 5000 mesos for the information?")) {
            sm.sayNext("Hey, business is business. Scram if you aren't interested. I'm a busy guy, you know.");
            return;
        }

        if (!sm.canAddMoney(-5000)) {
            sm.sayOk("You don't have enough mesos!");
            return;
        }

        sm.addMoney(-5000);
        sm.forceStartQuest(3715);

        sm.sayNext("I knew you'd understand! Now then, let me tell you about this wanderer I met a little while ago.");
        sm.sayBoth("One night, not too long ago, the stars stopped twinkling. The next day, a suspicious visitor arrived in Magatia. He wore a cape that covered his features, and I could tell he was from out of town. I started a conversation with him, hoping to get information.");
        sm.sayBoth("He was just plain strange. He kept mumbling to himself, some weird phrase over and over. \"#bI've come too far. It's too soon...\"#k\nBefore I could question him, he gave Humanoid A the stink eye and disappeared in the alley near the Alcadno Society.");
        sm.sayBoth("Late that night, there was a explosion near the Alcadno Society. There were no witnesses, but after that, no one saw the strange man again. The Alcadno Society blames the Zenumists, of course, but they're wrong. I'm pretty sure that man was responsible. He's got a huge secret. I can just feel it.");
        sm.sayOk("I can see in your eyes that you want to pursue that man. Let me offer you some advice. I think he's already left Nihal Desert, so he must have passed through Ariant Station. Go ask #bSyras#k the ticket agent at Ariant Station if he saw anything.");
    }

    @Script("q3715e")
    public static void q3715e(ScriptManager sm) {
        // Quest 3715 - The Suspicious Wanderer END
        // Syras (NPC 2102002) - Ariant Station
        final int answer1 = sm.askMenu("Here to buy a ticket? I only sell tickets to Orbis. How many would you like? You only need one to board the ship, but you never know...",
            Map.of(0, "I came here to ask you something. Did you see an out-of-towner not too long ago?"));
        if (answer1 != 0) {
            sm.sayOk("Come back if you need a ticket to Orbis!");
            return;
        }

        final int answer2 = sm.askMenu("Lookie here, mister. I meet hundreds of out-of-towners every day!",
            Map.of(0, "I'm looking for someone who's quiet. He was wearing a cape."));
        if (answer2 != 0) {
            sm.sayOk("Talk to me if you remember more details!");
            return;
        }

        sm.forceCompleteQuest(3715);
        sm.addExp(30000);
        sm.sayOk("Hm, I recall two people who fit that description. One passed by around 10AM, the other around 5PM. The first one was a rather extravagant magician, the second was a young warrior in a raggedy cap. The young warrior asked me how to get to Victoria Island. That's about it... Oh, wait! I remember one other person. I saw him at sunrise a few days ago.");
    }

    @Script("q3716s")
    public static void q3716s(ScriptManager sm) {
        // Quest 3716 - The Wanderer's Whereabouts 1 START
        // Syras (NPC 2102002) - Ariant Station
        sm.sayNext("He made a scene because he wanted to get going before schedule. I didn't know what to do. I tried to calm him down, but I couldn't understand his gibberish. Anyway, he got on the earliest airship and left for Orbis.");

        if (!sm.askYesNo("Now that I think about it, he was rather strange and awkward. If you're looking for that man, I recommend heading over to Orbis. Oh! Talk to #bIsa#k the station guide at Orbis Station. She'd remember the man if she saw him.")) {
            sm.sayNext("What? That's not the right guy? Then ask someone else, huh? I'm a busy guy.");
            return;
        }

        sm.forceStartQuest(3716);
        sm.sayOk("The ship is about to take off. If you want to go to Orbis, I suggest you hurry.");
    }

    @Script("q3716e")
    public static void q3716e(ScriptManager sm) {
        // Quest 3716 - The Wanderer's Whereabouts 1 END
        // Isa (NPC 2012006) - Orbis Station
        final int answer1 = sm.askMenu("Hello, welcome to Orbis Station. My name is Isa. How can I help you?",
            Map.of(0, "I'm looking for someone. Have you seen a suspicious fellow wearing a cape wrapped around his entire body?"));
        if (answer1 != 0) {
            sm.sayOk("Let me know if you need any help!");
            return;
        }

        final int answer2 = sm.askMenu("A suspicious fellow? Hmm, I do remember a couple of people wearing big capes, but they weren't exactly suspicious. There was a magician who seemed to be into flashy capes and a warrior who was headed to Victoria Island. And...",
            Map.of(0, "Was there someone who seemed a bit absent-minded? Who arrived from Ariant early in the morning."));
        if (answer2 != 0) {
            sm.sayOk("Come back if you remember more details!");
            return;
        }

        sm.forceCompleteQuest(3716);
        sm.addExp(30000);
        sm.sayOk("Oh, yes! That guy? He left for Leafre as soon as he got here. As you know, Orbis Station is always so hectic. He didn't know where to go, so I helped him find his way, although he seemed a bit uncomfortable around me for some reason.");
    }

    @Script("q3717s")
    public static void q3717s(ScriptManager sm) {
        // Quest 3717 - The Wanderer's Whereabouts 2 START
        // Isa (NPC 2012006) - Orbis Station
        final int answer = sm.askMenu("Come to think of it, he did mumble something under his breath... I think he said something about having to go to Tera Forest.",
            Map.of(0, "Tera Forest? Are you sure he didn't say Minar Forest?"));
        if (answer != 0) {
            sm.sayNext("Since you're here in Orbis, why don't you take a tour before you leave? You can catch an airship every hour to any destination.");
            return;
        }

        sm.forceStartQuest(3717);
        sm.sayOk("Corba in Leafre once told me about Tera Forest. Apparently, it's a small forest located near the eastern border of Minar Forest. He said not many people know about it because it's secluded. If you want to go to Leafre, you should leave now. The airship to Leafre will take off soon.");
    }

    @Script("q3717e")
    public static void q3717e(ScriptManager sm) {
        // Quest 3717 - The Wanderer's Whereabouts 2 END
        // Andy (NPC 2082004) - Tera Forest
        final int answer = sm.askMenu("You found me! But that means nothing! You'll never be able to drag me out of here!!",
            Map.of(0, "I only wanted to meet you. I'm not here to drag you anywhere..."));
        if (answer != 0) {
            sm.sayOk("Leave me alone!");
            return;
        }

        sm.forceCompleteQuest(3717);
        sm.addExp(30000);
        sm.sayOk("I don't know who you are. I don't trust you...or anyone! I won't tell you a thing. It could create time distortion. I don't know! I can't figure it out. I don't know what went wrong where.");
    }

    @Script("q3718s")
    public static void q3718s(ScriptManager sm) {
        // Quest 3718 - Andy the Time Traveler START
        // Andy (NPC 2082004) - Tera Forest
        int answer = sm.askMenu("Why have you followed me here? I don't know who you are.",
            Map.of(0, "Why did you attack the Alcadno Society?"));
        if (answer != 0) {
            sm.sayOk("Leave me be!");
            return;
        }

        answer = sm.askMenu("Alcadno? Oh, you're talking about those fools that rely on machines. They don't think about how their actions will affect the future. Those fools will spend a lifetime regretting what they've done. But I've come too far. It's too early. This isn't where I was trying to visit.",
            Map.of(0, "What in the world are you talking about? Where did you come from? Tell me what's going on!"));
        if (answer != 0) {
            sm.sayOk("I can't tell you anything!");
            return;
        }

        answer = sm.askMenu("Can you handle it? You could get lost in time like me. It's a dangerous risk. And I may not be able to complete my mission.",
            Map.of(0, "I'll help you. What is this mission you're talking about?"));
        if (answer != 0) {
            sm.sayOk("It's too dangerous for you!");
            return;
        }

        if (!sm.askAccept("You... You seem trustworthy. I can feel it. You can't tell a soul what I'm about to tell you. Can you promise to keep this a secret? Can you handle that?")) {
            sm.sayNext("Are you telling me not to trust you?");
            return;
        }

        sm.forceStartQuest(3718);

        sm.sayNext("I'm from the year 2503. I came from Neo City to change the errors of the past. But this isn't Neo City. I think I may have transcended time and space. Everything is a mess. And I seem to be lost.");
        sm.sayBoth("That door you see over there is called the #bTime Gate#k. It's a the key to traveling through time. I'm from Neo City in the year 2503. Machines have taken over our city and are attacking humans. We could not find a solution to the chaos, so I came to the past to set things right.");
        sm.sayBoth("But I've arrived somewhere, or somewhen, totally unexpected. I was running around like a headless chicken, thinking this was the Neo City of the past, but I realized I was in the wrong place and time. I must go to the right time! But I lost the Pocket Watch that acts as the key to the Time Gate.");
        sm.sayOk("Help me find the key to the Time Gate! I will compensate you. I must undo the mistakes in the past, but I can't find the Pocket Watch anywhere. It must be somewhere in the forest, though. Right?");
    }

    @Script("q3718e")
    public static void q3718e(ScriptManager sm) {
        // Quest 3718 - Andy the Time Traveler END
        // Andy (NPC 2082004) - Requires Time Traveler's Pocket Watch (4032511)
        final int POCKET_WATCH = 4032511;
        final int TIME_TRAVELERS_POCKET_WATCH = 4001393;

        if (!sm.hasItem(POCKET_WATCH, 1)) {
            sm.sayOk("The Pocket Watch has my name engraved on the back. I'm pretty sure I lost it somewhere in Minar Forest.");
            return;
        }

        sm.sayNext("Yes, this is the Pocket Watch! Let me see it!");
        sm.sayBoth("Oh no... It's not working. I can no longer carry out my mission. The Pocket Watch only works when its possessor is in an appropriate time. It also checks its possessor's mental and physical state. It probably detects my exhaustion.");

        if (!sm.askYesNo("Wait a minute. I have an idea. Put the Pocket Watch on your palm!\r\n#b(You place the Pocket Watch on one hand and cover it with the other. The Pocket Watch begins to glow, and then the glow disappears.)#k")) {
            return;
        }

        sm.removeItem(POCKET_WATCH, 1);
        sm.addItem(TIME_TRAVELERS_POCKET_WATCH, 1);
        sm.forceCompleteQuest(3718);
        sm.addExp(40000);
        sm.sayOk("You! You might be the one! Please, take over my mission and change the past! I hand over the Pocket Watch to you. Please, you must do this!");
    }

    // ========================================
    // NEO CITY TIME TRAVEL QUESTS (3719-3748)
    // ========================================

    @Script("q3719s")
    public static void q3719s(ScriptManager sm) {
        // Quest 3719 - Nex the Time Guard (Year 2021) START
        // Andy (NPC 2082004) - Requires quests 3715-3718 completed
        sm.sayNext("Before you can travel though time, you must first pass Nex's test. Nex is the Gatekeeper of Time, and he lives inside the Old Tree of Tera in Tera Forest. You must defeat him before you can time travel.");

        if (!sm.askAccept("Will you challenge Nex the Gatekeeper of Time?")) {
            sm.sayNext("You will not be granted authorization to travel through time unless you defeat Nex the Gatekeeper of Time.");
            return;
        }

        sm.forceStartQuest(3719);
        sm.sayOk("When you defeat Nex, the Time Gate that lets you teleport to year 2021 will open.");
    }

    @Script("q3719e")
    public static void q3719e(ScriptManager sm) {
        // Quest 3719 - Nex the Time Guard (Year 2021) END
        // Requires defeating Nex (7120100)
        sm.sayNext("Ah, you've passed Nex's first test. This means you have earned the right to travel to year 2021 using the Time Gate. You're doing great! What a relief!");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3719);
        sm.addExp(80000);
        sm.sayOk("Take the Time Traveler's Pocket Watch and go through the Time Gate.");
    }

    // ========================================
    // YEAR 2021 - JUST ANOTHER DAY (3720-3723)
    // ========================================

    @Script("q3720s")
    public static void q3720s(ScriptManager sm) {
        // Quest 3720 - The First Clue START
        // Andy (NPC 2082004) - Year 2021
        sm.sayNext("When you arrive in the year 2021, you need to retrieve a certain item. You see, the cause of all the problems in my time must've started in 2021, when the technology for A.I. machinery was invented.");
        sm.sayBoth("From what I've heard, it all started with some scribbles. They were just meaningless doodles, at first, but A.I. robots were built based on that drawing. The scribbled note should be in a trash can somewhere. You must find it! Please, rifle through trash cans and bring me back that piece of paper!");

        if (!sm.askAccept("Will you help me find this clue?")) {
            sm.sayNext("This is important. Before things get worse, we must destroy that paper.");
            return;
        }

        sm.forceStartQuest(3720);
        sm.sayOk("I don't exactly where the paper is... Search in every trash can you come across!");
    }

    @Script("q3720e")
    public static void q3720e(ScriptManager sm) {
        // Quest 3720 - The First Clue END
        // Andy (NPC 2082004) - Requires item 4032512
        final int CLUE_PAPER = 4032512;

        if (!sm.hasItem(CLUE_PAPER, 1)) {
            sm.sayOk("Keep searching in trash cans!");
            return;
        }

        sm.sayNext("You found it! Let me see!");
        sm.sayBoth("No, no, no. This isn't it. This isn't what I'm looking for. I could've sworn... I mean, I'm sure it was 2021 that A.I.'s were first designed and invented. Could it be that I'm mistaken?");

        sm.removeItem(CLUE_PAPER, 1);
        sm.forceCompleteQuest(3720);
        sm.addExp(61000);
    }

    @Script("q3721s")
    public static void q3721s(ScriptManager sm) {
        // Quest 3721 - Dangerous Slimes START
        // Brainy Boy (NPC 2082005) - Year 2021
        final int answer = sm.askMenu("Don't come any closer! Who are you? Where is everyone? And where did all these monsters come from?",
            Map.of(0, "Don't worry. I'll help you."));
        if (answer != 0) {
            sm.sayOk("Please be careful!");
            return;
        }

        sm.sayNext("Then defeat all of these monsters. I think they're exponentially increasing.");

        if (!sm.askAccept("Will you eliminate the monsters?")) {
            sm.sayNext("You said you were going to help me!! You lied!");
            return;
        }

        sm.forceStartQuest(3721);
        sm.sayOk("It's not just here. The entire town is crowded with monsters. Please eliminate 50 of every type of monster and collect 30 of each of those monsters' dropped items to stop them from respawning.");
    }

    @Script("q3721e")
    public static void q3721e(ScriptManager sm) {
        // Quest 3721 - Dangerous Slimes END
        // Brainy Boy (NPC 2082005) - Requires mob kills and items
        final int[] SLIME_ITEMS = {4000545, 4000546, 4000547};

        for (int item : SLIME_ITEMS) {
            if (!sm.hasItem(item, 30)) {
                sm.sayOk("Please collect 30 dropped items of each monster.");
                return;
            }
        }

        sm.sayNext("Have you eliminated all the monsters? Did you collect their dropped items too?");
        sm.sayBoth("Even now, these monsters are not going away. What's happening? Where are my mom and dad?");

        for (int item : SLIME_ITEMS) {
            sm.removeItem(item, 30);
        }
        sm.forceCompleteQuest(3721);
        sm.addExp(100000);
    }

    @Script("q3722s")
    public static void q3722s(ScriptManager sm) {
        // Quest 3722 - The Crying Girl's Sketchbook START
        // Crying Girl (NPC 2082006) - Year 2021
        sm.sayNext("*sniff sniff* These monsters ate my Sketchbook. Could you find my Sketchbook for me? *sniff sniff*");

        if (!sm.askAccept("Will you help find the sketchbook pages?")) {
            sm.sayNext("*sob sob* My sketchbook!!! *sob sob*");
            return;
        }

        sm.forceStartQuest(3722);
        sm.sayOk("Please help me find 20 Loose-Leaf Pages of my Sketchbook. *sniff sniff*");
    }

    @Script("q3722e")
    public static void q3722e(ScriptManager sm) {
        // Quest 3722 - The Crying Girl's Sketchbook END
        // Crying Girl (NPC 2082006) - Requires 20x 4032513
        final int SKETCHBOOK_PAGE = 4032513;

        if (!sm.hasItem(SKETCHBOOK_PAGE, 20)) {
            sm.sayOk("Please, please help me find 20 Loose-Leaf Pages of my Sketchbook! Those wicked Slimes ate them!");
            return;
        }

        sm.sayNext("Oh, my drawings... Did you bring them?");
        sm.sayBoth("Hmph, it's all wrinkled. My drawings... *sniff sniff*");

        sm.removeItem(SKETCHBOOK_PAGE, 20);
        sm.forceCompleteQuest(3722);
        sm.addExp(65000);
    }

    @Script("q3723s")
    public static void q3723s(ScriptManager sm) {
        // Quest 3723 - The Boy and the Girl START
        // Brainy Boy (NPC 2082005) - Year 2021
        sm.sayNext("I think she's disappointed because she didn't find all of the pages from her Sketchbook. She was so proud of her new robot drawing, too. Could you just keep an eye out for pieces of paper you might see lying around?");

        if (!sm.askAccept("Will you keep an eye out for the drawings?")) {
            sm.sayNext("I wasn't asking you to go out of your way to find them. I just thought you could keep an eye out for them in case you saw any. If you're too lazy to do that... Then...fine.");
            return;
        }

        sm.forceStartQuest(3723);
        sm.sayOk("It really was an incredible drawing. I hope she isn't too bummed out about it, though I know she is.");
    }

    @Script("q3723e")
    public static void q3723e(ScriptManager sm) {
        // Quest 3723 - The Boy and the Girl END
        // Andy (NPC 2082004) - Report back
        final int answer1 = sm.askMenu("What is it?",
            Map.of(0, "I think you should know something."));
        if (answer1 != 0) {
            sm.sayOk("Come back if you have information for me.");
            return;
        }

        final int answer2 = sm.askMenu("What? Did you learn something?",
            Map.of(0, "I'm not sure, but I met a boy while I was investigating a little town during year 2016, and he mentioned something about a robot."));
        if (answer2 != 0) {
            sm.sayOk("Let me know if you find out more.");
            return;
        }

        final int answer3 = sm.askMenu("Is that so? Hmm.",
            Map.of(0, "Yes, but the kids lost the drawing. I know it's just a child's scribbles, but I couldn't just ignore it."));
        if (answer3 != 0) {
            sm.sayOk("Keep investigating.");
            return;
        }

        sm.sayNext("I see. It really isn't anything definite. But I can't simply ignore it. either. Thank you. Nothing is for sure, but we may have found a clue.");

        sm.forceCompleteQuest(3723);
        sm.addExp(30000);
        sm.sayOk("Thanks. I think it's time you got ready for your next time travel. Let me know when you're prepared.");
    }

    @Script("q3724s")
    public static void q3724s(ScriptManager sm) {
        // Quest 3724 - Nex the Time Guard (Year 2099) START
        // Requires quests 3719-3723 completed
        sm.sayNext("You must pass Nex's test but you can travel to a different time. Don't get too comfortable. Nex will be much stronger this time around.");

        if (!sm.askAccept("Will you challenge Nex again?")) {
            sm.sayNext("You won't be authorized to travel time unless you defeat Nex the Gatekeeper of Time.");
            return;
        }

        sm.forceStartQuest(3724);
        sm.sayOk("When you defeat Nex, the Time Gate to the year 2099 will open.");
    }

    @Script("q3724e")
    public static void q3724e(ScriptManager sm) {
        // Quest 3724 - Nex the Time Guard (Year 2099) END
        // Requires defeating Nex (7120101)
        sm.sayNext("Ah, you've passed Nex's second test. Now then, you should be able to access the Time Gate to the year 2099.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3724);
        sm.addExp(90000);
        sm.sayOk("Take the Time Traveler's Pocket Watch and go through the Time Gate.");
    }

    // ========================================
    // YEAR 2099 - THE HARBOR (3725-3729)
    // ========================================

    @Script("q3725s")
    public static void q3725s(ScriptManager sm) {
        // Quest 3725 - The Hidden Truth about the Past START
        // Andy (NPC 2082004) - Year 2099
        sm.sayNext("According to the records I found, a giant robot appeared in the year 2099. At the time, this was regarded as a silly rumor, but I have a hunch it's not something we should overlook. Please travel to year 2099 and investigate.");

        if (!sm.askAccept("Will you investigate the giant robot?")) {
            sm.sayNext("This is serious business. If you've lost interest, now would be a good time for you to quit and go about your business.");
            return;
        }

        sm.forceStartQuest(3725);
        sm.sayOk("If you get lucky and find the robot, destroy it. Please. For the sake of the future.");
    }

    @Script("q3725e")
    public static void q3725e(ScriptManager sm) {
        // Quest 3725 - The Hidden Truth about the Past END
        // Andy (NPC 2082004) - Kill giant robot
        final int answer = sm.askMenu("So? Was there really a giant robot?",
            Map.of(0, "Yes, there really was. As you requested, I destroyed it."));
        if (answer != 0) {
            sm.sayOk("Please investigate the robot.");
            return;
        }

        sm.sayNext("You did? Thank you. I wish this were the end to our problem.");

        sm.forceCompleteQuest(3725);
        sm.addExp(120000);
    }

    @Script("q3726s")
    public static void q3726s(ScriptManager sm) {
        // Quest 3726 - Policeman in Danger START
        // Policeman (NPC 2082007) - Year 2099
        final int answer = sm.askMenu("It's dangerous here. Please go somewhere safer. A bunch of unidentifiable monsters appeared and took over the harbor.",
            Map.of(0, "Would you like me to help you?"));
        if (answer != 0) {
            sm.sayOk("Please be careful!");
            return;
        }

        sm.sayNext("Would you? I'm trying to eliminate the monsters that have taken over the harbor, but they outnumber me and I don't know if I can handle all of them on my own. Would you help me eliminate these monsters?");

        if (!sm.askAccept("Will you help eliminate the monsters?")) {
            sm.sayNext("It's dangerous here. Please get yourself to the nearest shelter where it's safe. Hurry!");
            return;
        }

        sm.forceStartQuest(3726);
        sm.sayOk("If you can, please eliminate 50 Overlord A's and 40 Overlord B's while I evacuate the people.");
    }

    @Script("q3726e")
    public static void q3726e(ScriptManager sm) {
        // Quest 3726 - Policeman in Danger END
        // Policeman (NPC 2082007) - Mob kills
        final int answer = sm.askMenu("How did it go?",
            Map.of(0, "I took care of the monsters as you requested."));
        if (answer != 0) {
            sm.sayOk("Please eliminate the monsters.");
            return;
        }

        sm.sayNext("Thank you. Thank you so much for your hard work. Thanks to you, the people at the harbor were able to evacuate safely.");

        sm.forceCompleteQuest(3726);
        sm.addExp(72000);
    }

    @Script("q3727s")
    public static void q3727s(ScriptManager sm) {
        // Quest 3727 - The Shelter Key START
        // Policeman (NPC 2082007) - Year 2099
        sm.sayNext("Could I ask you for one last favor? Everyone at the harbor was evacuated except one person. His name is Captain Edmond. He's stubborn and wouldn't cooperate. I think he remained behind, somewhere at the harbor. I must deliver the Shelter Key to him. Will you help me?");

        if (!sm.askAccept("Will you help find Captain Edmond?")) {
            sm.sayNext("I'm so worried about the captain. He's all alone right now.");
            return;
        }

        sm.forceStartQuest(3727);
        sm.sayOk("But there is one problem. I dropped the Shelter Key. I think one of the monsters might have picked it up. Could you find the Shelter Key and take it over to Captain Edmond?");
    }

    @Script("q3727e")
    public static void q3727e(ScriptManager sm) {
        // Quest 3727 - The Shelter Key END
        // Captain Edmond (NPC 2082008) - Deliver item 4032514
        final int SHELTER_KEY = 4032514;

        if (!sm.hasItem(SHELTER_KEY, 1)) {
            sm.sayOk("Please retrieve the Shelter Key from the monsters and deliver it to Captain Edmond. He's somewhere at the harbor.");
            return;
        }

        final int answer = sm.askMenu("What is it? What brings you here?",
            Map.of(0, "I've come to give you the Shelter Key. It's dangerous here."));
        if (answer != 0) {
            sm.sayOk("Be careful out there.");
            return;
        }

        sm.sayNext("Nonsense! I'm fine. But I'll take the key.");

        sm.removeItem(SHELTER_KEY, 1);
        sm.forceCompleteQuest(3727);
        sm.addExp(60000);
    }

    @Script("q3728s")
    public static void q3728s(ScriptManager sm) {
        // Quest 3728 - Temporary Relief START
        // Captain Edmond (NPC 2082008) - Year 2099
        sm.sayNext("Look here. The harbor has been entirely overtaken by monsters. The harbor represents the dream and dignity of the men of the sea, and I can't bear to just watch those monsters stomp all over that. I'm planning to drive them out. You seem young and capable. Why don't you help me, huh?");

        if (!sm.askAccept("Will you help drive out the monsters?")) {
            sm.sayNext("You're young, yet you lack determination and courage. Hmph.");
            return;
        }

        sm.forceStartQuest(3728);
        sm.sayOk("Alright! If you have been observing these monsters, you'll know that they have Radar Devices that operate as their eyes. If you eliminate those Radar Devices, they won't be able to do a thing. Bring me 20 Overlord A Radar Devices and 40 Overlord B Radar Devices. This isn't going to put a stop to the problem, but it'll earn us some time.");
    }

    @Script("q3728e")
    public static void q3728e(ScriptManager sm) {
        // Quest 3728 - Temporary Relief END
        // Captain Edmond (NPC 2082008) - Collect radar devices
        final int OVERLORD_A_RADAR = 4000548;
        final int OVERLORD_B_RADAR = 4000549;

        if (!sm.hasItem(OVERLORD_A_RADAR, 20) || !sm.hasItem(OVERLORD_B_RADAR, 40)) {
            sm.sayOk("Did you bring me 20 Overlord A Radar Devices and 40 Overlord B Radar Devices?");
            return;
        }

        sm.sayNext("Ha, sensational! Thanks to your hard work, I can sit back and watch these monsters completely lose their sense of direction. Even the thought of it makes me laugh.");

        sm.removeItem(OVERLORD_A_RADAR, 20);
        sm.removeItem(OVERLORD_B_RADAR, 40);
        sm.forceCompleteQuest(3728);
        sm.addExp(75000);
    }

    @Script("q3729s")
    public static void q3729s(ScriptManager sm) {
        // Quest 3729 - Time Traveler's Pocket Watch START (Daily Quest)
        // Andy (NPC 2082004) - Tera Forest
        final int answer1 = sm.askMenu("Time is like a continuum. It can either sweep you down like a raging river or jump you up like a relentless fish. My current location is... (mumble, mumble)",
            Map.of(0, "Um... Mr. Andy? What are you talking?"));
        if (answer1 != 0) {
            sm.sayOk("I'm busy thinking.");
            return;
        }

        final int answer2 = sm.askMenu("What...? Nevermind. What do you want? I'm very busy. Many things to think about.",
            Map.of(0, "I want to obtain the Time Traveler's Pocket Watch."));
        if (answer2 != 0) {
            sm.sayOk("Come back when you're ready.");
            return;
        }

        sm.sayNext("Aah, so you want to time travel. Well, then help me with something first.");

        if (!sm.askAccept("Will you help Andy?")) {
            sm.sayNext("Nothing given, then nothing gained. There's another law for ya. If you have no further business, please go away.");
            return;
        }

        sm.forceStartQuest(3729);
        sm.sayOk("This forest is just too noisy. Especially those bugs... what were they called... oh yes, #b'Beetles'#k. Those bugs make this annoying chewing sound on wood. And those #b'Dual Beetles'#k, they're even worse! Can you help decrease their number? Please eliminate 10 of each bug. If you indulge my request, I'll give you the #bTime Traveler's Pocket Watch#k.");
    }

    @Script("q3729e")
    public static void q3729e(ScriptManager sm) {
        // Quest 3729 - Time Traveler's Pocket Watch END (Daily Quest)
        // Andy (NPC 2082004) - Kill beetles, get pocket watch
        final int TIME_TRAVELERS_POCKET_WATCH = 4001393;

        sm.sayNext("Aah, it's finally gotten a bit more quiet. Though it'll probably get louder again by tomorrow. Still, I can at least enjoy some quiet for today. Here, this is the Time Traveler's Pocket Watch... As I say time and time again, when you're time-traveling you must be very careful, lest you get caught in the vacuum of time.");

        sm.addItem(TIME_TRAVELERS_POCKET_WATCH, 1);
        sm.forceCompleteQuest(3729);
        sm.addExp(1000);
    }

    @Script("q3730s")
    public static void q3730s(ScriptManager sm) {
        // Quest 3730 - Nex the Time Guard (Year 2215) START
        // Requires quests 3725-3729 completed
        sm.sayNext("Before your third time travel, you must pass Nex's test again. Don't get too comfortable, though. Nex will be even stronger this time around.");

        if (!sm.askAccept("Will you challenge Nex for the third time?")) {
            sm.sayNext("You won't be authorized to travel through time unless you defeat Nex the Gatekeeper of Time.");
            return;
        }

        sm.forceStartQuest(3730);
        sm.sayOk("When you defeat Nex, the Time Gate to the year 2215 will open.");
    }

    @Script("q3730e")
    public static void q3730e(ScriptManager sm) {
        // Quest 3730 - Nex the Time Guard (Year 2215) END
        // Requires defeating Nex (7120102)
        sm.sayNext("Ah, you've passed Nex's third test. Now then, you should be able to access the Time Gate to the year 2215.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3730);
        sm.addExp(110000);
        sm.sayOk("Take the Time Traveler's Pocket Watch and go through the Time Gate.");
    }

    // ========================================
    // YEAR 2215 - THE BOMBING (3731-3735)
    // ========================================

    @Script("q3731s")
    public static void q3731s(ScriptManager sm) {
        // Quest 3731 - Identity of the Missile START
        // Andy (NPC 2082004) - Year 2215
        sm.sayNext("I have an assignment for you in the year 2215. That year was the worst year in history, when the evolved robots bombed the City Center. The A.I. missile that bombed the City Center was called Dunas. Please travel to year 2215 and destroy the missile.");

        if (!sm.askAccept("Will you destroy Dunas?")) {
            sm.sayNext("What, are you getting scared? It's too late. Think about it. You've seen too much. You know too much about the future at this point.");
            return;
        }

        sm.forceStartQuest(3731);
        sm.sayOk("There isn't much recorded about Dunas. I don't even know what it looks like. All I can tell you is to be extra careful out there.");
    }

    @Script("q3731e")
    public static void q3731e(ScriptManager sm) {
        // Quest 3731 - Identity of the Missile END
        // Andy (NPC 2082004) - Kill Dunas
        sm.sayNext("Dunas is an android? Was that possible in 2215? Something doesn't add up.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3731);
        sm.addExp(140000);
        sm.sayOk("I have a bad feeling about this. I feel like we're missing a huge piece of the puzzle.");
    }

    @Script("q3732s")
    public static void q3732s(ScriptManager sm) {
        // Quest 3732 - Rambunctious Robots START
        // May (NPC 2082009) - Year 2215
        final int answer = sm.askMenu("Hello there, #b#h0##k. This is a very dangerous place. It was bombed recently, and I must warn you that walking around like you are doing right now poses a threat to your safety.",
            Map.of(0, "How did you know my name? And how did this happen?"));
        if (answer != 0) {
            sm.sayOk("Be careful!");
            return;
        }

        sm.sayNext("How do I know your name? That's a secret. I have to wipe out these noisy robots. They keep beeping rambunctiously, and it's driving me crazy. Won't you help me?");

        if (!sm.askAccept("Will you help eliminate the robots?")) {
            sm.sayNext("That's alright. I'll manage. I am a bit weak... And a little too pretty to be doing this... But I'll be alright.");
            return;
        }

        sm.forceStartQuest(3732);
        sm.sayOk("On the day of the bombing, I woke up and the City Center was already destroyed. It's been overtaken by those noisy robots. I'm THIS close to having a nervous breakdown. Please #b#h0##k, all you have to do is eliminate 50 of those noisy Robbies.");
    }

    @Script("q3732e")
    public static void q3732e(ScriptManager sm) {
        // Quest 3732 - Rambunctious Robots END
        // May (NPC 2082009) - Kill 50 Robbies
        sm.sayNext("Thank you. Now I can breathe again. Where could have those robots have come from?");

        sm.forceCompleteQuest(3732);
        sm.addExp(90000);
    }

    @Script("q3733s")
    public static void q3733s(ScriptManager sm) {
        // Quest 3733 - Survivor Search START
        // May (NPC 2082009) - Year 2215
        sm.sayNext("I was so happy that those Robbies were silenced, but then I began to think that I may be the only survivor left. That's so frightening. I mean, there must be other survivors, right? #b#h0##k, could you investigate the area and see if you can find any?");

        if (!sm.askAccept("Will you search for survivors?")) {
            sm.sayNext("#b#h0##k, do you think there are any survivors besides me?");
            return;
        }

        sm.forceStartQuest(3733);
        sm.sayOk("Could you search little deeper into the area, near where the missile landed? I haven't been able to search that far.");
    }

    @Script("q3733e")
    public static void q3733e(ScriptManager sm) {
        // Quest 3733 - Survivor Search END
        // Bao (NPC 2082010) - Find the survivor
        sm.sayNext("Who is that? There is a survivor! Here, right here!");

        sm.forceCompleteQuest(3733);
        sm.addExp(30000);
        sm.sayOk("I'm so relieved that there is another survivor.");
    }

    @Script("q3734s")
    public static void q3734s(ScriptManager sm) {
        // Quest 3734 - The Dangerous Android START
        // Bao (NPC 2082010) - Year 2215
        sm.sayNext("Quiet! Keep it down. This place is filled with Iruvatas. Don't you see them? Iruvatas are androids that resemble female warriors. The are made to attack, so they're extremely dangerous. I don't know who you are, but could you please eliminate the Iruvatas?");

        if (!sm.askAccept("Will you eliminate the Iruvatas?")) {
            sm.sayNext("I know. Anyone would be scared. They are, like I said, extremely dangerous.");
            return;
        }

        sm.forceStartQuest(3734);
        sm.sayOk("Wow, you're a brave one, aren't you? Then please eliminate 50 Iruvatas. Woohoo! I'll be rooting for you.");
    }

    @Script("q3734e")
    public static void q3734e(ScriptManager sm) {
        // Quest 3734 - The Dangerous Android END
        // Bao (NPC 2082010) - Kill 50 Iruvatas
        final int answer = sm.askMenu("I was cheering for you from here. That was incredible! But first things first. Am I the only survivor?",
            Map.of(0, "There is a lady named May not too far from here."));
        if (answer != 0) {
            sm.sayOk("Thank you for your help!");
            return;
        }

        sm.sayNext("Oh... Really? Then I should suck it up and go over there. Thank you so much for your help.");

        sm.forceCompleteQuest(3734);
        sm.addExp(90000);
    }

    @Script("q3735s")
    public static void q3735s(ScriptManager sm) {
        // Quest 3735 - The Wreckage of the Missile START
        // Bao (NPC 2082010) - Year 2215
        sm.sayNext("Right over there is where the missile landed. I have a feeling the Iruvatas and Robbies are being built there. There must be something going on. If you want to investigate, head right.");

        if (!sm.askAccept("Will you investigate?")) {
            sm.sayNext("You seem reluctant. Of course, I understand. After all, that's where the missile landed. It couldn't be safe.");
            return;
        }

        sm.forceStartQuest(3735);
        sm.sayOk("You seem like you're searching for something. I'd go right. It seems suspicious. Alright, I'll see you around then.");
    }

    @Script("q3735e")
    public static void q3735e(ScriptManager sm) {
        // Quest 3735 - The Wreckage of the Missile END
        // Andy (NPC 2082004) - Deliver Time Sand 4032516
        final int TIME_SAND = 4032516;

        if (!sm.hasItem(TIME_SAND, 1)) {
            sm.sayOk("You haven't found any clues?");
            return;
        }

        sm.sayNext("This... This is Time Sand. Time Sand stores the memories of time. This much Time Sand probably has a decent amount of memories. Good. It's definitely worth looking into. Don't you feel like we're getting somewhere?");

        sm.removeItem(TIME_SAND, 1);
        sm.forceCompleteQuest(3735);
        sm.addExp(60000);
        sm.sayOk("Keep up the good work.");
    }

    @Script("q3736s")
    public static void q3736s(ScriptManager sm) {
        // Quest 3736 - Nex the Time Guard (Year 2216) START
        // Requires quests 3731-3735 completed
        sm.sayNext("Before your fourth time travel, you must pass Nex's test again. Don't get too comfortable, though. Nex will be even stronger this time around!");

        if (!sm.askAccept("Will you challenge Nex for the fourth time?")) {
            sm.sayNext("You won't be authorized to travel through time unless you defeat Nex the Gatekeeper of Time.");
            return;
        }

        sm.forceStartQuest(3736);
        sm.sayOk("When you defeat Nex, the Time Gate to the year 2216 will open.");
    }

    @Script("q3736e")
    public static void q3736e(ScriptManager sm) {
        // Quest 3736 - Nex the Time Guard (Year 2216) END
        // Requires defeating Aufheben (8120100)
        sm.sayNext("Ah, you've passed Nex's fourth test. Now then, you should be able to access the Time Gate to year 2216.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3736);
        sm.addExp(130000);
        sm.sayOk("Take the Time Traveler's Pocket Watch and go through the Time Gate.");
    }

    // ========================================
    // YEAR 2216 - THE RUINS (3737-3740)
    // ========================================

    @Script("q3737s")
    public static void q3737s(ScriptManager sm) {
        // Quest 3737 - Central Robot Aufheben START
        // Andy (NPC 2082004) - Year 2216
        sm.sayNext("The purpose of this next time travel is to defeat Aufheben. Aufheben is the name of the central robot that began controlling all electronic devices after the City Center was bombed. Since Dunas was an android, it is quite likely that Aufheben is also an android. Do you think you can defeat Aufheben?");

        if (!sm.askAccept("Will you defeat Aufheben?")) {
            sm.sayNext("Are you not ready? Now, that's a problem. We don't have much time left.");
            return;
        }

        sm.forceStartQuest(3737);
        sm.sayOk("Aufheben is strong beyond your imagination. It won't be easy destroying Aufheben. Best wishes.");
    }

    @Script("q3737e")
    public static void q3737e(ScriptManager sm) {
        // Quest 3737 - Central Robot Aufheben END
        // Andy (NPC 2082004) - Kill Aufheben
        sm.sayNext("You're getting stronger and stronger. I can't believe you've defeated Aufheben. You are a true hero and an incredible time traveler.");

        sm.forceCompleteQuest(3737);
        sm.addExp(170000);
    }

    @Script("q3738s")
    public static void q3738s(ScriptManager sm) {
        // Quest 3738 - Disturbing the Army of Robots START
        // Ken (NPC 2082017) - Year 2216
        sm.sayNext("Are you from the support unit? I'm so glad you're here. Completing the mission here seems nearly impossible with the frequent attacks from the Afterlords and Prototype Lords. I'll give you the details a little later. Please just eliminate these monsters first.");

        if (!sm.askAccept("Will you eliminate the monsters?")) {
            sm.sayNext("Isn't the support unit supposed to provide support?");
            return;
        }

        sm.forceStartQuest(3738);
        sm.sayOk("Please eliminate 50 Afterlords and 40 Prototype Lords.");
    }

    @Script("q3738e")
    public static void q3738e(ScriptManager sm) {
        // Quest 3738 - Disturbing the Army of Robots END
        // Ken (NPC 2082017) - Mob kills
        sm.sayNext("Thank you. You've been extremely helpful.");

        sm.forceCompleteQuest(3738);
        sm.addExp(105000);
    }

    @Script("q3739s")
    public static void q3739s(ScriptManager sm) {
        // Quest 3739 - Isabella's Search START
        // Ken (NPC 2082017) - Year 2216
        sm.sayNext("I'm glad you came. I received a request for help, but I can't detect the exact location. We have to dispatch a search party, and I need you to lead it.");

        if (!sm.askAccept("Will you lead the search party?")) {
            sm.sayNext("Somebody's life depends on this. We don't have time to sit and wait around!");
            return;
        }

        sm.forceStartQuest(3739);
        sm.sayOk("Okay. The signal is coming from east, and we suspect the victim is a teenage female. Please investigate the eastern regions and let me know as soon as you find someone.");
    }

    @Script("q3739e")
    public static void q3739e(ScriptManager sm) {
        // Quest 3739 - Isabella's Search END
        // Isabella (NPC 2082016) - Find survivor
        sm.sayNext("Help! Help me! Are you here to rescue me?");

        sm.forceCompleteQuest(3739);
        sm.addExp(50000);
        sm.sayOk("Thank you. I've drained all my energy, but I'm hanging in there. I didn't think anyone would ever get here. I was so scared.");
    }

    @Script("q3740s")
    public static void q3740s(ScriptManager sm) {
        // Quest 3740 - What Was That I Saw? START
        // Isabella (NPC 2082016) - Year 2216
        final int answer1 = sm.askMenu("By the way, did you see it? I saw an angel.",
            Map.of(0, "An angel?"));
        if (answer1 != 0) {
            sm.sayOk("You have to believe me.");
            return;
        }

        final int answer2 = sm.askMenu("I saw an angel in the location where the high rise collapsed.",
            Map.of(0, "That can't be. Angels don't exist. Are you feeling alright?"));
        if (answer2 != 0) {
            sm.sayOk("I'm telling you. You should see for yourself.");
            return;
        }

        sm.sayNext("I'm serious. It's true. The angel was beaming radiantly. If you don't believe me, head east to where the high rise collapsed. You'll see.");

        if (!sm.askAccept("Do you want to investigate?")) {
            sm.sayNext("I'm telling you. You should see for yourself.");
            return;
        }

        sm.forceStartQuest(3740);
        sm.sayOk("You have to believe me.");
    }

    @Script("q3740e")
    public static void q3740e(ScriptManager sm) {
        // Quest 3740 - What Was That I Saw? END
        // Andy (NPC 2082004) - Deliver Time Sand 4032517
        final int TIME_SAND = 4032517;

        if (!sm.hasItem(TIME_SAND, 1)) {
            sm.sayOk("If you happen to find Time Sand, bring it over to me.");
            return;
        }

        sm.sayNext("Time Sand again. We've got a good amount of Time Sand now, but we still haven't found the pivotal clue. Ugh, this is so frustrating. What are we missing? I'll hang on to this for now.");

        sm.removeItem(TIME_SAND, 1);
        sm.forceCompleteQuest(3740);
        sm.addExp(70000);
    }

    @Script("q3742s")
    public static void q3742s(ScriptManager sm) {
        // Quest 3742 - Nex the Time Guard (Year 2230) START
        // Requires quests 3737-3740 completed
        sm.sayNext("Before your fifth time travel, you must pass Nex's test again. I know I keep saying this, but trust me, Nex will be even stronger this time around!");

        if (!sm.askAccept("Will you challenge Nex for the fifth time?")) {
            sm.sayNext("You won't be authorized to travel time until you defeat Nex the Gatekeeper of Time.");
            return;
        }

        sm.forceStartQuest(3742);
        sm.sayOk("When you defeat Nex, the Time Gate to the year 2230 will open.");
    }

    @Script("q3742e")
    public static void q3742e(ScriptManager sm) {
        // Quest 3742 - Nex the Time Guard (Year 2230) END
        // Requires defeating Oberon (8120101)
        sm.sayNext("Ah, you've passed Nex's fifth test. Now then, you should be able to access the Time Gate to the year 2230.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3742);
        sm.addExp(140000);
        sm.sayOk("Take the Time Traveler's Pocket Watch and go through the Time Gate.");
    }

    // ========================================
    // YEAR 2230 - IMMINENT COLLAPSE (3743-3744)
    // ========================================

    @Script("q3743s")
    public static void q3743s(ScriptManager sm) {
        // Quest 3743 - The New and Improved, Oberon START
        // Andy (NPC 2082004) - Year 2230
        sm.sayNext("You'll now travel through time a fifth time.. An android known as Oberon appeared in the year 2230. He is even more advanced than Aufheben. From what we've studied, there's a good chance that Oberon has the fifth Time Sand. Defeat Oberon and get me that Time Sand.");

        if (!sm.askAccept("Will you defeat Oberon and retrieve the Time Sand?")) {
            sm.sayNext("Are you saying you want to give up? Now?! Geez...");
            return;
        }

        sm.forceStartQuest(3743);
        sm.sayOk("We don't have much time!");
    }

    @Script("q3743e")
    public static void q3743e(ScriptManager sm) {
        // Quest 3743 - The New and Improved, Oberon END
        // Andy (NPC 2082004) - Kill Oberon, deliver Time Sand 4032518
        final int TIME_SAND = 4032518;

        if (!sm.hasItem(TIME_SAND, 1)) {
            sm.sayOk("Travel to the year 2230, defeat Oberon, and bring back the Time Sand.");
            return;
        }

        sm.sayNext("I was right. Oberon had the Time Sand. We're finally getting somewhere. Thank you again.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.removeItem(TIME_SAND, 1);
        sm.forceCompleteQuest(3743);
        sm.addExp(200000);
        sm.sayOk("Now, I suppose we one last time to travel to...");
    }

    @Script("q3744s")
    public static void q3744s(ScriptManager sm) {
        // Quest 3744 - Defeat the Mavericks START
        // Hoya (NPC 2082011) - Year 2230
        sm.sayNext("Yoohoo! Here! Over here! How did you get in here? This tower could collapse any minute, you know. Since you've come so far, you're probably here to rumble with the Mavericks. Am I right? Then would you do me a favor?");

        if (!sm.askAccept("Will you help Hoya?")) {
            sm.sayNext("Come on, it'll be fun!");
            return;
        }

        sm.forceStartQuest(3744);
        sm.sayOk("Nalo says I'm too young and can't take on any major tasks. So he asked me to watch the Mavericks' movement patterns. But I know I can handle bigger things. I have something to investigate, so could you eliminate the Mavericks for me and report it to Nalo upstairs? Just 30 Mavericks of each type. Easy, huh? Then I'll see you later!");
    }

    @Script("q3744e")
    public static void q3744e(ScriptManager sm) {
        // Quest 3744 - Defeat the Mavericks END
        // Nalo (NPC 2082012) - Report mob kills
        final int answer = sm.askMenu("Who are you? You want something from me?",
            Map.of(0, "I did a favor for Hoya, and I'm here to report it."));
        if (answer != 0) {
            sm.sayOk("I'm busy right now.");
            return;
        }

        sm.sayNext("Why, that little rascal. I can't believe he dumped his responsibilities on you! I apologize. He doesn't think things through! How could he entrust a stranger with his duties?");

        sm.forceCompleteQuest(3744);
        sm.addExp(90000);
        sm.sayOk("Thank you for your help. Hoya will get an earful from me later.");
    }

    @Script("q3748s")
    public static void q3748s(ScriptManager sm) {
        // Quest 3748 - Nex the Time Guard (Year 2503) START
        // Requires quests 3743-3744 completed
        sm.sayNext("Before your sixth time travel, you must pass Nex's test again. Don't get too comfortable, though. Nex will be much stronger this time around. Really!");

        if (!sm.askAccept("Will you challenge Nex for the sixth time?")) {
            sm.sayNext("You won't be authorized to travel though time unless you defeat Nex the Gatekeeper of Time.");
            return;
        }

        sm.forceStartQuest(3748);
        sm.sayOk("When you defeat Nex, the Time Gate to the year 2503 will open.");
    }

    @Script("q3748e")
    public static void q3748e(ScriptManager sm) {
        // Quest 3748 - Nex the Time Guard (Year 2503) END
        // Requires defeating Nibelung (8140510)
        sm.sayNext("Ah, you've passed Nex's sixth test. Now then, you should be able to access the Time Gate to the year 2503.");

        if (!sm.askYesNo("Do you want to complete the quest?")) {
            return;
        }

        sm.forceCompleteQuest(3748);
        sm.addExp(150000);
        sm.sayOk("Take the Time Traveler's Pocket Watch and go through the Time Gate.");
    }

    // ========================================
    // YEAR 2503 - FROM THE SKY (3749)
    // ========================================

    @Script("q3749s")
    public static void q3749s(ScriptManager sm) {
        // Quest 3749 - Nibelung's Song START
        // Andy (NPC 2082004) - Year 2503 (Final Quest)
        sm.sayNext("After all that time traveling, I feel like nothing got resolved. Judging from all the Time Sand we've collected, I think the key clue is in year 2503, where I'm from. Please travel to the year 2503, destroy Nibelung, and report it to Ashura.");

        if (!sm.askAccept("Will you travel to year 2503?")) {
            sm.sayNext("Are you going to quit? Give up? Just like that? When we're so close?");
            return;
        }

        sm.forceStartQuest(3749);
        sm.sayOk("Ashura will be somewhere in Air Battleship Hermes.");
    }

    @Script("q3749e")
    public static void q3749e(ScriptManager sm) {
        // Quest 3749 - Nibelung's Song END (Final Quest)
        // Ashura (NPC 2082013) - Year 2503
        final int ALTAIRE_HAT = 1003039;

        sm.sayNext("You've destroyed Nibelung. If you're here to tell me about Andy, I already know. I owe you a debt of gratitude. Of course, this doesn't solve everything. I don't think you fully understand yet, but someday you will. It just isn't time yet.");

        sm.addItem(ALTAIRE_HAT, 1);
        sm.forceCompleteQuest(3749);
        sm.addExp(220000);
        sm.sayOk("I will not forget you, time traveler...");
    }
}
