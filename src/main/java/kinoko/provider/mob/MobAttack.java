package kinoko.provider.mob;

public final class MobAttack {
    private final int skillId;
    private final int skillLevel;
    private final int conMp;
    private final boolean magic;

    public MobAttack(int skillId, int skillLevel, int conMp, boolean magic) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.conMp = conMp;
        this.magic = magic;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getConMp() {
        return conMp;
    }

    public boolean isMagic() {
        return magic;
    }
}
