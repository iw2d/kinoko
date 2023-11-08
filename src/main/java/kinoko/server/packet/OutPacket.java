package kinoko.server.packet;

import kinoko.server.header.OutHeader;
import kinoko.util.FileTime;

import java.time.Instant;

public interface OutPacket {
    void encodeByte(byte value);

    default void encodeByte(boolean value) {
        encodeByte(value ? 1 : 0);
    }

    default void encodeByte(short value) {
        encodeByte((byte) value);
    }

    default void encodeByte(int value) {
        encodeByte((byte) value);
    }

    void encodeShort(short value);

    default void encodeShort(int value) {
        encodeShort((short) value);
    }

    void encodeInt(int value);

    void encodeLong(long value);

    default void encodeFT(FileTime ft) {
        encodeInt(ft.getHighDateTime());
        encodeInt(ft.getLowDateTime());
    }

    default void encodeFT(Instant timestamp) {
        encodeFT(FileTime.from(timestamp));
    }

    void encodeArray(byte[] value);

    void encodeString(String value, int length);

    void encodeString(String value);

    int getSize();

    byte[] getData();

    static OutPacket of() {
        return new NioBufferOutPacket();
    }

    static OutPacket of(OutHeader op) {
        final OutPacket outPacket = of();
        outPacket.encodeShort(op.getValue());
        return outPacket;
    }
}
