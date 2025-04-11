package kinoko.provider.quest.check;

import kinoko.world.user.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class QuestDateCheck implements QuestCheck {
    private final Instant date;
    private final boolean isStart;

    public QuestDateCheck(Instant date, boolean isStart) {
        this.date = date;
        this.isStart = isStart;
    }

    @Override
    public boolean check(User user) {
        if (isStart) {
            return Instant.now().isAfter(date);
        } else {
            return Instant.now().isBefore(date);
        }
    }

    public static QuestDateCheck from(String dateString, boolean isStart) {
        final int year = Integer.parseInt(dateString.substring(0, 4));
        final int month = Integer.parseInt(dateString.substring(4, 6));
        final int day = Integer.parseInt(dateString.substring(6, 8));
        final int hour = Integer.parseInt(dateString.substring(8, 10));
        final int minute = dateString.length() >= 12 ? Integer.parseInt(dateString.substring(10, 12)) : 0;
        final ZonedDateTime dateTime = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.of("UTC"));
        return new QuestDateCheck(dateTime.toInstant(), isStart);
    }
}
