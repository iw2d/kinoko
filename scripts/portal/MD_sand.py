# Sunset Road : Sahel 2 (260020600)
#   MD00 (-180, -178)
# Mini Dungeon : Hill of Sandstorms (260020630)
#   out00 (742, 97)

SAHEL_2 = 260020600
HILL_OF_SANDSTORMS = 260020630

if sm.getFieldId() == SAHEL_2:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(HILL_OF_SANDSTORMS, "out00", SAHEL_2, 7200)
else:
    sm.playPortalSE()
    sm.warp(SAHEL_2, "MD00")
