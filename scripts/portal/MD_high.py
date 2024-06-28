# Malaysia : Fantasy Theme Park 3 (551030000)

FANTASY_THEME_PARK_3 = 551030000
LONGEST_RIDE_ON_BYEBYE_STATION = 551030001
MINI_DUNGEON_COUNT = 20

if fieldId == FANTASY_THEME_PARK_3:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    if not sm.warpInstance(LONGEST_RIDE_ON_BYEBYE_STATION, MINI_DUNGEON_COUNT, "out00"):
        sm.message("All of the Mini-Dungeons are in use right now, please try again later.")
        sm.dispose()
else:
    sm.warp(FANTASY_THEME_PARK_3, "MD00")