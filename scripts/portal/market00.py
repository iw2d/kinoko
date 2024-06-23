# Hidden Street : Free Market Entrance (910000000)

QR_FreeMarket = 7600

val = sm.getQRValue(QR_FreeMarket)

sm.playPortalSE()

if val == "1":
    sm.warp(100000100, "market00")
elif val == "2":
    sm.warp(220000000, "market00")
elif val == "3":
    sm.warp(211000100, "market00")
elif val == "4":
    sm.warp(102000000, "market00")
elif val == "5":
    sm.warp(230000000, "market01")
elif val == "6":
    sm.warp(221000000, "market00")
elif val == "7":
    sm.warp(200000000, "market00")
elif val == "8":
    sm.warp(801000300, "market00")
elif val == "9":
    sm.warp(240000000, "market00")
elif val == "10":
    sm.warp(250000000, "market00")
elif val == "11":
    sm.warp(251000000, "market00")
elif val == "12":
    sm.warp(600000000, "market00")
elif val == "13":
    sm.warp(260000000, "market00")
elif val == "14":
    sm.warp(222000000, "market00")
elif val == "15":
    sm.warp(540000000, "market00")
elif val == "16":
    sm.warp(541000000, "market00")
elif val == "17":
    sm.warp(120000200, "market00")
elif val == "18":
    sm.warp(261000000, "market0")
elif val == "19":
    sm.warp(130000200, "st00")
elif val == "20":
    sm.warp(550000000, "market00")
elif val == "21":
    sm.warp(551000000, "market00")
elif val == "22":
    sm.warp(140000000, "market00")
elif val == "23":
    sm.warp(103050000, "market00")
elif val == "24":
    sm.warp(310000000, "market00")
else:
    sm.warp(100000100, "market00")

sm.setQRValue(QR_FreeMarket, "")