package kinoko.world.quest;

/**
 * Special quest IDs used to store various state information for the user.
 */
public enum QuestRecordType {
    NautilusMomCow(2180),           // Find Fresh Milk

    MushroomCastleOpening(2311),    // Mushroom Castle Opening Cutscene
    MushroomCastleInvestigation(2314),
    MushroomCastleInvestigation2(2322),
    MushroomCastleThornRemover(2324),
    MushroomCastlePepe(2330),

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
    EvanIceWall(22605),

    ResistanceCheckyFlier(23006),
    ResistanceHideSeek(23007),
    ResistanceTraining(23128),
    ResistanceFirstMission(23129),
    ResistanceWaterTank(23130),
    ResistanceWaterTrade(23131),

    EdelsteinUnlockTownQuests(23977), //Not sure what quest is supposed to update this
    EdelsteinFabioFirebombs(23979),
    EdelsteinWonny10PM(23984),

    ZakumPreqStageOne(100200),      // Zakum Pre-quest Stage 1 tracking
    ZakumPreqStageTwo(100201),      // Zakum Pre-quest Stage 2 tracking
    Zakum(100250),                  // Zakum daily entry tracking
    MuLungDojoTutorial(100300),     // Mu Lung Dojo tutorial quest
    GachaponEvent(100400),          // Gachapon return map tracking
    AlcasterAndTheDarkCrystal(100500),  // Alcaster Dark Crystal quest
    WheresHella(100501),                // Where's Hella quest
    TheSmallGraveThatsHidden(100502),   // Small Grave quest
    AcquiringTheFairyDust(100503),      // Fairy Dust quest
    MemoryKeeper(100504);               // Memory Keeper quest

    private final int questId;

    QuestRecordType(int questId) {
        this.questId = questId;
    }

    public final int getQuestId() {
        return questId;
    }
}