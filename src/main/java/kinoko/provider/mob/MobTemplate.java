package kinoko.provider.mob;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.*;

public final class MobTemplate {
    private final int id;
    private final int level;
    private final int exp;
    private final int maxHp;
    private final int maxMp;
    private final int hpRecovery;
    private final int mpRecovery;
    private final int fixedDamage;
    private final int removeAfter;
    private final boolean boss;
    private final boolean noFlip;
    private final boolean damagedByMob;
    private final Map<Integer, MobAttack> attacks;
    private final Map<Integer, MobSkill> skills;
    private final List<Integer> revives;

    public MobTemplate(int id, int level, int exp, int maxHp, int maxMp, int hpRecovery, int mpRecovery,
                       int fixedDamage, int removeAfter, boolean boss, boolean noFlip, boolean damagedByMob,
                       Map<Integer, MobAttack> attacks, Map<Integer, MobSkill> skills, List<Integer> revives) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.maxHp = maxHp;
        this.maxMp = maxMp;
        this.hpRecovery = hpRecovery;
        this.mpRecovery = mpRecovery;
        this.fixedDamage = fixedDamage;
        this.removeAfter = removeAfter;
        this.boss = boss;
        this.noFlip = noFlip;
        this.damagedByMob = damagedByMob;
        this.attacks = attacks;
        this.skills = skills;
        this.revives = revives;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getHpRecovery() {
        return hpRecovery;
    }

    public int getMpRecovery() {
        return mpRecovery;
    }

