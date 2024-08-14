package kinoko.script.quest;

import kinoko.packet.user.UserLocal;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.job.Job;
import kinoko.world.quest.QuestRecordType;

public final class AranQuest extends ScriptHandler {
    @Script("rien")
    public static void rien(ScriptManager sm) {
        // Snow Island : Rien (140000000)
        if (sm.hasQuestCompleted(21101) && !sm.hasQRValue(QuestRecordType.AranGuideEffect, "guide=1")) {
            sm.addQRValue(QuestRecordType.AranGuideEffect, "guide=1");
            sm.write(UserLocal.openSkillGuide());
        }
    }

    @Script("enterGym")
    public static void enterGym(ScriptManager sm) {
        // Snow Island : Dangerous Forest (140010100)
        //   in00 (-1999, 86)
        if (sm.hasQuestStarted(21701)) {
            sm.playPortalSE();
            sm.warp(914010000, "out00");
        } else if (sm.hasQuestStarted(21702)) {
            sm.playPortalSE();
            sm.warp(914010100, "out00");
        } else if (sm.hasQuestStarted(21703)) {
            sm.playPortalSE();
            sm.warp(914010200, "out00");
        } else {
            sm.message("You will be allowed to enter the Penguin Training Ground only if you are receiving a lesson from Puo.");
        }
    }

    @Script("enterPort")
    public static void enterPort(ScriptManager sm) {
        // Snow Island : Snow-covered Field 3 (140020200)
        //   east00 (4769, 84)
        sm.playPortalSE();
        sm.warp(140020300, "west00");
    }

