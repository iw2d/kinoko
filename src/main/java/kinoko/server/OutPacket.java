package kinoko.server;

import kinoko.server.netty.NioBufferOutPacket;

public interface OutPacket {
    static OutPacket create() {
        return new NioBufferOutPacket();
    }

    static OutPacket create(OutHeader op) {
        final OutPacket outPacket = create();
        outPacket.encodeShort(op.getValue());
        return outPacket;
    }

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

    void encodeArray(byte[] value);

    void encodeString(String value, int length);

    void encodeString(String value);

    int getSize();

    byte[] getData();
}
