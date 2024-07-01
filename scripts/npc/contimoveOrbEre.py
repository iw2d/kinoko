# Kiru : Station Guide (1100008)

# move_OrbEre
TO_EREVE = [
    200090020, 200090022, 200090024, 200090026, 200090028,
    200090040, 200090042, 200090044, 200090046, 200090048
]

if sm.askYesNo("This ship will head towards #eEreve#n, an island where you'll find crimson leaves soaking up the sun, the gentle breeze that glides past the stream, and the Empress of Maple Cygnus. If you're interested in joining the Cygnus Knights, then you should definitely pay a visit here. Are you interested in visiting Ereve?\r\n\r\n The Trip will cost you #e1000#n Mesos"):
    if sm.canAddMoney(-1000):
        if sm.warpInstance(TO_EREVE, "sp"):
            sm.addMoney(-1000)
            sm.clock(120)
        else:
            sm.sayNext("Someone else is heading to Ereve at the moment. Please try again later.")
    else:
        sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...")
else:
    sm.sayNext("If you're not interested, then oh well...")