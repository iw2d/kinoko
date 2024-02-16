package kinoko.packet.world;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.quest.QuestRecord;

public final class Message implements Encodable {
    private final MessageType type;
    private boolean bool1;
    private boolean bool2;
    private int int1;
    private int int2;
    private String string1;
    private DropPickUpMessageInfo dropPickUpInfo;
    private QuestRecord questRecord;

    public Message(MessageType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case DROP_PICK_UP -> {
                outPacket.encodeByte(dropPickUpInfo.getType().getValue());
                switch (dropPickUpInfo.getType()) {
                    case MONEY -> {
                        outPacket.encodeByte(dropPickUpInfo.isPortionNotFound());
                        outPacket.encodeInt(dropPickUpInfo.getMoney());
                        outPacket.encodeShort(0); // Internet Cafe Meso Bonus
                    }
                    case ITEM_BUNDLE -> {
                        outPacket.encodeInt(dropPickUpInfo.getItemId()); // nItemID
                        outPacket.encodeInt(dropPickUpInfo.getItemCount());
                    }
                    case ITEM_SINGLE -> {
                        outPacket.encodeInt(dropPickUpInfo.getItemId());
                    }
                }
            }
            case QUEST_RECORD -> {
                outPacket.encodeShort(questRecord.getQuestId()); // nQuestID
                outPacket.encodeByte(questRecord.getQuestState().getValue());
                switch (questRecord.getQuestState()) {
                    case NONE -> {
                        outPacket.encodeByte(true); // delete quest
                    }
                    case PERFORM -> {
                        outPacket.encodeString(questRecord.getQuestInfo());
                    }
                    case COMPLETE -> {
                        outPacket.encodeFT(questRecord.getCompletedTime());
                    }
                }
            }
            case CASH_ITEM_EXPIRE, INC_POP, INC_MONEY, INC_GP, GIVE_BUFF -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case INC_EXP -> {
                outPacket.encodeByte(bool1); // white
                outPacket.encodeInt(int1); // exp
                outPacket.encodeByte(bool2); // bOnQuest
                outPacket.encodeInt(0); // bonus event exp
                outPacket.encodeByte(0); // nMobEventBonusPercentage
                outPacket.encodeByte(0); // ignored
                outPacket.encodeInt(0); // nWeddingBonusEXP
                // outPacket.encodeByte(0); // nPlayTimeHour (if nMobEventBonusPercentage > 0)
                outPacket.encodeByte(0); // nQuestBonusRemainCount (or spirit week bonus exp)
                outPacket.encodeByte(0); // nPartyBonusEventRate
                outPacket.encodeInt(0); // nPartyBonusExp
                outPacket.encodeInt(0); // nItemBonusEXP
                outPacket.encodeInt(0); // nPremiumIPEXP
                outPacket.encodeInt(0); // nRainbowWeekEventEXP
                outPacket.encodeInt(0); // nPartyEXPRingEXP
                outPacket.encodeInt(0); // nCakePieEventBonus
            }
            case INC_SP -> {
                outPacket.encodeShort(int1); // nJob
                outPacket.encodeByte(int2); // nSP
            }
            case GENERAL_ITEM_EXPIRE, ITEM_PROTECT_EXPIRE, SKILL_EXPIRE -> {
                outPacket.encodeByte(1); // count
                outPacket.encodeInt(int1); // nItemID / nSkillID
            }
            case SYSTEM -> {
                outPacket.encodeString(string1); // sChat
            }
            case QUEST_RECORD_EX -> {
                outPacket.encodeShort(int1); // usQuestID
                outPacket.encodeString(string1); // rawStr
            }
            case ITEM_EXPIRE_REPLACE -> {
                outPacket.encodeByte(1); // count
                outPacket.encodeString(string1); // sChat
            }
        }
    }

    public static Message dropPickUp(DropPickUpMessageInfo info) {
        final Message message = new Message(MessageType.DROP_PICK_UP);
        message.dropPickUpInfo = info;
        return message;
    }

    public static Message dropPickUp(DropPickUpMessageType type) {
        return dropPickUp(new DropPickUpMessageInfo(type));
    }

    public static Message dropPickUpMoney(int money, boolean portionNotFound) {
        final DropPickUpMessageInfo info = new DropPickUpMessageInfo(DropPickUpMessageType.MONEY);
        info.setMoney(money);
        info.setPortionNotFound(portionNotFound);
        return dropPickUp(info);
    }

    public static Message dropPickUpItem(int itemId, int itemCount) {
        final DropPickUpMessageInfo info = new DropPickUpMessageInfo(DropPickUpMessageType.ITEM_BUNDLE);
        info.setItemId(itemId);
        info.setItemCount(itemCount);
        return dropPickUp(info);
    }

    public static Message questRecord(QuestRecord questRecord) {
        final Message message = new Message(MessageType.QUEST_RECORD);
        message.questRecord = questRecord;
        return message;
    }

    public static Message questRecordEx(int questId, String value) {
        final Message message = new Message(MessageType.QUEST_RECORD_EX);
        message.int1 = questId;
        message.string1 = value;
        return message;
    }

    public static Message incPop(int pop) {
        final Message message = new Message(MessageType.INC_POP);
        message.int1 = pop;
        return message;
    }

    public static Message incMoney(int money) {
        final Message message = new Message(MessageType.INC_MONEY);
        message.int1 = money;
        return message;
    }

    public static Message incExp(int exp, boolean white, boolean quest) {
        final Message message = new Message(MessageType.INC_EXP);
        message.int1 = exp;
        message.bool1 = white;
        message.bool2 = quest;
        return message;
    }

    public static Message incSp(int job, int sp) {
        final Message message = new Message(MessageType.INC_SP);
        message.int1 = job;
        message.int2 = sp;
        return message;
    }

    public static Message system(String text) {
        final Message message = new Message(MessageType.SYSTEM);
        message.string1 = text;
        return message;
    }

    public static Message system(String format, Object... args) {
        final Message message = new Message(MessageType.SYSTEM);
        message.string1 = String.format(format, args);
        return message;
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

    private static final class DropPickUpMessageInfo {
        private final DropPickUpMessageType type;
        private boolean portionNotFound;
        private int money;
        private int itemId;
        private int itemCount;

        public DropPickUpMessageInfo(DropPickUpMessageType type) {
            this.type = type;
        }

        public DropPickUpMessageType getType() {
            return type;
        }

        public boolean isPortionNotFound() {
            return portionNotFound;
        }

        public void setPortionNotFound(boolean portionNotFound) {
            this.portionNotFound = portionNotFound;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }
    }
}
