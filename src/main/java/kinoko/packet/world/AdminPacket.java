package kinoko.packet.world;

import kinoko.server.Server;
import kinoko.server.family.FamilyEntitlement;
import kinoko.server.family.FamilyResultType;
import kinoko.server.family.FamilyTree;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.FamilyMember;
import kinoko.world.user.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public final class AdminPacket {
    public static OutPacket getAdminEffect(int type, byte mode) {
        final OutPacket outPacket = OutPacket.of(OutHeader.AdminResult);

        outPacket.encodeByte(type);
        outPacket.encodeByte(mode);

        return outPacket;
    }

}
