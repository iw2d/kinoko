package kinoko.packet.user.effect;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Item;

public class Effect implements Encodable {
    protected final EffectType type;
    private boolean bool1;
    private int int1;
    private int int2;
    private String string1;

    public Effect(EffectType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case LEVEL_UP, PLAY_PORTAL_SE, JOB_CHANGED, QUEST_COMPLETE, MOBSTER_BOOK_CARD_GET, ITEM_LEVEL_UP,
                    ITEM_MAKER, EXP_ITEM_CONSUMED, BUFF, SOUL_STONE_USE, REPEAT_EFFECT_REMOVE, EVOL_RING -> {
                // no encodes
            }
            case QUEST -> {
                outPacket.encodeByte(bool1); // byte > 0 ? byte * (item gain message) : screen message
                if (bool1) {
                    outPacket.encodeInt(int1); // nItemID
                    outPacket.encodeInt(int2); // quantity
                } else {
                    outPacket.encodeString(string1); // sStrMsg
                    outPacket.encodeInt(int1); // nEffect
                }
            }
            case PET -> {
                outPacket.encodeByte(int1); // nType
                outPacket.encodeByte(int2); // pet index
            }
            case PROTECT_ON_DIE_ITEM_USE -> {
                outPacket.encodeByte(bool1); // is safety charm
                if (bool1) {
                    outPacket.encodeByte(int1); // times left
                    outPacket.encodeByte(int2); // days left
                } else {
                    outPacket.encodeInt(int1); // nItemID
                }
            }
            case INC_DEC_HP_EFFECT -> {
                outPacket.encodeByte(int1); // nDelta
            }
            case BUFF_ITEM_EFFECT -> {
                outPacket.encodeInt(int1); // nItemID
            }
            case SQUIB_EFFECT -> {
                outPacket.encodeString(string1); // sEffect
            }
            case LOTTERY_USE -> {
                outPacket.encodeInt(int1); // nItemId
                outPacket.encodeByte(bool1); // bool
                if (bool1) {
                    outPacket.encodeString(string1); // sEffect
                }
            }
            case RESERVED_EFFECT -> {
                outPacket.encodeString(string1); // sEffect
            }
            case CONSUME_EFFECT -> {
                outPacket.encodeInt(int1); // nItemID (Item/Cash/0528.img/%d/effect)
            }
            case UPGRADE_TOMB_ITEM_USE -> {
                outPacket.encodeByte(int1); // number of wheels of destiny left
            }
            case BATTLEFIELD_ITEM_USE -> {
                outPacket.encodeString(string1); // sEffect
            }
            case AVATAR_ORIENTED -> {
                outPacket.encodeString(string1); // sEffect
            }
            case INCUBATOR_USE -> {
                outPacket.encodeInt(int1); // nItemId
                outPacket.encodeString(string1); // sEffect
            }
            case PLAY_SOUND_WITH_MUTE_BGM -> {
                outPacket.encodeString(string1); // sName
            }
            case INC_DEC_HP_EFFECT_EX -> {
                outPacket.encodeInt(int1); // nDelta
            }
            case DELIVERY_QUEST_ITEM_USE -> {
                outPacket.encodeInt(int1); // nItemId
            }
            default -> {
                throw new IllegalStateException("Tried to encode unsupported effect type");
            }
        }
    }

    public static Effect gainItem(Item item) {
        return gainItem(item.getItemId(), item.getQuantity());
    }

    public static Effect gainItem(int itemId, int quantity) {
        final Effect effect = new Effect(EffectType.QUEST);
        effect.bool1 = true; // item gain message
        effect.int1 = itemId;
        effect.int2 = quantity;
        return effect;
    }
}
