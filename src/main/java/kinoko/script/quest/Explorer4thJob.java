package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Explorer 4th Job Advancement Quest Scripts
 *
 * Quest Chain Pattern (Level 120):
 * - 6900-6904: Warrior (Dark Knight, Hero, Paladin)
 * - 6910-6914: Magician (Arch Mage F/P, Arch Mage I/L, Bishop)
 * - 6920-6924: Bowman (Bow Master, Marksman)
 * - 6930-6934: Thief (Night Lord, Shadower)
 * - 6940-6944: Pirate (Buccaneer, Corsair)
 */
public final class Explorer4thJob extends ScriptHandler {

    // ========================================
    // WARRIOR 4TH JOB QUEST CHAIN (6900-6904)
    // ========================================

    @Script("q6900s")
    public static void q6900s(ScriptManager sm) {
        // Quest 6900 - Tylus' Introduction Letter (START)
        // NPC: Tylus (2020008) in El Nath
        final int LETTER_OF_INTRODUCTION = 4031342;

        sm.sayNext("It's been a quite a while since I've last seen you. I'm happy to see you improved so much. Do you realize the hidden strength within you? You must have some reason to see me. What can I do for you?");

        final int answer = sm.askMenu("I knew that you would return to see me someday. But I have no power to make your wish come true. Go to #bMinar Forest#k. If you find #b#p2081100##k, she may be able help you. Would you like to meet her?",
            java.util.Map.of(0, "I want the 4th job advancement."));

        if (answer == 0) {
            if (sm.askYesNo("I'll recommend you to him. Hope you get stronger!")) {
                sm.forceStartQuest(6900);
                sm.addItem(LETTER_OF_INTRODUCTION, 1);
                sm.sayNext("Remember. Bishop of Minar forest, #b#p2081100##k. Please see him.");
            } else {
                sm.sayOk("Aren't you here to do the 4th job advancement? If not, that's fine.");
            }
        }
    }

