package kinoko.provider.wz;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public final class WzDirectory {
    private final WzPackage parent;
    private final int offset;
    private SequencedMap<String, Object> items;

    public WzDirectory(WzPackage parent, int offset) {
        this.parent = parent;
        this.offset = offset;
    }

    public Object getItem(String path) {
        final String[] split = path.split("/", 2);
        final var item = getItems().get(split[0]);
        if (split.length == 1) {
            return item;
        }
        if (item instanceof WzDirectory directory) {
            return directory.getItem(split[1]);
        } else if (item instanceof WzImage image) {
            return image.getItem(split[1]);
        }
        throw new IllegalStateException();
    }

    public SequencedMap<String, Object> getItems() {
        if (items == null) {
            items = readDirectory();
        }
        return items;
    }

    public SequencedMap<String, WzDirectory> getDirectories() {
        final SequencedMap<String, WzDirectory> result = new LinkedHashMap<>();
        for (var entry : getItems().entrySet()) {
            if (entry.getValue() instanceof WzDirectory directory) {
                result.put(entry.getKey(), directory);
            }
        }
        return Collections.unmodifiableSequencedMap(result);
    }

    public SequencedMap<String, WzImage> getImages() {
        final SequencedMap<String, WzImage> result = new LinkedHashMap<>();
        for (var entry : getItems().entrySet()) {
            if (entry.getValue() instanceof WzImage image) {
                result.put(entry.getKey(), image);
            }
        }
        return Collections.unmodifiableSequencedMap(result);
    }

    private SequencedMap<String, Object> readDirectory() {
        final SequencedMap<String, Object> result = new LinkedHashMap<>();
        final ByteBuffer buffer = parent.getBuffer(offset);
        final int size = WzReader.readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            final String itemName;
            byte itemType = buffer.get();
            switch (itemType) {
                case 1 -> {
                    // unknown : 01 XX 00 00 00 00 00 OFFSET
                    buffer.getInt();
                    buffer.getShort();
                    readOffset(buffer);
                    continue;
                }
                case 2 -> {
                    // string offset
                    final int stringOffset = buffer.getInt();
                    final int originalPosition = buffer.position();
                    buffer.position(parent.getStart() + stringOffset);
                    itemType = buffer.get();
                    itemName = WzReader.readString(buffer);
                    buffer.position(originalPosition);
                }
                case 3, 4 -> {
                    // directory | image
                    itemName = WzReader.readString(buffer);
                }
                default -> throw new WzReaderError("Unknown directory item type : %d", itemType);
            }
            final int itemSize = WzReader.readCompressedInt(buffer);
            final int itemChecksum = WzReader.readCompressedInt(buffer);
            final int itemOffset = readOffset(buffer);

            if (itemType == 3) {
                result.put(itemName, new WzDirectory(parent, itemOffset));
            } else if (itemType == 4) {
                result.put(itemName, new WzImage(parent, itemOffset));
            }
        }
        return Collections.unmodifiableSequencedMap(result);
    }

    private int readOffset(ByteBuffer buffer) {
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
}
