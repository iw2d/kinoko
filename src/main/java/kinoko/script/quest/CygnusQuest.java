package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.item.InventoryType;
import kinoko.world.job.Job;

import java.util.List;

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

    @Script("enterDisguise3")
    public static void enterDisguise3(ScriptManager sm) {
        // Empress' Road : Training Forest II (130010100)
        //   in00 (-1402, -338)
        sm.playPortalSE();
        sm.warp(130010110, "out00"); // Empress' Road : Timu's Forest
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
}
