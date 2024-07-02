# North Forest : Young Tree Forest (101030300)

YOUNG_TREE_FOREST = 101030300
ONE_EYED_LIZARD = 101030400

if fieldId == YOUNG_TREE_FOREST:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(ONE_EYED_LIZARD, "out00", YOUNG_TREE_FOREST, 7200)
else:
    sm.playPortalSE()
    sm.warp(YOUNG_TREE_FOREST, "MD00")