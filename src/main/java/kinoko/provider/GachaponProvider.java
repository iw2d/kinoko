package kinoko.provider;

import kinoko.provider.reward.Reward;
import kinoko.server.ServerConfig;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class GachaponProvider {
    public static final Path GACHAPON_DATA = Path.of(ServerConfig.DATA_DIRECTORY, "gachapon");
    private static final Map<String, List<Reward>> gachaponRewards = new HashMap<>(); // gachaponName -> rewards
    private static final Map<String, Map<String, Object>> gachaponConfigs = new HashMap<>(); // gachaponName -> full config
    private static List<Reward> globalRewards = new ArrayList<>(); // global rewards list

    public static void initialize() {
        final Load yamlLoader = new Load(LoadSettings.builder().build());

        // First, load the global.yaml file if it exists
        try {
            Path globalPath = GACHAPON_DATA.resolve("global.yaml");
            if (Files.exists(globalPath)) {
                try (final InputStream is = Files.newInputStream(globalPath)) {
                    loadGlobalRewards(yamlLoader.loadFromInputStream(is));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception caught while loading Global Gachapon Data", e);
        }

        // Then load all location-specific gachapon files
        try (final Stream<Path> paths = Files.list(GACHAPON_DATA)) {
            for (Path path : paths.toList()) {
                final String fileName = path.getFileName().toString();
                if (!fileName.endsWith(".yaml") || fileName.equals("global.yaml")) {
                    continue;
                }
                final String gachaponName = fileName.replace(".yaml", "");
                try (final InputStream is = Files.newInputStream(path)) {
                    Map<String, Object> config = (Map<String, Object>) yamlLoader.loadFromInputStream(is);
                    gachaponConfigs.put(gachaponName, config);
                    processGachaponConfig(gachaponName, config);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception caught while loading Gachapon Data", e);
        }
    }

    public static List<Reward> getGachaponRewards(String gachaponName) {
        return gachaponRewards.getOrDefault(gachaponName, List.of());
    }

    @SuppressWarnings("unchecked")
    private static void loadGlobalRewards(Object yamlObject) {
        if (!(yamlObject instanceof Map<?, ?> rewardData)) {
            throw new IllegalArgumentException("Could not resolve global reward data");
        }
        if (!(rewardData.get("rewards") instanceof List<?> rewardList)) {
            return;
        }

        for (Object rewardObject : rewardList) {
            if (!(rewardObject instanceof List<?> rewardInfo)) {
                throw new IllegalArgumentException("Invalid global reward format");
            }
            final int itemId = ((Number) rewardInfo.get(0)).intValue();
            final int min = ((Number) rewardInfo.get(1)).intValue();
            final int max = ((Number) rewardInfo.get(2)).intValue();
            final double prob = ((Number) rewardInfo.get(3)).doubleValue();
            globalRewards.add(Reward.item(itemId, min, max, prob, 0));
        }
    }

    @SuppressWarnings("unchecked")
    private static void processGachaponConfig(String gachaponName, Map<String, Object> config) {
        // Process location-specific rewards
        List<Reward> locationRewards = new ArrayList<>();
        if (config.get("rewards") instanceof List<?> rewardList) {
            for (Object rewardObject : rewardList) {
                if (!(rewardObject instanceof List<?> rewardInfo)) {
                    throw new IllegalArgumentException("Invalid reward format for Gachapon: " + gachaponName);
                }
                final int itemId = ((Number) rewardInfo.get(0)).intValue();
                final int min = ((Number) rewardInfo.get(1)).intValue();
                final int max = ((Number) rewardInfo.get(2)).intValue();
                final double prob = ((Number) rewardInfo.get(3)).doubleValue();
                locationRewards.add(Reward.item(itemId, min, max, prob, 0));
            }
        }

        // Process global item settings and merge with location rewards
        List<Reward> finalRewards = new ArrayList<>(locationRewards);

        if (config.containsKey("global_item_settings")) {
            Map<String, Object> globalSettings = (Map<String, Object>) config.get("global_item_settings");
            boolean enabled = globalSettings.containsKey("enabled") && (boolean) globalSettings.get("enabled");

            if (enabled) {
                double weightModifier = 1.0; // Default: no modification
                if (globalSettings.containsKey("weight_modifier")) {
                    weightModifier = ((Number) globalSettings.get("weight_modifier")).doubleValue();
                }

                // Check if we need to include or exclude specific items
                List<Map<String, Object>> itemsToInclude = globalSettings.containsKey("items_to_include") ?
                        (List<Map<String, Object>>) globalSettings.get("items_to_include") : null;

                List<Map<String, Object>> itemsToExclude = globalSettings.containsKey("items_to_exclude") ?
                        (List<Map<String, Object>>) globalSettings.get("items_to_exclude") : null;

                // Add modified global rewards
                for (Reward globalReward : globalRewards) {
                    // Skip if item should be excluded
                    if (shouldExcludeItem(globalReward.getItemId(), itemsToExclude)) {
                        continue;
                    }

                    // Include only if no specific inclusion rules or item matches inclusion rules
                    if (itemsToInclude == null || shouldIncludeItem(globalReward.getItemId(), itemsToInclude)) {
                        double modifiedProb = globalReward.getProb() * weightModifier;
                        finalRewards.add(Reward.item(
                                globalReward.getItemId(),
                                globalReward.getMin(),
                                globalReward.getMax(),
                                modifiedProb,
                                0
                        ));
                    }
                }
            }
        }

        gachaponRewards.put(gachaponName, Collections.unmodifiableList(finalRewards));
    }

    private static boolean shouldExcludeItem(int itemId, List<Map<String, Object>> exclusionRules) {
        if (exclusionRules == null) {
            return false;
        }

        for (Map<String, Object> rule : exclusionRules) {
            // Check for type-based exclusion (e.g., "scroll" items)
            if (rule.containsKey("type")) {
                String type = (String) rule.get("type");
                if (type.equals("scroll") && (itemId / 10000) == 204) {
                    return true;
                }
            }

            // Check for ID range exclusion
            if (rule.containsKey("id_range")) {
                List<Number> range = (List<Number>) rule.get("id_range");
                int minId = range.get(0).intValue();
                int maxId = range.get(1).intValue();
                if (itemId >= minId && itemId <= maxId) {
                    return true;
                }
            }

            // Check for specific ID exclusion
            if (rule.containsKey("id") && ((Number) rule.get("id")).intValue() == itemId) {
                return true;
            }
        }

        return false;
    }

    private static boolean shouldIncludeItem(int itemId, List<Map<String, Object>> inclusionRules) {
        if (inclusionRules == null) {
            return true;
        }

        for (Map<String, Object> rule : inclusionRules) {
            // Check for type-based inclusion (e.g., "scroll" items)
            if (rule.containsKey("type")) {
                String type = (String) rule.get("type");
                if (type.equals("scroll") && (itemId / 10000) == 204) {
                    return true;
                }
            }

            // Check for ID range inclusion
            if (rule.containsKey("id_range")) {
                List<Number> range = (List<Number>) rule.get("id_range");
                int minId = range.get(0).intValue();
                int maxId = range.get(1).intValue();
                if (itemId >= minId && itemId <= maxId) {
                    return true;
                }
            }

            // Check for specific ID inclusion
            if (rule.containsKey("id") && ((Number) rule.get("id")).intValue() == itemId) {
                return true;
            }
        }

        return false;
    }
}