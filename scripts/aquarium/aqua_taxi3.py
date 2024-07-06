# Dolphin (2060010)
#   Sea of Fog : Shipwrecked Ghost Ship (923020000)

QR_UnityPortal = 7050

AQUARIUM = 230000000

if sm.askYesNo("Do you want to go back now?"):
    returnMap = sm.getQRValue(QR_UnityPortal)
    if returnMap and returnMap.isdigit():
        returnMap = int(returnMap)
        sm.setQRValue(QR_UnityPortal, "")
        sm.warp(returnMap, "unityPortal2" if returnMap < 540000000 else "sp") # missing portal for CBD, NLC, and Mushroom Shrine
    else:
        sm.warp(AQUARIUM) # aqua_taxi
