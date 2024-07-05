# Ace : Pilot (2150008)
#   Edelstein : Edelstein Temporary Airport (310000010)

VICTORIA_ISLAND_BOUND = 200090710
STATION_TO_EDELSTEIN = 104020130

# move_EdeOrb
ORBIS_BOUND = 200090610
STATION_EDELSTEIN_BOUND = 200000170


answer = sm.askMenu("Would you like to leave Edelstein and travel to a different continent? I can take you to Victoria Island and the Orbis area of Ossyria. The cost is 800 Mesos. Where would you like to go?\r\n" + \
        "#L0##bVictoria Island#k#l\r\n" + \
        "#L1##bOrbis#k#l"
)

if answer == 0:
    if sm.addMoney(-800):
        sm.warpInstance(VICTORIA_ISLAND_BOUND, "sp", STATION_TO_EDELSTEIN, 300)
    else:
        sm.sayNext("Are you sure you have enough mesos?")
elif answer == 1:
    if sm.addMoney(-800):
        sm.warpInstance(ORBIS_BOUND, INSTANCE_COUNT, "sp", STATION_EDELSTEIN_BOUND, 180)
    else:
        sm.sayNext("Are you sure you have enough mesos?")
