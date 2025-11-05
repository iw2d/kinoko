package kinoko.script.party;

import kinoko.packet.field.FieldPacket;
import kinoko.script.UnityPortal;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.quest.QuestRecordType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class LudiPQ extends ScriptHandler {
    // Map constants
    public static final int LOBBY = 221023300;
    public static final int EXIT = 922010000;
    public static final int STAGE_1 = 922010100;
    public static final int STAGE_2 = 922010400;
    public static final int STAGE_2_1 = 922010401;
    public static final int STAGE_2_2 = 922010402;
    public static final int STAGE_2_3 = 922010403;
    public static final int STAGE_2_4 = 922010404;
    public static final int STAGE_2_5 = 922010405;
    public static final int STAGE_4 = 922010600;
    public static final int STAGE_5 = 922010700;
    public static final int STAGE_6 = 922010800;
    public static final int STAGE_7 = 922010900;
    public static final int STAGE_8 = 922011000;
    public static final int REWARD = 922011100;

    // Item constants
    public static final int LUDI_PQ_BOX = 2430066;
    public static final int LUDI_COIN = 4001022;
    public static final int LUDI_PASS = 4001023;

    @Script("ludiPQ_enter")
    public static void ludiPQ_enter(ScriptManager sm) {
        // Eak (2040025)
        //   Ludibrium : Eos Tower 101st Floor (221023300)
        if (sm.getFieldId() == LOBBY) {
            final int answer = sm.askMenu("#e<Party Quest: Dimensional Crack>#n\r\nHello, I'm Eak. There's a strange dimensional crack that appeared in Ludibrium. Will you help investigate it?", Map.of(
                    0, "I want to do the Party Quest",
                    1, "I want to hear the details"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("If you'd like to enter here, the leader of your party will have to talk to me. Talk to your party leader about this.");
                    return;
                }
                if (!sm.checkParty(3, 35)) {
                    sm.sayOk("You cannot enter because your party requirements are not met. You need 3-6 party members at Lv. 35+ to enter, so double-check and talk to me again.");
                    return;
                }
                sm.removeItem(LUDI_COIN);
                sm.removeItem(LUDI_PASS);
                sm.partyWarpInstance(List.of(
                        STAGE_1, STAGE_2, STAGE_2_1, STAGE_2_2, STAGE_2_3, STAGE_2_4, STAGE_2_5,
                        STAGE_4, STAGE_5, STAGE_6, STAGE_7, STAGE_8, REWARD
                ), "st00", EXIT, 20 * 60);
            } else if (answer == 1) {
                sm.sayOk("#e<Party Quest: Dimensional Crack>#n\r\nA strange dimensional crack has appeared in Ludibrium! Work together with your party to solve puzzles, defeat monsters, and investigate the source of this dimensional anomaly!\r\n\r\n#e - Level:#n 35-50 #r(Recommended Level: 35-50)#k\r\n#e - Time Limit:#n 20 min.\r\n#e - Players:#n 3 - 6\r\n#e - Reward:#n #v2430066# #t2430066#");
            }
        } else if (sm.getFieldId() == EXIT) {
            // Exit map
            if (sm.askYesNo("Would you like to return to the lobby?")) {
                sm.warp(LOBBY, "sp");
            }
        }
    }

    @Script("ludiPQ_mapEnter")
    public static void ludiPQ_mapEnter(ScriptManager sm) {
        // Map enter script for Ludi PQ stages
        final Field field = sm.getField();
        final int mapId = field.getFieldId();

        if (mapId == STAGE_1) {
            field.blowWeather(5120017, "Welcome to the Dimensional Crack! Work with your party to solve the puzzles ahead!", 20);
        }
    }

    @Script("ludiPQ_stage1")
    public static void ludiPQ_stage1(ScriptManager sm) {
        // Stage 1 - Introduction/Simple monster clear
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage1_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage1_intro", "1");
            sm.sayOk("Welcome to the Dimensional Crack Party Quest! In each stage, you'll need to work together to solve puzzles and defeat monsters. Let's begin!");
            return;
        }

        if (sm.getField().getMobPool().isEmpty()) {
            sm.sayOk("Great work! You've cleared this stage. Proceed to the next area!");
            sm.addExpAll(300);
            sm.setInstanceVariable("stage1_clear", "1");
        } else {
            sm.sayOk("Defeat all the monsters in this area first!");
        }
    }

    @Script("ludiPQ_stage4")
    public static void ludiPQ_stage4(ScriptManager sm) {
        // Stage 4 - Block puzzle
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage4_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage4_intro", "1");
            sm.sayOk("Welcome to Stage 4! This is a block-pushing puzzle. Work together to push the blocks onto the correct platforms!");
            return;
        }

        // Check if puzzle is solved
        if (sm.getInstanceVariable("stage4_clear").equals("1")) {
            sm.sayOk("You've already cleared this stage! Proceed through the portal.");
            return;
        }

        sm.sayOk("Solve the block puzzle to proceed!");
    }

    @Script("ludi_s4Clear")
    public static void ludi_s4Clear(ScriptManager sm) {
        // Portal script for stage 4 completion
        if (sm.getInstanceVariable("stage4_clear").equals("1")) {
            sm.warp(STAGE_5, "st00");
        } else {
            sm.message("You haven't completed this stage yet!");
        }
    }

    @Script("ludiPQ_stage5")
    public static void ludiPQ_stage5(ScriptManager sm) {
        // Stage 5 - Another puzzle stage
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage5_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage5_intro", "1");
            sm.sayOk("Welcome to Stage 5! Another puzzle awaits you. Work together!");
            return;
        }

        if (sm.getInstanceVariable("stage5_clear").equals("1")) {
            sm.sayOk("You've cleared this stage! Move forward!");
            return;
        }

        sm.sayOk("Solve the puzzle to continue!");
    }

    @Script("ludi_s5Clear")
    public static void ludi_s5Clear(ScriptManager sm) {
        // Portal script for stage 5 completion
        if (sm.getInstanceVariable("stage5_clear").equals("1")) {
            sm.warp(STAGE_6, "st00");
        } else {
            sm.message("You haven't completed this stage yet!");
        }
    }

    @Script("ludiPQ_stage8")
    public static void ludiPQ_stage8(ScriptManager sm) {
        // Stage 8 - Boss stage (Alishar or similar)
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage8_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage8_intro", "1");
            sm.sayOk("This is the final stage! Defeat the boss to complete the Party Quest!");
            return;
        }

        // Check if boss is defeated
        if (sm.getField().getMobPool().isEmpty()) {
            sm.sayOk("Excellent! You've defeated the boss! Proceed to claim your rewards!");
            sm.addExpAll(1000);
            sm.setInstanceVariable("stage8_clear", "1");
            sm.setInstanceVariable("stage9", "1");
            sm.broadcastScreenEffect("quest/party/clear");
            sm.broadcastSoundEffect("Party1/Clear");
        } else {
            sm.sayOk("Defeat the boss to complete this Party Quest!");
        }
    }

    @Script("ludiPQ_final")
    public static void ludiPQ_final(ScriptManager sm) {
        // Final reward NPC
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        if (sm.getInstanceVariable("stage8_clear").equals("1")) {
            sm.sayOk("Congratulations on completing the Dimensional Crack Party Quest! Your rewards have been distributed!");
            sm.partyWarp(REWARD, "sp");
        } else {
            sm.sayOk("Complete all the stages first!");
        }
    }

    @Script("ludiPQ_exit")
    public static void ludiPQ_exit(ScriptManager sm) {
        // Exit NPC
        final int mapId = sm.getFieldId();

        if (mapId == EXIT) {
            if (sm.askYesNo("Would you like to return to the lobby?")) {
                sm.warp(LOBBY, "sp");
            }
            return;
        }

        if (mapId == REWARD) {
            if (sm.askYesNo("Would you like to leave the reward room?")) {
                sm.removeItem(LUDI_COIN);
                sm.removeItem(LUDI_PASS);
                sm.warp(EXIT, "sp");
            }
            return;
        }

        // Regular stage exit
        if (!sm.askYesNo("Are you sure you want to leave this Party Quest? You will forfeit all progress.")) {
            return;
        }

        sm.removeItem(LUDI_COIN);
        sm.removeItem(LUDI_PASS);
        sm.warp(EXIT, "sp");
    }

    @Script("ludiPQ_StageMsg")
    public static void ludiPQ_StageMsg(ScriptManager sm) {
        // Stage entry messages
        final Field field = sm.getField();
        switch (field.getFieldId()) {
            case STAGE_1 -> {
                field.blowWeather(5120017, "Welcome to the first stage! Defeat all monsters!", 20);
            }
            case STAGE_4 -> {
                field.blowWeather(5120017, "Stage 4: Push the blocks onto the correct platforms!", 20);
            }
            case STAGE_5 -> {
                field.blowWeather(5120017, "Stage 5: Solve the puzzle to proceed!", 20);
            }
            case STAGE_8 -> {
                field.setMobSpawn(false);
                field.getMobPool().respawnMobs(Instant.MAX);
                field.blowWeather(5120017, "Final Stage: Defeat the boss!", 20);
            }
        }
    }
}
