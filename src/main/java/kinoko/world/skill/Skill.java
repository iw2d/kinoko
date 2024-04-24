package kinoko.world.skill;

import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.user.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class Skill {
    public int skillId;
    public int slv;

    public int positionX;
    public int positionY;

    public int affectedMemberBitMap = 0x80; // default when nPartyID = 0 (CUserLocal::FindParty)

    public int mobCount;
    public List<Integer> mobIds;

    public int captureTargetMobId;
    public int randomCapturedMobId;
    public int spiritJavelinItemId;
    public int rockAndShockCount;
    public int rockAndShock1;
    public int rockAndShock2;

    public boolean left;
    public boolean summonLeft;

    public void forEachAffectedMember(User caster, Field field, Consumer<User> consumer) {
        if (affectedMemberBitMap == 0x80) {
            return;
        }
        field.getUserPool().forEachPartyMember(caster, (member) -> {
            final int indexBit = 1 << (6 - member.getPartyMemberIndex());
            if ((affectedMemberBitMap & indexBit) != 0) {
                consumer.accept(member);
            }
        });
    }

    public void forEachAffectedMob(Field field, Consumer<Mob> consumer) {
        for (int mobId : mobIds) {
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
