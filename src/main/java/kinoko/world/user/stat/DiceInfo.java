package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class DiceInfo implements Encodable {
    public static final int SIZE = 22;
    private final int[] infoArray = new int[SIZE];

    public int[] getInfoArray() {
        return infoArray;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // aDiceInfo
        for (int i = 0; i < SIZE; i++) {
            outPacket.encodeInt(infoArray[i]);
        }
    }
}