    @Script("q6900e")
    public static void q6900e(ScriptManager sm) {
        // Quest 6900 - Tylus' Introduction Letter (END)
        // NPC: Harmonia (2081100) in Leafre
        final int LETTER_OF_INTRODUCTION = 4031342;

        if (!sm.hasItem(LETTER_OF_INTRODUCTION, 1)) {
            sm.sayOk("What are you doing here?");
            return;
        }

        sm.sayNext("Why do you want to see me, young warrior..");
        sm.sayBoth("#b#p2020008##k?... Is he the one in El Nath? Then I can trust you.");

        if (sm.askYesNo("A young warrior who wants increase their power. I have to tell you something. Talk to me only if you're ready to hear the truth. Many secrets will be revealed...")) {
            sm.removeItem(LETTER_OF_INTRODUCTION, 1);
            sm.forceCompleteQuest(6900);
            sm.addExp(20000);
            sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q6901s")
    public static void q6901s(ScriptManager sm) {
        // Quest 6901 - Harmonia's First Story (START)
        sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");

        if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
            sm.forceStartQuest(6901);
            sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
            sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
            sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
            sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
        } else {
            sm.sayOk("Are you afraid? You've come this far...");
        }
    }

    @Script("q6901e")
    public static void q6901e(ScriptManager sm) {
        // Quest 6901 - Harmonia's First Story (END)
        sm.forceCompleteQuest(6901);
        sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
    }

    @Script("q6902s")
    public static void q6902s(ScriptManager sm) {
        // Quest 6902 - Harmonia's Second Truth (START)
        sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");

        if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
            sm.forceStartQuest(6902);
            sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
            sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
            sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6902e")
    public static void q6902e(ScriptManager sm) {
        // Quest 6902 - Harmonia's Second Truth (END)
        sm.forceCompleteQuest(6902);
        sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
    }

    @Script("q6903s")
    public static void q6903s(ScriptManager sm) {
        // Quest 6903 - Harmonia's Third Story (START)
        sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");

        if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
            sm.forceStartQuest(6903);
            sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
            sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
            sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
            sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
            sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
        }
    }

    @Script("q6903e")
    public static void q6903e(ScriptManager sm) {
        // Quest 6903 - Harmonia's Third Story (END)
        sm.forceCompleteQuest(6903);
        sm.sayOk("Talk to me when you're ready.");
    }

    @Script("q6904s")
    public static void q6904s(ScriptManager sm) {
        // Quest 6904 - Hero's Quality (START)
        sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");

        if (sm.askYesNo("Get me two things: #b#t4031343##k and #b#t4031344##k. Are you ready?")) {
            sm.forceStartQuest(6904);
            sm.sayNext("Go and get #b#t4031343##k and #b#t4031344##k.");
            sm.sayOk("It's up to you how you get it. If you want to use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom, you can get them through #b#p2081000##k in Leafre.");
        } else {
            sm.sayOk("What are you afraid of? Great power awaits you..");
        }
    }

    @Script("q6904e")
    public static void q6904e(ScriptManager sm) {
        // Quest 6904 - Hero's Quality (END)
        final int HEROIC_PENTAGON = 4031343;
        final int HEROIC_STAR = 4031344;

        if (!sm.hasItem(HEROIC_PENTAGON, 1) || !sm.hasItem(HEROIC_STAR, 1)) {
            sm.sayOk("You haven't gathered #b#t4031343##k and #b#t4031344##k. That's will prove your quality.");
            return;
        }

        sm.removeItem(HEROIC_PENTAGON, 1);
        sm.removeItem(HEROIC_STAR, 1);
        sm.forceCompleteQuest(6904);
        sm.addExp(50000);
        sm.sayNext("You proved your quality as a hero.");
        sm.sayOk("Now, what lies before you is the Way of a #bWarrior#k. Talk to me again if you are ready for the 4th job Advancement.");
    }

    // ========================================
    // THIEF 4TH JOB QUEST CHAIN (6930-6934)
    // ========================================

    @Script("q6930s")
    public static void q6930s(ScriptManager sm) {
        // Quest 6930 - Arec's Letter of Introduction (START)
        // NPC: Arec (2020011) in El Nath
        final int LETTER_OF_INTRODUCTION = 4031516;

        sm.sayNext("Long time no see. I heard about you. You seem to have had a hard time. Did you find darkness within you? Then you must have a reason for being here. What can I do for you?");

        final int answer = sm.askMenu("I knew that you would return to see me someday. But I have no power to make your wish come true. Go to #bMinar Forest#k. If you find #b#p2081400##k, she may be able help you. Would you like to meet her?",
            java.util.Map.of(0, "I want the 4th job advancement."));

        if (answer == 0) {
            if (sm.askYesNo("I'll write a recommendation letter for you. Hope you get a new power.")) {
                sm.forceStartQuest(6930);
                sm.addItem(LETTER_OF_INTRODUCTION, 1);
                sm.sayNext("Take this letter to #b#p2081400##k in Leafre. She will guide you on the path to ultimate power.");
            } else {
                sm.sayOk("Aren't you here for the 4th job advancement? If you don't want to that's fine.");
            }
        }
    }

    @Script("q6930e")
    public static void q6930e(ScriptManager sm) {
        // Quest 6930 - Arec's Letter of Introduction (END)
        // NPC: Hellin (2081400) in Leafre
        final int LETTER_OF_INTRODUCTION = 4031516;

        if (!sm.hasItem(LETTER_OF_INTRODUCTION, 1)) {
            sm.sayOk("What are you doing here?");
            return;
        }

        sm.sayNext("What are you doing here, young Thief?");
        sm.sayBoth("#b#p2020011##k?... The one in El Nath? Then I can trust you.");

        if (sm.askYesNo("A young Thief dreaming of being a Nightlord or Shadower. I have a few stories to tell you, my stealthy friend. Are you ready?")) {
            sm.removeItem(LETTER_OF_INTRODUCTION, 1);
            sm.forceCompleteQuest(6930);
            sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q6931s")
    public static void q6931s(ScriptManager sm) {
        // Quest 6931 - Hellin's First Story (START)
        sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");

        if (sm.askYesNo("Good. You have the right to listen to the story. Are you ready?")) {
            sm.forceStartQuest(6931);
            sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
            sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
            sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
            sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople..");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6931e")
    public static void q6931e(ScriptManager sm) {
        // Quest 6931 - Hellin's First Story (END)
        sm.forceCompleteQuest(6931);
        sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
    }

    @Script("q6932s")
    public static void q6932s(ScriptManager sm) {
        // Quest 6932 - Hellin's Second Truth (START)
        sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");

        if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
            sm.forceStartQuest(6932);
            sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
            sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
            sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6932e")
    public static void q6932e(ScriptManager sm) {
        // Quest 6932 - Hellin's Second Truth (END)
        sm.forceCompleteQuest(6932);
        sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
    }

    @Script("q6933s")
    public static void q6933s(ScriptManager sm) {
        // Quest 6933 - Hellin's Third Story (START)
        sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");

        if (sm.askYesNo("Yes, you do have the right to listen to the stories. Are you ready?")) {
            sm.forceStartQuest(6933);
            sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where the time stopped.");
            sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
            sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
            sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
            sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6933e")
    public static void q6933e(ScriptManager sm) {
        // Quest 6933 - Hellin's Third Story (END)
        sm.forceCompleteQuest(6933);
        sm.sayOk("Talk to me when you're ready for the final trial.");
    }

    @Script("q6934s")
    public static void q6934s(ScriptManager sm) {
        // Quest 6934 - Hero's Quality (START)
        sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");

        if (sm.askYesNo("Get me two things: #b#t4031517##k and #b#t4031518##k. Are you ready?")) {
            sm.forceStartQuest(6934);
            sm.sayNext("It's up to you how you get it. If you wanna use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom and warm heart, you can get them through #b#p2081000##k in Leafre.");
        } else {
            sm.sayOk("What are you afraid of? Great power awaits you...");
        }
    }

    @Script("q6934e")
    public static void q6934e(ScriptManager sm) {
        // Quest 6934 - Hero's Quality (END)
        final int HEROIC_STAR = 4031517; // Heroic Star
        final int HEROIC_PENTAGON = 4031518; // Heroic Pentagon

        if (!sm.hasItem(HEROIC_STAR, 1) || !sm.hasItem(HEROIC_PENTAGON, 1)) {
            sm.sayOk("Haven't you got #b#t4031517##k and #b#t4031518##k?");
            return;
        }

        sm.removeItem(HEROIC_STAR, 1);
        sm.removeItem(HEROIC_PENTAGON, 1);
        sm.forceCompleteQuest(6934);
        sm.sayNext("You proved your quality as a hero.");
        sm.sayOk("Now you only have to go to the way of a Shadower or Nightlord. Talk to me if you're ready for the 4th job advancement.");
    }

    // ========================================
    // MAGICIAN 4TH JOB QUEST CHAIN (6910-6914)
    // ========================================

    @Script("q6910s")
    public static void q6910s(ScriptManager sm) {
        // Quest 6910 - Robeira's Introduction Letter (START)
        // NPC: Robeira (2020009) in El Nath
        final int LETTER_OF_INTRODUCTION = 4031510;

        sm.sayNext("Long time no see. I'm happy to see you improved. Did you find the truth in your mind? Then you must have some reason to be here. What can I do for you?");

        final int answer = sm.askMenu("Yes. I was expecting you. But I don't have enough power to help you. Go to #bMinar Forest#k. #b#p2081200##k will help make your dream come true. Do you want to see him?",
            java.util.Map.of(0, "I want the 4th job advancement."));

        if (answer == 0) {
            if (sm.askYesNo("Then I'll recommend you to him. Don't be rude to him. May you find the power you seek!")) {
                sm.forceStartQuest(6910);
                sm.addItem(LETTER_OF_INTRODUCTION, 1);
                sm.sayNext("Remember. The bishop of Minar Forest, #b#p2081200##k. Please see him.");
            } else {
                sm.sayOk("Aren't you here for the 4th job advancement? If you don't want it, that's fine.");
            }
        }
    }

    @Script("q6910e")
    public static void q6910e(ScriptManager sm) {
        // Quest 6910 - Robeira's Introduction Letter (END)
        // NPC: Bishop (2081200) in Leafre
        final int LETTER_OF_INTRODUCTION = 4031510;

        if (!sm.hasItem(LETTER_OF_INTRODUCTION, 1)) {
            sm.sayOk("What are you doing here?");
            return;
        }

        sm.sayNext("What are you doing here young magician?");
        sm.sayBoth("#b#p2020009##k?... That's the one who lives in El Nath. If she recommended you, I can trust you.");

        if (sm.askYesNo("A young magician dreaming of being an Arch Mage. I have to tell you a few stories. Talk to me when you're ready.")) {
            sm.removeItem(LETTER_OF_INTRODUCTION, 1);
            sm.forceCompleteQuest(6910);
            sm.addExp(20000);
            sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q6911s")
    public static void q6911s(ScriptManager sm) {
        // Quest 6911 - Bishop's First Story (START)
        sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");

        if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
            sm.forceStartQuest(6911);
            sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
            sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
            sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
            sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
        } else {
            sm.sayOk("Are you afraid? You've come this far...");
        }
    }

    @Script("q6911e")
    public static void q6911e(ScriptManager sm) {
        // Quest 6911 - Bishop's First Story (END)
        sm.forceCompleteQuest(6911);
        sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
    }

    @Script("q6912s")
    public static void q6912s(ScriptManager sm) {
        // Quest 6912 - Bishop's Second Truth (START)
        sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");

        if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
            sm.forceStartQuest(6912);
            sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
            sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
            sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6912e")
    public static void q6912e(ScriptManager sm) {
        // Quest 6912 - Bishop's Second Truth (END)
        sm.forceCompleteQuest(6912);
        sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
    }

    @Script("q6913s")
    public static void q6913s(ScriptManager sm) {
        // Quest 6913 - Bishop's Third Story (START)
        sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");

        if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
            sm.forceStartQuest(6913);
            sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
            sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
            sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
            sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
            sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
        }
    }

    @Script("q6913e")
    public static void q6913e(ScriptManager sm) {
        // Quest 6913 - Bishop's Third Story (END)
        sm.forceCompleteQuest(6913);
        sm.sayOk("Talk to me when you're ready.");
    }

    @Script("q6914s")
    public static void q6914s(ScriptManager sm) {
        // Quest 6914 - A Hero's Quality (START)
        sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");

        if (sm.askYesNo("Get me two things: #b#t4031511##k and #b#t4031512##k. Are you ready?")) {
            sm.forceStartQuest(6914);
            sm.sayNext("Get me #b#t4031511##k and #b#t4031512##k...");
            sm.sayOk("It's up to you how you get it. If you wanna use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom and warm heart, you can get them through #b#p2081000##k in Leafre.");
        } else {
            sm.sayOk("What are you afraid of? Great power awaits you...");
        }
    }

    @Script("q6914e")
    public static void q6914e(ScriptManager sm) {
        // Quest 6914 - A Hero's Quality (END)
        final int HEROIC_PENTAGON = 4031511;
        final int HEROIC_STAR = 4031512;

        if (!sm.hasItem(HEROIC_PENTAGON, 1) || !sm.hasItem(HEROIC_STAR, 1)) {
            sm.sayOk("You haven't found #b#t4031511##k and #b#t4031512##k.");
            return;
        }

        sm.removeItem(HEROIC_PENTAGON, 1);
        sm.removeItem(HEROIC_STAR, 1);
        sm.forceCompleteQuest(6914);
        sm.addExp(50000);
        sm.sayNext("You proved your quality as a hero.");
        sm.sayOk("Now you only have to go to the way of an Arch Mage. Talk to me if you're ready for the 4th job advancement.");
    }

    // ========================================
    // BOWMAN 4TH JOB QUEST CHAIN (6920-6924)
    // ========================================

    @Script("q6920s")
    public static void q6920s(ScriptManager sm) {
        // Quest 6920 - Rene's Introduction Letter (START)
        // NPC: Rene (2020010) in El Nath
        final int LETTER_OF_INTRODUCTION = 4031513;

        sm.sayNext("Long time no see. You remind me of the time when you came to me for the third advancement. Did you find the truth in your mind? You must have some reason to come to see me. What can I do for you?");

        final int answer = sm.askMenu("I knew that you would return to see me someday. But I have no power to make your wish come true. Go to #bMinar Forest#k. If you find #b#p2081300##k, he may be able help you. Would you like to meet him?",
            java.util.Map.of(0, "I want the 4th job advancement."));

        if (answer == 0) {
            if (sm.askYesNo("I'll write a recommendation letter for you. Hope you get a new power.")) {
                sm.forceStartQuest(6920);
                sm.addItem(LETTER_OF_INTRODUCTION, 1);
                sm.sayNext("Remember. The bishop of Minar Forest, #b#p2081200##k. Please see him.");
            } else {
                sm.sayOk("Aren't you here for the 4th job advancement? If you don't want it, that's fine.");
            }
        }
    }

    @Script("q6920e")
    public static void q6920e(ScriptManager sm) {
        // Quest 6920 - Rene's Introduction Letter (END)
        // NPC: Bishop (2081300) in Leafre
        final int LETTER_OF_INTRODUCTION = 4031513;

        if (!sm.hasItem(LETTER_OF_INTRODUCTION, 1)) {
            sm.sayOk("What are you doing here?");
            return;
        }

        sm.sayNext("What are you doing here, young Bowman?");
        sm.sayBoth("#b#p2020010##k?... The one in El Nath? Then I can trust you.");

        if (sm.askYesNo("A young Bowman dreaming of being a Bowmaster or Marksman. I have to tell you a few stories first. Talk to me when you're ready.")) {
            sm.removeItem(LETTER_OF_INTRODUCTION, 1);
            sm.forceCompleteQuest(6920);
            sm.addExp(20000);
            sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q6921s")
    public static void q6921s(ScriptManager sm) {
        // Quest 6921 - Bishop's First Story (START)
        sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");

        if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
            sm.forceStartQuest(6921);
            sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
            sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
            sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
            sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
        } else {
            sm.sayOk("Are you afraid? You've come this far...");
        }
    }

    @Script("q6921e")
    public static void q6921e(ScriptManager sm) {
        // Quest 6921 - Bishop's First Story (END)
        sm.forceCompleteQuest(6921);
        sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
    }

    @Script("q6922s")
    public static void q6922s(ScriptManager sm) {
        // Quest 6922 - Bishop's Second Truth (START)
        sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");

        if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
            sm.forceStartQuest(6922);
            sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
            sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
            sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6922e")
    public static void q6922e(ScriptManager sm) {
        // Quest 6922 - Bishop's Second Truth (END)
        sm.forceCompleteQuest(6922);
        sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
    }

    @Script("q6923s")
    public static void q6923s(ScriptManager sm) {
        // Quest 6923 - Bishop's Third Story (START)
        sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");

        if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
            sm.forceStartQuest(6923);
            sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
            sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
            sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
            sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
            sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
        }
    }

    @Script("q6923e")
    public static void q6923e(ScriptManager sm) {
        // Quest 6923 - Bishop's Third Story (END)
        sm.forceCompleteQuest(6923);
        sm.sayOk("Talk to me when you're ready.");
    }

    @Script("q6924s")
    public static void q6924s(ScriptManager sm) {
        // Quest 6924 - Hero's Quality (START)
        sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");

        if (sm.askYesNo("Get me two things. Nothing too hard. You have to bring me #b#t4031514##k and #b#t4031515##k.")) {
            sm.forceStartQuest(6924);
            sm.sayOk("It's up to you how you get it. If you wanna use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom and warm heart, you can get them through #b#p2081000##k in Leafre.");
        } else {
            sm.sayOk("What are you afraid of? Great power awaits you...");
        }
    }

    @Script("q6924e")
    public static void q6924e(ScriptManager sm) {
        // Quest 6924 - Hero's Quality (END)
        final int HEROIC_PENTAGON = 4031514;
        final int HEROIC_STAR = 4031515;

        if (!sm.hasItem(HEROIC_PENTAGON, 1) || !sm.hasItem(HEROIC_STAR, 1)) {
            sm.sayOk("You haven't got #b#t4031514##k and #b#t4031515##k...");
            return;
        }

        sm.removeItem(HEROIC_PENTAGON, 1);
        sm.removeItem(HEROIC_STAR, 1);
        sm.forceCompleteQuest(6924);
        sm.addExp(50000);
        sm.sayNext("You proved your quality as a hero.");
        sm.sayOk("Now you only have to go to the way of a Bowmaster or Marksman. Talk to me if you're ready for the 4th job advancement.");
    }

    // ========================================
    // PIRATE 4TH JOB QUEST CHAIN (6940-6944)
    // ========================================

    @Script("q6940s")
    public static void q6940s(ScriptManager sm) {
        // Quest 6940 - Pedro's Introduction (START)
        // NPC: Pedro (2020013) in El Nath
        final int LETTER_OF_INTRODUCTION = 4031859;

        sm.sayNext("It has been a long time. I have kept tabs on your steady progression. Seeing you standing before me, healthy and strong, I can sense that a lot has happened since our last encounter. Have you finally uncovered the freedom within you all this time? If so, then there must be a reason why you came all the way to see me. What is it?");

        final int answer = sm.askMenu("I see... I have known that this day will someday come. Unfortunately, I do not have the powers to fulfill your wish. In order for you to complete this process, you'll have to head over to the #bMinar Forst#k and meet #b#p2081500##k, who should be meditating as you walk in. He may be enough to fulfill your wish. Would you like to pay a visit?",
            java.util.Map.of(0, "I'd like to make the 4th job advancement."));

        if (answer == 0) {
            if (sm.askYesNo("I will write up a recommendation letter for you right now. I hope you come out of this with a wealth of new power at your disposal.")) {
                sm.forceStartQuest(6940);
                sm.addItem(LETTER_OF_INTRODUCTION, 1);
                sm.sayNext("Remember the name. The priest of Minar Forest, #b#p2081500##k. Visit him.");
            } else {
                sm.sayOk("Aren't you here to see me to make the 4th job advancement? If not, then don't mind me.");
            }
        }
    }

    @Script("q6940e")
    public static void q6940e(ScriptManager sm) {
        // Quest 6940 - Pedro's Introduction (END)
        // NPC: Priest (2081500) in Leafre
        final int LETTER_OF_INTRODUCTION = 4031859;

        if (!sm.hasItem(LETTER_OF_INTRODUCTION, 1)) {
            sm.sayOk("What are you doing here?");
            return;
        }

        sm.sayNext("What made you come all the way here to see me, young Pirate?");
        sm.sayBoth("#b#p2020013##k... Are you talking about the one in El Nath? If he's the one that recommended you, then you must be legit.");

        if (sm.askYesNo("Hello young Pirate, the one who strives to walk the path of the ultimate. I have a story I must share with you. When you are ready to see the truth, talk to me.")) {
            sm.removeItem(LETTER_OF_INTRODUCTION, 1);
            sm.forceCompleteQuest(6940);
            sm.addExp(20000);
            sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    @Script("q6941s")
    public static void q6941s(ScriptManager sm) {
        // Quest 6941 - Priest's First Story (START)
        sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");

        if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
            sm.forceStartQuest(6941);
            sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
            sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
            sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
            sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
        } else {
            sm.sayOk("Are you afraid? You've come this far...");
        }
    }

    @Script("q6941e")
    public static void q6941e(ScriptManager sm) {
        // Quest 6941 - Priest's First Story (END)
        sm.forceCompleteQuest(6941);
        sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
    }

    @Script("q6942s")
    public static void q6942s(ScriptManager sm) {
        // Quest 6942 - Priest's Second Truth (START)
        sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");

        if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
            sm.forceStartQuest(6942);
            sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
            sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
            sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
        }
    }

    @Script("q6942e")
    public static void q6942e(ScriptManager sm) {
        // Quest 6942 - Priest's Second Truth (END)
        sm.forceCompleteQuest(6942);
        sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
    }

    @Script("q6943s")
    public static void q6943s(ScriptManager sm) {
        // Quest 6943 - Priest's Third Story (START)
        sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");

        if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
            sm.forceStartQuest(6943);
            sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
            sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
            sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
            sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
            sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
        } else {
            sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
        }
    }

    @Script("q6943e")
    public static void q6943e(ScriptManager sm) {
        // Quest 6943 - Priest's Third Story (END)
        sm.forceCompleteQuest(6943);
        sm.sayOk("Talk to me when you're ready.");
    }

    @Script("q6944s")
    public static void q6944s(ScriptManager sm) {
        // Quest 6944 - Attributes of a Hero (START)
        sm.sayNext("I will now give you the last task required to complete the 4th job advancement.");

        if (sm.askYesNo("It is your mission to acquire two items that I assign to you. I want #b#t4031517##k and #b#t4031518##k.")) {
            sm.forceStartQuest(6944);
            sm.sayNext("How you will acquire these items, I'll leave that up to you. If you want to acquire by fully utilizing your courage and physical capabilities, then you should get them through #bManon and Griffey#k. If you want to acquire them using brains and wisdom, then head to Leafre and see #b#p2081000##k.");
        } else {
            sm.sayOk("What is there for you to fear? You are walking the path of Pirate greatness, and you don't wish to encounter hardship?");
        }
    }

    @Script("q6944e")
    public static void q6944e(ScriptManager sm) {
        // Quest 6944 - Attributes of a Hero (END)
        final int HEROIC_PENTAGON = 4031860;
        final int HEROIC_STAR = 4031861;

        if (!sm.hasItem(HEROIC_PENTAGON, 1) || !sm.hasItem(HEROIC_STAR, 1)) {
            sm.sayOk("I don't think you have acquired #b#t4031517##k and #b#t4031518##k, yet.");
            return;
        }

        sm.removeItem(HEROIC_PENTAGON, 1);
        sm.removeItem(HEROIC_STAR, 1);
        sm.forceCompleteQuest(6944);
        sm.addExp(50000);
        sm.sayNext("You have proven your worth as a person that can be called a hero.");
        sm.sayOk("What you'll need to do now is to keep walking the path of great Pirates. Talk to me when you are ready to make the job advancement.");
    }
}
