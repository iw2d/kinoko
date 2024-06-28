# Ludibrium : Eos Tower 71st - 90th Floor (221022200)

EOS_TOWER = 221022200
DRUMMER_BUNNYS_LAIR = 221023401
MINI_DUNGEON_COUNT = 50

if fieldId == EOS_TOWER:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.warpInstance(DRUMMER_BUNNYS_LAIR, MINI_DUNGEON_COUNT, "out00"):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(EOS_TOWER, "MD00")