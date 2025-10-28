package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.item.InventoryType;
import kinoko.world.job.Job;

import java.util.List;
import java.util.Map;

public final class CygnusQuest extends ScriptHandler {
    @Script("enterDisguise0")
    public static void enterDisguise0(ScriptManager sm) {
        // Empress' Road : Crossroads of Ereve (130000200)
        //   west00 (-675, 92)
        sm.playPortalSE();
        sm.warp(130010000, "east00"); // Empress' Road : Training Forest I
    }

    @Script("enterDisguise1")
    public static void enterDisguise1(ScriptManager sm) {
        // Empress' Road : Training Forest I (130010000)
        //   in00 (-724, -754)
        sm.playPortalSE();
        sm.warp(130010010, "out00"); // Empress' Road : Training Forest I
    }

    @Script("enterDisguise2")
    public static void enterDisguise2(ScriptManager sm) {
        // Empress' Road : Training Forest I (130010000)
        //   in01 (-1439, -755)
        sm.playPortalSE();
        sm.warp(130010020, "out00"); // Empress' Road : Tiv's Forest
    }

    @Script("enterDisguise4")
    public static void enterDisguise4(ScriptManager sm) {
        // Empress' Road : Training Forest II (130010100)
        //   in01 (-2887, -747)
        sm.playPortalSE();
        sm.warp(130010120, "out00"); // Empress' Road : Tiru's Forest
    }

    @Script("enterDisguise5")
    public static void enterDisguise5(ScriptManager sm) {
        // Empress' Road : Training Forest III (130010200)
        //   west00 (-4097, 90)
        sm.playPortalSE();
        sm.warp(130020000, "east00"); // Empress' Road : Entrance to the Drill Hall
    }

    @Script("enterFirstDH")
    public static void enterFirstDH(ScriptManager sm) {
        // Empress' Road : Entrance to the Drill Hall (130020000)
        //   in00 (539, 91)
        if (sm.hasQuestStarted(20701)) {
            sm.playPortalSE();
            sm.warp(913000000, "out00");
        } else if (sm.hasQuestStarted(20702)) {
            sm.playPortalSE();
            sm.warp(913000100, "out00");
        } else if (sm.hasQuestStarted(20703)) {
            sm.playPortalSE();
            sm.warp(913000200, "out00");
        } else {
            sm.message("Hall #1 can only be entered if you're engaged in Kiku's Acclimation Training.");
        }
    }

    @Script("enterSecondDH")
    public static void enterSecondDH(ScriptManager sm) {
        // Empress' Road : Entrance to the 2nd Drill Hall (130020000)
        //   in01 portal - Job Advancement maps (913001xxx)
        if (sm.hasQuestStarted(20201)) {
            sm.playPortalSE();
            sm.warp(913001000);  // 2nd Drill Hall for Dawn Warrior
        } else if (sm.hasQuestStarted(20202)) {
            sm.playPortalSE();
            sm.warp(913001001);  // 2nd Drill Hall for Blaze Wizard
        } else if (sm.hasQuestStarted(20203)) {
            sm.playPortalSE();
            sm.warp(913001002);  // 2nd Drill Hall for Wind Archer
        } else if (sm.hasQuestStarted(20204)) {
            sm.playPortalSE();
            sm.warp(913001000);  // 2nd Drill Hall for Night Walker
        } else if (sm.hasQuestStarted(20205)) {
            sm.playPortalSE();
            sm.warp(913001001);  // 2nd Drill Hall for Thunder Breaker
        } else {
            sm.message("Hall #2 can only be entered if you're engaged in the 2nd Job Advancement.");
        }
    }

    @Script("enterDisguise3")
    public static void enterDisguise3(ScriptManager sm) {
        // CORRECT PORTAL for Baroq instance (Master of Disguise quest)
        // This portal enters the 3rd job advancement Baroq instance
        final int INVESTIGATION_PERMIT = 4032179;

        // Check if player is on Master of Disguise quest (20301-20305) for 3rd job advancement
        if (sm.hasQuestStarted(20301)) {
            // Dawn Warrior (job 1110)
            if (!sm.hasItem(INVESTIGATION_PERMIT, 1)) {
                sm.message("You need an Investigation Permit from Neinheart before entering.");
                return;
            }
            sm.message("[DEBUG] About to warp to Dawn Warrior instance...");
            sm.playPortalSE();
            try {
                sm.warpInstance(List.of(913002200), "sp", 130020000, 900, Map.of());
                sm.message("[DEBUG] After warpInstance, about to spawn NPCs...");
                spawnBaroqNpcs(sm, 913002200);
            } catch (Exception e) {
                sm.message("[ERROR] Exception: " + e.getMessage());
            }
            return;
        } else if (sm.hasQuestStarted(20302)) {
            // Blaze Wizard (job 1210) - shares map with Night Walker
            if (!sm.hasItem(INVESTIGATION_PERMIT, 1)) {
                sm.message("You need an Investigation Permit from Neinheart before entering.");
                return;
            }
            sm.playPortalSE();
            sm.warpInstance(List.of(913002300), "sp", 130020000, 900, Map.of());
            spawnBaroqNpcs(sm, 913002300);
            return;
        } else if (sm.hasQuestStarted(20303)) {
            // Wind Archer (job 1310)
            if (!sm.hasItem(INVESTIGATION_PERMIT, 1)) {
                sm.message("You need an Investigation Permit from Neinheart before entering.");
                return;
            }
            sm.playPortalSE();
            sm.warpInstance(List.of(913002000), "sp", 130020000, 900, Map.of());
            spawnBaroqNpcs(sm, 913002000);
            return;
        } else if (sm.hasQuestStarted(20304)) {
            // Night Walker (job 1410)
            if (!sm.hasItem(INVESTIGATION_PERMIT, 1)) {
                sm.message("You need an Investigation Permit from Neinheart before entering.");
                return;
            }
            sm.playPortalSE();
            sm.warpInstance(List.of(913002300), "sp", 130020000, 900, Map.of());
            spawnBaroqNpcs(sm, 913002300);
            return;
        } else if (sm.hasQuestStarted(20305)) {
            // Thunder Breaker (job 1510)
            if (!sm.hasItem(INVESTIGATION_PERMIT, 1)) {
                sm.message("You need an Investigation Permit from Neinheart before entering.");
                return;
            }
            sm.playPortalSE();
            sm.warpInstance(List.of(913002100), "sp", 130020000, 900, Map.of());
            spawnBaroqNpcs(sm, 913002100);
            return;
        }

        sm.message("You need to be on the Master of Disguise quest to enter this area.");
    }

    @Script("enterthirdDH")
    public static void enterthirdDH(ScriptManager sm) {
        // Empress' Road : Entrance to the 3rd Drill Hall (130020000)
        //   in02 portal - For level 100/110 skill quests with class-specific boss rooms

        if (sm.hasQuestStarted(20601)) {
            // Dawn Warrior (job 1110) - Boss 9300287
            sm.playPortalSE();
            sm.warp(913010000);
        } else if (sm.hasQuestStarted(20602)) {
            // Blaze Wizard (job 1210) - Boss 9300288
            sm.playPortalSE();
            sm.warp(913010100);
        } else if (sm.hasQuestStarted(20603)) {
            // Wind Archer (job 1310) - Boss 9300289
            sm.playPortalSE();
            sm.warp(913010200);
        } else if (sm.hasQuestStarted(20604)) {
            // Night Walker (job 1410) - Boss 9300290
            sm.playPortalSE();
            sm.warp(913010300);
        } else if (sm.hasQuestStarted(20605)) {
            // Thunder Breaker (job 1510) - Boss 9300288 (shares with Blaze Wizard)
            sm.playPortalSE();
            sm.warp(913010100);
        } else {
            sm.message("Hall #3 can only be entered if you're engaged in a Level 100/110 skill quest.");
        }
    }

    private static void spawnBaroqNpcs(ScriptManager sm, int targetMapId) {
        // Spawn fake knight NPCs in the Baroq instance
        // Called after warpInstance() - pass the target map ID since sm.getFieldId() still returns portal map
        // spawnNpc() broadcasts NpcEnterField packet - NO map XML edits needed!
        // originalField=false means spawn in player's CURRENT field (the instance they just warped to)

        // Each Cygnus class has different spawn coordinates (tested in-game)
        int baseX;
        final int baseY = 88; // All maps use Y=88

        switch (targetMapId) {
            case 913002200 -> { // Dawn Warrior - tested spawn: X=187, Y=88
                baseX = 187;
            }
            case 913002000 -> { // Wind Archer - tested spawn: X=2620, Y=88
                baseX = 2620;
            }
            case 913002300 -> { // Night Walker + Blaze Wizard (shared map)
                // Night Walker tested: X=-2140, Blaze Wizard tested: X=-2225
                // Using Night Walker's spawn point as base
                baseX = -2140;
            }
            case 913002100 -> { // Thunder Breaker - tested spawn: X=3365, Y=88
                baseX = 3365;
            }
            default -> {
                // Fallback coordinates
                baseX = 180;
            }
        }

        // Spawn 5 NPCs in a row, 100 pixels apart starting at spawn point
        // originalField=false spawns in player's current field (the instance they're in now)
        sm.spawnNpc(1104100, baseX, baseY, false, false);        // Mihile
        sm.spawnNpc(1104101, baseX + 100, baseY, false, false);  // Oz
        sm.spawnNpc(1104102, baseX + 200, baseY, false, false);  // Irina
        sm.spawnNpc(1104103, baseX + 300, baseY, false, false);  // Eckart
        sm.spawnNpc(1104104, baseX + 400, baseY, false, false);  // Hawkeye

        // Debug: Show spawn coordinates
        sm.message(String.format("[DEBUG] Spawned 5 NPCs in map %d at X=%d~%d, Y=%d", targetMapId, baseX, baseX + 400, baseY));
        sm.broadcastMessage("Search for the Master of Disguise among the knights! Talk to each one to find Baroq!");
    }

    @Script("enterfourthDH")
    public static void enterfourthDH(ScriptManager sm) {
        // Empress' Road : Entrance to the 4th Drill Hall (130020000)
        //   in03 portal - For BOTH level 120 skill quests AND 4th job advancement

        // First check for 4th job advancement quest (takes priority)
        if (sm.hasQuestCompleted(20406) || sm.hasQuestStarted(20407)) {
            sm.playPortalSE();
            sm.warp(913030000);  // Dark Ereve - Black Witch Boss Map (4th Job)
            return;
        }

        // Then check for level 120 skill quests - class-specific boss rooms
        if (sm.hasQuestStarted(20611)) {
            // Dawn Warrior (job 1110) - Boss 9300291
            sm.playPortalSE();
            sm.warp(913020000);
        } else if (sm.hasQuestStarted(20612)) {
            // Blaze Wizard (job 1210) - Boss 9300292
            sm.playPortalSE();
            sm.warp(913020100);
        } else if (sm.hasQuestStarted(20613)) {
            // Wind Archer (job 1310) - Boss 9300293
            sm.playPortalSE();
            sm.warp(913020200);
        } else if (sm.hasQuestStarted(20614)) {
            // Night Walker (job 1410) - Boss 9300294
            sm.playPortalSE();
            sm.warp(913020300);
        } else if (sm.hasQuestStarted(20615)) {
            // Thunder Breaker (job 1510) - Boss 9300292 (shares with Blaze Wizard)
            sm.playPortalSE();
            sm.warp(913020100);
        } else {
            sm.message("Hall #4 can only be entered if you're on a Level 120 skill quest or the 4th Job Advancement quest.");
        }
    }

    @Script("outSecondDH")
    public static void outSecondDH(ScriptManager sm) {
        // NPC to exit 2nd Drill Hall - warps back to Drill Hall Entrance
        sm.sayNext("Are you ready to leave the 2nd Drill Hall?");
        if (sm.askYesNo("Would you like to return to the Drill Hall Entrance?")) {
            sm.warp(130020000);  // Empress' Road : Entrance to the Drill Hall
        }
    }

    @Script("outthirdDH")
    public static void outthirdDH(ScriptManager sm) {
        // NPC to exit 3rd Drill Hall - warps back to Drill Hall Entrance
        sm.sayNext("Are you ready to leave the 3rd Drill Hall?");
        if (sm.askYesNo("Would you like to return to the Drill Hall Entrance?")) {
            sm.warp(130020000);  // Empress' Road : Entrance to the Drill Hall
        }
    }

    @Script("outfourthDH")
    public static void outfourthDH(ScriptManager sm) {
        // NPC to exit 4th Drill Hall / Boss Map - warps back to Drill Hall Entrance
        sm.sayNext("Are you ready to leave?");
        if (sm.askYesNo("Would you like to return to the Drill Hall Entrance?")) {
            sm.warp(130020000);  // Empress' Road : Entrance to the Drill Hall
        }
    }

    @Script("outDarkEreb")
    public static void outDarkEreb(ScriptManager sm) {
        // Exit portal from Dark Ereve (913030000) - used after Black Witch boss fight
        // Portal name in map: out00, but script name is outDarkEreb
        sm.playPortalSE();
        sm.warp(130000000, "sp");  // Return to Ereve
    }

