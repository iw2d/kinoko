package kinoko.script.quest;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;

public final class ExplorerTutorial extends ScriptHandler {

    @Script("tutorialSkip")
    public static void tutorialSkip(ScriptManager sm) {
        // Maple Administrator (2007)
        //   Maple Road : Mushroom Park (10000)
        if (sm.askYesNo("Would you like to skip the tutorials and head straight to Lith Harbor?")) {
            sm.warp(104000000); // Lith Harbor : Lith Harbor
        } else {
            sm.sayNext("Enjoy your trip.");
        }
    }

    @Script("tutoChatNPC")
    public static void tutoChatNPC(ScriptManager sm) {
        // Maple Road : Mushroom Park (10000)
        //   tuto00 (-95, 428)
        tutorialSkip(sm);
    }

    @Script("begin5")
    public static void begin5(ScriptManager sm) {
        // Robin (2003)
        //   Maple Road : Inside the Dangerous Forest (50000)
        final int answer = sm.askMenu("Now...ask me any questions you may have on traveling!!", Map.ofEntries(
                Map.entry(0, "How do I move?"),
                Map.entry(1, "How do I take down the monsters?"),
                Map.entry(2, "How can I pick up an item?"),
                Map.entry(3, "What happens when I die?"),
                Map.entry(4, "When can I choose a job?"),
                Map.entry(5, "Tell me more about this island!"),
                Map.entry(6, "What should I do to become a Warrior?"),
                Map.entry(7, "What should I do to become a Bowman?"),
                Map.entry(8, "What should I do to become a Magician?"),
                Map.entry(9, "What should I do to become a Thief?"),
                Map.entry(10, "How do I raise the character stats? (S)"),
                Map.entry(11, "How do I check the items that I just picked up?"),
                Map.entry(12, "How do I put on an item?"),
                Map.entry(13, "How do I check out the items that I'm wearing?"),
                Map.entry(14, "What are skills? (K)"),
                Map.entry(15, "How do I get to Victoria Island?"),
                Map.entry(16, "What are mesos?")
        ));
        switch (answer) {
            case 0 -> {
                sm.sayNext("Alright this is how you move. Use #bleft, right arrow#k to move around the flatland and slanted roads, and press #bAlt#k to jump. A select number of shoes improve your speed and jumping abilities.");
                sm.sayBoth("In order to attack the monsters, you'll need to be equipped with a weapon. When equipped, press #bCtrl#k to use the weapon. With the right timing, you'll be able to easily take down the monsters.");
            }
            case 1 -> {
                sm.sayNext("Here's how to take down a monster. Every monster possesses an HP of its own and you'll take them down by attacking with either a weapon or through spells. Of course the stronger they are, the harder it is to take them down.");
                sm.sayBoth("Once you make the job advancement, you'll acquire different kinds of skills, and you can assign them to HotKeys for easier access. If it's an attacking skill, you don't need to press Ctrl to attack, just press the button assigned as a HotKey.");
            }
            case 2 -> {
                sm.sayNext("This is how you gather up an item. Once you take down a monster, an item will be dropped to the ground. When that happens, stand in front of the item and press #bZ#k or #b0 on the NumPad#k to acquire the item.");
                sm.sayBoth("Remember, though, that if your item inventory is full, you won't be able to acquire more. So if you have an item you don't need, sell it so you can make something out of it. The inventory may expand once you make the job advancement.");
            }
            case 3 -> {
                sm.sayNext("Curious to find out what happens when you die? You'll become a ghost when your HP reaches 0. There will be a tombstone in that place and you won't be able to move, although you still will be able to chat.");
                sm.sayBoth("There isn't much to lose when you die if you are just a beginner. Once you have a job, however, it's a different story. You'll lose a portion of your EXP when you die, so make sure you avoid danger and death at all cost.");
            }
            case 4 -> {
                sm.sayNext("When do you get to choose your job? Hahaha, take it easy, my friend. Each job has a requirement set for you to meet. Normally a level between 8 and 10 will do, so work hard.");
                sm.sayBoth("Level isn't the only thing that determines the advancement, though. You also need to boost up the levels of a particular ability based on the occupation. For example, to be a warrior, your STR has to be over 35, and so forth, you know what I'm saying? Make sure you boost up the abilities that has direct implications to your job.");
            }
            case 5 -> {
                sm.sayNext("Want to know about this island? It's called Maple Island and it floats in the air. It's been floating in the sky for a while so the nasty monsters aren't really around. It's a very peaceful island, perfect for beginners!");
                sm.sayBoth("But, if you want to be a powerful player, better not think about staying here for too long. You won't be able to get a job anyway. Underneath this island lies an enormous island called Victoria Island. That place is so much bigger than here, it's not even funny.");
            }
            case 6 -> {
                sm.sayNext("You want to become a #bWarrior#k? Hmm, then I suggest you head over to Victoria Island. Head over to a warrior-town called #rPerion#k and see #bDances with Balrog#k. He'll teach you all about becoming a true warrior. Ohh, and one VERY important thing: You'll need to be at least level 10 in order to become a warrior!!");
            }
            case 7 -> {
                sm.sayNext("You want to become a #bBowman#k? You'll need to go to Victoria Island to make the job advancement. Head over to a bowman-town called #rHenesys#k and talk to the beautiful #bAthena Pierce#k and learn the in's and out's of being a bowman. Ohh, and one VERY important thing: You'll need to be at least level 10 in order to become a bowman!!");
            }
            case 8 -> {
                sm.sayNext("You want to become a #bMagician#k? For you to do that, you'll have to head over to Victoria Island. Head over to a magician-town called #rEllinia#k, and at the very top lies the Magic Library. Inside, you'll meet the head of all wizards, #bGrendel the Really Old#k, who'll teach you everything about becoming a wizard.");
                sm.sayBoth("Oh by the way, unlike other jobs, to become a magician you only need to be at level 8. What comes with making the job advancement early also comes with the fact that it takes a lot to become a true powerful mage. Think long and carefully before choosing your path.");
            }
            case 9 -> {
                sm.sayNext("You want to become a #bThief#k? In order to become one, you'll have to head over to Victoria Island. Head over to a thief-town called #rKerning City#k, and on the shadier side of town, you'll see a thief's hideaway. There, you'll meet #bDark Lord#k who'll teach you everything about being a thief. Ohh, and one VERY important thing: You'll need to be at least level 10 in order to become a thief!!");
            }
            case 10 -> {
                sm.sayNext("You want to know how to raise your character's ability stats? First press #bS#k to check out the ability window. Every time you level up, you'll be awarded 5 ability points aka AP's. Assign those AP's to the ability of your choice. It's that simple.");
                sm.sayBoth("Place your mouse cursor on top of all abilities for a brief explanation. For example, STR for warriors, DEX for bowman, INT for magician, and LUK for thief. That itself isn't everything you need to know, so you'll need to think long and hard on how to emphasize your character's strengths through assigning the points.");
            }
            case 11 -> {
                sm.sayNext("You want to know how to check out the items you've picked up, huh? When you defeat a monster, it'll drop an item on the ground, and you may press #bZ#k to pick up the item. That item will then be stored in your item inventory, and you can take a look at it by simply pressing #bI#k.");
            }
            case 12 -> {
                sm.sayNext("You want to know how to wear the items, right? Press #bI#k to check out your item inventory. Place your mouse cursor on top of an item and double-click on it to put it on your character. If you find yourself unable to wear the item, chances are your character does not meet the level & stat requirements. You can also put on the item by opening the equipment inventory (#bE#k) and dragging the item into it. To take off an item, double-click on the item at the equipment inventory.");
            }
            case 13 -> {
                sm.sayNext("You want to check on the equipped items, right? Press #bE#k to open the equipment inventory, where you'll see exactly what you are wearing right at the moment. To take off an item, double-click on the item. The item will then be sent to the item inventory.");
            }
            case 14 -> {
                sm.sayNext("The special 'abilities' you get after acquiring a job are called skills. You'll acquire skills that are specifically for that job. You're not at that stage yet, so you don't have any skills yet, but just remember that to check on your skills, press #bK#k to open the skill book. It'll help you down the road.");
            }
            case 15 -> {
                sm.sayNext("How do you get to Victoria Island? On the east of this island there's a harbor called Southperry. There, you'll find a ship that flies in the air. In front of the ship stands the captain. Ask him about it.");
                sm.sayBoth("Oh yeah! One last piece of information before I go. If you are not sure where you are, always press #bW#k. The world map will pop up with the locator showing where you stand. You won't have to worry about getting lost with that.");
            }
            case 16 -> {
                sm.sayNext("It's the currency used in MapleStory. You may purchase items through mesos. To earn them, you may either defeat the monsters, sell items at the store, or complete quests...");
            }
        }
    }

