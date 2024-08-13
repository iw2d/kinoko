package kinoko.script.quest;

import kinoko.packet.user.UserLocal;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;

public final class AranTutorial extends ScriptHandler {
    public static final String TALK_TO_TUTOR_SCRIPT = "TalkToTutor_Aran";

    @Script(TALK_TO_TUTOR_SCRIPT)
    public static void talkToTutor(ScriptManager sm) {
        final int answer = sm.askMenu("Is there anything you're still curious about? If so, I'll try to explain it better.", Map.of(
                0, "Who am I?",
                1, "Where am I?",
                2, "Who are you?",
                3, "Tell me what I have to do.",
                4, "Tell me about my Inventory.",
                5, "How do I advance my skills?",
                6, "I want to know how to equip items.",
                7, "How do I use quick slots?",
                8, "How can I open breakable containers?",
                9, "I want to sit in a chair but I forgot how."
        ));
        switch (answer) {
            case 0 -> {
                sm.sayNext("You are one of the heroes that saved Maple World from the Black Mage hundreds of years ago. You've lost your memory due to the curse of the Black Mage.");
            }
            case 1 -> {
                sm.sayNext("This island is called Rien, and this is where the Black Mage's curse put you to sleep. It's a small island covered in ice and snow, and the majority of the residents are Penguins.");
            }
            case 2 -> {
                sm.sayNext("I'm Lilin, a clan member of Rien, and I've been waiting for your return as the prophecy foretold. I'll be your guide for now.");
            }
            case 3 -> {
                sm.sayNext("Let's not waste any more time and just get to town. I'll give you the details when we get there.");
            }
            case 4, 5, 6, 7, 8, 9 -> {
                sm.write(UserLocal.tutorMsg(answer + 10, 4000));
            }
        }
    }

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


    // SNOW ISLAND SCRIPTS ---------------------------------------------------------------------------------------------

    @Script("iceCave")
    public static void iceCave(ScriptManager sm) {
        // Snow Island : Ice Cave (140090000)
        sm.removeSkill(20000014);
        sm.removeSkill(20000015);
        sm.removeSkill(20000016);
        sm.removeSkill(20000017);
        sm.removeSkill(20000018);
        sm.setDirectionMode(false, 0);
        sm.reservedEffect("Effect/Direction1.img/aranTutorial/ClickLilin");
    }

    @Script("rienTutor1")
    public static void rienTutor1(ScriptManager sm) {
        // Snow Island : Cold Forest 1 (140090100)
        //   west00 (-1951, 82)
        if (!sm.hasQuestCompleted(21010)) {
            sm.message("You must complete the quest before proceeding to the next map.");
            return;
        }
        sm.playPortalSE();
        sm.warp(140090200, "east00");
    }

    @Script("awake")
    public static void awake(ScriptManager sm) {
        // Lilin (1202000)
        //   Snow Island : Ice Cave (140090000)
        if (!sm.hasQRValue(QuestRecordType.AranHelperClear, "helper=clear")) {
            sm.setFlipSpeaker(true);
            sm.sayNext("You've finally awoken...!");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("And you are...?");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("The hero who fought against the Black Mage... I've been waiting for you to wake up!");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("Who... Who are you? And what are you talking about?");
            sm.sayBoth("And who am I...? I can't remember anything... Ouch, my head hurts!");
            sm.reservedEffect("Effect/Direction1.img/aranTutorial/face");
            sm.reservedEffect("Effect/Direction1.img/aranTutorial/ClickLilin");
            sm.setQRValue(QuestRecordType.AranHelperClear, "helper=clear");
        } else {
            sm.setFlipSpeaker(true);
            sm.sayNext("Are you alright?");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("I can't remember anything. Where am I? And who are you...?");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("Stay calm. There is no need to panic. You can't remember anything because the curse of the Black Mage erased your memory. I'll tell you everything you need to know... step by step.");
            sm.sayBoth("You're a hero who fought the Black Mage and saved Maple World hundreds of years ago. But at the very last moment, the curse of the Black Mage put you to sleep for a long, long time. That's when you lost all of your memories.");
            sm.sayBoth("This island is called Rien, and it's where the Black Mage trapped you. Despite its name, this island is always covered in ice and snow because of the Black Mage's curse. You were found deep inside the Ice Cave.");
            sm.sayBoth("My name is Lilin and I belong to the clan of Rien. The Rien Clan has been waiting for a hero to return for a long time now, and we finally found you. You've finally returned!");
            sm.sayBoth("I've said too much. It's okay if you don't really understand everything I just told you. You'll get it eventually. For now, #byou should head to town#k. I'll stay by your side and help you until you get there.");
            sm.warp(140090100, "st00");
            sm.write(UserLocal.hireTutor(true));
        }
    }

    @Script("q21010s")
    public static void q21010s(ScriptManager sm) {
        // The Return of the Hero (21010 - start)
        sm.sayNext("Hm, what's a human doing on this island? Wait, it's #p1201000#. What are you doing here, #p1201000#? And who's that beside you? Is it someone you know, #p1201000#? What? The hero, you say?");
        sm.sayBoth("#i4001170#");
        sm.sayBoth("Ah, this must be the hero you and your clan have been waiting for. Am I right, Lilin? Ah, I knew you weren't just accompanying an average passerby...");
        if (!sm.askAccept("Oh, but it seems our hero has become very weak since the Black Mage's curse. It only makes sense, considering that the hero has been asleep for hundreds of years. #bHere, I'll give you a HP Recovery Potion...#k")) {
            sm.sayNext("Oh, no need to decline my offer. It's no big deal. It's just a potion. Well, let me know if you change your mind.");
            return;
        }
        sm.getUser().setHp(25);
        if (!sm.hasItem(2000022) && !sm.addItem(2000022, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(21010);
        sm.setFlipSpeaker(true);
        sm.sayNext("Drink it first. Then we'll talk.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(How do I drink the potion? I don't remember...)#k");
        sm.write(UserLocal.tutorMsg(14, 4000));
    }

    @Script("q21010e")
    public static void q21010e(ScriptManager sm) {
        // The Return of the Hero (21010 - end)
        final User user = sm.getUser();
        if (user.getHp() < user.getMaxHp()) {
            sm.sayNext("You didn't drink the potion yet.");
            return;
        }
        sm.sayNext("We've been digging and digging inside the Ice Cave in the hope of finding a hero, but I never thought I'd actually see the day... The prophecy was true! You were right, Lilin! Now that one of the legendary heroes has returned, we have no reason to fear the Black Mage!");
        sm.sayBoth("Oh, I've kept you too long. I'm sorry, I got a little carried away. I'm sure the other Penguins feel the same way. I know you're busy, but could you #bstop and talk to the other Penguins#k on your way to town? They would be so honored.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2000022# 5 Special Rien Red Potion\r\n#i2000023# 5 Special Rien Blue Potion\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 16 exp");
        if (!sm.addItems(List.of(
                Tuple.of(2000022, 5), // Special Rien Red Potion
                Tuple.of(2000023, 5) // Special Rien Blue Potion
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(16);
        sm.forceCompleteQuest(21010);
        sm.setFlipSpeaker(true);
        sm.sayBoth("Oh you've leveled up! You may have even received some skill points. In Maple World, you can acquire 3 skill points every time you level up. Press the #bK key#k to view the Skill window.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Everyone's been so nice to me, but I just can't remember anything. Am I really a hero? I should check my skills and see. But how do I check then?)#k");
        sm.write(UserLocal.tutorMsg(15, 4000));
    }
}
