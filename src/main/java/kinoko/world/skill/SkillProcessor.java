package kinoko.world.skill;

import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.item.*;
import kinoko.world.job.JobHandler;
import kinoko.world.life.mob.Mob;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public final class SkillProcessor {
    private static final Logger log = LogManager.getLogger(SkillProcessor.class);

    public static void processAttack(User user, Attack attack) {
        // Resolve bullet id
        if (attack.bulletPosition != 0 && !attack.isSoulArrow() && !attack.isSpiritJavelin()) {
            final Item weaponItem = user.getInventoryManager().getInventoryByType(InventoryType.EQUIPPED).getItem(BodyPart.WEAPON.getValue());
            final Item bulletItem = user.getInventoryManager().getInventoryByType(InventoryType.CONSUME).getItem(attack.bulletPosition);
            if (weaponItem == null || bulletItem == null || !ItemConstants.isCorrectBulletItem(weaponItem.getItemId(), bulletItem.getItemId())) {
                log.error("Tried to attack with incorrect bullet {} using weapon {}", bulletItem != null ? bulletItem.getItemId() : 0, weaponItem != null ? weaponItem.getItemId() : 0);
                return;
            }
            attack.bulletItemId = bulletItem.getItemId();
            // Consume bullet for basic attack
            if (attack.skillId == 0) {
                final int bulletCount = attack.isShadowPartner() ? 2 : 1;
                if (bulletItem.getQuantity() < bulletCount) {
                    log.error("Tried to attack without enough bullets in position {}", attack.bulletPosition);
                    return;
                }
                bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCount));
                user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, attack.bulletPosition, bulletItem.getQuantity()), true));
            }
        }

        // Process skill
        if (attack.skillId != 0) {
            // Set skill level
            attack.slv = user.getSkillManager().getSkillLevel(attack.skillId);
            if (attack.slv == 0) {
                log.error("Tried to attack with skill {} not learned by user", attack.skillId);
                return;
            }
            // Resolve skill info
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(attack.skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("Failed to resolve skill info for skill : {}", attack.skillId);
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            // Check skill cooltime and cost
            final int cooltime = si.getValue(SkillStat.cooltime, attack.slv);
            if (cooltime > 0 && user.getSkillManager().hasSkillCooltime(attack.skillId)) {
                log.error("Tried to use skill {} that is still on cooltime", attack.skillId);
                return;
            }
            final int hpCon = si.getValue(SkillStat.hpCon, attack.slv);
            if (user.getHp() <= hpCon) {
                log.error("Tried to use skill {} without enough hp, current : {}, required : {}", attack.skillId, user.getHp(), hpCon);
                return;
            }
            final int mpCon = si.getValue(SkillStat.mpCon, attack.slv);
            if (user.getMp() < mpCon) {
                log.error("Tried to use skill {} without enough mp, current : {}, required : {}", attack.skillId, user.getMp(), mpCon);
                return;
            }
            final int bulletCon = si.getBulletCon(attack.slv);
            if (bulletCon > 0 && attack.bulletPosition != 0 && !attack.isSoulArrow() && !attack.isSpiritJavelin()) {
                // Resolve bullet item
                final int bulletCount = bulletCon * (attack.isShadowPartner() ? 2 : 1);
                final Item bulletItem = user.getInventoryManager().getInventoryByType(InventoryType.CONSUME).getItem(attack.bulletPosition);
                if (bulletItem == null || bulletItem.getQuantity() < bulletCount) {
                    log.error("Tried to use skill {} without enough bullets", attack.skillId);
                    return;
                }
                // Consume bullets
                bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCount));
                user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, attack.bulletPosition, bulletItem.getQuantity()), true));
            }
            // Consume hp/mp
            user.addHp(-hpCon);
            user.addMp(-mpCon);
            // Set cooltime
            if (cooltime > 0) {
                user.getSkillManager().setSkillCooltime(attack.skillId, Instant.now().plus(cooltime, ChronoUnit.SECONDS));
                user.write(UserLocal.skillCooltimeSet(attack.skillId, cooltime));
            }
            // Skill-specific handling
            JobHandler.handleAttack(user, attack, si);
        }

        // Process attack damage
        final Field field = user.getField();
        for (AttackInfo ai : attack.getAttackInfo()) {
            final Optional<Mob> mobResult = field.getMobPool().getById(ai.mobId);
            if (mobResult.isEmpty()) {
                continue;
            }
            final int totalDamage = Arrays.stream(ai.damage).sum();
            // Acquire mob
            try (var lockedMob = mobResult.get().acquire()) {
                final Mob mob = lockedMob.get();
                mob.damage(user, totalDamage);
                // Show mob hp indicator
                final double percentage = (double) mob.getHp() / mob.getMaxHp();
                user.write(MobPacket.hpIndicator(mob, (int) (percentage * 100)));
                // Handle death
                if (mob.getHp() <= 0) {
                    field.getMobPool().removeMob(mob);
                    mob.distributeExp();
                    mob.dropRewards(user);
                }
            }
        }

        field.broadcastPacket(UserRemote.attack(user, attack), user);
    }

    public static void processSkill(User user, Skill skill, InPacket inPacket) {
        // Resolve skill info
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skill.skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Failed to resolve skill info for skill : {}", skill.skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();

        // Check skill cooltime and cost
        final int cooltime = si.getValue(SkillStat.cooltime, skill.slv);
        if (cooltime > 0 && user.getSkillManager().hasSkillCooltime(skill.skillId)) {
            log.error("Tried to use skill {} that is still on cooltime", skill.skillId);
            return;
        }
        final int hpCon = si.getValue(SkillStat.hpCon, skill.slv);
        if (user.getHp() <= hpCon) {
            log.error("Tried to use skill {} without enough hp, current : {}, required : {}", skill.skillId, user.getHp(), hpCon);
            return;
        }
        final int mpCon = si.getValue(SkillStat.mpCon, skill.slv);
        if (user.getMp() < mpCon) {
            log.error("Tried to use skill {} without enough mp, current : {}, required : {}", skill.skillId, user.getMp(), mpCon);
            return;
        }
        final int bulletCon = si.getBulletCon(skill.slv);
        if (bulletCon > 0) {
            // Resolve bullet item
            final Item weaponItem = user.getInventoryManager().getInventoryByType(InventoryType.EQUIPPED).getItem(BodyPart.WEAPON.getValue());
            if (weaponItem == null) {
                log.error("Tried to use skill {} without a weapon", skill.skillId);
                return;
            }
            final Optional<Map.Entry<Integer, Item>> bulletEntryResult = user.getInventoryManager().getInventoryByType(InventoryType.CONSUME).getItems().entrySet().stream()
                    .filter((entry) -> ItemConstants.isCorrectBulletItem(weaponItem.getItemId(), entry.getValue().getItemId()) && entry.getValue().getQuantity() >= bulletCon)
                    .findFirst();
            if (bulletEntryResult.isEmpty()) {
                log.error("Tried to use skill {} without enough bullets", skill.skillId);
                return;
            }
            final int position = bulletEntryResult.get().getKey();
            final Item bulletItem = bulletEntryResult.get().getValue();
            // Consume bullets
            bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCon));
            user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, position, bulletItem.getQuantity()), true));
        }
        // Consume hp/mp
        user.addHp(-hpCon);
        user.addMp(-mpCon);
        // Set cooltime
        if (cooltime > 0) {
            user.getSkillManager().setSkillCooltime(skill.skillId, Instant.now().plus(cooltime, ChronoUnit.SECONDS));
            user.write(UserLocal.skillCooltimeSet(skill.skillId, cooltime));
        }

        // Skill-specific handling
        JobHandler.handleSkill(user, skill, si);
        user.write(WvsContext.skillUseResult());
    }

    public static void processHit(User user, HitInfo hitInfo) {
        // Skill-specific handling
        JobHandler.handleHit(user, hitInfo);
        // Process hit damage
        if (hitInfo.damage > 0) {
            user.addHp(-hitInfo.damage);
        }
        user.getField().broadcastPacket(UserRemote.hit(user, hitInfo), user);
    }
}
