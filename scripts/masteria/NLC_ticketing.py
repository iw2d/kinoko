# Bell : NLC Subway Staff (9201057)
#   Victoria Road : Subway Ticketing Booth (103000100)
#   Kerning City Subway : Subway Ticketing Booth (103020000)
#   New Leaf City : NLC Subway Station (600010001)
#   New Leaf City : Waiting Room(From NLC to KC) (600010002)
#   Kerning City Town Street : Waiting Room(From KC to NLC) (600010004)

SUBWAY_TICKETING_BOOTH = 103020000
NLC_SUBWAY_STATION = 600010001

WAITING_ROOM_FROM_NLC_TO_KC = 600010002
WAITING_ROOM_FROM_KC_TO_NLC = 600010004

SUBWAY_TICKET_TO_NLC = 4031711
SUBWAY_TICKET_TO_KERNING_CITY = 4031713

if sm.getFieldId() == SUBWAY_TICKETING_BOOTH:
    if sm.askMenu("Hello. Would you like to buy a ticket for the subway?\r\n#L0##bNew Leaf City of Masteria#k#l") == 0:
        if sm.askYesNo("The ride to New Leaf City of Masteria takes off every 10 minutes, beginning on the hour, and it'll cost you #b5000 mesos#k. Are you sure you want to purchase a #b#t{}##k?".format(SUBWAY_TICKET_TO_NLC)):
            if sm.canAddItem(SUBWAY_TICKET_TO_NLC, 1):
                if sm.addMoney(-5000):
                    sm.addItem(SUBWAY_TICKET_TO_NLC, 1)
                else:
                    sm.sayOk("You don't have enough mesos.")
            else:
                sm.sayOk("Please check if your inventory is full or not.")
elif sm.getFieldId() == NLC_SUBWAY_STATION:
    if sm.askMenu("Hello. Would you like to buy a ticket for the subway?\r\n#L0##bKerning City of Victory Island#k#l") == 0:
        if sm.askYesNo("The ride to Kerning City of Victoria Island takes off every 10 minutes, beginning on the hour, and it'll cost you #b5000 mesos#k. Are you sure you want to purchase a #b#t{}##k?".format(SUBWAY_TICKET_TO_KERNING_CITY)):
            if sm.canAddItem(SUBWAY_TICKET_TO_KERNING_CITY, 1):
                if sm.addMoney(-5000):
                    sm.addItem(SUBWAY_TICKET_TO_KERNING_CITY, 1)
                else:
                    sm.sayOk("You don't have enough mesos.")
            else:
                sm.sayOk("Please check if your inventory is full or not.")
elif sm.getFieldId() == WAITING_ROOM_FROM_NLC_TO_KC:
    if sm.askYesNo("Do you want to go back to New Leaf City subway station now?"):
        sm.warp(NLC_SUBWAY_STATION)
elif sm.getFieldId() == WAITING_ROOM_FROM_KC_TO_NLC:
    if sm.askYesNo("Do you want to go back to Kerning City subway station now?"):
        sm.warp(SUBWAY_TICKETING_BOOTH)
