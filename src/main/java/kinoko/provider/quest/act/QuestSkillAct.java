package kinoko.provider.quest.act;

import kinoko.meta.SkillId;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.quest.QuestSkillData;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class QuestSkillAct implements QuestAct {
    private final List<QuestSkillData> skills;

    public QuestSkillAct(List<QuestSkillData> skills) {
        this.skills = skills;
    }

    public List<QuestSkillData> getSkills() {
        return skills;
    }

    @Override
    public boolean canAct(User user, int rewardIndex) {
        for (QuestSkillData qsd : skills) {
            if (!qsd.getJobs().contains(user.getJob())) {
                continue;
            }
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(qsd.getSkillId());
            if (skillInfoResult.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean doAct(User user, int rewardIndex) {
        for (QuestSkillData qsd : skills) {
            if (!qsd.getJobs().contains(user.getJob())) {
                continue;
            }
            final SkillId skillId = qsd.getSkillId();
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                return false;
            }
            final SkillRecord skillRecord = new SkillRecord(skillId);
            skillRecord.setSkillLevel(qsd.isOnlyMasterLevel() ? user.getSkillLevel(skillId) : qsd.getSkillLevel());
            skillRecord.setMasterLevel(SkillConstants.isSkillNeedMasterLevel(skillId) ? qsd.getMasterLevel() : 0);
            user.getSkillManager().addSkill(skillRecord);
            user.updatePassiveSkillData();
            user.validateStat();
            user.write(WvsContext.changeSkillRecordResult(skillRecord, false));
        }
        return true;
    }

    public static QuestSkillAct from(WzProperty skillList) {
        final List<QuestSkillData> skills = QuestSkillData.resolveSkillData(skillList);
        return new QuestSkillAct(
                Collections.unmodifiableList(skills)
        );
    }
}
