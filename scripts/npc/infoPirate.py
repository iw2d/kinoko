# Kyrin (10204) - Maple Road : Split Road of Destiny (1020000)

GO_PIRATE = 1020500

sm.sayNext("Pirates are blessed with outstanding dexterity and power, utilizing their guns for long-range attacks while using their power on melee combat situations. Gunslingers use elemental-based bullets for added damage, while Infighters transform to a different being for maximum effect.")

if sm.askYesNo("Would you like to experience what it's like to be a Pirate?"):
    sm.warp(GO_PIRATE)
else:
    sm.sayNext("If you wish to experience what it's like to be a Pirate, come see me again.")