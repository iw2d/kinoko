# Leafre : Destroyed Dragon Nest (240040520)

DESTROYED_DRAGON_NEST = 240040520
NEWT_SECURED_ZONE = 240040900

if sm.getFieldId() == DESTROYED_DRAGON_NEST:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(NEWT_SECURED_ZONE, "out00", DESTROYED_DRAGON_NEST, 7200)
else:
    sm.playPortalSE()
    sm.warp(DESTROYED_DRAGON_NEST, "MD00")