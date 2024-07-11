package kinoko.script.event;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.quest.QuestRecordType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MoonBunny extends ScriptHandler {
    public static final int MOON_BUNNY = 9300061;
    public static final int MOON_REACTOR = 9101000;

    public static final int PRIMROSE_SEED = 4001453;
    public static final int MOON_BUNNYS_RICE_CAKE = 4001101;
    public static final int A_RICE_CAKE_ON_TOP_OF_MY_HEAD = 1002798;

    @Script("moonrabbit_exit")
    public static void moonrabbit_exit(ScriptManager sm) {
        // Hidden Street : Moon Bunny Lobby (910010500)
        //   out00 (-420, 267)
        UnityPortal.returnPortal(sm, 100000200, "event00"); // Henesys : Henesys Park
    }

    @Script("moonrabbit")
    public static void moonrabbit(ScriptManager sm) {
        // Tory (1012112)
        //   Henesys : Henesys Park (100000200)
        //   Hidden Street : Shortcut (910010100)
        //   Hidden Street : Shortcut (910010400)
        //   Hidden Street : Moon Bunny Lobby (910010500)
        if (sm.getFieldId() == 100000200) {
            // Henesys : Henesys Park
            if (sm.askYesNo("Would you like to move to Moon Bunny Lobby?")) {
                sm.setQRValue(QuestRecordType.UnityPortal, "");
                sm.warp(910010500); // Hidden Street : Moon Bunny Lobby
            }
        } else if (sm.getFieldId() == 910010500) {
            // Hidden Street : Moon Bunny Lobby
            final int answer = sm.askMenu("#e<Party Quest: Moon Bunny's Rice Cake>#n\r\nHello, I'm Tory. Have you been to Primrose Hill? It's a beautiful hill where primroses bloom. I hear that a tiger named Growlie over at Primerose Hill is hungry. Won't you go with your party members and help Growlie?\r\n", Map.of(
                    0, "Go to Primrose Hill",
                    1, "Learn about Primrose Hill"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("If you'd like to enter here, the leader of your party will have to talk to me. Talk to your party leader about this.");
                    return;
                }
                if (!sm.checkParty(3, 10)) {
                    sm.sayOk("You cannot enter because your party doesn't have 3 members. You need 3 party members at Lv. 10 or higher to enter, so double-check and talk to me again.");
                    return;
                }
                sm.removeItem(PRIMROSE_SEED);
                sm.removeItem(MOON_BUNNYS_RICE_CAKE);
                // Hidden Street : Primrose Hill -> Hidden Street : Back to Town
                sm.partyWarpInstance(910010000, "sp", 910010300, 600);
            } else if (answer == 1) {
                sm.sayNext("#e<Party Quest: Moon Bunny's Rice Cake>#n\r\nA mysterious Moon Bunny that only appears in #b#m910010000##k during full moons. #b#p1012112##k of #b#m100000200##k is looking for Maplers to find #r#t4001101##k for #b#p1012114##k. If you want to meet the Moon Bunny, plant Primrose Seeds in the designated locations and summon forth a full moon. Protect the Moon Bunny from wild animals until all #r10 Rice Cakes#k are made.\r\n#e - Level:#n 10 or above #r(Recommended Level: 10 - 20 )#k\r\n#e - Time Limit:#n 10 min.\r\n#e - Number of Participants:#n 3 to 6\r\n#e - Items:#n #v1002798# #t1002798#\r\n#b(obtained by giving Tory 10 Rice Cakes.)#k");
            }
        } else {
            // Hidden Street : Shortcut
            final int exitAnswer = sm.askMenu("I appreciate you giving some rice cakes for the hungry Growlie. It looks like you'll have nothing to do here now. Would you like to leave this place?", Map.of(
                    0, "I want to give you the rest of my rice cakes.",
                    1, "Yes, please get me out of here."
            ));
            if (exitAnswer == 0) {
                final int giveAnswer = sm.askMenu("Oh, my! You brought Moon Bunny's Rice Cakes for me? Well, I've prepared some gifts to show you my appreciation. How many rice cakes do you want to give me?", Map.of(
                        0, String.format("#t%d# x10 - #t%d#", MOON_BUNNYS_RICE_CAKE, A_RICE_CAKE_ON_TOP_OF_MY_HEAD)
                ));
                if (giveAnswer == 0) {
                    if (!sm.canAddItem(A_RICE_CAKE_ON_TOP_OF_MY_HEAD, 1)) {
                        sm.sayOk("Please check and see if you have enough space in your inventory.");
                        return;
                    }
                    if (!sm.removeItem(MOON_BUNNYS_RICE_CAKE, 10)) {
                        sm.sayOk("Are you sure you have rice cakes with you? Don't you tease me now!");
                        return;
                    }
                    sm.addItem(A_RICE_CAKE_ON_TOP_OF_MY_HEAD, 1);
                    sm.sayNext("Thank you so much. I'm really going to enjoy these.");
                }
            } else if (exitAnswer == 1) {
                sm.removeItem(PRIMROSE_SEED);
                sm.removeItem(MOON_BUNNYS_RICE_CAKE);
                sm.warp(910010500); // Hidden Street : Moon Bunny Lobby
            }
        }
    }

    @Script("moonrabbit_tiger")
    public static void moonrabbit_tiger(ScriptManager sm) {
        // Growlie (1012114)
        //   Hidden Street : Primrose Hill (910010000)
        //   Hidden Street : Primrose Hill (910010001)
        if (sm.getInstanceVariable("clear").equals("1")) {
            if (sm.getUser().isPartyBoss()) {
                sm.sayNext("Mmmm... this is delicious. Please come see me next time for more #b#t4001101##k, Have a good trip!");
                sm.partyWarp(910010100, "st00"); // Hidden Street : Shortcut
            } else {
                sm.sayOk("Please proceed with the party leader.");
            }
            return;
        }
        final Map<Integer, String> options = new HashMap<>();
        options.put(0, "Please tell me what this place is all about.");
        if (sm.getUser().isPartyBoss()) {
            options.put(1, "I have brought Moon Bunny's Rice Cake.");
        }
        options.put(2, "I would like to leave this place.");
        final int answer = sm.askMenu("Growl! I am Growlie, always ready to protect this place. What brought you here?", options);
        if (answer == 0) {
            sm.sayNext("This place can be best described as the prime spot where you can taste the delicious rice cakes made by Moon Bunny every full moon.");
            sm.sayBoth("Gather up the primrose seeds from the primrose leaves all over this area, and plant the seeds at the footing near the crescent moon to see the primrose bloom.");
            sm.sayBoth("When the flowers of primrose blooms, the full moon will rise, and that's when the Moon Bunnies will appear and start pounding the mill. Your task is to fight off the monsters to make sure that Moon Bunny can concentrate on making the best rice cake possible.");
            sm.sayBoth("I would like for you and your party members to cooperate and get me 10 rice cakes. I strongly advise you to get me the rice cakes within the allotted time.");
        } else if (answer == 1) {
            final int itemCount = sm.getItemCount(MOON_BUNNYS_RICE_CAKE);
            if (itemCount >= 10) {
                sm.sayNext("Oh... isn't this rice cake made by Moon Bunny? Please hand me the rice cake.");
                if (sm.removeItem(MOON_BUNNYS_RICE_CAKE, 10)) {
                    sm.broadcastScreenEffect("quest/party/clear");
                    sm.broadcastSoundEffect("Party1/Clear");
                    sm.addExpAll(1600);
                    sm.setInstanceVariable("clear", "1");
                    sm.sayBoth("Mmmm... this is delicious. Please come see me next time for more #b#t4001101##k, Have a good trip!");
                    sm.partyWarp(910010100, "st00"); // Hidden Street : Shortcut
                } else {
                    sm.sayNext("Did you happen to lose the rice cake?");
                }
            } else {
                sm.sayNext("I advise you to check and make sure you have gathered #b10 #t4001101#s#k.");
            }
        } else if (answer == 2) {
            if (sm.askYesNo("If you leave now, you will not be able to complete the mission. Are you sure you want to leave?")) {
                sm.sayNext("Alright, then. See you around.");
                sm.warp(910010300, "st00"); // Hidden Street : Back to Town
            } else {
                sm.sayNext("Good. Keep trying.");
            }
        }
    }

    @Script("moonrabbit_bonus")
    public static void moonrabbit_bonus(ScriptManager sm) {
        // Tommy (1012113)
        //   Hidden Street : Shortcut (910010100)
        //   Hidden Street : Pig Town (910010200)
        //   Hidden Street : Pig Town (910010201)
        //   Hidden Street : Back to Town (910010300)
        if (sm.getFieldId() == 910010100) {
            // Hidden Street : Shortcut
            sm.sayNext("Hello, there! I'm Tommy. There's a Pig Town nearby where we're standing. The pigs there are rowdy and uncontrollable to the point where they have stolen numerous weapons from travelers. They were kicked out from their towns, and are currently hiding out at the Pig Town.");
            if (sm.getUser().isPartyBoss()) {
                if (sm.askMenu("What do you think about making your way there with your party members and teach those rowdy pigs a lesson?", Map.of(0, "Yeah, that sounds good! Take me there!")) == 0) {
                    sm.partyWarpInstance(910010200, "sp", 910010400, 300);
                }
            } else {
                sm.sayPrev("If you really want to teach those pigs a lesson, then please enter the place through your party leader.");
            }
        } else if (sm.getFieldId() == 910010200) {
            // Hidden Street : Pig Town
            if (sm.askMenu("Would you like to stop hunting and leave this place?", Map.of(0, "Yes. I would like to leave this place.")) == 0) {
                sm.warp(910010400, "st00"); // Hidden Street : Shortcut
            }
        } else if (sm.getFieldId() == 910010300) {
            // Hidden Street : Back to Town
            if (sm.askMenu("I think you're done with everything here. Would you like to leave this place?", Map.of(0, "Yes. I would like to leave this place.")) == 0) {
                sm.removeItem(PRIMROSE_SEED);
                sm.removeItem(MOON_BUNNYS_RICE_CAKE);
                sm.warp(910010500); // Hidden Street : Moon Bunny Lobby
            }
        }
    }

    @Script("moonrabbit_mapEnter")
    public static void moonrabbit_mapEnter(ScriptManager sm) {
        // Hidden Street : Primrose Hill (910010000)
        // Hidden Street : Primrose Hill (910010001)
        sm.getField().setMobSpawn(false);
        sm.getField().blowWeather(5120016, "Primrose Seeds fall from Primroses. If you pick up the seeds and plant them near the moon, the Moon Bunny will appear.", 20);
    }

    @Script("moonItem0")
    public static void moonItem0(ScriptManager sm) {
        // moonItem0 (9102002)
        //   Hidden Street : Primrose Hill (910010000)
        //   Hidden Street : Primrose Hill (910010001)
        sm.dropRewards(List.of(
                Reward.item(PRIMROSE_SEED, 1, 1, 1.0)
        ));
    }

    @Script("moonMob0")
    public static void moonMob0(ScriptManager sm) {
        // moonMob0 (9101000)
        //   Hidden Street : Primrose Hill (910010000)
        //   Hidden Street : Primrose Hill (910010001)
        sm.spawnMob(MOON_BUNNY, MobAppearType.REGEN, -180, -196); // Moon Bunny
        sm.getField().setMobSpawn(true);
        sm.getField().blowWeather(5120016, "Protect the Moon Bunny that's pounding the mill, and gather up 10 Moon Bunny's Rice Cakes!", 20);
        sm.broadcastMessage("Protect the Moon Bunny!");
    }
}
