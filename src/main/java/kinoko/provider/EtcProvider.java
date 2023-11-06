package kinoko.provider;

import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class EtcProvider {
    private static final Map<Integer, Set<Integer>> makeCharInfo = new HashMap<>();

    public static void initialize() {
        final File wzFile = Path.of(ServerConfig.WZ_DIRECTORY, "Etc.wz").toFile();
        try (final WzReader reader = WzReader.build(wzFile, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMakeCharInfo(wzPackage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidStartingItem(int index, int id) {
        return makeCharInfo.getOrDefault(index, Set.of()).contains(id);
    }

    private static void loadMakeCharInfo(WzPackage source) throws ProviderError {
        final WzImage infoImage = source.getDirectory().getImages().get("MakeCharInfo.img");
        if (infoImage == null) {
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
