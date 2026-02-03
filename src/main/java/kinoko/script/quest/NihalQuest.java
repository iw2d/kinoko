package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Nihal Desert Region Quests
 * Area 44 - Ariant, Magatia, Sand Bandits, Alcadno/Zenumist Alchemist storylines
 */
public final class NihalQuest extends ScriptHandler {


    // QUEST 3900: Learning the Culture of Ariant ========================================
    @Script("q3900s")
    public static void q3900s(ScriptManager sm) {
        // NPC 2101005 - Byron (Ariant)
        sm.sayNext("Hey, you are not from #m260000000#. Huh? How did I know? Come on, it's obvious! You don't look like someone from the desert, no offense. I'm sure you've noticed by now that the people of #m260000000# don't say a word to you...");

        sm.sayBoth("Nah, they are just extra careful around outsiders. These residents, having spent all their lives in the desert, don't really open up to strangers. It'll take some time for you to get adjusted to this place.");

        if (!sm.askYesNo("All you have to do is to become a citizen of #m260000000#. I'll tell you how. Are you sure you want to become a citizen?")) {
            sm.sayOk("Hmmm. Afraid to embrace a new culture? I understand it's hard to adapt, but just try it!");
            return;
        }

        sm.sayOk("It's easy! In the center of town, near #m260000300#, you'll find a huge oasis. Drink water from there, and the residents'll accept you.");
        sm.forceStartQuest(3900);
    }

    @Script("q3900e")
    public static void q3900e(ScriptManager sm) {
        // NPC 2101005 - Byron
        sm.sayNext("Hey, you drank the water! It's very important for the people of #m260000000# that you drink the water from the oasis. It means you're willing to be one of them, a rite of passage, so to speak.");

        sm.sayOk("Now you can start conversations with the people here. Be careful, though. The general mood around #m260000000# isn't very good these days. People are angered over the #bpowerless Sultan and the ever-greedy Queen#k.");

        sm.addExp(300);
        sm.forceCompleteQuest(3900);
    }

    // QUEST 3901: Tigun the Guard at the Palace ========================================
    @Script("q3901s")
    public static void q3901s(ScriptManager sm) {
        // NPC 2101004 - Tigun (Ariant Palace entrance)
        sm.sayNext("Hey, what are you doing snooping around the palace? This is where the Sultan of #m260000000# resides! What? You want to enter? Why would I let a traveler like you do that? No way will I let you in, unless…");

        if (!sm.askYesNo("If you really, really want to enter the palace, then there might be a way. You need to show your allegiance to the Sultan. How, you ask? That's easy. Just hand me #b2,000 mesos#k. As your way of paying respect to Sultan and #m260000000#, of course.")) {
            sm.sayOk("What? You don't want to pay 2,000 mesos? Then scram before I arrest you!");
            return;
        }

        if (!sm.canAddMoney(-2000)) {
            sm.sayOk("Hey, you're short on mesos! If you want to enter the palace, the least you can do is give a good faith offering of #b2,000 mesos#k.");
            return;
        }

        sm.addMoney(-2000);
        sm.addItem(4031582, 1); // Palace Entry Pass
        sm.sayOk("Alright, alright. I can see now that you're someone who pays allegiance to the Sultan, so I'll give you this #t4031582#. Be thankful I'm feeling generous today.");
        sm.forceCompleteQuest(3901);
    }

    // QUEST 3902: Queen's Make-up Kit ========================================
    @Script("q3902s")
    public static void q3902s(ScriptManager sm) {
        // NPC 2101007 - Queen (Ariant Palace)
        sm.sayNext("I can't stand the desert! This is not the kind of place for a fairy like me! How did I, #p2101007#, end up here... Dang it, my face's breaking out again! I need a more expensive make-up kit. You! Ready to receive an order from your queen?");

        if (!sm.askYesNo("Haha, of course, of course you are. This desert is full of useless sand, but there's fine, smooth sand here and there that can be used as exquisite make-up. It's called #b#t4000332##k! I hear it's perfect for skin treatment! Get #b20#k for me.")) {
            sm.sayOk("You won't do it? How rude. Guard! Take this vagabond straight to jail!");
            return;
        }

        sm.sayOk("You can get #b#t4000332#s#k from #r#o4230600#s#k. They're quite strong, but you'll defeat them for your glorious queen #p2101007#.");
        sm.forceStartQuest(3902);
    }

    @Script("q3902e")
    public static void q3902e(ScriptManager sm) {
        // NPC 2101007 - Queen
        if (!sm.hasItem(4000332, 20)) {
            sm.sayOk("Where's my #b20 #t4000332s##k? My skin needs it. Speed it up! Seriously, outsiders…");
            return;
        }

        sm.sayNext("Oh, ho, ho! So this is #t4000332#. It's shiny like gold, perfect for make-up. And it's so soft... Wow, this should be great for my skin.");
        sm.sayOk("What are you doing standing there? If you've done your task, then leave the palace immediately! You should be thankful that an outsider like you was even allowed in. You don't expect a reward for your work, do you?");

        sm.removeItem(4000332, 20);
        sm.addExp(15000);
        sm.forceCompleteQuest(3902);
    }

    // QUEST 3903: Queen's Tea 1 ========================================
    @Script("q3903s")
    public static void q3903s(ScriptManager sm) {
        // NPC 2101007 - Queen
        sm.sayNext("Ick, this is gross! Tea from barbarians. They may consider this drivel the finest around, but it does me no good. It's like drinking the very finest vinegar. A fairy with sensitive taste buds like me can't possibly drink tea made out of #t4000331#. You! Get #b#t4031577##k for me. NOW!");

        if (!sm.askYesNo("You can't say no to a queen. Hahaha! #b#t4031577##k can be purchased from #b#p2012012##k of #b#m200000000##k.They might not want to sell it to you, but...that's your problem. I want to drink tea right this instant, so go to #m200000000# immediately.")) {
            sm.sayOk("What? saying no to the queen? How idiotic... Don't think you'll last long in #m260000000# with that kind of attitude!");
            return;
        }

        sm.addExp(1000);
        sm.forceStartQuest(3903);
    }

    @Script("q3903e")
    public static void q3903e(ScriptManager sm) {
        // NPC 2012012 - Nella (Orbis - Helios Tower)
        sm.sayOk("What is it? Huh? Do I know a fairy named #p2101007#?");
    }

    // QUEST 3904: Queen's Tea 2 ========================================
    @Script("q3904s")
    public static void q3904s(ScriptManager sm) {
        // NPC 2012012 - Nella
        sm.sayNext("What can I do for you? What? You want a #t4031577#? I don't sell that to outsiders... What? A fairy named #p2101007# from #m260000000# wants it? #p2101007#...? But I've never heard that name before…");

        if (!sm.askYesNo("You look like you really need it. But Fairy Tea Leaves are hard to get, even for fairies. Ah, I know. I'll give you a Fairy Tea Leaf if you do me a favor! Just slay #r30#k #r#o5200000#s#k around #m200000000#...")) {
            sm.sayOk("I can't give you a #t4031577# unless you really, really need it.");
            return;
        }

        sm.sayOk("Actually, the reason I'm not selling Fairy Tea Leaves to outsiders is that it's really hard to get these days. The Fairy Tea plant is very sensitive and needs to be grown in a quiet place, but that's hard to do because of the #o5200000#s.");
        sm.forceStartQuest(3904);
    }

    @Script("q3904e")
    public static void q3904e(ScriptManager sm) {
        // NPC 2012012 - Nella
        sm.sayNext("Wow, you eliminated 30 #o5200000#s! Thank you so much! Now hold on one second while I bring the #t4031577# to you.");
        sm.addExp(1000);
        sm.forceCompleteQuest(3904);
    }

    // QUEST 3905: Queen's Tea 3 ========================================
    @Script("q3905s")
    public static void q3905s(ScriptManager sm) {
        // NPC 2012012 - Nella
        if (!sm.askYesNo("Now that you've taken care of the #o5200000#s, I'll give you a #t4031577#. It's a precious, rare item, so handle it with care.")) {
            sm.sayOk("You need some time before returning to #m260000000#. I'll hold on to the #t4031577# for now, then...");
            return;
        }

        sm.sayOk("A fairy called #p2101007# living in #m200000000# is the one who requested this? Odd. I know almost every fairy around, but I've never heard of her before.");
        sm.addItem(4031577, 1); // Fairy Tea Leaf
        sm.forceStartQuest(3905);
    }

    @Script("q3905e")
    public static void q3905e(ScriptManager sm) {
        // NPC 2101007 - Queen
        if (!sm.hasItem(4031577)) {
            sm.sayOk("What? You still haven't gotten me a #b#t4031577##k yet? I knew it... Commoners are incompetent.");
            return;
        }

        sm.sayNext("Did you bring the #t4031577#? Ho, this is a #t4031577#, indeed. The scent of this leaf is amazing compared to #t4000331#. This should be sufficient.");
        sm.sayOk("Not bad for a commoner. That's not saying much, since you're not a fairy, after all...");

        sm.removeItem(4031577);
        sm.addExp(2600);
        sm.forceCompleteQuest(3905);
    }

    // QUEST 3906: Jiyur's Sister 1 ========================================
    @Script("q3906s")
    public static void q3906s(ScriptManager sm) {
        // NPC 2101001 - Jiyur (Ariant)
        sm.sayNext("Excuse me. Um, do you have access to the palace? See, the thing is, my sister's in there, and I was wondering if you could look for her for me.");

        sm.sayBoth("No way! My sister didn't want to go! The guards came and took her away...even though she said she didn't want to go... I think the queen took her after hearing that my sister's a good storyteller. If you...go into the palace, please just tell her I miss her.");

        if (!sm.askYesNo("Thank you so much. My sister knows a lot of stories. She also has a beautiful voice. The stories she told were so much fun to listen to... Please find her and tell her that #p2101001# misses her.")) {
            sm.sayOk("You…you don't want to? I see... I'm sorry I asked for something that's so difficult to do.");
            return;
        }

        sm.addExp(450);
        sm.forceStartQuest(3906);
    }

    @Script("q3906e")
    public static void q3906e(ScriptManager sm) {
        // NPC 2101008 - Schegerazade (Ariant Palace)
        sm.sayOk("Hello, traveler. Did you see my sister? What? This is a letter from her?! I was so worried, because I heard that the queen is quite scary... I am so glad she's doing all right!");
    }

    // QUEST 3907: A Letter From the Sister ========================================
    @Script("q3907s")
    public static void q3907s(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        sm.sayNext("Hello, traveler. What kind of a story would you like to hear from me, #p2101008#?");
        sm.sayBoth("#p2101001#!? You know #p2101001#?! How is he?! I hope he's doing all right without me... So, #p2101001# sent you. Thank you. There's no way for me to check up on #p2101001# from the palace. I was so worried.");

        if (!sm.askYesNo("I see... It's only natural, since he can't contact his only sister. Could you tell #p2101001# that I'm doing all right in here?")) {
            sm.sayOk("You must be really busy... But there's nothing I can do from here. At least I know how #p2101001#'s doing now, but...");
            return;
        }

        sm.sayOk("Thank you so much. Here's a letter I wrote. Please give it to #b#p2101001. I can't see him face to face, but I want to let him know that I am okay and that I miss him terribly.");
        sm.addItem(4031589, 1); // Letter from Schegerazade
        sm.forceStartQuest(3907);
    }

    @Script("q3907e")
    public static void q3907e(ScriptManager sm) {
        // NPC 2101001 - Jiyur
        if (!sm.hasItem(4031589)) {
            sm.sayOk("You didn't meet with my sister yet? She's in the palace.");
            return;
        }

        sm.sayNext("Hello, traveler. Did you see my sister? What? This is a letter from her?! I was so worried, because I heard that the queen is quite scary... I am so glad she's doing all right!");
        sm.sayOk("Can you please wait while I read her letter? Thanks.");

        sm.removeItem(4031589);
        sm.addExp(450);
        sm.forceCompleteQuest(3907);
    }

    // QUEST 3908: A Present for His Sister ========================================
    @Script("q3908s")
    public static void q3908s(ScriptManager sm) {
        // NPC 2101001 - Jiyur
        sm.sayNext("I am so happy that you found my sister and told her how much I miss her. You even brought a letter from her! Can you help me with one more thing?");

        if (!sm.askYesNo("Reading this letter, I realized my sister must be constantly telling stories, so her throat must ache! I want to make her some #t4000331# tea, but #o2100104# s are too strong for me to fight…")) {
            sm.sayOk("Ahhh... you must be tired from all that traveling. Sorry for making these demands.");
            return;
        }

        sm.sayOk("But you can handle #r#o2100104#s#k easily, right? Please get #b20 #t4000331#s#k for my sister. Please...");
        sm.forceStartQuest(3908);
    }

    @Script("q3908e")
    public static void q3908e(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        if (!sm.hasItem(4000331, 20)) {
            sm.sayOk("You're the one came on #p2101001#'s behalf last time. What brings you back here? If you're here to meet the queen, please be careful.");
            return;
        }

        sm.sayNext("You're the one came on #p2101001#'s behalf last time. What brings you back here? Hmm? #t4000331#s, for me to make tea? You're here on #p2101001#'s behalf again, aren't you?");
        sm.sayOk("I was already so grateful to you last time, but wow... thank you so much. This will really soothe my throat.");

        sm.removeItem(4000331, 20);
        sm.addItem(4010007, 5); // Lidium Ore
        sm.addExp(4900);
        sm.forceCompleteQuest(3908);
    }

