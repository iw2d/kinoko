package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.item.Item;
import kinoko.world.quest.QuestRecord;

public final class MessagePacket {
    // CWvsContext::OnMessage ------------------------------------------------------------------------------------------

    public static OutPacket questRecord(QuestRecord questRecord) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.QuestRecord.getValue());
        outPacket.encodeShort(questRecord.getQuestId()); // nQuestID
        outPacket.encodeByte(questRecord.getState().getValue());
        switch (questRecord.getState()) {
            case NONE -> {
                outPacket.encodeByte(true); // delete quest
            }
            case PERFORM -> {
                outPacket.encodeString(questRecord.getValue()); // sQRValue
            }
            case COMPLETE -> {
                outPacket.encodeFT(questRecord.getCompletedTime()); // ftEnd
            }
        }
        return outPacket;
    }

    public static OutPacket cashItemExpire(int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.CashItemExpire.getValue());
        outPacket.encodeInt(itemId); // nItemID
        return outPacket;
    }

    public static OutPacket incExp(int exp, int partyBonus, boolean white, boolean quest) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.IncEXP.getValue());
        outPacket.encodeByte(white); // white
        outPacket.encodeInt(exp); // exp
        outPacket.encodeByte(quest); // bOnQuest
        outPacket.encodeInt(0); // bonus event exp
        outPacket.encodeByte(0); // nMobEventBonusPercentage
        outPacket.encodeByte(0); // ignored
        outPacket.encodeInt(0); // nWeddingBonusEXP
        // outPacket.encodeByte(0); // nPlayTimeHour (if nMobEventBonusPercentage > 0)
        if (quest) {
            outPacket.encodeByte(0); // nSpiritWeekEventEXP
            // outPacket.encodeByte(0); // nQuestBonusRemainCount (if nSpiritWeekEventEXP != 0)
        }
        outPacket.encodeByte(0); // nPartyBonusEventRate
        outPacket.encodeInt(partyBonus); // nPartyBonusExp
        outPacket.encodeInt(0); // nItemBonusEXP
        outPacket.encodeInt(0); // nPremiumIPEXP
        outPacket.encodeInt(0); // nRainbowWeekEventEXP
        outPacket.encodeInt(0); // nPartyEXPRingEXP
        outPacket.encodeInt(0); // nCakePieEventBonus
        return outPacket;
    }

    public static OutPacket incPop(int pop) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.IncPOP.getValue());
        outPacket.encodeInt(pop);
        return outPacket;
    }

    public static OutPacket incMoney(int money) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.IncMoney.getValue());
        outPacket.encodeInt(money);
        return outPacket;
    }

    public static OutPacket incSp(int job, int sp) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.IncSP.getValue());
        outPacket.encodeShort(job); // nJob
        outPacket.encodeByte(sp); // nSP
        return outPacket;
    }

    public static OutPacket system(String format, Object... args) {
        return MessagePacket.system(String.format(format, args));
    }

    public static OutPacket system(String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.System.getValue());
        outPacket.encodeString(text);
        return outPacket;
    }

    public static OutPacket questRecordEx(int questId, String value) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.QuestRecordEx.getValue());
        outPacket.encodeShort(questId); // usQuestID
        outPacket.encodeString(value); // rawStr
        return outPacket;
    }

    public static OutPacket skillExpire(int skillId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.SkillExpire.getValue());
        outPacket.encodeByte(1); // count
        outPacket.encodeInt(skillId); // nSkillID
        return outPacket;
    }


    // CWvsContext::OnDropPickUpMessage --------------------------------------------------------------------------------

    public static OutPacket cannotAcquireAnyItems() {
        return MessagePacket.dropPickUpMessage(DropPickUpMessageType.CANNOT_ACQUIRE_ANY_ITEMS);
    }

    public static OutPacket unavailableForPickUp() {
        return MessagePacket.dropPickUpMessage(DropPickUpMessageType.UNAVAILABLE_FOR_PICK_UP);
    }

    public static OutPacket cannotGetAnymoreItems() {
        return MessagePacket.dropPickUpMessage(DropPickUpMessageType.CANNOT_GET_ANYMORE_ITEMS);
    }

    public static OutPacket pickUpItem(Item item) {
        return MessagePacket.pickUpItem(item.getItemId(), item.getQuantity());
    }

    public static OutPacket pickUpItem(int itemId, int quantity) {
        final OutPacket outPacket = MessagePacket.dropPickUpMessage(DropPickUpMessageType.ITEM_BUNDLE);
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeInt(quantity);
        return outPacket;
    }

    public static OutPacket pickUpMoney(int money, boolean portionNotFound) {
        final OutPacket outPacket = MessagePacket.dropPickUpMessage(DropPickUpMessageType.MONEY);
        outPacket.encodeByte(portionNotFound);
        outPacket.encodeInt(money);
        outPacket.encodeShort(0); // Internet Cafe Meso Bonus
        return outPacket;
    }

    private static OutPacket dropPickUpMessage(DropPickUpMessageType dropPickUpMessageType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.Message);
        outPacket.encodeByte(MessageType.DropPickUp.getValue());
        outPacket.encodeByte(dropPickUpMessageType.getValue());
        return outPacket;
    }

    private enum DropPickUpMessageType {
        CANNOT_ACQUIRE_ANY_ITEMS(-3),
        UNAVAILABLE_FOR_PICK_UP(-2),
        CANNOT_GET_ANYMORE_ITEMS(-1),
        ITEM_BUNDLE(0),
        MONEY(1),
        ITEM_SINGLE(2);

        private final byte value;

        DropPickUpMessageType(int value) {
            this.value = (byte) value;
        }

        public final byte getValue() {
            return value;
        }
    }
}
