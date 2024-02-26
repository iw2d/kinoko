package kinoko.provider;

import kinoko.provider.mob.MobTemplate;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.WzReader;
import kinoko.provider.wz.WzReaderConfig;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.util.Tuple;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MobProvider implements WzProvider {
    public static final Path MOB_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Mob.wz");
    private static final Map<Integer, MobTemplate> mobTemplates = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(MOB_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMobTemplates(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Mob.wz", e);
        }
    }

    public static Optional<MobTemplate> getMobTemplate(int mobId) {
        return Optional.ofNullable(mobTemplates.get(mobId));
    }

    private static void loadMobTemplates(WzPackage source) throws ProviderError {
        final Map<Integer, WzListProperty> mobProperties = new HashMap<>(); // mobId -> mobProperty
        final Map<Integer, Tuple<Integer, WzListProperty>> linkedMobs = new HashMap<>(); // mobId -> link, infoProp
        for (var mobEntry : source.getDirectory().getImages().entrySet()) {
            final int mobId = Integer.parseInt(mobEntry.getKey().replace(".img", ""));
            final WzListProperty mobProperty = mobEntry.getValue().getProperty();
            if (!(mobEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                throw new ProviderError("Failed to resolve info property");
            }
            if (infoProp.getItems().containsKey("link")) {
                linkedMobs.put(mobId, new Tuple<>(WzProvider.getInteger(infoProp.get("link")), infoProp));
                continue;
            }
            mobProperties.put(mobId, mobProperty);
            mobTemplates.put(mobId, MobTemplate.from(mobId, mobProperty, infoProp));
        }
        // Process linked mobs
        for (var linkEntry : linkedMobs.entrySet()) {
            final int mobId = linkEntry.getKey();
            final int link = linkEntry.getValue().getLeft();
            final WzListProperty linkProp = mobProperties.get(link);
            if (linkProp == null) {
                throw new ProviderError("Failed to resolve linked Mob ID : %d, link : %d", mobId, link);
            }
            final WzListProperty infoProp = linkEntry.getValue().getRight();
            mobTemplates.put(mobId, MobTemplate.from(mobId, linkProp, infoProp));
        }
    }
}
