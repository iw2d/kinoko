# Cherry : Cabin Crew (1032008)
#   Port Road : Station to Orbis (104020110)
# Rini : Cabin Crew (2012001)
#   Orbis : Station <Victoria Bound> (200000111)
# Sunny : Cabin Crew (2012013)
#   Orbis : Station<Ludibrium> (200000121)
# Ramini : Crewmember (2012021)
#   Orbis : Cabin <To Leafre> (200000131)
# Geras : Crew (2012025)
#   Orbis : Station <To Ariant> (200000151)
# Tian : Cabin Crew (2041000)
#   Ludibrium : Station<Orbis> (220000110)
# Tommie : Crewmember (2082001)
#   Leafre : Station (240000110)
# Asesson : Crew (2102000)
#   Ariant : Ariant Station Platform (260000100)

GET_TICKET_TYPES = [
    ("CM_VICTORIA", "ship", 200000111, 200000112),  # Orbis : Station <Victoria Bound>
    ("CM_VICTORIA", "ship", 104020110, 104020111),  # Port Road : Station to Orbis
    ("CM_LUDIBRIUM", "ship", 200000121, 200000122), # Orbis : Station <Ludibrium>
    ("CM_LUDIBRIUM", "ship", 220000110, 220000111), # Ludibrium : Station <Orbis>
    ("CM_LEAFRE", "ship", 200000131, 200000132),    # Orbis : Cabin <To Leafre>
    ("CM_LEAFRE", "ship", 240000110, 240000111),    # Leafre : Station
    ("CM_ARIANT", "genie", 200000151, 200000152),   # Orbis : Station <To Ariant>
    ("CM_ARIANT", "genie", 260000100, 260000110),   # Ariant : Ariant Station Platform
]

for eventType, moveType, boardingField, waitingField in GET_TICKET_TYPES:
    if sm.getFieldId() == boardingField:
        eventState = sm.getEventState(eventType)
        if eventState == "CONTIMOVE_BOARDING":
            if sm.askYesNo("This will not be a short flight, so you need to take care of some things, I suggest you do that first before getting on board. Do you still wish to board the {}?".format(moveType)):
                sm.warp(waitingField)
            else:
                sm.sayNext("You must have some business to take care of here, right?")
        elif eventState == "CONTIMOVE_WAITING":
            sm.sayNext("This {} is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the usher at the ticketing booth.".format(moveType))
        else:
            sm.sayNext("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the {} will take off on time, and we stop receiving tickets 1 minute before that, so please make sure to be here on time.".format(moveType))
