package kinoko.script;

import kinoko.provider.reward.Reward;
import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

import java.util.List;

/**
 * Harvest Reactor Scripts
 * - Coconut trees, herbs, ore veins, etc.
 */
public final class HarvestReactor extends ScriptHandler {

    // COCONUT TREES (Florina Beach) ===============================================================================

    @Script("coconut0")
    public static void coconut0(ScriptManager sm) {
        // Coconut Tree Reactors (1102000, 1102001, 1102002)
        // Maps: Florina Beach (110000000+)
        // Drop: 4000136 (Coconut) - Only for Quest 22573 (Tropical Fruit Punch)
        sm.dropRewards(List.of(
                Reward.item(4000136, 1, 1, 1.0, 22573) // Coconut - 100% drop when quest 22573 is active
        ));
    }

    // DUAL BLADE REACTORS =============================================================================

    @Script("dual_ball00")
    public static void dual_ball00(ScriptManager sm) {
        // Opalescent Marble Reactor - Dual Blade Quest 2363 "Time for the Awakening"
        // Map: Marble Room (910350000)
        // Drop: 2430071 (Opalescent Glass Marble) - Use this item to get Mirror of Insight
        sm.dropRewards(List.of(
                Reward.item(2430071, 1, 1, 1.0, 2363) // Opalescent Glass Marble - 100% drop when quest 2363 is active
        ));
    }
}
