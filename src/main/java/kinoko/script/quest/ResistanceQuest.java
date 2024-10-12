package kinoko.script.quest;

import kinoko.packet.user.QuestPacket;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.script.common.ScriptMessageParam;
import kinoko.util.Tuple;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.util.List;

public class ResistanceQuest extends ScriptHandler {

    @Script("q23100e")
    public static void q23100e(ScriptManager sm) {
        // A Student of the Resistance (23100 - end)
        sm.sayNext("You joined the Resistance? I knew we were short on members... Guess we're accepting anyone these days. That #p2151001# is a lot softer than he looks.");
        sm.sayNext("Well, since you're part of our group now, you should train and level up. I'll teach you what you need to know to be a contributing member of the Resistance.");
        sm.setQRValue(QuestRecordType.ResistanceTraining, "1"); //Without this, you can't accept the training quests.
        sm.forceCompleteQuest(23100);
    }

    @Script("q23101e")
    public static void q23101e(ScriptManager sm) {
        // A Student of the Resistance (23101 - end)
        sm.sayNext("You joined the Resistance? I knew we were short on members... Guess we're accepting anyone these days. That #p2151001# is a lot softer than he looks.");
        sm.sayNext("Well, since you're part of our group now, you should train and level up. I'll teach you what you need to know to be a contributing member of the Resistance.");
        sm.setQRValue(QuestRecordType.ResistanceTraining, "1"); //Without this, you can't accept the training quests.
        sm.forceCompleteQuest(23101);
    }

    @Script("q23102e")
    public static void q23102e(ScriptManager sm) {
        // A Student of the Resistance (23102 - end)
        sm.sayNext("You joined the Resistance? I knew we were short on members... Guess we're accepting anyone these days. That #p2151001# is a lot softer than he looks.");
        sm.sayNext("Well, since you're part of our group now, you should train and level up. I'll teach you what you need to know to be a contributing member of the Resistance.");
        sm.setQRValue(QuestRecordType.ResistanceTraining, "1"); //Without this, you can't accept the training quests.
        sm.forceCompleteQuest(23102);
    }

    @Script("q23107e")
    public static void q23107e(ScriptManager sm) {
        // The First Mission (23107 - end)
        sm.sayNext("Welcome #h0#. As you already know, I'm in charge of Resistance mission assignments.");
        sm.sayNext("I should actually be a Thief job instructor, but I've taken this position since the Resistance doesn't train thieves. It's the same as why #p2151000#, who should be a Warrior Job Instructor, is in charge of education.");
        sm.sayNext("In any case, since I'm in charge of missions, you'll be seeing me more often than even #p2151001#, your job instructor. Now, let's drive those Black Wings out of our territory.");
        sm.setQRValue(QuestRecordType.ResistanceFirstMission, "1");
        sm.forceCompleteQuest(23107);
    }

    @Script("q23108e")
    public static void q23108e(ScriptManager sm) {
        // The First Mission (23108 - end)
        sm.sayNext("Welcome #h0#. As you already know, I'm in charge of Resistance mission assignments.");
        sm.sayNext("I should actually be a Thief job instructor, but I've taken this position since the Resistance doesn't train thieves. It's the same as why #p2151000#, who should be a Warrior Job Instructor, is in charge of education.");
        sm.sayNext("In any case, since I'm in charge of missions, you'll be seeing me more often than even #p2151002#, your job instructor. Now, let's drive those Black Wings out of our territory.");
        sm.setQRValue(QuestRecordType.ResistanceFirstMission, "1");
        sm.forceCompleteQuest(23108);
    }

    @Script("q23109e")
    public static void q23109e(ScriptManager sm) {
        // The First Mission (23109 - end)
        sm.sayNext("Welcome #h0#. As you already know, I'm in charge of Resistance mission assignments.");
        sm.sayNext("I should actually be a Thief job instructor, but I've taken this position since the Resistance doesn't train thieves. It's the same as why #p2151000#, who should be a Warrior Job Instructor, is in charge of education.");
        sm.sayNext("In any case, since I'm in charge of missions, you'll be seeing me more often than even #p2151004#, your job instructor. Now, let's drive those Black Wings out of our territory.");
        sm.setQRValue(QuestRecordType.ResistanceFirstMission, "1");
        sm.forceCompleteQuest(23109);
    }

