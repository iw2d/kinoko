package kinoko.packet.user;

import kinoko.provider.map.FieldType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

public final class UserPoolPacket {
    public static OutPacket userEnterField(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_ENTER_FIELD);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        // CUserRemote::Init
        outPacket.encodeByte(user.getLevel()); // nLevel
        outPacket.encodeString(user.getName()); // sCharacterName
        outPacket.encodeString(""); // sGuildName
        outPacket.encodeShort(0); // nGuildMarkBg
        outPacket.encodeByte(0); // nGuildMarkBgColor
        outPacket.encodeShort(0); // nGuildMark
        outPacket.encodeByte(0); // nGuildMarkColor

        user.getTemporaryStatManager().encodeForRemote(outPacket, true); // SecondaryStat::DecodeForRemote
        outPacket.encodeShort(user.getJob()); // nJobCode
        user.getCharacterData().getAvatarLook().encode(outPacket); // AvatarLook::AvatarLook

        outPacket.encodeInt(0); // dwDriverID
        outPacket.encodeInt(0); // dwPassenserID
        outPacket.encodeInt(0); // nChocoCount
        outPacket.encodeInt(0); // nActiveEffectItemID
        outPacket.encodeInt(0); // nCompletedSetItemID
        outPacket.encodeInt(0); // nPortableChairID

        outPacket.encodeShort(user.getX()); // x
        outPacket.encodeShort(user.getY()); // y
        outPacket.encodeByte(user.getMoveAction()); // nMoveAction
        outPacket.encodeShort(user.getFh());

        outPacket.encodeByte(false); // bShowAdminEffect

        outPacket.encodeByte(0); // byte -> CPet::Init

        outPacket.encodeInt(0); // nTamingMobLevel
        outPacket.encodeInt(0); // nTamingMobExp
        outPacket.encodeInt(0); // nTamingMobFatigue

        outPacket.encodeByte(0); // nMiniRoomType

        outPacket.encodeByte(false); // bADBoardRemote

        outPacket.encodeByte(false); // coupleItem
        outPacket.encodeByte(false); // friendShipItem
        outPacket.encodeByte(false); // marriageRecord

        outPacket.encodeByte(0); // CUser::DarkForceEffect | CDragon::CreateEffect | CUser::LoadSwallowingEffect

        outPacket.encodeByte(false); // bool -> int * int (CUserPool::OnNewYearCardRecordAdd)
        outPacket.encodeInt(0); // nPhase
        // ~CUserRemote::Init

        // field->DecodeFieldSpecificData
        final FieldType fieldType = user.getField().getFieldType();
        if (fieldType == FieldType.BATTLEFIELD || fieldType == FieldType.COCONUT) {
            // CField_BattleField::DecodeFieldSpecificData, CField_Coconut::DecodeFieldSpecificData
            outPacket.encodeByte(0); // nTeam
        } else if (fieldType == FieldType.MONSTER_CARNIVAL || fieldType == FieldType.MONSTER_CARNIVAL_REVIVE) {
            // CField_MonsterCarnival::DecodeFieldSpecificData,  CField_MonsterCarnivalRevive::DecodeFieldSpecificData
            outPacket.encodeByte(0); // nTeamForMCarnival
        }
        return outPacket;
    }

    public static OutPacket userLeaveField(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_LEAVE_FIELD);
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterId
        return outPacket;
    }
}
