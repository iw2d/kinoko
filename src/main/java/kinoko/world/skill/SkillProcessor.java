package kinoko.world.skill;

import kinoko.packet.user.UserRemote;
import kinoko.world.user.User;

public final class SkillProcessor {
    public static void processAttack(User user, Attack attack) {
        // TODO: set attack.slv
        user.getField().broadcastPacket(UserRemote.attack(user, attack), user);
    }

    public static void processSkill(User user, Skill skill) {

    }
}
