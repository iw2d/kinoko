package kinoko.packet.stage;

import kinoko.server.Client;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.FileTime;
import kinoko.world.Account;
import kinoko.world.ChannelServer;
import kinoko.world.World;
import kinoko.world.user.AvatarData;
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
        outPacket.encodeByte(LoginResult.SUCCESS.getValue());
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
        outPacket.encodeInt(account.getSlotCount()); // nNumOfCharacter

        outPacket.encodeByte(true); // true ? VIEW_WORLD_SELECT : CHECK_PIN_CODE
        outPacket.encodeByte(LoginOpt.getLoginOpt(account).getValue()); // bLoginOpt
        outPacket.encodeLong(0);
        return outPacket;
    }

    public static OutPacket checkPasswordResultFail(LoginResult failType) {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_PASSWORD_RESULT);
        outPacket.encodeByte(failType.getValue());
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        return outPacket;
    }

    public static OutPacket checkPasswordResultBlocked(int blockedType, FileTime unblockDate) {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_PASSWORD_RESULT);
        outPacket.encodeByte(LoginResult.BLOCKED.getValue());
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        outPacket.encodeByte(blockedType);
        outPacket.encodeFT(unblockDate);
        return outPacket;
    }

    public static OutPacket viewAllCharResult() {
        OutPacket outPacket = OutPacket.of(OutHeader.VIEW_ALL_CHAR_RESULT);
        outPacket.encodeByte(6);
        outPacket.encodeByte(true);
        outPacket.encodeString("This feature is disabled");
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

        final List<ChannelServer> channelServers = world.getChannels();
        outPacket.encodeByte(channelServers.size());
        for (ChannelServer channelServer : channelServers) {
            outPacket.encodeString(channelServer.getName()); // sName
            outPacket.encodeInt(channelServer.getUserNo()); // nUserNo
            outPacket.encodeByte(channelServer.getWorldId()); // nWorldID
            outPacket.encodeByte(channelServer.getChannelId()); // nChannelID
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

    public static OutPacket selectWorldResultSuccess(Account account) {
        OutPacket outPacket = OutPacket.of(OutHeader.SELECT_WORLD_RESULT);
        outPacket.encodeByte(LoginResult.SUCCESS.getValue());

        outPacket.encodeByte(account.getCharacterList().size());
        for (AvatarData avatarData : account.getCharacterList()) {
            avatarData.encode(outPacket);
            outPacket.encodeByte(false); // m_abOnFamily
            outPacket.encodeByte(false); // bool -> CLogin::RANK
        }

        outPacket.encodeByte(LoginOpt.getLoginOpt(account).getValue()); // bLoginOpt
        outPacket.encodeInt(account.getSlotCount()); // nSlotCount
        outPacket.encodeInt(0); // nBuyCharCount
        return outPacket;
    }

    public static OutPacket selectWorldResultFail(LoginResult failType) {
        OutPacket outPacket = OutPacket.of(OutHeader.SELECT_WORLD_RESULT);
        outPacket.encodeByte(failType.getValue());
        return outPacket;
    }

    public static OutPacket selectCharacterResultSuccess(byte[] channelAddress, int port, int characterId) {
        OutPacket outPacket = OutPacket.of(OutHeader.SELECT_CHARACTER_RESULT);
        outPacket.encodeByte(LoginResult.SUCCESS.getValue());
        outPacket.encodeByte(0);

        outPacket.encodeArray(channelAddress); // sin_addr
        outPacket.encodeShort(port); // uPort
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(0); // bAuthenCode
        outPacket.encodeInt(0); // ulPremiumArgument
        return outPacket;
    }

    public static OutPacket selectCharacterResultFail(LoginResult resultType, int errorType) {
        OutPacket outPacket = OutPacket.of(OutHeader.SELECT_CHARACTER_RESULT);
        outPacket.encodeByte(resultType.getValue());
        outPacket.encodeByte(errorType);
        return outPacket;
    }

    public static OutPacket checkDuplicatedIdResult(String name, int idResultType) {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_DUPLICATED_ID_RESULT);
        outPacket.encodeString(name);
        outPacket.encodeByte(idResultType);
        // 0: Success
        // 1: This name is currently being used.
        // 2: You cannot use this name.
        // default: Failed due to unknown reason
        return outPacket;
    }

    public static OutPacket createNewCharacterResultSuccess(CharacterData characterData) {
        OutPacket outPacket = OutPacket.of(OutHeader.CREATE_NEW_CHARACTER_RESULT);
        outPacket.encodeByte(LoginResult.SUCCESS.getValue());
        AvatarData.from(characterData).encode(outPacket);
        return outPacket;
    }

    public static OutPacket createNewCharacterResultFail(LoginResult failType) {
        OutPacket outPacket = OutPacket.of(OutHeader.CREATE_NEW_CHARACTER_RESULT);
        outPacket.encodeByte(failType.getValue());
        return outPacket;
    }

    public static OutPacket deleteCharacterResult(LoginResult resultType, int characterId) {
        OutPacket outPacket = OutPacket.of(OutHeader.DELETE_CHARACTER_RESULT);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }

    public static OutPacket latestConnectedWorld(int worldId) {
        OutPacket outPacket = OutPacket.of(OutHeader.LATEST_CONNECTED_WORLD);
        outPacket.encodeInt(worldId);
        return outPacket;
    }

    public static OutPacket checkSecondaryPasswordResult() {
        OutPacket outPacket = OutPacket.of(OutHeader.CHECK_SPW_RESULT);
        outPacket.encodeByte(-1); // ignored
        return outPacket;
    }

    public enum LoginOpt {
        INITIALIZE_SECONDARY_PASSWORD(0),
        CHECK_SECONDARY_PASSWORD(1),
        NO_SECONDARY_PASSWORD(2);

        private final int value;

        LoginOpt(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final LoginOpt getLoginOpt(Account account) {
            if (!ServerConfig.REQUIRE_SECONDARY_PASSWORD) {
                return NO_SECONDARY_PASSWORD;
            }

            if (account.hasSecondaryPassword()) {
                return CHECK_SECONDARY_PASSWORD;
            } else {
                return INITIALIZE_SECONDARY_PASSWORD;
            }
        }
    }
}
