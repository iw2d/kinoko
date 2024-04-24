package kinoko.world.social.party;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObjectImpl;
import kinoko.world.job.explorer.Magician;

import java.time.Instant;

public final class TownPortal extends FieldObjectImpl implements Encodable {
    public static final TownPortal EMPTY_PORTAL = new TownPortal(0, 0, 0, 0, 0);
    private final int townId;
    private final int fieldId;
    private final int skillId;
    private Instant expireTime = Instant.MIN;

    public TownPortal(int townId, int fieldId, int skillId, int x, int y) {
        this.townId = townId;
        this.fieldId = fieldId;
        this.skillId = skillId;
        setX(x);
        setY(y);
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }

    public void encodeForPartyData(OutPacket outPacket) {
        // PARTYDATA::TOWNPORTAL struct (20)
        outPacket.encodeInt(townId); // dwTownID
        outPacket.encodeInt(fieldId); // dwFieldID
        outPacket.encodeInt(skillId); // dwSkillID
        outPacket.encodeInt(getX()); // tagPOINT->x
        outPacket.encodeInt(getY()); // tagPOINT->y
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(townId); // dwTownID
        outPacket.encodeInt(fieldId); // dwFieldID
        outPacket.encodeInt(skillId); // dwSkillID
        outPacket.encodeShort(getX()); // tagPOINT->x
        outPacket.encodeShort(getY()); // tagPOINT->y
    }

    public static TownPortal decode(InPacket inPacket) {
        final int townId = inPacket.decodeInt();
        final int fieldId = inPacket.decodeInt();
        final int skillId = inPacket.decodeInt();
        final int x = inPacket.decodeShort();
        final int y = inPacket.decodeShort();
        return new TownPortal(townId, fieldId, skillId, x, y);
    }

    public static TownPortal from(Field field, int x, int y) {
        final TownPortal townPortal = new TownPortal(field.getReturnMap(), field.getFieldId(), Magician.MYSTIC_DOOR, x, y);
        townPortal.setField(field); // for removing on expiry / logout
        return townPortal;
    }
}
