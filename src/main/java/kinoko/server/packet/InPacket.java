package kinoko.server.packet;

import kinoko.meta.SkillId;

public interface InPacket {
    byte peekByte();

    byte decodeByte();

    default boolean decodeBoolean() {
        return decodeByte() != 0;
    }

    short decodeShort();

    int decodeInt();

    SkillId decodeSkillId();

    long decodeLong();

    byte[] decodeArray(int length);

    String decodeString(int length);

    String decodeString();

    byte[] getData();

    int getRemaining();
}
