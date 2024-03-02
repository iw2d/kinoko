package kinoko.world.user.funckey;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class FuncKeyMapped implements Encodable {
    private final FuncKeyType type;
    private final int id;

    public FuncKeyMapped(FuncKeyType type, int id) {
        this.type = type;
        this.id = id;
    }

    public FuncKeyType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // FUNCKEY_MAPPED::Decode
        outPacket.encodeByte(type.getValue()); // nType
        outPacket.encodeInt(id); // nID
    }

    public static FuncKeyMapped of(FuncKeyType type, int id) {
        return new FuncKeyMapped(type, id);
    }

    public static FuncKeyMapped none() {
        return new FuncKeyMapped(FuncKeyType.NONE, 0);
    }
}
