package kinoko.provider;

import kinoko.provider.quest.QuestInfo;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.server.ServerConfig;

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
        try (final WzPackage source = WzPackage.from(QUEST_WZ)) {
            loadQuestInfos(source);
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
        final WzImage infoImage = (WzImage) source.getItem("QuestInfo.img");
        final WzImage actImage = (WzImage) source.getItem("Act.img");
        final WzImage checkImage = (WzImage) source.getItem("Check.img");
        for (var entry : infoImage.getItems().entrySet()) {
            final int questId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzProperty infoProp)) {
                throw new ProviderError("Failed to resolve quest info");
            }
            final QuestInfo questInfo = QuestInfo.from(
                    questId,
                    infoProp,
                    (WzProperty) actImage.getItem(entry.getKey()),
                    (WzProperty) checkImage.getItem(entry.getKey())
            );
            questInfos.put(questId, questInfo);
        }
    }
}
