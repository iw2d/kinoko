# Magatia : Alcadno - Hidden Room (261000021)
#   out00 (-474, 146)

QR_UnityPortal = 7050

ALCADNO_SOCIETY = 261000020

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(ALCADNO_SOCIETY, "in00")
