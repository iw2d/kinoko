# NLC Taxi (9201056)
#   New Leaf City : NLC Town Center (600000000)
#   Phantom Forest : Haunted House (682000000)

NLC_TOWN_CENTER = 600000000
HAUNTED_HOUSE = 682000000

if sm.getFieldId() == NLC_TOWN_CENTER:
    if sm.askYesNo("Hey, there. Want to take a trip deeper into the Masterian wilderness? A lot of this continent is still quite unknown and untamed... so there's still not much in the way of roads. Good thing we've got this baby... we can go offroading, and in style too! Right now, I can drive you to the #bPhantom Forest#k. The old #bPrendergast Mansion#k is located there. Some people say the place is haunted! What do you say... want to head over there?"):
        sm.sayNext("Alright! Buckle your seat belt, and let's head to the Mansion!\r\nIt's going to get bumpy!")
        sm.warp(HAUNTED_HOUSE, "st00")
    else:
        sm.sayOk("Really? I don't blame you... Sounds like a pretty scary place to me too! If you change your mind, I'll be right here.")
elif sm.getFieldId() == HAUNTED_HOUSE:
    if sm.askYesNo("Hey, there. Hope you had fun here! Ready to head back to #bNew Leaf City#k?"):
        sm.sayNext("Back to civilization it is! Hop in and get comfortable back there... We'll have you back to the city in a jiffy!")
        sm.warp(NLC_TOWN_CENTER)
    else:
        sm.sayOk("Oh, you want to stay and look around some more? That's understandable. If you wish to go back to #bNew Leaf City#k, you know who to talk to!")
