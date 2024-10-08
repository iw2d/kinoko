package kinoko.script;

import kinoko.script.common.Script;
import kinoko.script.common.ScriptHandler;
import kinoko.script.common.ScriptManager;

public final class TitleQuest extends ScriptHandler {
    @Script("explorationPoint")
    public static void explorationPoint(ScriptManager sm) {
        // Henesys : Henesys (100000000)
        // Singing Mushroom Forest : Ghost Mushroom Forest (100020400)
        // Golem's Temple : Golem's Temple Entrance (100040000)
        // Ellinia : Ellinia (101000000)
        // Chimney Tree : Chimney Tree Top (101020300)
        // Cursed Forest : Polluted Tree (101040300)
        // Perion : Perion (102000000)
        // North Rocky Mountain : Gusty Peak (102020500)
        // Burnt Land : Ash-Covered Land (102030400)
        // Excavation Site : Relic Excavation Camp (102040200)
        // Victoria Road : Sunset Sky (102050000)
        // Kerning City : Kerning City (103000000)
        // Kerning City Subway : Transfer Area (103020200)
        // Swamp Region : Deep Mire (103030400)
        // Kerning Square  : Kerning Square Lobby (103040000)
        // Lith Harbor : Lith Harbor (104000000)
        // Port Road : Six Path Crossway (104020000)
        // Sleepywood : Sleepywood (105000000)
        // Swamp : Humid Swamp (105010100)
        // Drake Cave : Cave Cliff (105020100)
        // Drake Cave : Chilly Cave (105020300)
        // Drake Cave : Cave Exit (105020400)
        // Cursed Temple : Another Door (105030000)
        // Cursed Temple : Temple Entrance (105030100)
        // Cursed Temple : Collapsed Temple (105030200)
        // Cursed Temple : Endless Hallway (105030300)
        // Cursed Temple : Forbidden Altar (105030500)
        // Dungeon : Sleepywood (105040300)
        // Dungeon : Drake's Meal Table (105090300)
        // Balrog Temple : Bottom of the Temple  (105100100)
        // Balrog Temple : Balrog's Tomb   (105100300)
        // Mushroom Castle : Secluded Mushroom Forest (106020100)
        // Nautilus : Nautilus Harbor (120000000)
        // Beach : White Wave Harbor (120020400)
        // Florina Beach : A Look-Out Shed Around the Beach (120030000)
        // Orbis : Orbis (200000000)
        // Orbis : The Road to Garden of 3 Colors (200010100)
        // Orbis : Stairway to the Sky II (200010300)
        // Orbis : Cloud Park VI (200080000)
        // Orbis : Entrance to Orbis Tower (200080100)
        // El Nath : El Nath (211000000)
        // El Nath : Cold Field I (211030000)
        // El Nath : Sharp Cliff I (211040300)
        // El Nath : Forest of Dead Trees II (211041200)
        // El Nath : Dead Mine IV (211041800)
        // Ludibrium : Ludibrium (220000000)
        // Ludibrium : Toy Factory <Main Process 1> (220020300)
        // Ludibrium : Crossroad of Time (220040200)
        // Omega Sector : Omega Sector (221000000)
        // Hidden Street : Hidden Tower (221020701)
        // Omega Sector : Boswell Field VI (221030600)
        // Omega Sector : Kulan Field V (221040400)
        // Korean Folk Town : Korean Folk Town (222000000)
        // Korean Folk Town : Top of Black Mountain (222010400)
        // Ludibrium : Helios Tower <Library> (222020000)
        // Aquarium : Aquarium (230000000)
        // Aqua Road : Red Coral Forest (230010200)
        // Aqua Road : Snowy Whale's Island (230010201)
        // Aqua Road : Forked Road : West Sea (230010400)
        // Aqua Road : Forked Road : East Sea (230020000)
        // Aqua Road : Two Palm Trees (230020201)
        // Aqua Road : Mushroom Coral Hill (230030100)
        // Aqua Road : Deep Sea Gorge I (230040000)
        // Aqua Road : Dangerous Sea Gorge I (230040200)
        // Aqua Road : The Grave of a Wrecked Ship (230040400)
        // Leafre : Leafre (240000000)
        // Leafre : Cranky Forest (240010200)
        // Leafre : Entrance to Sky Nest (240010800)
        // Leafre : Griffey Forest (240020101)
        // Leafre : Griffey Forest (240020102)
        // Leafre : Manon's Forest (240020401)
        // Leafre : Manon's Forest (240020402)
        // Leafre : Entrance to Dragon Forest (240030000)
        // Leafre : Wyvern Canyon (240040400)
        // Leafre : The Dragon Nest Left Behind (240040511)
        // Leafre : Dangerous Dragon Nest (240040521)
        // Cave of Life : Cave Entrance (240050000)
        // Mu Lung : Mu Lung (250000000)
        // Mu Lung : Snake Area (250010300)
        // Mu Lung : Territory of Wandering Bear (250010304)
        // Mu Lung : Peach Farm 1 (250010500)
        // Mu Lung : Goblin Forest 2 (250010504)
        // Mu Lung : Practice Field : Advanced Level (250020300)
        // Herb Town : Herb Town (251000000)
        // Herb Town : 100-Year-Old Herb Garden (251010200)
        // Herb Town : Red-Nose Pirate Den 2 (251010402)
        // Herb Town : Isolated Swamp (251010500)
        // The Burning Road : Ariant (260000000)
        // The Burning Sands : White Rock Desert (260010300)
        // The Burning Sands : Tent of the Entertainers (260010600)
        // Sunset Road : The Desert of Red Sand (260020300)
        // Sunset Road : Sahel 1 (260020700)
        // Sunset Road : Magatia (261000000)
        // Zenumist Research Institute : Lab - 2nd Floor Hallway (261010100)
        // Alcadno Research Institute : Lab - Center Gate (261020000)
        // Hidden Street : Authorized Personnel Only (261020401)
        // Hidden Street : Lab - Secret Basement Path (261030000)
        // Victoria Road : Thief Training Center (910310001)
        // Hidden Street : Sea of Silence (923030000)
        if (sm.getFieldId() == 104000000) {
            sm.screenEffect("maplemap/enter/104000000");
        }
    }

