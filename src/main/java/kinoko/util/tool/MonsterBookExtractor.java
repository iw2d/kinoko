package kinoko.util.tool;

import kinoko.provider.ProviderError;
import kinoko.provider.StringProvider;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.util.*;

public final class MonsterBookExtractor {
    private static final Map<Integer, List<Integer>> monsterBookRewards = new HashMap<>(); // mob id -> item ids

    public static void main(String[] args) throws IOException {
        try (final WzReader reader = WzReader.build(StringProvider.STRING_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMonsterBookRewards(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading String.wz", e);
        }
    }

    private static void loadMonsterBookRewards(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("MonsterBook.img") instanceof WzImage monsterBookImage)) {
            throw new ProviderError("Could not resolve String.wz/MonsterBook.img");
        }
        for (var entry : monsterBookImage.getProperty().getItems().entrySet()) {
            final int mobId = WzProvider.getInteger(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty entryProp) ||
                    !(entryProp.get("reward") instanceof WzListProperty rewardProp)) {
                throw new ProviderError("Could not resolve monster book info");
            }
            final List<Integer> rewards = new ArrayList<>();
            for (var rewardEntry : rewardProp.getItems().entrySet()) {
                final int itemId = WzProvider.getInteger(rewardEntry.getValue());
                rewards.add(itemId);
            }
            monsterBookRewards.put(mobId, Collections.unmodifiableList(rewards));
        }
    }
}
