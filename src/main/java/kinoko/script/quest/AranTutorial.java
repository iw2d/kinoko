package kinoko.script.quest;

import kinoko.packet.user.UserLocal;
import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.item.BodyPart;
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
        sm.removeEquipped(BodyPart.WEAPON);
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

    @Script("rienTutor2")
    public static void rienTutor2(ScriptManager sm) {
        // Snow Island : Cold Forest 2 (140090200)
        //   west00 (-3562, 85)
        if (!sm.hasQuestCompleted(21011)) {
            sm.message("You must complete the quest before proceeding to the next map.");
            return;
        }
        sm.playPortalSE();
        sm.warp(140090300, "east00");
    }

    @Script("rienTutor3")
    public static void rienTutor3(ScriptManager sm) {
        // Snow Island : Cold Forest 3 (140090300)
        //   west00 (-5209, 84)
        if (!sm.hasQuestCompleted(21012)) {
            sm.message("You must complete the quest before proceeding to the next map.");
            return;
        }
        sm.playPortalSE();
        sm.warp(140090400, "east00");
    }

    @Script("rienTutor4")
    public static void rienTutor4(ScriptManager sm) {
        // Snow Island : Cold Forest 4 (140090400)
        //   west00 (117, 83)
        if (!sm.hasQuestCompleted(21013)) {
            sm.message("You must complete the quest before proceeding to the next map.");
            return;
        }
        sm.playPortalSE();
        sm.warp(140090500, "east00");
    }

    @Script("rienTutor5")
    public static void rienTutor5(ScriptManager sm) {
        // Snow Island : Cold Forest 5 (140090500)
        //   tutor00 (-458, 39)
        sm.write(UserLocal.tutorMsg("You're very close to town. I'll head over there first since I have some things to take care of. You take your time.", 200, 4000));
    }

    @Script("rienTutor6")
    public static void rienTutor6(ScriptManager sm) {
        // Snow Island : Cold Forest 5 (140090500)
        //   tutor01 (-863, 39)
        sm.write(UserLocal.hireTutor(false));
    }

    @Script("rienTutor7")
    public static void rienTutor7(ScriptManager sm) {
        // Snow Island : Dangerous Forest (140010000)
        //   west00 (-1332, 84)
        if (sm.hasQuestCompleted(21014) || sm.getUser().getJob() != 2000) {
            sm.playPortalSE();
            sm.warp(140010100, "east00");
        } else {
            sm.playPortalSE();
            sm.warp(140000000, "st00");
        }
    }

    @Script("rienTutor8")
    public static void rienTutor8(ScriptManager sm) {
        // Snow Island : Rien (140000000)
        //   west00 (-2457, 83)
        sm.playPortalSE();
        sm.warp(140010000, "east00");
    }

    @Script("rienArrow")
    public static void rienArrow(ScriptManager sm) {
        // Snow Island : Dangerous Forest (140010000)
        if (sm.hasQRValue(QuestRecordType.AranGuideEffect, "rien=1")) {
            return;
        }
        sm.addQRValue(QuestRecordType.AranGuideEffect, "rien=1");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
    }

    @Script("enterRienFirst")
    public static void enterRienFirst(ScriptManager sm) {
        // Snow Island : Dangerous Forest (140010000)
        //   east00 (-101, 83)
        if (sm.hasQuestCompleted(21014)) {
            sm.playPortalSE();
            sm.warp(140000000, "west00");
        } else {
            sm.playPortalSE();
            sm.warp(140000000, "st00");
        }
    }

    @Script("rienItem0")
    public static void rienItem0(ScriptManager sm) {
        // rienItem0 (1402000)
        //   Snow Island : Cold Forest 4 (140090400)
        sm.dropRewards(List.of(
                Reward.money(10, 10, 0.7),
                Reward.item(2000000, 1, 1, 0.1),
                Reward.item(2000001, 1, 1, 0.1),
                Reward.item(2010000, 1, 1, 0.1),
                Reward.item(4032309, 1, 1, 1.0, 21013), // Piece of Bamboo
                Reward.item(4032310, 1, 1, 1.0, 21013) // Wood
        ));
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
        sm.sayBoth("Oh, I've kept you too long. I'm sorry, I got a little carried away. I'm sure the other Penguins feel the same way. I know you're busy, but could you #bstop and talk to the other Penguins#k on your way to town? They would be so honored.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2000022# 5 #t2000022#\r\n#i2000023# 5 #t2000022#\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 16 exp");
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

    @Script("q21011e")
    public static void q21011e(ScriptManager sm) {
        // The Missing Weapon (21011 - end)
        sm.sayNext("Wait, are you... No way.... Are you the hero that #p1201000# has been talking about all this time?! #p1201000#! Don't just nod... Tell me! Is this the hero you've been waiting for?!");
        sm.sayBoth("#i4001171#");
        sm.sayBoth("I'm sorry. I'm just so overcome with emotions... *Sniff sniff* My goodness, I'm starting to tear up. You must be so happy, #p1201000#.");
        sm.sayBoth("Wait a minute... You're not carrying any weapons. From what I've heard, each of the heroes had a special weapon. Oh, you must have lost it during the battle against the Black Mage.");
        if (!sm.askYesNo("This isn't good enough to replace your weapon, but #bcarry this sword with you for now#k. It's my gift to you. A hero can't be walking around empty-handed.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#v1302000# 1 #t1302000#\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 35 exp")) {
            sm.sayNext("*sniff sniff* Isn't this sword good enough for you, just for now? I'd be so honored...");
            return;
        }
        if (!sm.addItem(1302000, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(35);
        sm.forceCompleteQuest(21011);
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(Your skills are nowhere close to being hero-like... But a sword? Have you ever even held a sword in your lifetime? You can't remember... How do you even equip it?)");
        sm.write(UserLocal.tutorMsg(16, 4000));
    }

    @Script("q21012s")
    public static void q21012s(ScriptManager sm) {
        // Abilities Lost (21012 - start)
        sm.sayNext("Welcome, hero! What's that? You want to know how I knew who you were? That's easy. I eavesdropped on some people talking loudly next to me. I'm sure the rumor has spread through the entire island already. Everyone knows that you've returned!");
        sm.sayBoth("Anyway, what's with the long face? Is something wrong? Hm? You're not sure whether you're really a hero or not? You lost your memory?! No way... It must be because you were trapped inside the ice for hundreds and hundreds of years.");
        if (!sm.askAccept("Hm, how about trying out that sword? Wouldn't that bring back some memories? How about #bfighting some monsters#k?")) {
            sm.sayNext("Hm... You don't think that would help? Think about it. It could help, you know...");
            return;
        }
        sm.forceStartQuest(21012);
        sm.sayNext("It just so happens that there are a lot of #r#o9300383##k near here. How about defeating just #r3#k of them? It could help you remember a thing or two.");
        sm.sayBoth("Ah, you've also forgotten how to use your skills? #bPlace skills in the quick slots for easy access#k. You can also place consumable items in the slots, so use the slots to your advantage.");
        sm.write(UserLocal.tutorMsg(17, 4000));
    }

    @Script("q21012e")
    public static void q21012e(ScriptManager sm) {
        // Abilities Lost (21012 - end)
        if (!sm.askYesNo("Hm... Your expression tells me that the exercise didn't jog any memories. But don't you worry. They'll come back, eventually. Here, drink this potion and power up!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2000022# 10 #t2000022#\r\n#i2000023# 10 #t2000022#\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 57 exp")) {
            sm.sayNext("What? You don't want the potion?");
            return;
        }
        if (!sm.addItems(List.of(
                Tuple.of(2000022, 10), // Special Rien Red Potion
                Tuple.of(2000023, 10) // Special Rien Blue Potion
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(57);
        sm.forceCompleteQuest(21012);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(Even if you're really the hero everyone says you are... What good are you without any skills?)#k");
    }

    @Script("q21013s")
    public static void q21013s(ScriptManager sm) {
        // A Gift for the Hero (21013 - start)
        if (sm.askMenu("Ah, you're the hero. I've been dying to meet you.", Map.of(0, "(Seems a bit shy...)")) != 0) {
            return;
        }
        if (!sm.askAccept("I have something I've been waiting to give you as a gift for a very long time... I know you're busy, especially since you're on your way to town, but will you accept my gift?")) {
            sm.sayNext("I'm sure it will come in handy during your journey. Please, don't decline my offer.");
            return;
        }
        sm.forceStartQuest(21013);
        sm.sayNext("The parts of the gift have been packed inside a box nearby. Sorry to trouble you, but could you break the box and bring me a #b#t4032309##k and some #b#t4032310##k? I'll assemble them for you right away.");
        sm.write(UserLocal.tutorMsg(18, 4000));
    }

    @Script("q21013e")
    public static void q21013e(ScriptManager sm) {
        // A Gift for the Hero (21013 - end)
        sm.sayNext("Ah, you've brought all the components. Give me a few seconds to assemble them... Like this... And like that... and...\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#v3010062# 1 #t3010062#\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 95 exp");
        sm.removeItem(4032309);
        sm.removeItem(4032310);
        if (!sm.addItem(3010062, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(95);
        sm.forceCompleteQuest(21013);
        sm.sayBoth("Here, a fully-assembled chair, just for you! I've always wanted to give you a chair as a gift, because I know a hero can occasionally use some good rest. Tee hee.");
        sm.sayBoth("A hero is not invincible. A hero is a human. I'm sure you will face challenges and even falter at times. But you are a hero because you have what it takes to overcome any obstacles you may encounter.");
        sm.write(UserLocal.tutorMsg(19, 4000));
    }

    @Script("q21015s")
    public static void q21015s(ScriptManager sm) {
        // Basic Fitness Training 1 (21015 - start)
        sm.sayNext("Alright. I've done enough explaining for now. Let's move on to the next stage. What's the next stage, you ask? I just told you. Train as hard as you can until you become strong enough to defeat the Black Mage with a single blow.");
        sm.sayBoth("You may have been a hero in the past, but that was hundreds of years ago. Even if it weren't for the curse of the Black Mage, all those years you spent frozen in time have stiffened your body. You must loosen up a bit and slowly regain your agility. How do you do that, you ask?");
        if (!sm.askAccept("Don't you know that you must first master the fundamentals? So the wise thing to do is begin with #bBasic Training#k. Oh, of course, I forgot that you lost your memory. Well, that's why I'm here. You'll just have to experience it yourself. Shall we begin?")) {
            sm.sayNext("What are you so hesitant about? You're a hero! You gotta strike while the iron is hot! Come on, let's do this!");
            return;
        }
        sm.forceStartQuest(21015);
        sm.setNotCancellable(true);
        sm.sayNext("The population of Rien may be mostly Penguins, but even this island has monsters. You'll find #o0100131#s if you go to #b#m140020000##k, located on the right side of the town. Please defeat #r10 of those #o0100131#s#k. I'm sure you'll have no trouble defeating the #o0100131#s that even the slowest penguins here can defeat.");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
    }

    @Script("q21016s")
    public static void q21016s(ScriptManager sm) {
        // Basic Fitness Training 2 (21016 - start)
        if (!sm.askAccept("Shall we continue with your Basic Training? Before accepting, please make sure you have properly equipped your sword and your skills and potions are readily accessible.")) {
            sm.sayNext("Are you not ready to hunt the #o0100132#s yet? Always proceed if and only if you are fully ready. There's nothing worse than engaging in battles without sufficient preparation.");
            return;
        }
        sm.forceStartQuest(21016);
        sm.setNotCancellable(true);
        sm.sayNext("Alright. This time, let's have you defeat #r#o0100132#s#k, which are slightly more powerful than #o0100131#s. Head over to #b#m140020100##k and defeat #r15#k of them. That should help you build your strength. Alright! Let's do this!");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
    }

    @Script("q21017s")
    public static void q21017s(ScriptManager sm) {
        // Basic Fitness Training 3 (21017 - start)
        sm.setFlipSpeaker(true);
        sm.sayNext("It seems like you're warmed up now. This is when rigorous training can really help you build a strong foundation. Let's proceed with the Basic Training, shall we?");
        sm.sayBoth("Go defeat some #r#o0100133#s#k in #b#m140020200##k this time. I think about  #r20#k should do it. Go on ahead and... Hm? Do you have something you'd like to say?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Isn't the number getting bigger and bigger?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Of course it is. What, are you not happy with 20? Would you like to defeat 100 of them instead? Oh, how about 999 of them? Someone in Sleepywood would be able to do it easily. After all, we are training...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Oh no, no, no. Twenty is plenty.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("You don't have to be so modest. I understand your desire to quickly become the hero you once were. This sort of attitude is what makes you a hero.")) {
            sm.setPlayerAsSpeaker(true);
            sm.sayNext("#b(You declined out of fear, but it's not like you can run away like this. Take a big breath, calm down, and try again.)#k");
            return;
        }
        sm.forceStartQuest(21017);
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(You accepted, thinking you might end up having to 999 of them if you let her keep talking.)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Please go ahead and slay 20 #o0100133#s.");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
    }

    @Script("q21018s")
    public static void q21018s(ScriptManager sm) {
        // Basic Fitness Test (21018 - start)
        sm.sayNext("Now, you will undergo a test t hat will determine whether you're fit or not. All you have to do is take on the most powerful monster on this island, #o0100134#s. About #r50#k of them would suffice, but...");
        if (!sm.askAccept("We can't have you wipe out the entire population of #o0100134#s, since they aren't many of them out there. How about 5 of them? You're here to train, not to destroy the ecosystem.")) {
            sm.sayNext("Oh, is 5 not enough? If you feel the need to train further, please feel free to slay more than that. If you slay all of them, I'll just have to look the other way even if it breaks my heart, since they will have been sacrificed for a good cause...");
            return;
        }
        sm.forceStartQuest(21018);
        sm.sayNext("#o0100134#s can be found in deeper parts of the island. Continue going left until you reach #b#m140010200##k, and defeat #r5 #o0100134#s#k.");
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
    }

    @Script("q21100s")
    public static void q21100s(ScriptManager sm) {
        // The Five Heroes (21100 - start)
        sm.setFlipSpeaker(true);
        sm.sayNext("There isn't much record left of the heroes that fought against the Black Mage. Even in the Book of Prophecy, the only information available is that there were five of them. There is nothing about who they were or what they looked like. Is there anything you remember? Anything at all?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("I don't remember a thing...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("As I expected. Of course, the curse of the Black Mage was strong enough to wipe out all of your memory. But even if that's the case, there has got to be a point where the past will uncover, especially now that we are certain you are one of the heroes. I know you've lost your armor and weapon during the battle but... Oh, yes, yes. I almost forgot! Your #bweapon#k!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("My weapon?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I found an incredible weapon while digging through blocks of ice a while back. I figured the weapon belonged to a hero, so I brought it to town and placed it somewhere in the center of the town. Haven't you seen it? #bThe #p1201001##k... \r\r#i4032372#\r\rIt looks like this...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Come to think of it, I did see a #p1201001# in town.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Yes, that's it. According to what's been recorded, the weapon of a hero will recognize its rightful owner, and if you're the hero that used the #p1201001#, the #p1201001# will react when you grab the #p1201001#. Please go find the #b#p1201001# and click on it.#k")) {
            sm.sayNext("What's stopping you? I promise, I won't be disappointed even if the #p1201001# shows no reaction to you. Please, rush over there and grab the #p1201001#. Just #bclick#k on it.");
            return;
        }
        sm.forceCompleteQuest(21100);
        sm.sayOk("If the #p1201001# reacts to you, then we'll know that you're #bAran#k, the hero that wielded a #p1201001#.");
        sm.reservedEffect("Effect/Direction1.img/aranTutorial/ClickPoleArm");
    }

    @Script("q21101s")
    public static void q21101s(ScriptManager sm) {
        // The Polearm-Wielding Hero (21101 - start)
        if (!sm.askYesNo("#b(Are you certain that you were the hero that wielded the #p1201001#? Yes, you're sure. You better grab the #p1201001# really tightly. Surely it will react to you.)#k")) {
            sm.sayNext("#b(You need to think about this for a second...)#k");
        }
        // TODO
    }
}
