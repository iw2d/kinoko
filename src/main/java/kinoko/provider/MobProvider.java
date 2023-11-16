package kinoko.provider;

import kinoko.provider.mob.MobInfo;
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

public final class MobProvider {
    public static final Path MOB_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Mob.wz");
    private static final Logger log = LogManager.getLogger(NpcProvider.class);
    private static final Map<Integer, MobInfo> mobInfos = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(MOB_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMobInfos(wzPackage);
        } catch (IOException | ProviderError e) {
            log.error("Exception caught while loading Npc.wz", e);
        }
    }

    public static Optional<MobInfo> getMobInfo(int mobId) {
        if (!mobInfos.containsKey(mobId)) {
            return Optional.empty();
        }
        return Optional.of(mobInfos.get(mobId));
    }

    private static void loadMobInfos(WzPackage source) throws ProviderError {
        for (var mobEntry : source.getDirectory().getImages().entrySet()) {
            final int mobId = Integer.parseInt(mobEntry.getKey().replace(".img", ""));
            if (!(mobEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            mobInfos.put(mobId, new MobInfo(mobId));
        }
    }
}
