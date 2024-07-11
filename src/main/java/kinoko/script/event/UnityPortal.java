package kinoko.script.event;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptError;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Util;
import kinoko.world.quest.QuestRecordType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class UnityPortal extends ScriptHandler {
    public static final Set<Integer> AVAILABLE_FIELDS = Set.of(
            100000000, // Henesys : Henesys
            101000000, // Ellinia : Ellinia
            102000000, // Perion : Perion
            103000000, // Kerning City : Kerning City
            105000000, // Sleepywood : Sleepywood
            105040300, // Dungeon : Sleepywood
            120000000, // Nautilus : Nautilus Harbor
            200000200, // Orbis : Orbis Park
            211000000, // El Nath : El Nath
            220000000, // Ludibrium : Ludibrium
            221000000, // Omega Sector : Omega Sector
            222000000, // Korean Folk Town : Korean Folk Town
            230000000, // Aquarium : Aquarium
            240000000, // Leafre : Leafre
            250000000, // Mu Lung : Mu Lung
            251000000, // Herb Town : Herb Town
            260000000, // The Burning Road : Ariant
            261000000, // Sunset Road : Magatia
            310000000, // Black Wing Territory : Edelstein
            540000000, // Singapore : CBD
            600000000, // New Leaf City : NLC Town Center
            800000000 // Zipangu : Mushroom Shrine
    );

    public static void returnPortal(ScriptManager sm) {
        returnPortal(sm, 100000000, "sp");
    }

    public static void returnPortal(ScriptManager sm, int fallbackMapId, String fallbackPortalName) {
        final String returnMap = sm.getQRValue(QuestRecordType.UnityPortal);
        if (Util.isInteger(returnMap)) {
            final int returnMapId = Integer.parseInt(returnMap);
            if (AVAILABLE_FIELDS.contains(returnMapId)) {
                sm.playPortalSE();
                sm.warp(returnMapId, returnMapId < 540000000 ? "unityPortal2" : "sp"); // missing portal for CBD, NLC and Mushroom Shrine
                sm.setQRValue(QuestRecordType.UnityPortal, "");
                return;
            }
            log.error("Tried to use Dimensional Mirror to warp to {}", returnMapId);
        }
        sm.playPortalSE();
        if (fallbackPortalName == null || fallbackPortalName.isEmpty()) {
            sm.warp(fallbackMapId);
        } else {
            sm.warp(fallbackMapId, fallbackPortalName);
        }
    }

    @Script("unityPortal")
    public static void unityPortal(ScriptManager sm) {
        // Dimensional Mirror : Multi-Functional Portal (9010022)
        //   Henesys : Henesys (100000000)
        //   Ellinia : Ellinia (101000000)
        //   Perion : Perion (102000000)
        //   Kerning City : Kerning City (103000000)
        //   Sleepywood : Sleepywood (105000000)
        //   Dungeon : Sleepywood (105040300)
        //   Nautilus : Nautilus Harbor (120000000)
        //   Orbis : Orbis Park (200000200)
        //   El Nath : El Nath (211000000)
        //   Ludibrium : Ludibrium (220000000)
        //   Omega Sector : Omega Sector (221000000)
        //   Korean Folk Town : Korean Folk Town (222000000)
        //   Aquarium : Aquarium (230000000)
        //   Leafre : Leafre (240000000)
        //   Mu Lung : Mu Lung (250000000)
        //   Herb Town : Herb Town (251000000)
        //   The Burning Road : Ariant (260000000)
        //   Sunset Road : Magatia (261000000)
        //   Black Wing Territory : Edelstein (310000000)
        //   Singapore : CBD (540000000)
        //   New Leaf City : NLC Town Center (600000000)
        //   Zipangu : Mushroom Shrine (800000000)
        final Map<Integer, String> options = new HashMap<>();
        options.put(0, "Ariant Coliseum");
        options.put(1, "Mu Lung Dojo");
        options.put(2, "Monster Carnival 1");
        options.put(3, "Monster Carnival 2");
        options.put(4, "Sea of Fog");
        options.put(5, "Nett's Pyramid");
        options.put(6, "Dusty Platform");
        // options.put(7, "Happyville");
        options.put(8, "Golden Temple");
        options.put(9, "Moon Bunny");
        options.put(10, "First Time Together");
        options.put(11, "Dimensional Crack");
        options.put(12, "Forest of Poison Haze");
        options.put(13, "Remnants of the Goddess");
        options.put(14, "Lord Pirate");
        options.put(15, "Romeo and Juliet");
        options.put(16, "Resurrection of the Hoblin King");
        options.put(17, "Dragon's Nest");
        // options.put(98, "Astaroth?");
        // options.put(99, "Leafre?");

        final int fieldId = sm.getFieldId();
        if (!AVAILABLE_FIELDS.contains(fieldId)) {
            throw new ScriptError("Tried to use Dimensional Mirror from field ID : %d", fieldId);
        }
        final int answer = sm.askSlideMenu(0, options);
        switch (answer) {
            case 0 -> {
                // Ariant Coliseum :Battle Arena Lobby
                sm.warp(980010000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 1 -> {
                // Mu Lung Dojo : Mu Lung Dojo Entrance
                sm.warp(925020000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 2 -> {
                // Monster Carnival : Spiegelmann's Office
                sm.warp(980000000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 3 -> {
                // The 2nd Monster Carnival : Spiegelmann's Office
                sm.warp(980030000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 4 -> {
                // Sea of Fog : Shipwrecked Ghost Ship
                sm.warp(923020000, "sp");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 5 -> {
                // Hidden Street : Pyramid Dunes
                sm.warp(926010000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 6 -> {
                // Hidden Street : Abandoned Subway Station
                sm.warp(910320000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 7 -> {
                // Hidden Street : Happyville
                sm.warp(209000000, "st00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 8 -> {
                // Golden Temple : Golden Temple
                sm.warp(950100000, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 9 -> {
                // Hidden Street : Moon Bunny Lobby
                sm.warp(910010500, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 10 -> {
                // Hidden Street : First Time Together Lobby
                sm.warp(910340700, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 11 -> {
                // Ludibrium : Eos Tower 101st Floor
                sm.warp(221023300, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 12 -> {
                // Elin Forest : Deep Fairy Forest
                sm.warp(300030100, "west00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 13 -> {
                // Orbis : The Unknown Tower
                sm.warp(200080101, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 14 -> {
                // Herb Town : Over the Pirate Ship
                sm.warp(251010404, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 15 -> {
                // Magatia : Alcadno - Hidden Room
                sm.warp(261000021, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 16 -> {
                // El Nath : Shammos's Solitary Room
                sm.warp(211000002, "out00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
            case 17 -> {
                // Leafre : Crimson Sky Dock
                sm.warp(240080000, "left00");
                sm.setQRValue(QuestRecordType.UnityPortal, String.valueOf(fieldId));
            }
        }
    }

    @Script("unityPortal2")
    public static void unityPortal2(ScriptManager sm) {
        // Henesys : Henesys (100000000)
        //   unityPortal2 (2453, 331)
        // Ellinia : Ellinia (101000000)
        //   unityPortal2 (-169, 357)
        // Perion : Perion (102000000)
        //   unityPortal2 (2071, 1723)
        // Kerning City : Kerning City (103000000)
        //   unityPortal2 (-2140, -53)
        // Sleepywood : Sleepywood (105000000)
        //   unityPortal2 (1297, -166)
        // Dungeon : Sleepywood (105040300)
        //   unityPortal2 (1291, -161)
        // Nautilus : Nautilus Harbor (120000000)
        //   unityPortal2 (3855, 10)
        // Orbis : Orbis Park (200000200)
        //   unityPortal2 (-241, 139)
        // El Nath : El Nath (211000000)
        //   unityPortal2 (-1088, 93)
        // Ludibrium : Ludibrium (220000000)
        //   unityPortal2 (2305, -315)
        // Omega Sector : Omega Sector (221000000)
        //   unityPortal2 (3725, 160)
        // Korean Folk Town : Korean Folk Town (222000000)
        //   unityPortal2 (2566, -208)
        // Aquarium : Aquarium (230000000)
        //   unityPortal2 (-43, -82)
        // Leafre : Leafre (240000000)
        //   unityPortal2 (-409, 88)
        // Mu Lung : Mu Lung (250000000)
        //   unityPortal2 (889, -551)
        // Herb Town : Herb Town (251000000)
        //   unityPortal2 (-129, 236)
        // The Burning Road : Ariant (260000000)
        //   unityPortal2 (-1100, 271)
        // Sunset Road : Magatia (261000000)
        //   unityPortal2 (769, -171)
        // Black Wing Territory : Edelstein (310000000)
        //   unityPortal2 (-95, 587)
        unityPortal(sm);
    }


    // RETURN SCRIPTS --------------------------------------------------------------------------------------------------

    @Script("aMatchMove2")
    public static void aMatchMove2(ScriptManager sm) {
        // Ariant Coliseum : Battle Arena Lobby (980010000)
        //   out00 (-601, 274)
        returnPortal(sm);
    }

    @Script("dojang_exit")
    public static void dojang_exit(ScriptManager sm) {
        // Mu Lung Dojo : Mu Lung Dojo Entrance (925020000)
        //   out00 (-2142, 50)
        returnPortal(sm);
    }

    @Script("mc_out")
    public static void mc_out(ScriptManager sm) {
        //  Monster Carnival : Spiegelmann's Office (980000000)
        //   out00 (-518, 133)
        // The 2nd Monster Carnival : Spiegelmann's Office (980030000)
        //   out00 (-411, 133)
        returnPortal(sm, 103000000, null); // Kerning City : Kerning City
    }

    @Script("aqua_taxi3")
    public static void aqua_taxi3(ScriptManager sm) {
        // Dolphin (2060010)
        //   Sea of Fog : Shipwrecked Ghost Ship (923020000)
        if (sm.askYesNo("Do you want to go back now?")) {
            returnPortal(sm, 230000000, null); // Aquarium : Aquarium
        }
    }

    @Script("nets_out")
    public static void nets_out(ScriptManager sm) {
        // Hidden Street : Pyramid Dunes (926010000)
        //   out00 (-169, 212)
        returnPortal(sm);
    }

    @Script("met_out")
    public static void met_out(ScriptManager sm) {
        // Hidden Street : Abandoned Subway Station (910320000)
        //   out00 (-209, -167)
        returnPortal(sm);
    }

    @Script("goback")
    public static void goback(ScriptManager sm) {
        // Golden Temple : Golden Temple (809060000)
        //   out00 (1830, 472)
        // Golden Temple : Golden Temple (950100000)
        //   out00 (-1391, 470)
        returnPortal(sm);
    }

    @Script("party_exit")
    public static void party_exit(ScriptManager sm) {
        // Hidden Street : First Time Together Lobby (910340700)
        //   out00 (-259, 158)
        returnPortal(sm, 103000000, null); // Kerning City : Kerning City
    }

    @Script("party2_exit")
    public static void party2_exit(ScriptManager sm) {
        // Ludibrium : Eos Tower 101st Floor (221023300)
        //   out00 (173, 2005)
        returnPortal(sm, 221023200, "in00"); // Ludibrium : Eos Tower 100th Floor
    }

    @Script("exit_party6")
    public static void exit_party6(ScriptManager sm) {
        // Elin Forest : Deep Fairy Forest (300030100)
        //   west00 (-344, 149)
        returnPortal(sm, 300030000, "east00"); // Elin Forest : Eastern Region of Mossy Tree Forest
    }

    @Script("exit_party3")
    public static void exit_party3(ScriptManager sm) {
        // Orbis : The Unknown Tower (200080101)
        //   out00 (-322, 173)
        returnPortal(sm, 200080100, "in00"); // Orbis : Entrance to Orbis Tower
    }

    @Script("davy_exit")
    public static void davy_exit(ScriptManager sm) {
        // Herb Town : Over the Pirate Ship (251010404)
        //   out00 (-1954, 243)
        returnPortal(sm, 251010401, "in00"); // Herb Town : Red-Nose Pirate Den 1
    }

    @Script("exit_juliet")
    public static void exit_juliet(ScriptManager sm) {
        // Magatia : Alcadno - Hidden Room (261000021)
        //   out00 (-474, 146)
        returnPortal(sm, 261000020, "in00"); // Magatia : Alcadno Society
    }

    @Script("exit_romio")
    public static void exit_romio(ScriptManager sm) {
        // Magatia : Zenumist - Hidden Room (261000011)
        //   out00 (-314, 181)
        returnPortal(sm, 261000010, "in00"); // Magatia : Zenumist Society
    }

    @Script("exit_shmmosP")
    public static void exit_shmmosP(ScriptManager sm) {
        // El Nath : Shammos's Solitary Room (211000002)
        //   out00 (-282, 64)
        returnPortal(sm, 211000001, "in00"); // El Nath : Chief's Residence
    }

    @Script("exit_dragonR")
    public static void exit_dragonR(ScriptManager sm) {
        // Leafre : Crimson Sky Dock (240080000)
        //   left00 (-512, 80)
        returnPortal(sm, 240030102, "right00"); // Leafre : The Forest That Disappeared
    }
}
