package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.Map;

/**
 * Ludibrium Region Quests
 * Area 37 - Ludibrium, Eos Tower, Toy Factory, Clocktower
 */
public final class LudibriumQuest extends ScriptHandler {
    // Item constants
    public static final int SOUL_TEDDY_SPIRIT = 4000144;
    public static final int TIMER_BABY_BIRD = 4220046;
    public static final int SPRINGY_WORM = 4000460;
    public static final int BLUEPRINT_MACHINE = 4031100;
    public static final int WAVE_TRANSLATOR = 4031927;
    public static final int SACK_OF_RICE = 4031229;
    public static final int SACK_OF_RICE_2 = 4031246;
    public static final int SPACE_FOOD = 4000117;
    public static final int LASER_GUN = 4031101;
    public static final int WORN_OUT_GOGGLE = 4000103;
    public static final int PROPELLER = 4000123;
    public static final int EOS_ROCK_SCROLL = 4001020;
    public static final int LUNCHBOX = 2020021;
    public static final int RATZ_CHEESE = 4031129;
    public static final int SACK_OF_RICE_3 = 4031247;
    public static final int BIRK_EGG = 4000116;
    public static final int RAT_TRAP = 4000095;
    public static final int SACK_OF_RICE_4 = 4031248;
    public static final int BROTHERLY_LOVE_LETTER = 4031237;
    public static final int LOST_SEED = 4031241;
    public static final int SWALLOW_SEED = 4031225;
    public static final int NOLBU_GOURD_SEED = 4031245;
    public static final int NOLBU_GOURD = 4031224;
    public static final int HONGBU_GOURD_SEED = 4031244;
    public static final int HONGBU_GOURD = 4031223;
    public static final int GOURD_TREASURE = 4031235;

    // ================================
    // Quest 3445 - Free Spirit
    // ================================

    @Script("q3445s")
    public static void q3445s(ScriptManager sm) {
        // Free Spirit (3445 - start)
        // NPC 2041026 - Ghosthunter Bob (Ludibrium - Path of Time)
        // Level 62+ requirement
        sm.sayNext("Who am I? I'm Bob, Ghosthunter Bob. Have you seen any monsters that roam around this area?");
        sm.sayBoth("You might have noticed it, but... the Teddys are being controlled by the force of evil. Looking at the Teddys, knowing that their souls are being controlled by someone else...");
        sm.sayBoth("That's right. I really want to free up their souls from that force of evil, you know. So... can you help me out by killing those monsters, and gather up their souls for me?");

        if (!sm.askYesNo("Will you help free the souls of Master Soul Teddy?")) {
            sm.sayOk("What? I don't think it's a difficult task, though... please reconsider.");
            return;
        }

        sm.sayNext("The monster you're dealing with is Master Soul Teddy, and you'll notice that a huge evil ghost hovers around it, controlling its every move.");
        sm.sayBoth("Go down below and fight the monster, and you may be able to gather up the freed-up souls of Master Soul Teddy. Bring those souls to me, and I'll do what I can for them to regain their freedom.");
        sm.sayBoth("Oh, and I'd sure appreciate it if you can collect 80 of their souls. It's not gonna be too hard for you, is it? Thanks~~");

        sm.forceStartQuest(3445);
    }

    @Script("q3445e")
    public static void q3445e(ScriptManager sm) {
        // Free Spirit (3445 - end)
        // NPC 2041026 - Ghosthunter Bob
        // Requires: 80x Soul Teddy's Spirit (4000144)
        if (!sm.hasItem(SOUL_TEDDY_SPIRIT, 80)) {
            sm.sayOk("Hmmm ... the numbers don't match. I don't think you brought the number I was asking for.");
            return;
        }

        sm.sayNext("What, you freed up their souls? All of them, like you promised?");

        if (!sm.askYesNo("Turn in 80 Soul Teddy's Spirits?")) {
            sm.sayOk("Come back when you have all 80 souls.");
            return;
        }

        sm.sayOk("Yeah, those souls may be sent to a much nicer place now, and may they rest in peace. I'm sure they are thankful of your good deeds.");

        sm.removeItem(SOUL_TEDDY_SPIRIT, 80);
        sm.addExp(35000);
        sm.forceCompleteQuest(3445);
    }

    // ================================
    // Quest 3250 - Raise the Timer
    // ================================

    @Script("q3250s")
    public static void q3250s(ScriptManager sm) {
        // Raise the Timer (3250 - start)
        // NPC at Clocktower - Forgotten Path of Time <1> (220070000)
        if (sm.hasItem(TIMER_BABY_BIRD)) {
            sm.sayOk("Oh what...? You still have the Timer from long ago. You want another Timer when you can't even take care of the one you have? At least return the one you have before you take on a new one.");
            return;
        }

        sm.sayNext("Wow! I fed this one...did I feed this one? Hmmm...I don't think I've fed the third one yet! Here, have some food. Hehe, it fills my appetite to just see them eat... Oh, are you here?");
        sm.sayBoth("Sigh... I've been really busy, ever since that Timer changed due to the Papulatus's influence. Proper nurturing can rear natural birds. I cleared out the monsters... so, since I have the time to spare now, I decided to rear a bird myself.");

        if (!sm.askYesNo("But it's so much work to raise not just one, but ten birds at once. Not to mention how hard it is to find food to feed all of them. But they're very adorable. Would you like to raise one?")) {
            sm.sayOk("Hmmm...I guess you're not a huge fan of animals. But they're so cute!");
            return;
        }

        if (!sm.canAddItem(TIMER_BABY_BIRD, 1)) {
            sm.sayOk("It seems like you don't have enough room for a baby bird...");
            return;
        }

        sm.forceStartQuest(3250);
        sm.addItem(TIMER_BABY_BIRD, 1);
        sm.sayNext("Here, I'll give you one. Please take good care of it. You can feed them Springy Worms that drop from monsters at the Clocktower.");
        sm.sayBoth("Timers must be returned to their original habitat when full-fledged, so bring the Timer back to me when it's full-grown. I'll be counting on you.");
    }

    @Script("q3250e")
    public static void q3250e(ScriptManager sm) {
        // Raise the Timer (3250 - end)
        // Return the fully grown Timer
        sm.sayNext("So, how's it feel to be raising the Timer?");

        if (!sm.hasItem(TIMER_BABY_BIRD)) {
            sm.sayOk("Where's the Timer I gave you? You need to bring it back when it's fully grown!");
            return;
        }

        sm.sayBoth("What? The Timer's already fully grown? Woah... Seems like you've been feeding the Springy Worms a whole bunch... Well then, I'll need that Timer back now. It's time for us to return it to its world...");
        sm.sayBoth("I hate to see them go, but they don't belong here...it's for their good.");

        sm.removeItem(TIMER_BABY_BIRD, 1);
        sm.addExp(80000);
        sm.forceCompleteQuest(3250);
    }

    // ================================
    // Quest 3200 - Cleaning up Eos Tower
    // ================================

    @Script("q3200s")
    public static void q3200s(ScriptManager sm) {
        // Cleaning up Eos Tower (3200 - start)
        // NPC 2041004 - Marcel (Eos Tower 101st Floor)
        sm.sayNext("Hmmm ... you look like someone that likes adventure. Can I ask you for a few favors? It's not going to be easy, but if you get the job done, I'll reward you well for it!");

        if (!sm.askYesNo("Are you willing to help clean up Eos Tower?")) {
            sm.sayOk("Really. This task isn't that difficult, and I think you can do it. If you have any free time, then come talk to me.");
            return;
        }

        sm.sayNext("Cool! What I am going to ask from you is simple. Ludibrium is a floating castle supported by two huge towers, so to get down to the ground level, you'll need to go through Eos Tower, a tower of mind-numbing heights.");
        sm.sayBoth("It's a very important tower, but the cleaning hasn't been done lately, and the tower is now infiltrated with filthy monsters. I can't move from my duties here as a guard, so I was hoping if you can go in the tower and take care of some monsters.");
        sm.sayBoth("Please take care of 50 #o3110102#s that are roaming around in Eos Tower. They are a bunch of white rats with springs on the back, and they are a handful. I'll be here waiting until you take of all of them.");

        sm.forceStartQuest(3200);
    }

