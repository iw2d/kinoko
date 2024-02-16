package kinoko.provider;

import kinoko.provider.npc.NpcInfo;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.WzReader;
import kinoko.provider.wz.WzReaderConfig;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NpcProvider implements WzProvider {
    public static final Path NPC_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Npc.wz");
    private static final Map<Integer, NpcInfo> npcInfos = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(NPC_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadNpcInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Npc.wz", e);
        }
    }

    public static Optional<NpcInfo> getNpcInfo(int npcId) {
        if (!npcInfos.containsKey(npcId)) {
            return Optional.empty();
        }
        return Optional.of(npcInfos.get(npcId));
    }

    private static void loadNpcInfos(WzPackage source) throws ProviderError {
        final Map<Integer, Integer> linkedNpcs = new HashMap<>();
        for (var npcEntry : source.getDirectory().getImages().entrySet()) {
            final int npcId = Integer.parseInt(npcEntry.getKey().replace(".img", ""));
            if (!(npcEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            final int link = WzProvider.getInteger(infoProp.get("link"));
            if (link != 0) {
                linkedNpcs.put(npcId, link);
                continue;
            }
            final boolean move = npcEntry.getValue().getProperty().get("move") instanceof WzListProperty;
            npcInfos.put(npcId, NpcInfo.from(npcId, move, infoProp));
        }
        for (var linkEntry : linkedNpcs.entrySet()) {
            final int npcId = linkEntry.getKey();
            final int link = linkEntry.getValue();
            final NpcInfo linkInfo = npcInfos.get(link);
            if (linkInfo == null) {
                throw new ProviderError("Failed to resolve linked Npc ID : %d, link : %d", npcId, link);
            }
            npcInfos.put(npcId, linkInfo);
        }
    }
}
