package kinoko.common.wz;

public final class WzPackage {

    private final int start;
    private final int hash;
    private WzDirectory directory;

    public WzPackage(int start, int hash) {
        this.start = start;
        this.hash = hash;
    }

    public int getStart() {
        return start;
    }

    public int getHash() {
        return hash;
    }

    public WzDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(WzDirectory directory) {
        this.directory = directory;
    }
}