    @Script("enterInfo")
    public static void enterInfo(ScriptManager sm) {
        // Lith Harbor : Lith Harbor (104000000)
        //   in03 (405, 406)
        sm.playPortalSE();
        sm.warp(104000004, "out00");
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
        sm.setFlipSpeaker(false);
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Yes, that's it. According to what's been recorded, the weapon of a hero will recognize its rightful owner, and if you're the hero that used the #p1201001#, the #p1201001# will react when you grab the #p1201001#. Please go find the #b#p1201001# and click on it.#k")) {
            sm.setFlipSpeaker(true);
            sm.sayNext("What's stopping you? I promise, I won't be disappointed even if the #p1201001# shows no reaction to you. Please, rush over there and grab the #p1201001#. Just #bclick#k on it.");
            return;
        }
        sm.forceCompleteQuest(21100);
        sm.setFlipSpeaker(true);
        sm.sayOk("If the #p1201001# reacts to you, then we'll know that you're #bAran#k, the hero that wielded a #p1201001#.");
        sm.reservedEffect("Effect/Direction1.img/aranTutorial/ClickPoleArm");
    }

    @Script("q21101s")
    public static void q21101s(ScriptManager sm) {
        // The Polearm-Wielding Hero (21101 - start)
        if (!sm.askYesNo("#b(Are you certain that you were the hero that wielded the #p1201001#? Yes, you're sure. You better grab the #p1201001# really tightly. Surely it will react to you.)#k")) {
            sm.sayNext("#b(You need to think about this for a second...)#k");
            return;
        }
        if (!sm.addItem(1142129, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.setJob(Job.ARAN_1);
        sm.forceCompleteQuest(21101);
        sm.setNotCancellable(true);
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(You might be starting to remember something...)#k");
        sm.setDirectionMode(true, 0);
        sm.warp(914090100);
    }

    @Script("q21700s")
    public static void q21700s(ScriptManager sm) {
        // New Beginnings (21700 - start)
        sm.setFlipSpeaker(true);
        sm.sayNext("It seems like you've started to remember things. Your Polearm must have recognized you. This means you are surely #bAran, the wielder of Polearms#k. Is there anything else you remember? Skills you used with the Polearm perhaps? Anything?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(You tell her that you remember a few skills.)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("That's not a lot, but it's progress. Our focus, then, should be to get you back to the state before you were frozen. You may have lost your memory, but I'm sure it won't take long for you to recover the abilities that your body remembers.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("How do I recover my abilities?");
        sm.setFlipSpeaker(false);
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("There is only one way to do that. Train! Train! Train! Train! If you continue to train, your body will instinctively remember its abilities. To help you through the process, I'll introduce you to an instructor.")) {
            sm.sayNext("No? Are you saying you can train on your own? I'm just letting you know that you'll get better results if you train with an instructor. You can't live in this world alone. You must learn to get along with other people.");
            return;
        }
        if (!sm.addItem(1442000, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(21700);
        sm.sayNext("I gave you a #bPolearm#k because I figured it would be best for you to use a weapon you're familiar with. It will be useful in your training.");
        sm.sayPrev("You'll find a Training Center if you exit to the #bleft#k. There, you'll meet #b#p1202006##k. I'm a bit worried because I think he may be struggling with bouts of Alzheimer's, but he spent a long time researching skills to help you. I'm sure you'll learn a thing or two from him.");
    }

    @Script("q21703s")
    public static void q21703s(ScriptManager sm) {
        // Train or Die! 3 (21703 - start)
        sm.sayNext("Your abilities are really beginning to take shape. I am surprised that an old man like me was able to help you. I'm tearing up just thinking about how happy it makes me to have been of assistance to you. *Sniff sniff*");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(You didn't even train that long with him... Why is he crying?)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Alright, here's the third and the final stage of training. Your last opponent is... #r#o9300343#s#k! Do you know anything about #o1210100#s?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Well, a little bit...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("They are natural warriors! They're born with a voracious appetite for food. They devour any food that's visible the moment they sweep by. Terrifying, isn't it?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Is that really true?)#k");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Okay, now... #bEnter the Training Center again#k, defeat #r30#k #o9300343#s, and show me what you're made of! You'll have to exert all your energy to defeat them! Go, go, go! Rise above me!")) {
            sm.sayNext("I know it takes an incredible amount of strength and will to outdo your instructor, but you weren't meant to let yourself wither away. You must move on to bigger and better things! You must do everything you can to embrace your heroic nature!");
            return;
        }
        sm.forceStartQuest(21703);
        sm.sayOk("Now go and take on those monstrous #o9300343#s!");
    }

    @Script("q21703e")
    public static void q21703e(ScriptManager sm) {
        // Train or Die! 3 (21703 - end)
        sm.sayNext("Ah, you've come back after defeating all 30 #o9300343#s. I knew you had it in you... Even though you have no memories and few abilities, I could see that you were different! How? Because you're carrying around a Polearm, obviously!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Is he pulling your leg?)#k");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askYesNo("I have nothing more to teach you, as you've surpassed my level of skill. Go now! Don't look back! This old man is happy to have served as your instructor.")) {
            sm.sayNext("Are you reluctant to leave your instructor? *Sniff sniff* I'm so moved, but you can't stop here. You are destined for bigger and better things!");
            return;
        }
        sm.addSkill(21000000, 0, 10); // Combo Ability
        sm.addExp(2000);
        sm.forceCompleteQuest(21703);
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("(You remembered the #bCombo Ability#k skill! You were skeptical of the training at first, since the old man suffers from Alzheimer's and all, but boy, was it effective!)");
        sm.setPlayerAsSpeaker(false);
        sm.sayPrev("Now report back to #p1201000#. I know she'll be ecstatic when she sees the progress you've made!");
    }

    @Script("q21704s")
    public static void q21704s(ScriptManager sm) {
        // Baby Steps (21704 - start)
        sm.sayNext("How did the training go? The Penguin Teacher #p1202006# likes to exaggerate and it worried me knowing that he has bouts of Alzheimer's, but I'm sure he helped you. He's been studying the skills of heroes for a very long time.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(You tell her that you were able to remember the Combo Ability skill.)#k");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("That's great! Honestly, though, I think it has less to do with the method of #p1202006#'s training and more to do with your body remembering its old abilities. #bI'm sure your body will remember more skills as you continue to train#k!  \r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 500 exp")) {
            return;
        }
        sm.addExp(500);
        sm.forceCompleteQuest(21704);
    }
}