    @Script("begin7")
    public static void begin7(ScriptManager sm) {
        // Shanks (22000)
        //   Maple Road : Southperry (2000000)
        if (!sm.askYesNo("Take this ship and you'll head off to a bigger continent. For #e150 mesos#n, I'll take you to #bVictoria Island#k. The thing is, once you leave this place, you can't ever come back. What do you think? Do you want to go to Victoria Island?")) {
            sm.sayOk("Hmm... I guess you still have things to do here?");
            return;
        }
        if (sm.getLevel() < 7) {
            sm.sayOk("Let's see... I don't think you are strong enough. You'll have to be at least Level 7 to go to Victoria Island.");
            return;
        }
        // Lucas's Recommendation Letter
        if (sm.hasItem(4031801)) {
            sm.sayNext("Okay, now give me 150 mesos... Hey, what's that? Is that the recommendation letter from Lucas, the chief of Amherst? Hey, you should have told me you had this. I, Shanks, recognize greatness when I see one, and since you have been recommended by Lucas, I see that you have a great, great potential as an adventurer. No way would I charge you for this trip!");
            sm.sayBoth("Since you have the recommendation letter, I won't charge you for this. Alright, buckle up, because we're going to head to Victoria Island right now, and it might get a bit turbulent!!");
            if (sm.removeItem(4031801, 1)) {
                sm.warp(2010000); // goLith
            }
        } else {
            sm.sayNext("Bored of this place? Here... Give me #e150 mesos#n first...");
            if (sm.addMoney(-150)) {
                sm.sayNext("Awesome! #e150#n mesos accepted! Alright, off to Victoria Island!");
                sm.warp(2010000); // goLith
            } else {
                sm.sayOk("What? You're telling me you wanted to go without any money? You're one weirdo...");
            }
        }
    }

