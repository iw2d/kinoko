package kinoko.server.script;

/**
 * Enum for classifying scripts that can run in parallel.
 */
public enum ScriptType {
    NPC,                // npc and quest scripts
    PORTAL,             // portal scripts
    REACTOR,            // reactor scripts
    FIRST_USER_ENTER,   // field onFirstUserEnter scripts
    USER_ENTER          // field onUserEnter scripts
}
