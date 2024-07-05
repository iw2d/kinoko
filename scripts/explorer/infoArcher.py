# Athena Pierce : Bowman Job Instructor (10200)
#   Maple Road : Split Road of Destiny (1020000)

GO_ARCHER = 1020300

sm.sayNext("Bowmen are blessed with dexterity and power, taking charge of long-distance attacks, providing support for those at the front line of the battle. Very adept at using landscape as part of the arsenal.")

if sm.askYesNo("Would you like to experience what it's like to be a Bowman?"):
    sm.warp(GO_ARCHER)
else:
    sm.sayNext("If you wish to experience what it's like to be a Bowman, come see me again.")
