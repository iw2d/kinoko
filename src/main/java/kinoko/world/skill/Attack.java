package kinoko.world.skill;

import kinoko.server.header.OutHeader;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class Attack {
    private final List<AttackInfo> attackInfo = new ArrayList<>();
    private final OutHeader headerType;

    public byte mask;
    public byte flag;
    public int skillId;
    public int slv;
    public byte combatOrders;
    public int keyDown;
    public byte exJablin;
    public short actionAndDir;
    public byte attackSpeed;
    public byte mastery;
    public short bulletPosition;
    public int bulletItemId;

    public short userX;
    public short userY;
    public short ballStartX;
    public short ballStartY;
    public short grenadeX;
    public short grenadeY;
    public short dragonX;
    public short dragonY;

    public int passiveSlv;
    public int passiveSkillId;
    public int swallowMobTemplateId;

    public Attack(OutHeader headerType) {
        this.headerType = headerType;
    }

    public List<AttackInfo> getAttackInfo() {
        return attackInfo;
    }

    public OutHeader getHeaderType() {
        return headerType;
    }

    public int getDamagePerMob() {
        return mask & 0xF;
    }

    public int getMobCount() {
        return (mask >> 4) & 0xF;
    }

    public boolean isFinalAfterSlashBlast() {
        return (flag & 1) != 0;
    }

    public boolean isSoulArrow() {
        return (flag & 0x2) != 0;
    }

    public boolean isMortalBlow() {
        return (flag & 0x4) != 0;
    }

    public boolean isShadowPartner() {
        return (flag & 0x8) != 0;
    }

    public boolean isSerialAttackSkill() {
        return (flag & 0x20) != 0;
    }

    public boolean isSpiritJavelin() {
        return (flag & 0x40) != 0;
    }

    public boolean isSpark() {
        return (flag & 0x80) != 0;
    }

    public int getAction() {
        return actionAndDir & 0x7FFF;
    }

    public boolean isLeft() {
        return (actionAndDir & 0x8000) != 0;
    }


    public void forEachMob(Field field, Consumer<Mob> consumer) {
        for (AttackInfo ai : getAttackInfo()) {
            final Optional<Mob> mobResult = field.getMobPool().getById(ai.mobId);
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
