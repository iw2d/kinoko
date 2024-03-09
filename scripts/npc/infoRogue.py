# Dark Lord (10203) - Maple Road : Split Road of Destiny (1020000)

GO_ROGUE = 1020400

sm.sayNext("Thieves are a perfect blend of luck, dexterity, and power that are adept at surprise attacks against helpless enemies. A high level of avoidability and speed allows the thieves to attack enemies with various angles.")

if sm.askYesNo("Would you like to experience what it's like to be a Thief?"):
    sm.warp(GO_ROGUE)
else:
    sm.sayNext("If you wish to experience what it's like to be a Thief, come see me again.")