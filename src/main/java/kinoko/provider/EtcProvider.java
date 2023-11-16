package kinoko.provider;

import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class EtcProvider {
    public static final Path ETC_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Etc.wz");
    private static final Logger log = LogManager.getLogger(EtcProvider.class);
    private static final Set<String> forbiddenNames = new HashSet<>();
    private static final Map<Integer, Set<Integer>> makeCharInfo = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(ETC_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadForbiddenNames(wzPackage);
            loadMakeCharInfo(wzPackage);
        } catch (IOException | ProviderError e) {
            log.error("Exception caught while loading Etc.wz", e);
        }
    }

    public static boolean isForbiddenName(String name) {
        return forbiddenNames.contains(name.toLowerCase());
    }

    public static boolean isValidStartingItem(int index, int id) {
        return makeCharInfo.getOrDefault(index, Set.of()).contains(id);
    }

    private static void loadForbiddenNames(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("ForbiddenName.img") instanceof WzImage nameImage)) {
            throw new ProviderError("Could not resolve Etc.wz/ForbiddenName.img");
        }
        for (var value : nameImage.getProperty().getItems().values()) {
            if (value instanceof String name) {
                forbiddenNames.add(name);
            }
        }
    }

    private static void loadMakeCharInfo(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("MakeCharInfo.img") instanceof WzImage infoImage)) {
            throw new ProviderError("Could not resolve Etc.wz/MakeCharInfo.img");
        }
        for (var entry : infoImage.getProperty().getItems().entrySet()) {
            if (entry.getKey().equals("Name")) {
                continue;
            }
            if (!(entry.getValue() instanceof WzListProperty prop)) {
                throw new ProviderError("Failed to resolve MakeCharInfo");
            }
            if (entry.getKey().equals("Info")) {
                addMakeCharInfo(prop.get("CharFemale"));
                addMakeCharInfo(prop.get("CharMale"));
            } else {
                addMakeCharInfo(prop);
            }
        }
    }

    private static void addMakeCharInfo(WzListProperty prop) {
        for (var propEntry : prop.getItems().entrySet()) {
            final int index = Integer.parseInt(propEntry.getKey());
            if (!(propEntry.getValue() instanceof WzListProperty idList)) {
                throw new ProviderError("Failed to resolve MakeCharInfo");
            }
            for (var idEntry : idList.getItems().entrySet()) {
                final int id = (Integer) idEntry.getValue();
                if (!makeCharInfo.containsKey(index)) {
                    makeCharInfo.put(index, new HashSet<>());
                }
                makeCharInfo.get(index).add(id);
            }
        }
    }
}
