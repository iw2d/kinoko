package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class FieldEffectPacket {
    // CField::OnFieldEffect -------------------------------------------------------------------------------------------

    public static OutPacket summon(int summonType, int x, int y) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.Summmon.getValue());
        outPacket.encodeByte(summonType);
        outPacket.encodeInt(x);
        outPacket.encodeInt(y);
        return outPacket;
    }

    public static OutPacket tremble(boolean isHeavyAndShort, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.Tremble.getValue());
        outPacket.encodeByte(isHeavyAndShort); // bHeavyNShortTremble
        outPacket.encodeInt(delay); // tDelay
        return outPacket;
    }

    public static OutPacket object(String name) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.Object.getValue());
        outPacket.encodeString(name); // sName -> CMapLoadable::SetObjectState
        return outPacket;
    }

    public static OutPacket screen(String name) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.Screen.getValue());
        outPacket.encodeString(name); // sName -> CField::ShowScreenEffect
        return outPacket;
    }

    public static OutPacket sound(String name) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.Sound.getValue());
        outPacket.encodeString(name); // sName -> play_field_sound
        return outPacket;
    }

    public static OutPacket mobHpTag(int mobId, int hp, int maxHp, int color, int bgColor) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.MobHPTag.getValue());
        outPacket.encodeInt(mobId); // dwMobID
        outPacket.encodeInt(hp); // nHP
        outPacket.encodeInt(maxHp); // nMaxHP
        outPacket.encodeByte(color); // nColor
        outPacket.encodeByte(bgColor); // nBgColor
        return outPacket;
    }

    public static OutPacket changeBgm(String uol) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.ChangeBGM.getValue());
        outPacket.encodeString(uol); // sUOL
        return outPacket;
    }

    public static OutPacket rewardRoulette(int jobIndex, int partIndex, int levIndex) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FieldEffect);
        outPacket.encodeByte(FieldEffectType.RewordRullet.getValue());
        outPacket.encodeInt(jobIndex); // nRewardJobIdx
        outPacket.encodeInt(partIndex); // nRewardPartIdx
        outPacket.encodeInt(levIndex); // nRewardLevIdx
        return outPacket;
    }
}
