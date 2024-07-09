package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class Minar extends ScriptHandler {
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
}
