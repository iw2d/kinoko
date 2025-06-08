package kinoko.script.quest;

import kinoko.packet.world.WvsContext;
import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.item.InventoryType;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.stat.Stat;

import java.util.List;

public final class EvanQuest extends ScriptHandler {
    public static final int SAFE_GUARD = 9300389;

    // FIELDS
    @Script("dollCave01")
    public static void dollCave01(ScriptManager sm) {
    }

    @Script("moveSDIRit")
    public static void moveSDIRit(ScriptManager sm) {
    }

    @Script("move_RitSDI")
    public static void move_RitSDI(ScriptManager sm) {
    }

    @Script("onSDI")
    public static void onSDI(ScriptManager sm) {
        // Slumbering Dragon Island  : Snowy Forest (914100010)
        if (sm.getQRValue(QuestRecordType.EvanSnowDragon).isBlank()) {
            sm.setQRValue(QuestRecordType.EvanSnowDragon, "1");
        }
    }

    @Script("evanTogether")
    public static void evanTogether(ScriptManager sm) {
    }


    // NPCS ------------------------------------------------------------------------------------------------------------

    @Script("periPatrol")
    public static void periPatrol(ScriptManager sm) {
        // Perion Warning Post (1022107)
        //   North Rocky Mountain : Dusty Wind Hill (102020100)
        //   Burnt Land : Wild Boar Land (102030000)
        //   Burnt Land : Wild Pig Land (102030100)
        //   Burnt Land : Armor Pig Land (102030200)
        //   Burnt Land : Burning Heat (102030300)
        if (!sm.hasQuestStarted(22530) || sm.getQRValue(QuestRecordType.EvanPerionSigns).equals("5")) {
            sm.sayOk("Information regarding the creatures of the area is displayed.");
            return;
        }
        switch (sm.getFieldId()) {
            case 102020100 -> {
                if (sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("1")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(I've already reviewed this sign.)");
                    return;
                }
                sm.sayNext("#b#m102020100# Warning Sign#k\r\n\r\nCreatures: #r#o130100##k, #r#o1110101##k\r\nNotes: #r3-Way Road to #r#m102030300##k\r\nCheck:");
                sm.setPlayerAsSpeaker(true);
                if (!sm.askYesNo("#b(There's no incorrect information on the #m102020100# Warning Sign.)")) {
                    sm.sayOk("#b(I should probably check off that this sign is correct...)");
                    return;
                }
                sm.sayNext("#b(I'll just mark off that the #m102020100# Warning Sign is correct and...)");
                sm.setPlayerAsSpeaker(false);
                sm.sayBoth("#b#m102020100# Warning Sign#k\r\n\r\nCreatures: #r#o130100##k, #r#o1110101##k\r\nNotes: #r3-Way Road to #m102030300##k\r\nCheck: #eO#k");
                sm.setPlayerAsSpeaker(true);
                sm.sayBoth("#bOne #p1022107##k has been checked. Four more to go.");
                sm.setQRValue(QuestRecordType.EvanPerionSigns, "1");
            }
            case 102030000 -> {
                if (sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("2")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(I've already reviewed this sign.)");
                    return;
                }
                if (!sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("1")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(Hmm, it seems I missed a sign. I better go back.)");
                    return;
                }
                sm.sayNext("#b#m102030000# Warning Sign#k\r\n\r\nCreatures: #r#o2130100##k\r\nNotes: None\r\nCheck:");
                sm.setPlayerAsSpeaker(true);
                if (!sm.askYesNo("#b(There appears to be some incorrect information on this Warning Sign.)")) {
                    sm.sayOk("#b(I should probably correct the misinformation on this Warning Sign...)");
                    return;
                }
                sm.sayNext("#b(I'll just correct the information on the #m102030000# Warning Sign...)");
                sm.setPlayerAsSpeaker(false);
                sm.sayBoth("#b#m102030000# Warning Sign#k\r\n\r\nCreatures: #r#o2230102##k, #r#o2230112##k\r\nNotes: None\r\nCheck: #eO#k");
                sm.setPlayerAsSpeaker(true);
                sm.sayBoth("#bTwo #p1022107#s#k have been checked. Three more to go.");
                sm.setQRValue(QuestRecordType.EvanPerionSigns, "1;2");
            }
            case 102030100 -> {
                if (sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("3")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(I've already reviewed this sign.)");
                    return;
                }
                if (!sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("2")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(Hmm, it seems I missed a sign. I better go back.)");
                }
                sm.sayNext("#b#m102030100# Warning Sign#k\r\n\r\nCreatures: #r#o2230102##k, #r#o4230103##k\r\nNotes: None\r\nCheck:");
                sm.setPlayerAsSpeaker(true);
                if (!sm.askYesNo("#b(There's no incorrect information on the #m102030100# Warning Sign.)")) {
                    sm.sayOk("#b(I should probably check off that this sign is correct...)");
                    return;
                }
                sm.sayNext("#b(I'll just mark off that the #m102030100# Warning Sign is correct and...)");
                sm.setPlayerAsSpeaker(false);
                sm.sayBoth("#b#m102030100# Warning Sign#k\r\n\r\nCreatures: #r#o2230102##k, #r#o4230103##k\r\nNotes: None\r\nCheck: #eO#k");
                sm.setPlayerAsSpeaker(true);
                sm.sayBoth("#bThree #p1022107#s#k have been checked. Two more to go.");
                sm.setQRValue(QuestRecordType.EvanPerionSigns, "1;2;3");
            }
            case 102030200 -> {
                if (sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("4")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#bI've already reviewed this sign.");
                    return;
                }
                if (!sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("3")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(Hmm, it seems I missed a sign. I better go back.)");
                    return;
                }
                sm.sayNext("#b#m102030200# Warning Sign#k\r\n\r\nCreatures: #r#o4230103##k, #r#o4230400##k\r\nNotes: None\r\nCheck:");
                sm.setPlayerAsSpeaker(true);
                if (!sm.askYesNo("#b(There's no incorrect information on the #m102030200# Warning Sign.)")) {
                    sm.sayOk("#b(I should probably check off that this sign is correct...)");
                    return;
                }
                sm.sayNext("#b(I'll just mark off that the #m102030200# Warning Sign is correct and...)");
                sm.setPlayerAsSpeaker(false);
                sm.sayBoth("#b#m102030200# Warning Sign#k\r\n\r\nCreatures: #r#o4230103##k, #r#o4230400##k\r\nNotes: None\r\nCheck: #eO#k");
                sm.setPlayerAsSpeaker(true);
                sm.sayBoth("#bFour #p1022107#s#k have been checked. One more to go.");
                sm.setQRValue(QuestRecordType.EvanPerionSigns, "1;2;3;4");
            }
            case 102030300 -> {
                if (sm.getQRValue(QuestRecordType.EvanPerionSigns).contains("4")) {
                    sm.setPlayerAsSpeaker(true);
                    sm.sayOk("#b(Hmm, it seems I missed a sign. I better go back.)");
                    return;
                }
                sm.sayNext("#b#m102030300# Warning Sign#k\r\n\r\nCreatures: #r#o4230400##k\r\nNotes: None\r\nCheck:");
                sm.setPlayerAsSpeaker(true);
                if (!sm.askYesNo("#b(There appears to be some incorrect information on this Warning Sign.)")) {
                    sm.sayOk("#b(I should probably correct the misinformation on this Warning Sign...)");
                    return;
                }
                sm.sayNext("#b(I'll just correct the information on the #m102030300# Warning Sign...)");
                sm.setPlayerAsSpeaker(false);
                sm.sayNext("#b#m102030300# Warning Sign#k\r\n\r\nCreatures: #r#o3210100##k, #r#o4230400##k\r\nNotes: None\r\nCheck: #eO#k");
                sm.setPlayerAsSpeaker(true);
                sm.sayBoth("All #b#p1022107#s#k have been checked. Let's report back to #b#p1040001##k.");
                sm.setQRValue(QuestRecordType.EvanPerionSigns, "5");
            }
        }
    }

    @Script("downCamillar")
    public static void downCamillar(ScriptManager sm) {
        // Camila (1013201)
        if (sm.hasQuestStarted(22557)) {
            if (sm.getUser().getField().getMobPool().getCount() > 0) {
                sm.setPlayerAsSpeaker(true);
                sm.sayOk("#b(I can't rescue #p1013201# with that #o9300387# there.)");
                return;
            }
            sm.sayOk("You... You rescued me. Thank you... Now, let's get out of here.");
            sm.setQRValue(QuestRecordType.EvanEnragedGolem, "2");
            sm.warp(100000000, "gm00"); // Not GMS-like. Just a QoL / convenience change. warps you next to chief stan
        }
    }

    @Script("contimoveSDIRit")
    public static void contimoveSDIRit(ScriptManager sm) {
        // Olaf (1002101)
        //   Lith Harbor : Lith Harbor (104000000)
        if (sm.hasQuestCompleted(22579)) {
            if (!sm.askAccept("Would you like to set sail? It's a pretty lengthy trip, it'll take some time to get there. Approximately #b15 minutes#k... It's not the kind of place you want to go to without reason. Would you like to go?")) {
                sm.sayOk("Just come back and let me know if you change your mind.");
                return;
            }
            sm.warpInstance(200090080, "sp", 914100000, 60 * 15); // Yes, it's a 15 minute ride
            return;
        }
        sm.sayOk("Sorry, the ship isn't quite ready to set sail.");
    }

    @Script("contimoveRitSDI")
    public static void contimoveRitSDI(ScriptManager sm) {
        // Olaf (1013207)
        //   Olaf's Voyage : To the Slumbering Dragon Island  (200090080)
        //   Olaf's Voyage : To Lith Harbor (200090090)
        //   Slumbering Dragon Island  : Temporary Harbor  (914100000)
        if (sm.getUser().getFieldId() == 914100000) {
            if (!sm.askAccept("Do you want to return to #b#m104000000##k?")) {
                sm.sayOk("Are you sure? What are you even doing here?");
                return;
            }
            sm.sayOk("All aboard!");
            sm.warpInstance(200090090, "sp", 104000000, 60 * 15); // Yes, it's a 15 minute ride
            return;
        }
        if (sm.getUser().getFieldId() == 200090090) {
            if (!sm.askAccept("Do you want to return to #bSlumbering Dragon Island#k?")) {
                sm.sayOk("Alright, sit back and relax! We'll arrive at #b#m104000000##k in no time!");
                return;
            }
            sm.sayOk("Alright, let's turn 'er around then!");
            sm.warp(914100000);
            return;
        }
        if (sm.getUser().getFieldId() == 200090080) {
            if (!sm.askAccept("Do you want to return to #b#m104000000##k?")) {
                sm.sayOk("Are you sure? Why are you even going there?");
                return;
            }
            sm.sayOk("Alright, let's turn 'er around then!");
            sm.warp(104000000);
        }
    }

    @Script("ibech")
    public static void ibech(ScriptManager sm) {
        // Hiver : Black Wing Captain (1013203)
        //   Hidden Street : Frog House  (922030000)
        if (sm.hasQuestStarted(22582)) {
            sm.warp(922030002, "out00");
            return;
        }
        // I'm pretty sure these aren't GMS95-like, but it's here for convenience.
        if (sm.hasQuestStarted(22583)) {
            sm.warpInstance(List.of(
                    922030010,
                    922030011
            ), "out00", 220011000, 60 * 10);
            return;
        }
        if (sm.hasQuestStarted(22584)) {
            sm.warpInstance(List.of(
                    922030020,
                    922030021,
                    922030022
            ), "out00", 220011000, 60 * 10);
        }
    }

    @Script("Afirentalk")
    public static void Afirentalk(ScriptManager sm) {
        // Afrien (1013205)
        //   Afrien's Memory  : Behind the Stronghold  (900030000)
        if (sm.hasQuestStarted(22591)) {
            sm.sayNext("Are you okay, master? You look so tired...");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bI'm fine. Aran is the only one who's hurt since she was fighting on the front lines. But everyone's all right now. How about you? Are you okay?");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("No problem whatsoever...");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bI'm not worried about your physical state. I'm more worried about your heart. Your entire race has been completely-");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("...");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bI'm so sorry. I got you into this whole mess. I should've let you go with the Black Mage. If you had gone with the Black Mage, all the Onyx Dragons would've survived!");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("Don't be silly, master... We fought because we chose to. It is not your fault.");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#b... But.");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("I don't care how much the Black Mage wants our powers, we'd never align ourselves with him. We Onyx Dragons belong with humans.You are the ones with such strong spirits. We could never become one with such an evil being.");
            sm.sayBoth("So please do not apologize, master... Freud. Even if we are completely annihilated, this is our choice. You must respect our wishes.");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bAfrien...");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("I have one request, though. If I... die in a final battle against the Black Mage, could you watch over my child? It will be a long, long time before it hatches from its egg, but... I trust you with it.");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bDon't say things like that, Afrien. You must stay alive and take care of your own child!");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("Who knows whether either of us will survive? That is why I'm asking you. Promise me, master?");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bOkay, okay, I promise. But you need to promise me something, too. You have to promise me that you will do everything in your power to survive.");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("Done, master.");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bDo not sacrifice yourself on my behalf...");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("... Freud...");
            sm.sayBoth("...");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("...");
            sm.sayBoth("#bAfrien... Thank you...");
            sm.setQRValue(QuestRecordType.EvanAfrienMemory, "1");
            sm.warp(914100021);
        }
    }

    @Script("froghiver")
    public static void froghiver(ScriptManager sm) {
        // Hiver (1013206)
        if (sm.hasQuestStarted(22596)) {
            sm.sayNext("I can't believe you've come all this way. Judging by the look on your face, you are quite angry.");
            sm.setPlayerAsSpeaker(true);
            sm.sayBoth("#bYou deceived me! You were lying to me all this time! I will not let you get away with this!");
            sm.setPlayerAsSpeaker(false);
            sm.sayBoth("What do you mean I deceived you? You let yourself imagine what you wanted. Anyway, thanks to you, we were able to accomplish quite a lot. But you are nothing but a hindrance now.");
            sm.sayBoth("You better get out of here. Now!");
            sm.spawnMob(9300393, MobAppearType.NORMAL, 230, 31, true);
            sm.removeNpc(9300393);
        }
    }

    @Script("SDIhiver")
    public static void SDIhiver(ScriptManager sm) {
        // Hiver (1013204)
        if (sm.getUser().getField().getMobPool().getCount() > 0) {
            sm.sayOk("Heh heh heh...");
            return;
        }
        sm.sayNext("Whoa... I had no clue you were THIS strong. Oh well, I'll have to retreat for now. The next time I see you... I suppose we'll be enemies, huh?");
        sm.setQRValue(QuestRecordType.EvanAfrien, "1");
        sm.warp(914100021, "out00");
    }


    // PORTALS ---------------------------------------------------------------------------------------------------------

    @Script("evanEntrance")
    public static void evanEntrance(ScriptManager sm) {
        // Farm Street : Large Forest Trail 2 (100030320)
        //   west00 (-2046, 36)
        if (sm.getJob() == 2001) {
            sm.message("You are not allowed to leave the farm yet.");
            return;
        }
        sm.warp(100030400, "east00");
    }

    @Script("enterDollcave")
    public static void enterDollcave(ScriptManager sm) {
        // South Rocky Mountain : Rocky Wasteland (102010100)
        //   in00 (502, 1901)
        if (sm.hasQuestStarted(22549) || sm.hasQuestCompleted(22549)) {
            sm.playPortalSE();
            sm.warp(910050300, "out00");
            return;
        }
        sm.message("A mysterious force prevents you from entering.");
    }

    @Script("evanGolemDoor")
    public static void evanGolemDoor(ScriptManager sm) {
        // Singing Mushroom Forest : Windflower Forest (100020200)
        //   east00 (1287, -470)
        if (sm.hasQuestStarted(22557)) {
            sm.playPortalSE();
            sm.warpInstance(910600000, "out00", 100020200, 60 * 10);
            sm.spawnNpc(1013201, 390, 305, false, false);
            return;
        }
        sm.playPortalSE();
        sm.warp(100040000, "west00");
    }

    @Script("evanDollGR")
    public static void evanDollGR(ScriptManager sm) {
        // Golem's Temple : Golem's Temple Entrance (100040000)
        //   scr00 (1453, 252)
        if (sm.hasQuestStarted(22556)) {
            sm.setQRValue(QuestRecordType.EvanEnragedGolem, "1");
            sm.setPlayerAsSpeaker(true);
            sm.sayOk("#bHmm, this puppet looks familiar. I better go report back to #p1012003#.");
            return;
        }
        if (sm.hasQuestStarted(22559)) {
            sm.playPortalSE();
            sm.warp(910600010, "out00");
        }
    }

    @Script("enterBlackRoom")
    public static void enterBlackRoom(ScriptManager sm) {
        // Orbis : Orbis Tower <16th Floor> (200080600)
        //   in00 (-46, -1447)
        sm.playPortalSE();
        sm.warp(200080601, "out00");
    }

    @Script("goQuest_22575")
    public static void goQuest_22575(ScriptManager sm) {
        // El Nath : Chief's Residence (211000001)
        //   out01 (304, 185)
        if (sm.hasQuestStarted(22575)) {
            sm.playPortalSE();
            sm.warp(921110100, "out00");
            return;
        }
        sm.message("The door is locked.");
    }

    @Script("goQuest_22404")
    public static void goQuest_22404(ScriptManager sm) {
        // Aquarium : Zoo (230000003)
        //   out02 (-7, -150)
        if (sm.hasQuestStarted(22404)) {
            sm.playPortalSE();
            sm.warp(923030000, "out00");
            return;
        }
        sm.setSpeakerId(2060005);
        sm.message("Uhm, excuse me? What are you doing? You're not allowed in the animal exhibits.");
    }

    @Script("enterSnowDragon")
    public static void enterSnowDragon(ScriptManager sm) {
        // Slumbering Dragon Island  : Snowy Forest (914100010)
        //   in00 (2545, 84)
        if (sm.hasQuestStarted(22580)) {
            sm.playPortalSE();
            sm.warp(914100020, "out00");
            return;
        }
        if (sm.hasQuestStarted(22588)) {
            sm.playPortalSE();
            sm.warpInstance(914100020, "out00", 922030000, 60 * 10);
            sm.spawnReactor(1409000, -243, 6, false, -1, false);
            return;
        }
        if (sm.hasQuestStarted(22589)) {
            sm.playPortalSE();
            sm.warpInstance(914100023, "out00", 914100010, 60 * 5);
            sm.spawnNpc(1013204, -245, 53, false, false);
            return;
        }
        if (sm.hasQuestCompleted(22589)) {
            sm.playPortalSE();
            sm.warp(914100021, "out00");
            return;
        }
        sm.message("A mysterious force prevents you from entering.");
    }

    @Script("stopIceWall")
    public static void stopIceWall(ScriptManager sm) {
        // Slumbering Dragon Island  : Cave of Silence (914100020)
        //   scr00 (56, 99)
        //   scr01 (56, 2)
        //   scr02 (56, -95)
        //   scr03 (56, -193)
        //   scr04 (56, -290)
        //   scr05 (153, 100)
        //   scr06 (153, 2)
        //   scr07 (154, -93)
        //   scr08 (155, -192)
        //   scr09 (155, -290)
        if (sm.getQRValue(QuestRecordType.EvanSnowDragon).equals("1")) {
            sm.setQRValue(QuestRecordType.EvanSnowDragon, "2");
            sm.warp(914100020, "out00");
            return;
        }
        sm.warp(914100020, "out00");
    }

    @Script("enterBlackFrog")
    public static void enterBlackFrog(ScriptManager sm) {
        // Ludibrium : Ludibrium Village (220000300)
        //   scr00 (703, 104)
        if (sm.hasQuestStarted(22596)) {
            sm.playPortalSE(); // TODO - don't spawn after defeating hiver
            sm.warpInstance(922030001, "out00", 211000300, 60 * 10);
            sm.spawnNpc(1013206, 175, 31, false, false);
        } else if (sm.hasQuestStarted(22581) || sm.hasQuestCompleted(22581)) {
            // After this quest, you have free access to the map WITH Hiver until a certain point
            sm.playPortalSE();
            sm.warp(922030000, "out00");
        } else {
            sm.playPortalSE();
            sm.warp(922030001, "out00"); // Everyone else warp to map WITHOUT Hiver
        }
    }

    @Script("enterBlackBC")
    public static void enterBlackBC(ScriptManager sm) {
        // Ludibrium : Sky Terrace<5> (220011000)
        //   in00 (613, 136)
        sm.playPortalSE();
        if (sm.hasQuestStarted(22583)) {
            sm.warpInstance(List.of(
                    922030010,
                    922030011
            ), "out00", 220011000, 60 * 10);
            return;
        }
        if (sm.hasQuestStarted(22584)) {
            sm.warpInstance(List.of(
                    922030020,
                    922030021,
                    922030022
            ), "out00", 220011000, 60 * 10);
            return;
        }
        sm.warp(220011001, "out00");
    }

    @Script("enterSDI")
    public static void enterSDI(ScriptManager sm) {
        // Hidden Street : Frog House  (922030000)
        //   tel00 (-205, 32)
        if (sm.hasQuestStarted(22588)) {
            sm.playPortalSE();
            sm.warpInstance(914100020, "out00", 922030000, 60 * 10);
            sm.spawnReactor(1409000, -243, 6, false, -1, false);
        }
    }

    @Script("outSDI")
    public static void outSDI(ScriptManager sm) {
        // Slumbering Dragon Island  : Cave of Silence (914100022)
        //   out00 (-548, 143)
        sm.playPortalSE();
        sm.setQRValue(QuestRecordType.EvanExitCave, "1");
        sm.warp(914100010, "in00");
    }

    @Script("outAfrienMemory")
    public static void outAfrienMemory(ScriptManager sm) {
        // Afrien's Memory  : Behind the Stronghold  (900030000)
        //   out00 (857, 4)
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#bAfrien is in the other direction.");
    }


    // REACTORS --------------------------------------------------------------------------------------------------------

    @Script("farmItem0")
    public static void farmItem0(ScriptManager sm) {
        // farmItem0 (1002008)
        //   Farm Street : Farm Center (100030300)
        sm.dropRewards(List.of(
                Reward.item(4032452, 1, 1, 1.0, 22502) // Bundle of Hay
        ));
    }

    @Script("SDIScript0")
    public static void SDIScript0(ScriptManager sm) {
        // SDIScript0 (1409000)
        //   Slumbering Dragon Island  : Cave of Silence (914100022)
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(The cave begins to shake violently.)");
        sm.setQRValue(QuestRecordType.EvanIceWall, "1");
        sm.warp(914100022, "out00");
    }


    // QUESTS - JOB ADVANCEMENT ----------------------------------------------------------------------------------------

    @Script("q22100s")
    public static void q22100s(ScriptManager sm) {
        // Dragon Master 1st Job Advancement (22100 - start)
        sm.forceCompleteQuest(22100);
        sm.setJob(Job.EVAN_1);
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
        sm.sayImage(List.of("UI/tutorial/evan/14/0"));
    }

    @Script("q22101s")
    public static void q22101s(ScriptManager sm) {
        // Dragon Master 2nd Job Advancement (22101 - start)
        sm.forceCompleteQuest(22101);
        sm.setJob(Job.EVAN_2);
    }

    @Script("q22102s")
    public static void q22102s(ScriptManager sm) {
        // Dragon Master 3rd Job Advancement (22102 - start)
        sm.forceCompleteQuest(22102);
        sm.setJob(Job.EVAN_3);
        //sm.addSkill(22111001, 0, 5); // Magic Guard GMS-like, NX Mastery Books are fucking memes
        sm.addSkill(22111001, 0, 20); // Magic Guard
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
    }

    @Script("q22103s")
    public static void q22103s(ScriptManager sm) {
        // Dragon Master 4th Job Advancement (22103 - start)
        sm.forceCompleteQuest(22103);
        sm.setJob(Job.EVAN_4);
    }

    @Script("q22104s")
    public static void q22104s(ScriptManager sm) {
        // Dragon Master 5th Job Advancement (22104 - start)
        sm.forceCompleteQuest(22104);
        sm.setJob(Job.EVAN_5);
    }

    @Script("q22105s")
    public static void q22105s(ScriptManager sm) {
        // Dragon Master 6th Job Advancement (22105 - start)
        sm.forceCompleteQuest(22105);
        sm.setJob(Job.EVAN_6);
        sm.addSkill(22140000, 0, 15); // Critical Magic
        sm.addSkill(22141002, 0, 20); // Magic Booster
        //sm.addSkill(22140000, 0, 5); // Critical Magic GMS-like, NX Mastery Books are fucking memes
        //sm.addSkill(22140002, 0, 5); // Magic Booster GMS-like, NX Mastery Books are fucking memes
    }

    @Script("q22106s")
    public static void q22106s(ScriptManager sm) {
        // Dragon Master 7th Job Advancement (22106 - start)
        sm.forceCompleteQuest(22106);
        sm.setJob(Job.EVAN_7);
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
    }

    @Script("q22107s")
    public static void q22107s(ScriptManager sm) {
        // Dragon Master 8th Job Advancement (22107 - start)
        sm.forceCompleteQuest(22107);
        sm.setJob(Job.EVAN_8);
    }

    @Script("q22108s")
    public static void q22108s(ScriptManager sm) {
        // Dragon Master 9th Job Advancement (22108 - start)
        sm.forceCompleteQuest(22108);
        sm.setJob(Job.EVAN_9);
        sm.addSkill(22170001, 0, 30); // Magic Mastery
        sm.addSkill(22171003, 0, 30); // Flame Wheel
        sm.addSkill(22171000, 0, 30); // Maple Warrior
        sm.addSkill(22171002, 0, 30); // Illusion
        //sm.addSkill(22171000, 0, 10); // Maple Warrior
        //sm.addSkill(22171002, 0, 10); // Illusion
        sm.addInventorySlots(InventoryType.EQUIP, 4);
        sm.addInventorySlots(InventoryType.ETC, 4);
    }

    @Script("q22109s")
    public static void q22109s(ScriptManager sm) {
        // Dragon Master 10th Job Advancement (22109 - start)
        sm.forceCompleteQuest(22109);
        sm.setJob(Job.EVAN_10);
        sm.addSkill(22181000, 0, 30); // Blessing of the Onyx
        sm.addSkill(22181001, 0, 30); // Blaze
        //sm.addSkill(22181000, 0, 10); // Blessing of the Onyx
        //sm.addSkill(22181001, 0, 10); // Blaze
        sm.addSkill(22181002, 0, 30); // Dark Fog
        sm.addSkill(22181003, 0, 20); // Soul Stone
    }

    @Script("q2344s")
    public static void q2344s(ScriptManager sm) {
        // Mushking Empire in Danger (2344 - start)
        if (!sm.askAccept("Evan, your progress is astonishing! You look like you're ready for this now. I have something I'd like to ask you for help. Are you willing to listen?")) {
            sm.sayNext("Really? It's an urgent matter, so if you have some time, please see me.");
            return;
        }
        sm.sayNext("I am not aware of the exact details, but it's obvious something terrible has taken place, so I think it'll be better if you go there and assess the damage yourself. An adventurer like you seems more than capable of saving #bMushroom Kingdom#k. I have just written you a #brecommendation letter#k, so I suggest you head over to #bMushroom Kingdom#k immediately and look for the #b#p1300005##k.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v4032375# #t4032375#");
        if (!sm.askYesNo("By the way, do you know where #bMushroom Kingdom#k is located? It'll be okay if you can find your way there, but if you don't mind, I can take you straight to the entrance.")) {
            if (!sm.addItem(4032375, 1)) { // Mike still gives instructor letter lul nxxxn just lazy
                sm.sayNext("Please check if your inventory is full or not.");
                return;
            }
            sm.forceStartQuest(2344);
            sm.sayNext("Okay. In that case, I'll just give you directions to the #bMushroom Kingdom#k.");
            sm.sayBoth("#bHead to #m100000000#, go to the right and enter Singing Mushroom Forest. Continue through the forest and at end you will find the entrance to <Themed Dungeon : Mushroom Castle>.");
            sm.sayBoth("Please hurry! There's not much time!");
            return;
        }
        if (!sm.addItem(4032375, 1)) { // Mike still gives instructor letter lul nxxxn just lazy
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(2344);
        MushroomCastle.enterThemeDungeon(sm);
    }

    @Script("q2344e")
    public static void q2344e(ScriptManager sm) {
        // Mushking Empire in Danger (2344 - end)
        sm.sayNext("Huh? #b#t4032375##k? What's this? You're the one sent here to save our #bMushroom Kingdom#k?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bY... Yesss?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Hmm, I see. Well, if a job instructor recommended you, I will put my trust in you as well. I apologize for my late introduction. I am the #b#p1300005##k in charge of the royal family's security. As you can see, I am currently in charge of security of this temporary basecamp and the tower key is missing. We're not in the best of situations, but nevertheless, let me welcome you to the #bMushroom Kingdom#k.");
        sm.removeItem(4032375);
        sm.forceCompleteQuest(2344);
    }


    // QUESTS - EVAN MOUNT ---------------------------------------------------------------------------------------------

    @Script("q22401s")
    public static void q22401s(ScriptManager sm) {
        // Is Dragon Mounting Possible? (22401 - start)
        sm.sayNext("Master, what is it? Huh? Mounting? Isn't that like riding around on pigs, birds, or wolves? What about it?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell, I was wondering if you think it's possible for me to ride an Onyx Dragon?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Ride an Onyx Dragon... HOLD ON JUST A MINUTE. What are you saying? You want to... Ride me? But I'm your partner, not some little pet! How could you, master?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bDon't be silly! That's why I want to know if I can ride you. BECAUSE you're my partner...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Oh, I get it... Wait, huh? Well, I suppose it would make things more convenient, but if I ever get tired, I get to ride you, okay?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat?! Are you trying to kill your one and only master?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Fine, fine. Nevermind. I was just joking anyways. If I tried to ride on your back, you'd turn into a flat ol' pancake! But if you want to ride me, no problem. You're not that big.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bSo it's okay for me to ride on your back then?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yeah, why not? I can fly a whole lot faster than you can walk anyway. But we can't just take off here and now. Two things must be prepared first!");
        if (!sm.askAccept("You need a #bsaddle#k and the #bMonster Riding skill#k! I don't think you'd survive long on my back without something to sit on. Think you can prepare both of those things?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i1902040# #t1902040#")) {
            sm.sayOk("Let me know if you change your mind, master!");
            return;
        }
        if (!sm.addItem(1902040, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22401);
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#bOf course. We should go talk to #p1032001# in #m101000000#.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Ok! Ok! Let's gooo!");
    }

    @Script("q22403s")
    public static void q22403s(ScriptManager sm) {
        // In Search of a Cool Saddle (22403 - start)
        sm.sayNext("What gives you the right to step into Ereve without permission?! State your name, job, and purpose! If you lie or if your purpose is not adequate, you will not be allowed to enter.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bThis is a restricted area? But I've seen so many people enter and leave freely...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("This island may only be accessed by the Cygnus Knights, protectors of Empress Cygnus. Since you did not know this, I will let it slide. Now leave. Immediately.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWa- Wait! Can you just answer one question?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Oh? You do have a purpose here then? Then state your name, job, and purpose.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bErm, uh... Okay. Evan. Dragon Master. Searching for a saddle. Look, all I need is a saddle, and I heard somebody can find great ones here. Just let me get a saddle and I'll be on my way...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Dragon Master...? That can't be...");
        sm.sayBoth("Ahem. Are you like a common street magician that can pull balloon dragons out of a hat? Or a Magician that can summon Bahamut?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bUhh, what? Err... No.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Hm. You're a peculiar one. Dragon Master... No, I've never heard of it. I must make a note to look into that. Yes.");
        sm.sayBoth("Anyway... You asked about a saddle?");
        if (!sm.askYesNo("The Knights' saddles aren't made here. We just don't have the resources for that. We outsource our saddle production. I can assume you'd like to find out where, am I right?")) {
            sm.sayOk("I suggest you leave. Immediately.");
            return;
        }
        sm.forceStartQuest(22403);
        sm.sayNext("All our saddles are made by #b#p2060005##k who oversees the #b#m230000003##k at the #b#m230000000##k.\r\n\r\n#bThey are very high quality saddles, but they cost so much you'll feel as though your eyeballs are popping out of your head. So prepare yourself.");
        sm.sayBoth("Is that all you need then? Then please see to it that you leave the island immediately. You seem nice, but rules are rules. We don't permit outsiders to linger.");
    }

    @Script("q22404s")
    public static void q22404s(ScriptManager sm) {
        // Making a Saddle (22404 - start)
        sm.sayNext("Welcome to the #b#m230000000##k! Are you an explorer? What? You want a saddle so you can ride the animal standing next to you?");
        sm.sayBoth("Hmm... That is a really strange-looking lizard. It almost looks like a dragon! Of course, I doubt anyone would ride a dragon! ... That's a REALLY peculiar looking lizard there! Sorry for repeating myself but I'm mesmerized by him for some reason!");
        sm.sayBoth("Anyway, you're looking to mount and ride this lizard, right? I'm an animal expert, I've measured more creatures than you can imagine so I know my stuff! Though, I can't really tell the exact size of this specific lizard. May I take some measurements?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Kenta begins to meticulously measure #p1013000#'s torso, as well as his wings, head, and tail. And... seemingly his toenails and mouth as well?)");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askYesNo("Hmm, the saddle size for this creature is very different than any I've seen before! I think you'll have to place an extra special custom order if you want to ride this one! What do you say?")) {
            sm.sayOk("Come see me again if you change your mind.");
            return;
        }
        sm.sayNext("I'll make you a saddle if you bring me #r50#k #b#t4000592##k and #r1#k #b#t4032474##k. And, for the service fee... Well, since this is the saddle will be a learning experience for me, I'll give you a special discount of only #b10,000,000 mesos#k.");
        if (!sm.askYesNo("I'll make you a saddle if you bring me the materials I request alongside the service fee. The materials can only be found in a special place, though. Would you like me to send you there now?")) {
            sm.sayOk("Come see me again if you change your mind.");
            return;
        }
        sm.forceStartQuest(22404);
        sm.warp(923030000, "out00");
    }

    @Script("q22406s")
    public static void q22406s(ScriptManager sm) {
        // Uncomfortable Saddle (22406 - start)
        sm.sayNext("Master, this saddle is getting pretty uncomfortable. It's too small!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bIt's getting pretty uncomfortable for me too. There's rips, tears, and holes everywhere!");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Maybe we should go talk to #b#p2060005##k over at the #b#m230000000##k again?#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i1902041# #t1902041#")) {
            sm.sayOk("Please? Can we go get a new saddle?");
        }
        if (!sm.addItem(1902041, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22406);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#bI agree. Let's go to #m230000000#.");
    }

    @Script("q22411s")
    public static void q22411s(ScriptManager sm) {
        // Uncomfortable Saddle II (22411 - start)
        sm.sayNext("Master, does seeing me all grown up remind you of anything?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bAre you talking about your saddle?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("This ol' saddle is so small. I can't even fly in this thing... never mind trying to fly with you on my back too. I'm telling you, we need to get a new saddle again.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYeah yeah, I know.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("What is it? Is something wrong?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI'm just terrified at the just the thought of how much this will cost me.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("I know, Master, but we can't possibly continue using this thing. We should go talk to #b#p2060005##k over at the #b#m230000000##k again?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#i1902042# #t1902042#")) {
            sm.sayOk("Please? Can we go get a new saddle?");
        }
        if (!sm.addItem(1902042, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22411);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#bHmm, you're right. We don't really have a choice, it'll be pricey but I can't NOT get you one. Let's go get another saddle for you.");
    }


    // QUESTS - STORY --------------------------------------------------------------------------------------------------

    @Script("q23908s")
    public static void q23908s(ScriptManager sm) {
        // Mir's Reaction (23908 - start)
        sm.sayNext("Master! Doesn't the relationship between the town of #m310000000# and the group we joined strike you as... Weird? We seem to be getting more and more involved with the Black Wings. Are you sure that's a good thing?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYeah, the more we help the Black Wings, the more I get the feeling that something is... Off. The people in #m310000000# really dislike the Black Wings. It really makes me wonder...");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Me, too. But all we can do for now is to try and find out more about the missions given to us by the Black Wings, right?")) {
            sm.sayOk("I'm just not sure...");
            return;
        }
        sm.forceCompleteQuest(23908);
        sm.sayOk("Ahhh! I'm so confused and flustered. Are the Black Wings good guys or bad guys?!");
    }

    @Script("q22500s")
    public static void q22500s(ScriptManager sm) {
        // Baby Dragon Awakens (22500 - start)
        sm.sayNext("I'm finally here! *inhales* Ah, this must be air I'm breathing. And that, that must be the sun! And that, a tree! And that, a plant! And that, a flower! Woohahahaha! This is incredible! This is much better than I imagined the world to be while I was trapped inside the egg. And you... Are you my master? Hm, I pictured you differently.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhoooooa, it talks!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("My master is strange. I guess I can't do anything about it now, since the pact has been made. *sigh* Well, good to meet you. We'll be seeing a lot of each other.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bEh? What do you mean? We'll be seeing a lot of each other? What pact?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("What do you mean what do I mean?! You woke me from the Egg. You're my master! So of course it's your responsibility to take care of me and train me and help me become a strong Dragon. Obviously!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhaaat? A Dragon? You're a Dragon?! I don't get it... Why am I your master? What are you talking about?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("What are YOU talking about? Your spirit made a pact with my spirit! We're pretty much the same person now. Do I really have to explain? As a result, you've become my master. We're bound by the pact. You can't change your mind... The pact cannot be broken.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWait, wait, wait. Let me get this straight. You're saying I have no choice but to help you?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yuuup! Heeeey...! What's with the face? You...don't want to be my master?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bNo... It's not that... I just don't know if I'm ready for a pet.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("A p-p-pet?! Did you just call me a pet?! How dare... Why, I'm a Dragon! The strongest being in the world!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b...(You stare at him skeptically. He looks like a lizard. A puny little one, at that.)");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Why are you looking at me like that?! Just watch! See what I can do with my power. Ready?")) {
            sm.sayNext("You don't believe me? Grrrrr, you're making me mad!");
            return;
        }
        sm.forceStartQuest(22500);
        sm.sayNext("Command me to slay the #r#o1210100##ks! Do it now! I'll show you how fast a Dragon can defeat the #o1210100#s! Goooo, charge!");
        sm.sayBoth("Wait a minute! Did you distribute your AP? I'm heavily affected by my master's #bINT and LUK#k! If you really want to see what I can do, distribute your AP and #bequip your Magician equipment#k before you use the skill!");
        sm.sayImage(List.of("UI/tutorial/evan/11/0"));
    }

    @Script("q22501s")
    public static void q22501s(ScriptManager sm) {
        // Hungry Baby Dragon (22501 - start)
        sm.sayNext("Yo, master. Now that I've shown you what I can do, it's your turn. Prove to me...that you can find food! I'm starving. You can use my power now, so you have to take care of me.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bEh, I still don't get what's going on, but I can't let a poor little critter like you starve, right? Food, you say? What do you want to eat?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Uhm, hello?! I was just born a few minutes ago! How would I know what I eat? All I know is that I'm a Dragon... I'm YOUR Dragon. And you're my master. You have to treat me well!");
        if (!sm.askAccept("I guess we're supposed to learn together. But I'm hungry. Master, I want food. Remember, I'm a baby! I'll start crying soon!")) {
            sm.sayOk("*gasp* How can you refuse to feed your Dragon? This is child abuse!");
            return;
        }
        sm.forceStartQuest(22501);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(#p1013000# is extremely hungry. I have to feed him! Maybe Dad can give me advice on what dragons eat.)");
    }

    @Script("q22502s")
    public static void q22502s(ScriptManager sm) {
        // A Bite of Hay (22502 - start)
        if (!sm.askAccept("Wouldn't a lizard enjoy a #b#t4032452##k, like a cow? There are a lot of #bHaystacks#k nearby, so try feeding it that.")) {
            sm.sayOk("Hm, you never know unless you try. That lizard is big enough to be on Maple's Believe It Or Not. It might eat hay.");
            return;
        }
        sm.forceStartQuest(22502);
        sm.sayImage(List.of("UI/tutorial/evan/12/0"));
    }

    @Script("q22503s")
    public static void q22503s(ScriptManager sm) {
        // A Bite of Pork (22503 - start)
        sm.sayNext("No, no, no. This isn't what I need. I need something more nutritious, master!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bHm... So you're not a herbivore. You might be a carnivore. You're a Dragon, after all. How does some #t4032453# sound?");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("What's a...#t4032453#? Never heard of it, but if it's yummy, I accept! Just feed me something tasty. Anything but plants!")) {
            sm.sayOk("How can you starve me like this. I'm just a baby. This is wrong!");
            return;
        }
        sm.forceStartQuest(22503);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(Try giving #p1013000# some #t4032453#. You have to hunt a few #o1210100#s at the farm. Ten should be plenty...)");
    }

    @Script("q22504s")
    public static void q22504s(ScriptManager sm) {
        // Tasty Milk 1 (22504 - start)
        sm.sayNext("Ugh. This isn't going to work. I need something else. No plants. No meat. What, you have no idea? But you're the master, and you're older than me, too. You must know what'd be good for me!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bBut I don't. It's not like age has anything to do with this...");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Since you're older, you must be more experienced in the world, too. Makes sense that you'd know more than me. Oh, fine. I'll ask someone who's even older than you, master!")) {
            sm.sayOk("No use trying to find an answer to this on my own. I'd better look for #bsomeone older and wiser than master#k!");
            return;
        }
        sm.forceStartQuest(22504);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(I've already asked Dad once, but I really don't have any better ideas. Time to ask him again!)");
    }

    @Script("q22507s")
    public static void q22507s(ScriptManager sm) {
        // What is a Dragon Master? (22507 - start)
        sm.sayNext("I knew it! I knew we were connected, master! When you get stronger, I get stronger, too. And when I get stronger, you can use my strength! That's our pact. I knew I picked a good master!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI see. How did we end up in this pact anyway?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I don't know. I was just an egg. I can't really remember...though I faintly recall you, master, walking toward me in a foggy forest. I remember your surprise upon seeing me. And I was calling out to you in return.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Wait! That sounds just like that one dream you had... Did the two of you meet in a dream? Is it possible that the giant Dragon you saw in that dream was...#p1013000#?)");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Master, you and I are one in spirit. I knew it the moment I saw you. That's why I wanted to make the pact with you. No one else. You had to pay the price I set, of course.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI paid a price?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Don't you remember? When you recognized me and touched me? That was the one condition I set. The moment you touched my egg, you and I became one in spirit.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bOne in...spirit?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yes! The Spirit Pact! You and I have seperate bodies, but we share one spirit. That's why you get stronger when I get stronger, and vice versa! Awesome, right? At least, I think so.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI have no idea what you're talking about, but it sounds like a pretty big deal.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Of course it's a big deal, silly master! You never have to worry about monsters again. You have me to protect you now! Go ahead and test me. In fact, let's go right now!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bBut it's peaceful here. There are no dangerous monsters around.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("WHAT?! That's no fun! Don't you like adventuring, master? Fighting monsters on behalf of your people, defeating evil, rescuing the innocent, and all that? You're not into that kind of thing?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bIt's not part of my five year plan. I'm just kidding, but seriously, I'm a farmer's kid...");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Bah, well let me tell you this. It's impossible for a Dragon Master to live a peaceful life. I'll have plenty of chances to prove my skills. Trust me, our life will be one big adventure. Promise me that you'll stick with me, okay?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 810 exp")) {
            sm.sayOk("Uh, you're kidding me, right? Tell me you're kidding...?");
            return;
        }
        sm.forceStartQuest(22507);
        sm.addExp(810);
        sm.sayNext("Hehehe, alrighty then, master. Let's get to it!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(I'm still a bit confused, but I guess I'm bound to Mir, the Dragon. Perhaps we'll go on an adventure together, like he said.)");
        sm.sayBoth("#b(I still have an errand to run, though. I should probably go talk to Dad now.)");
    }

