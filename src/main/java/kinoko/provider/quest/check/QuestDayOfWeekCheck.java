package kinoko.provider.quest.check;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.user.User;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class QuestDayOfWeekCheck implements QuestCheck {
    private final Set<DayOfWeek> allowed;

    public QuestDayOfWeekCheck(Set<DayOfWeek> allowed) {
        this.allowed = allowed;
    }

    @Override
    public boolean check(User user) {
        final DayOfWeek dayOfWeek = ZonedDateTime.now(ZoneId.of("UTC")).getDayOfWeek();
        return allowed.contains(dayOfWeek);
    }

    public static QuestDayOfWeekCheck from(WzListProperty dayOfWeekList) throws ProviderError {
        final Set<DayOfWeek> allowed = new HashSet<>();
        for (var entry : dayOfWeekList.getItems().entrySet()) {
            if (WzProvider.getInteger(entry.getValue()) == 0) {
                continue;
            }
            switch (entry.getKey()) {
                case "mon" -> allowed.add(DayOfWeek.MONDAY);
                case "tue" -> allowed.add(DayOfWeek.TUESDAY);
                case "wed" -> allowed.add(DayOfWeek.WEDNESDAY);
                case "thu" -> allowed.add(DayOfWeek.THURSDAY);
                case "fri" -> allowed.add(DayOfWeek.FRIDAY);
                case "sat" -> allowed.add(DayOfWeek.SATURDAY);
                case "sun" -> allowed.add(DayOfWeek.SUNDAY);
                default -> {
                    continue;
                }
            }
        }
        return new QuestDayOfWeekCheck(Collections.unmodifiableSet(allowed));
    }
}
