# Spinel : World Tour Guide (9000020)

QR_WorldTrip = 8792

AVAILABLE_FIELDS = [
    100000000, # Henesys
    101000000, # Ellinia
    102000000, # Perion
    103000000, # Kerning City
    104000000, # Lith Harbor
    200000000, # Orbis
    220000000, # Ludibrium
    240000000, # Leafre
    260000000, # Mu Lung
    541000000, # Boat Quay Town
    680000000 # Amoria
    # 800000000, 950000000, 950000100
]
MUSHROOM_SHRINE = 800000000
TREND_ZONE_METROPOLIS = 550000000

if sm.getFieldId() in AVAILABLE_FIELDS:
    sm.sayNext("If you're tired of the monotony of daily life, it might be time for change, and the #bMaple Travel Agency#k is here to give your life a new sense of adventure! For a low, low free, we can offer you a #bWorld Tour#k that will make your dreariest days sparkle!")
    answer = sm.askMenu("We're currently servicing multiple destinations for your traveling pleasure, though our list of service areas is always expanding. Just pick your destination below, and I'll be there to serve you as your travel guide.\r\n" + \
            "#L0##bMushroom Shrine of Japan (3,000 mesos)#l#k\r\n"# + "#L1##bMalaysian Metropolis (300,000 mesos)#l#k\r\n"
    )
    if answer == 0:
        sm.sayNext("Would you like to travel to #bMushrom Shrine of Japan#k? If you desire to feel the essence of Japan, there's nothing like visiting the Shrine, a Japanese cultural melting pot. Mushroom Shrine is a mythical place that serves the incomparable Mushroom God from ancient times.")
        sm.sayNext("Check out the female shaman serving the Mushroom God, and I strongly recommend trying Takoyaki, Yakisoba, and other delocious food sold in the streets of Japan. Now, let's head over to #bMushroom Shrine#k, a mythical place if there ever was one.")
        if sm.addMoney(-3000):
            sm.setQRValue(QR_WorldTrip, str(sm.getFieldId()))
            sm.warp(MUSHROOM_SHRINE, "st00")
        else:
            sm.sayNext("Please check if you have enough mesos.")
    elif answer == 1:
        if sm.askYesNo("You can return to Victoria Island through the Changi Airport in CBD. Would you like to go now? It will cost 300,000 mesos."):
            if sm.addMoney(-300000):
                sm.warp(TREND_ZONE_METROPOLIS)
            else:
                sm.sayNext("Please check if you have enough mesos.")
        else:
            sm.sayNext("Ok. If you ever change your mind, please let me know.")
    else:
        sm.sayNext("Ok. If you ever change your mind, please let me know.")
elif sm.getFieldId() == MUSHROOM_SHRINE:
    val = sm.getQRValue(QR_WorldTrip)
    if val.isdigit() and int(val) in AVAILABLE_FIELDS:
        returnMap = int(val)
    else:
        returnMap = 100000000 # default to Henesys
    answer = sm.askMenu("How's the traveling? Are you enjoying it?\r\n" + \
            "#L0##bYes, I'm done with traveling. Can i go back to #e#m{}##n?\r\n".format(returnMap) + \
            "#L1##bNo, I'd like to continue exploring this place.#"
    )
    if answer == 0:
        sm.sayNext("Alright. I'll take you back to where you were before the visit to Japan. If you ever feel like traveling again down the road, please let me know!")
        sm.warp(returnMap)
    else:
        sm.sayNext("Ok. If you ever change your mind, please let me know.")