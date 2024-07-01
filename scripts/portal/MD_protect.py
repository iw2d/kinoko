# Leafre : Destroyed Dragon Nest (240040520)

DESTROYED_DRAGON_NEST = 240040520
NEWT_SECURED_ZONE = 240040900
MINI_DUNGEON_COUNT = 50

if fieldId == DESTROYED_DRAGON_NEST:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.partyWarpInstance(NEWT_SECURED_ZONE, MINI_DUNGEON_COUNT, "out00"):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(DESTROYED_DRAGON_NEST, "MD00")