    @Script("q22510s")
    public static void q22510s(ScriptManager sm) {
        // Letter Delivery (22510 - start)
        sm.sayNext("Hm? What is it, Evan? Are you here to help your old dad? Huh? What do you mean, you defeated the #o1210111#s?! Geez, are you hurt?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI'm fine, Dad! It was easy."); // In GMS, this was displayed as menu option with the text above for some reason. Changed to fit with the rest of the scripts lol
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("What a relief. You need to be careful, though. It could've been dangerous... By the way, I've got something for you to do. Can you run an errand for me?")) {
            sm.sayOk("Hm, #p1013101# would have done it at the drop of a hat.");
            return;
        }
        if (!sm.addItem(4032455, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22510);
        sm.sayNext("Could you tell #b#p1012003##k in #b#m100000000##k that I'm not going to be able to deliver the Pork on time? The #b#o1210111##ks have caused so many problems.");
        sm.sayBoth("I've written everything down in this letter, so all you have to do is take this to him. I'd go myself, but I have to deal with problems here.");
        sm.sayImage(List.of("UI/tutorial/evan/13/0"));
    }

    @Script("q22512s")
    public static void q22512s(ScriptManager sm) {
        // The Dragon Master's Calling (22512 - start)
        sm.sayNext("Master! I'm touched! You are such a good person. Let's help all the people who need our help, okay? That's our calling!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat the...? What calling?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Well, master, you and I are so powerful together, you know? I have a feeling we were given these powers to help mankind! It's your calling as a Dragon Master, I think.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bMy calling as... a Dragon Master?");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Yup! That's what I'm talking about! I just KNOW there are people out there in desperate need of the Dragon Master's help.")) {
            sm.sayOk("But... it's your calling, master...");
            return;
        }
        sm.forceStartQuest(22512);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(You agree to help others using your powers as a Dragon Master. Sounds grandiose, even to you. But you'd better get started! Check around Henesys to see if anyone needs help.)");
    }

