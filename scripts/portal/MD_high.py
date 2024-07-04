# Malaysia : Fantasy Theme Park 3 (551030000)

FANTASY_THEME_PARK_3 = 551030000
LONGEST_RIDE_ON_BYEBYE_STATION = 551030001

if sm.getFieldId() == FANTASY_THEME_PARK_3:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(LONGEST_RIDE_ON_BYEBYE_STATION, "out00", FANTASY_THEME_PARK_3, 7200)
else:
    sm.playPortalSE()
    sm.warp(FANTASY_THEME_PARK_3, "MD00")