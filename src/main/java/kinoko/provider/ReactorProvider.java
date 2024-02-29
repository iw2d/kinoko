package kinoko.provider;

import kinoko.provider.reactor.ReactorTemplate;
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

public final class ReactorProvider implements WzProvider {
    public static final Path REACTOR_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Reactor.wz");
    private static final Map<Integer, ReactorTemplate> reactorTemplates = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(REACTOR_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadReactorTemplates(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reactor.wz", e);
        }
    }

    public static Optional<ReactorTemplate> getReactorTemplate(int reactorId) {
        return Optional.ofNullable(reactorTemplates.get(reactorId));
    }

    private static void loadReactorTemplates(WzPackage source) throws ProviderError {
        final Map<Integer, Tuple<Integer, String>> linkedReactors = new HashMap<>(); // reactorId -> link, action
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {
            final int reactorId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
            final WzListProperty imageProp = imageEntry.getValue().getProperty();
            final String action = imageProp.get("action");
            // Handle linked reactors
            if (imageProp.get("info") instanceof WzListProperty infoProp && infoProp.getItems().containsKey("link")) {
                final int link = WzProvider.getInteger(infoProp.get("link"));
                linkedReactors.put(reactorId, new Tuple<>(link, action));
                continue;
            }
            // Add template
            reactorTemplates.put(reactorId, ReactorTemplate.from(reactorId, action, imageProp));
        }
        // Process linked reactors
        for (var linkEntry : linkedReactors.entrySet()) {
            final int reactorId = linkEntry.getKey();
            final int link = linkEntry.getValue().getLeft();
            final ReactorTemplate linkedTemplate = reactorTemplates.get(link);
            if (linkedTemplate == null) {
                throw new ProviderError("Failed to resolve linked reactor ID : %d, link : %d", reactorId, link);
            }
            reactorTemplates.put(reactorId, new ReactorTemplate(
                    reactorId,
                    linkEntry.getValue().getRight(),
                    linkedTemplate.getStates()
            ));
        }
    }
}
