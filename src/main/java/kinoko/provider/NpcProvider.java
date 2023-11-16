package kinoko.provider;

import kinoko.provider.npc.NpcInfo;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.WzReader;
import kinoko.provider.wz.WzReaderConfig;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NpcProvider {
    public static final Path NPC_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Npc.wz");
    private static final Logger log = LogManager.getLogger(NpcProvider.class);
    private static final Map<Integer, NpcInfo> npcInfos = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(NPC_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadNpcInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            log.error("[NpcProvider] Exception caught while loading Npc.wz", e);
        }
    }

    public static Optional<NpcInfo> getNpcInfo(int npcId) {
        if (!npcInfos.containsKey(npcId)) {
            return Optional.empty();
        }
        return Optional.of(npcInfos.get(npcId));
    }

    private static void loadNpcInfos(WzPackage source) throws ProviderError {
        for (var npcEntry : source.getDirectory().getImages().entrySet()) {
            final int npcId = Integer.parseInt(npcEntry.getKey().replace(".img", ""));
            if (!(npcEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            final boolean move = npcEntry.getValue().getProperty().get("move") instanceof WzListProperty;
            npcInfos.put(npcId, NpcInfo.from(npcId, move, infoProp));
        }
    }
}
