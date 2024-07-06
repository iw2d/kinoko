# Herb Town : Over the Pirate Ship (251010404)
#   out00 (-1954, 243)

QR_UnityPortal = 7050

RED_NOSE_PIRATE_DEN = 251010401

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
    sm.setQRValue(QR_UnityPortal, "")
    sm.playPortalSE()
    sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
else:
    sm.playPortalSE()
    sm.warp(RED_NOSE_PIRATE_DEN, "in00")
