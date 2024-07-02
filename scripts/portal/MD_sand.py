# Sunset Road : Sahel 2 (260020600)

SAHEL_2 = 260020600
HILL_OF_SANDSTORMS = 260020630

if fieldId == SAHEL_2:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.partyWarpInstance(HILL_OF_SANDSTORMS, "out00", SAHEL_2, 7200):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(SAHEL_2, "MD00")