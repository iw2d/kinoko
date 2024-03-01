package kinoko.packet.world;

import kinoko.packet.world.message.Message;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.stat.ExtendSp;
import kinoko.world.user.stat.Stat;
import kinoko.world.user.temp.TemporaryStatManager;

import java.util.List;
import java.util.Map;

public final class WvsContext {
    public static OutPacket statChanged(Stat stat, Object value, boolean exclRequest) {
        return statChanged(Map.of(stat, value), exclRequest);
    }

    public static OutPacket statChanged(Map<Stat, Object> statMap, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.STAT_CHANGED);
        outPacket.encodeByte(exclRequest); // bool -> bExclRequestSent = 0

        outPacket.encodeInt(Stat.from(statMap.keySet()));
        for (Stat stat : Stat.ENCODE_ORDER) {
            if (statMap.containsKey(stat)) {
                switch (stat) {
                    case SKIN, LEVEL -> {
                        outPacket.encodeByte((byte) statMap.get(stat));
                    }
                    case JOB, STR, DEX, INT, LUK, AP, POP -> {
                        outPacket.encodeShort((short) statMap.get(stat));
                    }
                    case FACE, HAIR, HP, MAX_HP, MP, MAX_MP, EXP, MONEY, TEMP_EXP -> {
                        outPacket.encodeInt((int) statMap.get(stat));
                    }
                    case PET_1, PET_2, PET_3 -> {
                        outPacket.encodeLong((long) statMap.get(stat));
                    }
                    case SP -> {
                        if (statMap.get(stat) instanceof ExtendSp sp) {
                            sp.encode(outPacket);
                        } else {
                            outPacket.encodeShort((short) statMap.get(stat));
                        }
                    }
                }
            }
        }

        outPacket.encodeByte(false); // bool -> byte (CUserLocal::SetSecondaryStatChangedPoint)
        outPacket.encodeByte(false); // bool -> int, int (CBattleRecordMan::SetBattleRecoveryInfo)
        return outPacket;
    }

    public static OutPacket temporaryStatSet(TemporaryStatManager tsm, boolean complete) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TEMPORARY_STAT_SET);
        tsm.encodeForLocal(outPacket, complete);
        outPacket.encodeShort(0); // tDelay
        outPacket.encodeByte(0); // SecondaryStat::IsMovementAffectingStat -> bSN
        return outPacket;
    }

    public static OutPacket temporaryStatReset(TemporaryStatManager tsm) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TEMPORARY_STAT_SET);
        tsm.encodeReset(outPacket);
        outPacket.encodeByte(0); // SecondaryStat::IsMovementAffectingStat -> bSN
        return outPacket;
    }

    public static OutPacket changeSkillRecordResult(SkillRecord skillRecord) {
        return changeSkillRecordResult(List.of(skillRecord));
    }

    public static OutPacket changeSkillRecordResult(List<SkillRecord> skillRecords) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CHANGE_SKILL_RECORD_RESULT);
        outPacket.encodeByte(true); // bool -> bExclRequestSent = 0
        outPacket.encodeShort(skillRecords.size());
        for (SkillRecord sr : skillRecords) {
            outPacket.encodeInt(sr.getSkillId()); // nSkillID
            outPacket.encodeInt(sr.getSkillLevel());
            outPacket.encodeInt(sr.getMasterLevel());
            outPacket.encodeFT(FileTime.DEFAULT_TIME); // dateExpire
        }
        outPacket.encodeByte(0); // bSN
        return outPacket;
    }

    public static OutPacket skillUseResult() {
        final OutPacket outPacket = OutPacket.of(OutHeader.SKILL_USE_RESULT);
        outPacket.encodeByte(0); // unused, packet sets bExclRequestSent = 0
        return outPacket;
    }

    public static OutPacket message(Message message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MESSAGE);
        message.encode(outPacket);
        return outPacket;
    }

    public static OutPacket inventoryOperation(InventoryOperation op, boolean exclRequest) {
        return inventoryOperation(List.of(op), exclRequest);
    }

    public static OutPacket inventoryOperation(List<InventoryOperation> inventoryOperations, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.INVENTORY_OPERATION);
        outPacket.encodeByte(exclRequest); // bool -> bExclRequestSent = 0
        outPacket.encodeByte(inventoryOperations.size());
        for (InventoryOperation op : inventoryOperations) {
            op.encode(outPacket);
        }
        outPacket.encodeByte(0); // bSN
        return outPacket;
    }

    public static OutPacket gatherItemResult(InventoryType inventoryType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GATHER_ITEM_RESULT);
        outPacket.encodeByte(0); // ignored
        outPacket.encodeByte(inventoryType.getValue());
        return outPacket;
    }

    public static OutPacket sortItemResult(InventoryType inventoryType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SORT_ITEM_RESULT);
        outPacket.encodeByte(0); // ignored
        outPacket.encodeByte(inventoryType.getValue());
        return outPacket;
    }

    public static OutPacket setGender(int gender) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SET_GENDER);
        outPacket.encodeByte(gender); // nGender
        return outPacket;
    }
}
