# Miracle Cube Fragment (2430112)

MIRACLE_CUBE_FRAGMENT = 2430112

POTENTIAL_SCROLL = 2049401
ADVANCED_POTENTIAL_SCROLL = 2049400


if sm.hasItem(MIRACLE_CUBE_FRAGMENT, 10):
    if sm.canAddItem(ADVANCED_POTENTIAL_SCROLL, 1) and sm.removeItem(MIRACLE_CUBE_FRAGMENT, 10):
        sm.addItem(ADVANCED_POTENTIAL_SCROLL, 1)
    else:
        sm.sayNext("Please check if your inventory is full or not.")
elif sm.hasItem(MIRACLE_CUBE_FRAGMENT, 5):
    if sm.canAddItem(POTENTIAL_SCROLL, 1) and sm.removeItem(MIRACLE_CUBE_FRAGMENT, 5):
        sm.addItem(POTENTIAL_SCROLL, 1)
    else:
        sm.sayNext("Please check if your inventory is full or not.")
sm.dispose()