package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Beginner Training Quests (2128-2143)
 * Auto-start and auto-complete quests for new players
 */
public final class TrainingQuest extends ScriptHandler {

    // WARRIOR TRAINING QUESTS (2128-2130) ----------------------------------------------------------------

    @Script("q2128s")
    public static void q2128s(ScriptManager sm) {
        // Quest 2128 - Beginner Warrior's First Training Session (START)
        // Dances with Balrog (1022000) - Perion
        sm.sayNext("Little Warrior brother. Just becoming a warrior doesn't mean that you're completely strong. You still need to be trained.. I think this #p1022000# training is useful. What do you think? Do you want to go for a training session?");

        if (sm.askYesNo("Are you ready to begin your training?")) {
            sm.forceStartQuest(2128);
            sm.sayNext("You are far too weak to fight against strong monsters. You had better learn how to hunt by first hunting #o0130100#s. #o0130100#s live around #m102000000#. It won't be hard to hunt them. Please let me know when you have defeated twenty #o0130100#s.");
        } else {
            sm.sayOk("You're not ready yet to be trained.");
        }
    }

    @Script("q2128e")
    public static void q2128e(ScriptManager sm) {
        // Quest 2128 - Beginner Warrior's First Training Session (END)
        final int STUMP = 130100;
sm.sayNext("You got rid of twenty #o0130100#. Good. You can't be satisfied with this. This is the first step to be a Warrior.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(590);
            sm.addItem(2000000, 30); // Red Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2128);
            sm.sayOk("When I think you need more training, I'll call you. Until we meet again...keep training hard!");
        }
    }

    @Script("q2129s")
    public static void q2129s(ScriptManager sm) {
        // Quest 2129 - Beginner Warrior's Second Training Session (START)
        // Dances with Balrog (1022000) - Perion
        sm.sayNext("Young Warrior! You've improved. But, you not yet ready for the dangers of the Maple World. You need more basic training. This #p1022000# will help you train. Do you want to have a training session?");

        if (sm.askYesNo("Are you ready for more training?")) {
            sm.forceStartQuest(2129);
            sm.sayNext("Then hunt #rfifty #o0130100##k. You've hunted these monsters before, so it won't be that hard. After hunting all them, don't forget to report back to me.");
        } else {
            sm.sayOk("It's good for you to train yourself if you can.");
        }
    }

    @Script("q2129e")
    public static void q2129e(ScriptManager sm) {
        // Quest 2129 - Beginner Warrior's Second Training Session (END)
        final int STUMP = 130100;
sm.sayNext("You took out 50 #o0130100#s! Wonderful. I, #p1022000#, am happy to see you improve. But, don't be satisfied with this. You're still fairly weak.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(610);
            sm.addItem(2000001, 30); // Blue Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2129);
            sm.sayOk("When I think you need more training, I'll call you. Until we meet again...keep training hard!");
        }
    }

    @Script("q2130s")
    public static void q2130s(ScriptManager sm) {
        // Quest 2130 - Beginner Warrior's Third Training Session (START)
        // Dances with Balrog (1022000) - Perion
        sm.sayNext("Young Warrior! I'm happy to see you improve. I can see a strong Warrior's gaze in your eyes. Still... You're short of something. Would you like to go for another training?");

        if (sm.askYesNo("Are you ready for the final basic training?")) {
            sm.forceStartQuest(2130);
            sm.sayNext("Good choice. Your mission is to hunt #r80 #o0130100#s#k. It's quite a big number. If you are a Warrior, you need to have patience and endurance. Do not stop until you have achieved victory!");
        } else {
            sm.sayOk("If you can train yourself, I won't ask you.");
        }
    }

