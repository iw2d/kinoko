# Hikari (9120003)
#   Zipangu : Showa Town (801000000)

LOCKER_ROOM = 801000100

if sm.askYesNo("Would you like to enter the bathhouse? That'll be 300 mesos for you."):
    if sm.addMoney(-300):
        sm.warp(801000100 + (100 * sm.getGender()), "out00")
    else:
        sm.sayOk("Please check and see if you have 300 mesos to enter this place")