    // QUEST 3909: Dancer's Ringing ========================================
    @Script("q3909s")
    public static void q3909s(ScriptManager sm) {
        // NPC 2101000 - Sirin (Ariant)
        sm.sayNext("Ahh! Something's missing. Even with all these moves, something's not right. Hey, since you aren't from around here, you may have a different point of view. What's missing from my dance?");

        sm.sayBoth("Uh, can't you tell just by looking? I, #p2101000#, am the best dancer in #m260000000#. Unfortunately, I still have yet to dance inside the palace. Unless my moves are more radical, the queen won't be interested. Want to help me?");

        if (!sm.askYesNo("Hah, I knew you'd fall in love with my moves. Come to think of it, if I put some bells on my dress while I dance, that'll really look special. If you can get me #b20 #t4000328#s#k from #r#o2100105#s#k, then I promise I'll perform some better moves. You can count on it.")) {
            sm.sayOk("Pssssh, whatever. Are you really going to throw away a golden opportunity to see an amazing new dance?");
            return;
        }

        sm.forceStartQuest(3909);
    }

    @Script("q3909e")
    public static void q3909e(ScriptManager sm) {
        // NPC 2101000 - Sirin
        if (!sm.hasItem(4000328, 20)) {
            sm.sayOk("Hmmm. You didn't get #b20 #t4000328#s#k yet! You can find #t4000328#s off of #r#o2100105#s#k, so all you need to do is defeat them.");
            return;
        }

        sm.sayNext("Hey! You brought the #t4000328#s! Alright, I'll put these on the dress and once I dance around with all this festive clanging, the dance will seem that much more dynamic. Thanks!");
        sm.sayOk("Hmm? If I ring these bells, #o2100105#s will pop out? Uh… who told you that? They live in the desert. Why would they slide all the way here?");

        sm.removeItem(4000328, 20);
        sm.addExp(6000);
        sm.forceCompleteQuest(3909);
    }

    // QUEST 3910: Sword Dance 1 ========================================
    @Script("q3910s")
    public static void q3910s(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("Hey, you're the traveler that got me #t4000328#s from the #o2100105#s, right? Thanks to you, my dance practices have gotten much better lately. Once people see my new dance with the bells ringing, even the queen will have to love it!");

        sm.sayBoth("But I feel like this dance needs something more. Even with the perfect moves and the perfect dress... I really think it's the props, or lack thereof. The sword I use as a prop is so dull that when I do my patented Sword Dance, it doesn't look very dynamic. Will you help me out?");

        if (!sm.askYesNo("Haha, I knew you'd help. You are so in love with my moves! The only person in #m260000000# that makes swords is #b#p2100001##k, so ask him if he can make a dancer sword for me.")) {
            sm.sayOk("What? You aren't going to help? Come on! All I need is a nice sword, and my dance will be that much better. You really don't want to see #p2101000#'s spectacular Sword Dance?");
            return;
        }

        sm.sayOk("I've been asking for a while, but he always says no... But if a traveler like you asks for the dancer sword, maybe he'll relent...right? Right?");
        sm.addExp(100);
        sm.forceStartQuest(3910);
    }

    // QUEST 3911: Making the Fancy Sword ========================================
    @Script("q3911s")
    public static void q3911s(ScriptManager sm) {
        // NPC 2100001 - Muhamad (Ariant)
        sm.sayNext("What do you want me to make? A sword for dancers? I bet #p2101000# asked you, right? I am sorry, but I can't help. The taxes on dancer's swords have gone up, so I won't make any. But...");

        if (!sm.askYesNo("Making the sword itself wouldn't be a tough task... All right, if you can get all the materials used to ornament the sword, I'll make it for you. I've refused #p2101000# all this time, but I'll make one if you bring the materials, okay?")) {
            sm.sayOk("Hmm, #p2101000# would be disappointed...");
            return;
        }

        sm.sayNext("Woah, you must be confident. Then get me #b4 #t4031568#s#k and #b30 #t4000335#s#k to decorate the handle of the sword. Of course, you'll also have to pay #b5,000 mesos#k for the amount of iron that's going towards making the blade. Are you sure you want to do this?");
        sm.sayOk("You can get #b#t4031568#s#k from #r#o2100108##k and #b#t4000335#s#k from #r#o3100102#s#k. Once you bring them all, I'll make a fancy dancer sword worthy of #p2101000#.");
        sm.forceStartQuest(3911);
    }

    @Script("q3911e")
    public static void q3911e(ScriptManager sm) {
        // NPC 2100001 - Muhamad
        if (!sm.hasItem(4031568, 4) || !sm.hasItem(4000335, 30) || !sm.canAddMoney(-5000)) {
            sm.sayOk("You don't have all the materials yet. If you're lacking anything, the sword will not turn out perfectly. Bring #b4 #t4031568#s#k and #b30 #t4000335#s#k, along with #b5,000 mesos#k.");
            return;
        }

        sm.sayNext("So you brought all the materials needed to decorate the handle? Then show me. Woah, you did bring them all. This should be enough to make the fanciest sword in #m260000000#.");
        sm.sayOk("You don't think the sword will be made instantly, do you? It may not be used for combat, but it's not easy to make a sword, period. So please wait a bit.");

        sm.addMoney(-5000);
        sm.removeItem(4031568, 4);
        sm.removeItem(4000335, 30);
        sm.addExp(300);
        sm.forceCompleteQuest(3911);
    }

    // QUEST 3912: Deliver the Fancy Sword ========================================
    @Script("q3912s")
    public static void q3912s(ScriptManager sm) {
        // NPC 2100001 - Muhamad
        if (!sm.askYesNo("You're back. I'm just about done with the sword for #p2101000#. Take a look. It's more of an accessory than a sword, but for a dancer, this is the ultimate prop. Will you accept it?")) {
            sm.sayOk("Are you busy with something? Sirin is waiting for her sword…");
            return;
        }

        sm.sayOk("#p2101000#  is a smart young woman. She used the very first sword I made her for her very first dance. If not for the Queen's tyranny, I would've given her a sword as a gift. Please take this sword to #b#p2101000##k right now. Your kindess shall not go un-rewarded.");
        sm.addItem(4031569, 1); // Fancy Dancer Sword
        sm.forceStartQuest(3912);
    }

    @Script("q3912e")
    public static void q3912e(ScriptManager sm) {
        // NPC 2101000 - Sirin
        if (!sm.hasItem(4031569)) {
            sm.sayOk("You still haven't gone to #b#p2100001##k, yet? Find an old man who looks stubborn. His sword-making is the best in town, so ask him to make one for me.");
            return;
        }

        sm.sayNext("Did you go see #p2100001#? Did you get the fancy dancer sword for me? Wow! So these are the #t4031569#! Amazing!");
        sm.sayOk("Thank you! This will let me enter the castle with more pizzazz than anyone. I WILL enter the castle. I will be #p2101000#, the greatest dancer of #m260000000#, performing in front of the Sultan. You just wait and see!");

        sm.removeItem(4031569);
        sm.addExp(8000);
        sm.forceCompleteQuest(3912);
    }

    // QUEST 3913: Schegerazade's Fear ========================================
    @Script("q3913s")
    public static void q3913s(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        sm.sayNext("Hello, traveler... What kind of a story would you like to hear? A story of a monster trapped in the mountain of snow? A story of a beautiful town inside the ocean? If not, then a story of a race, unlike any other, deep inside the forest?");

        sm.sayBoth("My story? It's not a fun story to listen to. It's a story of a regular girl with a reputation for great storytelling, living in a regular town, who was dragged into the palace to... weave new stories while constantly trembling with fear.");

        if (!sm.askYesNo("I am afraid of the day when I've told every single story I know. It'd be nice if I were sent back home then...but would the queen really do that? I doubt it. I'm scared of what she WOULD do. So I want to get new books, so I never run out of stories. Could you help me get some?")) {
            sm.sayOk("Oh, I see. I guess there's no such thing as a courageous adventurer like in the stories…");
            return;
        }

        sm.sayNext("I am sure you've heard of the #m222020000#, considered the biggest and best library in the world. Don't you think a library like that must contain a story or two that's never been heard before?");
        sm.sayOk("This might not work, but... #b#m222020000##k is the only place I can think of. If I can find a way to get my hands on new storybooks, maybe I can buy more time. Please help me, traveler. Please borrow a book from #b#p2040052##k for me.");

        sm.addExp(500);
        sm.forceStartQuest(3913);
    }

    @Script("q3913e")
    public static void q3913e(ScriptManager sm) {
        // NPC 2040052 - Wiz the Librarian (Helios Tower Library)
        sm.sayOk("Welcome to #m222020000#. All the knowledge and records of Maple World is stored in here. Which book would you like to read...? What? You want me to help #p2101008# of #m260000000#?");
    }

    // QUEST 3914: Borrowing the Book from Wiz ========================================
    @Script("q3914s")
    public static void q3914s(ScriptManager sm) {
        // NPC 2040052 - Wiz
        sm.sayNext("#p2101008# of #m260000000#... I am amazed to find out someone knows that many stories! Even so, there must be stories out there that even #m222020000# doesn't know. Storybooks? I'll definitely lend you some.");

        if (!sm.askYesNo("The thing is, here at the Helios Library, there's one rule that must be obeyed. When you borrow a book, you must also offer a book. So, are you willing to offer a new storybook?")) {
            sm.sayOk("It's almost impossible to find a book that's not featured in #m222020000#... I understand that you're overwhelemed.");
            return;
        }

        sm.sayNext("It'll be hard to find a book that's not featured in this library... Oh, there's a better method, actually.");
        sm.sayOk("Wouldn't it be better if you write down the stories that #b#p2101008##k knows and make them into their own storybook? That would be much easier and simpler to do than finding a brand new book.");

        sm.addExp(500);
        sm.forceStartQuest(3914);
    }

    @Script("q3914e")
    public static void q3914e(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        sm.sayNext("You must have gone to #m222020000#. Did the librarian lend you a book?");
        sm.sayOk("So if one book is borrowed, it must be replaced by another... I didn't know there was such a rule in #m222020000#. Well, I've actually written down most of the stories I know. There's not much else to do when the queen doesn't need me... I just have to combine all the pages. Please come back in a bit.");
        sm.forceCompleteQuest(3914);
    }

    // QUEST 3915: Schegerazade's Storybook ========================================
    @Script("q3915s")
    public static void q3915s(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        if (!sm.askYesNo("As you requested, I've combined all the pages of the stories I know into a book. Can you please give this to #b#p2040052##k of #b#m222020000##k?")) {
            sm.sayOk("It's a long, long way from here to Helios Tower. I understand if you don't feel the urge to return there.");
            return;
        }

        sm.sayOk("Thank you so much. Without your help, I would have been trapped in here, worried sick...");
        sm.addItem(4031572, 1); // Schegerazade's Storybook
        sm.forceStartQuest(3915);
    }

    @Script("q3915e")
    public static void q3915e(ScriptManager sm) {
        // NPC 2040052 - Wiz
        if (!sm.hasItem(4031572)) {
            sm.sayOk("When will I be able to receive #b#p2101008##k's storybook? I am anxious to see what's inside....");
            return;
        }

        sm.sayNext("Wow, so you went all the way back to #m260000000# where #p2101008# is! I can tell because you're covered in sand. So. That's the storybook compiled by #p2101008#? Ohh... Ahh… Hmm... Amazing! There are so many good stories in here!");
        sm.sayOk("Now, I'll give you the book for #p2101008#. Where did I put it... Can you hold on one second?");

        sm.removeItem(4031572);
        sm.addExp(500);
        sm.forceCompleteQuest(3915);
    }

    // QUEST 3916: Schegerazade the Storyteller ========================================
    @Script("q3916s")
    public static void q3916s(ScriptManager sm) {
        // NPC 2040052 - Wiz
        if (!sm.askYesNo("Okay, this is a book that contains desert stories and other stories from afar that #p2101008# may not be aware of. It has a lot of stories in it, so she should be fine for a while. Please take it.")) {
            sm.sayOk("You don't plan on returning to #m260000000#? #p2101008# seemed pretty desperate...");
            return;
        }

        sm.sayOk("It's amazing to see a woman in such a stressful environment who nevertheless constantly searches for great stories... It really is. Once you go back to #b#m260000000##k, tell #b#p2101008##k I'd most like to meet her here at #m222020000# when she gets free of the queen.");
        sm.addItem(4031573, 1); // Storybook from Wiz
        sm.forceStartQuest(3916);
    }

    @Script("q3916e")
    public static void q3916e(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        if (!sm.hasItem(4031573)) {
            sm.sayOk("Have you not gone to #m222020000# yet? It's not close to here, so you should get moving.");
            return;
        }

        sm.sayNext("Welcome, traveler... Did you really go to #m222020000# and back? Ah, so this is the book from #p2040052#... Thank you very much. With this, I'll have enough stories to last a while.");
        sm.sayOk("I can't help but envy you for being able to travel to far places like #m220000000# and even #m222020000#... I wonder when I'll be free of the queen's grasp... I dream of the day I can travel the world with my brother.");

        sm.removeItem(4031573);
        sm.addExp(9000);
        sm.forceCompleteQuest(3916);
    }

