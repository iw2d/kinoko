package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptError;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ExplorerQuest extends ScriptHandler {
    // Generic function to handle entering an Explorer Training Center
    // Most text was obtained from: https://youtu.be/MMb1MJGGkg4?si=Gm9rG6p1l8iiHan2
    private static void enterTrainingCenter(ScriptManager sm, int reqJobId, String reqJobName, int fieldId) {
        // If the user is level 20 or above, they should not be able to access the Training Center
        // If the user is not the correct job, they should not be able to access the Training Center
        if (sm.getLevel() >= 20 || sm.getJob() != reqJobId) {
            sm.sayOk("Sorry, but this is a training center only available to " + reqJobName + " under Lv. 20.");
            return;
        }
        final List<String> fieldNames = List.of(
                "Room of Courage",
                "Room of Wisdom",
                "Room of Skill",
                "Room of Training",
                "Room of Power"
        );
        final Map<Integer, String> options = new HashMap<>();
        for (int i = 0; i < fieldNames.size(); i++) {
            final int roomFieldId = fieldId + i;
            final Optional<Field> fieldResult = sm.getUser().getConnectedServer().getFieldById(roomFieldId);
            if (fieldResult.isEmpty()) {
                throw new ScriptError("Could not resolve field ID : %d", roomFieldId);
            }
            // Only add the option if there are less than 5 people in the map
            final int userCount = fieldResult.get().getUserPool().getCount();
            if (userCount < 5) {
                options.put(roomFieldId, fieldNames.get(i) + " (" + userCount + "/5)");
            }
        }
        // If all maps are already full, notify the user
        if (options.isEmpty()) {
            sm.sayOk("I'm sorry, but it appears that all the training centers are currently full. Please come back later!");
            return;
        }
        final int targetFieldId = sm.askMenu("#e#b[Notice]#n#k\r\nAdventurers!\r\nThis is a training center for " + reqJobName + " under Lv. 20. While you can always train on your own, training with others will allow you to become stronger in a faster time. Select the room you would like to train in.#b", options);
        sm.warp(targetFieldId);
    }

    @Script("enter_warrior")
    public static void enter_warrior(ScriptManager sm) {
        // Power B. Fore : Entrance to Warrior Training Center (1022105)
        //   North Rocky Mountain : Perion Northern Ridge (102020000)
        enterTrainingCenter(sm, 100, "Warriors", 910220000); // Victoria Road : Warrior Training Center
    }

    @Script("enter_magicion")
    public static void enter_magicion(ScriptManager sm) {
        // Power B. Fore : Entrance to Magician Training Center (1032114)
        //   Chimney Tree : Close to the Wind (101020000)
        enterTrainingCenter(sm, 200, "Magicians", 910120000); // Victoria Road : Magician Training Center
    }

    @Script("enter_archer")
    public static void enter_archer(ScriptManager sm) {
        // Power B. Fore : Entrance to Bowman Training Center (1012119)
        //   Singing Mushroom Forest : Spore Hill (100020000)
        if (sm.hasQuestStarted(22518)) {
            sm.warpInstance(910060100, "start", 100020000, 60 * 30);
            return;
        }
        enterTrainingCenter(sm, 300, "Bowmen", 910060000); // Victoria Road : Bowman Training Center
    }

    @Script("enter_thief")
    public static void enter_thief(ScriptManager sm) {
        // Power B. Fore : Entrance to Thief Training Center (1052114)
        //   Construction Site : Caution Falling Down (103010000)
        enterTrainingCenter(sm, 400, "Thieves", 910310000); // Victoria Road : Thief Training Center
    }

    @Script("enter_pirate")
    public static void enter_pirate(ScriptManager sm) {
        // Power B. Fore : Entrance to Pirate Training Center (1095002)
        //   Beach : Coastal Forest (120020000)
        enterTrainingCenter(sm, 500, "Pirates", 912030000); // Victoria Road : Pirate Training Center
    }

    @Script("magician")
    public static void magician(ScriptManager sm) {
        // Grendel the Really Old : Magician Instructor (1032001)
        //   Ellinia : Magic Library (101000003)
        if (sm.getJob() == 0) {
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.MAGICIAN.getJobId(), 0, 1);
            sm.sayNext("Do you want to be a Magician? You need to meet some requirements in order to do so. You need to be at least #bLevel " + jobChangeLevel + "#k. Let's see if you have what it takes to become a Magician.");
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You need more training to be a Magician. In order to be one, you need to train yourself to be more powerful than you are right now. Please come back when you are much stronger.");
                return;
            }
            if (!sm.askYesNo("You definitely have the look of a Magician. You may not be there yet, but I can see the Magician in you. What do you think? Do you want to become a Magician?")) {
                sm.sayOk("Really? Have to give more thought to it, huh? Take your time. This is not something you should take lightly... come talk to me once you have made your decision");
                return;
            }
            sm.sayNext("You're now a Magician from here on out! It isn't much, but as the head Magician, I, #p1032001#, will give you a little bit of what I have...");
            if (!sm.addItem(1372043, 1)) { // Beginner Magician's Wand
                sm.sayOk("Please make sure that you have an empty slot in your #rEQP. inventory#k and then talk to me again.");
                return;
            }
            sm.setJob(Job.MAGICIAN);
            if (sm.getLevel() > jobChangeLevel) {
                sm.sayBoth("I think you are a bit late with making a job advancement. But don't worry, I have compensated you with additional Skill Points that you didn't receive by making the advancement so late.");
            }
            sm.sayBoth("You have just equipped yourself with more magical power. Please continue training and improving. I'll be watching you here and there.");
            sm.sayBoth("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower right corner of the screen, there are skills you can learn by using your SP. One warning, though; you can't raise them all at once. There are also skills you can acquire only after having learned a couple of skills first.");
            sm.sayBoth("One more warning, though it's kind of obvious. Once you have chosen your job, try your best to stay alive. Every death will cost you a certain amount of experience points, and you don't want to lose those, do you?");
            sm.sayBoth("Okay! This is all I can teach you. Go explore, train and better yourself. Find me when you feel like you've done all you can. I'll be waiting for you.");
            sm.sayPrev("Oh, and if you have any questions about being a Magician, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Until then, farewell...");
        } else if (sm.getJob() == 200) {
            final Map<Integer, String> options = Map.of(
                    0, "What are the basic characteristics of being a Magician?",
                    1, "What sort of weapons does a Magician use?",
                    2, "What kind of armor can a Magician wear?",
                    3, "What types of skills does a Magician have?"
            );
            final int answer = sm.askMenu("Any questions about being a Magician?#b", options);
            if (answer == 0) {
                sm.sayOk("Magicians put their high levels of magic and intelligence to good use. They can use the power of nature all around them to kill enemies, but they are very weak in close combat. Their stamina isn't high, either, so be careful and avoid getting too close.\r\n\r\nSince Magicians can attack monsters from afar, that helps quite a bit. Try boosting up the level of INT if you want to attack enemies accurately with your magic. The higher your intelligence, the better you'll be able to handle your magic.");
            } else if (answer == 1) {
                sm.sayOk("Actually, it doesn't mean much for Magicians to attack their opponents with weapons. Magicians lack power and dexterity, so they have a hard time even defeating a snail.\r\n\r\nIf we're talking about magical powers, then THAT's a whole different story. The weapons that Magicians use are staves, and wands. These weapons have special magical powers in them, so they enhance a Magician's effectiveness. It'll be wise to carry a weapon with a lot of magical powers in it...");
            } else if (answer == 2) {
                sm.sayOk("Honestly, Magicians don't have much armor to wear since they are weak in physical strength and low in stamina. Its defensive abilities aren't great either, so I don't know if it helps a lot or not...\r\n\r\nSome armors, however, have the ability to weaken an opponent's magical power, so it can guard you from magic attacks. It won't help much, but it is still better than not wearing them at all... so buy them if you have enough mesos...");
            } else if (answer == 3) {
                sm.sayOk("The skills available for Magicians use the high levels of intelligence and magic that they have. Also available are Magic Guard and Magic Armor, which help prevent Magicians with weak stamina from dying.\r\n\r\nTheir offensive skills are #bEnergy Bolt#k and #bMagic Claw#k. Firstly, Energy Bolt is a skill that applies a lot of damage to an opponent with minimal use of MP.\r\n\r\nMagic Claw, on the other hand, uses up a lot of MP to attack multiple opponents TWICE. But, you can only use it once Energy Bolt is at least Level 1, so keep that in mind. Whatever you choose to do, it's all up to you...");
            }
        } else {
            sm.sayOk("Would you like to have the power of nature itself in your hands? It may be a long, hard road to be on, but you'll surely be rewarded in the end, reaching the very top of wizardry...");
            // TODO 2nd, 3rd job advancement handling
        }
    }
}
