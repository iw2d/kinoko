package kinoko.provider;

import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class StringProvider implements WzProvider {
    public static final Path STRING_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "String.wz");
    private static final Map<Integer, String> skillNames = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(STRING_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadSkillNames(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Skill.wz", e);
        }
    }

    public static String getSkillName(int skillId) {
        return skillNames.get(skillId);
    }

    private static void loadSkillNames(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("Skill.img") instanceof WzImage skillImage)) {
            throw new ProviderError("Could not resolve String.wz/Skill.img");
        }
        for (var entry : skillImage.getProperty().getItems().entrySet()) {
            if (entry.getKey().length() < 7) {
                continue;
            }
            final int skillId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty skillProp) ||
                    !(skillProp.getItems().get("name") instanceof String skillName)) {
                continue;
            }
            skillNames.put(skillId, skillName);
        }
    }
}