    // QUEST 3917: The Little Prince that Loves Roses ========================================
    @Script("q3917s")
    public static void q3917s(ScriptManager sm) {
        // NPC 2101006 - Little Prince (Nihal Desert)
        sm.sayNext("Why is the desert so big. It's so big that I can't see a thing...");

        if (!sm.askYesNo("Come to think of it, this rose has been dying for a while. The desert heat must be too much for her. Can you get some #t2022155# for this little rose?")) {
            sm.sayOk("Why is there no pond in the desert?");
            return;
        }

        sm.sayOk("Wow... Thank you. I think #b5 #t2022155#s#k should be enough for the rose. It's such a small little thing.");
        sm.forceStartQuest(3917);
    }

    @Script("q3917e")
    public static void q3917e(ScriptManager sm) {
        // NPC 2101006 - Little Prince
        if (!sm.hasItem(2022155, 5)) {
            sm.sayOk("I don't think you've gotten #b5 #t2022155#s#k yet. I'm sorry, but she's quite selfish, and she demands more and more.");
            return;
        }

        sm.sayNext("Wow… You brought some #t2022155#... You must like flowers, too.");
        sm.sayOk("With this, I am sure the rose will not be mad anymore. It's been cranky at me for a while since it's been so thirsty. Thank you so much for your help... She's the prettiest, nicest rose you'll find in this world.");

        sm.removeItem(2022155, 5);
        sm.addExp(4200);
        sm.forceCompleteQuest(3917);
    }

    // QUEST 3918: The Little Prince that Loves the Stars ========================================
    @Script("q3918s")
    public static void q3918s(ScriptManager sm) {
        // NPC 2101006 - Little Prince
        sm.sayNext("...");

        sm.sayBoth("Ah... You're the one that got me the #t2022155# for the rose the other day... I can't thank you enough. Why am I gazing up at the sky? Because... that's where the stars are.");

        sm.sayBoth("Yes, that star you see over therrrrrrrre... Yes, I'm looking at that one. Isn't it pretty?");

        if (!sm.askYesNo("Yes, I think stars are hard to see because of the sand dust. I wish I could see the stars come clearly... Hey, if it isn't too much to ask...")) {
            sm.sayOk("If I could hold that star... That'd be so wonderful...");
            return;
        }

        sm.sayOk("Ah… Can you get me #b20 #t4000333#s#k? I've seen #r#o2100108#s#k carry them around... I think I'll really be able to see the stars better with them. Thanks…");
        sm.forceStartQuest(3918);
    }

    @Script("q3918e")
    public static void q3918e(ScriptManager sm) {
        // NPC 2101006 - Little Prince
        if (!sm.hasItem(4000333, 20)) {
            sm.sayOk("I don't think you've gotten #b20 #t4000333#s#k yet... Was it too much to ask?");
            return;
        }

        sm.sayNext("Ah. The #t4000333#s. With this, I'll be able to see the stars... I'm so happy now.");
        sm.sayOk("Look at that star! That small yet beautiful star...is actually my home. Hopefully I'll be able to go home someday.");

        sm.removeItem(4000333, 20);
        sm.addItem(1032010, 1); // Star Earring
        sm.addExp(4650);
        sm.forceCompleteQuest(3918);
    }

    // QUEST 3919: Recovering the Book The Little Prince 1 ========================================
    @Script("q3919s")
    public static void q3919s(ScriptManager sm) {
        // NPC 2040052 - Wiz
        sm.sayNext("Oh no... This is not good. The story of #p2101006# has been mixed to unrecognizable bits because of the dimensional rift in Ludibrium. I wonder where #p2101006# is... Have you seen #p2101006# by any chance? It's a kid wearing a scarf. He really likes roses...");

        sm.sayBoth("What? Nihal Desert? You saw a blonde kid who likes roses and just gazes around absently? Ohhh...#p2101006#'s there. But...was he with anyone? Besides the rose, I mean?");

        if (!sm.askYesNo("Oh no... #p2101006# is supposed to meet a fox in the desert... The story's  all jumbled up. Can you go to #p2101006# and get the #p2101006# storybook from him for me?")) {
            sm.sayOk("You seem busy. Then who should I ask for the storybook of #p2101006#?");
            return;
        }

        sm.sayOk("I can't even see how #p2101006# can be straightened out... How will I find the fox for him... Please help #b#p2101006##k make a friend of any kind. Then he should give you the book...");
        sm.addExp(2000);
        sm.forceStartQuest(3919);
    }

    @Script("q3919e")
    public static void q3919e(ScriptManager sm) {
        // NPC 2101006 - Little Prince
        sm.sayOk("Ahh... You're the kind person that got me the #t2022155#s for the rose and the #t4000333#s to help me see the stars... I'm glad to see you again...");
    }

    // QUEST 3920: Recovering the Book The Little Prince 2 ========================================
    @Script("q3920s")
    public static void q3920s(ScriptManager sm) {
        // NPC 2101006 - Little Prince
        sm.sayNext("I've sat in this desert for a long time, and no one else has talked to me except you... And no one has offered to help me as much as you...");

        sm.sayBoth("A fox? I've seen some rabbits and #o2100108#s, but not a single fox... Why do you need a fox? For you, I'll do anything to help.");

        sm.sayBoth("I am lo-- actually, no, I am not. I'm not, since you're here with me. Maybe I arrived in this desert just to meet someone like you... I do feel like that sometimes.");

        if (!sm.askYesNo("I don't know... I do want to return home...but...that means I won't be able to see you anymore. The only reason this desert is even remotely seem beautiful to me is because you're here.")) {
            sm.sayOk("A friend... Can a rose be a friend?");
            return;
        }

        sm.sayNext("Honestly, this is the first time I've ever made a true friend. A friend... I like...being friends with someone. I'm so happy. You ARE a friend.");
        sm.sayOk("As a way to celebrate our friendship, I'll give you this book. I feel like you need it for some reason. Hopefully this will help.");

        sm.addItem(4031591, 1); // The Little Prince Book
        sm.forceStartQuest(3920);
    }

    @Script("q3920e")
    public static void q3920e(ScriptManager sm) {
        // NPC 2040052 - Wiz
        if (!sm.hasItem(4031591)) {
            sm.sayOk("I don't think you've gotten the #bstorybook of #p2101006##k yet. Please observe #b#p2101006##k and discover what he's missing... If you can fill the void, he'll give you the storybook of #p2101006#.");
            return;
        }

        sm.sayNext("Wow, you got the storybook of #p2101006#! How is #p2101006#? Was he with a fox? What? He gave you the book after you told him you want to be his friend? Ah, so that's how the story found its equilibrium. I'm glad that happened.");
        sm.sayOk("You acted as the fox to #p2101006#, so the story of #p2101006# has straightened its course. Thank you so much. This will also help put #m222020000# back on course.");

        sm.removeItem(4031591);
        sm.addItem(2020012, 30); // Cider
        sm.addExp(6800);
        sm.forceCompleteQuest(3920);
    }

    // QUEST 3921: A Request from a Member of the Sand Bandits?! 1 ========================================
    @Script("q3921s")
    public static void q3921s(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member (outside Ariant)
        sm.sayNext("Shush! Lower your voice. I can't be seen... What do you want?");

        sm.sayBoth("Sand Bandits...? Hm, Sand Bandits... Why are you asking? You don't look one of the queen's guards...");

        if (!sm.askYesNo("Ooh. In that case. I mean, yes. Why, yes, I'm a member of the Sand Bandits. You caught me.  So, knowing that, will you help me?")) {
            sm.sayOk("Hey, if you aren't going to help, why'd you bother me in the first place?");
            return;
        }

        sm.sayNext("You know what we do, right? We steal the queen's treasures to, ah, help the poor. That's right. Help the poor. Now, to attack the transport group carrying her treasures, we need a rope. But first, I need material to craft the rope. You with me so far?");
        sm.sayOk("In the desert, #b#t4000329#s#k are used to create rope. Dry #t4000329#s can be pulled and stretch to make very fine rope indeed. Slay some #r#o2100102#s#k to get me #t4000329#s. #b30#k should do. I'll be waiting.");

        sm.forceStartQuest(3921);
    }

    @Script("q3921e")
    public static void q3921e(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member
        if (!sm.hasItem(4000329, 30)) {
            sm.sayOk("You're supposed to get me #b30 #t4000329#s#k to help the Sand Bandits. They drop off of #r#o2100102#s#k.");
            return;
        }

        sm.sayNext("Who-who is it?! Ohhh, you're the one that offered to help the Sand Bandits! Ah, hahaha, yes, yes. So did you get the #t4000329#s?");
        sm.sayOk("Ah, yes, this will definitely help us Sand Bandits do good in the world... It's people like you that makes my job so much easier...");

        sm.removeItem(4000329, 30);
        sm.addExp(3750);
        sm.forceCompleteQuest(3921);
    }

    // QUEST 3922: A Request from a Member of the Sand Bandits?! 2 ========================================
    @Script("q3922s")
    public static void q3922s(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member
        sm.sayNext("Shush! Lower your voice. You're the one that got me the #t4000329#s the other day... Thanks to you, I was able to make some rope. It should prove very profitable…for the people of Ariant, of course.");

        if (!sm.askYesNo("Honestly, though, our work isn't done. The transport group will be guarded, you see, and to defeat the guards, we'll need Poison Needles. Can you help? It's for the Sand Bandits, remember.")) {
            sm.sayOk("Really? You're not willing to help the Sand Bandits? I'm shocked! You are about that?");
            return;
        }

        sm.sayOk("Haha, I knew you'd agree. To make Poisoned Needles, we just need #b#t4000330#s#k since I already have the poison. You can get them from #r#o2100103#s#k, so get me around #b50#k.");
        sm.forceStartQuest(3922);
    }

    @Script("q3922e")
    public static void q3922e(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member
        if (!sm.hasItem(4000330, 50)) {
            sm.sayOk("You don't have #b50 #t4000330#s#k yet. All you need to do to get it is defeat #r#o2100103#s#k. This is for Sand Bandits. Please work harder.");
            return;
        }

        sm.sayNext("You brought the #t4000330#s. This should be more than enough for the Poisoned Needles. As a member of the Sand Bandits, I thank you deeply.");
        sm.sayOk("We Sand Bandits have a lot on our plates. If people like you didn't help us out, then we'd definitely be struggling.");

        sm.removeItem(4000330, 50);
        sm.addExp(5250);
        sm.forceCompleteQuest(3922);
    }

    // QUEST 3923: A Request from a Member of the Sand Bandits?! 3 ========================================
    @Script("q3923s")
    public static void q3923s(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member
        sm.sayNext("Hello there, loyal, faithful, unyielding supporter of the Sand Bandits. I've been waiting for you. This time, we're going to steal the queen's treasure, and you'll help of course, right?");

        if (!sm.askYesNo("I knew it. Your task is to steal the #bqueen's treasure#k from the #bqueen's accessory chest#k. It's placed in the deepest part of the palace, near the king. It'll be hard for you to get close to it, but this is for Sand Bandits. I am sure you're up to the task.")) {
            sm.sayOk("Hmmmph! You're backing out now?");
            return;
        }

        sm.sayOk("Security inside the castle is very tight these days. There will guards everywhere, ready to pounce. So once you enter, be careful where you walk. Use hidden portals to discreetly approach the chest and retrieve the jewel. I'll wait here.");
        sm.forceStartQuest(3923);
    }

    @Script("q3923e")
    public static void q3923e(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member
        if (!sm.hasItem(4031578)) {
            sm.sayOk("Hmmm... I don't think you've obtained the #b#t4031578##k yet. It should be inside the #bqueen's accessory chest#k inside the #bpalace#k. Of course, you must be discreet and use hidden portals. Good luck.");
            return;
        }

        sm.sayNext("Welcome back! Did you get the #t4031578#? Ohhhh, just as I expected! Nice, this should fetch some money…");
        sm.sayOk("Thanks to you, the Red Scor...er, I mean, Sand Bandits have been doing very, very well lately. Thank you. Hahaha... I hope you keep helping us down the road.");

        sm.removeItem(4031578);
        sm.addExp(6300);
        sm.forceCompleteQuest(3923);
    }

    // QUEST 3924: Sirin Speaks ========================================
    @Script("q3924s")
    public static void q3924s(ScriptManager sm) {
        // NPC 2101012 - Red Scorpion Member
        sm.sayNext("Argh!! I can't believe it! The jewel I stole just got stolen. I won't let whoever stole it get away with this! Oh! You're here. Thanks for coming. Someone stole the jewel you worked so hard to get us. I didn't even have time to store it safely in the cave. I just stashed it behind that rock over there. I think I know who took it, too. Can you help?");

        if (!sm.askYesNo("It was #b#p2101000##k, the dancer. How do I know this? Because besides #p2101000#, no one else ever come here! I should've been more suspicious when she started practicing her dance moves out here. It had to be #p2101000#! Please, get the jewel back from her.")) {
            sm.sayOk("What?! The Sand Bandits' precious stolen jewel just got stolen, and you aren't curious who did it?");
            return;
        }

        sm.addExp(1200);
        sm.forceStartQuest(3924);
    }

    @Script("q3924e")
    public static void q3924e(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("What's going on? It's almost showtime, and I'm practicing my new moves. I'll show them to you later... What? Did I steal a piece of jewelry that was hidden behind a rock?");
        sm.sayOk("Hmm, a jewel hidden behind a rock... Ohh, that! Yes, I took it and gave it away to feed the hungry. Why do you ask? What? I'm interfering in the work of the Sand Bandits? No way! What are you saying? I, Sirin, know more than anyone how much the Sand Bandits help the people of #m260000000#, and I would never disturb their work. The jewel hidden behind the rock was stolen by real bandits. That's why I took it.");
        sm.forceCompleteQuest(3924);
    }

