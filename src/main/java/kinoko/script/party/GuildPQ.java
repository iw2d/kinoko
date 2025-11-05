package kinoko.script.party;

import kinoko.packet.field.FieldPacket;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class GuildPQ extends ScriptHandler {
    // Map constants
    public static final int LOBBY = 102040200;
    public static final int WAITING_ROOM = 990000000;
    public static final int STAGE_1 = 990000100;
    public static final int STAGE_2 = 990000200;
    public static final int STAGE_3 = 990000300;
    public static final int STAGE_3_1 = 990000301;
    public static final int STAGE_4 = 990000400;
    public static final int STAGE_4_1 = 990000401;
    public static final int STAGE_4_10 = 990000410;
    public static final int STAGE_4_20 = 990000420;
    public static final int STAGE_4_30 = 990000430;
    public static final int STAGE_4_31 = 990000431;
    public static final int STAGE_4_40 = 990000440;
    public static final int STAGE_5 = 990000500;
    public static final int STAGE_5_1 = 990000501;
    public static final int STAGE_5_2 = 990000502;
    public static final int STAGE_6 = 990000600;
    public static final int STAGE_6_10 = 990000610;
    public static final int STAGE_6_11 = 990000611;
    public static final int STAGE_6_20 = 990000620;
    public static final int STAGE_6_30 = 990000630;
    public static final int STAGE_6_31 = 990000631;
    public static final int STAGE_6_40 = 990000640;
    public static final int STAGE_6_41 = 990000641;
    public static final int STAGE_7 = 990000700;
    public static final int STAGE_8 = 990000800;
    public static final int BOSS = 990000900;
    public static final int STAGE_10 = 990001000;
    public static final int EXIT = 990001100;
    public static final int BONUS = 990001101;

    // Mob constants
    public static final int ERGOTH = 9300028;

    // Item constants
    public static final int GUILD_COIN = 4000313;
    public static final int MOONSTONE = 4001113;
    public static final int STARSTONE = 4001114;

    @Script("guildPQ_enter")
    public static void guildPQ_enter(ScriptManager sm) {
        // Shuang (2040036)
        //   Perion : Excavation Team Camp (102040200)
        if (sm.getFieldId() == LOBBY) {
            final int answer = sm.askMenu("#e<Guild Quest: Sharenian>#n\r\nHello, I'm Shuang. The ancient kingdom of Sharenian has been discovered! Will your guild help explore it?", Map.of(
                    0, "I want to do the Guild Quest",
                    1, "I want to hear the details"
            ));
            if (answer == 0) {
                if (!sm.getUser().isPartyBoss()) {
                    sm.sayOk("If you'd like to enter here, the leader of your party will have to talk to me. Talk to your party leader about this.");
                    return;
                }
                // Check if user has a guild
                if (sm.getUser().getGuildId() == 0) {
                    sm.sayOk("You need to be in a guild to participate in the Guild Quest!");
                    return;
                }
                if (!sm.checkParty(6, 30)) {
                    sm.sayOk("You cannot enter because your party requirements are not met. You need 6+ guild members at Lv. 30+ to enter.");
                    return;
                }
                // Create instance with 3 minute entry timer
                sm.partyWarpInstance(List.of(
                        WAITING_ROOM, STAGE_1, STAGE_2, STAGE_3, STAGE_3_1,
                        STAGE_4, STAGE_4_1, STAGE_4_10, STAGE_4_20, STAGE_4_30, STAGE_4_31, STAGE_4_40,
                        STAGE_5, STAGE_5_1, STAGE_5_2,
                        STAGE_6, STAGE_6_10, STAGE_6_11, STAGE_6_20, STAGE_6_30, STAGE_6_31, STAGE_6_40, STAGE_6_41,
                        STAGE_7, STAGE_8, BOSS, STAGE_10, EXIT, BONUS
                ), "st00", EXIT, 3 * 60);
            } else if (answer == 1) {
                sm.sayOk("#e<Guild Quest: Sharenian>#n\r\nThe ancient kingdom of Sharenian has been discovered beneath Perion! Gather your guild members and explore the ruins, solve puzzles, and defeat the guardian Ergoth!\r\n\r\n#e - Level:#n 30+ #r(Recommended Level: 30-70)#k\r\n#e - Time Limit:#n 3 min entry + 120 min quest\r\n#e - Players:#n 6+ guild members\r\n#e - Boss:#n #rErgoth#k\r\n#e - Reward:#n Guild Points, Experience");
            }
        }
    }

    @Script("guildwaitingenter")
    public static void guildwaitingenter(ScriptManager sm) {
        // Portal: join00 in waiting room
        //   Hidden Street : Sharenian - Entrance (990000000)
        final String state = sm.getInstanceVariable("state");
        if (state.equals("1")) {
            sm.warp(STAGE_1, "st00");
        } else {
            sm.message("The gate has not been opened yet. Please wait for more guild members.");
        }
    }

    @Script("guildPQ_mapEnter")
    public static void guildPQ_mapEnter(ScriptManager sm) {
        // Map enter scripts
        final Field field = sm.getField();
        final int mapId = field.getFieldId();

        if (mapId == WAITING_ROOM) {
            field.blowWeather(5120025, "Welcome to the Sharenian Guild Quest! More guild members can join for the next 3 minutes.", 20);
        } else if (mapId == STAGE_1) {
            field.blowWeather(5120025, "Warning: Once entering the vicinity of the fortress, anyone without protective stone earrings will immediately die due to the deterioration of the surrounding air.", 20);
        }
    }

    @Script("guildPQ_waiting")
    public static void guildPQ_waiting(ScriptManager sm) {
        // Waiting room NPC
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String state = sm.getInstanceVariable("state");
        if (state.equals("0")) {
            final int playerCount = sm.getInstanceUserCount();
            if (playerCount < 6) {
                sm.sayOk("You need at least 6 guild members to enter. Currently you have: " + playerCount);
                return;
            }

            sm.setInstanceVariable("state", "1");
            sm.broadcastMessage("The gate to the castle has been opened!");
            sm.broadcastScreenEffect("quest/party/clear");
            sm.broadcastSoundEffect("Party1/Clear");
        } else {
            sm.sayOk("The gate is already open. Proceed through the portal!");
        }
    }

    @Script("guildPQ_stage1")
    public static void guildPQ_stage1(ScriptManager sm) {
        // Stage 1 - Combat stage
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        if (sm.getField().getMobPool().isEmpty()) {
            sm.sayOk("You've cleared this stage! Proceed to the next area.");
            sm.addExpAll(500);
        } else {
            sm.sayOk("Defeat all the monsters in this area!");
        }
    }

    @Script("guildPQ_boss")
    public static void guildPQ_boss(ScriptManager sm) {
        // Boss stage - Ergoth
        if (!sm.getUser().isPartyBoss()) {
            sm.sayOk("Please have your party leader talk to me.");
            return;
        }

        final String intro = sm.getInstanceVariable("boss_intro");
        if (intro.isEmpty()) {
            sm.setInstanceVariable("boss_intro", "1");
            sm.sayOk("This is the final chamber! Defeat Ergoth, the guardian of Sharenian, to complete the Guild Quest!");
            return;
        }

        // Check if Ergoth is defeated
        if (sm.getInstanceVariable("boss_dead").equals("1")) {
            sm.sayOk("Congratulations! You've defeated Ergoth! Proceed to collect your rewards!");
            sm.addExpAll(2000);
            sm.partyWarp(EXIT, "sp");
        } else {
            sm.sayOk("Defeat Ergoth to complete the Guild Quest!");
        }
    }

    @Script("guildPQ_ergoth_dead")
    public static void guildPQ_ergoth_dead(ScriptManager sm) {
        // When Ergoth (9300028) dies
        final Field field = sm.getField();
        if (field.getFieldId() == BOSS) {
            sm.setInstanceVariable("boss_dead", "1");
            sm.broadcastScreenEffect("quest/party/clear");
            sm.broadcastSoundEffect("Party1/Clear");
            sm.broadcastMessage("Ergoth has been defeated! The Guild Quest is complete!");
        }
    }

    @Script("guildPQ_exit")
    public static void guildPQ_exit(ScriptManager sm) {
        // Exit NPC
        final int mapId = sm.getFieldId();

        if (mapId == EXIT) {
            if (sm.askYesNo("Would you like to return to Perion?")) {
                sm.warp(LOBBY, "sp");
            }
            return;
        }

        if (mapId == BONUS) {
            if (sm.askYesNo("Would you like to leave the bonus room?")) {
                sm.warp(EXIT, "sp");
            }
            return;
        }

        // Regular stage exit
        if (!sm.askYesNo("Are you sure you want to leave the Guild Quest? You will forfeit all progress.")) {
            return;
        }

        sm.warp(EXIT, "sp");
    }

    @Script("guildPQ_StageMsg")
    public static void guildPQ_StageMsg(ScriptManager sm) {
        // Stage entry messages
        final Field field = sm.getField();
        switch (field.getFieldId()) {
            case STAGE_1 -> {
                field.blowWeather(5120017, "Stage 1: Defeat all monsters to proceed!", 20);
            }
            case STAGE_2 -> {
                field.blowWeather(5120017, "Stage 2: Work together to solve the puzzle!", 20);
            }
            case BOSS -> {
                field.setMobSpawn(false);
                field.getMobPool().respawnMobs(Instant.MAX);
                field.blowWeather(5120017, "Final Stage: Defeat Ergoth!", 20);
            }
        }
    }
}
