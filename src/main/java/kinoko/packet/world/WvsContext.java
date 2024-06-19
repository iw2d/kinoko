package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.FileTime;
import kinoko.world.GameConstants;
import kinoko.world.field.TownPortal;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.item.ItemConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.WildHunterInfo;
import kinoko.world.user.config.SingleMacro;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.ExtendSp;
import kinoko.world.user.stat.SecondaryStat;
import kinoko.world.user.stat.Stat;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class WvsContext {
    public static OutPacket statChanged(Stat stat, Object value, boolean exclRequest) {
        return statChanged(Map.of(stat, value), exclRequest);
    }

    public static OutPacket statChanged(Map<Stat, Object> statMap, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.StatChanged);
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
                    case FACE, HAIR, HP, MHP, MP, MMP, EXP, MONEY, TEMPEXP -> {
                        outPacket.encodeInt((int) statMap.get(stat));
                    }
                    case PETSN, PETSN2, PETSN3 -> {
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

    public static OutPacket temporaryStatSet(SecondaryStat secondaryStat, BitFlag<CharacterTemporaryStat> flag) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TemporaryStatSet);
        secondaryStat.encodeForLocal(flag, outPacket);
        outPacket.encodeShort(0); // tDelay
        outPacket.encodeByte(0); // SecondaryStat::IsMovementAffectingStat -> bSN
        return outPacket;
    }

    public static OutPacket temporaryStatReset(BitFlag<CharacterTemporaryStat> flag) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TemporaryStatReset);
        flag.encode(outPacket);
        outPacket.encodeByte(0); // SecondaryStat::IsMovementAffectingStat -> bSN
        return outPacket;
    }

    public static OutPacket changeSkillRecordResult(SkillRecord skillRecord, boolean exclRequest) {
        return changeSkillRecordResult(Set.of(skillRecord), exclRequest);
    }

    public static OutPacket changeSkillRecordResult(Set<SkillRecord> skillRecords, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ChangeSkillRecordResult);
        outPacket.encodeByte(exclRequest); // bool -> bExclRequestSent = 0
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
        final OutPacket outPacket = OutPacket.of(OutHeader.SkillUseResult);
        outPacket.encodeByte(0); // unused, packet sets bExclRequestSent = 0
        return outPacket;
    }

    public static OutPacket inventoryOperation(InventoryOperation op, boolean exclRequest) {
        return inventoryOperation(List.of(op), exclRequest);
    }

    public static OutPacket inventoryOperation(List<InventoryOperation> inventoryOperations, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.InventoryOperation);
        outPacket.encodeByte(exclRequest); // bool -> bExclRequestSent = 0
        outPacket.encodeByte(inventoryOperations.size());
        for (InventoryOperation op : inventoryOperations) {
            op.encode(outPacket);
        }
        outPacket.encodeByte(0); // bSN
        return outPacket;
    }

    public static OutPacket gatherItemResult(InventoryType inventoryType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GatherItemResult);
        outPacket.encodeByte(0); // ignored
        outPacket.encodeByte(inventoryType.getValue());
        return outPacket;
    }

    public static OutPacket sortItemResult(InventoryType inventoryType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SortItemResult);
        outPacket.encodeByte(0); // ignored
        outPacket.encodeByte(inventoryType.getValue());
        return outPacket;
    }

    public static OutPacket setGender(int gender) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SetGender);
        outPacket.encodeByte(gender); // nGender
        return outPacket;
    }

    public static OutPacket characterInfo(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CharacterInfo);
        // CWvsContext::OnCharacterInfo, TODO: add missing information
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        outPacket.encodeByte(user.getLevel()); // nLevel
        outPacket.encodeShort(user.getJob()); // nJob
        outPacket.encodeShort(user.getPop()); // nPOP
        outPacket.encodeByte(false); // bIsMarried
        outPacket.encodeString(""); // sCommunity
        outPacket.encodeString(""); // sAlliance
        outPacket.encodeByte(false); // bMedalInfo

        // bPetActivated -> CUIUserInfo::SetMultiPetInfo
        for (Pet pet : user.getPets()) {
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
        final List<Item> chairs = user.getInventoryManager().getInstallInventory().getItems().values().stream()
                .filter((item) -> ItemConstants.isPortableChairItem(item.getItemId()))
                .toList();
        outPacket.encodeInt(chairs.size());
        chairs.forEach((item) -> outPacket.encodeInt(item.getItemId()));
        return outPacket;
    }

    public static OutPacket townPortal(TownPortal townPortal) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TownPortal);
        outPacket.encodeInt(townPortal.getTownField().getFieldId()); // dwTownID
        outPacket.encodeInt(townPortal.getField().getFieldId()); // dwFieldID
        outPacket.encodeInt(townPortal.getSkillId()); // nSkillID
        outPacket.encodeShort(townPortal.getX()); // ptFieldPortal.x
        outPacket.encodeShort(townPortal.getY()); // ptFieldPortal.y
        return outPacket;
    }

    public static OutPacket resetTownPortal() {
        final OutPacket outPacket = OutPacket.of(OutHeader.TownPortal);
        outPacket.encodeInt(GameConstants.UNDEFINED_FIELD_ID);
        outPacket.encodeInt(GameConstants.UNDEFINED_FIELD_ID);
        outPacket.encodeInt(0);
        outPacket.encodeShort(0);
        outPacket.encodeShort(0);
        return outPacket;
    }

    public static OutPacket wildHunterInfo(WildHunterInfo wildHunterInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.WildHunterInfo);
        wildHunterInfo.encode(outPacket); // GW_WildHunterInfo::Decode
        return outPacket;
    }

    public static OutPacket macroSysDataInit(List<SingleMacro> macroSysData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MacroSysDataInit);
        outPacket.encodeByte(macroSysData.size());
        for (SingleMacro macroSysDatum : macroSysData) {
            macroSysDatum.encode(outPacket); // SINGLEMACRO::Decode
        }
        return outPacket;
    }
}
