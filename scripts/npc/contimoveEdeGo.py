# Ace : Pilot (2150008)

ORBIS_BOUND = 200090610
VICTORIA_ISLAND_BOUND = 200090710
INSTANCE_COUNT = 10


answer = sm.askMenu("Would you like to leave Edelstein and travel to a different continent? I can take you to Victoria Island and the Orbis area of Ossyria. The cost is 800 Mesos. Where would you like to go?\r\n" + \
        "#L0##bVictoria Island#k#l\r\n" + \
        "#L1##bOrbis#k#l"
)

if answer == 0:
    if sm.canAddMoney(-800):
        if sm.warpInstance(VICTORIA_ISLAND_BOUND, INSTANCE_COUNT, "sp"):
            sm.addMoney(-800)
            sm.clock(300)
        else:
            sm.sayNext("Someone else is heading to Victoria Island at the moment. Please try again later.")
    else:
        sm.sayNext("Are you sure you have enough mesos?")
elif answer == 1:
    if sm.canAddMoney(-800):
        if sm.warpInstance(ORBIS_BOUND, INSTANCE_COUNT, "sp"):
            sm.addMoney(-800)
            sm.clock(180)
        else:
            sm.sayNext("Someone else is heading to Orbis at the moment. Please try again later.")
    else:
        sm.sayNext("Are you sure you have enough mesos?")