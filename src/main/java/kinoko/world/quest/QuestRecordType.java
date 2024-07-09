package kinoko.world.quest;

/**
 * Special quest IDs used to store various state information for the user.
 */
public enum QuestRecordType {
    MushroomCastleOpening(2311),
    UnityPortal(7050),
    FreeMarket(7600),
    TatamoLikeness(7810),
    WorldTour(8792);

    private final int questId;

    QuestRecordType(int questId) {
        this.questId = questId;
    }

    public final int getQuestId() {
        return questId;
    }
}