    @Script("infoSwordman")
    public static void infoSwordman(ScriptManager sm) {
        // Dances with Balrog : Warrior Job Instructor (10202)
        //   Maple Road : Split Road of Destiny (1020000)
        sm.sayNext("Warriors possess an enormous power with stamina to back it up, and they shine the brightest in melee combat situation. Regular attacks are powerful to begin with, and armed with complex skills, the job is perfect for explosive attacks.");
        if (sm.askYesNo("Would you like to experience what it's like to be a Warrior?")) {
            sm.warp(1020100); // goSwordman
        } else {
            sm.sayNext("If you wish to experience what it's like to be a Warrior, come see me again.");
        }
    }

    @Script("infoMagician")
    public static void infoMagician(ScriptManager sm) {
        // Grendel the Really Old : Magician Job Instructor (10201)
        //   Maple Road : Split Road of Destiny (1020000)
        sm.sayNext("Magicians are armed with flashy element-based spells and secondary magic that aids party as a whole. After the 2nd job adv., the elemental-based magic will provide ample amount of damage to enemies of opposite element.");
        if (sm.askYesNo("Would you like to experience what it's like to be a Magician?")) {
            sm.warp(1020200); // goMagician
        } else {
            sm.sayNext("If you wish to experience what it's like to be a Magician, come see me again.");
        }
    }