    // QUEST 3925: True or False ========================================
    @Script("q3925s")
    public static void q3925s(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("Sigh... you look so clueless. Think! The Sand Bandits are based in #m260000000#. They hide their identities so no one suspects them of going against the queen. You really think they'd hide their treasures out in the open outside town?");

        sm.sayBoth("And the guy you said you helped... It sounds fishy to me. Why would he stand in the middle of the desert when everyone else is here in Ariant, doing their best to defy the queen?");

        sm.sayBoth("And the guy ordered you to steal the queen's ring, right out from under her nose? No, no, that's too risky. Imagine if you were caught! The Sand Bandits would be totally compromised. No, they'd never ask you to do that. You definitely weren't working with a member of the Sand Bandits...");

        if (!sm.askYesNo("Seriously, who fed you that junk? It's a mound of dung, my friend, one so large that a Mushmom could sprout out of it. Ah ha! I'll bet it was #p2101012#? I'm right, aren't I? He sounds like a member of the #rRed Scorpions#k. What? You don't even know about the Red Scorpions? Jeez... I'd better explain, lest you find yourself on the wrong end of a pointed sword. Will you listen?")) {
            sm.sayOk("Hmmm... I've been telling you the truth, and you don't trust me? Quite a complex you have there, my friend...");
            return;
        }

        sm.sayNext("The Red Scorpions are a notorious band of thieves based right outside #m260000000#. Unlike the Sand Bandits, they are in it for their own good, and they disturb the peace and take things from the innocent. I think you got duped by the Red Scorpions.");
        sm.sayOk("You've been tricked by the Red Scorpions all this time! You are quite gullible. Perhaps I should offer to sell you a rare and magical Flaming Sword for the low, low price of 550 million mesos... HA! I mock you, my friend, but I don't think you're a bad person. Hmm, are you interested in getting revenge on the Red Scorpions? They did play you like a trumpet, after all... If you want some payback on the Red Scorpions, let me know. I'll tell you exactly what to do.");

        sm.addExp(100);
        sm.forceStartQuest(3925);
    }

    @Script("q3925e")
    public static void q3925e(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("You're back, so I suppose you really want to turn the tables on the Red Scorpions. The way to do that is quite simple. Take their stolen treasures and give them out to the residents here. You're not only hurting the Red Scorpions, but you're also helping out the good people of Ariant. Poetic justice, don't you think?");

        sm.sayNext("The Red Scorpion's hideout is in The Scorching Desert, so the treasures they've stolen should be there as well. A suspicious man stands in front of the hideout like a guard. The thing is, even if you find the place, you won't be able to go in. The door can only be open by yelling out the magic words.");
        sm.sayOk("So you need to discover the password, then go to their hideout and steal their stolen treasures. What's the password? Hah...#byou probably already know it#k. It is a bandit hideout in the middle of a desert, after all. Afterwards, drop those treasures at secret stash spots in various homes in town. That's what I'd call revenge, served piping hot in this case! Oh, and if you see an X on any of the houses, then that means it belongs to a known thief, so stay away. Once you've robbed the robbers, let me know, alright? I'd love to hear details on how the Red Scorpions got a taste of their own medicine.");

        sm.forceCompleteQuest(3925);
    }

    // QUEST 3926: Screwing the Red Scorpions ========================================
    @Script("q3926s")
    public static void q3926s(ScriptManager sm) {
        // NPC 2103007 - Red Scorpion Treasure Chest
        sm.sayNext("This giant treasure chest is full of expensive treasures. There's plenty of stuff inside.");

        if (!sm.askYesNo("Remove the treasure from the chest and place it in a pocket.")) {
            return;
        }

        sm.sayOk("#b(Now, to give these to the people in town who need it.)#k");
        sm.addItem(4031579, 4); // Stolen Treasure
        sm.forceStartQuest(3926);
    }

    @Script("q3926e")
    public static void q3926e(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("Hahaha, looking at you, I can tell you took the stolen treasures from the Red Scorpions and gave them to the people of #m260000000#. Feel better now?");
        sm.sayOk("The silly queen was already a big enough headache for everyone in #m260000000#, and now the Red Scorpions... Seriously, why are things in #m260000000# are becoming such a mess?");

        sm.addExp(6500);
        sm.forceCompleteQuest(3926);
    }

    // QUEST 3927: The Existence of the Secret Organization ========================================
    @Script("q3927s")
    public static void q3927s(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("#m260000000# is a mineral-rich area with a lot of Lidium, an ore that can only be found at the Oasis and the Nihal Desert. But no thanks to the greedy queen, who charges ridiculous amount of taxes, everyone's struggling these days. Apparently the queen is a fairy from Orbis... She spends all the tax money to bring in unfathomable amounts of treasures.");

        sm.sayBoth("The Sultan? #p2101009# is supposedly brilliant but I've never seen him act that way. He certainly sleeps brilliantly... He has virtually no power. The word is, the queen put a spell on him...");

        if (!sm.askYesNo("Fortunately there are brave people who do. They serve as inspiration to the residents of #m260000000#. If this heroic group of bandits, called the Sand Bandits, didn't steal the queen's treasures and hand them to the people here, then the city would've been long dead by now. You want to join the Sand Bandits? But you're an outsider! Hm, but you give off a good vibe. Want me to tell you more about them?")) {
            sm.sayOk("A giant Pyramid has revealed itself out of the blue! Aren't you excited to find out what's inside?");
            return;
        }

        sm.sayNext("The Sand Bandits are despised by the queen, so they disguise themselves as common folk. Contacting them is not impossible, however…");
        sm.sayBoth("I heard a story, just a story, you hear, that the Sand Bandits write hints #bon the walls of a house somewhere in town#k. It's the key to contacting them. Find the wall, meet some members, and maybe, just maybe, you'll become one of them…");
        sm.sayOk("Of course, the wall looks just like any other wall, from a distance… So you need to check every wall. Here's a clue. It could be closer than you'd think. But even if you do everything right, it still might not work out. Don't get your hopes up. It's not an easy organization to join.");

        sm.addExp(1000);
        sm.forceStartQuest(3927);
    }

    @Script("q3927e")
    public static void q3927e(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("Did you find the wall?");

        final int answer = sm.askMenu("What did it say?", java.util.Map.of(
                0, "'If I had an iron hammer and a dagger, a bow and an arrow...'",
                1, "'Byron♡ Sirin'",
                2, "'Ahhh I forgot.'"
        ));

        if (answer == 1) {
            sm.sayOk("Man, Jiyur wrote on the wall again? Arrgh! I'll get that kid…");
            return;
        } else if (answer == 2) {
            sm.sayOk("Really? You forgot? Well, do you remember where it was written?");
            return;
        }

        sm.sayOk("If I had an iron hammer and a dagger, a bow and an arrow, eh? Think about this! A weapon is just an item...until someone uses it, right? That's the only clue I can think of…");
        sm.forceCompleteQuest(3927);
    }

    // QUEST 3928: The Man with a Bow ========================================
    @Script("q3928s")
    public static void q3928s(ScriptManager sm) {
        // NPC 2101011 - Sejan (Ariant)
        sm.sayNext("What?! If you just want to chat, buzz off. What? You want to join the Sand Bandits? Hmmm, you must have seen the writing on the wall. Haha! That means #p2101000# sent you…");

        if (!sm.askYesNo("You are not from #m260000000#. You can't even begin to understand the pain the people in #m260000000#  are suffering, so what qualifies you to become a member of our group? How do we know you're not a spy from the queen? You willing to do some work for us?")) {
            sm.sayOk("Look at that, you refuse! You must be a spy sent by the queen!");
            return;
        }

        sm.sayNext("Alright, then I'll test you. You know the Sand Bandits collect goods for the residents of #m260000000#, right? I want you to get some food for the people here. Get #b50 #t4000325#s#k, #b10 #t2010002#s#k, and #b10 #t2010000#s#k.");
        sm.sayOk("Don't tell me this is hard, okay? Our real missions are much, much tougher than this. Gathering #b#t4000325#s#k by killing #r#o2100101#s#k is nothing.");

        sm.forceStartQuest(3928);
    }

    @Script("q3928e")
    public static void q3928e(ScriptManager sm) {
        // NPC 2101011 - Sejan
        if (!sm.hasItem(4000325, 50) || !sm.hasItem(2010000, 10) || !sm.hasItem(2010002, 10)) {
            sm.sayOk("Hey, you didn't get it all! I told you, I need #b50 #t4000325#s#k, #b10 #t2010002#s#k, and #b10 #t2010000#s#k.");
            return;
        }

        sm.sayNext("Hmmm, did you bring all the items I requested? Let's see. One, two, three... Not bad. You got it all.");
        sm.sayOk("But this is nothing. Even spies from the queen can do this. If you really want to be accepted as a member of the Sand Bandits, you'll need to do more.");

        sm.removeItem(4000325, 50);
        sm.removeItem(2010000, 10);
        sm.removeItem(2010002, 10);
        sm.addExp(1000);
        sm.forceCompleteQuest(3928);
    }

    // QUEST 3929: Sejan's Test ========================================
    @Script("q3929s")
    public static void q3929s(ScriptManager sm) {
        // NPC 2101011 - Sejan
        if (!sm.askYesNo("It's good that you brought the food, but you need to help distribute it to the people of Ariant, too. If you are not a spy, then you have no problem with that, right?")) {
            sm.sayOk("You must be a spy from the queen. It was a mistake to trust you...");
            return;
        }

        sm.sayNext("Here, take these food packages. Your task is to drop them off at certain houses. Do not, I repeat, do not just place them anywhere. You have no idea how many people do that... Find a safe, secure hiding place to store the food in each house, understand?");
        sm.sayOk("Oh, and one more thing. Ariant has more than its share of thieves and crooks. We don't need to give them any food, obviously. You only need to drop the food packages at the homes of innocent residents, marked by a #bsign on the door#k. So check doors before entering.");

        sm.addItem(4031580, 4); // Food Package
        sm.forceStartQuest(3929);
    }

    @Script("q3929e")
    public static void q3929e(ScriptManager sm) {
        // NPC 2101011 - Sejan
        sm.sayNext("That was great. Now I can really tell you're not a spy from the queen.");
        sm.sayOk("You are definitely a good candidate to become a member of the Sand Bandits.");

        sm.addExp(2000);
        sm.forceCompleteQuest(3929);
    }

    // QUEST 3930: Sejan's Sand Bandits ========================================
    @Script("q3930s")
    public static void q3930s(ScriptManager sm) {
        // NPC 2101011 - Sejan
        sm.sayNext("One of the most important qualities of a Sand Bandit is selflessness. True, Sand Bandits steal riches from the queen, but it's not for us. It's for the people of #m260000000#.");

        if (!sm.askYesNo("A selfish individual cannot contain his greed. He will inevitably want to keep the expensive items for himself. This is taboo for a Sand Bandit. Thus, I must test how selfless you really are. It's simple. I need you to donate #b10,000 mesos#k. Too much? Think about it, we'll need much more than that to save #m260000000# from the queen. Selflessly part with 10,000 mesos, and I will believe in you.")) {
            sm.sayOk("You've done well so far. Don't falter now... Then again, it is hard to let go of mesos…");
            return;
        }

        if (!sm.canAddMoney(-10000)) {
            sm.sayOk("That's not enough. #b10,000 mesos#k shouldn't be hard to obtain.");
            return;
        }

        sm.sayNext("You've decided to donate 10,000 mesos? I am sure there was much you could have bought with that money, yet you selflessly sacrificed it for the people of #m260000000#. That's commendable.");
        sm.sayOk("Then you have passed the tests that I, #p2101011#, have for you. Now you just have to earn the trust of the other Sand Bandits hidden around town. Keep at it.");

        sm.addMoney(-10000);
        sm.addItem(2000002, 100); // White Potion
        sm.addItem(2000006, 100); // Mana Elixir
        sm.addExp(5000);
        sm.forceCompleteQuest(3930);
    }

    // QUEST 3931: The Man with the Iron Hammer ========================================
    @Script("q3931s")
    public static void q3931s(ScriptManager sm) {
        // NPC 2101003 - Ardin (Ariant)
        if (!sm.askYesNo("Who are you? What do you want? What? You want to join the Sand Bandits? How did you…nevermind. That's not important. Do you really want to be a member?")) {
            sm.sayOk("If you don't want to do it, then get out of my face.");
            return;
        }

        sm.sayOk("Hmmm, Sand Bandits are asked to perform difficult missions. You look like a skilled traveler, but I can't trust that you have the strength to be a Sand Bandit. Show me you're worth. Slay #r100 #o2100105#s#k.");
        sm.forceStartQuest(3931);
    }

    @Script("q3931e")
    public static void q3931e(ScriptManager sm) {
        // NPC 2101003 - Ardin
        sm.sayNext("You slew 100 #o2100105#s? Really? I find that hard to believe. Wow, you really did!");
        sm.sayOk("But #o2100105#s aren't really anything. Sand Bandits constantly face bigger and stronger monsters than that. You can't be a member just by destroying #o2100105#s.");

        sm.addExp(3000);
        sm.forceCompleteQuest(3931);
    }

