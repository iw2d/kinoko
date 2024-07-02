# Drake Cave : Cave Exit (105020400)

CAVE_EXIT = 105020400
BLUE_DRAKE_CAVE = 105020500

if fieldId == CAVE_EXIT:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.partyWarpInstance(BLUE_DRAKE_CAVE, "out00", CAVE_EXIT, 7200):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(CAVE_EXIT, "MD00")