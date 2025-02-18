package kinoko.packet.field;

import kinoko.provider.map.FieldType;
import kinoko.script.common.ScriptMessage;
import kinoko.server.cashshop.CashItemResultType;
import kinoko.server.dialog.miniroom.EntrustedShop;
import kinoko.server.dialog.shop.ShopDialog;
import kinoko.server.dialog.shop.ShopResultType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.GameConstants;
import kinoko.world.field.OpenGate;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;
import kinoko.world.user.data.FuncKeyMapped;

public final class FieldPacket {
    // CField::OnPacket ------------------------------------------------------------------------------------------------

    public static OutPacket transferFieldReqIgnored(TransferFieldType transferFieldType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TransferFieldReqIgnored);
        outPacket.encodeByte(transferFieldType.getValue());
        return outPacket;
    }

    public static OutPacket transferChannelReqIgnored(TransferChannelType transferChannelType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TransferChannelReqIgnored);
        outPacket.encodeByte(transferChannelType.getValue());
        return outPacket;
    }

    public static OutPacket fieldSpecificData(FieldType fieldType, int data) {
        // field->DecodeFieldSpecificData
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldSpecificData);
        if (fieldType == FieldType.COCONUT || fieldType == FieldType.BATTLEFIELD) {
            // CField_BattleField::DecodeFieldSpecificData, CField_Coconut::DecodeFieldSpecificData
            outPacket.encodeByte(data); // nTeam
        } else if (fieldType == FieldType.MONSTERCARNIVAL || fieldType == FieldType.MONSTERCARNIVALREVIVE) {
            // CField_MonsterCarnival::DecodeFieldSpecificData,  CField_MonsterCarnivalRevive::DecodeFieldSpecificData
            outPacket.encodeByte(data); // nTeamForMCarnival
        }
        return outPacket;
    }

    public static OutPacket groupMessage(ChatGroupType groupType, String characterName, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GroupMessage);
        outPacket.encodeByte(groupType.getValue());
        outPacket.encodeString(characterName); // sFrom
        outPacket.encodeString(message); // sMsg
        return outPacket;
    }

    public static OutPacket blowWeather(int itemId, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.BlowWeather);
        outPacket.encodeByte(0); // nBlowType
        outPacket.encodeInt(itemId); // nItemID, 0 to stop
        outPacket.encodeString(message);
        return outPacket;
    }

    public static OutPacket clock(int hour, int min, int sec) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Clock);
        outPacket.encodeByte(1); // CClock::SetClock
        outPacket.encodeByte(hour); // nHour
        outPacket.encodeByte(min); // nMin
        outPacket.encodeByte(sec); // nSec
        return outPacket;
    }

    public static OutPacket clock(int remain) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Clock);
        outPacket.encodeByte(2); // CClock::SetTimer
        outPacket.encodeInt(remain); // nRemain
        return outPacket;
    }

    public static OutPacket setObjectState(String name, int state) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SetObjectState);
        outPacket.encodeString(name); // sName
        outPacket.encodeInt(state); // nState
        return outPacket;
    }

    public static OutPacket destroyClock() {
        return OutPacket.of(OutHeader.DestroyClock);
    }

    public static OutPacket quickslotMappedInit(int[] quickslotKeyMap) {
        final OutPacket outPacket = OutPacket.of(OutHeader.QuickslotMappedInit);
        outPacket.encodeByte(true); // defaults if false
        // aQuickslotKeyMapped (32)
        assert quickslotKeyMap.length == GameConstants.QUICKSLOT_KEY_MAP_SIZE;
        for (int key : quickslotKeyMap) {
            outPacket.encodeInt(key);
        }
        return outPacket;
    }


    // CEmployeePool::OnPacket -----------------------------------------------------------------------------------------

    public static OutPacket employeeEnterField(EntrustedShop shop) {
        final OutPacket outPacket = OutPacket.of(OutHeader.EmployeeEnterField);
        outPacket.encodeInt(shop.getEmployerId()); // dwEmployerID
        outPacket.encodeInt(shop.getTemplateId()); // dwTemplateID
        // CEmployee::Init
        outPacket.encodeShort(shop.getX());
        outPacket.encodeShort(shop.getY());
        outPacket.encodeShort(shop.getFoothold());
        outPacket.encodeString(shop.getEmployerName()); // %s's Hired Merchant
        // CEmployee::SetBalloon
        outPacket.encodeByte(shop.getType().getValue()); // nMiniRoomType
        outPacket.encodeInt(shop.getId()); // dwMiniRoomSN
        outPacket.encodeString(shop.getTitle());
        outPacket.encodeByte(0); // nGameKind
        outPacket.encodeByte(shop.getUsers().size()); // nCurUsers
        outPacket.encodeByte(shop.getMaxUsers()); // nMaxUsers
        outPacket.encodeByte(shop.isGameOn()); // bGameOn
        return outPacket;
    }

    public static OutPacket employeeLeaveField(int employeeId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.EmployeeLeaveField);
        outPacket.encodeInt(employeeId);
        return outPacket;
    }

    public static OutPacket employeeMiniRoomBalloon(EntrustedShop shop) {
        final OutPacket outPacket = OutPacket.of(OutHeader.EmployeeLeaveField);
        outPacket.encodeInt(shop.getEmployerId());
        // CEmployee::SetBalloon
        outPacket.encodeByte(shop.getType().getValue()); // nMiniRoomType
        outPacket.encodeInt(shop.getId()); // dwMiniRoomSN
        outPacket.encodeString(shop.getTitle());
        outPacket.encodeByte(0); // nGameKind
        outPacket.encodeByte(shop.getUsers().size()); // nCurUsers
        outPacket.encodeByte(shop.getMaxUsers()); // nMaxUsers
        outPacket.encodeByte(shop.isGameOn()); // bGameOn
        return outPacket;
    }


    // CDropPool::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket dropEnterField(Drop drop, DropEnterType enterType, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DropEnterField);
        outPacket.encodeByte(enterType.getValue()); // nEnterType
        outPacket.encodeInt(drop.getId()); // DROP->dwId
        outPacket.encodeByte(drop.isMoney()); // DROP->bIsMoney
        outPacket.encodeInt(drop.isMoney() ? drop.getMoney() : drop.getItem().getItemId()); // DROP->nInfo
        outPacket.encodeInt(drop.getOwnerId()); // DROP->dwOwnerID
        outPacket.encodeByte(drop.getOwnType().getValue()); // DROP->nOwnType
        outPacket.encodeShort(drop.getX());
        outPacket.encodeShort(drop.getY());
        outPacket.encodeInt(drop.isUserDrop() ? 0 : drop.getSource().getId()); // DROP->dwSourceID (object id)
        if (enterType != DropEnterType.ON_THE_FOOTHOLD) {
            outPacket.encodeShort(drop.getSource().getX()); // source x
            outPacket.encodeShort(drop.getSource().getY()); // source y
            outPacket.encodeShort(delay); // tDelay
        }
        if (!drop.isMoney()) {
            outPacket.encodeFT(drop.getItem().getDateExpire()); // m_dateExpire
        }
        outPacket.encodeByte(!drop.isUserDrop()); // bByPet
        outPacket.encodeByte(false); // bool -> IWzGr2DLayer::Putz(0xC0041F15)
        return outPacket;
    }

    public static OutPacket dropLeaveField(Drop drop, DropLeaveType leaveType, int pickUpId, int petIndex, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DropLeaveField);
        outPacket.encodeByte(leaveType.getValue());
        outPacket.encodeInt(drop.getId());
        switch (leaveType) {
            case PICKED_UP_BY_USER, PICKED_UP_BY_MOB, PICKED_UP_BY_PET -> {
                outPacket.encodeInt(pickUpId); // dwPickUpID
                if (leaveType == DropLeaveType.PICKED_UP_BY_PET) {
                    outPacket.encodeInt(petIndex);
                }
            }
            case EXPLODE -> {
                outPacket.encodeShort(delay);
            }
        }
        return outPacket;
    }


    // CMessageBoxPool::OnPacket ---------------------------------------------------------------------------------------

    public static OutPacket messageBoxEnterField(User user, int messageBoxId, int itemId, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MessageBoxEnterField);
        outPacket.encodeInt(messageBoxId); // dwMessageBoxID
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeString(message); // sHope
        outPacket.encodeString(user.getCharacterName()); // sCharacterName
        outPacket.encodeShort(user.getX()); // ptHost.x
        outPacket.encodeShort(user.getY()); // ptHost.y
        return outPacket;
    }

    public static OutPacket messageBoxLeaveField(int messageBoxId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MessageBoxLeaveField);
        outPacket.encodeByte(0); // 0 = animate, 1 = no animation
        outPacket.encodeInt(messageBoxId); // dwMessageBoxID
        return outPacket;
    }


    // CAffectedAreaPool::OnPacket -------------------------------------------------------------------------------------

    public static OutPacket affectedAreaCreated(AffectedArea affectedArea) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AffectedAreaCreated);
        affectedArea.encode(outPacket);
        return outPacket;
    }

    public static OutPacket affectedAreaRemoved(AffectedArea affectedArea) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AffectedAreaRemoved);
        outPacket.encodeInt(affectedArea.getId());
        return outPacket;
    }


    // CTownPortalPool::OnPacket ---------------------------------------------------------------------------------------

    public static OutPacket townPortalCreated(User user, int x, int y, boolean animate) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TownPortalCreated);
        outPacket.encodeByte(!animate); // nState : create animation if false
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        outPacket.encodeShort(x);
        outPacket.encodeShort(y);
        return outPacket;
    }

    public static OutPacket townPortalRemoved(User user, boolean animate) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TownPortalRemoved);
        outPacket.encodeByte(!animate); // nState : remove animation if false
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        return outPacket;
    }


    // COpenGatePool::OnPacket ---------------------------------------------------------------------------------------

    public static OutPacket openGateCreated(User user, OpenGate openGate, boolean animate) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OpenGateCreated);
        outPacket.encodeByte(!animate); // nState : create animation if false
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        outPacket.encodeShort(openGate.getX());
        outPacket.encodeShort(openGate.getY());
        outPacket.encodeByte(openGate.isFirst());
        outPacket.encodeInt(user.getPartyId());
        return outPacket;
    }

    public static OutPacket openGateRemoved(User user, boolean notify, boolean first) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OpenGateRemoved);
        outPacket.encodeByte(notify); // The Portal that was installed earlier has disappeared.
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        outPacket.encodeByte(first);
        return outPacket;
    }


    // CReactorPool::OnPacket ------------------------------------------------------------------------------------------

    public static OutPacket reactorEnterField(Reactor reactor) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ReactorEnterField);
        outPacket.encodeInt(reactor.getId()); // dwID
        outPacket.encodeInt(reactor.getTemplateId()); // dwTemplateID
        outPacket.encodeByte(reactor.getState()); // nState
        outPacket.encodeShort(reactor.getX()); // ptPos.x
        outPacket.encodeShort(reactor.getY()); // ptPos.y
        outPacket.encodeByte(reactor.isFlip()); // bFlip
        outPacket.encodeString(reactor.getName()); // sName
        return outPacket;
    }

    public static OutPacket reactorLeaveField(Reactor reactor) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ReactorEnterField);
        outPacket.encodeInt(reactor.getId());
        outPacket.encodeByte(reactor.getState()); // nState
        outPacket.encodeShort(reactor.getX()); // ptPos.x
        outPacket.encodeShort(reactor.getY()); // ptPos.y
        return outPacket;
    }

    public static OutPacket reactorChangeState(Reactor reactor, int delay, int eventIndex, int endDelay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ReactorChangeState);
        outPacket.encodeInt(reactor.getId());
        outPacket.encodeByte(reactor.getState()); // nState
        outPacket.encodeShort(reactor.getX()); // ptPos.x
        outPacket.encodeShort(reactor.getY()); // ptPos.y
        outPacket.encodeShort(delay);
        outPacket.encodeByte(eventIndex); // nProperEventIdx
        outPacket.encodeByte(endDelay); // tStateEnd = update_time + 100 * byte
        return outPacket;
    }


    // CScriptMan::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket scriptMessage(ScriptMessage scriptMessage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ScriptMessage);
        scriptMessage.encode(outPacket);
        return outPacket;
    }


    // CShopDlg::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket openShopDlg(User user, ShopDialog dialog) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OpenShopDlg);
        dialog.encode(outPacket, user);
        return outPacket;
    }

    public static OutPacket shopResult(ShopResultType resultType) {
        return shopResult(resultType, null);
    }

    public static OutPacket shopResult(ShopResultType resultType, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ShopResult);
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case LimitLevel_Less, LimitLevel_More -> {
                outPacket.encodeInt(0); // level
            }
            case ServerMsg -> {
                outPacket.encodeByte(message != null && !message.isEmpty());
                outPacket.encodeString(message);
            }
        }
        return outPacket;
    }


    // CFuncKeyMappedMan::OnPacket -------------------------------------------------------------------------------------

    public static OutPacket funcKeyMappedInit(FuncKeyMapped[] funcKeyMap) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FuncKeyMappedInit);
        outPacket.encodeByte(false); // defaults if true
        // 89 * FUNC_KEY_MAPPED (5)
        assert funcKeyMap.length == GameConstants.FUNC_KEY_MAP_SIZE;
        for (FuncKeyMapped funcKeyMapped : funcKeyMap) {
            funcKeyMapped.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket petConsumeItemInit(int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PetConsumeItemInit);
        outPacket.encodeInt(itemId); // nPetConsumeItemID
        return outPacket;
    }

    public static OutPacket petConsumeMpItemInit(int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PetConsumeMPItemInit);
        outPacket.encodeInt(itemId); // nPetConsumeMPItemID
        return outPacket;
    }


    // CUIItemUpgrade::OnPacket ----------------------------------------------------------------------------------------

    public static OutPacket itemUpgradeResultSuccess(int iuc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ItemUpgradeResult);
        outPacket.encodeByte(CashItemResultType.ItemUpgradeSuccess.getValue());
        outPacket.encodeInt(0); // nResult
        outPacket.encodeInt(iuc); // nIUC
        return outPacket;
    }

    public static OutPacket itemUpgradeResultDone(int result) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ItemUpgradeResult);
        outPacket.encodeByte(CashItemResultType.ItemUpgradeDone.getValue());
        outPacket.encodeInt(result); // nResult
        outPacket.encodeInt(0); // nIUC?
        return outPacket;
    }

    public static OutPacket itemUpgradeResultErr(int reason) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ItemUpgradeResult);
        outPacket.encodeByte(CashItemResultType.ItemUpgradeErr.getValue());
        outPacket.encodeInt(reason); // 1 : The item is not upgradable. | 2 : 2 upgrade increases have been used already. | 3 : You can't use Vicious' Hammer on Horntail Necklace. | Unknown Error (%d).
        return outPacket;
    }
}