    // QUEST 3932: Ardin's Test ========================================
    @Script("q3932s")
    public static void q3932s(ScriptManager sm) {
        // NPC 2101003 - Ardin
        sm.sayNext("Ahh, what a headache. What is it? Well, #o2100106# and #o2100107# are such a hassle to everyone in town, but the queen doesn't care a whit! She's only interested in collecting weird treasures. That's why everyone's struggling.");

        if (!sm.askYesNo("So it's up to the Sand Bandits to deal with those pesky monsters. That's why we can't accept weaklings. If you want to be a member, can you take care of this task for us?")) {
            sm.sayOk("Too hard, eh? That's why we don't let people into our group that easily.");
            return;
        }

        sm.sayOk("Fine. I want you to take care of #r30 #o2100106#s#k and #r30 #o2100107#s#k, and bring back #b20 #t4000326#s#k and #b20 #t4000327#s#k as proof. Experience the true hardship of being a member before you commit yourself to the group.");
        sm.forceStartQuest(3932);
    }

    @Script("q3932e")
    public static void q3932e(ScriptManager sm) {
        // NPC 2101003 - Ardin
        if (!sm.hasItem(4000326, 20) || !sm.hasItem(4000327, 20)) {
            sm.sayOk("I guess you haven't slain #r30 #o2100106#s#k and #r30 #o2100107#s#k and brought back #b20 #t4000326#s#k and #b20 #t4000327#s#k yet. I told you it's not easy.");
            return;
        }

        sm.sayNext("Whoa, did you already take care of all those #o2100106#s and #o2100107#s? Whoa, you also brought the #t4000326#s  and #t4000327#s? You really did that? No way!");
        sm.sayOk("You're much stronger than I thought. I didn't think you'd handle #o2100106#s and #o2100107#s that easily. I think this...should be enough for you to join the Sand Bandits...");

        sm.removeItem(4000326, 20);
        sm.removeItem(4000327, 20);
        sm.addExp(4000);
        sm.forceCompleteQuest(3932);
    }

    // QUEST 3933: Ardin's Sand Bandits ========================================
    @Script("q3933s")
    public static void q3933s(ScriptManager sm) {
        // NPC 2101003 - Ardin - Summon Battle
        sm.sayNext("You've done well so far. I'm impressed. Here's the final test. I will test your strength directly. Are you ready?");

        if (!sm.askYesNo("If you can defeat me in battle, you will have proven your worth!")) {
            sm.sayOk("Not ready yet? Come back when you are.");
            return;
        }

        sm.sayOk("Let's begin! Show me what you've got!");
        sm.forceStartQuest(3933);
    }

    @Script("q3933e")
    public static void q3933e(ScriptManager sm) {
        // NPC 2101003 - Ardin
        sm.sayNext("Wow! I don't think I've had that much fun battling in a long time. To battle the queen, who's overly zealous and uses weird spells, we need to place her against a very formidable foe. Someone as powerful as you is definitely useful for the Sand Bandits.");
        sm.sayOk("That doesn't mean you're a member just yet. You may have passed my test, but you still haven't taken all the tests required to join the Sand Bandits. You'll need to find other members and pass their tests, too. Personally, I hope you become one of us.");

        sm.addItem(2012000, 10); // All Cure Potion
        sm.addItem(2012002, 10); // All Cure Potion
        sm.addExp(7250);
        sm.forceCompleteQuest(3933);
    }

    // QUEST 3934: The Lady with the Dagger ========================================
    @Script("q3934s")
    public static void q3934s(ScriptManager sm) {
        // NPC 2101002 - Eleska (Ariant)
        sm.sayNext("Why are you looking at me like that? If you don't turn around right now, I'll take it as an offense! What? Sand Bandits? How do you know that name?");

        sm.sayBoth("#p2101000#?! She's still doing stupid things. I appreciate that she's trying to help the Sand Bandits, but... Argh. Anyway, what do you want from me?");

        if (!sm.askYesNo("No way. An outsider like wouldn't understand the suffering of #m260000000# enough to join Sand Bandits? Do you even know anything about the queen? Tell you what. If you have the guts to break into the queen's storage room and steal some treasure, I'll accept you as one of us.")) {
            sm.sayOk("Of course you were all talk! I'm not putting up with this! Get out of here.");
            return;
        }

        sm.sayNext("Hah! At least you talk the talk. If you're so confident, then go into the queen's treasure storage room and steal the most expensive treasure there, #b#t4031574##k. It's at the very top of the treasure storage room, so it won't be easy.");
        sm.sayOk("How do you get in the treasure storage room? Someone in the palace should know...");

        sm.addExp(500);
        sm.forceStartQuest(3934);
    }

    @Script("q3934e")
    public static void q3934e(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        sm.sayOk("Hello, traveler... I don't know how you made your way into the palace, but please be careful inside.");
    }

    // QUEST 3935: Eleska's Test ========================================
    @Script("q3935s")
    public static void q3935s(ScriptManager sm) {
        // NPC 2101008 - Schegerazade
        sm.sayNext("Hello, traveler... What kind of story would you like to listen to?");

        final int answer = sm.askMenu("", java.util.Map.of(
                0, "A Story about the Cat's Eye Jewel",
                1, "A Story about a Jewel forged from the Desert Heat",
                2, "A Story about a Jewel with Heavenly Power"
        ));

        if (answer != 2) {
            sm.sayOk("That's a wonderful story...");
            return;
        }

        sm.sayNext("#t4031574#... She's a jewel with Heavenly Power. A jewel that is most beautiful when shined upon by the sun... But she now sleeps somewhere in the treasure storage room, deep in the palace where there is no light. She can only be reached by a secret path... #e#basleep under the lamp that's never lit.#k#n");
        sm.sayOk("For #t4031574#, the palace is the worst kind of fit for her... I am glad to know that someone from a special group is trying to rescue her...");

        sm.forceStartQuest(3935);
    }

    @Script("q3935e")
    public static void q3935e(ScriptManager sm) {
        // NPC 2101002 - Eleska
        if (!sm.hasItem(4031574)) {
            sm.sayOk("Haven't you gotten the #b#t4031574##k yet? I warned you it wasn't an easy task...");
            return;
        }

        sm.sayNext("You... You brought the #t4031574#! I honestly wasn't expecting it. That's incredible! You just might be the kind of person that the Sand Bandits have been looking for...");

        sm.removeItem(4031574);
        sm.addExp(3750);
        sm.forceCompleteQuest(3935);
    }

    // QUEST 3936: Eleska's Sand Bandits ========================================
    @Script("q3936s")
    public static void q3936s(ScriptManager sm) {
        // NPC 2101002 - Eleska
        sm.sayNext("I thought it was a case of an outsider mindlessly wanting to join the hype. I apologize for underestimating you. So you really want to join the Sand Bandits? Then I'll have to test you once more.");

        if (!sm.askYesNo("Every member of the Sand Bandits has a deep hatred towards the queen. We just don't show it publicly. You can't achieve anything if you don't even have self control. That is why the most important trait of a Sand Bandits is patience. Being patient enough not to be swayed by what's unfolding right in front of your eyes, but patiently waiting for the right time. You know of Lidium. It's a jewel you can find by slaying monsters in the desert. If you bring me #b2 #t4011008#s#k, then I'll personally accept you as a member of the Sand Bandits. It's not easy refining small ores into jewels, so... I think this will be a good test to gauge your patience.")) {
            sm.sayOk("This must be too tough for you to handle. If you can't do it, then just forget about it.");
            return;
        }

        sm.forceStartQuest(3936);
    }

    @Script("q3936e")
    public static void q3936e(ScriptManager sm) {
        // NPC 2101002 - Eleska
        if (!sm.hasItem(4011008, 2)) {
            sm.sayOk("I don't think you have gotten #b2 #t4011008#s#k yet. Lidium Ore can be refined by Muhammad, so once you get the ores, take them to him, okay?");
            return;
        }

        sm.sayNext("Ho!  You brought 2 #t4011008#s. This can only be found in the area around Ariant, and it's hard to make. But once it's made, it lasts for a long time. It may look brittle, but it's actually much harder than a diamond.");

        sm.sayBoth("#t4011008# has characteristics of the people in the desert. People here may not trust you at first. You have to earn it. But once you earn it, they never turn their back on you. You have definitely shown me your true character. I, Eleska, the desert warrior, now anoint you as a new member of the Sand Bandits!");

        sm.sayOk("But don't be satisfied with just this. You have earned one person's trust, that's all. You are not yet officially accepted as a true member of the Sand Bandits. If you really wish to become a part of our group, you have to find the other members and prove your worth to each and every one of them.");

        sm.removeItem(4011008, 2);
        sm.addItem(4020007, 3); // Diamond Ore
        sm.addItem(4010003, 3); // Silver Ore
        sm.addExp(4200);
        sm.forceCompleteQuest(3936);
    }

    // QUEST 3937: The True Identity of Sand Bandits ========================================
    @Script("q3937s")
    public static void q3937s(ScriptManager sm) {
        // NPC 2101010 - Ardin (Sand Bandits leader)
        sm.sayNext("You must be the one that #p2101011#, #p2101003#, and #p2101002# have been talking about. You've done well passing their tests. Now, let me tell you a story about #m260000000#.");

        sm.sayBoth("#m260000000# was founded by wanderers in Nihal Desert. Now, the place may not look like much, but thanks to #t4011008# and the oasis, Ariant has become a center of commerce. #t4011008# is an ore found only in the desert. Since it's very valuable, it's the main source of wealth for #m260000000#.");

        if (!sm.askYesNo("But look around. Where has that wealth gone? #m260000000# is falling apart around us, and there's only one reason: the queen. This so-called fairy queen is frivolous and heartless. Our taxes go to pay for her jewels while #m260000000# suffers! Do you understand now the pain of #m260000000#? Then wander through #m260000000# some more and open your eyes. See what this place is really like! If you are sure you want to be a part of the Sand Bandits, let me know.")) {
            sm.sayOk("You still don't get it? Then I'll tell you the story again.");
            return;
        }

        sm.forceStartQuest(3937);
    }

    @Script("q3937e")
    public static void q3937e(ScriptManager sm) {
        // NPC 2101010 - Ardin
        if (!sm.askYesNo("Are you ready to fight the queen as a member of the Sand Bandits, for the sake of #m260000000#?")) {
            return;
        }

        sm.sayOk("Then from here on out, I proudly accept you as an official member of the Sand Bandits. Go now to meet the brothers and sister who stand by your side.");

        sm.addItem(4031581, 1); // Sand Bandits Emblem
        sm.addExp(10000);
        sm.forceCompleteQuest(3937);
    }

    // QUEST 3938: First Mission ========================================
    @Script("q3938s")
    public static void q3938s(ScriptManager sm) {
        // NPC 2101010 - Ardin (Sand Bandits leader)
        sm.sayNext("The Sand Bandits want to steal an batch of silk that the queen ordered, and I want you to do the job. Are you up for it?");

        if (!sm.askYesNo("It's much too dangerous to just carry around an item stolen from the queen, so we have a different plan. Since the merchant is supposed to give the item to the guard #p2101004#, you should dress up as #p2101004# and pick up the item in his place. Brilliant, right?")) {
            sm.sayOk("I guess you're not ready to take on a mission yet.");
            return;
        }

        sm.sayOk("I already have a Transforming Potion from the magicians of #m101000000#. To transform into a specific person, however, you also need a piece of their hair. So grab a #bpiece of hair from #p2101004##k. Once you do that, I'll make the transforming potion for #p2101004#.");
        sm.forceStartQuest(3938);
    }

    @Script("q3938e")
    public static void q3938e(ScriptManager sm) {
        // NPC 2101010 - Ardin
        if (!sm.hasItem(4031570)) {
            sm.sayOk("Hmmm... I need some of #p2101004#'s hair. One piece is all I need.");
            return;
        }

        sm.sayOk("This is #p2101004#'s hair? Er, it doesn't look like head hair... What? Ohhh, from his beard? Well, hair's hair, so it shouldn't be a problem. Please wait as I get the Transforming Potion ready.");

        sm.removeItem(4031570);
        sm.addExp(1000);
        sm.forceCompleteQuest(3938);
    }

    // QUEST 3939: Tigun's Hair ========================================
    @Script("q3939s")
    public static void q3939s(ScriptManager sm) {
        // NPC 2101004 - Tigun
        sm.sayNext("What is it? We don't let any old stranger in the palace. What? You want a piece of my hair? Why? What do you plan on doing with it?");

        if (!sm.askYesNo("It doesn't matter. I can't give you the hair on my head. I need to save each and every last strand. But I CAN give you a hair from my beard...if you can do me a favor.")) {
            sm.sayOk("Then my hair is off limits. Scram.");
            return;
        }

        sm.sayOk("I'll tell you what I want. Have you heard of #t4011008#? It's an ore that's only produced in #m260000000#. If you can get me #b1 #t4010007##k, then I'll give a hair from my beard.");
        sm.forceStartQuest(3939);
    }

    @Script("q3939e")
    public static void q3939e(ScriptManager sm) {
        // NPC 2101004 - Tigun
        if (!sm.hasItem(4010007)) {
            sm.sayOk("What? You still haven't gotten #b1 #t4010007##k? I know it's expensive, but it's not as valuable as a hair from my beard!");
            return;
        }

        sm.sayNext("I don't know what you want with this, but a deal is a deal. I'm not giving back the Lidium Ore, you got me?");
        sm.sayOk("Now that you have a hair from my beard, get out of here.");

        sm.removeItem(4010007);
        sm.addItem(4031570, 1); // Ahmed's Hair
        sm.forceCompleteQuest(3939);
    }

