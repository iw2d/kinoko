package kinoko.provider;

import kinoko.provider.reactor.ReactorTemplate;
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

public final class ReactorProvider implements WzProvider {
    public static final Path REACTOR_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Reactor.wz");
    private static final Map<Integer, ReactorTemplate> reactorTemplates = new HashMap<>();

    public static Optional<ReactorTemplate> getReactorTemplate(int reactorId) {
        return Optional.ofNullable(reactorTemplates.get(reactorId));
    }

    public static void initialize() {
        try (final WzReader reader = WzReader.build(REACTOR_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadReactorActions(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Mob.wz", e);
        }
    }

    private static void loadReactorActions(WzPackage source) throws ProviderError {
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {
            final int reactorId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
            final WzListProperty imageProp = imageEntry.getValue().getProperty();

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                imageProp.get(String.valueOf(i));
            }
        }
    }
}
