package kinoko.script.continent;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Ludibrium continent portal and NPC scripts
 * Includes Ellin Forest access, pet training, and parkour functionality
 */
public final class Ludibrium extends ScriptHandler {
    public static final int PET_TRAINER_LETTER = 4031128;

    // PORTAL SCRIPTS

    @Script("move_elin")
    public static void move_elin(ScriptManager sm) {
        // Portal from Ludibrium Helios Tower (222020400) to Ellin Forest
        // Based on old script: move_elin.js
        sm.playPortalSE();
        sm.warp(300000100, "out00"); // Ellin Forest
    }

    // NPC SCRIPTS

    @Script("ludi028")
    public static void ludi028(ScriptManager sm) {
        // Pet Trainer Yuppy (2040032)
        // Ludibrium : Ludibrium Pet Walkway (220000006)
        // Pet training parkour course - Start NPC

        if (sm.hasItem(PET_TRAINER_LETTER)) {
            sm.sayNext("Get that letter, jump over obstacles with your pet, and take that letter to my brother #p2040033#. Get him the letter and something good is going to happen to your pet.");
            return;
        }

        if (!sm.askYesNo("This is the road where you can go take a walk with your pet. You can just walk around with it, or you can train your pet to go through the obstacles here. If you aren't too close with your pet yet, that may present a problem and he will not follow your command as much... so, what do you think? Wanna train your pet?")) {
            sm.sayNext("Hmmm... too busy to do it right now? If you feel like doing it, though, come back and find me.");
            return;
        }

        if (!sm.canAddItem(PET_TRAINER_LETTER, 1)) {
            sm.sayNext("Your etc. inventory is full! I can't give you the letter unless there's room on ur inventory. Make an empty slot and then talk to me.");
            return;
        }

        sm.addItem(PET_TRAINER_LETTER, 1);
        sm.sayOk("Ok, here's the letter. He wouldn't know I sent you if you just went there straight, so go through the obstacles with your pet, go to the very top, and then talk to #p2040033# to give him the letter. It won't be hard if you pay attention to your pet while going through obstacles. Good luck!");
    }

    @Script("ludi029")
    public static void ludi029(ScriptManager sm) {
        // Pet Trainer Neil (2040033)
        // Ludibrium : Ludibrium Pet Walkway (220000006)
        // Pet training parkour course - End NPC (rewards pet closeness)

        // Check if player is at the top of the parkour (y position check)
        if (sm.getUser().getY() > -1038) {
            // Player hasn't reached the top yet
            return;
        }

        if (!sm.hasItem(PET_TRAINER_LETTER)) {
            sm.sayOk("My brother told me to take care of the pet obstacle course, but... since I'm so far away from him, I can't help but wanting to goof around ...hehe, since I don't see him in sight, might as well just chill for a few minutes.");
            return;
        }

        sm.sayNext("Eh, that's my brother's letter! Probably scolding me for thinking I'm not working and stuff... Eh? Ahhh... you followed my brother's advice and trained your pet and got up here, huh? Nice!! Since you worked hard to get here, I'll boost your intimacy level with your pet.");

        if (!sm.hasItem(PET_TRAINER_LETTER) || sm.getUser().getPet(0) == null) {
            sm.sayBoth("Hmmm... did you really get here with your pet? These obstacles are for pets. What are you here for without it?? Get outta here!");
            return;
        }

        sm.removeItem(PET_TRAINER_LETTER, 1);
        // TODO: Implement pet closeness system
        // Random closeness gain between 1-9
        // final int closenessGain = (int) (Math.random() * 9) + 1;
        // sm.getUser().getPet(0).addCloseness(closenessGain);
        sm.sayOk("What do you think? Don't you think you have gotten much closer with your pet? If you have time, train your pet again on this obstacle course... of course, with my brother's permission.");
    }
}