    // QUEST 3940: Mission Complete! ========================================
    @Script("q3940s")
    public static void q3940s(ScriptManager sm) {
        // NPC 2101010 - Ardin
        if (!sm.askYesNo("Ah, you're back. The Tigun Transformation Potion is now complete. Drink the potion and go to #b#p2101013##k to receive an order of silk. The merchant that deals with the queen should be around #b#m260010600##k.")) {
            sm.sayOk("Did the potion scare you off? Hmmm, then who should I ask to do this? #p2101003#, since he looks a lot like Tigun?");
            return;
        }

        sm.sayNext("Once you have transformed into #p2101004#, you can't attack anyone. If you accidentally use a skill when using the disguise, everything will be ruined. Also, if you're attacked, the transformation will end. To summarize, do not engage in a fight on your way to #p2101013#.");
        sm.sayOk("You will be transformed for #b1 hour#k. During that time, you should #bnever attack or be attacked#k. In that time, you must also get an order of silk from  #p2101013# and safely come back. Good luck.");

        sm.addItem(2210005, 1); // Tigun Transformation Potion
        sm.forceStartQuest(3940);
    }

    @Script("q3940e")
    public static void q3940e(ScriptManager sm) {
        // NPC 2101010 - Ardin
        if (!sm.hasItem(4031571)) {
            sm.sayOk("Hmmm... Haven't obtained the #b#t4031571##k yet? With the money we'll earn from that, the residents of #m260000000# won't go hungry for a while.");
            return;
        }

        sm.sayNext("Ohhhh, you're back. Did you get the order of silk from #p2101013#?");
        sm.sayOk("Brilliant. Instead of fighting our fellow residents to steal the queen's treasures, it's much better to steal like this, nice and quiet, with no one realizing exactly what happened.");

        sm.removeItem(4031571);
        sm.addItem(2040701, 1); // Scroll for Gloves for ATT
        sm.addExp(4000);
        sm.forceCompleteQuest(3940);
    }

    // QUEST 3941: Stealing Queen's Order of Silk ========================================
    @Script("q3941s")
    public static void q3941s(ScriptManager sm) {
        // NPC 2101013 - Silk Merchant (Nihal Desert)
        // Transformation script
        sm.sayOk("I'll check if you're transformed as Tigun...");
    }

    @Script("q3941e")
    public static void q3941e(ScriptManager sm) {
        // NPC 2101013 - Silk Merchant
        sm.sayNext("Okay, here it is. Please handle it with care. This silk is very hard to get, and if any part of this fabric is ripped, the queen will put you in jail, #p2101004#.");

        sm.addItem(4031571, 1); // Queen's Silk Order
        sm.forceCompleteQuest(3941);
    }

    // QUEST 3942-3947: Byron's Recommendation Letters (Job Advancement)
    @Script("q3942s")
    public static void q3942s(ScriptManager sm) {
        // Warrior Recommendation
        sm.sayNext("Hey, how's it going? I've been watching your every move, and... you look like you're ready for an advancement... and I want to recommend you to #p1022000#. What do you think?");

        if (!sm.askYesNo("I've known #p1022000# for a long time. Please see #b#p1022000##k at #m102000000#. You may be able to make the advancement through him, too.")) {
            sm.sayOk("Do you feel like you're not ready to make the leap? You're humble, which is great, but keep in mind that this opportunity to make a job advancement disappears after a while...");
            return;
        }

        sm.addItem(4031620, 1); // Byron's Recommendation Letter
        sm.forceStartQuest(3942);
    }

    @Script("q3942e")
    public static void q3942e(ScriptManager sm) {
        // NPC 1022000 - Dances with Balrog
        if (!sm.hasItem(4031620)) {
            sm.sayOk("This is where you can make the advancement as a true warrior.");
            return;
        }

        sm.sayNext("Who are you? You have something for me? Let me see...");
        sm.sayOk("Oh, it's a recommendation letter from #p2101005#. If it's from #p2101005#, then it's 100% legit. You do look like someone who's primed for an advancement. If you feel like you're ready, then let me know. I'll put you to the test.");

        sm.removeItem(4031620);
        sm.addExp(3500);
        sm.forceCompleteQuest(3942);
    }

    @Script("q3943s")
    public static void q3943s(ScriptManager sm) {
        // Magician Recommendation
        sm.sayNext("Hey, how's it going? I've been watching your every move, and... you look like you're ready for an advancement... and I want to recommend you to #p1032001#. What do you think?");

        if (!sm.askYesNo("I've known #p1032001# for a long time. Please see #b#p1032001##k at #m102000000#. You may be able to make the advancement through him, too.")) {
            sm.sayOk("Do you feel like you're not ready to make the leap? You're humble, which is great, but keep in mind that this opportunity to make a job advancement disappears after a while...");
            return;
        }

        sm.addItem(4031621, 1);
        sm.forceStartQuest(3943);
    }

    @Script("q3943e")
    public static void q3943e(ScriptManager sm) {
        if (!sm.hasItem(4031621)) {
            sm.sayOk("This is where you can make the advancement as an intelligent magician.");
            return;
        }

        sm.sayNext("Who are you? You have something for me? Let me see...");
        sm.sayOk("Oh, it's a recommendation letter from #p2101005#. If it's from #p2101005#, then it's 100% legit. You do look like someone who's primed for an advancement. If you feel like you're ready, then let me know. I'll put you to the test.");

        sm.removeItem(4031621);
        sm.addExp(3500);
        sm.forceCompleteQuest(3943);
    }

    @Script("q3944s")
    public static void q3944s(ScriptManager sm) {
        // Bowman Recommendation
        sm.sayNext("Hey, how's it going? I've been watching your every move, and... you look like you're ready for an advancement... and I want to recommend you to #p1012100#. What do you think?");

        if (!sm.askYesNo("I've known #p1012100# for a long time. Please see #b#p1012100##k at #m100000000#. You may be able to make the advancement through her, too.")) {
            sm.sayOk("Do you feel like you're not ready to make the leap? You're humble, which is great, but keep in mind that this opportunity to make a job advancement disappears after a while...");
            return;
        }

        sm.addItem(4031622, 1);
        sm.forceStartQuest(3944);
    }

    @Script("q3944e")
    public static void q3944e(ScriptManager sm) {
        if (!sm.hasItem(4031622)) {
            sm.sayOk("This is where you can make the advancement as a careful bowman.");
            return;
        }

        sm.sayNext("Who are you? You have something for me? Let me see...");
        sm.sayOk("Oh, it's a recommendation letter from #p2101005#. If it's from #p2101005#, then it's 100% legit. You do look like someone who's primed for an advancement. If you feel like you're ready, then let me know. I'll put you to the test.");

        sm.removeItem(4031622);
        sm.addExp(3500);
        sm.forceCompleteQuest(3944);
    }

    @Script("q3945s")
    public static void q3945s(ScriptManager sm) {
        // Thief Recommendation
        sm.sayNext("Hey, how's it going? I've been watching your every move, and... you look like you're ready for an advancement... and I want to recommend you to #p1052001#. What do you think?");

        if (!sm.askYesNo("I've known #p1052001# for a long time. Please see #b#p1052001##k at #m103000000#. You may be able to make the advancement through her, too.")) {
            sm.sayOk("Do you feel like you're not ready to make the leap? You're humble, which is great, but keep in mind that this opportunity to make a job advancement disappears after a while...");
            return;
        }

        sm.addItem(4031623, 1);
        sm.forceStartQuest(3945);
    }

    @Script("q3945e")
    public static void q3945e(ScriptManager sm) {
        if (!sm.hasItem(4031623)) {
            sm.sayOk("This is where you can make the advancement as a brilliant thief.");
            return;
        }

        sm.sayNext("Who are you? You have something for me? Let me see...");
        sm.sayOk("Oh, it's a recommendation letter from #p2101005#. If it's from #p2101005#, then it's 100% legit. You do look like someone who's primed for an advancement. If you feel like you're ready, then let me know. I'll put you to the test.");

        sm.removeItem(4031623);
        sm.addExp(3500);
        sm.forceCompleteQuest(3945);
    }

    @Script("q3946s")
    public static void q3946s(ScriptManager sm) {
        // A Proper Reward for a Good Deed
        sm.sayNext("Hey, aren't you being a little too unselfish? What you've done has been quite incredible. Thanks to you, the Red Scorpions are a mess. That won't stop them from stealing stuff in the future, but when that happens, you can steal back the stuff they stole.");

        if (!sm.askYesNo("I know you helped the Red Scorpions, but that's because you didn't know any better. You've done a lot of good, too! It wouldn't be terrible for you to ask for a reward... Why are you so selfless?")) {
            sm.sayOk("Huh? You really don't want a reward? You must be an angel or something...");
            return;
        }

        sm.sayNext("As you know, the people of Ariant can't afford to reward you, and of course, the queen won't do a thing. And you couldn't possibly expect anything out of a poor dancer like me, right? So...take care of your own reward!");
        sm.sayOk("You really don't understand what I'm hinting at? Seriously, there's a place with a ton of treasure just sitting there... Think about it. I'll look the other way this one time. You know what I'm talking about now, right?");

        sm.forceStartQuest(3946);
    }

    @Script("q3946e")
    public static void q3946e(ScriptManager sm) {
        // Red Scorpion Treasure Chest - Give random ore
        sm.sayNext("This giant treasure chest is full of expensive treasures. There's plenty of stuff inside.");

        if (!sm.askYesNo("Remove the treasure from the chest and place it in a pocket.")) {
            return;
        }

        sm.sayOk("#b(Well, this is probably enough...)#k #b(Doing a good deed definitely pays off...)#k");

        // Random ore reward
        final int[] ores = {4020007, 4020005, 4020001, 4020002, 4020004, 4020006, 4020000, 4020008, 4020003, 4010007};
        final int randomOre = ores[(int) (Math.random() * ores.length)];
        sm.addItem(randomOre, 5);
        sm.forceCompleteQuest(3946);
    }

    @Script("q3947s")
    public static void q3947s(ScriptManager sm) {
        // Pirate Recommendation
        sm.sayNext("Hey, how's it going? I've been watching your every move, and... you look like you're ready for an advancement... and I want to recommend you to #p1090000#. What do you think?");

        if (!sm.askYesNo("I've known #p1090000# for a long time. Please visit #b#p1090000##k on the Nautilus. He'll be help you advance your job.")) {
            sm.sayOk("Do you feel like you're not ready to make the leap? You're humble, which is great, but keep in mind that this opportunity to make a job advancement disappears after a while...");
            return;
        }

        sm.addItem(4031893, 1);
        sm.forceStartQuest(3947);
    }

    @Script("q3947e")
    public static void q3947e(ScriptManager sm) {
        if (!sm.hasItem(4031893)) {
            sm.sayOk("Here, you can become a great Pirate.");
            return;
        }

        sm.sayNext("Do I know you? Are you here to deliver something to me?");
        sm.sayOk("#p2101005#'s recommendation, huh? You must be seeking job advancement. If you really want to change your job, you need to take the test.");

        sm.removeItem(4031893);
        sm.addExp(3500);
        sm.forceCompleteQuest(3947);
    }

    // QUEST 3948: Sejan's Good Habit ========================================
    @Script("q3948s")
    public static void q3948s(ScriptManager sm) {
        // NPC 2101011 - Sejan
        sm.sayNext("Hm? Are you one of us? Wait, it doesn't even matter...just find me my arrows. I usually collect them right after I shoot them, but a few days ago, I wasn't feeling well so I didn't. This morning, I finally got out of bed and looked all over town for my arrows, but couldn't find them all. Now I'm missing some. Can you help me find them?");

        if (!sm.askYesNo("I only use #b#t02060001##k. A true skillsman only uses the one type of arrow that fit them perfectly. Right now, I am missing #b10000#k arrows. Think that's a lot? In order to rescue Ariant from the queen, that's nothing. I use way more than that in a single day. Bring them to me once you find them all. You're my only hope.")) {
            sm.sayOk("People who constantly change their mind can't be trusted.  I'll be keeping an eye on you from now on.");
            return;
        }

        sm.forceStartQuest(3948);
    }

    @Script("q3948e")
    public static void q3948e(ScriptManager sm) {
        // NPC 2101011 - Sejan
        if (!sm.hasItem(2060001, 10000)) {
            sm.sayOk("Did you find all the arrows? Search carefully near town as well...");
            return;
        }

        sm.sayNext("Great. Now I can continue to fight for Ariant. I will never forget what you've done for me. But, you didn't just buy any old arrows at a store, right? I make my own, so I can tell if you're trying to fool me... If you're trying to trick me, you will regret it.");
        sm.sayOk("Mmm... These are it.  All #b10000#k of them. Thank you.");

        sm.removeItem(2060001, 10000);
        sm.addExp(15000);
        sm.forceCompleteQuest(3948);
    }

