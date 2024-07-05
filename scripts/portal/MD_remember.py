# Leafre : The Dragon Nest Left Behind (240040511)
#   MD00 (1028, 1099)
# Mini Dungeon : The Restoring Memory (240040800)
#   out00 (1080, 1094)

THE_DRAGON_NEST_LEFT_BEHIND = 240040511
THE_RESTORING_MEMORY = 240040800

if sm.getFieldId() == THE_DRAGON_NEST_LEFT_BEHIND:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(THE_RESTORING_MEMORY, "out00", THE_DRAGON_NEST_LEFT_BEHIND, 7200)
else:
    sm.playPortalSE()
    sm.warp(THE_DRAGON_NEST_LEFT_BEHIND, "MD00")
