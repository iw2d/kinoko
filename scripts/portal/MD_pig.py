# Beach : Wave Beach (120020100)

WAVE_BEACH = 120020100
THE_PIG_BEACH = 120020200

if fieldId == WAVE_BEACH:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.partyWarpInstance(THE_PIG_BEACH, "out00", WAVE_BEACH, 7200):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(WAVE_BEACH, "MD00")