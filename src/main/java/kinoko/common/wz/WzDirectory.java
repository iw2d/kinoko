package kinoko.common.wz;

import java.util.Map;

public class WzDirectory {
    private final Map<WzString, WzDirectory> directories;
    private final Map<WzString, WzImage> images;

    public WzDirectory(Map<WzString, WzDirectory> directories, Map<WzString, WzImage> images) {
        this.directories = directories;
        this.images = images;
    }

    public Map<WzString, WzDirectory> getDirectories() {
        return directories;
    }

    public Map<WzString, WzImage> getImages() {
        return images;
    }
}
