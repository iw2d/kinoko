# Hidden Street : Moon Bunny Lobby (910010500)
#   out00 (-420, 267)

QR_UnityPortal = 7050

HENESYS_PARK = 100000200

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(HENESYS_PARK, "event00") # moonrabbit
