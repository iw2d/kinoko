package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.job.Job;
import kinoko.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class MinarForest extends ScriptHandler {
    public static final int MAGIC_SEED = 4031346;
    public static final int MINI_DRACO_TRANSFORMATION = 2210016;

    @Script("job4_item")
    public static void job4_item(ScriptManager sm) {
        // Chief Tatamo (2081000)
        //   Leafre : Leafre (240000000)
        String likenessString = sm.getQRValue(QuestRecordType.TatamoLikeness);
        if (likenessString == null || likenessString.isEmpty()) {
            likenessString = "000000";
            sm.setQRValue(QuestRecordType.TatamoLikeness, likenessString);
        }
        final int likeness = Integer.parseInt(likenessString);
        final int answer = sm.askMenu("...Can I help you?", Map.of(
                0, "Buy the Magic Seed",
                1, "Do something for Leafre"
        ));
        if (answer == 0) {
            // Buy the Magic Seed
            final BiConsumer<Integer, Integer> buyMagicSeed = (count, price) -> {
                if (count == 0) {
                    sm.sayOk("I can't sell you 0.");
                } else {
                    final int total = count * price;
                    if (sm.askYesNo(String.format("Buying #b%d Magic Seed(s)#k will cost you #b%,d mesos#k. Are you sure you want to make the purchase?", count, total))) {
                        if (sm.canAddItem(MAGIC_SEED, count) && sm.addMoney(-total)) {
                            sm.addItem(MAGIC_SEED, count);
                            sm.sayOk("See you again~");
                        } else {
                            sm.sayOk("Please check and see if you have enough mesos to make the purchase. Also, I suggest you check the etc. inventory and see if you have enough space available to make the purchase.");
                        }
                    } else {
                        sm.sayOk("Please think carefully. Once you have made your decision, let me know.");
                    }
                }
            };
            if (likeness < 5000) {
                if (sm.askMenu("You don't seem to be from our town. How can I help you?", Map.of(0, "I would like some Magic Seed.")) == 0) {
                    final int count = sm.askNumber("#bMagic Seed#k is a precious item; I cannot give it to you just like that. How about doing me a little favor? Then I'll give it to you. I'll sell the #bMagic Seed#k to you for #b30,000 mesos#k each.\r\nAre you willing to make the purchase? How many would you like, then?", 0, 0, 100);
                    buyMagicSeed.accept(count, 30000);
                }
            } else if (likeness < 24000) {
                if (sm.askMenu("Haven't we met before? No wonder you looked familiar. Hahaha...\r\nHow can I help you this time?", Map.of(0, "I would like some Magic Seed.")) == 0) {
                    sm.sayNext("Ahh~ now I remember. If I'm mistaken, I gave you some #bMagic Seed#k before. How was it? I'm guessing you are more than satisfied with your previous purchase based on the look on your face.");
                    final int count = sm.askNumber("#bMagic Seed#k is a precious item; I cannot give it to you just like that. How about doing me a little favor? Then I'll give it to you. I'll sell the #bMagic Seed#k to you for #b27,000 mesos#k each.\r\nAre you willing to make the purchase? How many would you like, then?", 0, 0, 100);
                    buyMagicSeed.accept(count, 27000);
                }
            } else if (likeness < 50000) {
                if (sm.askMenu("It's a beautiful day again today. Days like this should be spent out in the park on a picnic with your family. I have to admit, when I first met you, I had my reservations, what with you not being from this town and all ... but now, I feel more than comfortable doing business with you.\r\nHow can I help you this time?", Map.of(0, "I would like some Magic Seed.")) == 0) {
                    final int count = sm.askNumber("#bMagic Seed#k is a rare, precious item indeed, but now that we have been acquainted for quite some time, I'll give you a special discount. How about #b24,000 mesos#k for a #bMagic Seed#k? It's cheaper than flying over here through the ship! How many would you like?", 0, 0, 100);
                    buyMagicSeed.accept(count, 24000);
                }
            } else if (likeness < 200000) {
                if (sm.askMenu("Hmmm ... It seems like Birk is crying out loud much louder than usual today. When Birk cries, it signals the fact that the egg of the baby dragon is ready to be hatched any minute now. Now that you have become part of the family here, I would like for you to personally witness the birth of the baby dragon when that time comes. \r\nDo you need something from me today?", Map.of(0, "I would like some Magic Seed.")) == 0) {
                    final int count = sm.askNumber("You must have run out of the #bMagic Seed#k. We have grown very close to one another, and it doesn't sound too good for me to ask you for something in return, but please understand that the #bMagic Seed#k is very rare and hard to come by. How about #b18,000 mesos#k for #b1 Magic Seed#k? How many would you like to get?", 0, 0, 100);
                    buyMagicSeed.accept(count, 18000);
                }
            } else if (likeness < 800000) {
                sm.sayNext("Ohh hoh. I had a feeling that you'd be coming here right about now ...\r\nanyway, a while ago, a huge war erupted at the Dragon Shrine, where the dragons reside. Did you hear anything about it?");
                if (sm.askMenu("The sky shook, and the ground trembled as this incredibly loud thud covered every part of the forest. The baby dragons are now shivering in fear, wondering what may happen next. I wonder what actually happened... anyway, you're here for the seed, right?", Map.of(0, "I would like some Magic Seed.")) == 0) {
                    final int count = sm.askNumber("I knew it. I can now tell just by looking at your eyes. I know that you will always be there for us here. We both understand that the #bMagic Seed#k is a precious item, but for you, I'll sell it to you for #b12,000 mesos#k. How many would you like?", 0, 0, 100);
                    buyMagicSeed.accept(count, 12000);
                }
            } else {
                if (sm.askMenu("Aren't you here for the Magic Seed? A lot of time has passed since we first met, and now I feel a sense of calmness and relief whenever I talk to you. People in this town love you, and I think the same way about you. You're a true friend.", Map.of(0, "Thank you so much for such kind words. I'd love to get some Magic Seeds right now.")) == 0) {
                    final int count = sm.askNumber("You know I always have them ready. Just give me #b8,000 mesos#k per seed. We've been friends for a while, anyway. How many would you like?", 0, 0, 100);
                    buyMagicSeed.accept(count, 8000);
                }
            }
        } else if (answer == 1) {
            // Do something for Leafre
            final BiConsumer<Integer, Integer> donateItem = (itemId, inc) -> {
                // Ask number of items to donate
                final int maxCount = sm.getItemCount(itemId);
                if (maxCount > 0) {
                    final int count = sm.askNumber(String.format("How many #b#t%d##k's would you like to donate?\r\n#b< Owned : %d >#k", itemId, maxCount), 0, 0, maxCount);
                    if (count == 0) {
                        sm.sayOk("Think about it, and then let me know your decision.");
                    } else if (count > 0 && sm.removeItem(itemId, count)) {
                        final int newLikeness = Math.min(likeness + (count * inc), 800000);
                        sm.setQRValue(QuestRecordType.TatamoLikeness, String.format("%06d", newLikeness));
                        sm.sayOk("Thank you very much.");
                    } else {
                        sm.sayOk("Please check and see if you have enough of the item.");
                    }
                } else {
                    sm.sayOk("I don't think you have the item.");
                }
            };
            if (likeness < 5000) {
                sm.sayNext("It is the chief's duty to make the town more hospitable for people to live in, and carrying out the duty will require lots of items. If you have collected items around Leafre, are you interested in donating them?");
            } else if (likeness < 24000) {
                sm.sayNext("You're the person that graciously donated some great items to us before. I cannot tell you how helpful that really was. By the way, if you have collected items around Leafre, are you interested in donating them to us once more?");
            } else if (likeness < 50000) {
                sm.sayNext("You came to see me again today. Thanks to your immense help, the quality of life in this town has been significantly upgraded. People in this town are very thankful of your contributions. By the way, if you have collected items around Leafre, are you interested in donating them to us once more?");
            } else if (likeness < 200000) {
                sm.sayNext("Hey, there! Your tremendous contribution to this town has resulted in our town thriving like no other. The town is doing really well as it is, but I'd appreciate it if you can help us out again. If you have collected items around Leafre, are you interested in donating them to us once more?");
            } else if (likeness < 800000) {
                sm.sayNext("It's you, the number 1 supporter of Leafre! Good things always seem to happen when you're in town. By the way, if you have collected items around Leafre, are you interested in donating them to us once more?");
            } else {
                sm.sayNext("Aren't you #b#h ##k? It's great to see you again! Thanks to your incredible work, our town is doing so well that I really don't have much to do these days. Everyone in this town seems to look up to you, and I mean that. I thoroughly appreciate your great help, but ... can you help us out once more? If you have collected items around Leafre, then would you be again interested in donating the items to us?");
            }
            final List<Tuple<Integer, Integer>> items = List.of(
                    Tuple.of(4000226, 2), // Rash's Furball
                    Tuple.of(4000229, 4), // Dark Rash's Furball
                    Tuple.of(4000236, 3), // Beetle's Horn
                    Tuple.of(4000237, 6), // Dual Beetle's Horn
                    Tuple.of(4000260, 3), // Hov's Shorts
                    Tuple.of(4000261, 6), // Pin Hov's Charm
                    Tuple.of(4000231, 7), // Hankie's Pan Flute
                    Tuple.of(4000238, 9), // Harp's Tail Feather
                    Tuple.of(4000239, 12), // Blood Harp's Crown
                    Tuple.of(4000241, 15), // Birk's Chewed Grass
                    Tuple.of(4000242, 20), // Dual Birk's Tiny Tail
                    Tuple.of(4000234, 20), // Kentaurus's Skull
                    Tuple.of(4000232, 20), // Kentaurus's Flame
                    Tuple.of(4000233, 20), // Kentaurus's Marrow
                    Tuple.of(4000235, 100), // Manon's Tail
                    Tuple.of(4000243, 100) // Griffey Horn
            );
            final Map<Integer, String> itemOptions = createOptions(items, (tuple) -> itemName(tuple.getLeft()));
            final int itemAnswer = sm.askMenu("Which item would you like to donate?", itemOptions);
            if (itemAnswer >= 0 && itemAnswer < items.size()) {
                donateItem.accept(items.get(itemAnswer).getLeft(), items.get(itemAnswer).getRight());
            }
        }
    }

    @Script("minar_job4")
    public static void minar_job4(ScriptManager sm) {
        // Leafre : Valley of the Antelope (240010500)
        //   in00 (622, -1025)
        sm.playPortalSE();
        sm.warp(240010501, "out00"); // Leafre : Forest of the Priest
    }

    @Script("gryphius")
    public static void gryphius(ScriptManager sm) {
        // Leafre : Battlefield of Fire and Darkness (240020100)
        //   boss00 (-50, 333)
        sm.playPortalSE();
        sm.warp(240020101, "out00"); // Leafre : Griffey Forest
    }

    @Script("mayong")
    public static void mayong(ScriptManager sm) {
        // Leafre : The Area of Blue Kentaurus (240020400)
        //   boss00 (1040, 452)
        sm.playPortalSE();
        sm.warp(240020401, "out00"); // Leafre : Manon's Forest
    }

    @Script("inNix1")
    public static void inNix1(ScriptManager sm) {
        // Leafre : Griffey Forest (240020101)
        //   in00 (735, 450)
        // Leafre : Griffey Forest (240020102)
        //   in00 (733, 451)
        sm.playPortalSE();
        sm.warp(240020600, "out00"); // Hidden Street : Isolated Forest
    }

    @Script("inNix2")
    public static void inNix2(ScriptManager sm) {
        // Leafre : Manon's Forest (240020401)
        //   in00 (-748, 451)
        // Leafre : Manon's Forest (240020402)
        //   in00 (-745, 451)
        sm.playPortalSE();
        sm.warp(240020600, "out01"); // Hidden Street : Isolated Forest
    }

    @Script("outNix1")
    public static void outNix1(ScriptManager sm) {
        // Hidden Street : Isolated Forest (240020600)
        //   out00 (-775, 453)
        sm.playPortalSE();
        sm.warp(240020101, "in00"); // Leafre : Griffey Forest
    }

    @Script("outNix2")
    public static void outNix2(ScriptManager sm) {
        // Hidden Street : Isolated Forest (240020600)
        //   out01 (866, 451)
        sm.playPortalSE();
        sm.warp(240020401, "in00"); // Leafre : Manon's Forest
    }


    // TEMPLE OF TIME SCRIPTS ------------------------------------------------------------------------------------------

    @Script("flyminidraco")
    public static void flyminidraco(ScriptManager sm) {
        // Corba : Retired Dragon Trainer (2082003)
        //   Leafre : Station (240000110)
        final int answer = sm.askMenu("If you had wings, I'm sure you could go there.  But, that alone won't be enough.  If you want to fly though the wind that's sharper than a blade, you'll need tough scales as well.  I'm the only Halfling left that knows the way back... If you want to go there, I can transform you.  No matter what you are, for this moment, you will become a #bDragon#k...", Map.of(
                0, "I want to become a dragon."
        ));
        if (answer == 0) {
            sm.setConsumeItemEffect(MINI_DRACO_TRANSFORMATION);
            sm.warp(200090500, "sp"); // In Flight : Way to Temple of Time
        }
    }

    @Script("outTemple")
    public static void outTemple(ScriptManager sm) {
        // Time Lane : Temple of Time (270000100)
        //   out00 (-1780, 176)
        sm.setConsumeItemEffect(MINI_DRACO_TRANSFORMATION);
        sm.playPortalSE();
        sm.warp(200090510, "in00"); // In Flight : Way to Minar Forest
    }

    @Script("templeenter")
    public static void templeenter(ScriptManager sm) {
        // In Flight : Way to Minar Forest (200090510)
        //   in00 (571, -227)
        sm.setConsumeItemEffect(MINI_DRACO_TRANSFORMATION);
        sm.playPortalSE();
        sm.warp(270000100, "out00"); // Time Lane : Temple of Time
    }

    @Script("undodraco")
    public static void undodraco(ScriptManager sm) {
        // In Flight : Way to Temple of Time (200090500)
        //   down00 (-763, 142)
        //   down01 (-665, 142)
        //   down02 (-568, 142)
        //   down03 (-471, 142)
        //   down04 (-373, 142)
        //   down05 (-275, 142)
        //   down06 (-179, 142)
        //   down07 (-82, 142)
        //   down08 (16, 142)
        //   down09 (114, 142)
        //   down10 (213, 142)
        //   down11 (311, 142)
        //   down12 (410, 142)
        //   down13 (508, 142)
        //   down14 (606, 142)
        //   down15 (704, 142)
        //   down16 (802, 142)
        //   down17 (902, 142)
        //   down18 (1001, 142)
        //   down19 (1101, 142)
        //   down20 (1200, 142)
        //   down21 (1298, 142)
        //   down22 (1395, 142)
        //   down23 (1492, 142)
        //   down24 (1591, 142)
        //   down25 (1690, 142)
        //   down26 (1787, 142)
        //   down27 (1885, 142)
        //   down28 (1981, 142)
        //   down29 (2079, 142)
        //   down30 (2177, 142)
        //   down31 (2275, 142)
        //   down32 (2372, 142)
        //   down33 (2471, 142)
        //   down34 (2567, 142)
        //   down35 (2666, 142)
        //   down36 (2764, 142)
        //   minar00 (-700, -214)
        // In Flight : Way to Minar Forest (200090510)
        //   down00 (760, 145)
        //   down01 (661, 145)
        //   down02 (563, 145)
        //   down03 (467, 145)
        //   down04 (371, 145)
        //   down05 (278, 145)
        //   down06 (182, 145)
        //   down07 (83, 145)
        //   down08 (-12, 145)
        //   down09 (-110, 145)
        //   down10 (-206, 145)
        //   down11 (-299, 145)
        //   down12 (-394, 145)
        //   down13 (-490, 145)
        //   down14 (-587, 145)
        //   down15 (-686, 145)
        //   down16 (-782, 145)
        //   down17 (-876, 145)
        //   down18 (-974, 145)
        //   down19 (-1072, 145)
        //   down20 (-1169, 145)
        //   down21 (-1359, 145)
        //   down22 (-1264, 145)
        //   down23 (-1457, 145)
        //   down24 (-1557, 145)
        //   down25 (-1655, 145)
        //   down26 (-1752, 145)
        //   down27 (-1851, 145)
        //   down28 (-1949, 145)
        //   down29 (-2047, 145)
        //   down30 (-2147, 145)
        //   down31 (-2246, 145)
        //   down32 (-2343, 145)
        //   down33 (-2442, 145)
        //   down34 (-2541, 145)
        //   down35 (-2640, 146)
        //   down36 (-2666, 77)
        sm.resetConsumeItemEffect(MINI_DRACO_TRANSFORMATION);
        sm.playPortalSE();
        sm.warp(240000110, "arrival00"); // Leafre : Station
    }

    @Script("undomorphdarco")
    public static void undomorphdarco(ScriptManager sm) {
        // Leafre : Station (240000110)
        sm.resetConsumeItemEffect(MINI_DRACO_TRANSFORMATION);
    }

    @Script("reundodraco")
    public static void reundodraco(ScriptManager sm) {
        // Leafre : Station (240000110)
        //   arrival00 (-196, 370)
        //   arrival01 (-63, 371)
        //   arrival02 (37, 371)
        //   arrival03 (170, 370)
        // Time Lane : Temple of Time (270000100)
        sm.resetConsumeItemEffect(MINI_DRACO_TRANSFORMATION);
    }


    // WARRIOR 4TH JOB ADVANCEMENT NPC ---------------------------------------------------------------------------

    @Script("warrior4")
    public static void warrior4(ScriptManager sm) {
        // Harmonia : The Warrior Instructor (2081100)
        //   Leafre : Forest of the Priest (240010501)

        // Check job and level
        if (!(sm.getJob() == 111 || sm.getJob() == 121 || sm.getJob() == 131)) {
            sm.sayOk("Why do you want to see me? There is nothing you want to ask me.");
            return;
        }

        if (sm.getLevel() < 120) {
            sm.sayOk("You're still weak to go to warrior extreme road. If you get stronger, come back to me.");
            return;
        }

        // Check if ready for 4th job change (Quest 6904 completed)
        if (sm.hasQuestCompleted(6904)) {
            // Ready for job advancement
            if (sm.getJob() == 111) {
                sm.sayNext("You're qualified to be a true warrior. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Hero.")) {
                    sm.setJob(Job.getById(112)); // Hero
                    sm.sayNext("You became the best warrior #bHero#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 121) {
                sm.sayNext("You're qualified to be a true warrior. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Paladin.")) {
                    sm.setJob(Job.getById(122)); // Paladin
                    sm.sayNext("You became the best warrior #bPaladin#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 131) {
                sm.sayNext("You're qualified to be a true warrior. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Dark Knight.")) {
                    sm.setJob(Job.getById(132)); // Dark Knight
                    sm.sayNext("You became the best warrior #bDark Knight#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            }
            return;
        }

        // Quest 6900 end - Receive letter from Tylus
        if (sm.hasQuestStarted(6900) && sm.hasItem(4031342, 1)) {
            sm.sayNext("Why do you want to see me, young warrior..");
            sm.sayBoth("#b#p2020008##k?... Is he the one in El Nath? Then I can trust you.");

            if (sm.askYesNo("A young warrior who wants increase their power. I have to tell you something. Talk to me only if you're ready to hear the truth. Many secrets will be revealed...")) {
                sm.removeItem(4031342, 1);
                sm.forceCompleteQuest(6900);
                sm.addExp(20000);
                sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
            } else {
                sm.sayOk("Come back when you're ready.");
            }
            return;
        }

        // Quest 6901 - First Story (Zakum)
        if (sm.hasQuestCompleted(6900) && !sm.hasQuestStarted(6901) && !sm.hasQuestCompleted(6901)) {
            sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");
            if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
                sm.forceStartQuest(6901);
                sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
                sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
                sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
                sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
                sm.forceCompleteQuest(6901);
                sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
            } else {
                sm.sayOk("Are you afraid? You've come this far...");
            }
            return;
        }

        // Quest 6902 - Second Story (Holychoras)
        if (sm.hasQuestCompleted(6901) && !sm.hasQuestStarted(6902) && !sm.hasQuestCompleted(6902)) {
            sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");
            if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
                sm.forceStartQuest(6902);
                sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
                sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
                sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
                sm.forceCompleteQuest(6902);
                sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        // Quest 6903 - Third Story (Ludibrium)
        if (sm.hasQuestCompleted(6902) && !sm.hasQuestStarted(6903) && !sm.hasQuestCompleted(6903)) {
            sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");
            if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
                sm.forceStartQuest(6903);
                sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
                sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
                sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
                sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
                sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
                sm.forceCompleteQuest(6903);
                sm.sayOk("Talk to me when you're ready.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
            }
            return;
        }

        // Quest 6904 - Final Trial
        if (sm.hasQuestCompleted(6903) && !sm.hasQuestStarted(6904)) {
            sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");
            if (sm.askYesNo("Get me two things: #b#t4031343##k and #b#t4031344##k. Are you ready?")) {
                sm.forceStartQuest(6904);
                sm.sayNext("Go and get #b#t4031343##k and #b#t4031344##k.");
                sm.sayOk("It's up to you how you get it. If you want to use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom, you can get them through #b#p2081000##k in Leafre.");
            } else {
                sm.sayOk("What are you afraid of? Great power awaits you..");
            }
            return;
        }

        // Quest 6904 - Turn in items
        if (sm.hasQuestStarted(6904) && !sm.hasQuestCompleted(6904)) {
            if (sm.hasItem(4031343, 1) && sm.hasItem(4031344, 1)) {
                sm.removeItem(4031343, 1);
                sm.removeItem(4031344, 1);
                sm.forceCompleteQuest(6904);
                sm.addExp(50000);
                sm.sayNext("You proved your quality as a hero.");
                sm.sayOk("Now, what lies before you is the Way of a #bWarrior#k. Talk to me again if you are ready for the 4th job Advancement.");
            } else {
                sm.sayOk("You haven't gathered #b#t4031343##k and #b#t4031344##k. That's will prove your quality.");
            }
            return;
        }

        // Default message
        sm.sayOk("You're not ready to make 4th job advancement. When you're ready, talk to me.");
    }

    // THIEF 4TH JOB ADVANCEMENT NPC ---------------------------------------------------------------------------

    @Script("thief4")
    public static void thief4(ScriptManager sm) {
        // Hellin : The Assassin Instructor (2081400)
        //   Leafre : Forest of the Priest (240010501)

        // Check job and level
        if (!(sm.getJob() == 411 || sm.getJob() == 421 || sm.getJob() == 433)) {
            sm.sayOk("Why do you want to see me? There is nothing you want to ask me.");
            return;
        }

        if (sm.getLevel() < 120) {
            sm.sayOk("You're still weak to go to thief extreme road. If you get stronger, come back to me.");
            return;
        }

        // Check if ready for 4th job change (Quest 6934 completed OR Dual Blade with Secret Scroll)
        final boolean hasDualBladeSecretScroll = (sm.getJob() == 433 && sm.hasItem(4031348, 1));
        if (sm.hasQuestCompleted(6934) || hasDualBladeSecretScroll) {
            // Ready for job advancement
            if (sm.getJob() == 411) {
                sm.sayNext("You're qualified to be a true thief. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Night Lord.")) {
                    sm.setJob(Job.getById(412)); // Night Lord
                    sm.sayNext("You became the best thief #bNight Lord#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 421) {
                sm.sayNext("You're qualified to be a true thief. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Shadower.")) {
                    sm.setJob(Job.getById(422)); // Shadower
                    sm.sayNext("You became the best thief #bShadower#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 433) {
                sm.sayNext("You're qualified to be a true thief. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Dual Master.")) {
                    // Remove Secret Scroll if quest wasn't completed
                    if (!sm.hasQuestCompleted(6934) && sm.hasItem(4031348, 1)) {
                        sm.removeItem(4031348, 1);
                    }
                    sm.setJob(Job.BLADE_MASTER); // Job 434
                    sm.sayNext("You became the best thief #bDual Master#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            }
            return;
        }

        // Quest 6930 end - Receive letter from Arec
        if (sm.hasQuestStarted(6930) && sm.hasItem(4031516, 1)) {
            sm.sayNext("What are you doing here, young Thief?");
            sm.sayBoth("#b#p2020011##k?... The one in El Nath? Then I can trust you.");

            if (sm.askYesNo("A young Thief dreaming of being a Nightlord or Shadower. I have a few stories to tell you, my stealthy friend. Are you ready?")) {
                sm.removeItem(4031516, 1);
                sm.forceCompleteQuest(6930);
                sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
            } else {
                sm.sayOk("Come back when you're ready.");
            }
            return;
        }

        // Quest 6931 - First Story (Zakum)
        if (sm.hasQuestCompleted(6930) && !sm.hasQuestStarted(6931) && !sm.hasQuestCompleted(6931)) {
            sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");
            if (sm.askYesNo("Good. You have the right to listen to the story. Are you ready?")) {
                sm.forceStartQuest(6931);
                sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
                sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
                sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
                sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople..");
                sm.forceCompleteQuest(6931);
                sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        // Quest 6932 - Second Story (Holychoras)
        if (sm.hasQuestCompleted(6931) && !sm.hasQuestStarted(6932) && !sm.hasQuestCompleted(6932)) {
            sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");
            if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
                sm.forceStartQuest(6932);
                sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
                sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
                sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
                sm.forceCompleteQuest(6932);
                sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        // Quest 6933 - Third Story (Ludibrium)
        if (sm.hasQuestCompleted(6932) && !sm.hasQuestStarted(6933) && !sm.hasQuestCompleted(6933)) {
            sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");
            if (sm.askYesNo("Yes, you do have the right to listen to the stories. Are you ready?")) {
                sm.forceStartQuest(6933);
                sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where the time stopped.");
                sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
                sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
                sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
                sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
                sm.forceCompleteQuest(6933);
                sm.sayOk("Talk to me when you're ready for the final trial.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        // Quest 6934 - Final Trial
        if (sm.hasQuestCompleted(6933) && !sm.hasQuestStarted(6934)) {
            sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");
            if (sm.askYesNo("Get me two things: #b#t4031517##k and #b#t4031518##k. Are you ready?")) {
                sm.forceStartQuest(6934);
                sm.sayOk("It's up to you how you get it. If you wanna use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom and warm heart, you can get them through #b#p2081000##k in Leafre.");
            } else {
                sm.sayOk("What are you afraid of? Great power awaits you...");
            }
            return;
        }

        // Quest 6934 - Turn in items
        if (sm.hasQuestStarted(6934) && !sm.hasQuestCompleted(6934)) {
            if (sm.hasItem(4031517, 1) && sm.hasItem(4031518, 1)) {
                sm.removeItem(4031517, 1);
                sm.removeItem(4031518, 1);
                sm.forceCompleteQuest(6934);
                sm.sayNext("You proved your quality as a hero.");
                sm.sayOk("Now you only have to go to the way of a Shadower or Nightlord. Talk to me if you're ready for the 4th job advancement.");
            } else {
                sm.sayOk("Haven't you got #b#t4031517##k and #b#t4031518##k?");
            }
            return;
        }

        // Default message
        sm.sayOk("You're not ready to make 4th job advancement. When you're ready, talk to me.");
    }

    // MAGICIAN 4TH JOB ADVANCEMENT NPC ---------------------------------------------------------------------------

    @Script("wizard4")
    public static void wizard4(ScriptManager sm) {
        // Bishop : The Magician Instructor (2081200)
        //   Leafre : Forest of the Priest (240010501)

        // Check job and level
        if (!(sm.getJob() == 211 || sm.getJob() == 221 || sm.getJob() == 231)) {
            sm.sayOk("Why do you want to see me? There is nothing you want to ask me.");
            return;
        }

        if (sm.getLevel() < 120) {
            sm.sayOk("You're still weak to go to magician extreme road. If you get stronger, come back to me.");
            return;
        }

        // Check if ready for 4th job change (Quest 6914 completed)
        if (sm.hasQuestCompleted(6914)) {
            // Ready for job advancement
            if (sm.getJob() == 211) {
                sm.sayNext("You're qualified to be a true magician. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Arch Mage (Fire, Poison).")) {
                    sm.setJob(Job.getById(212)); // Arch Mage F/P
                    sm.sayNext("You became the best magician #bArch Mage (Fire, Poison)#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 221) {
                sm.sayNext("You're qualified to be a true magician. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Arch Mage (Ice, Lightning).")) {
                    sm.setJob(Job.getById(222)); // Arch Mage I/L
                    sm.sayNext("You became the best magician #bArch Mage (Ice, Lightning)#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 231) {
                sm.sayNext("You're qualified to be a true magician. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Bishop.")) {
                    sm.setJob(Job.getById(232)); // Bishop
                    sm.sayNext("You became the best magician #bBishop#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            }
            return;
        }

        // Quest 6910 end - Receive letter from Robeira
        if (sm.hasQuestStarted(6910) && sm.hasItem(4031510, 1)) {
            sm.sayNext("What are you doing here young magician?");
            sm.sayBoth("#b#p2020009##k?... That's the one who lives in El Nath. If she recommended you, I can trust you.");

            if (sm.askYesNo("A young magician dreaming of being an Arch Mage. I have to tell you a few stories. Talk to me when you're ready.")) {
                sm.removeItem(4031510, 1);
                sm.forceCompleteQuest(6910);
                sm.addExp(20000);
                sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
            } else {
                sm.sayOk("Come back when you're ready.");
            }
            return;
        }

        // Quest 6911-6913 story quests (same as Warrior/Thief pattern)
        if (sm.hasQuestCompleted(6910) && !sm.hasQuestStarted(6911) && !sm.hasQuestCompleted(6911)) {
            sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");
            if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
                sm.forceStartQuest(6911);
                sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
                sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
                sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
                sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
                sm.forceCompleteQuest(6911);
                sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
            } else {
                sm.sayOk("Are you afraid? You've come this far...");
            }
            return;
        }

        if (sm.hasQuestCompleted(6911) && !sm.hasQuestStarted(6912) && !sm.hasQuestCompleted(6912)) {
            sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");
            if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
                sm.forceStartQuest(6912);
                sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
                sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
                sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
                sm.forceCompleteQuest(6912);
                sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        if (sm.hasQuestCompleted(6912) && !sm.hasQuestStarted(6913) && !sm.hasQuestCompleted(6913)) {
            sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");
            if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
                sm.forceStartQuest(6913);
                sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
                sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
                sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
                sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
                sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
                sm.forceCompleteQuest(6913);
                sm.sayOk("Talk to me when you're ready.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
            }
            return;
        }

        // Quest 6914 - Final Trial
        if (sm.hasQuestCompleted(6913) && !sm.hasQuestStarted(6914)) {
            sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");
            if (sm.askYesNo("Get me two things: #b#t4031511##k and #b#t4031512##k. Are you ready?")) {
                sm.forceStartQuest(6914);
                sm.sayNext("Get me #b#t4031511##k and #b#t4031512##k...");
                sm.sayOk("It's up to you how you get it. If you wanna use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom and warm heart, you can get them through #b#p2081000##k in Leafre.");
            } else {
                sm.sayOk("What are you afraid of? Great power awaits you...");
            }
            return;
        }

        // Quest 6914 - Turn in items
        if (sm.hasQuestStarted(6914) && !sm.hasQuestCompleted(6914)) {
            if (sm.hasItem(4031511, 1) && sm.hasItem(4031512, 1)) {
                sm.removeItem(4031511, 1);
                sm.removeItem(4031512, 1);
                sm.forceCompleteQuest(6914);
                sm.addExp(50000);
                sm.sayNext("You proved your quality as a hero.");
                sm.sayOk("Now you only have to go to the way of an Arch Mage. Talk to me if you're ready for the 4th job advancement.");
            } else {
                sm.sayOk("You haven't found #b#t4031511##k and #b#t4031512##k.");
            }
            return;
        }

        // Default message
        sm.sayOk("You're not ready to make 4th job advancement. When you're ready, talk to me.");
    }

    // BOWMAN 4TH JOB ADVANCEMENT NPC ---------------------------------------------------------------------------

    @Script("bowman4")
    public static void bowman4(ScriptManager sm) {
        // Bishop : The Bowman Instructor (2081300)
        //   Leafre : Forest of the Priest (240010501)

        // Check job and level
        if (!(sm.getJob() == 311 || sm.getJob() == 321)) {
            sm.sayOk("Why do you want to see me? There is nothing you want to ask me.");
            return;
        }

        if (sm.getLevel() < 120) {
            sm.sayOk("You're still weak to go to bowman extreme road. If you get stronger, come back to me.");
            return;
        }

        // Check if ready for 4th job change (Quest 6924 completed)
        if (sm.hasQuestCompleted(6924)) {
            // Ready for job advancement
            if (sm.getJob() == 311) {
                sm.sayNext("You're qualified to be a true bowman. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Bow Master.")) {
                    sm.setJob(Job.getById(312)); // Bow Master
                    sm.sayNext("You became the best bowman #bBow Master#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 321) {
                sm.sayNext("You're qualified to be a true bowman. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Marksman.")) {
                    sm.setJob(Job.getById(322)); // Marksman
                    sm.sayNext("You became the best bowman #bMarksman#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            }
            return;
        }

        // Quest 6920 end - Receive letter from Rene
        if (sm.hasQuestStarted(6920) && sm.hasItem(4031513, 1)) {
            sm.sayNext("What are you doing here, young Bowman?");
            sm.sayBoth("#b#p2020010##k?... The one in El Nath? Then I can trust you.");

            if (sm.askYesNo("A young Bowman dreaming of being a Bowmaster or Marksman. I have to tell you a few stories first. Talk to me when you're ready.")) {
                sm.removeItem(4031513, 1);
                sm.forceCompleteQuest(6920);
                sm.addExp(20000);
                sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
            } else {
                sm.sayOk("Come back when you're ready.");
            }
            return;
        }

        // Quest 6921-6923 story quests (same pattern)
        if (sm.hasQuestCompleted(6920) && !sm.hasQuestStarted(6921) && !sm.hasQuestCompleted(6921)) {
            sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");
            if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
                sm.forceStartQuest(6921);
                sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
                sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
                sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
                sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
                sm.forceCompleteQuest(6921);
                sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
            } else {
                sm.sayOk("Are you afraid? You've come this far...");
            }
            return;
        }

        if (sm.hasQuestCompleted(6921) && !sm.hasQuestStarted(6922) && !sm.hasQuestCompleted(6922)) {
            sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");
            if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
                sm.forceStartQuest(6922);
                sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
                sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
                sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
                sm.forceCompleteQuest(6922);
                sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        if (sm.hasQuestCompleted(6922) && !sm.hasQuestStarted(6923) && !sm.hasQuestCompleted(6923)) {
            sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");
            if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
                sm.forceStartQuest(6923);
                sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
                sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
                sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
                sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
                sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
                sm.forceCompleteQuest(6923);
                sm.sayOk("Talk to me when you're ready.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
            }
            return;
        }

        // Quest 6924 - Final Trial
        if (sm.hasQuestCompleted(6923) && !sm.hasQuestStarted(6924)) {
            sm.sayNext("Now I'll give you the last task to do the 4th job advancement.");
            if (sm.askYesNo("Get me two things. Nothing too hard. You have to bring me #b#t4031514##k and #b#t4031515##k.")) {
                sm.forceStartQuest(6924);
                sm.sayOk("It's up to you how you get it. If you wanna use your power and courage, you can catch #bManon and Griffey#k. If you wanna use wisdom and warm heart, you can get them through #b#p2081000##k in Leafre.");
            } else {
                sm.sayOk("What are you afraid of? Great power awaits you...");
            }
            return;
        }

        // Quest 6924 - Turn in items
        if (sm.hasQuestStarted(6924) && !sm.hasQuestCompleted(6924)) {
            if (sm.hasItem(4031514, 1) && sm.hasItem(4031515, 1)) {
                sm.removeItem(4031514, 1);
                sm.removeItem(4031515, 1);
                sm.forceCompleteQuest(6924);
                sm.addExp(50000);
                sm.sayNext("You proved your quality as a hero.");
                sm.sayOk("Now you only have to go to the way of a Bowmaster or Marksman. Talk to me if you're ready for the 4th job advancement.");
            } else {
                sm.sayOk("You haven't got #b#t4031514##k and #b#t4031515##k...");
            }
            return;
        }

        // Default message
        sm.sayOk("You're not ready to make 4th job advancement. When you're ready, talk to me.");
    }

    // PIRATE 4TH JOB ADVANCEMENT NPC ---------------------------------------------------------------------------

    @Script("pirate4")
    public static void pirate4(ScriptManager sm) {
        // Priest : The Pirate Instructor (2081500)
        //   Leafre : Forest of the Priest (240010501)

        // Check job and level
        if (!(sm.getJob() == 511 || sm.getJob() == 521)) {
            sm.sayOk("Why do you want to see me? There is nothing you want to ask me.");
            return;
        }

        if (sm.getLevel() < 120) {
            sm.sayOk("You're still weak to go to pirate extreme road. If you get stronger, come back to me.");
            return;
        }

        // Check if ready for 4th job change (Quest 6944 completed)
        if (sm.hasQuestCompleted(6944)) {
            // Ready for job advancement
            if (sm.getJob() == 511) {
                sm.sayNext("You're qualified to be a true pirate. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Viper.")) {
                    sm.setJob(Job.getById(512)); // Viper (Buccaneer)
                    sm.sayNext("You became the best pirate #bViper#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            } else if (sm.getJob() == 521) {
                sm.sayNext("You're qualified to be a true pirate. Do you want job advancement?");
                if (sm.askYesNo("I want to advance to Captain.")) {
                    sm.setJob(Job.getById(522)); // Captain (Corsair)
                    sm.sayNext("You became the best pirate #bCaptain#k.");
                    sm.sayOk("Don't forget that it all depends on how much you train.");
                }
            }
            return;
        }

        // Quest 6940 end - Receive letter from Pedro
        if (sm.hasQuestStarted(6940) && sm.hasItem(4031859, 1)) {
            sm.sayNext("What made you come all the way here to see me, young Pirate?");
            sm.sayBoth("#b#p2020013##k... Are you talking about the one in El Nath? If he's the one that recommended you, then you must be legit.");

            if (sm.askYesNo("Hello young Pirate, the one who strives to walk the path of the ultimate. I have a story I must share with you. When you are ready to see the truth, talk to me.")) {
                sm.removeItem(4031859, 1);
                sm.forceCompleteQuest(6940);
                sm.addExp(20000);
                sm.sayNext("Good. Talk to me when you're ready to hear the first story.");
            } else {
                sm.sayOk("Come back when you're ready.");
            }
            return;
        }

        // Quest 6941-6943 story quests (same pattern)
        if (sm.hasQuestCompleted(6940) && !sm.hasQuestStarted(6941) && !sm.hasQuestCompleted(6941)) {
            sm.sayNext("The first story is about the town vanished into lava volcano. Would you like to listen?");
            if (sm.askYesNo("Good. You should listen to the story. Are you ready?")) {
                sm.forceStartQuest(6941);
                sm.sayNext("Have you ever been to the deep lava volcano in the El Nath mountains? There used to be a town there.");
                sm.sayBoth("People in the town worshipped a human shaped stone statue and the volcano. They built an altar and stone statue under the tree at the basin of the deepest volcano and worshiped the altar to prove their faith.");
                sm.sayBoth("Then disaster struck. It was the wicked Zakum's tree. Zakum's spirit didn't have a body but instead possessed the stone statue that people built. His evil rapidly spread through the town...");
                sm.sayBoth("After that, the town disappeared. You've heard about the fearsome power called #bZakum#k sleeping under the lava volcano, as you've traveled the Maple World for a long time. Now you know how he came to be. You can guess what happened to the townspeople...");
                sm.forceCompleteQuest(6941);
                sm.sayOk("That's the end of the first story. Talk to me when you're ready to listen to the second story.");
            } else {
                sm.sayOk("Are you afraid? You've come this far...");
            }
            return;
        }

        if (sm.hasQuestCompleted(6941) && !sm.hasQuestStarted(6942) && !sm.hasQuestCompleted(6942)) {
            sm.sayNext("The second story is about a growing stone and the Aquarium. Do you want to hear about it?");
            if (sm.askYesNo("Good. You deserve to listen to the story. Are you ready?")) {
                sm.forceStartQuest(6942);
                sm.sayNext("Have you been to the Aquarium under the sea? It is a mysterious place, floating over the valley of the deep sea. Haven't you wondered how it floats?");
                sm.sayBoth("There is a stone called the #bHolychoras#k underneath the valley of the deep sea. Nobody knows how came to be there, but it has been there for ages.");
                sm.sayBoth("#bHolychoras#k has a strange power that purifies the sea. That's how Aqua Road is purified and the Aquarium can float over the valley under the sea. If #bHolychoras#k disappears, the sea will grow dark...");
                sm.forceCompleteQuest(6942);
                sm.sayOk("This is the end of the second story. Talk to me when you're ready to listen to the third story.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged.");
            }
            return;
        }

        if (sm.hasQuestCompleted(6942) && !sm.hasQuestStarted(6943) && !sm.hasQuestCompleted(6943)) {
            sm.sayNext("The third is the story about the lake where time stopped. Do you wanna listen?");
            if (sm.askYesNo("Yes, you do have the right to listen to this story. Are you ready?")) {
                sm.forceStartQuest(6943);
                sm.sayNext("When you go to the north, there's a big lake. Bigger than a forest! People call this lake Ludus or the lake where time stopped.");
                sm.sayBoth("Near the Lake lies Ludibrium Castle, supported by huge towers. The Holy Power of the clock tower in the middle of Ludibrium Castle protects the castle by stopping the time there.");
                sm.sayBoth("But the dimensional crack in Ludibrium castle is getting wider and wider. A wicked power has invaded through the crack and changed the castle. You could probably feel the power of beings from the other dimension. They are called Alishar and Papulatus, and both are getting stronger.");
                sm.sayBoth("You know why I'm telling you this story. The 4th job advancement requires more responsibility. You must know what you'll be up against.");
                sm.sayBoth("You have to understand the Maple World more and behave as a true hero. Now I'll give you the last task for the 4th job advancement.");
                sm.forceCompleteQuest(6943);
                sm.sayOk("Talk to me when you're ready.");
            } else {
                sm.sayOk("Are you afraid? You came this far. Don't be discouraged...");
            }
            return;
        }

        // Quest 6944 - Final Trial
        if (sm.hasQuestCompleted(6943) && !sm.hasQuestStarted(6944)) {
            sm.sayNext("I will now give you the last task required to complete the 4th job advancement.");
            if (sm.askYesNo("It is your mission to acquire two items that I assign to you. I want #b#t4031517##k and #b#t4031518##k.")) {
                sm.forceStartQuest(6944);
                sm.sayNext("How you will acquire these items, I'll leave that up to you. If you want to acquire by fully utilizing your courage and physical capabilities, then you should get them through #bManon and Griffey#k. If you want to acquire them using brains and wisdom, then head to Leafre and see #b#p2081000##k.");
            } else {
                sm.sayOk("What is there for you to fear? You are walking the path of Pirate greatness, and you don't wish to encounter hardship?");
            }
            return;
        }

        // Quest 6944 - Turn in items
        if (sm.hasQuestStarted(6944) && !sm.hasQuestCompleted(6944)) {
            if (sm.hasItem(4031860, 1) && sm.hasItem(4031861, 1)) {
                sm.removeItem(4031860, 1);
                sm.removeItem(4031861, 1);
                sm.forceCompleteQuest(6944);
                sm.addExp(50000);
                sm.sayNext("You have proven your worth as a person that can be called a hero.");
                sm.sayOk("What you'll need to do now is to keep walking the path of great Pirates. Talk to me when you are ready to make the job advancement.");
            } else {
                sm.sayOk("I don't think you have acquired #b#t4031517##k and #b#t4031518##k, yet.");
            }
            return;
        }

        // Default message
        sm.sayOk("You're not ready to make 4th job advancement. When you're ready, talk to me.");
    }

    // ARCHER 4TH JOB WRAPPER (game uses "archer4" instead of "bowman4")
    @Script("archer4")
    public static void archer4(ScriptManager sm) {
        bowman4(sm);
    }

    // MAGICIAN 4TH JOB WRAPPER (game uses "magician4" instead of "wizard4")
    @Script("magician4")
    public static void magician4(ScriptManager sm) {
        wizard4(sm);
    }
}
