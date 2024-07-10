package kinoko.world.user.data;

import kinoko.server.packet.OutPacket;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;
import kinoko.world.item.ItemConstants;

import java.util.ArrayList;
import java.util.List;

public final class CoupleRecord {
    private List<CoupleData> coupleRecords = new ArrayList<>();
    private List<CoupleData> friendRecords = new ArrayList<>();
    private CoupleData activeCoupleRecord;
    private CoupleData activeFriendRecord;

    public void reset(Inventory equipped, Inventory equipInventory) {
        // Load RingData from equipped items
        final List<CoupleData> coupleRecords = new ArrayList<>();
        final List<CoupleData> friendRecords = new ArrayList<>();
        for (Item item : equipped.getItems().values()) {
            if (item.getRingData() == null) {
                continue;
            }
            final CoupleData coupleData = CoupleData.from(item);
            if (ItemConstants.isCoupleEquipItem(coupleData.getItemId())) {
                coupleRecords.add(coupleData);
            }
            if (ItemConstants.isFriendshipEquipItem(coupleData.getItemId())) {
                friendRecords.add(coupleData);
            }
        }
        this.activeCoupleRecord = coupleRecords.isEmpty() ? null : coupleRecords.get(0);
        this.activeFriendRecord = friendRecords.isEmpty() ? null : friendRecords.get(0);
        // Load RingData from equip inventory
        for (Item item : equipInventory.getItems().values()) {
            if (item.getRingData() == null) {
                continue;
            }
            final CoupleData coupleData = CoupleData.from(item);
            if (ItemConstants.isCoupleEquipItem(coupleData.getItemId())) {
                coupleRecords.add(coupleData);
            }
            if (ItemConstants.isFriendshipEquipItem(coupleData.getItemId())) {
                friendRecords.add(coupleData);
            }
        }
        this.coupleRecords = coupleRecords;
        this.friendRecords = friendRecords;
    }

    public void encodeForLocal(OutPacket outPacket) {
        outPacket.encodeShort(coupleRecords.size());
        for (CoupleData coupleData : coupleRecords) {
            // GW_CoupleRecord::Decode (33)
            outPacket.encodeInt(coupleData.getPairCharacterId()); // dwPairCharacterID
            outPacket.encodeString(coupleData.getPairCharacterString(), 13); // sPairCharacterName
            outPacket.encodeLong(coupleData.getItemSn()); // liSN
            outPacket.encodeLong(coupleData.getPairItemSn()); // liPairSN
        }
        outPacket.encodeShort(friendRecords.size());
        for (CoupleData coupleData : friendRecords) {
            // GW_FriendRecord::Decode (37)
            outPacket.encodeInt(coupleData.getPairCharacterId()); // dwPairCharacterID
            outPacket.encodeString(coupleData.getPairCharacterString(), 13); // sPairCharacterName
            outPacket.encodeLong(coupleData.getItemSn()); // liSN
            outPacket.encodeLong(coupleData.getPairItemSn()); // liPairSN
            outPacket.encodeInt(coupleData.getItemId()); // dwFriendItemID
        }
        outPacket.encodeShort(0); // short * GW_MarriageRecord::Decode
    }

    public void encodeForRemote(OutPacket outPacket) {
        outPacket.encodeByte(activeCoupleRecord != null);
        if (activeCoupleRecord != null) {
            outPacket.encodeLong(activeCoupleRecord.getItemSn()); // liCoupleItemSN
            outPacket.encodeLong(activeCoupleRecord.getPairItemSn()); // liPairItemSN
            outPacket.encodeInt(activeCoupleRecord.getItemId()); // nItemID
        }
        outPacket.encodeByte(activeFriendRecord != null);
        if (activeFriendRecord != null) {
            outPacket.encodeLong(activeFriendRecord.getItemSn()); // liFriendshipItemSN
            outPacket.encodeLong(activeFriendRecord.getPairItemSn()); // liFriendshipPairItemSN
            outPacket.encodeInt(activeFriendRecord.getItemId()); // nItemID
        }
        outPacket.encodeByte(false); // marriage record
    }

    public static CoupleRecord from(Inventory equipped, Inventory equipInventory) {
        final CoupleRecord coupleRecord = new CoupleRecord();
        coupleRecord.reset(equipped, equipInventory);
        return coupleRecord;
    }

    private static class CoupleData {
        private final long itemSn;
        private final int itemId;
        private final int pairCharacterId;
        private final String pairCharacterString;
        private final long pairItemSn;

        private CoupleData(long itemSn, int itemId, int pairCharacterId, String pairCharacterString, long pairItemSn) {
            this.itemSn = itemSn;
            this.itemId = itemId;
            this.pairCharacterId = pairCharacterId;
            this.pairCharacterString = pairCharacterString;
            this.pairItemSn = pairItemSn;
        }

        public long getItemSn() {
            return itemSn;
        }

        public int getItemId() {
            return itemId;
        }

        public int getPairCharacterId() {
            return pairCharacterId;
        }

        public String getPairCharacterString() {
            return pairCharacterString;
        }

        public long getPairItemSn() {
            return pairItemSn;
        }

        public static CoupleData from(Item item) {
            return new CoupleData(
                    item.getItemSn(),
                    item.getItemId(),
                    item.getRingData().getPairCharacterId(),
                    item.getRingData().getPairCharacterName(),
                    item.getRingData().getPairItemSn()
            );
        }
    }
}
