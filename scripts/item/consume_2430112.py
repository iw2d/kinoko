# Miracle Cube Fragment (2430112)

POTENTIAL_SCROLL = 2049401
ADVANCED_POTENTIAL_SCROLL = 2049400

if sm.hasItem(itemId, 10):
    if sm.canAddItem(ADVANCED_POTENTIAL_SCROLL, 1) and sm.removeItem(itemId, 10):
        sm.addItem(ADVANCED_POTENTIAL_SCROLL, 1)
    else:
        sm.sayNext("Please check if your inventory is full or not.")
elif sm.hasItem(itemId, 5):
    if sm.canAddItem(POTENTIAL_SCROLL, 1) and sm.removeItem(itemId, 5):
        sm.addItem(POTENTIAL_SCROLL, 1)
    else:
        sm.sayNext("Please check if your inventory is full or not.")

sm.dispose()