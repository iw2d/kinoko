# Leafre : Battlefield of Fire and Water (240020500)
#   MD00 (772, 119)
# Mini Dungeon : The Round Table of Kentaurus (240020501)
#   out00 (839, -779)

BATTLEFIELD_OF_FIRE_AND_WATER = 240020500
THE_ROUND_TABLE_OF_KENTAURUS = 240020501

if sm.getFieldId() == BATTLEFIELD_OF_FIRE_AND_WATER:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(THE_ROUND_TABLE_OF_KENTAURUS, "out00", BATTLEFIELD_OF_FIRE_AND_WATER, 7200)
else:
    sm.playPortalSE()
    sm.warp(BATTLEFIELD_OF_FIRE_AND_WATER, "MD00")
