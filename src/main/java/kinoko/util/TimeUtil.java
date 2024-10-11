package kinoko.util;

import kinoko.server.ServerConfig;

import java.time.*;

public final class TimeUtil {
    public static Instant getCurrentTime() {
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of(ServerConfig.TIME_ZONE);
        ZoneOffset offset = zoneId.getRules().getOffset(now);
        return now.plusSeconds(offset.getTotalSeconds());
    }

    public static long getSecondsToNextHour() {
        Instant now = getCurrentTime();
        ZonedDateTime zNow = ZonedDateTime.from(getCurrentTime());
        int currentHour = zNow.getHour();

        int nextHour = currentHour + 1;
        if (nextHour == 24) {
            nextHour = 0;
        }
        ZonedDateTime nextHourStart = zNow.withHour(nextHour).withMinute(0).withSecond(0).withNano(0);
        Instant nextHourInstant = nextHourStart.toInstant();

        return Duration.between(now, nextHourInstant).toSeconds();
    }

    public static int getDayOfWeek() {
        return getDayOfWeek(0);
    }

    public static int getDayOfWeek(long delaySeconds) {
        Instant delayNow = getCurrentTime().plusSeconds(delaySeconds);
        return ZonedDateTime.from(delayNow).getDayOfWeek().getValue();
    }

    public static int getHour() {
        return getHour(0);
    }

    public static int getHour(long delaySeconds) {
        Instant delayNow = getCurrentTime().plusSeconds(delaySeconds);
        return ZonedDateTime.from(delayNow).getHour();
    }
}
