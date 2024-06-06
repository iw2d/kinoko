package kinoko.packet.field;

import kinoko.server.dialog.miniroom.*;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.item.Item;
import kinoko.world.user.MiniGameRecord;
import kinoko.world.user.User;

import java.util.List;

public final class MiniRoomPacket {
    // CMiniRoomBaseDlg::OnPacketBase ----------------------------------------------------------------------------------

    public static OutPacket inviteStatic(MiniRoomType miniRoomType, String inviterName, int miniRoomId) {
        // CMiniRoomBaseDlg::OnInviteStatic
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Invite);
        outPacket.encodeByte(miniRoomType.getValue());
        outPacket.encodeString(inviterName); // sInviter
        outPacket.encodeInt(miniRoomId); // dwSN
        return outPacket;
    }

    public static OutPacket inviteResult(InviteType inviteType, String targetName) {
        // CMiniRoomBaseDlg::OnInviteResultStatic
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_InviteResult);
        outPacket.encodeByte(inviteType.getValue());
        if (inviteType != InviteType.NO_CHARACTER) {
            outPacket.encodeString(targetName); // sTargetName
        }
        return outPacket;
    }

    public static OutPacket enterBase(int userIndex, User user) {
        // CMiniRoomBaseDlg::OnEnterBase
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Enter);
        outPacket.encodeByte(userIndex);
        user.getCharacterData().getAvatarLook().encode(outPacket); // CMiniRoomBaseDlg::DecodeAvatar
        outPacket.encodeString(user.getCharacterName()); // asUserID
        outPacket.encodeShort(user.getJob()); // anJobCode
        return outPacket;
    }

    public static OutPacket enterResult(MiniRoom miniRoom, User me) {
        // CMiniRoomBaseDlg::OnEnterResultStatic
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_EnterResult);
        outPacket.encodeByte(miniRoom.getType().getValue()); // nMiniRoomType
        // CMiniRoomBaseDlg::OnEnterResultBase
        outPacket.encodeByte(miniRoom.getMaxUsers()); // nMaxUsers
        outPacket.encodeByte(miniRoom.getPosition(me)); // nMyPosition
        miniRoom.getUsers().forEach((i, user) -> {
            outPacket.encodeByte(i);
            user.getCharacterData().getAvatarLook().encode(outPacket); // CMiniRoomBaseDlg::DecodeAvatar
            outPacket.encodeString(user.getCharacterName()); // asUserID
            outPacket.encodeShort(user.getJob()); // anJobCode
        });
        outPacket.encodeByte(-1);
        return outPacket;
    }

    public static OutPacket enterResult(EnterResultType resultType) {
        // CMiniRoomBaseDlg::OnEnterResultStatic
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_EnterResult);
        outPacket.encodeByte(0); // nMiniRoomType
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }

    public static OutPacket chat(int userIndex, String text) {
        // CMiniRoomBaseDlg::OnChat
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Chat);
        outPacket.encodeByte(MiniRoomProtocol.MRP_UserChat.getValue());
        outPacket.encodeByte(userIndex);
        outPacket.encodeString(text); // sText
        return outPacket;
    }

    public static OutPacket chat(int userIndex, String characterName, String message) {
        return chat(userIndex, String.format("%s : %s", characterName, message));
    }

    public static OutPacket gameMessage(GameMessageType messageType, String characterName) {
        // CMiniRoomBaseDlg::MakeGameMessage
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Chat);
        outPacket.encodeByte(MiniRoomProtocol.MRP_GameMessage.getValue());
        outPacket.encodeByte(messageType.getValue()); // nMessageCode
        outPacket.encodeString(characterName); // sCharacterName
        return outPacket;
    }

    public static OutPacket leave(int userIndex, LeaveType leaveType) {
        // CMiniRoomBaseDlg::OnEnterBase
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Leave);
        outPacket.encodeByte(userIndex);
        // *::OnLeave
        outPacket.encodeByte(leaveType.getValue());
        return outPacket;
    }

    private static OutPacket of(MiniRoomProtocol miniRoomProtocol) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MINIROOM);
        outPacket.encodeByte(miniRoomProtocol.getValue());
        return outPacket;
    }


    public static class TradingRoom {

        public static OutPacket putItem(int userIndex, int index, Item item) {
            // CTradingRoomDlg::OnPutItem
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.TRP_PutItem);
            outPacket.encodeByte(userIndex);
            outPacket.encodeByte(index);
            item.encode(outPacket); // GW_ItemSlotBase::Decode
            return outPacket;
        }

        public static OutPacket putMoney(int userIndex, int newMoney) {
            // CTradingRoomDlg::OnPutMoney
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.TRP_PutMoney);
            outPacket.encodeByte(userIndex);
            outPacket.encodeInt(newMoney); // anMoney
            return outPacket;
        }

        public static OutPacket trade() {
            // CTradingRoomDlg::OnTrade
            return MiniRoomPacket.of(MiniRoomProtocol.TRP_Trade);
        }

    }


    public static class MiniGame {

        public static OutPacket enter(int userIndex, User user, MiniRoomType miniGameType) {
            final OutPacket outPacket = MiniRoomPacket.enterBase(userIndex, user);
            new MiniGameRecord(miniGameType).encode(outPacket); // TODO persist in DB
            return outPacket;
        }

        public static OutPacket enterResult(MiniGameRoom miniGameRoom, User me) {
            final OutPacket outPacket = MiniRoomPacket.enterResult(miniGameRoom, me);
            miniGameRoom.getUsers().forEach((i, user) -> {
                outPacket.encodeByte(i);
                new MiniGameRecord(miniGameRoom.getType()).encode(outPacket);
            });
            outPacket.encodeByte(-1);
            outPacket.encodeString(miniGameRoom.getTitle()); // sTitle
            outPacket.encodeByte(miniGameRoom.getGameSpec()); // nGameKind
            outPacket.encodeByte(false); // bTournament
            return outPacket;
        }

        public static OutPacket tieRequest() {
            return MiniRoomPacket.of(MiniRoomProtocol.MGRP_TieRequest);
        }

        public static OutPacket tieResult() {
            return MiniRoomPacket.of(MiniRoomProtocol.MGRP_TieResult);
        }

        public static OutPacket retreatRequest() {
            // COmokDlg::OnRetreatRequest
            return MiniRoomPacket.of(MiniRoomProtocol.MGRP_RetreatRequest);
        }

        public static OutPacket retreatResult(boolean accepted, int count, int nextTurn) {
            // COmokDlg::OnRetreatResult
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGRP_RetreatResult);
            outPacket.encodeByte(accepted);
            if (accepted) {
                outPacket.encodeByte(count);
                outPacket.encodeByte(nextTurn); // nCurTurnIdx
            }
            return outPacket;
        }

        public static OutPacket ready(boolean ready) {
            return MiniRoomPacket.of(ready ? MiniRoomProtocol.MGRP_Ready : MiniRoomProtocol.MGRP_CancelReady);
        }

        public static OutPacket omokStart(int nextTurn) {
            // COmokDlg::OnUserStart
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGRP_Start);
            outPacket.encodeByte(nextTurn);
            return outPacket;
        }

        public static OutPacket memoryGameStart(int nextTurn, List<Integer> shuffle) {
            // CMemoryGameDlg::OnUserStart
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGRP_Start);
            outPacket.encodeByte(nextTurn);
            outPacket.encodeByte(shuffle.size());
            for (int card : shuffle) {
                outPacket.encodeInt(card);
            }
            return outPacket;
        }

        public static OutPacket gameResult(GameResultType resultType, int winnerIndex, MiniGameRecord record0, MiniGameRecord record1) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGRP_GameResult);
            outPacket.encodeByte(resultType.getValue());
            if (resultType != GameResultType.DRAW) {
                outPacket.encodeByte(winnerIndex); // nWinnerIdx
            }
            record0.encode(outPacket); // apMGR[0]
            record1.encode(outPacket); // apMGR[1]
            return outPacket;
        }

        public static OutPacket timeOver(int nextTurn) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGRP_TimeOver);
            outPacket.encodeByte(nextTurn);
            return outPacket;
        }

        public static OutPacket putStoneChecker(int x, int y, int type) {
            // COmokDlg::OnPutStoneChecker
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.ORP_PutStoneChecker);
            outPacket.encodeInt(x);
            outPacket.encodeInt(y);
            outPacket.encodeByte(type);
            return outPacket;
        }

        public static OutPacket invalidStonePosition(MiniRoomProtocol errorType) {
            // COmokDlg::OnPutStoneChecker
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.ORP_InvalidStonePosition);
            outPacket.encodeByte(errorType.getValue()); // errorType == ORP_InvalidStonePosition_By33 ? "You have double-3s." : "You can't put it there."
            return outPacket;
        }

        public static OutPacket turnUpCard(int cardIndex) {
            // CMemoryGameDlg::OnTurnUpCard
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGP_TurnUpCard);
            outPacket.encodeByte(true);
            outPacket.encodeByte(cardIndex);
            return outPacket;
        }

        public static OutPacket turnUpCard(int firstCard, int cardIndex, int userIndex, boolean isMatch) {
            // CMemoryGameDlg::OnTurnUpCard
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGP_TurnUpCard);
            outPacket.encodeByte(false);
            outPacket.encodeByte(cardIndex);
            outPacket.encodeByte(firstCard);
            outPacket.encodeByte(userIndex + (isMatch ? 2 : 0));
            return outPacket;
        }
    }
}
