package kinoko.script.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class ScriptHandler {
    protected static final Logger log = LogManager.getLogger(ScriptHandler.class);

    protected static String blue(String text) {
        return String.format("#b%s#k", text);
    }

    protected static String red(String text) {
        return String.format("#r%s#k", text);
    }

    protected static String bold(String text) {
        return String.format("#e%s#n", text);
    }

    protected static String itemName(int itemId) {
        return String.format("#t%d#", itemId);
    }

    protected static String itemImage(int itemId) {
        return String.format("#v%d#", itemId);
    }

    protected static String mapName(int mapId) {
        return String.format("#m%d#", mapId);
    }

    protected static String npcName(int npcId) {
        return String.format("#p%d#", npcId);
    }

    protected static <T> Map<Integer, String> createOptions(List<T> list, Function<T, String> mapper) {
        final Map<Integer, String> options = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            options.put(i, mapper.apply(list.get(i)));
        }
        return options;
    }
}
