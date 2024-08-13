package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.quest.QuestRecordType;

import java.util.Map;

public final class AranTutorial extends ScriptHandler {
    @Script("aranDirection")
    public static void aranDirection(ScriptManager sm) {
        // null (914090000)
        // null (914090001)
        // null (914090002)
        // null (914090003)
        // null (914090004)
        // null (914090005)
        // null (914090006)
        // null (914090007)
        // null (914090010)
        // null (914090011)
        // null (914090012)
        // null (914090013)
        // null (914090014)
        // null (914090015)
        // null (914090100)
        // null (914090200)
        // null (914090201)
        switch (sm.getFieldId()) {
            case 914090010 -> {
                sm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene0");
            }
            case 914090011 -> {
                sm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene1" + sm.getGender());
            }
            case 914090012 -> {
                sm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene2" + sm.getGender());
            }
            case 914090013 -> {
                sm.reservedEffect("Effect/Direction1.img/aranTutorial/Scene3");
            }
        }
    }

    @Script("aranTutorMono0")
    public static void aranTutorMono0(ScriptManager sm) {
        // Black Road : Wounded Soldier's Camp (914000000)
        //   tutor00 (224, -156)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "m0=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "m0=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon1");
    }

    @Script("aranTutorMono1")
    public static void aranTutorMono1(ScriptManager sm) {
        // Black Road : Wounded Soldier's Camp (914000000)
        //   tutor01 (-113, -184)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "m1=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "m1=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon2");
    }

    @Script("aranTutorMono2")
    public static void aranTutorMono2(ScriptManager sm) {
        // Black Road : Wounded Soldier's Camp (914000000)
        //   tutor02 (-418, -288)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "m2=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "m2=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon3");
    }

    @Script("aranTutorMono3")
    public static void aranTutorMono3(ScriptManager sm) {
        // Black Road : Burning Forest 1 (914000200)
        //   tutor01 (2119, -45)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "m3=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "m3=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/legendBalloon6");
    }

    @Script("aranTutorArrow0")
    public static void aranTutorArrow0(ScriptManager sm) {
        // Black Road : Ready to Leave (914000100)
        //   tutor00 (-381, -45)
        //   tutor01 (-607, -44)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "a0=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "a0=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
    }

    @Script("aranTutorArrow1")
    public static void aranTutorArrow1(ScriptManager sm) {
        // Black Road : Burning Forest 1 (914000200)
        //   tutor02 (1936, -45)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "a1=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "a1=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
    }

    @Script("aranTutorArrow2")
    public static void aranTutorArrow2(ScriptManager sm) {
        // Black Road : Burning Forest 2 (914000210)
        //   tutor01 (498, -41)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "a2=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "a2=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
    }

    @Script("aranTutorArrow3")
    public static void aranTutorArrow3(ScriptManager sm) {
        // Black Road : Burning Forest 3 (914000220)
        //   tutor01 (-945, -45)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "a3=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "a3=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
    }

    @Script("aranTutorAloneX")
    public static void aranTutorAloneX(ScriptManager sm) {
        // Black Road : Wounded Soldier's Camp (914000000)
        //   out00 (-680, -14)
        sm.playPortalSE();
        sm.warp(914000100, "in00");
    }

    @Script("aranTutorOut1")
    public static void aranTutorOut1(ScriptManager sm) {
        // Black Road : Ready to Leave (914000100)
        //   out00 (-1219, -2)
        if (!sm.hasQuestStarted(21000)) {
            sm.message("You can only exit after you accept the quest from Athena Pierce, who is to your right.");
        } else {
            sm.addSkill(20000017, 1, 1);
            sm.addSkill(20000018, 1, 1);
            sm.playPortalSE();
            sm.warp(914000200, "east00");
        }
    }

    @Script("aranTutorOut2")
    public static void aranTutorOut2(ScriptManager sm) {
        // Black Road : Burning Forest 1 (914000200)
        //   west00 (1305, -1)
        sm.addSkill(20000014, 1, 1);
        sm.addSkill(20000015, 1, 1);
        sm.playPortalSE();
        sm.warp(914000210, "east00");
    }

    @Script("aranTutorOut3")
    public static void aranTutorOut3(ScriptManager sm) {
        // Black Road : Burning Forest 2 (914000210)
        //   west00 (-133, 0)
        sm.addSkill(20000016, 1, 1);
        sm.playPortalSE();
        sm.warp(914000220, "east00");
    }

    @Script("aranTutorGuide0")
    public static void aranTutorGuide0(ScriptManager sm) {
        // Black Road : Burning Forest 1 (914000200)
        //   tutor00 (2387, -46)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "g0=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "g0=1");
        sm.message("To use a Regular Attack on monsters, press the Ctrl key.");
        sm.screenEffect("aran/tutorialGuide1");
    }

    @Script("aranTutorGuide1")
    public static void aranTutorGuide1(ScriptManager sm) {
        // Black Road : Burning Forest 2 (914000210)
        //   tutor00 (850, -45)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "g1=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "g1=1");
        sm.message("You can use Consecutive Attacks by pressing the Ctrl key multiple times.");
        sm.screenEffect("aran/tutorialGuide2");
    }

    @Script("aranTutorGuide2")
    public static void aranTutorGuide2(ScriptManager sm) {
        // Black Road : Burning Forest 3 (914000220)
        //   tutor00 (-586, -47)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "g2=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "g2=1");
        sm.message("You can use a Command Attack by pressing both the arrow key and the attack key after a Consecutive Attack.");
        sm.screenEffect("aran/tutorialGuide3");
    }

