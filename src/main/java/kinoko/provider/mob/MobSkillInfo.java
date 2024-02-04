package kinoko.provider.mob;

public final class MobSkillInfo {
    private final MobSkillType type;
    private final int level;

    public MobSkillInfo(MobSkillType type, int level) {
        this.type = type;
        this.level = level;
    }

    public MobSkillType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }
}
