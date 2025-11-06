package kinoko.handler.stage;

import kinoko.provider.GachaponProvider;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.reward.Reward;
import kinoko.util.Tuple;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class GachaponHandler {
    private static final Logger log = LogManager.getLogger(GachaponHandler.class);

    public static Tuple<Integer, Integer> rollGachapon(String gachaponName) {
        List<Reward> rewards = GachaponProvider.getGachaponRewards(gachaponName);
        if (rewards.isEmpty()) {
            throw new IllegalArgumentException("No rewards available for Gachapon: " + gachaponName);
        }

        // Calculate the sum of all weights
        double totalWeight = rewards.stream()
                .mapToDouble(Reward::getProb)
                .sum();

        // Generate a random value between 0 and the total weight
        double randomValue = Math.random() * totalWeight;

        // Find the reward that corresponds to the random value
        double cumulativeWeight = 0.0;
        for (Reward reward : rewards) {
            cumulativeWeight += reward.getProb();
            if (randomValue <= cumulativeWeight) {
                // We found our reward
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
                if (itemInfoResult.isEmpty()) {
                    // If the item doesn't exist, try again
                    return rollGachapon(gachaponName);
                }
                final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
                return Tuple.of(reward.getItemId(), quantity);
            }
        }

        // Fallback
        Reward mostProbableReward = rewards.stream()
                .max(Comparator.comparingDouble(Reward::getProb))
                .orElse(rewards.getFirst());
        return Tuple.of(mostProbableReward.getItemId(),
                Util.getRandom(mostProbableReward.getMin(), mostProbableReward.getMax()));
    }
}
