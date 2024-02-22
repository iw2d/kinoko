package kinoko.world.skill;

import kinoko.packet.user.UserRemote;
import kinoko.world.user.User;

public final class SkillProcessor {
    public static void processAttack(User user, Attack attack) {
        user.getField().broadcastPacket(UserRemote.attack(user, attack), user);
    }

    public static void processSkill(User user, Skill skill) {

    }

    public static void processHit(User user, HitInfo hitInfo) {
        // TODO: update stats
        user.getField().broadcastPacket(UserRemote.hit(user, hitInfo), user);
    }
}
