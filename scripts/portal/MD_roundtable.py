# Leafre : Battlefield of Fire and Water (240020500)

BATTLEFIELD_OF_FIRE_AND_WATER = 240020500
THE_ROUND_TABLE_OF_KENTAURUS = 240020501
MINI_DUNGEON_COUNT = 30

if fieldId == BATTLEFIELD_OF_FIRE_AND_WATER:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.warpInstance(THE_ROUND_TABLE_OF_KENTAURUS, MINI_DUNGEON_COUNT, "out00"):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(BATTLEFIELD_OF_FIRE_AND_WATER, "MD00")