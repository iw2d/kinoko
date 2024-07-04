# Audrey : Malaysia Tour Guide (9201135)

SINGAPORE_CBD = 540000000
TREND_ZONE_METROPOLIS = 550000000
KAMPUNG_VILLAGE = 551000000


def handleWarp(choices):
    answer = sm.askMenu("Where would you like to travel?\r\n" + \
            "\r\n".join("#L{}##b#m{}# ({:,} mesos)#k#l".format(i, choice[0], choice[1]) for i, choice in enumerate(choices))
    )
    if answer >= 0 and answer < len(choices):
        town = choices[answer][0]
        price = choices[answer][1]
        if sm.askYesNo("Would you like to travel to #b#m{}##k? To head over to #m{}#, it'll cost you #b{:,} mesos#k. Would you like to go right now?".format(town, town, price)):
            if sm.addMoney(-price):
                sm.warp(town, "sp")
            else:
                sm.sayNext("You do not seem to have enough mesos.")
        else:
            sm.sayNext("You know where to come if you need a ride!")


if sm.getFieldId() == SINGAPORE_CBD:
    handleWarp([
        (TREND_ZONE_METROPOLIS, 1000),
        (KAMPUNG_VILLAGE, 10000)
    ])
elif sm.getFieldId() == TREND_ZONE_METROPOLIS:
    handleWarp([
        (SINGAPORE_CBD, 1000),
        (KAMPUNG_VILLAGE, 10000)
    ])
elif sm.getFieldId() == KAMPUNG_VILLAGE:
    handleWarp([
        (SINGAPORE_CBD, 10000),
        (TREND_ZONE_METROPOLIS, 10000)
    ])