package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Tuple;
import kinoko.world.item.Item;
import kinoko.world.skill.maker.MakerResult;
import kinoko.world.skill.maker.RecipeClass;

import java.util.List;

public final class MakerPacket {
    // CUserLocal::OnMakerResult ---------------------------------------------------------------------------------------

    public static OutPacket normal(boolean success, int targetItemId, int targetItemCount, List<Tuple<Integer, Integer>> lostItems, int totalCost) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMakerResult);
        final MakerResult makerResult = success ? MakerResult.SUCCESS : MakerResult.DESTROYED;
        outPacket.encodeInt(makerResult.getValue()); // nResult
        outPacket.encodeInt(RecipeClass.NORMAL.getValue()); // same as HIDDEN
        outPacket.encodeByte(makerResult.getValue());
        if (success) {
            outPacket.encodeInt(targetItemId); // nTargetItem
            outPacket.encodeInt(targetItemCount); // nItemNum
        }
        outPacket.encodeInt(lostItems.size());
        for (var tuple : lostItems) {
            outPacket.encodeInt(tuple.getLeft());
            outPacket.encodeInt(tuple.getRight());
        }
        outPacket.encodeInt(0); // int * int
        outPacket.encodeByte(false); // bool -> int * int
        outPacket.encodeInt(totalCost);
        return outPacket;
    }

    public static OutPacket monsterCrystal(int targetItemId, int trophyItemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMakerResult);
        outPacket.encodeInt(MakerResult.SUCCESS.getValue()); // nResult
        outPacket.encodeInt(RecipeClass.MONSTER_CRYSTAL.getValue());
        outPacket.encodeInt(targetItemId); // nTargetItem
        outPacket.encodeInt(trophyItemId);
        return outPacket;
    }

    public static OutPacket equipDisassemble(int disassembledItemId, List<Item> rewardItems, int totalCost) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMakerResult);
        outPacket.encodeInt(MakerResult.SUCCESS.getValue()); // nResult
        outPacket.encodeInt(RecipeClass.EQUIP_DISASSEMBLE.getValue());
        outPacket.encodeInt(disassembledItemId);
        outPacket.encodeInt(rewardItems.size());
        for (Item item : rewardItems) {
            outPacket.encodeInt(item.getItemId());
            outPacket.encodeInt(item.getQuantity());
        }
        outPacket.encodeInt(totalCost);
        return outPacket;
    }

    public static OutPacket unknown() {
        return MakerPacket.of(MakerResult.UNKNOWN);
    }

    public static OutPacket emptySlot() {
        return MakerPacket.of(MakerResult.EMPTYSLOT);
    }

    private static OutPacket of(MakerResult makerResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMakerResult);
        outPacket.encodeInt(makerResult.getValue()); // nResult
        return outPacket;
    }
}
