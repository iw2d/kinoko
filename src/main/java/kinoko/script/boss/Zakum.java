/* Adobis
 *
 * El Nath: The Door to Zakum (211042300)
 *
 * Zakum Quest NPC

 * Custom Quest 100200 = whether you can do Zakum
 * Custom Quest 100201 = Collecting Gold Teeth <- indicates it's been started
 * Custom Quest 100203 = Collecting Gold Teeth <- indicates it's finished
 * Quest 7000 - Indicates if you've cleared first stage / fail
 * 4031061 = Piece of Fire Ore - stage 1 reward
 * 4031062 = Breath of Fire    - stage 2 reward
 * 4001017 = Eye of Fire       - stage 3 reward
 * 4000082 = Zombie's Gold Tooth (stage 3 req)
 */

package kinoko.script.boss;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.mob.MobType;
import kinoko.world.quest.QuestRecordType;
import kinoko.world.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Zakum extends ScriptHandler {
    final static int FIRST_STAGE_MAP = 280010000;
    final static int ZAKUM_BOSS_MAP = 280030000;
    @Script("Zakum00")
    public static void zakum00(ScriptManager sm) {
        // Adobis (2030008)
        //   El Nath : The Door to Zakum (211042300)
        //   Dead Mine : The Door to Chaos Zakum (211042301)
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        if (sm.getLevel() < 50) {
            sm.sayOk("Please come back to me when you've become stronger.  I've seen a few adventurers in my day, and you're far too weak to complete my tasks.");
            return;
        }

        // Initial dialog
        sm.sayOk("Shhhh ... be quiet. Deep in the dungeon rests a powerful foe. Complete the quests in order of the level, and you'll be able to meet the boss of the Zakum Dungeon. It won't be easy, at all ... but try your best.");

        // Ask the player which quest they want to complete
        int choice = sm.askMenu("Well ... alright. You seem more than qualified for this. Which of these tasks do want to tackle on?#b", Map.of(
                0, "Explore the Dead Mine. (Level 1)",
                1, "Observe the Zakum Dungeon. (Level 2)",
                2, "Request for a refinery. (Level 3)",
                3, "Get briefed for the quest.",
                4, "Skip all quests (15,000,000 mesos)")
        );

        switch (choice) {
            case 0:
                if (sm.getQRValue(QuestRecordType.ZakumPreqStageOne).equals("3")) {
                    sm.sayOk("You have already looked through the Cave at the Dead Mine three times today and therefore I cannot let you in once more. Please come back tomorrow.");
                    return;
                }

                // Check if at a party of at least 1 with level 50
                if (!sm.checkParty(1, 50)) {
                    sm.sayOk("You are not currently in a party right now. You may only tackle this assignment as a party.");
                    return;
                }

                if(!sm.getUser().getPartyInfo().isBoss()) {
                    sm.sayNext("This journey will be a never ending maze of quests you won't be able to solve by yourself. But if you're willing to take on the challenge, then talk to the chief of your occupation at the Chief's Residence in El Nath to receive the quest.");
                    sm.sayBoth("After receiving the quest, either join a party or form one yourself, and have the leader of the party speak to me to start the quest. Once you are ready, have the leader of the party come up and talk to me.");
                    return;
                }

                sm.removeItem(4001015);
                sm.removeItem(4001016);
                sm.removeItem(4001018);
                sm.forceStartQuest(100200);
                for (var member : sm.getField().getUserPool().getPartyMembers(sm.getUser().getPartyId())) {
                    if (!member.getQuestManager().hasQuestStarted(7000000)) {
                        sm.message("There's a member of your party that hasn't received the quest from the chief of the occupation at El Nath.");
                        shouldStop.set(true);
                        break;
                    }
                }

                if (shouldStop.get()) {
                    return;
                }

                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    member.getInventoryManager().removeItem(4001015, member.getInventoryManager().getItemCount(4001015));
                    member.getInventoryManager().removeItem(4001016, member.getInventoryManager().getItemCount(4001016));
                    member.getInventoryManager().removeItem(4031061, member.getInventoryManager().getItemCount(4031061));
                    member.getQuestManager().forceStartQuest(100200);
                });

                sm.setQRValue(QuestRecordType.ZakumPreqStageOne, sm.getUser().getCharacterName());
                sm.playPortalSE();
                sm.partyWarpInstance(280010000, "st00", 211042300, 30 * 60);
                break;

            case 1:
                // Check if at a party of at least 1 with level 50
                if (!sm.checkParty(1, 50)) {
                    sm.sayOk("You are not currently in a party right now. You may only tackle this assignment as a party.");
                    return;
                }

                // Check if Quest 1 is completed
                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    if (!member.getQuestManager().hasQuestCompleted(100200)) {
                        shouldStop.set(true);
                    }
                });

                if (!sm.hasQuestCompleted(100200) || shouldStop.get()) {
                    sm.sayOk("It doesn't look like you or someone from your party have cleared the previous stage yet. Please beat the previous stage before moving onto the next level.");
                    return;
                }

                // Check if still in the middle of Quest 1
                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    if (member.getQuestManager().hasQuestStarted(100200)) {
                        shouldStop.set(true);
                    }
                });

                if(sm.hasQuestStarted(100200) || shouldStop.get()) {
                    sm.sayOk("It seems like you or someone from your party in the middle of the 1st stage. You must first clear this one before moving on to Level 2. Please clear the 1st stage first.");
                    return;
                }

                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    if (member.getQuestManager().hasQuestStarted(100201)) {
                        shouldStop.set(true);
                    }
                });

                if (sm.hasQuestStarted(100201) || shouldStop.get()) {
                    if(!sm.askYesNo("Hmmm ... you or someone from your party must have tried this quest before and gave up midway through. What do you think? Do you want to retry this level?")) {
                        sm.sayOk("I see ... but if you ever decide to change your mind, then talk to me.");
                        return;
                    }
                } else if (sm.hasQuestCompleted(100201) || shouldStop.get()) {
                    if(!sm.askYesNo("Hmmm ... You or someone from your party have already cleared this level before. For you to be rewarded again, you need to restart the quest from Level 1. Otherwise, you will still be able to do the quest but will not be rewarded. Do you still want to retry this level?")) {
                        sm.sayOk("I see ... but if you ever decide to change your mind, then talk to me.");
                        return;
                    }
                }

                if (!sm.askYesNo("You have safely cleared the 1st stage. There's still a long way to go before meeting the boss of Zakum Dungeon, however. So, what do you think? Are you ready to move on to the next stage?")) {
                    sm.sayOk("I see ... but if you ever decide to change your mind, then talk to me.");
                    return;
                }

                sm.sayNext("Alright! From here on out, you'll be transported to the map where obstacles will be aplenty. There will be a person standing at the deepest part of the map, and if you talk to her, you'll find an item that will be used as a material to create an item that summons the boss of Zakum Dungeon. Please get me that item. Good luck!");
                sm.forceStartQuest(100201);
                sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                    member.getQuestManager().forceStartQuest(100201);
                });

                sm.partyWarpInstance(280020000, "sp", 211042300, 30 * 60);

                break;

            case 2:
                // Check if at a party of at least 1 with level 50
                if (!sm.checkParty(1, 50)) {
                    sm.sayOk("You are not currently in a party right now. You may only tackle this assignment as a party.");
                    return;
                }

                // Same logic as Quest 2 for Quest 3
                if (!sm.hasQuestCompleted(100201)) {
                    sm.sayOk("Hmmm ... I don't think you have cleared the previous stage, yet. Please beat the previous stage before moving onto the next level.");
                    return;
                }

                if (sm.hasQuestStarted(100202)) {
                    if (!sm.hasItem(4000082, 30)) {
                        sm.sayOk("I don't think you have #b30 Zombie's Lost Gold Teeth#k yet. Gather them all up and I may be able to refine them and make a special item for you ...");
                        return;
                    }
                    sm.sayNext("Ha ha ha, don't worry, I'll make it in a heartbeat!");
                    if(!sm.hasItem(4000082, 30) || !sm.hasItem(4001018, 1) || !sm.hasItem(4031062, 1) || !sm.canAddItem(4001017, 1)) {
                        sm.sayOk("Hmmm... are you sure you have all the items required to make #rEye of Fire#k with you? If so, then please check and see if your etc. inventory is full or not.");
                        return;
                    }

                    sm.addItem(4001017, 5); // Eye of Fire
                    sm.forceCompleteQuest(100202);
                    sm.sayOk("Here it is. You will now be able to enter the alter of the Zakum Dungeon when the door on the left is open.. You'll need\\r\\n#b#t4001017##k with you in order to go through the door and enter the stage. Now, let's see how many can enter this place ...?");
                }
                else if (sm.hasQuestCompleted(100202)) {
                    if (!sm.askYesNo("Hmmm ... aren't you the one who refined #b#t4001017##k before? Then what can I do for you? Are you interested in mixing #b#t4031061##k with #b#t4031062##k again to create #b#t4001017##k?")) {
                        sm.sayOk("I see ... but please be aware that you won't be able to see the boss of Zakum Dungeon without the #b#t4001017##k.");
                        return;
                    }
                    sm.sayOk("Hmmm, by mixing #b#t4031061##k with #b#t4031062##k, I can make an the item that will be used as a sacrifice to summon the boss, called #b#t4001017##k. The problem is ... (cough cough) as you can see, I am not feeling terribly well these days, so it's difficult for me to move around and gather up items. Well ... will it be ok for you to gather up #b30 Zombie's Lost Gold Teeth#k for me? Don't ask me where I'll be using it, though ...");
                    sm.forceStartQuest(100202);
                }
                else {
                    sm.sayOk("Hmmm, by mixing #b#t4031061##k with #b#t4031062##k, I can make an the item that will be used as a sacrifice to summon the boss, called #b#t4001017##k. The problem is ... (cough cough) as you can see, I am not feeling terribly well these days, so it's difficult for me to move around and gather up items. Well ... will it be ok for you to gather up #b30 Zombie's Lost Gold Teeth#k for me? Don't ask me where I'll be using it, though ...");
                    sm.forceStartQuest(100202);
                }
                break;

            case 3:
                sm.sayNext("Not sure where to start? In order to do this quest, you'll have to receive the approval from the chief of your occupation. I do not want to be scolded later on for letting someone in without going through the proper procedure. The only ones that I can let in are the party full of members that have received the approval.");
                sm.sayBoth("Complete the quests in order of the level, and you'll be able to meet the boss of the Zakum Dungeon. Gather up the items I'll request from you, and I'll make them into a sacrificial item. Place the sacrificial item at the altar, and you'll get to see what you've come to see. To do that, first look through the Dead Mine and bring back #b#t4001018##k.");
                sm.sayBoth("There, other than #b#t4001018##k, you'll also find Paper documents. Give that to #b#p2032002##k, and you may get something helpful in return along with Piece of Fire ore. Next, go across the lava area and find #b#t4031062##k. It'll be a treacherous road to take, but ... it's a must item, in terms of making a sacrificical item.");
                sm.sayBoth("Once you have gotten #b#t4031062##k, you'll need to refine the #bPieces of Fire ore#k and #b#t4031062#s#k that you have acquired at level 1 and 2. Don't worry about it, though; I can refine them for you. Once you've completed them all, all you'll have left to do is to meet the boss of Zakum Dungeon. It won't be easy, at all ... but try your best.");
                break;

            case 4:
                // Skip all quests with 15M mesos
                if (sm.hasQuestCompleted(100202)) {
                    sm.sayOk("You've already completed all the quests. You don't need to skip anything!");
                    return;
                }

                if (!sm.askYesNo("So, you wish to skip all the tedious quest work? I can give you #b5 Eyes of Fire#k right now if you pay me #e15,000,000 mesos#n. This will save you all the trouble of going through the three stages. Do you accept?")) {
                    sm.sayOk("Very well. Come back if you change your mind.");
                    return;
                }

                if (!sm.addMoney(-15000000)) {
                    sm.sayOk("You don't have enough mesos. I need exactly #e15,000,000 mesos#n to give you the Eyes of Fire.");
                    return;
                }

                if (!sm.canAddItem(4001017, 5)) {
                    sm.addMoney(15000000); // Refund
                    sm.sayOk("Your ETC inventory is full. Please make room and come back.");
                    return;
                }

                sm.addItem(4001017, 5); // Give 5x Eye of Fire
                sm.forceCompleteQuest(100200); // Complete stage 1
                sm.forceCompleteQuest(100201); // Complete stage 2
                sm.forceCompleteQuest(100202); // Complete stage 3
                sm.sayOk("Here are your #b5 Eyes of Fire#k. You may now enter the Zakum altar through the portal on the left. Good luck!");
                break;

            default:
                return;
        }
    }

    @Script("Zakum01")
    public static void zakum01(ScriptManager sm) {
        // Aura (2032002)
        //   Adobis's Mission I : Unknown Dead Mine (280010000)
        sm.sayNext("You are the one who wanted to investigate the Dead Mine. You need to gather up the necessary items to reach the point of your final goal: meeting the boss of the Zakum Dungeon. To obtain that item, you'll first need to acquire the materials for that item, right? You can get one of the materials, #b#t4031061##k, right here. It won't be easy, though ...");
        sm.sayNext("Here, there is an entrance that leads to numerous caves. Once inside the cave, you'll see some boxes. Destroy them all, and collect #b7 of #t4001016#s#k. The box cannot be destroyed using attack skills; only the regular, basic attack works. Afterwards, gather up the 7 keys, move into the innermost room, where the treasure chest is. Drop the keys there to obtain #b#t4031061##k. It'll take some time after dropping the keys to obtain it, so be patient.");
        sm.sayNext("Of course, not every box contains #t4001016#. You'll all run into some very unexpected circumstances, so please be aware of that. Every once in a while, in the middle of going through the boxes, #t4001015# will pop out. Gather those up, too, and something good will definitely happen. You need to collect at least 30 #t4001015#s. This is all I can tell you, for now.");
        final int answer = sm.askMenu("Anything do you want to ask?", Map.of(
                0, "I brought #t4031061#.",
                1, "Forget the quest, I'm out of here."
        ));

        if(answer == 0) {
            if (!sm.getQRValue(QuestRecordType.ZakumPreqStageOne).equals(sm.getUser().getCharacterName())) {
                sm.sayOk("Once you have obtained #b#t4031061##k by dropping 7 #b#t4001016#s#k at the huge chest in the cave, please hand the item over to the party leader. Once the leader of the party has #b#t4031061##k in possession and talks to me, that'll signal that you have cleared Level 1.");
                return;
            }

            if(!sm.hasItem(4031061, 1)) {
                sm.sayOk("I guess you haven't gotten #b#t4031061##k yet. Please go through the various treasure chests in here within the time limit, collect #b7 of #t4001016#s#k, and drop them all at the treasure chest in the innermost part of the cave to collect #b#t4031061##k. Once you have obtained the item, please hand it to me.");
                return;
            }

            if(!sm.hasItem(4001015)) {
                if(!sm.askYesNo("You brought back #b1 #t4031061##k safely, but it doesn't look like you have brought #b#t4001015# back. Is this all your party has gathered up?")) {
                    sm.sayOk("All the items collected from the cave by the party members should be given to the party leader, who'll give them all to me. Please double-check.");
                    return;
                }
            } else {
                if (!sm.askYesNo("You have brought back #b1 #t4031061##k and #b" + sm.getItemCount(4001015) + " #t4001015#s#k. Is this all the items your party members have gathered up?")) {
                    sm.sayOk("All the items collected from the cave by the party members should be given to the party leader, who'll give them all to me. Please double-check.");
                    return;
                }
            }

            if(!sm.removeItem(4031061, 1)) {
                sm.sayOk("Please check and see if you have #b1 #t4031061##k with you.");
                return;
            }

            sm.sayOk("Alright. Using the portal that's been made down there, you can return to the map where Adobis is. While using the portal, I'll be handing out #b#t4001018##k made out of #b#t4031061##k you've all given me to each and every member of the party. Congratulations on clearing Level 1. See you around ...");

            sm.forceCompleteQuest(100200);
            sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
                member.getQuestManager().forceCompleteQuest(100200);
            });
        } else if(answer == 1) {
            if (sm.askYesNo("If you quit in the middle of a mission, you'll have to start all over again ... not only that, but since it's a party quest, even if one player decides to leave, it may be difficult to clear the level. Are you SURE you want to leave?")) {
                sm.sayOk("Alright, I'll send you to the Exit Map. #b#p2030011##k will be there standing. Go talk to him; He'll let you out. So long...");
                sm.partyWarp(280090000, "st00");
            }
        }
    }

    @Script("go280010000")
    public static void go280010000(ScriptManager sm) {
        // go280010000 (2110000)
        //   Adobis's Mission I : Area 1-2 (280010011)
        //   Adobis's Mission I : Area 3-2 (280010031)
        //   Adobis's Mission I : Area 4-2 (280010041)
        //   Adobis's Mission I : Area 7-2 (280010071)
        //   Adobis's Mission I : Area 8-2 (280010081)
        //   Adobis's Mission I : Area 9-2 (280010091)
        //   Adobis's Mission I : Area 11-1 (280010110)
        //   Adobis's Mission I : Area 14-1 (280010140)
        //   Adobis's Mission I : Area 16 <A Dead Mine Somewhere> (280011000)
        //   Adobis's Mission I : Area 16-1 (280011001)
        //   Adobis's Mission I : Area 16-2 (280011002)
        //   Adobis's Mission I : Area 16-3 (280011003)
        //   Adobis's Mission I : Area 16-4 (280011004)
        //   Adobis's Mission I : Area 16-5 (280011005)
        //   Adobis's Mission I : Area 16-6 (280011006)
        sm.warp(280010000);
    }

    @Script("boxBItem0")
    public static void boxbitem0(ScriptManager sm) {
        // boxBItem0 (2112014)
        //   Adobis's Mission I : Area 16-5 (280011005)
        sm.dropRewards(List.of(
                Reward.item(4031061, 1, 1, 1)
        ));
    }

    @Script("Zakum03")
    public static void zakum03(ScriptManager sm) {
        // Adobis's Mission I : Unknown Dead Mine (280010000)
        //   ps01 (440, 193)
        if(sm.hasQuestCompleted(100200)) {
            if(!sm.canAddItem(4001018, 1)) {
                sm.sayOk("Please make room for the #b#t4001018##k.");
                return;
            }

            sm.addItem(4001018, 1);
            sm.warp(211042300, "sp");
        } else {
            sm.message("Currently, this portal doesn't work.");
        }
    }

    @Script("Zakum04")
    public static void zakum04(ScriptManager sm) {
        // Ali (2030011)
        //   Adobis's Mission I : The Room of Tragedy (280090000)
        if(sm.hasItem(4031061, 1)) {
            sm.sayOk("Great job clearing level 1! Alright ... I'll send you off to where #b#p2030008##k is. Before that!! Please be aware that the various, special items you have acquired here will not be carried out of here. I'll be taking away those items from your item inventory, so remember that. See ya!");
        } else {
            sm.sayOk("Must have quit midway through. Alright, I'll send you off right now. Before that!! Please be aware that the various, special items you have acquired here will not be carried out of here. I'll be taking away those items from your item inventory, so remember that. See ya!");
        }
        sm.removeItem(4001015);
        sm.removeItem(4001016);
        sm.removeItem(4031061);
        sm.warp(211042300);
    }

    @Script("boxKey0")
    public static void boxKey0(ScriptManager sm) {
        // boxKey0 (2112004)
        //   Adobis's Mission I : Area 9-2 (280010091)
        //   Adobis's Mission I : Area 11-1 (280010110)
        //   Adobis's Mission I : Area 14-1 (280010140)
        //   Adobis's Mission I : Area 16-2 (280011002)
        //   Adobis's Mission I : Area 16-3 (280011003)
        // boxKey0 (2112011)
        //   Adobis's Mission I : Area 4-2 (280010041)
        //   Adobis's Mission I : Area 16-5 (280011005)
        sm.dropRewards(List.of(
                Reward.item(4001016, 1, 1, 1)
        ));
    }

    @Script("Zakum02")
    public static void zakum02(ScriptManager sm) {
        // Lira (2032003)
        //   Adobis's Mission I : Breath of Lava <Level 2> (280020001)
        sm.sayNext("How did you go through such treacherous road to get here?? Incredible! #b#t4031062##k is here. Please give this to my brother. You'll finally be meeting up with the one you've been looking for, very soon.");
        if(!sm.canAddItem(4031062, 1)) {
            sm.sayOk("Your ETC inventory seems to be full. Please make room in order to receive the item.");
            return;
        }

        sm.addItem(4031062, 1);
        sm.forceCompleteQuest(100201);
        sm.addExp(15000);
        sm.warp(211042300);
    }

    @Script("Zakum06")
    public static void zakum06(ScriptManager sm) {
        // Amon (2030010)
        //   Adobis's Mission I : Breath of Lava <Level 1> (280020000)
        //   Adobis's Mission I : Breath of Lava <Level 2> (280020001)
        //   Last Mission : akum's Altar (280030000)
        //   Last Mission : Chaos Zakum's Altar (280030001)
        if (sm.getFieldId() == 280030000) {
            boolean exit = false;
            if(sm.getQRValue(QuestRecordType.Zakum).equals("1")) {
                exit = sm.askYesNo("Are you sure you want to leave this place? You are entitled to enter the Zakum Altar up to twice a day, and by leaving right now, you may only re-enter this shrine once more for the rest of the day.");
            } else if(sm.getQRValue(QuestRecordType.Zakum).equals("2")) {
                exit = sm.askYesNo("Are you sure you want to leave this place? You are entitled to enter the Zakum Altar up to twice a day, and since you have been here twice already, you will be denied entrance to this shrine for the rest of the day by leaving right now.");
            } else {
                sm.sayOk("How did you??? This is bonkers. Get out of here...");
                exit = true;
            }

            if (exit) {
                sm.partyWarp(211042300, "sp");
            }
        } else {
            if (sm.askYesNo("Are you sure you want to quit and leave this place? Next time you come back in, you'll have to start all over again.")) {
                sm.partyWarp(211042300, "sp");
            }
        }
    }

    @Script("Zakum05")
    public static void zakum05(ScriptManager sm) {
        // El Nath : The Door to Zakum (211042300)
        //   ps00 (-722, -217)
        // Dead Mine : The Door to Chaos Zakum (211042301)
        //   ps00 (-722, -217)
        if(!sm.hasQuestCompleted(100202)) {
            sm.sayOk("You may only enter this place after clearing level 3. You'll also need to have the Eye of Fire in possession.");
            return;
        }

        sm.setQRValue(QuestRecordType.Zakum, "1");
        sm.playPortalSE();
        sm.partyWarpInstance(ZAKUM_BOSS_MAP, "st00", 211042301, 60 * 60);
    }

    @Script("boss")
    public static void boss(ScriptManager sm) {
        // boss (2111001)
        //   Last Mission : Zakum's Altar (280030000)
        sm.soundEffect("Bgm06/FinalFight");
        sm.broadcastMessage("Zakum is summoned by the force of eye of fire.");
        sm.spawnMob(8800000, MobAppearType.SUSPENDED, -11, -215, false, MobType.PARENT_MOB);
        for (int i = 0; i < 8; i++) {
            sm.spawnMob(8800003 + i, MobAppearType.REGEN, -11, -215, false, MobType.SUB_MOB);
        }
    }
}
