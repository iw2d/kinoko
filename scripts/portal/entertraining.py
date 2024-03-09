# Maple Road : Entrance to Adventurer Training Center (1010000)

if sm.hasQuestStarted(1041):
    sm.warp(1010100, "out00")
elif sm.hasQuestStarted(1042):
    sm.warp(1010200, "out00")
elif sm.hasQuestStarted(1043):
    sm.warp(1010300, "out00")
elif sm.hasQuestStarted(1044):
    sm.warp(1010400, "out00")
else:
    sm.message("Only the adventurers that have been trained by Mai may enter.")
    sm.dispose()