    @Script("q22514s")
    public static void q22514s(ScriptManager sm) {
        // Let's Train (22514 - start)
        sm.sayNext("Let's train and get stronger, then! Let's train until we can beat a Blue Mushroom with ease, then go back and help that lady! All you have to do is train! Train! Traaaaaaaaaaiiiiiiiiinnnnn!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Geez, this dragon isn't going to let up!)"); // not GMS-like, gamers don't read text. No videos have this line complete.
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Let's train, master! Let's go!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bOkay, okay! Fine, geez. I'll go talk to #p1012003#.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Really? We're going to go train?")) {
            sm.sayOk("Don't tease me like that! You're so mean!");
            return;
        }
        sm.forceStartQuest(22514);
        sm.sayNext("Yippee! That's why I love you!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(I've finally calmed him down a bit. I should go talk to #p1012003# about the training center.)");
    }

    @Script("q22518s")
    public static void q22518s(ScriptManager sm) {
        // Power B. Fore's Never Ending Paranoia (22518 - start)
        sm.sayNext("I just don't believe it. That Stan is... the same miser who wouldn't speak to me for two years because I ate one of his candies. The same cheapskate that loaned me 3,000 mesos then calculated interest for each SSECOND I was late... I just don't believe it!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Wow, I had no idea that #p1012003# was such a grinch...)");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I don't believe Stan would send such a strong adventurer my way to help my training center. It makes no sense. Stan has never helped me. But... fine! I'll test you once more but this is the last time. I KNOW you and Stan are up to SOMETHING.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI swear, Stan and I aren't trying to pull a fast one on you!");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("This test is simple. You have to defeat #r#k in the training center, that's all. It's not going to be easy finding them, since they hang out amongst the Orange Mushrooms. Haha... Do you still want to enter?")) {
            sm.sayOk("I KNEW it!");
            return;
        }
        sm.forceStartQuest(22518);
        sm.warpInstance(910060100, "start", 100020000, 60 * 10);
    }

    @Script("q22536s")
    public static void q22536s(ScriptManager sm) {
        // Kerning City Investigation: Nella (22536 - start)
        sm.sayNext("Hmm? I don't recall seeing you around here before. What brings you to #b#m103000000##k? Are you here to become a Thief?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bHave you caught a whiff of anyone smelling of herbs?");
        sm.setPlayerAsSpeaker(false);
        sm.sayNext("Smelling of herbs? I'm not sure... I thought everybody used potions these days?! Why are you asking about herbs? Are you looking to buy some?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell, you see...\r\n\r\n(You explain what happened to #p1061005#.)");
        sm.setPlayerAsSpeaker(false);
        sm.sayNext("Huh, an herb thief, huh? I see... Wait! What? Wait, wait, wait just a minute!! Are you suggesting that the thief is from #b#m103000000##k?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bThis IS a thief town, afterall, isn't it?");
        sm.setPlayerAsSpeaker(false);
        sm.sayNext("Yes, but we're not burglars! This is a THIEF town, NOT a burglar town. UGH! It drives me absolutely crazy when- Geez! The things you're implying about us Thieves here in #b#m103000000##k! Sure, it's true that we can be a bit sneaky and petty, a little under-handed and cunning, yeah... But we DON'T threaten the livelihoods of others just to get what we want!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bReally? Wow...");
        sm.setPlayerAsSpeaker(false);
        sm.sayNext("Yes! Really! I know people get the wrong idea about us, but as someone who was born and raised in #b#m103000000##k, I'm DEEPLY offended! I swear on my MOTHER that the burglar you're looking for is not from here!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bOh? Well, where is the burglar from, you think?");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("I haven't got a clue! I'm still upset about your implications and accusations, but I WILL find the thief who stole #b#p1061005##k's herbs myself! Then I will take #b#m103000000##k's honor back! Did you get that?! I will find it by MYSELF!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0#\r\n8000 exp")) {
            sm.sayOk("You come here implying such things and have the audacity to just walk away?! HMPH!");
            return;
        }
        sm.forceCompleteQuest(22536);
        sm.addExp(8000);
        sm.sayNext("I'll investigate the burglar you're looking for, so keep yourself available as much as possible! I'll reach out when I get to the bottom of this!");
        sm.sayBoth("Just keep training or whatever it is you do until I contact you, okay?");
    }

    @Script("q22541s")
    public static void q22541s(ScriptManager sm) {
        // Where's the Book? 1 (22541 - start)
        sm.sayNext("Do you come seeking knowledge? Remember that a constant thirst for knowledge never leads to any good. Continue growing your willpower and you will find boundless power within yourself. Excuse me, are you here for a book?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI'm looking for #t4161050#.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Ahh, yes, the infamous book published in #b#m240000000##k. I believe the second volume is- Oh my... It appears someone's already borrowed this book.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat? Somebody already borrowed it out? Who?!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Do you know #b#p1052106##k who resides in #b#m103000000##k? He's a young boy who wishes to soar the skies. He has the book, it's been quite some time since he borrowed it out, though.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhen is it due back?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Well, our #b#m101000003##k doesn't have a limit on how long a book can be borrowed. You can, however, go to #b#m103000000##k and ask #b#p1052106##k for #b#t4161050##k.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b#m103000000#? That's quite the walk...");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("I can warp you to #b#m103000000##k, if you'd like?")) {
            sm.forceStartQuest(22541);
            sm.sayOk("No matter. You may take the #b#p1012003##k and pay a small fee or you can walk there. Check your map for directions. Good luck, young Evan.");
            return;
        }
        sm.forceStartQuest(22541);
        sm.warp(103000000);
    }

    @Script("q22546e")
    public static void q22546e(ScriptManager sm) {
        // Dragon Types and Characteristics (Vol. II) (22546 - end)
        sm.sayNext("Ahh... yes, you've returned! I heard from #b#p1032104##k that you traveled all around #bVictoria Island#k to retrieve that book. I hope it contained the information you needed. What did you find out?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell, I learned about Onyx Dragons, they're the most fascinating to me.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askYesNo("Onyx Dragons? Onyx Dragons have been extinct for quite some time, as you probably learned from that book. However, I'm happy to continue helping you with your research, if you'd like.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0#\r\n12000 exp")) {
            sm.sayOk("Hmm... No matter. If you ever do need help, please come back!");
            return;
        }
        sm.forceCompleteQuest(22546);
        sm.removeItem(4161050);
        sm.addExp(12000);
        sm.sayNext("There are lots of books about dragons here in our #b#m101000003##k but there aren't any other books regarding Onyx Dragons in particular. If a new book about the dragons ever crosses our library, I'll let you know immediately.");
        sm.sayBoth("Oh, by the way, I have a friend in #b#m240000000##k named #b#p2081000##k of the Halflingers. I'll ask and see if he knows anything about Onyx Dragons.");
        sm.sayBoth("I hear Onyx Dragons are covered in dark, clear scales and have golden horns. Your little lizard has golden horns, but doesn't have the dark, clear scales.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(He might try to take #p1013000# for research, or worse, if he finds out he's a true Onyx Dragon.)\r\n\r\nHe isn't a dragon, he's just a lizard!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Why of course... Did I imply otherwise? It's nothing more than a lizard.");
    }

    @Script("q22560s")
    public static void q22560s(ScriptManager sm) {
        // Condition for Joining the Secret Organization 1 (22560 - start)
        sm.sayNext("Great to see you again, lifesaver! My master has been very busy lately, letting his wounds heal and finding a new base for us, which is why there has been no communication from him lately. He just made contact recently, though!");
        sm.sayBoth("I told my master about you and he agreed that you could join the secret organization! There is one condition, however. I think it must be the entrance exam!");
        if (!sm.askAccept("A strong fancy hero like you should be able to pass the test, I think. Should I tell you about the test?")) {
            sm.sayOk("Maybe I was wrong about you!");
            return;
        }
        sm.forceStartQuest(22560);
        sm.sayNext("The test is simple! Go to #b#m101000000##k's #bNorth Forest#k and get rid of #r150#k #b#o3230100#s#k. Master says the #r#o3230100##ks are getting in the way of the new base's construction!");
        sm.sayBoth("I don't know why he doesn't just build the base somewhere else. He apparently tried building it in some garden, but had to halt the project because of some monsters that kept attacking. I guess that's why he's being more cautious this time.");
    }

    @Script("q22564e")
    public static void q22564e(ScriptManager sm) {
        // Knowledge about Dragons 3 (22564 - end)
        sm.sayNext("Hmm. You look like a human, so what brings you to the Halflingers' village of #m240000000#? Ack! That... That dragon next to you is... AN ONYX DRAGON?! That would make you the human #b#p1032001##k was talking about? The human with the Onyx Dragon?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(#p2081000# must indeed be a Halflinger because he instantly recognized #p1013000# as an Onyx Dragon. Since he's a Halflinger, it is unlikely he would do any harm to #p1013000#.)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Whoa! It's amazing that there are still Onyx Dragons in existence! It looks so young... It must've just hatched, hmm? I cannot believe my eyes!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(It seems #p1032001# must have been pretending he didn't recognize #p1013000# was an Onyx Dragon. He knew all along...)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Come to think of it, Onyx Dragons are one of those special dragons that can only be whole when they make a Spirit Pact! Without that pact, an Onyx Dragon is nothing. Your dragon looks quite strong... Wait... Are you his...?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYes. I'm his Dragon Master... Mir, say hello!");
        sm.setPlayerAsSpeaker(false);
        sm.setSpeakerId(1013000);
        sm.setSpeakerOnRight(true);
        sm.sayBoth("I- I- I don't have to speak to anyone but my master. *sniff*");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bSorry... he's a little shy.");
        sm.setPlayerAsSpeaker(false);
        sm.setSpeakerId(2081000);
        sm.setSpeakerOnRight(false);
        sm.sayBoth("No worries! I've heard that Onyx Dragons can be a bit skittish. I still can't believe I'm looking at a bona fide Onyx Dragon with my own two eyes!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bIf they're so skittish and cautious, how did they go extinct...?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("That's... Well, that's an all but forgotten story. Hundreds of years ago, there was a powerful, dark force named the #bBlack Mage#k in #bMaple World#k. It was he who destroyed all the Onyx Dragons...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bBut why did he destroy all of them?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I can't say. All I know is that the Onyx Dragons fought against him and that they were obliterated as a result. Gone. Extinct. I was still but a young Halflinger then, so I don't know all the details.");
        sm.sayBoth("But it seems they weren't completely obliterated after all. I wonder how difficult life must be for this little creature. We have great facilities for raising dragons here in #b#m240000000##k. Interested in settling down by any chance?");
        sm.setSpeakerId(1013000);
        sm.setSpeakerOnRight(true);
        sm.sayBoth("No. I go where my master goes.");
        sm.setSpeakerId(2081000);
        sm.setSpeakerOnRight(false);
        sm.sayBoth("Ah, yes. Of course. I've also heard that Onyx Dragons treasure their relationships with their masters more than even their own instincts. I see it's true.");
        sm.sayBoth("Onyx Dragons are supposedly spiritually connected to their masters. The master's power increases the power of the Onyx Dragon, and the master, in turn, can harness that strength.");
        sm.sayBoth("But not just anyone can become the master of an Onyx Dragon. Onyx Dragons have a keen eye for those with strong spirits. They are extremely picky, and as I said before... Cautious. You must have an extremely powerful and wholesome spirit, my friend!");
        sm.sayBoth("I wish you'd consider leaving him here in #b#m240000000##k but I know you won't... And I know he wouldn't stay. I wonder, though, are there other Onyx Dragons still out there? With this marvelous revelation... We can't give up! With the help of #b#p1032001##k, we will find other Onyx Dragons, I'm sure of it!");
        sm.sayBoth("I'll send word if I discover anything.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 17000 exp\r\n#fUI/UIWindow2.img/QuestIcon/10/0# 1 sp");
        sm.forceCompleteQuest(22564);
        sm.addExp(17000);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2212), 2); // Have to make sure the SP is being given to the correct job
        sm.getUser().write(WvsContext.statChanged(Stat.SP, sm.getUser().getCharacterStat().getSp(), false));
    }

    @Script("q22565s")
    public static void q22565s(ScriptManager sm) {
        // Never Give Up! (22565 - start)
        sm.sayNext("Master, do you really think I'm the only survivor of my race? Are the others really all gone? Why were they killed? And why was I spared? I just can't figure it out, and it makes me so sad...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b#p1013000#...");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("But, I refuse to give up! I beat the odds, so there must be others. I will find them! Master, you'll help me, right?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 20000 exp\r\n#fUI/UIWindow2.img/QuestIcon/10/0# 2 sp")) {
            sm.sayOk("What?! Why? You're joking, right?!");
            return;
        }
        sm.forceCompleteQuest(22565);
        sm.addExp(20000);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2212), 2); // Have to make sure the SP is being given to the correct job
        sm.getUser().write(WvsContext.statChanged(Stat.SP, sm.getUser().getCharacterStat().getSp(), false));
        sm.sayOk("All right, then! We'll give that #b#p1032001##k, or whatever his name is, time to find out more. In the meantime, let's train and get even stronger! Let's become heroes! Let's go help people!");
    }

    @Script("q22567e")
    public static void q22567e(ScriptManager sm) {
        // Secret Organization's First Mission (22567 - end)
        sm.setSpeakerOnRight(true);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#bAlright, just got to put these back here and replace the brick...#k\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 22500 exp\r\n#fUI/UIWindow2.img/QuestIcon/10/0# 1 sp");
        sm.forceCompleteQuest(22567);
        sm.removeItem(4032468);
        sm.addExp(22500);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2212), 1); // Have to make sure the SP is being given to the correct job
        sm.getUser().write(WvsContext.statChanged(Stat.SP, sm.getUser().getCharacterStat().getSp(), false));
        sm.sayNext("#bPhew! I thought the mission would be easy since I'm a temporary member and all, but it was tough! It's so exciting being part of this secret organization!");
        sm.setPlayerAsSpeaker(false);
        sm.setSpeakerId(1013000);
        sm.sayBoth("Pretty thrilling, right?! I wonder what this #t4032468# is for. Do you think I would grow like crazy if I drank some?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI don't know. #p2030012# says there could be side effects if consumed by an animal, so I don't think you should try...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Master! Are you calling me an animal?!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell, humans are animals too! Hahaha.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I don't know about that... Fine. I'll let that one go.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bDo you think this #t4032468# is being used to help crops grow? Like some kind of fertilizer or something?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("That makes sense. Faster-growing, bigger crops means more food for more people. Less people will go hungry. This organization is all about doing good deeds to improve people's lives, right?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYeah, I think that makes sense!");
    }

    @Script("q22575s")
    public static void q22575s(ScriptManager sm) {
        // Secret Organization's Second Mission (22575 - start)
        sm.sayNext("Hello, Mr. Evan. You look shocked. I suppose I did just abruptly talk to you seemingly from no where, huh? Don't worry, I'm not a suspicious person. I am a member of the organization you just joined.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bAre you the owner of that abandoned doll?!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Oh? Are you talking about Francis? No, I am not, but I certainly am his superior. I am the one who dispatched you on your first mission.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYou mean you left the note in the wall of #m200000000##k #bTower?#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yes, I was the one who left the note. I made good use of the #b#t4032468##k you got for me. You helped us out greatly! Thank you.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bIt's not a problem! I'm always down to help out if it's to help out others!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("You did well despite not being a full member of our organization. I think I can still trust you with this mission, however.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYou have another mission for me?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I do. This will be quite the difficult task though, but you seem capable. I need you to go to #b#m211000000##k's #bForest of the Dead#k and defeat #r#o9001027##k to retrieve #b#t4000593##ks.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bForest of the Dead? Zombies...? I've got a baaad feeling about this.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Heh, you'll be fine. It's very important you find me #r150#k #b#t4000593##ks and deliver them to #b#m211000001##k in #b#m211000000##k. There resides #b#p2022003##k. He will know what to do.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell... okay. Got it. If it's THAT important and necessary, I'll do it.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askYesNo("I like your attitude! If you're ready, I can send you directly to #b#m211000001##k in #b#m211000000##k. Once you're there head to the basement and enter the very last door!")) {
            sm.forceStartQuest(22575);
            sm.sayOk("Alright, you can get to #b#m211000000##k by heading to #b#m200000000##k and descending #b#m200000000##k Tower.");
            return;
        }
        sm.forceStartQuest(22575);
        sm.warp(211000001, "out01");
    }

    @Script("q22578s")
    public static void q22578s(ScriptManager sm) {
        // Question about the Secret Organization (22578 - start)
        sm.sayNext("Master! Master! Nicely done! Do you think your last mission was of great help to the people of #bMaple World#k?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell, I defeated most of the zombies in #m211000000#, so it must have been a good thing, right?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("The more monsters you defeat, the better, I suppose? But what about that #t4032471#? What do you think that was for?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI'm not sure... But this organization is all about doing good deeds, so it's got to be for a good purpose.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I suppose... But why do you think this organization carries out its activities in secret? How is anyone supposed to know of their good deeds if no one even knows they exist?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWell, like the saying goes, let not your left hand know what your right hand is doing!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Left hand? Right hand? What?! Are you saying you should let your left hand be a loser that doesn't know anything?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bHaha, no! I think it just means you should keep your good deeds to yourself since it's not virtuous to brag about your good deeds.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("... I don't get it. I love to let people know what I'm up to. Anyway, it just seems so secretive and calculated. It's exciting, yet... I don't know. But don't you agree master?!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 30000 exp\r\n#fUI/UIWindow2.img/QuestIcon/10/0# 2 sp")) {
            sm.sayOk("I'd love to hear your thoughts, master.");
            return;
        }
        sm.forceCompleteQuest(22578);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2213), 2); // Have to make sure the SP is being given to the correct job
        sm.getUser().write(WvsContext.statChanged(Stat.SP, sm.getUser().getCharacterStat().getSp(), false));
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#bBut I'm sure there's a reason for it. It's for a good cause, but... I'll ask about it next time. Yeah, I'll just ask what the organization is really about when I'm given my next mission.");
        sm.addExp(30000);
    }

    @Script("q22581s")
    public static void q22581s(ScriptManager sm) {
        // Before Receiving the Secret Organization's Third Mission (22581 - start)
        sm.sayNext("It's been quite some time, Mr. Evan. How are you? We are doing pretty well thanks to you help. It's time for your next mission.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bCan I ask a question first?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Sure. Ask me anything. If you have questions, I'll do my best with an answer.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI'm a temporary member of this organization, but I don't know anything about it. Tell me more about this organization.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("... Yes, right. It was only a matter of time before you became more curious about our organization. I suppose I can tell you about it, you'll have to meet up with me.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bOkay! Where should I go?");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Come to #b#m220000300##k, there you will find one of our organization's bases, I will meet you there. The house's entrance is in the shape of a #bfrog#k.\r\n\r\n#bIf you'd like, I can provide a shortcut for you?#k\r\n#r(You will be instantly warped to #k#b#m220000300##k#r.)#k")) {
            sm.forceStartQuest(22581);
            sm.sayOk("Okay, that's fine. To get to #b#m220000000##k, you can take a ferry, boat, or something similar.");
            return;
        }
        sm.forceStartQuest(22581);
        sm.warp(220000300, "scr00");
    }

    @Script("q22582s")
    public static void q22582s(ScriptManager sm) {
        // Secret Organization's Third Mission (22582 - start)
        if (!sm.askAccept("Your third mission is to collect #r100#k #b#t4000594##k from #b#o9001028##k.\r\n\r\n#bI will have to take you to where they are.#k")) {
            sm.sayOk("Come back and see me when you're ready. Don't wait too long, though...");
            return;
        }
        sm.forceStartQuest(22582);
        sm.warp(922030002, "out00");
    }

    @Script("q22582e")
    public static void q22582e(ScriptManager sm) {
        // Secret Organization's Third Mission (22582 - end)
        if (!sm.askYesNo("Heh! You completed the task? Please hand over the #b#t4000594##ks.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 50000 exp")) {
            sm.sayOk("What? Hand them over!");
            return;
        }
        sm.forceCompleteQuest(22582);
        sm.removeItem(4000594);
        sm.addExp(50000);
        sm.sayNext("Heh heh heh... This should be plenty for us to carry out our plans...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bLook, I have a question to ask...");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("I'm sorry, but I must be going. I have my hands tied taking care of all these #b#t4000594##ks you brought us. Can you come back later when I'm finished? It's going to take a little while...");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(What good is defeating some monsters in a place no one goes? I want to know so badly, but he does look pretty busy. I'll come back later...");
    }

    @Script("q22583s")
    public static void q22583s(ScriptManager sm) {
        if (!sm.askYesNo("This mission isn't complete yet, I have another task for you. Are you ready?")) {
            sm.sayOk("Please come back as soon as possible.");
            return;
        }
        sm.sayNext("Are you ready for your next task? Inside this pouch are all the Spirits you collected.");
        sm.sayBoth("I will bring you to #b#m922030011##k where you will release the Spirits.");
        sm.sayBoth("Act quickly, release the pouch once you're there.");
        if (!sm.addItem(2430032, 1)) {
            sm.sayNext("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22583);
        sm.warpInstance(List.of(
                922030010,
                922030011
        ), "out00", 220011000, 60 * 10);
    }

    @Script("q22585s")
    public static void q22585s(ScriptManager sm) {
        // Suspicions about the Secret Organization (22585 - start)
        sm.sayNext("Look, master. Don't you think the mission you just completed for the Black Wings is a little strange? Things just don't add up! I thought dropping off the Free Spirit was supposed to be a good thing?!");
        sm.sayBoth("Doesn't it seem unnecessary for them to have wrapped it in a pouch like that? And what about the fact that you could only unwrap the pouch in front of the #m922030010#? If the intention was to free it, why does it matter where you let it go?");
        sm.sayBoth("And then, did you hear the #o9300389#s screaming when you unwrapped the pouch? Remember how mad they were that we were getting in their way? Do you really think the #o9300389#s were the bad guys?");
        sm.sayBoth("And what about what #o9300390# said to us as it was disappearing? I don't know, it just bothers me. He called us thieves! I don't know... Killing monsters should make me feel better, but I feel horrible!");
        if (!sm.askAccept("That #p1013204#s or whatever his name is told us not to worry, but something tells me that this last mission wasn't for a good cause. Don't you agree, master?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 25000 exp\r\n#fUI/UIWindow2.img/QuestIcon/10/0# 1 sp")) {
            sm.sayOk("Hmm, I just don't know... I'd love to hear your thoughts, master.");
            return;
        }
        sm.forceCompleteQuest(22585);
        sm.addExp(50000);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2214), 1);
        sm.getUser().write(WvsContext.statChanged(Stat.SP, sm.getUser().getCharacterStat().getSp(), false));
        sm.sayOk("So, the Black Wings... I don't want to be suspicious of them, but I can't help it...");
    }

    @Script("q22587s")
    public static void q22587s(ScriptManager sm) {
        // Map of Turtle Island (22587 - start)
        sm.sayNext("Do you like adventure? You exude a strong energy, it's quite amazing. So, what is someone like you doing in a town like this? Are you here to see me?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bDo you have the Map of Turtle Island?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Turtle Island? Ahh, that island I saw a long time ago, during my days as a fisherman. To answer your question, yes, I have it. You can't go too far because of the pirates, but I kept the map, anyway.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bCan I have it?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("The island is surrounded by coral reef and powerful waves, not to mention the strong wind. It was given its name because it looks so much like a turtle, but not many people even know about it. Do you still want it?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYes, please!");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Well, if you still want to go, I can't stop you. I'll give you the map if you do me a favor. Defeat #r100#k #b#o9001029##k and #r100#k #b#o9001029##k of the Red-Nosed Pirates who threaten Herb Town. Think you can handle it?")) {
            sm.sayOk("I understand. You can come back and see me if you change your mind.");
            return;
        }
        sm.forceStartQuest(22587);
        if (!sm.askYesNo("If you want, I can send you to the #m925110001#?")) {
            sm.sayOk("Alright, if you have some things to do first, that's fine. Just head out of town, use your #rWorld Map#k if you get lost.");
            return;
        }
        sm.warpInstance(925110001, "out00", 251000000, 60 * 30);
    }

    @Script("q22591s")
    public static void q22591s(ScriptManager sm) {
        // The Past, Onyx Dragons, Black Mage (22591 - start)
        sm.sayNext("Hundreds of years ago in Maple World, there were many Onyx Dragons. There were just as many humans who loved the Onyx Dragons very much... We, my friend Freud and I, always hoped that humans and the Onyx Dragons could forever live in peace...");
        sm.sayBoth("As powerful as we are, Onyx Dragons are born with incomplete spirits. Humans are born with strong wills but weak bodies. Put the two together, and a Dragon Master is born. We wanted the two races to exist in a symbolic relationship, each helping each.");
        sm.sayBoth("Unfortunately, our wish was destroyed by the #rBlack Mage#k.");
        if (!sm.askAccept("Perhaps it would be best to show you. #bI will send you on a journey through my memories#k. Travel back hundreds of years, to just before the war against the Black Mage started. Go to into my memory of when Freud and I conversed about making our dream a reality...")) {
            sm.sayOk("...");
            return;
        }
        sm.forceStartQuest(22591);
        sm.warp(900030000, "sp");
    }

    @Script("q22593s")
    public static void q22593s(ScriptManager sm) {
        // Result of the First Mission (22593 - start)
        sm.sayNext("Hmm? Is there something I can do for you? You have such a determined expression.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI need to know if the plants in #m200000000# grew abnormally fast.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Huh? How did you know about that?\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 65000 exp");
        sm.forceCompleteQuest(22593);
        sm.addExp(65000);
        sm.sayNext("Yes, it was quite a dilemma for us when the #b#o4230105#s started growing like crazy#k! Thankfully, someone that was passing by did some investigating on our behalf and we were able to resolve the issue, but boy, it was a BIG deal!");
        sm.sayBoth("Why are you making that face? We've already resolved the issue so you don't need to worry about it.");
    }

    @Script("q22594s")
    public static void q22594s(ScriptManager sm) {
        // Result of the Second Mission (22594 - start)
        sm.sayNext("What is it? You don't look like you require my teachings.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI just... I need to know if it's a good thing to eliminate monsters around #m211000000#?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Well, of course! If it weren't for the zombies, #m211000000# could develop a lot further. If you have the energy, keep getting rid of those zombies!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(Perhaps this specific mission was indeed for a good cause...?)");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("But you'd better take care of the teeth after you've killed the zombies, because the teeth from the zombies around here have a dark force inside of them. If you're not careful, you may end up getting corrupted, just like #p2022003#. He wishes to be redeemed from his wrongdoings, but he just keeps becoming more and more evil.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bHas #p2022003# done something wrong?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("#p2022003# was caught with a copy of the basement key to the #m211000001# some time ago. The key was taken away, but he most likely made copies. We'll have to keep a closer lookout on the basement for the time being.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat's in the basement?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("There is an old treasure that's been kept hidden in #m211000000# for a very long time. I can't tell you anything more. It is something that must not get lost. Don't ask me anymore questions about it.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 65000 exp");
        sm.forceCompleteQuest(22594);
        sm.addExp(65000);
        sm.sayOk("No need to look so glum. It's not like YOU stole the treasure or helped steal the treasure. #rIt's true our security has become weaker lately#k but we just need to be more watchful, that's all.");
    }

    @Script("q22595s")
    public static void q22595s(ScriptManager sm) {
        // Result of the Third Mission (22595 - start)
        sm.sayNext("Hi there. I'm a #b#m220000000# Guard#k and my name is #b#p2041004##k. Is there something I can help you with?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI need to ask about #o9300390#.#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Huh? What? #b#o9300390##k?");
        sm.sayBoth("Shhhh! How do you know about the #b#o9300390##k? I'm an undercover guard watching over the secret Safe, so that's how I know about it. Uhm, erm... Okay. I'll tell you about #b#o9300390##k.\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#fUI/UIWindow2.img/QuestIcon/8/0# 65000 exp");
        sm.forceCompleteQuest(22595);
        sm.addExp(65000);
        sm.sayNext("#b#o9300390##k was vandalized by someone some time ago and broke as a result. #bNo one was watching the secret Safe at the time and so a burglar came in and stole the treasure.#k No one knows what kind of a treasure it was, but... It's a BIG deal...");
        sm.sayBoth("Uh... Why are you making such a scary face? Please remember, this issue must be kept a secret so please watch what you say!");
    }

    @Script("q22602s")
    public static void q22602s(ScriptManager sm) {
        // After Shedding 1 (22602 - start)
        sm.sayNext("Master! Look! I've grown some more!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bOh my! You really grew! Whoa, your voice is even different!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Ahem... Really? Do I sound cool?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bDefinitely! Dragons really do grow in leaps and bounds!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yep! I shed my old scales and grew new ones. I guess in human terms, it would be something like... Buying new clothes as your body grows?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYour new scales are so shiny and nice!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yuuup! They are, aren't they?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(His body's grown but he still talks the same.)");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Anyway, master, could you take a look at this? It's one the scales I shed. For some reason, this one's still shiny. All the others sort of fell apart. I feel like this scale still carries my strength in it. Do you think we could use it for something?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bHmm... maybe.");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Yippee! Humans don't have horns, scales, or claws like Dragons do, but they do have the ability to make useful things! That scale is extremely sturdy and carries my strength with it, so it will make you that much more powerful, master!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b#p1013000#, you're awesome! When did you start thinking like that?");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Ahem. It's not like I was born yesterday. I know a whole lot about humans now.");
        if (!sm.askAccept("Here you go, master. Take my scale. I know you'll be able to make something really great with it!")) {
            sm.sayOk("I'll hold onto it for now. But you should really hold on to it yourself...");
            return;
        }
        if (!sm.addItem(1142156, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22602);
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(#p1013000# gave me one of his scales! When I touched it, it transformed into the #r#t1142156#Medal#k#b.)#k"); // there's an extra space in the item name, so I'm not including a space between 1142156 and Medal lul
    }

    @Script("q22603s")
    public static void q22603s(ScriptManager sm) {
        // After Shedding 2 (22603 - start)
        sm.sayNext("Master, look. I think I'm really coming into my own strength.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bYou're right. You look quite imposing. I can feel so much of the strength of an Onyx Dragon!");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("It's the strength of an Onyx Dragon as well as the strength of its master. The Onyx Dragon can only grow when its master is growing stronger. That means your spirit has grown that much, too, master.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bI see you've matured. You even sound different, #p1013000#.");
        sm.setPlayerAsSpeaker(false);
        if (!sm.askAccept("Haha, of course. It would be embarrassing to talk like a child with this elegant body. Anyway, master, I have another shiny scale that came off when I was shedding. It seems to be even more powerful than the last one I gave you. Here you go.")) {
            sm.sayOk("I'll hold onto it for now. But you should really hold on to it yourself...");
            return;
        }
        if (!sm.addItem(1142157, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22603);
        sm.sayOk("Master, you should use this scale to make something useful that would reduce the damage you take when hit by a monster. You'll get stronger, which means that I'll get stronger. Sounds good to me!");
    }
}
