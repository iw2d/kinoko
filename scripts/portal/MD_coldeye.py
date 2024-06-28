# North Forest : Young Tree Forest (101030300)

YOUNG_TREE_FOREST = 101030300
ONE_EYED_LIZARD = 101030400
MINI_DUNGEON_COUNT = 100

if fieldId == YOUNG_TREE_FOREST:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.warpInstance(ONE_EYED_LIZARD, MINI_DUNGEON_COUNT, "out00"):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(YOUNG_TREE_FOREST, "MD00")