# Ace : Pilot (2150009)

# move_EliEde
EDELSTEIN_BOUND = 200090700
EDELSTEIN_TEMPORARY_AIRPORT = 310000010


if sm.askYesNo("Do you want to go to Edelstein? The fee is 800 Mesos. Hop on if you want to go."):
    if sm.canAddMoney(-800):
        if sm.warpInstance(EDELSTEIN_BOUND, "sp", EDELSTEIN_TEMPORARY_AIRPORT, 300):
            sm.addMoney(-800)
        else:
            sm.sayNext("Someone else is heading to Edelstein at the moment. Please try again later.")
    else:
        sm.sayNext("Are you sure you have enough mesos?")