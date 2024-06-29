package kinoko.provider;

import kinoko.provider.quest.QuestInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QuestProvider implements WzProvider {
    public static final Path QUEST_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Quest.wz");
    private static final Map<Integer, QuestInfo> questInfos = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(QUEST_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadQuestInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Quest.wz", e);
        }
    }

    public static List<QuestInfo> getQuestInfos() {
        return questInfos.values().stream().toList();
    }

    public static Optional<QuestInfo> getQuestInfo(int questId) {
        return Optional.ofNullable(questInfos.get(questId));
    }

    private static void loadQuestInfos(WzPackage source) throws ProviderError {
        final WzImage infoImage = source.getDirectory().getImages().get("QuestInfo.img");
        final WzImage actImage = source.getDirectory().getImages().get("Act.img");
        final WzImage checkImage = source.getDirectory().getImages().get("Check.img");
        for (var entry : infoImage.getProperty().getItems().entrySet()) {
            final int questId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve quest info");
            }
            final QuestInfo questInfo = QuestInfo.from(
                    questId,
                    infoProp,
                    actImage.getProperty().get(entry.getKey()),
                    checkImage.getProperty().get(entry.getKey())
            );
            questInfos.put(questId, questInfo);
        }
    }
}
