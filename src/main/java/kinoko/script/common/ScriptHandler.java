package kinoko.script.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ScriptHandler {
    protected static final Logger log = LogManager.getLogger(ScriptHandler.class);

    protected static String blue(String text) {
        return String.format("#b%s#k", text);
    }

    protected static String bold(String text) {
        return String.format("#e%s#n", text);
    }

    protected static String item(int itemId) {
        return String.format("#t%d#", itemId);
    }

    protected static String map(int mapId) {
        return String.format("#m%d#", mapId);
    }

    protected static String npc(int npcId) {
        return String.format("#n%d#", npcId);
    }
}