    @Script("q20101e")
    public static void q20101e(ScriptManager sm) {
        // Path of a Dawn Warrior (20101 - end)
        if (!sm.askYesNo("Have you made your decision? The decision will be final, so think carefully before deciding what to do. Are you sure you want to become a Dawn Warrior?")) {
            sm.sayNext("This is an important decision to make.");
            return;
        }
        if (!sm.addItems(List.of(
                Tuple.of(1302077, 1),
                Tuple.of(1142066, 1)
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
        sm.setJob(Job.DAWN_WARRIOR_1);
        sm.forceCompleteQuest(20101);
        sm.sayNext("I have just molded your body to make it perfect for a Dawn Warrior. If you wish to become more powerful, use Stat Window (S) to raise the appropriate stats. If you aren't sure what to raise, just click on #bAuto#k.");
        sm.sayBoth("I have also expanded your inventory slot counts for your equipment and etc. inventory. Use those slots wisely and fill them up with items required for Knights to carry.");
        sm.sayBoth("I have also given you a hint of #bSP#k, so open the #bSkill Menu#k to acquire new skills. Of course, you can't raise them at all once, and there are some skills out there where you won't be able to acquire them unless you master the basic skills first.");
        sm.sayBoth("Unlike your time as a Noblesse, once you become the Dawn Warrior, you will lost a portion of your EXP when you run out of HP, okay?");
        sm.sayBoth("Now... I want you to go out there and show the world how the Knights of Cygnus operate.");
    }

    @Script("q20102e")
    public static void q20102e(ScriptManager sm) {
        // Path of a Blaze Wizard (20102 - end)
        if (!sm.askYesNo("Have you made your decision? The decision will be final, so think carefully before deciding what to do. Are you sure you want to become a Blaze Wizard?")) {
            sm.sayNext("This is an important decision to make.");
            return;
        }
        if (!sm.addItems(List.of(
                Tuple.of(1372043, 1),
                Tuple.of(1142066, 1)
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
        sm.setJob(Job.BLAZE_WIZARD_1);
        sm.forceCompleteQuest(20102);
        sm.sayNext("I have just molded your body to make it perfect for a Blaze Wizard. If you wish to become more powerful, use Stat Window (S) to raise the appropriate stats. If you aren't sure what to raise, just click on #bAuto#k.");
        sm.sayBoth("I have also expanded your inventory slot counts for your equipment and etc. inventory. Use those slots wisely and fill them up with items required for Knights to carry.");
        sm.sayBoth("I have also given you a hint of #bSP#k, so open the #bSkill Menu#k to acquire new skills. Of course, you can't raise them at all once, and there are some skills out there where you won't be able to acquire them unless you master the basic skills first.");
        sm.sayBoth("Unlike your time as a Noblesse, once you become the Blaze Wizard, you will lost a portion of your EXP when you run out of HP, okay?");
        sm.sayBoth("Now... I want you to go out there and show the world how the Knights of Cygnus operate.");
    }

    @Script("q20103e")
    public static void q20103e(ScriptManager sm) {
        // Path of a Wind Archer (20103 - end)
        if (!sm.askYesNo("Have you made your decision? The decision will be final, so think carefully before deciding what to do. Are you sure you want to become a Wind Archer?")) {
            sm.sayNext("This is an important decision to make.");
            return;
        }
        if (!sm.addItems(List.of(
                Tuple.of(2060000, 2000),
                Tuple.of(1452051, 1),
                Tuple.of(1142066, 1)
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
        sm.setJob(Job.WIND_ARCHER_1);
        sm.forceCompleteQuest(20103);
        sm.sayNext("I have just molded your body to make it perfect for a Wind Archer. If you wish to become more powerful, use Stat Window (S) to raise the appropriate stats. If you aren't sure what to raise, just click on #bAuto#k.");
        sm.sayBoth("I have also expanded your inventory slot counts for your equipment and etc. inventory. Use those slots wisely and fill them up with items required for Knights to carry.");
        sm.sayBoth("I have also given you a hint of #bSP#k, so open the #bSkill Menu#k to acquire new skills. Of course, you can't raise them at all once, and there are some skills out there where you won't be able to acquire them unless you master the basic skills first.");
        sm.sayBoth("Unlike your time as a Noblesse, once you become the Wind Archer, you will lost a portion of your EXP when you run out of HP, okay?");
        sm.sayBoth("Now... I want you to go out there and show the world how the Knights of Cygnus operate.");
    }

    @Script("q20104e")
    public static void q20104e(ScriptManager sm) {
        // Path of a Night Walker (20104 - end)
        if (!sm.askYesNo("Have you made your decision? The decision will be final, so think carefully before deciding what to do. Are you sure you want to become a Night Walker?")) {
            sm.sayNext("This is an important decision to make.");
            return;
        }
        if (!sm.addItems(List.of(
                Tuple.of(2070000, 500),
                Tuple.of(1472061, 1),
                Tuple.of(1142066, 1)
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
        sm.setJob(Job.NIGHT_WALKER_1);
        sm.forceCompleteQuest(20104);
        sm.sayNext("I have just molded your body to make it perfect for a Night Walker. If you wish to become more powerful, use Stat Window (S) to raise the appropriate stats. If you aren't sure what to raise, just click on #bAuto#k.");
        sm.sayBoth("I have also expanded your inventory slot counts for your equipment and etc. inventory. Use those slots wisely and fill them up with items required for Knights to carry.");
        sm.sayBoth("I have also given you a hint of #bSP#k, so open the #bSkill Menu#k to acquire new skills. Of course, you can't raise them at all once, and there are some skills out there where you won't be able to acquire them unless you master the basic skills first.");
        sm.sayBoth("Unlike your time as a Noblesse, once you become the Night Walker, you will lost a portion of your EXP when you run out of HP, okay?");
        sm.sayBoth("Now... I want you to go out there and show the world how the Knights of Cygnus operate.");
    }

    @Script("q20105e")
    public static void q20105e(ScriptManager sm) {
        // Path of a Thunder Breaker (20105 - end)
        if (!sm.askYesNo("Have you made your decision? The decision will be final, so think carefully before deciding what to do. Are you sure you want to become a Thunder Breaker?")) {
            sm.sayNext("This is an important decision to make.");
            return;
        }
        if (!sm.addItems(List.of(
                Tuple.of(1482014, 1),
                Tuple.of(1142066, 1)
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
        sm.setJob(Job.THUNDER_BREAKER_1);
        sm.forceCompleteQuest(20105);
        sm.sayNext("I have just molded your body to make it perfect for a Thunder Breaker. If you wish to become more powerful, use Stat Window (S) to raise the appropriate stats. If you aren't sure what to raise, just click on #bAuto#k.");
        sm.sayBoth("I have also expanded your inventory slot counts for your equipment and etc. inventory. Use those slots wisely and fill them up with items required for Knights to carry.");
        sm.sayBoth("I have also given you a hint of #bSP#k, so open the #bSkill Menu#k to acquire new skills. Of course, you can't raise them at all once, and there are some skills out there where you won't be able to acquire them unless you master the basic skills first.");
        sm.sayBoth("Unlike your time as a Noblesse, once you become the Thunder Breaker, you will lost a portion of your EXP when you run out of HP, okay?");
        sm.sayBoth("Now... I want you to go out there and show the world how the Knights of Cygnus operate.");
    }

    @Script("q20700s")
    public static void q20700s(ScriptManager sm) {
        // Are You Sure You Can Leave? (20700 - start)
        sm.sayNext("You have finally become a Knight-in-Training. I'd like to give you a mission right away, but you still look miles away from even being able to handle a task on your own. Are you sure you can even go to Victoria Island like this?");
        if (!sm.askAccept("It's up to you to head over to Victoria Island, but a Knight-in-Training that can't take care of one's self in battles is likely to cause harm to the Empress's impeccable reputation. As the Head Tactician of this island, I can't let that happen, period. I want you to keep training until the right time comes.")) {
            sm.sayNext("When will you realize how weak you are... When you get yourself in trouble in Victoria Island?");
            return;
        }
        sm.forceCompleteQuest(20700);
        sm.sayNext("#p1102000#, the Training Instructor, will help you train into a serviceable knight. Once you reach Level 13, I'll assign you a mission or two. So until then, keep training.");
        sm.sayPrev("Oh, and are you aware that if you strike a conversation with #p1101001#, she'll give you a blessing? The blessing will definitely help you on your journey.");
    }

    // MUSHKING EMPIRE QUESTS (2305-2310) ----------------------------------------------------------------

    @Script("q2305s")
    public static void q2305s(ScriptManager sm) {
        // Quest 2305 - Endangered Mushking Empire (Dawn Warrior) (START)
        // Cygnus Knights Instructor (1101003)
        sm.sayNext("Hey, I have a request for you.");
        sm.sayBoth("The Mushking Empire is in dire straits and in desperate need of help! I need you to take this #bletter of recommendation#k and deliver it to #b#p1300005##k, the Head Security Officer of the Mushking Empire.");
        sm.sayBoth("The empire is facing a crisis, and they need brave adventurers like you. Please, take this letter and help them in their time of need!");

        if (sm.addItem(4032375, 1)) { // Letter of Recommendation
            sm.forceStartQuest(2305);
            sm.sayOk("Thank you! Please hurry to the Mushking Empire and deliver this letter to #b#p1300005##k. They're counting on you!");
        } else {
            sm.sayOk("You don't have enough space in your inventory. Please make room and come back.");
        }
    }

    @Script("q2305e")
    public static void q2305e(ScriptManager sm) {
        // Quest 2305 - Endangered Mushking Empire (Dawn Warrior) (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Who are you? You look like someone who's exploring Maple World. Our kingdom is in serious danger, and we need someone dependable that can save us. If you can't help us, then I suggest you move on.");
            return;
        }

        sm.sayNext("Ah! You brought the letter of recommendation! Thank you for coming to our aid. The Mushking Empire is grateful for your assistance.");

        if (!sm.removeItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Please make sure you have the letter of recommendation.");
            return;
        }

        sm.addExp(6000);
        sm.forceCompleteQuest(2305);
        sm.sayOk("Your help means everything to us. With brave adventurers like you, the Mushking Empire will surely overcome this crisis!");
    }

    @Script("q2306s")
    public static void q2306s(ScriptManager sm) {
        // Quest 2306 - Endangered Mushking Empire (Blaze Wizard) (START)
        // Cygnus Knights Instructor (1101004)
        sm.sayNext("Hey, I have a request for you.");
        sm.sayBoth("The Mushking Empire is in dire straits and in desperate need of help! I need you to take this #bletter of recommendation#k and deliver it to #b#p1300005##k, the Head Security Officer of the Mushking Empire.");
        sm.sayBoth("The empire is facing a crisis, and they need brave adventurers like you. Please, take this letter and help them in their time of need!");

        if (sm.addItem(4032375, 1)) { // Letter of Recommendation
            sm.forceStartQuest(2306);
            sm.sayOk("Thank you! Please hurry to the Mushking Empire and deliver this letter to #b#p1300005##k. They're counting on you!");
        } else {
            sm.sayOk("You don't have enough space in your inventory. Please make room and come back.");
        }
    }

    @Script("q2306e")
    public static void q2306e(ScriptManager sm) {
        // Quest 2306 - Endangered Mushking Empire (Blaze Wizard) (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Who are you? You look like someone who's exploring Maple World. Our kingdom is in serious danger, and we need someone dependable that can save us. If you can't help us, then I suggest you move on.");
            return;
        }

        sm.sayNext("Ah! You brought the letter of recommendation! Thank you for coming to our aid. The Mushking Empire is grateful for your assistance.");

        if (!sm.removeItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Please make sure you have the letter of recommendation.");
            return;
        }

        sm.addExp(6000);
        sm.forceCompleteQuest(2306);
        sm.sayOk("Your help means everything to us. With brave adventurers like you, the Mushking Empire will surely overcome this crisis!");
    }

    @Script("q2307s")
    public static void q2307s(ScriptManager sm) {
        // Quest 2307 - Endangered Mushking Empire (Wind Archer) (START)
        // Cygnus Knights Instructor (1101005)
        sm.sayNext("Hey, I have a request for you.");
        sm.sayBoth("The Mushking Empire is in dire straits and in desperate need of help! I need you to take this #bletter of recommendation#k and deliver it to #b#p1300005##k, the Head Security Officer of the Mushking Empire.");
        sm.sayBoth("The empire is facing a crisis, and they need brave adventurers like you. Please, take this letter and help them in their time of need!");

        if (sm.addItem(4032375, 1)) { // Letter of Recommendation
            sm.forceStartQuest(2307);
            sm.sayOk("Thank you! Please hurry to the Mushking Empire and deliver this letter to #b#p1300005##k. They're counting on you!");
        } else {
            sm.sayOk("You don't have enough space in your inventory. Please make room and come back.");
        }
    }

    @Script("q2307e")
    public static void q2307e(ScriptManager sm) {
        // Quest 2307 - Endangered Mushking Empire (Wind Archer) (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Who are you? You look like someone who's exploring Maple World. Our kingdom is in serious danger, and we need someone dependable that can save us. If you can't help us, then I suggest you move on.");
            return;
        }

        sm.sayNext("Ah! You brought the letter of recommendation! Thank you for coming to our aid. The Mushking Empire is grateful for your assistance.");

        if (!sm.removeItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Please make sure you have the letter of recommendation.");
            return;
        }

        sm.addExp(6000);
        sm.forceCompleteQuest(2307);
        sm.sayOk("Your help means everything to us. With brave adventurers like you, the Mushking Empire will surely overcome this crisis!");
    }

    @Script("q2308s")
    public static void q2308s(ScriptManager sm) {
        // Quest 2308 - Endangered Mushking Empire (Night Walker) (START)
        // Cygnus Knights Instructor (1101006)
        sm.sayNext("Hey, I have a request for you.");
        sm.sayBoth("The Mushking Empire is in dire straits and in desperate need of help! I need you to take this #bletter of recommendation#k and deliver it to #b#p1300005##k, the Head Security Officer of the Mushking Empire.");
        sm.sayBoth("The empire is facing a crisis, and they need brave adventurers like you. Please, take this letter and help them in their time of need!");

        if (sm.addItem(4032375, 1)) { // Letter of Recommendation
            sm.forceStartQuest(2308);
            sm.sayOk("Thank you! Please hurry to the Mushking Empire and deliver this letter to #b#p1300005##k. They're counting on you!");
        } else {
            sm.sayOk("You don't have enough space in your inventory. Please make room and come back.");
        }
    }

    @Script("q2308e")
    public static void q2308e(ScriptManager sm) {
        // Quest 2308 - Endangered Mushking Empire (Night Walker) (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Who are you? You look like someone who's exploring Maple World. Our kingdom is in serious danger, and we need someone dependable that can save us. If you can't help us, then I suggest you move on.");
            return;
        }

        sm.sayNext("Ah! You brought the letter of recommendation! Thank you for coming to our aid. The Mushking Empire is grateful for your assistance.");

        if (!sm.removeItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Please make sure you have the letter of recommendation.");
            return;
        }

        sm.addExp(6000);
        sm.forceCompleteQuest(2308);
        sm.sayOk("Your help means everything to us. With brave adventurers like you, the Mushking Empire will surely overcome this crisis!");
    }

    @Script("q2309s")
    public static void q2309s(ScriptManager sm) {
        // Quest 2309 - Endangered Mushking Empire (Thunder Breaker) (START)
        // Cygnus Knights Instructor (1101007)
        sm.sayNext("Hey, I have a request for you.");
        sm.sayBoth("The Mushking Empire is in dire straits and in desperate need of help! I need you to take this #bletter of recommendation#k and deliver it to #b#p1300005##k, the Head Security Officer of the Mushking Empire.");
        sm.sayBoth("The empire is facing a crisis, and they need brave adventurers like you. Please, take this letter and help them in their time of need!");

        if (sm.addItem(4032375, 1)) { // Letter of Recommendation
            sm.forceStartQuest(2309);
            sm.sayOk("Thank you! Please hurry to the Mushking Empire and deliver this letter to #b#p1300005##k. They're counting on you!");
        } else {
            sm.sayOk("You don't have enough space in your inventory. Please make room and come back.");
        }
    }

    @Script("q2309e")
    public static void q2309e(ScriptManager sm) {
        // Quest 2309 - Endangered Mushking Empire (Thunder Breaker) (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Who are you? You look like someone who's exploring Maple World. Our kingdom is in serious danger, and we need someone dependable that can save us. If you can't help us, then I suggest you move on.");
            return;
        }

        sm.sayNext("Ah! You brought the letter of recommendation! Thank you for coming to our aid. The Mushking Empire is grateful for your assistance.");

        if (!sm.removeItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Please make sure you have the letter of recommendation.");
            return;
        }

        sm.addExp(6000);
        sm.forceCompleteQuest(2309);
        sm.sayOk("Your help means everything to us. With brave adventurers like you, the Mushking Empire will surely overcome this crisis!");
    }

    @Script("q2310s")
    public static void q2310s(ScriptManager sm) {
        // Quest 2310 - Endangered Mushking Empire (Aran) (START)
        // Lilin (1201000) - Rien
        sm.sayNext("Hey, I have a request for you.");
        sm.sayBoth("The Mushking Empire is in dire straits and in desperate need of help! I need you to take this #bletter of recommendation#k and deliver it to #b#p1300005##k, the Head Security Officer of the Mushking Empire.");
        sm.sayBoth("The empire is facing a crisis, and they need brave adventurers like you. Please, take this letter and help them in their time of need!");

        if (sm.addItem(4032375, 1)) { // Letter of Recommendation
            sm.forceStartQuest(2310);
            sm.sayOk("Thank you! Please hurry to the Mushking Empire and deliver this letter to #b#p1300005##k. They're counting on you!");
        } else {
            sm.sayOk("You don't have enough space in your inventory. Please make room and come back.");
        }
    }

    @Script("q2310e")
    public static void q2310e(ScriptManager sm) {
        // Quest 2310 - Endangered Mushking Empire (Aran) (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Who are you? You look like someone who's exploring Maple World. Our kingdom is in serious danger, and we need someone dependable that can save us. If you can't help us, then I suggest you move on.");
            return;
        }

        sm.sayNext("Ah! You brought the letter of recommendation! Thank you for coming to our aid. The Mushking Empire is grateful for your assistance.");

        if (!sm.removeItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("Please make sure you have the letter of recommendation.");
            return;
        }

        sm.addExp(6000);
        sm.forceCompleteQuest(2310);
        sm.sayOk("Your help means everything to us. With brave adventurers like you, the Mushking Empire will surely overcome this crisis!");
    }

    // 2ND JOB ADVANCEMENT QUESTS (20200-20205) ----------------------------------------------------------------

    @Script("q20200s")
    public static void q20200s(ScriptManager sm) {
        // Quest 20200 - The End of Knight-in-Training (START)
        // Neinheart (1101002) - Ereve
        sm.sayNext("#h0#? Wow, your level has skyrocketed since the last time I saw you. You also look like you've taken care of a number of missions as well... you seem much more ready to move on now than the last time I saw you. What do you think? Are you interested in taking the #bKnighthood Exam#k? It's time for you to grow out of the Knight-in-Training and become a bonafide Knight, right?");

        if (!sm.askAccept("Are you ready to take the Knighthood Exam?")) {
            sm.sayNext("Hmmm... Do you feel like you still have missions to take care of as a trainee? I commend your level of patience, but this has gone too far. Cygnus Knights is in dire need of new, more powerful knights.");
            return;
        }

        sm.forceCompleteQuest(20200);
        sm.sayOk("If you wish to take the Knighthood Exam, please come to Ereve. Each Chief Knight will test your abilities, and if you meet their standards, then you will officially become a Knight.");
    }

    @Script("q20201e")
    public static void q20201e(ScriptManager sm) {
        // Quest 20201 - Knighthood Exam: Dawn Warrior (END)
        // Chief Knight of Light (1101003) - Ereve
        final int PROOF_OF_TEST = 4032096;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20201)) {
            sm.sayNext("Do you wish to take the Knighthood Exam? Based on your contributions to Cygnus Knights so far, it is indeed hard to keep you here as the Knight-in-Training. Fine, I will let you take the Knighthood Exam. Are you ready?");

            if (!sm.askYesNo("The test is rather simple. Enter #bthe 2nd Drill Hall#k located at the end of the Training Forest, defeat the Mimis inside, and bring back the #b#t" + PROOF_OF_TEST + "#s#k that they have with them. I need #b30#k.")) {
                sm.sayNext("Talk to me when you are ready to take the test.");
                return;
            }

            sm.forceStartQuest(20201);
            sm.sayOk("If you can bring all the #t" + PROOF_OF_TEST + "#s back, then I will grant you the right to become a Knight. At this point, I need your skills to do the talking.");
            return;
        }

        // Check if player has collected all proofs
        if (!sm.hasItem(PROOF_OF_TEST, 30)) {
            sm.sayOk("I don't think you have found #b30 #t" + PROOF_OF_TEST + "#s#k yet. Go to #bHall #2#k and find them.");
            return;
        }

        // Complete the quest
        sm.sayNext("So, you brought all of Proof of Exam. Okay, I believe that you are now qualified to become an official knight. Do you want to become one?");

        if (!sm.askYesNo("Are you ready to become an official Knight?")) {
            sm.sayNext("I guess you are not ready to tackle on the responsibilities of an official knight.");
            return;
        }

        if (!sm.addItem(1142067, 1)) {
            sm.sayNext("Please check and see if you have an empty slot available in your equip inventory.");
            return;
        }

        sm.removeItem(PROOF_OF_TEST, 30);
        sm.setJob(Job.DAWN_WARRIOR_2);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20201);

        sm.sayNext("You are a Knight-in-Training no more. You are now an official knight of the Cygnus Knights.");
        sm.sayBoth("I have given you some #bSP#k. I have also given you a number of skills for a Dawn Warrior that's only available to knights, so I want you to work on it and hopefully cultivate it as much as your soul.");
        sm.sayPrev("Now that you are officially a Cygnus Knight, act like one so that you will continue to honor the Empress.");
    }

    @Script("q20202e")
    public static void q20202e(ScriptManager sm) {
        // Quest 20202 - Knighthood Exam: Blaze Wizard (END)
        // Chief Knight of Fire (1101004) - Ereve
        final int PROOF_OF_TEST = 4032097;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20202)) {
            sm.sayNext("Do you wish to take the Knighthood Exam? Wow, you are really fast. Based on your contributions to Cygnus Knights so far, it makes sense for you to take the Knighthood Exam. Are you ready?");

            if (!sm.askYesNo("The test is actually not complicated at all. Remember the Training Forest in which you trained when you were a Noblesse? At the end of it, you'll find #bthe 2nd Drill Hall#k. There, your job is to defeat the Mimis and bring back #b30 #t" + PROOF_OF_TEST + "#s#k in return.")) {
                sm.sayNext("I suppose you're not ready to take the test yet. Let... me know when you're ready.");
                return;
            }

            sm.forceStartQuest(20202);
            sm.sayOk("If you can bring all the #t" + PROOF_OF_TEST + "#s, then I will grant you the right to become a Knight. At this point, I need your skills to do the talking.");
            return;
        }

        // Check if player has collected all proofs
        if (!sm.hasItem(PROOF_OF_TEST, 30)) {
            sm.sayOk("I don't think you have acquired #b30 #t" + PROOF_OF_TEST + "#s#k yet. Did you forget where to find it by chance? Go to the very end of the Training Forest, and you'll find #bthe 2nd Drill Hall#k. Find the Mimis inside, defeat them, and bring back #b30 #t" + PROOF_OF_TEST + "#s#k.");
            return;
        }

        // Complete the quest
        sm.sayNext("Excellent! You've brought all the Proofs of Exam. You are now qualified to become an official knight. Are you ready?");

        if (!sm.askYesNo("Do you want to become an official Knight?")) {
            sm.sayNext("Take your time to prepare yourself for the responsibilities ahead.");
            return;
        }

        if (!sm.addItem(1142067, 1)) {
            sm.sayNext("Please check and see if you have an empty slot available in your equip inventory.");
            return;
        }

        sm.removeItem(PROOF_OF_TEST, 30);
        sm.setJob(Job.BLAZE_WIZARD_2);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20202);

        sm.sayNext("You are a Knight-in-Training no more. You are now an official knight of the Cygnus Knights.");
        sm.sayBoth("I have given you some #bSP#k. I have also granted you access to new Blaze Wizard skills that are only available to official knights.");
        sm.sayPrev("Continue to grow stronger and bring honor to the Cygnus Knights and our Empress.");
    }

    @Script("q20203e")
    public static void q20203e(ScriptManager sm) {
        // Quest 20203 - Knighthood Exam: Wind Archer (END)
        // Chief Knight of the Wind (1101005) - Ereve
        final int PROOF_OF_TEST = 4032098;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20203)) {
            sm.sayNext("Knighthood Exam... I suppose it's your turn to take it now. Based on your contributions to the Cygnus Knights, it only makes sense. Okay, I will grant you the right to take the Knighthood Exam. Are you ready?");

            if (!sm.askYesNo("The test is actually quite easy. Remember the Training Forest in which you trained back in the day? At the end of it, you'll find #bthe 2nd Drill Hall#k. There, your job is to defeat the Mimis and bring back #b30 #t" + PROOF_OF_TEST + "#s#k in return.")) {
                sm.sayNext("If you have the slightest of hesitation, it's good to dust them off first before taking on the test.");
                return;
            }

            sm.forceStartQuest(20203);
            sm.sayOk("If you can bring all the #t" + PROOF_OF_TEST + "#s, then I will grant you the right to become a Knight. At this point, I need your skills to do the talking.");
            return;
        }

        // Check if player has collected all proofs
        if (!sm.hasItem(PROOF_OF_TEST, 30)) {
            sm.sayOk("I don't think you have brought #b30 #t" + PROOF_OF_TEST + "#s#k yet. Go to the end of the Training Forest. There, inside #bthe 2nd Drill Hall#k, you'll need to find #t" + PROOF_OF_TEST + "#...");
            return;
        }

        // Complete the quest
        sm.sayNext("You've returned with all the Proofs of Exam. You have proven yourself worthy of becoming an official knight. Shall we proceed?");

        if (!sm.askYesNo("Are you ready to become an official Knight?")) {
            sm.sayNext("Prepare yourself when you are ready.");
            return;
        }

        if (!sm.addItem(1142067, 1)) {
            sm.sayNext("Please check and see if you have an empty slot available in your equip inventory.");
            return;
        }

        sm.removeItem(PROOF_OF_TEST, 30);
        sm.setJob(Job.WIND_ARCHER_2);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20203);

        sm.sayNext("You are a Knight-in-Training no more. You are now an official knight of the Cygnus Knights.");
        sm.sayBoth("I have given you some #bSP#k. You now have access to advanced Wind Archer skills befitting of an official knight.");
        sm.sayPrev("May the wind guide your arrows and bring glory to the Cygnus Knights.");
    }

