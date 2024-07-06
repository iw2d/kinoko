# Elin Forest : Deep Fairy Forest (300030100)
#   west00 (-344, 149)

QR_UnityPortal = 7050

EASTERN_REGION_OF_MOSSY_TREE_FOREST = 300030000

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(EASTERN_REGION_OF_MOSSY_TREE_FOREST, "east00")
