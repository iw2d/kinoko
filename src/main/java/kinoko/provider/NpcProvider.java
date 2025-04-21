package kinoko.provider;

import kinoko.provider.npc.NpcImitateData;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.WzArchiveReader;
import kinoko.provider.wz.WzReaderConfig;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.util.Tuple;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NpcProvider implements WzProvider {
    public static final Path NPC_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Npc.wz");
    private static final Map<Integer, NpcTemplate> npcTemplates = new HashMap<>();
    private static final Map<Integer, NpcImitateData> npcImitateData = Map.of(
            9901000, NpcImitateData.NPC_9901000 // TODO move to separate provider, load from database?
    );

    public static void initialize() {
        try (final WzArchiveReader reader = WzArchiveReader.build(NPC_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadNpcTemplates(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Npc.wz", e);
        }
    }

    public static List<NpcTemplate> getNpcTemplates() {
        return npcTemplates.values().stream().toList();
    }

    public static Optional<NpcTemplate> getNpcTemplate(int npcId) {
        return Optional.ofNullable(npcTemplates.get(npcId));
    }

    public static Optional<NpcImitateData> getNpcImitateData(int npcId) {
        return Optional.ofNullable(npcImitateData.get(npcId));
    }

    private static void loadNpcTemplates(WzPackage source) throws ProviderError {
        final Map<Integer, Tuple<Integer, WzListProperty>> linkedNpcs = new HashMap<>(); // npcId -> link, infoProp
        for (var npcEntry : source.getDirectory().getImages().entrySet()) {
            final int npcId = Integer.parseInt(npcEntry.getKey().replace(".img", ""));
            if (!(npcEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            if (infoProp.getItems().containsKey("link")) {
                linkedNpcs.put(npcId, Tuple.of(WzProvider.getInteger(infoProp.get("link")), infoProp));
                continue;
            }
            final boolean move = npcEntry.getValue().getProperty().get("move") instanceof WzListProperty;
            npcTemplates.put(npcId, NpcTemplate.from(npcId, move, infoProp));
        }
        // Process linked npcs
        for (var linkEntry : linkedNpcs.entrySet()) {
            final int npcId = linkEntry.getKey();
            final int link = linkEntry.getValue().getLeft();
            final WzListProperty infoProp = linkEntry.getValue().getRight();
            final NpcTemplate linkedTemplate = npcTemplates.get(link);
            if (linkedTemplate == null) {
                throw new ProviderError("Failed to resolve linked npc ID : %d, link : %d", npcId, link);
            }
            npcTemplates.put(npcId, NpcTemplate.from(npcId, linkedTemplate.isMove(), infoProp));
        }
    }
}
