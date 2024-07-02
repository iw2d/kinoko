# Regular Cab in Victoria (1012000)

IS_BEGINNER = sm.getJob() % 1000 == 0 or sm.getJob() == 2001

TOWNS = [100000000, 101000000, 102000000, 103000000, 104000000, 105000000, 120000000]
PRICE = 100 if IS_BEGINNER else 1000

sm.sayNext("Hello! I'm #p1012000#, and I am here to take you to your destination, quickly and safely. #b#p1012000##k values your satisfaction, so you can always reach your destination at an affordable price. I am here to serve you.")

choices = list(filter(lambda x: x != sm.getFieldId(), TOWNS))
answer = sm.askMenu("Please select your destination." + \
        ("\r\nWe have a special 90% discount for beginners." if IS_BEGINNER else "") + \
        "".join("#b\r\n#L{}##m{}# ({} Mesos)#l#k".format(i, t, PRICE) for i, t in enumerate(choices))
)

if sm.askYesNo("You don't have anything else to do here, huh? Do you really want to go to #b#m{}##k? It'll cost you #b{}#k mesos.".format(choices[answer], PRICE)):
    if sm.addMoney(-PRICE):
        sm.warp(choices[answer])
    else:
        sm.sayOk("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.")
else:
    sm.sayOk("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.")