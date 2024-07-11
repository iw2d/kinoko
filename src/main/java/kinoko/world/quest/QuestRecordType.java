package kinoko.world.quest;

/**
 * Special quest IDs used to store various state information for the user.
 */
public enum QuestRecordType {
    NautilusMomCow(2180),           // Find Fresh Milk
    MushroomCastleOpening(2311),    // Mushroom Castle Opening Cutscene
    UnityPortal(7050),              // Dimensional Mirror Return Map
    FreeMarket(7600),               // Free Market Return Map
    TatamoLikeness(7810),           // Chief Tatamo Magic Seed Discount
    WorldTour(8792),                // World Tour Return Map

    CygnusTutorial(20022);          // Cygnus Tutorial

    private final int questId;

    QuestRecordType(int questId) {
        this.questId = questId;
    }

    public final int getQuestId() {
        return questId;
    }
}
