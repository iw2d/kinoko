package kinoko.server.node;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;
import kinoko.world.field.TownPortal;

public final class RemoteTownPortal implements Encodable {
    public static final RemoteTownPortal EMPTY = new RemoteTownPortal(GameConstants.UNDEFINED_FIELD_ID, GameConstants.UNDEFINED_FIELD_ID, 0, 0, 0);
    private final int townId;
    private final int fieldId;
    private final int skillId;
    private final int x;
    private final int y;

    public RemoteTownPortal(int townId, int fieldId, int skillId, int x, int y) {
        this.townId = townId;
        this.fieldId = fieldId;
        this.skillId = skillId;
        this.x = x;
        this.y = y;
    }

    public int getTownId() {
        return townId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void encodeForPartyResult(OutPacket outPacket) {
        outPacket.encodeInt(getTownId());
        outPacket.encodeInt(getFieldId());
        outPacket.encodeInt(getSkillId());
        outPacket.encodeShort(getX());
        outPacket.encodeShort(getY());
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getTownId());
        outPacket.encodeInt(getFieldId());
        outPacket.encodeInt(getSkillId());
        outPacket.encodeInt(getX());
        outPacket.encodeInt(getY());
    }

    public static RemoteTownPortal decode(InPacket inPacket) {
        final int townId = inPacket.decodeInt();
        final int fieldId = inPacket.decodeInt();
        final int skillId = inPacket.decodeInt();
        final int x = inPacket.decodeInt();
        final int y = inPacket.decodeInt();
        return new RemoteTownPortal(townId, fieldId, skillId, x, y);
    }

    public static RemoteTownPortal from(TownPortal townPortal) {
        return new RemoteTownPortal(
                townPortal.getTownField().getFieldId(),
                townPortal.getField().getFieldId(),
                townPortal.getSkillId(),
                townPortal.getX(),
                townPortal.getY()
        );
    }
}