    // QUEST 3949: Jiyur's Palace Entry Pass ========================================
    @Script("q3949s")
    public static void q3949s(ScriptManager sm) {
        // NPC 2101001 - Jiyur
        sm.sayNext("I decided go see my sister! I discovered a way to get into the palace.");

        sm.sayBoth("I talked to some grown-ups and they said I can pay a man named #p2101004# to give me a #t04031582#. Only thing is, I don't have any money. Can buy it for me? When I grow up and make lots of money, I'll pay you back. I promise!");

        sm.sayBoth("Wow! Thanks! With the #t04031582#, I can go see my sister. But, what should I say to #p2101004#? I've seen him from afar and he doesn't look friendly...");

        if (!sm.askYesNo("You would really do that for me?")) {
            sm.sayOk("If you change your mind, come see me.  I'll be here.");
            return;
        }

        sm.sayOk("Sniff. Thank you so much. I will never forget this. After you buy the #b#t04031582##k from #b#p2101004##k, please bring it to me. I'll be waiting here.");
        sm.forceStartQuest(3949);
    }

    @Script("q3949e")
    public static void q3949e(ScriptManager sm) {
        // NPC 2101001 - Jiyur
        if (!sm.hasItem(4031582)) {
            sm.sayOk("Did you go see #b#p2101004##k? #b#p2101004##k is the man who stands guard at the Ariant palace entrance. I heard he will give you a palace entry pass if you bring him 2000 mesos.");
            return;
        }

        sm.sayNext("Wow!! You really got me the #t04031582#? Thank you so much!");
        sm.sayOk("So this is the #t04031582#? With this, I can get inside the palace, right? Hm...? What's this empty space for? Am I supposed to write my name here? *Scribble, scribble*  Hehe, sister, here I come!");

        sm.removeItem(4031582);
        sm.addExp(15000);
        sm.forceCompleteQuest(3949);
    }

    // QUEST 3950: Dealing with Tigun ========================================
    @Script("q3950s")
    public static void q3950s(ScriptManager sm) {
        // NPC 2101001 - Jiyur (crying)
        sm.sayNext("Sniff, sniff... Wahh!");

        sm.sayBoth("#h0#!  No, I didn't get to see my sister...");

        sm.sayBoth("Tigun said...sniff...because I wrote my name in that empty space...sniff...that I can't use that entry pass anymore...sniff.  Plus...plus, he said I have to pay a fine for scribbling on such an important pass! He said he'll throw me in prison if I don't! Wah!!  I want my sister!");

        if (!sm.askYesNo("Sniff... It's #b50000 mesos#k... I think so...sniff... Will you really help me?")) {
            sm.sayOk("Oh... Oh I see... Sniff, sniff... Thanks anyway.");
            return;
        }

        sm.sayOk("Sniff, thank you so much. You know where to find #b#p2101004##k, right? He's probably still at the palace entrance.  Thank you, #h0#.");
        sm.forceStartQuest(3950);
    }

    @Script("q3950e")
    public static void q3950e(ScriptManager sm) {
        // NPC 2101004 - Tigun
        if (!sm.canAddMoney(-50000)) {
            sm.sayOk("What is it? You want to scribble on a #t04031582# too? If you don't want to pay the 50000 mesos, then fine, go to prison.");
            return;
        }

        sm.sayNext("#p2101001#? That little bratty kid?! Ha! I let people in the palace if they have the #b#t04031582##k, but I'll never accept a pass that's been scribbled on like that! Do you know the work I put into those entry passes!? Ha, if he doesn't pay 50000 mesos, I'll throw him in prison!");

        sm.sayBoth("Huh? Really? I can't do that! She's very important to the queen. She is the queen's storyteller... How about this? I'll deliver letters to her instead... Sound like a good deal?");

        sm.sayNext("Very well, hehe. Any time the kid brings me a letter, I promise on my sacred name to deliver it to his sister. If she wrote one for him, I'll give pass it to him, too. I'll collect #b50000 mesos#k as a small delivery fee though.");
        sm.sayOk("I'll explain it to #p2101001# when he comes to see me, so you can be on your way. What? Okay ,okay. I won't mention the delivery fee. Hehe.");

        sm.addMoney(-50000);
        sm.addExp(35000);
        sm.forceCompleteQuest(3950);
    }

    // QUEST 3952-3954: The Desert Bounty ========================================
    @Script("q3952s")
    public static void q3952s(ScriptManager sm) {
        // NPC 2101000 - Sirin
        sm.sayNext("If you're here to see the dance, wait until the performance... Oooooh. Are you here about the rumor?");

        sm.sayBoth("Oy vey! How could the dance of the amazing #p2101000# attract so few people? Ah well, so it goes. I'll tell you about the rumor then. A bounty has put on #r#o3220001##k.");

        sm.sayBoth("What? Who is #o3220001#? Are you serious? He's only the biggest threat in the desert, the ruler of all the Cactus, the most powerful monster in all the Burning Road! You should learn more about #m260000000# from Byron, honestly!");

        sm.sayOk("Anyway, a bounty has been put on #o3220001#. And the reward is quite generous. You've know about #m260000000#'s underground organization, #bthe Sand Bandits#k, right? That's where the rumor started. You want to hear more? If you want details, find out on your own. Maybe you should look for the #bleader of the Sand Bandits#k or something.");

        sm.forceStartQuest(3952);
    }

    @Script("q3952e")
    public static void q3952e(ScriptManager sm) {
        // NPC 2101010 - Ardin
        sm.sayNext("Oh, #h0# is that you? What is it...? Huh? A bounty...? Oh, you're talking about a bounty for #o3220001#. That means you heard the rumor from #p2101000#? It's true. The Sand Bandits have put a bounty on #o3220001#.");

        sm.sayBoth("#o3220001# is a dangerous monster out to destroy the desert. A group of merchants traveling the Burning Road has been attacked a number of times, but the sultan and the queen couldn't care less. Hence, the Sand Bandits took matters into our own hands.");

        sm.sayNext("But this is too much for the Sand Bandits to take care of on our own, which is why we have put a bounty on the monster... Why, you ask? Well...");

        sm.sayBoth("#o3220001#is an ancient #o2100104# that evolved into a monster. He's very smart. He can even use magic! That explains why some people worship him as the guardian of the desert. It sounds foolish, I know, but a good number of people actually believe that.");

        sm.sayNext("We can't just go out and defeat #o3220001#, as that will turn many people against the Sand Bandits. We must first convince the people... Will you take on this task?");

        sm.sayOk("The spokesman for the people who believe #o3220001# is the guardian of the desert is #b#p2100001##k. He is as stiff and stubborn as #t4011008#. #bConvincing#k that old man will be our first battle, before we even hunt the monster. Convincing him won't be easy. You might have to give him a #bLidium#k as a gift before you can get him to listen... Often, when you talk to someone that stubborn, it helps to #bbeat around the bush and change the subject to get him where you want him, then put your foot down and convince him when the time is right.#k");

        sm.forceCompleteQuest(3952);
    }

    // QUEST 3953: Convince Muhamad ========================================
    @Script("q3953s")
    public static void q3953s(ScriptManager sm) {
        // NPC 2100001 - Muhamad
        sm.sayNext("Are you here to make an item? Huh? #o3220001#? You're not here to spout that nonsense about #o3220001# being a monster, are you?");

        if (!sm.askYesNo("#o3220001#? A monster, huh? What an absurd thing to say! How dare you speak of the guardian of the desert that way! Sheesh, no respect. If you're going to believe that nonsense, get out of here! I have nothing to say to you! How dare you come #bempty-handed#k and...")) {
            sm.sayOk("What a relief. I've been rather irritable because people keep spewing that ridiculous nonsense about #o3220001# being a monster.");
            return;
        }

        sm.forceStartQuest(3953);
    }

    @Script("q3953e")
    public static void q3953e(ScriptManager sm) {
        // NPC 2100001 - Muhamad - Convince him with Lidium
        if (!sm.hasItem(4011008)) {
            return;
        }

        sm.sayOk("Well... you brought Lidium... I suppose I can listen to what you have to say...");
        sm.removeItem(4011008);
        sm.forceCompleteQuest(3953);
    }

    // QUEST 3954: Defeat Deo ========================================
    @Script("q3954s")
    public static void q3954s(ScriptManager sm) {
        // NPC 2101010 - Ardin
        sm.sayNext("You convinced #p2100001#? I'm impressed. No member of the Sand Bandits has been able to win over that stubborn old man... You're incredible. You are the pride and joy of the Sand Bandits.");

        if (!sm.askYesNo("Since you were able to do something no one has been able to do, you'll also defeat #o3220001# for us, won't you?")) {
            sm.sayOk("Too much for you? Not something I expected to hear. But, come closer and listen to me... There is a bounty being offered...");
            return;
        }

        sm.sayOk("Now, go and defeat #r1 #o3220001##k for us. It shouldn't be too difficult. Not for you. You want to know where #o3220001# is? Well, I'm not sure. Given that he's the leader of Cactus, wouldn't he be in #b#o2100104##k Desert? I don't think he appears that frequently, so be patient.");

        sm.forceStartQuest(3954);
    }

    @Script("q3954e")
    public static void q3954e(ScriptManager sm) {
        // NPC 2101010 - Ardin
        sm.sayNext("You've defeated #o3220001#! You're incredible! You have what it takes to protect this Desert! What? You want a reward?");
        sm.sayOk("HAHAHA! The Sand Bandits offered a bounty to the anyone who defeat #o3220001#, but that doesn't include members of the Sand Bandits! It's your duty to protect the desert! It's only right that you helped. HAHAHAHA! Mmm...");

        sm.addExp(55500);
        sm.forceCompleteQuest(3954);
    }

    // QUEST 3955: In Search of the Long Lost Pyramid ========================================
    @Script("q3955s")
    public static void q3955s(ScriptManager sm) {
        // NPC 2101005 - Byron (Auto-start quest)
        sm.sayNext("Did you hear the story? Recently, a huge sandstorm swept across Sunset Road in Nihal Desert. The path that connects Ariant to Magatia disappeared completely for a whole week. Even the residents there claim it was the biggest sandstorm they'd seen in a hundred years.");

        sm.sayBoth("After the sandstorm passed, I checked to see how it affected the creatures and discovered something amazing. A Pyramid! A giant Pyramid standing right before my eyes! I think it's a Pyramid that used to be buried underground but resurfaced after the sandstorm drastically changed the landscape. The local residents claim the Pyramid is evil and don't go anywhere near it.");

        sm.sayBoth("But I disagree with them. An evil Pyramid? That doesn't make any sense! So I decided to enter the Pyramid and study the basics of the ancient burial process. But someone stopped me in my tracks, saying that I was not qualified and that I would only enrage some god known as Nett.");

        if (!sm.askYesNo("Apparently, only people over Level 40 are allowed in. Unfortunately, I am only at Level 39. This is why I am suggesting that you venture inside the Pyramid. What do you say?")) {
            sm.sayOk("A giant Pyramid has revealed itself out of the blue! Aren't you excited to find out what's inside?");
            return;
        }

        sm.sayOk("Awesome! I want you to investigate the inside of the Pyramid thoroughly and tell me what it's like in detail. Head to #bSahel 3#k, and you'll find a new path that leads you to the Pyramid. I'll be here waiting. The man that stopped me at the entrance of the Pyramid is called #b#eDuarte#n#k. Strange name, don't you think?");

        sm.forceStartQuest(3955);
    }

    @Script("q3955e")
    public static void q3955e(ScriptManager sm) {
        // NPC 2103013 - Duarte (Pyramid entrance)
        sm.sayNext("Stop immedietely, foolish one. Are you not afraid of death?");

        sm.sayBoth("Fools. A sea of fools. Are all humans this foolish? Even one who called himself a scholar dared to venture here. But is not death something to avoid?");

        sm.sayBoth("I see. That fool. I was merciful enough to save him from the throngs of death, and instead he drags himself to another one of its doorsteps. So be it. Let us see if he can escape the breath of Anubis.");

        sm.sayOk("I will permit your entrance, but it remains to be seen if the Pyramid will accept you as well. If it does, then you will acquire the Pharaoh Yeti's Gem. If you bring that to me, I will lead you directly to a spot where you can acquire many other rare gems.");

        sm.addExp(6000);
        sm.forceCompleteQuest(3955);
    }
    @Script("q3311s")
    public static void q3311s(ScriptManager sm) {
        // Clue (3311 - start)
        // NPC 2111000 - Zenumist (Magatia)
        // Part of "Zenumist and the Missing Alchemist" questline
        // Level 70+ requirement
        sm.sayNext("What I want you to do now is to search the house of the alchemist that has gone missing. It's the very place where the accident occurred. We've already searched the house a number of times, but I'm sure there's still a lot that hasn't been found, especially #bDe Lang's Secret Note#k...");
        sm.sayBoth("That's why I want you to go there and find even the smallest piece of evidence. It'd be fantastic if you can find the Secret Note, but even if you can't, a nondescript sentence will be better than nothing.");

        if (!sm.askYesNo("Will you search De Lang's house for clues?")) {
            sm.sayOk("If you are not interested, then that's okay. Just don't ever mention the incident ever again.");
            return;
        }

        sm.sayOk("I do believe that someone like you, who never knew about the missing alchemist, can provide a fresh eye to this investigation and find something we may have missed...");
        sm.forceStartQuest(3311);
    }