    @Script("aranTutorLost")
    public static void aranTutorLost(ScriptManager sm) {
        // Black Road : Dead End Forest (914000300)
        //   tutor00 (-1887, -44)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "fin=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "fin=1");
        sm.reservedEffect("Effect/Direction1.img/aranTutorial/Child");
        sm.reservedEffect("Effect/Direction1.img/aranTutorial/ClickChild");
    }

    @Script("outChild")
    public static void outChild(ScriptManager sm) {
        // Black Road : Dead End Forest (914000300)
        //   east00 (-1757, 2)
        if (sm.hasQuestStarted(21001)) {
            sm.warp(914000400, "west00");
        } else {
            sm.warp(914000220, "west00");
        }
    }

    @Script("iceCave")
    public static void iceCave(ScriptManager sm) {
        // Snow Island : Ice Cave (140090000)
        sm.removeSkill(20000014);
        sm.removeSkill(20000015);
        sm.removeSkill(20000016);
        sm.removeSkill(20000017);
        sm.removeSkill(20000018);
        sm.setDirectionMode(false, 0);
    }

    @Script("talkHelena")
    public static void talkHelena(ScriptManager sm) {
        // Athena Pierce (1209000)
        //   Black Road : Ready to Leave (914000100)
        if (sm.getQRValue(QuestRecordType.AranTutorial).equals("1")) {
            sm.sayNext("Aran, you're awake! How are you feeling? Hm? You want to know what's been going on?");
            sm.sayBoth("We're almost done preparing for the escape. You don't have to worry. Everyone I could possibly find has boarded the ark, and Shinsoo has agreed to guide the way. We'll head to Victoria Island as soon as we finish the remaining preparations.");
            sm.sayBoth("The other heroes? They've left to fight the Black Mage. They're buying us time to scape. What? You want to fight with them? No! You cant! You're hurt. You must leave with us!");
            sm.setQRValue(QuestRecordType.AranTutorial, "1");
            sm.reservedEffect("Effect/Direction1.img/aranTutorial/Trio");
        } else {
            final int answer = sm.askMenu("We are in a dire situation. What would you like to know?", Map.of(
                    0, "About the Black Mage",
                    1, "About the preparations for the escape",
                    2, "About the other heroes"
            ));
            if (answer == 0) {
                sm.sayNext("I heard the Black Mage is very close. We can't even go into the forest because the dragons serving the Black Mage are there. That's why we're taking this route. We don't have any choice but to fly to Victoria Island, Aran...");
            } else if (answer == 1) {
                sm.sayNext("We're almost ready to go. We can head over to Victoria Island as soon as the remaining few people board the ark. Shinsoo says there isn't anyone left in Ereve he needs to protect, so he's agreed to guid us.");
            } else if (answer == 2) {
                sm.sayOk("The other heroes... They've already left to fight the Black Mage. They're slowing the Black Mage down so the rest of us can escape. They didn't want to take you with them because you were injured. Escape with us, Aran, as soon as we rescue the child!");
            }
        }
    }

    @Script("q21000s")
    public static void q21000s(ScriptManager sm) {
        // Find the Missing Kid 1 (21000 - start)
        if (!sm.askAccept("Oh, no! I think there's still a child in the forest! Aran, I'm very sorry, but could you rescue the child? I know you're injured, but I don't have anyone else to ask!")) {
            sm.sayNext("No, Aran... We can't leave a kid behind. I know it's a lot to ask, but please reconsider. Please!");
            return;
        }
        sm.forceStartQuest(21000);
        sm.sayNext("#bThe child is probably lost deep inside the forest!#k We have to escape before the Black Mage finds us. You must rush into the forest and bring the child back with you!");
        sm.sayBoth("Don't panic, Aran. If you wish the check the status of the quest, press #bQ#k and view the Quest window.");
        sm.sayBoth("Please, Aran! I'm begging you. I can't bear to lose another person to the Black Mage!");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
    }

    @Script("q21001s")
    public static void q21001s(ScriptManager sm) {
        // Find the Missing Kid 2 (21001 - start)
        sm.sayOk("Wah! *Sniff sniff* Hey, that's Aran! Are you here to rescue me?");
        if (!sm.askAccept("*Sniff sniff* I was so scared... Please take me to Athena Pierce.")) {
            sm.sayNext("*Sob* Aran has declined my request!");
            return;
        }
        sm.addItem(4001271, 1);
        sm.forceStartQuest(21001);
        sm.warp(914000300, "sp");
    }

    @Script("q21001e")
    public static void q21001e(ScriptManager sm) {
        // Find the Missing Kid 2 (21001 - end)
        if (!sm.askYesNo("You made it back safely! What about the child?! Did you bring the child with you?!")) {
            sm.sayNext("What about the child? Please give me the child.");
            return;
        }
        sm.removeItem(4001271);
        sm.forceCompleteQuest(21001);
        sm.setNotCancellable(true);
        sm.setFlipSpeaker(true);
        sm.sayNext("Oh, what a relief. I'm so glad...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Hurry and board the ship! We don't have much time!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("We don't have any time to waste. The Black Mage's forces are getting closer and closer! We're doomed if we don't leave right this moment!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Leave, now!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Aran, please! I know you want to stay and fight the Black Mage, but it's too late! Leave it to the others and come to Victoria Island with us!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("No, I can't!");
        sm.sayBoth("Athena Pierce, why don't you leave for Victoria Island first? I promise I'll come for you later. I'll be alright. I must fight the Black Mage with the other heroes!");
        sm.setDirectionMode(true, 0);
        sm.warp(914090010);
    }
}
