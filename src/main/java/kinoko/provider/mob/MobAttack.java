package kinoko.provider.mob;

public final class MobAttack {
    private final int skillId;
    private final int level;
    private final int conMp;

    public MobAttack(int skillId, int level, int conMp) {
        this.skillId = skillId;
        this.level = level;
        this.conMp = conMp;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getLevel() {
        return level;
    }

    public int getConMp() {
        return conMp;
    }
}