    @Script("infoArcher")
    public static void infoArcher(ScriptManager sm) {
        // Athena Pierce : Bowman Job Instructor (10200)
        //   Maple Road : Split Road of Destiny (1020000)
        sm.sayNext("Bowmen are blessed with dexterity and power, taking charge of long-distance attacks, providing support for those at the front line of the battle. Very adept at using landscape as part of the arsenal.");
        if (sm.askYesNo("Would you like to experience what it's like to be a Bowman?")) {
            sm.warp(1020300); // goArcher
        } else {
            sm.sayNext("If you wish to experience what it's like to be a Bowman, come see me again.");
        }
    }

    @Script("infoRogue")
    public static void infoRogue(ScriptManager sm) {
        // Dark Lord : Thief Job Instructor (10203)
        //   Maple Road : Split Road of Destiny (1020000)
        sm.sayNext("Thieves are a perfect blend of luck, dexterity, and power that are adept at surprise attacks against helpless enemies. A high level of avoidability and speed allows the thieves to attack enemies with various angles.");
        if (sm.askYesNo("Would you like to experience what it's like to be a Thief?")) {
            sm.warp(1020400); // goRogue
        } else {
            sm.sayNext("If you wish to experience what it's like to be a Thief, come see me again.");
        }
    }

    @Script("infoPirate")
    public static void infoPirate(ScriptManager sm) {
        // Kyrin : Pirate Job Instructor (10204)
        //   Maple Road : Split Road of Destiny (1020000)
        sm.sayNext("Pirates are blessed with outstanding dexterity and power, utilizing their guns for long-range attacks while using their power on melee combat situations. Gunslingers use elemental-based bullets for added damage, while Infighters transform to a different being for maximum effect.");
        if (sm.askYesNo("Would you like to experience what it's like to be a Pirate?")) {
            sm.warp(1020500); // goPirate
        } else {
            sm.sayNext("If you wish to experience what it's like to be a Pirate, come see me again.");
        }
    }

    @Script("infoAttack")
    public static void infoAttack(ScriptManager sm) {
        // Maple Road : Inside the Small Forest (40000)
        //   tuto00 (88, 194)
        sm.avatarOriented("UI/tutorial.img/20");
    }

    @Script("infoPickup")
    public static void infoPickup(ScriptManager sm) {
        // Maple Road : Inside the Small Forest (40000)
        //   tuto01 (657, 133)
        sm.avatarOriented("UI/tutorial.img/21");
    }

    @Script("infoReactor")
    public static void infoReactor(ScriptManager sm) {
        // Rainbow Street : Amherst (1000000)
        //   tuto00 (567, 228)
        //   tuto01 (202, 226)
        sm.avatarOriented("UI/tutorial.img/22");
    }

    @Script("infoSkill")
    public static void infoSkill(ScriptManager sm) {
        // Maple Road : Inside the Small Forest (40000)
        //   tuto02 (824, 133)
        sm.avatarOriented("UI/tutorial.img/23");
    }

    @Script("infoMinimap")
    public static void infoMinimap(ScriptManager sm) {
        // Maple Road : Mushroom Park (10000)
        //   tuto01 (280, 455)
        sm.avatarOriented("UI/tutorial.img/25");
    }

    @Script("infoWorldmap")
    public static void infoWorldmap(ScriptManager sm) {
        // Maple Road : Inside the Dangerous Forest (50000)
        //   tuto00 (1228, 244)
        sm.avatarOriented("UI/tutorial.img/26");
    }

    @Script("glTutoMsg0")
    public static void glTutoMsg0(ScriptManager sm) {
        // Maple Road : Mushroom Park (10000)
        //   glBmsg0 (987, 430)
        //   glBmsg1 (1164, 431)
        // Maple Road : Snail Park (20000)
        //   glBmsg0 (543, 168)
        // Maple Road : Snail Garden (30000)
        //   glBmsg0 (915, 53)
        sm.balloonMsg("Once you leave this area you won't be able to return.", 150, 5);
    }

