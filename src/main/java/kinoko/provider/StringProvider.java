package kinoko.provider;

import kinoko.provider.skill.SkillStringInfo;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StringProvider implements WzProvider {
    public static final Path STRING_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "String.wz");
    public static final List<String> EQUIP_TYPES = List.of("Accessory", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove", "Hair", "Longcoat", "Mechanic", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "Taming", "Weapon"); // TamingMob -> Taming compared to Item.wz
    private static final Map<Integer, String> itemNames = new HashMap<>();
    private static final Map<Integer, String> mapNames = new HashMap<>();
    private static final Map<Integer, String> mobNames = new HashMap<>();
    private static final Map<Integer, String> npcNames = new HashMap<>();
    private static final Map<Integer, SkillStringInfo> skillStrings = new HashMap<>();

    public static void initialize() {
        try (final WzReader reader = WzReader.build(STRING_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadItemNames(wzPackage);
            loadMapNames(wzPackage);
            loadMobNames(wzPackage);
            loadNpcNames(wzPackage);
            loadSkillStrings(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading String.wz", e);
        }
    }

    public static Map<Integer, String> getItemNames() {
        return itemNames;
    }

    public static Map<Integer, String> getMapNames() {
        return mapNames;
    }

    public static Map<Integer, String> getMobNames() {
        return mobNames;
    }

    public static Map<Integer, String> getNpcNames() {
        return npcNames;
    }

    public static Map<Integer, SkillStringInfo> getSkillStrings() {
        return skillStrings;
    }

    public static String getItemName(int itemId) {
        return itemNames.get(itemId);
    }

    public static String getMapName(int mapId) {
        return mapNames.get(mapId);
    }

    public static String getMobName(int mobId) {
        return mobNames.get(mobId);
    }

    public static String getNpcName(int npcId) {
        return npcNames.get(npcId);
    }

    public static String getSkillName(int skillId) {
        return skillStrings.get(skillId).getName();
    }

    public static SkillStringInfo getSkillString(int skillId) {
        return skillStrings.get(skillId);
    }

    private static void loadItemNames(WzPackage source) throws ProviderError {
        // Eqp.img
        if (!(source.getDirectory().getImages().get("Eqp.img") instanceof WzImage equipImage) ||
                !(equipImage.getProperty().get("Eqp") instanceof WzListProperty equipTypes)) {
            throw new ProviderError("Could not resolve String.wz/Eqp.img/Eqp");
        }
        for (String type : EQUIP_TYPES) {
            if (!(equipTypes.get(type) instanceof WzListProperty equipList)) {
                throw new ProviderError("Could not resolve String.wz/Eqp.img/Eqp/%s", type);
            }
            for (var entry : equipList.getItems().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey());
                if (!(entry.getValue() instanceof WzListProperty prop) ||
                        !(prop.getItems().get("name") instanceof String name)) {
                    continue;
                }
                itemNames.put(itemId, name);
            }
        }
        // Etc.img
        if (!(source.getDirectory().getImages().get("Etc.img") instanceof WzImage etcImage) ||
                !(etcImage.getProperty().get("Etc") instanceof WzListProperty etcList)) {
            throw new ProviderError("Could not resolve String.wz/Etc.img/Etc");
        }
        for (var entry : etcList.getItems().entrySet()) {
            final int itemId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty prop) ||
                    !(prop.getItems().get("name") instanceof String name)) {
                continue;
            }
            itemNames.put(itemId, name);
        }
        // Other types
        for (String imageName : List.of("Consume.img", "Ins.img", "Cash.img", "Pet.img")) {
            if (!(source.getDirectory().getImages().get(imageName) instanceof WzImage itemImage)) {
                throw new ProviderError("Could not resolve String.wz/%s", imageName);
            }
            for (var entry : itemImage.getProperty().getItems().entrySet()) {
                final int itemId = Integer.parseInt(entry.getKey());
                if (!(entry.getValue() instanceof WzListProperty itemProp) ||
                        !(itemProp.getItems().get("name") instanceof String itemName)) {
                    continue;
                }
                itemNames.put(itemId, itemName);
            }
        }
    }

    private static void loadMapNames(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("Map.img") instanceof WzImage image)) {
            throw new ProviderError("Could not resolve String.wz/Map.img");
        }
        for (var typeEntry : image.getProperty().getItems().entrySet()) {
            if (!(typeEntry.getValue() instanceof WzListProperty mapList)) {
                throw new ProviderError("Failed to resolve String.wz/Map.img");
            }
            for (var mapEntry : mapList.getItems().entrySet()) {
                final int mapId = Integer.parseInt(mapEntry.getKey());
                if (!(mapEntry.getValue() instanceof WzListProperty prop) ||
                        !(prop.getItems().get("mapName") instanceof String name)) {
                    continue;
                }
                mapNames.put(mapId, name);
            }
        }
    }

    private static void loadMobNames(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("Mob.img") instanceof WzImage image)) {
            throw new ProviderError("Could not resolve String.wz/Mob.img");
        }
        for (var entry : image.getProperty().getItems().entrySet()) {
            final int mobId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty prop) ||
                    !(prop.getItems().get("name") instanceof String name)) {
                continue;
            }
            mobNames.put(mobId, name);
        }
    }

    private static void loadNpcNames(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("Npc.img") instanceof WzImage image)) {
            throw new ProviderError("Could not resolve String.wz/Npc.img");
        }
        for (var entry : image.getProperty().getItems().entrySet()) {
            final int npcId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty prop) ||
                    !(prop.getItems().get("name") instanceof String name)) {
                continue;
            }
            npcNames.put(npcId, name);
        }
    }

    private static void loadSkillStrings(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getImages().get("Skill.img") instanceof WzImage image)) {
            throw new ProviderError("Could not resolve String.wz/Skill.img");
        }
        for (var entry : image.getProperty().getItems().entrySet()) {
            if (entry.getKey().length() < 7) {
                continue;
            }
            final int skillId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty prop)) {
                continue;
            }
            skillStrings.put(skillId, SkillStringInfo.from(prop));
        }
    }
}
