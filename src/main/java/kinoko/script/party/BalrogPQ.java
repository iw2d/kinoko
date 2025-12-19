package kinoko.script.party;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.server.event.EventType;
import kinoko.server.node.ServerExecutor;
import kinoko.world.BossConstants;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.mob.MobType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BalrogPQ extends ScriptHandler {
    @Script("balog_accept")
    public static void balog_accept(ScriptManager sm) {
        if (sm.getFieldId() == BossConstants.BALROG_ENTRY_MAP) {
            final int selection = sm.askMenu("Do you want to head to the '#bBalrog's Tomb#k' to fight the\r\n" + blue("Balrog") + "?\r\n", Map.of(
                    0, "Go to the Balrog's Tomb (Easy Mode) " + red("(Lv. 50+)"),
                    1, "Go to the Balrog's Tomb (Hard Mode) " + red("(Lv. 70+)"),
                    2, "Never mind."
            ));

            if (selection != 2) {
                if (!sm.getUser().getPartyInfo().isBoss()) {
                    sm.sayOk("Please have your party leader talk to me if you wish to face " + blue("Balrog") + ".");
                    return;
                }

                if (!sm.checkParty(1, selection == 0 ? 50 : 70)) {
                    sm.sayOk("One or more party members are lacking the prerequisite entry quests, or are below level " + blue(selection == 0 ? "50" : "70") + ".");
                    return;
                }

                if (sm.partyHasCoolDown(EventType.PQ_BALROG, BossConstants.BALROG_RUNS_PER_DAY)) {
                    String timeUntilReset = sm.getTimeUntilEventReset(EventType.PQ_BALROG);
                    sm.sayOk("You or one of your party member has already attempted facing \r\n"  + blue("Balrog") + " within the past 6 Hours.\r\n You have " + timeUntilReset + " left on your cooldown.");
                    return;
                }

                sm.partyWarpInstance(BossConstants.BALROG_NORMAL_BATTLE_MAP, "sp", BossConstants.BALROG_ENTRY_MAP, BossConstants.BALROG_TIME_LIMIT);
                sm.addCooldownTimeForParty(EventType.PQ_BALROG, BossConstants.BALROG_COOLDOWN);
            }
        }
    }

    @Script("easy_balog_summon")
    public static void easy_balog_summon(ScriptManager sm) {
        if (sm.getUser().getPartyInfo().isBoss()) {
            sm.spawnMob(BossConstants.BALROG_NORMAL_BODY, MobAppearType.SUSPENDED, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, true);
            sm.spawnMob(8830009, MobAppearType.NORMAL, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, true);
            sm.spawnMob(8830013, MobAppearType.NORMAL, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, true);

            ServerExecutor.schedule(sm.getUser(), () -> {
                sm.killMob(8830013);
            }, BossConstants.BALROG_RELEASE_LEFT_CLAW_INTERVAL, TimeUnit.SECONDS);
        }
    }

    @Script("balog_summon")
    public static void balog_summon(ScriptManager sm) {
        List<Integer> spawns = List.of(
                BossConstants.BALROG_MYSTIC_BODY,
                BossConstants.BALROG_LEFT_ARM,
                BossConstants.BALROG_RIGHT_ARM,
                8830003
        );

        for (Integer spawn : spawns) {
            sm.spawnMob(spawn, MobAppearType.REGEN, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, false);
        }

//        sm.spawnMob(BossConstants.BALROG_MYSTIC_BODY, MobAppearType.SUSPENDED, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, true, MobType.PARENT_MOB);
//        sm.spawnMob(BossConstants.BALROG_RIGHT_ARM, MobAppearType.REGEN, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, true, MobType.SUB_MOB);
//        sm.spawnMob(BossConstants.BALROG_RIGHT_ARM, MobAppearType.REGEN, BossConstants.BALROG_SPAWN_X, BossConstants.BALROG_SPAWN_Y, true, MobType.SUB_MOB);
    }

    @Script("balog_buff")
    public static void balog_buff(ScriptManager sm) {

    }

    @Script("balog_InOut")
    public static void balog_InOut(ScriptManager sm) {
        if (sm.askYesNo("Are you sure you want to leave the battlefield?")) {
            sm.warp(BossConstants.BALROG_ENTRY_MAP);
        }
    }

    @Script("balog_bonusSetting")
    public static void balog_bonusSetting(ScriptManager sm) {

    }

    @Script("balog_dateSet")
    public static void balog_dateSet(ScriptManager sm) {

    }
}