    @Script("q2345s")
    public static void q2345s(ScriptManager sm) {
        // Endangered Mushking Empire (2345 - start)
        if(!sm.askYesNo("#h0#, I know you're busy carrying out your Resistance missions, but could you spare me a moment? I received a request for help from outside, and I can't think of anyone better than you.")){
            sm.sayOk("Really? It's an urgent matter, so if you have some time, please see me."); //Unsure if this is GMS-like.
            return;
        }
        sm.sayNext("#bMushking Empire#k is in great danger right now. Their former Emperor is seriously ill... something terrible must have happened! Mushking Empire is located near Henesys. Please hurry!");
        sm.sayBoth("Unlike the Cygnus Knights, who declined Edelstein's help during a time of need, members of the Resistance cannot just stand back and watch others suffer. Please, go save the Mushking Empire from danger. Here is a recommendation letter.");
        boolean decision = sm.askYesNo("Mushking Empire is near Henesys. If you say yes, I'll send you to the Mushking Empire right away.");
        if(!sm.addItem(4032375, 1)) {
            sm.sayOk("Please make some space in your etc inventory."); //Unsure if this is GMS-like.
            return;
        }
        sm.forceStartQuest(2345);
        if(!decision) {
            sm.sayOk("Are you planning to walk all the way there? If so, please hurry. You can get to the Mushking Empire #bby heading west from Ghost Mushroom Forest#k, where the Henesys Mushroom Forest ends. #b<Theme Dungeon: Mushroom Castle>#k is its entrance.");
            return;
        }

        if (sm.getQRValue(QuestRecordType.MushroomCastleOpening).equals("1")) {
            sm.playPortalSE();
            sm.warp(106020000, "left00"); // Mushroom Castle : Mushroom Forest Field
        } else {
            sm.warp(106020001); // TD_MC_Openning
        }
    }

