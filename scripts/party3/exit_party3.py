# Orbis : The Unknown Tower (200080101)
#   out00 (-322, 173)

QR_UnityPortal = 7050

ENTRANCE_TO_ORBIS_TOWER = 200080100

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(ENTRANCE_TO_ORBIS_TOWER, "in00")
