package kinoko.script.party;

import kinoko.packet.field.FieldPacket;
import kinoko.script.UnityPortal;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class KerningPQ extends ScriptHandler {
    public static final int EXIT = 910340000;
    public static final int STAGE_1 = 910340100;
    public static final int STAGE_2 = 910340200;
    public static final int STAGE_3 = 910340300;
    public static final int STAGE_4 = 910340400;
    public static final int STAGE_5 = 910340500;
    public static final int BONUS = 910340600;

    public static final int COUPON = 4001007;

    @Script("party_exit")
    public static void party_exit(ScriptManager sm) {
        // Hidden Street : First Time Together Lobby (910340700)
        //   out00 (-259, 158)
        UnityPortal.returnPortal(sm, 103000000, null); // Kerning City : Kerning City
    }

    @Script("party_portal")
    public static void party_portal(ScriptManager sm) {
        // Hidden Street : First Time Together <1st Stage> (910340100)
        //   next000 (716, 106)
        // Hidden Street : First Time Together <2nd Stage> (910340200)
        //   next000 (-218, 91)
        // Hidden Street : First Time Together <3rd Stage> (910340300)
        //   next000 (1331, -122)
        // Hidden Street : First Time Together <4th Stage> (910340400)
        //   next000 (1655, 118)
        switch (sm.getFieldId()) {
            case STAGE_1 -> {
                if (sm.getInstanceVariable("stage1_gate").equals("1")) {
                    sm.warp(STAGE_2, "st00");
                }
            }
            case STAGE_2 -> {
                if (sm.getInstanceVariable("stage2_gate").equals("1")) {
                    sm.warp(STAGE_3, "st00");
                }
            }
            case STAGE_3 -> {
                if (sm.getInstanceVariable("stage3_gate").equals("1")) {
                    sm.warp(STAGE_4, "st00");
                }
            }
            case STAGE_4 -> {
                if (sm.getInstanceVariable("stage4_gate").equals("1")) {
                    sm.warp(STAGE_5, "st00");
                }
            }
        }
    }

    @Script("party1_enter")
    public static void party1_enter(ScriptManager sm) {
        // Lakelis (9020000)
        //   Kerning City : Kerning City (103000000)
        //   Hidden Street : First Time Together Lobby (910340700)
        if (sm.getFieldId() == 100000200) {
            // Henesys : Henesys Park
            if (sm.askYesNo("Would you like to move to First Time Toegether Lobby?")) {
                sm.setQRValue(QuestRecordType.UnityPortal, "");
                sm.warp(910340700); // Hidden Street : First Time Together Lobby
            }
        } else if (sm.getFieldId() == 910340700) {
            final int answer = sm.askMenu("#e<Party Quest: First Time Together>#n\r\nInside, you'll find many obstacles that can only be solved by working with a party. Interested? Then have you #bParty Leader#k talk to me.", Map.of(
                    0, "I want to do the Party Quest",
                    1, "I want to hear the details"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("If you'd like to enter here, the leader of your party will have to talk to me. Talk to your party leader about this.");
                    return;
                }
                if (!sm.checkParty(3, 20)) {
                    sm.sayOk("You cannot enter because your party doesn't have 3 members. You need 3 party members at Lv. 20 or higher to enter, so double-check and talk to me again.");
                    return;
                }
                sm.removeItem(COUPON);
                sm.partyWarpInstance(List.of(
                        STAGE_1,
                        STAGE_2,
                        STAGE_3,
                        STAGE_4,
                        STAGE_5,
                        BONUS
                ), "st00", EXIT, 60 * 30);
            } else if (answer == 1) {
                sm.sayOk("I'm waiting for brave adventurers. Please work together, share your strengths and wisdom to solve the challenges, and defeat the vicious #rKing Slime#k! King Slime will appear when you complete the challenges. You will need to find the right location and collect Passes corresponding to the answer to the quiz.\r\n\r\n#e - Level:#n 20 or above #r(Recommended Level: 20 - 29)#k\r\n#e - Time Limit:#n 30 min.\r\n#e - Players:#n 3 - 4\r\n#e - Reward:#n #v1072369# #t1072369# #b(Dropped by King Slime)#k\r\nVarious Use, Etc, and Equip items");
            }
        }
    }

    @Script("party1_play")
    public static void party1_play(ScriptManager sm) {
        // Cloto (9020001)
        //   Hidden Street : First Time Together <1st Stage> (103000800)
        //   Hidden Street : First Time Together <2nd Stage> (103000801)
        //   Hidden Street : First Time Together <3rd Stage> (103000802)
        //   Hidden Street : First Time Together <4th stage> (103000803)
        //   Hidden Street : First Time Together <Last Stage> (103000804)
        //   Hidden Street : First Time Together <1st Stage> (910340100)
        //   Hidden Street : First Time Together <1st Stage> (910340101)
        //   Hidden Street : First Time Together <2nd Stage> (910340200)
        //   Hidden Street : First Time Together <2nd Stage> (910340201)
        //   Hidden Street : First Time Together <3rd Stage> (910340300)
        //   Hidden Street : First Time Together <3rd Stage> (910340301)
        //   Hidden Street : First Time Together <4th Stage> (910340400)
        //   Hidden Street : First Time Together <4th Stage> (910340401)
        //   Hidden Street : First Time Together <Last Stage> (910340500)
        //   Hidden Street : First Time Together <Last Stage> (910340501)
        final User user = sm.getUser();
        final Field field = sm.getField();
        switch (field.getFieldId()) {
            case STAGE_1 -> {
                if (sm.getInstanceVariable("stage1_gate").equals("1")) {
                    return;
                }
                final int requiredPassCount = Math.max(sm.getInstanceUserCount() - 1, 2);
                if (user.isPartyBoss()) {
                    // Introduction
                    if (!sm.getInstanceVariable("stage1_intro").equals("1")) {
                        sm.setInstanceVariable("stage1_intro", "1");
                        sm.sayNext("Hello and welcome to the first stage. As you can see, this place is full of Ligators. Each Ligator will drop one #bcoupon#k when defeated. Each party member, except the party leader, must come talk to me and then bring me the exact number of #bcoupons#k that I ask for. Once everyone #bcompletes their individual missions#k, the party can move on to the next stage. Good luck!");
                        return;
                    }
                    // Check pass count
                    final String count = sm.getInstanceVariable("stage1_count");
                    final int passCount = Util.isInteger(count) ? Integer.parseInt(count) : 0;
                    if (passCount < requiredPassCount) {
                        sm.sayNext("I'm sorry, but at least one party member still hasn't completed their mission. Everyone except the party leader must clear their mission to move on.");
                        return;
                    }
                    // Stage clear
                    sm.addExpAll(100);
                    sm.setInstanceVariable("stage1_gate", "1");
                    sm.broadcastPacket(FieldPacket.setObjectState("gate", 0));
                    sm.sayNext("Congratulations on clearing this stage! I will create a portal that will lead you to the next one. You're on a time limit, so please hurry! Good luck!");
                } else {
                    // Introduction
                    final String mission = sm.getInstanceVariable(user.getCharacterName());
                    if (mission.equals("clear")) {
                        sm.sayNext("You've completed the mission! Please help other party members who may have not completed the mission yet.");
                        return;
                    }
                    if (!Util.isInteger(mission)) {
                        final int coupons = Util.getRandom(5, 20);
                        sm.sayNext("First, you must complete the mission I give. Once you complete the mission, you will receive a Pass, which will allow you to pass through.");
                        sm.setInstanceVariable(user.getCharacterName(), String.valueOf(coupons));
                        sm.sayBoth(String.format("Your mission is to collect #r%s Coupons#k. You can obtain the coupons by defeating the #rLigators#k found here.", coupons));
                        return;
                    }
                    // Check coupon count
                    final int couponCount = Integer.parseInt(mission);
                    if (sm.getItemCount(COUPON) != couponCount || !sm.removeItem(COUPON, couponCount)) {
                        sm.sayNext(String.format("I'm sorry, but that is not the right number of coupons. Your mission is to collect #r%s Coupons#k. You can obtain the coupons by defeating the #rLigators#k found here.", couponCount));
                        return;
                    }
                    sm.setInstanceVariable(user.getCharacterName(), "clear");
                    // Check pass count
                    final String count = sm.getInstanceVariable("stage1_count");
                    final int passCount = (Util.isInteger(count) ? Integer.parseInt(count) : 0) + 1;
                    sm.setInstanceVariable("stage1_count", String.valueOf(passCount));
                    if (passCount < requiredPassCount) {
                        sm.broadcastScriptProgressMessage(String.format("You've collected %d passes.", passCount));
                        sm.sayNext("You've completed the mission! Please help other party members who may have not completed the mission yet.");
                        return;
                    }
                    // Stage clear
                    field.blowWeather(5120017, "All individual missions have been cleared. The Party Leader should come talk to me.", 20);
                    sm.broadcastScreenEffect("quest/party/clear");
                    sm.broadcastSoundEffect("Party1/Clear");
                    sm.sayNext("You've completed the mission! Please tell your party leader to come talk to me to proceed.");
                }
            }
            case STAGE_2 -> {
                if (sm.getInstanceVariable("stage2_gate").equals("1") || !user.isPartyBoss()) {
                    return;
                }
                // Introduction
                final String answer = sm.getInstanceVariable("stage2_answer");
                if (answer.isEmpty()) {
                    final List<String> list = new ArrayList<>(List.of("1", "1", "1", "0"));
                    Collections.shuffle(list);
                    sm.setInstanceVariable("stage2_answer", String.join("", list));
                    sm.sayNext("Hi. Welcome to the 2nd Stage. Next to me, you'll see a number of ropes. Out of these ropes, #b3 are connected to the portal that sends you to the next stage#k. All you need to do is have #b3 party members to find the answer ropes and hang on them#k. BUT, it doesn't count as an answer if you hang on the rope too low; please bring yourself up enough to be counted as a correct answer. Also, only 3 members of your party are allowed on the ropes. Once they are hanging on, the leader of the party must #bdouble-click me to check and see if the answer's correct or not#k. Now, find the right ropes to hang on!");
                    return;
                }
                // Check answer
                if (!answer.equals(sm.getAreaCheck())) {
                    sm.broadcastScreenEffect("quest/party/wrong_kor");
                    sm.broadcastSoundEffect("Party1/Failed");
                    return;
                }
                // Stage clear
                sm.setInstanceVariable("stage2_gate", "1");
                sm.broadcastScreenEffect("quest/party/clear");
                sm.broadcastSoundEffect("Party1/Clear");
                sm.broadcastPacket(FieldPacket.setObjectState("gate", 0));
            }
            case STAGE_3 -> {
                if (sm.getInstanceVariable("stage3_gate").equals("1") || !user.isPartyBoss()) {
                    return;
                }
                // Introduction
                final String answer = sm.getInstanceVariable("stage3_answer");
                if (answer.isEmpty()) {
                    final List<String> list = new ArrayList<>(List.of("1", "1", "1", "0", "0"));
                    Collections.shuffle(list);
                    sm.setInstanceVariable("stage3_answer", String.join("", list));
                    sm.sayNext("Hello. Welcome to the 3rd stage. Next to you you'll see barrels with kittens inside on top of the platforms. Out of these platforms, #b3 of them lead to the portals for the next stage. 3 of the party members need to find the correct platform to step on and clear the stage#k.\r\nBUT, you need to stand firm right at the center of it, not standing on the edge, in order to be counted as a correct answer, so make sure to remember that. Also, only 3 members of your party are allowed on the platforms. Once the members are on them, the leader of the party must #bdouble-click me to check and see if the answer's right or not#k. Now, find the correct platforms~!");
                    sm.sayBoth("If there aren't enough people to stand on the platforms, purchase a #t4001454# #v4001454# from #p9020002# and place it on the correct platform. The platform will mistake #t4001454# for a character. Nifty, huh?");
                    return;
                }
                // Check answer
                final String current = sm.getAreaCheck();
                if (!answer.equals(current)) {
                    int count = 0;
                    for (int i = 0; i < answer.length(); i++) {
                        if (answer.charAt(i) == current.charAt(i)) {
                            count++;
                        }
                    }
                    sm.broadcastMessage(String.format("Currently, you've selected %d answer platforms", count));
                    sm.broadcastScriptProgressMessage(String.format("Currently, you've selected %d answer platforms", count));
                    sm.broadcastScreenEffect("quest/party/wrong_kor");
                    sm.broadcastSoundEffect("Party1/Failed");
                    return;
                }
                // Stage clear
                sm.setInstanceVariable("stage3_gate", "1");
                sm.broadcastScreenEffect("quest/party/clear");
                sm.broadcastSoundEffect("Party1/Clear");
                sm.broadcastPacket(FieldPacket.setObjectState("gate", 0));
            }
            case STAGE_4 -> {
                if (sm.getInstanceVariable("stage4_gate").equals("1") || !user.isPartyBoss()) {
                    return;
                }
                // Introduction
                if (!sm.getInstanceVariable("stage4_intro").equals("1")) {
                    sm.sayNext("TODO"); // TODO
                    sm.setInstanceVariable("stage4_intro", "1");
                    return;
                }
                // Check mob count
                if (field.getMobPool().getByTemplateId(9300002).isPresent()) {
                    sm.sayNext("TODO"); // TODO
                    return;
                }
                // Stage clear
                sm.sayNext("TODO"); // TODO
                sm.addExpAll(100);
                sm.setInstanceVariable("stage4_gate", "1");
                sm.broadcastPacket(FieldPacket.setObjectState("gate", 0));
            }
            case STAGE_5 -> {
                if (sm.getInstanceVariable("stage5_gate").equals("1") || !user.isPartyBoss()) {
                    return;
                }
                // Introduction
                if (!sm.getInstanceVariable("stage5_intro").equals("1")) {
                    sm.sayNext("TODO"); // TODO
                    sm.setInstanceVariable("stage5_intro", "1");
                    return;
                }
                // Check mob count
                if (field.getMobPool().getByTemplateId(9300003).isPresent()) {
                    sm.sayNext("TODO"); // TODO
                    return;
                }
                // Stage clear
                sm.sayNext("TODO"); // TODO
                sm.addExpAll(100);
                sm.setInstanceVariable("stage5_gate", "1");
                sm.broadcastPacket(FieldPacket.setObjectState("gate", 0));
            }
        }
    }

    @Script("party1_out")
    public static void party1_out(ScriptManager sm) {
        // Nella (9020002)
        //   Hidden Street : First Time Together <1st Stage> (103000800)
        //   Hidden Street : First Time Together <2nd Stage> (103000801)
        //   Hidden Street : First Time Together <3rd Stage> (103000802)
        //   Hidden Street : First Time Together <4th stage> (103000803)
        //   Hidden Street : First Time Together <Last Stage> (103000804)
        //   Hidden Street : First Time Together <Bonus> (103000805)
        //   Hidden Street : First Time Together <Exit> (910340000)
        //   Hidden Street : First Time Together <1st Stage> (910340100)
        //   Hidden Street : First Time Together <1st Stage> (910340101)
        //   Hidden Street : First Time Together <2nd Stage> (910340200)
        //   Hidden Street : First Time Together <2nd Stage> (910340201)
        //   Hidden Street : First Time Together <3rd Stage> (910340300)
        //   Hidden Street : First Time Together <3rd Stage> (910340301)
        //   Hidden Street : First Time Together <4th Stage> (910340400)
        //   Hidden Street : First Time Together <4th Stage> (910340401)
        //   Hidden Street : First Time Together <Last Stage> (910340500)
        //   Hidden Street : First Time Together <Last Stage> (910340501)
        //   Hidden Street : First Time Together <Bonus> (910340600)
        //   Hidden Street : First Time Together <Bonus> (910340601)
        sm.sayNext("TODO"); // TODO - exit stage, sell 4001454
    }

    @Script("StageMsg_together")
    public static void StageMsg_together(ScriptManager sm) {
        // Hidden Street : First Time Together <1st Stage> (103000800)
        // Hidden Street : First Time Together <2nd Stage> (103000801)
        // Hidden Street : First Time Together <3rd Stage> (103000802)
        // Hidden Street : First Time Together <4th stage> (103000803)
        // Hidden Street : First Time Together <Last Stage> (103000804)
        // Hidden Street : First Time Together <1st Stage> (910340100)
        // Hidden Street : First Time Together <1st Stage> (910340101)
        // Hidden Street : First Time Together <2nd Stage> (910340200)
        // Hidden Street : First Time Together <2nd Stage> (910340201)
        // Hidden Street : First Time Together <3rd Stage> (910340300)
        // Hidden Street : First Time Together <3rd Stage> (910340301)
        // Hidden Street : First Time Together <4th Stage> (910340400)
        // Hidden Street : First Time Together <4th Stage> (910340401)
        // Hidden Street : First Time Together <Last Stage> (910340500)
        // Hidden Street : First Time Together <Last Stage> (910340501)
        final Field field = sm.getField();
        switch (field.getFieldId()) {
            case STAGE_1 -> {
                field.blowWeather(5120017, "Everyone! Talk to Cloto, and defeat Ligators to find the coupons Cloto wants!.", 20);
            }
            case STAGE_2 -> {
                field.blowWeather(5120017, "Find 3 ropes that can open the door to the next stage, then grab onto them!.", 20);
            }
            case STAGE_3 -> {
                field.blowWeather(5120017, "Find the 3 Platforms that can open the door to the next stage.", 20);
            }
            case STAGE_4 -> {
                field.setMobSpawn(false);
                field.getMobPool().respawnMobs(Instant.MAX);
                field.blowWeather(5120017, "Eliminate the vicious Curse Eyes!", 20);
            }
            case STAGE_5 -> {
                field.setMobSpawn(false);
                field.getMobPool().respawnMobs(Instant.MAX);
                field.blowWeather(5120017, "Defeat King Slime!", 20);
            }
        }
    }
}
