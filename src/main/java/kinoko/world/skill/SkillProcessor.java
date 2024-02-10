package kinoko.world.skill;

import kinoko.packet.user.UserRemotePacket;
import kinoko.world.user.User;

public final class SkillProcessor {
    public static void handleActiveSkill(User user, Attack attack) {
        user.getField().broadcastPacket(UserRemotePacket.userAttack(user, attack));
    }

    public static void handleActiveSkill(User user) {

    }
}
