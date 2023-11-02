package kinoko.server;

import kinoko.util.Util;

public interface InPacket {
    byte decodeByte();

    short decodeShort();

    int decodeInt();

    long decodeLong();

    byte[] decodeArray(int length);

    String decodeString(int length);

    String decodeString();

    byte[] getData();
}