    @Script("entertraining")
    public static void entertraining(ScriptManager sm) {
        // Maple Road : Entrance to Adventurer Training Center (1010000)
        //   in00 (74, 154)
        if (sm.hasQuestStarted(1041)) {
            sm.playPortalSE();
            sm.warp(1010100, "out00");
        } else if (sm.hasQuestStarted(1042)) {
            sm.playPortalSE();
            sm.warp(1010200, "out00");
        } else if (sm.hasQuestStarted(1043)) {
            sm.playPortalSE();
            sm.warp(1010300, "out00");
        } else if (sm.hasQuestStarted(1044)) {
            sm.playPortalSE();
            sm.warp(1010400, "out00");
        } else {
            sm.message("Only the adventurers that have been trained by Mai may enter.");
        }
    }

    @Script("go10000")
    public static void go10000(ScriptManager sm) {
        // Maple Road : Mushroom Park (10000)
        sm.screenEffect("maplemap/enter/10000");
    }

    @Script("go20000")
    public static void go20000(ScriptManager sm) {
        // Maple Road : Snail Park (20000)
        sm.screenEffect("maplemap/enter/20000");
    }

    @Script("go30000")
    public static void go30000(ScriptManager sm) {
        // Maple Road : Snail Garden (30000)
        sm.screenEffect("maplemap/enter/30000");
    }

    @Script("go40000")
    public static void go40000(ScriptManager sm) {
        // Maple Road : Inside the Small Forest (40000)
        sm.screenEffect("maplemap/enter/40000");
    }

    @Script("go50000")
    public static void go50000(ScriptManager sm) {
        // Maple Road : Inside the Dangerous Forest (50000)
        sm.screenEffect("maplemap/enter/50000");
    }

    @Script("go1000000")
    public static void go1000000(ScriptManager sm) {
        // Rainbow Street : Amherst (1000000)
        sm.screenEffect("maplemap/enter/1000000");
    }

    @Script("go1010000")
    public static void go1010000(ScriptManager sm) {
        // Maple Road : Entrance to Adventurer Training Center (1010000)
        sm.screenEffect("maplemap/enter/1010000");
    }

    @Script("go1010100")
    public static void go1010100(ScriptManager sm) {
        // Maple Road : Adventurer Training Center 1 (1010100)
        sm.screenEffect("maplemap/enter/1010100");
    }

    @Script("go1010200")
    public static void go1010200(ScriptManager sm) {
        // Maple Road : Adventurer Training Center 2 (1010200)
        sm.screenEffect("maplemap/enter/1010200");
    }

    @Script("go1010300")
    public static void go1010300(ScriptManager sm) {
        // Maple Road : Adventurer Training Center 3 (1010300)
        sm.screenEffect("maplemap/enter/1010300");
    }

    @Script("go1010400")
    public static void go1010400(ScriptManager sm) {
        // Maple Road : Adventurer Training Center 4 (1010400)
        sm.screenEffect("maplemap/enter/1010400");
    }

    @Script("go1020000")
    public static void go1020000(ScriptManager sm) {
        // Maple Road : Split Road of Destiny (1020000)
        sm.screenEffect("maplemap/enter/1020000");
    }

    @Script("go2000000")
    public static void go2000000(ScriptManager sm) {
        // Maple Road : Southperry (2000000)
        sm.screenEffect("maplemap/enter/2000000");
    }

    @Script("goAdventure")
    public static void goAdventure(ScriptManager sm) {
        // Maple Road : Entrance - Mushroom Town Training Camp (0)
        sm.setDirectionMode(true, 0);
        sm.squibEffect("Effect/Direction3.img/goAdventure/Scene" + sm.getGender());
        sm.setDirectionMode(false, 14200);
    }

    @Script("goSwordman")
    public static void goSwordman(ScriptManager sm) {
        // null (1020100)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction3.img/swordman/Scene" + sm.getGender());
        sm.setDirectionMode(false, 3000);
    }

