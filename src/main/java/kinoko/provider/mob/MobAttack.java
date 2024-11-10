package kinoko.provider.mob;

public final class MobAttack {
    private final int skillId;
    private final int skillLevel;
    private final int conMp;
    private final int mpBurn;
    private final boolean magic;
    private final boolean deadlyAttack;

    public MobAttack(int skillId, int skillLevel, int conMp, int mpBurn, boolean magic, boolean deadlyAttack) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.conMp = conMp;
        this.mpBurn = mpBurn;
        this.magic = magic;
        this.deadlyAttack = deadlyAttack;
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

    public int getMpBurn() {
        return mpBurn;
    }

    public boolean isMagic() {
        return magic;
    }

    public boolean isDeadlyAttack() {
        return deadlyAttack;
    }
}
