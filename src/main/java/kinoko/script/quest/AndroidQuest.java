package kinoko.script.quest;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

/**
 * Android and Special Event Quest Scripts
 * Contains android-related and special event join scripts
 */
public final class AndroidQuest extends ScriptHandler {

    @Script("android_GL")
    public static void android_GL(ScriptManager sm) {
        // Android GL Quest
        sm.sayOk("Android GL quest is not yet implemented.");
    }

    @Script("eDay_join")
    public static void eDay_join(ScriptManager sm) {
        // eDay Join Event
        sm.sayOk("eDay join event is not yet implemented.");
    }

    @Script("eDay_join2")
    public static void eDay_join2(ScriptManager sm) {
        // eDay Join Event 2
        sm.sayOk("eDay join 2 event is not yet implemented.");
    }
}
