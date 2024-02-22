package kinoko.provider.quest.check;

import kinoko.provider.quest.QuestMobData;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.user.User;

import java.util.Collections;
import java.util.List;

public final class QuestMobCheck implements QuestCheck {
    private final List<QuestMobData> mobs;

    public QuestMobCheck(List<QuestMobData> mobs) {
        this.mobs = mobs;
    }

    @Override
    public boolean check(User user) {
        return false;
    }

    public static QuestMobCheck from(WzListProperty mobList) {
        final List<QuestMobData> mobs = QuestMobData.resolveMobData(mobList);
        return new QuestMobCheck(
                Collections.unmodifiableList(mobs)
        );
    }
}
