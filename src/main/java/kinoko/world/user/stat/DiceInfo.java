package kinoko.world.user.stat;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class DiceInfo implements Encodable {
    public static final DiceInfo DEFAULT = new DiceInfo();
    private final int[] infoArray = new int[22];

    public int[] getInfoArray() {
        return infoArray;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // aDiceInfo
        for (int i = 0; i < 22; i++) {
            outPacket.encodeInt(infoArray[i]);
        }
    }

    public static DiceInfo decode(InPacket inPacket) {
        final DiceInfo diceInfo = new DiceInfo();
        for (int i = 0; i < 22; i++) {
            diceInfo.infoArray[i] = inPacket.decodeInt();
        }
        return diceInfo;
    }
}
