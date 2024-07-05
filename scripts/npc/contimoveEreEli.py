# Kiriru : Station Guide (1100003)
#   Empress' Road : Sky Ferry  (130000210)

VICTORIA_BOUND = 200090031
STATION_TO_EREVE = 104020120


if sm.askYesNo("Eh, Hello...again. Do you want to leave Ereve and go somewhere else? If so, you've come to the right place. I operate a ferry that goes from Ereve to Victoria Island, I can take you to #eVictoria Island#n if you want... You'll have to pay a fee of #e1000#n Mesos."):
    if sm.addMoney(-1000):
        sm.warpInstance(VICTORIA_BOUND, "sp", STATION_TO_EREVE, 120)
    else:
        sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...")
else:
    sm.sayNext("If you're not interested, then oh well...")
