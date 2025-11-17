package kinoko.script.party;

import kinoko.packet.field.FieldPacket;
import kinoko.script.UnityPortal;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.quest.QuestRecordType;

import java.awt.*;
import java.util.List;
import java.util.Map;

public final class OrbisPQ extends ScriptHandler {
    // Map constants
    public static final int LOBBY = 200080101;
    public static final int EXIT = 920011200;
    public static final int CLEAR_EXIT = 920011300;
    public static final int STAGE_1 = 920010000;
    public static final int STAGE_2 = 920010100;
    public static final int STAGE_3 = 920010200;
    public static final int STAGE_4 = 920010300;
    public static final int STAGE_5 = 920010400;
    public static final int STAGE_6 = 920010500;
    public static final int STAGE_7 = 920010600;
    public static final int STAGE_8 = 920010700;
    public static final int STAGE_9 = 920010800;
    public static final int BONUS_1 = 920010900;
    public static final int BONUS_2 = 920010910;
    public static final int BONUS_3 = 920010920;
    public static final int BONUS_4 = 920010930;
    public static final int FINAL_STAGE = 920011000;
    public static final int REWARD = 920011100;

    // Mob constants
    public static final int SEALED_CHEST = 9300049;
    public static final int PAPA_PIXIE = 9300039;
    public static final int RED_CELLION = 9300040;
    public static final int KING_SLIME = 9300010;

    // Item constants
    public static final int ORBIS_PQ_BOX = 2430066;
    public static final int ORBIS_PQ_REWARD = 4001043; // Star Rock

    // Buff items (given on entry)
    public static final int[] ENTRY_BUFFS = {2022090, 2022091, 2022092, 2022093};

    @Script("orbisPQ_enter")
    public static void orbisPQ_enter(ScriptManager sm) {
        // Icarus (9020005)
        //   Orbis : Orbis Tower <20th Floor> (200080101)
        //   Hidden Street : Sealed Garden (920011200)
        if (sm.getFieldId() == LOBBY) {
            final int answer = sm.askMenu("#e<Party Quest: Sealed Garden>#n\r\nI am Icarus, the steward of the goddess. The goddess has been sealed away in the tower. Will you help us break the seal?", Map.of(
                    0, "I want to do the Party Quest",
                    1, "I want to hear the details"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("If you'd like to enter here, the leader of your party will have to talk to me. Talk to your party leader about this.");
                    return;
                }
                if (!sm.checkParty(3, 20)) {
                    sm.sayOk("You cannot enter because your party requirements are not met. You need 3-6 party members at Lv. 20+ to enter, so double-check and talk to me again.");
                    return;
                }
                sm.partyWarpInstance(List.of(
                        STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5,
                        STAGE_6, STAGE_7, STAGE_8, STAGE_9,
                        BONUS_1, BONUS_2, BONUS_3, BONUS_4,
                        FINAL_STAGE, REWARD
                ), "sp", EXIT, 20 * 60);
            } else if (answer == 1) {
                sm.sayOk("#e<Party Quest: Sealed Garden>#n\r\nThe goddess Minerva has been sealed in the Tower of Goddess. Work together with your party to solve puzzles, defeat monsters, and break the seal! Defeat all the monsters in each stage to proceed.\r\n\r\n#e - Level:#n 20-30 #r(Recommended Level: 20-30)#k\r\n#e - Time Limit:#n 20 min.\r\n#e - Players:#n 3 - 6\r\n#e - Reward:#n #v2430066# #t2430066#");
            }
        } else if (sm.getFieldId() == EXIT) {
            // Exit map
            if (sm.askYesNo("Would you like to return to the lobby?")) {
                sm.warp(LOBBY, "sp");
            }
        }
    }

    @Script("orbisPQ_mapEnter")
    public static void orbisPQ_mapEnter(ScriptManager sm) {
        // Hidden Street : Sealed Garden Stage 1-9
        final Field field = sm.getField();
        final int mapId = field.getFieldId();

        // Apply entry buff to player
        if (mapId == STAGE_1) {
            // TODO: Apply random buff from ENTRY_BUFFS array on entry
            field.blowWeather(5120019, "Hi, I am the steward of the goddess. I have been sealed, so you cannot see me now. Can you help me unseal it?", 20);
        }
    }

    @Script("orbisPQ_stage1")
    public static void orbisPQ_stage1(ScriptManager sm) {
        // First stage - defeat all Jr. Neckis and Neckis
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        if (sm.getField().getMobPool().isEmpty()) {
            sm.sayOk("Excellent! You've cleared this stage. You may now proceed to the next area.");
            sm.addExpAll(200);
        } else {
            sm.sayOk("Defeat all the monsters in this area to proceed!");
        }
    }

    @Script("orbisPQ_stage2")
    public static void orbisPQ_stage2(ScriptManager sm) {
        // Stage 2 - Minerva puzzle
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage2_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage2_intro", "1");
            sm.sayOk("Welcome to Stage 2! Find the correct combination to unlock the seal. Work together with your party!");
            return;
        }

