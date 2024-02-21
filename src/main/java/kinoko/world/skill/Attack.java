package kinoko.world.skill;

import kinoko.server.header.OutHeader;

import java.util.ArrayList;
import java.util.List;

public final class Attack {
    private final List<AttackInfo> attackInfo = new ArrayList<>();
    private final OutHeader headerType;

    public byte mask;
    public byte flag;
    public int skillId;
    public byte slv;
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
    public short grenadeX;
    public short grenadeY;
    public short dragonX;
    public short dragonY;

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
}
