package kinoko.server.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum OutHeader {
    // LP
    // CLogin::OnPacket
    CheckPasswordResult(0),
    GuestIDLoginResult(1),
    AccountInfoResult(2),
    CheckUserLimitResult(3),
    SetAccountResult(4),
    ConfirmEULAResult(5),
    CheckPinCodeResult(6),
    UpdatePinCodeResult(7),
    ViewAllCharResult(8),
    SelectCharacterByVACResult(9),
    WorldInformation(10),
    SelectWorldResult(11),
    SelectCharacterResult(12),
    CheckDuplicatedIDResult(13),
    CreateNewCharacterResult(14),
    DeleteCharacterResult(15),
    EnableSPWResult(21),
    DeleteCharacterOTPRequest(22),
    LatestConnectedWorld(24),
    RecommendWorldMessage(25),
    CheckExtraCharInfoResult(26),
    CheckSPWResult(27),

    // CClientSocket::ProcessPacket
    MigrateCommand(16),
    AliveReq(17),
    AuthenCodeChanged(18),
    AuthenMessage(19),
    SecurityPacket(20),
    CheckCrcResult(23),

    // CWvsContext::OnPacket
    InventoryOperation(28),
    InventoryGrow(29),
    StatChanged(30),
    TemporaryStatSet(31),
    TemporaryStatReset(32),
    ForcedStatSet(33),
    ForcedStatReset(34),
    ChangeSkillRecordResult(35),
    SkillUseResult(36),
    GivePopularityResult(37),
    Message(38),
    SendOpenFullClientLink(39),
    MemoResult(40),
    MapTransferResult(41),
    AntiMacroResult(42),
    InitialQuizStart(43),
    ClaimResult(44),
    SetClaimSvrAvailableTime(45),
    ClaimSvrStatusChanged(46),
    SetTamingMobInfo(47),
    QuestClear(48),
    EntrustedShopCheckResult(49),
    SkillLearnItemResult(50),
    SkillResetItemResult(51),
    GatherItemResult(52),
    SortItemResult(53),
    SueCharacterResult(55),
    MigrateToCashShopResult(56),
    TradeMoneyLimit(57),
    SetGender(58),
    GuildBBS(59),
    PetDeadMessage(60),
    CharacterInfo(61),
    PartyResult(62),
    ExpeditionNoti(64),
    FriendResult(65),
    GuildRequest(66),
    GuildResult(67),
    AllianceResult(68),
    TownPortal(69),
    OpenGate(70),
    BroadcastMsg(71),
    IncubatorResult(72),
    ShopScannerResult(73),
    ShopLinkResult(74),
    MarriageRequest(75),
    MarriageResult(76),
    WeddingGiftResult(77),
    MarriedPartnerMapTransfer(78),
    CashPetFoodResult(79),
    SetWeekEventMessage(80),
    SetPotionDiscountRate(81),
    BridleMobCatchFail(82),
    ImitatedNPCResult(83),
    ImitatedNPCData(84),
    LimitedNPCDisableInfo(85),
    MonsterBookSetCard(86),
    MonsterBookSetCover(87),
    HourChanged(88),
    MiniMapOnOff(89),
    ConsultAuthkeyUpdate(90),
    ClassCompetitionAuthkeyUpdate(91),
    WebBoardAuthkeyUpdate(92),
    SessionValue(93),
    PartyValue(94),
    FieldSetVariable(95),
    BonusExpRateChanged(96),
    PotionDiscountRateChanged(97),
    FamilyChartResult(98),
    FamilyInfoResult(99),
    FamilyResult(100),
    FamilyJoinRequest(101),
    FamilyJoinRequestResult(102),
    FamilyJoinAccepted(103),
    FamilyPrivilegeList(104),
    FamilyFamousPointIncResult(105),
    FamilyNotifyLoginOrLogout(106),
    FamilySetPrivilege(107),
    FamilySummonRequest(108),
    NotifyLevelUp(109),
    NotifyWedding(110),
    NotifyJobChange(111),
    IncRateChanged(112),
    MapleTVUseRes(113),
    AvatarMegaphoneRes(114),
    AvatarMegaphoneUpdateMessage(115),
    AvatarMegaphoneClearMessage(116),
    CancelNameChangeResult(117),
    CancelTransferWorldResult(118),
    DestroyShopResult(119),
    FAKEGMNOTICE(120),
    SuccessInUseGachaponBox(121),
    NewYearCardRes(122),
    RandomMorphRes(123),
    CancelNameChangeByOther(124),
    SetBuyEquipExt(125),
    SetPassenserRequest(126),
    ScriptProgressMessage(127),
    DataCRCCheckFailed(128),
    CakePieEventResult(129),
    UpdateGMBoard(130),
    ShowSlotMessage(131),
    WildHunterInfo(132),
    AccountMoreInfo(133),
    FindFirend(134),
    StageChange(135),
    DragonBallBox(136),
    AskUserWhetherUsePamsSong(137),
    TransferChannel(138),
    DisallowedDeliveryQuestList(139),
    MacroSysDataInit(140),

    // CStage::OnPacket
    SetField(141),
    SetITC(142),
    SetCashShop(143),

    // CMapLoadable::OnPacket
    SetBackgroundEffect(144),
    SetMapObjectVisible(145),
    ClearBackgroundEffect(146),

    // CField::OnPacket
    TransferFieldReqIgnored(147),
    TransferChannelReqIgnored(148),
    FieldSpecificData(149),
    GroupMessage(150),
    Whisper(151),
    CoupleMessage(152),
    MobSummonItemUseResult(153),
    FieldEffect(154),
    FieldObstacleOnOff(155),
    FieldObstacleOnOffStatus(156),
    FieldObstacleAllReset(157),
    BlowWeather(158),
    PlayJukeBox(159),
    AdminResult(160),
    Quiz(161),
    Desc(162),
    Clock(163),
    SetQuestClear(166),
    SetQuestTime(167),
    Warn(168),
    SetObjectState(169),
    DestroyClock(170),
    StalkResult(172),
    QuickslotMappedInit(175),
    FootHoldInfo(176),
    RequestFootHoldInfo(177),

    HontaleTimer(359),
    ChaosZakumTimer(360),
    HontailTimer(361),
    ZakumTimer(362),

    GoldHammerResult(418),

    // CUserPool::OnPacket
    UserEnterField(179),
    UserLeaveField(180),

    // CUserPool::OnUserCommonPacket
    UserChat(181),
    UserChatNLCPQ(182),
    UserADBoard(183),
    UserMiniRoomBalloon(184),
    UserConsumeItemEffect(185),
    UserItemUpgradeEffect(186),
    UserItemHyperUpgradeEffect(187),
    UserItemOptionUpgradeEffect(188),
    UserItemReleaseEffect(189),
    UserItemUnreleaseEffect(190),
    UserHitByUser(191),
    UserTeslaTriangle(192),
    UserFollowCharacter(193),
    UserShowPQReward(194),
    UserSetPhase(195),
    SetPortalUsable(196),
    ShowPamsSongResult(197),

    // CUser::OnPetPacket
    PetActivated(198),
    PetEvol(199),
    PetTransferField(200),
    PetMove(201),
    PetAction(202),
    PetNameChanged(203),
    PetLoadExceptionList(204),
    PetActionCommand(205),

    // CUser::OnDragonPacket
    DragonEnterField(206),
    DragonMove(207),
    DragonLeaveField(208),

    // CUserPool::OnUserRemotePacket
    UserMove(210),
    UserMeleeAttack(211),
    UserShootAttack(212),
    UserMagicAttack(213),
    UserBodyAttack(214),
    UserSkillPrepare(215),
    UserMovingShootAttackPrepare(216),
    UserSkillCancel(217),
    UserHit(218),
    UserEmotion(219),
    UserSetActiveEffectItem(220),
    UserShowUpgradeTombEffect(221),
    UserSetActivePortableChair(222),
    UserAvatarModified(223),
    UserEffectRemote(224),
    UserTemporaryStatSet(225),
    UserTemporaryStatReset(226),
    UserHP(227),
    UserGuildNameChanged(228),
    UserGuildMarkChanged(229),
    UserThrowGrenade(230),

    // CUserPool::OnUserLocalPacket
    UserSitResult(231),
    UserEmotionLocal(232),
    UserEffectLocal(233),
    UserTeleport(234),
    Premium(235),
    MesoGive_Succeeded(236),
    MesoGive_Failed(237),
    Random_Mesobag_Succeed(238),
    Random_Mesobag_Failed(239),
    FieldFadeInOut(240),
    FieldFadeOutForce(241),
    UserQuestResult(242),
    NotifyHPDecByField(243),
    UserPetSkillChanged(244),
    UserBalloonMsg(245),
    PlayEventSound(246),
    PlayMinigameSound(247),
    UserMakerResult(248),
    UserOpenConsultBoard(249),
    UserOpenClassCompetitionPage(250),
    UserOpenUI(251),
    UserOpenUIWithOption(252),
    SetDirectionMode(253),
    SetStandAloneMode(254),
    UserHireTutor(255),
    UserTutorMsg(256),
    IncCombo(257),
    UserRandomEmotion(258),
    ResignQuestReturn(259),
    PassMateName(260),
    SetRadioSchedule(261),
    UserOpenSkillGuide(262),
    UserNoticeMsg(263),
    UserChatMsg(264),
    UserBuffzoneEffect(265),
    UserGoToCommoditySN(266),
    UserDamageMeter(267),
    UserTimeBombAttack(268),
    UserPassiveMove(269),
    UserFollowCharacterFailed(270),
    UserRequestVengeance(271),
    UserRequestExJablin(272),
    UserAskAPSPEvent(273),
    QuestGuideResult(274),
    UserDeliveryQuest(275),
    SkillCooltimeSet(276),

    // CSummonedPool::OnPacket
    SummonedEnterField(278),
    SummonedLeaveField(279),
    SummonedMove(280),
    SummonedAttack(281),
    SummonedSkill(282),
    SummonedHit(283),

    // CMobPool::OnPacket
    MobEnterField(284),
    MobLeaveField(285),
    MobChangeController(286),
    MobMove(287),
    MobCtrlAck(288),
    MobCtrlHint(289),
    MobStatSet(290),
    MobStatReset(291),
    MobSuspendReset(292),
    MobAffected(293),
    MobDamaged(294),
    MobSpecialEffectBySkill(295),
    MobHPChange(296),
    MobCrcKeyChanged(297),
    MobHPIndicator(298),
    MobCatchEffect(299),
    MobEffectByItem(300),
    MobSpeaking(301),
    MobChargeCount(302),
    MobSkillDelay(303),
    MobRequestResultEscortInfo(304),
    MobEscortStopEndPermmision(305),
    MobEscortStopSay(306),
    MobEscortReturnBefore(307),
    MobNextAttack(308),
    MobAttackedByMob(309),

    // CNpcPool::OnPacket
    NpcEnterField(311),
    NpcLeaveField(312),
    NpcChangeController(313),
    NpcMove(314),
    NpcUpdateLimitedInfo(315),
    NpcSpecialAction(316),
    NpcSetScript(317),

    // CEmployeePool::OnPacket
    EmployeeEnterField(319),
    EmployeeLeaveField(320),
    EmployeeMiniRoomBalloon(321),

    // CDropPool::OnPacket
    DropEnterField(322),
    DropReleaseAllFreeze(323),
    DropLeaveField(324),

    // CMessageBoxPool::OnPacket
    CreateMessgaeBoxFailed(325),
    MessageBoxEnterField(326),
    MessageBoxLeaveField(327),

    // CAffectedAreaPool::OnPacket
    AffectedAreaCreated(328),
    AffectedAreaRemoved(329),

    // CTownPortalPool::OnPacket
    TownPortalCreated(330),
    TownPortalRemoved(331),

    // COpenGatePool::OnPacket
    OpenGateCreated(332),
    OpenGateRemoved(333),

    // CReactorPool::OnPacket
    ReactorChangeState(334),
    ReactorMove(335),
    ReactorEnterField(336),
    ReactorLeaveField(337),

    // CScriptMan::OnPacket
    ScriptMessage(363),

    // CShopDlg::OnPacket
    OpenShopDlg(364),
    ShopResult(365),

    // CAdminShopDlg::OnPacket
    AdminShopResult(366),
    AdminShopCommodity(367),

    // CTrunkDlg::OnPacket
    TrunkResult(368),

    // CStoreBankDlg::OnPacket
    StoreBankGetAllResult(369),
    StoreBankResult(370),

    // CRPSGameDlg::OnPacket
    RPSGame(371),

    // CUIMessenger::OnPacket
    Messenger(372),

    // CMiniRoomBaseDlg::OnPacketBase
    MiniRoom(373),

    // CParcelDlg::OnPacket
    Parcel(381),

    // CFuncKeyMappedMan::OnPacket
    FuncKeyMappedInit(398),
    PetConsumeItemInit(399),
    PetConsumeMPItemInit(400),

    // CMapleTVMan::OnPacket
    MapleTVUpdateMessage(405),
    MapleTVClearMessage(406),
    MapleTVSendMessageResult(407),

    // CUICharacterSaleDlg::OnPacket
    CheckDuplicatedIDResultInCS(413),
    CreateNewCharacterResultInCS(414),
    CreateNewCharacterFailInCS(415),
    CharacterSale(416),

    // CBattleRecordMan::OnPacket
    BattleRecordDotDamageInfo(421),
    BattleRecordRequestResult(422),

    // CUIItemUpgrade::OnPacket
    ItemUpgradeResult(425),
    ItemUpgradeFail(426),

    // CUIVega::OnPacket
    VegaResult(429),

    // -----------------------------------------------------------------------------------------------------------------

    // CField_ContiMove::OnPacket
    CONTIMOVE(164),
    CONTISTATE(165),

    // CField_Massacre::OnPacket
    MassacreIncGauge(173),

    // CField_MassacreResult::OnPacket
    MassacreResult(174),

    // CField_KillCount::OnPacket
    FieldKillCount(178),

    // CField_SnowBall::OnPacket
    SnowBallState(338),
    SnowBallHit(339),
    SnowBallMsg(340),
    SnowBallTouch(341),

    // CField_Coconut::OnPacket
    CoconutHit(342),
    CoconutScore(343),

    // CField_GuildBoss::OnPacket
    HealerMove(344),
    PulleyStateChange(345),

    // CField_MonsterCarnival::OnPacket | CField_MonsterCarnivalRevive::OnPacket
    MCarnivalEnter(346),
    MCarnivalPersonalCP(347),
    MCarnivalTeamCP(348),
    MCarnivalResultSuccess(349),
    MCarnivalResultFail(350),
    MCarnivalDeath(351),
    MCarnivalMemberOut(352),
    MCarnivalGameResult(353),

    // CField_AriantArena::OnPacket
    ShowArenaResult(171),
    ArenaScore(354),

    // CField_Battlefield::OnPacket
    BattlefieldEnter(355),
    BattlefieldScore(356),
    BattlefieldTeamChanged(357),

    // CField_Witchtower::OnPacket
    WitchtowerScore(358),

    // CField_Tournament::OnPacket
    Tournament(374),
    TournamentMatchTable(375),
    TournamentSetPrize(376),
    TournamentNoticeUEW(377),
    TournamentAvatarInfo(378),

    // CField_Wedding::OnPacket
    WeddingProgress(379),
    WeddingCremonyEnd(380),

    // -----------------------------------------------------------------------------------------------------------------

    // CCashShop::OnPacket
    CashShopChargeParamResult(382),
    CashShopQueryCashResult(383),
    CashShopCashItemResult(384),
    CashShopPurchaseExpChanged(385),
    CashShopGiftMateInfoResult(386),
    CashShopCheckDuplicatedIDResult(387),
    CashShopCheckNameChangePossibleResult(388),
    CashShopCheckTransferWorldPossibleResult(390),
    CashShopGachaponStampItemResult(391),
    CashShopCashItemGachaponResult(392),
    CashShopCashGachaponOpenResult(393),
    ChangeMaplePointResult(394),
    CashShopOneADay(395),
    CashShopNoticeFreeCashItem(396),
    CashShopMemberShopResult(397),

    // CITC::OnPacket
    ITCChargeParamResult(410),
    ITCQueryCashResult(411),
    ITCNormalItemResult(412),

    LogoutGift(432),
    NO(433);


    private static final Map<Short, OutHeader> headerMap = new HashMap<>();
    private static final Set<OutHeader> ignoreHeaders = Set.of(
            CharacterInfo,
            PartyResult,
            SetField,
            SetCashShop,
            StatChanged,
            UserMove,
            PetActivated,
            PetMove,
            PetAction,
            DragonMove,
            SummonedMove,
            MobEnterField,
            MobLeaveField,
            MobChangeController,
            MobMove,
            MobCtrlAck,
            MobHPIndicator,
            NpcEnterField,
            NpcLeaveField,
            NpcChangeController,
            NpcMove,
            DropEnterField,
            DropLeaveField,
            ReactorEnterField,
            ReactorLeaveField,
            QuickslotMappedInit,
            FuncKeyMappedInit
    );

    static {
        for (OutHeader header : values()) {
            headerMap.put(header.getValue(), header);
        }
    }

    private final short value;

    OutHeader(int value) {
        this.value = (short) value;
    }

    public final short getValue() {
        return value;
    }

    public final boolean isIgnoreHeader() {
        return ignoreHeaders.contains(this);
    }

    public static OutHeader getByValue(short op) {
        return headerMap.get(op);
    }
}