        // Check if stage cleared
        if (sm.getInstanceVariable("stage2_clear").equals("1")) {
            sm.sayOk("You've already cleared this stage. Proceed to the next area!");
            return;
        }

        // Add stage 2 logic here based on requirements
        sm.sayOk("Work together to solve this puzzle!");
    }

    @Script("orbisPQ_stage4")
    public static void orbisPQ_stage4(ScriptManager sm) {
        // Stage 4 - Red Cellion spawning stage
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage4_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage4_intro", "1");
            sm.sayOk("Welcome to Stage 4! Defeat all the Red Cellions that appear. They will keep spawning until you've defeated 15 of them!");
            sm.setInstanceVariable("stage4_count", "0");
            return;
        }

        final String count = sm.getInstanceVariable("stage4_count");
        final int cellionCount = count.isEmpty() ? 0 : Integer.parseInt(count);

        if (cellionCount >= 14) {
            sm.sayOk("Excellent work! You've defeated all the Red Cellions. Proceed to the next stage!");
            sm.addExpAll(300);
        } else {
            sm.sayOk(String.format("You've defeated %d/15 Red Cellions. Keep fighting!", cellionCount + 1));
        }
    }

    @Script("orbisPQ_stage9")
    public static void orbisPQ_stage9(ScriptManager sm) {
        // Stage 9 - Papa Pixie boss
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("stage9_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("stage9_intro", "1");
            sm.sayOk("This is the final combat stage! Defeat all the Sealed Chests to summon Papa Pixie, then defeat Papa Pixie to obtain the Life Grass!");
            return;
        }

        // Check if Papa Pixie is dead
        if (sm.getInstanceVariable("papa_dead").equals("1")) {
            sm.sayOk("Excellent! You've defeated Papa Pixie! Collect the Life Grass and proceed to save the goddess!");
        } else {
            sm.sayOk("Defeat the Sealed Chests to summon Papa Pixie!");
        }
    }

    @Script("orbisPQ_sealedChest_dead")
    public static void orbisPQ_sealedChest_dead(ScriptManager sm) {
        // When Sealed Chest (9300049) dies, spawn Papa Pixie
        final Field field = sm.getField();
        if (field.getFieldId() == STAGE_9) {
            sm.broadcastMessage("Papa Pixie has been spawned.");
            sm.spawnMob(PAPA_PIXIE, MobAppearType.NORMAL, -830, 563, false);
        }
    }

    @Script("orbisPQ_papaPixie_dead")
    public static void orbisPQ_papaPixie_dead(ScriptManager sm) {
        // When Papa Pixie (9300039) dies
        final Field field = sm.getField();
        if (field.getFieldId() == STAGE_9) {
            sm.setInstanceVariable("papa_dead", "1");
            sm.broadcastMessage("Please bring the Life Grass and go save the goddess as soon as possible.");
            sm.broadcastScreenEffect("quest/party/clear");
            sm.broadcastSoundEffect("Party1/Clear");
        }
    }

    @Script("orbisPQ_redCellion_dead")
    public static void orbisPQ_redCellion_dead(ScriptManager sm) {
        // When Red Cellion (9300040) dies, spawn another one
        final Field field = sm.getField();
        if (field.getFieldId() == STAGE_4) {
            final String count = sm.getInstanceVariable("stage4_count");
            final int cellionCount = count.isEmpty() ? 0 : Integer.parseInt(count);

            if (cellionCount < 14) {
                sm.setInstanceVariable("stage4_count", String.valueOf(cellionCount + 1));
                // Spawn another Red Cellion at random reactor position
                sm.spawnMob(RED_CELLION, MobAppearType.NORMAL, 0, 0, false);
                sm.broadcastMessage("Cellion has been spawned somewhere in the map.");
            }
        }
    }

    @Script("orbisPQ_exit")
    public static void orbisPQ_exit(ScriptManager sm) {
        // Nella (9020002) or similar exit NPC
        final int mapId = sm.getFieldId();

        if (mapId == EXIT || mapId == CLEAR_EXIT) {
            if (sm.askYesNo("Would you like to return to the lobby?")) {
                // TODO: Remove entry buffs on exit
                sm.warp(LOBBY, "sp");
            }
            return;
        }

        // Regular stage exit
        if (!sm.askYesNo("Are you sure you want to leave this Party Quest? You will forfeit all progress.")) {
            return;
        }

        // TODO: Remove entry buffs on exit
        sm.warp(EXIT, "sp");
    }

    @Script("orbisPQ_final")
    public static void orbisPQ_final(ScriptManager sm) {
        // Final NPC - Minerva or similar
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        // Check reactor state
        final String reactorState = sm.getInstanceVariable("minerva_state");
        final boolean cleared = reactorState.equals("5");

        if (cleared) {
            sm.sayOk("Thank you for freeing me! You may now claim your rewards.");
            sm.addExpAll(800);
            sm.setInstanceVariable("final_clear", "1");
            sm.partyWarp(CLEAR_EXIT, "sp");
        } else {
            sm.sayOk("Please complete the final puzzle to free me from the seal!");
        }
    }
}
