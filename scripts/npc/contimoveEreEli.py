# Kiriru : Station Guide (1100003)

# move_EreEli
VICTORIA_BOUND = [
    200090031, 200090033, 200090035, 200090037, 200090039,
    200090051, 200090053, 200090055, 200090057, 200090059
]

if sm.askYesNo("Eh, Hello...again. Do you want to leave Ereve and go somewhere else? If so, you've come to the right place. I operate a ferry that goes from Ereve to Victoria Island, I can take you to #eVictoria Island#n if you want... You'll have to pay a fee of #e1000#n Mesos."):
    if sm.canAddMoney(-1000):
        if sm.warpInstance(VICTORIA_BOUND, "sp"):
            sm.addMoney(-1000)
            sm.clock(120)
        else:
            sm.sayNext("Someone else is heading to Victoria Island at the moment. Please try again later.")
    else:
        sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...")
else:
    sm.sayNext("If you're not interested, then oh well...")