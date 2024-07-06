#  Monster Carnival : Spiegelmann's Office (980000000)
#   out00 (-518, 133)
# The 2nd Monster Carnival : Spiegelmann's Office (980030000)
#   out00 (-411, 133)

QR_UnityPortal = 7050

returnMap = sm.getQRValue(QR_UnityPortal)
if returnMap and returnMap.isdigit():
    returnMap = int(returnMap)
else:
    returnMap = 100000000 # default to Henesys

sm.setQRValue(QR_UnityPortal, "")
sm.playPortalSE()
sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
