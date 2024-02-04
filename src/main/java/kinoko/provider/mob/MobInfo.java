package kinoko.provider.mob;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.ArrayList;
import java.util.List;

public record MobInfo(int templateId, int level, int maxHP, int maxMP, List<MobSkillInfo> skills) {
    public static MobInfo from(int mobId, WzListProperty infoProp) throws ProviderError {
        int level = 0;
        int maxHP = 0;
        int maxMP = 0;
        List<MobSkillInfo> skills = List.of();
        for (var entry : infoProp.getItems().entrySet()) {
            switch (entry.getKey()) {
                case "level" -> {
                    level = WzProvider.getInteger(entry.getValue());
                }
                case "maxHP" -> {
                    maxHP = WzProvider.getInteger(entry.getValue());
                }
                case "maxMP" -> {
                    maxMP = WzProvider.getInteger(entry.getValue());
                }
                case "skill" -> {
                    if (!(entry.getValue() instanceof WzListProperty skillEntries)) {
                        throw new ProviderError("Failed to resolve mob skills for mob : {}", mobId);
                    }
                    skills = new ArrayList<>();
                    for (var skillEntry : skillEntries.getItems().entrySet()) {
                        if (!(skillEntry.getValue() instanceof WzListProperty skillProp)) {
                            throw new ProviderError("Failed to resolve mob skills for mob : {}", mobId);
                        }
                        final int skillId = WzProvider.getInteger(skillProp.get("skill"));
                        final MobSkillType type = MobSkillType.getByValue(skillId);
                        if (type == null) {
                            throw new ProviderError("Failed to resolve mob skill : {}", skillId);
                        }
                        skills.add(new MobSkillInfo(
                                type,
                                WzProvider.getInteger(skillProp.get("level"))
                        ));
                    }
                }
                default -> {
                    // System.err.printf("Unhandled info %s in mob %d%n", entry.getKey(), mobId);
                }
            }
        }
        return new MobInfo(mobId, level, maxHP, maxMP, skills);
    }
}
