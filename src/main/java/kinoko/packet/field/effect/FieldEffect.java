package kinoko.packet.field.effect;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class FieldEffect implements Encodable {
    private final FieldEffectType type;
    private int int1;
    private int int2;
    private int int3;
    private int int4;
    private int int5;
    private String string1;

    public FieldEffect(FieldEffectType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case SUMMON -> {
                outPacket.encodeByte(int1); // nType
                outPacket.encodeInt(int2); // x
                outPacket.encodeInt(int3); // y
            }
            case TREMBLE -> {
                outPacket.encodeByte(int1); // bHeavyNShortTremble
                outPacket.encodeInt(int2); // tDelay
            }
            case OBJECT, SCREEN, SOUND -> {
                outPacket.encodeString(string1); // sName
            }
            case MOB_HP_TAG -> {
                outPacket.encodeInt(int1); // dwMobID
                outPacket.encodeInt(int2); // nHP
                outPacket.encodeInt(int3); // nMaxHP
                outPacket.encodeInt(int4); // nColor
                outPacket.encodeInt(int5); // nBgColor
            }
            case CHANGE_BGM -> {
                outPacket.encodeString(string1); // BGM UOL
            }
            case REWORD_RULLET -> {
                outPacket.encodeInt(int1); // nRewardJobIdx
                outPacket.encodeInt(int2); // nRewardPartIdx
                outPacket.encodeInt(int3); // nRewardLevIdx
            }
        }
    }

    public static FieldEffect screen(String effectPath) {
        final FieldEffect fieldEffect = new FieldEffect(FieldEffectType.SCREEN);
        fieldEffect.string1 = effectPath; // Map.wz/Effect.img/%s
        return fieldEffect;
    }
}
