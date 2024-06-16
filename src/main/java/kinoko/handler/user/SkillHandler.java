package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.BitFlag;
import kinoko.world.job.explorer.Magician;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Set;

public final class SkillHandler {
    private static final Logger log = LogManager.getLogger(SkillHandler.class);

    @Handler(InHeader.UserSkillUseRequest)
    public static void handleUserSkillUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final Skill skill = new Skill();
        skill.skillId = inPacket.decodeInt(); // nSkillID
        skill.slv = inPacket.decodeByte(); // nSLV

        if (skill.skillId == Mechanic.ROCK_N_SHOCK) {
            // CUserLocal::DoActiveSkill_Summon
            skill.rockAndShockCount = inPacket.decodeByte();
            if (skill.rockAndShockCount == 2) {
                skill.rockAndShock1 = inPacket.decodeInt();
                skill.rockAndShock2 = inPacket.decodeInt();
            }
        }
        if (skill.skillId == Citizen.CAPTURE) {
            // CUserLocal::DoActiveSkill_MobCapture
            skill.captureTargetMobId = inPacket.decodeInt();
        }
        if (skill.skillId == Citizen.CALL_OF_THE_HUNTER) {
            // CUserLocal::DoActiveSkill_SummonMonster
            skill.randomCapturedMobId = inPacket.decodeInt();
        }
        if (SkillConstants.isEncodePositionSkill(skill.skillId)) {
            skill.positionX = inPacket.decodeShort(); // GetPos()->x
            skill.positionY = inPacket.decodeShort(); // GetPos()->y
            if (SkillConstants.isSummonSkill(skill.skillId)) {
                // CUserLocal::DoActiveSkill_Summon
                skill.summonLeft = inPacket.decodeBoolean();
            }
        }
        if (skill.skillId == Thief.SHADOW_STARS) {
            // CUserLocal::SendSkillUseRequest
            skill.spiritJavelinItemId = inPacket.decodeInt(); // nSpiritJavelinItemID
        }
        if (SkillConstants.isPartySkill(skill.skillId) && inPacket.getRemaining() > 2) {
            // CUserLocal::SendSkillUseRequest
            skill.affectedMemberBitMap = inPacket.decodeByte();
            if (skill.skillId == Magician.DISPEL) {
                inPacket.decodeShort(); // tDelay
            }
        }
        if (inPacket.getRemaining() > 2) {
            // CUserLocal::SendSkillUseRequest
            final int mobCount = inPacket.decodeByte(); // nMobCount
            skill.mobIds = new int[mobCount];
            for (int i = 0; i < mobCount; i++) {
                skill.mobIds[i] = inPacket.decodeInt();
                if (skill.skillId == Thief.CHAINS_OF_HELL) {
                    // CUserLocal::TryDoingMonsterMagnet
                    inPacket.decodeByte(); // anMobMove[k] == 3 || anMobMove[k] == 4
                }
            }
        }
        if (skill.skillId == Thief.CHAINS_OF_HELL || skill.skillId == Citizen.CALL_OF_THE_HUNTER || SkillConstants.isSummonSkill(skill.skillId)) {
            // CUserLocal::TryDoingMonsterMagnet || CUserLocal::DoActiveSkill_SummonMonster || CUserLocal::DoActiveSkill_Summon
            skill.left = inPacket.decodeBoolean(); // nMoveAction & 1
        }
        // ignore tDelay

        try (var locked = user.acquire()) {
            if (skill.skillId == Magician.MYSTIC_DOOR) {
                if (user.getTownPortal() != null && user.getTownPortal().getWaitTime().isAfter(Instant.now())) {
                    user.write(MessagePacket.system("Please wait 5 seconds before casting Mystic Door again."));
                    user.dispose();
                    return;
                }
            }
            SkillProcessor.processSkill(locked, skill);
        }
    }

    @Handler(InHeader.UserSkillCancelRequest)
    public static void handleUserSkillCancelRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        if (SkillConstants.isKeydownSkill(skillId)) {
            return;
        }
        try (var locked = user.acquire()) {
            final SecondaryStat ss = locked.get().getSecondaryStat();
            final Set<CharacterTemporaryStat> resetStats = ss.resetTemporaryStat((cts, option) -> option.rOption == skillId);
            if (resetStats.isEmpty()) {
                log.error("Tried to cancel skill {}", skillId);
                return;
            }
            if (resetStats.contains(CharacterTemporaryStat.Beholder)) {
                user.removeSummoned(Warrior.BEHOLDER);
            }
            final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(resetStats, CharacterTemporaryStat.FLAG_SIZE);
            if (!flag.isEmpty()) {
                user.write(WvsContext.temporaryStatReset(flag));
                user.getField().broadcastPacket(UserRemote.temporaryStatReset(user, flag), user);
            }
        }
    }

    @Handler(InHeader.UserSkillPrepareRequest)
    public static void handleUserSkillPrepareRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final int slv = inPacket.decodeByte(); // nSLV
        final short actionAndDir = inPacket.decodeShort(); // nOneTimeAction & 0x7FFF | (nMoveAction << 15)
        final byte attackSpeed = inPacket.decodeByte(); // attack_speed_degree
        if (skillId == WildHunter.JAGUAR_OSHI) {
            inPacket.decodeInt(); // dwSwallowMobID
        }
        user.getField().broadcastPacket(UserRemote.skillPrepare(user, skillId, slv, actionAndDir, attackSpeed), user);
    }

    @Handler(InHeader.UserCalcDamageStatSetRequest)
    public static void handleUserCalcDamageStatSetRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            user.updatePassiveSkillData();
            user.validateStat();

            // Handle effects
            Warrior.handleBerserkEffect(user);
        }
    }

    @Handler(InHeader.UserThrowGrenade)
    public static void handleUserThrowGrenade(User user, InPacket inPacket) {
        final Skill skill = new Skill();
        skill.positionX = inPacket.decodeInt();
        skill.positionY = inPacket.decodeInt();
        inPacket.decodeInt();
        skill.keyDown = inPacket.decodeInt();
        skill.skillId = inPacket.decodeInt();
        skill.slv = inPacket.decodeInt();

        try (var locked = user.acquire()) {
            user.getField().broadcastPacket(UserRemote.throwGrenade(user, skill), user);
        }
    }

    @Handler(InHeader.UserClientTimerEndRequest)
    public static void handleUserClientTimerEndRequest(User user, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final int[] skillIds = new int[size];
        for (int i = 0; i < size; i++) {
            skillIds[i] = inPacket.decodeInt();
            inPacket.decodeInt();
        }
        try (var locked = user.acquire()) {
            for (int skillId : skillIds) {
                user.resetTemporaryStat(skillId);
            }
        }
    }
}
