# Xinga : Pilot (9270017)

KERNING_CITY = 103000000
KERNING_AIRPORT = 540010100

if sm.getFieldId() == KERNING_AIRPORT:
    if sm.askYesNo("The plane will be taking off soon, will you leave now? You will have to buy the plane ticket again to come in here."):
        sm.sayNext("The ticket is not refundable, hope to see you again!")
        sm.warp(KERNING_CITY, "sp")