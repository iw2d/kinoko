# Drake Cave : Cave Exit (105020400)
#   MD00 (2442, 107)
# Drake Cave : Blue Drake Cave (105020500)
#   out00 (441, -1029)

CAVE_EXIT = 105020400
BLUE_DRAKE_CAVE = 105020500

if sm.getFieldId() == CAVE_EXIT:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(BLUE_DRAKE_CAVE, "out00", CAVE_EXIT, 7200)
else:
    sm.playPortalSE()
    sm.warp(CAVE_EXIT, "MD00")
