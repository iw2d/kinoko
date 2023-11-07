package kinoko.packet.stage;

import kinoko.server.Client;
import kinoko.server.ServerConstants;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.Account;
import kinoko.world.Channel;
import kinoko.world.World;
import kinoko.world.user.CharacterData;

import java.util.List;

public final class LoginPacket {
    public static OutPacket connect(Client c) {
        OutPacket outPacket = OutPacket.of();
        outPacket.encodeShort(0x0E);
        outPacket.encodeShort(ServerConstants.GAME_VERSION);
        outPacket.encodeString(ServerConstants.PATCH);
        outPacket.encodeArray(c.getRecvIv());
        outPacket.encodeArray(c.getSendIv());
        outPacket.encodeByte(ServerConstants.LOCALE);
        return outPacket;
    }

    public static OutPacket aliveReq() {
        return OutPacket.of(OutHeader.ALIVE_REQ);
    }

    public static OutPacket checkPasswordResultSuccess(Account account) {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_PASSWORD_RESULT);
        outPacket.encodeByte(0); // Success
        outPacket.encodeByte(0); // 0 or 1
        outPacket.encodeInt(0);

        outPacket.encodeInt(account.getId()); // dwAccountId
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

    public static OutPacket checkPasswordResultFail(int failureType) {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_PASSWORD_RESULT);
        outPacket.encodeByte(failureType);
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        return outPacket;
    }

    public static OutPacket worldInformation(World world) {
        OutPacket outPacket = OutPacket.of(OutHeader.WORLD_INFORMATION);
        outPacket.encodeByte(world.getId()); // nWorldID
        outPacket.encodeString(world.getName());
        outPacket.encodeByte(0); // nWorldState
        outPacket.encodeString(""); // sWorldEventDesc
        outPacket.encodeShort(100); // nWorldEventEXP_WSE
        outPacket.encodeShort(100); // nWorldEventDrop_WSE
        outPacket.encodeByte(0); // nBlockCharCreation

        final List<Channel> channels = world.getChannels();
        outPacket.encodeByte(channels.size());
        for (Channel channel : channels) {
            outPacket.encodeString(channel.getChannelName()); // sName
            outPacket.encodeInt(channel.getUserNo()); // nUserNo
            outPacket.encodeByte(channel.getWorldId()); // nWorldID
            outPacket.encodeByte(channel.getChannelId()); // nChannelID
            outPacket.encodeByte(false); // bAdultChannel
        }

        outPacket.encodeShort(0); // nBalloonCount
        return outPacket;
    }

    public static OutPacket worldInformationEnd() {
        OutPacket outPacket = OutPacket.of(OutHeader.WORLD_INFORMATION);
        outPacket.encodeByte(-1); // nWorldID
        return outPacket;
    }

    public static OutPacket checkUserLimitResult() {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_USER_LIMIT_RESULT);
        outPacket.encodeByte(false); // bOverUserLimit
        outPacket.encodeByte(0); // bPopulateLevel (0 = Normal, 1 = Populated, 2 = Full)
        return outPacket;
    }

    public static OutPacket selectWorldResult() {
        OutPacket outPacket = OutPacket.of(OutHeader.SELECT_WORLD_RESULT);
        outPacket.encodeByte(0); // Success

        outPacket.encodeByte(0); // characters.size();

        outPacket.encodeByte(2); // bLoginOpt
        outPacket.encodeInt(1); // nSlotCount
        outPacket.encodeInt(0); // nBuyCharCount
        return outPacket;
    }

    public static OutPacket checkDuplicatedIdResult(String name) {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_DUPLICATED_ID_RESULT);
        outPacket.encodeString(name);
        outPacket.encodeByte(0); // Success
        return outPacket;
    }

    public static OutPacket createNewCharacterResult(CharacterData cd) {
        OutPacket outPacket = OutPacket.of(OutHeader.CREATE_NEW_CHARACTER_RESULT);
        outPacket.encodeByte(0); // Success
        cd.getCharacterStat().encode(outPacket);
        cd.getAvatarLook().encode(outPacket);
        return outPacket;
    }
}
