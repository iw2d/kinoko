package kinoko.server.packet;

public interface InPacket {
    byte decodeByte();

    default boolean decodeBoolean() {
        return decodeByte() != 0;
    }

    short decodeShort();

    int decodeInt();

    long decodeLong();

    byte[] decodeArray(int length);

    String decodeString(int length);

    String decodeString();

    byte[] getData();

    int getRemaining();
}