    @Script("q3200e")
    public static void q3200e(ScriptManager sm) {
        // Cleaning up Eos Tower (3200 - end)
        // NPC 2041004 - Marcel
        sm.sayNext("Awesome! You took out all of them! There must have been quite a few #o3110102#s roaming around Eos Tower. Anyway, thank you for helping me out. I'll give you an item only available in Ludibrium that you'll need to use for hunting. Please take it!");

        if (!sm.askYesNo("Accept the rewards?")) {
            return;
        }

        sm.sayOk("Do you like the 50 #t2000008#s and 50 #t2000010#s? Thank you so much for your help, but it seems like Eos Tower is still full of nasty monsters. I have a lot to ask of you, so if you have any free time, please come talk to me.");

        if (sm.canAddItem(2000008, 50) && sm.canAddItem(2000010, 50)) {
            sm.addItem(2000008, 50); // Orange Potion
            sm.addItem(2000010, 50); // White Potion
            sm.addExp(1250);
            sm.forceCompleteQuest(3200);
        } else {
            sm.sayOk("Please make sure you have enough inventory space.");
        }
    }

    // ================================
    // Quest 3401 - Dr. Kim's Comments: Blueprint for New Robot
    // ================================

    @Script("q3401s")
    public static void q3401s(ScriptManager sm) {
        // Dr. Kim's Comments: Blueprint for New Robot (3401 - start)
        // NPC 2050001 - Dr. Kim (Omega Sector Command Center)
        sm.sayNext("Hmmm ... done! The #t" + BLUEPRINT_MACHINE + "#, which contains the blueprint of the new robot, is now COMPLETE! Hahaha!! Once the making of the robot is complete, straight out of this blueprint, the aliens outside the sector will be of no factor whatsoever! Yes... hey you! You look like you don't have much to do. Can you do me a favor?");

        if (!sm.askYesNo("Will you help Dr. Kim show the blueprint to Chury, Hoony, and Gunny?")) {
            sm.sayOk("Did you feel irked when I said you look like you don't have much to do? I know a whole bunch of people just like you... hmmm... anyway, if you change your mind, talk to me. I am well aware that you really don't have much to do right now.");
            return;
        }

        if (!sm.canAddItem(BLUEPRINT_MACHINE, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Thank you! I'll give you this #t" + BLUEPRINT_MACHINE + "#. Your job is to show this to #p2050005#, #p2050006#, and #p2050007#. They should be all over Omega Sector. The first person you'll need to meet is #p2050005#. He should be resting somewhere around the Silo.");

        sm.addItem(BLUEPRINT_MACHINE, 1);
        sm.forceStartQuest(3401);
    }

    @Script("q3401e")
    public static void q3401e(ScriptManager sm) {
        // Dr. Kim's Comments: Blueprint for New Robot (3401 - end)
        // NPC 2050005 - Chury (Omega Sector Silo)
        if (!sm.hasItem(BLUEPRINT_MACHINE)) {
            sm.sayOk("Hmmm... are you saying that #p2050001# has completed the #t" + BLUEPRINT_MACHINE + "# of the new robot? But where is the #t" + BLUEPRINT_MACHINE + "#? Maybe you lost it on the way here... if so, then please go back to #p2050001#. He put a security device on that baby just in case something like this happened.");
            return;
        }

        sm.sayNext("Hoh... so this is #t" + BLUEPRINT_MACHINE + "#, the blueprint for the new robot that #p2050001# had been diligently working on for the past few months. Hmmm... so when I put my eyes right there, I can see the contents inside, just for a security measure. Amazing, just the kind of stuff that he would make. Okay, now let's see what's inside this baby...");

        sm.removeItem(BLUEPRINT_MACHINE, 1);
        sm.addExp(2400);
        sm.forceCompleteQuest(3401);
    }

    // ================================
    // Quest 3455 - The Endangered Lives of Grays
    // ================================

    @Script("q3455s")
    public static void q3455s(ScriptManager sm) {
        // The Endangered Lives of Grays (3455 - start)
        // NPC 2050002 - Alien Gray (Omega Sector)
        // This quest requires the Wave Translator from quest 3457

        int response = sm.askMenu("You... you're back. My human friend, it's nice to see you again but I really don't have time to chat. Perhaps next time?",
                Map.of(0, "#b(Activate the Wave Translator.)#k"));

        if (response != 0) {
            return;
        }

        sm.sayNext("You can ask about Zeno all you want but I don't have anything to say. The seniors are in charge of Zeno, so I don't know anything.(Oh no, my palate is getting accustomed to human food. Everything about me is becoming more and more human.)");

        sm.askMenu("",
                Map.of(0, "#b(Wave Translator activated)#k"));

        sm.sayNext("More importantly, what happened to you, my human friend? You're not cooperating with the hypocrites in the Omega Sector, are you?(Even my standards for attractiveness is becoming human too. Agent Marco is probably the most handsome human ever.)");

        sm.askMenu("",
                Map.of(0, "#b(Wave Translator activated)#k"));

        sm.sayNext("The Grays do not want to hurt the humans. We want to get closer. Don't you know? (No, forget it! If Prince finds out, he'll be furious!)");

        sm.askMenu("",
                Map.of(0, "#b(Wave Translator activated)#k"));

        sm.sayNext("Don't be afraid of our guidance. Grays are on humans' side. We will lead the humans to their glory.(Let's think of more practical things... like washing my chute and drying food... I'm going to get alien's eczema soon.)");

        sm.askMenu("",
                Map.of(0, "#b(Wave Translator activated)#k"));

        if (!sm.askYesNo("So... would you like to cooperate with us now?")) {
            sm.sayOk("Too bad. Well, I guess I'll see you around.");
            return;
        }

        sm.sayNext("You must have decided to side with the Grays! Here, sign this contract to pledge your loyalty!");
        sm.forceStartQuest(3455);
    }

    @Script("q3455e")
    public static void q3455e(ScriptManager sm) {
        // The Endangered Lives of Grays (3455 - end)
        // NPC 2050002 - Alien Gray
        sm.sayNext("Seems like you've made up your mind. Haha! Are you truly THAT happy to have signed your life away to the Grays? ... What? Am I joking around?");
        sm.sayBoth("What, what are you talking about... You seem so serious... Hmmm... I guess I don't have what it takes to read human facial expressions yet...");

        if (!sm.askYesNo("Complete the quest?")) {
            return;
        }

        sm.sayOk("#b(Alien Gray has nothing more to say... It seems you've collected a bunch of useless information... The Wave Translator must have malfunctioned. Let's ask Dr. Kim to build another Wave Translator.)#k");

        sm.removeItem(WAVE_TRANSLATOR, 1);
        sm.forceCompleteQuest(3455);
    }

    // ================================
    // Quest 3601 - The Brothers' Stack of Rice 1
    // ================================

    @Script("q3601s")
    public static void q3601s(ScriptManager sm) {
        // The Brothers' Stack of Rice 1 (3601 - start)
        // NPC 2071005 - Chil Sung (Korean Folk Town)
        sm.sayNext("Hello there, you must be new to this town; you don't look familiar. I live here in town with my younger brother, farming for a living. We harvested our crops a few days ago, but there's something that concerns me right now. I have split the Sack of Rice equally between my brother and me, but my brother raises more kids, and...");
        sm.sayBoth("I'd much prefer giving my brother a little more of #t" + SACK_OF_RICE + "#, but I know he won't accept it. That's why ... can you please secretly put one #t" + SACK_OF_RICE + "# on top of the stack of rice in front of my brother's house? My brother Chil Nam lives at the very east of this town.");

        if (!sm.askYesNo("Will you help Chil Sung secretly deliver the sack of rice?")) {
            sm.sayOk("I'm sorry. I shouldn't have asked for such a huge favor to a stranger. I apologize for my mishap.");
            return;
        }

        if (!sm.canAddItem(SACK_OF_RICE, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("I urge you, please make sure my brother doesn't know about this~");

        sm.addItem(SACK_OF_RICE, 1);
        sm.forceStartQuest(3601);
    }

    @Script("q3601e")
    public static void q3601e(ScriptManager sm) {
        // The Brothers' Stack of Rice 1 (3601 - end)
        // NPC 2072001 - Rice Stack (Korean Folk Town)
        if (!sm.hasItem(SACK_OF_RICE)) {
            sm.sayOk("This is Chil Nam's stack of rice.");
            return;
        }

        sm.sayOk("I see a stack of rice stacked at the garden of Chil Nam's house. Secretly, I brought out a sack of rice and laid it unnoticeably on top of the stack.");

        sm.removeItem(SACK_OF_RICE, 1);
        sm.addExp(1000);
        sm.forceCompleteQuest(3601);
    }

    // ================================
    // Marcel's Eos Tower Cleaning Quest Chain
    // ================================

    @Script("q3201s")
    public static void q3201s(ScriptManager sm) {
        // Cleaning Up the Inner Parts of Eos Tower (3201 - start)
        sm.sayNext("Wow, great timing! I actually have something else to ask you for a little help on. A few days ago, one of the guys from us reported that Eos Tower is now full of even nastier monsters than last time, and so I thought of you as the enforcer ... so can you do it instead of me??");

        if (!sm.askYesNo("Will you help clean up the inner parts of Eos Tower?")) {
            sm.sayOk("Really. This task isn't that difficult, and I think you can do it. If you have any free time, then come talk to me.");
            return;
        }

        sm.sayNext("Alright. It's similar to others last time. The tower that connects Ludibrium and ground, Eos Tower, is now infected with dirty spiders and black rats. So please go in and take care of some of them.");
        sm.sayBoth("Please take care of 40 #o3210205#s and 40 #o2230103#s that are roaming around in Eos Tower. They are a bunch of huge black rats with springs on the back, and spiders making spider webs on the wall. I'll be here waiting until you take of all of them.");

        sm.forceStartQuest(3201);
    }

    @Script("q3201e")
    public static void q3201e(ScriptManager sm) {
        // Cleaning Up the Inner Parts of Eos Tower (3201 - end)
        sm.sayNext("Awesome! You took them all out again! There must have been quite a few #o2230103#s and #o3210205#s around Eos Tower. Anyway, thank you for helping me out. I'll give you an item that you'll need to use for hunting. Please take it!");

        if (!sm.askYesNo("Accept the rewards?")) {
            return;
        }

        sm.sayOk("Here they are, 50 #t2000009#s and 50 #t2000011#s. Thank you so much for your help, but it seems like Eos Tower is still full of nasty monsters. I have a lot to ask of you, so if you have any free time, please come talk to me.");

        if (sm.canAddItem(2000009, 50) && sm.canAddItem(2000011, 50)) {
            sm.addItem(2000009, 50); // Blue Potion
            sm.addItem(2000011, 50); // Red Potion
            sm.addExp(2500);
            sm.forceCompleteQuest(3201);
        } else {
            sm.sayOk("Please make sure you have enough inventory space.");
        }
    }

    @Script("q3202s")
    public static void q3202s(ScriptManager sm) {
        // Cleaning Up the Outer Parts of Eos Tower (3202 - start)
        sm.sayNext("Hey, you're back! Actually, I have another favor to ask you. This time, I've heard reports that the menacing toy monsters have been appearing around the outer walls of Eos Tower, and I'd really like for you to defeat them for me. Will you help me out?");

        if (!sm.askYesNo("Will you help clean the outer walls?")) {
            sm.sayOk("Really. This task isn't that difficult, and I think you can do it. If you have any free time, then come talk to me.");
            return;
        }

        sm.sayNext("Okay! It's similar to what I requested before. There have been reports that some of the toys made from the Toy Factory, located at the bottom of the Ludibrium Clocktower, have been attacking anyone walking around the outer walls of Eos Tower. I would like for you to go back into the tower and take care of them.");
        sm.sayBoth("Please take out 25 #o3230303#s and 25 #o3230308#s. Both of them can be found at the outer wall of Eos Tower. One resemble a toy pink plane, while the other looks like a pink bird. I'll be waiting for you here.");

        sm.forceStartQuest(3202);
    }

    @Script("q3202e")
    public static void q3202e(ScriptManager sm) {
        // Cleaning Up the Outer Parts of Eos Tower (3202 - end)
        sm.sayNext("Awesome! You took them all out again! There must have been quite a few #o3230303#s and #o3230308#s around Eos Tower. Anyway, thank you for helping me out. I'll give you an item that you'll need to use for hunting. Please take it!");

        if (!sm.askYesNo("Accept the rewards?")) {
            return;
        }

        sm.sayOk("Here they are, 50 #t2000004#s. Thank you so much for your help, but it seems like Eos Tower is still full of nasty, evil monsters. I have a lot to ask of you, so if you have any free time, please come talk to me.");

        if (sm.canAddItem(2000004, 50)) {
            sm.addItem(2000004, 50); // Elixir
            sm.addExp(4000);
            sm.forceCompleteQuest(3202);
        } else {
            sm.sayOk("Please make sure you have enough inventory space.");
        }
    }

    @Script("q3203s")
    public static void q3203s(ScriptManager sm) {
        // Eos Tower Threatened! (3203 - start)
        sm.sayNext("Oh, hello! I've been waiting for you, actually, because something serious has occurred here. A group of unruly monsters have come to threaten and actually destroy Eos Tower and Ludibrium. I was wondering if you can help us out here ...");

        if (!sm.askYesNo("Will you help protect Eos Tower?")) {
            sm.sayOk("Really. This task isn't that difficult, then come talk to me.");
            return;
        }

        sm.sayNext("That's good! There were some leftover blocks while building Eos Tower, and those blocks were exposed to dark forces, reinventing themselves as Golems. Those Golems are nasty, unruly, and most definitely destructive. I know it is going to be dangerous, but I really hope you can take some of them out from Eos Tower for us.");
        sm.sayBoth("Please take out 20 #o4230109#s and 12 #o4230110#s. They are Golems made in toy blocks, and they are much more powerful than any you may have faced before. I'll be here wishing you a good luck.");

        sm.forceStartQuest(3203);
    }

    @Script("q3203e")
    public static void q3203e(ScriptManager sm) {
        // Eos Tower Threatened! (3203 - end)
        sm.sayNext("I can't believe you took care of all those unruly Golems! How was it? They didn't destroy Eos Tower to the point of no return, did they? Anyway, I can't thank you enough for this. I'll give you this item that's essential on hunting, so please accept it!");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        sm.sayOk("Did you like the reward? Thank you so much for your effort. Eos Tower will be safer than it was before you came here, but what is this weird feeling ... I don't know, but just to make sure, if you have any time available, please drop by.");

        // Random scroll reward (one of many weapon scrolls)
        sm.addExp(7000);
        sm.forceCompleteQuest(3203);
    }

    @Script("q3204s")
    public static void q3204s(ScriptManager sm) {
        // Peace at Eos Tower (3204 - start)
        sm.sayNext("Oh wow! Thank goodness you're here, because I've been waiting for you. First of all, I'd like to personally thank you for helping us protect Eos Tower from destruction. The problem is, unless we find the root of the problem, Eos Tower will be put to test again. I'd appreciate it if you find a way to take out this boss-life character in this game.");

        if (!sm.askYesNo("Will you defeat the leader of the Block Golems?")) {
            sm.sayOk("Really? I understand, since this is much tougher than any of the other things people asked for, but if you have any time, then please come back and talk to me.");
            return;
        }

        sm.sayNext("Thank you! As luck would have it, a few days ago, one of the guys here spotted what appeared to be a head figure. The way I see it, as long as it is here, Eos Tower will suffer the consequences. I would like for you to find it and kill it for me...");
        sm.sayBoth("The monster you're facing is the leader of the Block Golems, #o4130103#. Astonishingly powerful, it is considered incomparable to other Golems. I strongly suggest you take on it with your party or guild. I'll be here waiting for you.");

        sm.forceStartQuest(3204);
    }

    @Script("q3204e")
    public static void q3204e(ScriptManager sm) {
        // Peace at Eos Tower (3204 - end)
        sm.sayNext("Unbelievable ... you really took care of #o4130103#? That's just un-be-lievable. You have done so much for me and other residents of Ludibrium, that I'll have to give you a handsome reward. The item I'm giving you shall help you out while hunting, so please accept it.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        sm.sayOk("Thank you so much for helping. It seems like Eos Tower AND Ludibrium are now in peace. I am so glad I ran into you in dire times like this. Thank you for all your hard work. Please drop by often!");

        sm.addExp(10500);
        sm.forceCompleteQuest(3204);
    }

    @Script("q3205s")
    public static void q3205s(ScriptManager sm) {
        // The Lost Guard (3205 - start)
        sm.sayNext("Hmmm ... where am I?? I'm in charge of security for the 59th floor of Eos Tower, but I have nooo idea where I am at right this minute. Dang ... this place is also full of monsters, which makes it impossible for me to do my duty here. Can you help me out, if it's okay with you?");

        if (!sm.askYesNo("Will you help the lost guard?")) {
            return;
        }

        sm.sayNext("Alright! It's a simple request, actually. Please take out the monsters that are on my way to the 59th floor of Eos Tower, and hand me the leftovers as a proof. It'll be much easier for me to go to the 59th floor if the numbers of the monsters decrease on my way there.");
        sm.sayBoth("The ones you'll need to take down are #o3230307# and #o3210206#. Take those down and hand me 15 #t4000123#s and 15 #t4000103#s. I'll be here waiting for you, my friend. Good luck!");

        sm.forceStartQuest(3205);
    }

    @Script("q3205e")
    public static void q3205e(ScriptManager sm) {
        // The Lost Guard (3205 - end)
        if (!sm.hasItem(PROPELLER, 15) || !sm.hasItem(WORN_OUT_GOGGLE, 15)) {
            sm.sayOk("I don't think you have collected all the items I asked of you. Please take down the monsters within Eos Tower and collect 15 #t4000123#s and 15 #t4000103#s for me. I can't get to the 59th floor where I am in charge of the security because of those darn monsters. Please help me!");
            return;
        }

        sm.sayNext("Wow ... you really took them all down. I knew I had an eye for talent. Anyway, thank you for your hard work. As a sign of appreciation, I'll give you #t4001020#s, something I've cherished for a long time. I'll tell you how to use it after you receive it.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        if (!sm.canAddItem(EOS_ROCK_SCROLL, 20)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("The #t4001020# that I gave you is an item that is very essential in activating the four stones within Eos Tower. It'll allow you to use #p2040024# at the 100th floor, #p2040025# at the 71st floor, #p2040026# at the 41st floor, and #p2040027# at the 1st floor. Use those rocks to teleport to other rocks. Please drop by again ~");

        sm.removeItem(PROPELLER, 15);
        sm.removeItem(WORN_OUT_GOGGLE, 15);
        sm.addItem(EOS_ROCK_SCROLL, 20);
        sm.forceCompleteQuest(3205);
    }

    // ================================
    // Dr. Kim's Blueprint Quest Chain
    // ================================

    @Script("q3402s")
    public static void q3402s(ScriptManager sm) {
        // Dr. Kim's Comments: A Meeting with Chury (3402 - start)
        sm.sayNext("Ohhh... this is just incredible! I can't believe he drew up a robot as technologically advanced as this! I knew he was incredible to begin with, but... wow!!! He's much more than I thought! Anyway, please take this #t" + BLUEPRINT_MACHINE + "# and show it to #p2050006#, who should be at a field around Omega Sector. I bet you he'll be just as astounded looking at it as I am right now.");
        sm.sayBoth("Oh, and... #p2050006# ... he's been out on a mission for a while now and I'm pretty sure he's run out of food as we speak. Please gather up 20 #t" + SPACE_FOOD + "#s, which can be found through #o4230116# the alien, and give them to him. Good luck!");

        sm.forceStartQuest(3402);
    }

    @Script("q3402e")
    public static void q3402e(ScriptManager sm) {
        // Dr. Kim's Comments: A Meeting with Chury (3402 - end)
        if (!sm.hasItem(SPACE_FOOD, 20)) {
            sm.sayOk("Hmmm... are you sure you have 20 #t" + SPACE_FOOD + "#s? Or is your etc. inventory full by any chance? Please check your item inventory one more time.");
            return;
        }

        sm.sayNext("Hoh... #t" + SPACE_FOOD + "#!!! Great timing, because I've just ran out of food, and I'm still in the middle of a mission. Can you give me the #t" + SPACE_FOOD + "#s? I'll need to fill my belly up first before I check out the #t" + BLUEPRINT_MACHINE + "# or anything else for that matter.");

        sm.removeItem(SPACE_FOOD, 20);
        sm.addExp(2700);
        sm.forceCompleteQuest(3402);
    }

    @Script("q3403s")
    public static void q3403s(ScriptManager sm) {
        // Dr. Kim's Comments: A Meeting with Hoony (3403 - start)
        sm.sayNext("Phew... I feel much better now. But #t" + BLUEPRINT_MACHINE + "# is really amazing! I really want to show #p2050007# this... please find him somewhere around the fields. Oh, and please give him the #t" + LASER_GUN + "# that I just gave you, too. He told me it was broken, so I fixed it for him.");

        if (!sm.canAddItem(LASER_GUN, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.addItem(LASER_GUN, 1);
        sm.forceStartQuest(3403);
    }

    @Script("q3403e")
    public static void q3403e(ScriptManager sm) {
        // Dr. Kim's Comments: A Meeting with Hoony (3403 - end)
        if (!sm.hasItem(LASER_GUN)) {
            sm.sayOk("Well, I was sent here on a mission, and one day, my precious #t" + LASER_GUN + "# broke down on me. I gave it to #p2050006# so he can fix it up quickly, but I'm guessing the fixing has taken longer than expected, and thus, I do not have a weapon to defend myself with. It's quite dangerous around here without a trusty weapon... I hope I can get that really soon...");
            return;
        }

        sm.sayNext("Oh wow, you have my #t" + LASER_GUN + "#! I am guessing you ran into #p2050006# first. Anyway, it's been tough for me to handle all these aliens by myself. Now, let's see if this gun is fixed right.");

        if (!sm.askYesNo("Give Gunny the Laser Gun?")) {
            return;
        }

        if (!sm.canAddItem(2030011, 7)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Whoa... this #t" + LASER_GUN + "# ... it seems like it's actually working better than it did before it broke. And this... this must be the #t" + BLUEPRINT_MACHINE + "#  of the new robot that #p2050001# had been working so hard on. This is incredible!! I truly believe with this robot, the aliens will be much easier to handle.");

        sm.removeItem(LASER_GUN, 1);
        sm.addItem(2030011, 7); // Lemon
        sm.addExp(3000);
        sm.forceCompleteQuest(3403);
    }

    // ================================
    // Brothers' Rice Quest Chain (continued)
    // ================================

    @Script("q3602s")
    public static void q3602s(ScriptManager sm) {
        // The Brothers' Stack of Rice 2 (3602 - start)
        sm.sayNext("Umm, hi there. I don't think we've ever met. Someone told me a person from outside came by here, and I guess that must be you. This is great; I need to ask you for a small favor. My brother and I live here in this town, farming for a living. We harvested our crop a few days ago, but something's been nagging me ever since. I have equally split the #t" + SACK_OF_RICE_2 + "# between my brother and myself, but my brother takes care of my parents, and I seriously believe my brother deserves a little more of #t" + SACK_OF_RICE_2 + "#, but I know he won't accept it. That's why ... can you please secretly put one #t" + SACK_OF_RICE_2 + "# on top of the stack of rice in front of my brother's house?");

        if (!sm.askYesNo("Will you help Chil Nam deliver rice to his brother?")) {
            sm.sayOk("I understand. It's rude for me to ask for such a huge favor to a stranger. I'm sorry.");
            return;
        }

        if (!sm.canAddItem(SACK_OF_RICE_2, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Please, please make sure my brother does NOT find out about this.");

        sm.addItem(SACK_OF_RICE_2, 1);
        sm.forceStartQuest(3602);
    }

    @Script("q3602e")
    public static void q3602e(ScriptManager sm) {
        // The Brothers' Stack of Rice 2 (3602 - end)
        if (!sm.hasItem(SACK_OF_RICE_2)) {
            sm.sayOk("This is Chil Sung's stack of rice.");
            return;
        }

        sm.sayOk("I see a stack of rice stacked at the garden of Chil Sung's house. Secretly, I brought out a sack of rice and laid it unnoticeably on top of the stack.");

        sm.removeItem(SACK_OF_RICE_2, 1);
        sm.addExp(1000);
        sm.forceCompleteQuest(3602);
    }

    @Script("q3603s")
    public static void q3603s(ScriptManager sm) {
        // The Brothers' Stack of Rice 3 (3603 - start)
        sm.sayNext("Did you make sure my brother did not find out about this? Something's weird, though. It seems like my stack of rice didn't shrink one bit. Don't you think so, too? Well, that's why ... can you go back to Chil Nam's house once more?");

        if (!sm.askYesNo("Will you deliver another sack of rice?")) {
            sm.sayOk("I'm sorry. I should have known that I was pushing it by asking you to do this twice...");
            return;
        }

        if (!sm.canAddItem(SACK_OF_RICE_3, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("I urge you, please make sure my brother doesn't know about this~");

        sm.addItem(SACK_OF_RICE_3, 1);
        sm.forceStartQuest(3603);
    }

    @Script("q3603e")
    public static void q3603e(ScriptManager sm) {
        // The Brothers' Stack of Rice 3 (3603 - end)
        if (!sm.hasItem(SACK_OF_RICE_3)) {
            sm.sayOk("This is Chil Nam's stack of rice.");
            return;
        }

        sm.sayOk("I see a stack of rice stacked at the garden of Chil Nam's house. Secretly, I brought out a sack of rice and laid it unnoticeably on top of the stack.");

        sm.removeItem(SACK_OF_RICE_3, 1);
        sm.addExp(1000);
        sm.forceCompleteQuest(3603);
    }

    // ================================
    // Nemi's Quest Chain (Ludibrium)
    // ================================

    @Script("q3206s")
    public static void q3206s(ScriptManager sm) {
        // Nemi's Lunchbox Delivery (3206 - start)
        sm.sayNext("My father may be waiting for me by now. I have so many chores to take care of here ... I have a favor to ask you regarding my father... will you help us out?");

        if (!sm.askYesNo("Will you deliver Nemi's lunchbox to her father?")) {
            sm.sayOk("I see ... you must be swamped with other things yourself. If you ever find some time, then please come talk to me. I can always use some help, you know~");
            return;
        }

        if (!sm.canAddItem(LUNCHBOX, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Thank you so much~ My father is the manager of the Toy Factory inside the Ludibrium Clocktower, and I think he forgot to pack his lunch today. I am hoping you can deliver this to my father for me.");

        sm.addItem(LUNCHBOX, 1);
        sm.forceStartQuest(3206);
    }

    @Script("q3206e")
    public static void q3206e(ScriptManager sm) {
        // Nemi's Lunchbox Delivery (3206 - end)
        if (!sm.hasItem(LUNCHBOX)) {
            sm.sayOk("You must have met my daughter! Didn't she want you to deliver the lunchbox to me? If you lost it in the middle of the track or have eaten it, please go back to her immediately. If she feels good, she may just make you another one.");
            return;
        }

        sm.sayNext("Ohh, this is it! The lunchbox from heaven by none other than my daughter! I can't work without this. Great job bringing this here. Thanks to you, I'll be able to concentrate on my work now. Here are some mesos for you.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        sm.sayOk("Mmmhmmm, this is how it should be! #p2041005#'s the best, bar none! Oh, by the way, I have been really busy these days with work, and I was hoping someone help me out here. If you have any free time down the road, please drop by.");

        sm.removeItem(LUNCHBOX, 1);
        sm.addMoney(4500);
        sm.addExp(750);
        sm.forceCompleteQuest(3206);
    }

    @Script("q3207s")
    public static void q3207s(ScriptManager sm) {
        // Nemi's First Ingredient (3207 - start)
        sm.sayNext("So much work to do at home... oh, hello there? What are you doing here? Oh, oh yeah! If it's all right with you, can you help me out a little? I have to do something for my father, but I have no time to do it with all these chores to take care of.");

        if (!sm.askYesNo("Will you help Nemi gather ingredients?")) {
            sm.sayOk("I see ... you must be swamped with other things yourself. If you ever find some time, then please come talk to me. I can always use some help, you know~");
            return;
        }

        sm.sayNext("Oh, thank you~! Actually it seems like my father has been working really hard lately, with an overwhelming number of new orders for toys. He's been leaving before dawn, and comes home before midnight, so I've decided to make him a special dinner, but I don't have anything to work with right now...");
        sm.sayBoth("Head over to Eos Tower and you'll see a monster called #o3110102#. Take them out and please gather up 10 #t4031129#s. Their cheese is supposed to be top-notch. I feel like I can make the best soup in the world with that chesse, you know? Well, good luck~~");

        sm.forceStartQuest(3207);
    }

    @Script("q3207e")
    public static void q3207e(ScriptManager sm) {
        // Nemi's First Ingredient (3207 - end)
        if (!sm.hasItem(RATZ_CHEESE, 10)) {
            sm.sayOk("Hmmm ... I don't think you have gathered up #t4031129#s yet. Please head over to Eos Tower and defeat the #o3110102#, then gather up 10 #t4031129#s for me. I feel like I'll be the best cook in the world with that cheese.");
            return;
        }

        sm.sayNext("Hey, you're back! Yes! That's the cheese I was talking about! With this, I think I can make a hearty bowl of soup that my father will really enjoy after a long day at work. Thank you so much for helping me out again. I'll have to reward you for this. I hope you like it...");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        if (!sm.canAddItem(2020004, 100)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Do you like the 100 #t2020004#s that I gave you? Now that I've gotten the ingredients and all, I'll have to start working on this soup. My father is coming soon, and I'll need to start this now in order for him to enjoy it the moment he gets here. Thank you so much for your help! Now, if you'll excuse me...");

        sm.removeItem(RATZ_CHEESE, 10);
        sm.addItem(2020004, 100); // Salad
        sm.addExp(4200);
        sm.forceCompleteQuest(3207);
    }

    // ================================
    // Dr. Kim's Blueprint Chain (Final)
    // ================================

    @Script("q3404s")
    public static void q3404s(ScriptManager sm) {
        // Dr. Kim's Comments: A Meeting with Gunny (3404 - start)
        sm.sayNext("Must have been tough for you to show all 3 of us the #t" + BLUEPRINT_MACHINE + "#. All you need to do now is to report all this to #p2050001#. The #t2030011# that I just gave you is an item that allows you to teleport straight to the Command Center in Omega Sector. This will help you report this much easier.");

        sm.forceStartQuest(3404);
    }

    @Script("q3404e")
    public static void q3404e(ScriptManager sm) {
        // Dr. Kim's Comments: A Meeting with Gunny (3404 - end)
        sm.sayNext("Oh ho... you must have met #p2050005#, #p2050006#, and #p2050007# and showed them #t" + BLUEPRINT_MACHINE + "#. How did they react to it? Were they happy to see it? Hahaha... that was good to know. Now, since you helped us out a great deal, here's a small reward for your job well done. I know it isn't much, but please take it.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        sm.sayOk("Did you get the 12,000 Mesos? I also raised your fame level a little bit. It's only fair that your reputation improves after the great job you did. I may need your help again down the road, so please drop by from time to time again.");

        sm.removeItem(BLUEPRINT_MACHINE, 1);
        sm.addMoney(12000);
        sm.addExp(4500);
        // Fame is handled by quest definition XML
        sm.forceCompleteQuest(3404);
    }

    // ================================
    // Nemi's Quest Chain (Continued)
    // ================================

    @Script("q3208s")
    public static void q3208s(ScriptManager sm) {
        // Nemi's Second Ingredient (3208 - start)
        sm.sayNext("Hi~ It's sunny here today. I can't tell you how much I love the fact that it's sunny here 365 days a year. How's it going? Are you here just to say hi? Well, if that's so... then can you help me out one more time...?");

        if (!sm.askYesNo("Will you help Nemi gather more ingredients?")) {
            sm.sayOk("I see ... you must be swamped with other things yourself. If you ever find some time, then please come talk to me. I can always use some help, you know~");
            return;
        }

        sm.sayNext("Thank you so much~! This one's also on the fact that I don't have anything to cook with at home. My dad works everyday, and I want to make something healthy as well as delicious for his lunch today, but I have nothing to work with at home. My dad seems to be losing his appetite, so I need something better than usual.");
        sm.sayBoth("Head over to Eos Tower and you'll see a monster called #b#o3230308##k hanging around at the outer wall. They cough up #t4000116#, so please gather up #b15 #t4000116#s#k. They are full inside, and tasty like no other. It can be used on a variety of dishes, so ... good luck!");

        sm.forceStartQuest(3208);
    }

    @Script("q3208e")
    public static void q3208e(ScriptManager sm) {
        // Nemi's Second Ingredient (3208 - end)
        if (!sm.hasItem(BIRK_EGG, 15)) {
            sm.sayOk("I don't think you have the stuff I asked for, yet. Defeat the #b#o3230308#s#k, who are usually located at the outer wall of Eos Tower, and collect #b15 #t4000116#s#k in the process. That will make me look like a great cook in a hurry!");
            return;
        }

        sm.sayNext("Hey, you're back! That's it! That's what I was looking for! My father will love it when he sees it in his lunchbox! Thank you so much, yet again. Here, this is an item that I truly cherish, and it's for you. Hope you like it.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        if (!sm.canAddItem(1032006, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Do you like the #b#t1032006##k? Now that I have the ingredients ready, I'll have to get back to cooking now. I can literally see my dad drooling over this as we speak. Thank you so much for you help. I'll have to go now. See you around!");

        sm.removeItem(BIRK_EGG, 15);
        sm.addItem(1032006, 1); // Silver Earring
        sm.addExp(5000);
        sm.forceCompleteQuest(3208);
    }

    @Script("q3209s")
    public static void q3209s(ScriptManager sm) {
        // Nemi's Dilemma (3209 - start)
        sm.sayNext("Oh no... what should I do... oh hi!! You came by again! I have run into yet another problem. I am so sorry that I keep asking you for favors, but I seriously can't find a way to wiggle out of this by myself. Can you help me out...?");

        if (!sm.askYesNo("Will you help Nemi with her rat problem?")) {
            sm.sayOk("I see ... you must be swamped with other things yourself. If you ever find some time, then please come talk to me. I can always use some help, you know~");
            return;
        }

        sm.sayNext("Thank you. I am sure you're terribly busy but still. Well, here's a problem. It has happened before, but it has happened much more often lately. Every time I make food, it disappears. I am sure it's the rats, but I can't prove it.");
        sm.sayBoth("I work hard at making the best food possible, and as soon as I leave, even for a few minutes, the food just disappears. It's so disheartening. The thing is ... this has happened before, and because of that, I got some #o3110102#s as a toy rat, and I set up rat traps on them to catch rats.");
        sm.sayBoth("But as you can see, they have since turned into monsters and are attacking people now. It's all my fault... and because of that... can you go back to Eos Tower and defeat #b#o3110102##k? Please gather up #b45 #t4000095#s#k afterwards, and do it ASAP. Thank you.");

        sm.forceStartQuest(3209);
    }

    @Script("q3209e")
    public static void q3209e(ScriptManager sm) {
        // Nemi's Dilemma (3209 - end)
        if (!sm.hasItem(RAT_TRAP, 45)) {
            sm.sayOk("Please head over to Eos Tower and collect #b45 #t4000095#s#k by defeating the #b#o3110102##k so I can chase off the rats from my food. I'll have to get the #t4000095#s back from the rats. Thanks again!");
            return;
        }

        sm.sayNext("Oh no, those rats must have taken my lunch ... whoa, hello! You're back already! That's much faster than I thought! Sigh ... I'm just glad it's over now. With these traps, I can at least surround my food with them. This may not seem like much, but please accept it. Thanks.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        sm.sayOk("Do you like the hat that I gave you? I've been saving up the hat in case I venture outside Ludibrium, but it looks like you need it more than I do right now. Now I'll have to set these traps up before making the dinner. I'll see you around!");

        // Job-specific hat reward - the system will handle job selection
        sm.removeItem(RAT_TRAP, 45);
        sm.addExp(6000);
        sm.forceCompleteQuest(3209);
    }

    // ================================
    // Brothers' Rice Quest Chain (Continued)
    // ================================

    @Script("q3604s")
    public static void q3604s(ScriptManager sm) {
        // The Brothers' Stack of Rice 4 (3604 - start)
        sm.sayNext("Did you make sure my brother did not notice a change in the stack of rice? It's weird, though; my stack of rice hasn't changed one bit. Don't you think so, too? This is why... um...  can you go back to my brother's house once more?");

        if (!sm.askYesNo("Will you deliver another sack of rice?")) {
            sm.sayOk("I'm sorry that I asked you to do the same thing twice.");
            return;
        }

        if (!sm.canAddItem(SACK_OF_RICE_4, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Please, please make sure my brother does NOT find out about this.");

        sm.addItem(SACK_OF_RICE_4, 1);
        sm.forceStartQuest(3604);
    }

    @Script("q3604e")
    public static void q3604e(ScriptManager sm) {
        // The Brothers' Stack of Rice 4 (3604 - end)
        if (!sm.hasItem(SACK_OF_RICE_4)) {
            sm.sayOk("This is Chil Sung's stack of rice.");
            return;
        }

        sm.sayOk("I see a stack of rice stacked at the garden of Chil Sung's house. Secretly, I brought out a sack of rice and laid it unnoticeably on top of the stack.");

        sm.removeItem(SACK_OF_RICE_4, 1);
        sm.addExp(1000);
        sm.forceCompleteQuest(3604);
    }

    @Script("q3605s")
    public static void q3605s(ScriptManager sm) {
        // Brotherly Love (3605 - start)
        sm.sayNext("Hey, what's that sack of rice you're carrying right now? I asked you to leave it at my brother's house, and ... what are you doing bring it back here? What? My brother was the one making the request? Ahhh ... that's my brother there. No wonder my stack of rice hasn't shrunk one bit...");
        sm.sayBoth("So you went through all this trouble thanks to us brothers... thankfully, this incident is bound to make our brotherly bond that much tighter. I think my brother may have noticed it by now, too. I know it's a lot of work, but please go visit my brother Chil Nam right now. I'm sure he wants to find a way to say thank you for your hard work.");

        sm.forceStartQuest(3605);
    }

    @Script("q3605e")
    public static void q3605e(ScriptManager sm) {
        // Brotherly Love (3605 - end)
        sm.sayNext("Hi, there. I've noticed it, too. I was wondering why my stack of rice didn't shrink at all, and I just got a message from my brother about what happened. I really don't know how to thank you for going through all this trouble...");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        if (!sm.canAddItem(BROTHERLY_LOVE_LETTER, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("I will never forget your hard work in this. I can't give you anything right this minute, but if you ever feel like feasting on some pork and buckwheat paste, please come visit us. My brother and I will make the best possible food you'll find in this town, just for you.");

        sm.addItem(BROTHERLY_LOVE_LETTER, 1);
        sm.addExp(4000);
        sm.forceCompleteQuest(3605);
    }

    // ================================
    // Hongbu and Nolbu Quest Chain (Korean Folk Town)
    // ================================

    @Script("q3606s")
    public static void q3606s(ScriptManager sm) {
        // The Lost Seed (3606 - start)
        sm.sayNext("Oh no... My wing must be clipped. I need to find the seed, though... please help me.");

        if (!sm.askYesNo("Will you help the injured Swallow?")) {
            sm.sayOk("Aww, you're not going to leave me here, are you? My wing just got clipped!");
            return;
        }

        sm.sayNext("I'm the messenger from the God of Sky, and I am in a little bit of a tight spot right now. I was carrying a present from the God of Sky for this man that took care of my broken leg last spring, but while I was resting at the top of the mountain, I dropped it somewhere, and now I can't find it.");
        sm.sayBoth("So I went into the woods looking for that lost seed, only to find a bunch of angry rabbits chasing me. That's how I clipped my wing, frantically trying to fly away from those rabbits. If the lord found out that I have lost the seed, then I'll be harshly reprimanded for this!! Pleeeeeease help me find the seed. I have a feeling that it's one of those rabbits that have the seed. I am pretty sure of that!");

        sm.forceStartQuest(3606);
    }

    @Script("q3606e")
    public static void q3606e(ScriptManager sm) {
        // The Lost Seed (3606 - end)
        if (!sm.hasItem(LOST_SEED)) {
            sm.sayOk("Did you find the seed? If I ever tell the lord up there I lost the seed, then I will be scolded by him... sigh...");
            return;
        }

        sm.sayNext("Hey, that seed you have in your hand... that's it!!! You really did find it! That's incredible! Thank you thank you thank you! Wait, oh no... now I forgot who to give this seed too. I keep forgetting the most important things! Was it Hongbu or Nolbu?");

        if (!sm.askYesNo("Accept the seed to deliver?")) {
            return;
        }

        if (!sm.canAddItem(SWALLOW_SEED, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("I really cannot remember which one... I'm sure it's either Hongbu or Nolbu, but ... I can't even fly there right now because my wing is clipped... please take this seed to either one of the two. I'll trust you to make the correct call.");

        sm.removeItem(LOST_SEED, 1);
        sm.addItem(SWALLOW_SEED, 1);
        sm.addExp(10000);
        sm.forceCompleteQuest(3606);
    }

    @Script("q3607s")
    public static void q3607s(ScriptManager sm) {
        // Opening Nolbu's Gourd (3607 - start)
        int choice = sm.askMenu("What? What do you want from me? If you want to ask me for a favor, then get lost! I don't have time to listen to your 'request'!",
                Map.of(
                        0, "#bFine! I don't want anything to do with you, either!#k",
                        1, "#bDid you fix a swallow's leg by any chance? Someone told me to give you this seed.#k"
                ));

        if (choice == 0) {
            sm.sayOk("Get out! Leave!");
            return;
        }

        sm.sayNext("A swallow? Right, right. I did fix its legs before. You're telling me this seed is for me? Haha, alright, then. Okay, while you're here with the seed, can you do me a favor and plant it on my roof? And once it yields the gourd, open it and bring me whatever that's inside, okay?");

        if (!sm.askYesNo("Plant the seed on Nolbu's roof?")) {
            sm.sayOk("You're telling me that's too much? This is why the outsiders cannot be trusted...");
            return;
        }

        if (!sm.canAddItem(NOLBU_GOURD_SEED, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Haha... wonder what kind of expensive jewelry is inside the gourd ... hey, what are you doing standing there? Go up there and plant the seed right now! What's taking you so long?");

        sm.removeItem(SWALLOW_SEED, 1);
        sm.addItem(NOLBU_GOURD_SEED, 1);
        sm.forceStartQuest(3607);
    }

    @Script("q3607e")
    public static void q3607e(ScriptManager sm) {
        // Opening Nolbu's Gourd (3607 - end)
        if (!sm.hasItem(NOLBU_GOURD)) {
            sm.sayOk("I told you to open the gourd! Why are you empty-handed? Go open it right now!");
            return;
        }

        int choice = sm.askMenu("Okay, so did you open the gourd? What came out of it? How big's the jewelry?",
                Map.of(0, "#bJust this piece of paper.#k"));

        sm.sayNext("What? That can't be! Give me that paper! What's this? Warrant of attachment? I need to forfeit my possessions to whoever brought this document? THIS CANNOT BE!!!!");

        choice = sm.askMenu("",
                Map.of(0, "#bSo I get to take all your possessions? What should I take?#k"));

        sm.sayNext("NO! NO! This represents everything I have!");

        if (!sm.askYesNo("Complete the quest?")) {
            return;
        }

        if (!sm.canAddItem(GOURD_TREASURE, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Oh my ... I am RUINED! I am BROKE! NOOOO!");

        sm.removeItem(NOLBU_GOURD, 1);
        sm.addItem(GOURD_TREASURE, 1);
        sm.addExp(10000);
        sm.forceCompleteQuest(3607);
    }

    @Script("q3608s")
    public static void q3608s(ScriptManager sm) {
        // Opening Hongbu's Gourd (3608 - start)
        int choice = sm.askMenu("How can I help you? As you can see, our family isn't doing too well here, so I am really sorry, but I can't serve anyone right now, even a stranger from out of town like you.",
                Map.of(
                        0, "#bWow, you really must be struggling here.#k",
                        1, "#bDid you fix a swallow's leg by any chance? Someone told me to give you this seed.#k"
                ));

        if (choice == 0) {
            sm.sayOk("...yes, I know , but ... I still feel very rich inside.");
            return;
        }

        sm.sayNext("Well, I did find this swallow that broke a leg back in spring, and fixed its leg, but ... you're telling me that Swallow told you to give this seed to me? That's interesting. Say, if you aren't really busy and all, can you please do me a favor and plant the seed on the roof of our house? I am sorry, but I haven't had anything to eat for the past few days, and I am too tired to go up there. If the seed turns into a gourd later on, then can you do me another favor and open it and give me what's inside? Thank you.");

        if (!sm.askYesNo("Plant the seed on Hongbu's roof?")) {
            sm.sayOk("I am sorry. I must have asked too much from a stranger like you. If you are busy with other things, then please go take care of your business.");
            return;
        }

        if (!sm.canAddItem(HONGBU_GOURD_SEED, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Thank you so much. Please be careful while climbing up there. The rope is really old and ragged, you know.");

        sm.removeItem(SWALLOW_SEED, 1);
        sm.addItem(HONGBU_GOURD_SEED, 1);
        sm.forceStartQuest(3608);
    }

    @Script("q3608e")
    public static void q3608e(ScriptManager sm) {
        // Opening Hongbu's Gourd (3608 - end)
        if (!sm.hasItem(HONGBU_GOURD)) {
            sm.sayOk("Is it still a long time before the gourd opens? Was there anything inside?");
            return;
        }

        int choice = sm.askMenu("Did you open the gourd? Is it full inside? If so, then I better make a huge bowl of porridge out of it for my family. They missed a meal today, you know...",
                Map.of(0, "#bHey, inside the gourd is nothing but jewelry and gold!#k"));

        sm.sayNext("What?? All this?? Wow ... it's a relief; now I can definitely feed my family until they are too full to eat. Thank you so very much. As a sign of thank you, I want you to take one of these jewelries. Please choose one.");

        if (!sm.askYesNo("Accept the reward?")) {
            return;
        }

        if (!sm.canAddItem(GOURD_TREASURE, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("I got lucky thanks to the seed you brought here. I can't thank you enough for this. Thank you so much. Have a safe trip~");

        sm.removeItem(HONGBU_GOURD, 1);
        sm.addItem(GOURD_TREASURE, 1);
        sm.addExp(10000);
        sm.forceCompleteQuest(3608);
    }

    @Script("q3609s")
    public static void q3609s(ScriptManager sm) {
        // The Seed That Swallow Lost (3609 - start)
        sm.sayNext("What's going on? What? You lost the seed?? How can you lose it? Okay, I'll give you another one right now. This time, PLEASE don't lose it, and give it to the rightful owner, okay?");

        if (!sm.askYesNo("Get another seed from Swallow?")) {
            sm.sayOk("Well, then you won't need the seed. Good bye.");
            return;
        }

        sm.sayOk("I need to look for it, so please talk to me in a little bit.");
        sm.forceStartQuest(3609);
    }

    @Script("q3609e")
    public static void q3609e(ScriptManager sm) {
        // The Seed That Swallow Lost (3609 - end)
        if (!sm.canAddItem(SWALLOW_SEED, 1)) {
            sm.sayOk("Please make sure you have enough inventory space.");
            return;
        }

        sm.sayOk("Please get this to the rightful owner, although I still have no idea who should receive this.");

        sm.addItem(SWALLOW_SEED, 1);
        sm.forceCompleteQuest(3609);
    }

    // ADDITIONAL LUDIBRIUM QUESTS (3100-3800) ------------------------------------------------------------------------------------------------------------

    @Script("q3108s")
    public static void q3108s(ScriptManager sm) {
        // Quest 3108 - Snowman's Rage-Found a clue (START)
        // NPC: 2020012
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3108);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3116s")
    public static void q3116s(ScriptManager sm) {
        // Quest 3116 - Ludibrium Quest (START)
        sm.forceStartQuest(3116);
    }


    @Script("q3118s")
    public static void q3118s(ScriptManager sm) {
        // Quest 3118 - Ludibrium Quest (START)
        sm.forceStartQuest(3118);
    }


    @Script("q3122e")
    public static void q3122e(ScriptManager sm) {
        // Quest 3122 - Ludibrium Quest (END)
        sm.forceCompleteQuest(3122);
    }


    @Script("q3122s")
    public static void q3122s(ScriptManager sm) {
        // Quest 3122 - Ludibrium Quest (START)
        sm.forceStartQuest(3122);
    }


    @Script("q3125s")
    public static void q3125s(ScriptManager sm) {
        // Quest 3125 - Ludibrium Quest (START)
        sm.forceStartQuest(3125);
    }


    @Script("q3452e")
    public static void q3452e(ScriptManager sm) {
        // Quest 3452 - Blocktopus is an Alien? (END)
        // NPC: 2050001

        final int QUEST_ITEM_4000099 = 4000099;

        if (!sm.hasItem(QUEST_ITEM_4000099, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4000099, 1);
            sm.forceCompleteQuest(3452);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q3514e")
    public static void q3514e(ScriptManager sm) {
        // Quest 3514 - The Sorcerer Who Sells Emotions (END)
        // NPC: 2140002

        final int QUEST_ITEM_2022337 = 2022337;

        if (!sm.hasItem(QUEST_ITEM_2022337, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_2022337, 1);
            sm.forceCompleteQuest(3514);
            sm.addExp(891500); // EXP reward
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q3523s")
    public static void q3523s(ScriptManager sm) {
        // Quest 3523 - In Search for the Lost Memory (START)
        // NPC: 1022000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3523);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3524s")
    public static void q3524s(ScriptManager sm) {
        // Quest 3524 - In Search for the Lost Memory (START)
        // NPC: 1032001
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3524);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3525s")
    public static void q3525s(ScriptManager sm) {
        // Quest 3525 - In Search for the Lost Memory (START)
        // NPC: 1012100
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3525);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3526s")
    public static void q3526s(ScriptManager sm) {
        // Quest 3526 - In Search for the Lost Memory (START)
        // NPC: 1052001
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3526);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3527s")
    public static void q3527s(ScriptManager sm) {
        // Quest 3527 - In Search for the Lost Memory (START)
        // NPC: 1090000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3527);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3529s")
    public static void q3529s(ScriptManager sm) {
        // Quest 3529 - In Search for the Lost Memory (START)
        // NPC: 1101002
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3529);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3539s")
    public static void q3539s(ScriptManager sm) {
        // Quest 3539 - Searching for Lost Memories (START)
        // NPC: 1201000
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3539);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3540s")
    public static void q3540s(ScriptManager sm) {
        // Quest 3540 - In Search of Lost Memories (START)
        // NPC: 1012003
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3540);
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3541s")
    public static void q3541s(ScriptManager sm) {
        // Quest 3541 - Ludibrium Quest (START)
        sm.forceStartQuest(3541);
    }


    @Script("q3714s")
    public static void q3714s(ScriptManager sm) {
        // Quest 3714 - The Remnants of Horned Tail... (START)
        // NPC: 2081011
        sm.sayNext("Would you like to begin this quest?");

        if (sm.askAccept("Are you ready to start?")) {
            sm.forceStartQuest(3714);
            sm.addItem(4001094, 1); // Quest item
            sm.sayOk("Good luck with your quest!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }


    @Script("q3759e")
    public static void q3759e(ScriptManager sm) {
        // Quest 3759 - Towards the Sky 2 (END)
        // NPC: 2085000

        final int QUEST_ITEM_4032531 = 4032531;

        if (!sm.hasItem(QUEST_ITEM_4032531, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4032531, 1);
            sm.forceCompleteQuest(3759);
            sm.addExp(11000); // EXP reward
            sm.addItem(4032531, -1); // Reward item
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }


    @Script("q3833e")
    public static void q3833e(ScriptManager sm) {
        // Quest 3833 - Gathering Up the Lacking Ingredients (END)
        // NPC: 2092000

        final int QUEST_ITEM_4000294 = 4000294;

        if (!sm.hasItem(QUEST_ITEM_4000294, 1)) {
            sm.sayOk("You need 1 of the required quest items.");
            return;
        }

        sm.sayNext("You have completed the quest!");
        
        if (sm.askYesNo("Would you like to complete this quest and receive your reward?")) {
            sm.removeItem(QUEST_ITEM_4000294, 1);
            sm.forceCompleteQuest(3833);
            sm.sayOk("Congratulations on completing the quest!");
        } else {
            sm.sayOk("Come back when you're ready to complete the quest.");
        }
    }
}
