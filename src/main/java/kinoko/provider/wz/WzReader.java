package kinoko.provider.wz;

import kinoko.provider.wz.property.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WzReader implements AutoCloseable {
    private final RandomAccessFile file;
    private final FileChannel channel;
    private final WzReaderConfig config;
    private final WzCrypto crypto;

    private WzReader(RandomAccessFile file, FileChannel channel, WzReaderConfig config, WzCrypto crypto) {
        this.file = file;
        this.channel = channel;
        this.config = config;
        this.crypto = crypto;
    }

    private int computeVersionHash(int version) {
        int versionHash = 0;
        for (final byte c : String.valueOf(version).getBytes()) {
            versionHash = (versionHash * 32) + c + 1;
        }
        return versionHash;
    }

    private int readCompressedInt(ByteBuffer buffer) {
        final byte value = buffer.get();
        if (value == Byte.MIN_VALUE) {
            return buffer.getInt();
        } else {
            return value;
        }
    }

    private int readOffset(WzPackage parent, ByteBuffer buffer) {
        final int start = parent.getStart();
        final int hash = parent.getHash();
        int result = buffer.position();
        result = ~(result - start);
        result = result * hash;
        result = result - WzConstants.WZ_OFFSET_CONSTANT;
        result = Integer.rotateLeft(result, result & 0x1F);
        result = result ^ buffer.getInt(); // encrypted offset
        result = result + (start * 2);
        return result;
    }

    private String readString(ByteBuffer buffer) {
        int length = buffer.get();
        if (length < 0) {
            if (length == Byte.MIN_VALUE) {
                length = buffer.getInt();
            } else {
                length = -length;
            }
            if (length > 0) {
                final byte[] data = new byte[length];
                buffer.get(data);
                crypto.cryptAscii(data);
                return new String(data, StandardCharsets.US_ASCII);
            }
        } else if (length > 0) {
            if (length == Byte.MAX_VALUE) {
                length = buffer.getInt();
            }
            if (length > 0) {
                length = length * 2; // UTF16
                final byte[] data = new byte[length];
                buffer.get(data);
                crypto.cryptUnicode(data);
                return new String(data, StandardCharsets.UTF_16LE);
            }
        }
        return "";
    }

    private String readStringBlock(WzImage image, ByteBuffer buffer) throws WzReaderError {
        final byte stringType = buffer.get();
        switch (stringType) {
            case 0x00, 0x73 -> {
                return readString(buffer);
            }
            case 0x01, 0x1B -> {
                final int stringOffset = buffer.getInt();
                final int originalPosition = buffer.position();
                buffer.position(image.getOffset() + stringOffset);
                final String string = readString(buffer);
                buffer.position(originalPosition);
                return string;
            }
            default -> throw new WzReaderError("Unknown string block type : %d%n", stringType);
        }
    }

    public WzReaderConfig getConfig() {
        return config;
    }

    public WzCrypto getCrypto() {
        return crypto;
    }

    public ByteBuffer getBuffer(int offset) throws IOException {
        final ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, offset, file.length());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }

    public WzPackage readPackage() throws IOException, WzReaderError {
        return readPackage(0);
    }

    public WzPackage readPackage(int offset) throws IOException, WzReaderError {
        final ByteBuffer buffer = getBuffer(offset);

        // Check PKG1 header
        if (buffer.getInt() != 0x31474B50) {
            throw new WzReaderError("PKG1 header missing");
        }
        final long size = buffer.getLong();
        final int start = buffer.getInt();

        // Check version hash
        buffer.position(start);
        final int versionHeader = Short.toUnsignedInt(buffer.getShort());
        final int versionHash = computeVersionHash(getConfig().getVersion());
        final int computedHeader = 0xFF
                ^ ((versionHash >> 24) & 0xFF)
                ^ ((versionHash >> 16) & 0xFF)
                ^ ((versionHash >> 8) & 0xFF)
                ^ (versionHash & 0xFF);
        if (versionHeader != computedHeader) {
            throw new WzReaderError("Incorrect version");
        }
        WzPackage pkg = new WzPackage(start, versionHash);
        pkg.setDirectory(readDirectory(pkg, buffer));
        return pkg;
    }

    public WzDirectory readDirectory(WzPackage parent, ByteBuffer buffer) throws WzReaderError {
        final Map<String, WzDirectory> directories = new HashMap<>();
        final Map<String, WzImage> images = new HashMap<>();
        final int size = readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            final String childName;
            byte childType = buffer.get();
            switch (childType) {
                case 1 -> {
                    // unknown : 01 XX 00 00 00 00 00 OFFSET
                    buffer.getInt();
                    buffer.getShort();
                    readOffset(parent, buffer);
                    continue;
                }
                case 2 -> {
                    // string offset
                    final int stringOffset = buffer.getInt();
                    final int originalPosition = buffer.position();
                    buffer.position(parent.getStart() + stringOffset);
                    childType = buffer.get();
                    childName = readString(buffer);
                    buffer.position(originalPosition);
                }
                case 3, 4 -> {
                    // directory | image
                    childName = readString(buffer);
                }
                default -> throw new WzReaderError("Unknown directory child type : %d", childType);
            }
            final int childSize = readCompressedInt(buffer);
            final int childChecksum = readCompressedInt(buffer);
            final int childOffset = readOffset(parent, buffer);

            final int originalPosition = buffer.position();
            buffer.position(childOffset);
            if (childType == 3) {
                directories.put(childName, readDirectory(parent, buffer));
            } else if (childType == 4) {
                final WzImage image = new WzImage(childOffset);
                if (!(readProperty(image, buffer) instanceof WzListProperty listProperty)) {
                    throw new WzReaderError("Image property is not a list");
                }
                image.setProperty(listProperty);
                images.put(childName, image);
            }
            buffer.position(originalPosition);
        }
        return new WzDirectory(directories, images);
    }

    public WzProperty readProperty(WzImage image, ByteBuffer buffer) throws WzReaderError {
        final String propertyTypeId = readStringBlock(image, buffer);
        final WzPropertyType propertyType = WzPropertyType.getById(propertyTypeId);
        switch (propertyType) {
            case LIST -> {
                buffer.getShort(); // reserved
                return new WzListProperty(readListItems(image, buffer));
            }
            case CANVAS -> {
                buffer.position(buffer.position() + 1);
                final boolean hasProperties = buffer.get() == 1;
                final WzListProperty properties;
                if (hasProperties) {
                    buffer.position(buffer.position() + 2);
                    properties = new WzListProperty(readListItems(image, buffer));
                } else {
                    properties = new WzListProperty(Map.of());
                }
                // Canvas meta
                final int width = readCompressedInt(buffer);
                final int height = readCompressedInt(buffer);
                final int format = readCompressedInt(buffer);
                final int format2 = readCompressedInt(buffer);
                buffer.position(buffer.position() + 4);
                // Canvas data
                final int dataSize = buffer.getInt() - 1;
                buffer.position(buffer.position() + 1);
                ByteBuffer dataSlice = buffer.slice(buffer.position(), dataSize);
                buffer.position(buffer.position() + dataSize);
                return new WzCanvasProperty(properties, width, height, format, format2, dataSlice);
            }
            case VECTOR -> {
                final int x = readCompressedInt(buffer);
                final int y = readCompressedInt(buffer);
                return new WzVectorProperty(x, y);
            }
            case CONVEX -> {
                final List<WzProperty> properties = new ArrayList<>();
                final int size = readCompressedInt(buffer);
                for (int i = 0; i < size; i++) {
                    properties.add(readProperty(image, buffer));
                }
                return new WzConvexProperty(properties);
            }
            case SOUND -> {
                buffer.position(buffer.position() + 1);
                final int dataSize = readCompressedInt(buffer);
                final int duration = readCompressedInt(buffer);
                // Read header info
                final int headerOffset = buffer.position();
                buffer.position(buffer.position() + WzSoundProperty.SOUND_HEADER.length);
                final int formatSize = Byte.toUnsignedInt(buffer.get());
                buffer.position(buffer.position() + formatSize);
                // Create slices
                final ByteBuffer headerSlice = buffer.slice(headerOffset, buffer.position() - headerOffset);
                final ByteBuffer dataSlice = buffer.slice(buffer.position(), dataSize);
                buffer.position(buffer.position() + dataSize);
                return new WzSoundProperty(headerSlice, dataSlice);
            }
            case UOL -> {
                buffer.position(buffer.position() + 1);
                final String uol = readStringBlock(image, buffer);
                return new WzUolProperty(uol);
            }
            default -> throw new WzReaderError("Unhandled property type : %s", propertyType.name());
        }
    }

    public Map<String, Object> readListItems(WzImage image, ByteBuffer buffer) throws WzReaderError {
        final Map<String, Object> items = new HashMap<>();
        final int size = readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            final String itemName = readStringBlock(image, buffer);
            final byte itemType = buffer.get();
            switch (itemType) {
                case 0 -> {
                    items.put(itemName, null);
                }
                case 2, 11 -> {
                    final short shortValue = buffer.getShort();
                    items.put(itemName, shortValue);
                }
                case 3, 19 -> {
                    final int intValue = readCompressedInt(buffer);
                    items.put(itemName, intValue);
                }
                case 20 -> {
                    final long value = buffer.get();
                    if (value == Byte.MIN_VALUE) {
                        items.put(itemName, buffer.getLong());
                    } else {
                        items.put(itemName, value);
                    }
                }
                case 4 -> {
                    final byte floatType = buffer.get();
                    switch (floatType) {
                        case 0x00 -> items.put(itemName, 0f);
                        case (byte) 0x80 -> {
                            final float floatValue = buffer.getFloat();
                            items.put(itemName, floatValue);
                        }
                        default -> throw new WzReaderError("Unknown float type : %d", floatType);
                    }
                }
                case 5 -> {
                    final double doubleValue = buffer.getDouble();
                    items.put(itemName, doubleValue);
                }
                case 8 -> {
                    final String stringValue = readStringBlock(image, buffer);
                    items.put(itemName, stringValue);
                }
                case 9 -> {
                    final int propertySize = buffer.getInt();
                    final int propertyOffset = buffer.position();
                    final WzProperty property = readProperty(image, buffer);
                    items.put(itemName, property);
                    buffer.position(propertyOffset + propertySize);
                }
                default -> throw new WzReaderError("Unknown property item type : %d", itemType);
            }
        }
        return items;
    }

    @Override
    public void close() throws IOException {
        file.close();
        channel.close();
    }

    public static WzReader build(String path, WzReaderConfig config) throws FileNotFoundException {
        return build(new File(path), config);
    }

    public static WzReader build(Path path, WzReaderConfig config) throws FileNotFoundException {
        return build(path.toFile(), config);
    }

    public static WzReader build(File file, WzReaderConfig config) throws FileNotFoundException {
        return build(file, config, config.buildEncryptor());
    }

    public static WzReader build(File file, WzReaderConfig config, WzCrypto crypto) throws FileNotFoundException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        return new WzReader(randomAccessFile, fileChannel, config, crypto);
    }
}