    @Script("highposition")
    public static void highposition(ScriptManager sm) {
        // Chimney Tree : Chimney Tree Top (101020300)
        //   highposition (-1113, 319)
        // Perion : Perion (102000000)
        //   highposition1 (1996, 166)
        // Kerning City : Kerning City (103000000)
        //   highposition1 (238, -1197)
        //   highposition2 (-1618, -1137)
        // Nautilus : Nautilus Harbor (120000000)
        //   highposition (4561, -985)
        // Orbis : Entrance to Orbis Tower (200080100)
        //   highposition (94, -487)

    }

    @Script("q29900s")
    public static void q29900s(ScriptManager sm) {
        // Beginner Adventurer (29900 - start)
        sm.forceStartQuest(29900);
    }

    @Script("q29901s")
    public static void q29901s(ScriptManager sm) {
        // Junior Adventurer (29901 - start)
        sm.forceStartQuest(29901);
    }

    @Script("q29902s")
    public static void q29902s(ScriptManager sm) {
        // Veteran Adventurer (29902 - start)
        sm.forceStartQuest(29902);
    }

    @Script("q29903s")
    public static void q29903s(ScriptManager sm) {
        // Master Adventurer (29903 - start)
        sm.forceStartQuest(29903);
    }

    @Script("q29905s")
    public static void q29905s(ScriptManager sm) {
        // Noblesse (29905 - start)
        sm.forceStartQuest(29905);
    }

    @Script("q29906s")
    public static void q29906s(ScriptManager sm) {
        // Knight-in-Training (29906 - start)
        sm.forceStartQuest(29906);
    }

    @Script("q29907s")
    public static void q29907s(ScriptManager sm) {
        // Official Knight (29907 - start)
        sm.forceStartQuest(29907);
    }

    @Script("q29908s")
    public static void q29908s(ScriptManager sm) {
        // Advanced Knight (29908 - start)
        sm.forceStartQuest(29908);
    }

    @Script("q29909s")
    public static void q29909s(ScriptManager sm) {
        // Chief Knight (29909 - start)
        sm.forceStartQuest(29909);
    }

    @Script("q29934s")
    public static void q29934s(ScriptManager sm) {
        // Well Behaved Child (29934 - start)
        sm.forceCompleteQuest(29934);
    }

    @Script("q29935s")
    public static void q29935s(ScriptManager sm) {
        // Perion Guard (29935 - start)
        sm.forceCompleteQuest(29935);
    }

    @Script("q29936s")
    public static void q29936s(ScriptManager sm) {
        // Kerning City Honorary Citizen (29936 - start)
        sm.forceCompleteQuest(29936);
    }

    @Script("q29937s")
    public static void q29937s(ScriptManager sm) {
        // Secret Organization Temporary Member (29937 - start)
        sm.forceCompleteQuest(29937);
    }

    @Script("q29938s")
    public static void q29938s(ScriptManager sm) {
        // Dragon Master (29938 - start)
        sm.forceCompleteQuest(29938);
    }

    @Script("q29939s")
    public static void q29939s(ScriptManager sm) {
        // Dragon Master (29939 - start)
        sm.forceCompleteQuest(29939);
    }

    @Script("q29940s")
    public static void q29940s(ScriptManager sm) {
        // Hero's Successor (29940 - start)
        sm.forceCompleteQuest(29940);
    }

    @Script("q29941s")
    public static void q29941s(ScriptManager sm) {
        // Special Training Beginner (29941 - start)
        sm.forceStartQuest(29941);
    }

    @Script("q29942s")
    public static void q29942s(ScriptManager sm) {
        // Special Training Intermediate (29942 - start)
        sm.forceStartQuest(29942);
    }

    @Script("q29943s")
    public static void q29943s(ScriptManager sm) {
        // Special Training Graduate (29943 - start)
        sm.forceStartQuest(29943);
    }

    @Script("q29944s")
    public static void q29944s(ScriptManager sm) {
        // Special Training Superior (29944 - start)
        sm.forceStartQuest(29944);
    }

    @Script("q29945s")
    public static void q29945s(ScriptManager sm) {
        // Special Training Master (29945 - start)
        sm.forceStartQuest(29945);
    }
}
