# Grendel the Really Old : Magician Job Instructor (10201)
#   Maple Road : Split Road of Destiny (1020000)

GO_MAGICIAN = 1020200

sm.sayNext("Magicians are armed with flashy element-based spells and secondary magic that aids party as a whole. After the 2nd job adv., the elemental-based magic will provide ample amount of damage to enemies of opposite element.")

if sm.askYesNo("Would you like to experience what it's like to be a Magician?"):
    sm.warp(GO_MAGICIAN)
else:
    sm.sayNext("If you wish to experience what it's like to be a Magician, come see me again.")
