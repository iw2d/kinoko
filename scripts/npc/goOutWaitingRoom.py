# Purin : Crewmember (1032009)
#   Port Road : To Orbis <Before Starting> (104020111)
# Erin : Crewmember (2012002)
#   Orbis : Pre-Departure <Victoria Bound> (200000112)
# Pelace : Crewmember (2012022)
#   Orbis : Cabin <To Leafre> (200000132)
# Egnet : Crew (2012024)
#   Orbis : Station <To Ariant> (200000152)
# Rosey : Crewmember (2041001)
#   Orbis : Before the Departure <Ludibrium> (200000122)
#   Ludibrium : Before the Departure <Orbis> (220000111)
# Harry : Crewmember (2082002)
#   Leafre : Before Takeoff <To Orbis> (240000111)
# Slyn : Crew (2102001)
#   Ariant : Before Takeoff <To Orbis> (260000110)

WAITING_ROOM_TYPES = [
    (200000111, 200000112), # Orbis : Station <Victoria Bound>
    (104020110, 104020111), # Port Road : Station to Orbis
    (200000121, 200000122), # Orbis : Station <Ludibrium>
    (220000110, 220000111), # Ludibrium : Station <Orbis>
    (200000131, 200000132), # Orbis : Cabin <To Leafre>
    (240000110, 240000111), # Leafre : Station
    (200000151, 200000152), # Orbis : Station <To Ariant>
    (260000100, 260000110), # Ariant : Ariant Station Platform
]

for boardingField, waitingField in WAITING_ROOM_TYPES:
    if sm.getFieldId() == waitingField:
        if sm.askYesNo("We're just about to take off. Are you sure you want to get off the ship? You may do so, but then you'll have to wait until the next available flight. Do you still wish to get off board?"):
            sm.warp(boardingField, "sp")
        else:
            sm.sayOk("You'll get to your destination in a short while. Talk to other passengers and share your stories to them, and you'll be there before you know it.")
