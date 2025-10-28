package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.job.Job;

/**
 * Pirate Job Advancement Quests
 *
 * Quest 2191 - How to become a Brawler (2nd job)
 * Quest 2192 - How to Become a Gunslinger (2nd job)
 * Quest 2304 - Endangered Mushking Empire (3rd job letter of recommendation)
 */
public final class PirateQuest extends ScriptHandler {

    // BRAWLER PATH (Quest 2191) ---------------------------------------------------------------------------------

    @Script("q2191s")
    public static void q2191s(ScriptManager sm) {
        // Quest 2191 - How to become a Brawler (START)
        // Kyrin (1090000) - Nautilus
        sm.sayNext("You have reached Level 30 already. Are you here to become the Brawler? Then you've come to the right place.");

        if (sm.askYesNo("But I can't just give you a job advancement that easily. I need you to prove yourself worthy of it. Brawlers use brute strength, steel fists, and their full bodies' power to eliminate all opposition. In order to become a commendable Brawler, you'll need to be good with your fists! Prove to me that you are.")) {
            sm.forceStartQuest(2191);
            sm.sayNext("It's simple really. You'll just need to head to the test room where I'll send you, and fight #rOctopirates#k. Your task is to bring back #b15 Potent Power Crystals#k.");
            sm.sayOk("You think it's easy? Then think again. These Octopirates can only be attacked with #rFlash Fist#k. Other attacks will be fruitless. It may be tough, but any honorable Brawler must go through this. Good luck...");
            // TODO: Warp to test room when implemented
        } else {
            sm.sayOk("Do you not wish to become a Brawler?");
        }
    }

    @Script("q2191e")
    public static void q2191e(ScriptManager sm) {
        // Quest 2191 - How to become a Brawler (END)
        final int POTENT_POWER_CRYSTAL = 4031856;

        if (!sm.hasItem(POTENT_POWER_CRYSTAL, 15)) {
            sm.sayOk("Bring 15 Potent Power Crystals.");
            return;
        }

        sm.sayNext("Good. You got all 15.");

        if (sm.askYesNo("That performance in the testing ground was impressive. You look like you can become a legitimate force as a Brawler. Now tell me when you're ready to make the job advancement, and I'll go ahead and do it for you.")) {
            sm.removeItem(POTENT_POWER_CRYSTAL, 15);
            sm.forceCompleteQuest(2191);
            sm.setJob(Job.BRAWLER); // Brawler
            sm.sayOk("Congratulations! You are now a #bBrawler#k. Train hard and become stronger!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    // GUNSLINGER PATH (Quest 2192) ---------------------------------------------------------------------------------

    @Script("q2192s")
    public static void q2192s(ScriptManager sm) {
        // Quest 2192 - How to Become a Gunslinger (START)
        // Kyrin (1090000) - Nautilus
        sm.sayNext("You have reached Level 30 already. Are you here to become a Gunslinger? Then you've come to the right place.");

        if (sm.askYesNo("But I can't just give you a job advancement that easily. I need you to prove yourself worthy of it. Gunslingers use Guns to eliminate oppositions. In order to become a commendable Gunslinger, you'll need to be good with your Gun, right? Prove it to me that you are.")) {
            sm.forceStartQuest(2192);
            sm.sayNext("It's simple really. You'll just need to head to the test room where I'll send you, and fight #rOctopirates#k. Your task is to bring back #b15 Potent Wind Crystals#k.");
            sm.sayOk("You think it's easy? Then think again. These Octopirates can only be attacked with #rDouble Shot#k. Other attacks will be fruitless. It may be tough, but any honorable Gunslinger must go through this. Good luck...");
            // TODO: Warp to test room when implemented
        } else {
            sm.sayOk("Do you not wish to become a Gunslinger?");
        }
    }

    @Script("q2192e")
    public static void q2192e(ScriptManager sm) {
        // Quest 2192 - How to Become a Gunslinger (END)
        final int POTENT_WIND_CRYSTAL = 4031857;

        if (!sm.hasItem(POTENT_WIND_CRYSTAL, 15)) {
            sm.sayOk("Bring 15 Potent Wind Crystals.");
            return;
        }

        sm.sayNext("Good. You got all 15.");

        if (sm.askYesNo("That performance in the testing ground was impressive. You look like you can become a legitimate force as a Gunslinger. Now tell me when you're ready to make the job advancement, and I'll go ahead and do it for you.")) {
            sm.removeItem(POTENT_WIND_CRYSTAL, 15);
            sm.forceCompleteQuest(2192);
            sm.setJob(Job.GUNSLINGER); // Gunslinger
            sm.sayOk("Congratulations! You are now a #bGunslinger#k. Train hard and become stronger!");
        } else {
            sm.sayOk("Come back when you're ready.");
        }
    }

    // ENDANGERED MUSHKING EMPIRE (Quest 2304) ----------------------------------------------------------------

    @Script("q2304s")
    public static void q2304s(ScriptManager sm) {
        // Quest 2304 - Endangered Mushking Empire (START)
        // Kyrin (1090000) - Nautilus
        // This is an optional quest for 3rd job Pirates to help the Mushking Empire

        sm.sayNext("The Mushking Empire is in dire straits and in desperate need of help! As a strong Pirate, I believe you can make a difference there.");

        if (sm.askYesNo("I'd like to give you a #bletter of recommendation#k to take to #b#p1300005##k, the Head Security Officer of the Mushking Empire. Will you help them?")) {
            sm.forceStartQuest(2304);
            sm.addItem(4032375, 1); // Letter of Recommendation
            sm.sayNext("Thank you! Please take this letter to #b#p1300005##k in the Mushking Empire. They need all the help they can get!");
            sm.sayOk("You can find the Mushking Empire through the dimensional portal. Good luck!");
        } else {
            sm.sayOk("If you change your mind, come back and talk to me.");
        }
    }

    @Script("q2304e")
    public static void q2304e(ScriptManager sm) {
        // Quest 2304 - Endangered Mushking Empire (END)
        // Head Security Officer (1300005) - Mushking Empire
        final int LETTER_OF_RECOMMENDATION = 4032375;

        if (!sm.hasItem(LETTER_OF_RECOMMENDATION, 1)) {
            sm.sayOk("You need to bring the letter of recommendation from Kyrin.");
            return;
        }

        sm.sayNext("Ah, you have a letter of recommendation from Kyrin! Thank you for coming to help the Mushking Empire in our time of need.");

        if (sm.askYesNo("We are grateful for your assistance. Will you help us defend the Empire?")) {
            sm.removeItem(LETTER_OF_RECOMMENDATION, 1);
            sm.forceCompleteQuest(2304);
            sm.sayOk("Thank you! The Mushking Empire is in your debt. There will be more challenges ahead, but with your help, we can overcome them!");
        } else {
            sm.sayOk("Please reconsider. We really need your help.");
        }
    }
}
