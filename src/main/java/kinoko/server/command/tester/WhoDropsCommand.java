package kinoko.server.command.tester;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.RewardProvider;
import kinoko.provider.StringProvider;
import kinoko.provider.reward.Reward;
import kinoko.server.command.Arguments;
import kinoko.server.command.Command;
import kinoko.world.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class WhoDropsCommand {
    /**
     * Finds which mobs drop a specific item.
     * Usage: !whodrops <item ID>
     */
    @Command("whodrops")
    @Arguments("item ID")
    public static void whoDrops(User user, String[] args) {
        if (args.length < 2) {
            user.systemMessage("Usage: !whodrops <item ID>");
            return;
        }

        final int itemId;
        try {
            itemId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            user.systemMessage("Invalid item ID: '%s'. Please provide a valid number.", args[1]);
            return;
        }

        final String itemName = StringProvider.getItemName(itemId);
        if (itemName.isEmpty()) {
            user.systemMessage("Could not find item with ID: %d", itemId);
            return;
        }

        // Search through all mob rewards
        final List<Map.Entry<Integer, Double>> droppedBy = new ArrayList<>();
        for (Map.Entry<Integer, List<Reward>> entry : RewardProvider.getAllMobRewards().entrySet()) {
            final int mobId = entry.getKey();
            for (Reward reward : entry.getValue()) {
                if (reward.getItemId() == itemId) {
                    droppedBy.add(Map.entry(mobId, reward.getProb()));
                    break; // Only count each mob once
                }
            }
        }

        if (droppedBy.isEmpty()) {
            user.systemMessage("No mobs drop '%s' (ID: %d)", itemName, itemId);
            return;
        }

        // Sort by mob ID
        droppedBy.sort(Comparator.comparingInt(Map.Entry::getKey));

        user.systemMessage("Item: %s (ID: %d)", itemName, itemId);
        user.systemMessage("Dropped by %d mob(s):", droppedBy.size());
        for (Map.Entry<Integer, Double> mobEntry : droppedBy) {
            final int mobId = mobEntry.getKey();
            final double dropChance = mobEntry.getValue();
            final String mobName = StringProvider.getMobName(mobId);
            user.systemMessage("  %d - %s (%.2f%% chance)", mobId, mobName, dropChance);
        }
    }
}
