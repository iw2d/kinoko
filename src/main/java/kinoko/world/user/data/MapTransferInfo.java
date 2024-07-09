package kinoko.world.user.data;

import kinoko.server.packet.OutPacket;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;

public final class MapTransferInfo {
    private final List<Integer> mapTransfer = new ArrayList<>();
    private final List<Integer> mapTransferEx = new ArrayList<>();

    public List<Integer> getMapTransfer() {
        return mapTransfer;
    }

    public List<Integer> getMapTransferEx() {
        return mapTransferEx;
    }

    public boolean delete(int fieldId, boolean canTransferContinent) {
        if (canTransferContinent) {
            return mapTransferEx.remove(Integer.valueOf(fieldId));
        } else {
            return mapTransfer.remove(Integer.valueOf(fieldId));
        }
    }

    public boolean register(int fieldId, boolean canTransferContinent) {
        if (canTransferContinent) {
            if (mapTransferEx.size() >= 10) {
                return false;
            }
            mapTransferEx.add(fieldId);
        } else {
            if (mapTransfer.size() >= 5) {
                return false;
            }
            mapTransfer.add(fieldId);
        }
        return true;
    }

    public void encodeMapTransfer(OutPacket outPacket) {
        for (int i = 0; i < 5; i++) {
            if (i < mapTransfer.size()) {
                outPacket.encodeInt(mapTransfer.get(i));
            } else {
                outPacket.encodeInt(GameConstants.UNDEFINED_FIELD_ID);
            }
        }
    }

    public void encodeMapTransferEx(OutPacket outPacket) {
        for (int i = 0; i < 10; i++) {
            if (i < mapTransferEx.size()) {
                outPacket.encodeInt(mapTransferEx.get(i));
            } else {
                outPacket.encodeInt(GameConstants.UNDEFINED_FIELD_ID);
            }
        }
    }
}
