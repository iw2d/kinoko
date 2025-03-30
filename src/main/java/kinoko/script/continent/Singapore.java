package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.field.mob.MobAppearType;

import java.util.concurrent.atomic.AtomicBoolean;

public class Singapore extends ScriptHandler {
    @Script("treeboss00")
    public static void treeboss00(ScriptManager sm) {
        AtomicBoolean allEligible = new AtomicBoolean(true);
        Field krexMap = sm.getField().getFieldStorage().getFieldById(541020800).orElseThrow();

        if (!sm.checkParty(1, 70)) {
            sm.message("You must be in a party of at least 1 with minimum level of 70.");
            return;
        }

        if (!sm.getUser().isPartyBoss()) {
            sm.message("Only the party leader can get you inside.");
        }

        if (!sm.hasQuestStarted(4530)) {
            sm.message("Not all party members are eligible to get inside.");
            return;
        }

        sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
            try (var lockedMember = member.acquire()) {
                if (!member.getQuestManager().hasQuestStarted(4530)) {
                    sm.message("Not all party members are eligible to get inside.");
                    allEligible.set(false);
                }
            }
        });

        if (allEligible.get()) {
            sm.partyWarpInstance(krexMap.getFieldId(), "sp", 541020700, 60 * 60);
        }
    }

    @Script("treeboss01")
    public static void treeboss01(ScriptManager sm) {
        if (sm.askYesNo("Would you like to leave this place?")) {
            sm.warp(541020700);
        }
    }

    @Script("treeBossSG")
    public static void treeBossSG(ScriptManager sm) {
        sm.soundEffect("Bgm09/TimeAttack");
        sm.spawnMob(9420520, MobAppearType.NORMAL, sm.getSource().getX(), sm.getSource().getY(), true);
        sm.broadcastMessage("As you wish, here comes Krexel.");
    }
}
