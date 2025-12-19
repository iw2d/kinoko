package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.script.quest.ExplorerQuest;
import kinoko.script.quest.PirateQuest;

import java.util.Map;

/**
 * Nautilus (Pirate Town) Scripts
 */
public final class Nautilus extends ScriptHandler {

    @Script("kairinT")
    public static void kairinT(ScriptManager sm) {
        // Handle 1st, 2nd, and 3rd job advancement
        if (sm.getJob() == 0) {
            // 1st job advancement - Beginner -> Pirate
            ExplorerQuest.pirate(sm);
        } else if (sm.getJob() == 500) {
            // 2nd job advancement - Pirate -> Brawler/Gunslinger
            // Check if player has quest started and items ready to turn in
            if (sm.hasQuestStarted(2191) && sm.hasItem(4031856, 15)) {
                // Quest 2191 completion - Brawler
                PirateQuest.q2191e(sm);
            } else if (sm.hasQuestStarted(2192) && sm.hasItem(4031857, 15)) {
                // Quest 2192 completion - Gunslinger
                PirateQuest.q2192e(sm);
            } else {
                // Start new quest
                pirate1(sm);
            }
        } else if (sm.getJob() == 510 || sm.getJob() == 520) {
            // 3rd job advancement - direct to El Nath
            sm.sayOk("You've become a formidable pirate! To advance to 3rd job, you must travel to #bEl Nath#k and seek out the pirate instructor there. They will help you become a #bMarauder#k or #bOutlaw#k!");
        } else if (sm.getJob() == 511 || sm.getJob() == 521) {
            // 4th job advancement - direct to El Nath then Leafre
            sm.sayOk("You have reached the peak of piracy! To achieve your 4th job advancement, you must travel to #bEl Nath#k and speak with the instructor there. They will guide you on your final trial.");
        } else {
            sm.sayOk("I can only help Pirates with their job advancement.");
        }
    }

    @Script("pirate1")
    public static void pirate1(ScriptManager sm) {
        // Kyrin : Pirate Job Instructor (1090000)
        //   Nautilus : Navigation Room (120000101)

        // Check if player is a Pirate beginner (job 500)
        if (sm.getJob() != 500) {
            sm.sayOk("You don't seem to be a Pirate beginner. I can only help those who wish to become Pirates.");
            return;
        }

        // Check level requirement
        if (sm.getLevel() < 30) {
            sm.sayOk("You need to be at least Level 30 to make your first job advancement. Train harder and come back when you're ready!");
            return;
        }

        // Check if already has quest started
        if (sm.hasQuestStarted(2191)) {
            sm.sayOk("You're already on the path to becoming a Brawler. Complete the quest and return to me!");
            return;
        }

        if (sm.hasQuestStarted(2192)) {
            sm.sayOk("You're already on the path to becoming a Gunslinger. Complete the quest and return to me!");
            return;
        }

        // Check if already completed a quest (already advanced)
        if (sm.hasQuestCompleted(2191) || sm.hasQuestCompleted(2192)) {
            sm.sayOk("You've already made your job advancement. Continue training and become stronger!");
            return;
        }

        // Offer job choice
        sm.sayNext("You've reached Level 30. Impressive! You're now ready to choose your path as a Pirate. There are two paths available to you:");
        sm.sayBoth("#bBrawlers#k use their fists and raw power to crush their enemies in close combat. They are fierce warriors who rely on strength and agility.");
        sm.sayBoth("#bGunslingers#k use guns to attack from a distance. They are skilled marksmen who rely on precision and speed.");

        final int answer = sm.askMenu("Which path do you wish to take?", Map.of(
                0, "I want to become a Brawler.",
                1, "I want to become a Gunslinger."
        ));

        if (answer == 0) {
            // Brawler path - trigger quest 2191
            sm.sayNext("Ah, the path of the Brawler! A fine choice. Brawlers are powerful warriors who use their fists to dominate the battlefield.");

            if (sm.askYesNo("Are you ready to take the test to become a Brawler?")) {
                sm.forceStartQuest(2191);
                sm.sayNext("Excellent! I will now test your abilities.");
                sm.sayBoth("You'll need to head to the test room and fight #rOctopirates#k. Your task is to bring back #b15 Potent Power Crystals#k.");
                sm.sayOk("Remember: These Octopirates can only be attacked with #rFlash Fist#k. Other attacks will be fruitless. Good luck!");
                // TODO: Warp to test room when map is configured
            } else {
                sm.sayOk("Think carefully about your decision and return when you're ready.");
            }
        } else if (answer == 1) {
            // Gunslinger path - trigger quest 2192
            sm.sayNext("Ah, the path of the Gunslinger! A fine choice. Gunslingers are skilled marksmen who can take down enemies from afar.");

            if (sm.askYesNo("Are you ready to take the test to become a Gunslinger?")) {
                sm.forceStartQuest(2192);
                sm.sayNext("Excellent! I will now test your abilities.");
                sm.sayBoth("You'll need to head to the test room and fight #rOctopirates#k. Your task is to bring back #b15 Potent Wind Crystals#k.");
                sm.sayOk("Remember: These Octopirates can only be attacked with #rDouble Shot#k. Other attacks will be fruitless. Good luck!");
                // TODO: Warp to test room when map is configured
            } else {
                sm.sayOk("Think carefully about your decision and return when you're ready.");
            }
        }
    }
}
