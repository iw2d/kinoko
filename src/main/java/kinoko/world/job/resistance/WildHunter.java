package kinoko.world.job.resistance;

import kinoko.world.skill.Attack;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;

public final class WildHunter extends SkillProcessor {
    // WILD_HUNTER_1
    public static final int TRIPLE_SHOT = 33001000;
    public static final int JAGUAR_RIDER = 33001001;
    public static final int JAG_JUMP = 33001002;
    public static final int CROSSBOW_BOOSTER = 33001003;
    // WILD_HUNTER_2
    public static final int CROSSBOW_MASTERY = 33100000;
    public static final int FINAL_ATTACK = 33100009;
    public static final int RICOCHET = 33101001;
    public static final int JAGUAR_RAWR = 33101002;
    public static final int SOUL_ARROW_WH = 33101003;
    public static final int ITS_RAINING_MINES = 33101004;
    public static final int JAGUAR_OSHI = 33101005;
    public static final int JAGUAR_OSHI_DIGESTED = 33101006;
    public static final int JAGUAR_OSHI_ATTACK = 33101007;
    public static final int ITS_RAINING_MINES_HIDDEN = 33101008;
    // WILD_HUNTER_3
    public static final int JAGUAR_BOOST = 33110000;
    public static final int ENDURING_FIRE = 33111001;
    public static final int DASH_N_SLASH = 33111002;
    public static final int WILD_TRAP = 33111003;
    public static final int BLIND = 33111004;
    public static final int SILVER_HAWK = 33111005;
    public static final int SWIPE = 33111006;
    // WILD_HUNTER_4
    public static final int CROSSBOW_EXPERT = 33120000;
    public static final int WILD_INSTINCT = 33120010;
    public static final int EXPLODING_ARROWS = 33121001;
    public static final int SONIC_ROAR = 33121002;
    public static final int SHARP_EYES_WH = 33121004;
    public static final int STINK_BOMB_SHOT = 33121005;
    public static final int FELINE_BERSERK = 33121006;
    public static final int MAPLE_WARRIOR_WH = 33121007;
    public static final int HEROS_WILL_WH = 33121008;
    public static final int WILD_ARROW_BLAST = 33121009;

    public static void handleAttack(User user, Attack attack) {
        final int skillId = attack.skillId;
        final int slv = attack.slv;
        switch (skillId) {
        }
    }

    public static void handleSkill(User user, Skill skill) {
        log.error("Unhandled skill {}", skill.skillId);
    }
}