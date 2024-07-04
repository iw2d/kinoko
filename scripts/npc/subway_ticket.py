# Jake : Subway Worker (1052006)

TICKET_TO_CONSTRUCTION_SITE_B1 = 4031036
TICKET_TO_CONSTRUCTION_SITE_B2 = 4031037
TICKET_TO_CONSTRUCTION_SITE_B3 = 4031038

# Shumi's Lost Coin
CHOICES = [
    TICKET_TO_CONSTRUCTION_SITE_B1
]
# Shumi's Lost Bundle of Money
if sm.getLevel() >= 30:
    CHOICES.append(TICKET_TO_CONSTRUCTION_SITE_B2)
# Shumi's Lost Sack of Money
if sm.getLevel() >= 40:
    CHOICES.append(TICKET_TO_CONSTRUCTION_SITE_B3)


answer = sm.askMenu("You must purchase the ticket to enter. Once you have made the purchase, you can enter through #p1052007# on the right. What would you like to buy?\r\n" + \
        "\r\n".join("#L{}##b#t{}##k#l".format(i, t) for i, t in enumerate(CHOICES))
)
if answer == 0:
    if sm.askYesNo("Will you purchase the Ticket to #bConstruction site B1#k? It'll cost you 500 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory."):
        if sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1) and sm.addMoney(-500):
            sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B1, 1)
            sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 1 has some precious items available but with so many traps all over the place most come back out early. Wishing you the best of luck.")
        else:
            sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.")
    else:
        sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.")
elif answer == 1:
    if sm.askYesNo("Will you purchase the Ticket to #bConstruction site B2#k? It'll cost you 1200 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory."):
        if sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1) and sm.addMoney(-1200):
            sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B2, 1)
            sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 2 has rare, precious items available but with so many traps all over the place most come back out early. Please be safe.")
        else:
            sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.")
    else:
        sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.")
elif answer == 2:
    if sm.askYesNo("Will you purchase the Ticket to #bConstruction site B3#k? It'll cost you 2000 Mesos. Before making the purchase, please make sure you have an empty slot on your ETC inventory."):
        if sm.canAddItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1) and sm.addMoney(-2000):
            sm.addItem(TICKET_TO_CONSTRUCTION_SITE_B3, 1)
            sm.sayNext("You can insert the ticket in the #p1052007#. I heard Area 3 has very rare, very precious items available but with so many traps all over the place most come back out early. Wishing you the best of luck.")
        else:
            sm.sayNext("Are you lacking Mesos? Check and see if you have an empty slot on your ETC inventory or not.")
    else:
        sm.sayNext("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.")
