package kinoko.provider.quest.act;

import kinoko.packet.world.WvsContext;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.world.job.JobConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;

public final class QuestSpAct implements QuestAct {
    private final int job;
    private final int sp;

    public QuestSpAct(int job, int sp) {
        this.job = job;
        this.sp = sp;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        return JobConstants.getSkillRootFromJob(locked.get().getJob()).contains(job);
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        final CharacterStat cs = user.getCharacterStat();
        if (JobConstants.isExtendSpJob(cs.getJob())) {
            cs.getSp().addSp(JobConstants.getJobLevel(job), sp);
            user.write(WvsContext.statChanged(Stat.SP, cs.getSp(), false));
        } else {
            cs.getSp().addNonExtendSp(sp);
            user.write(WvsContext.statChanged(Stat.SP, (short) cs.getSp().getNonExtendSp(), false));
        }
        return true;
    }

    public static QuestSpAct from(WzListProperty spList) throws ProviderError {
        if (spList.getItems().size() != 1 || !(spList.get("0") instanceof WzListProperty spProp)) {
            throw new ProviderError("Failed to resolve quest sp act data");
        }
        if (!(spProp.get("job") instanceof WzListProperty jobProp) || jobProp.getItems().size() != 1) {
            throw new ProviderError("Failed to resolve quest sp act data");
        }
        return new QuestSpAct(
                WzProvider.getInteger(jobProp.get("0")),
                WzProvider.getInteger(spProp.get("sp_value"))
        );
    }
}
