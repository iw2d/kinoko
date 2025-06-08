package kinoko.provider.wz.serialize;

import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzReader;
import kinoko.provider.wz.WzReaderError;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public final class WzProperty extends WzSerialize {
    private SequencedMap<String, Object> items;

    public WzProperty(WzImage parent, int offset) {
        super(parent, offset);
    }

    public WzProperty(WzImage parent, int offset, SequencedMap<String, Object> items) {
        super(parent, offset);
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) getItems().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        final T result = get(key);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public Object getItem(String path) {
        final String[] split = path.split("/", 2);
        final var item = getItems().get(split[0]);
        if (split.length == 1) {
            return item;
        }
        if (item instanceof WzProperty property) {
            return property.getItem(split[1]);
        }
        throw new WzReaderError("Tried to access path : %s which does not exist", path);
    }

    public SequencedMap<String, Object> getItems() {
        if (items == null) {
            items = readProperty();
        }
        return items;
    }

    private SequencedMap<String, Object> readProperty() {
        final ByteBuffer buffer = parent.getBuffer(offset);
        WzReader.readStringBlock(parent, buffer);
        buffer.position(buffer.position() + 2); // reserved

        final SequencedMap<String, Object> items = new LinkedHashMap<>();
        final int size = WzReader.readCompressedInt(buffer);
        for (int i = 0; i < size; i++) {
            final String itemName = WzReader.readStringBlock(parent, buffer);
            final byte itemType = buffer.get();
            switch (itemType) {
                case 0 -> {
                    items.put(itemName, null);
                }
                case 2, 18 -> {
                    final short shortValue = buffer.getShort();
                    items.put(itemName, shortValue);
                }
                case 3, 19 -> {
                    final int intValue = WzReader.readCompressedInt(buffer);
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
                    final String stringValue = WzReader.readStringBlock(parent, buffer);
                    items.put(itemName, stringValue);
                }
                case 9 -> {
                    final int itemSize = buffer.getInt();
                    final int itemOffset = buffer.position();
                    items.put(itemName, WzReader.readPropertyItem(parent, buffer, itemOffset));
                    buffer.position(itemSize + itemOffset);
                }
                default -> throw new WzReaderError("Unknown property item type : %d", itemType);
            }
        }
        return Collections.unmodifiableSequencedMap(items);
    }
}
