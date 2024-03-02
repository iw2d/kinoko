package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.Item;
import kinoko.world.user.User;

public final class CashItemInfo implements Encodable {
    private final Item item;
    private final int commodityId;
    private final int accountId;
    private final int characterId;
    private final String characterName;

    public CashItemInfo(Item item, int commodityId, int accountId, int characterId, String characterName) {
        this.item = item;
        this.commodityId = commodityId;
        this.accountId = accountId;
        this.characterId = characterId;
        this.characterName = characterName;
    }

    public Item getItem() {
        return item;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_CashItemInfo struct (55)
        outPacket.encodeLong(item.getItemSn()); // liSN
        outPacket.encodeInt(accountId); // dwAccountID
        outPacket.encodeInt(characterId); // dwCharacterID
        outPacket.encodeInt(item.getItemId()); // nItemID
        outPacket.encodeInt(commodityId); // nCommodityID
        outPacket.encodeShort(item.getQuantity()); // nNumber
        outPacket.encodeString(characterName, 13); // sBuyCharacterID
        outPacket.encodeFT(item.getDateExpire()); // dateExpire
        outPacket.encodeInt(0); // nPaybackRate
        outPacket.encodeInt(0); // nDiscountRate
    }

    public static CashItemInfo from(Item item, User user) {
        return new CashItemInfo(
                item,
                0,
                user.getAccountId(),
                user.getCharacterId(),
                ""
        );
    }
}
