# Leafre : Crimson Sky Dock (240080000)
#   left00 (-512, 80)

QR_UnityPortal = 7050

THE_FOREST_THAT_DISAPPEARED = 240030102
returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(THE_FOREST_THAT_DISAPPEARED, "right00")
