package kinoko.server.dialog.miniroom;

public enum MiniRoomProtocol {
    // MRP - MiniRoom
    MRP_Create(0),
    MRP_CreateResult(1),
    MRP_Invite(2),
    MRP_InviteResult(3),
    MRP_Enter(4),
    MRP_EnterResult(5),
    MRP_Chat(6),
    MRP_GameMessage(7),
    MRP_UserChat(8),
    MRP_Avatar(9),
    MRP_Leave(10),
    MRP_Balloon(11),
    MRP_NotAvailableField(12),
    MRP_FreeMarketClip(13),
    MRP_CheckSSN2(14),
    // TRP - Trading Room
    TRP_PutItem(15),
    TRP_PutMoney(16),
    TRP_Trade(17),
    TRP_UnTrade(18),
    TRP_MoveItemToInventory(19),
    TRP_ItemCRC(20),
    TRP_LimitFail(21),
    // PSP - Personal Shop
    PSP_PutItem(22),
    PSP_BuyItem(23),
    PSP_BuyResult(24),
    PSP_Refresh(25),
    PSP_AddSoldItem(26),
    PSP_MoveItemToInventory(27),
    PSP_Ban(28),
    PSP_KickedTimeOver(29),
    PSP_DeliverBlackList(30),
    PSP_AddBlackList(31),
    PSP_DeleteBlackList(32),
    // ESP - Entrusted Shop
    ESP_PutItem(33),
    ESP_BuyItem(34),
    ESP_BuyResult(35),
    ESP_Refresh(36),
    ESP_AddSoldItem(37),
    ESP_MoveItemToInventory(38),
    ESP_GoOut(39),
    ESP_ArrangeItem(40),
    ESP_WithdrawAll(41),
    ESP_WithdrawAllResult(42),
    ESP_WithdrawMoney(43),
    ESP_WithdrawMoneyResult(44),
    ESP_AdminChangeTitle(45),
    ESP_DeliverVisitList(46),
    ESP_DeliverBlackList(47),
    ESP_AddBlackList(48),
    ESP_DeleteBlackList(49),
    // MGRP - MiniGame Room
    MGRP_TieRequest(50),
    MGRP_TieResult(51),
    MGRP_GiveUpRequest(52),
    MGRP_GiveUpResult(53),
    MGRP_RetreatRequest(54),
    MGRP_RetreatResult(55),
    MGRP_LeaveEngage(56),
    MGRP_LeaveEngageCancel(57),
    MGRP_Ready(58),
    MGRP_CancelReady(59),
    MGRP_Ban(60),
    MGRP_Start(61),
    MGRP_GameResult(62),
    MGRP_TimeOver(63),
    // ORP - Omok Room
    ORP_PutStoneChecker(64),
    ORP_InvalidStonePosition(65),
    ORP_InvalidStonePosition_Normal(66),
    ORP_InvalidStonePosition_By33(67),
    // MGP - Memory Game
    MGP_TurnUpCard(68),
    MGP_MatchCard(69);


    private final int value;

    MiniRoomProtocol(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MiniRoomProtocol getByValue(int value) {
        for (MiniRoomProtocol type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
