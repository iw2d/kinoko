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

    CygnusTutorial(20022),          // Cygnus Tutorial
    AranTutorial(21002),            // Aran Tutorial
    AranGuideEffect(21003),         // Aran Tutorial Effects
    AranHelperClear(21019),         // Aran Tutorial Helper
    EvanDragonEyes(22012),
    EvanDreamEffect(22013),
    EvanTutorialEffect(22014),
    EvanPerionSigns(22597),
    EvanEnragedGolem(22598),
    EvanSnowDragon(22599),
    EvanExitCave(22600),
    EvanAfrienMemory(22601),
    EvanAfrien(22604),
    EvanIceWall(22605);

    private final int questId;

    QuestRecordType(int questId) {
        this.questId = questId;
    }

    public final int getQuestId() {
        return questId;
    }
}
