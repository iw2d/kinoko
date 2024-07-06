# Magatia : Zenumist - Hidden Room (261000011)
#   out00 (-314, 181)

QR_UnityPortal = 7050

ZENUMIST_SOCIETY = 261000010

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(ZENUMIST_SOCIETY, "in00")
