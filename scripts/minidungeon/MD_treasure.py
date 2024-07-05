# Herb Town : Red-Nose Pirate Den 2 (251010402)
#   MD00 (549, -234)
# Mini Dungeon : Pillage of Treasure Island (251010410)
#   out00 (361, -394)

RED_NOSE_PIRATE_DEN_2 = 251010402
PILLAGE_OF_TREASURE_ISLAND = 251010410

if sm.getFieldId() == RED_NOSE_PIRATE_DEN_2:
    if sm.hasParty() and not sm.isPartyBoss():
        sm.message("You are not the leader of the party.")
        sm.dispose()
    else:
        sm.playPortalSE()
        sm.partyWarpInstance(PILLAGE_OF_TREASURE_ISLAND, "out00", RED_NOSE_PIRATE_DEN_2, 7200)
else:
    sm.playPortalSE()
    sm.warp(RED_NOSE_PIRATE_DEN_2, "MD00")
