package kinoko.world.skill;

public final class HitInfo {
    public final HitType hitType;
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

    public int missSkillId;

    public HitInfo(HitType hitType) {
        this.hitType = hitType;
    }
}