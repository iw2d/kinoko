package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.script.common.ScriptMessageParam;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

public final class ResistanceTutorial extends ScriptHandler {

    private static final int JUN = 2159000, ULRIKA = 2159001, VON = 2159002;

    private static final int VITA = 2159006, VITA_FREE = 2159007, J = 2159010;
    private static final int SCHILLER = 2159008, GELIMER = 2159012;

    private static final int MAP_HIDESEEK = 931000001, MAP_LAB1 = 931000010, MAP_LAB2 = 931000011, MAP_LAB4 = 931000013;
    private static final int MAP_ESCAPE1 = 931000020, MAP_ESCAPE2 = 931000021;
    private static final int EDELSTEIN = 310000000;

    //region Npcs
    @Script("talk2159000")
    public static void talk2159000(ScriptManager sm) {
        // Jun (2159000)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000000)
        sm.setSpeakerId(JUN);
        sm.sayNext("I'm glad you made it. Safety in numbers, right? I feel like we're being watched... Shouldn't we think about heading back? The grown-ups in town say the mines aren't safe...");
        sm.setSpeakerId(VON);
        sm.sayNext("Sheesh, why are you such a scaredy cat? We've come all this way! We should at least do something before we go back.");
    }

    @Script("talk2159001")
    public static void talkUlrika(ScriptManager sm) {
        // Ulrika (2159001)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000000)
        sm.sayNext("There you are, #h0#! You're late. Get over here.", ScriptMessageParam.FLIP_SPEAKER);

        sm.setSpeakerId(VON);
        sm.sayBoth("What was the hold up? You scared or something?", ScriptMessageParam.SPEAKER_ON_RIGHT);

        sm.sayBoth("Don't be ridiculous.", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.setSpeakerOnRight(true);
        sm.setSpeakerId(JUN);
        sm.sayBoth("You're not s-s-scared at all? I am, a little b-b-bit... The grown-ups warned us never to venture into the #bVerne Mines#k... Plus, there are all those #rBlack Wings#k around, watching us, I just know it.");

        sm.setSpeakerId(VON);
        sm.sayBoth("We snuck here, Jun. No one saw us. No one's watching us, okay? Come on, when else would we have ever gotten the chance to leave #bEdelstein#k? Don't be a chicken.");

        sm.setSpeakerId(JUN);
        sm.sayBoth("But what if we get in trouble?");
        sm.setSpeakerOnRight(false);

        sm.setSpeakerId(ULRIKA);
        sm.sayBoth("Jun, we're already here. If we're going to get in trouble, let's at least have some fun first. Let's play hide-and-seek!", ScriptMessageParam.FLIP_SPEAKER);

        sm.sayBoth("Hide and seek?", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.setSpeakerId(VON);
        sm.sayBoth("Ugh, la-ame.", ScriptMessageParam.SPEAKER_ON_RIGHT);

        sm.setSpeakerId(ULRIKA);
        sm.sayBoth("Don't be a brat, Von. What? Are you scared to hide all by yourself in these big, bad caves? *snicker*\r\n#h0#, since you were late, you're it. Count to 10 and then come find us. No peeking.", ScriptMessageParam.FLIP_SPEAKER);

        sm.warp(MAP_HIDESEEK);
    }

    @Script("talk2159002")
    public static void talkVon(ScriptManager sm) {
        // Von (2159002)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000000)
        sm.sayNext("If Jun's too chicken, let's leave him here. But why's it have to be hide-and-seek? Let's play something cool...");
        sm.sayPrev("That's not what I said...");
    }

    @Script("talk2159013")
    public static void talkCutie(ScriptManager sm) {
        // Cutie (2159013)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000000)
        sm.sayOk("My heart is pounding, but this is kind of exciting. We're going to get in so much trouble if we're caught, though.");
    }

    @Script("talk2159014")
    public static void talkFattie(ScriptManager sm) { //No clue what this should be
    }

    @Script("talk2159003")
    public static void findJun(ScriptManager sm) {
        // Jun (2159003)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        final User user = sm.getUser();

        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp1=1")) {
            sm.sayNext("Eep! You found me.");
            sm.sayBoth("Eh, I wanted to go further into the wagon, but my head wouldn't fit.");
            sm.sayBoth("Did you find Ulrika and Von yet? Von is really, really good at hiding.\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 5 exp");
            user.addExp(5);
            sm.addQRValue(QuestRecordType.ResistanceHideSeek, "exp1=1");
        } else {
            sm.sayNext("Did you find Ulrika and Von yet? Von is really, really good at hiding.");
        }
    }

    @Script("talk2159004")
    public static void findUlrika(ScriptManager sm) {
        // Ulrika (2159004)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        final User user = sm.getUser();

        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp2=1")) {
            user.addExp(5);
            sm.addQRValue(QuestRecordType.ResistanceHideSeek, "exp2=1");
            sm.sayNext("Haha, you found me. Guess I should've found a better hiding spot.");
            sm.sayBoth("Have you found Jun and Von yet? Von's going to be pretty hard to find. Better keep your eyes open.\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 5 exp");
        } else {
            sm.sayNext("Have you found Jun and Von yet? Von's going to be pretty hard to find. Better keep your eyes open.");
        }
    }

