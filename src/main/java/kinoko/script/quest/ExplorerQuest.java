package kinoko.script.quest;

import java.util.*;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptError;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;
import kinoko.world.job.JobConstants;

public final class ExplorerQuest extends ScriptHandler {
    
    private final static int MAGICIAN_TRAINING_CENTER = 910120000;

    @Script("enter_magicion")
    public static void enter_magicion(ScriptManager sm) {
        // Most text was obtained from: https://youtu.be/MMb1MJGGkg4?si=Gm9rG6p1l8iiHan2
        // Power B. Fore : Entrance to Magician Training Center (1032114)
        //   Chimney Tree : Close to the Wind (101020000)
        // If the user is level 20 or above, they should not be able to access the Training Center
        // If the user is not a magician, they should not be able to access the Training Center
        if (sm.getUser().getLevel() >= 20 || sm.getUser().getJob() != 200) {
            sm.sayOk("Sorry, but this is a training center only available to Magicians under Lv. 20.");
            return;
        }
        final List<String> fieldNames = Arrays.asList(
                "Room of Courage",
                "Room of Wisdom",
                "Room of Skill",
                "Room of Training",
                "Room of Power"
        );
        final Map<Integer, String> options = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            int fieldId = MAGICIAN_TRAINING_CENTER + i;
            final Optional<Field> fieldResult = sm.getUser().getConnectedServer().getFieldById(fieldId);
            if (fieldResult.isEmpty()) {
                throw new ScriptError("Could not resolve field ID : %d", fieldId);
            }
            // Only add the option if there are less than 5 people in the map
            final int userCount = fieldResult.get().getUserPool().getCount();
            if (userCount < 5) {
                options.put(fieldId, fieldNames.get(i) + " (" + userCount + "/5)");
            }
        }
        // If all maps are already full, notify the user
        if (options.isEmpty()) {
            sm.sayOk("I'm sorry, but it appears that all the training centers are currently full. Please come back later!");
            return;
        }
        final int fieldId = sm.askMenu("#e#b[Notice]#n#k\r\nAdventurers!\r\nThis is a training center for Magicians under Lv. 20. While you can always train on your own, training with others will allow you to become stronger in a faster time. Select the room you would like to train in.#b", options);
        sm.warp(fieldId);
    }

    @Script("enter_archer")
    public static void enter_archer(ScriptManager sm) {
        // Power B. Fore : Entrance to Bowman Training Center (1012119)
        //   Singing Mushroom Forest : Spore Hill (100020000)
        if (sm.hasQuestStarted(22518)) {
            sm.warpInstance(910060100, "start", 100020000, 60 * 30);
            return;
        }
        sm.warp(910060000); // Victoria Road : Bowman Training Center
    }
}
