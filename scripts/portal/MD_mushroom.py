# Singing Mushroom Forest : Ghost Mushroom Forest (100020400)

GHOST_MUSHROOM_FOREST = 100020400
WARM_SHADE = 100020500

if sm.getFieldId() == GHOST_MUSHROOM_FOREST:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(WARM_SHADE, "out00", GHOST_MUSHROOM_FOREST, 7200)
else:
    sm.playPortalSE()
    sm.warp(GHOST_MUSHROOM_FOREST, "MD00")