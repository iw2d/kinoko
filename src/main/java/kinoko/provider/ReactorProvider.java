package kinoko.provider;

import kinoko.provider.reactor.ReactorTemplate;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.WzArchiveReader;
import kinoko.provider.wz.WzReaderConfig;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.util.Triple;
import kinoko.util.Tuple;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ReactorProvider implements WzProvider {
    public static final Path REACTOR_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Reactor.wz");
    private static final Map<Integer, ReactorTemplate> reactorTemplates = new HashMap<>();

    public static void initialize() {
        try (final WzArchiveReader reader = WzArchiveReader.build(REACTOR_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadReactorTemplates(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reactor.wz", e);
        }
    }

    public static List<ReactorTemplate> getReactorTemplates() {
        return reactorTemplates.values().stream().toList();
    }

    public static Optional<ReactorTemplate> getReactorTemplate(int reactorId) {
        return Optional.ofNullable(reactorTemplates.get(reactorId));
    }

    private static void loadReactorTemplates(WzPackage source) throws ProviderError {
        final Map<Integer, Triple<Integer, Tuple<Boolean, Boolean>, String>> linkedReactors = new HashMap<>(); // reactorId -> link, [notHitable, activateByTouch], action
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {
            final int reactorId = Integer.parseInt(imageEntry.getKey().replace(".img", ""));
            final WzListProperty imageProp = imageEntry.getValue().getProperty();
            final String action = imageProp.get("action");
            boolean notHitable = false;
            boolean activateByTouch = false;
            if (imageProp.get("info") instanceof WzListProperty infoProp) {
                notHitable = WzProvider.getInteger(imageProp.get("notHitable"), 0) != 0;
                activateByTouch = WzProvider.getInteger(imageProp.get("activateByTouch"), 0) != 0;

                // Handle linked reactors
                if (infoProp.getItems().containsKey("link")) {
                    final int link = WzProvider.getInteger(infoProp.get("link"));
                    linkedReactors.put(reactorId, Triple.of(link, Tuple.of(notHitable, activateByTouch), action));
                    continue;
                }
            }
            // Add template
            reactorTemplates.put(reactorId, ReactorTemplate.from(reactorId, notHitable, activateByTouch, action, imageProp));
        }
        // Process linked reactors
        for (var linkEntry : linkedReactors.entrySet()) {
            final int reactorId = linkEntry.getKey();
            final int link = linkEntry.getValue().getFirst();
            final ReactorTemplate linkedTemplate = reactorTemplates.get(link);
            if (linkedTemplate == null) {
                throw new ProviderError("Failed to resolve linked reactor ID : %d, link : %d", reactorId, link);
            }
            reactorTemplates.put(reactorId, new ReactorTemplate(
                    reactorId,
                    linkEntry.getValue().getSecond().getLeft(), // notHitable
                    linkEntry.getValue().getSecond().getRight(), // activateByTouch
                    linkEntry.getValue().getThird(),
                    linkedTemplate.getStates()
            ));
        }
    }
}
