package kinoko.common.wz;

public final class WzReaderConfig {

    private final byte[] iv;
    private final int version;

    public WzReaderConfig(byte[] iv, int version) {
        this.iv = iv;
        this.version = version;
    }

    public byte[] getIv() {
        return iv;
    }

    public int getVersion() {
        return version;
    }

    public WzCrypto buildEncryptor() {
        return WzCrypto.fromIv(getIv());
    }
}
