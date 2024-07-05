# Maple Administrator (2007)
#   Maple Road : Mushroom Park (10000)

LITH_HARBOR = 104000000

if sm.askYesNo("Would you like to skip the tutorials and head straight to Lith Harbor?"):
    sm.warp(LITH_HARBOR)
else:
    sm.sayNext("Enjoy your trip.")
