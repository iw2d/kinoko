package kinoko.provider.quest.act;

import kinoko.packet.user.QuestPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
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
    public boolean canAct(User user, int rewardIndex) {
        if (!JobConstants.getSkillRootFromJob(user.getJob()).contains(job)) {
            user.write(QuestPacket.failedUnknown());
            return false;
        }
        return true;
    }

    @Override
    public boolean doAct(User user, int rewardIndex) {
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

    public static QuestSpAct from(WzProperty spList) throws ProviderError {
        if (spList.getItems().size() != 1 || !(spList.get("0") instanceof WzProperty spProp)) {
            throw new ProviderError("Failed to resolve quest sp act data");
        }
        if (!(spProp.get("job") instanceof WzProperty jobProp) || jobProp.getItems().size() != 1) {
            throw new ProviderError("Failed to resolve quest sp act data");
        }
        return new QuestSpAct(
                WzProvider.getInteger(jobProp.get("0")),
                WzProvider.getInteger(spProp.get("sp_value"))
        );
    }
}
