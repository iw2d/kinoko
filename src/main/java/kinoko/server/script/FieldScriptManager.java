package kinoko.server.script;

import kinoko.world.field.Field;
import kinoko.world.user.User;

public final class FieldScriptManager extends ScriptManager {
    private final Field field;
    private final boolean firstEnter;

    public FieldScriptManager(User user, Field field, boolean firstEnter) {
        super(user);
        this.field = field;
        this.firstEnter = firstEnter;
    }

    @Override
    public void disposeManager() {
        ScriptDispatcher.removeScriptManager(firstEnter ? ScriptType.FIRST_USER_ENTER : ScriptType.USER_ENTER, user);
    }

    public Field getField() {
        return field;
    }
}
