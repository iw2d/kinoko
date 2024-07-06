# Singing Mushroom Forest : Ghost Mushroom Forest (100020400)
#   TD00 (-1094, 214)

QR_Opening = 2311

MUSHROOM_FOREST_FIELD = 106020000
THEME_DUNGEON_OPENING = 106020001

if sm.getLevel() < 30:
    sm.message("A strange force is blocking you from entering.")
elif sm.getQRValue(QR_Opening) == "1":
    sm.playPortalSE()
    sm.warp(MUSHROOM_FOREST_FIELD, "left00")
else:
    sm.warp(THEME_DUNGEON_OPENING)
