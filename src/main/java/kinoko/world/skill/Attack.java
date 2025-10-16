package kinoko.world.skill;

import kinoko.meta.SkillId;
import kinoko.server.header.OutHeader;
import kinoko.world.job.resistance.BattleMage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Attack {
    private final List<AttackInfo> attackInfo = new ArrayList<>();
    private final OutHeader headerType;

    public byte mask;
    public byte flag;
    public SkillId skillId;
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
    public int[] drops;
    public int dropExplodeDelay;

    public int crc;

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

    public boolean isShootAttack() {
        return getHeaderType() == OutHeader.UserShootAttack;
    }

    public boolean isMagicAttack() {
        /*
                    case BattleMage.TRIPLE_BLOW:
            case BattleMage.QUAD_BLOW:
            case BattleMage.QUINTUPLE_BLOW:
            case BattleMage.FINISHING_BLOW:
            case BattleMage.THE_FINISHER_STANDALONE:
            case BattleMage.THE_FINISHER_TRIPLE_BLOW:
            case BattleMage.THE_FINISHER_QUAD_BLOW:
            case BattleMage.THE_FINISHER_QUINTUPLE_BLOW:
            case BattleMage.THE_FINISHER_FINISHING_BLOW:
         */

        //TODO
        return switch (skillId) {
            case SkillId.TRIPLE_BLOW, SkillId.QUAD_BLOW, SkillId.QUINTUPLE_BLOW, SkillId.FINISHING_BLOW,
                 SkillId.THE_FINISHER -> true;
            default -> getHeaderType() == OutHeader.UserMagicAttack;
        };
    }

    @Override
    public String toString() {
        return "Attack{" +
                "attackInfo=" + attackInfo +
                ", headerType=" + headerType +
                ", mask=" + mask +
                ", flag=" + flag +
                ", skillId=" + skillId +
                ", slv=" + slv +
                ", combatOrders=" + combatOrders +
                ", keyDown=" + keyDown +
                ", exJablin=" + exJablin +
                ", actionAndDir=" + actionAndDir +
                ", attackSpeed=" + attackSpeed +
                ", mastery=" + mastery +
                ", bulletPosition=" + bulletPosition +
                ", bulletItemId=" + bulletItemId +
                ", userX=" + userX +
                ", userY=" + userY +
                ", ballStartX=" + ballStartX +
                ", ballStartY=" + ballStartY +
                ", grenadeX=" + grenadeX +
                ", grenadeY=" + grenadeY +
                ", dragonX=" + dragonX +
                ", dragonY=" + dragonY +
                ", passiveSlv=" + passiveSlv +
                ", passiveSkillId=" + passiveSkillId +
                ", swallowMobTemplateId=" + swallowMobTemplateId +
                ", drops=" + Arrays.toString(drops) +
                ", dropExplodeDelay=" + dropExplodeDelay +
                ", crc=" + crc +
                '}';
    }
}
