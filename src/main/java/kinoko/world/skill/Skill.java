package kinoko.world.skill;

import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.user.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public final class Skill {
    public int skillId;
    public int slv;

    public int positionX;
    public int positionY;

    public int affectedMemberBitMap = Byte.MIN_VALUE; // default when nPartyID = 0 (CUserLocal::FindParty)

    public int[] mobIds;

    public int captureTargetMobId;
    public int randomCapturedMobId;
    public int spiritJavelinItemId;
    public int rockAndShockCount;
    public int rockAndShock1;
    public int rockAndShock2;

    public boolean left;
    public boolean summonLeft;
    public byte summonBuffType;

    // UserThrowGrenade
    public int keyDown;

    public int getAffectedMemberCount() {
        if (affectedMemberBitMap == Byte.MIN_VALUE) {
            return 1;
        }
        return Integer.bitCount(affectedMemberBitMap);
    }

    public void forEachAffectedMember(User caster, Field field, Consumer<User> consumer) {
        if (affectedMemberBitMap == Byte.MIN_VALUE) {
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
        if (mobIds == null) {
            return;
        }
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

    @Override
    public String toString() {
        return "Skill{" +
                "skillId=" + skillId +
                ", slv=" + slv +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", affectedMemberBitMap=" + affectedMemberBitMap +
                ", mobIds=" + Arrays.toString(mobIds) +
                ", captureTargetMobId=" + captureTargetMobId +
                ", randomCapturedMobId=" + randomCapturedMobId +
                ", spiritJavelinItemId=" + spiritJavelinItemId +
                ", rockAndShockCount=" + rockAndShockCount +
                ", rockAndShock1=" + rockAndShock1 +
                ", rockAndShock2=" + rockAndShock2 +
                ", left=" + left +
                ", summonLeft=" + summonLeft +
                ", summonBuffType=" + summonBuffType +
                ", keyDown=" + keyDown +
                '}';
    }
}
