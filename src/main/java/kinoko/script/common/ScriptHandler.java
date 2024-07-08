package kinoko.script.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ScriptHandler {
    protected static final Logger log = LogManager.getLogger(ScriptHandler.class);

    protected static String blue(String text) {
        return String.format("#b%s#k", text);
    }
}
