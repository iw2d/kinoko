package kinoko.script.continent;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.util.Tuple;
import kinoko.world.job.Job;
import kinoko.world.quest.QuestRecordType;

import java.util.List;
import java.util.Map;

public final class ElNathMts extends ScriptHandler {
    @Script("getAboard")
    public static void getAboard(ScriptManager sm) {
        // Isa the Station Guide : Platform Usher (2012006)
        //   Orbis : Orbis Station Entrance (200000100)
        final List<Tuple<Integer, String>> platforms = List.of(
                Tuple.of(200000111, "Platform to Board a Ship to Victoria Island"),
                Tuple.of(200000121, "Platform to Board a Ship to Ludibrium"),
                Tuple.of(200000131, "Platform to Board a Ship to Leafre"),
                Tuple.of(200000141, "Platform to Ride a Crane to Mu Lung"),
                Tuple.of(200000151, "Platform to Ride a Genie to Ariant"),
                Tuple.of(200000161, "Platform to Board a Ship to Ereve"),
                Tuple.of(200000170, "Platform to Board a Ship to Edelstein")
        );
        final Map<Integer, String> options = createOptions(platforms, Tuple::getRight);
        final int answer = sm.askMenu("There are many Platforms at the Orbis Station. You must find the correct Platform for your destination. Which Platform would you like to go to?", options);
        if (answer >= 0 && answer < platforms.size()) {
            final int mapId = platforms.get(answer).getLeft();
            final String platform = platforms.get(answer).getRight();
            if (sm.askYesNo(String.format("Even if you took the wrong passage you can get back here using the portal, so no worries. Will you move to the #b%s#k?", platform))) {
                sm.warp(mapId, "west00");
            }
        }
    }

    @Script("station_in")
    public static void station_in(ScriptManager sm) {
        // Orbis : Orbis Station Entrance (200000100)
        //   east00 (1219, 86)
        getAboard(sm);
    }

    @Script("oBoxItem0")
    public static void oBoxItem0(ScriptManager sm) {
        // oBoxItem0 (2002000)
        //   Orbis : Orbis (200000000)
        //   Orbis : Orbis Park (200000200)
        sm.dropRewards(List.of(
                Reward.money(20, 20, 0.7),
                Reward.item(2000000, 1, 1, 0.1), // Red Potion
                Reward.item(2000001, 1, 1, 0.1), // Orange Potion
                Reward.item(2010000, 1, 1, 0.1), // Apple
                Reward.item(4031198, 1, 1, 0.8, 3043) // Empty Potion Bottle
        ));
    }


    // AQUA ROAD SCRIPTS -----------------------------------------------------------------------------------------------

