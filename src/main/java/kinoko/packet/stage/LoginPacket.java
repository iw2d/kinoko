package kinoko.packet.stage;

import kinoko.server.*;
import kinoko.world.character.AvatarLook;
import kinoko.world.character.CharacterStat;

public final class LoginPacket {
    public static OutPacket connect(Client c) {
        OutPacket outPacket = OutPacket.create();
        outPacket.encodeShort(0x0E);
        outPacket.encodeShort(ServerConstants.GAME_VERSION);
        outPacket.encodeString(ServerConstants.PATCH);
        outPacket.encodeArray(c.getRecvIv());
        outPacket.encodeArray(c.getSendIv());
        outPacket.encodeByte(ServerConstants.LOCALE);
        return outPacket;
    }

    public static OutPacket aliveReq() {
        return OutPacket.create(OutHeader.ALIVE_REQ);
    }

    public static OutPacket checkPasswordResult() {
        OutPacket outPacket = OutPacket.create(OutHeader.CHECK_PASSWORD_RESULT);
        outPacket.encodeByte(0); // Success
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);

        outPacket.encodeInt(1); // dwAccountId
        outPacket.encodeByte(0); // nGender
        outPacket.encodeByte(0); // nGradeCode
        outPacket.encodeShort(0); // nSubGradeCode | bTesterAccount
        outPacket.encodeByte(0); // nCountryID
        outPacket.encodeString(""); // sNexonClubID
        outPacket.encodeByte(0); // nPurchaseExp
        outPacket.encodeByte(0); // nChatBlockReason
        outPacket.encodeLong(0); // dtChatUnblockDate
        outPacket.encodeLong(0); // dtRegisterDate
        outPacket.encodeInt(1); // nNumOfCharacter

        outPacket.encodeByte(true); // true ? VIEW_WORLD_SELECT : CHECK_PIN_CODE
        outPacket.encodeByte(false); // bLoginOpt
        outPacket.encodeLong(0);
        return outPacket;
    }

    public static OutPacket worldInformation() {
        OutPacket outPacket = OutPacket.create(OutHeader.WORLD_INFORMATION);
        outPacket.encodeByte(ServerConfig.WORLD_ID); // nWorldID
        outPacket.encodeString(ServerConfig.WORLD_NAME);
        outPacket.encodeByte(0); // nWorldState
        outPacket.encodeString(""); // sWorldEventDesc
        outPacket.encodeShort(0); // nWorldEventEXP_WSE
        outPacket.encodeShort(0); // nWorldEventDrop_WSE
        outPacket.encodeByte(0); // nBlockCharCreation

        outPacket.encodeByte(1); // channels.size()
        outPacket.encodeString("Channel 1"); // sName
        outPacket.encodeInt(0); // nUserNo
        outPacket.encodeByte(0); // nWorldID
        outPacket.encodeByte(0); // nChannelID
        outPacket.encodeByte(false); // bAdultChannel

        outPacket.encodeShort(0); // nBalloonCount
        return outPacket;
    }

    public static OutPacket worldInformationEnd() {
        OutPacket outPacket = OutPacket.create(OutHeader.WORLD_INFORMATION);
        outPacket.encodeByte(-1); // nWorldID
        return outPacket;
    }

    public static OutPacket checkUserLimitResult() {
        OutPacket outPacket = OutPacket.create(OutHeader.CHECK_USER_LIMIT_RESULT);
        outPacket.encodeByte(false); // bOverUserLimit
        outPacket.encodeByte(0); // bPopulateLevel (0 = Normal, 1 = Populated, 2 = Full)
        return outPacket;
    }

    public static OutPacket selectWorldResult() {
        OutPacket outPacket = OutPacket.create(OutHeader.SELECT_WORLD_RESULT);
        outPacket.encodeByte(0); // Success

        outPacket.encodeByte(0); // characters.size();

        outPacket.encodeByte(2); // bLoginOpt
        outPacket.encodeInt(1); // nSlotCount
        outPacket.encodeInt(0); // nBuyCharCount
        return outPacket;
    }

    public static OutPacket checkDuplicatedIdResult(String name) {
        OutPacket outPacket = OutPacket.create(OutHeader.CHECK_DUPLICATED_ID_RESULT);
        outPacket.encodeString(name);
        outPacket.encodeByte(0); // Success
        return outPacket;
    }

    public static OutPacket createNewCharacterResult() {
        OutPacket outPacket = OutPacket.create(OutHeader.CREATE_NEW_CHARACTER_RESULT);
        outPacket.encodeByte(0); // Success
        new CharacterStat().encode(outPacket);
        new AvatarLook().encode(outPacket);
        return outPacket;
    }
}
