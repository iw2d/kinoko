package kinoko.util.tool;

import kinoko.provider.ItemProvider;
import kinoko.provider.ProviderError;
import kinoko.provider.StringProvider;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Path;

final class NpcShopExtractor {
    public static final Path NPC_SHOP_IMG = Path.of(ServerConfig.WZ_DIRECTORY, "bms", "NpcShop.img");

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        StringProvider.initialize();

        final WzImage npcShopImage = readImage(NPC_SHOP_IMG);
        // TODO
    }

    private static WzImage readImage(Path path) {
        try (final WzReader reader = WzReader.build(path, new WzReaderConfig(WzConstants.WZ_EMPTY_IV, ServerConstants.GAME_VERSION))) {
            final WzImage image = new WzImage(0);
            if (!(reader.readProperty(image, reader.getBuffer(0)) instanceof WzListProperty listProperty)) {
                throw new WzReaderError("Image property is not a list");
            }
            image.setProperty(listProperty);
            return image;
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Reward.img", e);
        }
    }
}
