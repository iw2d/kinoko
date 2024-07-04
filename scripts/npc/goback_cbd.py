# Kerny : Pilot (9270018)

CHANGI_AIRPORT = 540010000
BEFORE_DEPARTURE_TO_KERNING_CITY = 540010001

sm.sayNext(sm.getEventState("CM_AIRPORT"))
if sm.getFieldId() == BEFORE_DEPARTURE_TO_KERNING_CITY:
    if sm.askYesNo("The plane will be taking off soon, will you leave now? You will have to buy the plane ticket again to come in here."):
        sm.sayNext("The ticket is not refundable, hope to see you again!")
        sm.warp(CHANGI_AIRPORT, "sp")