    @Script("q2130e")
    public static void q2130e(ScriptManager sm) {
        // Quest 2130 - Beginner Warrior's Third Training Session (END)
        final int STUMP = 130100;
sm.sayNext("You came back after hunting 80 #o0130100#. I pay homage to you for finishing this hard training. Impressive!");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(645);
            sm.addItem(2000002, 30); // White Potion
            sm.addItem(2000003, 50); // Orange Potion
            sm.forceCompleteQuest(2130);
            sm.sayOk("When I think you need more training, I'll call you. Until we meet again...keep training hard!");
        }
    }

    // MAGICIAN TRAINING QUESTS (2132-2135) ---------------------------------------------------------------

    @Script("q2132s")
    public static void q2132s(ScriptManager sm) {
        // Quest 2132 - Beginner Magician's First Training Session (START)
        // Grendel the Really Old (1032001) - Ellinia
        sm.sayNext("Hey, young Magician. You have many problems as a beginner. You don't know much even though you became a Magician. I guess you find it hard to hunt. Am I right? Do you mind if #p1032001# helps you with training?");

        if (sm.askYesNo("Will you train?")) {
            sm.forceStartQuest(2132);
            sm.sayNext("You're too weak to compete with strong monsters. First, you had better learn how to hunt. If you hunt eight #r#o0210100#s#k, you'll master basic skills. #o0210100#s live around #m101000000#.");
        } else {
            sm.sayOk("You're not ready to be trained.");
        }
    }

    @Script("q2132e")
    public static void q2132e(ScriptManager sm) {
        // Quest 2132 - Beginner Magician's First Training Session (END)
        final int SNAIL = 210100;
sm.sayNext("Oh~ You killed eight #o0210100#. Much faster than I expected. Wonderful. Fantastic.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(290);
            sm.addItem(2000000, 30); // Red Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2132);
            sm.sayOk("But, you can't be happy with this. Until we meet again...keep training hard!");
        }
    }

    @Script("q2133s")
    public static void q2133s(ScriptManager sm) {
        // Quest 2133 - Beginner Magician's Second Training Session (START)
        // Grendel the Really Old (1032001) - Ellinia
        sm.sayNext("You have improved a lot. You wouldn't notice it but I, #p1032001#, can see it. Still, you are not good enough. I, #p1032001#, will help you out little bit more. What do you think? Do you want to be trained a bit more?");

        if (sm.askYesNo("Will you continue your training?")) {
            sm.forceStartQuest(2133);
            sm.sayNext("Then, eliminate #r20 #o0210100##k. What is important is basic training. You have already eliminated 8 of them before so it shouldn't be hard this time.");
        } else {
            sm.sayOk("It's good if you can train by yourself.");
        }
    }

    @Script("q2133e")
    public static void q2133e(ScriptManager sm) {
        // Quest 2133 - Beginner Magician's Second Training Session (END)
        final int SNAIL = 210100;
sm.sayNext("Did you clear 20 #o0210100#? Oh, good. You made it. Wonderful. Keep your pace and you'll get better soon.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(323);
            sm.addItem(2000001, 30); // Blue Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2133);
            sm.sayOk("You can't be a good Magician if you're satisfied with this. Until we meet again...keep training hard!");
        }
    }

    @Script("q2134s")
    public static void q2134s(ScriptManager sm) {
        // Quest 2134 - Beginner Magician's Third Training Session (START)
        // Grendel the Really Old (1032001) - Ellinia
        sm.sayNext("You finished your second training session! You're improved. If you keep training yourself, you can be the best. Now, you're still a beginner and you'll need #p1032001#'s help.");

        if (sm.askYesNo("Will you continue your training?")) {
            sm.forceStartQuest(2134);
            sm.sayNext("Your mission is to kill #r35 #o0210100##k. Too many? It's not a small number for sure. But, you can handle it. If you want to be stronger, you must learn to train hard. Go and eliminate #o0210100#. I wish you luck!");
        } else {
            sm.sayOk("It's the best if you can train by yourself.");
        }
    }

    @Script("q2134e")
    public static void q2134e(ScriptManager sm) {
        // Quest 2134 - Beginner Magician's Third Training Session (END)
        final int SNAIL = 210100;
sm.sayNext("You killed 35 #o0210100#s! I'm mildly astonished by your skill thus far. See me if you want another traning session!");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(345);
            sm.addItem(2000002, 30); // White Potion
            sm.addItem(2000003, 50); // Orange Potion
            sm.forceCompleteQuest(2134);
            sm.sayOk("You shouldn't be satisfied with this. Until we meet again...keep training hard!");
        }
    }

    @Script("q2135s")
    public static void q2135s(ScriptManager sm) {
        // Quest 2135 - Beginner Magician's Last Training Session (START)
        // Grendel the Really Old (1032001) - Ellinia
        sm.sayNext("Was your training helpful? You are quite ready to be a real Magician. Now, let me test your skills. Don't be afraid. It won't be that hard...or will it? Are you ready?");

        if (sm.askYesNo("Are you ready for the final test?")) {
            sm.forceStartQuest(2135);
            sm.sayNext("Eliminate #r10 #o1110101##k found in #m101010000# and #m101010100#. They may look cute, but trust me, they're much stronger monsters than #o0210100#. STAY ALERT! Now go and eliminate 10 #o1110101#!");
        } else {
            sm.sayOk("If you train by yourself, I won't ask you any more.");
        }
    }

    @Script("q2135e")
    public static void q2135e(ScriptManager sm) {
        // Quest 2135 - Beginner Magician's Last Training Session (END)
        final int OCTOPUS = 1110101;
sm.sayNext("You succeeded in eliminating 10 #o1110101#. Fabulous! Magicians rarely improve as fast as you do. Now, you learned everything that you need to be a real Magician.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(470);
            sm.addItem(2000002, 50); // White Potion
            sm.addItem(2000003, 50); // Orange Potion
            sm.addItem(2030000, 10); // Mana Elixir
            sm.forceCompleteQuest(2135);
            sm.sayOk("You've improved, but you can't be satisfied with this level of power. The road to becoming an Arch Mage is a long one. Although it'll be sometimes hard and tough, you can achieve many things if you're willing to make the effort. That's who Magicians are. Our power comes from within, and can overcome anything, so long as we are willing to push forward! I hope to see you again!");
        }
    }

    // BOWMAN TRAINING QUESTS (2136-2138) -----------------------------------------------------------------

    @Script("q2136s")
    public static void q2136s(ScriptManager sm) {
        // Quest 2136 - Beginner Bowman's First Training Session (START)
        // Athena Pierce (1012100) - Henesys
        sm.sayNext("Young Bowman! You are no longer a beginner. I guess you still have many difficulties. It's hard to fight by yourself, isn't it? I can show you some guidelines for your training. What do you say? Would you like to go for it?");

        if (sm.askYesNo("I want to be trained.")) {
            sm.forceStartQuest(2136);
            sm.sayNext("You're not familiar with controlling a bow. It's better to hunt #o0210100# and learn how to hunt. Please go and hunt #rsixteen #o0210100##k.#o0210100# live near #m100000000# town. It's easy to find them.");
        } else {
            sm.sayOk("You're not ready to be trained.");
        }
    }

    @Script("q2136e")
    public static void q2136e(ScriptManager sm) {
        // Quest 2136 - Beginner Bowman's First Training Session (END)
        final int SNAIL = 210100;
sm.sayNext("You killed 16 #o0210100#. Good. You can't be stronger if you're happy with this. It is just first step as a Bowman.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(590);
            sm.addItem(2000000, 30); // Red Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2136);
            sm.sayOk("I'll call you when you need another training session. Until we meet again...keep training hard!");
        }
    }

    @Script("q2137s")
    public static void q2137s(ScriptManager sm) {
        // Quest 2137 - Beginner Bowman's Second Training Session (START)
        // Athena Pierce (1012100) - Henesys
        sm.sayNext("You've improved. That's not enough though. I believe you need more training. What do you say? If you want, #p1012100# can help you out with training.");

        if (sm.askYesNo("Will you continue your training?")) {
            sm.forceStartQuest(2137);
            sm.sayNext("Then hunt #r40 #o0210100##k.You've hunted them before so it won't be hard. After hunting all #o0210100#, come back here to tell me what you did.");
        } else {
            sm.sayOk("It's good to train by yourself.");
        }
    }

    @Script("q2137e")
    public static void q2137e(ScriptManager sm) {
        // Quest 2137 - Beginner Bowman's Second Training Session (END)
        final int SNAIL = 210100;
sm.sayNext("You've eliminated forty #o0210100#. Fantastic. I'm happy with your enhancement. Please don't be satisfied with this. You're still weak compared to many monsters in Maple World.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(610);
            sm.addItem(2000001, 30); // Blue Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2137);
            sm.sayOk("I'll call you when you need more training. Until we meet again...keep training hard!");
        }
    }

    @Script("q2138s")
    public static void q2138s(ScriptManager sm) {
        // Quest 2138 - Beginner Bowman's Third Training Session (START)
        // Athena Pierce (1012100) - Henesys
        sm.sayNext("Bowman with unskilled arms. I'm impressed by your enhancement.Your eyes already belong to another strong Bowman. But, would you like to go for one more training for further improvement?");

        if (sm.askYesNo("Are you ready for the final basic training?")) {
            sm.forceStartQuest(2138);
            sm.sayNext("Good choice. Your mission is to hunt #r65 #o0210100##k. 65... It's quite many. You are calm and have a strong will so that I believe you can do it. Now, let me see your strength.");
        } else {
            sm.sayOk("If you train by yourself, I won't ask you any more.");
        }
    }

    @Script("q2138e")
    public static void q2138e(ScriptManager sm) {
        // Quest 2138 - Beginner Bowman's Third Training Session (END)
        final int SNAIL = 210100;
sm.sayNext("You killed 65#o0210100#. I pay homage to you for finishing this hard training.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(645);
            sm.addItem(2000002, 30); // White Potion
            sm.addItem(2000003, 50); // Orange Potion
            sm.forceCompleteQuest(2138);
            sm.sayOk("I'll call you when you need another training. Keep training yourself.");
        }
    }

    // THIEF TRAINING QUESTS (2140-2143) ------------------------------------------------------------------

    @Script("q2140s")
    public static void q2140s(ScriptManager sm) {
        // Quest 2140 - Beginner Thief's First Training Session (START)
        // Dark Lord (1052001) - Kerning City
        sm.sayNext("Being a Thief doesn't mean you are strong. You're just out of the beginner's level. It's hard for you to fight. You look even worse. Would you like to go for a special training by #p1052001#?");

        if (sm.askYesNo("I want to be trained.")) {
            sm.forceStartQuest(2140);
            sm.sayNext("I'm sure that you don't know how to use weapons. It's better to hunt #o130100#. You can do anything after you learn how to hunt. Go and hunt #r20 #o130100#s#k and let me know when you've finished. You can do it easily as #o130100#s are all around Kerning City.");
        } else {
            sm.sayOk("You're not ready to be trained.");
        }
    }

    @Script("q2140e")
    public static void q2140e(ScriptManager sm) {
        // Quest 2140 - Beginner Thief's First Training Session (END)
        final int STUMP = 130100;
sm.sayNext("You came back with 20 #o130100#s. It's a piece of cake. Don't be happy with this. It is just first step.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(590);
            sm.addItem(2000000, 30); // Red Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2140);
            sm.sayOk("I'll call you when you need another training session. Until we meet again...keep training hard!");
        }
    }

    @Script("q2141s")
    public static void q2141s(ScriptManager sm) {
        // Quest 2141 - Beginner Thief's Second Training Session (START)
        // Dark Lord (1052001) - Kerning City
        sm.sayNext("Hmm. You seem to be improved. That doesn't make a big difference though. You have a long way to go. You should be trained by #p1052001#. What do you think? Would you like to go for a training?");

        if (sm.askYesNo("Will you continue your training?")) {
            sm.forceStartQuest(2141);
            sm.sayNext("Then hunt #r50 #o130100##k. You have hunted these monsters before. It won't be hard. Keep hunting #o130100# and make sure you master a basic training. Then let me know.");
        } else {
            sm.sayOk("If you train by yourself, do it. I wonder how long you can do that.");
        }
    }

    @Script("q2141e")
    public static void q2141e(ScriptManager sm) {
        // Quest 2141 - Beginner Thief's Second Training Session (END)
        final int STUMP = 130100;
sm.sayNext("You killed 50 #o130100#s. That's not bad. You just began and that's good. Hmm? What is that face? Do you believe that you did something great? Don't be proud of yourself. You have a long way to go.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(610);
            sm.addItem(2000001, 30); // Blue Potion
            sm.addItem(2000003, 30); // Orange Potion
            sm.forceCompleteQuest(2141);
            sm.sayOk("I'll call you when you need another training session. Until we meet again...keep training hard!");
        }
    }

    @Script("q2142s")
    public static void q2142s(ScriptManager sm) {
        // Quest 2142 - Beginner Thief's Third Training Session (START)
        // Dark Lord (1052001) - Kerning City
        sm.sayNext("Hmm...you have definitely improved. That's good. We might have a real Thief here... But you're not good enough. Take another training session for further improvement!");

        if (sm.askYesNo("Are you ready for more training?")) {
            sm.forceStartQuest(2142);
            sm.sayNext("Good. Your mission is to hunt #r80 #o130100#s#k. It's a lot, but If you are a Thief, you should fight with concentration and calmness. A cool head and calm heart will always win in the end.");
        } else {
            sm.sayOk("If you train by yourself, I won't ask you any more.");
        }
    }

    @Script("q2142e")
    public static void q2142e(ScriptManager sm) {
        // Quest 2142 - Beginner Thief's Third Training Session (END)
        final int STUMP = 130100;
sm.sayNext("You hunted 80 #o130100#s. Now I have to say you're quite good.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(645);
            sm.addItem(2000002, 30); // White Potion
            sm.addItem(2000003, 50); // Orange Potion
            sm.forceCompleteQuest(2142);
            sm.sayOk("I'll call you when you need another training session. Until we meet again...keep training hard!");
        }
    }

    @Script("q2143s")
    public static void q2143s(ScriptManager sm) {
        // Quest 2143 - Beginner Thief's Last Training Session (START)
        // Dark Lord (1052001) - Kerning City
        sm.sayNext("Beginner Thief. You've been doing a good job till now. I can see some of your weak points. But you're a Thief now. Let me test you.");

        if (sm.askYesNo("Are you ready for the final test?")) {
            sm.forceStartQuest(2143);
            sm.sayNext("It's simple:Hunt #rten #o1120100##k. They're much stronger than #o0130100# which you've dealt with. If you've been doing well with training, it's not that hard. Now go and get them!");
        } else {
            sm.sayOk("Are you afraid of the test? Don't be! I won't give you any test that you can't pass!");
        }
    }

    @Script("q2143e")
    public static void q2143e(ScriptManager sm) {
        // Quest 2143 - Beginner Thief's Last Training Session (END)
        final int OCTOPUS = 1120100;
sm.sayNext("You succeeded to eliminate ten #o1120100#. Hahaha. Quite good. You were beginner who can't do anything. Now you made it thanks to Dark Lord. Haha. I'm really happy.");

        if (sm.askYesNo("Are you ready to complete your training?")) {
            sm.addExp(840);
            sm.addItem(2000002, 50); // White Potion
            sm.addItem(2000003, 50); // Orange Potion
            sm.addItem(2030000, 10); // Mana Elixir
            sm.forceCompleteQuest(2143);
            sm.sayOk("But this is only beginning as a Thief. There is a long way to be a real Thief. The way of Thief...It is tough. If you make efforts, you can make it. Be calm and concentrate on your job.");
        }
    }
}
