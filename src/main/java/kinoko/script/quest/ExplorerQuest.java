package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptError;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;
import kinoko.world.field.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ExplorerQuest extends ScriptHandler {
    // Generic function to handle entering an Explorer Training Center
    // Most text was obtained from: https://youtu.be/MMb1MJGGkg4?si=Gm9rG6p1l8iiHan2
    private static void enterTrainingCenter(ScriptManager sm, int reqJobId, String reqJobName, int fieldId) {
        // If the user is level 20 or above, they should not be able to access the Training Center
        // If the user is not the correct job, they should not be able to access the Training Center
        if (sm.getLevel() >= 20 || sm.getJob() != reqJobId) {
            sm.sayOk("Sorry, but this is a training center only available to " + reqJobName + " under Lv. 20.");
            return;
        }
        final List<String> fieldNames = List.of(
                "Room of Courage",
                "Room of Wisdom",
                "Room of Skill",
                "Room of Training",
                "Room of Power"
        );
        final Map<Integer, String> options = new HashMap<>();
        for (int i = 0; i < fieldNames.size(); i++) {
            final int roomFieldId = fieldId + i;
            final Optional<Field> fieldResult = sm.getUser().getConnectedServer().getFieldById(roomFieldId);
            if (fieldResult.isEmpty()) {
                throw new ScriptError("Could not resolve field ID : %d", roomFieldId);
            }
            // Only add the option if there are less than 5 people in the map
            final int userCount = fieldResult.get().getUserPool().getCount();
            if (userCount < 5) {
                options.put(roomFieldId, fieldNames.get(i) + " (" + userCount + "/5)");
            }
        }
        // If all maps are already full, notify the user
        if (options.isEmpty()) {
            sm.sayOk("I'm sorry, but it appears that all the training centers are currently full. Please come back later!");
            return;
        }
        final int targetFieldId = sm.askMenu("#e#b[Notice]#n#k\r\nAdventurers!\r\nThis is a training center for " + reqJobName + " under Lv. 20. While you can always train on your own, training with others will allow you to become stronger in a faster time. Select the room you would like to train in.#b", options);
        sm.warp(targetFieldId, "out00");
    }

    @Script("enter_warrior")
    public static void enter_warrior(ScriptManager sm) {
        // Power B. Fore : Entrance to Warrior Training Center (1022105)
        //   North Rocky Mountain : Perion Northern Ridge (102020000)
        enterTrainingCenter(sm, 100, "Warriors", 910220000); // Victoria Road : Warrior Training Center
    }

    @Script("enter_magicion")
    public static void enter_magicion(ScriptManager sm) {
        // Power B. Fore : Entrance to Magician Training Center (1032114)
        //   Chimney Tree : Close to the Wind (101020000)
        enterTrainingCenter(sm, 200, "Magicians", 910120000); // Victoria Road : Magician Training Center
    }

    @Script("enter_archer")
    public static void enter_archer(ScriptManager sm) {
        // Power B. Fore : Entrance to Bowman Training Center (1012119)
        //   Singing Mushroom Forest : Spore Hill (100020000)
        if (sm.hasQuestStarted(22518)) {
            sm.warpInstance(910060100, "start", 100020000, 60 * 30);
            return;
        }
        enterTrainingCenter(sm, 300, "Bowmen", 910060000); // Victoria Road : Bowman Training Center
    }

    @Script("enter_thief")
    public static void enter_thief(ScriptManager sm) {
        // Power B. Fore : Entrance to Thief Training Center (1052114)
        //   Construction Site : Caution Falling Down (103010000)
        enterTrainingCenter(sm, 400, "Thieves", 910310000); // Victoria Road : Thief Training Center
    }

    @Script("enter_pirate")
    public static void enter_pirate(ScriptManager sm) {
        // Power B. Fore : Entrance to Pirate Training Center (1095002)
        //   Beach : Coastal Forest (120020000)
        enterTrainingCenter(sm, 500, "Pirates", 912030000); // Victoria Road : Pirate Training Center
    }
}