    @Script("aqua_taxi")
    public static void aqua_taxi(ScriptManager sm) {
        // Dolphin (2060009)
        //   Aquarium : Aquarium (230000000)
        //   Herb Town : Pier on the Beach (251000100)
        if (sm.getFieldId() == 230000000) {
            // Aquarium : Aquarium
            final int answer = sm.askMenu("Oceans are all connected to each other. Place you can't reach by foot can easily reached oversea. How about taking #bDolphin Taxi#k with us today?", Map.of(
                    0, "Go to the Sharp Unknown.",
                    1, "Go to Herb Town.",
                    2, "Go to the Sea of Fog"
            ));
            if (answer == 0) {
                if (sm.askYesNo("There is a fee of 1000 mesos. Would you like to go there now?")) {
                    if (sm.addMoney(-1000)) {
                        sm.warp(230030200); // Aqua Road : The Sharp Unknown
                    } else {
                        sm.sayNext("I don't think you have enough money...");
                    }
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            } else if (answer == 1) {
                if (sm.askYesNo("There is a fee of 10000 mesos. Would you like to go there now?")) {
                    if (sm.addMoney(-10000)) {
                        sm.warp(251000100); // // Herb Town : Pier on the Beach
                    } else {
                        sm.sayNext("I don't think you have enough money...");
                    }
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            } else if (answer == 2) {
                if (sm.askYesNo("Umm... You want to go to the Sea of Fog? I really don't think you should... Well... What I mean is... Do you want to go now?")) {
                    sm.warp(923020000); // Sea of Fog : Shipwrecked Ghost Ship
                    sm.setQRValue(QuestRecordType.UnityPortal, "");
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            }
        } else if (sm.getFieldId() == 251000100) {
            // Herb Town : Pier on the Beach
            final int answer = sm.askMenu("Oceans are all connected to each other. Place you can't reach by foot can easily reached oversea. How about taking #bDolphin Taxi#k with us today?", Map.of(
                    0, "Go to Aquarium."
            ));
            if (answer == 0) {
                if (sm.askYesNo("There is a fee of 10000 mesos. Would you like to go there now?")) {
                    if (sm.addMoney(-10000)) {
                        sm.warp(230000000); // Aquarium : Aquarium
                    } else {
                        sm.sayNext("I don't think you have enough money...");
                    }
                } else {
                    sm.sayOk("OK. If you ever change your mind, please let me know.");
                }
            }
        }
    }

    @Script("Pianus")
    public static void Pianus(ScriptManager sm) {
        // Aqua Road : The Dangerous Cave (230040410)
        //   boss00 (1090, 46)
        sm.playPortalSE();
        sm.warp(230040420, "out00"); // Aqua Road : The Cave of Pianus
    }

    @Script("aquaItem0")
    public static void aquaItem0(ScriptManager sm) {
        // aquaItem0 (2302000)
        //   Aqua Road : Ocean I.C (230010000)
        // Mushking Empire quest item drops
        sm.dropRewards(List.of(
                Reward.item(4031274, 1, 1, 0.2), // Paper A
                Reward.item(4031275, 1, 1, 0.2), // Paper B
                Reward.item(4031276, 1, 1, 0.2), // Paper C
                Reward.item(4031277, 1, 1, 0.2), // Paper D
                Reward.item(4031278, 1, 1, 0.2)  // Paper E
        ));
    }

    @Script("aquaItem1")
    public static void aquaItem1(ScriptManager sm) {
        // aquaItem1 (2302001)
        //   Aqua Road : Deep Sea Canyon 2 (230040100)
        sm.dropRewards(List.of(
                Reward.item(2022040, 1, 1, 0.3), // Bubble
                Reward.item(4031251, 1, 1, 0.2)  // Deep Sea Dust
        ));
    }

    @Script("aquaItem2")
    public static void aquaItem2(ScriptManager sm) {
        // aquaItem2 (2302002)
        //   Hidden Street : Fish Resting Place (230030001)
        sm.dropRewards(List.of(
                Reward.item(2022040, 1, 1, 0.3) // Bubble
        ));
    }

    @Script("aquaItem3")
    public static void aquaItem3(ScriptManager sm) {
        // aquaItem3 (2302006)
        //   Aqua Road : Ocean I.C (230010000)
        //   Aqua Road : Crystal Gorge (230010100)
        //   Aqua Road : Red Coral Forest (230010200)
        //   Aqua Road : Turban Shell Hill (230010300)
        //   Aqua Road : Forked Road : West Sea (230010400)
        //   Aqua Road : Forked Road : East Sea (230020000)
        sm.dropRewards(List.of(
                Reward.item(4032476, 1, 1, 0.2, 22407) // Captain Alpha's Buckle
        ));
    }

    @Script("aquaItem4")
    public static void aquaItem4(ScriptManager sm) {
        // aquaItem4 (2302003)
        //   Hidden Street : Cold Cave (923000100)
        sm.dropRewards(List.of(
                Reward.item(4001108, 1, 1, 0.2), // Cold Fire
                Reward.item(4001107, 1, 1, 0.2), // Black Book
                Reward.item(4161017, 1, 1, 0.1)  // Calen's Notebook
        ));
    }

    @Script("aquaItem5")
    public static void aquaItem5(ScriptManager sm) {
        // aquaItem5 (2302005)
        //   Hidden Street : Boar Breeding Room (923010000)
        sm.dropRewards(List.of(
                Reward.item(4031508, 1, 1, 0.2) // Research Report
        ));
    }

    @Script("mistSeaReactor")
    public static void mistSeaReactor(ScriptManager sm) {
        // mistSeaReactor (2309000)
        //   Mist Sea : 5th Operation Room (923020114)
        // Triggers jump reactor in same map when activated
        sm.getField().getReactorPool().getBy(reactor -> "jump".equals(reactor.getName())).ifPresent(reactor -> {
            sm.getField().getReactorPool().hitReactor(sm.getUser(), reactor, 0);
        });
    }

    // THIEF 3RD JOB ADVANCEMENT NPC ---------------------------------------------------------------------------

    @Script("thief3")
    public static void thief3(ScriptManager sm) {
        // Arec : Shadow Instructor (2020011)
        //   El Nath : Chief's Residence (211000001)

        // Check for 3rd job advancement (Level 70, job 410/420/432)
        if (sm.getLevel() >= 70 && (sm.getJob() == 410 || sm.getJob() == 420 || sm.getJob() == 432)) {
            sm.sayNext("You're a strong and determined Thief. You have trained yourself well and are now ready to take the next step in your journey.");

            if (sm.askYesNo("You are ready to become a #b3rd job Thief#k. Would you like to make the job advancement now?")) {
                if (sm.getJob() == 410) {
                    // Assassin → Hermit
                    sm.setJob(Job.getById(411));
                    sm.sayNext("Congratulations! You are now a #bHermit#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 420) {
                    // Bandit → Chief Bandit
                    sm.setJob(Job.getById(421));
                    sm.sayNext("Congratulations! You are now a #bChief Bandit#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 432) {
                    // Blade Specialist → Blade Lord (Dual Blade 3rd job)
                    sm.setJob(Job.BLADE_LORD);
                    sm.sayNext("You passed the test. The fight with the master Thief has proven your own worth as a Thief. You are now a #bBlade Lord#k, and bring your might to a new level!");
                    sm.sayOk("Continue your training. When you reach Level 120, seek out #b#p2081400##k in Leafre for your final advancement.");
                }
            } else {
                sm.sayOk("Come back when you are ready to make the job advancement.");
            }
            return;
        }

        // Check for 4th job quest (Quest 6930, Level 120, job 411/421)
        if (sm.getLevel() >= 120 && (sm.getJob() == 411 || sm.getJob() == 421)) {
            // 4th job path - handled by q6930s quest script
            if (!sm.hasQuestStarted(6930) && !sm.hasQuestCompleted(6930)) {
                // Trigger quest 6930 start dialog
                sm.sayNext("Long time no see. I heard about you. You seem to have had a hard time. Did you find darkness within you? Then you must have a reason for being here. What can I do for you?");

                final int answer = sm.askMenu("I knew that you would return to see me someday. But I have no power to make your wish come true. Go to #bMinar Forest#k. If you find #b#p2081400##k, she may be able help you. Would you like to meet her?",
                    java.util.Map.of(0, "I want the 4th job advancement."));

                if (answer == 0) {
                    if (sm.askYesNo("I'll write a recommendation letter for you. Hope you get a new power.")) {
                        sm.forceStartQuest(6930);
                        sm.addItem(4031516, 1); // Letter of Introduction
                        sm.sayNext("Take this letter to #b#p2081400##k in Leafre. She will guide you on the path to ultimate power.");
                    } else {
                        sm.sayOk("Aren't you here for the 4th job advancement? If you don't want to that's fine.");
                    }
                }
                return;
            }
        }

        // Default message
        sm.sayOk("Continue your training. When you are strong enough, return to me for your next job advancement.");
    }

    @Script("warrior3")
    public static void warrior3(ScriptManager sm) {
        // Tylus : Warrior Job Instructor (2020008)
        //   El Nath : Chief's Residence (211000001)

        // Check for 3rd job advancement (Level 70, job 110/120/130)
        if (sm.getLevel() >= 70 && (sm.getJob() == 110 || sm.getJob() == 120 || sm.getJob() == 130)) {
            sm.sayNext("You have proven yourself as a strong and dedicated Warrior. You are now ready to take the next step on your path.");

            if (sm.askYesNo("You are ready to become a #b3rd job Warrior#k. Would you like to make the job advancement now?")) {
                if (sm.getJob() == 110) {
                    // Fighter → Crusader
                    sm.setJob(Job.getById(111));
                    sm.sayNext("Congratulations! You are now a #bCrusader#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 120) {
                    // Page → White Knight
                    sm.setJob(Job.getById(121));
                    sm.sayNext("Congratulations! You are now a #bWhite Knight#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 130) {
                    // Spearman → Dragon Knight
                    sm.setJob(Job.getById(131));
                    sm.sayNext("Congratulations! You are now a #bDragon Knight#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                }
            } else {
                sm.sayOk("Come back when you are ready to make the job advancement.");
            }
            return;
        }

        // Check for 4th job quest (Quest 6900, Level 120, job 111/121/131)
        if (sm.getLevel() >= 120 && (sm.getJob() == 111 || sm.getJob() == 121 || sm.getJob() == 131)) {
            // 4th job path - handled by q6900s quest script
            if (!sm.hasQuestStarted(6900) && !sm.hasQuestCompleted(6900)) {
                // Trigger quest 6900 start dialog
                sm.sayNext("It's been a quite a while since I've last seen you. I'm happy to see you improved so much. Do you realize the hidden strength within you? You must have some reason to see me. What can I do for you?");

                final int answer = sm.askMenu("I knew that you would return to see me someday. But I have no power to make your wish come true. Go to #bMinar Forest#k. If you find #b#p2081100##k, she may be able help you. Would you like to meet her?",
                    java.util.Map.of(0, "I want the 4th job advancement."));

                if (answer == 0) {
                    if (sm.askYesNo("I'll recommend you to him. Hope you get stronger!")) {
                        sm.forceStartQuest(6900);
                        sm.addItem(4031342, 1); // Letter of Introduction
                        sm.sayNext("Remember. Bishop of Minar forest, #b#p2081100##k. Please see him.");
                    } else {
                        sm.sayOk("Aren't you here to do the 4th job advancement? If not, that's fine.");
                    }
                }
                return;
            }
        }

        // Default message
        sm.sayOk("Continue your training. When you are strong enough, return to me for your next job advancement.");
    }

    @Script("wizard3")
    public static void wizard3(ScriptManager sm) {
        // Robeira : Magician Job Instructor (2020009)
        //   El Nath : Chief's Residence (211000001)

        // Check for 3rd job advancement (Level 70, job 210/220/230)
        if (sm.getLevel() >= 70 && (sm.getJob() == 210 || sm.getJob() == 220 || sm.getJob() == 230)) {
            sm.sayNext("Your magical prowess has grown significantly. You have mastered the fundamentals and are ready to unlock greater power.");

            if (sm.askYesNo("You are ready to become a #b3rd job Magician#k. Would you like to make the job advancement now?")) {
                if (sm.getJob() == 210) {
                    // F/P Wizard → F/P Mage
                    sm.setJob(Job.getById(211));
                    sm.sayNext("Congratulations! You are now a #bFire/Poison Mage#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 220) {
                    // I/L Wizard → I/L Mage
                    sm.setJob(Job.getById(221));
                    sm.sayNext("Congratulations! You are now an #bIce/Lightning Mage#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 230) {
                    // Cleric → Priest
                    sm.setJob(Job.getById(231));
                    sm.sayNext("Congratulations! You are now a #bPriest#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                }
            } else {
                sm.sayOk("Come back when you are ready to make the job advancement.");
            }
            return;
        }

        // Check for 4th job quest (Quest 6910, Level 120, job 211/221/231)
        if (sm.getLevel() >= 120 && (sm.getJob() == 211 || sm.getJob() == 221 || sm.getJob() == 231)) {
            // 4th job path - handled by q6910s quest script
            if (!sm.hasQuestStarted(6910) && !sm.hasQuestCompleted(6910)) {
                // Trigger quest 6910 start dialog
                sm.sayNext("Long time no see. I'm happy to see you improved. Did you find the truth in your mind? Then you must have some reason to be here. What can I do for you?");

                final int answer = sm.askMenu("Yes. I was expecting you. But I don't have enough power to help you. Go to #bMinar Forest#k. #b#p2081200##k will help make your dream come true. Do you want to see him?",
                    java.util.Map.of(0, "I want the 4th job advancement."));

                if (answer == 0) {
                    if (sm.askYesNo("Then I'll recommend you to him. Don't be rude to him. May you find the power you seek!")) {
                        sm.forceStartQuest(6910);
                        sm.addItem(4031510, 1); // Letter of Introduction
                        sm.sayNext("Remember. The bishop of Minar Forest, #b#p2081200##k. Please see him.");
                    } else {
                        sm.sayOk("Aren't you here for the 4th job advancement? If you don't want it, that's fine.");
                    }
                }
                return;
            }
        }

        // Default message
        sm.sayOk("Continue your training. When you are strong enough, return to me for your next job advancement.");
    }

    @Script("bowman3")
    public static void bowman3(ScriptManager sm) {
        // Rene : Bowman Job Instructor (2020010)
        //   El Nath : Chief's Residence (211000001)

        // Check for 3rd job advancement (Level 70, job 310/320)
        if (sm.getLevel() >= 70 && (sm.getJob() == 310 || sm.getJob() == 320)) {
            sm.sayNext("Your archery skills have reached an impressive level. You are now ready to advance to the next stage of your journey.");

            if (sm.askYesNo("You are ready to become a #b3rd job Bowman#k. Would you like to make the job advancement now?")) {
                if (sm.getJob() == 310) {
                    // Hunter → Ranger
                    sm.setJob(Job.getById(311));
                    sm.sayNext("Congratulations! You are now a #bRanger#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 320) {
                    // Crossbowman → Sniper
                    sm.setJob(Job.getById(321));
                    sm.sayNext("Congratulations! You are now a #bSniper#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                }
            } else {
                sm.sayOk("Come back when you are ready to make the job advancement.");
            }
            return;
        }

        // Check for 4th job quest (Quest 6920, Level 120, job 311/321)
        if (sm.getLevel() >= 120 && (sm.getJob() == 311 || sm.getJob() == 321)) {
            // 4th job path - handled by q6920s quest script
            if (!sm.hasQuestStarted(6920) && !sm.hasQuestCompleted(6920)) {
                // Trigger quest 6920 start dialog
                sm.sayNext("Long time no see. You remind me of the time when you came to me for the third advancement. Did you find the truth in your mind? You must have some reason to come to see me. What can I do for you?");

                final int answer = sm.askMenu("I knew that you would return to see me someday. But I have no power to make your wish come true. Go to #bMinar Forest#k. If you find #b#p2081300##k, he may be able help you. Would you like to meet him?",
                    java.util.Map.of(0, "I want the 4th job advancement."));

                if (answer == 0) {
                    if (sm.askYesNo("I'll write a recommendation letter for you. Hope you get a new power.")) {
                        sm.forceStartQuest(6920);
                        sm.addItem(4031513, 1); // Letter of Introduction
                        sm.sayNext("Remember. The bishop of Minar Forest, #b#p2081200##k. Please see him.");
                    } else {
                        sm.sayOk("Aren't you here for the 4th job advancement? If you don't want it, that's fine.");
                    }
                }
                return;
            }
        }

        // Default message
        sm.sayOk("Continue your training. When you are strong enough, return to me for your next job advancement.");
    }

    @Script("pirate3")
    public static void pirate3(ScriptManager sm) {
        // Pedro : Pirate Job Instructor (2020013)
        //   El Nath : Chief's Residence (211000001)

        // Check for 3rd job advancement (Level 70, job 510/520)
        if (sm.getLevel() >= 70 && (sm.getJob() == 510 || sm.getJob() == 520)) {
            sm.sayNext("You have honed your skills as a Pirate and proven yourself worthy of greater power. The time has come for you to advance.");

            if (sm.askYesNo("You are ready to become a #b3rd job Pirate#k. Would you like to make the job advancement now?")) {
                if (sm.getJob() == 510) {
                    // Brawler → Marauder
                    sm.setJob(Job.getById(511));
                    sm.sayNext("Congratulations! You are now a #bMarauder#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                } else if (sm.getJob() == 520) {
                    // Gunslinger → Outlaw
                    sm.setJob(Job.getById(521));
                    sm.sayNext("Congratulations! You are now an #bOutlaw#k. Continue your training and seek out the masters when you reach Level 120 for your final advancement.");
                }
            } else {
                sm.sayOk("Come back when you are ready to make the job advancement.");
            }
            return;
        }

        // Check for 4th job quest (Quest 6940, Level 120, job 511/521)
        if (sm.getLevel() >= 120 && (sm.getJob() == 511 || sm.getJob() == 521)) {
            // 4th job path - handled by q6940s quest script
            if (!sm.hasQuestStarted(6940) && !sm.hasQuestCompleted(6940)) {
                // Trigger quest 6940 start dialog
                sm.sayNext("It has been a long time. I have kept tabs on your steady progression. Seeing you standing before me, healthy and strong, I can sense that a lot has happened since our last encounter. Have you finally uncovered the freedom within you all this time? If so, then there must be a reason why you came all the way to see me. What is it?");

                final int answer = sm.askMenu("I see... I have known that this day will someday come. Unfortunately, I do not have the powers to fulfill your wish. In order for you to complete this process, you'll have to head over to the #bMinar Forst#k and meet #b#p2081500##k, who should be meditating as you walk in. He may be enough to fulfill your wish. Would you like to pay a visit?",
                    java.util.Map.of(0, "I'd like to make the 4th job advancement."));

                if (answer == 0) {
                    if (sm.askYesNo("I will write up a recommendation letter for you right now. I hope you come out of this with a wealth of new power at your disposal.")) {
                        sm.forceStartQuest(6940);
                        sm.addItem(4031859, 1); // Letter of Introduction
                        sm.sayNext("Remember the name. The priest of Minar Forest, #b#p2081500##k. Visit him.");
                    } else {
                        sm.sayOk("Aren't you here to see me to make the 4th job advancement? If not, then don't mind me.");
                    }
                }
                return;
            }
        }

        // Default message
        sm.sayOk("Continue your training. When you are strong enough, return to me for your next job advancement.");
    }
}
