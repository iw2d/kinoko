package kinoko.provider.quest.check;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Locked;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class QuestJobCheck implements QuestCheck {
    private final Set<Integer> jobs;

    public QuestJobCheck(Set<Integer> jobs) {
        this.jobs = jobs;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final int jobId = locked.get().getJob();
        return jobs.contains(jobId);
    }

    public static QuestJobCheck from(WzListProperty jobList) {
        final Set<Integer> jobs = new HashSet<>();
        for (var jobEntry : jobList.getItems().entrySet()) {
            jobs.add(WzProvider.getInteger(jobEntry.getValue()));
        }
        return new QuestJobCheck(
                Collections.unmodifiableSet(jobs)
        );
    }
}
