package kinoko.packet.field;

import kinoko.server.dialog.miniroom.*;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.GameConstants;
import kinoko.world.item.Item;
import kinoko.world.user.User;
import kinoko.world.user.data.MiniGameRecord;

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

    public static OutPacket inviteResult(MiniRoomInviteType inviteType, String targetName) {
        // CMiniRoomBaseDlg::OnInviteResultStatic
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_InviteResult);
        outPacket.encodeByte(inviteType.getValue());
        if (inviteType != MiniRoomInviteType.NoCharacter) {
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
        outPacket.encodeByte(miniRoom.getUserIndex(me)); // nMyPosition
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

    public static OutPacket gameMessage(MiniGameMessageType messageType, String characterName) {
        // CMiniRoomBaseDlg::MakeGameMessage
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Chat);
        outPacket.encodeByte(MiniRoomProtocol.MRP_GameMessage.getValue());
        outPacket.encodeByte(messageType.getValue()); // nMessageCode
        outPacket.encodeString(characterName); // sCharacterName
        return outPacket;
    }

    public static OutPacket leave(int userIndex, MiniRoomLeaveType leaveType) {
        // CMiniRoomBaseDlg::OnEnterBase
        final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MRP_Leave);
        outPacket.encodeByte(userIndex);
        // *::OnLeave
        outPacket.encodeByte(leaveType.getValue());
        return outPacket;
    }

    private static OutPacket of(MiniRoomProtocol miniRoomProtocol) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MiniRoom);
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
            encodeMiniGameRecord(outPacket, miniGameType, user);
            return outPacket;
        }

        public static OutPacket enterResult(MiniGameRoom miniGameRoom, User me) {
            final OutPacket outPacket = MiniRoomPacket.enterResult(miniGameRoom, me);
            miniGameRoom.getUsers().forEach((i, user) -> {
                outPacket.encodeByte(i);
                encodeMiniGameRecord(outPacket, miniGameRoom.getType(), user);
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

        public static OutPacket gameResult(MiniGameResultType resultType, MiniGameRoom miniGameRoom, int winnerIndex) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.MGRP_GameResult);
            outPacket.encodeByte(resultType.getValue());
            if (resultType != MiniGameResultType.DRAW) {
                outPacket.encodeByte(winnerIndex); // nWinnerIdx
            }
            encodeMiniGameRecord(outPacket, miniGameRoom.getType(), miniGameRoom.getUser(0)); // apMGR[0]
            encodeMiniGameRecord(outPacket, miniGameRoom.getType(), miniGameRoom.getUser(1)); // apMGR[1]
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

        private static void encodeMiniGameRecord(OutPacket outPacket, MiniRoomType miniRoomType, User user) {
            final MiniGameRecord miniGameRecord = user != null ? user.getMiniGameRecord() : new MiniGameRecord();
            miniGameRecord.encode(miniRoomType, outPacket);
        }
    }


    public static class PlayerShop {
        public static OutPacket enterResult(PersonalShop personalShop, User me) {
            final OutPacket outPacket = MiniRoomPacket.enterResult(personalShop, me);
            // CPersonalShopDlg::OnEnterResult
            outPacket.encodeString(personalShop.getTitle());
            outPacket.encodeByte(GameConstants.PLAYER_SHOP_SLOT_MAX); // nItemMaxCount
            // CPersonalShopDlg::OnRefresh
            outPacket.encodeByte(personalShop.getItems().size());
            for (PlayerShopItem item : personalShop.getItems()) {
                outPacket.encodeShort(item.getSetCount()); // nNumber
                outPacket.encodeShort(item.getSetSize()); // nSet
                outPacket.encodeInt(item.getPrice()); // nPrice
                item.getItem().encode(outPacket);
            }
            return outPacket;
        }

        public static OutPacket buyResult(PlayerShopBuyResult buyResult) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.PSP_BuyResult);
            outPacket.encodeByte(buyResult.getValue());
            return outPacket;
        }

        public static OutPacket refresh(List<PlayerShopItem> items) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.PSP_Refresh);
            outPacket.encodeByte(items.size()); // nItem
            for (PlayerShopItem item : items) {
                outPacket.encodeShort(item.getSetCount()); // nNumber
                outPacket.encodeShort(item.getSetSize()); // nSet
                outPacket.encodeInt(item.getPrice()); // nPrice
                item.getItem().encode(outPacket); // GW_ItemSlotBase::Decode
            }
            return outPacket;
        }

        public static OutPacket refreshEntrustedShop(int money, List<PlayerShopItem> items) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.PSP_Refresh);
            outPacket.encodeInt(money); // nMoney
            outPacket.encodeByte(items.size()); // nItem
            for (PlayerShopItem item : items) {
                outPacket.encodeShort(item.getSetSize()); // nNumber
                outPacket.encodeShort(item.getSetCount()); // nSet
                outPacket.encodeInt(item.getPrice()); // nPrice
                item.getItem().encode(outPacket); // GW_ItemSlotBase::Decode
            }
            return outPacket;
        }

        public static OutPacket addSoldItem(int itemIndex, int quantity, String buyerName) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.PSP_AddSoldItem);
            outPacket.encodeByte(itemIndex);
            outPacket.encodeShort(quantity);
            outPacket.encodeString(buyerName);
            return outPacket;
        }

        public static OutPacket moveItemToInventory(int newSize, int itemIndex) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.PSP_MoveItemToInventory);
            outPacket.encodeByte(newSize); // nItem
            outPacket.encodeShort(itemIndex);
            return outPacket;
        }

        public static OutPacket arrangeItem(int money) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.ESP_ArrangeItem);
            outPacket.encodeInt(money); // nMoney
            return outPacket;
        }

        public static OutPacket withdrawAllResult(PlayerShopWithdrawResult withdrawResult) {
            final OutPacket outPacket = MiniRoomPacket.of(MiniRoomProtocol.ESP_WithdrawAllResult);
            outPacket.encodeByte(withdrawResult.getValue());
            return outPacket;
        }

        public static OutPacket withdrawMoneyResult() {
            return MiniRoomPacket.of(MiniRoomProtocol.ESP_WithdrawMoneyResult);
        }

        // TODO : DeliverVisitList, DeliverBlackList
    }
}
