# Ludibrium : Eos Tower 101st Floor (221023300)
#   out00 (173, 2005)

QR_UnityPortal = 7050

EOS_TOWER_100TH_FLOOR = 221023200

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(EOS_TOWER_100TH_FLOOR, "in00")
