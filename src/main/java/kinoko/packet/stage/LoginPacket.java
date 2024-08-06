package kinoko.packet.stage;

import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.header.OutHeader;
import kinoko.server.node.ChannelInfo;
import kinoko.server.packet.OutPacket;
import kinoko.server.rank.CharacterRank;
import kinoko.server.rank.RankManager;
import kinoko.world.user.Account;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class LoginPacket {
    // CLogin::OnPacket ------------------------------------------------------------------------------------------------

    public static OutPacket connect(byte[] sendIv, byte[] recvIv) {
        final OutPacket outPacket = OutPacket.of();
        outPacket.encodeShort(ServerConstants.GAME_VERSION);
        outPacket.encodeString(ServerConstants.PATCH);
        outPacket.encodeArray(sendIv); // sendIv for client (recvIv for server)
        outPacket.encodeArray(recvIv); // recvIv for client (sendIv for server)
        outPacket.encodeByte(ServerConstants.LOCALE);
        return outPacket;
    }

    public static OutPacket checkPasswordResultSuccess(Account account, byte[] clientKey) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CheckPasswordResult);
        outPacket.encodeByte(LoginResultType.Success.getValue());
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
        outPacket.encodeArray(clientKey);
        return outPacket;
    }

    public static OutPacket checkPasswordResultFail(LoginResultType failType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CheckPasswordResult);
        outPacket.encodeByte(failType.getValue());
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        return outPacket;
    }

    public static OutPacket checkPasswordResultBlocked(int blockedType, Instant unblockDate) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CheckPasswordResult);
        outPacket.encodeByte(LoginResultType.Blocked.getValue());
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        outPacket.encodeByte(blockedType);
        outPacket.encodeFT(unblockDate);
        return outPacket;
    }

    public static OutPacket viewAllCharResult() {
        final OutPacket outPacket = OutPacket.of(OutHeader.ViewAllCharResult);
        outPacket.encodeByte(6);
        outPacket.encodeByte(true);
        outPacket.encodeString("This feature is disabled");
        return outPacket;
    }

    public static OutPacket worldInformation(List<ChannelInfo> channels) {
        final OutPacket outPacket = OutPacket.of(OutHeader.WorldInformation);
        outPacket.encodeByte(ServerConfig.WORLD_ID); // nWorldID
        outPacket.encodeString(ServerConfig.WORLD_NAME);
        outPacket.encodeByte(0); // nWorldState
        outPacket.encodeString(""); // sWorldEventDesc
        outPacket.encodeShort(100); // nWorldEventEXP_WSE
        outPacket.encodeShort(100); // nWorldEventDrop_WSE
        outPacket.encodeByte(0); // nBlockCharCreation

        outPacket.encodeByte(channels.size());
        for (ChannelInfo channelInfo : channels) {
            outPacket.encodeString(channelInfo.getName()); // sName
            outPacket.encodeInt(channelInfo.getUserCount()); // nUserNo
            outPacket.encodeByte(ServerConfig.WORLD_ID); // nWorldID
            outPacket.encodeByte(channelInfo.getId()); // nChannelID
            outPacket.encodeByte(false); // bAdultChannel
        }

        outPacket.encodeShort(0); // nBalloonCount
        return outPacket;
    }

    public static OutPacket worldInformationEnd() {
        final OutPacket outPacket = OutPacket.of(OutHeader.WorldInformation);
        outPacket.encodeByte(-1); // nWorldID
        return outPacket;
    }

    public static OutPacket checkUserLimitResult() {
        final OutPacket outPacket = OutPacket.of(OutHeader.CheckUserLimitResult);
        outPacket.encodeByte(false); // bOverUserLimit
        outPacket.encodeByte(0); // bPopulateLevel (0 = Normal, 1 = Populated, 2 = Full)
        return outPacket;
    }

    public static OutPacket selectWorldResultSuccess(Account account) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SelectWorldResult);
        outPacket.encodeByte(LoginResultType.Success.getValue());

        outPacket.encodeByte(account.getCharacterList().size());
        for (AvatarData avatarData : account.getCharacterList()) {
            avatarData.encode(outPacket);
            outPacket.encodeByte(false); // m_abOnFamily
            final Optional<CharacterRank> characterRankResult = RankManager.getCharacterRank(avatarData.getCharacterId());
            if (characterRankResult.isPresent()) {
                outPacket.encodeByte(true);
                characterRankResult.get().encode(outPacket); // CLogin::RANK
            } else {
                outPacket.encodeByte(false);
            }
        }

        outPacket.encodeByte(LoginOpt.getLoginOpt(account).getValue()); // bLoginOpt
        outPacket.encodeInt(account.getSlotCount()); // nSlotCount
        outPacket.encodeInt(0); // nBuyCharCount
        return outPacket;
    }

    public static OutPacket selectWorldResultFail(LoginResultType failType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SelectWorldResult);
        outPacket.encodeByte(failType.getValue());
        return outPacket;
    }

    public static OutPacket selectCharacterResultSuccess(byte[] channelHost, int channelPort, int characterId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SelectCharacterResult);
        outPacket.encodeByte(LoginResultType.Success.getValue());
        outPacket.encodeByte(0);

        outPacket.encodeArray(channelHost); // sin_addr
        outPacket.encodeShort(channelPort); // uPort
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(0); // bAuthenCode
        outPacket.encodeInt(0); // ulPremiumArgument
        return outPacket;
    }

    public static OutPacket selectCharacterResultFail(LoginResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SelectCharacterResult);
        outPacket.encodeByte(resultType.getValue());
        outPacket.encodeByte(0); // Trouble logging in?
        return outPacket;
    }

    public static OutPacket checkDuplicatedIdResult(String name, int idResultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CheckDuplicatedIDResult);
        outPacket.encodeString(name);
        outPacket.encodeByte(idResultType);
        // 0: Success
        // 1: This name is currently being used.
        // 2: You cannot use this name.
        // default: Failed due to unknown reason
        return outPacket;
    }

    public static OutPacket createNewCharacterResultSuccess(CharacterData characterData) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CreateNewCharacterResult);
        outPacket.encodeByte(LoginResultType.Success.getValue());
        AvatarData.from(characterData).encode(outPacket);
        return outPacket;
    }

    public static OutPacket createNewCharacterResultFail(LoginResultType failType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CreateNewCharacterResult);
        outPacket.encodeByte(failType.getValue());
        return outPacket;
    }

    public static OutPacket deleteCharacterResult(LoginResultType resultType, int characterId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DeleteCharacterResult);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }

    public static OutPacket latestConnectedWorld(int worldId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.LatestConnectedWorld);
        outPacket.encodeInt(worldId);
        return outPacket;
    }

    public static OutPacket checkSecondaryPasswordResult() {
        final OutPacket outPacket = OutPacket.of(OutHeader.CheckSPWResult);
        outPacket.encodeByte(-1); // ignored
        return outPacket;
    }

    private enum LoginOpt {
        INITIALIZE_SECONDARY_PASSWORD(0),
        CHECK_SECONDARY_PASSWORD(1),
        NO_SECONDARY_PASSWORD(2);

        private final int value;

        LoginOpt(int value) {
            this.value = value;
        }

        public final int getValue() {
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
