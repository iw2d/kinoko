package kinoko.packet.world;

import kinoko.provider.npc.NpcImitateData;
import kinoko.server.cashshop.CashItemResultType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.util.FileTime;
import kinoko.world.GameConstants;
import kinoko.world.field.TownPortal;
import kinoko.world.item.*;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.data.PopularityResultType;
import kinoko.world.user.data.SingleMacro;
import kinoko.world.user.data.WildHunterInfo;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.ExtendSp;
import kinoko.world.user.stat.SecondaryStat;
import kinoko.world.user.stat.Stat;

import java.util.List;
import java.util.Map;

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
        return changeSkillRecordResult(List.of(skillRecord), exclRequest);
    }

    public static OutPacket changeSkillRecordResult(List<SkillRecord> skillRecords, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ChangeSkillRecordResult);
        outPacket.encodeByte(exclRequest); // bool -> bExclRequestSent = 0
        outPacket.encodeShort(skillRecords.size());
        for (SkillRecord sr : skillRecords) {
            outPacket.encodeSkillId(sr.getSkillId()); // nSkillID
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

    public static OutPacket givePopularityResult(PopularityResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GivePopularityResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }

    public static OutPacket givePopularityResultSuccess(String targetCharacterName, boolean inc, int pop) {
        final OutPacket outPacket = WvsContext.givePopularityResult(PopularityResultType.Success);
        outPacket.encodeString(targetCharacterName);
        outPacket.encodeByte(inc);
        outPacket.encodeInt(pop); // nPOP
        return outPacket;
    }

    public static OutPacket givePopularityResultNotify(String fromCharacterName, boolean inc) {
        final OutPacket outPacket = WvsContext.givePopularityResult(PopularityResultType.Notify);
        outPacket.encodeString(fromCharacterName);
        outPacket.encodeByte(inc);
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

    public static OutPacket inventoryGrow(InventoryType inventoryType, int newSize) {
        final OutPacket outPacket = OutPacket.of(OutHeader.InventoryGrow);
        outPacket.encodeByte(inventoryType.getValue());
        outPacket.encodeByte(newSize);
        return outPacket;
    }

    public static OutPacket skillLearnItemResult(int characterId, boolean masteryBook, boolean used, boolean success, boolean exclRequest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SkillLearnItemResult);
        outPacket.encodeByte(exclRequest); // bOnExclRequest
        outPacket.encodeInt(characterId); // dwCharacterId
        outPacket.encodeByte(masteryBook); // bIsMasteryBook
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeByte(used); // bUsed
        outPacket.encodeByte(success); // bSucceed
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
        outPacket.encodeString(user.getGuildInfo().getGuildName()); // sCommunity
        outPacket.encodeString(user.getGuildInfo().getAllianceName()); // sAlliance
        outPacket.encodeByte(false); // bMedalInfo

        // CUIUserInfo::SetMultiPetInfo
        final Inventory equipped = user.getInventoryManager().getEquipped();
        for (Pet pet : user.getPets()) {
            outPacket.encodeByte(true); // bPetActivated
            outPacket.encodeInt(pet.getTemplateId()); // dwTemplateId
            outPacket.encodeString(pet.getName()); // sName
            outPacket.encodeByte(pet.getLevel()); // nLevel
            outPacket.encodeShort(pet.getTameness()); // nTameness
            outPacket.encodeByte(pet.getFullness()); // nRepleteness
            outPacket.encodeShort(pet.getPetSkill()); // usPetSkill
            outPacket.encodeInt(pet.getPetWear()); // nItemID
        }
        outPacket.encodeByte(false);
        // ~CUIUserInfo::SetMultiPetInfo

        // CUIUserInfo::SetTamingMobInfo (bool -> int, int, int)
        outPacket.encodeByte(false);

        // aWishItem (byte * int), nCommSN = 0 becomes Brown Flight Headgear for some reason
        final List<Integer> wishlist = user.getAccount().getWishlist().stream()
                .filter((commodityId) -> commodityId != 0)
                .toList();
        outPacket.encodeByte(wishlist.size());
        wishlist.forEach(outPacket::encodeInt);

        // MedalAchievementInfo::Decode
        final Item medalItem = equipped.getItem(BodyPart.MEDAL.getValue());
        outPacket.encodeInt(medalItem != null ? medalItem.getItemId() : 0); // nEquipedMedalID
        final List<QuestRecord> titleQuestRecords = user.getQuestManager().getTitleQuests();
        outPacket.encodeShort(titleQuestRecords.size());
        titleQuestRecords.forEach((qr) -> outPacket.encodeShort(qr.getQuestId())); // p_ausMedalQuestID
        // ~MedalAchievementInfo::Decode

        // aChairItem
        final Inventory installInventory = user.getInventoryManager().getInstallInventory();
        final List<Item> chairs = installInventory.getItems().values().stream()
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

    public static OutPacket imitatedNpcData(List<NpcImitateData> npcImitateData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ImitatedNPCData);
        outPacket.encodeByte(npcImitateData.size());
        for (NpcImitateData data : npcImitateData) {
            data.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket mapleTvUseRes(String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MapleTVUseRes);
        outPacket.encodeString(message);
        return outPacket;
    }

    public static OutPacket avatarMegaphoneResQueueFull() {
        final OutPacket outPacket = OutPacket.of(OutHeader.AvatarMegaphoneRes);
        outPacket.encodeByte(CashItemResultType.AvatarMegaphone_Queue_Full.getValue());
        return outPacket;
    }

    public static OutPacket avatarMegaphoneResLevelLimit() {
        final OutPacket outPacket = OutPacket.of(OutHeader.AvatarMegaphoneRes);
        outPacket.encodeByte(CashItemResultType.AvatarMegaphone_Level_Limit.getValue());
        return outPacket;
    }

    public static OutPacket avatarMegaphoneRes(String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AvatarMegaphoneRes);
        outPacket.encodeByte(0);
        outPacket.encodeString(message);
        return outPacket;
    }

    public static OutPacket avatarMegaphoneUpdateMessage(User user, int itemId, String s1, String s2, String s3, String s4, boolean whisperIcon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AvatarMegaphoneUpdateMessage);
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeString(user.getCharacterName()); // sName
        outPacket.encodeString(s1);
        outPacket.encodeString(s2);
        outPacket.encodeString(s3);
        outPacket.encodeString(s4);
        outPacket.encodeInt(user.getChannelId());
        outPacket.encodeByte(whisperIcon);
        user.getCharacterData().getAvatarLook().encode(outPacket);
        return outPacket;
    }

    public static OutPacket avatarMegaphoneClearMessage() {
        return OutPacket.of(OutHeader.AvatarMegaphoneClearMessage);
    }

    public static OutPacket scriptProgressMessage(String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ScriptProgressMessage);
        outPacket.encodeString(message); // sMsg
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
