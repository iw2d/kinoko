package kinoko.script.event;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.user.User;

public final class MiniDungeon extends ScriptHandler {
    public static void handleMiniDungeon(ScriptManager sm, int mapId, int dungeonId, int timeLimit) {
        if (sm.getFieldId() == mapId) {
            final User user = sm.getUser();
            if (user.getPartyId() != 0 && !user.isPartyBoss()) {
                sm.message("You are not the leader of the party.");
            } else {
                sm.playPortalSE();
                sm.partyWarpInstance(dungeonId, "out00", mapId, timeLimit);
            }
        } else {
            sm.playPortalSE();
            sm.warp(mapId, "MD00");
        }
    }

    @Script("MD_mushroom")
    public static void MD_mushroom(ScriptManager sm) {
        // Singing Mushroom Forest : Ghost Mushroom Forest (100020400)
        //   MD00 (289, -867)
        // Singing Mushroom Forest : Warm Shade (100020500)
        //   out00 (497, -716)
        handleMiniDungeon(sm, 100020400, 100020500, 7200);
    }

    @Script("MD_coldeye")
    public static void MD_coldeye(ScriptManager sm) {
        // North Forest : Young Tree Forest (101030300)
        //   MD00 (-1300, -338)
        // North Forest : One-Eyed Lizard (101030400)
        //   out00 (194, 267)
        handleMiniDungeon(sm, 101030300, 101030400, 7200);
    }

    @Script("MD_golem")
    public static void MD_golem(ScriptManager sm) {
        // Golem's Temple : Golem's Temple 4 (100040400)
        //   MD00 (978, -359)
        // Golem's Temple : Golem's Castle Ruins (100040500)
        //   out00 (-232, 1142)
        // Dungeon : Sleepy Dungeon IV (105040304)
        //   MD00 (717, 674)
        if (sm.getFieldId() == 105040304) {
            // Dungeon : Sleepy Dungeon IV
            sm.message("You cannot go to that place.");
        } else {
            handleMiniDungeon(sm, 100040400, 100040500, 7200);
        }
    }

    @Script("MD_drake")
    public static void MD_drake(ScriptManager sm) {
        // Drake Cave : Cave Exit (105020400)
        //   MD00 (2442, 107)
        // Drake Cave : Blue Drake Cave (105020500)
        //   out00 (441, -1029)
        handleMiniDungeon(sm, 105020400, 105020500, 7200);
    }

    @Script("MD_pig")
    public static void MD_pig(ScriptManager sm) {
        // Beach : Wave Beach (120020100)
        //   MD00 (1077, -56)
        // Beach : The Pig Beach (120020200)
        //   out00 (612, 212)
        handleMiniDungeon(sm, 120020100, 120020200, 7200);
    }

    @Script("MD_rabbit")
    public static void MD_rabbit(ScriptManager sm) {
        // Ludibrium : Eos Tower 71st - 90th Floor (221022200)
        //   MD00 (-233, -1572)
        // Mini Dungeon : Drummer Bunny's Lair (221023401)
        //   out00 (196, 466)
        handleMiniDungeon(sm, 221022200, 221023401, 7200);
    }

    @Script("MD_roundTable")
    public static void MD_roundTable(ScriptManager sm) {
        // Leafre : Battlefield of Fire and Water (240020500)
        //   MD00 (772, 119)
        // Mini Dungeon : The Round Table of Kentaurus (240020501)
        //   out00 (839, -779)
        handleMiniDungeon(sm, 240020500, 240020501, 7200);
    }

    @Script("MD_remember")
    public static void MD_remember(ScriptManager sm) {
        // Leafre : The Dragon Nest Left Behind (240040511)
        //   MD00 (1028, 1099)
        // Mini Dungeon : The Restoring Memory (240040800)
        //   out00 (1080, 1094)
        handleMiniDungeon(sm, 240040511, 240040800, 7200);
    }

    @Script("MD_protect")
    public static void MD_protect(ScriptManager sm) {
        // Leafre : Destroyed Dragon Nest (240040520)
        //   MD00 (-1082, 1106)
        // Mini Dungeon : Newt Secured Zone (240040900)
        //   out00 (305, 445)
        handleMiniDungeon(sm, 240040520, 240040900, 7200);
    }

    @Script("MD_treasure")
    public static void MD_treasure(ScriptManager sm) {
        // Herb Town : Red-Nose Pirate Den 2 (251010402)
        //   MD00 (549, -234)
        // Mini Dungeon : Pillage of Treasure Island (251010410)
        //   out00 (361, -394)
        handleMiniDungeon(sm, 251010402, 251010410, 7200);
    }

    @Script("MD_sand")
    public static void MD_sand(ScriptManager sm) {
        // Sunset Road : Sahel 2 (260020600)
        //   MD00 (-180, -178)
        // Mini Dungeon : Hill of Sandstorms (260020630)
        //   out00 (742, 97)
        handleMiniDungeon(sm, 260020600, 260020630, 7200);
    }

    @Script("MD_error")
    public static void MD_error(ScriptManager sm) {
        // Alcadno Research Institute : Lab - Area C-1 (261020300)
        //   MD00 (234, -93)
        // Hidden Street : Critical Error (261020301)
        //   out00 (-310, -85)
        handleMiniDungeon(sm, 261020300, 261020301, 7200);
    }

    @Script("MD_high")
    public static void MD_high(ScriptManager sm) {
        // Malaysia : Fantasy Theme Park 3 (551030000)
        //   MD00 (-160, 638)
        // Malaysia : Longest Ride on ByeBye Station (551030001)
        //   out00 (-131, 158)
        handleMiniDungeon(sm, 551030000, 551030001, 7200);
    }

    @Script("MD_cakeEnter")
    public static void MD_cakeEnter(ScriptManager sm) {
        // New Leaf City : NLC Town Center (600000000)
        //   yn00 (1771, 499)
        sm.message("You cannot go to that place.");
    }
}
