package kinoko.world.social.party;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.field.FieldObjectImpl;

public final class TownPortal extends FieldObjectImpl implements Encodable {
    public static final TownPortal EMPTY_PORTAL = new TownPortal(0);
    private final int skillId;

    public TownPortal(int skillId) {
        this.skillId = skillId;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getReturnMap() {
        return getField() != null ? getField().getReturnMap() : 0;
    }

    public int getFieldId() {
        return getField() != null ? getField().getFieldId() : 0;
    }

    @Override
    public OutPacket enterFieldPacket() {
        return null;
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return null;
    }

    public void encodeForPartyData(OutPacket outPacket) {
        // PARTYDATA::TOWNPORTAL struct (20)
        outPacket.encodeInt(getReturnMap()); // dwTownID
        outPacket.encodeInt(getFieldId()); // dwFieldID
        outPacket.encodeInt(getSkillId()); // dwSkillID
        outPacket.encodeInt(getX()); // tagPOINT->x
        outPacket.encodeInt(getY()); // tagPOINT->y
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getReturnMap()); // dwTownID
        outPacket.encodeInt(getFieldId()); // dwFieldID
        outPacket.encodeInt(getSkillId()); // dwSkillID
        outPacket.encodeShort(getX()); // tagPOINT->x
        outPacket.encodeShort(getY()); // tagPOINT->y
    }
}
