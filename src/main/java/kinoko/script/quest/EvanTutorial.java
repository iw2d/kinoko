package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;

public final class EvanTutorial extends ScriptHandler {
    @Script("evanAlone")
    public static void evanAlone(ScriptManager sm) {
        // Dream World : Dream Forest Entrance (900010000)
        // Hidden Street : Lush Forest (900020100)

    }

    @Script("evantalk00")
    public static void evantalk00(ScriptManager sm) {
        // Dream World : Dream Forest Entrance (900010000)
        //   scr00 (-996, -11)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo00=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "mo00=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon00");
    }

    @Script("mirtalk00")
    public static void mirtalk00(ScriptManager sm) {
        // Dream World : Dream Forest Entrance (900010000)
        //   scr01 (-411, -10)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "dt00=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;mo00=o");
        sm.screenEffect("evan/dragonTalk00");
    }

    @Script("evantalk01")
    public static void evantalk01(ScriptManager sm) {
        // Dream World : Dream Forest Entrance (900010000)
        //   scr02 (-215, -10)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo01=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;mo00=o;mo01=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon01");
    }

    @Script("evantalk02")
    public static void evantalk02(ScriptManager sm) {
        // Dream World : Dream Forest Entrance (900010000)
        //   scr03 (298, -6)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo02=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;mo00=o;mo01=o;mo02=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon02");
    }

    @Script("evantalk10")
    public static void evantalk10(ScriptManager sm) {
        // Dream World : Dream Forest Trail  (900010100)
        //   scr00 (410, -9)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo10=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;mo00=o;mo01=o;mo10=0;mo02=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon10");
    }

