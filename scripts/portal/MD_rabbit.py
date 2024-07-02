# Ludibrium : Eos Tower 71st - 90th Floor (221022200)

EOS_TOWER = 221022200
DRUMMER_BUNNYS_LAIR = 221023401

if fieldId == EOS_TOWER:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(DRUMMER_BUNNYS_LAIR, "out00", EOS_TOWER, 7200)
else:
    sm.playPortalSE()
    sm.warp(EOS_TOWER, "MD00")