    @Script("talk2159005")
    public static void findVon(ScriptManager sm) {
        // Von (2159005)
        //   Dangerous Hide-and-Seek : Behind the Mine (931000030)
        sm.sayNext("Aww, you found me. I thought I found a great spot, too."); //Unsure if GMS-like
    }

    @Script("talk2159015")
    public static void findCutie(ScriptManager sm) {
        // Cutie (2159015)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp3=1")) {
            sm.addExp(3);
            sm.addQRValue(QuestRecordType.ResistanceHideSeek, "exp3=1");
            sm.sayNext("Aw shucks. You found me. Wow, you're really good at this game!\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 3 exp");
        } else {
            sm.sayNext("Hehehe... I should have hidden somewhere else.");
        }
    }

    @Script("talk2159016")
    public static void findFattie(ScriptManager sm) {
        // Fattie (2159016)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        final User user = sm.getUser();

        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp4=1")) {
            user.addExp(3);
            sm.addQRValue(QuestRecordType.ResistanceHideSeek, "exp4=1");
            sm.sayNext("D'oh! You found me. But I'm tiny! Are you a professional at this game or something?\r\n\r\n\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 3 exp");
        } else {
            sm.sayNext("Drats. Might as well eat another piece of candy.");
        }
    }

    @Script("talk2159011")
    public static void talk2159011(ScriptManager sm) {
        // Suspicious Hollow (2159011)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        if (sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp1=1") && sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp2=1") && sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp3=1") && sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "exp4=1")) {
            if (sm.askYesNo("#b(What a suspicious hole. Maybe Von is hiding inside. Peek inside?)#k")) {
                sm.addExp(35);
                sm.playPortalSE();
                sm.warp(MAP_LAB1);
            } else {
                sm.sayOk("#b(Even Von wouldn't hide here, right?)#k");
            }
        } else {
            sm.sayOk("#bFind your hiding friends before continuing.#k");
        }
    }

    @Script("talk2159006")
    public static void talkVitaLab(ScriptManager sm) {
        // Vita (2159006)
        //   Dangerous Hide-and-Seek : Suspicious Laboratory (931000010)
        //   Dangerous Hide-and-Seek : Suspicious Laboratory (931000011)
        //   Dangerous Hide-and-Seek : Suspicious Laboratory (931000012)
        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "vel00=1")) {
            sm.sayNext("Stay back!");

            sm.sayBoth("How did you get here? This place is prohibited!");

            sm.sayBoth("Who's talking? Where are you?!", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.sayBoth("Look up.");
            sm.addQRValue(QuestRecordType.ResistanceHideSeek, "vel00=1");
            sm.reservedEffect("Effect/Direction4.img/Resistance/ClickVel");
            return;
        }
        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "vel00=2")) {
            sm.sayNext("My name is #b#p2159006##k. I'm one of #rDoctor #p2159012#'s#k test subjects. But that's not important right now. You have to get out of here before someone sees you!");

            sm.sayBoth("Wait, what are you talking about? Someone's doing experiments on you?! And who's #p2159012#?", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.sayBoth("You've never heard of Doctor #p2159012#, the Black Wings' mad scientist? This is his lab, where he conducts experiments...on people.");

            sm.sayBoth("Experiments...on people? Are you serious?", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.sayBoth("Yes! And if he catches you here, he won't be merciful. Get out of here! Quickly!");

            sm.sayBoth("What? But what about you?!", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.sayBoth("Shhh! Did you hear that? Someone's coming! It's got to be Doctor #p2159012#! Oh no!");

            sm.addQRValue(QuestRecordType.ResistanceHideSeek, "vel00=2");
            sm.warp(MAP_LAB2);
            return;
        }

        sm.sayNext("Whew, something must have distracted them. Now's your chance. GO!");

        sm.sayBoth("But if I flee, you'll be left here alone...", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.sayBoth("Forget about me. You can't help me. Doctor #p2159012# would realize right away if I'm missing, and then he'd summon the Black Wings to look for us. No, forget me and save yourself. Please!");

        sm.sayBoth("I can't just leave you here! And you shouldn't give up hope so easily!", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.sayBoth("But it IS hopeless. I'm stuck in here. But thank you for caring. It's been a long time since anyone's been kind to me. But now, hurry! You must go!");
        if (!sm.askYesNo("#b(#p2159006# closes her eyes like she's given up. What should you do? How about trying to break open the vat?)#k")) {
            sm.sayNext("#b(You tried to hit the vat with all your might, but your hand slipped!)#k");
        }

        sm.addExp(60);
        sm.warp(MAP_LAB4);
    }

    @Script("talk2159007")
    public static void talk2159007(ScriptManager sm) {
        // Vita (2159007)
        //   Dangerous Hide-and-Seek : Suspicious Laboratory (931000013)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000020)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000021)
        //   Dangerous Hide-and-Seek : Behind the Mine (931000030)

        if (sm.getFieldId() == MAP_LAB4) {
            sm.sayBoth("Whoa. Wh-what happened? The glass is broken... Did that vibration earlier break it?");

            sm.sayBoth("Now, there's nothing stopping you right? Let's get out of here!", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.sayBoth("But...");

            sm.sayBoth("Do you WANT to stay here or something?", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.sayBoth("Of course not!");

            sm.sayBoth("Then hurry up! Let's go!", ScriptMessageParam.PLAYER_AS_SPEAKER);

            sm.warp(MAP_ESCAPE1);
        } else if (sm.getFieldId() != MAP_ESCAPE2) {
            sm.sayOk("It's been... a really long time since I've been outside the laboratory.");
        }

    }

    @Script("talk2159008")
    public static void talk2159008(ScriptManager sm) {
        // Schiller (2159008)
        //   Dangerous Hide-and-Seek : Suspicious Laboratory (931000011)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000020)
        //   Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000021)
        if (sm.getFieldId() == MAP_ESCAPE1) {
            User user = sm.getUser();

            if (!user.hasDialog()) { //Try at fixing ScriptError spam due to user.addHP()

                sm.setSpeakerId(SCHILLER);
                sm.sayNext("Little rats. I say, how DARE you try to escape this place?");
                sm.sayBoth("Shoot, we were spotted!", ScriptMessageParam.PLAYER_AS_SPEAKER);

                sm.sayBoth("Now, now, children. Don't make this harder than it needs to be. Just walk towards me, nice and easy... Wait, you're not one of the test subjects. You're one of the townspeople, aren't you?");

                sm.sayBoth("That's right. I'm a resident of Edelstein, not a test subject. You can't boss ME around.", ScriptMessageParam.PLAYER_AS_SPEAKER);

                sm.sayBoth("Oh my, oh my. I told them to make sure the townspeople kept their kids away from the mines... Alas, it's too late now. I can't allow you to tell anyone about this laboratory, so I guess you'll just have to stay here and...help with the experiments. *snicker*");

                sm.sayBoth("Hmph. Big words, but let's see if you can catch me first.", ScriptMessageParam.PLAYER_AS_SPEAKER);

                sm.sayBoth("Why, you insolent, little-- Ahem, ahem, ahem. Your words don't matter. Time for me to pull out the big guns. I do hope you're ready. If not, you will suffer.");

                user.addHp(-user.getHp() / 2);
                sm.sayBoth("#b(Oh no! Schiller's attack HALVED your HP! He's tougher than you anticipated.)#k", ScriptMessageParam.PLAYER_AS_SPEAKER);

                sm.sayBoth("I say, got any more big words, kiddo? I'll make sure Gelimer performs some especially atrocious experiments on you. But I'll be nice if you come with me quiet-like.");

                sm.setSpeakerId(J);
                sm.sayBoth("Hold it right there!");

                sm.warp(MAP_ESCAPE2);
            }
        }
    }

    @Script("talk2159010")
    public static void talk2159010(ScriptManager sm) {
        // J (2159010)
        //   Dangerous Hide-and-Seek : Behind the Mine (931000030)

        sm.sayNext("Looks like we lost him. Of course, I could've easily handled him, no problemo, but I wasn't sure I could protect you kiddos at the same time. *chuckle* What're you two doing here anyway? Didn't your parents warn you to steer clear of the mines?", ScriptMessageParam.FLIP_SPEAKER);

        sm.setSpeakerId(VITA_FREE);
        sm.sayBoth("It's my fault! #h0# was just trying to help! #h0# rescued me!", ScriptMessageParam.SPEAKER_ON_RIGHT);

        sm.setSpeakerId(J);
        sm.sayBoth("Rescued you, eh? Hm, you are dressed kind of funny, little girl. Ooooh. Were you a prisoner of the Black Wings?", ScriptMessageParam.FLIP_SPEAKER);

        sm.setSpeakerId(VITA_FREE);
        sm.sayBoth("#b(#p2159006# quickly explains the situation.)#k", ScriptMessageParam.SPEAKER_ON_RIGHT);

        sm.setSpeakerId(J);
        sm.sayBoth("Ah, yes, I knew the Black Wings were up to something dangerous. I knew it all along. I must tell the others so we can devise a plan.", ScriptMessageParam.FLIP_SPEAKER);

        sm.sayBoth("But who are you? Where did you come from? And why did you rescue us?", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.setSpeakerId(J);
        sm.setFlipSpeaker(true);
        sm.sayBoth("I suppose I can't hide it after everything you've seen today, including but not limited to my heroic rescue and brazen bravery. *cough* You know our grand city of Edelstein is currently under the control of the Black Wings, right?");
        sm.sayBoth("The stolen mines, the occupation of City Hall, the existence of the Watchmen... They are all signs that we no longer have our liberty. Despite all that, the Black Wings will never rule our hearts!");
        sm.sayBoth("I am a proud member of the Resistance, a group secretly fighting and undermining the Black Wings. I cannot tell you who I am, but I go by the codename of J.");
        sm.sayBoth("Now, please return to town and stay away from the mines. As for you, #p2159006#, come with me. If you're left unprotected, I fear the Black Wings will come look for you. No one can keep you safe like I can! Now, keep my words a secret. The fate of the Resistance depends on your discretion.");
        sm.setFlipSpeaker(false);

        sm.sayBoth("Wait, before you go, tell me one thing. How can I join the Resistance?", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.setSpeakerId(J);
        sm.sayBoth("Ah, little youngling, so you wish to fight the Black Wings, do you? Your heart is noble, but there is little you can do to aid our efforts until you reach Lv. 10. Do so, and I will have someone from the Resistance contact you. That's a promise, kiddo. Now, I must be off, but perhaps we will meet again someday!", ScriptMessageParam.FLIP_SPEAKER);

        sm.forceCompleteQuest(23007);
        sm.addExp(90);
        sm.addItem(2000000, 3);
        sm.addItem(2000003, 3);
        sm.warp(EDELSTEIN, "st00");
    }

    @Script("talk2159012")
    public static void talk2159012(ScriptManager sm) {
        // Gelimer (2159012)
        //   Dangerous Hide-and-Seek : Suspicious Laboratory (931000011)
        sm.setNotCancellable(true); //Not GMS-like, being able to cancel this dialog is awkward.
        sm.sayNext("The experiment is going well, quite well. The endless supply of Rue is certainly speeding things along. Joining the Black Wings was a wise decision, a wise decision indeed. Muahaha!");

        sm.setSpeakerId(SCHILLER);
        sm.sayBoth("I say, you have great foresight about these things.");

        sm.setSpeakerId(GELIMER);
        sm.sayBoth("The android the Black Wings wanted will be completed soon. Oh yes, very soon. Then, the next stage will begin! I will conduct an experiment wilder than their wildest dreams!");

        sm.setSpeakerId(SCHILLER);
        sm.sayBoth("Pardon? The next stage?");

        sm.setSpeakerId(GELIMER);
        sm.sayBoth("Teeheehee, do you still not comprehend what I'm trying to create? Look around! Here's a clue: it's eons more interesting than a simple android. Eons more interesting.");

        sm.setSpeakerId(SCHILLER);
        sm.sayBoth("What?? All these test subjects... I say, sir, just what are you planning to do?");

        sm.setSpeakerId(GELIMER);
        sm.sayBoth("Now, now, you may not understand the grandness of my experiments. I don't expect you to. No, I don't expect you to. Just focus on your job and make sure none of the test subjects run away.");
        sm.sayBoth("Hey... Did you hear that?");

        sm.setSpeakerId(SCHILLER);
        sm.sayBoth("Huh? Well... Now that you mention it, I do hear something. Yes, I do hear something...");

        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction4.img/Resistance/TalkInLab");
    }

    @Script("Resi_tutor10")
    public static void Resi_tutor10(ScriptManager sm) { //Unsure if GMS-like, not sure what this should do
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000000)
        sm.setQRValue(QuestRecordType.EdelsteinUnlockTownQuests, "1"); //Added this here so even perma-citizens can get the town quests.
    }

    @Script("Resi_tutor20")
    public static void Resi_tutor20(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        sm.screenEffect("resistance/tutorialGuide");
        sm.forceStartQuest(23007);
    }

    @Script("Resi_tutor30")
    public static void Resi_tutor30(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Suspicious Laboratory (931000010)
        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk");
    }

    @Script("Resi_tutor40")
    public static void Resi_tutor40(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Suspicious Laboratory (931000011)
        sm.setSpeakerId(GELIMER);
        talk2159012(sm);
    }

    @Script("Resi_tutor50")
    public static void Resi_tutor50(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Suspicious Laboratory (931000012)
        sm.setDirectionMode(false, 0);
        sm.setSpeakerId(VITA);
        talkVitaLab(sm);
    }

    @Script("Resi_tutor50_1")
    public static void Resi_tutor50_1(ScriptManager sm) {
    }

    @Script("Resi_tutor60")
    public static void Resi_tutor60(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000020)
        sm.setSpeakerId(VITA_FREE);
        sm.sayNext("It's been...a really long time since I've been outside the laboratory. Where are we?");

        sm.sayBoth("This is the road that leads to Edelstein, where I live! Let's get out of here before the Black Wings follow us.", ScriptMessageParam.PLAYER_AS_SPEAKER);

        sm.avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
    }

    @Script("Resi_tutor70")
    public static void Resi_tutor70(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000021)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction4.img/Resistance/TalkJ");
    }

    @Script("Resi_tutor80")
    public static void Resi_tutor80(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Behind the Mine (931000030)
        sm.setDirectionMode(false, 0);
    }

    @Script("Resi_tutor11")
    public static void startHideSeek(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000000)
        //   tutor00 (-152, -19)
        sm.setSpeakerId(ULRIKA);
        talkUlrika(sm);
    }

    @Script("Resi_tutor31")
    public static void labVita(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Suspicious Laboratory (931000010)
        //   tutor00 (841, -33)
        if (!sm.hasQRValue(QuestRecordType.ResistanceHideSeek, "vel00=1")) {
            sm.setSpeakerId(VITA);
            talkVitaLab(sm);
        }
    }

    @Script("Resi_tutor61")
    public static void Resi_tutor61(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000020)
        //   tutor00 (-37, -20)
        //   tutor01 (-136, -20)
        //   tutor02 (-236, -20)
        talk2159008(sm);
    }

    @Script("in2159011")
    public static void in2159011(ScriptManager sm) {
        // Dangerous Hide-and-Seek : Neglected Rocky Mountain (931000001)
        //   in00 (1440, 27)
        sm.setSpeakerId(2159011);
        talk2159011(sm);
    }

    @Script("q23005ing")
    public static void q23005ing(ScriptManager sm) {
        // Edelstein Message Board (2152019)
        //   Black Wing Territory : Edelstein (310000000)
        if (sm.hasQuestStarted(23005) && sm.hasItem(4032783)) {
            sm.removeItem(4032783);
            sm.setQRValue(QuestRecordType.ResistanceCheckyFlier, "1");
            sm.sayNext("You pin the poster to the message board.");
            return;
        }
        sm.sayOk("It's a message board for Edelstein's Free Market. Supposedly, anyone can put up a poster, but the board is covered with propaganda about the Black Wings.");
    }
}
