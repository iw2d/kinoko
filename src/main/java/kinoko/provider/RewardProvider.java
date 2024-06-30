package kinoko.provider;

import kinoko.provider.reward.Reward;
import kinoko.server.ServerConfig;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.Mob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class RewardProvider implements DataProvider {
    public static final Path MOB_REWARD = Path.of(ServerConfig.DATA_DIRECTORY, "mob_reward.csv");
    private static final Map<Integer, Set<Reward>> mobRewards = new HashMap<>(); // mobId -> mob rewards

    public static void initialize() {
        try {
            // Mob rewards
            DataProvider.readData(MOB_REWARD).forEach((props) -> {
                final int mobId = Integer.parseInt(props.get(0));
                final Reward reward = getReward(props);
                if (!mobRewards.containsKey(mobId)) {
                    mobRewards.put(mobId, new HashSet<>());
                }
                mobRewards.get(mobId).add(reward);
            });
            for (var entry : mobRewards.entrySet()) {
                mobRewards.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception caught while loading Reward Data", e);
        }
    }

    public static Set<Reward> getMobRewards(Mob mob) {
        if (!mobRewards.containsKey(mob.getTemplateId())) {
            return Set.of();
        }
        return mobRewards.get(mob.getTemplateId());
    }

    public static Reward getMobMoneyReward(Mob mob) {
        final Tuple<Integer, Integer> money = GameConstants.getMoneyForMobLevel(mob.getLevel());
        return Reward.money(money.getLeft(), money.getRight(), GameConstants.DROP_MONEY_PROB);
    }

    private static Reward getReward(List<String> props) {
        final int itemId = Integer.parseInt(props.get(1));
        if (itemId == 0) {
            return Reward.money(
                    Integer.parseInt(props.get(2)),
                    Integer.parseInt(props.get(3)),
                    Double.parseDouble(props.get(4))
            );
        } else {
            return Reward.item(
                    Integer.parseInt(props.get(1)),
                    Integer.parseInt(props.get(2)),
                    Integer.parseInt(props.get(3)),
                    Double.parseDouble(props.get(4)),
                    Integer.parseInt(props.get(5))
            );
        }
    }
}
