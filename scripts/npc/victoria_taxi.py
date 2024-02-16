# Regular Cab in Victoria

towns = [100000000, 101000000, 102000000, 103000000, 104000000, 105000000, 120000000]
price = 1000

sm.sayNext("Hello! I'm Regular Cab in Victoria, and I am here to take you to your destination, quickly and safely. #bRegular Cab in Victoria#k values your satisfaction, so you can always reach your destination at an affordable price. I am here to serve you.")

choices = list(filter(lambda x: x != user.getField().getFieldId(), towns))
answer = sm.askMenu("Please select your destination.\r\n#b" + "".join("\r\n#L{}##m{}# ({} Mesos)#l".format(i, t, price) for i, t in enumerate(choices)))

if sm.askYesNo("You don't have anything else to do here, huh? Do you really want to go to #b#m{}##k? It'll cost you #b{}#k mesos.".format(choices[answer], price)):
    if sm.addMoney(-price):
        sm.warp(choices[answer])
    else:
        sm.sayOk("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.")
else:
    sm.sayOk("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.")