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
        } else if (sm.getJob() == 200 && sm.getLevel() < 30) {
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
        } else if (sm.getJob() == 200) {
            // 2nd Job Advancement: Magician → Wizard (F/P), Wizard (I/L), Cleric
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.WIZARD_FP.getJobId(), 1, 2);
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You are still weak. Train more and grow stronger. Come back when you reach #bLevel " + jobChangeLevel + "#k and we can talk about your advancement.");
                return;
            }
            sm.sayNext("You have grown much stronger since you first became a Magician. I can sense your magical power growing. You are ready to choose your path. There are three paths available to you.");
            final Map<Integer, String> jobOptions = new HashMap<>();
            jobOptions.put((int) Job.WIZARD_FP.getJobId(), "#bWizard (Fire/Poison)#k - Master of fire and poison magic. Devastating area-of-effect damage.");
            jobOptions.put((int) Job.WIZARD_IL.getJobId(), "#bWizard (Ice/Lightning)#k - Master of ice and lightning magic. Powerful single-target and freezing abilities.");
            jobOptions.put((int) Job.CLERIC.getJobId(), "#bCleric#k - Holy magic user with healing and support abilities. Essential for party play.");
            final int selectedJob = sm.askMenu("Which path of magic calls to you?#b", jobOptions);

            String jobName;
            String jobDescription;
            if (selectedJob == Job.WIZARD_FP.getJobId()) {
                jobName = "Wizard (Fire/Poison)";
                jobDescription = "Fire/Poison Wizards command the destructive forces of fire and poison. Your spells will burn and corrode your enemies, dealing massive area damage over time. This path is perfect for those who want to see their enemies engulfed in flames.";
            } else if (selectedJob == Job.WIZARD_IL.getJobId()) {
                jobName = "Wizard (Ice/Lightning)";
                jobDescription = "Ice/Lightning Wizards wield the power of ice and lightning. Your spells will freeze and shock your enemies, dealing high damage while controlling the battlefield. This path is perfect for those who want precision and control.";
            } else {
                jobName = "Cleric";
                jobDescription = "Clerics are blessed with holy magic. Your spells can heal allies and smite undead enemies with holy light. You will be invaluable in party situations. This path is perfect for those who want to support their allies while still dealing respectable damage.";
            }

            if (!sm.askYesNo("You wish to walk the path of the #b" + jobName + "#k. " + jobDescription + "\r\n\r\nThis decision is permanent. Once chosen, you cannot change your path. Are you certain this is what you want?")) {
                sm.sayOk("Take your time to consider. This is an important decision. Come back when you've made up your mind.");
                return;
            }

            sm.sayNext("Very well! You are now a #b" + jobName + "#k! Your magical abilities have increased dramatically. Use your new powers wisely!");
            sm.setJob(Job.getById(selectedJob));
            sm.sayBoth("You have gained access to new spells as a " + jobName + ". Open your skill window and study your new abilities carefully. Each spell has its purpose.");
            sm.sayBoth("Continue to study and train. When you reach #bLevel 70#k, return to me for your 3rd job advancement. Greater power awaits you!");
        } else if (sm.getJob() == 210 || sm.getJob() == 220 || sm.getJob() == 230) {
            // 3rd Job Advancement - Direct to El Nath
            sm.sayOk("Your magical power has grown immensely! To advance to 3rd job, you must travel to #bEl Nath#k and seek out the magician instructor there.");
        } else if (sm.getJob() == 211 || sm.getJob() == 221 || sm.getJob() == 231) {
            // 4th Job Advancement - Direct to El Nath for quest
            sm.sayOk("You have reached incredible magical mastery! To achieve your 4th job advancement, you must travel to #bEl Nath#k and speak with the instructor there. They will guide you on your final trial.");
        } else {
            sm.sayOk("Would you like to have the power of nature itself in your hands? It may be a long, hard road to be on, but you'll surely be rewarded in the end, reaching the very top of wizardry...");
        }
    }

    @Script("warrior")
    public static void warrior(ScriptManager sm) {
        // Dances with Balrog : Warrior Instructor (1022000)
        //   Perion : Warrior Sanctuary (102000003)
        if (sm.getJob() == 0) {
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.WARRIOR.getJobId(), 0, 1);
            sm.sayNext("So you want to become the Warrior? You need to meet some requirements to become one. You better check and see if you meet them. At least #bLevel " + jobChangeLevel + "#k. Let's see....");
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You need more training to be a Warrior. Please come back when you are much stronger.");
                return;
            }
            if (!sm.askYesNo("Oh, you look strong enough. Great! You definitely have the look of a Warrior. What do you think? Do you want to become a Warrior?")) {
                sm.sayOk("Really? Have to give more thought to it, huh? Take your time. This is not something you should take lightly... come talk to me once you have made your decision.");
                return;
            }
            sm.sayNext("Alright! You are the Warrior from here on out! It isn't much, but I'll give you a little bit of what I have...");
            if (!sm.addItem(1402001, 1)) { // Wooden Club
                sm.sayOk("Please make sure that you have an empty slot in your #rEQP. inventory#k and then talk to me again.");
                return;
            }
            sm.setJob(Job.WARRIOR);
            if (sm.getLevel() > jobChangeLevel) {
                sm.sayBoth("I think you are a bit late with making a job advancement. But don't worry, I have compensated you with additional Skill Points that you didn't receive by making the advancement so late.");
            }
            sm.sayBoth("I have just given you a book that gives you the list of skills you can acquire as a Warrior. In that book, you'll find a bunch of passive skills that will help you train. These skills are always in effect, whether you use them or not.");
            sm.sayBoth("You have also gotten much stronger. You can view your status window to view your improvement. You'll gain more AP and SP as you level up, so use them wisely. A Warrior's most important skill is #bPower Strike#k, so remember that.");
            sm.sayBoth("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower right corner of the screen, there are skills you can learn by using your SP. One warning, though; you can't raise them all at once. There are also skills you can acquire only after having learned a couple of skills first.");
            sm.sayBoth("Now go, live as a Warrior. After you have made the job advancement, you will have a low HP, since you have moved into a different job class. But as time passes and you continue to hunt and gather experience, you'll see that Warriors are stronger than anyone else. I'll be waiting for you for when you make the 2nd job advancement.");
            sm.sayPrev("Oh, and if you have any questions about being a Warrior, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Until then, farewell...");
        } else if (sm.getJob() == 100 && sm.getLevel() < 30) {
            final Map<Integer, String> options = Map.of(
                    0, "What are the basic characteristics of being a Warrior?",
                    1, "What sort of weapons does a Warrior use?",
                    2, "What kind of armor can a Warrior wear?",
                    3, "What types of skills does a Warrior have?"
            );
            final int answer = sm.askMenu("Any questions about being a Warrior?#b", options);
            if (answer == 0) {
                sm.sayOk("Warriors possess an enormous power with stamina to back it up, and they shine the brightest in melee combat situations. Regular attacks are powerful to begin with, and armed with complex skills, the job is perfect for explosive attacks.\r\n\r\nWarriors are the strongest when it comes to attacking. They have high STR, which means their physical attacks are just incredible. Not only that, because of their high levels of HP and Weapon Defense, they make great people to brawl up front in the battles. Warriors are always known to be the leaders in any fight.");
            } else if (answer == 1) {
                sm.sayOk("Warriors are well trained in using weapons. Their specialty lies in pole-arm style weapons, one-handed and two-handed swords, axes, and blunt weapons. There's a lot of weapons to choose from.\r\n\r\nIf you make the 2nd job advancement, you can only use a specific weapon based on the job. For example, Fighters and Crusaders only use one-handed and two-handed swords and axes. Pages and White Knights can only use one-handed swords, one-handed axes, and blunt weapons. Spearmen and Dragon Knights can only use spears and pole-arms.");
            } else if (answer == 2) {
                sm.sayOk("Warriors boast strong stamina and strength, and they can wear pretty much anything. You really won't have to worry much about this. Of course, armors with high Defense also require high levels, so even if you are a Warrior, it's best to plan ahead on what to wear.\r\n\r\nIt's better to wear armors with high Defense and many options, rather than choosing ones because they look good. Warriors have many options as for armor, so look around for a powerful set. Armors with 2 slots can equip scrolls that are good, so make sure to purchase it with a few slots available.");
            } else if (answer == 3) {
                sm.sayOk("The skills for Warriors are mostly to help them attack more effectively and inflict bigger damage. That's why Power Strike and Slash Blast are so popular.\r\n\r\nBesides the attacking skills, Warriors also have defensive skills such as #bWeapon Mastery#k and #bWeapon Booster#k that allows Warriors to boost up their expertise on weapons, using it to its maximum potential, along with #bIron Body#k and #bPower Guard#k to help you defend against enemy attacks. Make sure you keep your eyes on them, because you never know when you might need them.");
            }
        } else if (sm.getJob() == 100) {
            // 2nd Job Advancement: Warrior → Fighter/Page/Spearman
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.FIGHTER.getJobId(), 1, 2);
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You're still weak. You need to be much stronger in order to advance to the 2nd job. Please train yourself and come back to me when you feel like you are much stronger than you are right now.");
                return;
            }
            sm.sayNext("Hmmm... You have grown much stronger. I can see that you are ready to advance to the next level. There are three paths available to Warriors. Which path will you choose?");
            final Map<Integer, String> jobOptions = new HashMap<>();
            jobOptions.put((int) Job.FIGHTER.getJobId(), "#bFighter#k - Master of swords and axes. Fighters are close-combat specialists who excel at dealing massive damage.");
            jobOptions.put((int) Job.PAGE.getJobId(), "#bPage#k - Defender who uses swords, axes, and blunt weapons along with a shield. High defense and HP.");
            jobOptions.put((int) Job.SPEARMAN.getJobId(), "#bSpearman#k - Long-range melee specialist using spears and polearms. Balanced offense and defense.");
            final int selectedJob = sm.askMenu("Which job advancement would you like to make?#b", jobOptions);

            String jobName;
            String jobDescription;
            if (selectedJob == Job.FIGHTER.getJobId()) {
                jobName = "Fighter";
                jobDescription = "Fighters are the most aggressive of all Warriors. They specialize in dealing massive damage using two-handed swords and axes. Their skills focus on pure offensive power, making them perfect for players who want to deal the highest damage possible.";
            } else if (selectedJob == Job.PAGE.getJobId()) {
                jobName = "Page";
                jobDescription = "Pages are defensive Warriors who use shields to protect themselves. They excel at tanking damage and protecting party members. Their skills focus on defense and survivability, making them perfect for players who want to be at the front lines.";
            } else {
                jobName = "Spearman";
                jobDescription = "Spearmen use long-range melee weapons to attack from a safer distance. They have a good balance of offense and defense. Their skills focus on crowd control and sustained damage, making them perfect for players who want versatility.";
            }

            if (!sm.askYesNo("So you have chosen the path of the #b" + jobName + "#k. " + jobDescription + "\r\n\r\nOnce you have made your decision, you will not be able to turn back. Are you sure about this?")) {
                sm.sayOk("You don't have to make your decision right now. Come back when you're ready.");
                return;
            }

            sm.sayNext("Alright! You are now a #b" + jobName + "#k! You have chosen wisely. From this point forward, your training will be much harder, but the rewards will be great. Train hard and you will become even stronger!");
            sm.setJob(Job.getById(selectedJob));
            sm.sayBoth("You have gained new skills as a " + jobName + ". Open your skill window and check out your new abilities. Make sure to use your SP wisely!");
            sm.sayBoth("I have also given you additional SP as a reward for your advancement. Continue training and when you reach #bLevel 70#k, come back to me for your 3rd job advancement.");
        } else if (sm.getJob() == 110 || sm.getJob() == 120 || sm.getJob() == 130) {
            // 3rd Job Advancement - Direct to El Nath
            sm.sayOk("You have grown very strong! To advance to 3rd job, you must travel to #bEl Nath#k and seek out the warrior instructor there.");
        } else if (sm.getJob() == 111 || sm.getJob() == 121 || sm.getJob() == 131) {
            // 4th Job Advancement - Direct to El Nath for quest
            sm.sayOk("You have reached an incredible level of power! To achieve your 4th job advancement, you must travel to #bEl Nath#k and speak with the instructor there. They will guide you on your final trial.");
        } else {
            sm.sayOk("You want more power? Then you will have to bring yourself more strength first. Come back when you have trained yourself harder.");
        }
    }

    @Script("bowman")
    public static void bowman(ScriptManager sm) {
        archer(sm);
    }

    @Script("archer")
    public static void archer(ScriptManager sm) {
        // Athena Pierce : Bowman Instructor (1012100)
        //   Henesys : Bowman Instructional School (100000201)
        if (sm.getJob() == 0) {
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.ARCHER.getJobId(), 0, 1);
            sm.sayNext("Want to be a Bowman? Well... You need to meet some standards in order to be one. Let me see... Hmm... At least #bLevel " + jobChangeLevel + "#k. Alright, let me see how much you have been training...");
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("Hmmm... You haven't trained enough to be a Bowman. Please train a little more until you reach Level " + jobChangeLevel + " and come see me.");
                return;
            }
            if (!sm.askYesNo("You look like you can be a Bowman. Great! But I have to make sure, you know... So do you really want to become the Bowman?")) {
                sm.sayOk("Really? Well, this is understandable. This is not an easy decision to make. But if you wish to become the Bowman, come back to me.");
                return;
            }
            sm.sayNext("Nice! Your training and your reflexes are great! From here on out, you'll live as a Bowman! I'll make you stronger than you are right now. I'll make you a tough bowman. Hahaha!");
            if (!sm.addItem(1452002, 1)) { // Beginner Hunter's Bow
                sm.sayOk("Please make sure that you have an empty slot in your #rEQP. inventory#k and then talk to me again.");
                return;
            }
            sm.setJob(Job.ARCHER);
            if (sm.getLevel() > jobChangeLevel) {
                sm.sayBoth("I think you are a bit late with making a job advancement. But don't worry, I have compensated you with additional Skill Points that you didn't receive by making the advancement so late.");
            }
            sm.sayBoth("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower right corner of the screen, there are skills you can learn by using your SP. One warning, though; you can't raise them all at once. There are also skills you can acquire only after having learned a couple of skills first.");
            sm.sayBoth("Now... I'll explain to you the characteristics of a Bowman. First, you must know how to use a bow and a crossbow. Bows are better for long-distance attacks, whereas crossbows offer more explosive attacking power. It's up to you to pick a weapon that fits your style.");
            sm.sayBoth("Unlike other warriors, Bowman possesses weak stamina and strength, but is blessed with dexterity and power that will help you attack enemies from afar. We provide support to those that are in the heat of the battle.");
            sm.sayBoth("One more warning: Once you become a Bowman, you'll be a lot weaker than ever. But as you train and train, you'll see yourself getting stronger and stronger. Believe in yourself!");
            sm.sayPrev("Oh, and if you have any questions about being a Bowman, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Until then, farewell...");
        } else if (sm.getJob() == 300 && sm.getLevel() < 30) {
            final Map<Integer, String> options = Map.of(
                    0, "What are the basic characteristics of being a Bowman?",
                    1, "What sort of weapons does a Bowman use?",
                    2, "What kind of armor can a Bowman wear?",
                    3, "What types of skills does a Bowman have?"
            );
            final int answer = sm.askMenu("Any questions about being a Bowman?#b", options);
            if (answer == 0) {
                sm.sayOk("Bowmen are blessed with dexterity and power, taking charge of long-distance attacks, providing support for those at the front line of the battle. Very adept at using landscape as part of the arsenal.\r\n\r\nBowmen also possess high accuracy and avoidability. It doesn't have much health or defense, but its long-range attack power is better than anyone else, so you won't have to worry much about being in close combat. The higher the DEX, the higher your attack rate and accuracy will be.");
            } else if (answer == 1) {
                sm.sayOk("Bowmen can use bows and crossbows as weapons. One-handed or Two-handed weapons, it doesn't matter either way. Bows have long-distance attacks, whereas Crossbows offer more power. Whatever the choice, the skills you'll learn will be the same.\r\n\r\nPlease remember that the type of weapon WILL determine the job. If you like explosive attacking power, you can choose to become a Crossbowman, and if you prefer ranged attacks, you can choose to become a Hunter. This choice will have to be made once you reach Level 30 and do the 2nd job advancement.");
            } else if (answer == 2) {
                sm.sayOk("Unlike Warriors, Bowmen lack physical strength and stamina, so the only armors you can put on will have low defense. However, these items have a lot of DEX options, which will prove to be quite useful. Always look for armors that increases DEX. It'll help you a lot!");
            } else if (answer == 3) {
                sm.sayOk("For Bowmen, skills that are available are the ones that allows you to use bows and crossbows more efficiently. It's the perfect arsenal of skills for Bowmen. You'll have #bArrow Blow#k and #bDouble Shot#k to attack enemies from a far, and you will also have #bFocus#k to boost up the weapon mastery and accuracy.\r\n\r\nIn addition, there are also #bCritical Shot#k, which allows you a certain probability of doing critical damage to the enemy, and #bThe Eye of Amazon#k, which increases the overall focus and accuracy of Bowmen. It's all very useful skills that can only be used by the Bowmen. If you get to learn a high level skill, you may also hit multiple enemies at once!");
            }
        } else if (sm.getJob() == 300) {
            // 2nd Job Advancement: Archer → Hunter/Crossbowman
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.HUNTER.getJobId(), 1, 2);
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You're not ready yet. You need to train more and reach #bLevel " + jobChangeLevel + "#k before you can make the 2nd job advancement. Keep practicing!");
                return;
            }
            sm.sayNext("You have trained well as a Bowman. I can see your skills have improved greatly. Now it's time for you to choose your path. Will you specialize in the bow or the crossbow?");
            final Map<Integer, String> jobOptions = new HashMap<>();
            jobOptions.put((int) Job.HUNTER.getJobId(), "#bHunter#k - Master of the bow. Specializes in rapid, long-range attacks with high mobility.");
            jobOptions.put((int) Job.CROSSBOWMAN.getJobId(), "#bCrossbowman#k - Master of the crossbow. Specializes in powerful, piercing attacks with high accuracy.");
            final int selectedJob = sm.askMenu("Choose your path carefully:#b", jobOptions);

            String jobName;
            String jobDescription;
            if (selectedJob == Job.HUNTER.getJobId()) {
                jobName = "Hunter";
                jobDescription = "Hunters are masters of the bow, capable of firing arrows at incredible speeds. Your attacks will be swift and deadly, allowing you to take down enemies from a distance. This path is perfect for those who value speed and mobility.";
            } else {
                jobName = "Crossbowman";
                jobDescription = "Crossbowmen wield powerful crossbows that can pierce through even the toughest defenses. Your attacks pack more punch than a Hunter's, trading some speed for raw power. This path is perfect for those who value precision and stopping power.";
            }

            if (!sm.askYesNo("You have chosen to become a #b" + jobName + "#k. " + jobDescription + "\r\n\r\nThis decision is final. Are you sure this is the path you want to follow?")) {
                sm.sayOk("It's okay to take your time. This is an important choice. Come back when you've decided.");
                return;
            }

            sm.sayNext("Perfect! You are now a #b" + jobName + "#k! Your archery skills have reached a new level. Train hard and become even stronger!");
            sm.setJob(Job.getById(selectedJob));
            sm.sayBoth("As a " + jobName + ", you have gained powerful new abilities. Check your skill window to see what you can learn. Master these skills!");
            sm.sayBoth("Keep training diligently. When you reach #bLevel 70#k, come back to me for your 3rd job advancement!");
        } else if (sm.getJob() == 310 || sm.getJob() == 320) {
            // 3rd Job Advancement - Direct to El Nath
            sm.sayOk("Your archery skills are exceptional! To advance to 3rd job, you must travel to #bEl Nath#k and seek out the bowman instructor there.");
        } else if (sm.getJob() == 311 || sm.getJob() == 321) {
            // 4th Job Advancement - Direct to El Nath for quest
            sm.sayOk("You have reached the pinnacle of archery! To achieve your 4th job advancement, you must travel to #bEl Nath#k and speak with the instructor there. They will guide you on your final trial.");
        } else {
            sm.sayOk("Would you like to be even stronger? Then you'd better train even more. I can always make you stronger once you meet my standards.");
        }
    }

    @Script("rogue")
    public static void rogue(ScriptManager sm) {
        // Dark Lord : Thief Instructor (1052001)
        //   Kerning City : Thieves' Hideout (103000003)
        if (sm.getJob() == 0) {
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.ROGUE.getJobId(), 0, 1);
            sm.sayNext("Do you want to become a Thief? You need to meet some criteria in order to do so. You need to be at least #bLevel " + jobChangeLevel + "#k. Let's see...");
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("Hmmm, you are not strong enough. Come back to me when you are at least Level " + jobChangeLevel + ".");
                return;
            }
            if (!sm.askYesNo("Oh, you look pretty strong. I can see that you have what it takes to be a Thief. What do you think, do you want to become a Thief?")) {
                sm.sayOk("Really? Think about it carefully. If you decide to become one, come and find me.");
                return;
            }
            sm.sayNext("Alright! I've made up my mind. You are ready to be a Thief from here on out! I'll make you stronger than you are right now. Hahaha!");
            if (!sm.addItem(1472061, 1)) { // Garnier
                sm.sayOk("Please make sure that you have an empty slot in your #rEQP. inventory#k and then talk to me again.");
                return;
            }
            sm.setJob(Job.ROGUE);

            // For Dual Blade players: Set quest info flag for quest 2351 completion
            if (sm.getUser().getCharacterStat().getSubJob() == 1 && sm.hasQuestStarted(2351)) {
                sm.setQRValue(7635, "1");
            }

            if (sm.getLevel() > jobChangeLevel) {
                sm.sayBoth("I think you are a bit late with making a job advancement. But don't worry, I have compensated you with additional Skill Points that you didn't receive by making the advancement so late.");
            }
            sm.sayBoth("I have just given you a book that gives you the list of skills you can acquire as a Thief. Also, your Max HP and MP have increased, too. Check out your Stat Window now.");
            sm.sayBoth("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower right corner of the screen, there are skills you can learn by using your SP. One warning, though; you can't raise them all at once. There are also skills you can acquire only after having learned a couple of skills first.");
            sm.sayBoth("Thieves have to be strong. But remember that you can't beat Warrior in physical strength; instead, you will have the highest dexterity of all. The combination of high dexterity and speed is one of the biggest assets of the Thieves.");
            sm.sayBoth("If you are having trouble deciding whether to be a Thief or not, I have one word of advice for you - Thieves have many options to explore. Rather than being absolutely fixed in one form, you can experiment with your style and improve on those which suit you better. See the positive side of that flexibility.");
            sm.sayPrev("Oh, and if you have any questions about being a Thief, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Until then, farewell...");
        } else if (sm.getJob() == 400 && sm.getLevel() < 30) {
            final Map<Integer, String> options = Map.of(
                    0, "What are the basic characteristics of being a Thief?",
                    1, "What sort of weapons does a Thief use?",
                    2, "What kind of armor can a Thief wear?",
                    3, "What types of skills does a Thief have?"
            );
            final int answer = sm.askMenu("Any questions about being a Thief?#b", options);
            if (answer == 0) {
                sm.sayOk("Thieves are a perfect blend of luck, dexterity, and power that are adept at surprise attacks against helpless enemies. A high level of avoidability and speed allows the thieves to attack enemies with various angles.\r\n\r\nApart from great attacking ability, Thieves also possess some skills that are simply impossible for other jobs. They can hide themselves from the radar through #bHide#k, use throwing stars through #bLucky Seven#k, summon other people to their side through #bTeleport#k, and even be immune to poison! There are so many things Thieves can do for themselves.");
            } else if (answer == 1) {
                sm.sayOk("Thieves have a wide variety of weapons to choose from. They can use both daggers and throwing stars. Daggers are used for close-combat situations, whereas stars can be thrown for long-distance attacks. Both weapons are quite powerful, so it will be your choice on which one to choose.\r\n\r\nThieves can't wear shields. Instead, they have access to Subani Pendant which can be used to raise all-around stat increases. Same goes with Maple Shield, which can raise weapon defense. That's not so bad, is it?");
            } else if (answer == 2) {
                sm.sayOk("Thieves have high dexterity and high avoidability in general. Therefore, you don't really need an armor with high defense. What matters most is the stat increase and options on different pieces of clothing. With quickness and avoidability at hands, you don't really have to worry much about high defense.");
            } else if (answer == 3) {
                sm.sayOk("For Thieves, you'll find various kinds of attacking skills along with a number of supporting skills. The attacking skills are #bLucky Seven#k, a skill that allows you to throw a large number of stars at once, and #bDark Sight#k, a skill that allows you to hide yourself from the enemies and make an absolute escape.\r\n\r\nThieves are also able to boost their attacking ability through #bClaw Mastery#k, critical attack rate through #bCritical Throw#k, along with raising stats such as DEX and LUK through #bNimble Body#k. Please keep in mind that all these skills require mastery to use them, so make sure to keep training for the betterment of yourself.");
            }
        } else if (sm.getJob() == 400) {
            // 2nd Job Advancement: Rogue → Assassin/Bandit (NO QUEST SYSTEM)
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.ASSASSIN.getJobId(), 1, 2);

            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You're not quite strong enough yet. Train more and come back when you reach #bLevel " + jobChangeLevel + "#k.");
                return;
            }

            sm.sayNext("You have grown much stronger. You're ready to choose your path. Which style of thievery calls to you?");

            final Map<Integer, String> jobOptions = new HashMap<>();
            jobOptions.put((int) Job.ASSASSIN.getJobId(), "#bAssassin#k - Master of throwing stars and critical strikes. High burst damage.");
            jobOptions.put((int) Job.BANDIT.getJobId(), "#bBandit#k - Master of daggers and melee combat. Versatile and powerful.");
            final int selectedJob = sm.askMenu("Choose your path:#b", jobOptions);

            String jobName;
            String jobDescription;
            if (selectedJob == Job.ASSASSIN.getJobId()) {
                jobName = "Assassin";
                jobDescription = "Assassins are masters of the shadows, specializing in throwing stars and deadly critical strikes. You excel at eliminating targets quickly. This path is perfect for those who value burst damage and precision.";
            } else {
                jobName = "Bandit";
                jobDescription = "Bandits are versatile thieves who use daggers for close combat. You have access to stealing abilities and powerful melee combos. This path is perfect for those who value flexibility.";
            }

            if (!sm.askYesNo("You wish to become a #b" + jobName + "#k. " + jobDescription + "\r\n\r\nThis is a permanent choice. Are you certain?")) {
                sm.sayOk("No rush. Take your time. Come back when you're ready.");
                return;
            }

            sm.sayNext("Perfect! You are now a #b" + jobName + "#k! Your thieving skills have evolved!");
            sm.setJob(Job.getById(selectedJob));
            sm.sayBoth("As a " + jobName + ", you've gained powerful new skills. Check your skill window!");
            sm.sayBoth("Continue training. When you reach #bLevel 70#k, return for your 3rd job advancement!");
        } else if (sm.getJob() == 410 || sm.getJob() == 420) {
            // 3rd Job Advancement - Direct to El Nath
            sm.sayOk("You have become a skilled thief! To advance to 3rd job, you must travel to #bEl Nath#k and seek out the thief instructor there.");
        } else if (sm.getJob() == 411 || sm.getJob() == 421) {
            // 4th Job Advancement - Direct to El Nath for quest
            sm.sayOk("You have reached the peak of thievery! To achieve your 4th job advancement, you must travel to #bEl Nath#k and speak with the instructor there. They will guide you on your final trial.");
        } else {
            sm.sayOk("Hmm, do you want to be even stronger than you are now? Well, I can't do anything for you until you train more and improve yourself. Come back when you are stronger, and I'll see what I can do.");
        }
    }

    // Warrior 2nd job advancement NPC wrappers
    @Script("fighter")
    public static void fighter(ScriptManager sm) {
        warrior(sm);
    }

    @Script("page")
    public static void page(ScriptManager sm) {
        warrior(sm);
    }

    @Script("spearman")
    public static void spearman(ScriptManager sm) {
        warrior(sm);
    }

    // Magician 2nd job advancement NPC wrappers
    @Script("FPwizard")
    public static void FPwizard(ScriptManager sm) {
        magician(sm);
    }

    @Script("ILwizard")
    public static void ILwizard(ScriptManager sm) {
        magician(sm);
    }

    @Script("cleric")
    public static void cleric(ScriptManager sm) {
        magician(sm);
    }

    // Archer 2nd job advancement NPC wrappers
    @Script("hunter")
    public static void hunter(ScriptManager sm) {
        archer(sm);
    }

    @Script("crossbowman")
    public static void crossbowman(ScriptManager sm) {
        archer(sm);
    }

    // Thief 2nd job advancement NPC wrappers
    @Script("assassin")
    public static void assassin(ScriptManager sm) {
        rogue(sm);
    }

    @Script("bandit")
    public static void bandit(ScriptManager sm) {
        rogue(sm);
    }

    // Pirate 2nd job advancement NPC wrappers
    @Script("brawler")
    public static void brawler(ScriptManager sm) {
        pirate(sm);
    }

    @Script("gunslinger")
    public static void gunslinger(ScriptManager sm) {
        pirate(sm);
    }

    // MUSHKING EMPIRE QUESTS (2300-2303) --------------------------------------------------------------------

    @Script("q2300s")
    public static void q2300s(ScriptManager sm) {
        // Quest 2300 - Endangered Mushking Empire (START) - Warriors
        // Dances with Balrog (1022000) - Perion
        sm.sayNext("The Mushking Empire is in dire straits and in desperate need of help! As a strong Warrior, I believe you can make a difference there.");

        if (sm.askYesNo("I'd like to give you a #bletter of recommendation#k to take to #b#p1300005##k, the Head Security Officer of the Mushking Empire. Will you help them?")) {
            sm.forceStartQuest(2300);
            sm.addItem(4032375, 1); // Letter of Recommendation
            sm.sayNext("Thank you! Please take this letter to #b#p1300005##k in the Mushking Empire. They need all the help they can get!");
            sm.sayOk("You can find the Mushking Empire by heading west from Henesys through Ghost Mushroom Forest. Good luck!");
        } else {
            sm.sayOk("If you change your mind, come back and talk to me.");
        }
    }

    @Script("q2300e")
    public static void q2300e(ScriptManager sm) {
        // Quest 2300 - Endangered Mushking Empire (END) - Warriors
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("You need to bring the letter of recommendation from Dances with Balrog.");
            return;
        }

        sm.sayNext("Ah, you have a letter of recommendation from Dances with Balrog! Thank you for coming to help the Mushking Empire in our time of need.");

        if (sm.askYesNo("We are grateful for your assistance. Will you help us defend the Empire?")) {
            sm.removeItem(LETTER_OF_RECOMMENDATION, 1);
            sm.forceCompleteQuest(2300);
            sm.sayOk("Thank you! The Mushking Empire is in your debt. There will be more challenges ahead, but with your help, we can overcome them!");
        } else {
            sm.sayOk("Please reconsider. We really need your help.");
        }
    }

    @Script("q2301s")
    public static void q2301s(ScriptManager sm) {
        // Quest 2301 - Endangered Mushking Empire (START) - Magicians
        // Grendel the Really Old (1032001) - Ellinia
        sm.sayNext("The Mushking Empire is in dire straits and in desperate need of help! As a powerful Magician, I believe you can make a difference there.");

        if (sm.askYesNo("I'd like to give you a #bletter of recommendation#k to take to #b#p1300005##k, the Head Security Officer of the Mushking Empire. Will you help them?")) {
            sm.forceStartQuest(2301);
            sm.addItem(4032375, 1); // Letter of Recommendation
            sm.sayNext("Thank you! Please take this letter to #b#p1300005##k in the Mushking Empire. They need all the help they can get!");
            sm.sayOk("You can find the Mushking Empire by heading west from Henesys through Ghost Mushroom Forest. Good luck!");
        } else {
            sm.sayOk("If you change your mind, come back and talk to me.");
        }
    }

    @Script("q2301e")
    public static void q2301e(ScriptManager sm) {
        // Quest 2301 - Endangered Mushking Empire (END) - Magicians
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("You need to bring the letter of recommendation from Grendel the Really Old.");
            return;
        }

        sm.sayNext("Ah, you have a letter of recommendation from Grendel! Thank you for coming to help the Mushking Empire in our time of need.");

        if (sm.askYesNo("We are grateful for your assistance. Will you help us defend the Empire?")) {
            sm.removeItem(LETTER_OF_RECOMMENDATION, 1);
            sm.forceCompleteQuest(2301);
            sm.sayOk("Thank you! The Mushking Empire is in your debt. There will be more challenges ahead, but with your help, we can overcome them!");
        } else {
            sm.sayOk("Please reconsider. We really need your help.");
        }
    }

    @Script("q2302s")
    public static void q2302s(ScriptManager sm) {
        // Quest 2302 - Endangered Mushking Empire (START) - Thieves
        // Dark Lord (1052001) - Kerning City
        sm.sayNext("The Mushking Empire is in dire straits and in desperate need of help! As a skilled Thief, I believe you can make a difference there.");

        if (sm.askYesNo("I'd like to give you a #bletter of recommendation#k to take to #b#p1300005##k, the Head Security Officer of the Mushking Empire. Will you help them?")) {
            sm.forceStartQuest(2302);
            sm.addItem(4032375, 1); // Letter of Recommendation
            sm.sayNext("Thank you! Please take this letter to #b#p1300005##k in the Mushking Empire. They need all the help they can get!");
            sm.sayOk("You can find the Mushking Empire by heading west from Henesys through Ghost Mushroom Forest. Good luck!");
        } else {
            sm.sayOk("If you change your mind, come back and talk to me.");
        }
    }

    @Script("q2302e")
    public static void q2302e(ScriptManager sm) {
        // Quest 2302 - Endangered Mushking Empire (END) - Thieves
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("You need to bring the letter of recommendation from the Dark Lord.");
            return;
        }

        sm.sayNext("Ah, you have a letter of recommendation from the Dark Lord! Thank you for coming to help the Mushking Empire in our time of need.");

        if (sm.askYesNo("We are grateful for your assistance. Will you help us defend the Empire?")) {
            sm.removeItem(LETTER_OF_RECOMMENDATION, 1);
            sm.forceCompleteQuest(2302);
            sm.sayOk("Thank you! The Mushking Empire is in your debt. There will be more challenges ahead, but with your help, we can overcome them!");
        } else {
            sm.sayOk("Please reconsider. We really need your help.");
        }
    }

    @Script("q2303s")
    public static void q2303s(ScriptManager sm) {
        // Quest 2303 - Endangered Mushking Empire (START) - Bowmen
        // Athena Pierce (1012100) - Henesys
        sm.sayNext("The Mushking Empire is in dire straits and in desperate need of help! As a skilled Archer, I believe you can make a difference there.");

        if (sm.askYesNo("I'd like to give you a #bletter of recommendation#k to take to #b#p1300005##k, the Head Security Officer of the Mushking Empire. Will you help them?")) {
            sm.forceStartQuest(2303);
            sm.addItem(4032375, 1); // Letter of Recommendation
            sm.sayNext("Thank you! Please take this letter to #b#p1300005##k in the Mushking Empire. They need all the help they can get!");
            sm.sayOk("You can find the Mushking Empire by heading west from Henesys through Ghost Mushroom Forest. Good luck!");
        } else {
            sm.sayOk("If you change your mind, come back and talk to me.");
        }
    }

    @Script("q2303e")
    public static void q2303e(ScriptManager sm) {
        // Quest 2303 - Endangered Mushking Empire (END) - Bowmen
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("You need to bring the letter of recommendation from Athena Pierce.");
            return;
        }

        sm.sayNext("Ah, you have a letter of recommendation from Athena Pierce! Thank you for coming to help the Mushking Empire in our time of need.");

        if (sm.askYesNo("We are grateful for your assistance. Will you help us defend the Empire?")) {
            sm.removeItem(LETTER_OF_RECOMMENDATION, 1);
            sm.forceCompleteQuest(2303);
            sm.sayOk("Thank you! The Mushking Empire is in your debt. There will be more challenges ahead, but with your help, we can overcome them!");
        } else {
            sm.sayOk("Please reconsider. We really need your help.");
        }
    }

    @Script("pirate")
    public static void pirate(ScriptManager sm) {
        // Kyrin : Pirate Instructor (1090000)
        //   Nautilus : Navigation Room (120000101)
        if (sm.getJob() == 0) {
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.PIRATE.getJobId(), 0, 1);
            sm.sayNext("So you want to become a Pirate? Well, I have to see if you qualify to be one. You must be at least #bLevel " + jobChangeLevel + "#k. Let's see...");
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("Hmm... You don't seem strong enough yet. Please get to Level " + jobChangeLevel + " first, then come back to me.");
                return;
            }
            if (!sm.askYesNo("Oh, I see you meet the requirements! Would you like to become a Pirate?")) {
                sm.sayOk("I see. Think carefully and see if you want to become one. If you do, come back to me.");
                return;
            }
            sm.sayNext("Alright, from here on out you are a Pirate! Being flexible is the key to becoming a Pirate, and it will allow you to take on enemies in all situations. For now, I'll enhance your abilities a little bit and give you some SP.");
            if (!sm.addItem(1482014, 1)) { // Knuckle Mace
                sm.sayOk("Please make sure that you have an empty slot in your #rEQP. inventory#k and then talk to me again.");
                return;
            }
            sm.setJob(Job.PIRATE);
            if (sm.getLevel() > jobChangeLevel) {
                sm.sayBoth("I think you are a bit late with making a job advancement. But don't worry, I have compensated you with additional Skill Points that you didn't receive by making the advancement so late.");
            }
            sm.sayBoth("I have just given you a book that gives you the list of skills you can acquire as a Pirate. Pirates are blessed with remarkable dexterity and power, using their guns for long-range attacks while using their power in melee combat situations.");
            sm.sayBoth("I just gave you a little bit of #bSP#k. When you open up the #bSkill menu#k on the lower right corner of the screen, there are skills you can learn by using your SP. One warning, though; you can't raise them all at once. There are also skills you can acquire only after having learned a couple of skills first.");
            sm.sayBoth("Unlike other jobs, Pirates are very versatile in combat. They can attack close-range with their fists or long-range with their guns. Gunslingers use elemental-based bullets for added damage, while Brawlers transform their energy into devastating melee attacks.");
            sm.sayBoth("Remember that being a Pirate means quick reflexes and versatility in battle. Work hard and aim to be the best that you can be. I'll be here if you want to make the 2nd job advancement.");
            sm.sayPrev("Oh, and if you have any questions about being a Pirate, feel free to ask. I don't know EVERYTHING, per se, but I'll help you out with all that I know of. Until then, farewell...");
        } else if (sm.getJob() == 500 && sm.getLevel() < 30) {
            final Map<Integer, String> options = Map.of(
                    0, "What are the basic characteristics of being a Pirate?",
                    1, "What sort of weapons does a Pirate use?",
                    2, "What kind of armor can a Pirate wear?",
                    3, "What types of skills does a Pirate have?"
            );
            final int answer = sm.askMenu("Any questions about being a Pirate?#b", options);
            if (answer == 0) {
                sm.sayOk("Pirates are blessed with outstanding dexterity and power, utilizing their guns for long-range attacks while using their power in melee combat situations. Gunslingers use elemental-based bullets for added damage, while Brawlers transform their energy for maximum effect.\r\n\r\nPirates are pretty unique in that you can use both close-range and long-range attacks, depending on which path you choose. If you enjoy explosive attacks at close-range, choose to be an Brawler. If you enjoy sniping from afar, choose to be a Gunslinger.");
            } else if (answer == 1) {
                sm.sayOk("Pirates can use knuckles and guns as weapons. For those that enjoy pummeling their enemies up close, knuckles are the way to go. For those that enjoy shooting enemies from afar, guns are the perfect weapon. Unlike other weapons, guns require bullets to fire, so be sure to keep a good stock at all times.\r\n\r\nThe Brawler uses Knuckles to fight and the Gunslinger uses Guns to shoot. Those that choose to fight with knuckles will have some of the highest HP out of all the jobs, along with fast-paced, combo-based attacking abilities.");
            } else if (answer == 2) {
                sm.sayOk("Pirates possess high HP and avoidability, so the defensive ability of your armor isn't the most important factor to look for. It's more important to look for bonuses in STR and DEX when choosing armor. Of course, your armor must match your level to wear it.");
            } else if (answer == 3) {
                sm.sayOk("For Pirates, you have various attacking and supplemental skills at your disposal. Brawlers use #bSomersault Kick#k and #bDouble Shot#k for close combat, while Gunslingers use #bDouble Shot#k and #bInvisibility#k to shoot from afar.\r\n\r\nAll Pirates can use #bBullet Time#k, which allows you to temporarily avoid all attacks. Pirates also have #bDash#k, which allows you to cover distances very quickly. There are so many useful skills, so definitely experiment and try them all.");
            }
        } else if (sm.getJob() == 500) {
            // 2nd Job Advancement: Pirate → Brawler/Gunslinger
            final int jobChangeLevel = JobConstants.getJobChangeLevel(Job.BRAWLER.getJobId(), 1, 2);
            if (sm.getLevel() < jobChangeLevel) {
                sm.sayOk("You're not ready yet. Train harder and reach #bLevel " + jobChangeLevel + "#k first!");
                return;
            }
            sm.sayNext("You've trained well as a Pirate. I can see your potential. Now it's time to specialize. Will you fight up close with your fists, or attack from range with your guns?");
            final Map<Integer, String> jobOptions = new HashMap<>();
            jobOptions.put((int) Job.BRAWLER.getJobId(), "#bBrawler#k - Close-combat specialist using knuckles. High HP and devastating melee combos.");
            jobOptions.put((int) Job.GUNSLINGER.getJobId(), "#bGunslinger#k - Ranged specialist using guns. Elemental bullets and high mobility.");
            final int selectedJob = sm.askMenu("Choose your path:#b", jobOptions);

            String jobName;
            String jobDescription;
            if (selectedJob == Job.BRAWLER.getJobId()) {
                jobName = "Brawler";
                jobDescription = "Brawlers are melee fighters who use their fists and knuckles to deliver devastating combos. You will have some of the highest HP in the game and powerful close-range attacks. This path is perfect for those who want to get up close and personal with enemies.";
            } else {
                jobName = "Gunslinger";
                jobDescription = "Gunslingers use guns to attack from a distance with elemental bullets. You have high mobility and can deal consistent ranged damage. This path is perfect for those who prefer to keep their distance while dealing steady damage.";
            }

            if (!sm.askYesNo("You want to become a #b" + jobName + "#k. " + jobDescription + "\r\n\r\nThis is a permanent decision. Are you sure?")) {
                sm.sayOk("Take your time. Come back when you've decided.");
                return;
            }

            sm.sayNext("Great! You are now a #b" + jobName + "#k! Your pirate abilities have evolved. Train hard!");
            sm.setJob(Job.getById(selectedJob));
            sm.sayBoth("As a " + jobName + ", you've gained new abilities. Check your skill window!");
            sm.sayBoth("Continue training. When you reach #bLevel 70#k, return for your 3rd job advancement!");
        } else if (sm.getJob() == 510 || sm.getJob() == 520) {
            // 3rd Job Advancement - Direct to El Nath
            sm.sayOk("You've become a formidable pirate! To advance to 3rd job, you must travel to #bEl Nath#k and seek out the pirate instructor there.");
        } else if (sm.getJob() == 511 || sm.getJob() == 521) {
            // 4th Job Advancement - Direct to El Nath for quest
            sm.sayOk("You have reached the peak of piracy! To achieve your 4th job advancement, you must travel to #bEl Nath#k and speak with the instructor there. They will guide you on your final trial.");
        } else {
            sm.sayOk("Hmmm... I think you need to train a bit more before I can help you. Come back to me once you are much stronger.");
        }
    }

    // DRAGON RIDER QUESTS (6008-6013) - Level 200 Congratulations -----------------------------------------------

    @Script("q6008s")
    public static void q6008s(ScriptManager sm) {
        // Quest 6008 - Dragon Rider (START) - Warriors
        // Harmonia (2081100) - El Nath / Leafre
        sm.sayNext("I know who you are. You were born a warrior a long time ago at a town deep in the mountains, established your presence and power at the land of the snow, and... I also bestowed you of a name worthy of a great warrior.");

        sm.sayBoth("Now that you have reached the apex of this profession, I'll introduce to you a new companion. Many warriors who have taken their mind and body to the very limit, strived for it, but couldn't get it, and now you have the opportunity to get it... What do you think... do you want #t1902002#?");

        if (sm.askYesNo("It may not be a human, but #t1902002# is much more proud than the humans... but, if it's someone like you, who's reached the level only a select few can ever reach, then it should serve you with the utmost loyalty.\r\n\r\nIf you are ready to engage in a powerful friendship with an unknown being, then let me know.")) {
            sm.forceStartQuest(6008);
            sm.sayNext("You are ready to meet your new companion! However, you will need to first obtain a #b#t1902001##k. This mount shows your readiness to handle an even greater creature.");
            sm.sayOk("Once you have obtained the #b#t1902001##k, return to me and we will complete the bonding ceremony with #t1902002#!");
        } else {
            sm.sayOk("I don't think you are ready. No one in Maple can defeat you, so why fear the change? As a warrior whose inner strength knows no limits, this should seem enticing to you...");
        }
    }

    @Script("q6008e")
    public static void q6008e(ScriptManager sm) {
        // Quest 6008 - Dragon Rider (END) - Warriors
        final int SILVER_MANE = 1902001;
        final int DRAGON_MOUNT = 1902002;

        if (!sm.hasItem(SILVER_MANE, 1)) {
            sm.sayOk("I don't think you are ready to fully embrace #t1902002#, yet. In order for you to meet #t1902002#, you'll first have to have some experience with #t1902001#. Until you bring me #b#t1902001##k, I am afraid I won't be able to introduce you to #t1902002#.");
            return;
        }

        sm.sayNext("Once you are ready to accept your new companion, step up. You will now be encountering a mysterious being totally unlike anything you've ever faced.");

        if (sm.askYesNo("Are you ready to exchange your #b#t1902001##k for the legendary #b#t1902002##k?")) {
            if (!sm.removeItem(SILVER_MANE, 1)) {
                sm.sayOk("There seems to be an issue. Please make sure you have #b#t1902001##k in your inventory.");
                return;
            }

            if (!sm.addItem(DRAGON_MOUNT, 1)) {
                sm.sayOk("Please make sure you have space in your inventory.");
                sm.addItem(SILVER_MANE, 1); // Return the Silver Mane
                return;
            }

            sm.forceCompleteQuest(6008);
            sm.sayOk("You are the one who has already exceeded the boundaries and the limits of a human being... the only time the proud #t1902002# bows its head is to you. Hopefully you'll experience new, amazing adventures that are surely to await you with #t1902002#...");
        } else {
            sm.sayOk("Come back when you are ready to accept this power.");
        }
    }

    @Script("q6009s")
    public static void q6009s(ScriptManager sm) {
        // Quest 6009 - Dragon Rider (START) - Magicians
        // Gritto (2081200) - El Nath / Leafre
        sm.sayNext("I know who you are. You were born a magician a long time ago at a town deep in the forest where mana filled the air, established your presence and power at the land of the snow, and... I also bestowed you of a name worthy of a great magician.");

        sm.sayBoth("Now that you have reached the very top of the magicians, I'll introduce to you a new companion. Many magicians who have taken their mind and body to the very limit in search of mana, strived for it, but couldn't get it, and now you have the opportunity to get it... What do you think... do you want #t1902002#?");

        if (sm.askYesNo("It may not be a human, but #t1902002# is much more proud than the humans... but, if it's someone like you, who's reached the level only a select few can dare reach, then it should serve you with the utmost loyalty.\r\n\r\nIf you are ready to engage in a powerful friendship with an unknown being, then let me know.")) {
            sm.forceStartQuest(6009);
            sm.sayNext("You are ready to meet your new companion! However, you will need to first obtain a #b#t1902001##k. This mount shows your readiness to handle an even greater creature.");
            sm.sayOk("Once you have obtained the #b#t1902001##k, return to me and we will complete the bonding ceremony with #t1902002#!");
        } else {
            sm.sayOk("I don't think you are ready. No one in Maple can defeat you, so why fear the change? As a mage who has explored the limits of supreme magical power, this should seem enticing to you...");
        }
    }

    @Script("q6009e")
    public static void q6009e(ScriptManager sm) {
        // Quest 6009 - Dragon Rider (END) - Magicians
        final int SILVER_MANE = 1902001;
        final int DRAGON_MOUNT = 1902002;

        if (!sm.hasItem(SILVER_MANE, 1)) {
            sm.sayOk("I don't think you are ready to fully embrace #t1902002#, yet. In order for you to meet #t1902002#, you'll first have to have some experience with #t1902001#. Until you bring me #b#t1902001##k, I am afraid I won't be able to introduce you to #t1902002#.");
            return;
        }

        sm.sayNext("Once you are ready to accept your new companion, step up. You will now be encountering a mysterious being totally unlike anything you've ever faced.");

        if (sm.askYesNo("Are you ready to exchange your #b#t1902001##k for the legendary #b#t1902002##k?")) {
            if (!sm.removeItem(SILVER_MANE, 1)) {
                sm.sayOk("There seems to be an issue. Please make sure you have #b#t1902001##k in your inventory.");
                return;
            }

            if (!sm.addItem(DRAGON_MOUNT, 1)) {
                sm.sayOk("Please make sure you have space in your inventory.");
                sm.addItem(SILVER_MANE, 1); // Return the Silver Mane
                return;
            }

            sm.forceCompleteQuest(6009);
            sm.sayOk("You are the one who has already exceeded the boundaries and the limits of a human being... the only time the proud #t1902002# bows its head is to you. Hopefully you'll experience new, amazing adventures that are surely to await you with #t1902002#...");
        } else {
            sm.sayOk("Come back when you are ready to accept this power.");
        }
    }

    @Script("q6010s")
    public static void q6010s(ScriptManager sm) {
        // Quest 6010 - Dragon Rider (START) - Bowmen
        // Legor (2081300) - El Nath / Leafre
        sm.sayNext("I know who you are. You were born a bowman a long time ago at a town fiercely protected by the Elves, established your presence and power at the land of the snow, and... I also bestowed you of a name worthy of a great bowman.");

        sm.sayBoth("Now that you have reached the highest you can go as a bowman, I'll introduce to you a new companion. Countless bowmen have used bows to pierce the darkness this world offers, strived for it, but couldn't get it, and now you have the opportunity to get it... What do you think... do you want #t1902002#?");

        if (sm.askYesNo("It may not be a human, but #t1902002# is much more proud than the humans... but, if it's someone like you, who's reached the level only a select few can dare reach, then it should serve you with the utmost loyalty.\r\n\r\nIf you are ready to engage in a powerful friendship with an unknown being, then let me know.")) {
            sm.forceStartQuest(6010);
            sm.sayNext("You are ready to meet your new companion! However, you will need to first obtain a #b#t1902001##k. This mount shows your readiness to handle an even greater creature.");
            sm.sayOk("Once you have obtained the #b#t1902001##k, return to me and we will complete the bonding ceremony with #t1902002#!");
        } else {
            sm.sayOk("I don't think you are ready. No one in Maple can defeat you, so why fear the change? As one with superior accuracy and the keenest eyes in the Maple World, this should seem enticing to you...");
        }
    }

    @Script("q6010e")
    public static void q6010e(ScriptManager sm) {
        // Quest 6010 - Dragon Rider (END) - Bowmen
        final int SILVER_MANE = 1902001;
        final int DRAGON_MOUNT = 1902002;

        if (!sm.hasItem(SILVER_MANE, 1)) {
            sm.sayOk("I don't think you are ready to fully embrace #t1902002#, yet. In order for you to meet #t1902002#, you'll first have to have some experience with #t1902001#. Until you bring me #b#t1902001##k, I am afraid I won't be able to introduce you to #t1902002#.");
            return;
        }

        sm.sayNext("Once you are ready to accept your new companion, step up. You will now be encountering a mysterious being totally unlike anything you've ever faced.");

        if (sm.askYesNo("Are you ready to exchange your #b#t1902001##k for the legendary #b#t1902002##k?")) {
            if (!sm.removeItem(SILVER_MANE, 1)) {
                sm.sayOk("There seems to be an issue. Please make sure you have #b#t1902001##k in your inventory.");
                return;
            }

            if (!sm.addItem(DRAGON_MOUNT, 1)) {
                sm.sayOk("Please make sure you have space in your inventory.");
                sm.addItem(SILVER_MANE, 1); // Return the Silver Mane
                return;
            }

            sm.forceCompleteQuest(6010);
            sm.sayOk("You are the one who has already exceeded the boundaries and the limits of a human being... the only time the proud #t1902002# bows its head is to you. Hopefully you'll experience new, amazing adventures that are surely to await you with #t1902002#...");
        } else {
            sm.sayOk("Come back when you are ready to accept this power.");
        }
    }

    @Script("q6011s")
    public static void q6011s(ScriptManager sm) {
        // Quest 6011 - Dragon Rider (START) - Thieves
        // Hellin (2081400) - El Nath / Leafre
        sm.sayNext("I know who you are. You were born a thief a long time ago at a dark city late at night, established your presence and power at the land of the snow, and... I also bestowed you of a new name worthy of a great thief...");

        sm.sayBoth("Now that you have reached the apex as a thief, I'll introduce to you a new companion. Countless thieves, considered the dominant forces of the dark, strived for it, but couldn't get it, and now you have the opportunity to get it... What do you think... do you want #t1902002#?");

        if (sm.askYesNo("It may not be a human, but #t1902002# is much more proud than the humans... but, if it's someone like you, who's reached the level only a select few can dare reach, then it should serve you with the utmost loyalty.\r\n\r\nIf you are ready to engage in a powerful friendship with an unknown being, then let me know.")) {
            sm.forceStartQuest(6011);
            sm.sayNext("You are ready to meet your new companion! However, you will need to first obtain a #b#t1902001##k. This mount shows your readiness to handle an even greater creature.");
            sm.sayOk("Once you have obtained the #b#t1902001##k, return to me and we will complete the bonding ceremony with #t1902002#!");
        } else {
            sm.sayOk("I don't think you are ready. No one in Maple can defeat you, so why fear the change? As a cunning master of stealth tactics, one who feels at home amongst the shadows, this should seem enticing to you...");
        }
    }

    @Script("q6011e")
    public static void q6011e(ScriptManager sm) {
        // Quest 6011 - Dragon Rider (END) - Thieves
        final int SILVER_MANE = 1902001;
        final int DRAGON_MOUNT = 1902002;

        if (!sm.hasItem(SILVER_MANE, 1)) {
            sm.sayOk("I don't think you are ready to fully embrace #t1902002#, yet. In order for you to meet #t1902002#, you'll first have to have some experience with #t1902001#. Until you bring me #b#t1902001##k, I am afraid I won't be able to introduce you to #t1902002#.");
            return;
        }

        sm.sayNext("Once you are ready to accept your new companion, step up. You will now be encountering a mysterious being totally unlike anything you've ever faced.");

        if (sm.askYesNo("Are you ready to exchange your #b#t1902001##k for the legendary #b#t1902002##k?")) {
            if (!sm.removeItem(SILVER_MANE, 1)) {
                sm.sayOk("There seems to be an issue. Please make sure you have #b#t1902001##k in your inventory.");
                return;
            }

            if (!sm.addItem(DRAGON_MOUNT, 1)) {
                sm.sayOk("Please make sure you have space in your inventory.");
                sm.addItem(SILVER_MANE, 1); // Return the Silver Mane
                return;
            }

            sm.forceCompleteQuest(6011);
            sm.sayOk("You are the one who has already exceeded the boundaries and the limits of a human being... the only time the proud #t1902002# bows its head is to you. Hopefully you'll experience new, amazing adventures that are surely to await you with #t1902002#...");
        } else {
            sm.sayOk("Come back when you are ready to accept this power.");
        }
    }

    @Script("q6013s")
    public static void q6013s(ScriptManager sm) {
        // Quest 6013 - Dragon Rider (START) - Pirates
        // Kydo (2081500) - El Nath / Leafre
        sm.sayNext("I know who you are. You were born a Pirate a long time ago aboard the mighty vessel known as the Nautilus. You established your presence and power at the land of the snow, and...I also bestowed you of a new name worthy of a great Pirate...");

        sm.sayBoth("Now that you have reached the apex as a Pirate, I'll introduce to you a new companion. Countless Pirates, from Ms.Behave to Loosetooth Pete, strived for it, but couldn't get it, and now you have the opportunity to get it... What do you think...do you want #t1902002#?");

        if (sm.askYesNo("It may not be a human, but #t1902002# possesses a fierce spirit...but, for someone like you, who's reached the level only a select few can dare reach, then it should serve you with the utmost loyalty.\r\n\r\nIf you are ready to initiate a powerful friendship, then let me know.")) {
            sm.forceStartQuest(6013);
            sm.sayNext("You are ready to meet your new companion! However, you will need to first obtain a #b#t1902001##k. This mount shows your readiness to handle an even greater creature.");
            sm.sayOk("Once you have obtained the #b#t1902001##k, return to me and we will complete the bonding ceremony with #t1902002#!");
        } else {
            sm.sayOk("I don't think you are ready. No one in Maple can defeat you, so why fear the change? As the undisputed master of the sea, one who feels at home amongst the waves, this should seem enticing to you...");
        }
    }

    @Script("q6013e")
    public static void q6013e(ScriptManager sm) {
        // Quest 6013 - Dragon Rider (END) - Pirates
        final int SILVER_MANE = 1902001;
        final int DRAGON_MOUNT = 1902002;

        if (!sm.hasItem(SILVER_MANE, 1)) {
            sm.sayOk("I don't think you are ready to fully embrace #t1902002#, yet. In order for you to meet #t1902002#, you'll first have to have some experience with #t1902001#. Until you bring me #b#t1902001##k, I am afraid I won't be able to introduce you to your new friend. Return to me when you are ready, and I shall make the introduction.");
            return;
        }

        sm.sayNext("Once you are ready to accept your new companion, step up. You will encounter a mysterious being unlike anything you've ever faced.");

        if (sm.askYesNo("Are you ready to exchange your #b#t1902001##k for the legendary #b#t1902002##k?")) {
            if (!sm.removeItem(SILVER_MANE, 1)) {
                sm.sayOk("There seems to be an issue. Please make sure you have #b#t1902001##k in your inventory.");
                return;
            }

            if (!sm.addItem(DRAGON_MOUNT, 1)) {
                sm.sayOk("Please make sure you have space in your inventory.");
                sm.addItem(SILVER_MANE, 1); // Return the Silver Mane
                return;
            }

            sm.forceCompleteQuest(6013);
            sm.sayOk("You are the one who has already exceeded the boundaries and the limits of a human being...the only time the proud #t1902002# bows its head is to you. Hopefully you'll experience new, amazing adventures that are surely to await you with #t1902002#...");
        } else {
            sm.sayOk("Come back when you are ready to accept this power.");
        }
    }

    // ADDITIONAL EXPLORER QUESTS (2100-2400) --------------------------------------------------------------------

    @Script("q2124e")
    public static void q2124e(ScriptManager sm) {
        // Quest 2124 - A Supply from the Sand Crew (END)
        // NPC: 2101002
        final int QUEST_ITEM_4031619 = 4031619;

        if (!sm.hasItem(QUEST_ITEM_4031619, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031619, 1);
            sm.forceCompleteQuest(2124);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2126e")
    public static void q2126e(ScriptManager sm) {
        // Quest 2126 - A Supply from the Sand Crew! (END)
        // NPC: 2101002
        final int QUEST_ITEM_4031624 = 4031624;

        if (!sm.hasItem(QUEST_ITEM_4031624, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4031624, 1);
            sm.forceCompleteQuest(2126);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2127e")
    public static void q2127e(ScriptManager sm) {
        // Quest 2127 - To the Desert... (END)
        // NPC: 1022002
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2127);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2147e")
    public static void q2147e(ScriptManager sm) {
        // Quest 2147 - Stumpy's Seedling (END)
        // NPC: 1022006
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2147);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2147s")
    public static void q2147s(ScriptManager sm) {
        // Quest 2147 - Stumpy's Seedling (START)
        // NPC: 1022006
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2147);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2148s")
    public static void q2148s(ScriptManager sm) {
        // Quest 2148 - Truth of the Rumor-Blackbull (START)
        // NPC: 1020000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2148);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2149s")
    public static void q2149s(ScriptManager sm) {
        // Quest 2149 - Truth of the Rumor-Manji (START)
        // NPC: 1022002
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2149);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2150s")
    public static void q2150s(ScriptManager sm) {
        // Quest 2150 - Truth of the Rumor-Ayan (START)
        // NPC: 1022007
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2150);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2151s")
    public static void q2151s(ScriptManager sm) {
        // Quest 2151 - Truth of the Rumor- Dances with Balrog (START)
        // NPC: 1022000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2151);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2152s")
    public static void q2152s(ScriptManager sm) {
        // Quest 2152 - Truth of the Rumor-Betty (START)
        // NPC: 1032104
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2152);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2156e")
    public static void q2156e(ScriptManager sm) {
        // Quest 2156 - A rainbow snail shell that makes wishes come true!? (END)
        // NPC: 1012102
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2156);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2197e")
    public static void q2197e(ScriptManager sm) {
        // Quest 2197 - Tienk, the Monster Book Salesman (END)
        // NPC: 2006
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2197);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2214e")
    public static void q2214e(ScriptManager sm) {
        // Quest 2214 - The Run-down Huts in the Swamp (END)
        // NPC: 1052108
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2214);
            sm.addItem(4031894, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2215e")
    public static void q2215e(ScriptManager sm) {
        // Quest 2215 - Find the Crumpled Piece of Paper Again (END)
        // NPC: 1052108
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2215);
            sm.addMoney(-2000); // Mesos reward
            sm.addItem(4031894, 1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2216s")
    public static void q2216s(ScriptManager sm) {
        // Quest 2216 - Information from Mr. Pickall (START)
        // NPC: 9000008
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2216);
            sm.addItem(4031894, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2217s")
    public static void q2217s(ScriptManager sm) {
        // Quest 2217 - Information from Shumi (START)
        // NPC: 1052102
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2217);
            sm.addItem(4031894, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2218s")
    public static void q2218s(ScriptManager sm) {
        // Quest 2218 - Information from Nella (START)
        // NPC: 1052103
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2218);
            sm.addItem(4031894, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2219s")
    public static void q2219s(ScriptManager sm) {
        // Quest 2219 - Information from Jake (START)
        // NPC: 1052006
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2219);
            sm.addItem(4031894, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2228s")
    public static void q2228s(ScriptManager sm) {
        // Quest 2228 - Reef's Gratitude (START)
        // NPC: 1032108
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2228);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2232e")
    public static void q2232e(ScriptManager sm) {
        // Quest 2232 - Find a Junior! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2232);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2233e")
    public static void q2233e(ScriptManager sm) {
        // Quest 2233 - Raise the Rep! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2233);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2234e")
    public static void q2234e(ScriptManager sm) {
        // Quest 2234 - Enjoy the Entitlement! (END)
        // NPC: Unknown
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2234);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2238s")
    public static void q2238s(ScriptManager sm) {
        // Quest 2238 - Who is the Owner of the Mysterious Note? (START)
        // NPC: 1061014
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2238);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2244e")
    public static void q2244e(ScriptManager sm) {
        // Quest 2244 - Tristan's Successor (END)
        // NPC: 1061017
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2244);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2245s")
    public static void q2245s(ScriptManager sm) {
        // Quest 2245 - To Tristan's Tomb (START)
        // NPC: 1061014
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2245);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2251e")
    public static void q2251e(ScriptManager sm) {
        // Quest 2251 - Zombie Mushroom Signal 3 (END)
        // NPC: 1061011
        final int QUEST_ITEM_4032399 = 4032399;

        if (!sm.hasItem(QUEST_ITEM_4032399, 20)) {
            sm.sayOk("You need 20 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032399, 20);
            sm.forceCompleteQuest(2251);
            sm.addExp(8000); // EXP reward
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2254s")
    public static void q2254s(ScriptManager sm) {
        // Quest 2254 - Karcasa of the Desert (START)
        // NPC: 1061011
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2254);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2257e")
    public static void q2257e(ScriptManager sm) {
        // Quest 2257 - Karcasa Relents (END)
        // NPC: 2110005
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2257);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2258e")
    public static void q2258e(ScriptManager sm) {
        // Quest 2258 - Meerkats Listen During the Day (END)
        // NPC: 2110005
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2258);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2258s")
    public static void q2258s(ScriptManager sm) {
        // Quest 2258 - Meerkats Listen During the Day (START)
        // NPC: 2110005
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2258);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2259e")
    public static void q2259e(ScriptManager sm) {
        // Quest 2259 - Scorpions Can't Listen at Night (END)
        // NPC: 2110005
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2259);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2259s")
    public static void q2259s(ScriptManager sm) {
        // Quest 2259 - Scorpions Can't Listen at Night (START)
        // NPC: 2110005
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2259);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2260e")
    public static void q2260e(ScriptManager sm) {
        // Quest 2260 - To the Mushroom Castle! (END)
        // NPC: 2110005
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2260);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2260s")
    public static void q2260s(ScriptManager sm) {
        // Quest 2260 - To the Mushroom Castle! (START)
        // NPC: 2110005
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2260);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2291e")
    public static void q2291e(ScriptManager sm) {
        // Quest 2291 - Admission to the VIP Zone (END)
        // NPC: 1052125
        final int QUEST_ITEM_4032521 = 4032521;

        if (!sm.hasItem(QUEST_ITEM_4032521, 10)) {
            sm.sayOk("You need 10 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032521, 10);
            sm.forceCompleteQuest(2291);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2293s")
    public static void q2293s(ScriptManager sm) {
        // Quest 2293 - The Last Song (START)
        // NPC: 1052120
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2293);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2332e")
    public static void q2332e(ScriptManager sm) {
        // Quest 2332 - Where's Violetta? (END)
        // NPC: 1300002
        sm.sayNext("You have completed the quest!");

        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.forceCompleteQuest(2332);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }

    @Script("q2342s")
    public static void q2342s(ScriptManager sm) {
        // Quest 2342 - The Recovered Royal Seal (START)
        // NPC: 1300002
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(2342);
            sm.addItem(4001318, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q2363e")
    public static void q2363e(ScriptManager sm) {
        // Quest 2363 - Dual Blade: Time for the Awakening (END)
        // First Job Advancement: Rogue -> Blade Recruit (430)
        // NPC: Lady Syl (1056000)
        final int MIRROR_OF_INSIGHT = 4032616;

        // Check if player has the Mirror of Insight
        if (!sm.hasItem(MIRROR_OF_INSIGHT, 1)) {
            sm.sayOk("You must bring me the #bMirror of Insight#k to prove you are ready.");
            return;
        }

        // Check level requirement
        if (sm.getLevel() < 20) {
            sm.sayOk("You must be at least #bLevel 20#k to awaken as a Dual Blade.");
            return;
        }

        sm.sayNext("I can see it in your eyes... the Mirror of Insight has chosen you. You have proven yourself worthy to walk the path of the Dual Blade.");
        sm.sayBoth("From this moment forward, you are a #bBlade Recruit#k. Your journey as a Dual Blade has truly begun!");

        // Remove Mirror of Insight
        sm.removeItem(MIRROR_OF_INSIGHT, 1);

        // Job advancement to Blade Recruit
        sm.setJob(Job.BLADE_RECRUIT); // Job 430

        // Give SP for job advancement (jobLevel 1, 1 SP)
        sm.addSp(1, 1);

        // Complete the quest
        sm.forceCompleteQuest(2363);

        sm.sayOk("Congratulations on awakening as a Dual Blade! You have gained #b1 SP#k. Continue your training and return to me when you reach Level 30 for your next advancement.");
    }

    @Script("q2367e")
    public static void q2367e(ScriptManager sm) {
        // Quest 2367 - Seventh Mission: Eyewitness (END)
        // NPC: Lady Syl (1056000)
        // Requires quest 2368 to be completed

        // Check if quest 2368 is completed
        if (!sm.hasQuestCompleted(2368)) {
            sm.sayOk("You haven't completed the 'Eyewitness Holding the Key' quest yet. Please finish that first.");
            return;
        }

        // Multi-stage conversation with choices
        final int answer1 = sm.askMenu("I heard that you were handling this mission. Were you able to find any eyewitnesses?",
                Map.of(0, "I met and spoke with Manji in Perion since he knows Tristan, who was a friend of the former Dark Lord. I figured he might know something about the former Dark Lord."));

        final int answer2 = sm.askMenu("Yes, I suppose that's reasonable. So, what did he say?",
                Map.of(0, "He said that the former Dark Lord went to the Cursed Sanctuary upon Tristan's request to join in the battle against Balrog. Unfortunately, they were somehow separated, and by the time Tristan and Manji arrived, the former Dark Lord had already passed away."));

        final int answer3 = sm.askMenu("So...did he see anything?",
                Map.of(0, "Manji said that when he got there...he was there...covered in blood..."));

        final int answer4 = sm.askMenu("By 'he' you mean...",
                Map.of(0, "A boy named Jin..."));

        sm.sayNext("Hmpf! It really was him! Jin was the culprit behind the death... Now, the time has come for vengeance!");

        // Give rewards
        sm.addExp(30000);
        sm.forceCompleteQuest(2367);

        sm.sayOk("I need to be alone right now. Please leave.");
    }

    @Script("q2369e")
    public static void q2369e(ScriptManager sm) {
        // Quest 2369 - Dual Blade: Time for the Awakening (END)
        // Second Job Advancement: Blade Recruit → Blade Acolyte (431)
        // NPC: Lady Syl (1056000)
        final int FORMER_DARK_LORD_DIARY = 4032617;

        // Check if player has the diary
        if (!sm.hasItem(FORMER_DARK_LORD_DIARY, 1)) {
            sm.sayOk("You must bring me the #bFormer Dark Lord's Diary#k from the Jazz Bar secret room.");
            return;
        }

        // Check level requirement
        if (sm.getLevel() < 30) {
            sm.sayOk("You must be at least #bLevel 30#k to advance to Blade Acolyte.");
            return;
        }

        sm.sayNext("You've returned with the diary... I can feel my father's presence within these pages.");
        sm.sayBoth("You have proven yourself worthy time and time again. It's time for you to take the next step on your path as a Dual Blade.");

        // Remove diary
        sm.removeItem(FORMER_DARK_LORD_DIARY, 1);

        // Job advancement to Blade Acolyte
        sm.setJob(Job.BLADE_ACOLYTE); // Job 431

        // Give SP for 2nd job advancement
        sm.addSp(2, 1);

        // Complete the quest
        sm.forceCompleteQuest(2369);

        sm.sayOk("Congratulations! You are now a #bBlade Acolyte#k! You have gained #b1 SP#k. Continue your training and become even stronger!");
    }

    @Script("q2374e")
    public static void q2374e(ScriptManager sm) {
        // Quest 2374 - Arec's Secret Letter (END)
        // NPC: Lady Syl (1056000) - Dual Blade 3rd Job Advancement (Level 55)
        final int QUEST_ITEM_4032619 = 4032619;
        final int RING_REWARD = 1132021;

        if (!sm.hasItem(QUEST_ITEM_4032619, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("I've been waiting for you. Do you have Arec's answer? Please give me his letter.");
        sm.sayBoth("We have finally received Arec's official recognition. This is an important moment for us. It's also time that you experience a change.");

        // Check inventory space for ring reward
        if (!sm.canAddItem(RING_REWARD, 1)) {
            sm.sayOk("Please check and see if you have an empty slot available at your equip inventory.");
            return;
        }

        // Complete quest and give rewards
        sm.removeItem(QUEST_ITEM_4032619, 1);
        sm.forceCompleteQuest(2374);
        sm.setJob(Job.BLADE_SPECIALIST); // Job advancement to 432
        sm.addItem(RING_REWARD, 1); // Give ring reward
        sm.sayOk("Now that we have Arec's recognition, you can make a job advancement by going to see him when you reach Lv. 70. Finally, a new future has been opened for the Dual Blades.");
    }

    @Script("q1447e")
    public static void q1447e(ScriptManager sm) {
        // Quest 1447 - Endurance (END)
        // NPC: Lady Syl (1056000) - Dual Blade 3.5 Job Advancement (Level 70)
        // Blade Specialist (432) → Blade Lord (433)
        final int HOLY_STONE = 4031059;
        final int MEDAL_REWARD = 1142109;
        final int BLACK_CHARM = 2290156;

        // Check if player has the Holy Stone (proof of defeating Dark Lord's clone)
        if (!sm.hasItem(HOLY_STONE, 1)) {
            sm.sayOk("You must complete the trial before you can advance to Blade Lord.");
            return;
        }

        // Check level requirement
        if (sm.getLevel() < 70) {
            sm.sayOk("You must be at least #bLevel 70#k to advance to Blade Lord.");
            return;
        }

        sm.sayNext("You passed the test. Not bad. I should tell you, the Dark Lord from another dimension you fought was merely a clone, but I still didn't think you would win. I was surprised the Dark Lord went out of his way to summon his clone using the Holy Stone, but I guess it was worth it.");

        if (!sm.askYesNo("The fight with the master Thief, Dark Lord, has proven your own worth as a Thief. The only thing left is your Job Advancement. Are you prepared to become a Blade Lord, and bring your might to a new level?")) {
            sm.sayOk("Come back when you are ready.");
            return;
        }

        // Check inventory space for rewards
        if (!sm.canAddItem(MEDAL_REWARD, 1) || !sm.canAddItem(BLACK_CHARM, 1)) {
            sm.sayNext("The Job Advancement cannot continue because you either have no room in your Equip or Use tab, or you do not have a Black Charm.");
            return;
        }

        // Job advancement
        sm.removeItem(HOLY_STONE, 1);
        sm.setJob(Job.BLADE_LORD); // Job 433
        sm.addItem(MEDAL_REWARD, 1); // Medal
        sm.addItem(BLACK_CHARM, 1); // Black Charm
        sm.forceCompleteQuest(1447);

        sm.sayOk("You are now a #bBlade Lord#k. As a true Blade Lord, use your strength to the fullest!");
    }

    @Script("q2351e")
    public static void q2351e(ScriptManager sm) {
        // Quest 2351 - First Mission: Infiltration (END)
        // NPC: Ryden (1057001) in Kerning City
        // Requirement: Player must have become a Rogue (job 400)
        final int EARRING_REWARD = 1032076;

        // Check if quest is started
        if (!sm.hasQuestStarted(2351)) {
            sm.sayOk("Huh...is something wrong?");
            return;
        }

        // Check if player has become a Rogue
        if (sm.getJob() != 400) {
            sm.sayOk("Ahh... #h0#, you found me. Now, you haven't forgotten what your mission is, right? In order to get close to the Dark Lord, you have to become his subordinate. Act like a casual beginner and request an advancement as a Rogue. Camouflage is the first step in infiltration. Understood?");
            return;
        }

        sm.sayNext("I see that you have been successful. The plan went smoother than I expected. But hmm... It looks like he didn't teach you all of the skills. I guess the Dark Lord is smarter than I thought... ");
        sm.sayBoth("But we'll just let it be. After all, the goal was for you to advance, and that you did even while being under his suspicious glare. Let me reward you for successfully completing your first mission.");

        // Check inventory space
        if (!sm.canAddItem(EARRING_REWARD, 1)) {
            sm.sayOk("Please make space in your equip inventory first.");
            return;
        }

        // Give rewards
        sm.addExp(500);
        sm.addItem(EARRING_REWARD, 1);
        sm.forceCompleteQuest(2351);

        sm.sayOk("The real mission starts now. You are to act as a Rogue and collect information about the Dark Lord and other thieves. When the time comes, Lady Syl will call for you. Until then, you must be sure to hide your true identity and act like a Rogue. Is that clear?");
    }
}
