# Drake Cave : Cave Exit (105020400)

CAVE_EXIT = 105020400
BLUE_DRAKE_CAVE = 105020500
MINI_DUNGEON_COUNT = 100

if fieldId == CAVE_EXIT:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.warpInstance(BLUE_DRAKE_CAVE, MINI_DUNGEON_COUNT, "out00"):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(CAVE_EXIT, "MD00")