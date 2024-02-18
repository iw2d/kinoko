package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.item.InventoryOperation;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterStat;
import kinoko.world.user.StatFlag;

import java.util.List;
import java.util.Set;

public final class WvsContext {
    public static OutPacket statChanged(Set<StatFlag> flags, CharacterData characterData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.STAT_CHANGED);
        outPacket.encodeByte(true); // bool -> bExclRequestSent = 0

        final CharacterStat characterStat = characterData.getCharacterStat();
        characterStat.encodeChangeStat(flags, characterData.getCharacterInventory().getMoney(), outPacket);

        outPacket.encodeByte(false); // bool -> byte (CUserLocal::SetSecondaryStatChangedPoint)
        outPacket.encodeByte(false); // bool -> int, int (CBattleRecordMan::SetBattleRecoveryInfo)
        return outPacket;
    }

    public static OutPacket message(Message message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MESSAGE);
        message.encode(outPacket);
        return outPacket;
    }

    public static OutPacket inventoryOperation(InventoryOperation op) {
        return inventoryOperation(List.of(op), true);
    }

    public static OutPacket inventoryOperation(List<InventoryOperation> inventoryOperations, boolean exclRequestSent) {
        final OutPacket outPacket = OutPacket.of(OutHeader.INVENTORY_OPERATION);
        outPacket.encodeByte(exclRequestSent); // bool -> bExclRequestSent = 0
        outPacket.encodeByte(inventoryOperations.size());
        for (InventoryOperation op : inventoryOperations) {
            op.encode(outPacket);
        }
        return outPacket;
    }
}
