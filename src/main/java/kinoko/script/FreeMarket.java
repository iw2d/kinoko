package kinoko.script;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.quest.QuestRecordType;

public final class FreeMarket extends ScriptHandler {
    public static void enterPortal(ScriptManager sm, String value) {
        sm.playPortalSE();
        sm.warp(910000000, "out00"); // Hidden Street : Free Market Entrance
        sm.setQRValue(QuestRecordType.FreeMarket, value);
    }

    @Script("market00")
    public static void market00(ScriptManager sm) {
        // Hidden Street : Free Market Entrance (910000000)
        //   out00 (-538, 30)
        final String value = sm.getQRValue(QuestRecordType.FreeMarket);
        sm.playPortalSE();
        switch (value) {
            case "01" -> sm.warp(100000100, "market00");
            case "02" -> sm.warp(220000000, "market00");
            case "03" -> sm.warp(211000100, "market00");
            case "04" -> sm.warp(102000000, "market00");
            case "05" -> sm.warp(230000000, "market01");
            case "06" -> sm.warp(221000000, "market00");
            case "07" -> sm.warp(200000000, "market00");
            case "08" -> sm.warp(801000300, "market00");
            case "09" -> sm.warp(240000000, "market00");
            case "10" -> sm.warp(250000000, "market00");
            case "11" -> sm.warp(251000000, "market00");
            case "12" -> sm.warp(600000000, "market00");
            case "13" -> sm.warp(260000000, "market00");
            case "14" -> sm.warp(222000000, "market00");
            case "15" -> sm.warp(540000000, "market00");
            case "16" -> sm.warp(541000000, "market00");
            case "17" -> sm.warp(120000200, "market00");
            case "18" -> sm.warp(261000000, "market0");
            case "19" -> sm.warp(130000200, "st00");
            case "20" -> sm.warp(550000000, "market00");
            case "21" -> sm.warp(551000000, "market00");
            case "22" -> sm.warp(140000000, "market00");
            case "23" -> sm.warp(103050000, "market00");
            case "24" -> sm.warp(310000000, "market00");
            default -> {
                log.error("Tried to leave Free Market with QR value : {}", value);
                sm.warp(100000100, "market00"); // Henesys : Henesys Market
            }
        }
        sm.setQRValue(QuestRecordType.FreeMarket, "");
    }

    @Script("market01")
    public static void market01(ScriptManager sm) {
        // Henesys : Henesys Market (100000100)
        //   market00 (838, 155)
        enterPortal(sm, "01");
    }

    @Script("market02")
    public static void market02(ScriptManager sm) {
        // Ludibrium : Ludibrium (220000000)
        //   market00 (-684, 103)
        enterPortal(sm, "02");
    }

    @Script("market03")
    public static void market03(ScriptManager sm) {
        // El Nath : El Nath Market (211000100)
        //   market00 (-1098, -85)
        enterPortal(sm, "03");
    }

    @Script("market04")
    public static void market04(ScriptManager sm) {
        // Perion : Perion (102000000)
        //   market00 (1157, 581)
        enterPortal(sm, "04");
    }

    @Script("market05")
    public static void market05(ScriptManager sm) {
        // Aquarium : Aquarium (230000000)
        //   market01 (1511, 38)
        enterPortal(sm, "05");
    }

    @Script("market06")
    public static void market06(ScriptManager sm) {
        // Omega Sector : Omega Sector (221000000)
        //   market00 (3577, 25)
        enterPortal(sm, "06");
    }

    @Script("market07")
    public static void market07(ScriptManager sm) {
        // Orbis : Orbis (200000000)
        //   market00 (2399, -517)
        enterPortal(sm, "07");
    }

    @Script("market08")
    public static void market08(ScriptManager sm) {
        // Zipangu : Showa Street Market (801000300)
        //   market00 (258, 151)
        enterPortal(sm, "08");
    }

    @Script("market09")
    public static void market09(ScriptManager sm) {
        // Leafre : Leafre (240000000)
        //   market00 (-1936, -32)
        enterPortal(sm, "09");
    }

    @Script("market10")
    public static void market10(ScriptManager sm) {
        // Mu Lung : Mu Lung (250000000)
        //   market00 (-197, -547)
        enterPortal(sm, "10");
    }

    @Script("market11")
    public static void market11(ScriptManager sm) {
        // Herb Town : Herb Town (251000000)
        //   market00 (1020, -66)
        enterPortal(sm, "11");
    }

    @Script("market12")
    public static void market12(ScriptManager sm) {
        // New Leaf City : NLC Town Center (600000000)
        //   market00 (2021, 18)
        enterPortal(sm, "12");
    }

    @Script("market13")
    public static void market13(ScriptManager sm) {
        // The Burning Road : Ariant (260000000)
        //   market00 (433, 273)
        enterPortal(sm, "13");
    }

    @Script("market14")
    public static void market14(ScriptManager sm) {
        // Korean Folk Town : Korean Folk Town (222000000)
        //   market00 (-1399, 149)
        enterPortal(sm, "14");
    }

    @Script("market15")
    public static void market15(ScriptManager sm) {
        // Singapore : CBD (540000000)
        //   market00 (4582, 43)
        enterPortal(sm, "15");
    }

    @Script("market16")
    public static void market16(ScriptManager sm) {
        // Singapore : Boat Quay Town (541000000)
        //   market00 (34, -109)
        enterPortal(sm, "16");
    }

    @Script("market17")
    public static void market17(ScriptManager sm) {
        // Nautilus : Mid Floor - Hallway (120000200)
        //   market00 (1867, 148)
        enterPortal(sm, "17");
    }

    @Script("market18")
    public static void market18(ScriptManager sm) {
        // Sunset Road : Magatia (261000000)
        //   market0 (336, -227)
        enterPortal(sm, "18");
    }

    @Script("market19")
    public static void market19(ScriptManager sm) {
        // Empress' Road : Crossroads of Ereve (130000200)
        //   st00 (-160, 91)
        enterPortal(sm, "19");
    }

    @Script("market20")
    public static void market20(ScriptManager sm) {
        // Malaysia : Trend Zone Metropolis (550000000)
        //   market00 (2685, 649)
        enterPortal(sm, "20");
    }

    @Script("market21")
    public static void market21(ScriptManager sm) {
        // Malaysia : Kampung Village (551000000)
        //   market00 (-233, 132)
        enterPortal(sm, "21");
    }

    @Script("market22")
    public static void market22(ScriptManager sm) {
        // Snow Island : Rien (140000000)
        //   market00 (772, -335)
        enterPortal(sm, "22");
    }

    @Script("market23")
    public static void market23(ScriptManager sm) {
        // Victoria Road : Kerning City Back Alley (103050000)
        //   market00 (-471, 154)
        enterPortal(sm, "23");
    }

    @Script("market24")
    public static void market24(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   market00 (780, -16)
        enterPortal(sm, "24");
    }
}
