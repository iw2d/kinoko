# Maple Road : Entrance to Adventurer Training Center (1010000)
#   in00 (74, 154)

if sm.hasQuestStarted(1041):
    sm.playPortalSE()
    sm.warp(1010100, "out00")
elif sm.hasQuestStarted(1042):
    sm.playPortalSE()
    sm.warp(1010200, "out00")
elif sm.hasQuestStarted(1043):
    sm.playPortalSE()
    sm.warp(1010300, "out00")
elif sm.hasQuestStarted(1044):
    sm.playPortalSE()
    sm.warp(1010400, "out00")
else:
    sm.message("Only the adventurers that have been trained by Mai may enter.")
    sm.dispose()
