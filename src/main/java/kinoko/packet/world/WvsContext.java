package kinoko.packet.world;

import kinoko.packet.world.broadcast.BroadcastMessage;
import kinoko.packet.world.message.Message;
import kinoko.server.header.OutHeader;
import kinoko.server.memo.MemoResult;
import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.item.ItemConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static OutPacket temporaryStatSet(SecondaryStat ss, Map<CharacterTemporaryStat, TemporaryStatOption> setStats) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TEMPORARY_STAT_SET);
        SecondaryStat.encodeForLocal(outPacket, setStats);
        outPacket.encodeShort(0); // tDelay
        outPacket.encodeByte(0); // SecondaryStat::IsMovementAffectingStat -> bSN
        return outPacket;
    }

    public static OutPacket temporaryStatReset(Set<CharacterTemporaryStat> resetStats) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TEMPORARY_STAT_RESET);
        SecondaryStat.encodeReset(outPacket, resetStats);
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

    public static OutPacket memoResult(MemoResult memoResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MEMO_RESULT);
        memoResult.encode(outPacket);
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

    public static OutPacket characterInfo(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CHARACTER_INFO);
        // CWvsContext::OnCharacterInfo, TODO: add missing information
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        outPacket.encodeByte(user.getLevel()); // nLevel
        outPacket.encodeShort(user.getJob()); // nJob
        outPacket.encodeShort(user.getCharacterStat().getPop()); // nPOP
        outPacket.encodeByte(false); // bIsMarried
        outPacket.encodeString(""); // sCommunity
        outPacket.encodeString(""); // sAlliance
        outPacket.encodeByte(false); // bMedalInfo

        // bPetActivated -> CUIUserInfo::SetMultiPetInfo
        for (Pet pet : user.getPets()) {
            if (pet == null) {
                continue;
            }
            outPacket.encodeByte(true);
            outPacket.encodeInt(pet.getTemplateId()); // dwTemplateId
            outPacket.encodeString(pet.getName()); // sName
            outPacket.encodeByte(pet.getLevel()); // nLevel
            outPacket.encodeShort(pet.getTameness()); // nTameness
            outPacket.encodeByte(pet.getFullness()); // nRepleteness
            outPacket.encodeShort(pet.getPetSkill()); // usPetSkill
            outPacket.encodeInt(0); // nItemID
        }
        outPacket.encodeByte(0);
        // ~CUIUserInfo::SetMultiPetInfo

        // CUIUserInfo::SetTamingMobInfo (bool -> int, int, int)
        outPacket.encodeByte(false);

        // aWishItem (byte * int), itemId = 0 becomes Brown Flight Headgear for some reason
        final List<Integer> wishlist = user.getAccount().getWishlist().stream()
                .filter((itemId) -> itemId != 0)
                .toList();
        outPacket.encodeByte(wishlist.size());
        wishlist.forEach(outPacket::encodeInt);

        // MedalAchievementInfo::Decode
        outPacket.encodeInt(0); // nEquipedMedalID
        outPacket.encodeShort(0); // p_ausMedalQuestID (short * short)
        // ~MedalAchievementInfo::Decode

        // aChairItem
        final List<Item> chairs = user.getInventoryManager().getInventoryByType(InventoryType.INSTALL).getItems().values().stream()
                .filter((item) -> ItemConstants.isPortableChairItem(item.getItemId()))
                .toList();
        outPacket.encodeInt(chairs.size());
        chairs.forEach((item) -> outPacket.encodeInt(item.getItemId()));
        return outPacket;
    }

    public static OutPacket broadcastMsg(BroadcastMessage message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.BROADCAST_MSG);
        message.encode(outPacket);
        return outPacket;
    }
}
