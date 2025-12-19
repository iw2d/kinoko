package kinoko.script.quest;

import kinoko.packet.world.WvsContext;
import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.item.InventoryType;
import kinoko.world.job.Job;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.stat.Stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EvanQuest extends ScriptHandler {
    public static final int SAFE_GUARD = 9300389;

    // FIELDS
    @Script("dollCave00")
    public static void dollCave00(ScriptManager sm) {
        // Puppeteer's Cave (910510200) - onUserEnter
        // Quest 21719: The Puppeteer's Invitation
    }

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

    @Script("dollMaster00")
    public static void dollMaster00(ScriptManager sm) {
        // Francis the Puppeteer (NPC 1204001) - Inside the Puppeteer's Cave (910510200)
        // Quest 21719: The Puppeteer's Invitation
        // This is the script name used by the NPC in the map

        // Quest hasn't started yet - shouldn't normally happen but just in case
        if (!sm.hasQuestStarted(21719) && !sm.hasQuestCompleted(21719)) {
            sm.sayOk("How did you get in here? This is my private hideout! Get out!");
            return;
        }

        // Quest already completed
        if (sm.hasQuestCompleted(21719)) {
            sm.sayOk("What are you doing back here? I told you everything I wanted to tell you. Now leave me alone!");
            return;
        }

        // Quest is in progress - show the main story dialogue
        sm.sayNext("Well, well, well... Look who we have here. A genuine hero, in the flesh! Or should I say, freshly thawed from ice?");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWho are you? And what do you want?#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("My name is #rFrancis#k, and I'm what you might call a... #rPuppeteer#k. I control monsters, bend them to my will. Those aggressive #o1210102#s in #m100000000#? The violent #o1110100#s in #m101000000#? All my handiwork!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#bWhat?! You're the one causing all that chaos?!#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Chaos? I prefer to call it... preparation. You see, I'm part of something much bigger than myself. An organization dedicated to the resurrection of the #rBlack Mage#k!");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(The Black Mage?! That's the evil wizard I helped seal away before I was frozen!)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Oh yes, I know all about your past, hero. We of the #rBlack Wings#k have been studying you. And now that you're back, things are about to get very interesting...");
        sm.sayBoth("But I've said enough for now. Run along and tell your little friends what you've learned. I'll be seeing you again soon, hero. Very soon...");
        sm.sayBoth("Now get out of my cave!");

        // Warp player back to where they came from (or a safe location)
        // Typically warps back to Lith Harbor to report to Tru
        sm.warp(104000000); // Lith Harbor
    }

    @Script("1204001")
    public static void npc1204001(ScriptManager sm) {
        // Backup script name for NPC 1204001 - calls the main script
        dollMaster00(sm);
    }

    @Script("1002104")
    public static void npc1002104(ScriptManager sm) {
        // Tru - Information Dealer in Lith Harbor (104000000)
        // Handles quest 21719 completion and quest 21720 start

        // Quest 21719 - The Puppeteer's Invitation (COMPLETION)
        if (sm.hasQuestStarted(21719)) {
            final Map<Integer, String> options = new HashMap<>();
            options.put(0, "(You tell him about your encounter with Francis the Puppeteer.)");
            final int answer = sm.askMenu("Hmmm? I still haven't found a suitable Informant Assignment for you… well, do you need me for anything else? Or do you have some juicy information for me...?", options);
            sm.sayNext("#p1204001#, the Black Wing Puppeteer. Okay, now this all makes sense. What happened with the #o1210102#s in #m100000000# and the #o1110100#s in #m101000000# are all being done by the same guy. But wait...are you telling me he also mentioned the Black Mage?");
            // Complete quest 21719
            sm.forceCompleteQuest(21719);
            sm.addExp(1200);
            return;
        }

        // Quest 21720 - The Puppeteer's Warning (START)
        if (sm.hasQuestCompleted(21719) && !sm.hasQuestStarted(21720) && !sm.hasQuestCompleted(21720)) {
            final Map<Integer, String> options = new HashMap<>();
            options.put(0, "(You tell him about your encounter with Francis the Puppeteer.)");
            final int answer = sm.askMenu("Hmmm? I still haven't found a suitable Informant Assignment for you… well, do you need me for anything else? Or do you have some juicy information for me...?", options);
            sm.sayNext("#p1204001#, the Black Wing Puppeteer. Okay, now this all makes sense. What happened with the #o1210102#s in #m100000000# and the #o1110100#s in #m101000000# are all being done by the same guy. But wait...are you telling me he also mentioned the Black Mage?");
            if (!sm.askYesNo("Now that I think of it, I do remember a report saying that there's a group that's trying to revive the Black Mage. I thought it was bogus, but now…it seems quite legit. Are they really trying to revive the Black Mage? Could the prophecy be true?")) {
                sm.sayOk("You don't want to give #p1201000# the dangerous news? She may seem weak on the outside, but remember, she lived alone on that island a long time, just to find you. She's much stronger than you think.");
                return;
            }
            sm.sayNext("I think the Black Wings might be worth looking into. It may seem like a very secretive organization, but there's no way they can outsmart my intelligence network. I'll let you know when I hear something that's relevant. In the meanwhile, you should head over to #b#m140000000##k and tell #b#p1201000##k what has happened here.");
            sm.sayBoth("The return of the hero, a group following the Black Mage, and the prophecy…the three seem to go hand-in-hand. As someone who revived the hero, #p1201000# has a right to know about this as well. #p1201000# may be a great help, too, since she's studied heroes for so long.");
            // Start quest 21720
            sm.forceStartQuest(21720);
            return;
        }

        // Quest 21720 - The Puppeteer's Warning (IN PROGRESS)
        if (sm.hasQuestStarted(21720)) {
            sm.sayOk("Haven't you gone to #m140000000# yet? For anything to do with the Black Mage, you should definitely keep #p1201000# in the loop.");
            return;
        }

        // Default dialogue
        sm.sayOk("Hey there. I'm Tru, the Information Dealer. If you need any intel on monsters, items, or quests, I'm your guy. Just let me know what you need.");
    }

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


    // QUESTS - LEVEL 200 QUEST ----------------------------------------------------------------------------------------

    @Script("q22300s")
    public static void q22300s(ScriptManager sm) {
        // Hero's Succession (22300 - start)
        // Auto-start quest at Level 200
        // NPC 1205000 - Afrien (Slumbering Dragon)
        sm.sayNext("Evan and #p1013000#... You've become so strong. In this state, you should be able to use the power that Freud once held...");
        sm.sayBoth("Come to my island....");
        sm.sayBoth("Now it is time to choose the forgotten power's successor.....");
        sm.sayBoth("And that is you......");

        sm.forceStartQuest(22300);
    }

    @Script("q22300e")
    public static void q22300e(ScriptManager sm) {
        // Hero's Succession (22300 - end)
        // NPC 1205000 - Afrien
        sm.sayNext("The Dragon Master's strength clearly shows the relationship between Humans and Onyx Dragons.");
        sm.sayBoth("When the relationship between the two are not in harmony, the Onyx Dragon is only at half power. But when the two are in perfect harmony, the Onyx Dragon's power explodes.");
        sm.sayBoth("You are both young, yet you have great powers. Freud and I were not as strong at that age... That is because the two of you are bonded so tightly.");
        sm.sayBoth("Onyx Dragons are a race drawn to strong spirits. Just looking at you two, it is obvious how strong your spirits must be... There is no doubt that you are qualified to receive this skill...");

        if (!sm.askYesNo("Will you accept the power of Hero's Echo?")) {
            sm.sayOk("Come back when you are ready to accept this power.");
            return;
        }

        sm.sayOk("#bHero's Echo#k... This is a skill that was used by Freud, my former master. I trust that you will use the skill well... Please take good care of Maple World...and of #p1013000#.");

        // Give Hero's Echo skill (20011005) and medal (1142158)
        sm.addSkill(20011005, 1, 1); // Hero's Echo
        sm.addItem(1142158, 1); // Hero's Echo Medal
        sm.forceCompleteQuest(22300);
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

    @Script("q22400s")
    public static void q22400s(ScriptManager sm) {
        // Rumor about the Dragon Mount (22400 - start/auto-complete)
        sm.sayNext("How is your research on Onyx Dragons going? I recently heard an interesting rumor about a #bDragon mount#k. Someone was seen in Victoria Island riding a Dragon.");
        sm.sayBoth("Amazing, right? Dragons are known to be haughty and prideful, but I guess they let humans they are close to ride them. I wonder if the same applies to Onyx Dragons.");
        sm.sayBoth("But we can't study Onyx Dragons, since they became extinct a long time ago... I wish there were #bsomeone we could talk to who'd know about whether Onyx Dragons can be used as mounts#k.");
        sm.sayBoth("I thought I'd tell you about it, since you're researching Onyx Dragons. If you ever find out whether Onyx Dragons can be mounted, do come back to #b#m101000000##k and let me know.");
        sm.forceStartQuest(22400);
    }

    @Script("q22400e")
    public static void q22400e(ScriptManager sm) {
        // Rumor about the Dragon Mount (22400 - end)
        final int answer = sm.askMenu("Oh, it's you... How is your study of Onyx Dragons going? Did you ever find out if Onyx Dragons can be mounted.", java.util.Map.of(
                0, "I think it is possible, but I'm not sure how. I should ask the person who is rumored to have ridden a Dragon."
        ));
        sm.sayNext("Hm. I guess the person I mentioned before who was riding a Dragon wasn't riding an Onyx Dragon. Still, maybe you'd learn how to mount an Onyx Dragon if you talk to him about how he mounts his own Dragon.");
        sm.sayBoth("If you are really interested, you should visit #m102000000#. #b#p9901000##k is said to be at the #b#m102000000# Warrior's Sanctuary. You should be able to find out more about mounting Dragons from him. He is supposed to be very powerful... ");
        sm.sayBoth("I guess if an Onyx Dragon had a close enough relationship with a human, then the Dragon would let that human mount him. This will take some more studying...");
        sm.forceCompleteQuest(22400);
    }

    @Script("q22402s")
    public static void q22402s(ScriptManager sm) {
        // Meeting the Dragon Rider (22402 - start)
        sm.sayNext("Hello. My name is #p9901000#. Would you like advice on how to be a powerful Warrior? Hm, you're not a Warrior... What can I do for you?");
        final int answer1 = sm.askMenu("", java.util.Map.of(
                0, "I heard that you know how to mount a Dragon... Is that true?"
        ));
        sm.sayNext("Yes, it's true. I am able to mount a Dragon thanks to the blessing of the Goddess, though I was quite surprised when I discovered that fact. But why do you ask?");
        final int answer2 = sm.askMenu("", java.util.Map.of(
                0, "Well, I would also like to mount a Dragon and want to know how! Can you teach me?"
        ));
        sm.sayNext("Well, to get a Dragon from the Goddess, you need to reach Lv. 200 with an Adventurer character. As you may know, that is no easy task. It actually took me several years.");
        final int answer3 = sm.askMenu("", java.util.Map.of(
                0, "Oh, you don't have to worry about that! I already have a Dragon!"
        ));
        sm.sayNext("That thing standing next to you? It looks so different from my Dragon that I thought it was just a really big lizard! I don't think you are Lv. 200. How did you get it?");
        final int answer4 = sm.askMenu("", java.util.Map.of(
                0, "I found it. Hehe."
        ));
        sm.sayNext("You found it? There must be a story behind this... In any case, I will tell you about mounting Dragons. But just having a Dragon doesn't mean you can mount it. You'll need to prepare some items.");
        final int answer5 = sm.askMenu("", java.util.Map.of(
                0, "Sure. Like what?"
        ));
        sm.sayNext("The first thing you'll need is a very #bstrong saddle#k. Without one, the hard scales will destroy your bottom...");
        final int answer6 = sm.askMenu("", java.util.Map.of(
                0, "Where can I get a strong saddle?"
        ));
        sm.sayNext("I'm not sure. I actually had my saddle naturally upgraded through the Goddess's blessing.");
        final int answer7 = sm.askMenu("", java.util.Map.of(
                0, "I see. Well, do you have any guesses?"
        ));
        sm.sayNext("Hmm... Those #bpeople who ride birds#k seem to have nice-looking saddles... Maybe you can get a strong saddle from wherever they get theirs. At the very least, you'd get more info...");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmm... You haven't seen the people who ride around on birds? Perhaps someone in their homeland makes their sandles for them...");
            return;
        }
        sm.sayNext("I believe those people who ride birds come from #b#m130000000##k. Maybe you should over there.");
        sm.sayBoth("I'm sorry I couldn't be of more help. But if you are able to get a strong saddle, I will go ahead and teach you the Monster Rider skill.");
        sm.forceStartQuest(22402);
    }

    @Script("q22402e")
    public static void q22402e(ScriptManager sm) {
        // Meeting the Dragon Rider (22402 - end)
        sm.sayNext("Oh, you got a saddle! It looks strong enough to protect you from the Dragon's scales. I will now teach you the Monster Rider skill.");
        sm.sayBoth("I've taught you the Monster Rider skill. With this skill and a saddle, you should be able to mount your Dragon freely.");
        sm.sayBoth("Just remember that you have to get off your Dragon when you use skills. Otherwise, your Dragon may get hurt. Also remember that you will not be able to mount in certain areas.");
        sm.sayBoth("I hope you have great adventures mounted on your Dragon.");
        sm.removeItem(1912033, 1);
        sm.addSkill(20011004, 1, 1); // Monster Rider
        sm.forceCompleteQuest(22402);
    }

    @Script("q22405s")
    public static void q22405s(ScriptManager sm) {
        // The Lost Saddle (22405 - start)
        sm.sayNext("Oh, you're the person who wanted to mount that strange animal. What brings you back? Did you lose the saddle?");
        if (!sm.askAccept("")) {
            sm.sayOk("If you don't want to bring me the materials, I can't make you a saddle.");
            return;
        }
        sm.sayNext("In that case, you'll have to gather all of the materials for the saddle again. Let me remind you what they are. You need #b50 #t4000155#s#k, #b1 #t4032474##k, and most importantly, the fee... Hehehe...");
        sm.sayBoth("I gave you a special discount last time, but this time I'll have to charge you #b20 million mesos#k. Since your level is so high, this should be easy for you, right? Have a nice day!");
        sm.forceStartQuest(22405);
    }

    @Script("q22405e")
    public static void q22405e(ScriptManager sm) {
        // The Lost Saddle (22405 - end)
        sm.sayNext("You've brought all the materials! And the fee as well! Hehe, I'll go ahead and start making your saddle. I should be able to make it much faster this time. ");
        sm.sayBoth("Here is the completed saddle. Enjoy it and feel free to come back if you happen to lose it. See you next time!");
        sm.removeItem(4000155, 50);
        sm.removeItem(4032474, 1);
        sm.addMoney(-20000000);
        if (!sm.addItem(1912033, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22405);
    }

    @Script("q22407s")
    public static void q22407s(ScriptManager sm) {
        // Making a Bigger Saddle (22407 - start)
        sm.sayNext("Hello. Aren't you the person who special ordered a saddle before. What brings you back? Whoa, that animal next to you, is that the same animal as before? It's huge now! I can hardly recognize it!");
        sm.sayBoth("Seeing that animal, I can guess why you are here. He's gotten so big, there's no way that saddle still fits him. Are you here to order a #bnew saddle#k?");
        if (!sm.askAccept("")) {
            sm.sayOk("I guess you don't need a saddle? By the looks of it though, I doubt that the old saddle is going to fit anymore...");
            return;
        }
        sm.sayNext("Okay, I will make another special saddle for you. Seeing how much it has grown, I'll have to use a more flexible material this time. Hmm... The only materials that might work are so expensive, I'm not sure if you'll be able to find them...");
        sm.sayBoth("Let me tell you what the materials are. First, #b#m211000000#s#k. They can be obtained from #r#o8140000#s#k, a frightful monster that appears in the #b#m211000000##k area. You'll need just #b10#k.");
        sm.sayBoth("Second, you need #b2 #t4032476#s#k, which can be found inside the #bShipwreck Tresure Chest#k on the wrecked ship deep inside Aqua Road. The monsters there that are over level 85 so be very careful.\nA Shipwreck Treasure Chest looks like this.\n#i4032557#\r\n");
        sm.sayBoth("Third, you need #b2 #t4032477##ks#k, a specialty product from #m251000000#. They used to sell it at #m251000000#, but for some reason they've stopped. You'll have to go ask #b#p2092001##k about it.");
        sm.sayBoth("Fourth and most important, you need to pay a fee of #b30 million mesos#k.");
        sm.sayBoth("What?! I'm the only person in Maple World that can handle those kinds of materials. Besides, you must be rich to have such an interesting animal.");
        sm.forceStartQuest(22407);
    }

    @Script("q22407e")
    public static void q22407e(ScriptManager sm) {
        // Making a Bigger Saddle (22407 - end)
        sm.sayNext("You brought all the materials! I'll start making your saddle right away.");
        sm.sayBoth("A beautiful saddle has been completed that is flexible enough to let the animal move yet strong enough to protect the rider. ");
        sm.sayBoth("However, there is a limit to the flexibility of this saddle. I'm not sure how big this Dragon will get, but if it gets too big, the saddle might not be able to handle it. If that happens, you'll have to come back and order a new one.");
        sm.removeItem(4032475, 10);
        sm.removeItem(4032476, 2);
        sm.removeItem(4032477, 2);
        sm.addMoney(-30000000);
        if (!sm.addItem(1912034, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22407);
    }

    @Script("q22408s")
    public static void q22408s(ScriptManager sm) {
        // Obtaining the Unbreakable Porcelain (22408 - start)
        sm.sayNext("What can I do for you? If you're looking purchase herbs, you can't right now becuase of the Red-Nosed Pirates... Huh? You're looking for #t4032477#? Unfortunately, you can't get them here anymore because of the Red-Nosed Pirates!");
        sm.sayBoth("There was a master artisan known as the #b#t4032497##K in #m251000000#. The porcelain he used had a mysterious power that allowed it never to break. That porcelain also made it so herbs never spoiled. His skills were so amazing that the Red-Nosed Pirates kidnapped the #b#t4032497##K.");
        sm.sayBoth("Since then, the herbs spoil easily and the only porcelain we have left are these strange ones in which odd organisms grow. There is no more #t4032477#. If you want one, you'll have to enter the Red-Nosed Pirate hideout and rescue the #t4032497#... Can you do it?");
        if (!sm.askAccept("")) {
            sm.sayOk("I guess I was expecting too much... How will we ever rescue the #t4032497#...");
            return;
        }
        sm.sayNext("I'm sorry, I didn't realize what a great adventurer you were! Please rescue the #t4032497#. The #t4032497# should be inside the #b#m251010403# storage#k, where the Red-Nosed Pirates hold all the treasures that they have stolen.");
        sm.sayBoth("Enter carefully and rescue the #t4032497#. If you can do it, all of #m251000000# will thank you.");
        sm.forceStartQuest(22408);
    }

    @Script("q22408e")
    public static void q22408e(ScriptManager sm) {
        // Obtaining the Unbreakable Porcelain (22408 - end)
        sm.sayNext("Oh wow, you rescued the #t4032497#! Is the #t4032497# all right? Let me take a look at him.");
        sm.sayBoth("Whew... Luckily, he's only passed out. Than again, even the Red-Nosed Pirates wouldn't have hurt such a skilled artisan... Now the #t4032497# should be able to make you the #t4032477# you need.");
        sm.removeItem(4032497, 1);
        sm.forceCompleteQuest(22408);
    }

    @Script("q22409s")
    public static void q22409s(ScriptManager sm) {
        // Making the Unbreakable Porcelain (22409 - start)
        sm.sayNext("Thank you so much for rescuing me! Let me know if there is anything I can do for you. Huh? You want me to make you #t4032477#?");
        sm.sayBoth("That'd be easy, except all of my materials have been stolen by the Pirates! I'm so sorry...");
        sm.sayBoth("If this is really urgent, you could  gather the materials yourself... If you can bring me the materials, I'll have it made for you faster than you can blink. ");
        if (!sm.askAccept("")) {
            sm.sayOk("I guess getting the materials is too difficult... What to do? I can't even reward you properly...");
            return;
        }
        sm.sayNext("Ah, what a great young man you are. To get the materials, break the #r#o4230505#s#k and the #r#o4230506#s#k around #m251000000# and bring back #b100 #t4000291#s#k and #b100 #t4000292#s#k. ");
        sm.forceStartQuest(22409);
    }

    @Script("q22409e")
    public static void q22409e(ScriptManager sm) {
        // Making the Unbreakable Porcelain (22409 - end)
        sm.sayNext("Great, you've brought all the materials. Please give them to me. I will make it right away.");
        sm.sayBoth("Here is the completed #t4032477#s. It would take quite some power to break these. Of course, someone really high level may be able to break it... Really, really high level...");
        sm.removeItem(4000291, 100);
        sm.removeItem(4000292, 100);
        if (!sm.addItem(4032477, 2)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22409);
    }

    @Script("q22410s")
    public static void q22410s(ScriptManager sm) {
        // The Lost Big Saddle (22410 - start)
        sm.sayNext("Hello. How are you enjoying your saddle? Wait, where is your saddle? Eek! You want me to make you another saddle? You didn't lose the saddle, did you?");
        sm.sayBoth("I worked so hard to make such a nice saddle for you. How could you lose it? You really don't take care of your stuff. Honestly, I really don't want to make another one for someone like you...");
        sm.sayBoth("But, fine. I'll make you a new one just this once. There is a condition however. You'll have to bring me the materials just like before but you'll have to pay double the fee.");
        if (!sm.askAccept("")) {
            sm.sayOk("Are you refusing because the fee is too high? You shouldn't have lost it in the first palce. I won't lower the price.");
            return;
        }
        sm.sayNext("You do remember what the material are right? #b10 #t4032504#s#k from #m211000000#, #b2 #t4032505#s#k from the Shipwrecked Treasure Chests deep inside Aqua Road, #b2 #t4032477#s#k, a specialty product of  #m251000000#, and...");
        sm.sayBoth("You will also need to pay me a #bfee of 60 million mesos#k. It may seem like a lot but I'm charging you extra this time so that you won't lose it again. I will be waiting.");
        sm.forceStartQuest(22410);
    }

    @Script("q22410e")
    public static void q22410e(ScriptManager sm) {
        // The Lost Big Saddle (22410 - end)
        sm.sayNext("You brought all of the materials. Please give them to me along with the fee.");
        sm.sayBoth("Here is the newly made saddle. Please use this incident as a lesson and be careful not to lose it again.");
        sm.removeItem(4032504, 10);
        sm.removeItem(4032505, 2);
        sm.removeItem(4032477, 2);
        sm.addMoney(-60000000);
        if (!sm.addItem(1912034, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22410);
    }

    @Script("q22412s")
    public static void q22412s(ScriptManager sm) {
        // Making a Really Big Saddle (22412 - start)
        sm.sayNext("Hi, how are you? The special animal you are raising seems to be growing well. His horn and wings have gotten so big... It looks like he's going to need a new saddle.");
        sm.sayBoth("The materials that were used to make the current saddle won't be able to handle his bigger size or his sharper and stronger scales. He's going to need a saddle made with stronger materials. ");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmm... I guess you're not as rich as I thought. If it's too difficult for you, it's not a bad idea to level up a bit more and save some more money.");
            return;
        }
        sm.sayNext("Please bring me #b300 #t4000270#s#k from the #r#o8150302#s#k, #b300 #t4000271#s#k  from the #r#o8190005#s#k, and #b300 #t4000272#s#k from the #r#o8190000#s#k. With those materials, I should be able to make a new saddle that will be suitable for #p1013000#.");
        sm.sayBoth("The only thing is that those materials are much more difficult to handle than before so I'm going to have to charge you a higher fee. It's going to me 60000000 mesos. Your eyes are spinning from all those 0s. Simply stated, it will cost #b60 million mesos#k.");
        sm.sayBoth("I realize how expensive that sounds but it's an adequate price for the skill it takes to make a saddle using those materials. If the #m230000003# cost less to operate, I might be able to give you a discount, but as you can see, there are so few visitors here at the #m230000003#...");
        sm.sayBoth("But since you're raising an animal that must cost a fortune to feed, you must be very well off. 60 million mesos is probably nothing to you. Heck, someone like you could probably buy dozens of saddles.");
        sm.sayBoth("I will be here waiting for you to bring all the materials.");
        sm.forceStartQuest(22412);
    }

    @Script("q22412e")
    public static void q22412e(ScriptManager sm) {
        // Making a Really Big Saddle (22412 - end)
        sm.sayNext("You've brought all the materials! And the 60 million mesos as well! I knew it, I really have a good eye for picking out rich people... I mean... Nevermind. Now, please give the materials to me. I will make your new saddle right away. ");
        sm.sayBoth("I've made the saddle strong enough to protect the rider from the sharp scales while still being very light. There is also a safety feature to protect the rider from falling off. Enjoy your new saddle and please let me know if you lose it!");
        sm.removeItem(4000270, 300);
        sm.removeItem(4000272, 300);
        sm.removeItem(4000271, 300);
        sm.addMoney(-60000000);
        if (!sm.addItem(1912035, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22412);
    }

    @Script("q22413s")
    public static void q22413s(ScriptManager sm) {
        // The Lost Really Big Saddle (22413 - start)
        sm.sayNext("Hi, how are you? Hey, since you're here, can I see you ride? I've really wanted to see that for a while... What? You lost your saddle? You want me to make you another one?");
        sm.sayBoth("How...how could you lose that saddle...? Do you know how hard it was to make? Working with #t4000270#s is so difficult...and putting all those  #t4000272#s together... You really should learn to take better care of your belongings!");
        sm.sayBoth("I can make you a new saddle. You just have to give me the materials and the fee. Seeing as you don't know how to take care of your things, you'll probably just lose it again! So this time, I'm going to have to charge you double!");
        if (!sm.askAccept("")) {
            sm.sayOk("Then I can't make you another saddle. ");
            return;
        }
        sm.sayNext("So you agree? Then please bring me #b300#k #b#t4000270#s#k, #b#t4000272#s#k, and#b#t4000271#s#k! Once you bring them, I will be able to make a new saddle for you.");
        sm.sayBoth("You do remember what the fee was last time right? 60 million mesos... So this time, it's 120 million mesos.. Yup, #b120000000 mesos#k. So many 0s...");
        sm.sayBoth("What? I look like I'm happy? Of course not! No matter how much wealth I might accumulate from making all these saddles, I hate people who can't take care of their stuff. That's the only reason I'm charging so much. I'm serious!");
        sm.forceStartQuest(22413);
    }

    @Script("q22413e")
    public static void q22413e(ScriptManager sm) {
        // The Lost Really Big Saddle (22413 - end)
        sm.sayNext("Have you brought all the materials for the saddle? And the fee? Whoa... I didn't think you would really... Umm... Nevermind. Now, please give me that materials and the fee.");
        sm.sayBoth("Here is the completed saddle. I made it really strong so please don't lose it this time. If you lose do. I'll have to charge you 240 million... Okay I won't really charge that much, but it won't be cheap!");
        sm.removeItem(4000270, 300);
        sm.removeItem(4000272, 300);
        sm.removeItem(4000271, 300);
        sm.addMoney(-120000000);
        if (!sm.addItem(1912035, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22413);
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

    @Script("q22505s")
    public static void q22505s(ScriptManager sm) {
        // Tasty Milk 2 (22505 - start)
        sm.sayNext("He's so big I didn't realize he was a baby. He probably can't digest meat yet. My guess is that all #bbabies need milk#k first.");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmm... I think most babies are the same. Think about it and let me know if you change your mind.");
            return;
        }
        sm.sayNext("You can get milk from the #b#p1013105##k at the #b#m100030310##k. Why don't you go ask her to give you some?");
        sm.sayBoth("Oh, and once you're done feeding the lizard, can you come back to me? I have something to talk to you about.");
        sm.addExp(1150);
        sm.forceCompleteQuest(22505);
        sm.forceStartQuest(22506);
    }

    @Script("q22505e")
    public static void q22505e(ScriptManager sm) {
        // Tasty Milk 2 (22505 - end, at Dairy Cow)
        sm.sayOk("Mooo!");
    }

    @Script("q22506s")
    public static void q22506s(ScriptManager sm) {
        // Tasty Milk 3 (22506 - start)
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(You ask the #p1013105# to give you some milk.)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Mooo...");
        if (!sm.askAccept("")) {
            sm.setPlayerAsSpeaker(true);
            sm.sayOk("#b(You're too afraid to get closer. Come back later for the milk.)#k");
            return;
        }
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(The #p1013105# gives you some milk. Go feed the milk to #p1013000#.)#k");
        if (!sm.addItem(4032454, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22506);
    }

    @Script("q22506e")
    public static void q22506e(ScriptManager sm) {
        // Tasty Milk 3 (22506 - end, feed to Mir)
        sm.sayNext("I'm so hungry, I have no strength left... Master, I'm so hungry I might shrivel up and really become a lizard. What's this? Water? You want me to fill my stomach with water? If you say so, master...");
        sm.sayBoth("(Gulp, gulp, gulp)");
        sm.sayBoth("Wow, this is so good! What is this water called? Milk? Yum! I feel sooo strong now!");
        sm.sayBoth("Hey, it looks like you've become stronger too, master. Your HP and MP is much higher than when I first saw you.");
        sm.removeItem(4032454, 1);
        sm.addExp(1420);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2200), 1);
        sm.forceCompleteQuest(22506);
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

    @Script("q22508s")
    public static void q22508s(ScriptManager sm) {
        // Strange Pigs 1 (22508 - start)
        sm.sayNext("Sheesh, it doesn't matter how many times I fix it. The #r#o1210111#s#k are acting so crazy! I've fixed the fence a few times already but they are so strong, they just keep breaking through.");
        sm.sayBoth("They even look different from normal Pigs. They look like... \n\n#i4032527#\n\n...this. Don't they look strange?");
        sm.sayBoth("I wish someone could take care them... Evan, you be careful. A lot of #o1210111#s are around the #b#m100030320##k so be careful if you have to go past there.");
        if (!sm.askAccept("")) {
            sm.sayOk("Huh? Haha, you must be full of energy. But you can get hurt if you think of them like normal #o1210100#s.");
            return;
        }
        sm.sayOk("Yes, they shouldn't be taken lightly just because they are #o1210100#s...");
        sm.addExp(320);
        sm.forceCompleteQuest(22508);
        sm.forceStartQuest(22509);
    }

    @Script("q22508e")
    public static void q22508e(ScriptManager sm) {
        // Strange Pigs 1 (22508 - end, talk to Mir)
        sm.sayOk("Master, master! This is our chance! This is our chance to show how strong I am! Let's eliminate the #o1210111#s that that human was talking about!");
    }

    @Script("q22509s")
    public static void q22509s(ScriptManager sm) {
        // Strange Pigs 2 (22509 - start)
        final int answer1 = sm.askMenu("If we work together, the #o1210100#s are a cinch! Well?Come on! Let's eliminate them! Please, master!", java.util.Map.of(
                0, "It's too dangerous. Those #o1210100#s are stronger than normal #o1210100#s."
        ));
        sm.sayNext("I know, but we can do it! Besides, if it gets too dangerous, we can run away. Just trust me! Well, master? Please?");
        if (!sm.askAccept("")) {
            sm.sayOk("Oh come on, really? Master, are you chicken?!");
            return;
        }
        sm.sayOk("All right! Let's hurry to the #b#m100030320##k and teach those #r#o1210111#s#k a lesson! I think eliminating #r20#k of them should be enough? Let's go!");
        sm.forceStartQuest(22509);
    }

    @Script("q22509e")
    public static void q22509e(ScriptManager sm) {
        // Strange Pigs 2 (22509 - end)
        final int answer = sm.askMenu("Woohoo! I told you it'd be simple! #o1210100#s are nothing for us!", java.util.Map.of(
                0, "Wow! #p1013000#, you're stronger than I thought!"
        ));
        sm.sayNext("Hehehe. Master, when you and I work together, there's nothing that we can't defeat.");
        sm.sayBoth("Hey, we should show off our accomplishment! Let's go tell #bDad#k what we did! Hehe, he'll probably be really happy and tell us we did a good job!");
        sm.addExp(1980);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2200), 1);
        sm.forceCompleteQuest(22509);
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

    @Script("q22511s")
    public static void q22511s(ScriptManager sm) {
        // Mushrooms Instead of Meat! (22511 - start)
        final int answer = sm.askMenu("Delayed delivery of the #t4032453#? We really need meat in Henesys, but I guess we'll just have to survive on mushrooms for a while... ", java.util.Map.of(
                0, "Is there anything I can help you with?"
        ));
        sm.sayNext("Oh, you're still here, Evan? Well, if you can, do you think you can bring me #b20 #t4000001#s#k?");
        if (!sm.askAccept("")) {
            sm.sayOk("It's probably too difficult for you. What should I do... Should I just ask another adventurer passing through?");
            return;
        }
        sm.sayNext("Oh, you can? #r#o1210102#s#k can be found easily in #b#m104040001##k or #bIII#k. They can also be found at the #b#m100010100# near #m100000000##k. Thanks. ");
        sm.sayBoth("By the way, that lizard... Nevermind. Kids these days have such strange pets... I'd rather act like I didn't see anything.");
        sm.forceStartQuest(22511);
    }

    @Script("q22511e")
    public static void q22511e(ScriptManager sm) {
        // Mushrooms Instead of Meat! (22511 - end)
        sm.sayNext("Oh wow, you've brought the 20 #t4000001#s that I asked for! That's pretty good. #p1013103# must be teaching you hunting skills too! This is great. Thank you.");
        sm.sayBoth("If there is anything I can ever do for you, please let me know. If there is something I can help you with in #m100000000#, I will do my best to assist you.");
        sm.removeItem(4000001, 20);
        sm.addExp(3560);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2200), 1);
        if (!sm.addItems(List.of(
                Tuple.of(2000001, 30), // Red Potion
                Tuple.of(2000003, 30)  // Blue Potion
        ))) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        if (!sm.addItem(1142152, 1)) { // The Dragon Master's Necklace
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22511);
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

    @Script("q22513s")
    public static void q22513s(ScriptManager sm) {
        // Rina's Worries (22513 - start)
        final int answer1 = sm.askMenu("Huh? Am I worried about something? Well, the truth is that #p1012101#'s health has been getting worse these days so I want to make her some Blue Mushroom Porridge but it's so difficult to find any #t4000009#s.", java.util.Map.of(
                0, " Are #o2220100#s rare?"
        ));
        final int answer2 = sm.askMenu("No, there are a lot of them at the #m106010100#. But the #o2220100#s have become so violent these days, it's difficult to get Blue Mushroom Caps... I really want to make some Blue Mushroom Porridge for #p1012101#... Sigh...", java.util.Map.of(
                0, "Should I get some #t4000009#s for you?"
        ));
        final int answer3 = sm.askMenu("You?! No way! What are you thinking? You can't handle the #o2220100#s!  #o1210102#s and #o2220100#s are totally different. For your own safety, don't even think of going anywhere near them! Okay?", java.util.Map.of(
                0, "But..."
        ));
        sm.sayNext("No buts! If I ask #p1040000#, the Security Guard, and he tells me that he's seen you, I'll ask Chief Stan to deny you entrance to #m100000000#! Okay? You promise?");
        if (!sm.askAccept("")) {
            sm.sayOk("Don't even think about it! Don't you dare go there! Never!");
            return;
        }
        sm.sayNext("Okay... Don't do anything dangerous. I'll just ask another adventurer who happens to pass this way.");
        sm.sayBoth("Oh and um... Please don't take this the wrong way but... Why are you walking around with a lizard? A pet? I guess he is kind of cute but...you have a pretty peculiar taste...");
        sm.addExp(360);
        sm.forceCompleteQuest(22513);
        sm.forceStartQuest(22514);
    }

    @Script("q22513e")
    public static void q22513e(ScriptManager sm) {
        // Rina's Worries (22513 - end, talk to Mir)
        final int answer1 = sm.askMenu("Master, she is in trouble! We can help her!", java.util.Map.of(
                0, "Not this time. She says it's dangerous."
        ));
        final int answer2 = sm.askMenu("If we avoid it just because it's dangerous, how can we call ourselves heroes? We have to do it!", java.util.Map.of(
                0, "Well, you're kind of right, but we made a promise and breaking a promise is wrong. I think a hero would keep his word."
        ));
        final int answer3 = sm.askMenu("Well, I suppose... Bu...but...", java.util.Map.of(
                0, "No is no. Besides I don't think we can win against the #o2220100#s."
        ));
        sm.sayOk("So if we can beat the #o2220100#s, we can go?");
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

    @Script("q22515s")
    public static void q22515s(ScriptManager sm) {
        // Power B. Fore's Training Center (22515 - start)
        sm.sayNext("There is a Bowman Training Center near #m100000000#. It should be quite useful if you haven't reached Level 20 yet. But as the name suggests, it's only open to Bowman... Well, since you did help me, I'll make an exception and let you use it.");
        if (!sm.askAccept("")) {
            sm.sayOk("Are you not planning on using the Training Center? Then why are you asking?");
            return;
        }
        sm.sayNext("If you take the Recommendation Letter I just gave you to the #b#m100010000##k, you'll meet #b#p1012118##k. He's full of hot air, but he is supposed to be pretty helpful. Just show him the Recommendation Letter to use the Training Center.");
        sm.sayBoth("But why do you want to train? Is #p1013103# planning for you to become an Adventurer? You're not going to leave home and not come back like #p1052000#, are you? Hmm... Nevermind. Take care of yourself.");
        if (!sm.addItem(4032456, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22515);
    }

    @Script("q22515e")
    public static void q22515e(ScriptManager sm) {
        // Power B. Fore's Training Center (22515 - end)
        sm.sayNext("Oh, are you a Bowman who's come to train? Or not... From the wand in your hand, you must be a Magician. What is that strange pet you have? I can't let anyone in who is not a Bowman. Huh? This is... #t4032456#?");
        sm.sayBoth("Wait, a Recommendation Letter?! Stan is not compassionate enough to write something like this. Let me take a closer look.");
        sm.removeItem(4032456, 1);
        sm.addExp(1210);
        sm.forceCompleteQuest(22515);
    }

    @Script("q22516s")
    public static void q22516s(ScriptManager sm) {
        // Power B. Fore's Paranoia (22516 - start)
        final int answer1 = sm.askMenu("This...this isn't possible! That miserly Stan would never write a Recommondation Letter for anyone! I don't believe it! I grew up with Stan, so I know! This is a conspiracy! That Stan is just trying to trick me! ", java.util.Map.of(
                0, "(Hmm... Chief Stand did say #p1012118# was full of hot air. Do they hate each other?)"
        ));
        final int answer2 = sm.askMenu("And you're nothing but his little instrument, here to muddy the quality of my Training Center! If people were to find out that someone like you was training here, rumor would spread that training here isn't very helpful. I won't let that happen!", java.util.Map.of(
                0, "(This guy is paranoid. Who'd want to muddy his Training Center's reputation?)"
        ));
        sm.sayNext("I won't simply fall for Stan's little trick. I refuse this Recommendation Letter! Well, that's what I want to say. But he's the Chief of #m100000000#, I can't just refuse... Oh, a test! I'll give you a test!  ");
        if (!sm.askAccept("")) {
            sm.sayOk("Yes, I'm sure of it. This is all a part of Stan's conspiracy! Did you think I would fall for it, Stan?! Puhaha!");
            return;
        }
        sm.sayNext("There are a lot of #r#o9300274#s#k that have appeared at the #b#m100010100##k to your right. Eliminate #r50#k of them! Puhaha, of course there's no way you can pull that off. You're just Stan's instrument!");
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(Sheesh, he's wrong, but there's no point trying to convince him. You have little choice but to eliminate the #o9300274#s.)#k");
        sm.forceStartQuest(22516);
    }

    @Script("q22516e")
    public static void q22516e(ScriptManager sm) {
        // Power B. Fore's Paranoia (22516 - end)
        sm.sayOk("Wha..What? You eliminated 50 #o9300274#s? Th...there's no way!");
        sm.addExp(4390);
        if (!sm.addItems(List.of(
                Tuple.of(2000001, 20), // Red Potion
                Tuple.of(2000003, 20)  // Blue Potion
        ))) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22516);
    }

    @Script("q22517s")
    public static void q22517s(ScriptManager sm) {
        // Power B. Fore's Continuing Paranoia (22517 - start)
        final int answer1 = sm.askMenu("No way! This can't be! I can't accept it! There is no way that a person recommended by Stan would have such skills... This is impossible!", java.util.Map.of(
                0, "Ahem, so can I start using the Training Center now?"
        ));
        final int answer2 = sm.askMenu("I can't accept it! You're using some sort of trick! Yes, that's it! Some high level adventurer passing by must have helped you! I'm right, aren't I?!", java.util.Map.of(
                0, "No one helped me..."
        ));
        sm.sayNext("Ha, an instrument of Stan would have no problems telling a lie. I can't believe you! I will give you another test! This time, eliminate #r80 #o9300274##ks!");
        if (!sm.askAccept("")) {
            sm.sayOk("Puhaha... So I was right! It was all a lie. It wasn't a bad attempt but you're far from being able to trick me!");
            return;
        }
        sm.sayOk("Hehehe, even a high level adventurer would be too annoyed to help you eliminate so many of them. This time, I will uncover your mask once and for all!");
        sm.forceStartQuest(22517);
    }

    @Script("q22517e")
    public static void q22517e(ScriptManager sm) {
        // Power B. Fore's Continuing Paranoia (22517 - end)
        sm.sayOk("Huh?! You're back... You...you didn't...eliminate 80 #o9300274#s, did you? What? No way! This is not possible! It's all a lie!");
        sm.addExp(2400);
        if (!sm.addItems(List.of(
                Tuple.of(2000001, 20), // Red Potion
                Tuple.of(2000003, 20)  // Blue Potion
        ))) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22517);
        sm.forceStartQuest(22518);
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

    @Script("q22519s")
    public static void q22519s(ScriptManager sm) {
        // Power B. Fore's Training (22519 - start)
        final int answer1 = sm.askMenu("So what do you think? Isn't my Training Center amazing?", java.util.Map.of(
                0, "You talk like it's so great but there are no strong monsters inside..."
        ));
        final int answer2 = sm.askMenu("No...no strong monsters?! Do you not realize how frightful of a monster a #o0210100# is...!", java.util.Map.of(
                0, "Um, #o0210100#s are even lower level than #o1210102#s."
        ));
        final int answer3 = sm.askMenu("Uggh! How did that secret get out?", java.util.Map.of(
                0, "(You could tell just by fighting the monsters, but you get the hunch it'd be useless to explain that.)"
        ));
        sm.sayNext("Hmm... Well aside from the #o0210100#s, the #o1210101#s are stronger than the #o1210102#s, aren't they? You can level up in the blink of an eye just hunting them! Here, let me train you personally!");
        if (!sm.askAccept("")) {
            sm.sayOk("You...you refuse the training? Why? Why is it that no one I train believes in me?");
            return;
        }
        sm.sayOk("Okay, go hunt #r150 #o1210101#s#k! If you can do it, I will recognize your abilities! Puhaha!");
        sm.forceStartQuest(22519);
    }

    @Script("q22519e")
    public static void q22519e(ScriptManager sm) {
        // Power B. Fore's Training (22519 - end)
        final int answer = sm.askMenu("Have you eliminated the #o1210101#s? Ah, I see that my eyes have not deceived me. From the moment I first laid eyes on you, I knew how strong you were.", java.util.Map.of(
                0, "(Didn't he call you Stan's instrument?)"
        ));
        sm.sayOk("Hehe... Everyone who trains under me levels up so quickly.");
        sm.addExp(2900);
        sm.forceCompleteQuest(22519);
    }

    @Script("q22520s")
    public static void q22520s(ScriptManager sm) {
        // Receiving Power B. Fore's Certificate of Training Again (22520 - start)
        sm.sayNext("What is it? You want to receive my training again? Puhaha! My training is so great, even people who've completed training want to receive it again! Okay, let's start training!");
        if (!sm.askAccept("")) {
            sm.sayOk("You refuse? revolting already? Now, now... You're not nearly strong enough to fight me. Hehe, why don't you cool your head and then come back.");
            return;
        }
        sm.sayOk("Hunt #r200 #o1210101#s#k. Only difficult training can make you a master! ");
        sm.forceStartQuest(22520);
    }

    @Script("q22520e")
    public static void q22520e(ScriptManager sm) {
        // Receiving Power B. Fore's Certificate of Training Again (22520 - end)
        sm.sayNext("Ah, you defeated 200 #o1210101#s? Faster than I thought. This is all a result of my training. Here, take this.");
        sm.sayBoth("You must be very happy to receive the Certificate you so desired. Puhaha.");
        if (!sm.addItem(4032457, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22520);
    }

    @Script("q22521s")
    public static void q22521s(ScriptManager sm) {
        // Become a Hero (22521 - start)
        final int answer1 = sm.askMenu("Master, master! Now that our skills have gotten better, let's go hunt the #o2220100#s! Come on!", java.util.Map.of(
                0, "But it's too dangerous..."
        ));
        final int answer2 = sm.askMenu("That was because we were weaker back then. But now that we've trained, #o2220100#s are easy! ", java.util.Map.of(
                0, "But I promised..."
        ));
        sm.sayNext("If it looks like it'll be too dangerous, we can just run away. Come on! Let's at least test if we can elminate a #o2220100#! What do you say? Please!");
        if (!sm.askAccept("")) {
            sm.sayOk("Master is a chicken, a scaredy cat, a wussy... What else is there?");
            return;
        }
        sm.sayNext("Woohoo! Really? Let's hurry! According to what that female human said, the #r#o2220100#s#k seem to be in a place called the #b#m106010100##k! Let's hurry and go there!");
        sm.sayBoth("If we get #b20 #t4000009#s#k and give it to that female human named #p1010100#, she'll be so surprised! We'll be heroes that helped a person in trouble!");
        sm.forceStartQuest(22521);
    }

    @Script("q22521e")
    public static void q22521e(ScriptManager sm) {
        // Become a Hero (22521 - end)
        sm.sayNext("Evan? You haven't returned to the farm yet? Huh? This...oh my. So many #t4000009#s! You didn't get these yourself did you?");
        sm.sayBoth("I told you to not go near the #m106010100# because it's dangerous! You should've listened to me!");
        sm.sayBoth("But since you've got so many of these, I guess you can handle the #o2220100#s. I guess I can't even really yell at you... Sheesh, I'll let it go this time but please don't break your promises in the future. Okay?");
        sm.sayBoth("In any case, you really are very strong for your age. What's your secret? You might be as strong as the Guards at the Dungeon entrances. They might even come ask you for help. Maybe when you're level 20 or so?");
        sm.removeItem(4000009, 20);
        sm.addExp(2950);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2200), 1);
        sm.forceCompleteQuest(22521);
    }

    @Script("q22522s")
    public static void q22522s(ScriptManager sm) {
        // Delivering Maya's Porridge (22522 - start)
        sm.sayNext("Evan, if you're not too busy, do you think I can ask you for a favor? I made some porridge with the #t4000009#s you brought me. Will you deliver it to #p1012101# before it gets cold?");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmm... Are you busy with something? But still, helping out a friend like #p1012101# would be nice...");
            return;
        }
        sm.sayOk("You do know where #b#p1012101##k is, right? The two of you are pretty close, right? She is at #b#m100000001##k. It's the house on the right side of town.");
        if (!sm.addItem(4032458, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22522);
    }

    @Script("q22522e")
    public static void q22522e(ScriptManager sm) {
        // Delivering Maya's Porridge (22522 - end)
        sm.sayNext("Cough, cough! Hi Evan, how are you? It's been a long time! I haven't seen you since my field trip to the Farm. Heh, I've been too sick to go outside... Is that lizard your pet? He's so cute...");
        sm.sayBoth("So what brings you to #m100000000#? An errand? Or are you here to hang out with #p1012108#? Huh? #p1010100# asked you to deliver this to me? Wow, its Blue Mushroom Porridge!");
        sm.sayBoth("Thank you so much for delivering it. Although the color looks weird, it really tastes good. Hehe, a lot of people hate it but it tastes great to me.");
        sm.removeItem(4032458, 1);
        sm.addExp(300);
        sm.forceCompleteQuest(22522);
    }

    @Script("q22523s")
    public static void q22523s(ScriptManager sm) {
        // Investigating Strange Mushrooms (22523 - start)
        sm.sayNext("Hey, are you Evan? I'm #p1040000#, a Guard in charge of the #m100000000# Dungeon Entrance. I heard a lot about you from #p1010100#. She told me that you're strong for your age... Is that true? If so, I have a favor I'd like to ask.");
        sm.sayBoth("You may have heard from #p1010100# already, but the #o2220100#s near the #m106010100# area have become very strange. Some #o2220100#s appear to be crying. They also attack people who pass by. I think a detailed investigation is needed. Can you help?");
        if (!sm.askAccept("")) {
            sm.sayOk("Oh...is it too difficult? I was hoping you could help because I was told that you're strong enough to defeat #o2220100#s... I guess I'll have to find someone else...");
            return;
        }
        sm.sayNext("Luckly, there is a scholar named #p1012111# in #m100000000# who studies Mushrooms. I think you may be able to ask him for help. Please bring him #b40#k #b#t4000009#s#k that he can use as samples in his study.");
        sm.sayBoth("Oh...and are you sure that the lizard next to you isn't dangerous? Maybe you should leash him so he can't attack or bite people. What? Hey, don't take offense. It's always smart to restrain your pets.");
        sm.forceStartQuest(22523);
    }

    @Script("q22523e")
    public static void q22523e(ScriptManager sm) {
        // Investigating Strange Mushrooms (22523 - end)
        sm.sayNext("Oh? Aren't you Evan? Is there something I can do for you? Huh? These are #t4000009#s. #p1040000# wants me to investigate the #o2220100#s?");
        sm.sayBoth("The #o2220100#s are becoming violent? Could it be related to that other incident...?");
        sm.removeItem(4000009, 40);
        sm.addExp(2500);
        sm.forceCompleteQuest(22523);
    }

    @Script("q22524s")
    public static void q22524s(ScriptManager sm) {
        // Strange Puppet (22524 - start)
        sm.sayNext("I'm sorry. I was thinking about something else. Similar things have been happening around #m100000000# lately. Have you heard of the #o9300274#s? ");
        sm.sayBoth("They used to be normal mushrooms but they turned strange. Your situation sounds similar. Luckily we know the solution to this problem thanks to someone who helped us before.");
        sm.sayBoth("It you hunt about #r100 #o2220100#s#k and recover the #b#t4032459##k that they drop, it cools down the situation. Can you recover the #t4032459# and deliver it to #p1040000#?");
        if (!sm.askAccept("")) {
            sm.sayOk("Is it too hard? I heard rumors that you eliminated #o2220100#s... Hmm. Maybe #p1010100# was mistaken.");
            return;
        }
        sm.sayOk("Okay, please do your best. #o2220100#'s puppet probably won't drop that easily. You're going to need patience.");
        sm.forceStartQuest(22524);
    }

    @Script("q22524e")
    public static void q22524e(ScriptManager sm) {
        // Strange Puppet (22524 - end)
        sm.sayNext("Oh, you're here. So what did #p1012111# say? Why are the #o2220100#s acting so weird? Huh? This is a...puppet?");
        sm.sayBoth("This puppet is what's causing the #o2220100# to change? That's difficult to believe, but since an expert is saying it...I guess I'll have to believe it. In any case, thanks.");
        sm.removeItem(4032459, 1);
        sm.addExp(2600);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        sm.forceCompleteQuest(22524);
        sm.forceStartQuest(22525);
    }

    @Script("q22525s")
    public static void q22525s(ScriptManager sm) {
        // Mike at the Perion Dungeon Entrance (22525 - start)
        sm.sayNext("Hmm... This seems doubtful but a Guard should always be prepared for even the smallest chance of danger. I better warn the other towns too. Evan, if you're not too busy, do you think you could help me out?");
        if (!sm.askAccept("")) {
            sm.sayOk("Oh, are you busy? All right then. I wonder if there is anyone that is headed to #m102000000#... Let me know if you change your mind.");
            return;
        }
        sm.sayOk("Gee, thanks! Please go to #m102000000# and tell #p1040001#, the Guard in charge of the #b#m106000300##k, this official notice. #bHigh level of possibilities for unprecedented phenomenon occuring in monster forces near towns. Observation requested!#k");
        sm.forceStartQuest(22525);
    }

    @Script("q22525e")
    public static void q22525e(ScriptManager sm) {
        // Mike at the Perion Dungeon Entrance (22525 - end - complex menu-based dialog)
        final int answer1 = sm.askMenu("Yawn...snore...wha..what? Yes sir! Atten-hut! No problems to report sir! Wait... I mean... You're just an adventurer. Huh? #p1040000# asked you to come here? What is it?", java.util.Map.of(
                0, "High rate of people for...",
                1, "High level of possibilites for...",
                2, "High chances of feasibility for..."
        ));
        if (answer1 == 0 || answer1 == 2) {
            if (answer1 == 0) {
                sm.sayOk("High rate of people for what? Are a lot of people getting sick? What? No?");
            } else {
                sm.sayOk("Chance of feasibility? What are you talking about? Can you just cut to the chase and tell me what #p1040000# wants to tell me?");
            }
            return;
        }
        final int answer2 = sm.askMenu("High level of possibilities for? For what? Is there a problem?", java.util.Map.of(
                0, "unprecedented phenomon occuring in monster forces near town",
                1, "unprecedented dancing of monster forces near town",
                2, "unprecedented lullabies being sung by monsters near town"
        ));
        if (answer2 == 1 || answer2 == 2) {
            if (answer2 == 1) {
                sm.sayOk("The monsters are dancing? Are they bored?");
            } else {
                sm.sayOk("If the monsters are singing lullabies, just sing along with them... Stop talking nonsense and tell me what's going on.");
            }
            return;
        }
        final int answer3 = sm.askMenu("Unprecedented phenomenon? So... What am I supposed to do?", java.util.Map.of(
                0, "Dance lessons requested!",
                1, "Singing lessons requested!",
                2, "Observation requested!"
        ));
        if (answer3 == 0 || answer3 == 1) {
            if (answer3 == 0) {
                sm.sayOk("You want me to teach the monsters how to dance? What the heck are you talking about?");
            } else {
                sm.sayOk("You want me to teach the monsters how to sing? Oh, my. I'm a terrible singer...");
            }
            return;
        }
        sm.sayNext("I see. Tell me the details. What's been happening in #m100000000#?");
        sm.sayBoth("Huh? Puppets are influencing monster behavior? Strange. Okay. There are no problems now, but I will let you know if something happens.");
        sm.addExp(1700);
        sm.forceCompleteQuest(22525);
    }

    @Script("q22526s")
    public static void q22526s(ScriptManager sm) {
        // Mike's Request (22526 - start)
        sm.sayNext("Hello, you're Evan right? #p1040000# tells me that you're pretty strong. Are you busy? No no, this has nothing to do with the puppets.");
        sm.sayBoth("The #m102000000# Guards are always short-handed. We would love to have you join the #b#m102000000# Guards so that you can help us maintain security#k ");
        sm.sayBoth("The work isn't too difficult. It's just clearing up the area near #m102000000# so that beginner adventurers don't get hurt. What do you think?");
        if (!sm.askAccept("")) {
            sm.sayOk("Eh? You're going to refuse without even thinking about it? The pay isn't much but there is a lot of pride that comes with it...");
            return;
        }
        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(This is sure to interfere with your training. Will #p1013000# agree to do it? Talk to #p1013000#.)#k");
        sm.forceStartQuest(22526);
    }

    @Script("q22526e")
    public static void q22526e(ScriptManager sm) {
        // Mike's Request (22526 - end)
        final int answer1 = sm.askMenu("What is it, master? What?! You're thinking about joining the #m102000000# Guards? What do the Guards to anyway?", java.util.Map.of(
                0, "(You explain the Guards' job briefly.)"
        ));
        sm.sayOk("Oh! Helping people is a good thing! Heroes shouldn't refuse such a request! Training is important but that doesn't mean we should ignore people in need! Great idea! Let's join the Guards!");
        sm.addExp(1200);
        sm.forceCompleteQuest(22526);
    }

    @Script("q22527s")
    public static void q22527s(ScriptManager sm) {
        // A Guard's First Assignment: Cleaning up Around the Dungeon (22527 - start)
        sm.sayNext("Are you going to join the #m102000000# Guards? You are?! This is great! I will now appoint you as a #m102000000# Guard! Since you're a little young, you can't be a full Guard, but I'll treat you the same as an official Guard.");
        sm.sayBoth("Now onto your first assignment! It's pretty simple. Clean up the Dungeon Entrance area by eliminating the #o2110200#s surrounding #m106000300#.  If it's too difficult, just tell me. Want to do it?");
        if (!sm.askAccept("")) {
            sm.sayOk("Well, it's okay if it's too hard. Let me think of something easier. Hmm... But this is a basic job that all Guards should be able to do...");
            return;
        }
        sm.sayOk("Great! Go on and eliminate #r100 #o2110200#s#k!");
        sm.forceStartQuest(22527);
    }

    @Script("q22527e")
    public static void q22527e(ScriptManager sm) {
        // A Guard's First Assignment: Cleaning up Around the Dungeon (22527 - end)
        sm.sayNext("Wow, you eliminated 100 #o2110200#s already? I was right about you. I thought you would do a good job. And oh, there is something I forgot to give you earlier.");
        sm.sayBoth("Here, this is the Honorary #m102000000# Guard Medal. It's not much but it should come in useful. Now please continue your good work. I'll call when I have a new assignment for you.");
        sm.addExp(2900);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        if (!sm.addItem(1142153, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22527);
    }

    @Script("q22528s")
    public static void q22528s(ScriptManager sm) {
        // A Guard's Second Assignment: Helping Beginner Adventurers (22528 - start)
        sm.sayNext("Oh, you're here! I have your second assignment. This time, it's even easier. Once in a while, beginner adventurers accidentally enter the Deep Valleys, which are the Warning Streets in #m102000000#. Your job is to help them. Do you think you can do it?");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmm. Let me know if you get more confidence.");
            return;
        }
        sm.sayOk("Okay, go patrol #bDeep Valley 1, 2, and 3 and help any lost Adventurers you find#k. Good luck.");
        sm.forceStartQuest(22528);
    }

    @Script("q22528e")
    public static void q22528e(ScriptManager sm) {
        // A Guard's Second Assignment: Helping Beginner Adventurers (22528 - end)
        final int answer1 = sm.askMenu("You helped a beginner adventurer? Wow... You really are great! I doubt even #p1040000# could do better.", java.util.Map.of(
                0, "Are you and #p1040000# brothers?"
        ));
        final int answer2 = sm.askMenu("Nah, but we get that all the time, just because we have the same job and our names end in the same letters. Since our faces are covered, some people think we're twins. Actually, I'm a lot better looking than #p1040000#.", java.util.Map.of(
                0, "(You want to check but you can't see through his helmet.)"
        ));
        sm.sayNext("Don't compare me to a rookie like #p1040000#. I'm much more experienced. That's why I'm stationed in #m102000000#, which is way more dangerous than #m100000000#. Hehehe. There was a time when I used to compete with Manji... Wait, nevermind.");
        sm.sayBoth("In any case, good job. I'll call you again if something else comes up.");
        sm.addExp(3400);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        if (!sm.addItem(1942000, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22528);
    }

    @Script("q22529s")
    public static void q22529s(ScriptManager sm) {
        // Helping Beginner Adventurer Christopher (22529 - start)
        final int answer1 = sm.askMenu("So thirsty... Is this the end for me? All of my dreams of discovering relics... All of my hopes...were they all for naught? Am I hallucinating? I see someone...", java.util.Map.of(
                0, "Are you okay? (Did he hurt his head?)"
        ));
        final int answer2 = sm.askMenu("Whoa! A real person! I'm saved! I knew it! The heavens would not be so cruel as to abandon a genius like me! Did Shuang send you here to look for me? Of course she did!", java.util.Map.of(
                0, "Um, no... I'm a #m102000000# Guard... Do you need help?"
        ));
        final int answer3 = sm.askMenu("My name is #p1022106#. I'm a member of the Relic Excavation Team. Just to let you know, I'm not lost. I just can't move! I'm too thirsty! I dropped my water bottle and spilled all my water.", java.util.Map.of(
                0, "Sure... (He must be lost.)"
        ));
        sm.sayNext("Like I told you, I can find my way! Bu...but if you can just bring me some water...no not water...um...#bjust 3 #t4032460#s#k, I would really appreciate it!");
        if (!sm.askAccept("")) {
            sm.sayOk("How can you refuse? Don't you feel sorry for me? If you just leave, it will be demise of one of the most brilliant geniuses of this century!");
            return;
        }
        sm.sayOk("You can get the #t4032460# from #rany stump#k around here. Of course it's not a good idea to attack dangerous ones like the  #o2130100#s. Not that I can't defeat a #o2130100#... I'm just too tired at the moment. Really!");
        sm.forceStartQuest(22529);
    }

    @Script("q22529e")
    public static void q22529e(ScriptManager sm) {
        // Helping Beginner Adventurer Christopher (22529 - end)
        sm.sayNext("The saa....aaap of a #o0130100#! Ca...can I drink this?");
        sm.sayBoth("Gulp gulp gulp!");
        sm.sayBoth("Aaah, that hit the spot!");
        sm.sayBoth("Thank you! I'm full of energy now! I can find my own way to the Excavation Site! Since I'm not lost, I will go as soon as my thirst is quenched! I'll be going really soon... Can you just tell me which way is North?");
        sm.removeItem(4032460, 3);
        sm.addExp(3100);
        sm.forceCompleteQuest(22529);
    }

    @Script("q22530s")
    public static void q22530s(ScriptManager sm) {
        // A Guard's Third Assignment: Maintaining Warning Signs (22530 - start)
        sm.sayNext("Evan, do you know what a Guard's most important job is? It's to prevent incidents before they happen. To do that, we've posted warning signs throughout #m102000000#.");
        sm.sayBoth("But we have to keep them up to date. Monsters often change their locations, so we have to continually update the signs. I'd like to check the information on the signs posted between #bEast Rocky Montain 1#k and #bEast Domain of Perion#k. Got it?");
        if (!sm.askAccept("")) {
            sm.sayOk("How could you refuse? It's really quite simple... Please rethink your decision.");
            return;
        }
        sm.sayOk("It seems complicated but it's not. Simply go find the five Warning Signs located in the section I mentioned and click on them to read them. All you have to do is to fix the mistakes.");
        sm.forceStartQuest(22530);
    }

    @Script("q22530e")
    public static void q22530e(ScriptManager sm) {
        // A Guard's Third Assignment: Maintaining Warning Signs (22530 - end)
        sm.sayNext("Wow, have you finished updating the Warning Signs? Great job! You probably now totally know your way around #m102000000#. #p1040000# still gets lost in #m100000000#...");
        sm.sayBoth("Thanks. I'll call you if there is something else to do. I'll also let you know if anything strange happens, like #p1040000# mentioned.");
        sm.addExp(3900);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        if (!sm.addItem(1952000, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22530);
    }

    @Script("q22531s")
    public static void q22531s(ScriptManager sm) {
        // A Guard's Fourth Assignment: Discovery of Strange Mushrooms (22531 - start)
        sm.sayNext("Evan, a phenomenon similar to what #p1040000# described has occurred in #m102000000#. To be specific, it's happening inside #m105040300# Dungeon.");
        sm.sayBoth("According to some adventurers who were exploring, there is something strange going on with the #r#o2230101#s#k in #b#m105050300##k. I think we're going to have to investigate this. Can you handle it?");
        if (!sm.askAccept("")) {
            sm.sayOk("Huh? This is a part of what a Gaurd must do. Will you still refuse? Hmm...this won't do. This just won't do.");
            return;
        }
        sm.sayOk("Then go into the Sleepywood Dungeon and eliminate about #r100 Annoyed #o2230101#s#k. If this is the same phenomenon that #p1040000# described, you should be able to discover a #bpuppet#k. Please bring it to me.");
        sm.forceStartQuest(22531);
    }

    @Script("q22531e")
    public static void q22531e(ScriptManager sm) {
        // A Guard's Fourth Assignment: Discovery of Strange Mushrooms (22531 - end)
        sm.sayNext("How did things go? Whoa, is that the puppet? It really looks strange...Let me take a closer look.");
        sm.sayBoth("How could a puppet cause all this...? I guess I should strengthen security around #m102000000#. I'll let you know if anything else happens.");
        sm.removeItem(4032461, 1);
        sm.addExp(5100);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        if (!sm.addItem(1962000, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22531);
    }

    @Script("q22532s")
    public static void q22532s(ScriptManager sm) {
        // A Guard's Fifth Assignment: Strange Wild Boars (22532 - start)
        sm.sayNext("Evan, there is another case of monsters acting strange. I thought it only happened to mushroom monsters, but this time it's the #o2230102#s! ");
        sm.sayBoth("I thought at first that the incidents weren't related, but there are too many similarities. Why don't you take care of this as well.");
        if (!sm.askAccept("")) {
            sm.sayOk("You are the only Guard who can take care of this! Please think about it!");
            return;
        }
        sm.sayOk("Good, I'm glad I can count on you. Go to #b#m101030001##k and eliminate about #r100 #o2230112#s#k. If you find #bpuppet#k, bring it to me.");
        sm.forceStartQuest(22532);
    }

    @Script("q22532e")
    public static void q22532e(ScriptManager sm) {
        // A Guard's Fifth Assignment: Strange Wild Boars (22532 - end)
        sm.sayNext("So what was the result of your investigation? Was it another puppet? Who is responsible for this?");
        sm.sayBoth("Because of the strange incidents lately, I've requested additional Guards. I probably won't have to keep pestering you to do things for us any more.");
        sm.sayBoth("But that doesn't mean that you are no longer an Honorary #m102000000# Guard. If ever we need your help, we'll be sure to call on you. Until then, please take good care of yourself.");
        sm.sayBoth("By the way, now that your deeds as a Guard are famous, there will probably be more people that recognize you. If someone asks for help, please try to help.");
        sm.removeItem(4032462, 1);
        sm.addExp(5750);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        if (!sm.addItem(1972000, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22532);
    }

    @Script("q22533s")
    public static void q22533s(ScriptManager sm) {
        // Please Catch the Thief (22533 - start)
        sm.sayNext("Oh, are you Evan? I've heard so much about you from #p1040001#. He told me what a great Guard you are. That's why I thought you could help me.");
        sm.sayBoth("My job is to collect herbs and make them into medicine. But recently, someone stole one of my herbs! It was a very rare and expensive herb! Do you think you can catch the thief for me?");
        if (!sm.askAccept("")) {
            sm.sayOk("I didn't think you would refuse. #p1040001# told me what a great person you are but I guess he was wrong.");
            return;
        }
        sm.sayNext("It's doubtful that there are thieves in #m105040300# so I'm certain that a thief from #m103000000# stole it! Can you go to #m103000000# to investigate and bring back my herb?");
        sm.sayBoth("How should go about the investigation? Er, shouldn't you know that? Maybe look around and search for a witness? Try talking to someone you know in #b#m103000000#... I don't know. That's YOUR job!");
        sm.forceStartQuest(22533);
    }

    @Script("q22533e")
    public static void q22533e(ScriptManager sm) {
        // Please Catch the Thief (22533 - end)
        sm.sayNext("You've returned! So who was the thief? What? You haven't caught the thief yet?");
        sm.sayBoth("It's hard for me to believe that the Thieves in #m103000000# aren't responsible... But since they are willing to put their honor on the line to help find the culprit, I will trust them for now. Now, please find the thief.");
        sm.addExp(7250);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2210), 1);
        sm.forceCompleteQuest(22533);
    }

    @Script("q22534s")
    public static void q22534s(ScriptManager sm) {
        // Kerning City Investigation: Alex (22534 - start - very complex with multiple quiz questions)
        final int answer1 = sm.askMenu("Hey, Evan, how are ya? Long time no see. What are you doing here? Have you run away from home, too? Or are you on an adventure?", java.util.Map.of(
                0, "(You tell him about becoming a Guard.)"
        ));
        final int answer2 = sm.askMenu("Wow, a Guard? That's pretty cool. You used to be so clumsy, so I'm kind of shocked. But what brings you to #m103000000#?", java.util.Map.of(
                0, "(You tell him about #p1061005#'s stolen herb.)"
        ));
        sm.sayNext("So you're looking for the herb thief in #m103000000#? Well, can I help? Actually, if you can give me some news about #b#m100000000##k, I'll tell you about someone who might know what's going on.");
        if (!sm.askAccept("")) {
            sm.sayOk("You don't want to? And I was going to help you since you are an old friend.");
            return;
        }
        sm.sayOk("News about #m100000000#... What should I ask you about first. It's been so long. Give me a second. Let me think.");
        sm.forceStartQuest(22534);
    }

    @Script("q22534e")
    public static void q22534e(ScriptManager sm) {
        // Kerning City Investigation: Alex (22534 - end - multiple quiz answers)
        sm.sayOk("Try talking to #b#p1052002##k down there. He know about all the back alley transactions. If the herb or whatever was being traded, he would know.");
        sm.addExp(3250);
        sm.forceCompleteQuest(22534);
    }

    @Script("q22535s")
    public static void q22535s(ScriptManager sm) {
        // Kerning City Investigation: JM (22535 - start)
        sm.sayNext("Do you have business with me? If you want something, trade something first. If you pay the price, I'll give you whatever information you want. I know everything about #m103000000#.");
        if (!sm.askAccept("")) {
            sm.sayOk("Are you refusing? Okay, then no trade. I doubt you can find anyone that knows as much as I do about #m103000000#.");
            return;
        }
        sm.sayOk("Oh, so a trade then, eh? Okay. Get me #b50 #t4000042#s#k. Then I'll give you some information. Ah, I will give you a freebie. #o2300100#s can be found in the subway, so go #bdeep inside the subway#k.");
        sm.forceStartQuest(22535);
    }

    @Script("q22535e")
    public static void q22535e(ScriptManager sm) {
        // Kerning City Investigation: JM (22535 - end)
        final int answer = sm.askMenu("Ah, you brought the #t4000042#s. Good. So what do you want to know? Huh? Herb dealing? Start from the beginning...", java.util.Map.of(
                0, "(You tell him about #p1061005#'s situation.)"
        ));
        sm.sayNext("Hmm... But there hasn't been a single person in #m103000000# who has been trading herbs. Potions are way more convenient than herbs these days.");
        sm.sayBoth("I'm sorry that the information you got isn't as great as what you traded me for it. To show you how sorry I am, let me tell you this. The person who stole the herb may be want to use it rather than sell it. That would make it tough for me to find him or her.");
        sm.sayBoth("But there is one person who might be able to find the culprit. #p1052103#. She has a fabulous nose. She would have noticed someone who walked by smelling like herbs. Try asking #b#p1052103##k.");
        sm.removeItem(4000042, 50);
        sm.addExp(7250);
        sm.forceCompleteQuest(22535);
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

    @Script("q22537s")
    public static void q22537s(ScriptManager sm) {
        // Investigating the Biology of Dragons (22537 - start)
        final int answer1 = sm.askMenu("Ma...master...", java.util.Map.of(
                0, "Whoa! #p1013000#, what happened to you? You're so much bigger! Your horns look sharper too. You look great, but what happened? Do Dragons always change so suddenly?"
        ));
        final int answer2 = sm.askMenu("I don't know master! Like I told you before, the only thing I know is that I am a Dragon and that we have a pact! Wha...what happened to me? My cuteness has decreased...", java.util.Map.of(
                0, "Come to think of it, we really don't know very much about Dragons. This just won't do! Let's find out more about Dragons!"
        ));
        final int answer3 = sm.askMenu("Master? Are you worried about me?", java.util.Map.of(
                0, "Yes! What if you suddenly fell asleep in the air and broke someone's roof or something? You know how much trouble I'd be in?"
        ));
        final int answer4 = sm.askMenu("Wait, you're worried about the roof, and not me?!", java.util.Map.of(
                0, "Hehehe. I wonder how we can find out more about Dragons? I've heard there are other Dragons in Maple World but I've never seen one. Who would know about Dragons?"
        ));
        final int answer5 = sm.askMenu("Way to change the topic! Well, whatever. There must be someone who knows... I mean, you knew about Dragons before you ever saw one, right?", java.util.Map.of(
                0, " I knew about Dragons before seeing one... That's it! Books! Reading about Dragons in books will let me learn about Dragons without having to find one in person!"
        ));
        final int answer6 = sm.askMenu("Wow! Master! You're so smart! But do you have any books about Dragons?", java.util.Map.of(
                0, "I don't, but someone must. The person with the most books in our town... Yes, #p1012109#! #p1012109# reads lots of books so he must have a book about Dragons!"
        ));
        sm.sayNext("All right! Let's hurry to that human named #p1012109#!");
        if (!sm.askAccept("")) {
            sm.sayOk("Huh? Why? Do you not get along with that human named #p1012109#? But I really want to know more about my race...");
            return;
        }
        sm.sayOk("Let's hurry back to #b#m100000000##k where you used to live!");
        sm.forceStartQuest(22537);
    }

    @Script("q22537e")
    public static void q22537e(ScriptManager sm) {
        // Investigating the Biology of Dragons (22537 - end)
        sm.sayOk("Huh? Evan? Hey, what are you doing here? I heard that you've been helping a lot of people. Hehe, are you here to help me too?");
        sm.addExp(2000);
        sm.forceCompleteQuest(22537);
    }

    @Script("q22538s")
    public static void q22538s(ScriptManager sm) {
        // Dragon Types and Characteristics (Vol. I) (22538 - start)
        sm.sayNext("Yeah, I do have a book about Dragons. It's called #t4161049#. Why? Are you interested in Dragons, too? Hmm, do you want to borrow it?");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmm... So you weren't very interested? Then nevermind. Let me know if you change your mind.");
            return;
        }
        sm.sayNext("Wow, you really do have a lot of interests in dragons. You're even raising a lizard that looks like a Dragon... Here, did you get the book? I haven't been able to read all of it yet so please #bread it quickly and give it back to me#k. Okay?");
        if (!sm.addItem(4161049, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22538);
    }

    @Script("q22538e")
    public static void q22538e(ScriptManager sm) {
        // Dragon Types and Characteristics (Vol. I) (22538 - end)
        final int answer = sm.askMenu("You finished the book already? Wow, you're a fast reader. Did the book contain the information you needed?", java.util.Map.of(
                0, "No, it wasn't in there. Do you have any other books?"
        ));
        sm.sayNext("I see, the book I let you borrow is just Volume 1. Volume 2 might contain more information about Dragon types, but I don't have that book...");
        sm.removeItem(4161049);
        sm.addExp(1000);
        sm.forceCompleteQuest(22538);
    }

    @Script("q22539s")
    public static void q22539s(ScriptManager sm) {
        // Knowledge About Dragons 1 (22539 - start)
        sm.sayNext("Master, master! You just got a book about Dragons, didn't you? Let me see it! Ugh... I can't read human letters. Please read it and tell me what it says!");
        if (!sm.askAccept("")) {
            sm.sayOk("Huh? Don't you want to explain it to me? Fine, then can you teach me how to read human letters? This could take a while...");
            return;
        }
        sm.sayOk("I wonder what is written in it. I can't wait to hear about my race!");
    }

    @Script("q22539e")
    public static void q22539e(ScriptManager sm) {
        // Knowledge About Dragons 1 (22539 - end - multi-choice quiz)
        final int answer1 = sm.askMenu("Master, master! Did you finish reading the book? Please tell me! What does the book talk about?", java.util.Map.of(
                0, "Dragon Culture",
                1, "Dragon Attack and Defense",
                2, "Dragon History and Traditions",
                3, "Dragon Types and Characteristics"
        ));
        if (answer1 != 3) {
            String response = switch (answer1) {
                case 0 -> "Dragon culture? That does sound pretty interesting, but I'm more interested about knowing what kind of Dragon I am...";
                case 1 -> "Attack and defense? I know that stuff without having to read about it. I think it must be innate.";
                case 2 -> "Dragon history and traditions... That sounds so boring... Yawn.";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        final int answer2 = sm.askMenu("Oh, Dragon Types and Characteristics? That's perfect! It'll tell me what kind of Dragon I am! Since you've read it, tell me what kind of Dragon I am master!", java.util.Map.of(
                0, "Blue Dragon",
                1, "Gold Dragon",
                2, "Black Dragon",
                3, "Can't Be Known"
        ));
        if (answer2 != 3) {
            String response = switch (answer2) {
                case 0 -> "Blue Dragon? Hmm... I guess my scales are kind of bluish...but I don't have any ice attributes. Read the book again.";
                case 1 -> "Gold Dragon? Only a really small part of me is gold. I don't think that's it, master. Read the book more closely.";
                case 2 -> "Black Dragon! Is that what I am? Hmm... But there is a small part of me that is gold colored. That can't be it. Please read it more closely, master.";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        final int answer3 = sm.askMenu("Huh? What do you mean can't be known... Why? Am I not a Dragon? What am I then, master?", java.util.Map.of(
                0, "Special Dragon",
                1, "Wyvern",
                2, "Drake",
                3, "Alien Being"
        ));
        if (answer3 != 0) {
            String response = switch (answer3) {
                case 1 -> "Wyvern? But Wyverns don't have any front feet. Am I a mutant Wyvern? Read it more closely master!";
                case 2 -> "Drake? I don't think I look as dumb as a Drake... Really? Please tell me it's not true!";
                case 3 -> "Ahhh, so was I an alien being all this time!";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        sm.sayNext("Special Dragon? What's a special Dragon? Tell me more master!");
        sm.sayOk("Huh? This book doesn't contain information about special Dragons? It's in another book called Dragon Types and Characterics (Vol. II)? Master, let's go look for it! That book must contain information about me!");
        sm.addExp(5900);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        sm.forceCompleteQuest(22539);
    }

    @Script("q22540s")
    public static void q22540s(ScriptManager sm) {
        // Ellinia Magic Library (22540 - start)
        sm.sayNext("To tell you the truth, this #t4161049# isn't mine. It's a book I borrowed from #m101000003# in #m101000000#. I wanted to buy it, but I couldn't find it because it was so rare.");
        sm.sayBoth("So if you go to #b#m101000000##k, you'll probably find #t4161050#, the sequel to this one. If there is anything you'd like to know about Dragons, borrow the book in #b#m101000003##k.");
        if (!sm.askAccept("")) {
            sm.sayOk("Hm, I suppose it's a bit of a hassle.");
            return;
        }
        sm.sayOk("A lot of people go to #m101000003# in #m101000000# to find that book, so you'll have to hurry if you don't want someone else borrows it first. Rush there! I'll see you later then, Evan.");
        sm.forceStartQuest(22540);
    }

    @Script("q22540e")
    public static void q22540e(ScriptManager sm) {
        // Ellinia Magic Library (22540 - end)
        sm.sayOk("Welcome to the #m101000000# #m101000003#. Hm, aren't you a strange one... You have a lot of MP but no Magic ATT. I've never met anyone like you.");
        sm.addExp(3000);
        sm.forceCompleteQuest(22540);
        sm.forceStartQuest(22541);
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

    @Script("q22541e")
    public static void q22541e(ScriptManager sm) {
        // Where's the Book? 1 (22541 - end)
        final int answer1 = sm.askMenu("Ah, I've finished reading and now I'm bored. Hm? A book? Ah, you mean #t4161050#, yes? I thought I'd be able to fly with wings like a Dragon if I read that book, but I was wrong, so I stopped reading. You need it?", java.util.Map.of(
                0, "Yes. I'll return it for you so hand it over if you're done with the book."
        ));
        final int answer2 = sm.askMenu("#b#p1002100##k in #b#m104000000##k needed the book so I gave it to her. She said she'd return it for me. So go see #m104000000# because I don't have it anymore.", java.util.Map.of(
                0, "(Ugh, it's more work than you thought it'd be.)"
        ));
        final int answer3 = sm.askMenu("Wait, what is that thing flying next to you? That's a strange-looking lizard. Whoa, it flies too? Impressive. Can you let me borrow that lizard? I want to study it... Just for a little while...", java.util.Map.of(
                0, "Absolutely not!"
        ));
        sm.sayOk("Fine, you meanie... Fine, fine. I think flying with small wings looks silly anyway.");
        sm.addExp(2000);
        sm.forceCompleteQuest(22541);
    }

    @Script("q22542s")
    public static void q22542s(ScriptManager sm) {
        // Where's the Book? 2 (22542 - start)
        sm.sayNext("Are you here to ask me about my wonderful and impressive concoction? No? A book? Oh, #t4161050#? I thought, with that book, I'd be able to concoct something that would give me a Dragon's power, but I was wrong.");
        sm.sayBoth("I wasn't going to read it anyway, so I gave it to #p1002001# since he wanted it. He even told me he'd return the book himself, so if anything, #p1002001# would have it. Ask #b#p1002001##k.");
        if (!sm.askAccept("")) {
            sm.sayOk("Why are you asking me so many questions when you're not even that curious? ");
            return;
        }
        sm.sayOk("Oh, you have a strange lizard as a pet. Could I pet it? Oh my, it looks scary. I think I'll just stand back and observe.");
        sm.forceStartQuest(22542);
    }

    @Script("q22542e")
    public static void q22542e(ScriptManager sm) {
        // Where's the Book? 2 (22542 - end)
        sm.sayNext("Ah, my face itches. Hm? I haven't seen you here before. What can I do for you? I wasn't planning on going out to the sea for a while... Hm? A book? Are you talking about #t4161050# by any chance? If you are, I don't have it.");
        sm.sayBoth("A man of the sea like me has nothing to do with Dragons. I didn't borrow it for me. I borrowed it for #p1012101# since she's too weak to go anywhere. I already sent it to #b#m100000000##k, so if you want it, talk to #b#p1012101##k.");
        sm.sayOk("Anyway, what is that weird pet you've got there? Not too long ago, I saw an adventurer with a Jr. Balrog! People are starting to travel with some strange creatures...");
        sm.addExp(1500);
        sm.forceCompleteQuest(22542);
    }

    @Script("q22543s")
    public static void q22543s(ScriptManager sm) {
        // Where's the Book? 3 (22543 - start)
        sm.sayNext("Evan, are you here on an adventure? Huh? A book? Oh, you mean #t4161050#. I'm sorry but I've given it to someone else because it was too hard for me to read. I feel bad because #p1002001# went out of his way to send it to me. Who has the book now, you ask?");
        sm.sayBoth("I gave it to #p1012110#. I don't know if a kid like #p1012110# can understand anything written in that book, but oh well... #p1012110# should have it, so go see #b#p1012110##k in #b#m100010000##k if you really need that book.");
        if (!sm.askAccept("")) {
            sm.sayOk("Hm? It isn't all that important that you find the book? Then why don't you hang out with me for a bit?");
            return;
        }
        sm.sayOk("In any case, I think you've changed a lot, Evan. I don't mean that in a bad way. It's a compliment. You seem more confident and mature... You were always a bright one, but more so now.");
        sm.forceStartQuest(22543);
    }

    @Script("q22543e")
    public static void q22543e(ScriptManager sm) {
        // Where's the Book? 3 (22543 - end)
        sm.sayNext("Who are you? I may be bored, but I won't play with a stranger. What? A book? Hm... #t4161050#? I don't have it. I gave it to my mom because I thought she'd need it.");
        sm.sayBoth("My mom? Do you know Dr. #p1032104# in #m101000000#? She conducts research on organisms and she's interested in Dragons. If you really want to find the book, go to #b#m101000000##k and talk to Dr. #b#p1032104##k.");
        sm.sayOk("Hm... Haven't you gone to #b#p1012110##k yet? Ah, you probably don't know #p1012110# very well. She's the kid that was brought  to #m100000000# not too long ago. Maybe it's because she doesn't have any friends. Whatever the reason, she's always sitting alone in #b#m100010000##k.");
        sm.addExp(1500);
        sm.forceCompleteQuest(22543);
    }

    @Script("q22544s")
    public static void q22544s(ScriptManager sm) {
        // Where's the Book? 4 (22544 - start)
        sm.sayNext("Is there something I can do for you? Hm? #p1012110# sent you? Has my kid done something bad? Oh, then what brings you here? A book? Oh, you mean #t4161050#, correct? ");
        sm.sayBoth("I sent it to my scholarly master who is conducting research on fossils. He's been trying to idenitify a fossil he recently discovered and thought it might be a Dragon fossil. If you want the book, please visit my master.");
        if (!sm.askAccept("")) {
            sm.sayOk("I thought you needed that book. I guess not.");
            return;
        }
        sm.sayNext("My master is a renowned scholar in fossilology. His name is Dr. #b#p1022006##k. He is always roaming outside. He should be near the #bEast Rocky Mountain in #m102000000##k right about now. ");
        sm.sayOk("That's one unique creature you've got there. It looks like a lizard but looking at its bone structure, it's obvious that it isn't a lizard. Could I just... Oh, I'm sorry. I got carried away. Anyway, good luck.");
        sm.forceStartQuest(22544);
    }

    @Script("q22544e")
    public static void q22544e(ScriptManager sm) {
        // Where's the Book? 4 (22544 - end)
        sm.sayOk("Whoa! Wha... What is that? What is that strange creature? A lizard? I don't care if it's a lizard or a salamander! Get it out of here. It's gross! Just tell me why you are here! ");
        sm.addExp(3000);
        sm.forceCompleteQuest(22544);
    }

    @Script("q22545s")
    public static void q22545s(ScriptManager sm) {
        // Where's the Book? 5 (22545 - start)
        sm.sayNext("I have the book you're looking for, but I don't feel comfortable taking it out. Just a while ago, I was attacked by a #o3210100# while reading the book #p1032104# sent.");
        sm.sayBoth("Running away is easy to do, but if the #o3210100# sets the book on fire, then what do you do? Could you please eliminate the #o3210100#s first before I take out the book?");
        if (!sm.askAccept("")) {
            sm.sayOk("Hm... I can't take the book out if you don't eliminate the #o3210100#s. I don't feel safe.");
            return;
        }
        sm.sayOk("Those dangerous #r#o3210100##ks will be in #bThe Burnt Land#k. You must go even deeper than #b#m106000100##k. Eliminate #r120 of them#k but be careful. I'll wait here.");
        sm.forceStartQuest(22545);
    }

    @Script("q22545e")
    public static void q22545e(ScriptManager sm) {
        // Where's the Book? 5 (22545 - end)
        sm.sayOk("Wow, did you defeat all the #o3210100#s? Hold on a minute. I hid the book so it wouldn't get burned... But where did I hide it?");
        sm.addExp(8900);
        sm.forceCompleteQuest(22545);
    }

    @Script("q22546s")
    public static void q22546s(ScriptManager sm) {
        // Dragon Types and Characteristics (Vol. II) (22546 - start)
        sm.sayNext("Thank you for getting rid of those #o3210100#s. I don't need this #t4161050# anymore so #byou take the book and read it, and then return it to the #m101000003# in #m101000000##k when you're done. ");
        if (!sm.askAccept("")) {
            sm.sayOk("Hm? Didn't you defeat the #o3210100#s because you needed the book? If you don't want it, I'll hold on to it a little longer and read a bit more. Let me know if you change your mind.");
            return;
        }
        sm.sayNext("I hope there isn't a late fee or anything like that... No way, I guarded this book with my life! They couldn't possibly ask for more.");
        if (!sm.addItem(4161050, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22546);
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

    @Script("q22547s")
    public static void q22547s(ScriptManager sm) {
        // Knowledge about the Dragon 2 (22547 - start)
        final int answer1 = sm.askMenu("Master, master! Did you find the book? Do you think the book has information on my race?", java.util.Map.of(
                0, "I'm sure of it!"
        ));
        final int answer2 = sm.askMenu("I... I agree... But why am I getting so nervous? I've never seen any other Dragons before. I wonder if there are pictures?", java.util.Map.of(
                0, "If we discover where your race lives, want to go visit?"
        ));
        final int answer3 = sm.askMenu("Huh? Really?", java.util.Map.of(
                0, "Sure! We should go visit if we find out where they live. After all, they are your kin!"
        ));
        final int answer4 = sm.askMenu("Really? You really promise to go with me, master? You promise? You can't take it back if you promise!", java.util.Map.of(
                0, "Yes, hehe, I promise."
        ));
        sm.sayOk("You promised! Yay, you are the best master ever! Now, open the book. See if you can find my race!");
    }

    @Script("q22547e")
    public static void q22547e(ScriptManager sm) {
        // Knowledge about the Dragon 2 (22547 - end - multi-quiz)
        final int answer1 = sm.askMenu("Did you find it? Did the book have any information on my race? Huh? Huh? Tell me, master! What type of Dragon am I?", java.util.Map.of(
                0, "Serpent Dragon",
                1, "Onyx Dragon",
                2, "Mutant Dragon"
        ));
        if (answer1 != 1) {
            String response = switch (answer1) {
                case 0 -> "A Serpent Dragon? Hm, that doesn't sound familiar at all. Are you sure, master? Check again!";
                case 2 -> "Mu... Mutant?! I'm a Mutant? Am I really a Mutant Dragon?";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        final int answer2 = sm.askMenu("An Onyx Dragon? Wow, that sounds majestic! Haha, but of course! That's the race I belong to. So what are some characteristics of Onyx Dragons?", java.util.Map.of(
                0, "They become whole after they make a pact.",
                1, "They are extremely strong and intelligent.",
                2, "They have three heads."
        ));
        if (answer2 != 0) {
            String response = switch (answer2) {
                case 1 -> "Ah, I'm as strong and intelligent as can be because I'm an Onyx Dragon! Right?";
                case 2 -> "Three heads...? But I only have one head! Will I grow two more when I'm older? Ewww, that's gross.";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        final int answer3 = sm.askMenu("Ah, that's why I was able to meet you and awaken. Tell me more about the pact.", java.util.Map.of(
                0, "It's a slave pact, where the Dragon exploits a Human.",
                1, "It's a marriage between a Dragon and a Human!",
                2, "It's a bonding of spirit between a Human and a Dragon destined to enter that pact."
        ));
        if (answer3 != 2) {
            String response = switch (answer3) {
                case 0 -> "Oh, is that so? But I've never thought of you as a slave, master. Please, master... Don't leave me!";
                case 1 -> "Marriage! Master! Are we in love? Shall I call you honey from now on?";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        final int answer4 = sm.askMenu("More importantly, where do the Onyx Dragons live now? Where should we travel to meet my family of Onxy Dragons?!", java.util.Map.of(
                0, "Well..."
        ));
        final int answer5 = sm.askMenu("Well what? Is that not in the book?", java.util.Map.of(
                0, "It's in the book, but..."
        ));
        final int answer6 = sm.askMenu("I don't get it. If it's in the book, how come you don't know where they are? Tell me, master!", java.util.Map.of(
                0, "The... The Onyx Dragons have gone extinct..."
        ));
        sm.sayNext("What?! Extinct? No way... I'm still alive! I can't believe it. Did all of my family die? Then who am I? I don't understand.");
        sm.sayOk("Ugh, nothing is certain. It's just a book, right? It doesn't mean it has to be true! Let's keep searching for information on Dragons, master! I am determined to find my race!");
        sm.addExp(6900);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        sm.forceCompleteQuest(22547);
    }

    @Script("q22548s")
    public static void q22548s(ScriptManager sm) {
        // Clue about the Thief (22548 - start - auto-start)
        sm.sayNext("You've waited so long, Evan. I've finally found the clue about the culprit who stole the #t4032464#! Finally, #m103000000# will clear its name! Or soon...because there seems to be one more problem.");
        sm.sayBoth("I had one of my underlings hide out #b#m105040300##k to find the clue. He's the go-to person for a stakeout but he's so weak... As he was returning to #m103000000# he was attacked by a #o3110100# and lost the document containing all the clues.");
        sm.sayBoth("If you're not too busy, will you find that document for me? Just eliminate some #o3110100# in the swamp area to find it. Defeating a few #o3110100#s should be a breeze for you. What do you say?");
        sm.sayOk("Go to the swamp and eliminate some #r#o3110100##ks and find the #b#t4032463##k. That document will clear #m103000000# of this false charge!");
        sm.forceStartQuest(22548);
    }

    @Script("q22548e")
    public static void q22548e(ScriptManager sm) {
        // Clue about the Thief (22548 - end)
        sm.sayOk("Oh, you've brought the #t4032463#! You probably won't be able to read it since it's in a code that only Thieves can read. Here, let me see it. I'll be able to find out who is responsible for all of this!");
        sm.addExp(10900);
        sm.removeItem(4032463);
        sm.forceCompleteQuest(22548);
    }

    @Script("q22549s")
    public static void q22549s(ScriptManager sm) {
        // The Culprit is in the Dungeon (22549 - start)
        sm.sayNext("Hm, good. I found the suspect! The culprit is in the dungeon. According to the document, the culprit who stole the document was seen heading towards #m105070300#. There must be a hideout somewhere near there.");
        sm.sayBoth("A puppet stole the herbs? Ha. That makes no sense! I suppose that's not important.");
        sm.sayBoth("Anyway, the culprit's hideout must be in Sleepy Dungeon #b#m105070300##k! Go catch the culprit! Please recover the honorable name of #m103000000#!");
        if (!sm.askAccept("")) {
            sm.sayOk("What? You've accused the people of #m103000000# and now you refuse to help? Where is your heart, huh?");
            return;
        }
        sm.sayOk("The culrpit is in the dungeon! That's something I've been wanting to say for a long time now, haha.");
        sm.forceStartQuest(22549);
    }

    @Script("q22549e")
    public static void q22549e(ScriptManager sm) {
        // The Culprit is in the Dungeon (22549 - end)
        final int answer1 = sm.askMenu("...", java.util.Map.of(
                0, "(An ugly wooden puppet that's broken and worn out lies on the floor. There is nothing else here. Could the culprit have gone somewhere?)"
        ));
        final int answer2 = sm.askMenu("...", java.util.Map.of(
                0, "(Anyway, this is the ugliest puppet ever. Who could have brought this ugly thing here?)"
        ));
        final int answer3 = sm.askMenu("(Flinch)", java.util.Map.of(
                0, "(Hm? The puppet flinched. Was that your imagination? Go over and give it a nudge.)"
        ));
        final int answer4 = sm.askMenu("Yikes! Don't touch me!", java.util.Map.of(
                0, "(The puppet talks!)"
        ));
        sm.sayOk("Intruder! How dare you enter my master's cave and try to steal me! I will not forgive you for this! But don't hit me. I can't fight. I'm not a battle puppet.");
        sm.addExp(2500);
        sm.forceCompleteQuest(22549);
        sm.forceStartQuest(22550);
    }

    @Script("q22550s")
    public static void q22550s(ScriptManager sm) {
        // Puppet Caring for his Master 1 (22550 - start)
        final int answer1 = sm.askMenu("Who, who are you? My job is to guard my master's cave, though there is nothing to steal here and I can't fight at all!", java.util.Map.of(
                0, "Did you steal #p1061005#'s Herb?"
        ));
        final int answer2 = sm.askMenu("Eeeek! Are you a policeman? I'm so sorry. I'll never do it again. I'm sorry I stole it! I knew I'd get caught someday, but I didn't expect it to be so soon. But I cannot return the Herb!", java.util.Map.of(
                0, "Why can't you return the Herb?"
        ));
        final int answer3 = sm.askMenu("My master who created me was injured while battling his enemies, and I needed the Herb to make medicine to heal his wound. Potions would have been best, but I didn't have any money to buy them. I'm so sorry. Please forgive me.", java.util.Map.of(
                0, "(How could you not forgive this poor, pitiful puppet...?)"
        ));
        sm.sayNext("Please? My master's wound has not healed yet... I'm so sorry. And I'm so ashamed to ask you, but could you donate some potions for my master if I return the Herb? Please? *sniff sniff* I beg you.");
        if (!sm.askAccept("")) {
            sm.sayOk("Wahhhh, the world is such a cruel place. I'm sorry, master. No one is willing to help us.");
            return;
        }
        sm.sayOk("Thank you! You are a wonderful, wonderful person! Please find me #b20 #t2000002#s#k and #b30 #t2000003#s#k! *sniff sniff* I can finally heal my master!");
        sm.forceStartQuest(22550);
    }

    @Script("q22550e")
    public static void q22550e(ScriptManager sm) {
        // Puppet Caring for his Master 1 (22550 - end)
        sm.sayNext("You've brought the potions! I don't know how to thank you for bringing me these precious potions. I'm so touched. Thank you, on behalf of my master, for helping us! ");
        sm.sayOk("Here, I'll return the Herb as I promised. Please hold on a minute.");
        sm.removeItem(2000002, 20);
        sm.removeItem(2000003, 30);
        sm.addExp(5450);
        sm.forceCompleteQuest(22550);
        sm.forceStartQuest(22551);
    }

    @Script("q22551s")
    public static void q22551s(ScriptManager sm) {
        // The Returned Herbs (22551 - start)
        sm.sayNext("Here is the Herb I stole. I've already used a little of it, but no one will be able to tell. Please return #bthe Herb to its owner#k. I'm sorry for the inconvenience I've caused. Please apologize to the owner for me.");
        if (!sm.askAccept("")) {
            sm.sayOk("Hm? Don't you need the Herb back? If not, can I keep it?");
            return;
        }
        sm.sayNext("I'll never do anything like this ever again. You're a lifesaver. Thank you so much for your help!");
        if (!sm.addItem(4032464, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22551);
        sm.forceStartQuest(22552);
    }

    @Script("q22551e")
    public static void q22551e(ScriptManager sm) {
        // The Returned Herbs (22551 - end)
        sm.sayNext("It's been a long time, Evan. Have you heard anything about my Herb? I don't want to rush you, but there isn't anything else for me to do but blame #m103000000# for my stolen Herb. What? You found it?");
        sm.sayOk("Oh, that's it! That's definitely my Herb! Wait, let me take a look!");
        sm.removeItem(4032464);
        sm.addExp(5450);
    }

    @Script("q22552s")
    public static void q22552s(ScriptManager sm) {
        // Kerning City's Honor Restored (22552 - start)
        final int answer = sm.askMenu("A tiny bit of my Herb is missing, but I suppose I can live with that. I was ready to give up, but I'm so glad you found it! ", java.util.Map.of(
                0, "I only managed to find your Herb because #p1052103# in #m103000000# helped me."
        ));
        sm.sayNext("Is that right? I thought it was the Thieves in #m103000000# who stole it. I'm so embarrassed. I shouldn't have jumped to conclusion like that. I'm sorry. Will you tell that girl, #p1052103#, that I am truly sorry?");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmmm, if you don't tell her, #p1052103# in #m103000000# will still think that I am falsely accusing the Thieves. ");
            return;
        }
        sm.sayOk("Thank you for your help. I know it isn't easy to help someone, especially when you don't really know that person... You're even more wonderful than #p1040001# said. Thank you again.");
        sm.forceStartQuest(22552);
    }

    @Script("q22552e")
    public static void q22552e(ScriptManager sm) {
        // Kerning City's Honor Restored (22552 - end)
        final int answer = sm.askMenu("Oh, Evan! So what happened? Did you catch the culprit that stole the Herb? What a relief! ", java.util.Map.of(
                0, "#p1061005# asked me to tell you he's sorry."
        ));
        sm.sayNext("Hehehe. #p1061005# won't blame #m103000000# anymore, right? That's excellent! I'm so relieved that the honorable name of #m103000000# has been restored. Here. This is for you.");
        sm.sayOk("I'm giving you this as a token of my appreciation for clearing the name of #m103000000#. Thank you again. You're strong and kind... You're simply the best!");
        sm.addExp(2500);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        if (!sm.addItem(1142154, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22552);
    }

    @Script("q22553s")
    public static void q22553s(ScriptManager sm) {
        // Puppet Caring for his Master 2 (22553 - start - auto-start)
        sm.sayNext("Oh, my lifesaver! Please help my master! I don't think the potion alone is enough to heal my master. Could you please help me find a bandage I can use to wrap my master's wound?");
        sm.sayBoth("You really are amazing, lifesaver. Please find me some #t4000035#es so I can make a bandage. If I wash the #t4000035#es and cut them, I can use them as a bandage.");
        sm.sayOk("You can find #t4000035#es in... Where was that... Oh, right! I heard creatures called #r#o3230101#s#k in #b#m103000000# Subway#k wear #t4000035#es over their heads. Can you eliminate those creatures and bring me #b60 #t4000035#es#k? I'm counting on you.");
        sm.forceStartQuest(22553);
    }

    @Script("q22553e")
    public static void q22553e(ScriptManager sm) {
        // Puppet Caring for his Master 2 (22553 - end)
        final int answer1 = sm.askMenu("It's my lifesaver! Did you bring me the #t4000035#es? Wow, that's a lot! I think you have enough for me to make a bandage. You're such a lovely person, lifesaver. You deserve to be a part of my master's organization!", java.util.Map.of(
                0, "Your master's organization? What organization?"
        ));
        final int answer2 = sm.askMenu("I don't know what it's called, but it's an organization of people in Maple World that secretly do good deeds! A lot of people want to join! I can pull some strings and get you into the organization!", java.util.Map.of(
                0, "(As a Dragon Master, you should join an organization of people who secretly do good deeds, right?) Sounds good!"
        ));
        sm.sayOk("Alright! Just leave it to me!");
        sm.removeItem(4000035, 60);
        sm.addExp(12100);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        sm.forceCompleteQuest(22553);
    }

    @Script("q22554s")
    public static void q22554s(ScriptManager sm) {
        // Nella's Introductions (22554 - start - auto-start)
        final int answer = sm.askMenu("Hey, Evan. Are you busy? I have something to tell you... I told someone about how you helped me before, and now that person wants to meet you. I'm not bothering you, am I?", java.util.Map.of(
                0, "No, you're not bothering me. But who wants to meet me?"
        ));
        sm.sayNext("It's Chief Stan of #m100000000#. Something happened and he's looking for someone strong. Won't you go over to #b#m100000000# Town#k and help #bChief Stan#k? I'll write you a Letter of Introduction.");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmph, fine. Something happened in #m100000000#, but I'm sure Chief Stan will find a capable person to help him.");
            return;
        }
        sm.sayNext("Thanks for accepting my request without giving me grief. I was scared that you might snap at me.");
        if (!sm.addItem(4032465, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22554);
    }

    @Script("q22554e")
    public static void q22554e(ScriptManager sm) {
        // Nella's Introductions (22554 - end)
        sm.sayOk("Is that you, Evan? Shouldn't you be helping out at the farm? Hm? Isn't this #t4032465#? Hm, I asked her to introduce a capable adventurer to me... Why did she send this with you? Let me give it a read.");
        sm.removeItem(4032465);
        sm.addExp(1500);
        sm.forceCompleteQuest(22554);
    }

    @Script("q22555s")
    public static void q22555s(ScriptManager sm) {
        // Chief Stan's Test (22555 - start)
        final int answer = sm.askMenu("According to this Letter of Introduction, you are the strong adventurer #p1052103# has chosen to send me. But you're #p1013103#'s second child, not a strong adventurer! I thought I could count on #p1052103#, but I guess I was wrong.", java.util.Map.of(
                0, "I'm actually really strong. Believe me."
        ));
        sm.sayNext("You may have spent your time chasing a few monsters instead of helping out at the farm, but I need a competent adventurer. If you really are capable, prove your strength to me. Why don't I give you a little test?");
        if (!sm.askAccept("")) {
            sm.sayOk("Stop wasting time trying to be someone you're not and go help your father at the farm. You...? An adventurer...? HA!");
            return;
        }
        sm.sayOk("Do you know a monster called #o3000001#? It's small but quite powerful. If you enter from the Warning Post at #m106010100#, you'll find a Hidden Street leading to Golem's Temple. Defeat the #r#o3000001##k in #b#m106010102##k and bring me a #b#t4000068##k. Then I will recognize you as a true adventurer.");
        sm.forceStartQuest(22555);
    }

    @Script("q22555e")
    public static void q22555e(ScriptManager sm) {
        // Chief Stan's Test (22555 - end)
        final int answer = sm.askMenu("Hm, I don't believe this. You've really brought me a #t4000068#. You're stronger than I thought. But don't get excited. The monster you have to battle is much more powerful than #o3000001#. ", java.util.Map.of(
                0, "I'm much more powerful than #o3000001# as well. Stop worrying and let me help you."
        ));
        sm.sayOk("Let me think about it a little longer.");
        sm.removeItem(4000068, 2);
        sm.addExp(3200);
        sm.forceCompleteQuest(22555);
    }

    @Script("q22556s")
    public static void q22556s(ScriptManager sm) {
        // Chief Stan's Request (22556 - start)
        sm.sayNext("I'd look for another adventurer, if I could, but I have no choice... You must promise me that you will run away if you find yourself in danger. Don't be foolish. Do you understand?");
        if (!sm.askAccept("")) {
            sm.sayOk("Don't overestimate your strength. Many adventurers hurt themselves being foolish like that. I won't let you go anywhere until you promise me that you'll put your safety before anything else.");
            return;
        }
        sm.sayNext("Okay then. #m100000000# is a peaceful town, but there is a dangerous area nearby known as the Golem's Temple. So far, it hasn't been a problem since the Golems are so slow and they don't leave their area.");
        sm.sayBoth("But I've been hearing strange noises coming from the Golem's Temple lately. Thumping and banging... They're up to something, and we must find out what. That's where you come in.");
        sm.sayOk("It might be dangerous, so don't go in too deep. Yes, go no further than #b#m106010102##k and see if you can find out anything. Don't get yourself into trouble now, you hear?");
        sm.forceStartQuest(22556);
    }

    @Script("q22556e")
    public static void q22556e(ScriptManager sm) {
        // Chief Stan's Request (22556 - end)
        final int answer = sm.askMenu("Did you visit the Golem's Temple? Was there anything out of the ordinary? Tell me.", java.util.Map.of(
                0, "There was a door with a strange puppet sitting on top. "
        ));
        sm.sayNext("A door with a strange puppet sitting on top... What could it be? Is someone playing a joke on us?");
        sm.sayOk("Monsters in various towns have started acting strange. It would be awful if those Golems became more violent than they already are. We can stop Mushrooms or #o1210100#s, but not Golems.. I'll ask for your help if something happens.");
        sm.addExp(6600);
        sm.forceCompleteQuest(22556);
    }

    @Script("q22557s")
    public static void q22557s(ScriptManager sm) {
        // Kidnapping of Camila (22557 - start - auto-start)
        sm.sayNext("Oh no! #p1012108# has been kidnapped by the Golems! I warned her to stay home, but she went out to pick strawberries! One of the Golems grabbed #p1012108# and disappeared into the Golem's Temple!");
        sm.sayBoth("We don't have much time! Please, hurry over to the #bGolem's Temple#k and rescue #b#p1012108##k!");
        if (!sm.askAccept("")) {
            sm.sayOk("This is no time for jokes! Quit stalling and accept my request!");
            return;
        }
        sm.sayOk("I don't know how far the Golem went! You must hurry! Please, bring back #p1012108#!");
        sm.forceStartQuest(22557);
    }

    @Script("q22557e")
    public static void q22557e(ScriptManager sm) {
        // Kidnapping of Camila (22557 - end)
        sm.sayNext("You've brought #p1012108# back! Phew, what a relief. I never thought something so scary would happen.");
        sm.sayOk("I must come up with a plan. Just hold on a minute. I have to concentrate.");
        sm.addExp(14500);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        sm.forceCompleteQuest(22557);
    }

    @Script("q22558s")
    public static void q22558s(ScriptManager sm) {
        // Reason for the Golem's Change (22558 - start)
        sm.sayNext("I just can't figure out why the Golems are behaing like this. I wonder if it has to do with a puppet, like the other incidents that have occurred... I guess we have no other option than to ask #p1012108#. ");
        sm.sayBoth("Please go talk to #b#p1012108##k and ask if she remembers anything... Anything at all. She's probably in shock right now, so comfort her a little while you're at it, would you?");
        if (!sm.askAccept("")) {
            sm.sayOk("Hmph, don't you want to ask her? You two know each other, so #p1012108# might be more willing to share what she saw with you.");
            return;
        }
        sm.sayOk("Please be nice to her. She's very fragile.");
        sm.forceStartQuest(22558);
    }

    @Script("q22558e")
    public static void q22558e(ScriptManager sm) {
        // Reason for the Golem's Change (22558 - end)
        final int answer1 = sm.askMenu("Oh, Evan! I was so out of it that I forgot to thank you. Am I okay? Of course.  I'm a little shaken up but I'm fine. What brings you here?", java.util.Map.of(
                0, "Hm, I wanted to ask you something. Did you see anything when you were in Golem's Temple? Anything that seemed out of place or strange?"
        ));
        final int answer2 = sm.askMenu("Well, I don't know if it's out of the ordinary or not, but I did see a door with a puppet sitting on top. I saw a lot of Golems jumping out of that door. Is that strange?", java.util.Map.of(
                0, "Golems from that door? Were they average Golems? Or were they reddish Golems?"
        ));
        sm.sayNext("All the Golems I saw were reddish. Are there other types? Anyway, that's all I can tell you. I hope it helps.");
        sm.sayOk("By the way, how did you get so strong? You weren't this strong when you, me, and #p1013101# used to play together. What's your secret? Okay, okay, I'll stop prying.");
        sm.addExp(1000);
        sm.forceCompleteQuest(22558);
    }

    @Script("q22559s")
    public static void q22559s(ScriptManager sm) {
        // Eliminate the Golems (22559 - start)
        sm.sayNext("What did #p1012108# say? Hm, so that #rsuspicious door with the strange puppet sitting on top#k has something to do with this. I have a feeling there is an object of some sort that changes the Golems behind that door. It could very well be a puppet.");
        sm.sayBoth("There have been many incidents involving puppets near #m100000000# lately. But it was mostly the Mushrooms causing a ruckus.");
        sm.sayBoth("Golems are much scarier. But the cause of their change appears to be linked to the other incidents that have happened in #m100000000# and other towns.");
        sm.sayBoth("But we can't assume anything until we see what's behind that door with the puppet ourselves. Could you look into this?");
        if (!sm.askAccept("")) {
            sm.sayOk("You don't think you can do it? I thought you'd be confident in yourself. Must I find a stronger adventurer?");
            return;
        }
        sm.sayNext("Go to #m106010102# and enter through the #bdoor with strange puppet sitting on top#k. When you see #rEnranged Golems#k, defeat them and bring me a #bPuppet#k if you happen to find one. Good luck.");
        sm.sayOk("I never thought I'd depend on #p1013103#'s second child like this. Ha, life really is full of surprises.");
        sm.forceStartQuest(22559);
    }

    @Script("q22559e")
    public static void q22559e(ScriptManager sm) {
        // Eliminate the Golems (22559 - end)
        sm.sayNext("So? What did you find behind that door? Isn't this a #t4032466#? Who could be behind all of this?");
        sm.sayOk("If we had left the doll, more Golems would have turned violent and attacked #m100000000#. You have saved us, Evan. Thank you. You are a hero that has saved #m100000000#.");
        sm.removeItem(4032466);
        sm.addExp(14200);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        sm.forceCompleteQuest(22559);
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

    @Script("q22560e")
    public static void q22560e(ScriptManager sm) {
        // Condition for Joining the Secret Organization 1 (22560 - end)
        sm.sayOk("Wow, you've defeated all 150 #o3230100#s! My master will be very happy! Let me go ask my master if you can join the secret organization.");
        sm.addExp(15800);
        sm.forceCompleteQuest(22560);
        sm.forceStartQuest(22561);
    }

    @Script("q22561s")
    public static void q22561s(ScriptManager sm) {
        // Condition for Joining the Secret Organization 2 (22561 - start - auto-start)
        sm.sayNext("I don't think that was enough to get you into the secret organization, lifesaver. My master says he'll let you join if you fulfill one more request.");
        sm.sayBoth("This is like a practice round before you join the organization. I'm sure it'll be simple for you. Now, let me tell you about this task.");
        sm.sayOk("The task requires you to find #b20 #t4000031#s#k. You'll find a lot of #r#o4230101#s#k in #b#m100040103##k, and all you have to do is acquire #t4000031#s from these monsters. I'll be waiting!");
        sm.forceStartQuest(22561);
    }

    @Script("q22561e")
    public static void q22561e(ScriptManager sm) {
        // Condition for Joining the Secret Organization 2 (22561 - end)
        sm.sayNext("Oh, you've brought all the #t4000031#s! That was quite impressive! Let me go ask my master if you can now join the organization!");
        sm.sayOk("My master was too busy, so I couldn't ask. I'll ask him a little later so please wait a few moments.");
        sm.removeItem(4000031, 20);
        sm.addExp(15800);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2211), 1);
        sm.forceCompleteQuest(22561);
    }

    @Script("q22562s")
    public static void q22562s(ScriptManager sm) {
        // Onyx Dragon Study (22562 - start - auto-start)
        final int answer1 = sm.askMenu("It's been a long time. I had some news I wanted to share with you since you are conducting a research on Onyx Dragons. My friend, halflinger #p2081000#, who nurtures baby Dragons in #m240000000#, says he knows something about Onyx Dragons. ", java.util.Map.of(
                0, "Will he be able to distinguish whether a Dragon is an Onyx Dragon just by looking?"
        ));
        final int answer2 = sm.askMenu("Of course! He's a halflinger, so he can tell just by looking at the Dragon's scale. If you have an Onyx Dragon's scale, he'll be able to confirm it in a split second. That's how well he knows Dragons.", java.util.Map.of(
                0, "(Chief Tatamo probably has the answer to why the Onyx Dragons have become extinct, too...)"
        ));
        final int answer3 = sm.askMenu("But Tatamo may not tell you much about Onyx Dragons. He doesn't even tell me, his good friend. It's as if he feels guilty about something. ", java.util.Map.of(
                0, "What can I do to have Chief Tatamo share what he knows about Onyx Dragons?"
        ));
        sm.sayNext("Well, maybe if you showed him an Onyx Dragon's Scale... He might tell you what he knows and ask you to tell him how you got the scale in return.");
        if (!sm.askAccept("")) {
            sm.sayOk("Hm, of course. Even you wouldn't happen to have an Onyx Dragon's Scale.");
            return;
        }
        sm.sayOk("It would be easy to find a scale if you were near an Onyx Dragon....");
        sm.forceStartQuest(22562);
    }

    @Script("q22562e")
    public static void q22562e(ScriptManager sm) {
        // Onyx Dragon Study (22562 - end)
        sm.sayNext("What is it? Huh? This is an Onyx Dragon's Scale? It has a mysterious glow. But I can't tell if this is real or fake. Let me ask Tatamo to appraise it.");
        sm.sayOk("I'll send this over to #m240000000#. It'll take a while since it takes some time to send, receive, then reply. I'll contact you as soon as I receive an answer from #m240000000#.");
        sm.removeItem(4032467);
        sm.addExp(23000);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2212), 1);
        sm.forceCompleteQuest(22562);
    }

    @Script("q22563s")
    public static void q22563s(ScriptManager sm) {
        // The Value of One Scale (22563 - start)
        final int answer1 = sm.askMenu("Ma... Master, why are you looking at me like that?", java.util.Map.of(
                0, "You heard, didn't you? I need one of your scales. Give me one."
        ));
        final int answer2 = sm.askMenu("You want one of my scales? How could you say something so cruel with a straight face, master? Do you know how painful that would be?", java.util.Map.of(
                0, "Well, not really, but there is no other way for us to identify which family of Dragons you belong to. Come on now, hurry."
        ));
        final int answer3 = sm.askMenu("No way! I can't do that! How could you try to take one of my pretty and perfect scales? Think about it, master. How pathetic would you feel if you had a bald spot?", java.util.Map.of(
                0, "A scale isn't like hair. I can take one from your body where it wouldn't be so noticeable. "
        ));
        final int answer4 = sm.askMenu("Wahhh, don't say that with such a happy face! I'm scared! Can't I give you something else? ", java.util.Map.of(
                0, "Something else... Like your horn?"
        ));
        sm.sayNext("Yikes! My horn? Of course not! Fine, just take a scale! But I have to consume enough calcium for my DEF to compensate for the loss of my scale.");
        if (!sm.askAccept("")) {
            sm.sayOk("Absolutely not, then! I don't care if you're my master, I can't just give you a scale. No way!");
            return;
        }
        sm.sayOk("Oh, what about a bone? I am going to eat a bone! If you go near #bRemains <Tomb>#k, you will find #r#o4230125#s#k. Bring me #b50#k of their #bBones#k! Then I will give you one... I repeat, just ONE scale.");
        sm.forceStartQuest(22563);
    }

    @Script("q22563e")
    public static void q22563e(ScriptManager sm) {
        // The Value of One Scale (22563 - end)
        sm.sayNext("Did you already find all the #t4000204#s I've asked you to bring? I've always been impressed with your strength and speed, but I'm not so happy this time. Hmph, fine. Give me a minute. Eeeeeeeeeeeeek!");
        sm.sayOk("*sniff* Here is my scale. My precious scale. Please be careful with that. Okay? I'm not giving you another one.");
        sm.removeItem(4000204, 50);
        if (!sm.addItem(4032467, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22563);
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

    @Script("q22566s")
    public static void q22566s(ScriptManager sm) {
        // Permission to Join the Secret Organization (22566 - start - auto-start)
        sm.sayNext("Hello, lifesaver! Long time no see! I'm sorry I couldn't contact you earlier. My master has been very busy... To tell you the truth, my master was fighting against his enemy and got himself in trouble, so he's running away. I think he's safe for now, though.");
        sm.sayBoth("Oh, but that's not why you're here. Congratulations! You have been approved to join the secret organization! More accurately, you're a temporary member of the organization, but you will soon be promoted and become an official member like my master!");
        sm.sayBoth("I don't know what kind of tasks you will be given, lifesaver. If you wish to receive a mission as a member of the secret organization, go to the #b#m200080601##k in #b#m200080600##k. If you look in the #b#p2012034##k, you'll be able to view the mission you have been given.");
        if (!sm.askYesNo("It's really hidden, isn't it? That's how the organization delivers its missions. Once you get promoted, you'll be able to meet with other members and decide on the missions you want to accept, like my master.")) {
            sm.sayOk("Huh? Don't you approve of the way you receive missions? But it's a secret organization and everything must be done discreetly...");
            return;
        }
        sm.forceStartQuest(22566);
    }

    @Script("q22566e")
    public static void q22566e(ScriptManager sm) {
        // Permission to Join the Secret Organization (22566 - end)
        sm.sayOk("#b(Could this be the #p2012034# the #p1063018# was talking about? This brick that's popping out appears to be movable. Try picking up the brick.)#k");
        sm.addExp(6100);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2212), 1);
        if (!sm.addItem(1142155, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22566);
    }

    @Script("q22567s")
    public static void q22567s(ScriptManager sm) {
        // Secret Organization's First Mission (22567 - start)
        // NPC 2012034 - Loose Brick
        sm.setPlayerAsSpeaker(true);
        sm.sayNext("#b(You take the brick out and place your hand inside the empty gap. You find a piece of paper. Read what it says.)#k");
        sm.setPlayerAsSpeaker(false);
        sm.sayBoth("Bring back a #bGrowth Accelerant#k made by #b#p2030012##k and place it behind the brick.\r\nThe ingredients are as follows.\n\n10 #t4000070#s\n10 #t4000071#s\n10 #t4000072#s\n10 #t4000068#s\n\nDo not throw away or take this paper. Place it back behind the brick.");
        sm.setPlayerAsSpeaker(true);
        sm.sayBoth("#b(You place the paper back behind the brick.)#k");

        if (!sm.askYesNo("Will you accept this mission?")) {
            sm.setPlayerAsSpeaker(true);
            sm.sayOk("#b(Ugh, this mission seems tedious. Since you don't want this mission, put the paper back and pretend you didn't see it.)#k");
            return;
        }

        sm.setPlayerAsSpeaker(true);
        sm.sayOk("#b(The #p2012034# has been put back into place)#k");
        sm.forceStartQuest(22567);
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

    @Script("q22568s")
    public static void q22568s(ScriptManager sm) {
        // Making the Growth Accelerant (22568 - start - complex multi-choice quiz)
        final int answer1 = sm.askMenu("The only people who come all the way here are adventurers or those who need my research results. Do you fall under the former or the latter? ", java.util.Map.of(
                0, "Former",
                1, "Latter"
        ));
        if (answer1 == 0) {
            sm.sayOk("You must be on an adventure. Take some time to rest here if you wish.");
            return;
        }
        final int answer2 = sm.askMenu("Ah, how nice to meet someone in need of my research results! It's been a while. So, what would you like to make?", java.util.Map.of(
                0, "Growth Accelerant",
                1, "Hair-Regrowth Medication",
                2, "Diet Pills"
        ));
        if (answer2 != 0) {
            String response = switch (answer2) {
                case 1 -> "I'm sorry, but I don't know how to make such thing since I show no signs of balding. Haha.";
                case 2 -> "Hm, it doesn't seem like you need to go on a diet. Exercise is the healthiest way to keep your body in shape.";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        final int answer3 = sm.askMenu("Growth Accelerant? I've completed my research pertaining to the Growth Accelerant ages ago, but I haven't really made it because the ingredients are so hard to find. Do you know what ingredients are required to make it?", java.util.Map.of(
                0, "10 Cellion Tails, 10 Lioner Tails, 10 Grupin Tails, and 10 Fierry's Wings",
                1, "10 Cellion Tails, 10 Lioner Tails, 10 Grupin Tails, and 100 Fierry's Tentacles",
                2, "10 Cellion Tails, 10 Lioner Tails, 10 Grupin Tails, and 10 Fierry's Tentacles"
        ));
        if (answer3 != 0) {
            sm.sayOk("I have no idea what you can make with those ingredients.");
            return;
        }
        sm.sayNext("I'm glad you know the ingredients. I will make you the Growth Accelerant as soon you bring me the required ingredients. ");
        if (!sm.askAccept("")) {
            sm.sayOk("I can't make anything without proper ingredients...");
            return;
        }
        sm.sayOk("I can make it in a second if you just bring me the ingredients. Go on and bring me the #bingredients for the Growth Accelerant#k.");
        sm.forceStartQuest(22568);
    }

    @Script("q22568e")
    public static void q22568e(ScriptManager sm) {
        // Making the Growth Accelerant (22568 - end)
        sm.sayOk("You've found all the ingredients I need to make the Growth Accelerant. But why do you need the Growth Accelerant anyway? Well, I suppose that's none of my business. I'll make it for you in a few.");
        sm.removeItem(4000070, 10);
        sm.removeItem(4000071, 10);
        sm.removeItem(4000072, 10);
        sm.removeItem(4000068, 10);
        if (!sm.addItem(4032468, 10)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22568);
    }

    @Script("q22569s")
    public static void q22569s(ScriptManager sm) {
        // Another Clue about the Onyx Dragon (22569 - start - auto-start)
        sm.sayNext("It's been a long time, researcher of Onyx Dragons. I have called you because I found a book you might be interested in. This book appears to be an ordinary diary, but it contains a line that might interest you.");
        sm.sayBoth("I think it will help you with your research, so if you want to read it, come to #m101000003##k in #b#m101000000#. I'll hold on to the book and won't let anyone borrow it.");
        if (!sm.askYesNo("It has an explanation that seems to indicate something about Onyx Dragons. No one knows whether what it says here is true or not, but that is why you must verify the facts. I'll be waiting.")) {
            sm.sayOk("You can't come? That's too bad. I'll have to make the book available for anyone that wants to borrow it.");
            return;
        }
        sm.forceStartQuest(22569);
    }

    @Script("q22569e")
    public static void q22569e(ScriptManager sm) {
        // Another Clue about the Onyx Dragon (22569 - end)
        sm.sayOk("Oh, I'm glad you came. Wait here for a second. I'll take out the book I told you about. Let me see where that Voyage Log is...");
        sm.addExp(3000);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2213), 1);
        sm.forceCompleteQuest(22569);
        sm.forceStartQuest(22570);
    }

    @Script("q22570s")
    public static void q22570s(ScriptManager sm) {
        // Crew Member's Voyage Log (22570 - start)
        sm.sayNext("This book was written by a crew member. It's a Voyage Log he wrote. It contains all the struggles he encountered, but towards the end, it mentions something rather interesting. I think that's the part that would help you. Here, take the book.");
        if (!sm.askYesNo("")) {
            sm.sayOk("Hm... What's wrong? Do you have too many things in your Inventory? I'll wait so talk to me again when you're ready.");
            return;
        }
        sm.sayOk("I don't have to explain everything. It's really up to you to use it to your benefit. Go ahead and read it. It isn't too long, so #bread through it and return it to me soon#k.");
        if (!sm.addItem(4161051, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22570);
        sm.forceStartQuest(22571);
    }

    @Script("q22570e")
    public static void q22570e(ScriptManager sm) {
        // Crew Member's Voyage Log (22570 - end - multi-quiz)
        final int answer1 = sm.askMenu("Did you finish reading the #t4161051#? That was fast. You're a speed reader! Which page had information about Onyx Dragons?", java.util.Map.of(
                0, "Page 15",
                1, "Page 17",
                2, "Page 18",
                3, "Page 20"
        ));
        if (answer1 != 2) {
            sm.sayOk("Was there something about a Dragon on that page? I don't think so...");
            return;
        }
        final int answer2 = sm.askMenu("Right, that was the page. And the date the crewmember wrote that was... Wait, when was it again?", java.util.Map.of(
                0, "June 29th",
                1, "July 8th",
                2, "July 13th",
                3, "August 2nd"
        ));
        if (answer2 != 2) {
            sm.sayOk("I don't think that's the correct date. Read it again.");
            return;
        }
        final int answer3 = sm.askMenu("Do you remember the name of the crew member's ship? It was really unique... ", java.util.Map.of(
                0, "Thunder Bolt",
                1, "Thunder Bird",
                2, "Under Bolt",
                3, "Usain Bolt"
        ));
        if (answer3 != 1) {
            sm.sayOk("I don't think that was his name...");
            return;
        }
        final int answer4 = sm.askMenu("It says there was a giant Dragon in the island the crewmember arrived in. Do you remember how many horns the Dragon had?", java.util.Map.of(
                0, "None",
                1, "One",
                2, "Two",
                3, "Four"
        ));
        if (answer4 != 3) {
            sm.sayOk("Hm, I don't think that's the correct number...");
            return;
        }
        final int answer5 = sm.askMenu("Oh, right. That's why I said the Dragon mentioned in the Voyage Log might be an Onyx Dragon. Most Dragons don't have that many horns. I'm sure the Dragon this crew member saw was an old Onyx Dragon. But for all I know, he could have just made it up.", java.util.Map.of(
                0, "What can I do to find the truth?"
        ));
        final int answer6 = sm.askMenu("That's simple. You can just go find the author and ask him yourself. If you read the book carefully, you should have caught the name of the author. Do you remember the name of the crew member who wrote the Voyage Log?", java.util.Map.of(
                0, "Retired Crewmember #p0020000#",
                1, "Retired Crewmember Teo",
                2, "Retired Crewmember Kyrin",
                3, "Retired Crewmember Gustav"
        ));
        if (answer6 != 0) {
            String response = switch (answer6) {
                case 1 -> "Hm... He hasn't retired yet.";
                case 2 -> "Kyrin is a very active pirate. Haha.";
                case 3 -> "Did you forget who your father is? Haha!";
                default -> "";
            };
            sm.sayOk(response);
            return;
        }
        sm.sayOk("Right, that was his name. If you find #p0020000# and ask him, I'm sure you'll get information about the island where he saw the Onyx Dragon. Oh, you know know #p0020000#? He lives in #m104000000#. Go to #m104000000# if you want to find him.");
        sm.removeItem(4161051);
        sm.addExp(1000);
    }

    @Script("q22571s")
    public static void q22571s(ScriptManager sm) {
        // John's Testimony (22571 - start)
        sm.sayNext("Are you here to buy a fish? Huh? #t4161051#...? Well, of course I wrote it! But I've retired long ago... What? You want to know whether what I wrote is true?");
        sm.sayBoth("Do I look like I need embellish stories to make them interesting? Of course it's all true! Even the bit about the island I where found a sleeping dragon. But why do you ask? You're not trying to get to that island, are you?");
        sm.sayBoth("Haha, another youngling acting with reckless bravado... I'd urge you to play it safe, but I suppose you must learn that on your own. Alright, I'll give you a map to the island.");
        if (!sm.askYesNo("")) {
            sm.sayOk("Don't need this map? Smart choice. Don't put yourself in danger for some ridiculous ambition.");
            return;
        }
        sm.sayOk("Since you're not a crew member, find #b#p1002001##k near the ticketing booth and ask him if you can go to this island. The sea route will be rough, so he may decline your request.");
        if (!sm.addItem(4032469, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22571);
    }

    @Script("q22571e")
    public static void q22571e(ScriptManager sm) {
        // John's Testimony (22571 - end)
        sm.sayOk("That whale there is getting on my nerves. I probably shouldn't catch it though, huh? What are you doing here? Hm? You want to know if you can get to the island on this map? Let me see...");
        sm.removeItem(4032469);
        sm.addExp(2000);
        sm.forceCompleteQuest(22571);
        sm.forceStartQuest(22572);
    }

    @Script("q22572s")
    public static void q22572s(ScriptManager sm) {
        // Teo's Advice (22572 - start)
        sm.sayNext("Must you go to the island on this map? If so, I can't help you. But I know someone who might be able to... #b#p1002101##k. He's retired now, but #p1002101# is the best crew member I know.");
        sm.sayBoth("Like I said, #p1002101# retired a long time ago. He doesn't miss it much, either... He may not want to help you, but I know something that might change #p1002101#'s mind.");
        if (!sm.askYesNo("")) {
            sm.sayOk("Hm, I guess you're not THAT interested.");
            return;
        }
        sm.sayOk("If you bring #p1002101# some #t4032470#, he might change his mind. Go to #b#m110000000##k and see if you can find some #b#t4032470##k. They don't make it often, so it might be hard to get...");
        if (!sm.addItem(4032526, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22572);
        sm.forceStartQuest(22573);
    }

    @Script("q22572e")
    public static void q22572e(ScriptManager sm) {
        // Teo's Advice (22572 - end)
        sm.sayNext("Ah, I don't think we've met. But you seem quite competent. It's rare to find someone so strong that I haven't trained myself. But what brings you here? Eh? You want me to take you to the island on this map?");
        sm.sayBoth("Hahaha, someone must have told you that I was the best crew member in town! But I'm retired. I don't have any desire to get back on a ship. I'm just here to guide adventurers and offer advice.");
        sm.sayBoth("Oh, isn't this...#t4032470#? Oh, boy! There's always a shortage of this in Florina Beach. It smells delicious... Are you giving it to me?");
        sm.sayOk("Haha, what a generous person you are! Alright, why not? I'll take you to the island on the map. It'll be a nice change of pace.");
        sm.removeItem(4032526);
        sm.removeItem(4032470);
        sm.addExp(38900);
        sm.forceCompleteQuest(22572);
    }

    @Script("q22573s")
    public static void q22573s(ScriptManager sm) {
        // Tropical Fruit Punch (22573 - start)
        sm.sayNext("Welcome to #m110000000#. I hope you enjoy your stay. Yes? Is there anything I can do for you? Oh, #t4032470#? I'm all sold out of that.");
        sm.sayBoth("#t4032470# takes a lot of time and energy to make, so we don't always have enough.");
        sm.sayBoth("But if you really, really want some #t4032470#, I have a special offer for you! If you pay a service fee and bring me the necessary ingredients to make #t4032470#, I'll be more than glad to make some for you. What do you say?");
        if (!sm.askYesNo("")) {
            sm.sayOk("You don't want it that badly, huh? Yeah, not many people choose to go through all that for a drink.");
            return;
        }
        sm.sayOk("Wow, I've never met anyone who wanted #t4032470# so badly! Now then, please find #b5 #t4000136#s#k, #b30 #t4000029#s#k, and #b30 #t4000044#s#k. Oh, and I almost forgot! The fee is #b60000 mesos#k.");
        sm.forceCompleteQuest(22573);
    }

    @Script("q22573e")
    public static void q22573e(ScriptManager sm) {
        // Tropical Fruit Punch (22573 - end)
        sm.sayOk("Did you bring me the ingredients? Let me have them. I just have to peel the #t4000136#s, dice the fruit, ripen the #t4000029#s, and put a few #t4000044#s in for kick...");
        sm.addMoney(-60000);
        sm.removeItem(4000136, 5);
        sm.removeItem(4000029, 30);
        sm.removeItem(4000044, 30);
        if (!sm.addItem(4032470, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
    }

    @Script("q22574s")
    public static void q22574s(ScriptManager sm) {
        // Strong Sail Needed (22574 - start)
        sm.sayNext("Oh my, I didn't realize how tough it would be to get to the island. It's not even the reef or the rough waves I'm worried about. It'll be so windy out there that the sail on our boat will get ripped into pieces. We need a stronger sail.");
        sm.sayBoth("Oh, right. Are you familiar with #t4000030#? Geez! Calm down! What made you jump like that? It's not really the skin of a Dragon. It's the skin of creatures that resemble Dragons...like Drakes. People just call it #t4000030#. ");
        sm.sayBoth("Anyway! I think I could make a sturdier sail that could withstand those winds if I used Dragon Skin. Will you bring me some #t4000030#? You have to hunt Drakes inside the #m105040300# Dungeon. Think you can do it?");
        if (!sm.askYesNo("")) {
            sm.sayOk("You look strong enough. Maybe you just need more confidence. Well, I understand. Drakes may not be real Dragons, but they're extremely strong. Alright then.");
            return;
        }
        sm.sayOk("Ah, I'm impressed. You must be more powerful than I thought. Bring me #b2 #t4000030#s#k and we'll be on our way to the island before you know it!");
        sm.forceStartQuest(22574);
    }

    @Script("q22574e")
    public static void q22574e(ScriptManager sm) {
        // Strong Sail Needed (22574 - end)
        sm.sayOk("Did you bring the #t4000030# I requested? Marvelous! I can see now how you have the confidence to venture to such a dangerous island. Give me a minute.");
        sm.removeItem(4000030, 2);
        sm.addExp(38900);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2213), 2);
        sm.forceCompleteQuest(22574);
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

    @Script("q22576s")
    public static void q22576s(ScriptManager sm) {
        // Delivering the Black Key (22576 - start)
        sm.sayNext("Looks like you've received something from #p2022003#. Thanks for your work. Now all you have to do is deliver that package to me. Oh, you don't need to come all the way to see me in person, though.");
        if (!sm.askYesNo("Have you ever heard of the #b#m211040400##k in #m211000000#? There's a #b#p2030015##k at the #m211040400#. Just go ahead and put the item under it, and another member will come and retrieve it.")) {
            sm.sayOk("Hmm. Alright, I'll assume you are very busy at this time. But don't make me wait for too long.");
            return;
        }
        sm.forceStartQuest(22576);
    }

    @Script("q22576e")
    public static void q22576e(ScriptManager sm) {
        // Delivering the Black Key (22576 - end)
        sm.sayNext("#b(There is a tree stump that looks rather suspicious. When you reach into it, you notice that it is hollow, as if man-made.)#k");
        sm.sayBoth("#b(You push the key into the hollow space inside the tree stump.)#k");
        sm.removeItem(4032471);
        sm.addExp(7400);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2213), 1);
        sm.forceCompleteQuest(22576);
        sm.sayOk("(The stump looks just like any other tree stump.)");
    }

    @Script("q22577s")
    public static void q22577s(ScriptManager sm) {
        // The Lost Black Key (22577 - start)
        sm.sayNext("Oh no, it seems you've lost the item. This affects the entire organization! Alright, I'll let this one go, but you're going to have to collect another #t4000069# right away.");
        if (!sm.askYesNo("Well, it's just like last time... No, actually, that's not going to be enough. This time, you're going to have to collect #b300 #t4000069#s#k and deliver them to #b#p2022003##k. Only then will #p2022003# give you the item again.")) {
            sm.sayOk("Honestly, one mistake is enough. If you are making yet another mistake by refusing to take on this assignment, then I just don't know if you'll be excused again.");
            return;
        }
        sm.forceStartQuest(22577);
    }

    @Script("q22577e")
    public static void q22577e(ScriptManager sm) {
        // The Lost Black Key (22577 - end)
        sm.sayOk("Hehe. So you want me to give you the item again? Alright. I've made like 10 copies of the key anyway. Kekeke!");
        sm.removeItem(4000069, 300);
        if (!sm.addItem(4032471, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceCompleteQuest(22577);
        sm.sayOk("Kekeke! Such a dark force...");
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

    @Script("q22579s")
    public static void q22579s(ScriptManager sm) {
        // Completed Sail (22579 - start)
        sm.sayNext("Look here, Evan! The sail made of #t4000030# is finally done. You can now sail to the island on your map without worrying about hard-hitting gale winds! Hahaha! If you want to go, come to #b#m104000000##k right away!");
        if (!sm.askYesNo("You'd better hurry before I change my mind! Hahahaha!")) {
            sm.sayOk("Huh? You're not going to go to the island after all that? Could we use the sail for something else then?");
            return;
        }
        sm.forceStartQuest(22579);
    }

    @Script("q22579e")
    public static void q22579e(ScriptManager sm) {
        // Completed Sail (22579 - end)
        sm.sayOk("Oh, that was quick! Ready to set sail? Is there anything else you want to do before taking off? It's going to take at least 15 minutes to get to the island, so if there's anything you need to do before setting sail, take care of it now!");
        sm.addExp(5200);
        sm.forceCompleteQuest(22579);
        sm.sayOk("If you'd like to go to the island on the map, just come and let me know.");
    }

    @Script("q22580s")
    public static void q22580s(ScriptManager sm) {
        // Slumbering Dragon Island (22580 - start)
        sm.sayNext("Master! Master! Something's strange. I don't hear anything, not even birds chirping, squirrels running, or leaves rustling in the wind! Seriously, all I hear are the waves. I would think that such quiet would make me anxious, but I kind of feel rather relaxed.");
        if (!sm.askYesNo("There's something in the middle of the island, I can feel it. Something familiar, yet new! Maybe there's another dragon! Master, let's go check it out! I wonder if it's something related to Onyx Dragons!")) {
            sm.sayOk("It's no fun when your hands play butterfingers at a time like this. C'mon, master!");
            return;
        }
        sm.setQRValue(22599, "1");
        sm.forceStartQuest(22580);
        sm.sayOk("Please hurry, master!");
    }

    @Script("q22580e")
    public static void q22580e(ScriptManager sm) {
        // Slumbering Dragon Island (22580 - end)
        final int answer = sm.askMenu("Wait. What's going on? I think that #o9300391# might be made of magic. Another being of my race...right at my fingertips. So close and yet so far!", java.util.Map.of(
                0, "Perhaps give it one more try?"
        ));
        sm.sayNext("No, I don't think it'll be any use. You don't want to do anything that might hurt a fellow dragon on the other side.");
        sm.sayBoth("Doesn't look like you can get to the other side unless you break the #o9300391#. It doesn't look like it will break easily, either. Sigh, let's just keep training. Perhaps a clue will come to us eventually.");
        sm.addExp(55200);
        sm.getUser().getCharacterStat().getSp().addSp(JobConstants.getJobLevel(2214), 1);
        sm.setQRValue(22599, "2");
        sm.forceCompleteQuest(22580);
        sm.sayOk("Yeah, let's get stronger and come back to this island later, master!");
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

    @Script("q22584s")
    public static void q22584s(ScriptManager sm) {
        // Eliminating Door Blocks (22584 - start)
        sm.sayNext("I am going to give you one last mission. Thanks to all your great work, one of our members has unlocked the First Door of #m922030011#.");
        if (!sm.askYesNo("You must now go back up to #m922030020# and re-enter the Safe. Go in there and eliminate #r#o9300390##k, the monster.")) {
            sm.sayOk("What? What's the matter? Are you unhappy about something that I've discussed about our plans?");
            return;
        }
        sm.forceStartQuest(22584);
        sm.sayOk("#o9300390# is a scary, scary monster. You're going to have to eliminate him as fast as you can or we'll all be in danger. Please hurry.");
    }

    @Script("q22584e")
    public static void q22584e(ScriptManager sm) {
        // Eliminating Door Blocks (22584 - end)
        final int answer = sm.askMenu("Thank you for getting rid of #o9300390#. You've done some great work. You must be tired now. Why don't you get some rest. Another member will take it from here.", java.util.Map.of(
                0, "#o9300390# said something strange."
        ));
        sm.sayOk("Really? Well now, I wouldn't worry about it. #o9300390# may seem stupid, but it's actually really conniving and tries to confuse people. Don't think too much about it.");
        sm.addExp(64900);
        sm.forceCompleteQuest(22584);
        sm.sayOk("I will talk to you again when there's another mission for you.");
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

    @Script("q22586s")
    public static void q22586s(ScriptManager sm) {
        // Secret Organization's Fourth Mission (22586 - start)
        sm.sayNext("I didn't think I'd be seeing you so soon. I've actually received intel a bit earlier than expected. So now I have your fourth mission.");
        sm.sayBoth("The fourth mission is to retrieve the map of an island. Don't worry, it won't be too difficult to find. #b#p2092001##k in #b#m251000000##k has the map, so all you need to do is get it from him.");
        sm.sayBoth("Just tell #p2092001# that you need the #b#t4032472##k and he'll know what you're talking about. I'll tell you what the map is for after you bring it to me.");
        if (!sm.askYesNo("Hehe... What luck! I had no idea that I was standing next to #rsomeone with an Onyx Dragon#k. Hmm. No, it's nothing. Just go ahead and complete the mission.")) {
            sm.sayOk("What, you're refusing? Oh no, oh no. Why is everyone being so defiant today?");
            return;
        }
        sm.forceStartQuest(22586);
    }

    @Script("q22586e")
    public static void q22586e(ScriptManager sm) {
        // Secret Organization's Fourth Mission (22586 - end)
        sm.sayOk("You've brought the #t4032472#? Show it to me. I want to make sure it is the one that we are looking for.");
        sm.removeItem(4032472);
        sm.addExp(68100);
        sm.forceCompleteQuest(22586);
        sm.sayOk("Haha. Looks like you've got the right one.");
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

    @Script("q22588s")
    public static void q22588s(ScriptManager sm) {
        // Secret Organization's Fifth Mission (22588 - start)
        sm.sayNext("Ha! If this is the exact location, we can open a portal there with magic. Evan, I would like to give you your fifth mission now. This one should be easy.");
        sm.sayBoth("All you have to do is go through the portal and you'll get to Turtle Island. You will find an altar there. #bPlace a certain item on the altar#k. Just #bdrop#k it onto it. Then, #rleave the cave quickly#k! I warn you. Don't linger behind...");
        sm.sayBoth("#rSome evil being has put a magic spell on the island, which keeps the members of our organization, including me, from entering#k. Since you are immune to that spell, we are relying on you to handle this mission.");
        if (!sm.askYesNo("What do you say? If you accept this mission, I will give you the item right now.")) {
            sm.sayOk("What? You're refusing to accept this mission? But we have never needed you more! Please, think about it!");
            return;
        }
        if (!sm.addItem(4032473, 1)) {
            sm.sayOk("Please check if your inventory is full or not.");
            return;
        }
        sm.forceStartQuest(22588);
        sm.sayOk("You received the item, right? Now, go through the portal. You will immediately be taken to Turtle Island.");
    }

    @Script("q22588e")
    public static void q22588e(ScriptManager sm) {
        // Secret Organization's Fifth Mission (22588 - end - auto-complete)
        sm.sayNext("You destroyed the #o9300391#! I knew it. Someone with an Onyx Dragon would of course be able to do it. Hehehe. Oh, no, don't mind me. Please leave the island at once. You will see a boat just outside.");
        sm.setQRValue(22605, "1");
        sm.forceCompleteQuest(22588);
        sm.sayOk("Huh? A Dragon sleeping on the island? That is not something you should concern yourself with! Do not let the Dragon see you. You must get off the island immediately!");
    }

    @Script("q22589s")
    public static void q22589s(ScriptManager sm) {
        // Dangerous Premonition (22589 - start)
        sm.sayNext("Master! Something is seriously wrong! Why is #p1013203# interested in the island where my fellow creature sleeps and why does he want you to destroy the #o9300391#? How is all this supposed to help Maple World?");
        if (!sm.askYesNo("I have a bad feeling about this. Something is not right! We must not leave the island right now, master! #bLet's go back into the cave!#k! I need to find out what is going on!")) {
            sm.sayOk("Master! Don't let your fingers slip at a time like this! Come on! This is important stuff!");
            return;
        }
        sm.setQRValue(22600, "1");
        sm.forceStartQuest(22589);
        sm.sayOk("Let's go back to the cave!");
    }

    @Script("q22589e")
    public static void q22589e(ScriptManager sm) {
        // Dangerous Premonition (22589 - end)
        sm.sayOk("I wonder what's happening. Master, why is #p1013203# trying to attack my fellow creature? And why is he saying that he doesn't need you anymore? I mean, why is he attacking us?");
        sm.addExp(68100);
        sm.setQRValue(22604, "1");
        sm.forceCompleteQuest(22589);
        sm.forceStartQuest(22590);
        sm.sayOk("I have no idea.");
    }

    @Script("q22590s")
    public static void q22590s(ScriptManager sm) {
        // Voice of the Sleeping Dragon (22590 - start)
        sm.sayNext("Ma...master! Did you hear that? But you just called me! You didn't call me? Was that a Dragon...? Wha... Listen! There it is again! You don't hear that, master? Master! Try talking to that sleeping dragon over there!");
        if (!sm.askYesNo("")) {
            sm.sayOk("Don't tell me you're scared of the Dragon? It may be big, but I can tell it's really sweet. I know the Dragon won't hurt you!");
            return;
        }
        sm.forceStartQuest(22590);
        sm.sayOk("Maybe the Dragon's awake?! You should go talk to it!");
    }

    @Script("q22590e")
    public static void q22590e(ScriptManager sm) {
        // Voice of the Sleeping Dragon (22590 - end)
        final int answer1 = sm.askMenu("Are you the child's master?", java.util.Map.of(
                0, "Uh, er, if you want to put it that way. Yes. Are you...an Onyx Dragon? What is your name?"
        ));
        final int answer2 = sm.askMenu("I am. My name is #p1205000#. I am the king of the Onyx Dragons of the past... The now extinct Onyx Dragons.", java.util.Map.of(
                0, "You are the king of the Onyx Dragons? But how did the Onyx Dragons become extinct? And why are you trapped in ice?"
        ));
        if (!sm.askYesNo("To tell you all that...I need to go back in time hundreds of years. It is a long and sad story.")) {
            return;
        }
        sm.addExp(20000);
        sm.forceCompleteQuest(22590);
        sm.sayOk("Are you ready to hear the story?");
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

    @Script("q22592s")
    public static void q22592s(ScriptManager sm) {
        // Unavoidable Truth (22592 - start)
        final int answer1 = sm.askMenu("Master, master! I don't understand! You and that Dragon...#p1205000# just stared at each other with blank looks and then started talking. He said something about sending you into his memory. Did you really see his past?", java.util.Map.of(
                0, "Yeah... I heard a conversation between #p1205000# and Freud from hundreds of years ago."
        ));
        final int answer2 = sm.askMenu("What happened and how is this related to the Black Mage? Isn't the Black Mage the great person that #p1013203# said needed to be revived for the good of Maple World? Tell me what happened!", java.util.Map.of(
                0, "The Black Mage was an evil being who tried to conquer Maple World hundreds of years ago. He realized the great power that the Onyx Dragons held and told them he would complete their incomplete spirits if they betrayed their masters and follow him."
        ));
        final int answer3 = sm.askMenu("What? That's impossible! A Dragon and his master are one! There is no way that they would betray their masters!", java.util.Map.of(
                0, "Yes, the Onyx Dragons did not betray their masters. They loved humans and hated the Black Mage, who was evil incarnate. They knew that a spirit completed by the Black Mage would also become evil. That is when the Black Mage...destroyed the Onyx Dragons."
        ));
        final int answer4 = sm.askMenu("How...how could that be? That's why my entire race is extinct?! That's why #p1205000# is trapped in ice...?", java.util.Map.of(
                0, "After the Onyx Dragons were annihilated, #p1205000# and his master, along with the other heroes, fought the Black Mage to the end. Ultimately, the Black Mage was sealed away and Maple World regained its former peace. However, #p1205000# was trapped in ice by the Black Mage's final curse."
        ));
        sm.sayNext("So everything was caused by the Black Mage? Then what about all the things that we've been doing? I thought they were all for the good of Maple World! Were we wrong? Master! Let's go find out!");
        if (!sm.askYesNo("")) {
            return;
        }
        sm.sayNext("The things we did in #m200000000#, the things we did in #m211000000#, and the things we did in #m220000000#... All the missions the Black Wings gave us...");
        sm.forceStartQuest(22592);
        sm.sayOk("Let's look at the consequences of what we've accomplished. #bLet's go to each town and ask#k. If we were tricked by the Black Wings... I won't forgive them!");
    }

    @Script("q22592e")
    public static void q22592e(ScriptManager sm) {
        // Unavoidable Truth (22592 - end)
        sm.sayNext("The Growth Accelerant that we thought would make the crops grow caused problems for #m200000000# by making the #o4230105#s grow abnormally.");
        sm.sayBoth("We thought we were helping people by eliminating Zombies, but we were only gathering the material that would be used to trade for a stolen #t4032471#.");
        sm.sayBoth("We defeated a monster who was guarding an important treasure in #m220000000#, causing the treasure to get stolen.");
        sm.addExp(10000);
        sm.forceCompleteQuest(22592);
        sm.forceStartQuest(22596);
        sm.sayOk("This confirms it. The Black Wings were only using us. They made us perform evil tasks so that they could revive the Black Mage! I can't forgive them!");
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

    @Script("q22596s")
    public static void q22596s(ScriptManager sm) {
        // Rage (22596 - start)
        sm.sayNext("All of the things that the Black Wings had us do...were for evil! We wanted to help people but instead, we ended up causing them more problems!");
        if (!sm.askYesNo("Master, let's go to the #b#m922030001##k in #m220000300#. The house where we met #p1013203# in person. Let's go there and teach #r#p1013203##k a lesson! How could he trick us like this?!")) {
            sm.sayOk("Master, I think you are too cautious at times. We have every right to be angry!");
            return;
        }
        sm.forceStartQuest(22596);
    }

    @Script("q22596e")
    public static void q22596e(ScriptManager sm) {
        // Rage (22596 - end)
        final int answer1 = sm.askMenu("Darn it... We just about had him but he ran away. Then again, a guy who could trick us so many times is probably great at running away. He probably won't show up here again.", java.util.Map.of(
                0, "Yeah, you're right. Geez... He was such a suspicious guy. We were so foolish to have believed him!"
        ));
        final int answer2 = sm.askMenu("Well, we could've been more careful. But I still think the deceiver deserves more blame than the deceived!", java.util.Map.of(
                0, "Of course!"
        ));
        final int answer3 = sm.askMenu("They said that their ultimate goal was to revive the Black Mage, the being that destroyed my race and trapped #p1205000# in ice... Master, if I said I wanted to take revenge on the Black Mage, would you help me?", java.util.Map.of(
                0, "Of course!! Personal reasons aside, the Black Mage is an evil being who wants to destroy Maple World. It's only right that an Onyx Dragon and his master stop him!!"
        ));
        sm.addExp(68100);
        sm.forceCompleteQuest(22596);
        sm.sayOk("Yeah! Master, you and I are on the same page! We lost to #p1013203# this time but next time we'll get him. We won't let the Black Wings revive the Black Mage! For the Onyx Dragons and for Maple World!");
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


    // ADDITIONAL EVAN QUESTS (23900-23968) ------------------------------------------------------------------------------------------------------------
    // Placeholder scripts for quests without detailed XML information

    @Script("q23903e")
    public static void q23903e(ScriptManager sm) {
        // Quest 23903 - Evan Quest (END)
        sm.forceCompleteQuest(23903);
    }

    @Script("q23907s")
    public static void q23907s(ScriptManager sm) {
        // Quest 23907 - Evan Quest (START)
        sm.forceStartQuest(23907);
    }

    @Script("q23909s")
    public static void q23909s(ScriptManager sm) {
        // Quest 23909 - Evan Quest (START)
        sm.forceStartQuest(23909);
    }

    @Script("q23928s")
    public static void q23928s(ScriptManager sm) {
        // Quest 23928 - Evan Quest (START)
        sm.forceStartQuest(23928);
    }

    @Script("q23961e")
    public static void q23961e(ScriptManager sm) {
        // Quest 23961 - Evan Quest (END)
        sm.forceCompleteQuest(23961);
    }

    @Script("q23961s")
    public static void q23961s(ScriptManager sm) {
        // Quest 23961 - Evan Quest (START)
        sm.forceStartQuest(23961);
    }

    @Script("q23963e")
    public static void q23963e(ScriptManager sm) {
        // Quest 23963 - Evan Quest (END)
        sm.forceCompleteQuest(23963);
    }

    @Script("q23963s")
    public static void q23963s(ScriptManager sm) {
        // Quest 23963 - Evan Quest (START)
        sm.forceStartQuest(23963);
    }

    @Script("q23968e")
    public static void q23968e(ScriptManager sm) {
        // Quest 23968 - Evan Quest (END)
        sm.forceCompleteQuest(23968);
    }

    @Script("q23968s")
    public static void q23968s(ScriptManager sm) {
        // Quest 23968 - Evan Quest (START)
        sm.forceStartQuest(23968);
    }
}
