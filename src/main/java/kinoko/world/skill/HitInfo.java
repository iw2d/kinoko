package kinoko.world.skill;

public final class HitInfo {
    public AttackIndex attackIndex;
    public int damage;
    public short obstacleData;
    public short diseaseData;
    public byte diseaseType;

    public byte magicElemAttr;
    public int templateId;
    public int mobId;
    public byte dir;
    public byte reflect;
    public byte guard;
    public byte powerGuard;
    public int reflectMobId;
    public byte reflectMobAction;
    public short reflectMobX;
    public short reflectMobY;
    public byte stance;

    public int finalDamage;
    public int missSkillId;

    @Override
    public String toString() {
        return "HitInfo{" +
                "attackIndex=" + attackIndex +
                ", damage=" + damage +
                ", obstacleData=" + obstacleData +
                ", diseaseData=" + diseaseData +
                ", diseaseType=" + diseaseType +
                ", magicElemAttr=" + magicElemAttr +
                ", templateId=" + templateId +
                ", mobId=" + mobId +
                ", dir=" + dir +
                ", reflect=" + reflect +
                ", guard=" + guard +
                ", powerGuard=" + powerGuard +
                ", reflectMobId=" + reflectMobId +
                ", reflectMobAction=" + reflectMobAction +
                ", reflectMobX=" + reflectMobX +
                ", reflectMobY=" + reflectMobY +
                ", stance=" + stance +
                ", finalDamage=" + finalDamage +
                ", missSkillId=" + missSkillId +
                '}';
    }
}