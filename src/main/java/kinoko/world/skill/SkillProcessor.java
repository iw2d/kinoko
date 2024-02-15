package kinoko.world.skill;

import kinoko.packet.user.UserRemotePacket;
import kinoko.world.user.User;

public final class SkillProcessor {
    public static void processAttack(User user, Attack attack) {
        // TODO: set attack.slv
        user.getField().broadcastPacket(UserRemotePacket.userAttack(user, attack), user);
    }

    public static void processSkill(User user, Skill skill) {

    }
}