    @Script("q2345e")
    public static void q2345e(ScriptManager sm) {
        // Endangered Mushking Empire (2345 - end)
        sm.sayNext("Huh? #bRecommendation Letter from a job instructor#k! What's this? You're the one sent here to save our Mushking Kingdom?");
        sm.sayBoth("Y...Yesss?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayBoth("Hmm, I see. Well, if a job instructor recommended you, I will put my trust in you as well. I apologize for my late introduction. I am the #bHead Security Officer#k in charge of the royal family's security. As you can see, I am currently in charge of security over this temporary housing and the key figures inside. We're not in the best of situations, but nevertheless, let me welcome you to the Mushking Empire.");
        sm.removeItem(4032375);
        sm.setQRValue(QuestRecordType.MushroomCastleOpening, "1");
        sm.forceCompleteQuest(2345);
    }

    @Script("q23127s")
    public static void q23127s(ScriptManager sm) {
        // Protecting Surl (23127 - start)
        sm.sayNext("It's quiet. Too quiet. Is someone really after me? To think someone would want to hurt an old man like me... Those Black Wings are truly cowards. Still, I'm not worried. I've been through too much in life. They can't scare me!");
        sm.sayBoth("#b(#p2159201# doesn't seem scared at all. How brave.)#k", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayBoth("I think the Black Wings are too chicken to come out with you around. Let's find a way to lure them out.");
        sm.sayBoth("#bBut if I leave your side, you'll be in danger. You're the one they're after...#k", ScriptMessageParam.PLAYER_AS_SPEAKER);
        if(sm.askAccept("Heh, you think they can frighten this old man. Nah, I trust you. You're a strong member of the Resistance. You'll keep me safe. Now, let's go someplace more secluded, where the Black Wings will feel safe enough to show their faces.")) {
            sm.forceStartQuest(23127);
            sm.warpInstance(931000441, "out00", 931000440, 60);
            //Should start a 55 second long time limit to keep Surl safe. Referenced current GMS for this.
            //TODO: Handle QuestCheck: fieldset, fieldsetkeeptime.
            sm.write(QuestPacket.startTimeKeepQuestTimer(23127, 55000)); //startQuestTimer crashes the game, startTimeKeepQuestTimer doesn't end...
            sm.message("Protect Surl from the Black Wings for a set amount of time!");
            return;
        }
        sm.sayOk("I think the Black Wings are too chicken to come out with you around."); //Not GMS-like, unsure what he'd say
    }

    @Script("enterResi_23120")
    public static void enterResi_23120(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   in05 (915, 581)
        sm.playPortalSE();
        sm.warpInstance(931000410, "out00", 310000000, 10 * 60);
    }

    @Script("enterDelivery")
    public static void enterDelivery(ScriptManager sm) {
        // Concrete Road : Serpent Path (310030110)
        //   in00 (565, -556)
        if(!sm.hasQuestStarted(23125)) {
            return;
        }
        sm.playPortalSE();
        sm.warp(931000430, "out00");
    }

    @Script("enterSuar")
    public static void enterSuar(ScriptManager sm) {
        // Concrete Road : Edelstein Park 2 (310020100)
        //   in00 (862, -674)
        if(sm.hasQuestCompleted(23127)) {
            return;
        }
        sm.playPortalSE();
        sm.warp(931000440, "out00");
    }

    @Script("edelScript0")
    public static void edelScript0(ScriptManager sm) {
        // edelScript0 (3109000)
        //   Edelstein : Surl's Water Cellar (931000410)
        sm.setQRValue(QuestRecordType.ResistanceWaterTank, "1");
    }

    @Script("jaguar_in")
    public static void jaguar_in(ScriptManager sm) {
        // Black Jack (2151008)
        //   Resistance Headquarters : Secret Plaza (310010000)
        User user = sm.getUser();
        if (!JobConstants.isWildHunterJob(user.getJob())) {
            sm.sayOk("Grrrr....\r\n(You can't enter. Only Wild Hunters may enter.)");
            return;
        }

        if(sm.askAccept("Enter Jaguar Habitat?")) {
            sm.warp(931000500);
        }
    }

    @Script("giveWater")
    public static void giveWater(ScriptManager sm) {
        // Ace (2159202)
        //   Edelstein : Danger! Makeshift Airport (931000420)
        User user = sm.getUser();
        if(user.getField().getMobPool().isEmpty()) {
            sm.setQRValue(QuestRecordType.ResistanceWaterTrade, "1");
            sm.sayOk("Whew, we're safe now. Let's trade the water now.");
            sm.warp(310000010, "out00");
        }
    }

    @Script("q23011e")
    public static void q23011e(ScriptManager sm) {
        // Path of the Battle Mage (23011 - end)

        if(sm.askYesNo("So you've finally decided to become a Battle Mage, eh? Well, you can still change your mind. Just stop our conversation, forfeit this quest, and talk to another class trainer. So, you sure you want to become a Battle Mage? I'm not interested in teaching unless you're a hundred percent sure...")) {
            if (!sm.addItems(List.of(
                    Tuple.of(1382100, 1),
                    Tuple.of(1142242, 1)
            ))) {
                sm.sayNext("Whoa! Why are you carrying so many things? I was going to give you a gift but there isn't enough room in the Equip tab of your inventory.");
                return;
            }
            sm.setJob(Job.BATTLE_MAGE_1);
            sm.addSp(5);
            sm.forceCompleteQuest(23011);
            sm.forceCompleteQuest(29941);
            sm.sayNext("Okay, okay. Welcome to the Resistance, kid. From now on, you will play the role of a Battle Mage, a fierce Magician always ready to lead your party into battle.");
            sm.sayBoth("But don't go spreading it around that you're a Battle Mage, hm No need to tempt the Black Wings to come after you. From now on, I'll be your teacher. If anyone asks, you're visiting me just as a regular student, not as a member of the Resistance. I'll give you special lessons now and then. You better not fall asleep in class, hear?");
        }
    }

    @Script("q23012e")
    public static void q23012e(ScriptManager sm) {
        // Path of the Wild Hunter (23012 - end)

        if(sm.askYesNo("I applaud your spirit! But are you certain about this? Wild Hunters are very strong, but they're also difficult to control. You have to control your mount and attack at the same time. It requires excellent reflexes. Are you sure you're up for a job like this?")) {
            if (!sm.addItems(List.of(
                    Tuple.of(1462092, 1),
                    Tuple.of(1142242, 1),
                    Tuple.of(2061000, 2000),
                    Tuple.of(2061000, 2000),
                    Tuple.of(2061000, 2000)
            ))) {
                sm.sayNext("I was going to give you a gift for making the job advancement but I can't. Your Inventory Equip or Use tab is full. Empty out at least three slots if you're interested in my gift.");
                return;
            }
            sm.setJob(Job.WILD_HUNTER_1);
            sm.addSkill(30001061, 1, 0);
            sm.addSkill(30001062, 1, 0);
            sm.addSp(5);
            sm.forceCompleteQuest(23012);
            sm.forceCompleteQuest(29941);
            sm.sayNext("Well, well! Congratulations! You're now an official member of the Resistance and a Wild Hunter. Hop on your mount, move like the wind, and slay all enemies who get in your way!");
            sm.sayPrev("Now, a warning. Don't lure the Black Wings' attention to you by telling people you're a Wild Hunter. I'll be your \"teacher\" from now on. This IS a school after all, right? I'll give you special lessons to turn you into the best Wild Hunter ever!");
        }
    }

    @Script("q23013e")
    public static void q23013e(ScriptManager sm) {
        // Path of a Mechanic (23013 - end)

        if(sm.askYesNo("Have you made your decision to become a Mechanic? You can still change your mind, you know. Just stop the conversation, forfeit this quest, and talk to another job trainer. So, are you certain becoming a Mechanic is the best way for you to serve the Resistance?")) {
            if (!sm.addItems(List.of(
                    Tuple.of(1492014, 1),
                    Tuple.of(1142242, 1)
            ))) {
                sm.sayNext("I wanted to give you a gift to commemorate your new powers but I can't. Why do you carry so many things in your Inventory's Equip tab?");
                return;
            }
            sm.setJob(Job.MECHANIC_1);
            sm.addSkill(30001068, 1, 0);
            sm.addSp(5);
            sm.forceCompleteQuest(23013);
            sm.forceCompleteQuest(29941);
            sm.sayNext("Welcome to the Resistance. From now on, you are a Mechanic. As one who works with machines, use every method available to defeat the enemies before you!");
            sm.sayBoth("We have to be careful that our identity is not revealed to the Black Wings. So from now on, refer to me as teacher. You will pretend to be a student who is coming here for extracurricular lessons. It's during these lessons that I will teach you to become a strong Mechanic.");
        }
    }

    @Script("q23015s")
    public static void q23015s(ScriptManager sm) {
        // Taming a Jaguar (23015 - start)
        sm.sayNext("Wild Hunters must have a Mount. When you became a Wild Hunter, you should have gotten the Capture skill. You can use that skill to tame and ride a Jaguar.");
        sm.sayBoth("You can find the #s30001061# skill in your skill window. After you attack a Jaguar and get it down to half life, you can use the Capture skill to capture it. Then, use the #s33001001# skill to ride it. Simple, right?");
        sm.sayBoth("You would like to know where you can find some Jaguars? #p2151008#, sitting here in front of me, will lead you to them.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Umm, #p2151008#? Can you tell me where I should go?");
        sm.setPlayerAsSpeaker(false);
        sm.setSpeakerId(2151008); //Black Jack
        sm.sayBoth("Hmm, a new Wild Hunter? You are still a rookie.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("Although I am still weak, I will work hard to become a valuable member of the Resistance. Now, where can I find the Jaguars?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("You have a good attitude. I will let you meet my brothers. Talk to me whenever you want to meet with them.");
        sm.forceStartQuest(23015);
        sm.forceCompleteQuest(23015);
        sm.warp(931000500);
    }

    @Script("summonSchiller")
    public static void summonSchiller(ScriptManager sm) {
        // Hidden Street : 2nd Job Advancement (931000100)
        // Hidden Street : 2nd Job Advancement (931000101)
        // null (931000102)
        // null (931000103)
        // null (931000104)
        sm.spawnNpc(2159100, 180, -14, false, true); //This is snapping for some reason
        sm.message("Schiller has appeared! Defeat him and take the Report!");
    }

    @Script("SecJob_Schiller")
    public static void SecJob_Schiller(ScriptManager sm) {
        // Schiller (2159100)
        sm.sayNext("Oh my. What's this? I gave specific instructions to make sure no one else used the airport at this time... But, I say, are you a member of the Resistance?");
        sm.sayNext("#b(You are surprised Schiller doesn't immediately recognize you. You certainly remember him.)#k", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayNext("Come to think of it, you do look familiar... Where have I seen you before?");
        sm.sayNext("I couldn't fight you the last time we met, but I plan to fix that today.", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayNext("You! I remember now! You stole that one test subject! Do you have any idea how much I suffered because of that? I was demoted... five times! Now I'm stuck doing menial jobs like this.\r\nTime for you to pay, oh yes.");
        sm.spawnMob(9001031, MobAppearType.NORMAL, 230, -14, true); //This is offset for some reason
        sm.removeNpc(2159100);
    }

    @Script("q23023e")
    public static void q23023e(ScriptManager sm) {
        // Revenge and Growth (23023 - end)

        sm.sayNext("You brought the Black Wings Report? Good!");
        sm.sayBoth("You know, I gave you that mission on purpose. That member of the Black Wings was the one who hurt you in the past. How's it feel to defeat someone who once seemed impossible to fight?");
        sm.sayBoth("Still, I had no idea you'd handle the mission so excellently. To be honest, I had my doubts about you. But I'm starting to think there's something... special about you.");
        if(!sm.askYesNo("Okay, I think you're ready for the next stage, a stage in which you'll be transformed into an unimaginably strong Battle Mage...")) {
            sm.sayOk("Come back when you're ready."); //Unsure if GMS-like
            return;
        }
        if (!sm.addItem(1142243, 1)) {
            sm.sayNext("Whoa! Why are you carrying so many things? I was going to give you a gift but there isn't enough room in the Equip tab of your inventory.");
            return;
        }
        sm.removeItem(4032737);
        sm.setJob(Job.BATTLE_MAGE_2);
        sm.addSp(3);
        sm.forceCompleteQuest(23023);
        sm.sayNext("I've advanced your job. I've also passed onto you skills that are much more powerful than the ones you've had before. You are now an even more powerful Battle Mage. Guess I'm a pretty good teacher, heh.");
        sm.sayPrev("I will see you at the next lesson. Until then, keep up the good fight.");
    }

    @Script("q23024e")
    public static void q23024e(ScriptManager sm) {
        // Revenge and Growth (23024 - end)

        sm.sayNext("So you have the Black Wings Report. Ha! I knew I was right about you!");
        sm.sayBoth("This mission was supposed to go to someone else but I had it re-assigned to you. That guy from the Black Wings was the one who attacked you in the past. I gave you the mission so you could take your revenge. Two birds with one stone, eh?");
        sm.sayBoth("To be honest though, you completed the mission more easily than I expected. You've really developed your skills...");
        if(!sm.askYesNo("I originally thought it might be too soon, but you've proved me wrong. You're more than ready to advance. You're ready to enhance your powers as a Wild Hunter.")) {
            sm.sayOk("Come back when you're ready."); //Unsure if GMS-like
            return;
        }
        if (!sm.addItem(1142243, 1)) {
            sm.sayNext("I was going to give you a gift for making the job advancement but I can't. Your Inventory Equip tab is full. Empty out at least one slot if you're interested in my gift.");
            return;
        }
        sm.removeItem(4032738);
        sm.setJob(Job.WILD_HUNTER_2);
        sm.addSp(3);
        sm.forceCompleteQuest(23024);
        sm.sayNext("I've advanced your job. I've also upgraded your skills. Enjoy your new abilities!");
        sm.sayPrev("I will see you at the next lesson. Until then, keep up the good fight.");
    }

    @Script("q23025e")
    public static void q23025e(ScriptManager sm) {
        // Revenge and Growth (23025 - end)

        sm.sayNext("So this is the Black Wings Report we needed. With this document, we can figure out the plans of the Black Wings. Thank you so much.");
        sm.sayBoth("This mission was not originally assigned to you, but I swapped a few things around. I wanted you to defeat that individual personally, give you a chance to right past wrongs, you know?");
        sm.sayBoth("Even so, I didn't think you would accomplish the mission so easily. You're progressing much faster than I expected.");
        if(!sm.askYesNo("I wasn't planning to do this for a while yet, but I think you're ready. Yes, I will advance you to become a Mechanic who can handle even more machines.")) {
            sm.sayOk("Come back when you're ready."); //Unsure if GMS-like
            return;
        }
        if (!sm.addItem(1142243, 1)) {
            sm.sayNext("I wanted to give you a gift to commemorate your new powers but I can't. Why do you carry so many things in your Inventory's Equip tab?");
            return;
        }
        sm.removeItem(4032739);
        sm.setJob(Job.MECHANIC_2);
        sm.addSp(3);
        sm.forceCompleteQuest(23025);
        sm.sayNext("I've advanced your job. I've also passed you a few more skills. Enjoy the new powers that you have gained.");
        sm.sayPrev("I will see you at the next lesson. Until then, keep up the good fight!");
    }

    @Script("q23033e")
    public static void q23033e(ScriptManager sm) {
        // Destroying the Energy Conducting Device (23033 - end)

        sm.sayNext("You destroyed the Energy Conducting Device! Good. This should alleviate the problem of insufficient energy in town. We'll all be able to sleep a little easier now. You've done a tremendous good for Edelstein.");
        if(!sm.askYesNo("You've proven yourself so thoroughly that there's no reason to put this off. I think you are ready for your advancement. Now you will become an even stronger Battle Mage. I trust you'll be able to handle it...")) {
            sm.sayOk("Come back when you're ready."); //Unsure if GMS-like
            return;
        }
        if (!sm.addItem(1142244, 1)) {
            sm.sayNext("Whoa! Why are you carrying so many things? I was going to give you a gift but there isn't enough room in the Equip tab of your inventory.");
            return;
        }
        sm.setJob(Job.BATTLE_MAGE_3);
        sm.addSp(3);
        sm.forceCompleteQuest(23033);
        sm.sayNext("You've been advanced. Now you have access to a maddening variety of powerful skills. They might not be easy to control, but from the way you completed that last mission, I think you can handle them.");
        sm.sayPrev("I will see you at the next lesson. Until then, continue your good fight.");
    }

    @Script("q23034e")
    public static void q23034e(ScriptManager sm) {
        // Destroying the Energy Conducting Device (23034 - end)

        sm.sayNext("You destroyed the Energy Conducting Device! I was right about you. Now our town won't have to worry about energy issues for a while. You've really done a great thing for #m310000000#.");
        if(!sm.askYesNo("Now that I know how much you've grown, I will give you the next lesson. I believe you are now strong enough to be reborn as a more powerful Wild Hunter!")) {
            sm.sayOk("Come back when you're ready."); //Unsure if GMS-like
            return;
        }
        if (!sm.addItem(1142244, 1)) {
            sm.sayNext("I was going to give you a gift for making the job advancement but I can't. Your Inventory Equip tab is full. Empty out at least one slot if you're interested in my gift.");
            return;
        }
        sm.setJob(Job.WILD_HUNTER_3);
        sm.addSp(3);
        sm.forceCompleteQuest(23034);
        sm.sayNext("You've been advanced. You now have a larger arsenal of skills to manage. It might not be easy, since you still have to control your mount, but I'm not worried.");
        sm.sayPrev("I'll see you at the next lesson. Until then, continue your good fight.");
    }

    @Script("q23035e")
    public static void q23035e(ScriptManager sm) {
        // Destroying the Energy Conducting Device (23035 - end)

        sm.sayNext("You've successfully destroyed the Energy Conducting Device! Now we don't have to worry about energy for a while. You've accomplished a truly great feat for Edelstein.");
        sm.sayBoth("This mission was not originally assigned to you");
        sm.sayBoth("Even so, I didn't think you would accomplish the mission so easily. You're progressing much faster than I expected.");
        if(!sm.askYesNo("Now that I've seen your abilities, it is time to show you mine. I will now pass on a new skill to you.")) {
            sm.sayOk("Come back when you're ready."); //Unsure if GMS-like
            return;
        }
        if (!sm.addItem(1142244, 1)) {
            sm.sayNext("I wanted to give you a gift to commemorate your new powers but I can't. Why do you carry so many things in your Inventory's Equip tab?");
            return;
        }
        sm.setJob(Job.MECHANIC_3);
        sm.addSp(3);
        sm.forceCompleteQuest(23035);
        sm.forceCompleteQuest(29943);
        sm.sayNext("I have advanced you. You will now wield a skill that is more varied, more complex, and much, much more powerful. Don't worry, I trust that you will be able to handle it with ease.");
        sm.sayPrev("I'll see you for your next mission. Keep fighting the good fight.");
    }

    @Script("q23049e")
    public static void q23049e(ScriptManager sm) {
        // Black Wings' New Weapon (23049 - end)
        sm.sayNext("You successfully destroyed the Black Wings' new weapon! Ha! I can't believe it! You did something I couldn't even do. I'm proud that you're a part of the Resistance.");
        if(!sm.askYesNo("Wait, we don't have time for this. Once #p2154009# realizes that his new weapon has been destroyed, he'll rush down with his minions. We need to get out of here now. Use the Underground Base #t4032740#. On my count. One... two... three!")) {
            return;
        }
        sm.forceCompleteQuest(23049);
        sm.warp(310010000);
    }

    @Script("q23050e")
    public static void q23050e(ScriptManager sm) {
        // Black Wings' New Weapon (23050 - end)
        sm.sayNext("You really destroyed the Black Wings' new weapon! I knew I was right about you! There is nothing sharper than the eyes of a bowman. I'm proud to call you a fellow member of the Resistance!");
        if(!sm.askYesNo("I'd love nothing more than to rub what we've done in #p2154009#'s face, but things could get hairy if he gathers all his minions. Let's get out of here. Use the Underground Base #t4032740# on my count. One... two... three!")) {
            return;
        }
        sm.forceCompleteQuest(23050);
        sm.warp(310010000);
    }

    @Script("q23051e")
    public static void q23051e(ScriptManager sm) {
        // Black Wings' New Weapon (23051 - end)
        sm.sayNext("You really destroyed the Black Wings' new weapon! I can't believe my eyes. You've upturned the status quo! The Resistance is lucky to have you! Truly lucky!");
        if(!sm.askYesNo("Oh... I was so happy, I forgot about our next move. Once Gelimer finds out that his new weapon has been destroyed, he is sure to come down with his minions. We better scram before that happens. I'll use the Underground Base #t4032742#. Ready to go? One... two... three!")) {
            return;
        }
        sm.forceCompleteQuest(23051);
        sm.warp(310010000);
    }

    @Script("q23052s")
    public static void q23052s(ScriptManager sm) {
        // You Surpass Me (23052 - start)
        sm.sayNext("Hey, it's #h0#, the hero of #m310000000#. Ah, isn't #m310000000# great? Even if it IS under the control of the Black Wings...");
        sm.sayBoth("Are you feeling better?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayBoth("Yes. #p2151003#'s skills are second to none. I'm completely back to my old self.\r\nThe only problem is...");
        sm.sayBoth("What?! Are the Black Wings planning something?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        if(!sm.askYesNo("No, the problem is... you! You've become too strong. I'm supposed to be your teacher but you've accomplished something I couldn't do. So I want to give you a more difficult mission!")) {
            return;
        }
        if (!sm.addItem(1142245, 1)
        ) {
            sm.sayNext("Whoa! Why are you carrying so many things? I was going to give you a gift but there isn't enough room in the Equip tab of your inventory.");
            return;
        }
        sm.setJob(Job.BATTLE_MAGE_4);
        sm.addSp(3);
        sm.addSkill(32120000, 0, 10);
        sm.addSkill(32120001, 0, 10);
        sm.addSkill(33120009, 0, 10);
        sm.addSkill(32121002, 0, 10);
        sm.addSkill(32121003, 0, 10);
        sm.addSkill(32121004, 0, 10);
        sm.addSkill(32121005, 0, 10);
        sm.addSkill(32121006, 0, 10);
        sm.addSkill(32121007, 0, 10);

        sm.forceStartQuest(23052);
        sm.forceCompleteQuest(23052);
        sm.forceCompleteQuest(29944);
        sm.sayNext("I've advanced you. I've also given you some sills that I know of but haven't mastered yet. I have a hunch that you'll be able to master them. After all, you are the most skilled member of the Resistance now!");
        sm.sayBoth("Could this be my last lesson with you? Nah, can't be. You may be stronger, but I'm still smarter. I'm sure there's plenty more you can learn from me. So I'll see you at your next lesson... whenever that is...");
        sm.sayPrev("Until then, I look forward to seeing what you accomplish.");
    }

    @Script("q23053s")
    public static void q23053s(ScriptManager sm) {
        // You Surpass Me (23053 - start)

        sm.sayNext("Well, if it isn't the town hero, #h0#! It's so wonderful to see you. Ah, even though it's under the control of the Black Wings, I do so love #m310000000#.");
        sm.sayBoth("Are you feeling better?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayBoth("I still have a few aches and pains, but I'm fine. #p2151003# is the best healer around, after all. The only problem is...");
        sm.sayBoth("What?! Are the Black Wings planning something?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        if(!sm.askYesNo("Haha, no, no. Rest easy. The problem is... you! You've become so strong that I don't have much to do. I used to be the best Wild Hunter in the Resistance, but now I'm not even good enough to teach you. That's why I want to give you an even more difficult mission!")) {
            return;
        }
        if (!sm.addItem(1142245, 1)
        ) {
            sm.sayNext("I was going to give you a gift for making the job advancement but I can't. Your Inventory Equip tab is full. Empty out at least one slot if you're interested in my gift.");
            return;
        }
        sm.setJob(Job.WILD_HUNTER_4);
        sm.addSp(3);
        sm.addSkill(33120000, 0, 10);
        sm.addSkill(33120010, 0, 10);
        sm.addSkill(33121001, 0, 10);
        sm.addSkill(33121002, 0, 10);
        sm.addSkill(33121004, 0, 10);
        sm.addSkill(33121005, 0, 10);
        sm.addSkill(33121006, 0, 10);
        sm.addSkill(33121007, 0, 10);
        sm.addSkill(33121009, 0, 10);
        sm.forceStartQuest(23053);
        sm.forceCompleteQuest(23053);
        sm.sayNext("I've advanced you. I've also given you some sills that I know of but haven't mastered yet. I have a hunch that you'll be able to master them. After all, you are the most skilled member of the Resistance now!");
        sm.sayBoth("And with that, my lessons have... NOT come to an end. I can still be pretty useful, you know. There's more I can teach you. Plus, we're friends, right? So I'll see you at your next lesson... Whenever that might be...");
        sm.sayPrev("Until then, I look forward to seeing what you accomplish.");
    }

    @Script("q23054s")
    public static void q23054s(ScriptManager sm) {
        // You Surpass Me (23054 - start)
        sm.sayNext("Well, if it isn't the town hero, #h0#! It's so wonderful to see you. Ah, even though it's under the control of the Black Wings, I do so love #m310000000#.");
        sm.sayBoth("Are you feeling better?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        sm.sayBoth("I still have a few aches and pains, but I'm fine. #p2151003# is the best healer around, after all. The only problem is...");
        sm.sayBoth("What?! Are the Black Wings planning something?", ScriptMessageParam.PLAYER_AS_SPEAKER);
        if(!sm.askYesNo("Haha, no, no. Rest easy. The problem is... you! You've become so strong that I don't have much to do. I used to be the best Wild Hunter in the Resistance, but now I'm not even good enough to teach you. That's why I want to give you an even more difficult mission!")) {
            return;
        }
        if (!sm.addItem(1142245, 1)
        ) {
            sm.sayNext("I wanted to give you a gift to commemorate your new powers but I can't. Why do you carry so many things in your Inventory's Equip tab?");
            return;
        }
        sm.setJob(Job.MECHANIC_4);
        sm.addSp(3);
        sm.addSkill(35120000, 0, 30);
        sm.addSkill(35120001, 0, 15);
        sm.addSkill(35121003, 0, 10);
        sm.addSkill(35121005, 0, 10);
        sm.addSkill(35121006, 0, 10);
        sm.addSkill(35121007, 0, 10);
        sm.addSkill(35121009, 0, 10);
        sm.addSkill(35121010, 0, 10);
        sm.addSkill(35121012, 0, 10);
        sm.forceStartQuest(23054);
        sm.forceCompleteQuest(23054);
        sm.forceCompleteQuest(29944);
        sm.sayNext("I've advanced you. I've also given you some sills that I know of but haven't mastered yet. I have a hunch that you'll be able to master them. After all, you are the most skilled member of the Resistance now.");
        sm.sayBoth("With this, the end of my lessons has... neared. Though you are stronger than I am, there are a lot of things you can still learn from me. I will see you at our next lesson... Whenever that may be...");
        sm.sayPrev("Until then, I look forward to seeing your accomplishments!");
    }
}
