package kinoko.packet.user;

import kinoko.provider.map.FieldType;
import kinoko.server.dialog.miniroom.MiniRoom;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.legend.Evan;
import kinoko.world.user.GuildInfo;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.List;

public final class UserPacket {
    // CUserPool::OnPacket ---------------------------------------------------------------------------------------------
    public static OutPacket userEnterField(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserEnterField);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        // CUserRemote::Init
        outPacket.encodeByte(user.getLevel()); // nLevel
        outPacket.encodeString(user.getCharacterName()); // sCharacterName

        final GuildInfo guildInfo = user.getGuildInfo();
        outPacket.encodeString(guildInfo.getGuildName()); // sGuildName
        outPacket.encodeShort(guildInfo.getMarkBg()); // nGuildMarkBg
        outPacket.encodeByte(guildInfo.getMarkBgColor()); // nGuildMarkBgColor
        outPacket.encodeShort(guildInfo.getMark()); // nGuildMark
        outPacket.encodeByte(guildInfo.getMarkColor()); // nGuildMarkColor

        user.getSecondaryStat().encodeForRemote(outPacket); // SecondaryStat::DecodeForRemote
        outPacket.encodeShort(user.getJob()); // nJobCode
        user.getCharacterData().getAvatarLook().encode(outPacket); // AvatarLook::AvatarLook

        outPacket.encodeInt(0); // dwDriverID
        outPacket.encodeInt(0); // dwPassenserID
        outPacket.encodeInt(0); // nChocoCount
        outPacket.encodeInt(user.getEffectItemId()); // nActiveEffectItemID
        outPacket.encodeInt(0); // nCompletedSetItemID
        outPacket.encodeInt(user.getPortableChairId()); // nPortableChairID

        outPacket.encodeShort(user.getX()); // x
        outPacket.encodeShort(user.getY()); // y
        outPacket.encodeByte(user.getMoveAction()); // nMoveAction
        outPacket.encodeShort(user.getFoothold());

        outPacket.encodeByte(false); // bShowAdminEffect

        for (Pet pet : user.getPets()) {
            outPacket.encodeByte(true);
            pet.encode(outPacket); // CPet::Init
        }
        outPacket.encodeByte(0);

        outPacket.encodeInt(0); // nTamingMobLevel
        outPacket.encodeInt(0); // nTamingMobExp
        outPacket.encodeInt(0); // nTamingMobFatigue

        if (user.getDialog() instanceof MiniRoom miniRoom && miniRoom.getType().isBalloon() && miniRoom.isOwner(user)) {
            outPacket.encodeByte(miniRoom.getType().getValue()); // nMiniRoomType
            outPacket.encodeInt(miniRoom.getId()); // dwMiniRoomSN
            outPacket.encodeString(miniRoom.getTitle()); // sMiniRoomTitle
            outPacket.encodeByte(miniRoom.isPrivate()); // bPrivate
            outPacket.encodeByte(miniRoom.getGameSpec()); // nGameKind
            outPacket.encodeByte(miniRoom.getUsers().size()); // nCurUsers
            outPacket.encodeByte(miniRoom.getMaxUsers()); // nMaxUsers
            outPacket.encodeByte(miniRoom.isGameOn()); // bGameOn
        } else {
            outPacket.encodeByte(0); // nMiniRoomType
        }

        outPacket.encodeByte(user.getAdBoard() != null); // bADBoardRemote
        if (user.getAdBoard() != null) {
            outPacket.encodeString(user.getAdBoard()); // sMsg
        }

        user.getCharacterData().getCoupleRecord().encodeForRemote(outPacket);

