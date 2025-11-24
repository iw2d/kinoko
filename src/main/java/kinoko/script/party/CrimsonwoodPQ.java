package kinoko.script.party;

import kinoko.packet.field.FieldPacket;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobAppearType;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class CrimsonwoodPQ extends ScriptHandler {
    // Map constants
    public static final int LOBBY = 610030020;
    public static final int STAGE_1 = 610030100;
    public static final int STAGE_2 = 610030200;
    public static final int STAGE_3 = 610030300;
    public static final int STAGE_4 = 610030400;
    public static final int STAGE_5 = 610030500;
    public static final int STAGE_5_1 = 610030510;
    public static final int STAGE_5_2 = 610030520;
    public static final int STAGE_5_21 = 610030521;
    public static final int STAGE_5_22 = 610030522;
    public static final int STAGE_5_3 = 610030530;
    public static final int STAGE_5_4 = 610030540;
    public static final int STAGE_5_5 = 610030550;
    public static final int BOSS = 610030600;
    public static final int STAGE_7 = 610030700;
    public static final int REWARD = 610030800;

    // Mob constants
    public static final int GUARDIAN = 9400594;
    public static final int BOSS_1 = 9400589;
    public static final int BOSS_2 = 9400590;
    public static final int BOSS_3 = 9400591;
    public static final int BOSS_4 = 9400592;
    public static final int BOSS_5 = 9400593;

    @Script("cwkPQ_enter")
    public static void cwkPQ_enter(ScriptManager sm) {
        // Jack (9270035)
        //   Crimsonwood Keep : Hallway to Secret Hall (610030020)
        if (sm.getFieldId() == LOBBY) {
            final int answer = sm.askMenu("#e<Party Quest: Crimsonwood Keep>#n\r\nGreetings, brave adventurer. The Crimsonwood Keep holds many secrets and dangers. Will you venture inside?", Map.of(
                    0, "I want to enter Crimsonwood Keep",
                    1, "I want to hear the details"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("Only the party leader can enter Crimsonwood Keep PQ.");
                    return;
                }
                if (!sm.checkParty(3, 90)) {
                    sm.sayOk("You need 3-6 party members at level 90+ to enter Crimsonwood Keep.");
                    return;
                }
                sm.partyWarpInstance(List.of(
                        STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5,
                        STAGE_5_1, STAGE_5_2, STAGE_5_21, STAGE_5_22, STAGE_5_3, STAGE_5_4, STAGE_5_5,
                        BOSS, STAGE_7, REWARD
                ), "st00", LOBBY, 3 * 60);
            } else if (answer == 1) {
                sm.sayOk("#e<Party Quest: Crimsonwood Keep>#n\r\nCrimsonwood Keep is a challenging party quest that requires teamwork, puzzle-solving, and combat prowess!\r\n\r\n#e - Level:#n 90+ #r(Recommended Level: 90-120)#k\r\n#e - Time Limit:#n 3 min entry + extended time\r\n#e - Players:#n 3-6\r\n#e - Bosses:#n Multiple Crimsonwood Bosses\r\n#e - Reward:#n Experience, Crimsonwood Equipment");
            }
        }
    }

    @Script("cwkPQ_mapEnter")
    public static void cwkPQ_mapEnter(ScriptManager sm) {
        // Map enter scripts
        final Field field = sm.getField();
        final int mapId = field.getFieldId();

        if (mapId == STAGE_1) {
            field.blowWeather(5120017, "Welcome to Crimsonwood Keep! Find the entrance quickly before the guardians discover you!", 20);
            // Schedule guardian spawn after 30 seconds
            // sm.scheduleInstanceEvent("spawnGuardians", 30);
        }
    }

    @Script("cwkPQ_stage1")
    public static void cwkPQ_stage1(ScriptManager sm) {
        // Stage 1 - Find entrance quickly
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage1_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage1_intro", "1");
            sm.sayOk("You have entered Crimsonwood Keep! Find the entrance to proceed deeper into the keep. Be quick - guardians will spawn in 30 seconds!");
            return;
        }

        sm.sayOk("Proceed to the next stage through the portal!");
    }

    @Script("cwkPQ_stage4")
    public static void cwkPQ_stage4(ScriptManager sm) {
        // Stage 4 - Skills puzzle
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage4_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage4_intro", "1");
            sm.sayOk("Welcome to the Skills Puzzle! Use your class skills to activate the correct reactors in sequence!");
            return;
        }

        if (sm.getInstanceVariable("stage4_clear").equals("1")) {
            sm.sayOk("You've cleared this puzzle! Move forward!");
            return;
        }

        sm.sayOk("Solve the skills puzzle to proceed!");
    }

    @Script("cwkPQ_stage5_4")
    public static void cwkPQ_stage5_4(ScriptManager sm) {
        // Stage 5-4 - Sigil mob spawn stage
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage5_4_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage5_4_intro", "1");
            sm.sayOk("This stage has 5 Guardian Sigils. Defeat all 5 to proceed!");

            // Spawn 5 guardians at specific positions
            final int[][] positions = {
                    {944, -204}, {401, -384}, {28, -504}, {-332, -384}, {-855, -204}
            };
            for (int[] pos : positions) {
                sm.spawnMob(GUARDIAN, MobAppearType.NORMAL, pos[0], pos[1], false);
            }
            return;
        }

        if (sm.getField().getMobPool().isEmpty()) {
            sm.sayOk("Excellent! All guardians defeated. Proceed to the next area!");
            sm.addExpAll(1000);
        } else {
            sm.sayOk("Defeat all 5 Guardian Sigils to proceed!");
        }
    }

    @Script("cwkPQ_boss")
    public static void cwkPQ_boss(ScriptManager sm) {
        // Boss stage
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("boss_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("boss_intro", "1");
            sm.setInstanceVariable("boss_count", "0");
            sm.sayOk("This is the final chamber! Defeat all 5 Crimsonwood bosses to complete the quest!");
            return;
        }

        final String count = sm.getInstanceVariable("boss_count");
        final int bossCount = count.isEmpty() ? 0 : Integer.parseInt(count);

        if (bossCount >= 5) {
            sm.sayOk("Congratulations! You've defeated all the bosses! Proceed to claim your rewards!");
            sm.addExpAll(3000);
            sm.broadcastScreenEffect("quest/party/clear");
            sm.broadcastSoundEffect("Party1/Clear");
            sm.partyWarp(STAGE_7, "sp");
        } else {
            sm.sayOk(String.format("You've defeated %d/5 bosses. Keep fighting!", bossCount));
        }
    }

    @Script("cwkPQ_boss_dead")
    public static void cwkPQ_boss_dead(ScriptManager sm) {
        // When a boss dies (9400589-9400593)
        final Field field = sm.getField();
        if (field.getFieldId() == BOSS) {
            final String count = sm.getInstanceVariable("boss_count");
            final int bossCount = count.isEmpty() ? 0 : Integer.parseInt(count);
            sm.setInstanceVariable("boss_count", String.valueOf(bossCount + 1));

            if (bossCount + 1 >= 5) {
                sm.broadcastScreenEffect("quest/party/clear");
                sm.broadcastSoundEffect("Party1/Clear");
                sm.broadcastMessage("All bosses defeated! You may now proceed to the reward area!");
            } else {
                sm.broadcastMessage(String.format("Boss defeated! %d/5 bosses remaining.", 5 - (bossCount + 1)));
            }
        }
    }

    @Script("cwkPQ_exit")
    public static void cwkPQ_exit(ScriptManager sm) {
        // Exit NPC
        final int mapId = sm.getFieldId();

        if (mapId == REWARD) {
            if (sm.askYesNo("Would you like to leave and return to the lobby?")) {
                sm.warp(LOBBY, "sp");
            }
            return;
        }

        if (!sm.askYesNo("Are you sure you want to leave Crimsonwood Keep PQ? You will forfeit all progress.")) {
            return;
        }

        sm.warp(LOBBY, "sp");
    }

    @Script("cwkPQ_StageMsg")
    public static void cwkPQ_StageMsg(ScriptManager sm) {
        // Stage entry messages
        final Field field = sm.getField();
        switch (field.getFieldId()) {
            case STAGE_1 -> {
                field.blowWeather(5120017, "Stage 1: Find the entrance quickly!", 20);
            }
            case STAGE_4 -> {
                field.blowWeather(5120017, "Stage 4: Use your skills to solve the puzzle!", 20);
            }
            case BOSS -> {
                field.setMobSpawn(false);
                field.getMobPool().respawnMobs(Instant.MAX);
                field.blowWeather(5120017, "Final Stage: Defeat all 5 Crimsonwood Bosses!", 20);
            }
        }
    }
}
