package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.user.User;

import java.util.concurrent.atomic.AtomicBoolean;

public class AquaRoad extends ScriptHandler {
    @Script("s4common2")
    public static void s4common2(ScriptManager sm) {
        // TODO: Make more GMS-like
        AtomicBoolean allEligible = new AtomicBoolean(true);
        if (!sm.hasQuestStarted(6301)) {
            sm.sayOk("Cracked Dimension? Where did you hear that?");
            return;
        }

        if (!sm.getUser().getPartyInfo().isBoss()) {
            sm.sayOk("Only party leader can apply to enter. Please get your representative to talk to me.");
            return;
        }

        if (sm.getItemCount(4031472) > 40) {
            sm.sayOk("If you have 40 " + blue(itemName(4031472)) + ", you need no more.");
            return;
        }

        if (!sm.hasItem(4000175, 1)) {
            sm.sayOk("Without " + blue(itemName(4000175)) + ", you can't enter Cracked Dimension.");
            return;
        }

        if (!sm.checkParty(1, 10)) {
            sm.sayOk("You don't have a  party. You can challenge with party.");
            return;
        }

        sm.getField().getUserPool().forEachPartyMember(sm.getUser(), (member) -> {
            if (!member.is4thJob()) {
                allEligible.set(false);
            }
        });

        if (!sm.getUser().is4thJob() || !allEligible.get()) {
            sm.sayOk("You can't enter if anyone in your party hasn't make 4th job advancement.");
            return;
        }

        sm.removeItem(4000175, 1);
        sm.partyWarpInstance(923000000, "sp", 230040001, 60 * 5);
    }

}
