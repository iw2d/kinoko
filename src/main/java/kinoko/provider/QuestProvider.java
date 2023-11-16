package kinoko.provider;

import kinoko.provider.quest.QuestInfo;
import kinoko.provider.wz.*;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestProvider {
    public static final Path QUEST_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Quest.wz");
    private static final Logger log = LogManager.getLogger(NpcProvider.class);
    private static final Map<Integer, QuestInfo> questInfos = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(QUEST_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadQuestInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            log.error("Exception caught while loading Npc.wz", e);
        }
    }

    public static Optional<QuestInfo> getQuestInfo(int questId) {
        if (!questInfos.containsKey(questId)) {
            return Optional.empty();
        }
        return Optional.of(questInfos.get(questId));
    }

    private static void loadQuestInfos(WzPackage source) throws ProviderError {
        final WzDirectory questData = source.getDirectory().getDirectories().get("QuestData");
        if (questData == null) {
            throw new ProviderError("Failed to resolve Quest.wz/QuestData");
        }
        for (var questEntry : questData.getImages().entrySet()) {
            final String imageName = questEntry.getKey().replace(".img", "");
            if (!imageName.matches("[0-9]+")) {
                continue;
            }
            final int questId = Integer.parseInt(imageName);
            questInfos.put(questId, QuestInfo.from(questId, questEntry.getValue().getProperty()));
        }
    }
}
