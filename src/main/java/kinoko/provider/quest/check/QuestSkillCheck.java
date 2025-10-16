package kinoko.provider.quest.check;

import kinoko.meta.SkillId;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class QuestSkillCheck implements QuestCheck {
    private final Map<SkillId, Boolean> skills;

    public QuestSkillCheck(Map<SkillId, Boolean> skills) {
        this.skills = skills;
    }

    @Override
    public boolean check(User user) {
        for (var entry : skills.entrySet()) {
            // IsSkillVisible != bAcquire
            if (user.getSkillManager().getSkill(entry.getKey()).isPresent() != entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public static QuestSkillCheck from(WzProperty skillList) throws ProviderError {
        final Map<SkillId, Boolean> skills = new HashMap<>();
        for (var skillEntry : skillList.getItems().entrySet()) {
            if (!(skillEntry.getValue() instanceof WzProperty skillProp)) {
                throw new ProviderError("Failed to resolve quest skill prop");
            }
            final SkillId skillId = SkillId.fromValue(WzProvider.getInteger(skillProp.get("id")));
            final boolean acquire = WzProvider.getInteger(skillProp.get("acquire"), 0) != 0;
            skills.put(skillId, acquire);
        }
        return new QuestSkillCheck(
                Collections.unmodifiableMap(skills)
        );
    }
}
