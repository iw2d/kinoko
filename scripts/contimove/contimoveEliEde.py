# Ace : Pilot (2150010)
#   Port Road : Station to Edelstein (104020130)

EDELSTEIN_BOUND = 200090700
EDELSTEIN_TEMPORARY_AIRPORT = 310000010


if sm.askYesNo("Do you want to go to Edelstein? The fee is 800 Mesos. Hop on if you want to go."):
    if sm.addMoney(-800):
        sm.warpInstance(EDELSTEIN_BOUND, "sp", EDELSTEIN_TEMPORARY_AIRPORT, 300)
    else:
        sm.sayNext("Are you sure you have enough mesos?")