    @Script("goMagician")
    public static void goMagician(ScriptManager sm) {
        // null (1020200)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction3.img/magician/Scene" + sm.getGender());
        sm.setDirectionMode(false, 4000);
    }

    @Script("goArcher")
    public static void goArcher(ScriptManager sm) {
        // null (1020300)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction3.img/archer/Scene" + sm.getGender());
        sm.setDirectionMode(false, 3000);
    }

    @Script("goRogue")
    public static void goRogue(ScriptManager sm) {
        // null (1020400)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction3.img/rogue/Scene" + sm.getGender());
        sm.setDirectionMode(false, 3000);
    }

    @Script("goPirate")
    public static void goPirate(ScriptManager sm) {
        // null (1020500)
        sm.setDirectionMode(true, 0);
        sm.reservedEffect("Effect/Direction3.img/pirate/Scene" + sm.getGender());
        sm.setDirectionMode(false, 4000);
    }

    @Script("goLith")
    public static void goLith(ScriptManager sm) {
        // null (2010000)
        sm.setDirectionMode(true, 0);
        sm.squibEffect("Effect/Direction3.img/goLith/Scene" + sm.getGender());
        sm.setDirectionMode(false, 5000);
    }

    @Script("mBoxItem0")
    public static void mBoxItem0(ScriptManager sm) {
        // mBoxItem0 (2000)
        //   Orbis : Top of the Hill (200000300)
        // mBoxItem0 (2001)
        //   Rainbow Street : Amherst (1000000)
        //   Rainbow Street : Amherst Townstreet (1000002)
        // mBoxItem0 (9008000)
        // mBoxItem0 (9008001)
        sm.dropRewards(List.of(
                Reward.money(10, 10, 0.7),
                Reward.item(2000000, 1, 1, 0.1),
                Reward.item(2000001, 1, 1, 0.1),
                Reward.item(2010000, 1, 1, 0.1),
                Reward.item(4031161, 1, 1, 1.0, 1008), // Rusty Screw
                Reward.item(4031162, 1, 1, 1.0, 1008) // Old Wooden Board
        ));
    }

    @Script("q1021s")
    public static void q1021s(ScriptManager sm) {
        // Roger's Apple (1021 - start)
        final User user = sm.getUser();
        sm.sayNext("Hey, " + (user.getGender() == 0 ? "Man" : "Miss") + "~ What's up? Haha! I am Roger who teaches you new travellers with lots of information.");
        sm.sayBoth("You are asking who made me do this? Ahahahaha! Myself! I wanted to do this and just be kind to you new travellers.");
        if (!sm.askAccept("So..... Let me just do this for fun! Abaracadabra~!")) {
            return;
        }
        user.setHp(25);
        sm.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you  #rRoger's Apple#k. Please take it. You will feel stronger. Open the item window and double click to consume. Hey, It's very simple to open the item window. Just press #bI#k on your keyboard.");
        sm.sayBoth("Please take all Roger's Apples that I gave you. You will be able to see the HP bar increasing right away. Please talk to me again when you recover your HP 100%.");
        if (!sm.hasItem(2010007) && !sm.addItem(2010007, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(1021);
        sm.avatarOriented("UI/tutorial.img/28");
    }

    @Script("q1021e")
    public static void q1021e(ScriptManager sm) {
        // Roger's Apple (1021 - end)
        final User user = sm.getUser();
        if (user.getHp() < user.getMaxHp()) {
            sm.sayNext("Hey, your HP is not fully recovered yet. Did you take all the Roger's Apple that I gave you? Are you sure?");
            return;
        }
        sm.sayNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.");
        sm.sayBoth("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!");
        sm.sayBoth("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care of yourself and Good luck my friend!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i2010000# 3 #t2010000#\r\n#i2010009# 3 #t2010009#\r\n\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 10 exp");
        if (!sm.addItems(List.of(
                Tuple.of(2010000, 3), // Apple
                Tuple.of(2010009, 3) // Green Apple
        ))) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.addExp(10);
        sm.forceCompleteQuest(1021);
    }
}
