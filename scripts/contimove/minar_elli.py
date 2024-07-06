# North Forest : Green Tree Trunk (101030100)
#   minar00 (-1252, 525)
# Leafre : Minar Forest : West Border (240010100)
#   elli00 (938, 267)

MAGIC_SEED = 4031346
GREEN_TREE_TRUNK = 101030100
WEST_BORDER = 240010100

if sm.removeItem(MAGIC_SEED, 1):
    if sm.getFieldId() == GREEN_TREE_TRUNK:
        sm.playPortalSE()
        sm.warp(WEST_BORDER, "elli00")
    elif sm.getFieldId() == WEST_BORDER:
        sm.playPortalSE()
        sm.warp(GREEN_TREE_TRUNK, "minar00")
else:
    sm.message("You need a magic seed to use this portal.")
    sm.dispose()