        // CUser::DarkForceEffect | CDragon::CreateEffect | CUser::LoadSwallowingEffect
        byte effectFlag = 0;
        if (Warrior.isBerserkEffect(user)) {
            effectFlag |= 0x1;
        }
        if (Evan.isDragonFury(user)) {
            effectFlag |= 0x2;
        }
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Swallow_Mob)) {
            effectFlag |= 0x4;
        }
        outPacket.encodeByte(effectFlag);

        outPacket.encodeByte(false); // bool -> int * int (CUserPool::OnNewYearCardRecordAdd)
        outPacket.encodeInt(0); // nPhase
        // ~CUserRemote::Init

        // field->DecodeFieldSpecificData
        final FieldType fieldType = user.getField().getFieldType();
        if (fieldType == FieldType.COCONUT || fieldType == FieldType.BATTLEFIELD) {
            // CField_BattleField::DecodeFieldSpecificData, CField_Coconut::DecodeFieldSpecificData
            outPacket.encodeByte(0); // nTeam
        } else if (fieldType == FieldType.MONSTERCARNIVAL || fieldType == FieldType.MONSTERCARNIVALREVIVE) {
            // CField_MonsterCarnival::DecodeFieldSpecificData,  CField_MonsterCarnivalRevive::DecodeFieldSpecificData
            outPacket.encodeByte(0); // nTeamForMCarnival
        }
        return outPacket;
    }

    public static OutPacket userLeaveField(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserLeaveField);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        return outPacket;
    }


    // CUserPool::OnUserCommonPacket -----------------------------------------------------------------------------------

    public static OutPacket userChat(User user, ChatType type, String text, boolean onlyBalloon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserChat);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(type.getValue()); // lType
        outPacket.encodeString(text); // sChat
        outPacket.encodeByte(onlyBalloon); // bOnlyBalloon
        return outPacket;
    }

    public static OutPacket userChatRemote(User user, String characterName, ChatType type, String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserChatNLCPQ);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(type.getValue()); // lType
        outPacket.encodeString(text); // sChat
        outPacket.encodeByte(false); // bOnlyBalloon
        outPacket.encodeString(characterName); // sChatCharacter
        return outPacket;
    }

    public static OutPacket userAdBoard(User user, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserADBoard);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(message != null);
        if (message != null) {
            outPacket.encodeString(message);
        }
        return outPacket;
    }

    public static OutPacket userMiniRoomBalloon(User user, MiniRoom miniRoom) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMiniRoomBalloon);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(miniRoom.getType().getValue()); // nMiniRoomType
        outPacket.encodeInt(miniRoom.getId()); // dwMiniRoomSN
        outPacket.encodeString(miniRoom.getTitle()); // sMiniRoomTitle
        outPacket.encodeByte(miniRoom.isPrivate()); // bPrivate
        outPacket.encodeByte(miniRoom.getGameSpec()); // nGameKind
        outPacket.encodeByte(miniRoom.getUsers().size()); // nCurUsers
        outPacket.encodeByte(miniRoom.getMaxUsers()); // nMaxUsers
        outPacket.encodeByte(miniRoom.isGameOn()); // bGameOn
        return outPacket;
    }

    public static OutPacket userMiniRoomBalloonRemove(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMiniRoomBalloon);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(0); // 0 -> CChatBalloon::DestroyMiniRoomBalloon
        return outPacket;
    }

    public static OutPacket userConsumeItemEffect(User user, int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserConsumeItemEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(itemId); // nConsumeItemID
        return outPacket;
    }

    public static OutPacket userItemUpgradeEffect(User user, boolean success, boolean cursed, boolean enchantSkill, boolean whiteScroll) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserItemUpgradeEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(success); // bSuccess
        outPacket.encodeByte(cursed); // bCursed
        outPacket.encodeByte(enchantSkill); // bEnchantSkill
        outPacket.encodeInt(0); // nEnchantCategory
        outPacket.encodeByte(whiteScroll); // bWhiteScroll
        outPacket.encodeByte(false); // bRecoverable -> CWvsContext::AskWhetherUsePamsSong
        return outPacket;
    }

    public static OutPacket userItemUpgradeEffectEnchantError(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserItemUpgradeEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(-1); // CUIEnchantDlg::SetResult => CUtilDlg::Notice("You cannot use a Scroll with this item.");
        outPacket.encodeByte(false); // bCursed
        outPacket.encodeByte(true); // bEnchantSkill
        outPacket.encodeInt(0); // nEnchantCategory
        outPacket.encodeByte(false); // bWhiteScroll
        outPacket.encodeByte(false); // bRecoverable
        return outPacket;
    }

    public static OutPacket userItemHyperUpgradeEffect(User user, boolean success, boolean cursed, boolean enchantSkill) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserItemHyperUpgradeEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(success); // bSuccess
        outPacket.encodeByte(cursed); // bCursed
        outPacket.encodeByte(enchantSkill); // bEnchantSkill
        outPacket.encodeInt(0); // nEnchantCategory
        return outPacket;
    }

    public static OutPacket userItemOptionUpgradeEffect(User user, boolean success, boolean cursed, boolean enchantSkill) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserItemOptionUpgradeEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(success); // bSuccess
        outPacket.encodeByte(cursed); // bCursed
        outPacket.encodeByte(enchantSkill); // bEnchantSkill
        outPacket.encodeInt(0); // nEnchantCategory
        return outPacket;
    }

    public static OutPacket userItemReleaseEffect(User user, int position) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserItemReleaseEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeShort(position);
        return outPacket;
    }

    public static OutPacket userItemUnreleaseEffect(User user, boolean success) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserItemUnreleaseEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(success);
        return outPacket;
    }

    public static OutPacket userTeslaTriangle(User user, List<Summoned> rockAndShockList) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTeslaTriangle);
        outPacket.encodeInt(user.getCharacterId());
        for (Summoned summoned : rockAndShockList) {
            outPacket.encodeInt(summoned.getId());
        }
        return outPacket;
    }
}