    @Script("mirtalk01")
    public static void mirtalk01(ScriptManager sm) {
        // Dream World : Dream Forest Trail  (900010100)
        //   scr01 (691, -10)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "dt01=o")) {
            return;
        }
        sm.screenEffect("evan/dragonTalk01");
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o");
    }

    @Script("evantalk11")
    public static void evantalk11(ScriptManager sm) {
        // Dream World : Dream Forest Trail  (900010100)
        //   scr02 (928, -12)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo11=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon11");
    }

    @Script("contactDragon")
    public static void contactDragon(ScriptManager sm) {
        // Dream World : Dream Forest Trail  (900010100)
        //   in00 (1523, 36)
        sm.playPortalSE();
        sm.warp(900090100); // Video : Tutorial 0
    }

    @Script("meetWithDragon")
    public static void meetWithDragon(ScriptManager sm) {
        // Video : Tutorial 0  (900090100)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction4.img/meetWithDragon/Scene" + sm.getGender());
        sm.setDirectionMode(false, 6000);
    }

    @Script("evantalk20")
    public static void evantalk20(ScriptManager sm) {
        // Dream World : Dream Forest  (900010200)
        //   scr00 (-1780, -8)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo20=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o;mo20=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon20");
    }

    @Script("evantalk21")
    public static void evantalk21(ScriptManager sm) {
        // Dream World : Dream Forest  (900010200)
        //   scr01 (-1154, -10)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "mo21=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o;mo20=o;mo21=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon21");
    }

    @Script("dragoneyes")
    public static void dragoneyes(ScriptManager sm) {
        // Dream World : Dream Forest  (900010200)
        //   scr02 (-661, -12)
        sm.setQRValue(QuestRecordType.EvanDragonEyes, "1");
    }

    @Script("dragon_dream")
    public static void dragon_dream(ScriptManager sm) {
        // Dragon (1013001)
        //   Dream World : Dream Forest  (900010200)
        sm.sayNext("You, who are destined to be a Dragon Master... You have finally arrived.");
        sm.sayBoth("Go and fulfill your duties as the Dragon Master...");
        sm.warp(900090101); // Video : Tutorial 1
    }

    @Script("PromiseDragon")
    public static void PromiseDragon(ScriptManager sm) {
        // Video : Tutorial 1  (900090101)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction4.img/PromiseDragon/Scene0");
        sm.setDirectionMode(false, 4000);
    }

    @Script("evanleaveD")
    public static void evanleaveD(ScriptManager sm) {
        // Utah's House : Small Attic (100030100)
        // Dream World : Dream Forest  (900010200)
        // Hidden Street : Lost Forest Entrance  (900020200)
        sm.setDirectionMode(false, 0);
    }

    @Script("evanRoom0")
    public static void evanRoom0(ScriptManager sm) {
        // Utah's House : Small Attic (100030100)
        //   tutor00 (4, -115)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon30");
    }

    @Script("evanRoom1")
    public static void evanRoom1(ScriptManager sm) {
        // Utah's House : Small Attic (100030100)
        //   tutor01 (221, -13)
        if (sm.hasQRValue(QuestRecordType.EvanDreamEffect, "hand=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanDreamEffect, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo20=o;hand=o;mo21=o");
        sm.sayImage(List.of("UI/tutorial/evan/0/0"));
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon70");
    }

    @Script("evanlivingRoom")
    public static void evanlivingRoom(ScriptManager sm) {
        // Utah's House : Living Room (100030101)
        //   out00 (373, 33)
        sm.playPortalSE();
        sm.warp(100030102, "in00"); // Utah's Farm : Front Yard
    }

    @Script("evanGarden0")
    public static void evanGarden0(ScriptManager sm) {
        // Utah's House : Front Yard (100030102)
        //   west00 (-2234, 37)
        sm.playPortalSE();
        sm.warp(100030200, "east00"); // Farm Street : Small Forest Trail
    }

    @Script("evanGarden1")
    public static void evanGarden1(ScriptManager sm) {
        // Utah's House : Front Yard (100030102)
        //   east00 (253, 36)
        if (!sm.hasQuestStarted(22008)) {
            sm.message("You cannot go to the Back Yard without a reason");
            return;
        }
        sm.playPortalSE();
        sm.warpInstance(100030103, "west00", 100030102, 60 * 10); // Utah's Backyard
    }

    @Script("inDragonEgg")
    public static void inDragonEgg(ScriptManager sm) {
        // Farm Street : Farm Center (100030300)
        //   in00 (181, -865)
        if (!sm.hasQuestStarted(22005)) {
            sm.playPortalSE();
            sm.warp(100030301); // Farm Street : Forest Hall (Hall of Fame)
            return;
        }
        sm.playPortalSE();
        sm.warp(900020100, "out00"); // Hidden Street : Lush Forest
    }

    @Script("babyPig")
    public static void babyPig(ScriptManager sm) {
        // Piglet (1013200)
        //   Hidden Street : Lush Forest (900020100)
        //   Hidden Street : Lush Forest (900020110)
        if (sm.getFieldId() == 900020100) {
            sm.setPlayerAsSpeaker(true);
            sm.sayOk("#b(I'm too far from the Piglet. I have to move closer to grab it.)");
        } else if (sm.getFieldId() == 900020110) {
            if (!sm.hasQuestStarted(22005)) {
                return;
            }
            if (!sm.addItem(4032449, 1)) {
                sm.sayOk("Please check if your inventory is full or not.");
                return;
            }
            sm.forceCompleteQuest(22015); // Npc/1013200.img/condition1/22015
        }
    }

    @Script("evanFall")
    public static void evanFall(ScriptManager sm) {
        // Hidden Street : Lush Forest (900020100)
        //   scr00 (760, 168)
        //   scr01 (858, 168)
        //   scr02 (956, 168)
        //   scr03 (1054, 168)
        //   scr04 (1152, 168)
        //   scr05 (1250, 168)
        //   scr06 (1348, 168)
        //   scr07 (1445, 168)
        //   scr08 (1542, 168)
        //   scr09 (1640, 168)
        //   scr10 (1738, 168)
        //   scr11 (1836, 168)
        //   scr12 (1934, 168)
        //   scr13 (2032, 168)
        //   scr14 (2131, 168)
        sm.playPortalSE();
        if (!sm.hasQuestStarted(22005)) { // failsafe incase somehow someone gets in this map without having the quest

            sm.warp(100030300); // Farm Street : Farm Center
            return;
        }
        sm.warp(900090102); // Video : Tutorial 2
    }

    @Script("crash_Dragon")
    public static void crash_Dragon(ScriptManager sm) {
        // Video : Tutorial 2 (900090102)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction4.img/crash/Scene" + sm.getGender());
        sm.setDirectionMode(false, 3000);
    }

    @Script("evantalk40")
    public static void evantalk40(ScriptManager sm) {
        // Hidden Street : Lost Forest Entrance  (900020200)
        //   scr00 (-1093, -10)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "mo40=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o;mo40=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon40");
    }

    @Script("evantalk41")
    public static void evantalk41(ScriptManager sm) {
        // Hidden Street : Lost Forest Entrance  (900020200)
        //   scr01 (-523, -12)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "mo41=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o;mo40=o;mo41=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon41");
    }

    @Script("evantalk42")
    public static void evantalk42(ScriptManager sm) {
        // Hidden Street : Lost Forest Entrance  (900020200)
        //   scr02 (152, -9)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "mo42=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o;mo40=o;mo41=o;mo42=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon42");
    }

    @Script("evantalk50")
    public static void evantalk50(ScriptManager sm) {
        // Hidden Street : Lost Forest Trail  (900020210)
        //   scr00 (609, -8)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "mo50=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o;mo40=o;mo41=o;mo50=o;mo42=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon50");
    }

    @Script("evantalk60")
    public static void evantalk60(ScriptManager sm) {
        // Hidden Street : Lost Forest  (900020220)
        //   scr00 (-707, -11)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "mo60=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon60");
    }

    @Script("dragonEgg")
    public static void dragonEgg(ScriptManager sm) {
        // Dragon Nest (1013002)
        //   Hidden Street : Lost Forest  (900020220)
        sm.playPortalSE();
        sm.warp(900090103); // Video : Job Advancement
    }

    @Script("getDragonEgg")
    public static void getDragonEgg(ScriptManager sm) {
        // Video : Job Advancement (900090103)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction4.img/getDragonEgg/Scene" + sm.getGender());
        sm.setDirectionMode(false, 6500);
    }

    @Script("DragonEggNotice")
    public static void DragonEggNotice(ScriptManager sm) {
        // Hidden Street : Lush Forest (900020110)
        //   scr00 (834, -308)
        if (sm.hasQRValue(QuestRecordType.EvanTutorialEffect, "egg=o")) {
            return;
        }
        sm.addQRValue(QuestRecordType.EvanTutorialEffect, "egg=o;mo30=o;mo40=o;mo41=o;mo50=o;mo42=o;mo60=o");
        sm.forceCompleteQuest(22011); // Show Dragon Egg in Skill Window
        sm.sayImage(List.of("UI/tutorial/evan/8/0"));
        sm.message("You have received a Dragon Egg.");
    }

    @Script("babyPigOut")
    public static void babyPigOut(ScriptManager sm) {
        // Hidden Street : Lush Forest (900020110)
        //   out00 (-143, 38)
        sm.warp(100030300, "in00");
    }

    @Script("giveEggEvan")
    public static void giveEggEvan(ScriptManager sm) {
        // Hen (1013104)
        //   Utah's House : Front Yard (100030102)
        if (!sm.hasQuestStarted(22007) || sm.hasItem(4032451)) {
            return;
        }
        if (!sm.addItem(4032451, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.sayNext("#b(You have obtained an Egg. Deliver it to Utah.)");
    }

    @Script("evanFarmCT")
    public static void evanFarmCT(ScriptManager sm) {
        // Farm Street : Farm Center (100030300)
        //   west00 (196, 31)
        if (sm.hasQuestStarted(22010) || sm.hasQuestCompleted(22010)) {
            sm.playPortalSE();
            sm.warp(100030310, "east00"); // Farm Street : Large Forest Trail
            return;
        }
        sm.message("You are not allowed to leave the farm yet.");
    }

    @Script("q22000s")
    public static void q22000s(ScriptManager sm) {
        // Strange Dream (22000 - start)
        sm.sayNext("Did you sleep well, Evan?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYes, what about you, Mom?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I did as well, but you seem so tired. Are you sure you slept okay? Did the thunder and lightning last night keep you up?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bOh, no. It's not that, Mom. I just had a strange dream last night.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("A strange dream? What kind of strange dream?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell there was this dragon in the middle of the forest and he spoke to me!");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Hahaha, a dragon? That's incredible. I'm glad he didn't swallow you whole! You should tell #p1013101# about your dream. I'm sure he'll enjoy it.")) {
            sm.sayNext("Hm? Don't you want to tell #p1013101#? You have to be nice to your brother, dear.");
            return;
        }
        sm.sayNext("#b#p1013101##k went to the #b#m100030102##k to feed #p1013102#. You'll see him right outside.");
        sm.forceStartQuest(22000);
        sm.sayImage(List.of("UI/tutorial/evan/1/0"));
    }

    @Script("q22000e")
    public static void q22000e(ScriptManager sm) {
        // Strange Dream (22000 - end)
        sm.sayNext("Hey, Evan. You up? What's with the dark circles under your eyes? Didn't sleep well? Huh? A strange dream? What was it about? Whoa? A dream about a dragon?");
        sm.sayBoth("Muahahahahaha, a dragon? Are you serious? I don't know how to interpret dreams, but that sounds like a good one! Did you see a dog in your dream, too? Hahaha!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 20 exp");
        sm.forceCompleteQuest(22000);
        sm.addExp(20);
        sm.sayImage(List.of("UI/tutorial/evan/2/0"));
    }

    @Script("q22001s")
    public static void q22001s(ScriptManager sm) {
        // Feeding Bull Dog (22001 - start)
        sm.sayNext("Haha. I had a good laugh. Hahaha. But enough with that nonsense. Feed #p1013102#, would you?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat? That's your job, #p1013101#!");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("You little brat! I told you to call me Older Brother! You know how much #p1013102# hates me. He'll bite me if I go near him. You feed him. He likes you.")) {
            sm.sayNext("Stop being lazy. Do you want to see your brother bitten by a dog? Hurry up! Talk to me again and accept the quest!");
            return;
        }
        if (!sm.addItem(4032447, 1)) { // Dog Food
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22001);
        sm.sayNext("Hurry up and head #bleft#k to feed #b#p1013102##k. He's been barking to be fed all morning.");
    }

    @Script("q22001e")
    public static void q22001e(ScriptManager sm) {
        // Feeding Bull Dog (22001 - end)
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(You place food in #p1013102#'s bowl.)");
        sm.sayBoth("#b(#p1013102# is totally sweet. #p1013101# is just a coward.)");
        sm.sayBoth("#b(Looks like #p1013102# has finished eating. Return to #p1013101# and let him know.)\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 35 exp");
        sm.removeItem(4032447, 1);
        sm.addExp(35);
        sm.forceCompleteQuest(22001);
    }

    @Script("q22002s")
    public static void q22002s(ScriptManager sm) {
        // Sandwich for Breakfast (22002 - start)
        sm.sayNext("Did you feed #p1013102#? You should have some breakfast now then, Evan. Today's breakfast is a #t2022620#. I've brought it with me. Hee hee. I was going to eat it myself if you didn't agree to feed #p1013102#.");
        if (!sm.askAccept("Here, I'll give you this #bSandwich#k, so #bgo talk to mom when you finish eating#k. She says she has something to tell you.")) {
            sm.sayNext("Oh, what? Aren't you going to have breakfast? Breakfast is the most important meal of the day! Talk to me again if you change your mind. If you don't, I'm going to eat it myself.");
            return;
        }
        if (!sm.addItem(2022620, 1)) { // Homemade Sandwich
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22002);
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(Mom has something to say? Eat your #t2022620# and head back inside the house.)");
        sm.sayImage(List.of("UI/tutorial/evan/3/0"));
    }

    @Script("q22002e")
    public static void q22002e(ScriptManager sm) {
        // Sandwich for Breakfast (22002 - end)
        sm.sayNext("Did you eat your breakfast, Evan? Then, will you do me a favor?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i1003028# 1 #t1003028#\r\n#i2022621# 5 #t2022621#s\r\n#i2022622# 5 #t2022622#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 60 exp");
        if (!sm.addItems(List.of(
                Tuple.of(1003028, 1), // Straw Hat
                Tuple.of(2022621, 5), // Tasty Milk
                Tuple.of(2022622, 5) // Squeezed Juice
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(60);
        sm.forceCompleteQuest(22002);
        sm.sayImage(List.of("UI/tutorial/evan/4/0"));
    }

    @Script("q22003s")
    public static void q22003s(ScriptManager sm) {
        // Delivering the Lunch Box (22003 - start)
        if (!sm.askAccept("Your #bDad#k forgot his Lunch Box when he left for the farm this morning. Will you #bdeliver this Lunch Box#k to your Dad in #b#m100030300##k, honey?")) {
            sm.sayNext("Good kids listen to their mothers. Now, Evan, be a good kid and talk to me again.");
            return;
        }
        sm.forceStartQuest(22003);
        if (!sm.addItem(4032448, 1)) { // Lunch Made with Love
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.sayNext("Heehee, my Evan is such a good kid! Head #bleft after you exit the house#k. Rush over to your dad. I'm sure he's starving.");
        sm.sayBoth("Come back to me if you happen to lose the Lunch Box. I'll make his lunch again.");
        sm.sayImage(List.of("UI/tutorial/evan/5/0"));
    }

    @Script("q22003e")
    public static void q22003e(ScriptManager sm) {
        // Delivering the Lunch Box (22003 - end)
        sm.sayNext("Oh, Evan! What are you doing here? Are you here to help your man? Hey, that's a Lunch Box you've got there!");
        sm.sayBoth("Ah, I knew I was missing something! I always am, it seems. Today it was my #t4032448#, yesterday it was my hat, and the day before it was my shoes. I'm getting so forgetful!");
        sm.sayBoth("In any case, since you're here, will you do me a favor?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2022621# 10 #t2022621#\r\n#i2022622# 10 #t2022622#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 175 exp");
        sm.removeItem(4032448, 1);
        if (!sm.addItems(List.of(
                Tuple.of(2022621, 10), // Tasty Milk
                Tuple.of(2022622, 10) // Squeezed Juice
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(175);
        sm.forceCompleteQuest(22003);
    }

    @Script("q22004s")
    public static void q22004s(ScriptManager sm) {
        // Fixing the Fence (22004 - start)
        sm.sayNext("The #o1210100#s at the farm have been acting strange these past couple days. They've been angry and irritable for no reason. I was worried so I came out to the farm early this morning and sure enough, it seems like a few of these #o1210100#s got past the fence.");
        if (!sm.askAccept("Before I go and find the #o1210100#s, I should mend the broken fence. Luckily, it wasn't damaged too badly. I just need a few #t4032498#es to fix it right up. Will you bring me #b3#k #b#t4032498#es#k, Evan?")) {
            sm.sayNext("Hm, #p1013101# would have done it at the drop of a hat.");
            return;
        }
        sm.forceStartQuest(22004);
        sm.sayNext("Oh, that's very nice of you. You'll be able to find #b#t4032498#es#k from the nearby #r#o0130100#s#k. They're not too strong, but use your skills and items when you find yourself in danger.");
        sm.sayImage(List.of("UI/tutorial/evan/6/0"));
    }

    @Script("q22004e")
    public static void q22004e(ScriptManager sm) {
        // Fixing the Fence (22004 - end)
        sm.sayNext("Ah, did you bring all the #t4032498#es? That's my kid! What shall I give you as a reward... Let's see... Oh, right!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i3010097# 1 #t3010097#\r\n#i2022621# 15 #t2022621#s\r\n#i2022622# 15 #t2022622#s\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 210 exp");
        sm.removeItem(4032498);
        if (!sm.addItems(List.of(
                Tuple.of(3010097, 1), // Strong Wooden Chair
                Tuple.of(2022621, 15), // Tasty Milk
                Tuple.of(2022622, 15) // Squeezed Juice
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(210);
        sm.forceCompleteQuest(22004);
        sm.sayNext("Here. I made this new chair from the wooden boards I had left over after fixing the fence. It may not seem like much, but it's sturdy. I'm sure it'll come in handy.");
        sm.sayImage(List.of("UI/tutorial/evan/7/0"));
    }

    @Script("q22005s")
    public static void q22005s(ScriptManager sm) {
        // Rescuing the Piglet (22005 - start)
        sm.sayNext("Oh no! A #b#p1013200##k ran away while the fence was broken. He's too young to find his way home, so we'll have to go find him. Do you think you can help me?");
        if (!sm.askAccept("I think the #p1013200# ran towards the #b#m900020100##k. Please head there to look for the #p1013200#.")) {
            sm.sayNext("Hmm. #p1013101# would have volunteered to do it even before I asked.");
            return;
        }
        sm.forceStartQuest(22005);
        sm.sayNext("The Lush Forest is towards the #bupper left#k. The recent flood washed away much of the path, so be careful.");
    }

    @Script("q22005e")
    public static void q22005e(ScriptManager sm) {
        // Rescuing the Piglet (22005 - end)
        final int answer = sm.askMenu("That took a while. The #p1013200# must ran pretty far.", Map.of(
                0, "Er, yeah. Sure... Dad, is there a strange foggy forest around here?"
        ));
        sm.sayNext("A foggy forest? I don't think so. It's always clear around Henesys.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Strange. Did you have another dream? What's going on... )");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Anyway, thanks for your help. Er, Evan? What has you so lost in thought?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Wait, what's this? This is the egg from earlier! Then...it wasn't a dream?!)");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Evan?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bDad! Quick! How do I get an egg to hatch?!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Whoa! You scared me! You want to know how to hatch an egg? Why are you asking such a stange question...?");
        sm.sayBoth("I don't know how to hatch an egg... Maybe your mother would know.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 700 exp");
        sm.removeItem(4032449, 1);
        sm.addExp(700);
        sm.forceCompleteQuest(22005);
    }

    @Script("q22006s")
    public static void q22006s(ScriptManager sm) {
        // Returning the Empty Lunch Box (22006 - start)
        sm.sayNext("If you want to learn about hatching eggs, you should head #bhome#k and ask #bMom#k. She raises all our chickens, so she'd know. Also...");
        if (!sm.askAccept("Since you're going home, return this #b#t4032450##k to your mother. I have so much work to do, I may not get home until late tonight.")) {
            sm.sayNext("Hmm. #p1013101# would have been more than willing...");
            return;
        }
        if (!sm.addItem(4032450, 1)) { // Empty Lunch Box
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22006);
        sm.sayNext("Thanks. See you later, kiddo.");
    }

    @Script("q22006e")
    public static void q22006e(ScriptManager sm) {
        // Returning the Empty Lunch Box (22006 - end)
        sm.sayNext("Evan, you're back? Ah, you brought back the #t4032450#. You're such a good kid. Huh? How do you raise an egg?");
        sm.sayBoth("There are many ways, but the simplest is to use an Incubator. Come to think of it, I think I saw #b#p1013101##k with one. Why don't you ask #p1013101# to lend it to you?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2022621# 20 #t2022621#\r\n#i2022622# 20 #t2022622#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 270 exp");
        sm.removeItem(4032450, 1);
        if (!sm.addItems(List.of(
                Tuple.of(2022621, 20), // Tasty Milk
                Tuple.of(2022622, 20) // Squeezed Juice
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(270);
        sm.forceCompleteQuest(22006);
    }

    @Script("q22007s")
    public static void q22007s(ScriptManager sm) {
        // Collecting Eggs (22007 - start)
        sm.sayNext("An Incubator? Yeah, I have one. I picked it up after an adventurer tossed it a while ago. It should still work. Why? You need it?");
        if (!sm.askAccept("Okay, you can have the Incubator, but you have to do me a favor first. Mom wants me to collect some #t4032451#s, but it's such a bother. If you collect an #t4032451# for me, I'll give you the Incubator. Do we have a deal?")) {
            sm.sayNext("Fine. I'll just keep the Incubator, then.");
            return;
        }
        sm.forceStartQuest(22007);
        sm.sayNext("Okay, then go to that #b#p1013104# to your right#k and bring back an #t4032451#. You can get an #t4032451# by clicking on the #p1013104#. You just need to get me #bone#k.");
    }

    @Script("q22007e")
    public static void q22007e(ScriptManager sm) {
        // Collecting Eggs (22007 - end)
        sm.sayNext("Oh, did you bring the egg? Here, give it to me, I'll give you the incubator then.");
        sm.askYesNo("Alright, here you go. I have no idea how to use it, but it's yours.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 360 exp");
        if (!sm.removeItem(4032451, 1)) {
            sm.sayNext("Are you sure you have the Egg with you?");
            return;
        }
        sm.addExp(360);
        sm.forceCompleteQuest(22007);
        sm.sayImage(List.of("UI/tutorial/evan/9/0"));
    }

    @Script("q22008s")
    public static void q22008s(ScriptManager sm) {
        // Chasing away the Foxes (22008 - start)
        if (!sm.askAccept("It's strange. The chickens are acting funny. They used to hatch way more #t4032451#s. Do you think the Foxes have something to do with it? If so, we better hurry up and do something.")) {
            sm.sayNext("Are you scared of the #o9300385#es? Don't tell anyone you're related to me. That's shameful.");
        }
        sm.forceStartQuest(22008);
        sm.sayNext("Right? Let us go and defeat those Foxes. Go on ahead and defeat #r10 #o9300385#es#k in #b#m100030103##k first. I'll follow you and take care of what's left behind. Now, hurry over to #m100030103#!");
        sm.sayImage(List.of("UI/tutorial/evan/10/0"));
    }

    @Script("q22008e")
    public static void q22008e(ScriptManager sm) {
        // Chasing away the Foxes (22008 - end)
        sm.sayNext("Did you defeat the #b#o9300385#es#k?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat happened to slaying the Foxes left behind?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Oh, that? Haha. I did chase them, sort of, but I wanted to make sure that they do not catch up to you. I wouldn't want you eaten by a #o9300385# or anything. So I just let them be.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bAre you sure you weren't just hiding because you were scared of the Foxes?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("What? No way! Sheesh, I fear nothing!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWatch out! There's a #o9300385# right behind you!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Eeeek! Mommy!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("...");
        sm.sayBoth("You little brat! I'm your older brother. Don't you mess with me! Your brother has a weak heart, you know. Don't surprise me like that!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(This is why I don't want to call you Older Brother...)");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Hmph! Anyway, I'm glad you were able to defeat the #o9300385#es. As a reward, I'll give you something an adventurer gave me a long time ago. Here you are.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i1372043# 1 #t1372043#\r\n#i2022621# 25 #t2022621#\r\n#i2022622# 25 #t2022622#s\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 910 exp");
        if (!sm.addItems(List.of(
                Tuple.of(1372043, 1), // Wooden Wand
                Tuple.of(2022621, 25), // Tasty Milk
                Tuple.of(2022622, 25) // Squeezed Juice
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(910);
        sm.forceCompleteQuest(22008);
        sm.sayNext("#bThis is a weapon that Magicians use. It's a Wand#k. You probably won't really need it, but it'll make you look important if you carry it around. Hahahahaha.");
        sm.sayPrev("Anyway, the Foxes have increased, right? How weird is that? Why are they growing day by day? We should really look into it and get to the bottom of this.");
    }

    @Script("q22009s")
    public static void q22009s(ScriptManager sm) {
        // Verifying the Farm Situation (22009 - start)
        sm.sayNext("If the number of foxes has increased near the farm just like it has near our house, that'll interfere with Dad's farm work. We should investigate this. Don't you agree?");
        if (!sm.askAccept("Go to the #b#m100030300##k and ask #bDad#k about the situation. If the number of #o9300385#es haa increased there as well, we're going to have to conduct a major #o9300385# hunt.")) {
            sm.sayNext("What? Think hard about this! If the farm fails, what are we going to survive on! Huh? Talk to me again and press ACCEPT this time!");
            return;
        }
        sm.forceStartQuest(22009);
    }

    @Script("q22009e")
    public static void q22009e(ScriptManager sm) {
        // Verifying the Farm Situation (22009 - end)
        sm.sayNext("What is it, Evan? I'm sure you're not here to deliver another #t4032448#, and I'm too busy to play with you... What? Have the number of foxes increased here?");
        sm.sayBoth("Well, I'm not sure. I've been too busy to notice. The #b#o1210100##ks have been acting crazy, jumping all over the place. Even the foxes seem to be running away from the #o1210100#s...");
        sm.sayBoth("Ah, maybe that is why the #o9300385# population near the house has increased. They ran there to escape from the #o1210100#s. Hmm...\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 260 exp");
        sm.addExp(260);
        sm.forceCompleteQuest(22009);
    }

    @Script("q22010s")
    public static void q22010s(ScriptManager sm) {
        // Strange Farm (22010 - start)
        sm.sayNext("Forget about the #o9300385#es. Since you're here, want to help me out again? I think the only way to calm the #o1210100#s is by disciplining them. Why don't you go take care of a few of the #r#o1210100#s#k?");
        if (!sm.askAccept("The crazy pigs can be found starting at the #b#m100030310##k. Head over and take care of just #r20#k of them. Hey, kiddo, you've really become a huge help to me.")) {
            sm.sayNext("Huh? Are you scared of the #o1210100#s? They are jumping around like crazy, but you shouldn't be scared of them...");
            return;
        }
        sm.forceStartQuest(22010);
    }

    @Script("q22010e")
    public static void q22010e(ScriptManager sm) {
        // Strange Farm (22010 - end)
        sm.sayNext("Oh, you disciplined the #o1210100#s. Good job! Thank you.");
        sm.sayBoth("Now I'll just get back to work.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2022621# 30 #t2022621#\r\n#i2022622# 30 #t2022622#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 980 exp");
        if (!sm.addItems(List.of(
                Tuple.of(2022621, 30), // Tasty Milk
                Tuple.of(2022622, 30) // Squeezed Juice
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(980);
        sm.forceCompleteQuest(22010);
    }
}
