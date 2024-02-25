package kinoko.provider;

import kinoko.provider.reward.Reward;
import kinoko.server.ServerConfig;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.field.life.mob.Mob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class RewardProvider implements DataProvider {
    public static final Path MOB_REWARD = Path.of(ServerConfig.DATA_DIRECTORY, "mob_reward.csv");
    public static final Path REACTOR_REWARD = Path.of(ServerConfig.DATA_DIRECTORY, "reactor_reward.csv");
    private static final Map<Integer, Set<Reward>> mobRewards = new HashMap<>();
    private static final Map<Integer, Set<Reward>> reactorRewards = new HashMap<>();

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

    public static void initialize() {
        try {
            // Mob rewards
            DataProvider.readData(MOB_REWARD).forEach((props) -> {
                final Tuple<Integer, Reward> tuple = getReward(props);
                final int mobId = tuple.getLeft();
                if (!mobRewards.containsKey(mobId)) {
                    mobRewards.put(mobId, new HashSet<>());
                }
                mobRewards.get(mobId).add(tuple.getRight());
            });
            for (var entry : mobRewards.entrySet()) {
                mobRewards.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
            }

            // Reactor rewards
            DataProvider.readData(REACTOR_REWARD).forEach((props) -> {
                final Tuple<Integer, Reward> tuple = getReward(props);
                final int reactorId = tuple.getLeft();
                if (!reactorRewards.containsKey(reactorId)) {
                    reactorRewards.put(reactorId, new HashSet<>());
                }
                reactorRewards.get(reactorId).add(tuple.getRight());
            });
            for (var entry : reactorRewards.entrySet()) {
                reactorRewards.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception caught while loading Reward Data", e);
        }
    }

    private static Tuple<Integer, Reward> getReward(List<String> props) {
        final int templateId = Integer.parseInt(props.get(0));
        final int itemId = Integer.parseInt(props.get(1));
        final Reward reward;
        if (itemId == 0) {
            reward = Reward.money(
                    Integer.parseInt(props.get(2)),
                    Integer.parseInt(props.get(3)),
                    Double.parseDouble(props.get(4))
            );
        } else {
            reward = Reward.item(
                    Integer.parseInt(props.get(1)),
                    Integer.parseInt(props.get(2)),
                    Integer.parseInt(props.get(3)),
                    Double.parseDouble(props.get(4)),
                    Boolean.parseBoolean(props.get(5))
            );
        }
        return new Tuple<>(templateId, reward);
    }
}
