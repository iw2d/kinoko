# Ace : Pilot (2150009)

# move_OrbEde
EDELSTEIN_BOUND = 200090600
INSTANCE_COUNT = 10

if sm.askYesNo("Do you want to go to Edelstein? The fee is 800 Mesos. Hop on if you want to go."):
    if sm.canAddMoney(-800):
        if sm.warpInstance(EDELSTEIN_BOUND, INSTANCE_COUNT, "sp"):
            sm.addMoney(-800)
            sm.clock(180)
        else:
            sm.sayNext("Someone else is heading to Edelstein at the moment. Please try again later.")
    else:
        sm.sayNext("Are you sure you have enough mesos?")