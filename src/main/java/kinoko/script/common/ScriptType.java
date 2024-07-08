package kinoko.script.common;

/**
 * Enum for classifying scripts that can run in parallel.
 */
public enum ScriptType {
    NPC,                // npc scripts
    ITEM,               // item scripts
    QUEST,              // quest scripts
    PORTAL,             // portal scripts
    REACTOR,            // reactor scripts
    FIRST_USER_ENTER,   // field onFirstUserEnter scripts
    USER_ENTER          // field onUserEnter scripts
}