    public int getFixedDamage() {
        return fixedDamage;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public boolean isBoss() {
        return boss;
    }

    public boolean isNoFlip() {
        return noFlip;
    }

    public boolean isDamagedByMob() {
        return damagedByMob;
    }

    public Map<Integer, MobAttack> getAttacks() {
        return attacks;
    }

    public Optional<MobAttack> getAttack(int attackIndex) {
        return Optional.ofNullable(getAttacks().get(attackIndex));
    }

    public Map<Integer, MobSkill> getSkills() {
        return skills;
    }

    public Optional<MobSkill> getSkill(int skillIndex) {
        return Optional.ofNullable(getSkills().get(skillIndex));
    }

    public List<Integer> getRevives() {
        return revives;
    }

    @Override
    public String toString() {
        return "MobTemplate{" +
                "id=" + id +
                ", level=" + level +
                ", exp=" + exp +
                ", maxHp=" + maxHp +
                ", maxMp=" + maxMp +
                ", hpRecovery=" + hpRecovery +
                ", mpRecovery=" + mpRecovery +
                ", fixedDamage=" + fixedDamage +
                ", boss=" + boss +
                ", noFlip=" + noFlip +
                ", damagedByMob=" + damagedByMob +
                ", attacks=" + attacks +
                ", skills=" + skills +
                ", revives=" + revives +
                '}';
    }

    public static MobTemplate from(int mobId, WzListProperty mobProp, WzListProperty infoProp) throws ProviderError {
        int level = 0;
        int exp = 0;
        int maxHP = 0;
        int maxMP = 0;
        int hpRecovery = 0;
        int mpRecovery = 0;
        int fixedDamage = 0;
        int removeAfter = 0;
        boolean boss = false;
        boolean noFlip = false;
        boolean damagedByMob = false;
        final Map<Integer, MobAttack> attacks = new HashMap<>();
        final Map<Integer, MobSkill> skills = new HashMap<>();
        final List<Integer> revives = new ArrayList<>();
        // Process attacks
        for (var entry : mobProp.getItems().entrySet()) {
            if (entry.getKey().startsWith("attack")) {
                final int attackIndex = Integer.parseInt(entry.getKey().replace("attack", "")) - 1;
                if (!(entry.getValue() instanceof WzListProperty attackProp) ||
                        !(attackProp.get("info") instanceof WzListProperty attackInfoProp)) {
                    throw new ProviderError("Failed to resolve attack info for mob : %d", mobId);
                }
                int skillId = 0;
                int skillLevel = 0;
                int conMp = 0;
                boolean magic = false;
                for (var attackInfoEntry : attackInfoProp.getItems().entrySet()) {
                    switch (attackInfoEntry.getKey()) {
                        case "disease" -> {
                            skillId = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "level" -> {
                            skillLevel = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "conMP" -> {
                            conMp = WzProvider.getInteger(attackInfoEntry.getValue());
                        }
                        case "magic" -> {
                            magic = WzProvider.getInteger(attackInfoEntry.getValue()) != 0;
                        }
                        default -> {
                            // System.err.printf("Unhandled mob attack info %s in mob %d%n", infoEntry.getKey(), mobId);
                        }
                    }
                }
                attacks.put(attackIndex, new MobAttack(
                        skillId,
                        skillLevel,
                        conMp,
                        magic
                ));
            }
        }
        // Process info
        for (var infoEntry : infoProp.getItems().entrySet()) {
            switch (infoEntry.getKey()) {
                case "level" -> {
                    level = WzProvider.getInteger(infoEntry.getValue());
                }
                case "exp" -> {
                    exp = WzProvider.getInteger(infoEntry.getValue());
                }
                case "maxHP" -> {
                    maxHP = WzProvider.getInteger(infoEntry.getValue());
                }
                case "maxMP" -> {
                    maxMP = WzProvider.getInteger(infoEntry.getValue());
                }
                case "hpRecovery" -> {
                    hpRecovery = WzProvider.getInteger(infoEntry.getValue());
                }
                case "mpRecovery" -> {
                    mpRecovery = WzProvider.getInteger(infoEntry.getValue());
                }
                case "fixedDamage" -> {
                    fixedDamage = WzProvider.getInteger(infoEntry.getValue());
                }
                case "removeAfter" -> {
                    removeAfter = WzProvider.getInteger(infoEntry.getValue());
                }
                case "boss" -> {
                    boss = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "noFlip" -> {
                    noFlip = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "damagedByMob" -> {
                    damagedByMob = WzProvider.getInteger(infoEntry.getValue()) != 0;
                }
                case "skill" -> {
                    if (!(infoEntry.getValue() instanceof WzListProperty skillEntries)) {
                        throw new ProviderError("Failed to resolve mob skills for mob : %d", mobId);
                    }
                    for (var skillEntry : skillEntries.getItems().entrySet()) {
                        final int skillIndex = Integer.parseInt(skillEntry.getKey());
                        if (!(skillEntry.getValue() instanceof WzListProperty skillProp)) {
                            throw new ProviderError("Failed to resolve mob skills for mob : %d", mobId);
                        }
                        final int skillId = WzProvider.getInteger(skillProp.get("skill"));
                        final MobSkillType skillType = MobSkillType.getByValue(skillId);
                        if (skillType == null) {
                            throw new ProviderError("Failed to resolve mob skill : %d", skillId);
                        }
                        skills.put(skillIndex, new MobSkill(
                                skillType,
                                skillId,
                                WzProvider.getInteger(skillProp.get("level"))
                        ));
                    }
                }
                case "revive" -> {
                    if (!(infoEntry.getValue() instanceof WzListProperty reviveList)) {
                        throw new ProviderError("Failed to resolve revives for mob : %d", mobId);
                    }
                    for (var reviveEntry : reviveList.getItems().entrySet()) {
                        final int reviveId = WzProvider.getInteger(reviveEntry.getValue());
                        revives.add(reviveId); // validate in MobProvider
                    }
                }
                default -> {
                    // System.err.printf("Unhandled info %s in mob %d%n", infoEntry.getKey(), mobId);
                }
            }
        }
        return new MobTemplate(
                mobId,
                level,
                exp,
                maxHP,
                maxMP,
                hpRecovery,
                mpRecovery,
                fixedDamage,
                removeAfter, boss,
                noFlip,
                damagedByMob,
                Collections.unmodifiableMap(attacks),
                Collections.unmodifiableMap(skills),
                Collections.unmodifiableList(revives)
        );
    }
}
