package kinoko.provider.wz;

import kinoko.provider.wz.property.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public final class WzArchiveReader implements AutoCloseable {
    private final WzReader reader;
    private final Map<Integer, String> stringTable;

    private WzArchiveReader(WzReader reader) {
        this.reader = reader;
        this.stringTable = new HashMap<>();
    }

    private int computeVersionHash(int version) {
        int versionHash = 0;
        for (final byte c : String.valueOf(version).getBytes()) {
            versionHash = (versionHash * 32) + c + 1;
        }
        return versionHash;
    }
    private int readOffset(WzPackage parent) throws IOException {
        final int start = parent.getStart();
        final int hash = parent.getHash();
        int result = (int)reader.position();
        result = ~(result - start);
        result = result * hash;
        result = result - WzConstants.WZ_OFFSET_CONSTANT;
        result = Integer.rotateLeft(result, result & 0x1F);
        result = result ^ reader.readInt(); // encrypted offset
        result = result + (start * 2);
        return result;
    }


    private String readStringBlock(WzImage image) throws WzReaderError, IOException {
        final byte stringType = reader.readByte();
        switch (stringType) {
            case 0x00, 0x73 -> {
                var offset = (int)reader.position();
                var result = reader.readString();
                stringTable.put(offset, result);
                return result;
            }
            case 0x01, 0x1B -> {
                var offset = reader.readInt() + image.getOffset();
                return stringTable.get(offset);
            }
            default -> throw new WzReaderError("Unknown string block type : %d%n", stringType);
        }
    }

    public WzPackage readPackage() throws IOException, WzReaderError {
        return readPackage(0);
    }

    public WzPackage readPackage(int offset) throws IOException, WzReaderError {
        reader.seek(offset);


        // Check PKG1 header
        if (reader.readInt() != 0x31474B50) {
            throw new WzReaderError("PKG1 header missing");
        }
        final long size = reader.readLong();
        final int start = reader.readInt();

        // Check version hash
        reader.seek(start);
        final int versionHeader = Short.toUnsignedInt(reader.readShort());
        final int versionHash = computeVersionHash(reader.getConfig().getVersion());
        final int computedHeader = 0xFF
                ^ ((versionHash >> 24) & 0xFF)
                ^ ((versionHash >> 16) & 0xFF)
                ^ ((versionHash >> 8) & 0xFF)
                ^ (versionHash & 0xFF);
        if (versionHeader != computedHeader) {
            throw new WzReaderError("Incorrect version");
        }
        WzPackage pkg = new WzPackage(start, versionHash);
        pkg.setDirectory(readDirectory(pkg));
        return pkg;
    }

    public WzDirectory readDirectory(WzPackage parent) throws WzReaderError, IOException {
        final Map<String, WzDirectory> directories = new HashMap<>();
        final Map<String, WzImage> images = new HashMap<>();
        final int size = reader.readWzInt();
        for (int i = 0; i < size; i++) {
            final String childName;
            byte childType = reader.readByte();
            switch (childType) {
                case 1 -> {
                    // unknown : 01 XX 00 00 00 00 00 OFFSET
                    reader.readInt();
                    reader.readByte();
                    reader.readByte();
                    readOffset(parent);
                    continue;
                }
                case 2 -> {
                    // string offset
                    final int stringOffset = reader.readInt();
                    final int originalPosition = (int)reader.position();
                    reader.seek(parent.getStart() + stringOffset);
                    childType = reader.readByte();
                    childName = reader.readString();
                    reader.seek(originalPosition);
                }
                case 3, 4 -> {
                    // directory | image
                    childName = reader.readString();
                }
                default -> throw new WzReaderError("Unknown directory child type : %d", childType);
            }
            final int childSize = reader.readWzInt();
            final int childChecksum = reader.readWzInt();
            final int childOffset = readOffset(parent);

            final int originalPosition = (int)reader.position();
            reader.seek(childOffset);
            if (childType == 3) {
                directories.put(childName, readDirectory(parent));
            } else if (childType == 4) {
                final WzImage image = new WzImage(childOffset);
                if (!(readProperty(image) instanceof WzListProperty listProperty)) {
                    throw new WzReaderError("Image property is not a list");
                }
                image.setProperty(listProperty);
                images.put(childName, image);
            }

            reader.seek(originalPosition);
        }
        return new WzDirectory(directories, images);
    }

    public WzProperty readProperty(WzImage image) throws WzReaderError, IOException {
        final String propertyTypeId = readStringBlock(image);
        final WzPropertyType propertyType = WzPropertyType.getById(propertyTypeId);
        switch (propertyType) {
            case LIST -> {
                reader.readShort(); // reserved
                return WzListProperty.from(readListItems(image));
            }
            case CANVAS -> {
                reader.readByte();
                final boolean hasProperties = reader.readByte() == 1;
                final WzListProperty properties;
                if (hasProperties) {
                    reader.readShort();
                    properties = WzListProperty.from(readListItems(image));
                } else {
                    properties = WzListProperty.from(new LinkedHashMap<>());
                }
                // Canvas meta
                final int width = reader.readWzInt();
                final int height = reader.readWzInt();
                final int format = reader.readWzInt();
                final int format2 = reader.readWzInt();
                reader.readInt();
                // Canvas data
                final int dataSize = reader.readInt() - 1;
                reader.readByte();

                //ByteBuffer dataSlice = buffer.slice(buffer.position(), dataSize);
                //buffer.position(buffer.position() + dataSize);
                reader.skip(dataSize);
                ByteBuffer dataSlice = ByteBuffer.allocate(10);
                return new WzCanvasProperty(properties, width, height, format, format2, dataSlice);
            }
            case VECTOR -> {
                final int x = reader.readWzInt();
                final int y = reader.readWzInt();
                return new WzVectorProperty(x, y);
            }
            case CONVEX -> {
                final List<WzProperty> properties = new ArrayList<>();
                final int size = reader.readWzInt();
                for (int i = 0; i < size; i++) {
                    properties.add(readProperty(image));
                }
                return new WzConvexProperty(properties);
            }
            case SOUND -> {
                /*buffer.position(buffer.position() + 1);
                final int dataSize = reader.readWzInt();
                final int duration = reader.readWzInt();
                // Read header info
                final int headerOffset = buffer.position();
                buffer.position(buffer.position() + WzSoundProperty.SOUND_HEADER.length);
                final int formatSize = Byte.toUnsignedInt(buffer.get());
                buffer.position(buffer.position() + formatSize);
                // Create slices
                final ByteBuffer headerSlice = buffer.slice(headerOffset, buffer.position() - headerOffset);
                final ByteBuffer dataSlice = buffer.slice(buffer.position(), dataSize);
                buffer.position(buffer.position() + dataSize);
                return new WzSoundProperty(headerSlice, dataSlice);*/
                throw new IOException("Java is crap");
            }
            case UOL -> {
                reader.readByte();
                final String uol = readStringBlock(image);
                return new WzUolProperty(uol);
            }
            default -> throw new WzReaderError("Unhandled property type : %s", propertyType.name());
        }
    }

    public SequencedMap<String, Object> readListItems(WzImage image) throws WzReaderError, IOException {
        final SequencedMap<String, Object> items = new LinkedHashMap<>();
        final int size = reader.readWzInt();
        for (int i = 0; i < size; i++) {
            final String itemName = readStringBlock(image);
            final var itemType = VarType.fromCode((int)reader.readByte());
            switch (itemType) {
                case VarType.EMPTY -> {
                    items.put(itemName, null);
                }
                case VarType.I2, VarType.UI2 -> {
                    final short shortValue = reader.readShort();
                    items.put(itemName, shortValue);
                }
                case VarType.I4, VarType.UI4 -> {
                    final int intValue = reader.readWzInt();
                    items.put(itemName, intValue);
                }
                case VarType.I8 -> {
                    final long value = reader.readByte();
                    if (value == Byte.MIN_VALUE) {
                        items.put(itemName, reader.readLong());
                    } else {
                        items.put(itemName, value);
                    }
                }
                case VarType.R4 -> {
                    final byte floatType = reader.readByte();
                    switch (floatType) {
                        case 0x00 -> items.put(itemName, 0f);
                        case (byte) 0x80 -> {
                            final float floatValue = reader.readFloat();
                            items.put(itemName, floatValue);
                        }
                        default -> throw new WzReaderError("Unknown float type : %d", floatType);
                    }
                }
                case VarType.R8 -> {
                    final double doubleValue = reader.readDouble();
                    items.put(itemName, doubleValue);
                }
                case VarType.BSTR -> {
                    final String stringValue = readStringBlock(image);
                    items.put(itemName, stringValue);
                }
                case VarType.DISPATCH -> {
                    final int propertySize = reader.readInt();
                    final int propertyOffset = (int)reader.position();
                    final WzProperty property = readProperty(image);
                    items.put(itemName, property);
                    reader.seek(propertyOffset + propertySize);
                }
                default -> throw new WzReaderError("Unknown property item type : %d", itemType);
            }
        }
        return items;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public static WzArchiveReader build(String path, WzReaderConfig config) throws IOException {
        return build(Path.of(path), config);
    }

    public static WzArchiveReader build(Path path, WzReaderConfig config) throws IOException {
        return build(FileChannel.open(path), config);
    }

    public static WzArchiveReader build(FileChannel file, WzReaderConfig config) throws FileNotFoundException {
        return build(file, config, config.buildEncryptor());
    }

    public static WzArchiveReader build(FileChannel file, WzReaderConfig config, WzCrypto crypto) throws FileNotFoundException {
        var reader = new BufferedSeekableReader(file);
        var wzReader = new WzReader(reader, config, crypto);

        return new WzArchiveReader(wzReader);
    }
}
