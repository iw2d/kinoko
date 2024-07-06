# El Nath : Shammos's Solitary Room (211000002)
#   out00 (-282, 64)

QR_UnityPortal = 7050

CHIEFS_RESIDENCE = 211000001

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(CHIEFS_RESIDENCE, "in00")
