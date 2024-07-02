# Golem's Temple : Golem's Temple 4 (100040400)

GOLEMS_TEMPLE_4 = 100040400
GOLEMS_CASTLE_RUINS = 100040500

SLEEPY_DUNGEON_IV = 105040304 # dungeon map doesn't exist

if fieldId == SLEEPY_DUNGEON_IV:
    sm.message("You cannot go to that place")
    sm.dispose()
elif fieldId == GOLEMS_TEMPLE_4:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.partyWarpInstance(GOLEMS_CASTLE_RUINS, "out00", GOLEMS_TEMPLE_4, 7200):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(GOLEMS_TEMPLE_4, "MD00")