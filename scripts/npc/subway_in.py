# The Ticket Gate (1052007)

ALONG_THE_SUBWAY = 103020100

KERNING_SQUARE_SUBWAY_1 = 103020010
KERNING_SQUARE_STATION = 103020020

WAITING_ROOM_FROM_KC_TO_NLC = 600010004
SUBWAY_TICKET_TO_NLC = 4031711


answer = sm.askMenu("Pick your destination.\r\n" + \
        "#L0##b#eKerning City Subway#r(Beware of Stirges and Wraiths!)#k#n#l\r\n" + \
        "#L1##bKerning Square Shopping Center (Get on the Subway)#k#l\r\n\r\n" + \
        "#L2##bEnter Construction Site#k#l\r\n" + \
        "#L3##bNew Leaf City#k#l"
)
if answer == 0:
    sm.warp(ALONG_THE_SUBWAY, "out00")
elif answer == 1:
    sm.warpInstance(KERNING_SQUARE_SUBWAY_1, "sp", KERNING_SQUARE_STATION, 10)
elif answer == 2:
    # TODO
    sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.")
elif answer == 3:
    if sm.hasItem(SUBWAY_TICKET_TO_NLC, 1):
        eventState = sm.getEventState("CM_SUBWAY")
        if eventState == "SUBWAY_BOARDING":
            if sm.askYesNo("It looks like there's plenty of room for this ride. Please have your ticket ready so I can let you in. The ride will be long, but you'll get to your destination just fine. What do you think? Do you want to get on this ride?"):
                if sm.removeItem(SUBWAY_TICKET_TO_NLC, 1):
                    sm.warp(WAITING_ROOM_FROM_KC_TO_NLC, "st00")
            else:
                sm.sayNext("You must have some business to take care of here, right?")
        elif eventState == "SUBWAY_WAITING":
            sm.sayNext("This subway is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the usher at the ticketing booth.")
        else:
            sm.sayNext("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the subway will take off right on time, and we stop receiving tickets 1 minute before that, so please make sure to be here on time.")
    else:
        sm.sayOk("Here's the ticket reader. You are not allowed in without the ticket.")