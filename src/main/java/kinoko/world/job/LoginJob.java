package kinoko.world.job;

import java.util.Arrays;
import java.util.Optional;

public enum LoginJob {
    RESISTANCE(0, Job.CITIZEN),
    EXPLORER(1, Job.BEGINNER),
    CYGNUS(2, Job.NOBLESSE),
    ARAN(3, Job.ARAN_BEGINNER),
    EVAN(4, Job.EVEN_BEGINNER);

    private final int race;
    private final Job job;

    LoginJob(int race, Job job) {
        this.race = race;
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public static Optional<LoginJob> getByRace(int race) {
        return Arrays.stream(values()).filter(j -> j.race == race).findFirst();
    }
}
