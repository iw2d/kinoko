package kinoko.world.skill;

import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;

import java.util.Optional;
import java.util.function.Consumer;

public final class Skill {
    public int skillId;
    public int slv;

    public int positionX;
    public int positionY;

    public int spiritJavelinItemId;
    public int randomCapturedMobId;
    public int rockAndShockCount;
    public int rockAndShockId1;
    public int rockAndShockId2;


    public static void forEachMob(InPacket inPacket, Field field, Consumer<Mob> consumer) {
        final int mobCount = inPacket.decodeByte(); // nMobCount
        for (int i = 0; i < mobCount; i++) {
            final int mobId = inPacket.decodeInt();
            final Optional<Mob> mobResult = field.getMobPool().getById(mobId);
            if (mobResult.isEmpty()) {
                continue;
            }
            try (var lockedMob = mobResult.get().acquire()) {
                final Mob mob = lockedMob.get();
                if (mob.getHp() > 0) {
                    consumer.accept(mob);
                }
            }
        }
    }
}