    @Script("q3311e")
    public static void q3311e(ScriptManager sm) {
        // Clue (3311 - end)
        // NPC 2111000 - Zenumist
        final int answer = sm.askMenu("How was the search? Did you find anything particularly interesting? Any clues?", java.util.Map.of(
                0, "I wasn't able to find anything.",
                1, "(What did it Say again...)",
                2, "I saw a formula to making a mechanical glove.",
                3, "It really wasn't much... just a story about a pendant..."
        ));

        if (answer == 0) {
            sm.sayOk("Hmm... How was it? I thought a fresh eye like you who doesn't know anything about the missing alchemist would find something new...");
            return;
        } else if (answer == 1) {
            sm.sayOk("Why aren't you saying something? Did you forget the clue already?");
            return;
        } else if (answer == 2) {
            sm.sayOk("A formula for the mechanical glove? The missing alchemist was an Alcadno, so of course he should have something like that.");
            return;
        } else if (answer == 3) {
            sm.sayNext("...really? A pendant... a pendant... maybe that's...");
            final int followUp = sm.askMenu("", java.util.Map.of(
                    0, "What do you mean by that? Is there a special meaning to the word Pendant that was written on the wall?"
            ));
            sm.sayOk("No! It's nothing. We better find out more information about the missing alchemist. Thank you for your help. Now, if you'll excuse me...");

            sm.addExp(60000);
            sm.forceCompleteQuest(3311);
        }
    }

    @Script("q3301s")
    public static void q3301s(ScriptManager sm) {
        // Test from the Head of Zenumist Society (3301 - start)
        // NPC 2111007 - Han the Broker
        // Part of "Joining Zenumist" questline
        final int answer1 = sm.askMenu("Do you really think Magatia is a town full of scholars with pure intent on research? That's nonsense. There's no other town that bickers over territory for their own good like them. The days of scholars purely searching for the truth is long gone... well, that's perfect for brokers, though.", java.util.Map.of(
                0, "Is there a big conflict?"
        ));

        final int answer2 = sm.askMenu("Yes, which makes life harder for travelers like you. People are in so much conflict between one another that it's hard to join either of the two forces. There's so much mistrust in the air that the outsiders will have a hard time entering their tight circle. If you ever went there, you know what I am talking about.", java.util.Map.of(
                0, "A little bit..."
        ));

        final int answer3 = sm.askMenu("If you want to know more about the situation in Magatia, you will have to join either Zenumist or Alcadno, and be part of the group. Even that is going to be difficult, but... your humble broker knows a way or two to get in. What do you think? Are you interested in joining #bZenumist#k?", java.util.Map.of(
                0, "I am interested in becoming a part of Zenumist."
        ));

        final int answer4 = sm.askMenu("Hahaha... I knew you would Say that. An adventurer like you would never reject an opportunity like this. I have to warn you, though, that joining the Zenumist sect is not easy. They believe that they are the true scholars, and they treat themselves as an exclusive bunch. In order to join Zenumist, you'll need to present to them your work as an alchemist.", java.util.Map.of(
                0, "Does that mean it's impossible?"
        ));

        if (!sm.askYesNo("Of course not... haha. I, #p2111007#, find ways to satisfy every client that pays me. If you can bring me 2 ores of a jewel that can be turned into mesos, then I'll help you join them. I am telling you, I am the only person here that can help you here in #m261000000#.")) {
            sm.sayOk("You declined? You must be very confident in conducting alchemy in front of those scholars.");
            return;
        }

        sm.sayOk("That's the right decision. Come back to me when you are ready to make the deal. I don't care which jewel it is, just get me #b2 jewel ores#k that'll make me some money. It's not that hard, is it? That should be enough for me to help you join the Zenumists...");
        sm.forceStartQuest(3301);
    }

    @Script("q3301e")
    public static void q3301e(ScriptManager sm) {
        // Test from the Head of Zenumist Society (3301 - end)
        // NPC 2111007 - Han the Broker
        // Auto-complete via quest system (no dialogue needed)
    }

    @Script("q3303s")
    public static void q3303s(ScriptManager sm) {
        // Test from the Head of Alcadno Society (3303 - start)
        // NPC 2111007 - Han the Broker
        // Part of "Joining Alcadno" questline
        final int answer1 = sm.askMenu("Do you really think Magatia is a town full of scholars with pure intent on research? That's nonsense. There's no other town that bickers over territory for their own good like them. The days of scholars purely searching for the truth is long gone... well, that's perfect for brokers, though.", java.util.Map.of(
                0, "Is there a big conflict?"
        ));

        final int answer2 = sm.askMenu("Yes, which makes life harder for travelers like you. People are in so much conflict between one another that it's hard to join either of the two forces. There's so much mistrust in the air that any outsiders will have a hard time entering their tight circle. If you ever went there, you know what I am talking about.", java.util.Map.of(
                0, "A little bit..."
        ));

        final int answer3 = sm.askMenu("If you want to know more about the situation in Magatia, you will have to join either Zenumist or Alcadno, and be part of the group. Even that is going to be difficult, but... your humble broker knows a way or two to get in. What do you think? Are you interested in joining #bAlcadno#k?", java.util.Map.of(
                0, "I am interested in becoming a part of Alcadno."
        ));

        if (!sm.askYesNo("I knew it! Hahaha... you know nothing's free in life, right? If you don't mind paying, then I will help you become a member. Just give me a few ores of jewels, and I'll give you a Report that'll let you in Alcadno.")) {
            sm.sayOk("Hmm... you must be struggling to come up with the ores. It's not easy, sure, but if you don't turn in that report, then you won't be able to join Alcadno. Think!");
            return;
        }

        sm.sayNext("In order to join Alcadno, you'll have to compile a report of all the experiments you've conducted, and hand them to the leader of Alcadno Society, #p2111001#. Of course, the test doesn't end there... but I'll tell you the rest after you give me the ores.");
        sm.sayBoth("Let me warn you in advance that Alcadno is full of mechanical engineers, so they are a bit...um... cranky. They are also shunned by other scholars, so you get the idea. Even with all that, if you still want to join Alcadno, then just get me #b2 ores of jewels#k. It doesn't matter what kind, just get me two.");
        sm.forceStartQuest(3303);
    }

    @Script("q3303e")
    public static void q3303e(ScriptManager sm) {
        // Test from the Head of Alcadno Society (3303 - end)
        // NPC 2111007 - Han the Broker
        // Auto-complete via quest system (no dialogue needed)
    }

    @Script("q3305s")
    public static void q3305s(ScriptManager sm) {
        // Re-acquiring Zenumist Cape (3305 - start)
        // NPC 2111000 - Zenumist
        // Lost cape replacement quest
        if (!sm.hasItem(4000021, 10) || !sm.hasItem(4021003, 5) || !sm.canAddMoney(-10000)) {
            sm.sayOk("You need #b10 Pig Ribbons#k, #b5 Mithril Ores#k, and #b10,000 mesos#k to get a replacement Zenumist Cape.");
            return;
        }

        if (!sm.askYesNo("You brought all the items. Do you want me to make you a new Zenumist Cape?")) {
            return;
        }

        sm.sayNext("Great job. Here, please take the Zenumist Cape.");
        sm.sayBoth("This will be the only time I'll make the cape for you this easily. A Zenumist Cape is a form of identification that proves that you're a member of Zenumist. If you are not wearing the cape, the you might risk being recognized as Alcadno. Please don't lose it... and remember, any alchemist that makes this cape will need to rest for the next 3 days to recuperate, so again... please don't lose it.");

        sm.removeItem(4000021, 10);
        sm.removeItem(4021003, 5);
        sm.addMoney(-10000);
        sm.addItem(1102135, 1);
        sm.forceStartQuest(3305);
        sm.forceCompleteQuest(3305);
    }

    @Script("q3306s")
    public static void q3306s(ScriptManager sm) {
        // Re-acquiring Alcadno Cape (3306 - start)
        // NPC 2111001 - Alcadno Society Chief
        // Lost cape replacement quest
        if (!sm.hasItem(4000021, 10) || !sm.hasItem(4021006, 5) || !sm.canAddMoney(-10000)) {
            sm.sayOk("You need #b10 Pig Ribbons#k, #b5 Gold Ores#k, and #b10,000 mesos#k to get a replacement Alcadno Cape.");
            return;
        }

        if (!sm.askYesNo("You brought all the items. Do you want me to make you a new Alcadno Cape?")) {
            return;
        }

        sm.sayNext("Since you have brought all the items, I'll make you another Alcadno Cape, but... please remember this. Wearing Alcadno Cape signifies that you are a member of Alcadno. The cape is also laden with a special spell. Please don't lose it. It can only be made once every 3 days.");
        sm.sayBoth("As a proud member of Alcadno, you should handle the cape with utmost pride and care.");

        sm.removeItem(4000021, 10);
        sm.removeItem(4021006, 5);
        sm.addMoney(-10000);
        sm.addItem(1102136, 1);
        sm.forceStartQuest(3306);
        sm.forceCompleteQuest(3306);
    }

    @Script("q3314e")
    public static void q3314e(ScriptManager sm) {
        // Life Alchemy, and the Missing Alchemist (3314 - end)
        // NPC 2111009 - Russellon
        // Auto-start quest completion (rewards handled by quest system)
        sm.addExp(12500);
        sm.forceCompleteQuest(3314);
    }

    @Script("q3320s")
    public static void q3320s(ScriptManager sm) {
        // What Parwen Knows (3320 - start)
        // NPC 2111006 - Parwen the Ghost
        // Part of "Parwen's Memory" questline
        sm.sayNext("Hmm? I guess you haven't met him yet... Do you not wish to go to the world of afterimage? Let me know if you are ready to head there.");
        sm.forceStartQuest(3320);
    }

    @Script("q3321s")
    public static void q3321s(ScriptManager sm) {
        // Dr. De Lang, the Missing Alchemist (3321 - start)
        // NPC 2111006 - Parwen the Ghost
        final int answer1 = sm.askMenu("...Hmm... You look quite indifferent. I thought the person you mentioned was that alchemist, so I brought you to him but... was he the right person? I guess not... I mean, unlike you, he has that dark, gloomy shadow that's cast over him. He's someone that's hard to approach.", java.util.Map.of(
                0, "Any idea where he is?"
        ));

        final int answer2 = sm.askMenu("Where? Hmm... there's no such place where he resides. It's just a afterimage from the past.", java.util.Map.of(
                0, "Can you tell me where he is right now?"
        ));

        sm.sayOk("There's no way you can tell. Like I said, it's just a afterimage. It's nothing important, so just forget about it.");
        sm.addExp(5000);
        sm.forceStartQuest(3321);
        sm.forceCompleteQuest(3321);
    }

    @Script("q3353s")
    public static void q3353s(ScriptManager sm) {
        // What De Lang Wants (3353 - start)
        // NPC 2111006 - Parwen the Ghost
        // Auto-start when entering map 261020401
        sm.sayNext("Huh? You didn't meet with De Lang yet? What are you doing just standing around here then?");
        sm.forceStartQuest(3353);
    }

    @Script("q3354s")
    public static void q3354s(ScriptManager sm) {
        // De Lang's Potion (3354 - start)
        // NPC 2111001 - Alcadno Society Chief
        sm.sayNext("Hmm... this is interesting. Why isn't he banned?");
        sm.addExp(100);
        sm.forceStartQuest(3354);
        sm.forceCompleteQuest(3354);
    }

    @Script("q3360s")
    public static void q3360s(ScriptManager sm) {
        // Verifying the Password (3360 - start)
        // NPC 2111006 - Parwen the Ghost
        // Part of "The Secret, Quiet Passage" questline
        if (!sm.askYesNo("How was it? Were you able to gain access to the Secret Passage? I knew it. My brain's still working well... even though I don't have one now.")) {
            return;
        }

        sm.sayOk("The existence of the Secret Passage should not be made public to others. Why? Because inside the Secret Passage, you'll be led to... uhm, never mind. You didn't hear that from me. Anyway, keep this a secret, okay?");
        sm.setQRValue(3360, "1");
        sm.forceStartQuest(3360);
        sm.forceCompleteQuest(3360);
    }

    @Script("q3382s")
    public static void q3382s(ScriptManager sm) {
        // Yulete's Reward (3382 - start)
        // NPC 2112014 - Yulete
        sm.sayNext("It's been a while. How are things with #m261000000#? I have been spending most of my time reading at home, and have been detached from the rest of the world... Hmm? Why are you asking me questions on my experiment?  ...! Is that what I think it is that you're holding? #t4001159# and #t4001160#...?");

        if (!sm.askYesNo("Do you have the Zenumist and Alcadno marbles?")) {
            sm.sayOk("Hmm... are they really #t4001159# and #t4001160#? My eyes have gone bad...");
            return;
        }

        sm.sayNext("Ah... someone like you may hold this marble without any problems, since you experienced firsthand the conflict within #m261000000#, and yet you've managed to keep your integrity in tact, not taking any sides on issues... Thanks to you I am here safely conducting experiments, right? Again, thank you.");
        sm.sayBoth("As my way of saying thanks... I know this may not be much, but I think it'll help you a bit. As someone that studied both Zenumist and Alcadno methods, I feel that I can create a #bnew power by combining the essence of the two studies#k... what do you think? Are you interested?");
        sm.sayBoth("Then I want you to gather up more #b#t4001159#s#k and #b#t4001160#s#k. I need more marbles of the two in order to fuse the two together. Once I get enough marbles, I'll make something you've always wanted.");
        sm.forceStartQuest(3382);
    }

    @Script("q3382e")
    public static void q3382e(ScriptManager sm) {
        // Yulete's Reward (3382 - end)
        // NPC 2112014 - Yulete
        // Auto-complete via quest system (no dialogue needed)
    }
}
