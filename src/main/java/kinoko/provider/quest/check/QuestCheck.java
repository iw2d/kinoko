package kinoko.provider.quest.check;

import kinoko.util.Locked;
import kinoko.world.user.User;

public interface QuestCheck {
    boolean check(Locked<User> locked);
}
