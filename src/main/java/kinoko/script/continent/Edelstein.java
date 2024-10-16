package kinoko.script.continent;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.item.BodyPart;
import kinoko.world.item.Item;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class Edelstein extends ScriptHandler {

    //region NPCs
    @Script("WendelinHeal")
    public static void WendelinHeal(ScriptManager sm) {
        // Wendelline : Treatment Specialist (2151006)
        //   Resistance Headquarters : Training Room Entrance (310010010)
        if (sm.askYesNo("Are you hurt? Allow me to treat your wounds.")) {
            final User user = sm.getUser();
            user.setHp(user.getMaxHp());
            user.setMp(user.getMaxMp());
            sm.sayOk("There you go. You're fully healed.");
        }
    }

    @Script("edelstein_taxi")
    public static void Edelstein_taxi(ScriptManager sm) {
        // Taxi : Affiliated with Black Wings (2150007)
        //   Black Wing Territory : Edelstein (310000000)
        if (!sm.askYesNo("Hello, welcome to the Edelstein Taxi. I can take members of the Black Wings safely and quickly to #bVerne Mines#k. And if you're not part of the Black Wings? Well, I guess I'll take you as long as you pay... So, are you going to the mines?")) {
            sm.sayNext("Let me know if you change your mind.");
            return;
        }
        final Item equippedCap = sm.getUser().getInventoryManager().getEquipped().getItem(BodyPart.CAP.getValue());
        final boolean hat = equippedCap != null && equippedCap.getItemId() == 1003134;
        final int cost = hat ? 3000 : 10000;
        String askWhat = hat ? "Oh, you're a member of the Black Wings. I have a special discount for the Black Wings. You can ride for a mere #b3000 Mesos#k. Hop on." :
                "Please pay #b10000 Mesos#k to go to Verne Mine."; //Not GMS-like
        if (!sm.askYesNo(askWhat)) {
            sm.sayNext("Let me know if you change your mind.");
            return;
        }
        if (!sm.addMoney(-cost)) {
            sm.sayOk("It appears that you do not have enough mesos.");
            return;
        }
        sm.warp(310040200);
    }

    @Script("intoResiTR")
    public static void intoResiTR(ScriptManager sm) {
        // Elevator Control (2151007)
        //   Resistance Headquarters : Training Room Entrance (310010010)
        sm.setSpeakerId(2151007);
        final List<Tuple<Integer, String>> rooms = List.of(
                Tuple.of(310010100, "Underground 2nd Floor Training Room A"),
                Tuple.of(310010200, "Underground 3rd Floor Training Room B"),
                Tuple.of(310010300, "Underground 4th Floor Training Room C"),
                Tuple.of(310010400, "Underground 5th Floor Training Room D"),
                Tuple.of(931000400, "Underground 6th Floor Training Room E")
        );
        final Map<Integer, String> options = createOptions(rooms, Tuple::getRight);
        final int answer = sm.askMenu("An elevator that will take you to your desired training room.\r\nChoose the floor you'd like to go to.", options);
        if (answer >= 0 && answer < rooms.size()) {
            final int mapId = rooms.get(answer).getLeft();
            if (answer == 4) {
                if (!sm.hasQuestStarted(23118)) {
                    sm.sayOk("You cannot go there at this time."); //Unsure if GMS-like
                    return;
                }
                sm.playPortalSE();
                sm.warpInstance(mapId, "out00", 310010000, 60 * 5);
                return;
            }
            sm.playPortalSE();
            sm.warp(mapId, "out00");
        }
    }

    @Script("giveDress")
    public static void giveDress(ScriptManager sm) {
        // Gabrielle's Suitcase (2159301)
        //   Edelstein : Guarded Mansion (931010010)
        final User user = sm.getUser();
        if (!user.getField().getMobPool().isEmpty()) {
            sm.message("Defeat the robots first."); //Unsure if GMS-like
            return;
        }
        if (sm.addItem(4032757, 1)) {
            sm.sayOk("#b(You've grabbed Gabrielle's clothes. Deliver them quickly to #p2152006#.)#k");
            return;
        }
        sm.sayOk("#bYour Etc inventory is full. Empty out at least one slot.#k"); //Not GMS-like
    }

    @Script("talk2159305")
    public static void talk2159305(ScriptManager sm) {
        // Wonny (2159305)
        //   Black Wing Territory : Edelstein (310000000)
        if (sm.hasQuestStarted(23938)) {
            final LocalDateTime now = LocalDateTime.now();
            if (now.getHour() == 22 && !sm.hasQRValue(QuestRecordType.EdelsteinWonny10PM, "1")) {
                sm.setQRValue(QuestRecordType.EdelsteinWonny10PM, "1");
                sm.sayNext("What are you looking at? I'm not standing here because I miss #p2154004#. I just... want to make sure that no thieves get in. Yeah.");
            }
        }
    }

    @Script("talkPavio")
    public static void talkPavio(ScriptManager sm) {
        // Fabio (2159300)
        //   Edelstein : Shady Hair Salon (931010030)
        if (sm.getField().getMobPool().isEmpty()) {
            sm.sayNext("What are you doing?! How dare you try to destroy the #o5100002#s I worked so hard to obtain! Do you realize how expensive these are? I paid an exorbitant amount of money so that I could learn a new perm!!");
            sm.sayBoth("What? You've come in the name of the Watchmen to keep an eye on me? I don't even have the freedom to pursure beautiful hair?! I won't stand for this! I'm going to require compensation for the damage you've caused!");
            sm.setQRValue(QuestRecordType.EdelsteinFabioFirebombs, "1");
        }
    }

    @Script("q23905e")
    public static void q23905e(ScriptManager sm) {
        // Town Filled With Suspicion (23905 - end)
        sm.sayNext("So you met #p2152016#. I told you it wouldn't be pleasant. Still, #p2152016# is the nicest of all the <Watchmen>.");
        sm.sayBoth("As you've no doubt learned, our town is under the control of the Black Wings. And let me make one thing clear: we hate it. That's why we were suspicious of you, an outsider.");
        sm.sayBoth("Now that it's been confirmed that you are not one of <Them>, the townspeople won't be suspicious of you. So let me be the first to welcome you to #m310000000#! As long as you do not aid our enemies, the residents of #m310000000# will accept you as a friend.");
        sm.setQRValue(QuestRecordType.EdelsteinUnlockTownQuests, "1"); //Unsure if this is only used for resistance chars, but this probably makes sense? since they'll trust you...
        sm.forceCompleteQuest(23905);
    }

    @Script("edelItem0")
    public static void reactorTree(ScriptManager sm) {
        // edelItem0 (3102000)
        //   Black Wing Territory : Edelstein (310000000)
        //   Concrete Road : Edelstein Park (310020000)
        //   Concrete Road : Edelstein Park 2 (310020100)
        //   Concrete Road : Edelstein Park 3 (310020200)
        sm.dropRewards(List.of(
                Reward.item(2022712, 1, 1, 1.0),
                Reward.item(2022712, 1, 1, 0.7),
                Reward.item(2022712, 1, 1, 0.3)
        ));
    }

    @Script("edelItem1")
    public static void reactorDahlia(ScriptManager sm) {
        // edelItem1 (3102001)
        //   Concrete Road : Edelstein Park 3 (310020200)
        //   Hidden Street : Dahlia Garden (931010000)
        sm.dropRewards(List.of(
                Reward.item(4032752, 1, 1, 1, 23919)
        ));
    }

    @Script("enterResiTR")
    public static void enterResiTR(ScriptManager sm) {
        // Resistance Headquarters : Training Room Entrance (310010010)
        //   in00 (-397, 62)
        intoResiTR(sm);
    }

    @Script("enterDangerHair")
    public static void enterDangerHair(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   in03 (1090, 587)
        sm.playPortalSE();
        if (sm.hasQuestStarted(23940) && !sm.hasQRValue(QuestRecordType.EdelsteinFabioFirebombs, "1")) {
            final LocalDateTime now = LocalDateTime.now();
            if (now.getHour() == 18) {
                sm.warpInstance(931010030, "out00", 310000000, 10 * 60);
                return;
            }
        }
        sm.warp(310000003, "out00"); // Edelstein : Edelstein Hair Salon
    }

    @Script("outPavio")
    public static void outPavio(ScriptManager sm) {
        // Edelstein : Shady Hair Salon (931010030)
        //   out00 (308, 24)
        sm.playPortalSE();
        sm.warp(310000000, "in03");
    }

    @Script("enterMansion")
    public static void enterMansion(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   in01 (1290, -53)
        sm.playPortalSE();
        if (sm.hasQuestStarted(23925) && !sm.hasItem(4032757)) {
            sm.warpInstance(931010010, "out00", 310000000, 10 * 60);
            sm.scriptProgressMessage("Defeat the robots and get Gabrielle's clothes.");
            return;
        }
        sm.warp(310000004, "out00");
    }

    @Script("outMansion")
    public static void outMansion(ScriptManager sm) {
        // Edelstein : Guarded Mansion (931010010)
        //   out00 (-325, 46)
        sm.playPortalSE();
        sm.warp(310000000, "in01");
    }

    @Script("enterSecJobResi")
    public static void enterSecJobResi(ScriptManager sm) {
        // Black Wing Territory : Edelstein (310000000)
        //   in02 (1864, -16)
        sm.playPortalSE();
        if (sm.hasQuestStarted(23023) || sm.hasQuestStarted(23024) || sm.hasQuestStarted(23025)) {
            sm.warpInstance(List.of(931000100, 931000101), "out00", 310000000, 10 * 60);
            return;
        }
        if (sm.hasQuestStarted(23121) && !sm.hasQRValue(QuestRecordType.ResistanceWaterTrade, "1")) {
            sm.warpInstance(931000420, "out00", 310000000, 10 * 60);
            sm.scriptProgressMessage("Thieves have attacked! Defeat all the thieves and then go see Ace the Pilot.");
            return;
        }
        sm.warp(310000010, "out00"); // Edelstein : Edelstein Temporary Airport
    }

    @Script("enterBlackWing")
    public static void enterBlackWing(ScriptManager sm) {
        // Dry Road : Mine Entrance (310040200)
        //   east00 (2240, -15)
        final Item equippedCap = sm.getUser().getInventoryManager().getEquipped().getItem(BodyPart.CAP.getValue());
        if (equippedCap == null || equippedCap.getItemId() != 1003134) {
            sm.message("You can't enter without proof that you are a member of the Black Wings. Equip something with the Black Wings' logo on it to enter.");
            return;
        }
        sm.playPortalSE();
        sm.warp(310050000, "west00");
    }

    @Script("enterFranRoom")
    public static void enterFranRoom(ScriptManager sm) {
        // Verne Mine : Power Plant Lobby (310050000)
        //   in00 (-289, 17)
        sm.warpInstance(931020010, "out00", 310050000, 10 * 60);
    }

    @Script("enterEelroom")
    public static void enterEelroom(ScriptManager sm) {
        // Verne Mine : Power Plant Lobby (310050000)
        //   in01 (728, 17)
        sm.warpInstance(931020011, "out00", 310050000, 10 * 60);
    }

    @Script("ThirdJobResi")
    public static void ThirdJobResi(ScriptManager sm) {
        // Verne Mine : Power Plant Security (310050100)
        //   in00 (793, 16)
        sm.playPortalSE();
        if (sm.hasQuestStarted(23033) || sm.hasQuestStarted(23034) || sm.hasQuestStarted(23035)) {
            sm.warpInstance(931000200, "out00", 310050100, 60 * 15);
        }
    }

    @Script("FourthJobResi")
    public static void FourthJobResi(ScriptManager sm) {
        // Hidden Street : Leery Corridor (310060221)
        //   in00 (953, 20)
        sm.playPortalSE();
        if (!sm.hasItem(4032743)) {
            sm.message("You cannot enter without a keycard."); //Not GMS-like
            return;
        }
        if (sm.hasQuestStarted(23043) || sm.hasQuestStarted(23044) || sm.hasQuestStarted(23045)) {
            sm.message("Find the missing Job Instructor!");
            if (JobConstants.isBattleMageJob(sm.getUser().getJob())) {
                sm.warpInstance(List.of(931000300, 931000310, 931000320), "sp", 310050100, 60 * 15);
            } else if (JobConstants.isWildHunterJob(sm.getUser().getJob())) {
                sm.warpInstance(List.of(931000301, 931000311, 931000321), "sp", 310050100, 60 * 15);
            } else if (JobConstants.isMechanicJob(sm.getUser().getJob())) {
                sm.warpInstance(List.of(931000302, 931000312, 931000322), "sp", 310050100, 60 * 15);
            }
        }
    }

    @Script("enterNewWeapon1")
    public static void enterNewWeapon1(ScriptManager sm) {
        // Hidden Street : Booby Trap! Laboratory Jail (931000310)
        //   west00 (-799, -103)
        sm.playPortalSE();
        sm.warp(931000320);
    }

    @Script("enterNewWeapon2")
    public static void enterNewWeapon2(ScriptManager sm) {
        // Hidden Street : Booby Trap! Laboratory Jail (931000311)
        //   west00 (-791, -104)
        sm.playPortalSE();
        sm.warp(931000321);
    }

    @Script("enterNewWeapon3")
    public static void enterNewWeapon3(ScriptManager sm) {
        // Hidden Street : Booby Trap! Laboratory Jail (931000312)
        //   west00 (-798, -105)
        sm.playPortalSE();
        sm.warp(931000322);
    }

    @Script("edelPortal0")
    public static void edelPortal0(ScriptManager sm) {
        // edelPortal0 (3100000)
        //   Verne Mine : Power Plant Security (310050100)
        sm.warp(931020000, "out00");
        sm.setReactorState(3100000, 0);
    }

    @Script("edelPortal1")
    public static void edelPortal1(ScriptManager sm) {
        // edelPortal1 (3100001)
        //   Verne Mine : Power Plant Security (310050100)
        sm.warp(931020001, "out00");
        sm.setReactorState(3100001, 0);
    }

    @Script("edelPortal2")
    public static void edelPortal2(ScriptManager sm) {
        // edelPortal2 (3100002)
        //   Verne Mine : Power Plant Security (310050100)
        sm.warp(931020002, "out00");
        sm.setReactorState(3100002, 0);
    }
}
