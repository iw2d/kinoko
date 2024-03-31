package kinoko.packet.field;

import kinoko.packet.field.effect.FieldEffect;
import kinoko.provider.map.FieldType;
import kinoko.server.dialog.shop.ShopDialog;
import kinoko.server.dialog.shop.ShopResultType;
import kinoko.server.dialog.trunk.TrunkResult;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.server.whisper.WhisperResult;
import kinoko.world.GameConstants;
import kinoko.world.social.party.TownPortal;
import kinoko.world.user.User;
import kinoko.world.user.config.FuncKeyMapped;

public final class FieldPacket {
    // CField::OnPacket ------------------------------------------------------------------------------------------------

    public static OutPacket transferFieldReqIgnored(TransferFieldType transferFieldType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRANSFER_FIELD_REQ_IGNORED);
        outPacket.encodeByte(transferFieldType.getValue());
        return outPacket;
    }

    public static OutPacket transferChannelReqIgnored(TransferChannelType transferChannelType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRANSFER_CHANNEL_REQ_IGNORED);
        outPacket.encodeByte(transferChannelType.getValue());
        return outPacket;
    }

    public static OutPacket fieldSpecificData(FieldType fieldType, int data) {
        // field->DecodeFieldSpecificData
        final OutPacket outPacket = OutPacket.of(OutHeader.FIELD_SPECIFIC_DATA);
        if (fieldType == FieldType.BATTLEFIELD || fieldType == FieldType.COCONUT) {
            // CField_BattleField::DecodeFieldSpecificData, CField_Coconut::DecodeFieldSpecificData
            outPacket.encodeByte(data); // nTeam
        } else if (fieldType == FieldType.MONSTER_CARNIVAL || fieldType == FieldType.MONSTER_CARNIVAL_REVIVE) {
            // CField_MonsterCarnival::DecodeFieldSpecificData,  CField_MonsterCarnivalRevive::DecodeFieldSpecificData
            outPacket.encodeByte(data); // nTeamForMCarnival
        }
        return outPacket;
    }

    public static OutPacket groupMessage(GroupMessageType messageType, String characterName, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.GROUP_MESSAGE);
        outPacket.encodeByte(messageType.getValue());
        outPacket.encodeString(characterName);
        outPacket.encodeString(message);
        return outPacket;
    }

    public static OutPacket whisper(WhisperResult whisperResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.WHISPER);
        whisperResult.encode(outPacket);
        return outPacket;
    }

    public static OutPacket effect(FieldEffect fieldEffect) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FIELD_EFFECT);
        fieldEffect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket quickSlotMappedInit(int[] quickslotKeyMap) {
        final OutPacket outPacket = OutPacket.of(OutHeader.QUICKSLOT_MAPPED_INIT);
        outPacket.encodeByte(true); // defaults if false
        // aQuickslotKeyMapped (32)
        assert quickslotKeyMap.length == GameConstants.QUICKSLOT_KEY_MAP_SIZE;
        for (int key : quickslotKeyMap) {
            outPacket.encodeInt(key);
        }
        return outPacket;
    }


    // CTownPortalPool::OnPacket ---------------------------------------------------------------------------------------

    public static OutPacket townPortalCreated(User user, TownPortal townPortal, boolean enterField) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TOWN_PORTAL_CREATED);
        outPacket.encodeByte(enterField); // nState : create animation if false
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        outPacket.encodeShort(townPortal.getX());
        outPacket.encodeShort(townPortal.getY());
        return outPacket;
    }

    public static OutPacket townPortalRemoved(User user, boolean enterField) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TOWN_PORTAL_REMOVED);
        outPacket.encodeByte(enterField); // nState : remove animation if false
        outPacket.encodeInt(user.getCharacterId()); // dwCharacterID
        return outPacket;
    }


    // CShopDlg::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket openShopDlg(ShopDialog dialog) {
        final OutPacket outPacket = OutPacket.of(OutHeader.OPEN_SHOP_DLG);
        dialog.encode(outPacket);
        return outPacket;
    }

    public static OutPacket shopResult(ShopResultType resultType) {
        return shopResult(resultType, "Due to an error, the trade did not happen."); // Default message for SERVER_MSG
    }

    public static OutPacket shopResult(ShopResultType resultType, String message) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SHOP_RESULT);
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case LIMIT_LEVEL_LESS, LIMIT_LEVEL_MORE -> {
                outPacket.encodeInt(0); // level
            }
            case SERVER_MSG -> {
                outPacket.encodeByte(true);
                outPacket.encodeString(message);
            }
        }
        return outPacket;
    }


    // CTrunkDlg::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket trunkResult(TrunkResult trunkResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRUNK_RESULT);
        trunkResult.encode(outPacket);
        return outPacket;
    }


    // CFuncKeyMappedMan::OnPacket -------------------------------------------------------------------------------------

    public static OutPacket funcKeyMappedInit(FuncKeyMapped[] funcKeyMap) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FUNC_KEY_MAPPED_INIT);
        outPacket.encodeByte(false); // defaults if true
        // 89 * FUNC_KEY_MAPPED (5)
        assert funcKeyMap.length == GameConstants.FUNC_KEY_MAP_SIZE;
        for (FuncKeyMapped funcKeyMapped : funcKeyMap) {
            funcKeyMapped.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket petConsumeItemInit(int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_CONSUME_ITEM_INIT);
        outPacket.encodeInt(itemId); // nPetConsumeItemID
        return outPacket;
    }

    public static OutPacket petConsumeMpItemInit(int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.PET_CONSUME_MP_ITEM_INIT);
        outPacket.encodeInt(itemId); // nPetConsumeMPItemID
        return outPacket;
    }
}
