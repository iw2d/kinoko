# Ludibrium : Helios Tower 2nd Floor (222020100) | Ludibrium : Helios Tower 99th Floor (222020200)

HELIOS_TOWER_2ND_FLOOR = 222020100
HELIOS_TOWER_99TH_FLOOR = 222020200

ELEVATOR_TO_LUDIBRIUM = 222020110
ELEVATOR_TO_KOREAN_FOLK_TOWN = 222020210

if sm.getFieldId() == HELIOS_TOWER_2ND_FLOOR:
    if sm.getEventState("CM_ELEVATOR") == "ELEVATOR_2ND_FLOOR":
        sm.playPortalSE()
        sm.warp(ELEVATOR_TO_LUDIBRIUM, "out00")
    else:
        sm.message("At the moment, the elevator is not available for this route. Please try again later.")
        sm.dispose()
elif sm.getFieldId() == HELIOS_TOWER_99TH_FLOOR:
    if sm.getEventState("CM_ELEVATOR") == "ELEVATOR_99TH_FLOOR":
        sm.playPortalSE()
        sm.warp(ELEVATOR_TO_KOREAN_FOLK_TOWN, "out00")
    else:
        sm.message("At the moment, the elevator is not available for this route. Please try again later.")
        sm.dispose()