    @Script("q20204e")
    public static void q20204e(ScriptManager sm) {
        // Quest 20204 - Knighthood Exam: Night Walker (END)
        // Chief Knight of Darkness (1101006) - Ereve
        final int PROOF_OF_TEST = 4032099;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20204)) {
            sm.sayNext("Knighthood Exam? Is it already that time? Come to think of it... you have been working for the Cygnus Knights for quite some time... Okay, I will give you the opportunity to take the Knighthood Exam right now. Are you ready?");

            if (!sm.askYesNo("The test is really nothing special. Go to the end of Training Forest and enter #bthe 2nd Drill Hall#k, where you'll find some Mimis inside. Defeat them all and bring back #b30 #t" + PROOF_OF_TEST + "#s#k with you.")) {
                sm.sayNext("It doesn't matter when you start, but delaying the inevitable doesn't change a single thing.");
                return;
            }

            sm.forceStartQuest(20204);
            sm.sayOk("If you can bring all the #t" + PROOF_OF_TEST + "#s back, then I will grant you the right to become a Knight. At this point, I need your skills to do the talking.");
            return;
        }

        // Check if player has collected all proofs
        if (!sm.hasItem(PROOF_OF_TEST, 30)) {
            sm.sayOk("You still haven't brought back #b30 #t" + PROOF_OF_TEST + "#s#k yet. I thought I told you to find them at the end of Training Forest, inside #bthe 2nd Drill Hall#k... Don't you remember this?");
            return;
        }

        // Complete the quest
        sm.sayNext("You've collected all the Proofs of Exam. You have what it takes to be an official knight. Do you accept this responsibility?");

        if (!sm.askYesNo("Will you become an official Knight?")) {
            sm.sayNext("Consider your decision carefully.");
            return;
        }

        if (!sm.addItem(1142067, 1)) {
            sm.sayNext("Please check and see if you have an empty slot available in your equip inventory.");
            return;
        }

        sm.removeItem(PROOF_OF_TEST, 30);
        sm.setJob(Job.NIGHT_WALKER_2);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20204);

        sm.sayNext("You are a Knight-in-Training no more. You are now an official knight of the Cygnus Knights.");
        sm.sayBoth("I have given you some #bSP#k. The shadows will now serve you better as an official knight with access to advanced Night Walker skills.");
        sm.sayPrev("Move through the darkness and strike down the enemies of the Empress.");
    }

    @Script("q20205e")
    public static void q20205e(ScriptManager sm) {
        // Quest 20205 - Knighthood Exam: Thunder Breaker (END)
        // Chief Knight of Lightning (1101007) - Ereve
        final int PROOF_OF_TEST = 4032100;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20205)) {
            sm.sayNext("What? Knighthood Exam? Are you taking that already? Wow, that was fast! Not a lot of time has passed since you first came here... but then again, seeing your contributions to the Cygnus Knights, it only makes sense. Okay, I'll let you take the test. Do you want to take it right now?");

            if (!sm.askYesNo("The test is simple. At the end of the Training Forest, you'll find a training hall. At #bthe 2nd Drill Hall#k, you'll defeat a number of Mimis and bring back #b30 #t" + PROOF_OF_TEST + "#s#k.")) {
                sm.sayNext("Hmmmm... do you have anything you want to prepare for? Okay, I will wait for you. Let me know when you're ready.");
                return;
            }

            sm.forceStartQuest(20205);
            sm.sayOk("Once you bring all the Proofs of Test, everyone will consider you a bonafide Knight. Enjoy it!");
            return;
        }

        // Check if player has collected all proofs
        if (!sm.hasItem(PROOF_OF_TEST, 30)) {
            sm.sayOk("I don't think you have gathered up #b30 #t" + PROOF_OF_TEST + "#s#k yet. Did you forget where the 2nd Drill Hall was? I understand if you're not good with directions, because neither I am... hahaha. Anyway, go to the end of the Training Forest, and you'll find #bthe 2nd Drill Hall#k, where you can find #t" + PROOF_OF_TEST + "#.");
            return;
        }

        // Complete the quest
        sm.sayNext("Great job! You've brought all the Proofs of Exam! You're officially ready to become a knight. Are you excited?");

        if (!sm.askYesNo("Ready to become an official Knight?")) {
            sm.sayNext("Take your time! It's a big decision.");
            return;
        }

        if (!sm.addItem(1142067, 1)) {
            sm.sayNext("Please check and see if you have an empty slot available in your equip inventory.");
            return;
        }

        sm.removeItem(PROOF_OF_TEST, 30);
        sm.setJob(Job.THUNDER_BREAKER_2);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20205);

        sm.sayNext("You are a Knight-in-Training no more. You are now an official knight of the Cygnus Knights!");
        sm.sayBoth("I have given you some #bSP#k. You now have access to powerful Thunder Breaker skills that befit an official knight!");
        sm.sayPrev("Let the lightning guide your fists and bring victory to the Cygnus Knights!");
    }

    // ACCLIMATION TRAINING QUESTS (20701-20703) ----------------------------------------------------------------

    @Script("q20701s")
    public static void q20701s(ScriptManager sm) {
        // Quest 20701 - 1st Acclimation Training (START)
        // Kiku (1102000) - Training Instructor
        sm.sayNext("Haha, so you're here! #p1101002# would have never let you out when you have just become a Knight-in-Training. I'm sure he said something along the lines of you diligently training until you reach Level 13.");
        sm.sayBoth("What is there to do except train anyway. This is all for your own good, and for the good of the Cygnus Knights. Now, let's work on the training.");

        if (!sm.askYesNo("The training you are about to undergo entails you getting acclimated to monsters outside Ereve, which means you are training for the real world. You'll need to do that sooner or later, anyway.")) {
            sm.sayNext("Hmmm? What is it? If you don't like the way #p1101002# talks, then don't mind him. He's always like that anyway, so you might as well get used to him.");
            return;
        }

        sm.forceStartQuest(20701);
        sm.sayNext("Go to the end of Training Forest, and you'll encounter a number of entrances. Your job is to enter #bthe 1st Drill Hall#k and take on a number of #r#o9300271#s#k that are specialized for this training. I want you to defeat #r30#k of them.");
    }

    @Script("q20701e")
    public static void q20701e(ScriptManager sm) {
        // Quest 20701 - 1st Acclimation Training (END)
        // Kiku (1102000) - Training Instructor
        final int TIGURU = 9300271;

        sm.sayNext("Ohhh, you managed to defeat 30 #o" + TIGURU + "#s. That's fast. If you keep up this pace, this training might be a breeze.");

        sm.addExp(1450);
        sm.addItem(2000020, 30); // Red Potion
        sm.addItem(2000021, 30); // Blue Potion
        sm.forceCompleteQuest(20701);

        sm.sayOk("Talk to me when you're ready to take on the next step.");
    }

    @Script("q20702s")
    public static void q20702s(ScriptManager sm) {
        // Quest 20702 - 2nd Acclimation Training (START)
        // Kiku (1102000) - Training Instructor
        sm.sayNext("Okay, are you ready to take on the next training session? I'm sure #p1101002# told you this already, but if you talk to #p1101001#, you'll be divinely blessed, which will help you go through the training that much easier. If you forgot to do so, then go get it right now. Are you ready?");

        if (!sm.askYesNo("This time, the targets are #r#o9300272#s#k, which are monsters you'll run into quite often in Victoria Island. Honestly, I've never been there, so I don't know if that statement is legit.")) {
            sm.sayNext("If you forgot something, then I suggest you take care of that first before going off to battle. There's nothing worse than looking for an item in the heat of the battle, only to realize you left it at home.");
            return;
        }

        sm.forceStartQuest(20702);
        sm.sayOk("Like I said last time, enter #bthe 1st Drill Hall#k and you'll see a group of #o9300272#s ready to battle. I need you to defeat #r30#k of them.");
    }

    @Script("q20702e")
    public static void q20702e(ScriptManager sm) {
        // Quest 20702 - 2nd Acclimation Training (END)
        // Kiku (1102000) - Training Instructor
        final int RIBBON_PIG = 9300272;

        sm.sayNext("Oh, did you manage to defeat 30 #o" + RIBBON_PIG + "#s? I have to say, you're doing quite well right now. Let's do one final session. Talk to me when you are ready.");

        sm.addExp(2000);
        sm.addItem(2000020, 40); // Red Potion
        sm.addItem(2000021, 40); // Blue Potion
        sm.forceCompleteQuest(20702);
    }

    @Script("q20703s")
    public static void q20703s(ScriptManager sm) {
        // Quest 20703 - 3rd Acclimation Training (START)
        // Kiku (1102000) - Training Instructor
        sm.sayNext("Are you ready to start the next training session? You did receive the blessing from #p1101001#, right? Then let's get this started. This is the final chapter of the Acclimation Training, which means after you pass this, you will be pressed into real duties!");

        if (!sm.askYesNo("The targets for this session are #o9300273#s. They are very much aggressive creatures, and they definitely look the part as well. You'll find a number of those creatures inside #bthe 1st Drill Hall#k, so go ahead and defeat #r30#k of them.")) {
            sm.sayNext("Your real Knight duties are coming up soon. Aren't you excited? Get this training session over with so you can take on the real duties!");
            return;
        }

        sm.forceStartQuest(20703);
        sm.sayOk("Good luck! Be careful, as #o9300273#s have quite a temper!");
    }

    @Script("q20703e")
    public static void q20703e(ScriptManager sm) {
        // Quest 20703 - 3rd Acclimation Training (END)
        // Kiku (1102000) - Training Instructor
        final int SLIME = 9300273;
sm.sayNext("I see that you have eliminated all the #o" + SLIME + "#s. Great work there! Do you feel like you can do well out of this island and in the Victoria Island?");

        sm.addExp(2800);
        sm.addItem(2000020, 50); // Red Potion
        sm.addItem(2000021, 50); // Blue Potion
        sm.forceCompleteQuest(20703);

        sm.sayBoth("This marks the end of the Acclimation Training sessions. You'll do well and not only survive, but thrive in Victoria Island. You will be thrown into fire, but I believe in you enough that you will find no trouble persevering through all that!");
        sm.sayPrev("Your true duties begin now. Once you reach Level 13, talk to #p1101002# and he will give you a list of duties you'll have to take on. Please serve and honor the Empress well, and do not engage in activities that may jeopardize your standings in the Cygnus Knights.");
    }

    // 3RD JOB ADVANCEMENT QUESTS (20300-20315) ----------------------------------------------------------------

    @Script("q20300s")
    public static void q20300s(ScriptManager sm) {
        // Quest 20300 - The Lost Treasure (START)
        // Neinheart (1101002) - Ereve
        sm.sayNext("Mayday, mayday! All members of the Cygnus Knights must return to #bEreve#k immediately. I repeat, everyone must return to Ereve immediately! Details available at Ereve.");

        if (!sm.askAccept("We need everyone there at once, so go there ASAP.")) {
            sm.sayNext("I don't know what you're doing right now, but I can't think of anything else as urgent as this. I trust you to make a judgment worthy of a Cygnus Knight.");
            return;
        }

        sm.forceCompleteQuest(20300);
        sm.sayNext("You've heard of a group out there that is there to support Black Mage, right? They are called the Black Wings and they are our main adversaries. Apparently, one of them managed to enter Ereve and steal a very important treasure.");
        sm.sayOk("I doubt the thief went very far, so we must conduct the search right this minute. The Chief Knights will provide details, so please go see your Chief Knight immediately.");
    }

    @Script("q20301e")
    public static void q20301e(ScriptManager sm) {
        // Quest 20301 - The Master of Disguise: Dawn Warrior (END)
        // Chief Knight of Light (1101003) - Ereve
        final int SHINSOO_TEARDROP = 4032101;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20301)) {
            sm.sayNext("Ereve is protected by the Shinsoo, so when an evil force enters the island, it's easy to detect that something is wrong. The problem is, we were unable to do so this time around, and that's because the culprit disguised himself as a Knight. He's #r#o9001009# Baroq, the member of the Black Wings#k. He is responsible for the theft.");
            sm.sayBoth("The treasure is one of those important items that MUST NOT be in the hands of the Black Mage. #o9001009# has yet to escape Ereve, so I need you to join the search as well. Before conducting the search, however, you must first acquire #b#t4032179##k, so go visit #p1101002# and obtain the Investigation Permit.");
            sm.sayBoth("You MUST locate the #r#o9001009##k, eliminate it, and bring back the #btreasure#k. I, as well as other Knights, will join you in this effort.");

            if (!sm.askYesNo("Beware of the fact that #o9001009# is always capable of tricking you by pretending to be someone else. The appearances may change, but the inner self won't, so #rI suggest you keep talking to various Knights until you hear something that doesn't sound like it would come from a Knight. If you do, you can consider that individual #o9001009##k and attack immediately.")) {
                sm.sayNext("You can run away if you're afraid of this. Just know that if you keep running away, you will never amount to anything.");
                return;
            }

            sm.forceStartQuest(20301);
            sm.sayOk("Good luck in your search. The fate of Ereve depends on you!");
            return;
        }

        // Check if player defeated Baroq and got treasure
        if (!sm.hasItem(SHINSOO_TEARDROP, 1)) {
            sm.sayOk("Were you not able to find #o9001009#? #o9001009# has yet to escape Ereve. You must first visit #b#p1101002##k and obtain the #bInvestigation Permit#k before conducting the search. Once you have begun the search, you must defeat #r#o9001009##k and recover #b#t" + SHINSOO_TEARDROP + "#k.");
            return;
        }

        // Complete quest
        sm.sayNext("So you were able to defeat #o9001009# #p1104001#. Brilliant!");
        sm.removeItem(SHINSOO_TEARDROP, 1);
        sm.forceCompleteQuest(20301);
        sm.sayOk("The Empress has expressed gratitude for your effort and loyalty.");
    }

    @Script("q20302e")
    public static void q20302e(ScriptManager sm) {
        // Quest 20302 - The Master of Disguise: Blaze Wizard (END)
        // Chief Knight of Fire (1101004) - Ereve
        final int SHINSOO_TEARDROP = 4032102;

        if (!sm.hasQuestStarted(20302)) {
            sm.sayNext("Wow, you're here! #p1101002# gave you an outline, right? Then let's cut to the chase. Ereve is an island protected by the Shinsoo, which means no force of evil can enter the island. Even if someone did enter, the person would have been noticed right away.");
            sm.sayBoth("The problem is, we were unable to spot the intruder this time. That's because the thief is the #rmember of the Black Wings, #o9001009# #p1104001##k. We detected an intrusion, but we were unable to spot the fake. So while people were panicking, the thief brazenly breezed through and stole the treasure.");
            sm.sayBoth("That is the one item that SHOULD NOT be at the hands of Black Mage! Since #o9001009# has yet to leave Ereve, I want you to join in on the search as well. If you meet #p1101002#, you'll be able to receive a #b#t4032179##k that requires rummaging through Ereve.");
            sm.sayBoth("Okay, now I want you to defeat #r#o9001009##k and bring back the #btreasure#k! I, as well as other members, will also aid you in the search.");

            if (!sm.askYesNo("#o9001009# is most likely disguised as one of the Knights. The appearances may change, but the inner self won't, so #rI suggest you keep talking to various Knights until you hear something that doesn't sound like it would come from a Knight. If you do, you can consider that individual #o9001009##k and attack immediately.")) {
                sm.sayNext("Are... are you scared? I can understand the fear that's creeping through you... but running away will not help you accomplish a single thing. Please think about this once more.");
                return;
            }

            sm.forceStartQuest(20302);
            sm.sayOk("Good hunting! Bring back the treasure safely!");
            return;
        }

        if (!sm.hasItem(SHINSOO_TEARDROP, 1)) {
            sm.sayOk("Were you not able to find #o9001009# yet? #o9001009# has yet to leave Ereve, so receive the #bInvestigation Permit#k first from #b#p1101002##k, then conduct a search of your own. Sooner or later, you'll find #r#o9001009##k. I need you to really eliminate it and bring back #b#t" + SHINSOO_TEARDROP + "#k!");
            return;
        }

        sm.sayNext("Were you able to defeat #o9001009# #p1104001#?? Wow, that is just incredible! You are indeed an incredible Knight!");
        sm.removeItem(SHINSOO_TEARDROP, 1);
        sm.forceCompleteQuest(20302);
        sm.sayOk("The Empress is very proud of your work!");
    }

    @Script("q20303e")
    public static void q20303e(ScriptManager sm) {
        // Quest 20303 - The Master of Disguise: Wind Archer (END)
        // Chief Knight of Wind (1101005) - Ereve
        final int SHINSOO_TEARDROP = 4032103;

        if (!sm.hasQuestStarted(20303)) {
            sm.sayNext("..I am sure .#p1101002# gave you a thorough outline, so I will just go straight to the facts. As you know, Ereve is an island protected by the Shinsoo. Any form of darkness is not only forbidden from winning, but even if it does enter, it'd be detected right away and be promptly removed.");
            sm.sayBoth("Unfortunately, we weren't able to do that this time, and... that's because... we did not account for who the thief was. The culprit was#ra member of Black Wings, #o9001009# #p1104001##k... We were unable to locate him even after entering this island, because he was in disguise.");
            sm.sayBoth("The treasure should NEVER end up in Black Mage's hands. #o9001009# has yet to escape Ereve, so you must participate in this search as well. Go visit #p1101002# and receive #b#t4032179##k.");
            sm.sayBoth("Now... Go defeat #r#o9001009##k... And bring back the #btreasure#k... I, as well as other Knights, will also be conducting the search.");

            if (!sm.askYesNo("#o9001009# is most likely disguised as one of the Knights. The appearances may change, but the inner self won't, so #rI suggest you keep talking to various Knights until you hear something that doesn't sound like it would come from a Knight. If you do, you can consider that individual #o9001009##k and attack immediately.")) {
                sm.sayNext("Fear? Hesitation? As long as you keep the two with you, you will never be able to move up.");
                return;
            }

            sm.forceStartQuest(20303);
            sm.sayOk("May the wind guide you in your search.");
            return;
        }

        if (!sm.hasItem(SHINSOO_TEARDROP, 1)) {
            sm.sayOk("Were you unable to find #o9001009#...? I know for sure that he has yet to escape. First, visit #b#p1101002##k and receive the #bInvestigation Permit#k, then conduct a thorough search of the whole Ereve to locate #r#o9001009##k, then defeat it to bring #b#t" + SHINSOO_TEARDROP + "#k back...");
            return;
        }

        sm.sayNext("Wow... you were able to defeat #o9001009# #p1104001#... You are a special Knight, indeed.");
        sm.removeItem(SHINSOO_TEARDROP, 1);
        sm.forceCompleteQuest(20303);
        sm.sayOk("The Empress observes your accomplishment. Do not forget the fact that you're an important individual here.");
    }

    @Script("q20304e")
    public static void q20304e(ScriptManager sm) {
        // Quest 20304 - The Master of Disguise: Night Walker (END)
        // Chief Knight of Darkness (1101006) - Ereve
        final int SHINSOO_TEARDROP = 4032104;

        if (!sm.hasQuestStarted(20304)) {
            sm.sayNext("You're here. I'm sure #p1101002# told you the gist of things, so I should forgo the details and... but you look like you need the details again. Sigh. As you are well aware, Ereve is an island protected by Shinsoo. No evil presence may enter the island, and even if it were successfully here, they'd be immediately detected and removed from the premise.");
            sm.sayBoth("The problem is, we were unable to find him this time. It's simple, really. The thief was actually #o9001009#. #o9001009# #p1104001##k, an important member of the #rBlack Wings#k. We were unable to spot him because of his ability to transform himself to another being.");
            sm.sayBoth("The treasure is an important item that shall NEVER end up at the hands of the Black Mage. #o9001009# has yet to escape Ereve, so join the search effort. #b#t4032179##k will be needed to conduct the search, so go visit #p1101002# first.");
            sm.sayBoth("Now, I want you to defeat #r#o9001009##k and bring back the #btreasure#k.I, as well as other Knights, will also be searching.");

            if (!sm.askYesNo("#o9001009# is most likely disguised as one of the Knights. The appearances may change, but the inner self won't, so #rI suggest you keep talking to various Knights until you hear something that doesn't sound like it would come from a Knight. If you do, you can consider that individual #o9001009##k and attack immediately.")) {
                sm.sayNext("Are you scared already? Keep being afraid and you will never receive the chance to move up.");
                return;
            }

            sm.forceStartQuest(20304);
            sm.sayOk("Move through the shadows and find the intruder.");
            return;
        }

        if (!sm.hasItem(SHINSOO_TEARDROP, 1)) {
            sm.sayOk("Were you not able to find #o9001009#? Reports are that he has yet to escape Ereve, so keep searching. First, acquire the #bInvestigation Permit#k through #b#p1101002##k to start the search of Ereve. Once you run into #r#o9001009##k, eliminate him and bring back #b#t" + SHINSOO_TEARDROP + "#k.");
            return;
        }

        sm.sayNext("Did you manage to defeat #o9001009# #p1104001#? Hmmm... not bad. That was brilliant.");
        sm.removeItem(SHINSOO_TEARDROP, 1);
        sm.forceCompleteQuest(20304);
        sm.sayOk("The Empress is indeed watching your every move, and she is impressed as well.");
    }

    @Script("q20305e")
    public static void q20305e(ScriptManager sm) {
        // Quest 20305 - The Master of Disguise: Thunder Breaker (END)
        // Chief Knight of Lightning (1101007) - Ereve
        final int SHINSOO_TEARDROP = 4032105;

        if (!sm.hasQuestStarted(20305)) {
            sm.sayNext("#p1101002# told you what happened, right? It's just terrible! Ereve is usually an island protected by the power of Shinsoo, which prevented forces of evil from landing here, but... a master of transformation!!! Who would have thought?");
            sm.sayBoth("Ah, you didn't hear anything about the master of transformation? #r#o9001009# #p1104001#is actually a member of the Black Wings, and he's very good at transforming to someone else#k. He'll come back as one of the knights, and I need you to spot the fake.");
            sm.sayBoth("It is a treasure that MUST NOT end up at the hands of the Black Mage. #o9001009# has yet to leave Ereve, so start the dash! Ereve shall be found soon. The #b#t4032179##k required to conduct a search will be given to by #p1101002#!");
            sm.sayBoth("Now go ahead and defeat #r#o9001009##k, so you can bring back the #btreasure#k home! I'll be conducting searches of my own with other Knights as well.");

            if (!sm.askYesNo("#o9001009# is most likely disguised as one of the Knights. The appearances may change, but the inner self won't, so #rI suggest you keep talking to various Knights until you hear something that doesn't sound like it would come from a Knight. If you do, you can consider that individual #o9001009##k and attack immediately.")) {
                sm.sayNext("Wait a minute, are you afraid of this? Seriously, what other adventure can bring this much excitement? Don't be scared... Think it over. You can't possibly expect yourself to improve just by sitting around doing nothing, right?");
                return;
            }

            sm.forceStartQuest(20305);
            sm.sayOk("Let the lightning guide your search! Yeah!");
            return;
        }

        if (!sm.hasItem(SHINSOO_TEARDROP, 1)) {
            sm.sayOk("You still haven't found #o9001009#? I hear that he has yet to escape Ereve, so if you keep searching, you'll find him. Go see #b#p1101002##k and receive the #bInvestigation Permit#k to start searching on Ereve. Once you encounter #r#o9001009##k, eliminate it, and bring back #b#t" + SHINSOO_TEARDROP + "#k with you!");
            return;
        }

        sm.sayNext("Wow!! You were able to defeat #p1104001#! That is just incredible! Yeah!");
        sm.removeItem(SHINSOO_TEARDROP, 1);
        sm.forceCompleteQuest(20305);
        sm.sayOk("The Empress is ecstatic, as well! Yeah!");
    }

    // INVESTIGATION PERMIT QUESTS (20306-20310) ----------------------------------------------------------------

    @Script("q20306e")
    public static void q20306e(ScriptManager sm) {
        // Quest 20306 - Ereve Investigation Permit: Dawn Warrior (END)
        // Neinheart (1101002) - Ereve
        final int POWER_CRYSTAL = 4005004;
        final int INVESTIGATION_PERMIT = 4032179;

        // Check if quest needs to be started
        if (!sm.hasQuestStarted(20306)) {
            sm.sayNext("...#t" + INVESTIGATION_PERMIT + "#? I am sorry, but there's a problem with that. Truth be told, Ereve had never faced a crisis like this before, so naturally, I have never had to provide so many copies of Investigation Permits to various Knights. Yes, I am short on supplies needed to make the Investigation Permit.");
            sm.sayBoth("I have been quickly making new Investigation Permits, but that's also not easy in that... #t" + INVESTIGATION_PERMIT + "# is created using an item that is placed in specifically to prevent illegal copies from being made, so... yes, I am short on one item. The search needs to start right away, as well...");
            sm.sayBoth("Tell you what. I want you to bring me that item that is required to make the Investigation Permit. I know we don't have much time, but even the best Master of Disguise won't be able to escape Ereve that quickly, especially if the island is in code red as it is now. Once you bring me the item, I will go ahead and print you the Investigation Permit.");

            if (!sm.askYesNo("The item I need is 1 #t" + POWER_CRYSTAL + "#. The Investigation Permit created with the magic power of #t" + POWER_CRYSTAL + "# cannot be duplicated nor stolen, so it's perfect for this. Thanks in advance.")) {
                sm.sayNext("Oh no.. are you giving up on the search?");
                return;
            }

            sm.forceStartQuest(20306);
            sm.sayOk("Please hurry! Every second counts!");
            return;
        }

        // Check if player has Power Crystal
        if (!sm.hasItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("You have yet to bring #t" + POWER_CRYSTAL + "#. Every second counts, and we can't afford to lose any more time. Please hurry.");
            return;
        }

        // Complete quest and give Investigation Permit
        sm.sayNext("Oh you brought #t" + POWER_CRYSTAL + "#. I'll go ahead and make you #t" + INVESTIGATION_PERMIT + "# right now.");

        if (!sm.removeItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("Please make sure you have the Power Crystal.");
            return;
        }

        sm.addItem(INVESTIGATION_PERMIT, 1);
        sm.forceCompleteQuest(20306);
        sm.sayOk("This will give you access to every part of Ereve that is currently in code red. Please search through the island carefully and find the Master of Disguise that is responsible for this mess.");
    }

    @Script("q20307e")
    public static void q20307e(ScriptManager sm) {
        // Quest 20307 - Ereve Investigation Permit: Blaze Wizard (END)
        final int POWER_CRYSTAL = 4005004;
        final int INVESTIGATION_PERMIT = 4032179;

        if (!sm.hasQuestStarted(20307)) {
            sm.sayNext("...#t" + INVESTIGATION_PERMIT + "#? I am sorry, but there's a problem with that. Truth be told, Ereve had never faced a crisis like this before, so naturally, I have never had to provide so many copies of Investigation Permits to various Knights. Yes, I am short on supplies needed to make the Investigation Permit.");
            sm.sayBoth("I have been quickly making new Investigation Permits, but that's also not easy in that... #t" + INVESTIGATION_PERMIT + "# is created using an item that is placed in specifically to prevent illegal copies from being made, so... yes, I am short on one item. The search needs to start right away, as well...");
            sm.sayBoth("Tell you what. I want you to bring me that item that is required to make the Investigation Permit. I know we don't have much time, but even the best Master of Disguise won't be able to escape Ereve that quickly, especially if the island is in code red as it is now. Once you bring me the item, I will go ahead and print you the Investigation Permit.");

            if (!sm.askYesNo("The item I need is 1 #t" + POWER_CRYSTAL + "#. The Investigation Permit created with the magic power of #t" + POWER_CRYSTAL + "# cannot be duplicated nor stolen, so it's perfect for this. Thanks in advance.")) {
                sm.sayNext("Oh no.. are you giving up on the search?");
                return;
            }

            sm.forceStartQuest(20307);
            sm.sayOk("Please hurry! Every second counts!");
            return;
        }

        if (!sm.hasItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("You have yet to bring #t" + POWER_CRYSTAL + "#. Every second counts, and we can't afford to lose any more time. Please hurry.");
            return;
        }

        sm.sayNext("Oh you brought #t" + POWER_CRYSTAL + "#. I'll go ahead and make you #t" + INVESTIGATION_PERMIT + "# right now.");

        if (!sm.removeItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("Please make sure you have the Power Crystal.");
            return;
        }

        sm.addItem(INVESTIGATION_PERMIT, 1);
        sm.forceCompleteQuest(20307);
        sm.sayOk("This will give you access to every part of Ereve that is currently in code red. Please search through the island carefully and find the Master of Disguise that is responsible for this mess.");
    }

    @Script("q20308e")
    public static void q20308e(ScriptManager sm) {
        // Quest 20308 - Ereve Investigation Permit: Wind Archer (END)
        final int POWER_CRYSTAL = 4005004;
        final int INVESTIGATION_PERMIT = 4032179;

        if (!sm.hasQuestStarted(20308)) {
            sm.sayNext("...#t" + INVESTIGATION_PERMIT + "#? I am sorry, but there's a problem with that. Truth be told, Ereve had never faced a crisis like this before, so naturally, I have never had to provide so many copies of Investigation Permits to various Knights. Yes, I am short on supplies needed to make the Investigation Permit.");
            sm.sayBoth("I have been quickly making new Investigation Permits, but that's also not easy in that... #t" + INVESTIGATION_PERMIT + "# is created using an item that is placed in specifically to prevent illegal copies from being made, so... yes, I am short on one item. The search needs to start right away, as well...");
            sm.sayBoth("Tell you what. I want you to bring me that item that is required to make the Investigation Permit. I know we don't have much time, but even the best Master of Disguise won't be able to escape Ereve that quickly, especially if the island is in code red as it is now. Once you bring me the item, I will go ahead and print you the Investigation Permit.");

            if (!sm.askYesNo("The item I need is 1 #t" + POWER_CRYSTAL + "#. The Investigation Permit created with the magic power of #t" + POWER_CRYSTAL + "# cannot be duplicated nor stolen, so it's perfect for this. Thanks in advance.")) {
                sm.sayNext("Oh no.. are you giving up on the search?");
                return;
            }

            sm.forceStartQuest(20308);
            sm.sayOk("Please hurry! Every second counts!");
            return;
        }

        if (!sm.hasItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("You have yet to bring #t" + POWER_CRYSTAL + "#. Every second counts, and we can't afford to lose any more time. Please hurry.");
            return;
        }

        sm.sayNext("Oh you brought #t" + POWER_CRYSTAL + "#. I'll go ahead and make you #t" + INVESTIGATION_PERMIT + "# right now.");

        if (!sm.removeItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("Please make sure you have the Power Crystal.");
            return;
        }

        sm.addItem(INVESTIGATION_PERMIT, 1);
        sm.forceCompleteQuest(20308);
        sm.sayOk("This will give you access to every part of Ereve that is currently in code red. Please search through the island carefully and find the Master of Disguise that is responsible for this mess.");
    }

    @Script("q20309e")
    public static void q20309e(ScriptManager sm) {
        // Quest 20309 - Ereve Investigation Permit: Night Walker (END)
        final int POWER_CRYSTAL = 4005004;
        final int INVESTIGATION_PERMIT = 4032179;

        if (!sm.hasQuestStarted(20309)) {
            sm.sayNext("...#t" + INVESTIGATION_PERMIT + "#? I am sorry, but there's a problem with that. Truth be told, Ereve had never faced a crisis like this before, so naturally, I have never had to provide so many copies of Investigation Permits to various Knights. Yes, I am short on supplies needed to make the Investigation Permit.");
            sm.sayBoth("I have been quickly making new Investigation Permits, but that's also not easy in that... #t" + INVESTIGATION_PERMIT + "# is created using an item that is placed in specifically to prevent illegal copies from being made, so... yes, I am short on one item. The search needs to start right away, as well...");
            sm.sayBoth("Tell you what. I want you to bring me that item that is required to make the Investigation Permit. I know we don't have much time, but even the best Master of Disguise won't be able to escape Ereve that quickly, especially if the island is in code red as it is now. Once you bring me the item, I will go ahead and print you the Investigation Permit.");

            if (!sm.askYesNo("The item I need is 1 #t" + POWER_CRYSTAL + "#. The Investigation Permit created with the magic power of #t" + POWER_CRYSTAL + "# cannot be duplicated nor stolen, so it's perfect for this. Thanks in advance.")) {
                sm.sayNext("Oh no.. are you giving up on the search?");
                return;
            }

            sm.forceStartQuest(20309);
            sm.sayOk("Please hurry! Every second counts!");
            return;
        }

        if (!sm.hasItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("You have yet to bring #t" + POWER_CRYSTAL + "#. Every second counts, and we can't afford to lose any more time. Please hurry.");
            return;
        }

        sm.sayNext("Oh you brought #t" + POWER_CRYSTAL + "#. I'll go ahead and make you #t" + INVESTIGATION_PERMIT + "# right now.");

        if (!sm.removeItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("Please make sure you have the Power Crystal.");
            return;
        }

        sm.addItem(INVESTIGATION_PERMIT, 1);
        sm.forceCompleteQuest(20309);
        sm.sayOk("This will give you access to every part of Ereve that is currently in code red. Please search through the island carefully and find the Master of Disguise that is responsible for this mess.");
    }

    @Script("q20310e")
    public static void q20310e(ScriptManager sm) {
        // Quest 20310 - Ereve Investigation Permit: Thunder Breaker (END)
        final int POWER_CRYSTAL = 4005004;
        final int INVESTIGATION_PERMIT = 4032179;

        if (!sm.hasQuestStarted(20310)) {
            sm.sayNext("...#t" + INVESTIGATION_PERMIT + "#? I am sorry, but there's a problem with that. Truth be told, Ereve had never faced a crisis like this before, so naturally, I have never had to provide so many copies of Investigation Permits to various Knights. Yes, I am short on supplies needed to make the Investigation Permit.");
            sm.sayBoth("I have been quickly making new Investigation Permits, but that's also not easy in that... #t" + INVESTIGATION_PERMIT + "# is created using an item that is placed in specifically to prevent illegal copies from being made, so... yes, I am short on one item. The search needs to start right away, as well...");
            sm.sayBoth("Tell you what. I want you to bring me that item that is required to make the Investigation Permit. I know we don't have much time, but even the best Master of Disguise won't be able to escape Ereve that quickly, especially if the island is in code red as it is now. Once you bring me the item, I will go ahead and print you the Investigation Permit.");

            if (!sm.askYesNo("The item I need is 1 #t" + POWER_CRYSTAL + "#. The Investigation Permit created with the magic power of #t" + POWER_CRYSTAL + "# cannot be duplicated nor stolen, so it's perfect for this. Thanks in advance.")) {
                sm.sayNext("Oh no.. are you giving up on the search?");
                return;
            }

            sm.forceStartQuest(20310);
            sm.sayOk("Please hurry! Every second counts!");
            return;
        }

        if (!sm.hasItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("You have yet to bring #t" + POWER_CRYSTAL + "#. Every second counts, and we can't afford to lose any more time. Please hurry.");
            return;
        }

        sm.sayNext("Oh you brought #t" + POWER_CRYSTAL + "#. I'll go ahead and make you #t" + INVESTIGATION_PERMIT + "# right now.");

        if (!sm.removeItem(POWER_CRYSTAL, 1)) {
            sm.sayOk("Please make sure you have the Power Crystal.");
            return;
        }

        sm.addItem(INVESTIGATION_PERMIT, 1);
        sm.forceCompleteQuest(20310);
        sm.sayOk("This will give you access to every part of Ereve that is currently in code red. Please search through the island carefully and find the Master of Disguise that is responsible for this mess.");
    }

    // SHINSOO'S TEARDROP - FINAL ADVANCEMENT QUESTS (20311-20315) ----------------------------------------------------------------

    @Script("q20311s")
    public static void q20311s(ScriptManager sm) {
        // Quest 20311 - Shinsoo's Teardrop: Dawn Warrior (START & COMPLETE)
        // Chief Knight of Light (1101003) - Ereve
        sm.sayNext("Congratulations on recovering Shinsoo's Teardrop! This treasure is extremely important to Ereve and the Empress. Your bravery and skill in defeating #o9001009# have proven that you are ready for greater responsibilities.");
        sm.sayBoth("As a reward for your exceptional service, I am promoting you to the rank of #bAdvanced Knight#k. This is a great honor, and you have earned it through your dedication to the Cygnus Knights.");

        if (!sm.askYesNo("Are you ready to accept this promotion and become an Advanced Knight?")) {
            sm.sayNext("Think carefully about this decision. This is a significant step in your journey.");
            return;
        }

        sm.setJob(Job.DAWN_WARRIOR_3);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20311);

        sm.sayNext("You are now an Advanced Knight! As a Dawn Warrior of the third tier, you have access to powerful new abilities.");
        sm.sayBoth("I have given you #bSP#k to invest in your new skills. Train hard and continue to bring honor to the Cygnus Knights.");
        sm.sayPrev("Never forget that you are a protector of Ereve and a champion of the Empress. Your journey continues!");
    }

    @Script("q20312s")
    public static void q20312s(ScriptManager sm) {
        // Quest 20312 - Shinsoo's Teardrop: Blaze Wizard (START & COMPLETE)
        // Chief Knight of Fire (1101004) - Ereve
        sm.sayNext("Excellent work recovering Shinsoo's Teardrop! That was no easy task, and you handled it with the skill and courage of a true knight. The Black Wings won't soon forget this defeat!");
        sm.sayBoth("For your outstanding service to Ereve and your victory over #o9001009#, I am promoting you to the rank of #bAdvanced Knight#k. You have more than earned this honor!");

        if (!sm.askYesNo("Are you ready to become an Advanced Knight?")) {
            sm.sayNext("Take your time to prepare yourself for the responsibilities ahead.");
            return;
        }

        sm.setJob(Job.BLAZE_WIZARD_3);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20312);

        sm.sayNext("You are now an Advanced Knight! As a third-tier Blaze Wizard, the flames of your magic burn even brighter!");
        sm.sayBoth("I have given you #bSP#k to develop your new powers. Use them wisely and continue to serve the Empress with passion!");
        sm.sayPrev("Keep training and growing stronger. The Cygnus Knights are proud to have you among our ranks!");
    }

    @Script("q20313s")
    public static void q20313s(ScriptManager sm) {
        // Quest 20313 - Shinsoo's Teardrop: Wind Archer (START & COMPLETE)
        // Chief Knight of Wind (1101005) - Ereve
        sm.sayNext("You have done well... Recovering Shinsoo's Teardrop from the clutches of the Black Wings is no small feat. Your precision and determination were crucial to this success.");
        sm.sayBoth("In recognition of your exemplary service and your defeat of #o9001009#, I am promoting you to the rank of #bAdvanced Knight#k. You have shown that you are worthy of this honor.");

        if (!sm.askYesNo("Will you accept this promotion?")) {
            sm.sayNext("Prepare yourself when you are ready.");
            return;
        }

        sm.setJob(Job.WIND_ARCHER_3);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20313);

        sm.sayNext("You are now an Advanced Knight. As a third-tier Wind Archer, the wind itself will bend to your will.");
        sm.sayBoth("I have provided you with #bSP#k to enhance your abilities. Continue to refine your skills and serve the Empress well.");
        sm.sayPrev("May the wind always guide your arrows true. The Cygnus Knights are honored by your presence.");
    }

    @Script("q20314s")
    public static void q20314s(ScriptManager sm) {
        // Quest 20314 - Shinsoo's Teardrop: Night Walker (START & COMPLETE)
        // Chief Knight of Darkness (1101006) - Ereve
        sm.sayNext("Impressive... You managed to defeat #o9001009# and recover Shinsoo's Teardrop. That master of disguise didn't stand a chance against your skills.");
        sm.sayBoth("For your service to Ereve and your successful completion of this critical mission, I am promoting you to #bAdvanced Knight#k. You've earned it.");

        if (!sm.askYesNo("Ready to accept this promotion?")) {
            sm.sayNext("Consider your decision carefully.");
            return;
        }

        sm.setJob(Job.NIGHT_WALKER_3);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20314);

        sm.sayNext("You are now an Advanced Knight. As a third-tier Night Walker, the shadows are now truly yours to command.");
        sm.sayBoth("I have given you #bSP#k. Use it to unlock new powers that lurk in the darkness.");
        sm.sayPrev("Continue your training. The Empress watches your progress with approval.");
    }

    @Script("q20315s")
    public static void q20315s(ScriptManager sm) {
        // Quest 20315 - Shinsoo's Teardrop: Thunder Breaker (START & COMPLETE)
        // Chief Knight of Lightning (1101007) - Ereve
        sm.sayNext("YES! You did it! You defeated #o9001009# and got back Shinsoo's Teardrop! That was absolutely amazing! I knew you could do it!");
        sm.sayBoth("For your incredible bravery and your victory over the Black Wings, I'm promoting you to #bAdvanced Knight#k! You totally deserve it! Yeah!");

        if (!sm.askYesNo("Are you ready to become an Advanced Knight? Let's do this!")) {
            sm.sayNext("No worries! Let me know when you're ready for this awesome promotion!");
            return;
        }

        sm.setJob(Job.THUNDER_BREAKER_3);
        sm.addSp(1, 1);
        sm.forceCompleteQuest(20315);

        sm.sayNext("You're now an Advanced Knight! As a third-tier Thunder Breaker, lightning will strike with even more power when you fight!");
        sm.sayBoth("I've given you #bSP#k to power up your abilities! This is so exciting!");
        sm.sayPrev("Keep training and getting stronger! The Cygnus Knights are lucky to have someone as awesome as you! Yeah!");
    }

    // 4TH JOB ADVANCEMENT QUESTS (20400-20408) ----------------------------------------------------------------

    @Script("q20400s")
    public static void q20400s(ScriptManager sm) {
        // Quest 20400 - Chasing the Knight's Target (START)
        // Neinheart (1101002) - Ereve
        sm.sayNext("It's been a while since I last saw you. I can't even recognize you now, seeing how powerful you have become since our last meeting. I can honestly say that you just might be one of the most powerful Knights in all of Cygnus Knights, Chief Knights included. Okay, enough pleasantries. Let's get down to business.");

        if (!sm.askAccept("It's a new mission. According to the information we acquired, a member of the #rBlack Wings#k is targeting the Empress. In order to prevent that, Advanced Knight #b#p1103000##k has been secretly tracing that individual, but it doesn't look too good from here.")) {
            sm.sayNext("Hmmm... you seem way too at ease. It's a waste of talent and firepower for an accomplished individual like you to just sit around, being content with the way things are...");
            return;
        }

        sm.forceCompleteQuest(20400);
        sm.sayNext("If it's Victoria Island, at least we know everything that goes on there. This one's Ossyria, where not even the intelligence officials know everything inside out. This means the Advanced Knight will need help. Please provide help to #p1103000#. The last place she contacted was at #b#m211000000##k, so try looking for #p1103000#.");
        sm.sayOk("Well, I know I may have talked to you like this is a joke, but it is true that you are one of the better Knights, and that is why you are given a task with a huge responsibility. I'll be looking forward to your work.");
    }

    @Script("q20401s")
    public static void q20401s(ScriptManager sm) {
        // Quest 20401 - Hunting the Zombies (START)
        // NPC 2020006 - El Nath
        sm.sayNext("#p1103000#? Oh, you're looking for that Knight? I'm not entirely sure where #p1103000# is now, but I can confirm that he did stay at #m211000000# for quite some time.");
        sm.sayBoth("And while he was here, he was very busy #rhunting the zombies#k around this area. Not just regular hunting, mind you - he seemed to be searching for something specific among them, some kind of clue.");

        if (!sm.askYesNo("If you want to find out what #p1103000# was looking for, you should #rhunt some zombies yourself#k. Would you like to investigate the zombies around #m211000000#?")) {
            sm.sayOk("Come back if you change your mind. #p1103000# was definitely onto something with those zombies.");
            return;
        }

        sm.forceStartQuest(20401);
        sm.sayOk("Hunt #b50 zombies#k around #m211000000# and see if you can find what #p1103000# was looking for. Be careful - those zombies might be carrying something cursed!");
    }

    @Script("q20401e")
    public static void q20401e(ScriptManager sm) {
        // Quest 20401 - Hunting the Zombies (END)
        // NPC 2020006 - El Nath
        // Player should have killed zombies (we'll skip the kill check for now and just give the item)

        sm.sayNext("Did you find anything interesting while hunting the zombies?");
        sm.sayBoth("Wait... I can sense something strange from you. It's a dark, cursed energy...");

        // Give the Black Scale item
        if (!sm.addItem(4001207, 1)) {
            sm.sayOk("Your inventory is full! Please make space in your ETC tab.");
            return;
        }

        sm.forceCompleteQuest(20401);
        sm.sayNext("You found a #b#t4001207##k! That must be what #p1103000# was looking for!");
        sm.sayBoth("This scale is radiating with dark energy... You should take it to someone who knows about curses. I've heard that #b#p2032001# in Orbis#k is an expert on magical items.");
        sm.sayOk("Be careful with that #t4001207#. It seems very dangerous! Take it to #b#p2032001# in #m200000000##k right away!");
    }

    @Script("q20402s")
    public static void q20402s(ScriptManager sm) {
        // Quest 20402 - Black Scale (START - auto-triggered when obtaining item 4001207)
        // NPC 2032001 - Orbis
        final int BLACK_SCALE = 4001207;

        sm.sayNext("I feel a strange aura emitting from you. Do you by any chance have #b#t" + BLACK_SCALE + "##k with you? Recently, a number of cursed #t" + BLACK_SCALE + "#s have been appearing all over the world, and I can feel the same thing from you. It's a very dangerous item, so would you mind giving it to me?");

        if (!sm.askYesNo("Where are you located? Hmmm... the fact that it feels close makes me suspect that you might be around #m211000000#. It's not too far from here, so I want you to go to #b#m200000000##k and hand me #t" + BLACK_SCALE + "#. #p1103000# had that too, and... it seems like #t" + BLACK_SCALE + "#s are easily found in #m211000000#...")) {
            sm.sayNext("You should know that as long as you hold on to it, you will be affected by it as well, from a higher percentage of scroll failure and lower drop rate to item decomposition. You may even lose hair as well. Are you sure you want to hold on to it?");
            return;
        }

        sm.forceStartQuest(20402);
        sm.sayOk("Please bring me the #t" + BLACK_SCALE + "#. It's for your own safety!");
    }

    @Script("q20402e")
    public static void q20402e(ScriptManager sm) {
        // Quest 20402 - Black Scale (END)
        // NPC 2032001 - Orbis
        final int BLACK_SCALE = 4001207;

        if (!sm.hasItem(BLACK_SCALE, 1)) {
            sm.sayOk("Hmmm...? You didn't bring #b#t" + BLACK_SCALE + "##k with you? Please gather up the #t" + BLACK_SCALE + "#s that are laden with the power of curse.");
            return;
        }

        sm.sayNext("Oh, so you did bring #t" + BLACK_SCALE + "#. Good work. It's a dangerous item, so as soon as I feel it, I make sure to contact that individual and have that person bring it to me. Thankfully, most of the people I contacted promptly came and gave them to me, so it's not that difficult... the problem is that the level of curse seems quite powerful.");

        if (sm.askMenu("What happened?", Map.of(0, "Did you... take one from #p1103000#, by chance?")) != 0) {
            return;
        }

        sm.sayBoth("#p1103000#? Are you talking about that Knight #p1103000#? Yes, he brought a whole bunch of #t" + BLACK_SCALE + "#s acquired from zombies. He wasn't satisfied with just retrieving those items, but he was rather interested in finding the origin so he could snuff them out. That's why he started asking me about the information on #t" + BLACK_SCALE + "#.");

        if (sm.askMenu("Can you tell me more?", Map.of(0, "Can you tell me what you told #p1103000#?")) != 0) {
            return;
        }

        sm.sayBoth("It was really nothing special. #t" + BLACK_SCALE + "# may look small on the outside, but it's really a scale of a dragon. Most dragon scales reflect magic, but since the curse is so deeply ingrained, I told him I suspect #bthese scales are from a special dragon#k.");

        if (!sm.askYesNo("Apparently, that was enough for #p1103000# to pack up and leave, and told me he'll be heading over to #b#m240000000##k and find out more... about the scales. Since #m240000000# is the land where dragons and Halfrings coexist, there's got to be more information there.")) {
            return;
        }

        sm.removeItem(BLACK_SCALE, 1);
        sm.forceCompleteQuest(20402);
        sm.sayOk("Good luck finding #p1103000# in #m240000000#!");
    }

    @Script("q20403s")
    public static void q20403s(ScriptManager sm) {
        // Quest 20403 - Dragon Outcasts (START)
        // NPC 2081000 - Leafre
        sm.sayNext("Hmm...? #p1103000#? Of course I remember. Aren't you talking about the Knight that planned on investigating #t4001207#? He seemed very much a proper gentleman who fits the bill of a knight. Do you want to know where he went?");

        if (!sm.askYesNo("When I told #p1103000# the #t4001207#s he showed me were similar to that of #bthe Dragon Outcasts#k, he took off in hopes of finding the Dragon Outcasts. The house of the Dragon Outcasts can only be accessed through #b#m240020401# or #m240020101#, so be careful#k.")) {
            sm.sayNext("Hmmm... I wonder what you're really looking for. Maybe my hearing has failed me, but... I don't know if your answer means yes or no.");
            return;
        }

        sm.forceCompleteQuest(20403);
        sm.sayNext("Afterwards, I was never able to see #p1103000# again. I wonder if he was captured by the Dragon Outcasts. Recently, there have been reports of #rtheft on the dragon's eggs#k... Hopefully he's not involved in that.");
    }

    @Script("q20404s")
    public static void q20404s(ScriptManager sm) {
        // Quest 20404 - The Stolen Egg (START)
        // NPC 2081012 - Dragon Outcast
        sm.sayNext("Who are you? Who told you you're welcome here? Don't be barging in on someone else's important research!");

        if (sm.askMenu("I'm looking for someone...", Map.of(0, "Hi, did #p1103000# stop by anytime recently?")) != 0) {
            return;
        }

        sm.sayBoth("#p1103000#? That blonde knight? Don't tell me you're one of them, too. Ahhh screw it. Screw it!!!");

        if (!sm.askYesNo("#t4001207# is trying to tell me something, but I have no idea what it's saying. I #bhate being in strange, dangerous areas, so I stay in this forest at all times. How are my scales supposedly out there being passed around?#k Are you looking down on me because I'm a Halfling?")) {
            sm.sayNext("Then get out of here! Why are there so many unruly people barging into people's houses?");
            return;
        }

        sm.forceStartQuest(20404);
        sm.sayNext("Seriously, that old man Tatamo! I can see why he wouldn't like me, but that doesn't give him a right to make baseless assumptions! He should spend that time #rlooking for #o9001010#, the person responsible for stealing an egg#k...");
        sm.sayOk("No I won't tell you more! Why should I do it? If you really want to know, then you better do something for me in return! I had been looking for a top-tier talent for this, anyway... I want you to go out there and eliminate all the #r#o8180000#s#k and #r#o8180001#s#k. You can do this, right?");
    }

    @Script("q20404e")
    public static void q20404e(ScriptManager sm) {
        // Quest 20404 - The Stolen Egg (END)
        // NPC 2081012 - Dragon Outcast

        sm.sayNext("Ohh... I really wasn't expecting this, but you did indeed defeat all the #o8180000#s and #o8180001#s. Hey... you're much stronger than I thought. I mean, I could have gone out there and wiped out the #o8180000#s and #o8180001#s myself, but... that was good work. Not bad for a human.");

        if (!sm.askYesNo("#o9001010#? She's a lady that lives way deep in the area near the #rDragon's Nest#k, and... she's one scary woman. Apparently, she handles all those powerful monsters around Dragon's Nest with her eyes closed. Even scarier than that...")) {
            return;
        }

        sm.sayBoth("...I overheard a number of Kentauruses gossiping around, and... they think she's the #rone responsible for the Egg-Theft#k. I don't know what she's trying to do with those eggs, but I don't think it'll be for good use. She just seems very dangerous...");

        sm.forceCompleteQuest(20404);
        sm.sayOk("Okay, the information stops here. I don't really like you, but since you helped me here, I'll give you an advice. I strongly advise you not to get too close to #o9001010#. You don't want to risk your life being associated with someone like #o9001010#. Maybe that gentleman #r#p1103000# might have been swept by that, as well#k.");
    }

    @Script("q20405s")
    public static void q20405s(ScriptManager sm) {
        // Quest 20405 - The Cave of the Black Witch (START & COMPLETE)
        // NPC 2081013 - Black Witch's Cave
        sm.sayNext("(You see a note posted on the wall of the cave. It reads...)");
        sm.sayBoth("'To whoever finds this note: I have been investigating the source of the cursed scales that have been plaguing our world. The trail has led me to this cave, the lair of #o9001010#, the Black Witch.'");
        sm.sayBoth("'After much investigation, I have discovered a device here that appears to be the source of the curse. I have recovered it and am sending it back to Ereve for safekeeping. The Black Witch herself was not here, but I fear she may be planning something even more sinister.'");
        sm.sayBoth("'I will continue my investigation and report back to Ereve when I have more information. - Advanced Knight #p1103000#'");

        sm.forceCompleteQuest(20405);
        sm.sayOk("(It seems #p1103000# has already completed his mission here. You should return to Ereve and report to #b#p1101002##k.)");
    }

    // NOTE: Cygnus Knights in v95 do NOT have 4th job advancement
    // They cap at level 120 as 3rd job (Advanced Knights)
    // Quests 20406-20408 (4th job chain) are disabled for v95 authenticity

    @Script("q20520s")
    public static void q20520s(ScriptManager sm) {
        // Knight's Dignity (20520 - start)
        // Level 50 auto-start quest about Monster Mounts for Cygnus Knights
        sm.sayNext("Ah, you've reached Level 50. Congratulations on your achievement! You are now a formidable knight, and it's time we address something important.");
        sm.sayBoth("As a Level 50 Cygnus Knight, it seems beneath your rank to simply walk everywhere. A knight of your stature should have a proper mount to ride.");
        sm.sayBoth("I have information about #bMonster Mounts#k that may interest you. There are special mounts available exclusively for Cygnus Knights like yourself.");
        sm.sayBoth("Head to #bEreve#k and speak with the appropriate trainers. They will guide you on how to obtain a mount befitting your status as a knight of the Empress!");

        sm.forceStartQuest(20520);
    }

    @Script("oldBook5")
    public static void oldBook5(ScriptManager sm) {
        // NPC: Spiruna (2032001) - Refines Dark Crystal Ore into Power Crystal
        // Used for Cygnus Knights 3rd job advancement quest (20306-20310)
        final int DARK_CRYSTAL_ORE = 4004004;
        final int POWER_CRYSTAL = 4005004;

        sm.sayNext("Ah, you have some #bDark Crystal Ore#k with you. I can sense the dark energy emanating from it...");
        sm.sayBoth("These ores contain powerful magical energy, corrupted by evil forces. I can refine them into #bPower Crystals#k, purifying the darkness and extracting the raw magical essence.");

        if (!sm.hasItem(DARK_CRYSTAL_ORE, 1)) {
            sm.sayBoth("However, I don't see any Dark Crystal Ore in your possession. You can obtain these ores from monsters corrupted by dark powers, such as the #bDrum Bunnies#k at the Eos Tower.");
            return;
        }

        sm.sayBoth("I see you have the ore. Would you like me to refine it into a Power Crystal? I'll need #b1 Dark Crystal Ore#k for the refinement process.");

        if (sm.askYesNo("Exchange #b1 Dark Crystal Ore#k for #b1 Power Crystal#k?")) {
            if (sm.removeItem(DARK_CRYSTAL_ORE, 1)) {
                if (sm.addItem(POWER_CRYSTAL, 1)) {
                    sm.sayNext("The refinement is complete! The dark energy has been purified, leaving behind a pristine #bPower Crystal#k filled with magical energy.");
                    sm.sayBoth("Use this crystal wisely. Its power is immense and should only be used for noble purposes.");
                } else {
                    sm.sayNext("It seems your inventory is full. Please make some space and come back.");
                    sm.addItem(DARK_CRYSTAL_ORE, 1); // Give the ore back
                }
            } else {
                sm.sayNext("It appears you no longer have the Dark Crystal Ore. Please come back when you have it.");
            }
        } else {
            sm.sayNext("Very well. If you change your mind, come back and see me.");
        }
    }

    // ==============================================================================================================
    // BAROQ FAKE KNIGHT NPCs (3rd Job Advancement Instance Maps)
    // ==============================================================================================================

    @Script("1104100")
    public static void npc1104100(ScriptManager sm) {
        baroqNpcDawnWarrior(sm);
    }

    @Script("desguiseSoul")
    public static void desguiseSoul(ScriptManager sm) {
        baroqNpcDawnWarrior(sm);
    }

    private static void baroqNpcDawnWarrior(ScriptManager sm) {
        // NPC 1104100 (Mihile) - Fake Knight for Dawn Warrior
        final int BAROQ_MOB = 9001009;

        if (sm.getJob() != 1110) {
            // Wrong job - show innocent dialog
            sm.sayOk("What's going on? How's the search? The Master of Disguise was not found in this area. I'll stay here and be on the lookout, so you can search other areas instead.");
        } else {
            // Dawn Warrior found the correct NPC - reveal as Baroq!
            sm.sayNext("Darn, you found me! Then there's only one way out! Let's fight, like #rBlack Wings#k should!");
            // Remove this NPC and spawn Baroq boss
            sm.removeNpc(sm.getSpeakerId());
            sm.spawnMob(BAROQ_MOB, -3, 0, 0, false);
            sm.broadcastMessage("Baroq has revealed himself! Defeat him and recover Shinsoo's Teardrop!");
        }
    }

    @Script("1104101")
    public static void npc1104101(ScriptManager sm) {
        baroqNpcBlazeWizard(sm);
    }

    @Script("desguiseFlame")
    public static void desguiseFlame(ScriptManager sm) {
        baroqNpcBlazeWizard(sm);
    }

    private static void baroqNpcBlazeWizard(ScriptManager sm) {
        // NPC 1104101 (Oz) - Fake Knight for Blaze Wizard
        final int BAROQ_MOB = 9001009;

        if (sm.getJob() != 1210) {
            // Wrong job - show innocent dialog
            sm.sayOk("How is the search going? I don't see anything suspicious around the area. I'll keep looking, so please search other areas as well.");
        } else {
            // Blaze Wizard found the correct NPC - reveal as Baroq!
            sm.sayNext("Darn, you found me! Then there's only one way out! Let's fight, like #rBlack Wings#k should!");
            // Remove this NPC and spawn Baroq boss
            sm.removeNpc(sm.getSpeakerId());
            sm.spawnMob(BAROQ_MOB, -3, 0, 0, false);
            sm.broadcastMessage("Baroq has revealed himself! Defeat him and recover Shinsoo's Teardrop!");
        }
    }

    @Script("1104102")
    public static void npc1104102(ScriptManager sm) {
        baroqNpcWindArcher(sm);
    }

    @Script("desguiseWind")
    public static void desguiseWind(ScriptManager sm) {
        baroqNpcWindArcher(sm);
    }

    private static void baroqNpcWindArcher(ScriptManager sm) {
        // NPC 1104102 (Irina) - Fake Knight for Wind Archer
        final int BAROQ_MOB = 9001009;

        if (sm.getJob() != 1310) {
            // Wrong job - show innocent dialog
            sm.sayOk("How's the search? I don't see anything peculiar around the area. I'll keep my eye on the area, so I want you to search other areas as well.");
        } else {
            // Wind Archer found the correct NPC - reveal as Baroq!
            sm.sayNext("Darn, you found me! Then there's only one way out! Let's fight, like #rBlack Wings#k should!");
            // Remove this NPC and spawn Baroq boss
            sm.removeNpc(sm.getSpeakerId());
            sm.spawnMob(BAROQ_MOB, -3, 0, 0, false);
            sm.broadcastMessage("Baroq has revealed himself! Defeat him and recover Shinsoo's Teardrop!");
        }
    }

    @Script("1104103")
    public static void npc1104103(ScriptManager sm) {
        baroqNpcNightWalker(sm);
    }

    @Script("desguiseNight")
    public static void desguiseNight(ScriptManager sm) {
        baroqNpcNightWalker(sm);
    }

    private static void baroqNpcNightWalker(ScriptManager sm) {
        // NPC 1104103 (Eckart) - Fake Knight for Night Walker
        final int BAROQ_MOB = 9001009;

        if (sm.getJob() != 1410) {
            // Wrong job - show innocent dialog
            sm.sayOk("How's the search? I don't see anything different here. I'll stay here and keep looking, so you can search other areas.");
        } else {
            // Night Walker found the correct NPC - reveal as Baroq!
            sm.sayNext("Darn, you found me! Then there's only one way out! Let's fight, like #rBlack Wings#k should!");
            // Remove this NPC and spawn Baroq boss
            sm.removeNpc(sm.getSpeakerId());
            sm.spawnMob(BAROQ_MOB, -3, 0, 0, false);
            sm.broadcastMessage("Baroq has revealed himself! Defeat him and recover Shinsoo's Teardrop!");
        }
    }

    @Script("1104104")
    public static void npc1104104(ScriptManager sm) {
        baroqNpcThunderBreaker(sm);
    }

    @Script("desguiseStrike")
    public static void desguiseStrike(ScriptManager sm) {
        baroqNpcThunderBreaker(sm);
    }

    private static void baroqNpcThunderBreaker(ScriptManager sm) {
        // NPC 1104104 (Hawkeye) - Fake Knight for Thunder Breaker
        final int BAROQ_MOB = 9001009;

        if (sm.getJob() != 1510) {
            // Wrong job - show innocent dialog
            sm.sayOk("How's the search? I don't see anything suspicious here, but who knows?");
        } else {
            // Thunder Breaker found the correct NPC - reveal as Baroq!
            sm.sayNext("Oh... did I just get found? Then there's only one way out! Let's fight, like a #rBlack Wing#k should!");
            // Remove this NPC and spawn Baroq boss
            sm.removeNpc(sm.getSpeakerId());
            sm.spawnMob(BAROQ_MOB, -3, 0, 0, false);
            sm.broadcastMessage("Baroq has revealed himself! Defeat him and recover Shinsoo's Teardrop!");
        }
    }

    // ==============================================================================================================
    // BLACK WITCH NPC (4TH JOB BOSS) - NPC 1104002
    // ==============================================================================================================

    @Script("1104002")
    public static void npc1104002(ScriptManager sm) {
        // NPC 1104002 - Eleanor / Black Witch (disguised as innocent lady)
        // When player talks to her, she reveals herself and spawns the boss
        final int BLACK_WITCH_NPC = 1104002;
        final int BLACK_WITCH_MOB = 9001010;

        if (!sm.hasQuestStarted(20407)) {
            // Quest not started yet - show innocent dialog
            sm.sayOk("...");
            return;
        }

        // Player has started Quest 20407 - reveal as Black Witch!
        sm.sayNext("Foolish knight... you've walked right into my trap! Did you really think you could stop me from cursing Ereve?");
        sm.sayBoth("The Empress and all of Ereve will fall to my curse! But first, I'll deal with you personally!");

        // Remove this NPC and spawn Black Witch boss
        sm.removeNpc(BLACK_WITCH_NPC);
        sm.spawnMob(BLACK_WITCH_MOB, -3, 0, 0, false);
        sm.broadcastMessage("The Black Witch has revealed her true form! Defeat her to save Ereve!");
    }

    // ==============================================================================================================
    // CYGNUS MOUNT QUESTS (Quest 20522 - Raising Mimiana)
    // ==============================================================================================================

    @Script("q20522s")
    public static void q20522s(ScriptManager sm) {
        // Quest 20522 - Raising Mimiana (START)
        // NPC 1102002 - Mount Trainer
        sm.sayNext("Welcome! I see you've completed raising the #t1902005# egg. That's great! However, just having a hatched #t1902005# isn't enough to ride it yet.");
        sm.sayBoth("A #t1902005# needs to be fully grown and trained before it can be mounted. The way to do this is by #bsharing your experiences with it#k. As you gain experience and level up, the #t1902005# will grow stronger alongside you.");

        if (!sm.askYesNo("Are you ready to take on the responsibility of raising your #t1902005#?")) {
            sm.sayNext("Come back when you're ready to commit to raising your #t1902005#.");
            return;
        }

        sm.forceStartQuest(20522);
        sm.sayOk("Excellent! Take good care of your #t1902005#. Feed it your experiences, and it will grow into a loyal companion. Come back to me once it's fully grown!");
    }

    @Script("q20522e")
    public static void q20522e(ScriptManager sm) {
        // Quest 20522 - Raising Mimiana (END)
        // NPC 1102002 - Mount Trainer
        sm.sayNext("Oh! I can see your #t1902005# has grown quite a bit! It looks healthy and strong. You've done a great job raising it!");
        sm.sayBoth("However, before you can ride it, you'll need to make it even stronger. You'll need special supplements from #b#p2060005##k in #bAqua Road#k.");

        if (!sm.askYesNo("Are you ready to take the next step and get the supplements?")) {
            sm.sayNext("Come back when you're ready to continue.");
            return;
        }

        sm.forceCompleteQuest(20522);
        sm.sayOk("Go to #b#m230000000##k and purchase the supplements from #b#p2060005##k. They're expensive, but necessary for your #t1902005# to become strong enough to ride!");
    }

    @Script("giveupRiding")
    public static void giveupRiding(ScriptManager sm) {
        // NPC script for giving up/abandoning mount training
        // NPC 1102002 - Mount Trainer
        sm.sayNext("You want to give up on raising your mount? Are you sure about this? All the time and effort you've put in will be lost...");

        if (!sm.askYesNo("Do you really want to #rgive up#k on your mount training?")) {
            sm.sayOk("Good! I knew you wouldn't give up that easily. Keep training your mount!");
            return;
        }

        sm.sayNext("I understand... Sometimes the journey is too difficult. If you change your mind in the future, you can always start over.");
        sm.sayOk("Your mount training has been cancelled. Come back if you want to try again.");
    }

    @Script("q20525s")
    public static void q20525s(ScriptManager sm) {
        // Quest 20525 - Making a Saddle (START)
        // NPC 1102002 - Mount Trainer (Ereve)
        // Recovery quest for lost Monster Mount Saddle
        sm.sayNext("What is it? Hmmm...? You lost #t1912005#? Hmmm... that's not good. You can't Mount without #t1912005#. Hmmm? You want another one? Well... we have a tight budget right now, and we cannot give out extra #t1912005#s to those who've lost theirs. There are other knights out there... and as you know, #p1101002# is not one to extend budget on something like this...");
        sm.sayBoth("But, that doesn't mean there's absolutely no way you can get another #t1912005#. You should make one yourself, no? All #t1912005#s are custom-made by #b#p2060005##k of #b#m230000000##k, so if you give him the materials for #t1912005# along with some money, you'll be able to get yourself a new #t1912005#. It won't be cheap by any means, but... that's the only way you'll be able to get #t1912005#.");

        if (!sm.askYesNo("Are you willing to gather the materials and pay the fee to remake your saddle?")) {
            sm.sayOk("Hmmm... that's unfortunate. You'll just have to use #t1902005# as an accessory.");
            return;
        }

        sm.forceStartQuest(20525);
        sm.sayOk("Bring #b200 #t4000030#s#k, #b200 #t4000055#s#k, #b200 #t4000171#s#k... and #b5 million mesos#k to #p2060005#, and you'll be able to get yourself a new #t1912005#. Best of luck to you.");
    }

    @Script("q20525e")
    public static void q20525e(ScriptManager sm) {
        // Quest 20525 - Making a Saddle (END)
        // NPC 2060005 (Maker in Aqua Road)
        // Requires: 200 Dragon Skin, 200 Soft Feather, 200 Leather, 5,000,000 mesos

        if (!sm.hasItem(4000030, 200) || !sm.hasItem(4000055, 200) || !sm.hasItem(4000171, 200) || !sm.canAddMoney(-5000000)) {
            sm.sayOk("Hmm... what is it? You don't look too happy. Wait, you have #t1902005#, but you don't have #t1912005# with you.");
            return;
        }

        sm.sayNext("Hmmm? What is it that you need? I see that you're carrying a hefty amount of leather... WHAT? You want me to build a new #t1912005#? That won't be too difficult, but I have other work to do, so... I can't build one for you for free. What? You brought some money as well? In that case, I'll try my best to make one as soon as possible. Just give me the materials first!");

        if (!sm.askYesNo("Will you pay 5 million mesos and provide the materials to craft a new #t1912005#?")) {
            sm.sayOk("Come back when you have everything ready.");
            return;
        }

        // Remove items and money
        sm.addMoney(-5000000);
        sm.removeItem(4000030, 200); // Dragon Skin
        sm.removeItem(4000055, 200); // Soft Feather
        sm.removeItem(4000171, 200); // Leather

        sm.forceCompleteQuest(20525);
        sm.addItem(1912005, 1); // Monster Mount Saddle
        sm.sayOk("Here's #t1912005#! Hope to see you again!");
    }

    @Script("q20527s")
    public static void q20527s(ScriptManager sm) {
        // Quest 20527 - A Knight's Pride (START)
        // Auto-start quest at level 100 - NPC 1101002 (Neinheart)
        sm.sayNext("#h0#. You've reached Level 100 and have become a formidable Knight. However, I've noticed you're still riding a regular #t1902005#...");
        sm.sayBoth("As a high-ranked Knight of the Cygnus Order, you should be riding something more befitting your status. Have you heard of the #bMonster Mount#k?");

        if (!sm.askYesNo("The Monster Mount is far more powerful than your current #t1902005#. Would you like to learn more about upgrading your mount?")) {
            sm.sayNext("I see. Well, when you're ready to upgrade, speak with me again.");
            return;
        }

        sm.forceStartQuest(20527);
        sm.sayOk("Excellent! Go speak with #b#p1102002# the Mount Trainer#k. He knows all about enhancing mounts and can help you transform your #t1902005# into a powerful #bMonster Mount#k!");
    }

    @Script("q20528s")
    public static void q20528s(ScriptManager sm) {
        // Quest 20528 - Raising Mimio (START)
        // NPC 1102002 - Mount Trainer
        sm.sayNext("Ah, #h0#! I heard from #p1101002# that you want to upgrade your #t1902005#. That's wonderful!");
        sm.sayBoth("#t1902005#, with an extensive amount of experience stored in it, will become a much more powerful creature through shedding. But that will also require some food that is much more nutritious and potent than the ones that are available.");
        sm.sayBoth("If you want to see if your #t1902005# can become as powerful as a dragon, how about feeding it a #bspecial formula used for the dragons of #m240000000##k?");

        if (!sm.askYesNo("Are you interested in having your #t1902005# undergo shedding to become a #bMonster Mount#k?")) {
            sm.sayNext("Hmmm... I take it that you are not interested in having your #t1902005# shed.");
            return;
        }

        sm.forceStartQuest(20528);
        sm.sayNext("I want you to bring back the Concentrated Formula that #bPam#k makes. Formulas should be fed in different levels, and you'll need to bring #bStep 1, 2, and 3 Concentrated Formulas, 3 each#k.");
        sm.sayOk("I'll be waiting. Go to #b#m240000000##k and purchase the formulas from #bPam#k!");
    }

    @Script("q20528e")
    public static void q20528e(ScriptManager sm) {
        // Quest 20528 - Raising Mimio (END)
        // NPC 1102002 - Mount Trainer
        // Requires: 3x Step 1 Formula (4032196), 3x Step 2 Formula (4032197), 3x Step 3 Formula (4032198)

        if (!sm.hasItem(4032196, 3) || !sm.hasItem(4032197, 3) || !sm.hasItem(4032198, 3) || !sm.hasItem(1902005, 1)) {
            sm.sayOk("Hmmm... I don't think you have brought the ingredients ready for shedding. #bPam#k of #b#m240000000##k is famous for making #bConcentrated Formulas#k for the #t1902005#s, and... I want you to purchase #b3 concentrated formulas from each step#k, then bring them back along with your #t1902005#.");
            return;
        }

        sm.sayNext("Oh, so you were able to bring in all the Formulas. Now, let's go ahead and have #t1902005# undergo shedding. First, we'll feed Formula: Step 1... then Step 2... then Step 3...");

        if (!sm.askYesNo("Are you ready to transform your #t1902005# into a #bMonster Mount#k?")) {
            sm.sayOk("Come back when you're ready!");
            return;
        }

        // Remove formulas and old mount, give new mount
        if (!sm.removeItem(4032196, 3) || !sm.removeItem(4032197, 3) || !sm.removeItem(4032198, 3) || !sm.removeItem(1902005, 1)) {
            sm.sayOk("Something went wrong. Please make sure you have all the required items.");
            return;
        }

        if (!sm.addItem(1902006, 1)) {
            sm.sayOk("Please make sure you have space in your inventory.");
            return;
        }

        sm.forceCompleteQuest(20528);
        sm.sayOk("That's some shedding there. Wait, it's not #t1902005# anymore, but #t1902006#! #t1902006# will be a much more formidable and trustworthy companion for you as you travel your way around the world. Happy Mounting!");
    }

    // ==============================================================================================================
    // CYGNUS SKILL QUESTS (Level 100 and 110)
    // ==============================================================================================================

    @Script("q20600s")
    public static void q20600s(ScriptManager sm) {
        // Quest 20600 - Training Never Ends (Level 100 Skill Quest)
        // NPC 1101002 - Neinheart
        sm.sayNext("#h0#. Have you been slacking off in training since reaching Level 100? We all know how powerful you are, but the training is not complete. Take a look at these Chief Knights. They train day and night, preparing themselves for the possible encounter with the Black Mage.");
        sm.sayBoth("I suggest you visit other Chief Knights and ask for some words of advice. Who knows? You may be able to learn a #bnew skill#k in the process.");

        sm.forceCompleteQuest(20600);
    }

    @Script("q20610s")
    public static void q20610s(ScriptManager sm) {
        // Quest 20610 - Training Still Never Ends (Level 110 Skill Quest)
        // NPC 1101002 - Neinheart
        sm.sayNext("Have you been mastering your skills? I am sure you've mastered all your skills, which means... it's time for you to learn a #bnew skill#k, right?");
        sm.sayBoth("#bChief Knights#k must have come up with another skill. Don't just stand here, find a way to learn that skill for yourself! I am sure the Chief Knights will be against it, but... acquiring the skill will depend purely on your abilities.");

        sm.forceCompleteQuest(20610);
    }

    // ==============================================================================================================
    // CYGNUS LEVEL 120 NPC SCRIPT
    // ==============================================================================================================

    @Script("cygnus_lv120")
    public static void cygnus_lv120(ScriptManager sm) {
        // Generic NPC script for level 120 Cygnus Knights content
        // This handles NPCs that appear at level 120 milestone
        sm.sayNext("Congratulations on reaching level 120! You have proven yourself to be a true champion of the Empress.");
        sm.sayBoth("At this level, you are one of the elite knights of Ereve. Continue to bring honor to the Cygnus Knights!");
        sm.sayOk("If you have any quests related to your advancement, please speak with #b#p1101002# Neinheart#k or your Chief Knight instructor.");
    }

}
