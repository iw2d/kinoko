package kinoko.provider.wz;

import java.util.Map;

public class WzDirectory {
    private final Map<String, WzDirectory> directories;
    private final Map<String, WzImage> images;

    public WzDirectory(Map<String, WzDirectory> directories, Map<String, WzImage> images) {
        this.directories = directories;
        this.images = images;
    }

    public Map<String, WzDirectory> getDirectories() {
        return directories;
    }

    public Map<String, WzImage> getImages() {
        return images;
    }
}
