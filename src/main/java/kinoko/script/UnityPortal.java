package kinoko.script;

import kinoko.script.common.Script;
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
                sm.setQRValue(QuestRecordType.UnityPortal, "");
                sm.playPortalSE();
                sm.warp(returnMapId, returnMapId < 540000000 ? "unityPortal2" : "sp"); // missing portal for CBD, NLC and Mushroom Shrine
                return;
            }
            log.error("Tried to use Dimensional Mirror to warp to {}", returnMapId);
        }
        sm.playPortalSE();
        sm.warp(fallbackMapId, fallbackPortalName);
    }

    @Script({ "unityPortal", "unityPortal2" })
    public static void unityPortal(ScriptManager sm) {
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
        if (AVAILABLE_FIELDS.contains(fieldId)) {
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
    }
}
