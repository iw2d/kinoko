# Kiriru : Station Guide (1100007)

# move_EliEre
TO_EREVE = 200090030
SKY_FERRY = 130000210


if sm.askYesNo("Eh... So... Um... Are you trying to leave Victoria to go to a different region? You can take this boat to #eEreve#n. There, you will see bright sunlight shining on the leaves and feel a gentle breeze on your skin. It's where Shinsoo and Empress Cygnus are. Would you like to go to Ereve?\r\n\r\nIt will take about #e2 minutes#n and it will cost you #e1000#n Mesos."):
    if sm.canAddMoney(-1000):
        if sm.warpInstance(TO_EREVE, "sp", SKY_FERRY, 120):
            sm.addMoney(-1000)
        else:
            sm.sayNext("Someone else is heading to Ereve at the moment. Please try again later.")
    else:
        sm.sayNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...")
else:
    sm.sayNext("If you're not interested, then oh well...")