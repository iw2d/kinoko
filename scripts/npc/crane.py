# Crane : Public Transportation (2090005)

ORBIS_CABIN_TO_MU_LUNG = 200000141
MU_LUNG_TEMPLE = 250000100
HERB_TOWN = 251000000

DURING_THE_RIDE_TO_MU_LUNG = 200090300
DURING_THE_RIDE_TO_ORBIS = 200090310

if sm.getFieldId() == ORBIS_CABIN_TO_MU_LUNG:
    answer = sm.askMenu("Hello there. How's the traveling so far? I've been transporting other travelers like you to other regions in no time, and... are you interested? If so, then select the town you'd like to head to.\r\n" + \
            "#L0##bMu Lung (1500 mesos)#l#k"
    )
    if answer == 0:
        if sm.canAddMoney(-1500):
            if sm.warpInstance(DURING_THE_RIDE_TO_MU_LUNG, 1, "sp"):
                sm.addMoney(-1500)
                sm.clock(60)
            else:
                sm.sayNext("Someone else is heading to Mu Lung at the moment. Please try again later.")
        else:
            sm.sayNext("Are you sure you have enough mesos?")
    else:
        sm.sayNext("Ok. If you ever change your mind, please let me know.")
elif sm.getFieldId() == MU_LUNG_TEMPLE:
    answer = sm.askMenu("Hello there. How's the traveling so far? I understand that walking on two legs is much harder to cover ground compared to someone like me that can navigate the skies. I've been transporting other travelers like you to other regions in no time, and... are you interested? If so, then select the town you'd like to head to.\r\n" + \
                "#L0##bOrbis (1500 mesos)#l#k\r\n" + \
                "#L1##bHerb Town (500 mesos)#l#k"
    )
    if answer == 0:
        if sm.askYesNo("Do you want to fly to #bOrbis#k right now? As long as you don't act silly while in the air, you should reach your destination in no time. It'll only cost you #b1500 mesos#k."):
            if sm.canAddMoney(-1500):
                if sm.warpInstance(DURING_THE_RIDE_TO_ORBIS, 1, "sp"):
                    sm.addMoney(-1500)
                    sm.clock(60)
                else:
                    sm.sayNext("Someone else is heading to Orbis at the moment. Please try again later.")
            else:
                sm.sayNext("Are you sure you have enough mesos?")
        else:
            sm.sayNext("Ok. If you ever change your mind, please let me know.")
    elif answer == 1:
        if sm.askYesNo("Do you want to fly to #bHerb Town#k right now? As long as you don't act silly while in the air, you should reach your destination in no time. It'll only cost you #b500 mesos#k."):
            if sm.addMoney(-500):
                sm.warp(HERB_TOWN)
            else:
                sm.sayNext("Are you sure you have enough mesos?")
        else:
            sm.sayNext("Ok. If you ever change your mind, please let me know.")
    else:
        sm.sayNext("Ok. If you ever change your mind, please let me know.")
elif sm.getFieldId() == HERB_TOWN:
    if sm.askYesNo("Hello there. How's the traveling so far? I've been transporting other travelers like you to #bMu Lung#k in no time, and... are you interested? It's not as stable as the ship, so you'll have to hold on tight, but i can get there much faster than the ship. I'll take you there as long as you pay #b500 mesos#k."):
        if sm.addMoney(-500):
            sm.warp(MU_LUNG_TEMPLE)
        else:
            sm.sayNext("Are you sure you have enough mesos?")
    else:
        sm.sayNext("Ok. If you ever change your mind, please let me know.")