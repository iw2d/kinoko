package kinoko.script;

import java.util.Map;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;

public class ExplorerAdvancement extends ScriptHandler {

    public static final int BEGINNER_WARRIORS_SWORD = 1302077;
    public static final int BEGINNER_THIEFS_SHORT_SWORD = 1332063;
    public static final int BEGINNER_MAGICIANS_WAND = 1372043;
    public static final int BEGINNER_BOWMANS_BOW = 1452051;
    public static final int BEGINNER_THIEF_WRIST_GUARDS = 1472061;
    public static final int SCALLYWAG_KNUCKLER = 1482029;
    public static final int PIRATES_PISTOL = 1492014;

    @Script("magician")
    public static void magician(ScriptManager sm) {
        // Most text extracted from: https://www.youtube.com/watch?v=w-izi_W8Dl0&ab_channel=jamsey700
        // Grendel the Really Old (1032001)
        //   Ellinia : Magic Library (101000003)
        final int playerLevel = sm.getUser().getLevel();

        // Beginner -> 1st job Magician handling
        if (JobConstants.isBeginnerJob(sm.getUser().getJob())) {
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.MAGICIAN.getJobId(), 0, 1);
            sm.sayNext("Do you want to be a Magician? You need to meet some requirements in order to do so. You need to be at least #bLevel " + jobChangeLevel + "#k. Let's see if you have what it takes to become a Magician.");
            if (playerLevel < jobChangeLevel) {
                sm.sayOk("You need more training to be a Magician. In order to be one, you need to train yourself to be more powerful than you are right now. Please come back when you are much stronger.");
                return;
            }
            if (sm.askYesNo("You definitely have the look of a Magician. You may not be there yet, but I can see the Magician in you. What do you think? Do you want to become a Magician?")) {
                sm.sayNext("You're now a Magician from here on out! It isn't much, but as the head Magician, I, #p1032001#, will give you a little bit of what I have...");
                // Only give the Beginner Magician Wand and change the users job if they are a beginner
                // This check allows the user to press "Next" and "Prev" without exploits
                if (JobConstants.isBeginnerJob(sm.getUser().getJob())) {
                    if (!sm.canAddItem(BEGINNER_MAGICIANS_WAND, 1)) {
                        sm.sayOk("Please make sure that you have an empty slot in your #rEQP. inventory#k and then talk to me again.");
                        return;
                    }
                    sm.setJob(Job.MAGICIAN);
                    sm.addItem(BEGINNER_MAGICIANS_WAND, 1);
                }
                if (playerLevel > jobChangeLevel) {
                    sm.sayBoth("I think you are a bit late with making a job advancement. But don't worry, I have compensated you with additional Skill Points that you didn't receive by making the advancement so late.");
                }
                sm.sayBoth("You have just equipped yourself with more magical power. Please continue training and improving. I'll be watching you here and there.");
                sm.sayBoth("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower right corner of the screen, there are skills you can learn by using your SP. One warning, though; you can't raise them all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                sm.sayBoth("One more warning, though it's kind of obvious. Once you have chosen your job, try your best to stay alive. Every death will cost you a certain amount of experience points, and you don't want to lose those, do you?");
                sm.sayBoth("Okay! This is all I can teach you. Go explore, train and better yourself. Find me when you feel like you've done all you can. I'll be waiting for you.");
                sm.sayPrev("Oh, and if you have any questions about being a Magician, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Until then, farewell...");
            } else {
                sm.sayOk("Really? Have to give more thought to it, huh? Take your time. This is not something you should take lightly... come talk to me once you have made your decision");
            }
            return;
        }

        // Any Magician - asking questions about Magicians
        if (JobConstants.isMagicianJob(sm.getUser().getJob())) {
            final Map<Integer, String> options = Map.ofEntries(
                    Map.entry(0, "What are the general characteristics of a Magician?"),
                    Map.entry(1, "What sort of weapons does a Magician use?"),
                    Map.entry(2, "What kind of armor can a Magician wear?"),
                    Map.entry(3, "What types of skills does a Magician have?")
            );
            final int answer = sm.askMenu("Any questions about being a Magician?#b", options);
            switch (answer) {
                case 0 -> sm.sayOk("Magicians put their high levels of magic and intelligence to good use. They can use the power of nature all around them to kill enemies, but they are very weak in close combat. Their stamina isn't high, either, so be careful and avoid getting too close.\r\n\r\nSince Magicians can attack monsters from afar, that helps quite a bit. Try boosting up the level of INT if you want to attack enemies accurately with your magic. The higher your intelligence, the better you'll be able to handle your magic.");
                case 1 -> sm.sayOk("Actually, it doesn't mean much for Magicians to attack their opponents with weapons. Magicians lack power and dexterity, so they have a hard time even defeating a snail.\r\n\r\nIf we're talking about magical powers, then THAT's a whole different story. The weapons that Magicians use are staves, and wands. These weapons have special magical powers in them, so they enhance a Magician's effectiveness. It'll be wise to carry a weapon with a lot of magical powers in it...");
                case 2 -> sm.sayOk("Honestly, Magicians don't have much armor to wear since they are weak in physical strength and low in stamina. Its defensive abilities aren't great either, so I don't know if it helps a lot or not...\r\n\r\nSome armors, however, have the ability to weaken an opponent's magical power, so it can guard you from magic attacks. It won't help much, but it is still better than not wearing them at all... so buy them if you have enough mesos...");
                case 3 -> sm.sayOk("The skills available for Magicians use the high levels of intelligence and magic that they have. Also available are Magic Guard and Magic Armor, which help prevent Magicians with weak stamina from dying.\r\n\r\nTheir offensive skills are #bEnergy Bolt#k and #bMagic Claw#k. Firstly, Energy Bolt is a skill that applies a lot of damage to an opponent with minimal use of MP.\r\n\r\nMagic Claw, on the other hand, uses up a lot of MP to attack multiple opponents TWICE. But, you can only use it once Energy Bolt is at least Level 1, so keep that in mind. Whatever you choose to do, it's all up to you...");
            }
            return;
        }

        // Otherwise, provide some generic text about Magicians
        sm.sayOk("Would you like to have the power of nature itself in your hands? It may be a long, hard road to be on, but you'll surely be rewarded in the end, reaching the very top of wizardry...");
    }
}
