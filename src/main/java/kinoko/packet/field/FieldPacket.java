package kinoko.packet.field;

import kinoko.provider.map.FieldType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptMessage;
import kinoko.world.GameConstants;
import kinoko.world.dialog.shop.ShopDialog;
import kinoko.world.dialog.shop.ShopResultType;
import kinoko.world.dialog.trunk.TrunkResult;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.config.FuncKeyMapped;

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
        if (fieldType == FieldType.BATTLEFIELD || fieldType == FieldType.COCONUT) {
            // CField_BattleField::DecodeFieldSpecificData, CField_Coconut::DecodeFieldSpecificData
            outPacket.encodeByte(data); // nTeam
        } else if (fieldType == FieldType.MONSTERCARNIVAL || fieldType == FieldType.MONSTERCARNIVALREVIVE) {
            // CField_MonsterCarnival::DecodeFieldSpecificData,  CField_MonsterCarnivalRevive::DecodeFieldSpecificData
            outPacket.encodeByte(data); // nTeamForMCarnival
        }
        return outPacket;
    }

    public static OutPacket groupMessage(GroupMessageType messageType, String characterName, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GroupMessage);
        outPacket.encodeByte(messageType.getValue());
        outPacket.encodeString(characterName); // sFrom
        outPacket.encodeString(message); // sMsg
        return outPacket;
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

    // CDropPool::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket dropEnterField(Drop drop, DropEnterType enterType) {
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
            outPacket.encodeShort(0); // tDelay
        }
        if (!drop.isMoney()) {
            outPacket.encodeFT(drop.getItem().getDateExpire()); // m_dateExpire
        }
        outPacket.encodeByte(!drop.isUserDrop()); // bByPet
        outPacket.encodeByte(false); // bool -> IWzGr2DLayer::Putz(0xC0041F15)
        return outPacket;
    }

    public static OutPacket dropLeaveField(Drop drop, DropLeaveType leaveType, int pickUpId, int petIndex) {
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
                outPacket.encodeShort(0); // delay
            }
        }
        return outPacket;
    }


    // CTownPortalPool::OnPacket ---------------------------------------------------------------------------------------

    public static OutPacket townPortalCreated(int characterId, int x, int y, boolean animate) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TownPortalCreated);
        outPacket.encodeByte(!animate); // nState : create animation if false
        outPacket.encodeInt(characterId); // dwCharacterID
        outPacket.encodeShort(x);
        outPacket.encodeShort(y);
        return outPacket;
    }

    public static OutPacket townPortalRemoved(int characterId, boolean animate) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TownPortalRemoved);
        outPacket.encodeByte(!animate); // nState : remove animation if false
        outPacket.encodeInt(characterId); // dwCharacterID
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

    public static OutPacket openShopDlg(ShopDialog dialog) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OpenShopDlg);
        dialog.encode(outPacket);
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


    // CTrunkDlg::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket trunkResult(TrunkResult trunkResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TrunkResult);
        trunkResult.encode(outPacket);
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
}
